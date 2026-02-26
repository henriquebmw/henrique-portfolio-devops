# MelodyMind Android App

MelodyMind is an experimental mobile application that blends **Spotify music playback**, **audio analysis**, and **AI-powered creativity**. The goal is to help musicians and music lovers explore songs, analyze tempo/key, generate chord suggestions and lyrics, and even invent new tunes.

## Key Features

- 🔍 **Spotify Search** – Browse Spotify tracks using the official web API, listen to previews, and analyze selected songs.
- 🎧 **Real‑time Audio Analyzer** – Capture live microphone audio or preview playback to estimate **BPM**, **musical key**, and display a waveform.
- 🎼 **Chord Suggestions** – Based on the detected key/mode, MelodyMind proposes a four‑bar chord progression; you can also transpose chord lists by semitones using the built‑in helper.
- ✍️ **AI Assistant** – Generate song lyrics, chord progressions, and transpose keys with a few taps using OpenAI.
- 💡 **AI Chat** – Ask MelodyMind for creative ideas or small talk about music.
- 🎶 **Song Invention** – Start with a Spotify entry and let the AI invent new melodies and lyrics.
- 📦 **Offline Search Cache** – Spotify queries are cached in memory during a session so you can re‑visit recent results without hitting the network again.

## Download

- **Debug APK:** Available from the [GitHub Actions artifacts](https://github.com/henrique-portfolio-devops/henrique-portfolio-devops/actions/workflows/android-ci.yml). Look for `MelodyMind-debug-*.apk` under the latest successful run.
- **Release APK:** Similarly available under the release artifacts (`MelodyMind-release-*.apk`).

> 🔗 If this repository is forked or the CI workflow is renamed, adjust the URL accordingly or build the project locally (see instructions below).

## Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/henrique-portfolio-devops/henrique-portfolio-devops.git
   cd henrique-portfolio-devops/projects/MelodyMind
   ```
2. Open the project in Android Studio (Arctic Fox or later) and let Gradle sync.
3. Add your **Spotify** and **OpenAI** API keys to `local.properties`:
   ```properties
   SPOTIFY_CLIENT_ID=...
   SPOTIFY_CLIENT_SECRET=...
   OPENAI_API_KEY=...
   ```

   The app will also prompt for Spotify credentials if it doesn't find any at startup. You can
   enter them on the fly; they will be stored in shared preferences so you won't need to
   rebuild. OpenAI key can likewise be entered via code or future settings screen.

   Defaults from `buildConfig` are used when available, so you can bake in test accounts
   for quick experimentation.
4. Build and run on a device or emulator (minSdk 24, targetSdk 36).

## Notes & Roadmap

- The app is a work‑in‑progress. Some UI elements are placeholders and sensors (e.g. tilt cards) are experimental.
- Audio processing runs entirely on‑device and should be efficient for short clips or streams.
- Offline Spotify caching (in‑memory) and basic key transposition are now implemented. Playback transposition and more advanced AI composition helpers will follow.
Enjoy exploring music with MelodyMind! 🎶
