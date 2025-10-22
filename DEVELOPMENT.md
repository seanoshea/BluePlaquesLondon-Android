# Development Setup Guide

This guide covers the optimal setup for developing the Blue Plaques London Android application.

## Prerequisites

### Required Software
- **Android Studio**: Latest stable version (Hedgehog 2023.1.1+)
- **JDK 17**: Required for Android Gradle Plugin 8.0+
- **Git**: For version control and git hooks

### System Requirements
- **Minimum RAM**: 8GB (16GB recommended)
- **Storage**: 10GB+ free space for Android SDK and emulators
- **OS**: Windows 10+, macOS 10.14+, or Ubuntu 18.04+

## Project Setup

### 1. Clone Repository
```bash
git clone https://github.com/seanoshea/BluePlaquesLondon-Android.git
cd BluePlaquesLondon-Android
```

### 2. Configure Git Hooks
```bash
# Set up git hooks for code quality
git config core.hooksPath scripts
```

### 3. Android SDK Setup
- **Minimum SDK**: API 30 (Android 11)
- **Target SDK**: API 35 (Android 15)
- **Build Tools**: 35.0.0+

### 4. API Keys Configuration

**Google Maps API Key**
Create `local.properties` in the `BluePlaquesLondon/` directory:
```properties
# Google Maps API Key (get from Google Cloud Console)
GOOGLE_MAPS_API_KEY=your_api_key_here

# Android SDK location
sdk.dir=/path/to/android/sdk
```

**Firebase Configuration**
1. Download `google-services.json` from Firebase Console
2. Replace `BluePlaquesLondon/app/google-services.json` with your file
3. Use `google-services.json.sample` as reference

**⚠️ Never commit API keys or real Firebase config to version control**

## Development Environment

### Android Studio Configuration
1. **Import Project**: Open `BluePlaquesLondon/` folder in Android Studio
2. **Gradle Sync**: Let Android Studio sync dependencies
3. **Enable Git Integration**: VCS → Enable Version Control Integration

### Recommended Plugins
- **Hilt Navigation**: For dependency injection
- **Room**: Database inspection
- **LeakCanary**: Memory leak detection (debug builds only)

### Emulator Setup
Create an Android 11+ (API 30+) emulator:
- **Device**: Pixel 6 or newer
- **System Image**: Google APIs (x86_64)
- **RAM**: 4GB minimum
- **Storage**: 8GB minimum

## Building and Testing

### Build Commands
```bash
cd BluePlaquesLondon

# Debug build
./gradlew assembleDebug

# Release build (requires signing config)
./gradlew assembleRelease
```

### Testing
```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (requires emulator/device)
./gradlew connectedDebugAndroidTest

# Generate coverage report
./gradlew jacocoTestReport
```

### Code Quality
```bash
# Android lint
./gradlew lintDebug

# All quality checks
./gradlew check
```

## Architecture Overview

### Tech Stack
- **Language**: Java 17
- **Architecture**: MVVM with Repository pattern
- **DI**: Hilt (Dagger)
- **Database**: Room with RxJava3
- **UI**: Traditional Views + Jetpack Compose (migration in progress)
- **Navigation**: Navigation Component
- **Maps**: Google Maps SDK
- **Testing**: JUnit, Espresso, Robolectric

### Project Structure
```
BluePlaquesLondon/app/src/main/java/
├── activities/          # Activity classes
├── fragments/           # Fragment classes
├── adapters/           # RecyclerView adapters
├── data/               # Data layer (Repository, DAO, API)
├── model/              # Data models
├── ui/                 # UI components (ViewModels, Compose)
├── utils/              # Utility classes
└── di/                 # Dependency injection modules
```

## Development Workflow

### Git Workflow
1. **Create Feature Branch**: `git checkout -b feature/your-feature`
2. **Make Changes**: Follow conventional commit format
3. **Pre-commit Checks**: Automatic via git hooks
4. **Push**: `git push origin feature/your-feature`
5. **Create PR**: Target `develop` branch

### Commit Message Format
```
type(scope): description

Examples:
feat: add Wikipedia search functionality
fix(maps): resolve marker clustering issue
docs: update README with build instructions
```

### Code Style
- **Indentation**: 4 spaces (no tabs)
- **Line Length**: 120 characters max
- **Naming**: camelCase for variables, PascalCase for classes
- **Comments**: JavaDoc for public methods

## Troubleshooting

### Common Issues

**Build Fails with "SDK not found"**
- Set `sdk.dir` in `local.properties`
- Ensure Android SDK is installed

**Tests Fail on CI**
- Check Android 11+ compatibility
- Verify API keys are not in source code

**Maps Not Loading**
- Verify Google Maps API key is valid
- Check API key restrictions in Google Cloud Console
- Ensure Maps SDK is enabled

**Memory Issues**
- Increase Gradle heap size: `org.gradle.jvmargs=-Xmx4g`
- Close unused applications
- Use smaller emulator configuration

### Performance Tips
- **Gradle Daemon**: Keep running for faster builds
- **Parallel Builds**: Enable in `gradle.properties`
- **Build Cache**: Use `--build-cache` flag
- **Incremental Builds**: Avoid `clean` unless necessary

## Release Process

### Signing Configuration
Create `gradle.properties` with signing details:
```properties
RELEASE_STORE_FILE=path/to/keystore
RELEASE_STORE_PASSWORD=store_password
RELEASE_KEY_ALIAS=key_alias
RELEASE_KEY_PASSWORD=key_password
```

### Release Checklist
- [ ] Update version code/name in `build.gradle`
- [ ] Run full test suite
- [ ] Generate signed APK
- [ ] Test on physical device
- [ ] Update CHANGELOG.md
- [ ] Create release tag

## Contributing

### Before Contributing
1. Read [CONTRIBUTING.md](CONTRIBUTING.md) if available
2. Check existing issues and PRs
3. Set up development environment per this guide
4. Run tests to ensure setup is correct

### Pull Request Guidelines
- Include unit tests for new features
- Update documentation if needed
- Follow existing code style
- Ensure CI passes
- Add meaningful commit messages

## Support

- **Issues**: [GitHub Issues](https://github.com/seanoshea/BluePlaquesLondon-Android/issues)
- **Discussions**: [GitHub Discussions](https://github.com/seanoshea/BluePlaquesLondon-Android/discussions)
- **Contact**: [@seanoshea](https://twitter.com/seanoshea)