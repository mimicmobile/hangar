import 'package:settings/models/pref.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/utils.dart';

class BehaviorData extends PreferenceData {
  Future refresh() async {
    Utils.getSharedPrefs().then((sp) {
      prefs.add(RadioChoicePref<String>(
          "jobInterval",
          "Update interval",
          "Notification updates happen every _value_",
          sp,
          "20 seconds",
          ["5 seconds", "10 seconds", "15 seconds", "20 seconds", "30 seconds", "1 minute", "2 minutes"]));
    });
  }
}