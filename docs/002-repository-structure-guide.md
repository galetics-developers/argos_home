# Argos Home — Project Structure

This document describes the files and folders that are versioned in the repository. It is kept up to date as the project evolves.

```
argos_home/
├── apps/
│   ├── mobile/                      # Android phone app (Gradle module :apps:mobile)
│   │   ├── build.gradle.kts         # Module build script and dependencies
│   │   ├── .gitignore               # Ignores the module build output
│   │   └── src/
│   │       ├── main/
│   │       │   ├── AndroidManifest.xml
│   │       │   ├── java/com/example/argos_home/
│   │       │   │   ├── MainActivity.kt        # Hosts the UI and the Data Layer message listener
│   │       │   │   ├── WearableConstants.kt   # Shared contract: message paths and capability names
│   │       │   │   ├── protocol/              # Messages exchanged with the watch and their JSON codec
│   │       │   │   ├── domain/                # Models: activity, pet profile, medical record, notification; states, rules, seed
│   │       │   │   ├── data/                  # In-memory repositories: activities, pets, medical records, notifications
│   │       │   │   ├── wearable/              # Sends results back to the watch
│   │       │   │   └── ui/
│   │       │   │       ├── AppRoot.kt         # Root host: bottom-nav tab, pets sub-screens and notifications overlay
│   │       │   │       ├── calendar/          # Day view: header, activity cards, bottom bar
│   │       │   │       ├── pets/              # Pets module: list, detail, tabs, forms and delete dialog
│   │       │   │       ├── notifications/     # Notifications screen, cards and formatting
│   │       │   │       └── theme/             # Colors, typography and Material 3 theme
│   │       │   ├── res/
│   │       │   │   ├── drawable/              # Vector icons (navigation, bell, chevrons, filter, plus, pet species, calendar, weight, ruler, syringe, pill, clipboard, mail, user-plus, shield, camera, trash, alert, pencil, check-circle, launcher)
│   │       │   │   ├── mipmap-*/              # Launcher icons
│   │       │   │   ├── values/                # strings.xml, colors.xml, themes.xml, wear.xml
│   │       │   │   └── xml/                   # Backup and data extraction rules
│   │       │   └── keepRules/                 # Shrinker keep rules
│   │       ├── test/java/           # Local unit tests
│   │       └── androidTest/java/    # Instrumented tests
│   └── wear/                        # Wear OS watch app (Gradle module :apps:wear)
│       ├── build.gradle.kts         # Module build script and dependencies
│       ├── lint.xml                 # Lint configuration for the watch module
│       ├── .gitignore               # Ignores the module build output
│       └── src/main/
│           ├── AndroidManifest.xml
│           ├── java/com/example/argos_home/
│           │   ├── WearableConstants.kt       # Shared contract: message paths and capability names
│           │   ├── protocol/                  # Messages exchanged with the phone and their JSON codec
│           │   ├── domain/                    # Activity model, states, rules, seed, and the watch's local notifications
│           │   └── presentation/
│           │       ├── MainActivity.kt        # Hosts the watch UI and the Data Layer message listener
│           │       ├── home/                  # Home module: next activity card, action buttons, screen states
│           │       ├── actions/               # Actions module: the pending list and its focused detail
│           │       ├── notifications/         # Notifications module: list, focused detail and empty state
│           │       ├── wearable/              # Phone node discovery and message sending
│           │       └── theme/                 # Colors and Wear Material 3 theme
│           ├── res/
│           │   ├── drawable/                  # Vector icons (check, circle plus, circle x, clock, pet, sync, syringe, user-plus, shield, bell, mail, splash, launcher)
│           │   ├── mipmap-*/                  # Launcher icons
│           │   └── values/                    # strings.xml, styles.xml, wear.xml
│           └── keepRules/                     # Shrinker keep rules
├── docs/
│   ├── 001-project-setup-and-usage-manual.md      # First-time setup and run guide
│   ├── 002-repository-structure-guide.md          # This document
│   └── 003-software-requirements-specification.md # Functional requirements specification
├── gradle/
│   ├── libs.versions.toml           # Version catalog (dependencies and plugins)
│   ├── gradle-daemon-jvm.properties # Gradle daemon JVM configuration
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   └── task.md                  # Issue template
│   └── pull_request_template.md     # Pull request template
├── .gitignore                       # Ignored files (build outputs, local config, secrets)
├── build.gradle.kts                 # Root build script
├── settings.gradle.kts             # Declares the modules :apps:mobile and :apps:wear
├── gradle.properties                # Project-wide Gradle settings
├── gradlew                          # Gradle wrapper (Unix)
├── gradlew.bat                      # Gradle wrapper (Windows)
├── README.md                        # Project overview and quick start
└── LICENSE                          # MIT license
```

