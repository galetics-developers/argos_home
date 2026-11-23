# Argos Home

**Argos Home** is the final project for the **Development for Smart Devices** course. It is a multi-platform application that coordinates the daily care of a household's pets across the devices each caregiver has at hand.

> How can two or more caregivers share a pet's daily care (feeding, walks, vaccines) without duplicating tasks or forgetting them?

Argos Home answers this by splitting the experience across two Android apps that talk to each other directly: the **phone** concentrates management and history, and the **Wear OS watch** handles immediate, glanceable confirmations.

- **Devices:** an Android phone app (`:apps:mobile`) and a Wear OS watch app (`:apps:wear`).
- **Communication:** the Google Wearable Data Layer (`MessageClient`), device to device, with no backend, API or database.
- **Requirements documentation:** [`docs/003-software-requirements-specification.md`](docs/003-software-requirements-specification.md) describes the full product vision, including the parts that are not built yet.

**Quick start:** follow [`docs/001-project-setup-and-usage-manual.md`](docs/001-project-setup-and-usage-manual.md) to install the tools, pair the emulators and run both apps.

---

## What the apps do today (v1.0.0)

The first version delivers the five main sections of the phone app and the three modules of the watch app. The phone owns the state of every activity, and a change made on either device is reflected on the other one while both apps are open. Everything runs on a sample household that is generated in memory when the apps start: there are no accounts, no server and no persistence yet.

A screen-by-screen guide is in [`docs/005-application-modules-guide.md`](docs/005-application-modules-guide.md), and the entities behind them are described in [`docs/004-data-model-guide.md`](docs/004-data-model-guide.md).

### Phone (`:apps:mobile`)

A bottom navigation bar switches between five sections. A notification bell with an unread counter is available from the top bar of Home, Pets, Calendar and Profile.

- **Home:** a greeting with the active household, the day's progress ring with total, pending and completed counters and the next event, a carousel of the household's pets, and the list of upcoming activities. A *Switch household* modal lists the households the user belongs to.
- **Pets:** the list of pets with each pet's progress for the day, a detail screen with the pet profile and two tabs (*Care schedule* and *Health records*), forms to add and edit a pet and to add a medical record (vaccine, medication or procedure), and a confirmation dialog to delete a pet.
- **Calendar:** Month, Week and Day views, a pet filter, activity cards with only the actions that are legal for their current state (**Cancel**, **Done**, **Confirm**), and a *New activity* modal to create a task or an event.
- **Household:** the member list with roles (admin or member), admin tools to invite members (QR code with a 6-digit code that expires, share link, or invitation by email), to remove members and to transfer the admin role with a password confirmation, and the option to leave the household.
- **Profile:** the account header, counters for households, pets and role, account settings (edit profile, change password, change email), the list of the user's households, and sign out.
- **Notifications:** a full-screen list grouped by day (Today, Yesterday, older dates), filterable by household, with a *Mark all read* action.

### Watch (`:apps:wear`)

Three modules are arranged in a horizontal pager with a page indicator at the bottom.

- **Home:** the next unresolved activity assigned to the local member (pet, activity type, schedule and time) with the actions that apply to it: **CANCELED**, **DONE** and **CONFIRMED**. It shows a *Syncing with phone...* state while a request is in flight, a connection error with a retry option when the phone cannot be reached, and an *Up to date* screen once every activity is resolved.
- **Actions:** the list of the member's activities that are still open, with a pending counter and a focused detail that offers the same actions as Home.
- **Notifications:** a glanceable list of notifications, a focused detail for each one, and an empty state.

Every state on both devices is written as text next to its color, so no status depends on color alone.

### How the two sides agree

- The phone is the **authority on state**. The watch never decides a change by itself: it asks, and it displays whatever the phone answers.
- The watch finds the phone through `CapabilityClient` and sends an activity update over `MessageClient`. The phone validates the transition against the activity's current state, applies it if it is legal, and replies with the resulting state, or with the reason for the rejection, including who resolved the activity and when.
- A change made on the phone's own calendar is pushed to the watch too, so the watch card never stays stale.
- An activity moves between four states: `PENDING`, `CONFIRMED`, `DONE` and `CANCELED`. `DONE` and `CANCELED` are final.
- Both modules carry an identical copy of the message contract and the shared domain rules, because there is no Gradle dependency between them. See [`docs/002-repository-structure-guide.md`](docs/002-repository-structure-guide.md).
- Pets, medical records, household members, invitations and the user profile live only on the phone. They are never sent over the Data Layer.

### Not in this version

The requirements document describes features that are planned but not part of v1.0.0: user accounts and sign-in, a server with real-time synchronization and persistence, real multi-household data (switching household only changes the label today), accepting invitations, push and email reminders, picking a photo for a pet or a profile, and the smart display.

---

## Running it

- Two **paired emulators**: a phone with a Google Play system image and a Wear OS Small Round watch, paired from Android Studio's Device Manager.
- **Both apps installed and open.** The listeners are active only while each app is in the foreground, so the phone app must be on screen for the watch to get an answer.
- Build both apps with `./gradlew :apps:mobile:assembleDebug :apps:wear:assembleDebug`.

Step-by-step instructions, including a walkthrough of every section, are in [`docs/001-project-setup-and-usage-manual.md`](docs/001-project-setup-and-usage-manual.md).

---

## Documentation

| Document | Contents |
| :-- | :-- |
| [`docs/001-project-setup-and-usage-manual.md`](docs/001-project-setup-and-usage-manual.md) | First-time setup: prerequisites, emulators, pairing, running each app and a walkthrough of the main sections. |
| [`docs/002-repository-structure-guide.md`](docs/002-repository-structure-guide.md) | Annotated tree of the repository and the shared contract conventions. |
| [`docs/003-software-requirements-specification.md`](docs/003-software-requirements-specification.md) | Functional requirements specification of Argos Home (product vision). |
| [`docs/004-data-model-guide.md`](docs/004-data-model-guide.md) | Entities, fields, enumerations, relationships and validation rules. |
| [`docs/005-application-modules-guide.md`](docs/005-application-modules-guide.md) | Modules, submodules, screens and modals of the phone and watch apps in the first version. |

---

## Authors

- Cesar Sanchez
- Aaron Santos

---

## License

Released under the [MIT License](LICENSE).
