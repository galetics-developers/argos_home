# Argos Home: Project Structure

This document describes the files and folders that are versioned in the repository. It is kept up to date as the project evolves.

```
argos_home/
|-- apps/
|   |-- mobile/                          # Android phone app (Gradle module :apps:mobile)
|   |   |-- build.gradle.kts             # Module build script and dependencies
|   |   |-- .gitignore                   # Ignores the module build output
|   |   `-- src/
|   |       |-- main/
|   |       |   |-- AndroidManifest.xml
|   |       |   |-- java/com/example/argos_home/
|   |       |   |   |-- MainActivity.kt          # Hosts the UI, the Data Layer listener and attaches the watch notifier
|   |       |   |   |-- WearableConstants.kt     # Shared contract: message paths and capability names
|   |       |   |   |-- protocol/                # Messages exchanged with the watch and their JSON codec
|   |       |   |   |-- domain/                  # Models, enumerations, pure rules, formatting and the sample data
|   |       |   |   |-- data/                    # In-memory repositories: activities, pets, medical records, household, notifications, profile
|   |       |   |   |-- wearable/                # Watch node discovery, result sending and state push to the watch
|   |       |   |   `-- ui/
|   |       |   |       |-- AppRoot.kt           # Root host: bottom-nav tab, per-tab sub-screens and notifications overlay
|   |       |   |       |-- home/                # Home: header, progress ring, counters, pet carousel, upcoming list, switch household modal
|   |       |   |       |-- pets/                # Pets: list, detail, tabs, pet and medical record forms, delete dialog
|   |       |   |       |-- calendar/            # Calendar: Month, Week and Day views, activity cards, new activity modal, bottom bar
|   |       |   |       |-- household/           # Household: members, roles, admin tools, invite and transfer submodules, dialogs
|   |       |   |       |-- profile/             # Profile: header, counters, account settings, account submodules, households, sign out
|   |       |   |       |-- notifications/       # Notifications screen, cards and formatting
|   |       |   |       |-- components/          # Components shared by several sections: notification bell, password field
|   |       |   |       `-- theme/               # Colors, typography and Material 3 theme
|   |       |   |-- res/
|   |       |   |   |-- drawable/                # Vector icons used by the phone screens and the launcher icon
|   |       |   |   |-- mipmap-*/                # Launcher icons
|   |       |   |   |-- values/                  # strings.xml, colors.xml, themes.xml, wear.xml
|   |       |   |   `-- xml/                     # Backup and data extraction rules
|   |       |   `-- keepRules/                   # Shrinker keep rules
|   |       |-- test/java/               # Local unit tests (calendar, household and profile rules)
|   |       `-- androidTest/java/        # Instrumented tests
|   `-- wear/                            # Wear OS watch app (Gradle module :apps:wear)
|       |-- build.gradle.kts             # Module build script and dependencies
|       |-- lint.xml                     # Lint configuration for the watch module
|       |-- .gitignore                   # Ignores the module build output
|       `-- src/
|           |-- main/
|           |   |-- AndroidManifest.xml
|           |   |-- java/com/example/argos_home/
|           |   |   |-- WearableConstants.kt     # Shared contract: message paths and capability names
|           |   |   |-- protocol/                # Messages exchanged with the phone and their JSON codec
|           |   |   |-- domain/                  # Shared models and rules, plus the watch's own notification model and sample
|           |   |   `-- presentation/
|           |   |       |-- MainActivity.kt      # Hosts the watch UI and the Data Layer listener
|           |   |       |-- WearApp.kt           # Horizontal pager over the three modules and the page indicator
|           |   |       |-- home/                # Home module: next activity card, action buttons, screen states, state holder
|           |   |       |-- actions/             # Actions module: the open activities list and its focused detail
|           |   |       |-- notifications/       # Notifications module: list, focused detail and empty state
|           |   |       |-- wearable/            # Phone node discovery and message sending
|           |   |       `-- theme/               # Colors and Wear Material 3 theme
|           |   |-- res/
|           |   |   |-- drawable/                # Vector icons used by the watch screens, splash and launcher icons
|           |   |   |-- mipmap-*/                # Launcher icons
|           |   |   `-- values/                  # strings.xml, styles.xml, wear.xml
|           |   `-- keepRules/                   # Shrinker keep rules
|           `-- test/java/               # Local unit tests (phone node choice)
|-- docs/
|   |-- 001-project-setup-and-usage-manual.md       # First-time setup, run guide and walkthrough
|   |-- 002-repository-structure-guide.md           # This document
|   |-- 003-software-requirements-specification.md  # Functional requirements specification
|   |-- 004-data-model-guide.md                     # Entities, fields, enumerations, relationships and rules
|   `-- 005-application-modules-guide.md            # Modules, submodules, screens and modals of both apps
|-- gradle/
|   |-- libs.versions.toml               # Version catalog (dependencies and plugins)
|   |-- gradle-daemon-jvm.properties     # Gradle daemon JVM configuration
|   `-- wrapper/
|       |-- gradle-wrapper.jar
|       `-- gradle-wrapper.properties
|-- .github/
|   |-- ISSUE_TEMPLATE/
|   |   `-- task.md                      # Issue template
|   `-- pull_request_template.md         # Pull request template
|-- .idea/.gitignore                     # Ignores the local IDE files
|-- .gitignore                           # Ignored files (build outputs, local config, secrets)
|-- build.gradle.kts                     # Root build script
|-- settings.gradle.kts                  # Declares the modules :apps:mobile and :apps:wear
|-- gradle.properties                    # Project-wide Gradle settings
|-- gradlew                              # Gradle wrapper (Unix)
|-- gradlew.bat                          # Gradle wrapper (Windows)
|-- README.md                            # Project overview and quick start
`-- LICENSE                              # MIT license
```

## Notes

- **Two modules under `apps/`.** The phone app and the watch app are independent Gradle modules declared in `settings.gradle.kts` as `:apps:mobile` and `:apps:wear`. They do not depend on each other at the module level.
- **Build outputs are not versioned.** Each module's `build/` directory, the root `build/`, the Gradle caches and `local.properties` are ignored (see `.gitignore`).
- **Documentation lives in `docs/`.** Start with `001-project-setup-and-usage-manual.md` to run the project, read `004-data-model-guide.md` and `005-application-modules-guide.md` for what the first version contains, and `003-software-requirements-specification.md` for the full product vision.
- **No navigation library.** Each app switches its screens from plain Compose state: `AppRoot` on the phone and a horizontal pager in `WearApp` on the watch.

---

## Where each responsibility lives

### Phone (`:apps:mobile`)

| Package | Responsibility |
| :-- | :-- |
| `protocol/` | The two messages that travel over the Wearable Data Layer and the JSON codec that encodes and decodes them. Decoding never throws: a malformed payload is discarded and logged. |
| `domain/` | The entities and enumerations described in `004-data-model-guide.md` (activity, pet, household, member role, invitation, medical record, notification, user profile), the pure transition rules, the Home summary, the validation helpers (email, password, time range), date and time formatting, and the sample data. |
| `data/` | The in-memory sources of truth: `ActivityRepository` (the activities and the only place where a transition is applied), `PetRepository` (the pets, with the pet limit), `MedicalRecordRepository` (the health history), `HouseholdRepository` (members, roles, invitations and the invite code), `NotificationRepository` (the user's notifications) and `ProfileRepository` (the editable account). |
| `wearable/` | `WatchNodeResolver` finds the watch nodes, `ActivityResultSender` sends a result to one of them, and `WatchNotifier` pushes every state change made on the phone to the watch. |
| `ui/` | `AppRoot`, the state-based host that switches the bottom-nav tab, the sub-screens of Pets, Household and Profile, and the notifications overlay. |
| `ui/home/` | The Home screen: the header with greeting and household pill, the progress ring and counters, the pet carousel, the upcoming list and the *Switch household* modal. |
| `ui/pets/` | The Pets section: the list with per-pet day progress, the detail with its two tabs, the care schedule and health records tabs, the pet and medical record forms, and the delete dialog. |
| `ui/calendar/` | The Calendar section: the top bar, the view selector, the pet filter, the Month, Week and Day views, the activity cards and status labels, the *New activity* modal, and the bottom navigation bar with the tab enum. |
| `ui/household/` | The Household section: the header, the member cards and role badges, the admin and member tools, the *Invite member* submodule (QR and email tabs, pending invitations), the *Transfer administration* submodule, and the remove, leave and confirm transfer dialogs. |
| `ui/profile/` | The Profile section: the header, the counters, the account settings, the *Edit profile*, *Change password* and *Change email* submodules, the household list and the sign out dialog. |
| `ui/notifications/` | The Notifications screen grouped by date with a household filter, the notification card, and the relative time and section formatting. |
| `ui/components/` | Pieces reused by several sections: the notification bell with its unread badge and the password field with its show or hide control. |
| `ui/theme/` | The product palette, the typography and the Material 3 theme. |

### Watch (`:apps:wear`)

| Package | Responsibility |
| :-- | :-- |
| `protocol/`, `domain/` | Identical copies of the phone files that form the shared contract (see *Shared contract conventions* below), plus the watch's own notification model and sample. |
| `presentation/` | `MainActivity`, which registers the Data Layer listener, and `WearApp`, the pager over the three modules with the page indicator. |
| `presentation/home/` | The Home module: the next activity card, the action buttons, the result text, the *Up to date*, *Syncing* and connection error screens, the module indicator and `HomeStore`, the state holder shared with the Actions module. |
| `presentation/actions/` | The Actions module: the list of open activities with its pending counter and the focused detail that reuses the Home card. |
| `presentation/notifications/` | The Notifications module (third pager panel): the glanceable list, the centered detail and the empty state. It reads a local notification sample, not the Data Layer. |
| `presentation/wearable/` | Discovers the phone node through `CapabilityClient` (with a fallback to the connected nodes) and sends the activity update message to it. |
| `presentation/theme/` | The product palette and the Wear Material 3 theme. |

---

## Shared contract conventions

- **`res/values/wear.xml` in both modules.** Each module advertises one capability name through the `android_wear_capabilities` string array: `argos_home_mobile_app` on the phone and `argos_home_wear_app` on the watch. That is how one device finds the other at runtime. The literal values must stay identical to the capability constants in `WearableConstants.kt`: a mismatch is not a compile error, it simply makes discovery find nothing.

- **Files replicated by copy.** The fifteen files that make up the shared contract exist twice, once per module, byte for byte identical, package line included:

  ```
  WearableConstants.kt
  protocol/ActivityUpdate.kt    protocol/ActivityResult.kt    protocol/MessageCodec.kt
  domain/ActivityState.kt       domain/ItemType.kt            domain/ScheduledActivity.kt
  domain/TransitionRules.kt     domain/SeedData.kt            domain/ActivityFormats.kt
  domain/Household.kt           domain/PetType.kt             domain/PetMeasures.kt
  domain/Recurrence.kt          domain/ReminderUnit.kt
  ```

  The duplication is deliberate. There is no Gradle dependency between `:apps:mobile` and `:apps:wear` and no shared library module, so holding the same source on both sides is what keeps them agreeing on message paths, payload field names, state names and the sample household. Both modules use the same `namespace`, which is what allows the copies to be identical rather than merely equivalent.

  **Rule: any change to the shared contract is applied to both copies in the same change set.** Editing one side alone still compiles, and then breaks the device-to-device communication silently at runtime. The copies can be checked with a plain `diff` of those fifteen paths between `apps/mobile/src/main/java/com/example/argos_home/` and `apps/wear/src/main/java/com/example/argos_home/`; the expected output is empty.

- **Phone-only domain files.** Everything else in the phone's `domain/` package (roles, invitations, medical records, notifications, user profile, household colors, the household lists of Home and Profile, the Home summary and the validation helpers) exists only on the phone, because that data never crosses the Data Layer.

- **Icons live per module.** Each module carries only the vector drawables its own screens use, under its own `res/drawable/`. They are not shared between modules either.

- **Notifications are per module, not a shared contract.** The phone and the watch each hold their own notification model and sample, because notifications are never exchanged over the Wearable Data Layer. Unlike the shared contract files above, these copies are intentionally different: the phone groups notifications by date across several households, while the watch keeps a short glanceable list with ready-made time labels.
