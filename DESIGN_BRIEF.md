# Sacred Flow — Design Brief

A redesign brief for a UI/UX designer. Describes what every screen must do and contain, the current design system, and constraints to design within. The designer has freedom over visual style; structure and content are fixed by product requirements.

---

## 1. Product context

**Sacred Flow** is an Android app that generates personalized prayers, intentions, and reflections using AI (Claude Sonnet 4.6 via a backend proxy). It is explicitly **belief-neutral** — the user chooses what (if anything) they address (God, Universe, Higher Self, Ancestors, a custom word, etc.), and the app phrases content in their language. There is no doctrine, no community feed, no social layer. It is a quiet, single-player tool.

### Audience
Adults seeking a daily contemplative practice. Many will be skeptical of overtly religious apps or overtly "spiritual-aesthetic" apps (crystals, mandalas, sanskrit). The visual language should feel:

- **Calm, not corporate.** Not Headspace-cheerful, not enterprise-flat.
- **Warm, not mystical.** Avoid moons, stars, lotus, sacred geometry, "ethnic spiritual" iconography.
- **Quiet, not loud.** Sparse layouts. Generous whitespace. Restraint.
- **Personal, not performative.** No badges, streaks, share-to-feed, gamification.

### Core promise
"A quiet space for what matters to you." This tagline appears on the welcome screen and sets the tone for everything else.

---

## 2. What's broken about the current design (USER TO FILL IN)

> **Designer:** the product owner has flagged the current implementation as visually unsatisfying. Before designing, ask for specifics. Likely candidates based on the build:
> - Generic Material 3 chips and form layouts on the **Create** screen
> - Lack of distinctive visual identity beyond the color palette
> - **Result** screen reads like a generic AI app output, not a sacred artifact
> - **Onboarding** is functional but doesn't establish the contemplative mood
> - Bottom navigation feels utilitarian
>
> Ask the owner to mark up screenshots before starting.

---

## 3. Information architecture

```
Splash (auto-route)
 ├─ Onboarding (first launch only — 6 steps)
 │   1. Welcome
 │   2. Use case (Prayer / Intention / Gratitude / Healing / Reflection / Custom)
 │   3. Recipient (God / Universe / Nature / Higher Self / Divine Energy / Ancestors / The Sacred / Custom)
 │   4. Need (pick 1–2 of 12)
 │   5. Tone (Gentle / Hopeful / Thankful / Grounded / Powerful / Surrendering)
 │   6. Personal context (optional, 300 char max)
 │
 └─ Main app (4-tab bottom nav)
     ├─ Home (greeting + quota + recent saved prayers)
     ├─ Library (browse + filter + search saved prayers)
     ├─ Reminders (configure daily nudge)
     └─ Settings (theme, subscription, about)

Modal / full-screen flows:
 ├─ Create (form to start a new generation)
 ├─ Generation Loading (animated wait)
 ├─ Result (the generated prayer + actions)
 ├─ Prayer Detail (a saved prayer reopened)
 ├─ Practice (bottom sheet — "repeat after me" with TTS)
 ├─ Paywall (Sacred Flow Plus upsell)
 ├─ Help (about, disclaimer, FAQ)
 └─ Crisis Resources (shown if safety keywords detected in user input)
```

---

## 4. Screens — content & function specs

For each screen: **what it does**, **what it contains** (every piece of content/control), **what the user can do**, and **what states it can be in**.

### 4.1 Splash
- **Purpose:** Brief brand moment while the app decides where to route.
- **Content:** App name "Sacred Flow", centered.
- **Actions:** None.
- **States:** Always navigates away within ~1s.

### 4.2 Onboarding — Welcome
- **Purpose:** First impression. Set tone.
- **Content:**
  - Hero: "A quiet space for what matters to you."
  - Subhead: "Sacred Flow helps you put words to your prayers, intentions, and reflections — in whatever spiritual language is yours."
- **Actions:** "Begin" (primary, full-width).

### 4.3 Onboarding — Use Case
- **Purpose:** Choose what they're creating.
- **Content:**
  - Title: "What brings you here today?"
  - Subhead: "You can change this anytime."
  - 6 selectable cards: **Prayer** ("A reverent address"), **Intention** ("Setting a direction"), **Gratitude** ("Thanks for what is here"), **Healing** ("Words for what hurts"), **Reflection** ("Pause and notice"), **Custom** ("Your own way").
