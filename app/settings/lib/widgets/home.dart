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

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    _presenter.pages = [
      AppListWidget(key: _presenter.appListKey),
      AppearanceWidget(),
      BehaviorWidget(),
    ];

    return MaterialApp(
        home: DefaultTabController(
            length: 3,
            child: Scaffold(
              appBar: AppBar(
                  backgroundColor: Config.primaryColor,
                  title: Text('Hangar Settings')),
              bottomNavigationBar: Theme(
                  data: Theme.of(context).copyWith(
                      canvasColor: Config.bottomNavBarColor,
                      primaryColor: Config.accentColor,
                      textTheme: Theme.of(context).textTheme.copyWith(
                          caption: TextStyle(color: Colors.grey[500]))),
                  // sets the inactive color of the `BottomNavigationBar`
                  child: BottomNavigationBar(
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
                          icon: Icon(Icons.accessibility),
                          title: Text('Behavior'))
                    ],
                  )),
              body: Builder(builder: (BuildContext context) {
                return PageStorage(
                  child: _presenter.pages[_presenter.currentIndex],
                  bucket: _presenter.bucket,
                );
              }),
            )));
  }

  @override
  void onTabTapped(int index) {
    setState(() {
      _presenter.currentIndex = index;
    });
  }
}
