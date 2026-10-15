# Nova AI — Android-friendly prototype

This is the first browser-based prototype for a personal AI assistant. It is intentionally separate from the AppFactory default branch and lives on the `ai-assistant-prototype` branch.

## Features in this prototype
- Mobile-first chat UI
- Connects to Google's Gemini API using a key you provide
- New chat button
- Text/Markdown/CSV/JSON file context (text extraction only)
- Browser voice input when supported, plus optional spoken replies
- No chat history is uploaded to this repository; conversation memory currently lasts only for the open page

## Run it
Open `index.html` in a browser, or publish the `ai-assistant/` folder with a static hosting service. On first use, open Settings and paste a Gemini API key from https://aistudio.google.com/apikey.

## Important limitations
- This is a prototype, not a packaged Android APK.
- It uses an API key in browser requests. Keep your key private and restrict/monitor it in Google AI Studio. This prototype keeps the key in session storage for the current tab, but browser-based keys are not suitable for a public production app.
- Free API access, models, regional availability, and quotas are controlled by Google and can change.
- Uploaded files are read as text; PDFs, images, and audio are not interpreted in this first version.
- Voice input depends on browser support and permissions.
- Do not use this as a source of professional medical, legal, financial, or other high-stakes advice.

## Next steps
1. Test the UI and Gemini connection.
2. Add a secure backend so the API key is not exposed in the browser.
3. Add image/PDF support and saved chat history.
4. Build and test the native Android APK in a cloud build environment.
