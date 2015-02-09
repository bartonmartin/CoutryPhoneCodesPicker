WeatherApp
=======

Sample app gets today weather and next week forecast from World Weather Online API. It uses geolocation for determining current position of the device.

Building project
================

1. Clone this repository
2. Run `gradlew assemble` in console
3. APK should be available in _/app directory

**Note:** You will also need a "local.properties" file to set the location of the SDK in the same way that the existing SDK requires, using the "sdk.dir" property. Example of "local.properties" on Windows: `sdk.dir=C:\\adt-bundle-windows\\sdk`. Alternatively, you can set an environment variable called "ANDROID\_HOME".

**Tip:** Command `gradlew assemble` builds both - debug and release APK. You can use `gradlew assembleDebug` to build debug APK. You can use `gradlew assembleRelease` to build release APK. Debug APK is signed by debug keystore. Release APK is signed by own keystore, stored in _/extras/keystore_ directory.

**Signing process:** Keystore passwords are automatically loaded from property file during building the release APK. Path to this file is defined in "keystore.properties" property in "gradle.properties" file. If this property or the file does not exist, user is asked for passwords explicitly.

Dependencies
============

* [GSON](http://code.google.com/p/google-gson/)
* [Universal Image Loader](https://github.com/nostra13/Android-Universal-Image-Loader)

Developed by
============

* Martin Barto+n+n