- **Actions:** Tap card → select. Back. Continue (disabled until selected).

### 4.4 Onboarding — Recipient
- **Purpose:** Choose what's being addressed. Foundational to belief-neutrality.
- **Content:**
  - Title: "Who or what do you address?"
  - Subhead: "This is just for you. There's no wrong answer."
  - 7 chips: God, Universe, Nature, Higher Self, Divine Energy, Ancestors, The Sacred. Plus "Custom…" chip.
  - If Custom selected: text field with placeholder "e.g., Source, Spirit, Mother Earth".
- **Actions:** Tap chip → select (single). Type custom. Back. Continue.

### 4.5 Onboarding — Need
- **Purpose:** Themes/qualities to invoke. Max 2 selections.
- **Content:**
  - Title: "What would you like to bring in?"
  - Subhead: "Pick one or two."
  - 12 chips: Peace, Healing, Clarity, Gratitude, Strength, Hope, Success, Abundance, Forgiveness, Protection, Joy, Courage.
- **Actions:** Toggle chip. Cap at 2. Back. Continue.

### 4.6 Onboarding — Tone
- **Purpose:** Emotional register of the output.
- **Content:**
  - Title: "How would you like it to feel?"
  - Subhead: "The mood of your reflection."
  - 6 swipeable cards (currently horizontal scroll), each with:
    - Name (Gentle / Hopeful / Thankful / Grounded / Powerful / Surrendering)
    - Descriptor (e.g., "soft and tender", "steady and clear")
    - Sample line in italic prayer style (e.g., "May the morning meet you kindly.")
- **Actions:** Swipe. Tap to select. Back. Continue.

### 4.7 Onboarding — Personal Context
- **Purpose:** Optional free-text to personalize the first generation.
- **Content:**
  - Title: "Anything specific on your mind?"
  - Subhead: "Optional. A few words help personalize what you receive."
  - Multi-line text field with placeholder examples: "Starting a new job tomorrow… / Missing my grandmother… / Healing after surgery…"
  - Char counter (max 300). Helper: "This stays on your device unless you choose to save it."
- **Actions:** Type. Submit ("Create my first reflection") or Skip (clears + submits). Back.
- **Edge case:** If text matches safety keywords (self-harm, crisis), auto-route to Crisis Resources.

### 4.8 Home
- **Purpose:** Daily landing. Returning users.
- **Content:**
  - Time-of-day greeting: "Good morning" / "Good afternoon" / "Good evening".
  - Subhead: "What would you like to bring into today?"
  - Primary CTA: "Create reflection" (disabled if quota exhausted).
  - Quota label: "3 of 5 free reflections left today" or "Unlimited with Sacred Flow Plus".
  - Section: "Recent" — horizontal scroll of saved prayer cards (preview text + recipient + relative date + favorite heart).
- **Actions:** Tap CTA → Create. Tap card → Detail.
- **States:**
  - No saved prayers yet → "Nothing yet. Your first saved reflection will appear here."
  - Quota exhausted → CTA disabled, shows "0 of 5".

### 4.9 Create
- **Purpose:** Full form to configure a generation. Power-user version of onboarding flow.
- **Content (sections in order):**
  1. **Type** — 6 chips (Use Case enum)
  2. **Addressed to** — 7 built-in chips + past custom recipients + "New custom…" chip → reveals text input
  3. **What you'd like to bring in** — 12 chips (Need), max 2
  4. **Tone** — 6 chips (Tone)
  5. **Length** — 3-segment toggle: Short (60–100 words) / Medium (120–180) / Long (220–320 — Plus-only, locked)
  6. **Personal context (optional)** — multi-line text, max 500 chars, char counter
  7. Text button: "Reset to defaults"
  - Pinned bottom: "Generate" button (changes to "Out of free — Sacred Flow Plus" when quota exhausted).
- **Actions:** All form inputs. Generate. Reset. Back.
- **States:** Invalid (button disabled) / Submitting / Quota exhausted.

