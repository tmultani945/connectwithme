# Launch checklist — Connect Yourself

Living document. Pre-launch state as of phase 16.

---

## ✅ Done in code

- [x] In-app Privacy Policy screen ([PrivacyPolicyScreen.kt](app/src/main/java/com/sacredflow/app/ui/screen/legal/PrivacyPolicyScreen.kt))
- [x] In-app Terms of Use screen ([TermsScreen.kt](app/src/main/java/com/sacredflow/app/ui/screen/legal/TermsScreen.kt))
- [x] Settings rows wire to legal screens
- [x] AI-content disclaimer footer on Result screen
- [x] Existing Help & disclaimer screen with crisis-resource link
- [x] In-app crisis routing when user input includes self-harm/danger language (worker + on-device)
- [x] Room migrations through v6
- [x] ProGuard rules for serialization / Room / Retrofit / Hilt
- [x] Release build is minified + resource-shrunk

---

## ⚠️ Before submitting to Play Store

### Legal — needs review

- [ ] **Have a lawyer review** [PrivacyPolicyScreen.kt](app/src/main/java/com/sacredflow/app/ui/screen/legal/PrivacyPolicyScreen.kt) and [TermsScreen.kt](app/src/main/java/com/sacredflow/app/ui/screen/legal/TermsScreen.kt). The drafts are a faithful starting point but should not ship without legal review, especially around AI-content disclaimers and EU GDPR / California CCPA compliance.
- [ ] **Host the policy at a public URL** (e.g., connectyourself.app/privacy). Play Console requires a URL — the in-app screen isn't enough.
- [ ] **Update contact email** in both legal screens if `hello@connectyourself.app` isn't set up yet.

### Account & infra

- [ ] **Buy the domain** connectyourself.app (or whatever final domain). The URL appears on the share card footer and in the legal docs.
- [ ] **Set up a Firebase project** for Crashlytics + Analytics. Add `google-services.json` to `app/`. Add Firebase dependencies (deferred — wasn't done in this phase to avoid forcing a Firebase project on you).
- [ ] **Create Google Play Console developer account** ($25 one-time).
- [ ] **Configure Cloudflare Worker production secrets** — ANTHROPIC_API_KEY, OPENAI_API_KEY — pointing at real, billed accounts. (Currently both are set; just verify before launch.)

### Play Console

- [ ] Create the app in Play Console.
- [ ] Upload signed AAB to **internal testing track** first.
- [ ] Fill out **Data Safety** form. Key points to declare:
  - Personal info: optional name (stored on device, sent to LLM provider per request).
  - Activity: prayer text + topic + tone selections sent to LLM provider per request.
  - Device identifier: anonymous install UUID for rate-limiting only.
  - No tracking across apps. No advertising.
- [ ] Submit **Permissions Declaration form** for `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` — justify with the reminder feature.
- [ ] **Content rating questionnaire** — answer truthfully about user-generated content + AI generation.
- [ ] **Subscription products** — create `sacredflow_plus_monthly` ($4.99) and `sacredflow_plus_yearly` ($29.99) products. Match the IDs in [BillingProducts](app/src/main/java/com/sacredflow/app/billing).
- [ ] **Beta test cohort** — recruit 10-20 friendly testers, get them on the closed-testing track for at least 1 week before public launch.

### Store listing assets — see [STORE_LISTING.md](STORE_LISTING.md)

- [ ] App icon (already done — Cadillac Mountain sunrise webp)
- [ ] Feature graphic (1024×500 PNG) — design needed
- [ ] 8 screenshots (1080×1920 PNG) — see Store Listing doc for shot list
- [ ] Short description (80 chars)
- [ ] Long description (4000 chars max)

### Code

- [ ] **Restore Length.Long → isPlusOnly = true** in [Length.kt](app/src/main/java/com/sacredflow/app/domain/model/Length.kt) (currently unlocked for testing per phase 12).
- [ ] **Sign the release build** with an upload key. Generate the keystore once, keep it safe; Play Store requires every subsequent upload to use the same key.
- [ ] **Test the release build on at least one real device** before uploading to Play. Minification can break things ProGuard rules don't cover.
- [ ] **Verify versionCode increments** in [build.gradle.kts](app/build.gradle.kts) for each upload. Currently `versionCode = 1`.

---

## 🌱 First-week post-launch

- [ ] Watch Firebase Crashlytics for any release-build crashes.
- [ ] Watch the worker logs (Cloudflare tail) for unusual API spend or refusals.
- [ ] Read every review under 4 stars in the first week.
- [ ] Reach out personally to ~5 users who left detailed feedback.
- [ ] Tag the v1.0.0 commit in git.

---

## Outside-of-code instructions

### Generate the upload signing key

```bash
keytool -genkey -v -keystore upload-keystore.jks -alias upload -keyalg RSA -keysize 2048 -validity 25000
```

Store the keystore + password somewhere safe (1Password, etc.). Add a `~/.gradle/gradle.properties` with:
```
CONNECT_YOURSELF_UPLOAD_STORE_FILE=/path/to/upload-keystore.jks
CONNECT_YOURSELF_UPLOAD_STORE_PASSWORD=...
CONNECT_YOURSELF_UPLOAD_KEY_ALIAS=upload
CONNECT_YOURSELF_UPLOAD_KEY_PASSWORD=...
```
Then wire `signingConfigs { release { ... } }` in [build.gradle.kts](app/build.gradle.kts).

### Build a release AAB

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`. Upload that to Play Console.
