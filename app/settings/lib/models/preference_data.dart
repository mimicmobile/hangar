import 'package:flutter/material.dart';
import 'package:settings/models/pref.dart';

class PreferenceData {
  List<PrefSet> prefSet = <PrefSet>[];

  PreferenceData();

  Future refresh() async {}
}

class PrefSet {
  String title;
  List<Pref> prefs = <Pref>[];

  PrefSet(this.title, this.prefs);

  Widget rowWidget(context, orientation, index, {required Function onTapCallback}) {
    return prefs[index].rowWidget(context, onTapCallback: onTapCallback);
  }
}