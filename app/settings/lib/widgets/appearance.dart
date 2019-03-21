import 'package:flutter/material.dart';
import 'package:settings/config.dart';

class AppearanceWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => AppearanceWidgetState();
}

class AppearanceWidgetState extends State<AppearanceWidget> {
  @override
  Widget build(BuildContext context) {
    return Container(color: Config.backgroundColor);
  }
}
