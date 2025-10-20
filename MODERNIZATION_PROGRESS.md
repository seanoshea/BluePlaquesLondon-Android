# Android Modernization Progress

## Overview
This document tracks the progress of modernizing the Blue Plaques London Android app from 2017-era architecture to modern MVVM + Jetpack architecture.

## ✅ Phase 1: Build System & Dependencies (COMPLETED)

### Gradle & Build Tools
- ✅ Gradle: 3.3 → 8.11
- ✅ Android Gradle Plugin: 2.3.1 → 8.7.3
- ✅ Java: 8 → 17
- ✅ compileSdk: 25 → 35
- ✅ targetSdk: 25 → 35
- ✅ minSdk: 15 → 24 (removes multidex requirement!)
- ✅ Build Tools updated to latest

### Configuration Files Updated
- ✅ `gradle-wrapper.properties` - Gradle 8.11
- ✅ `build.gradle` (root) - AGP 8.7.3, modern repositories
- ✅ `app/build.gradle` - Completely rewritten with modern config
- ✅ `gradle.properties` - AndroidX enabled, Jetifier enabled
- ✅ `AndroidManifest.xml` - Modernized for Android 12+
- ✅ `google-services.json` - Placeholder created (needs real Firebase project)
- ✅ `data_extraction_rules.xml` - Android 12+ backup rules

### Dependencies Modernized

#### Removed (Deprecated)
- ❌ Otto event bus → Replaced with ViewModel/LiveData
- ❌ Fabric Crashlytics → Replaced with Firebase Crashlytics
- ❌ Google Analytics → Replaced with Firebase Analytics
- ❌ Monolithic Play Services → Replaced with modular Play Services
- ❌ Old Support Libraries → Migrated to AndroidX
- ❌ android-rate library → Will use Google Play Core In-App Review

#### Added (Modern)
- ✅ **AndroidX Core Libraries**
  - appcompat:1.7.0
  - core:1.15.0
  - constraintlayout:2.2.0
  - Material Design:1.12.0

- ✅ **Lifecycle Components (MVVM)**
  - lifecycle-viewmodel:2.8.7
  - lifecycle-livedata:2.8.7
  - lifecycle-runtime:2.8.7

- ✅ **Navigation Component**
  - navigation-fragment:2.8.5
  - navigation-ui:2.8.5
  - Safe Args plugin configured

- ✅ **Room Database**
  - room-runtime:2.6.1
  - room-compiler:2.6.1
  - room-rxjava3:2.6.1

- ✅ **Hilt Dependency Injection**
  - hilt-android:2.51.1
  - hilt-compiler:2.51.1
  - hilt-work:1.2.0

- ✅ **WorkManager**
  - work-runtime:2.10.0

- ✅ **DataStore** (modern SharedPreferences)
  - datastore-preferences:1.1.1

- ✅ **Networking**
  - Retrofit:2.11.0
  - OkHttp:4.12.0
  - Gson:2.11.0
  - RxJava3:3.1.10

- ✅ **Firebase**
  - Firebase BOM:33.7.0
  - Firebase Crashlytics
  - Firebase Analytics

- ✅ **Google Play Services** (modular)
  - play-services-maps:19.0.0
  - play-services-location:21.3.0

- ✅ **Google Play Core**
  - In-App Review API:2.0.2

- ✅ **Testing**
  - JUnit:4.13.2
  - Espresso:3.6.1
  - AndroidX Test:1.6.2

### Build Features Enabled
- ✅ View Binding
- ✅ BuildConfig generation
- ✅ Java 17 source/target compatibility
- ✅ Non-transitive R classes
- ✅ Gradle configuration cache

## ✅ Phase 2: Hilt Setup (COMPLETED)

### Application Class
- ✅ `BluePlaquesLondonApplication.java` - Migrated to Hilt
  - Removed MultiDex (no longer needed with minSdk 24)
  - Removed Otto bus
  - Removed old Google Analytics
  - Removed Fabric Crashlytics
  - Added `@HiltAndroidApp` annotation
  - Integrated Firebase Analytics
  - Integrated Firebase Crashlytics
  - Removed location services (moved to repository layer)

### Hilt Modules Created
- ✅ `NetworkModule.java` - Provides Retrofit, OkHttp, Gson
- ✅ `LocationModule.java` - Provides FusedLocationProviderClient
- ✅ `DatabaseModule.java` - Provides Room database and DAOs

