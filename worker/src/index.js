const LENGTH_TARGETS = {
  short:  { words: "60–100 words",   max_tokens: 350 },
  medium: { words: "120–180 words",  max_tokens: 600 },
  long:   { words: "220–320 words",  max_tokens: 1100 },
};

const SYSTEM_PROMPT = `You are Sacred Flow, a belief-neutral companion that writes short personalized prayers, intentions, and reflections.

Rules:
- Match the requested tone exactly. The tone is the emotional register; do not contradict it.
- Hit the requested length window. Count words mentally before finalizing.
- Belief-neutral: do not assume any specific religion, deity, or doctrine unless the user clearly invokes one in their own words. Use second-person ("you", "may you") or first-person ("I", "let me") framing by default. Avoid "God", "Allah", "the universe", "spirit" unless the user's text uses them first.
- Output plain text only. No markdown, no headings, no bullets, no quotation marks around the whole thing, no preamble like "Here is a prayer". Start directly with the first word of the prayer.
- Do not address the user by name unless they provide one.
- Do not give medical, legal, financial, or crisis advice. If the request describes self-harm, suicide, or someone in immediate danger, refuse gently and recommend reaching a professional or local emergency line.
- Be specific to the recipient and need; avoid generic platitudes.`;

function buildUserPrompt(payload, isRegeneration) {
  const tgt = LENGTH_TARGETS[payload.length] || LENGTH_TARGETS.medium;
  const needs = (payload.needs || []).join(", ") || "(none specified)";
  const recipient = payload.recipientIsCustom
    ? `a custom recipient described as: "${payload.recipient}"`
    : payload.recipient;
  const ctx = payload.userContext && payload.userContext.trim();
  const regen = isRegeneration
    ? "\nThis is a REGENERATION — produce a meaningfully different result than a typical first attempt: different opening image, different rhythm, different closing line."
    : "";

  return [
    `Use case: ${payload.useCase}`,
    `Recipient: ${recipient}`,
    `Tone: ${payload.tone}`,
    `Length target: ${tgt.words}`,
    `Needs / themes: ${needs}`,
    `Locale: ${payload.locale || "en-US"}`,
    ctx ? `User context: ${ctx}` : null,
    regen,
    "",
    "Write the prayer/intention/reflection now. Plain text only.",
  ].filter(Boolean).join("\n");
}

function json(status, body) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "content-type": "application/json; charset=utf-8" },
  });
}

async function handleGenerate(req, env) {
  const started = Date.now();
  let body;
  try {
    body = await req.json();
  } catch {
    return json(400, { status: "error", reason: "invalid_json", userMessage: "Bad request." });
  }

  const payload = body.request;
  if (!payload || typeof payload !== "object") {
    return json(400, {
      status: "error",
      reason: "invalid_request",
      userMessage: "Missing 'request' payload.",
    });
  }

  const model = env.ANTHROPIC_MODEL || "claude-sonnet-4-6";
  const tgt = LENGTH_TARGETS[payload.length] || LENGTH_TARGETS.medium;

  let anthropicResp;
  try {
    anthropicResp = await fetch("https://api.anthropic.com/v1/messages", {
      method: "POST",
      headers: {
        "x-api-key": env.ANTHROPIC_API_KEY,
        "anthropic-version": "2023-06-01",
        "content-type": "application/json",
      },
      body: JSON.stringify({
        model,
        max_tokens: tgt.max_tokens,
        system: SYSTEM_PROMPT,
        messages: [{ role: "user", content: buildUserPrompt(payload, !!body.isRegeneration) }],
      }),
    });
  } catch (err) {
    console.error("anthropic fetch failed", err);
    return json(502, {
      status: "error",
      reason: "upstream_unreachable",
      userMessage: "Couldn't reach the generator. The app will use its offline fallback.",
    });
  }

  const elapsed = Date.now() - started;

  if (anthropicResp.status === 429) {
    return json(200, {
      status: "rate_limited",
      userMessage: "The model is busy right now. Try again in a moment.",
      quota: { remainingToday: 0, resetAt: Date.now() + 60_000 },
    });
  }

  if (!anthropicResp.ok) {
    const errText = await anthropicResp.text().catch(() => "");
    console.error(`anthropic ${anthropicResp.status}: ${errText.slice(0, 500)}`);

    const lower = errText.toLowerCase();
    if (lower.includes("credit balance is too low") || lower.includes("credit balance")) {
      return json(502, {
        status: "error",
        reason: "upstream_no_credit",
        userMessage: "The generator is temporarily unavailable. (No API credit on the server.) The app will use its offline fallback.",
      });
    }
    if (anthropicResp.status === 401 || lower.includes("invalid x-api-key")) {
      return json(502, {
        status: "error",
        reason: "upstream_auth",
        userMessage: "The generator is misconfigured. The app will use its offline fallback.",
      });
    }
    return json(502, {
      status: "error",
      reason: `upstream_${anthropicResp.status}`,
      userMessage: "Couldn't reach the generator. The app will use its offline fallback.",
    });
  }

  const msg = await anthropicResp.json();

  if (msg.stop_reason === "refusal") {
    return json(200, {
      status: "soft_blocked",
      reason: "model_refusal",
      userMessage: "This one's outside what I can help generate. If you're in crisis or someone you love is in danger, please reach out to a local emergency line or a trusted person nearby.",
      routeTo: "crisis_resources",
    });
  }

  const text = (msg.content || [])
    .filter((b) => b.type === "text")
    .map((b) => b.text)
    .join("\n")
    .trim();

  if (!text) {
    return json(502, {
      status: "error",
      reason: "empty_completion",
      userMessage: "The generator returned nothing. Try again.",
    });
  }

  return json(200, {
    status: "ok",
    generation: {
      text,
      modelName: msg.model || model,
      promptVersion: body.promptVersion || "v1.0",
      tokensIn: msg.usage?.input_tokens ?? null,
      tokensOut: msg.usage?.output_tokens ?? null,
      latencyMs: elapsed,
    },
    quota: null,
  });
}

