# AppFactory

AppFactory is a reusable starter template for building **mobile apps and websites** from one TypeScript codebase. It is based on Expo, React Native, Next.js, and React Native Web.

## Start a separate project

1. Open the [AppFactory repository](https://github.com/hotshooter14-png/AppFactory).
2. Tap **Use this template** → **Create a new repository**.
3. Give the new repository a unique name for your app or website.
4. Choose **Private** if you want the new project's code to stay private.
5. Create the repository and make future changes in that new repository.

**Keep AppFactory as the clean starter.** Make each finished app or website in its own repository so projects stay separate. Creating a repository from this template copies the starter files; later changes to one project do not automatically change the others.

For a phone-friendly checklist, see [Starting New Projects](docs/STARTING-NEW-PROJECTS.md).

## What's included

- `apps/native`: React Native app built with [Expo](https://docs.expo.dev/)
- `apps/web`: [Next.js](https://nextjs.org/) app using [React Native Web](https://necolas.github.io/react-native-web/)
- `packages/ui`: shared UI components for web and native
- `packages/app`: shared app logic for web and native

## Technologies

- [Expo](https://docs.expo.dev/) for native development
- [Next.js](https://nextjs.org/) for web development
- [React Native](https://reactnative.dev/) and [React Native Web](https://necolas.github.io/react-native-web/)
- [NativeWind](https://www.nativewind.dev/) styling
- [TypeScript](https://www.typescriptlang.org/)
- [Prettier](https://prettier.io/) formatting
- [Turborepo](https://turborepo.dev/) monorepo build system

## Local development

This project uses Node.js and Yarn Classic (v1.22.19). A computer or cloud development environment is normally needed to install dependencies and run the development commands.

```sh
yarn
yarn dev
yarn build
```

These commands are documented from the starter configuration; run them in a suitable Node.js environment and review any build errors before publishing an app or site.

## Credits

AppFactory is based on the [Universal App Starter](https://github.com/adebayoileri/rnw-starter-private) by [Adebayo Ilerioluwa](https://github.com/adebayoileri). The underlying framework and dependencies retain their original licenses.
