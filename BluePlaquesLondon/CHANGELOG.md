# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.0.0]

### Added
- **Jetpack Compose UI** - Complete migration to modern declarative UI framework with Material Design 3
- **Navigation Component** - Replaced Intent-based navigation with Navigation Component architecture
- **MVVM Architecture** - Implemented ViewModels, LiveData, and Repository pattern across all layers
- **Hilt Dependency Injection** - Centralized dependency injection for better testability
- **Room Database** - Modern local persistence with RxJava3 integration
- **Comprehensive Testing** - 165+ unit tests with 40%+ code coverage
- **GitHub Actions CI/CD** - Automated build, test, and coverage reporting
- **DataStore** - Modern alternative to SharedPreferences for data persistence
- **Firebase Integration** - Analytics and crash reporting
- **RxJava3 Support** - Reactive programming for data streams
- **Instrumented Tests** - 73+ integration tests with Espresso and Robolectric
- **Test Coverage Reports** - JaCoCo integration for coverage analysis

### Changed
- **Modernized Build System** - Updated to latest Android Gradle Plugin and dependencies
- **Material Design 3** - Replaced Material Design 2 with Material Design 3 theme
- **API Level** - Increased minimum SDK from 21 to 24, target SDK to 35
- **Java Version** - Updated Java compatibility to version 17

### Technical
- Migrated from CircleCI to GitHub Actions
- Removed all legacy `android.support.*` imports (AndroidX migration complete)
- Updated all Jetpack libraries to latest versions
- Configured Room schema versioning
- Implemented proper Hilt testing with `HiltTestRunner`
- Set up code coverage with JaCoCo
- Added git hooks for pre-commit validation

## [2.3.0]

### Added
- Dependency Injection framework setup
- Room database migration planning
- Modern build configuration

### Changed
- Began modernization of legacy Android codebase

## [2.2.0]

### Fixed
- Build system compatibility with modern Android toolchain

## [2.1.0]

### Added
- Initial project structure for modernization

## [2.0.0]

### Added
- Google Maps integration for plaque visualization
- Search functionality
- Wikipedia integration
- Street View support

### Changed
- Major architectural improvements
- Updated UI/UX design

## [1.0.0]

### Added
- Initial release
- Map of Blue Plaques across London
- Basic search functionality
- Plaque details view

---

## Migration Notes

### From 2.x to 3.0

The 3.0 release represents a major modernization of the codebase:

- **AndroidX**: All `android.support.*` libraries have been replaced with AndroidX equivalents
- **Architecture**: Project now follows MVVM pattern with Repository layer
- **Testing**: Significantly expanded test coverage (from minimal to 40%+)
- **Jetpack**: Heavy adoption of Jetpack components (Compose, Navigation, Hilt, Room, DataStore)
- **Kotlin**: Prepared for Kotlin adoption (Java remains the primary language for now)

Existing feature behavior remains unchanged, but the internal architecture is substantially improved for maintainability and testability.
