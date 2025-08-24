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
      AppListWidget(),
      AppearanceWidget(),
      BehaviorWidget(),
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
            label: 'Apps',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.photo),
            label: 'Appearance',
          ),
          BottomNavigationBarItem(
              icon: Icon(Icons.accessibility),
              label: 'Behavior'
          )
        ],
      ),
      body: IndexedStack(
        index: _presenter.currentIndex,
        children: _presenter.pages,
      ),
    );
  }

  @override
  void onTabTapped(int index) {
    setState(() {
      _presenter.currentIndex = index;
    });
  }
}
