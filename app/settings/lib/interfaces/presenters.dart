abstract class IHomePresenter {
  void init() {}
}

abstract class IAppListWidgetPresenter {
  get appData => null;
  void init() {}
  void appTap(String packageName, String key) {}
  refreshApps() {}
}

abstract class IAppearanceWidgetPresenter {
}

abstract class IBehaviorWidgetPresenter {
}
