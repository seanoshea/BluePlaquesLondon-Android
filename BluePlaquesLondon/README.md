# Blue Plaques London (Android)

A mobile application that showcases the blue plaques of London - historical markers commemorating notable figures and events across the city. Built with modern Android development practices using Jetpack, Hilt, and Compose.

## Features

- 📍 **Interactive Map** - Explore over 900 blue plaques across London
- 🔍 **Search & Filter** - Find plaques by person, location, or occupation
- 📖 **Wikipedia Integration** - Learn more about each plaque's history
- 📷 **Street View** - See the plaques in their real-world context with Google Street View
- 🗺️ **Offline Support** - Works with cached plaque data
- 🌙 **Modern UI** - Material Design 3 with Jetpack Compose

## Technology Stack

- **Architecture**: MVVM with Repository pattern
- **UI Framework**: Jetpack Compose + Material Design 3
- **Navigation**: Navigation Component
- **Database**: Room with RxJava3 integration
- **Dependency Injection**: Hilt
- **Testing**: JUnit 4, Mockito, Robolectric
- **CI/CD**: GitHub Actions
- **API Integration**: Google Maps, Firebase, Wikipedia

## Getting Started

### Requirements

- Android Studio Koala or later
- JDK 17+
- Android SDK 35+
- Gradle 8.11+

### Quick Setup

```bash
git clone https://github.com/seanoshea/BluePlaquesLondon-Android.git
cd BluePlaquesLondon-Android/BluePlaquesLondon
./gradlew assembleDebug
```

For detailed setup instructions, see [DEVELOPMENT.md](./DEVELOPMENT.md).

## Documentation

- **[DEVELOPMENT.md](./DEVELOPMENT.md)** - Complete developer setup and contributing guide
- **[CHANGELOG.md](./CHANGELOG.md)** - Release history and version changes
- **[CONTRIBUTING.md](./.github/CONTRIBUTING.md)** - Contributing guidelines

## License

BSD 2-Clause License - See [LICENSE](../LICENSE) for details

Copyright (c) 2014-2024 Upwards Northwards Software Limited

## Support

For issues, questions, or feature requests, please open an issue on GitHub.