### 4.10 Generation Loading
- **Purpose:** Wait state while AI generates (~3–8s).
- **Content:**
  - Centered animated "breathing circle" (scale 0.85→1.0, ~10s loop, gentle).
  - Rotating microcopy beneath: "Listening…" → "Choosing words…" → "Almost ready…" (changes every ~1.8s).
- **Actions:** None (auto-routes when ready). Minimum 2s display even if API is fast.
- **States:** Success → Result, Soft-blocked → Crisis Resources, Rate-limited → Paywall, Error → Result (with error UI).

### 4.11 Result
- **Purpose:** The generated prayer. The hero moment of the app.
- **Content:**
  - Small recipient badge at top: "TO UNIVERSE" (uppercase, tracked-out).
  - Offline banner (only if fallback content): "An offline reflection".
  - The prayer text itself — serif, italic, selectable. **This is the most important block in the entire app.** Reads like a quiet artifact.
  - Metadata footer: "Tone: Gentle · Need: Peace, Healing · Length: Medium".
  - Bottom action row (5 icons + labels): **Save**, **Practice** (TTS repeat-after-me), **Regenerate**, **Share**, **Copy**.
  - Bottom: "Done" primary button.
- **Actions:** All of the above + tapping saved badge opens Detail.
- **States:** Saved / Unsaved / Regenerating / Fallback (offline).
- **Critical:** This screen should feel sacred. The current implementation reads like a chat reply. It is the screen most worth redesigning.

### 4.12 Library
- **Purpose:** Browse, filter, search, manage saved prayers.
- **Content:**
  - Top bar: "Library" + Search icon.
  - In search mode: text field + close.
  - In selection mode: "N selected" + Delete + close.
  - Filter chips row: All / Favorites / Prayer / Intention / Gratitude / Healing / Reflection.
  - Vertical list of prayer cards (recipient badge, 120-char preview, date, favorite).
- **Actions:** Tap → Detail. Long-press → toggle selection. Multi-select delete (with undo snackbar).
- **States:** Empty (no saved) / Empty (search miss) / Empty (favorites filter) / Selection mode / Loading.

### 4.13 Prayer Detail
- **Purpose:** Reopen a saved prayer. Add private note. Manage.
- **Content:**
  - Back bar + Delete icon.
  - Recipient badge "TO DIVINE ENERGY".
  - Formatted date "May 23, 2024".
  - Full prayer text (serif italic, selectable).
  - Metadata chips: use case, tone, needs (each as a pill).
  - "PRIVATE NOTE" section — display text or "Add a private note." placeholder, with edit icon → text field + cancel/save.
  - Bottom action row: **Favorite**, **Practice**, **Share**, **Copy**.
  - Delete confirmation dialog: "Delete this prayer?" with undo affordance.
- **Actions:** All of the above.
- **States:** Editing note / Favorited / Confirming delete.

### 4.14 Practice (bottom sheet)
- **Purpose:** "Repeat after me" — TTS speaks one chunk, pauses 2.5s, next chunk.
- **Content:**
  - Header: "Repeat after me" + progress badge "3 / 8".
  - Phase label (changes during play): "LISTEN…" / "YOUR TURN" / "PAUSED" / "DONE" — tracked-out, primary color.
  - Big centered current chunk text (1–2 visual lines worth, ~12 words).
  - Controls: Restart / **Play-Pause** (large circular, primary fill) / Skip / Close.
- **Actions:** Play/pause toggle, skip line, restart, close.
- **States:** Speaking / Pausing (between chunks) / Paused (user) / Finished.
- **Constraints:** Bottom sheet, half-height. Must remain visually distinct from main screen yet calm.

### 4.15 Reminder
- **Purpose:** Schedule one daily reminder notification.
- **Content:**
  - Title: "Daily reflection". Subhead: "A gentle daily nudge can help build the practice."
  - "Enabled" toggle row.
  - Time picker (clock dial or wheel — Material 3 TimePicker).
  - "Days" section — 7 day chips (M T W T F S S, ISO order).
  - If permissions needed: rationale text.
  - Primary button: "Set reminder" or "Save changes". Text button: "Remove reminder".
- **Actions:** Toggle enabled. Set time. Toggle days. Save. Remove (with confirm).
- **States:** Disabled (controls grayed) / Needs permission / Editing existing / Saving.

