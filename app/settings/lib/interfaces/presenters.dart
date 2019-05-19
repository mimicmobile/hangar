abstract class IHomePresenter {
  void init() {}
}

abstract class IAppListWidgetPresenter {
  get appData => null;
  void init() {}
  void appTap(String packageName, String key) {}
  refreshApps() {}
}

abstract class IPreferenceWidgetPresenter {
  get data => null;
  void loaded() {}
  void init() {}
  void prefTap(String pref, Object value) {}
}
