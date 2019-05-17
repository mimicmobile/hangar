import 'package:flutter/material.dart';
import 'package:settings/config.dart';
import 'package:settings/utils.dart';

class App {
  final String name;
  final String packageName;
  final bool cachedIcon;
  final bool isSystemApp;

  // UsageStats
  final int totalTimeInForeground;
  final int lastTimeUsed;

  // Variable
  final int lastUpdated;
  final int timesLaunched;
  final int timesUpdated;
  final double sortScore;
  bool pinned;
  bool blacklisted;

  App(
      this.name,
      this.packageName,
      this.cachedIcon,
      this.isSystemApp,
      this.totalTimeInForeground,
      this.lastTimeUsed,
      this.lastUpdated,
      this.timesLaunched,
      this.timesUpdated,
      this.sortScore,
      this.pinned,
      this.blacklisted);

  App.fromJson(Map<String, dynamic> json)
      : name = json['name'],
        packageName = json['packageName'],
        cachedIcon = json['cachedIcon'],
        isSystemApp = json['isSystemApp'],
        totalTimeInForeground = json['totalTimeInForeground'],
        lastTimeUsed = json['lastTimeUsed'],
        lastUpdated = json['lastUpdated'],
        timesLaunched = json['timesLaunched'],
        timesUpdated = json['timesUpdated'],
        sortScore = json['sortScore'],
        pinned = json['pinned'] ?? false,
        blacklisted = json['blacklisted'] ?? false;

  get totalTimeReadable {
    var duration = Duration(seconds: (totalTimeInForeground / 1000).round());
    var time = duration.inHours > 0 ? "${duration.inHours}h " : "";
    return time += "${duration.inMinutes - (duration.inHours * 60)}m";
  }

  Widget rowWidget(context, cachePath, {Function onTapCallback}) {
    return Padding(
      padding: const EdgeInsets.all(8),
      child: Theme(
        data: Theme.of(context).copyWith(cardColor: Config.bottomNavBarColor),
        child: PopupMenuButton(
          offset: Offset(1, 0),
          onSelected: (s) => onTapCallback(packageName, s),
          itemBuilder: (_) => <PopupMenuItem<String>>[
                PopupMenuItem<String>(
                    value: "blacklist",
                    child: Text(
                        blacklisted
                            ? "Remove app from blacklist"
                            : "Blacklist app",
                        style: TextStyle(
                            fontSize: 12.0, color: Colors.grey[200]))),
                PopupMenuItem<String>(
                    value: "pin",
                    child: Text(pinned ? "Remove pin from app" : "Pin app",
                        style: TextStyle(
                            fontSize: 12.0, color: Colors.grey[200]))),
              ],
          child: Row(
            children: <Widget>[
              Text('$name', style: nameTextStyle()),
              Spacer(),
              Text(totalTimeReadable,
                  style: TextStyle(fontSize: 12, color: Colors.white)),
              Padding(
                  padding: const EdgeInsets.only(
                      top: 2, bottom: 2, right: 6, left: 12),
                  child: Stack(
                    children: <Widget>[
                      Image.file(
                          Utils.cachedFileImage(cachePath, '$packageName'),
                          height: 46),
                      Image.asset('images/pin_icon.png',
                          color: pinned ? Colors.white : Colors.transparent,
                          height: 26),
                    ],
                    alignment: Alignment(1.4, -1.4),
                  ))
            ],
          ),
        ),
      ),
    );
  }

  TextStyle nameTextStyle() {
    var decorations = <TextDecoration>[];
    if (blacklisted) decorations.add(TextDecoration.lineThrough);

    return TextStyle(
        fontSize: 20.0,
        color: blacklisted ? Colors.grey[600] : Colors.white,
        decoration:
            TextDecoration.combine(<TextDecoration>[]..addAll(decorations)));
  }

  Map<String, dynamic> toJson() => {
        'name': name,
        'packageName': packageName,
        'cachedIcon': cachedIcon,
        'isSystemApp': isSystemApp,
        'totalTimeInForeground': totalTimeInForeground,
        'lastTimeUsed': lastTimeUsed,
        'lastUpdated': lastUpdated,
        'timesLaunched': timesLaunched,
        'timesUpdated': timesUpdated,
        'sortScore': sortScore,
        'pinned': pinned,
        'blacklisted': blacklisted,
      };
}
