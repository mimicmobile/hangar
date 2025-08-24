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

  Widget rowWidget(context, {required Function onTapCallback}) {
    return Padding(padding: const EdgeInsets.all(12));
  }
}

class MultipleChoicePref<T> extends Pref<T> {
  final List<List<Object>> choices;
  final bool previewIcon;

  MultipleChoicePref(key, title, description, SharedPreferences sp, def,
      this.choices,
      {this.previewIcon = false})
      : super(key, title, description, sp, def);

  Widget rowWidget(context, {required Function onTapCallback}) {
    return InkWell(
      onTap: () => showRadioDialog(context, onTapCallback),
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
      final typedChoices = choices.cast<List<Object>>();
      final String keyVal = value[key] as String;

      final path = typedChoices.singleWhere(
            (l) => l.length > 2 && l[1].toString() == keyVal,
        orElse: () => typedChoices.first,
      )[2];

      widgets.add(Padding(
        padding: EdgeInsets.only(right: 14),
        child: Image.file(Utils.cachedFileImage(path.toString(), null),
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

  Widget _getRadioChild(context, String label, T choice, String? icon,
      String key, T _value, Function onTapCallback) {
    return RadioListTile(
      activeColor: Config.accentColor,
      title: _getMultipleChoiceChildTitle(label, icon),
      groupValue: _value,
      value: choice,
      onChanged: (v) {
        value[key] = v;
        onTapCallback(key, v);
        Navigator.pop(context);
      },
    );
  }

  Widget _getListChild(context, String label, T choice, String? icon, T _value,
      Function onTapCallback) {
    return ListTile(
      title: _getMultipleChoiceChildTitle(label, icon),
      onTap: () {
        onTapCallback('', choice);
        Navigator.pop(context);
      },
    );
  }

  Row _getMultipleChoiceChildTitle(String label, String? icon) {
    List<Widget> widgets = <Widget>[];

    final image = icon != null
        ? Image.file(Utils.cachedFileImage(icon, null), height: 46)
        : SizedBox.square(dimension: 1);
    widgets.add(Padding(
      padding: EdgeInsets.only(right: 14),
      child: image,
    ));

    widgets.add(Flexible(child: Text(label)));

    return Row(children: widgets);
  }

  Future<Widget> showRadioDialog(BuildContext context,
      Function onTapCallback) async {
    return await showDialog(
        context: context,
        builder: (context) =>
            SimpleDialog(
              title: Text(sprintf(title, [value[key]])),
              children: choices
                  .map((e) =>
                  _getRadioChild(
                      context,
                      e[0] as String,
                      e[1] as T,
                      e.length == 3 ? e[2] as String : null,
                      key,
                      value[key],
                      onTapCallback))
                  .toList(),
            ));
  }

  Future<dynamic> showListDialog(BuildContext context,
      Function onTapCallback) async {
    return showDialog(
        context: context,
        builder: (context) =>
            SimpleDialog(
              title: Text(sprintf(title, [value[key]])),
              children: choices
                  .map((e) =>
                  _getListChild(
                      context,
                      e[0] as String,
                      e[1] as T,
                      e.length == 3 ? e[2] as String : null,
                      value[key],
                      onTapCallback))
                  .toList(),
            ));
  }

  String _getPlural() {
    final List<List<Object?>> typedChoices = (choices as List)
        .map<List<Object?>>((e) => (e as List).cast<Object?>())
        .toList();

    final String keyVal = value[key].toString();

    final match = typedChoices.singleWhere((l) => l[1]?.toString() == keyVal,
        orElse: () => typedChoices.first);

    final String label =
    (match.isNotEmpty && match[0] != null) ? match[0]!.toString() : '';

    final String def = sprintf(description[0], [label]);

    if (value[key] is int && description.length > 1) {
      return Intl.plural(value[key],
          other: def, one: sprintf(description[1], [label]));
    }
    return def;
  }
}
