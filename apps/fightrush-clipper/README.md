# FightRush Clipper Studio

Standalone Android app project inside the AppFactory monorepo. It does not replace or modify the existing StrideFit app.

## Current first build

- Choose a local video from Android storage.
- Scan sampled frames for visual motion and select a high-motion window.
- Export that window as an MP4 using AndroidX Media3 Transformer.
- Preserve the source video's aspect ratio; no forced 9:16 conversion.
- Configurable maximum window length (default 155 seconds).
- Intro duration control defaults to 35 seconds.

## Important limitations

This is an early MVP. The current export creates one selected high-motion segment. The intro-duration control is not yet assembled into the exported video, and the walkout/face-off controls are preferences only; the app does not yet detect those scenes. Motion scanning is a visual heuristic, not a trained MMA action-recognition model. YouTube/Facebook URL importing and multi-segment highlight assembly are not implemented yet.

## Build

The GitHub Actions workflow builds a debug APK whenever this folder changes and publishes the APK as a prerelease asset. You can install the debug APK on a compatible Android device after downloading it from the repository's Releases page.
