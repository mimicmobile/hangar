abstract class IHomeView {
  void onTabTapped(int index);
}

abstract class IAppListWidgetView {
  bool loaded;

  void refreshState(bool shouldShow);
}

abstract class IPreferenceWidgetView {
  bool loaded;

  void refreshState(bool shouldShow);
}

abstract class IBehaviorWidgetView {}
