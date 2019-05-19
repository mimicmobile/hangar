import 'package:flutter/material.dart';
import 'package:settings/config.dart';
import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';
import 'package:settings/reusable.dart';

class PreferenceWidget extends StatefulWidget {
  const PreferenceWidget({Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => PreferenceWidgetState();
}

class PreferenceWidgetState extends State<PreferenceWidget>
    implements IPreferenceWidgetView {
  IPreferenceWidgetPresenter presenter;
  bool loaded = false;

  @override
  void initState() {
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
          itemCount: presenter.data.prefs.length,
          itemBuilder: (context, index) {
            return presenter.data.rowWidget(context, orientation, index,
                onTapCallback: presenter.prefTap);
          },
          separatorBuilder: (BuildContext context, int index) =>
              Divider(color: Config.lightBgColor, height: 1));
    } else {
      return Reusable.loadingProgress(orientation);
    }
  }
}
