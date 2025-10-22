# Development Guide

## Prerequisites

- **Android Studio**: Koala or later (2024.1+)
- **JDK**: Version 17 or higher
- **Gradle**: 8.11+ (included with Android Studio)
- **Android SDK**:
  - Min SDK: 30 (Android 11)
  - Target SDK: 35 (Android 15)
  - Compile SDK: 35

## Initial Setup

### 1. Clone the Repository

```bash
git clone https://github.com/seanoshea/BluePlaquesLondon-Android.git
cd BluePlaquesLondon-Android/BluePlaquesLondon
```

### 2. Set Up Local Properties

Create a `local.properties` file in the project root with your local Android SDK path:

```properties
sdk.dir=/path/to/android/sdk
```

This file is ignored by git and will not be committed.

### 3. Install Dependencies

Gradle will automatically download all dependencies when you build the project. No manual dependency installation is needed.

### 4. Open in Android Studio

```bash
open -a "Android Studio" .
```

Or manually open Android Studio and select this project directory.

## API Configuration

### Overview

The app uses secure API key management with:
- API keys stored in `local.properties` (git-ignored)
- Keys read at build time and injected into BuildConfig
- Firebase configuration via `google-services.json` (git-ignored)

### Google Maps API Key

The app uses Google Maps for displaying plaques on an interactive map.

**Current Setup:**

- The project has an existing Google Maps API key configured in `AndroidManifest.xml`
- For development, this key works out of the box
- API key is also available in `BuildConfig.GOOGLE_MAPS_API_KEY` for programmatic access

**To use a different API key:**

1. **Add to `local.properties`**:

   Open or create `BluePlaquesLondon/local.properties`:

   ```properties
   sdk.dir=/path/to/android/sdk
   GOOGLE_MAPS_API_KEY=YOUR_API_KEY_HERE
   ```

