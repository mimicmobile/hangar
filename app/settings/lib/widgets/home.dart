import 'package:settings/config.dart';
import 'package:settings/interfaces/views.dart';
import 'package:flutter/material.dart';
import 'package:settings/presenters/home_presenter.dart';
import 'package:settings/widgets/app_list.dart';
import 'package:settings/widgets/appearance.dart';
import 'package:settings/widgets/behavior.dart';

class Home extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _HomeState();
  }
}

class _HomeState extends State<Home> implements IHomeView {
  HomePresenter _presenter;

  @override
  void initState() {
    _presenter = HomePresenter(this);
    _presenter.init();

    _presenter.pages = [
      AppListWidget(key: _presenter.appListKey),
      AppearanceWidget(key: _presenter.appearanceKey),
      BehaviorWidget(key: _presenter.behaviorKey),
    ];

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Hangar')),
      bottomNavigationBar: BottomNavigationBar(
        onTap: onTabTapped,
        currentIndex: _presenter.currentIndex,
        items: [
          BottomNavigationBarItem(
            icon: Icon(Icons.list),
            title: Text('Apps'),
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.photo),
            title: Text('Appearance'),
          ),
          BottomNavigationBarItem(
              icon: Icon(Icons.accessibility), title: Text('Behavior'))
        ],
      ),
      body: Builder(builder: (BuildContext context) {
        return PageStorage(
          child: _presenter.pages[_presenter.currentIndex],
          bucket: _presenter.bucket,
        );
      }),
    );
  }

  @override
  void onTabTapped(int index) {
    setState(() {
      _presenter.currentIndex = index;
    });
  }
}
