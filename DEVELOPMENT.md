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

#### Google Maps API Key Setup

**Step 1: Create local.properties**
Create `local.properties` in the `BluePlaquesLondon/` directory:
```properties
# Google Maps API Key (get from Google Cloud Console)
GOOGLE_MAPS_API_KEY=your_api_key_here

# Android SDK location
sdk.dir=/path/to/android/sdk
```

**Step 2: Configure Google Cloud Console**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your project or create a new one
3. Navigate to **APIs & Services** > **Credentials**
4. Create or select your API key

**Step 3: Enable Maps SDK for Android**
1. In the API key details, under **API restrictions**
2. Select **Restrict key** and add **Maps SDK for Android**
3. Save the changes

**Step 4: Configure Android App Restrictions**
1. Under **Application restrictions**, select **Android apps**
2. Add package name: `com.upwardsnorthwards.blueplaqueslondon`
3. Add SHA-1 certificate fingerprint:

   **For Debug Builds:**
   ```bash
   # Get debug keystore fingerprint
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android | grep SHA1
   ```

   **For Release Builds:**
   ```bash
   # Get release keystore fingerprint
   keytool -list -v -keystore path/to/production.keystore -alias your_alias | grep SHA1
   ```

#### Firebase Configuration

Firebase provides automatic crash reporting through Google Play Console (no Crashlytics library needed). Firebase Analytics is configured via the `google-services.json` file.

**Setup Steps:**

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create or select your project
3. Add Android app with package name: `com.upwardsnorthwards.blueplaqueslondon`
4. Use the SHA-1 fingerprint from your debug keystore (see [Google Maps API Key Setup](#google-maps-api-key-setup) above)
5. Download `google-services.json` from Firebase Console
6. Place the file at: `BluePlaquesLondon/app/google-services.json`

**Important: Firebase Credentials Cannot Be Stored in local.properties**

Unlike the Google Maps API key, Firebase credentials must come from the `google-services.json` file. The Gradle plugin `com.google.gms.google-services` reads and processes this file at build time. **Credentials cannot be injected from `local.properties`.**

**File Structure:**
- `BluePlaquesLondon/app/google-services.json` - **Real credentials (gitignored)** - Only on local machines
- `BluePlaquesLondon/app/google-services.json.sample` - **Template with placeholders (checked in)** - Reference for structure

**For Local Development:**
- Download the real `google-services.json` from Firebase Console
- Place it in `BluePlaquesLondon/app/google-services.json`
- The `.gitignore` file automatically excludes it from version control

**For CI/CD Builds:**
- Encode the real `google-services.json` as a base64 secret in your CI system
- Decode and write it during the build step before Gradle runs

#### Security Notes
- **⚠️ Never commit the real `google-services.json` to version control**
- **⚠️ Never commit `local.properties` containing real API keys**
- Google Maps API keys go in `local.properties` (gitignored)
- Firebase configuration comes from `google-services.json` (gitignored)
- Use placeholder values in `google-services.json.sample` and `local.defaults.properties`
- For automated CI/CD builds, store sensitive files as encrypted secrets in your CI system

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
- Verify Google Maps API key is valid and in `local.properties`
- Check API key restrictions in Google Cloud Console
- Ensure Maps SDK for Android is enabled
- Verify package name and SHA-1 fingerprint match exactly
- Check logcat for authorization errors: `adb logcat | grep -i "google\|maps\|authorization"`

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