2. **For production**: Create a new restricted API key:
   - Go to [Google Cloud Console](https://console.cloud.google.com)
   - Enable Maps SDK for Android
   - Create an API key with Android app restrictions
   - Add your app's SHA-1 certificate fingerprint:
     ```bash
     ./gradlew signingReport
     ```
   - Add this key to `local.properties` as shown above
   - The key will be automatically injected into `BuildConfig` at build time

### Firebase Configuration

The app uses Firebase for analytics and error tracking.

**Setup:**

1. **Check for existing configuration**: `app/google-services.json`
   - If present with correct `blue-plaques-london-android` project ID, Firebase is ready
   - If missing, follow step 2

2. **Set up your own Firebase project** (if needed):
   - Create a Firebase project at [firebase.google.com](https://firebase.google.com)
   - Project ID: `blue-plaques-london-android`
   - Add an Android app to your Firebase project
   - Download the `google-services.json` file

3. **Place the file**:
   ```
   app/
   └── google-services.json
   ```

   This file is git-ignored for security and won't be committed.

4. **Build**: The build system automatically integrates Firebase configuration

### Default Values

The project provides reference values in `local.defaults.properties`. If you need different keys:
- Copy the format from `local.defaults.properties`
- Create your own `local.properties` with your actual keys
- Never commit `local.properties`

### Security Notes

- **local.properties**: Git-ignored, stores real API keys per developer (never commit)
- **local.defaults.properties**: Git-tracked, provides placeholder reference values
- **google-services.json**: Git-ignored, contains Firebase credentials
- **BuildConfig**: API keys available as `BuildConfig.GOOGLE_MAPS_API_KEY` for programmatic use
- `.gitignore` and pre-commit hooks prevent accidental key commits

## Building and Running

### Build Debug APK

```bash
./gradlew assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

### Run on Connected Device or Emulator

```bash
./gradlew installDebug
```

This builds and installs the debug APK on your connected device/emulator.

### Run from Android Studio

1. Open the project in Android Studio
2. Select your target device or emulator
3. Press `Run` (⌘R on Mac, Shift+F10 on Windows/Linux)

## Testing

### Run All Unit Tests

```bash
./gradlew testDebugUnitTest
```

Unit tests use JUnit 4, Mockito, and Robolectric for Android framework mocking.

### Run Instrumented Tests

```bash
./gradlew connectedDebugAndroidTest
```

Instrumented tests require an Android 11+ device or emulator to be connected. These tests include:
- Database integration tests with Room
- Repository layer tests
- UI component tests with Espresso

**Note**: Instrumented tests are not run in the CI/CD pipeline. Run them locally before submitting PRs for UI-related changes.

### Generate Test Coverage Report

```bash
./gradlew jacocoTestReport
```

Coverage reports are generated at: `app/build/reports/jacoco/jacocoTestReport/html/index.html`

### Run Specific Test Class

```bash
./gradlew testDebugUnitTest --tests "com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.MapDetailViewModelTest"
```

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/upwardsnorthwards/blueplaqueslondon/
│   │   │   ├── activities/        # Legacy Activities (mostly converted to fragments)
│   │   │   ├── fragments/         # UI fragments using Compose/ViewBinding
│   │   │   ├── ui/
│   │   │   │   ├── composables/  # Jetpack Compose components
│   │   │   │   ├── viewmodel/    # MVVM ViewModels
│   │   │   │   └── theme/        # Material 3 theme configuration
│   │   │   ├── data/
│   │   │   │   ├── local/        # Room database, DAOs
│   │   │   │   ├── remote/       # API clients (Retrofit)
│   │   │   │   └── repository/   # Repository pattern implementations
│   │   │   ├── di/               # Hilt dependency injection modules
│   │   │   ├── model/            # Data models and entities
│   │   │   ├── utils/            # Utility functions and helpers
│   │   │   └── workers/          # WorkManager background tasks
│   │   ├── res/                  # Resources (layouts, strings, drawables, etc.)
│   │   └── AndroidManifest.xml
│   ├── test/                     # Unit tests
│   ├── androidTest/              # Instrumented tests
│   └── schemas/                  # Room database schemas
├── build.gradle                  # App-level build configuration
└── google-services.json          # Firebase configuration (not in git)
```

## Architecture

### MVVM with Repository Pattern

```
UI Layer (Fragments/Compose)
        ↓
ViewModel (State Management)
        ↓
Repository (Business Logic)
        ↓
Data Sources (Room DB, APIs)
```

### Key Components

- **ViewModels**: Manage UI state and handle lifecycle-aware data
- **LiveData**: Observe and react to data changes
- **Repositories**: Abstract data sources (local DB, remote APIs)
- **DAOs**: Direct database access for Room
- **Hilt Modules**: Provide dependencies and configure DI

## Development Workflow

### 1. Create a Feature Branch

```bash
git checkout -b feature/your-feature-name
```

### 2. Make Changes

Write code following the project's conventions and patterns.

### 3. Run Tests

```bash
./gradlew testDebugUnitTest
```

Ensure all unit tests pass.

### 4. Check Code Quality

```bash
./gradlew lint
```

Address any lint warnings before committing.

### 5. Commit Your Changes

```bash
git add .
git commit -m "Add clear description of your changes"
```

### 6. Push to Remote

```bash
git push origin feature/your-feature-name
```

### 7. Submit a Pull Request

Create a PR on GitHub and describe your changes. The CI pipeline will automatically run tests and generate reports.

## Common Tasks

### Add a New Fragment

1. Create a new Fragment class in `ui/fragments/`
2. Add its layout in `res/layout/`
3. Register it in Navigation Graph (`res/navigation/nav_graph.xml`)
4. Update navigation from parent fragment

### Add a ViewModel

1. Create a class extending `ViewModel` in `ui/viewmodel/`
2. Use `@HiltViewModel` and inject dependencies with `@Inject constructor()`
3. Expose `LiveData` properties for UI observation
4. Write unit tests in `test/java/`

### Add a Database Entity

1. Create a class with `@Entity` annotation in `data/local/entity/`
2. Create a DAO with `@Dao` annotation in `data/local/dao/`
3. Add DAO to `PlaqueDatabase` in `data/local/`
4. Update migration if changing existing schema

### Add a Repository

1. Create a class in `data/repository/`
2. Inject necessary DAOs and API clients
3. Implement business logic combining data sources
4. Use Hilt's `@Singleton` for single instance

## Debugging

### Enable Debug Logging

The app uses Android's standard logging. View logs with:

```bash
./gradlew connectedDebugAndroidTest --info
adb logcat
```

### Debug with Android Studio

1. Set breakpoints in code
2. Run app in debug mode: **Run** → **Debug 'app'**
3. Use the debugger window to inspect variables

### Profile with Android Studio Profiler

1. Open **View** → **Tool Windows** → **Profiler**
2. Select your device/emulator
3. Monitor CPU, Memory, Network, and Energy usage

## Troubleshooting

### Gradle Build Fails

```bash
./gradlew clean build
```

Clear caches and rebuild.

### Dependencies Not Downloading

Ensure you have internet connection and try:

```bash
./gradlew build --refresh-dependencies
```

### API Key Issues

- Verify `google-services.json` is in `app/src/`
- Check API key is correctly set in AndroidManifest.xml
- Ensure API key has Android app restrictions enabled

### Test Failures

1. Run tests individually to isolate the issue
2. Check logcat for error messages
3. Verify test data is properly set up
4. Ensure mocks are correctly configured

### Emulator Won't Connect

```bash
adb kill-server
adb start-server
```

## CI/CD Pipeline

The project uses GitHub Actions for continuous integration:

- **Build**: Compiles debug and release APKs
- **Unit Tests**: Runs all unit tests with coverage reporting
- **Lint**: Checks code quality
- **Coverage**: Generates coverage reports and uploads to Codecov

View workflow file: `.github/workflows/ci.yml`

## Performance Tips

- Use `@HiltViewModel` and constructor injection for ViewModels
- Leverage `LiveData` and `StateFlow` for reactive updates
- Implement database queries efficiently with proper indices
- Use Compose `remember()` and `mutableStateOf()` carefully to avoid unnecessary recompositions
- Profile the app regularly with Android Profiler

## Resources

- [Android Developer Guide](https://developer.android.com/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Hilt Dependency Injection](https://dagger.dev/hilt/)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Navigation Component](https://developer.android.com/guide/navigation)
- [Material Design 3](https://m3.material.io/)

## Getting Help

- Check existing issues on GitHub
- Review the codebase for similar implementations
- Consult Android documentation
- Ask in PR reviews or discussions
