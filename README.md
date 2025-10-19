[![Twitter: @seanoshea](https://img.shields.io/badge/contact-@seanoshea-blue.svg?style=flat)](https://twitter.com/seanoshea)
[![License](http://img.shields.io/badge/license-BSD-green.svg?style=flat)](https://github.com/seanoshea/BluePlaquesLondon-Android/blob/master/LICENSE)
[![Android CI](https://github.com/seanoshea/BluePlaquesLondon-Android/actions/workflows/android-ci.yml/badge.svg)](https://github.com/seanoshea/BluePlaquesLondon-Android/actions/workflows/android-ci.yml)
[![codecov](https://codecov.io/gh/seanoshea/BluePlaquesLondon-Android/branch/develop/graph/badge.svg)](https://codecov.io/gh/seanoshea/BluePlaquesLondon-Android)
[![PRs Welcome](https://img.shields.io/badge/prs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

# BluePlaquesLondon-Android

This is a modernized version of the BluePlaquesLondon Android application, which helps users find historical blue plaques throughout London. The app is available on the [Google Play Store](https://play.google.com/store/apps/details?id=com.upwardsnorthwards.blueplaqueslondon).

This project has undergone a significant modernization effort to align with the latest Android development best practices. The app now features a single-activity architecture, Jetpack Compose, Material Design 3, and a modern tech stack.

## Screenshots

<a href="http://imgur.com/NXis7Ui"><img src="http://i.imgur.com/NXis7Ui.png" title="source: imgur.com" /></a>
<a href="http://imgur.com/L6TKtZW"><img src="http://i.imgur.com/L6TKtZW.png" title="source: imgur.com" /></a>
<a href="http://imgur.com/tsyVF9L"><img src="http://i.imgur.com/tsyVF9L.png" title="source: imgur.com" /></a>
<a href="http://imgur.com/crwJFqh"><img src="http://i.imgur.com/crwJFqh.png" title="source: imgur.com" /></a>

## Tech Stack

- **Architecture**: Single-activity architecture with Jetpack Navigation Component
- **UI**: Jetpack Compose and Material Design 3
- **Build System**: Gradle with Kotlin DSL
- **Asynchronous Operations**: Coroutines
- **Dependency Injection**: Hilt (to be implemented)
- **Testing**: JUnit, Mockito, Espresso, Robolectric
- **Code Quality**: Spotless with ktlint

## Building from Source

To build the application from source, follow these steps:

1.  **Clone the repository**:

    ```
    git clone https://github.com/seanoshea/BluePlaquesLondon-Android.git
    ```

2.  **Create a `local.properties` file**:

    Create a `local.properties` file in the root of the project and add your Google Maps API key:

    ```
    GOOGLE_MAPS_API_KEY=YOUR_API_KEY
    ```

3.  **Build the app**:

    ```
    ./gradlew assembleDebug
    ```

## Contributing

Suggestions and bug reports for the application are always welcome. Open an issue on GitHub if you'd like to see an addition to the application or if you spot a bug. Pull requests are especially welcome (and most likely to get merged if you have some unit tests associated with the merge request).

## License

This project is licensed under the BSD License - see the [LICENSE](LICENSE) file for details.
