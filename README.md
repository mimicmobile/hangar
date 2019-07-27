# Hangar
Hangar (3.0?) for Android -- rewritten in Kotlin and Flutter
Currently a WIP rebuild of the original [Hangar](https://github.com/corcoran/hangar)
## TODO
- Ability to change the 'switch page' icon
- App shortcuts/Stats widgets
## Screenshots
<a href="screenshots/1.jpg"><img src="screenshots/1.jpg" width="260"></a>
<a href="screenshots/2.jpg"><img src="screenshots/2.jpg" width="260"></a>
<a href="screenshots/3.jpg"><img src="screenshots/3.jpg" width="260"></a>
## Why?!
The motivation behind this rewrite is explained in this youtube video!

<a href="https://www.youtube.com/watch?v=aN699vpnRXg"><img src="screenshots/talk.jpg" width="260"></a>
## Building
There are a few glitches and gotchas that seem to happen (currently) when your Android app has a Flutter module.
1) Set the `sdk.dir` and `flutter.sdk` variables in `app/settings/.android/local.properties` to reflect where you have the Android SDKs and flutter SDK installed respectively.
2) If you wish to use Flutter hot reload, run `flutter attach` in `app/settings` _before_ running a debug build.
4) Dart support seems to randomly switch off in Android Studio.  Enable to fix.
5) Occasionally the `io.flutter.plugins.GeneratedPluginRegistrant` import in `MainActivity` will fail to be found by Android Studio. Gradle sync will look like it's succeeding but you'll see the pinned message that it has failed.  Restarting Android Studio seems to fix this.
