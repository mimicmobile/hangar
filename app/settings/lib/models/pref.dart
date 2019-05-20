import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:settings/config.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Pref<T> {
  final String key;
  final String title;
  final List<String> description;
  final T def;
  Map value = {};

  Pref(this.key, this.title, this.description, SharedPreferences sp, this.def) {
    if (T == int) {
      value[key] = sp.getInt(key) ?? this.def;
    } else if (T == String) {
      value[key] = sp.getString(key) ?? this.def;
    }
    print("Preference $key with type $T created");
  }

  String _replaceValue(String s) {
    return s.replaceAll("_value_", value[key].toString());
  }

  Widget rowWidget(context, {Function onTapCallback}) {
    return Padding(padding: const EdgeInsets.all(12));
  }
}

class RadioChoicePref<T> extends Pref<T> {
  final List<T> choices;

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
              Text(_replaceValue(title),
                  style: TextStyle(fontSize: 18, color: Colors.white)),
              Spacer(),
              Text(_getPlural(description, value[key]),
                  style: TextStyle(fontSize: 18, color: Config.accentColor))
            ],
          )),
    );
  }

  Widget _getRadioChild(
      context, T choice, String key, T _value, Function onTapCallback) {
    return RadioListTile(
      activeColor: Config.accentColor,
      title: Text(choice.toString()),
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
              title: Text(_replaceValue(title)),
              children: choices
                  .map((e) => _getRadioChild(
                      context, e, key, value[key], onTapCallback))
                  .toList(),
            ));
  }

  String _getPlural(List<String> description, value) {
    final String def = _replaceValue(description[0]);

    if (value is int && description.length > 1) {
      return Intl.plural(value, other: def, one: _replaceValue(description[1]));
    }
    return def;
  }
}