### 4.16 Settings
- **Purpose:** Preferences, subscription, app info.
- **Content (sections):**
  1. **Practice** (default address, default tone, default type — read-only for v1).
  2. **Appearance** — Theme row (opens bottom sheet: System / Light / Dark). "Use system color" switch (Material You, Android 12+).
  3. **Subscription** — "Free plan" or "Sacred Flow Plus" row. Subtitle differs by state. Tap → Paywall.
  4. **Library** — "N total" reflections (read-only).
  5. **About** — "Help & disclaimer" → Help. "Privacy policy" (stub). Version text.
- **Actions:** Tap rows. Toggle switches.
- **States:** Plus subscriber vs. free.

### 4.17 Paywall
- **Purpose:** Sell Sacred Flow Plus.
- **Content:**
  - Close icon top-right.
  - Hero: "Keep the practice flowing."
  - Subhead: "Unlimited reflections, long-form prayers, gentle voice reading, and more daily reminders."
  - Feature list (6 rows with check icons): Unlimited reflections / Long-form prayers / Gentle voice reading (coming soon) / Multiple reminders / Beautiful share cards / Priority generation.
  - Two plan cards (Yearly with "Save 50%" badge / Monthly). Each shows title, price, badge.
  - Bottom: "Start Sacred Flow Plus" primary. "Restore purchases" text button. Helper: "Cancel anytime. Subscriptions auto-renew."
- **Actions:** Select plan. Purchase. Restore. Close.
- **States:** Subscribed (CTA → "Manage subscription") / Purchase in flight / Plan selection.

### 4.18 Help
- **Purpose:** About, disclaimer, FAQ, crisis link.
- **Content:**
  - Back bar.
  - **About** section + body.
  - **Disclaimer** boxed warning: "Sacred Flow generates text using AI based on your inputs. It is not a substitute for medical, mental health, religious, legal, or financial guidance. If you're in crisis, please reach out to a qualified professional."
  - "If you need support" clickable row → Crisis Resources.
  - **Frequently asked** section — 8 expandable rows (Q→A).
  - Footer: "Contact: hello@sacredflow.app".
- **Actions:** Expand/collapse FAQ. Tap support row.

### 4.19 Crisis Resources
- **Purpose:** Safety net. Shown if user input contains self-harm/crisis keywords.
- **Content:**
  - Back bar (back goes to Home).
  - Title: "Please take a moment."
  - Subhead: "What you shared sounds heavy. You don't have to carry it alone. If you're in crisis or thinking about hurting yourself, please talk to someone right now."
  - Regional resource cards (US, UK, Canada — each with org name, description, Call / Text / Web buttons).
  - Global card with "Find a helpline" button (links to findahelpline.com).
  - Bottom button: "When you're ready, come back" → Home.
- **Actions:** Call (dialer), Text (SMS), Web (browser), Return.
- **Tone:** This screen is **life-critical**. Visually it should be the calmest, most grounding screen in the app. No marketing. No flourishes. Big tap targets. High contrast. Reachable from anywhere.

---

## 5. Shared components

These appear across multiple screens. Designer should treat them as a small system to redesign coherently.

| Component | Purpose |
|---|---|
| **BackTopBar** | Single-line top app bar with back arrow + title + trailing action slot. Transparent. |
| **PrimaryButton** | Full-width, ~52dp tall, primary-color fill. Has loading spinner state. |
| **PrayerCard** | Horizontal card (~280dp wide). Recipient badge + truncated preview + date + heart. Long-press selectable. |
| **ToneCard** | Card (~220dp wide). Tone name + descriptor + sample line in italic serif. Selected border. |
| **BreathingCircle** | Animated circle (~160dp). Scales 0.85↔1.0 with opacity drift. ~10s loop. Used during generation. |
| **EmptyState** | Centered title + subtitle + optional action button. For "nothing yet" states. |
| **ErrorState** | Like EmptyState but with "Try again" button. For failed generations. |
| **SectionHeader** | Uppercase, tracked-out label. Delineates form sections. |
| **ChipCloud** | FlowRow of FilterChips with multi-select toggle. |
| **OfflineBanner** | Rounded box (secondary container fill) + cloud-off icon + "An offline reflection". |
| **SacredBottomBar** | 4-item bottom nav: Home / Library / Reminders / Settings. Material 3 NavigationBar. |

