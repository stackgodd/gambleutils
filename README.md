# GambleUtils v1 — Fabric Minecraft 26.2 source (preview)

**Development status: source draft. Not compiled, not game-tested.**

## What this first source release includes
- `G` opens a compact Auction screen.
- Select a held item's **name and stack count**, or type a name/quantity yourself.
- Enter **manual unit sell value** using `125`, `2.5k`, `3m`, `1b`; total = unit value × entered quantity.
- Start/stop a **client-side 1–3600 second auction countdown** with an on-screen bar.
- **Four simple appearance presets** (Midnight, Light, Emerald, Rose).
- Colour controls (hue/saturation/brightness buttons plus HEX input) for **accent, panel, text and progress bar**.
- Custom colours persist to `.minecraft/config/gambleutils.json`.
- No outbound HTTP/API calls, no authentication or account-file access.

## Critical limitations
- **DonutSMP automatic sell-price detection is NOT enabled in this draft.** `TooltipReader` is deliberately an empty placeholder: a genuine DonutSMP item tooltip is required before implementing and validating the actual detection path. This prevents fake numbers.
- A price shown by DonutSMP may refer to **one unit or an entire stack**. Do not multiply until that meaning is confirmed; manual input here is explicitly **per item**.
- No payment interception, bidding/queue features, original mod decompilation, or prior AHS source merge is included.
- The theme editor is a simple HSB/HEX picker, not a circular drag colour wheel; font mode and complete cosmetic layout editor are later work.
- This release targets **26.2 only**; 26.1.2 and 26.3 require separate builds/adaptation.

## Build on GitHub (no local Java required)
1. Create a new empty GitHub repository named `GambleUtils`, or upload files to a branch in your existing repository **without deleting the old project**.
2. Upload **the contents** of this ZIP (including `.github/workflows/build.yml`) to the repository root.
3. Click **Actions** → **Build GambleUtils v1** → **Run workflow**.
4. Check the build log. Only after successful build download the workflow artifact and extract its `.jar`.
5. Install with Minecraft 26.2 + matching Fabric Loader/Fabric API in a separate test instance.
6. Press `G` in game to open the menu.

If Actions fails, send the error lines. The source and dependency versions have not been compiled here.

## Privacy & licence
All original code written for this starter. No original compiled code or assets copied from Streamer Utility. MIT licensed; see LICENSE.
