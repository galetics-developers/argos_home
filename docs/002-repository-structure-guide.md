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
│   │       │   ├── java/            # Kotlin sources (UI and app logic)
│   │       │   ├── res/             # Resources: strings, colors, themes, icons
│   │       │   └── keepRules/       # Shrinker keep rules
│   │       ├── test/java/           # Local unit tests
│   │       └── androidTest/java/    # Instrumented tests
│   └── wear/                        # Wear OS watch app (Gradle module :apps:wear)
│       ├── build.gradle.kts         # Module build script and dependencies
│       ├── lint.xml                 # Lint configuration for the watch module
│       ├── .gitignore               # Ignores the module build output
│       └── src/main/
│           ├── AndroidManifest.xml
│           ├── java/                # Kotlin sources (watch UI and logic)
│           ├── res/                 # Resources
│           └── keepRules/           # Shrinker keep rules
├── docs/
│   ├── 001-project-setup-and-usage-manual.md                    # First-time setup and run guide
│   ├── 002-repository-structure-guide.md         # This document
│   └── 003-software-requirements-specification.md                       # Functional requirements specification
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
