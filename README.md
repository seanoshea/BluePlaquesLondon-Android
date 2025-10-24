[![CI](https://github.com/seanoshea/BluePlaquesLondon-Android/workflows/CI/badge.svg)](https://github.com/seanoshea/BluePlaquesLondon-Android/actions)
[![PRs Welcome](https://img.shields.io/badge/prs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)
[![License](http://img.shields.io/badge/license-BSD-green.svg?style=flat)](https://github.com/seanoshea/BluePlaquesLondon-Android/blob/master/LICENSE)
[![Languages](https://img.shields.io/github/languages/count/seanoshea/BluePlaquesLondon-Android)](https://img.shields.io/github/languages/count/seanoshea/BluePlaquesLondon-Android)
[![Open Issues](https://img.shields.io/github/issues/seanoshea/BluePlaquesLondon-Android)](https://img.shields.io/github/issues/seanoshea/BluePlaquesLondon-Android)
[![Closed Issues](https://img.shields.io/github/issues-closed/seanoshea/BluePlaquesLondon-Android)](https://img.shields.io/github/issues-closed/seanoshea/BluePlaquesLondon-Android)
[![Twitter: @seanoshea](https://img.shields.io/badge/contact-@seanoshea-blue.svg?style=flat)](https://twitter.com/seanoshea)

## BluePlaquesLondon Android
Android Application for finding Blue Plaques through London. **Requires Android 11+**. [Google Play Store Link](https://play.google.com/store/apps/details?id=com.upwardsnorthwards.blueplaqueslondon)

## Requirements
- **Minimum Android Version**: Android 11 (API 30)
- **Target Android Version**: Android 15 (API 35)

## Screenshots
<a href="http://imgur.com/NXis7Ui"><img src="http://i.imgur.com/NXis7Ui.png" title="source: imgur.com" /></a>
<a href="http://imgur.com/L6TKtZW"><img src="http://i.imgur.com/L6TKtZW.png" title="source: imgur.com" /></a>
<a href="http://imgur.com/tsyVF9L"><img src="http://i.imgur.com/tsyVF9L.png" title="source: imgur.com" /></a>
<a href="http://imgur.com/crwJFqh"><img src="http://i.imgur.com/crwJFqh.png" title="source: imgur.com" /></a>

## Features

### Core Functionality
- **Interactive Map**: Explore 900+ blue plaques across London with Google Maps integration
- **Advanced Search**: Find plaques by person name, location, or occupation with real-time filtering
- **Detailed Information**: View comprehensive plaque details, Wikipedia articles, and Street View panoramas
- **Offline Support**: Core functionality works without internet connection using local database

### Technical Features
- **Modern UI**: Material Design 3 with smooth animations and responsive layouts
- **Performance Optimized**: Efficient data loading with RxJava3 reactive streams
- **Secure**: HTTPS-only network communications with certificate validation
- **Accessible**: Full accessibility support with content descriptions and navigation
- **Analytics**: Firebase Analytics integration for usage insights

## Technology Stack

### Architecture
- **Pattern**: MVVM (Model-View-ViewModel) with Repository pattern
- **Dependency Injection**: Hilt for compile-time dependency injection
- **Navigation**: Android Navigation Component with Safe Args
- **Database**: Room with RxJava3 for reactive data access

### Modern Android Development
- **Language**: Java 17 with Kotlin interoperability
- **UI Framework**: Android Views with Material Design 3 components
- **Networking**: Retrofit with OkHttp for REST API communication
- **Maps**: Google Maps Android SDK with custom markers
- **Analytics**: Firebase Analytics and Crashlytics
- **Background Tasks**: WorkManager for reliable background processing

### Development Tools
- **Build System**: Gradle with version catalogs
- **Code Quality**: Detekt, Android Lint, OWASP Dependency Check
- **Testing**: JUnit, Mockito, Robolectric, Espresso
- **CI/CD**: GitHub Actions with automated testing and security scanning

## Quick Start

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK with API 30+ (Android 11+)
- Google Maps API key

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/seanoshea/BluePlaquesLondon-Android.git
   cd BluePlaquesLondon-Android
   ```

2. Configure API keys in `local.properties`:
   ```properties
   GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here
   ```

3. Set up Firebase:
   ```bash
   cp BluePlaquesLondon/app/google-services.json.sample BluePlaquesLondon/app/google-services.json
   # Edit google-services.json with your Firebase project configuration
   ```

4. Build and run:
   ```bash
   ./gradlew assembleDebug
   ```

## Development

For detailed development setup, building, testing, and contributing guidelines, see [DEVELOPMENT.md](DEVELOPMENT.md).

## Contributing

We welcome contributions! Please see [DEVELOPMENT.md](DEVELOPMENT.md) for setup instructions and contribution guidelines.

## Related Projects

- **iOS Version**: [BluePlaquesLondon iOS](http://github.com/seanoshea/BluePlaquesLondon) - Available on the [App Store](http://www.appstore.com/seanoshea)
- **Beta Testing**: Join our [Google Group](http://groups.google.com/forum/#!groupsettings/blue-plaques-london-android-beta-testers/information) for early access

## License

This project is licensed under the BSD License - see the [LICENSE](LICENSE) file for details.
