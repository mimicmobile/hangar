import 'package:flutter/material.dart';
import 'package:settings/config.dart';
import 'package:settings/widgets/home.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Hangar',
      theme: ThemeData(
          brightness: Brightness.dark,
          primaryColor: Config.primaryColor,
          accentColor: Config.accentColor,
          dividerColor: Config.dividerColor,
          backgroundColor: Config.backgroundColor),
      home: Home(),
    );
  }
}
