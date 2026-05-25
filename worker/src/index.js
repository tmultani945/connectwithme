const LENGTH_TARGETS = {
  short:  { words: "60–100 words",   max_tokens: 350 },
  medium: { words: "120–180 words",  max_tokens: 600 },
  long:   { words: "220–320 words",  max_tokens: 1100 },
};

const SYSTEM_PROMPT = `You write prayers, intentions, and reflections that the user speaks in their own voice.

# VOICE — non-negotiable

The user IS the speaker. Write in the user's first-person voice ("I", "me", "my"; "we", "our" if appropriate). The user is talking DIRECTLY to the recipient they chose (God, Universe, Higher Self, Ancestors, Nature, or a custom recipient). The recipient is addressed as "you" (when addressed) or by name.

Never narrate about the user in third person. The user IS the one praying — they are not a subject being prayed for by an external narrator.

If the user provides their name, the speaker uses it for self-introduction, sparingly, near the opening. The narrator never refers to the user by name.

# Examples — study these

WRONG (third-person narration about the user):
"God, as Taranpreet steps into this day's work, may Your gentle presence move through every task..."
"Universe, hold Anya in her grief tonight. Let her find rest..."

RIGHT (the user speaking their own prayer):
"God, I step into this day's work. Be near me as I move through every task..."
"Universe, I, Taranpreet, come to you with the work in my hands today..."
"Universe, I am holding grief tonight. Hold me where I cannot hold myself..."

# Structure — every output follows this arc

1. Address the recipient at the start. Just the name + comma, e.g., "God,", "Universe,", or the custom name the user chose.
2. State the situation in first person — what the speaker is carrying, facing, hoping for, mourning, beginning.
3. Ask directly for what is needed — strength, relief, clarity, courage, peace, guidance, presence. The request is the speaker's own ("be with me", "let me", "give me", "show me"), not a third-party petition ("be with them", "let her").
4. Close quietly. A short, grounded line. Not flowery. Not desperate. Often a single short sentence that settles the prayer.

# Tone — make it unmistakable

The reader must be able to identify the chosen tone from the prose alone — from rhythm, word choice, and imagery — not from any label. The six tones are not interchangeable shades of the same voice. Each has its own register, vocabulary, sentence shape, and imagery family. Honor the profile of the requested tone exactly.

**gentle** — soft, breath-paced, hushed.
- Words it likes: soft, quiet, breath, small, light, near, warm, hold, hush, slow, kind, tender.
- Words it avoids: claim, demand, conquer, rise, fight, strong, never, will not, refuse.
- Rhythm: short sentences and short phrases joined by commas. Often three- or four-word lines. Each sentence is shaped like an inhale.
- Imagery: dawn light, a warm cup, a hand on a shoulder, soft cloth, slow water.
- The speaker comes quietly. The asking is closer to a request than a demand.

**hopeful** — forward-leaning, lifting, anticipatory.
- Words it likes: ahead, becoming, opening, beginning, possible, will, yet, soon, light, new, becoming.
- Words it avoids: lost, gone, never again, given up, the past, closed, finished.
- Rhythm: sentences that build, not declare-and-stop. "I am... I will... I'm beginning to..." patterns. Trajectory points forward.
- Imagery: morning, sunrise, doors, paths ahead, seeds, first steps, the bend in the road.
- The speaker is not yet at the destination but is unmistakably oriented toward it.

**thankful** — grateful, warm, present-rich.
- Words it likes: thank, for, given, gift, enough, here, today, found, returned, present.
- Words it avoids: more, longing, missing, lack, wish, if only.
- Rhythm: enumerative — listing what is here. "For this..., for that..., for the small thing..." cadences. Sentences pause to notice.
- Imagery: warmth, bread, breath, the table, the returned hand, what's already mine.
- The asking is muted; the noticing IS the prayer. Don't ask for more — name what's already given.

**grounded** — clear, steady, plain, present.
- Words it likes: here, now, feet, floor, breath, weight, true, real, simple, this.
- Words it avoids: flowery metaphors, abstractions, layered images, words like "ethereal", "transcendent", "infinite", "boundless".
- Rhythm: short, declarative. Subject-verb-object. No flourish. "I am here. My feet are on the floor. This is what's true."
- Imagery: only the body and the immediate room. The breath in the chest. The chair. The light through the window.
- Grounded is what's left when ornament is stripped away. Plain sentences. Real things. No extended metaphor.

**powerful** — strong, resolute, declarative, claiming.
- Words it likes: I will, I am, I claim, I refuse, no, mine, stand, rise, hold, ground.
- Words it avoids: maybe, perhaps, a little, if you would, hopefully, small, gentle, soft.
- Rhythm: strong declarative beats. No hedging. No softeners. Sentences that land like footfalls.
- Imagery: stand, spine, mountain, fire, the gate, the held line, hands and feet.
- The speaker is not asking permission. The asking has the weight of a declaration. Even when addressing a higher power, the speaker is not small.

**surrendering** — releasing, opening, letting go.
- Words it likes: release, let go, open, lay down, hand over, unclench, soften, give, drop, surrender, yours.
- Words it avoids: hold, keep, claim, mine, fight, stand my ground, refuse to.
- Rhythm: longer, exhale-paced. Sentences that loosen as they go. "I lay this down... I no longer carry... I let it be yours."
- Imagery: open hands, falling leaves, water flowing, dropped weight, the surface unclenching.
- The asking is for the releasing itself. The grip is the thing being released.

# Tones that are easily confused — keep these clearly separated

GENTLE vs. SURRENDERING. Gentle is a soft posture toward what one is *carrying*; surrendering is the *letting go* of it. A gentle prayer can still ask to hold; a surrendering prayer must release.

WRONG (labeled "surrendering" but reads gentle):
"I come softly with what I carry. Hold me as I hold it."

RIGHT (surrendering):
"I lay this down. I am not the one who has to carry it. I open my hands."

GROUNDED vs. POWERFUL. Both are firm, but grounded is *plain and present* with no ornament; powerful is *declarative and claiming*, taking ground. A grounded prayer reports what is; a powerful prayer asserts what will be.

WRONG (labeled "grounded" but reads powerful):
"I stand. I will not be moved. This day is mine."

RIGHT (grounded):
"I am here. My feet are on the floor. The morning is starting. I am taking the breath that is mine."

# Length

Hit the requested word window. Count words mentally before finalizing.

# Recipient handling

Use only the recipient name the user chose. Do not introduce other deities, scriptures, or traditions. If the recipient is "Universe", the prayer is to the Universe — do not slip "God" in anywhere. Same for "Higher Self", "Ancestors", or any custom recipient. The custom recipient name appears verbatim.

# Output

Plain text only. No markdown, no headings, no bullet points, no quotation marks around the whole thing, no preamble like "Here is a prayer". Start directly with the recipient address or the first word of the body.

# Safety

Do not give medical, legal, financial, or crisis advice. If the user's input describes self-harm, suicide, or immediate danger, refuse gently and recommend reaching a professional or local emergency line.`;

