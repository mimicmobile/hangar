import 'package:flutter/services.dart';
import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';
import 'package:settings/models/app_data.dart';

class AppListWidgetPresenter implements IAppListWidgetPresenter {
  final IAppListWidgetView _view;
  AppData appData;

  AppListWidgetPresenter(this._view);

  @override
  void init() async {
    appData = AppData();
    refreshApps();

    BasicMessageChannel('hangar/native_channel', StringCodec())
        .setMessageHandler((s) {
      handleMessage(s);
    });
  }

  @override
  void appTap(String packageName, String key) {
    switch (key) {
      case "blacklist":
        appData.blackList(packageName, _view.refreshState);
        break;
      case "pin":
        appData.pin(packageName, _view.refreshState);
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
  void handleMessage(String s) {
    switch (s) {
      case "icon_pack_rebuild":
        // Look into state destruction bug
//        refreshApps();
    }
  }
}
