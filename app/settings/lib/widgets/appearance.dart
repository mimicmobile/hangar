import 'package:flutter/material.dart';
import 'package:settings/config.dart';
import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';
import 'package:settings/presenters/appearance_widget_presenter.dart';
import 'package:settings/reusable.dart';

class AppearanceWidget extends StatefulWidget {
  const AppearanceWidget({Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _AppearanceWidgetState();
}

class _AppearanceWidgetState extends State<AppearanceWidget>
    implements IAppearanceWidgetView {
  IAppearanceWidgetPresenter _presenter;
  bool loaded = false;

  @override
  void initState() {
    _presenter = AppearanceWidgetPresenter(this);
    _presenter.init();

    super.initState();
  }

  @override
  void refreshState() {
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return OrientationBuilder(builder: (context, orientation) {
      return Container(
          color: Config.backgroundColor,
          child: Stack(children: <Widget>[_prefList(context, orientation)]));
    });
  }

  _prefList(context, orientation) {
    if (loaded) {
      return ListView.separated(
          physics: const AlwaysScrollableScrollPhysics(),
          itemCount: _presenter.appearanceData.prefs.length,
          itemBuilder: (context, index) {
            return _presenter.appearanceData.rowWidget(context, orientation, index,
                onTapCallback: _presenter.prefTap);
          },
          separatorBuilder: (BuildContext context, int index) =>
              Divider(color: Config.lightBgColor, height: 1));
    } else {
      return Reusable.loadingProgress(orientation);
    }
  }
}