## ⚠️ Phase 3: Room Database (IN PROGRESS - NOT STARTED)

### What Needs to be Done
1. Create `data/local/entity/PlaqueEntity.java`
   - Convert `Placemark` model to Room entity
   - Add `@Entity`, `@PrimaryKey`, `@ColumnInfo` annotations
   - Keep all existing fields

2. Create `data/local/dao/PlaqueDao.java`
   - CRUD operations using RxJava3
   - Search/filter queries
   - Get all plaques
   - Get plaque by location

3. Create `data/local/PlaqueDatabase.java`
   - Room database class
   - Define entities and version
   - Migration strategy

4. Update `Placemark.java`
   - Migrate to AndroidX annotations
   - Keep as domain model (separate from entity)
   - Add mapper methods to/from PlaqueEntity

## ⚠️ Phase 4: Repository Layer (NOT STARTED)

### Repositories to Create
1. `data/repository/PlaquesRepository.java`
   - Single source of truth for plaque data
   - Parse KML file and insert into Room
   - Expose LiveData/RxJava observables
   - Handle caching logic

2. `data/repository/WikipediaRepository.java`
   - Use Retrofit for API calls
   - Return RxJava3 observables
   - Error handling and retry logic

3. `data/repository/LocationRepository.java`
   - Wrap FusedLocationProviderClient
   - Request location permissions
   - Expose location updates as LiveData
   - Handle permission denial

### API Services to Create
1. `data/remote/WikipediaApiService.java`
   - Retrofit interface for Wikipedia API
   - Search endpoint
   - Article endpoint

## ⚠️ Phase 5: ViewModels (NOT STARTED)

### ViewModels to Create
1. `ui/main/MainViewModel.java`
   - Manage map state
   - Handle plaque selection
   - Search functionality
   - Location updates

2. `ui/detail/MapDetailViewModel.java`
   - Selected plaque details
   - Similar plaques logic

3. `ui/wikipedia/WikipediaViewModel.java`
   - Load Wikipedia article
   - Handle loading states

## ⚠️ Phase 6: Update Activities & Fragments (NOT STARTED)

### Files Needing AndroidX Migration
All Java files need import statement updates:
- `android.support.v7.app.AppCompatActivity` → `androidx.appcompat.app.AppCompatActivity`
- `android.support.v4.app.Fragment` → `androidx.fragment.app.Fragment`
- `android.support.annotation.NonNull` → `androidx.annotation.NonNull`
- And many more...

### Activities to Update
1. `activities/BaseActivity.java`
   - Migrate to AndroidX
   - Add Hilt injection (`@AndroidEntryPoint`)
   - Remove Otto bus subscriptions
   - Inject dependencies

2. `activities/MainActivity.java`
   - Add `@AndroidEntryPoint`
   - Inject MainViewModel
   - Observe LiveData instead of Otto bus
   - Implement View Binding
   - Update to Navigation Component

3. `activities/MapDetailActivity.java`
   - Add Hilt + ViewModel
   - Implement View Binding
   - Remove intent extras, use Safe Args

4. `activities/WikipediaActivity.java`
   - Add Hilt + ViewModel
   - Implement View Binding

5. `activities/PanoramaActivity.java`
   - Add Hilt
   - Implement View Binding

### Fragments to Update
1. `fragments/BluePlaquesMapFragment.java`
   - Add `@AndroidEntryPoint`
   - Inject ViewModel
   - Remove AsyncTask
   - Use RxJava/Executors for KML parsing
   - Observe LiveData from ViewModel

2. `fragments/AboutFragment.java`
   - AndroidX migration
   - View Binding

3. `fragments/SettingsFragment.java`
   - AndroidX migration
   - Migrate to PreferenceFragmentCompat
   - Use DataStore instead of SharedPreferences

## ⚠️ Phase 7: Replace AsyncTasks (NOT STARTED)

### Files Using AsyncTask
1. `BluePlaquesMapFragment.java` - ParsePlaquesTask
   - Replace with RxJava3 or Executors
   - Use repository pattern

2. `model/WikipediaModel.java` - WikipediaModel extends AsyncTask
   - Replace with Retrofit + RxJava3
   - Move to WikipediaRepository

## ⚠️ Phase 8: Navigation Component (NOT STARTED)