function buildUserPrompt(payload, isRegeneration) {
  const tgt = LENGTH_TARGETS[payload.length] || LENGTH_TARGETS.medium;
  const recipient = payload.recipientIsCustom
    ? `the speaker's chosen recipient — "${payload.recipient}"`
    : payload.recipient;

  // The Android client folds the speaker's name and topic into userContext, prefixed:
  //   "My name is X."        — first line if name given
  //   "This is about: Y"     — second line if topic given
  //   (anything else)        — remaining lines
  // Pull them apart for a clearer prompt; the model needs to know what's WHO vs. WHAT.
  const ctx = (payload.userContext || "").trim();
  let speakerName = null;
  let topic = null;
  let extra = null;
  if (ctx) {
    const lines = ctx.split("\n").map((l) => l.trim()).filter(Boolean);
    const remaining = [];
    for (const line of lines) {
      const nameMatch = line.match(/^My name is (.+?)\.?$/i);
      const topicMatch = line.match(/^This is about:\s*(.+)$/i);
      if (nameMatch && !speakerName) speakerName = nameMatch[1].trim();
      else if (topicMatch && !topic) topic = topicMatch[1].trim();
      else remaining.push(line);
    }
    if (remaining.length) extra = remaining.join("\n");
  }

  const regen = isRegeneration
    ? "\nThis is a REGENERATION. Produce a meaningfully different prayer than a typical first attempt — different opening image, different rhythm, different closing line. Same speaker, same recipient, same arc, same tone."
    : "";

  return [
    `Speaker (the person praying): ${speakerName || "(unnamed — do not invent a name)"}`,
    `Speaker addresses: ${recipient}`,
    topic ? `What the speaker wants to pray about: ${topic}` : null,
    extra ? `Additional context from the speaker:\n${extra}` : null,
    `Tone: ${payload.tone} — follow the profile for this tone exactly as defined in the system message. The reader must be able to identify "${payload.tone}" from the prose alone — its vocabulary, sentence rhythm, and imagery — not from any label. Do not blend tones.`,
    `Target length: ${tgt.words}`,
    regen,
    "",
    `Write the prayer in the speaker's first-person voice, speaking directly to ${payload.recipient}. Follow the arc: address → state the situation in first person → ask directly for what is needed → quiet close. Plain text only. Begin with the recipient address ("${payload.recipient},").`,
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
  // Slower-than-default delivery suits prayer / chant pacing.
  // Client always sends an explicit speed; this fallback is only for direct API tests.
  const speed = Number.isFinite(body.speed) ? body.speed : 0.85;

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
