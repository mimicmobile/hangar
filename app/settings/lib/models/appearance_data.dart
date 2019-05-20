import 'package:settings/models/pref.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/utils.dart';

class AppearanceData extends PreferenceData {
  Future refresh() async {
    Utils.getSharedPrefs().then((sp) {
      prefSet.add(PrefSet("Notification", [
        RadioChoicePref<int>("appsPerRow", "Number of apps",
            ["_value_ per row"], sp, 7, [6, 7, 8]),
        RadioChoicePref<int>("numRows", "Number of rows",
            ["_value_ rows", "_value_ row"], sp, 2, [1, 2, 3]),
        RadioChoicePref<int>("numPages", "Number of pages",
            ["_value_ pages", "_value_ page"], sp, 1, [1, 2, 3]),
        RadioChoicePref<String>("backgroundColor", "Background color",
            ["_value_"], sp, "White", ["White", "Material Dark", "Black"])
      ]));
    });
  }
}
