import 'package:flutter/material.dart';
import 'package:settings/config.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Pref<T> {
  final String key;
  final String title;
  final String description;
  final T def;
  Map value = {};

  Pref(this.key, this.title, this.description, SharedPreferences sp, this.def) {
    if (T == int) {
      value[key] = sp.getInt(key) ?? this.def;
      print("Preference $key with type $T created");
    }
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
    return GestureDetector(
      onTap: () => _showRadioDialog(context, onTapCallback),
      behavior: HitTestBehavior.translucent,
      child: Padding(
          padding: const EdgeInsets.all(12),
          child: Theme(
              data: Theme.of(context)
                  .copyWith(cardColor: Config.bottomNavBarColor),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.max,
                children: <Widget>[
                  Text(_replaceValue(title),
                      style: TextStyle(fontSize: 20, color: Colors.white)),
                  Divider(height: 6, color: Colors.transparent),
                  Text(_replaceValue(description),
                      style: TextStyle(fontSize: 16, color: Colors.grey[300]))
                ],
              ))),
    );
  }

  Widget _getRadioChild(
      context, T choice, String key, T _value, Function onTapCallback) {
    return RadioListTile(
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
}
