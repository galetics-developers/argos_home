# Argos Home — Project Manual

This manual explains how to get `argos_home` running on your machine for the first time. Argos Home is a two-app project: an Android **phone** app (`:apps:mobile`) and a **Wear OS** watch app (`:apps:wear`) that communicate through the Google Wearable Data Layer.

---

## 1. Prerequisites

Install the following before you start:

| Tool | Required version | Notes |
|------|------------------|-------|
| Android Studio | Recent stable version | Includes the JDK and the Gradle wrapper. |
| Android SDK | API 37 (compile/target), API 34 minimum | Install from Android Studio → **SDK Manager**. |
| Git | any | Verify with `git --version`. |

No separate JDK installation is required: Android Studio bundles the one the build uses. The Android SDK location is written by Android Studio into `local.properties`, which is **not** versioned.

---

## 2. Get the source code

Clone the repository and enter it:

```bash
git clone https://github.com/YOUR-USERNAME/argos_home.git
```

```bash
cd argos_home
```

---

## 3. Open the project in Android Studio

1. Launch Android Studio.
2. Choose **File → Open** and select the `argos_home` folder.
3. Wait for the **Gradle sync** to finish without errors. Android Studio downloads the dependencies declared in `gradle/libs.versions.toml` and creates `local.properties` with your SDK path.

To confirm the modules resolve, you can run:

```bash
./gradlew projects
```

You should see `:apps:mobile` and `:apps:wear` in the project list.

---

## 4. Create and pair the emulators

Argos Home runs on two devices that must be **paired** so they can exchange messages.

1. Open **Tools → Device Manager**.
2. Create a **phone** emulator: a Medium Phone with a system image that **includes Google Play**.
3. Create a **watch** emulator: a Wear OS Small Round image (API 34).
4. From the watch entry's menu, choose **Pair Wearable** and select the phone emulator. A chain-link icon confirms the pairing.

> The phone app and the watch app are different modules. Installing the phone app on the watch (or vice versa) will show a `uses-feature` warning; always run each module on its matching device.

---

## 5. Run the apps

1. Start both emulators.
2. Select the **`:apps:mobile`** run configuration and run it on the **phone** emulator. Keep it open in the foreground.
3. Select the **`:apps:wear`** run configuration and run it on the **watch** emulator.

From the command line you can build both apps with:

```bash
./gradlew :apps:mobile:assembleDebug :apps:wear:assembleDebug
```

Or install them on running emulators with:

```bash
./gradlew :apps:mobile:installDebug :apps:wear:installDebug
```

---

## 6. Common commands

| Action | Command |
|--------|---------|
| List modules | `./gradlew projects` |
| Build phone app (Debug) | `./gradlew :apps:mobile:assembleDebug` |
| Build watch app (Debug) | `./gradlew :apps:wear:assembleDebug` |
| Install on emulator | `./gradlew :apps:mobile:installDebug` / `:apps:wear:installDebug` |
| Run the Android linter | `./gradlew lint` |
| Clean build outputs | `./gradlew clean` |

> On Windows, use `gradlew.bat` (or `./gradlew` from Git Bash).

---

## 7. Troubleshooting

- **Gradle sync fails:** confirm the Android SDK is installed (SDK Manager) and that `local.properties` points to it. Re-run **File → Sync Project with Gradle Files**.
- **No device found when the watch sends a message:** make sure both emulators are running and paired, and that the phone app is open in the foreground.
- **Wrong app on a device:** `:apps:mobile` is for the phone and `:apps:wear` is for the watch; re-select the correct run configuration.

For the repository layout, see [`002-repository-structure-guide.md`](002-repository-structure-guide.md). For the full product specification, see [`003-software-requirements-specification.md`](003-software-requirements-specification.md).
