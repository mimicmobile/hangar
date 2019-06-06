import 'package:settings/models/pref.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/reusable.dart';
import 'package:settings/utils.dart';

class AppearanceData extends PreferenceData {
  Future refresh() async {
    var sp = await Utils.getSharedPrefs();
    var iconPackList = await Reusable.fetchIconPacks();

    prefSet.add(PrefSet("Notification", [
      MultipleChoicePref<int>(
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
      MultipleChoicePref<int>(
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
      MultipleChoicePref<int>(
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
      MultipleChoicePref<String>(
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
      MultipleChoicePref<String>(
          "pinnedAppPlacement",
          "Pinned app placement",
          ["%s"],
          sp,
          "left",
          [
            ["Left", "left"],
            ["Right", "right"]
          ])
    ]));
    prefSet.add(PrefSet("Icons", [
      MultipleChoicePref<String>(
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
      MultipleChoicePref<String>(
          "iconPack", "Icon pack", ["%s"], sp, "default", iconPackList,
          previewIcon: true)
    ]));
  }
}
