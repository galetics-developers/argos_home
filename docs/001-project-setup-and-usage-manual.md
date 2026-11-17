# Argos Home — Project Manual

This manual explains how to get `argos_home` running on your machine for the first time. Argos Home is a two-app project: an Android **phone** app (`:apps:mobile`) and a **Wear OS** watch app (`:apps:wear`) that communicate through the Google Wearable Data Layer.

---

## 1. Prerequisites

Install the following before you start:

| Tool | Required version | Notes |
| :-- | :-- | :-- |
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

Argos Home runs on two devices that must be **paired** so they can exchange messages. Both devices are required: the watch app sends its requests to the phone app, which owns the state of the day.

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

### The phone app must stay open

Both apps register their Data Layer listeners when they come to the foreground (`onResume`) and remove them when they leave it (`onPause`). This is the recommended behaviour for an interactive app — it keeps the apps from listening and consuming battery while nobody is looking at them — but it has a direct consequence for anyone trying the project:

> **The phone app has to be open and in the foreground for the watch to get an answer.** If the phone app is in the background or closed, there is nobody listening, the watch shows a send or sync failure and nothing changes. This is expected, not a defect.

So, while you are testing: leave the phone app on screen on the phone emulator and drive the interaction from the watch emulator.

---

## 6. Try the activity confirmation flow

The feature available today is **confirming a meal or a walk from the watch**. The phone owns the state; the watch asks and shows the answer.

**Before you start:** both emulators running and paired (section 4), both apps installed and **open**, with the phone app in the foreground (section 5).

Each app starts from the same five scheduled activities for the day, so both screens talk about the same items:

- The **phone** shows the `Calendar` day view with the five activity cards, each one with its status in text and a single action button (**Mark done** on a task, **Confirm** on an event).
- The **watch** shows its Home module with the next unresolved activity and three buttons: **CANCELED**, **DONE** and **CONFIRMED**.

To walk through the cycle:

1. On the watch, tap **DONE** on the activity shown. The watch switches to *Syncing with phone…* while it waits.
2. The watch sends the request to the phone, the phone validates it, applies it and replies with the resulting state.
3. The watch shows the answer, and after a moment moves on to the next unresolved activity.
4. Look at the phone: the matching card now shows the new status, updated without reopening or reloading the screen.

Things worth trying, because they show how the phone stays in charge:

- **Mark an activity done on the phone first, then try to resolve the same one from the watch.** The watch gets a rejection and shows who completed it and when; the card does not advance.
- **Tap `CONFIRMED` on a task.** Confirming only applies to events, so the button is shown disabled and sends nothing. Canceling, in turn, is only available from the watch.
- **Send the phone app to the background and tap a button on the watch.** You get an explicit failure message with the option to retry, and the displayed state stays as it was.
- **Resolve all five activities.** The watch shows its *Up to date* screen.

---

## 7. Common commands

| Action | Command |
| :-- | :-- |
| List modules | `./gradlew projects` |
| Build phone app (Debug) | `./gradlew :apps:mobile:assembleDebug` |
| Build watch app (Debug) | `./gradlew :apps:wear:assembleDebug` |
| Install on emulator | `./gradlew :apps:mobile:installDebug` / `:apps:wear:installDebug` |
| Run the Android linter | `./gradlew lint` |
| Clean build outputs | `./gradlew clean` |

> On Windows, use `gradlew.bat` (or `./gradlew` from Git Bash).

---

## 8. Troubleshooting

- **Gradle sync fails:** confirm the Android SDK is installed (SDK Manager) and that `local.properties` points to it. Re-run **File → Sync Project with Gradle Files**.
- **No device found when the watch sends a message:** make sure both emulators are running and paired, and that the phone app is open in the foreground.
- **The watch stays on "Syncing with phone…" or reports a failure:** the phone app is not in the foreground, or the pairing is not active. Bring the phone app back to the front and tap again to retry.
- **The phone card does not change after a watch action:** check that the watch reported an accepted result and not a rejection. A rejection means the phone refused the transition — for example because the activity was already resolved — and the card is left as it is on purpose.
- **Wrong app on a device:** `:apps:mobile` is for the phone and `:apps:wear` is for the watch; re-select the correct run configuration.

Both apps work entirely on the device pair: there is no server, no network access and no database, so nothing else needs to be set up.

For the repository layout, see [`002-repository-structure-guide.md`](002-repository-structure-guide.md). For the full product specification, see [`003-software-requirements-specification.md`](003-software-requirements-specification.md).
