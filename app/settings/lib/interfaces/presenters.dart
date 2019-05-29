import 'package:settings/models/preference_data.dart';

abstract class IHomePresenter {
  void init() {}
}

abstract class IAppListWidgetPresenter {
  get appData => null;
  void init() {}
  void appTap(String packageName, String key) {}
  refreshApps() {}
  void handleMessage(String s) {}
}

abstract class IPreferenceWidgetPresenter {
  PreferenceData get data => null;
  void loaded() {}
  void init() {}
  void handleMessage(String s) {}
  Future<Null> prefAction(String pref, Object value) async {}
  void prefTap(String pref, Object value) {}
}
