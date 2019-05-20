import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:settings/config.dart';
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
    } catch(exception) {
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

  RadioChoicePref(
      key, title, description, SharedPreferences sp, def, this.choices)
      : super(key, title, description, sp, def);

  Widget rowWidget(context, {Function onTapCallback}) {
    return InkWell(
      onTap: () => _showRadioDialog(context, onTapCallback),
      child: Padding(
          padding: const EdgeInsets.only(right: 12, left: 12, top: 17, bottom: 17),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.max,
            children: <Widget>[
              Text(sprintf(title, [value[key]]),
                  style: TextStyle(fontSize: 18, color: Colors.white)),
              Spacer(),
              Text(_getPlural(description, value[key]),
                  style: TextStyle(fontSize: 18, color: Config.accentColor))
            ],
          )),
    );
  }

  Widget _getRadioChild(
      context, String label, T choice, String key, T _value, Function onTapCallback) {
    return RadioListTile(
      activeColor: Config.accentColor,
      title: Text(label),
      groupValue: _value,
      value: choice,
      onChanged: (v) {
        value[key] = v;
        onTapCallback(key, v);
        Navigator.pop(context);
      },
    );
  }

  Future<Widget> _showRadioDialog(
      BuildContext context, Function onTapCallback) async {
    return showDialog(
        context: context,
        builder: (context) => SimpleDialog(
              title: Text(sprintf(title, [value[key]])),
              children: choices
                  .map((e) => _getRadioChild(
                      context, e[0], e[1], key, value[key], onTapCallback))
                  .toList(),
            ));
  }

  String _getPlural(List<String> description, v) {
    var label = choices.singleWhere((l) => l[1] == v)[0];
    final String def = sprintf(description[0], [label]);

    if (v is int && description.length > 1) {
      return Intl.plural(v, other: def, one: sprintf(description[1], [label]));
    }
    return def;
  }
}