---

## 6. Current design tokens

The designer may keep, adjust, or replace these. They reflect the current implementation. Tokens defined in [app/src/main/java/com/sacredflow/app/ui/theme/](app/src/main/java/com/sacredflow/app/ui/theme/).

### Color (light mode)

| Role | Value | Description |
|---|---|---|
| Primary | `#7C6F5A` | warm taupe / brown |
| Primary container | `#EFE6D2` | warm beige |
| Secondary | `#8DA48E` | sage green |
| Secondary container | `#E2EBE0` | light mint |
| Tertiary | `#B98970` | rust / terracotta |
| Background | `#FAF7F1` | off-white / linen |
| Surface | `#FFFDF8` | near-white |
| Surface variant | `#EDE7DA` | warm gray |
| Outline | `#B5AB95` | light tan |
| Error | `#9C5A4E` | muted dark red |

Dark mode swaps to deep brown/sage on near-black backgrounds with light text. Full palette in [Color.kt](app/src/main/java/com/sacredflow/app/ui/theme/Color.kt).

### Typography

- **Serif:** Cormorant Garamond (Google Fonts) — used for prayer text, headlines.
- **Sans-serif:** Inter (Google Fonts) — used for UI, labels, buttons.

Roles include all Material 3 defaults plus two custom:
- **prayerText** — 22sp Cormorant Garamond italic, 34sp line height. Used for the full prayer body on Result and Detail.
- **recipientBadge** — 12sp Inter medium, 1.5sp letter spacing. Used for the "TO UNIVERSE" badge.

### Shape
8 / 12 / 16 / 24 / 32 dp corner radii (extra small → extra large).

### Spacing
4 / 8 / 16 / 24 / 32 / 48 / 64 dp scale.

---

## 7. Domain content the designer must accommodate

These enums drive form chips and copy throughout the app.

### Use Cases (6)
Prayer / Intention / Gratitude / Healing / Reflection / Custom

### Tones (6, with sample lines)
- **Gentle** — "May the morning meet you kindly."
- **Hopeful** — "Something good is on its way."
- **Thankful** — "For this breath, thank you."
- **Grounded** — "My feet are on the floor. I am here."
- **Powerful** — "I will not be smaller than I am."
- **Surrendering** — "I let go of what is not mine to carry."

### Needs (12, max 2 selectable)
Peace, Healing, Clarity, Gratitude, Strength, Hope, Success, Abundance, Forgiveness, Protection, Joy, Courage

### Recipients (7 + custom)
God / Universe / Nature / Higher Self / Divine Energy / Ancestors / The Sacred — plus user-entered Custom

### Lengths (3)
Short (60–100w) / Medium (120–180w) / Long (220–320w, Plus-only)

---

## 8. Constraints & non-functional requirements

- **Platform:** Android only (no iOS, no web). Built with Jetpack Compose + Material 3. Designer can hand back Figma; engineering will translate.
- **Min API 24** (Android 7.0). Most users on API 30+. Modern Compose patterns fine.
- **Dark mode required.** Equal effort to light mode.
- **Dynamic color (Material You)** is supported on Android 12+ as a user option. Brand colors must still feel coherent when Material You overrides them.
- **Accessibility:** All text must pass WCAG AA contrast. Tap targets ≥ 48dp. Content descriptions on icons. Don't rely on color alone to convey state.
- **No proprietary fonts.** Use Google Fonts only. (Currently Cormorant Garamond + Inter.) Designer may propose alternatives if better matches the tone — Lora, Cormorant, EB Garamond, Source Serif, Fraunces are reasonable swaps for the serif; Inter, IBM Plex Sans, Söhne (if free equivalent) for sans.
- **No third-party imagery.** No illustrations licensed from a stock site. Custom illustrations OK but optional.
- **Animations:** Subtle and slow. Default Compose easing is fine. Avoid bouncy or playful springs. The BreathingCircle's ~10s loop is the right pacing benchmark.
- **No notifications-style red dots, gamification badges, streak counters, or social affordances.**

---

## 9. Out of scope

The designer does **not** need to design:

