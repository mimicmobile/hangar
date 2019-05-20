import 'package:settings/models/pref.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/utils.dart';

class BehaviorData extends PreferenceData {
  Future refresh() async {
    Utils.getSharedPrefs().then((sp) {
      prefSet.add(PrefSet("Notification", [
        RadioChoicePref<int>(
            "jobInterval",
            "Update interval",
            ["Every %s"],
            sp,
            15000,
            [
              ["5 seconds", 5000],
              ["10 seconds", 10000],
              ["15 seconds", 15000],
              ["20 seconds", 20000],
              ["30 seconds", 30000],
              ["1 minute", 60000],
              ["2 minutes", 120000]
            ])
      ]));
      prefSet.add(PrefSet("Sorting", [
        RadioChoicePref<String>(
            "notificationWeight",
            "Order priority",
            ["%s"],
            sp,
            "lastUsed",
            [
              ["Last used", "lastUsed"],
              ["Time spend in app", "foregroundTime"],
              ["Most launched", "timesLaunched"]
            ])
      ]));
    });
  }
}
