import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:settings/models/app_data.dart';
import 'package:settings/utils.dart';

class Reusable {
  static loadingProgress(orientation) {
    return Padding(
        padding:
            EdgeInsets.only(top: 100.0, right: 20.0, left: 20.0, bottom: 40.0),
        child: Center(child: CircularProgressIndicator()));
  }

  static showSnackBar(BuildContext context, String text,
      {duration: 1400, String actionText, Function actionCallback}) {
    Future.delayed(Duration.zero, () {
      var snackBarAction;
      if (actionText != null && actionCallback != null) {
        snackBarAction = SnackBarAction(
            label: actionText,
            onPressed: () {
              actionCallback();
            });
      }

      var snackBar = SnackBar(
          action: snackBarAction,
          duration: Duration(milliseconds: duration),
          content: Text(text),
          backgroundColor: Theme.of(context).dialogBackgroundColor);
      Scaffold.of(context).showSnackBar(snackBar);
    });
  }

  static refreshNotification() {
    return Utils.getSharedPrefs().then((sp) async {
      sp.setBool("forceRefresh", true);

      MethodChannel('hangar/native_channel')
          .invokeMethod('refresh_notification');
    });
  }

  static Future<List<List<String>>> fetchIconPacks(
      {packageName = "ca.mimic.hangar"}) async {
    var s = await MethodChannel('hangar/native_channel').invokeMethod(
        'icon_pack_list', <String, String>{"packageName": packageName});
    return AppData()
        .getThemesFromJson(s)
        .expand((e) => [
              [e.name, e.packageName, e.cachedFile]
            ])
        .toList();
  }

  static Future<Null> iconPackRebuild() async {
    await MethodChannel('hangar/native_channel')
        .invokeMethod('icon_pack_rebuild');
  }

  static Future<Null> changeIcon(String packageName, String iconPack) async {
    await MethodChannel('hangar/native_channel').invokeMethod(
        'change_icon', <String, String>{
      "packageName": packageName,
      "launchPackageName": iconPack
    });
  }
}
