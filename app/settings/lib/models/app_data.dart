import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:settings/models/app.dart';
import 'package:settings/utils.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AppData {
  List<App> apps;
  String cachePath;

  Future refresh() async {
    cachePath = await Utils.cachePath;
    print("Refreshing app list...");

    SharedPreferences sharedPreferences = await Utils.getSharedPrefs();
    await sharedPreferences.refreshCache();

    _getAppsFromJson(sharedPreferences.getString("apps") ?? "[]");
  }

  Future save() async {
    Utils.getSharedPrefs().then((SharedPreferences sp) {
      sp.setString("apps", _toJson(apps));
      sp.setBool("forceRefresh", true);
      print("Saved ${apps.length} apps to SharedPrefs");

      _sortApps(apps);
    });
  }

  _getAppsFromJson(String json) {
    List<App> jApps = (jsonDecode(json) as List).map((e) => App.fromJson(e)).toList();
    _sortApps(jApps);
  }

  void _sortApps(List<App> jApps) {
    jApps.sort(
            (a, b) => b.totalTimeInForeground.compareTo(a.totalTimeInForeground));

    // Remove pinned from main list
    List<App> pinned = jApps.where((a) => a.pinned == true).toList();
    jApps.removeWhere((a) => a.pinned == true);

    // Combine lists
    apps = List.of(pinned)..addAll(jApps);
  }

  String _toJson(List<App> apps) {
    return jsonEncode(apps);
  }

  bool emptyList() {
    return apps.isEmpty;
  }

  Widget rowWidget(context, orientation, index, {Function onTapCallback}) {
    return apps[index]
        .rowWidget(context, cachePath, onTapCallback: onTapCallback);
  }

  App findByPackageName(String packageName) =>
      apps.firstWhere((app) => app.packageName == packageName);

  void blackList(String packageName, Function refreshState) {
    refresh().then((_) async {
      var app = findByPackageName(packageName);
      app.blacklisted = !app.blacklisted;
      await save();
      refreshState(true);
    });
  }

  void pin(String packageName, Function refreshState) {
    refresh().then((_) async {
      var app = findByPackageName(packageName);
      app.pinned = !app.pinned;
      await save();
      refreshState(true);
    });
  }
}
