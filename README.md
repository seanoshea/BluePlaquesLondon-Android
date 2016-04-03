[![Twitter: @seanoshea](https://img.shields.io/badge/contact-@seanoshea-blue.svg?style=flat)](https://twitter.com/seanoshea)
[![License](http://img.shields.io/badge/license-BSD-green.svg?style=flat)](https://github.com/seanoshea/BluePlaquesLondon-Android/blob/master/LICENSE)
[![Build Status](https://img.shields.io/travis/seanoshea/BluePlaquesLondon-Android/develop.svg?style=flat)](https://travis-ci.org/seanoshea/BluePlaquesLondon-Android)
### BluePlaquesLondon Android
Android Application for finding Blue Plaques through London. [Google Play Store Link](https://play.google.com/store/apps/details?id=com.upwardsnorthwards.blueplaqueslondon)

### Screenshots
<a href="http://imgur.com/NXis7Ui"><img src="http://i.imgur.com/NXis7Ui.png" title="source: imgur.com" /></a>
<a href="http://imgur.com/L6TKtZW"><img src="http://i.imgur.com/L6TKtZW.png" title="source: imgur.com" /></a>
<a href="http://imgur.com/tsyVF9L"><img src="http://i.imgur.com/tsyVF9L.png" title="source: imgur.com" /></a>
<a href="http://imgur.com/crwJFqh"><img src="http://i.imgur.com/crwJFqh.png" title="source: imgur.com" /></a>

### Contributing
Suggestions and bug reports for the application are always welcome. Open an issue on github if you'd like to see an addition to the application or if you spot a bug. Pull requests are especially welcome (and most likely to get merged if you have some unit tests associated with the merge request).

### Building a Release
- Alter the `gradle.properties` file to look like:
```
RELEASE_STORE_FILE=~/production.keystore
RELEASE_STORE_PASSWORD=password_here
RELEASE_KEY_ALIAS=production
RELEASE_KEY_PASSWORD=password_here
```
- Ensure the `production.keystore` file includes the `AB:47:6B:2C:00:7E:00:35:3C:CA:58:5F:43:89:6F:8D:2F:F6:EB:B3` SHA by executing:
```
keytool -list -v -keystore ~/production.keystore
```
- Navigate to https://console.developers.google.com/apis/credentials?project=blue-plaques-london
- Alter the `com.google.android.maps.v2.API_KEY` property in `AndroidManifest.xml` to include the production key for Google Maps. The Google Developer Console lists it as a production key.
- Execute `./gradlew assembleRelease` to build.
- Execute `adb install app/app-release.apk` while having a device attached.

### iPhone/iPad Version
The original idea behind an application for showing historical blue plaques around London was implemented in Objective-C for iOS devices. The source code for that version of the application is available [here](http://github.com/seanoshea/BluePlaquesLondon). It is downloadable from the Apple's [AppStore](http://www.appstore.com/seanoshea)

### Beta Builds
If you're interested in access to beta-builds of the application, there's a Google Group available [here](http://groups.google.com/forum/#!groupsettings/blue-plaques-london-android-beta-testers/information)