- Marketing site, app store screenshots, social posts
- Notification visuals beyond a single line of text
- Widgets / lock screen / wear OS / TV
- iOS adaptations
- Adaptive icon (a separate task — designer can propose but it's not required)
- Audio waveforms or visualizations (Practice TTS feature shows text only, no waveform)

---

## 10. Deliverables wanted

In order of priority:

1. **High-fidelity mockups** of the 5 highest-impact screens in light + dark mode:
   - Home
   - Create
   - Generation Loading
   - **Result** (this is the hero — most important)
   - Prayer Detail
2. **Onboarding flow** (6 screens) in light mode.
3. **Secondary screens** in light mode: Library, Settings, Reminder, Paywall, Help, Crisis Resources, Practice sheet.
4. **Design system file** with:
   - Color tokens (light + dark)
   - Type ramp
   - Component states (button, chip, card, text field)
   - Icons (specify Material Symbols or custom set)
5. **Notes on motion** — any specific animations or transitions worth specifying.

Figma is the expected handoff format.

---

## 11. Direction & inspiration (designer suggestions)

The product owner asked: how do we make the app feel more attractive — more reverent, more "connected to something larger" — without becoming explicitly religious? The constraint is hard: belief-neutral. Crosses, mandalas, lotus, sanskrit, deity art are all off-limits. So the visual language has to evoke the sacred through **light, breath, paper, time, and typography** instead of iconography.

Concrete directions to explore:

### 11.1 Atmosphere over decoration

Treat the screen like a contemplative space, not a UI. Heavy lifting comes from:

- **Generous negative space.** The Result screen should look like a poem printed on a page, not a chat reply. 60%+ of the screen empty is correct.
- **Subtle paper texture** on background surfaces. Almost imperceptible grain. Look at how a real prayer book or letterpress card feels — the warmth comes from texture, not decoration.
- **Soft warm vignetting** at screen edges. Implied candlelight. Don't overdo — barely perceptible at 5–10% opacity.
- **Asymmetric, slightly-off-grid layouts.** Perfect grids feel corporate. Handset layouts that breathe slightly off-balance feel personal.

### 11.2 Typography as the main visual element

For an app whose core artifact is text, typography *is* the design.

- **Drop cap** on the first letter of the generated prayer. Large, serif, slightly offset, in primary color. This single move transforms the Result screen from "AI output" to "illuminated text."
- **Generous line-height** on prayer body (already 34sp on 22sp — keep or push further to 38sp). Prayers should be read like poetry, not paragraphs.
- **Large display headlines** with significant letter-spacing on welcome / section intros. Think editorial magazine.
- **Small caps + wide tracking** for badges and section labels (already in use — keep).
- **Two-font discipline.** One serif for prose/prayer, one sans for UI. Resist the urge to introduce a third.
- Try alternative serifs in mockups: **Fraunces**, **EB Garamond**, **Lora**, **Source Serif**, **Cormorant** (current). Fraunces has a softer, warmer feel; Source Serif reads more "modern editorial."

### 11.3 Light, breath, water, time — the non-religious metaphor set

Imagery and motion should reference universal human experiences of the sacred:

- **Light:** dawn gradient, candle flame, the way light falls through a window. Subtle warm-tinted backgrounds. Optional time-of-day shift (warm rose at sunrise, cool indigo at evening).
- **Breath:** the existing BreathingCircle is the right move. Apply the same slow inhale-exhale rhythm to other elements — a saved-heart that subtly pulses, an idle-state bottom bar that breathes.
- **Water:** subtle ripple at moments of completion (saving, generating). Almost imperceptible.
- **Time:** the prayer text could fade in word-by-word over ~2s on first display. Slow. Reverent. The user is reading something arriving, not just appearing.

### 11.4 Motion (slow, never bouncy)

The app's animation budget should feel like meditation pacing. Every default Compose spring should be replaced with slow ease-out curves.

Specific animation moves worth designing:

- **Prayer text reveal.** Word-by-word fade in on Result. ~80ms per word. Cap at 1.5s total. Then sit still.
- **Page transitions.** Slow cross-fades (~400ms) between screens instead of slides. Slides feel app-y; fades feel cinematic.
- **Bottom sheet (Practice).** Slow ease-out spring, ~500ms. Not bouncy.
- **Save heart.** Subtle scale 1.0 → 1.15 → 1.0 over ~600ms + faint warm glow ring that expands and fades. Not a Twitter heart explosion.
- **Generation breathing circle.** Already there. The visual benchmark.
- **Time-of-day background shift.** Top of Home screen background subtly tints with the hour. Warm linen by day, deeper bone by night. Slow.
- **Tone card selection.** Selected card slowly raises elevation + warmth tint over ~300ms. Unselected cards slightly fade.
- **Onboarding step transitions.** A faint horizontal drift (parallax) with cross-fade. Continuity, not jolt.

### 11.5 Sound (optional, opt-in only)

Not currently implemented but worth designing for:

- An **ambient mode** toggle in Settings: optional low-volume background (soft bells, gentle wind, ocean, single sustained note). Off by default. Pauses any active media on the device.
- **Haptic feedback** at meaningful moments: light tap on Save, light tap when a Practice line finishes, no haptic during normal navigation. Use sparingly.

### 11.6 Reference moods (apps/objects worth looking at)

- **Day One** (journaling app) — restraint, paper feel, serif headers, gentle UI. Very close to what Sacred Flow should feel like.
- **Calm** — for color and gradient work (not for layout — Calm's UI is too marketing-flavored).
- **Letterboxd** (lists, library, detail patterns) — surprisingly good at making text-driven content feel curated rather than utilitarian.
- **Apple Books** in reader mode — typography, paper, restraint.
- **The Marginalian** (brainpickings.org) — editorial typographic restraint with warmth.
- **Vintage prayer books / breviaries** — drop caps, marginalia, illuminated capitals. Steal the typographic moves, leave the religious iconography behind.
- **Printed letterpress greeting cards** — the warmth-without-fussiness benchmark.
- **Stoa** (philosophy app, since shut down) — example of belief-adjacent reflection done with restraint.

Apps to **avoid** referencing:

- Headspace, Calm marketing pages — too commercial-cheerful
- Hallow, Glorify, PrayApp — explicitly religious
- Any app with crystals, mandalas, "spiritual" stock illustration
- Anything with achievement badges, streaks, progress rings

### 11.7 Specific high-impact moves for the Result screen

This is the hero screen. Worth disproportionate design effort. Suggested moves:

1. **Drop cap** on the prayer's first letter — large serif, primary color.
2. **Word-by-word fade-in reveal** on first display.
3. **A tiny ornamental glyph** centered above or below the text — a single dot, a hairline rule, a small asterism (∴). Marks the prayer as a complete artifact.
4. **Tonal background** subtly tinted by the chosen Tone (gentle = warmest, surrendering = coolest, etc.) — barely perceptible but present.
5. **Action row recedes** until needed — visible but quiet, ~50% opacity until the prayer has been read (heuristic: after 3s of stable display, fade up to full opacity).
6. **No card containing the prayer.** No border, no shadow, no surface elevation. The prayer floats on the background — text as the only visual element. This single decision changes everything about how the screen reads.

### 11.8 Onboarding: the "first prayer" reveal

The end of onboarding currently jumps to the loading screen and then results. Worth designing the *first prayer* as a distinct moment from subsequent generations:

- Slower reveal (~2s vs. ~1s for subsequent ones)
- A one-time line above the prayer: "Here is the first one. It belongs to you."
- A gentle haptic at completion
- Save defaulted to **on** (auto-save first prayer to library, surface a small "Saved to your library" toast after the reveal)

This creates an emotional anchor on first use that subsequent generations don't need to repeat.

---

## 12. Open questions to confirm with product owner

Before the designer starts, get answers to these:

1. **What specifically feels off** about the current implementation? (Annotate screenshots if possible.)
2. **Brand references** — apps/sites whose feel you want to evoke? Apps to explicitly avoid?
3. **Is the warm taupe + sage palette keeper, or open to replacement?**
4. **Is Cormorant Garamond + Inter keeper, or open to replacement?**
5. **Any visual constraints beyond what's stated** (e.g., must work for an older user demographic, must be one-handed reachable)?
6. **Adaptive icon scope** — included or separate?
7. **Imagery** — any custom illustrations welcome, or strictly typographic + iconic?