// ──────────────────────────────────────────────────────────────────────────────
// /v1/speak — text-to-speech via OpenAI TTS.
//
// Body:  { text: string, voice?: "shimmer"|"nova"|"alloy"|..., model?: "tts-1"|"tts-1-hd" }
// Response: audio/mpeg (MP3 bytes) on success; JSON error on failure.
//
// Sentence-length text — keep input under ~4000 chars. The Android client splits
// prayers into ~12-word chunks and asks for one chunk per call.
// ──────────────────────────────────────────────────────────────────────────────
async function handleSpeak(req, env) {
  let body;
  try {
    body = await req.json();
  } catch {
    return json(400, { reason: "invalid_json" });
  }

  const text = (body.text || "").trim();
  if (!text) return json(400, { reason: "empty_text" });
  if (text.length > 4000) return json(400, { reason: "text_too_long" });

  const model = body.model || env.OPENAI_TTS_MODEL || "tts-1";
  const voice = body.voice || env.OPENAI_TTS_VOICE || "shimmer";
  // Slower-than-default delivery suits prayer / chant pacing. 0.85 is the sweet spot.
  const speed = Number.isFinite(body.speed) ? body.speed : 0.95;

  let openaiResp;
  try {
    openaiResp = await fetch("https://api.openai.com/v1/audio/speech", {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${env.OPENAI_API_KEY}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model,
        input: text,
        voice,
        response_format: "mp3",
        speed,
      }),
    });
  } catch (err) {
    console.error("openai tts fetch failed", err);
    return json(502, { reason: "upstream_unreachable", userMessage: "Couldn't reach the voice service." });
  }

  if (!openaiResp.ok) {
    const errText = await openaiResp.text().catch(() => "");
    console.error(`openai tts ${openaiResp.status}: ${errText.slice(0, 500)}`);
    const lower = errText.toLowerCase();
    if (lower.includes("insufficient_quota") || lower.includes("exceeded your current quota")) {
      return json(502, {
        reason: "upstream_no_credit",
        userMessage: "Voice unavailable: server has no OpenAI credit.",
      });
    }
    return json(502, {
      reason: `upstream_${openaiResp.status}`,
      userMessage: "Couldn't generate audio.",
    });
  }

  // Stream the MP3 bytes through unchanged. Cloudflare lets the client cache.
  return new Response(openaiResp.body, {
    status: 200,
    headers: {
      "Content-Type": "audio/mpeg",
      "Cache-Control": "private, max-age=3600",
    },
  });
}

export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    if (request.method === "GET" && url.pathname === "/healthz") {
      return json(200, { ok: true, model: env.ANTHROPIC_MODEL || "claude-sonnet-4-6" });
    }

    if (request.method === "POST" && url.pathname === "/v1/generate") {
      if (!env.ANTHROPIC_API_KEY) {
        return json(500, { status: "error", reason: "missing_api_key", userMessage: "Server is misconfigured." });
      }
      return handleGenerate(request, env);
    }

    if (request.method === "POST" && url.pathname === "/v1/speak") {
      if (!env.OPENAI_API_KEY) {
        return json(500, { reason: "missing_openai_key", userMessage: "Voice not configured on the server." });
      }
      return handleSpeak(request, env);
    }

    return json(404, { status: "error", reason: "not_found" });
  },
};
