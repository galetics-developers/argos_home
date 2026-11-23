# Argos Home: Project Manual

This manual explains how to get `argos_home` running on your machine for the first time and how to walk through the features of the first version. Argos Home is a two-app project: an Android **phone** app (`:apps:mobile`) and a **Wear OS** watch app (`:apps:wear`) that communicate through the Google Wearable Data Layer.

---

## 1. Prerequisites

Install the following before you start:

| Tool | Required version | Notes |
| :-- | :-- | :-- |
| Android Studio | Recent stable version | Includes the JDK and the Gradle wrapper. |
| Android SDK | API 37 (compile/target), API 34 minimum | Install from Android Studio, **SDK Manager**. |
| Git | any | Verify with `git --version`. |

No separate JDK installation is required: Android Studio bundles the one the build uses. The Android SDK location is written by Android Studio into `local.properties`, which is **not** versioned.

---

## 2. Get the source code

Clone the repository and enter it:

```bash
git clone https://github.com/galetics-developers/argos_home.git
```

```bash
cd argos_home
```

---

## 3. Open the project in Android Studio

1. Launch Android Studio.
2. Choose **File > Open** and select the `argos_home` folder.
3. Wait for the **Gradle sync** to finish without errors. Android Studio downloads the dependencies declared in `gradle/libs.versions.toml` and creates `local.properties` with your SDK path.

To confirm the modules resolve, you can run:

```bash
./gradlew projects
```

You should see `:apps:mobile` and `:apps:wear` in the project list.

---

## 4. Create and pair the emulators

Argos Home runs on two devices that must be **paired** so they can exchange messages. Both devices are required: the watch app sends its requests to the phone app, which owns the state of every activity.

1. Open **Tools > Device Manager**.
2. Create a **phone** emulator: a Medium Phone with a system image that **includes Google Play**.
3. Create a **watch** emulator: a Wear OS Small Round image (API 34).
4. From the watch entry's menu, choose **Pair Wearable** and select the phone emulator. A chain-link icon confirms the pairing.

> The phone app and the watch app are different modules. Installing the phone app on the watch (or the other way around) shows a `uses-feature` warning; always run each module on its matching device.

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

Both apps register their Data Layer listeners when they come to the foreground (`onResume`) and remove them when they leave it (`onPause`). This is the recommended behavior for an interactive app, because it keeps the apps from listening and consuming battery while nobody is looking at them, but it has a direct consequence for anyone trying the project:

> **The phone app has to be open and in the foreground for the watch to get an answer.** If the phone app is in the background or closed, there is nobody listening, the watch shows a connection error and nothing changes. This is expected, not a defect.

So, while you are testing: leave the phone app on screen on the phone emulator and drive the interaction from the watch emulator.

### About the sample data

Both apps start from the same sample household, generated in memory every time they launch. Its activities are spread over the weeks around the current date and cover every state, so each screen has something to show. Nothing is saved: closing an app discards every change and the next launch starts from the same sample again.

---

## 6. Walk through the phone app

The phone app opens on **Home**. The bottom navigation bar switches between **Home**, **Pets**, **Calendar**, **Household** and **Profile**. The bell in the top bar of Home, Pets, Calendar and Profile opens **Notifications**; the system back gesture closes it. A full description of every screen and modal is in [`005-application-modules-guide.md`](005-application-modules-guide.md).

### 6.1. Home

1. Read the greeting and the day's progress ring: the ring shows the share of today's activities that are done, and the counters show the total, pending and completed activities and the next event.
2. Swipe the **Care by pet** carousel. **Double tap** a pet card to open that pet's profile in Pets.
3. Scroll the **Upcoming** list. **Double tap** an activity to open it in the Calendar Day view on its own date.
4. Tap the household pill in the header to open **Switch household** and pick another household. In this version switching only changes the label; the data stays the same.

### 6.2. Pets

