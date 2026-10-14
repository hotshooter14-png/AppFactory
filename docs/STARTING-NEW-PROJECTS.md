# Starting New Projects with AppFactory

Use AppFactory as the reusable starting point. Give every real app or website its own repository.

## Create a project on your phone

1. Sign in to GitHub and open [AppFactory](https://github.com/hotshooter14-png/AppFactory).
2. Tap **Use this template** and then **Create a new repository**.
3. Enter a repository name that describes the project, such as `my-fitness-app` or `my-cafe-website`.
4. Choose **Private** unless you specifically want the code to be public.
5. Tap **Create repository**.
6. Open the newly created repository. Make project-specific edits there, not in AppFactory.

## Keep projects separate

- Keep AppFactory as the reusable base.
- Use one new repository per app or website.
- A change made in a template-created repository does not automatically update AppFactory or other projects.
- Do not copy API keys, passwords, private customer data, or other secrets into source files. Use appropriate secrets/configuration for your build or hosting service.
- Before publishing, test the project and check its license, permissions, privacy disclosures, and platform requirements.

## Development note

The starter includes an Expo/React Native app and a Next.js web app. Running it locally generally requires Node.js and Yarn, which may be difficult to set up on an Android phone alone. You can use a compatible cloud development environment or build service if available; review its limits and privacy terms first.

## Starter layout

- `apps/native`: mobile app
- `apps/web`: website
- `packages/ui`: shared UI components
- `packages/app`: shared app logic
