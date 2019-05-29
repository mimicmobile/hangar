abstract class IHomeView {
  void onTabTapped(int index);
}

abstract class IAppListWidgetView {
  bool loaded;

  void refreshState(bool shouldRefreshNotification);
}

abstract class IPreferenceWidgetView {
  bool loaded;

  void refreshState(bool shouldRefreshNotification);
  void showSnackBar(String s) {}
}

abstract class IBehaviorWidgetView {}
