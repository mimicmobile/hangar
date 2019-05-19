abstract class IHomeView {
  void onTabTapped(int index);
}

abstract class IAppListWidgetView {
  bool loaded;

  void refreshState();
}

abstract class IPreferenceWidgetView {
  bool loaded;

  void refreshState();
}

abstract class IBehaviorWidgetView {}
