import 'package:flutter/material.dart';

abstract class IHomeView {
  void onTabTapped(int index);
}

abstract class IAppListWidgetView {
  bool loaded = false;

  void refreshState(bool shouldRefreshNotification);
  BuildContext getContext();
}

abstract class IPreferenceWidgetView {
  bool loaded = false;

  void refreshState(bool shouldRefreshNotification);
}

abstract class IBehaviorWidgetView {}
