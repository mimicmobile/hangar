import 'package:settings/models/pref.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/reusable.dart';
import 'package:settings/utils.dart';

class AppearanceData extends PreferenceData {
  Future refresh() async {
    var sp = await Utils.getSharedPrefs();
    var iconPackList = await Reusable.fetchIconPacks();

    prefSet.add(PrefSet("Notification", [
      RadioChoicePref<int>(
          "appsPerRow",
          "Number of apps",
          ["%s per row"],
          sp,
          7,
          [
            ["6", 6],
            ["7", 7],
            ["8", 8]
          ]),
      RadioChoicePref<int>(
          "numRows",
          "Number of rows",
          ["%s rows", "%s row"],
          sp,
          2,
          [
            ["1", 1],
            ["2", 2],
            ["3", 3]
          ]),
      RadioChoicePref<int>(
          "numPages",
          "Number of pages",
          ["%s pages", "%s page"],
          sp,
          1,
          [
            ["1", 1],
            ["2", 2],
            ["3", 3]
          ]),
      RadioChoicePref<String>(
          "backgroundColor",
          "Background color",
          ["%s"],
          sp,
          "white",
          [
            ["White", "white"],
            ["Material Dark", "materialDark"],
            ["Black", "black"]
          ]),
      RadioChoicePref<String>(
          "iconSize",
          "Icon size",
          ["%s"],
          sp,
          "medium",
          [
            ["Small", "small"],
            ["Medium", "medium"],
            ["Large", "large"]
          ]),
      RadioChoicePref<String>(
          "pinnedAppPlacement",
          "Pinned app placement",
          ["%s"],
          sp,
          "left",
          [
            ["Left", "left"],
            ["Right", "right"]
          ]),
      RadioChoicePref<String>(
          "iconPack", "Icon pack", ["%s"], sp, "default", iconPackList)
    ]));
  }
}
