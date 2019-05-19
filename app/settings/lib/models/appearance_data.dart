import 'package:settings/models/pref.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/utils.dart';

class AppearanceData extends PreferenceData {
  Future refresh() async {
    Utils.getSharedPrefs().then((sp) {
      prefs.add(RadioChoicePref<int>("appsPerRow", "Number of apps",
          "Showing _value_ per row", sp, 7, [6, 7, 8]));
      prefs.add(RadioChoicePref<int>("numRows", "Number of rows",
          "_value_ rows when expanded", sp, 2, [1, 2, 3]));
      prefs.add(RadioChoicePref<int>("numPages", "Number of pages",
          "_value_ page(s) to cycle through", sp, 1, [1, 2, 3]));
      prefs.add(RadioChoicePref<String>("backgroundColor", "Background color",
          "Notification background is set to _value_", sp, "White", ["White", "Material Dark", "Black"]));
    });
  }
}
