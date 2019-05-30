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
          scaffoldBackgroundColor: Color(0xFF141414),
          primaryColor: Config.primaryColor,
          accentColor: Config.accentColor,
          dividerColor: Config.dividerColor,
          cardColor: Config.darkBgColor,
          backgroundColor: Config.darkBgColor,
          appBarTheme: AppBarTheme(color: Config.primaryColor),
          textTheme: TextTheme(caption: TextStyle(color: Colors.grey[500]))),
      home: Home(),
    );
  }
}
