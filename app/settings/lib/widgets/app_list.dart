import 'package:flutter/material.dart';
import 'package:settings/config.dart';
import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';
import 'package:settings/presenters/app_list_widget_presenter.dart';
import 'package:settings/reusable.dart';

class AppListWidget extends StatefulWidget {
  const AppListWidget({Key key})
      : super(key: key);

  @override
  State<StatefulWidget> createState() => _AppListWidgetState();
}

class _AppListWidgetState extends State<AppListWidget>
    with WidgetsBindingObserver implements IAppListWidgetView {
  IAppListWidgetPresenter _presenter;
  bool loaded = false;
  BuildContext _buildContext;

  @override
  void initState() {
    super.initState();

    _presenter = AppListWidgetPresenter(this);
    _presenter.init();

    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state.index == 0) {
      _presenter.refreshApps();
    }
  }

  @override
  void refreshState(bool shouldRefreshNotification) {
    setState(() {});

    if (shouldRefreshNotification) {
      Reusable.refreshNotification();
    }
  }

  @override
  Widget build(BuildContext context) {
    _buildContext = context;

    return OrientationBuilder(builder: (context, orientation) {
      return Container(
          color: Config.darkBgColor,
          child: Stack(children: <Widget>[_appList(context, orientation)]));
    });
  }

  _appList(context, orientation) {
    if (loaded) {
      return RefreshIndicator(
          child: SizedBox.expand(
            child: _presenter.appData.emptyList()
                ? Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: <Widget>[
                      Padding(
                        padding: const EdgeInsets.only(
                            top: 8.0, left: 8.0, bottom: 20.0, right: 8.0),
                        child: Text("App list is being built..",
                            style: TextStyle(
                                fontSize: 20.0, color: Colors.grey[300])),
                      ),
                      MaterialButton(
                          color: Config.accentColor,
                          elevation: 2,
                          onPressed: _presenter.refreshApps,
                          child: Text("Refresh",
                              style: TextStyle(
                                  fontSize: 20.0, color: Colors.white))),
                    ],
                  )
                : ListView.separated(
                    physics: const AlwaysScrollableScrollPhysics(),
                    itemCount: _presenter.appData.apps.length,
                    itemBuilder: (context, index) {
                      return _presenter.appData.rowWidget(
                          context, orientation, index,
                          onTapCallback: _presenter.appTap);
                    },
                    separatorBuilder: (BuildContext context, int index) =>
                        Divider(color: Config.lightBgColor, height: 1)),
          ),
          onRefresh: _presenter.refreshApps);
    } else {
      return Reusable.loadingProgress(orientation);
    }
  }

  @override
  BuildContext getContext() {
    return _buildContext;
  }
}
