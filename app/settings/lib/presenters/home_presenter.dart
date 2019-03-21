import 'package:flutter/material.dart';
import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';

class HomePresenter implements IHomePresenter {
  IHomeView _view;

  final appListKey = PageStorageKey('appListKey');
  final appearanceKey = PageStorageKey('appearanceKey');
  final behaviorKey = PageStorageKey('behaviorKey');

  HomePresenter(this._view);
  PageStorageBucket bucket = PageStorageBucket();
  int currentIndex = 0;

  List<Widget> pages;

  @override
  void init() {
  }
}