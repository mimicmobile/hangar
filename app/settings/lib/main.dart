import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:settings/config.dart';
import 'package:settings/widgets/home.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Hangar',
      theme: buildDarkTheme(),
      home: Home(),
    );
  }
}

ThemeData buildDarkTheme() {
  const bg = Color(0xFF141414);
  final scheme = ColorScheme.dark(
    primary: Config.primaryColor,
    secondary: Config.accentColor,
    background: bg,
    surface: Config.darkBgColor,
    onPrimary: Colors.white,
    onSecondary: Colors.white,
    onSurface: Colors.white70,
  );

  return ThemeData(
    useMaterial3: false,
    colorScheme: scheme,
    brightness: Brightness.dark,
    scaffoldBackgroundColor: scheme.background,
    canvasColor: scheme.background,
    cardColor: scheme.surface,
    dividerColor: Config.dividerColor,
    appBarTheme: AppBarTheme(
      backgroundColor: scheme.primary,
      foregroundColor: scheme.onPrimary,
      systemOverlayStyle: SystemUiOverlayStyle.light,
      elevation: 0,
    ),
    bottomNavigationBarTheme: BottomNavigationBarThemeData(
      backgroundColor: scheme.surface,
      selectedItemColor: scheme.secondary,
      unselectedItemColor: scheme.onSurface,
      type: BottomNavigationBarType.fixed,
      showUnselectedLabels: true,
    ),
    snackBarTheme: SnackBarThemeData(
      backgroundColor: scheme.surface,
      contentTextStyle: const TextStyle(color: Colors.white),
      actionTextColor: scheme.secondary,
    ),
    textTheme: ThemeData
        .dark()
        .textTheme
        .apply(
      bodyColor: Colors.white,
      displayColor: Colors.white,
    ),
  );
}