1. The list shows every pet with its progress for the day. Tap a pet to open its profile.
2. In the profile, switch between the **Care schedule** tab (the pet's activities and who confirmed them) and the **Health records** tab (the medical history, newest first).
3. On the Health records tab, tap **Add health record**, choose the record type (vaccine, medication or procedure), fill the title and date, and save. The record shows as *Pending* or *Registered* depending on its date.
4. Tap the pencil in the top bar to **edit** the pet, or go back to the list and tap **Add pet** to create one. Only the name is required.
5. Tap **Delete pet** and confirm the dialog to remove a pet and all of its records.

### 6.3. Calendar

1. Use the **Month / Week / Day** selector to change the view. In Month, tap a day to open it in Day view.
2. Use **Filter by Pet** to show the activities of one pet only.
3. On an activity card, tap one of the actions it offers: **Cancel**, **Done** or **Confirm**. A card only shows the actions that are legal for its current state, and a resolved card shows none.
4. Tap **New event** to open the **New activity** modal. Choose **Task** or **Event**, fill the title, pet and time (an event also needs an end time after its start time) and save. The new card appears at once.

### 6.4. Household

1. The member list shows each member with their role. The local user is marked as *(you)* and starts as the admin.
2. As the admin, open **Invite new member**. The **QR code and digit code** tab shows a scannable QR code, the 6-digit code and a countdown; when it reaches zero the code is replaced by a new one. **Share invite link** opens the system share sheet. The **By email** tab adds a pending invitation for a valid email address.
3. Tap the remove icon on a member and confirm the dialog to remove them.
4. Open **Transfer admin rights**, pick a member and confirm with your password. The roles swap: you become a member and the admin tools disappear.
5. **Leave household** asks for confirmation. In this version there is no other household to land in, so the screen simply returns.
6. **View schedule** opens the Calendar in Month view.

### 6.5. Profile

1. The header shows the user's initials, name, email and the active household. The counters show how many households the user belongs to, the pets in the active household and the user's role there.
2. **Edit profile** changes the names, the birth date (past dates only) and the phone number.
3. **Change password** asks for the current password and the new one twice. **Change email** asks for the new address and the current password. Errors are generic on purpose, so they never reveal which check failed.
4. **My households** lists the user's households and marks the active one. **Sign out** asks for confirmation and returns to Home, because there are no accounts in this version.

### 6.6. Notifications

1. Notifications are grouped into **TODAY**, **YESTERDAY** and older dates, each with its relative time and an unread dot.
2. Use **Filter by household** to narrow the list, and **Mark all read** to clear the unread dots and the bell counter.

---

## 7. Walk through the watch app

The watch app has three modules in a horizontal pager: **Home**, **Actions** and **Notifications**. Swipe sideways, or tap a dot of the page indicator at the bottom, to move between them. The watch only shows the activities assigned to the local member.

**Before you start:** both emulators running and paired (section 4), both apps installed and **open**, with the phone app in the foreground (section 5).

### 7.1. Home: confirm an activity from the watch

1. Home shows the next unresolved activity and the actions that apply to it: **CANCELED**, **DONE** and **CONFIRMED**.
2. Tap **DONE**. The watch switches to *Syncing with phone...* while it waits.
3. The phone validates the request, applies it and replies with the resulting state. The watch shows the answer in words and, after a moment, moves on to the next unresolved activity.
4. Look at the phone: the matching card in the Calendar shows the new status, updated without reopening or reloading the screen.

### 7.2. Actions

1. Swipe to **Actions** to see every open activity of the member with the pending counter. Dates are highlighted in yellow when the activity is today and in red when it is overdue.
2. Tap an activity to open its detail, which offers the same actions as Home, and use the back control to return to the list.

### 7.3. Notifications

1. Swipe to **Notifications** to see the watch's notifications. Tap one to read it centered on the screen.
2. When the list is empty, the module shows an empty state.

### 7.4. Things worth trying

These show how the phone stays in charge:

- **Resolve an activity on the phone first, then look at the watch.** The phone pushes the new state to the watch, so the watch card updates on its own.
- **Resolve an activity on the phone while its card is still on the watch, and tap an action on the watch.** The watch gets a rejection and shows who completed it and when; the card does not advance.
- **Send the phone app to the background and tap an action on the watch.** You get an explicit connection error with the option to retry, and the displayed state stays as it was.
- **Resolve every activity of the member.** The watch shows its *Up to date* screen.

---

## 8. Common commands

| Action | Command |
| :-- | :-- |
| List modules | `./gradlew projects` |
| Build phone app (Debug) | `./gradlew :apps:mobile:assembleDebug` |
| Build watch app (Debug) | `./gradlew :apps:wear:assembleDebug` |
| Install on emulator | `./gradlew :apps:mobile:installDebug` / `:apps:wear:installDebug` |
| Run the unit tests | `./gradlew :apps:mobile:testDebugUnitTest :apps:wear:testDebugUnitTest` |
| Run the Android linter | `./gradlew lint` |
| Clean build outputs | `./gradlew clean` |

> On Windows, use `gradlew.bat` (or `./gradlew` from Git Bash).

---

## 9. Troubleshooting

- **Gradle sync fails:** confirm the Android SDK is installed (SDK Manager) and that `local.properties` points to it. Run **File > Sync Project with Gradle Files** again.
- **No device found when the watch sends a message:** make sure both emulators are running and paired, and that the phone app is open in the foreground.
- **The watch stays on "Syncing with phone..." or reports a connection error:** the phone app is not in the foreground, or the pairing is not active. Bring the phone app back to the front and tap again to retry.
- **The phone card does not change after a watch action:** check that the watch reported an accepted result and not a rejection. A rejection means the phone refused the transition, for example because the activity was already resolved, and the card is left as it is on purpose.
- **A change disappeared after closing the app:** this version keeps everything in memory, so every launch starts from the sample data again.
- **Wrong app on a device:** `:apps:mobile` is for the phone and `:apps:wear` is for the watch; select the correct run configuration again.

Both apps work entirely on the device pair: there is no server, no network access and no database, so nothing else needs to be set up.

For the repository layout, see [`002-repository-structure-guide.md`](002-repository-structure-guide.md). For the entities and their rules, see [`004-data-model-guide.md`](004-data-model-guide.md). For the full product specification, see [`003-software-requirements-specification.md`](003-software-requirements-specification.md).
