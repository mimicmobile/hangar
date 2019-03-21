abstract class IHomeView {
  void onTabTapped(int index);
}

abstract class IAppListWidgetView {
  bool loaded;

  void refreshState();
}

abstract class IAppearanceWidgetView {
}

abstract class IBehaviorWidgetView {
}