## Notes

- **Two modules under `apps/`.** The phone app and the watch app are independent Gradle modules declared in `settings.gradle.kts` as `:apps:mobile` and `:apps:wear`. They do not depend on each other at the module level.
- **Build outputs are not versioned.** Each module's `build/` directory, the root `build/`, the Gradle caches and `local.properties` are ignored (see `.gitignore`).
- **Documentation lives in `docs/`.** Start with `001-project-setup-and-usage-manual.md` to run the project, and read `003-software-requirements-specification.md` for the functional requirements.

---

## Where each responsibility lives

### Phone (`:apps:mobile`)

| Package | Responsibility |
| :-- | :-- |
| `protocol/` | The two messages that travel over the Wearable Data Layer and the JSON codec that encodes and decodes them. Decoding never throws: a malformed payload is discarded and logged. |
| `domain/` | The activity model (`ScheduledActivity`), the four states, the item types, the pure transition rules and the seed data; the pet profile (`Pet` plus its weight/height units), the medical record model and the notification model. |
| `data/` | The in-memory sources of truth: `ActivityRepository` (the day), `PetRepository` (the pets, with the 30-pet limit), `MedicalRecordRepository` (the health history) and `NotificationRepository` (the user's notifications). |
| `wearable/` | Sends the outcome of a request back to the watch node that asked for it. |
| `ui/` | `AppRoot`, the state-based host that switches the bottom-nav tab, the pets sub-screens and the notifications overlay without a navigation library. |
| `ui/calendar/` | The `Calendar` day screen: top bar and date navigator, the activity cards, the bottom navigation bar and the tab enum. |
| `ui/pets/` | The Pets module: the list with per-pet day progress, the detail with its two tabs, the care schedule and health records tabs, the pet and medical-record forms, and the delete dialog. |
| `ui/notifications/` | The Notifications screen grouped by date with a household filter, the notification card, and the relative-time and section formatting. |
| `ui/theme/` | The product palette, the typography and the Material 3 theme. |

### Watch (`:apps:wear`)

| Package | Responsibility |
| :-- | :-- |
| `protocol/`, `domain/` | Identical copies of the phone packages (see *Shared contract conventions* below). |
| `presentation/home/` | The Home module: the next activity card, the three action buttons, the "up to date" and "syncing" screens, the module indicator and the screen state holder. |
| `presentation/actions/` | The Actions module: the list of pending activities and the focused detail that reuses the Home card. |
| `presentation/notifications/` | The Notifications module (third pager panel): the glanceable list, the centered detail, and the green empty state; it reads a local notification seed, not the Data Layer. |
| `presentation/wearable/` | Discovers the phone node through `CapabilityClient` and sends the activity update message to it. |
| `presentation/theme/` | The product palette and the Wear Material 3 theme. |

---

## Shared contract conventions

- **`res/values/wear.xml` in both modules.** Each module advertises one capability name through the `android_wear_capabilities` string array — `argos_home_mobile_app` on the phone and `argos_home_wear_app` on the watch. That is how one device finds the other at runtime. The literal values must stay identical to the capability constants in `WearableConstants.kt`: a mismatch is not a compile error, it simply makes discovery find nothing.

- **Files replicated by copy.** The nine files that make up the shared contract exist twice, once per module, byte for byte identical, package line included:

  ```
  WearableConstants.kt
  protocol/ActivityUpdate.kt   protocol/ActivityResult.kt   protocol/MessageCodec.kt
  domain/ActivityState.kt      domain/ItemType.kt           domain/ScheduledActivity.kt
  domain/TransitionRules.kt    domain/SeedData.kt
  ```

  The duplication is deliberate. There is no Gradle dependency between `:apps:mobile` and `:apps:wear` and no shared library module, so holding the same source on both sides is what keeps them agreeing on message paths, payload field names and state names. Both modules use the same `namespace`, which is what allows the copies to be identical rather than merely equivalent.

  **Rule: any change to the shared contract is applied to both copies in the same change set.** Editing one side alone still compiles, and then breaks the device-to-device communication silently at runtime. The copies can be checked with a plain `diff` of those nine paths between `apps/mobile/src/main/java/com/example/argos_home/` and `apps/wear/src/main/java/com/example/argos_home/`; the expected output is empty.

- **Icons live per module.** Each module carries only the vector drawables its own screens use, under its own `res/drawable/`. They are not shared between modules either.

- **Notifications are per module, not a shared contract.** The phone and the watch each hold their own notification model and seed, because notifications are never exchanged over the Wearable Data Layer. Unlike the nine shared-contract files above, these copies are intentionally different: the phone groups notifications by date across several households, while the watch keeps a short glanceable seed.
