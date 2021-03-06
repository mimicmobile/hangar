import 'package:flutter/material.dart';
import 'package:settings/config.dart';
import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/reusable.dart';

class PreferenceWidget extends StatefulWidget {
  const PreferenceWidget({Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => PreferenceWidgetState();
}

class PreferenceWidgetState<T extends PreferenceWidget> extends State<T>
    implements IPreferenceWidgetView {
  IPreferenceWidgetPresenter presenter;
  bool loaded = false;

  @override
  void initState() {
    super.initState();
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
    return OrientationBuilder(builder: (context, orientation) {
      return Container(child: _cardHolder(context, orientation));
    });
  }

  _cardHolder(context, orientation) {
    if (loaded) {
      return ListView(children: _cardChildren(context, orientation));
    } else {
      return Container();
    }
  }

  _cardChildren(context, orientation) {
    List<Widget> widgets = <Widget>[];
    for (PrefSet prefSet in presenter.data.prefSet) {
      widgets.add(Card(
          shape: BeveledRectangleBorder(),
          margin: EdgeInsets.only(top: 0, bottom: 20, right: 0, left: 0),
          child: Stack(children: [
            Container(
                padding:
                    EdgeInsets.only(top: 16, bottom: 16, left: 12, right: 12),
                child: Text(prefSet.title,
                    style: TextStyle(
                        color: Config.accentColor,
                        fontSize: 16,
                        fontWeight: FontWeight.w600))),
            ListView.separated(
                padding: EdgeInsets.only(left: 0, right: 0, bottom: 0, top: 38),
                physics: NeverScrollableScrollPhysics(),
                shrinkWrap: true,
                itemCount: prefSet.prefs.length,
                itemBuilder: (context, index) {
                  return prefSet.rowWidget(context, orientation, index,
                      onTapCallback: presenter.prefTap);
                },
                separatorBuilder: (BuildContext context, int index) =>
                    Divider(color: Config.lightBgColor, height: 1, indent: 12))
          ])));
    }
    return widgets;
  }
}