### What Needs to be Done
1. Create `res/navigation/nav_graph.xml`
2. Define all destinations (Main, Detail, Wikipedia, Panorama)
3. Define navigation actions
4. Add Safe Args for type-safe argument passing
5. Update MainActivity to use NavHostFragment
6. Remove manual intent navigation

## ⚠️ Phase 9: DataStore Migration (NOT STARTED)

### Files to Update
1. `utils/BluePlaquesSharedPreferences.java`
   - Migrate from SharedPreferences to DataStore
   - Use Preferences DataStore (not Proto)
   - Expose as Flow/LiveData
   - Inject via Hilt

## ⚠️ Phase 10: Utilities & Models (NOT STARTED)

### Files Needing AndroidX Migration
1. `utils/InternetConnectivityHelper.java` - AndroidX imports
2. `utils/BluePlaquesKMLParser.java` - AndroidX imports
3. `model/MapModel.java` - AndroidX imports
4. `model/KeyedMarker.java` - AndroidX imports
5. `model/WikipediaModelSearchResult.java` - AndroidX imports
6. All adapter classes - AndroidX imports

## ⚠️ Phase 11: GitHub Actions CI (NOT STARTED)

### What Needs to be Done
1. Update `.github/workflows/ci.yml`
   - JDK 17 (not JDK 8)
   - Android SDK 35
   - API 35 emulator
   - Remove obsolete SDK tools installation
   - Add Firebase dummy config for CI builds

## 🚧 Known Issues & Next Steps

### Immediate Blockers
1. **All source files need AndroidX import updates** - This is a massive refactoring across 20+ files
2. **Room database not implemented** - Database module references non-existent classes
3. **Repositories not created** - ViewModels can't be created without repositories
4. **Build will fail** - Source code still uses old support library imports

### Recommended Approach
**Option 1: Continue Manual Migration**
- Systematically update each Java file
- Time-consuming but complete control
- Will take several more hours

**Option 2: Use Android Studio Automated Migration**
- Open project in Android Studio
- Run "Refactor → Migrate to AndroidX"
- Automatic but may need manual fixes
- Much faster (5-10 minutes vs several hours)

**Option 3: Commit Current Progress, Test Build**
- Commit build configuration changes
- Verify Gradle sync works
- Continue source code migration in follow-up session

### Testing Strategy
Once migration is complete:
1. Run `./gradlew clean build` - Verify compilation
2. Run `./gradlew test` - Unit tests
3. Run `./gradlew connectedAndroidTest` - Instrumented tests
4. Manual testing on device/emulator
5. Verify all features work:
   - Map display
   - Plaque search
   - Plaque details
   - Wikipedia integration
   - Street View
   - Location services

### Firebase Setup Required
The placeholder `google-services.json` needs to be replaced:
1. Create Firebase project at https://console.firebase.google.com
2. Add Android app with package name `com.upwardsnorthwards.blueplaqueslondon`
3. Download real `google-services.json`
4. Replace placeholder file
5. Configure Crashlytics and Analytics

### API Keys to Update
1. **Google Maps API Key** - Currently in `AndroidManifest.xml`
   - Verify it's still valid
   - Update billing if needed
2. **Firebase** - Needs real project setup

## 📊 Estimated Remaining Work

- **Phase 3-5 (Room, Repositories, ViewModels)**: 2-3 days
- **Phase 6 (Update Activities/Fragments)**: 3-4 days
- **Phase 7 (Replace AsyncTasks)**: 1 day
- **Phase 8 (Navigation Component)**: 1-2 days
- **Phase 9 (DataStore)**: 1 day
- **Phase 10 (Utilities)**: 1 day
- **Phase 11 (CI/CD)**: 0.5 day
- **Testing & Bug Fixes**: 2-3 days

**Total Estimated Time**: 2-3 weeks of development work

## 🎯 Current State Summary

**What Works:**
- ✅ Build configuration is modern (Gradle 8.11, AGP 8.7.3)
- ✅ All modern dependencies added
- ✅ Hilt configured with modules
- ✅ Firebase integrated (placeholder config)
- ✅ AndroidManifest modernized

**What Doesn't Work Yet:**
- ❌ Build will fail - source code not migrated to AndroidX
- ❌ No ViewModels - architecture not implemented
- ❌ No Room database - data layer not implemented
- ❌ No repositories - business logic not refactored
- ❌ Activities/Fragments still use old APIs

**Next Critical Step:**
Migrate all Java source files to AndroidX imports, then implement the MVVM architecture layer by layer.
