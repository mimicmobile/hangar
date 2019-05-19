import 'package:flutter/material.dart';
import 'package:settings/models/pref.dart';

class PreferenceData {
  List<Pref> prefs = <Pref>[];

  PreferenceData();

  Future refresh() async {}

  Widget rowWidget(context, orientation, index, {Function onTapCallback}) {
    return prefs[index].rowWidget(context, onTapCallback: onTapCallback);
  }
}