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
  }

  void appTap(String packageName, String key) {
    switch (key) {
      case "blacklist":
        appData.blackList(packageName, _view.refreshState);
        break;
      case "pin":
        appData.pin(packageName, _view.refreshState);
    }
  }

  Future<Null> refreshApps() {
    return appData.refresh().then((_) {
      print("Total apps: ${appData.apps.length}");
      _view.loaded = true;
      _view.refreshState();
    });
  }
}