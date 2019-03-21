import 'package:flutter/material.dart';
import 'package:settings/config.dart';

class BehaviorWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => BehaviorWidgetState();
}

class BehaviorWidgetState extends State<BehaviorWidget> {
  @override
  Widget build(BuildContext context) {
    return Container(color: Config.backgroundColor);
  }
}
