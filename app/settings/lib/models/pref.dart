import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:settings/config.dart';
import 'package:settings/utils.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sprintf/sprintf.dart';

class Pref<T> {
  final String key;
  final String title;
  final List<String> description;
  final T def;
  Map value = {};

  Pref(this.key, this.title, this.description, SharedPreferences sp, this.def) {
    try {
      if (T == int) {
        value[key] = sp.getInt(key) ?? this.def;
      } else if (T == String) {
        value[key] = sp.getString(key) ?? this.def;
      }
      print("Preference $key with type $T created");
    } catch (exception) {
      sp.remove(key);
      Pref(key, title, description, sp, def);
    }
  }

  Widget rowWidget(context, {Function onTapCallback}) {
    return Padding(padding: const EdgeInsets.all(12));
  }
}

class RadioChoicePref<T> extends Pref<T> {
  final List<List<Object>> choices;
  final bool previewIcon;

  RadioChoicePref(key, title, description, SharedPreferences sp, def,
      this.choices, {this.previewIcon = false})
      : super(key, title, description, sp, def);

  Widget rowWidget(context, {Function onTapCallback}) {
    return InkWell(
      onTap: () => _showRadioDialog(context, onTapCallback),
      child: Padding(
          padding:
          const EdgeInsets.only(right: 12, left: 12, top: 17, bottom: 17),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.start,
            mainAxisSize: MainAxisSize.max,
            children: _getRowWidgetTitle(),
          )),
    );
  }

  List<Widget> _getRowWidgetTitle() {
    var widgets = <Widget>[];

    if (this.previewIcon) {
      var path = choices.singleWhere((l) => l[1] == value[key])[2];

      widgets.add(Padding(
        padding: EdgeInsets.only(right: 14),
        child: Image.file(
            Utils.cachedFileImage(path, null),
            height: 46),
      ));
    }

    widgets.addAll([
        Text(sprintf(title, [value[key]]),
            style: TextStyle(fontSize: 18, color: Colors.white)),
        Spacer(),
        Text(_getPlural(),
            style: TextStyle(fontSize: 18, color: Config.accentColor))
    ]);

    return widgets;
  }

  Widget _getRadioChild(context, String label, T choice, String icon,
      String key, T _value,
      Function onTapCallback) {
    return RadioListTile(
      activeColor: Config.accentColor,
      title: _getRadioChildTitle(label, icon),
      groupValue: _value,
      value: choice,
      onChanged: (v) {
        value[key] = v;
        onTapCallback(key, v);
        Navigator.pop(context);
      },
    );
  }

  Row _getRadioChildTitle(String label, String icon) {
    List<Widget> widgets = <Widget>[];

    if (icon != null) {
      widgets.add(Padding(
        padding: EdgeInsets.only(right: 14),
        child: Image.file(
          Utils.cachedFileImage(icon, null),
          height: 46),
      ));
    }

    widgets.add(Flexible(child: Text(label)));

    return Row(children: widgets);
  }

  Future<Widget> _showRadioDialog(BuildContext context,
      Function onTapCallback) async {
    return showDialog(
        context: context,
        builder: (context) =>
            SimpleDialog(
              title: Text(sprintf(title, [value[key]])),
              children: choices
                  .map((e) =>
                  _getRadioChild(
                      context,
                      e[0],
                      e[1],
                      e.length == 3 ? e[2] : null,
                      key,
                      value[key],
                      onTapCallback))
                  .toList(),
            ));
  }

  String _getPlural() {
    var label = choices.singleWhere((l) => l[1] == value[key])[0];
    final String def = sprintf(description[0], [label]);

    if (value[key] is int && description.length > 1) {
      return Intl.plural(value[key], other: def, one: sprintf(description[1], [label]));
    }
    return def;
  }
}
