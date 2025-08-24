import 'package:flutter/services.dart';
import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';
import 'package:settings/models/app_data.dart';
import 'package:settings/models/pref.dart';
import 'package:settings/reusable.dart';
import 'package:settings/utils.dart';

class AppListWidgetPresenter implements IAppListWidgetPresenter {
  final IAppListWidgetView _view;
  AppData appData = AppData();
  late String selectedAppPackageName;

  AppListWidgetPresenter(this._view);

  @override
  void init() async {
    refreshApps();

    MethodChannel('hangar/native_channel').setMethodCallHandler((call) async {
      handleMessage(call);
    });
  }

  @override
  void appTap(String packageName, String key) async {
    selectedAppPackageName = packageName;

    switch (key) {
      case "blacklist":
        appData.blackList(packageName, _view.refreshState);
        break;
      case "pin":
        appData.pin(packageName, _view.refreshState);
        break;
      case "change_icon":
        var sp = await Utils.getSharedPrefs();
        var iconPackList = await Reusable.fetchIconPacks(
            packageName: packageName);

        var pref = MultipleChoicePref<String>(
            "iconPack", "Choose from icon pack", ["%s"], sp, "default",
            iconPackList,
            previewIcon: true);
        pref.showListDialog(_view.getContext(), iconPackSelected);
    }
  }

  @override
  Future<Null> refreshApps() {
    return appData.refresh().then((_) {
      print("Total apps: ${appData.apps.length}");
      _view.loaded = true;
      _view.refreshState(false);
    });
  }

  @override
  Future<Object?> handleMessage(MethodCall call) async {
    switch (call.method) {
      case "icon_pack_rebuild":
        refreshApps();
    }
    return null;
  }

  void iconPackSelected(String _, String choice) {
    Reusable.changeIcon(selectedAppPackageName, choice);
  }
}
