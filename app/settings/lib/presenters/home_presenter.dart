import 'package:flutter/material.dart';
import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';

class HomePresenter implements IHomePresenter {
  IHomeView _view;

  HomePresenter(this._view);
  int currentIndex = 0;

  late List<Widget> pages;

  @override
  void init() {
  }
}