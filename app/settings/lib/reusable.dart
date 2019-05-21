import 'package:flutter/material.dart';

class Reusable {
  static loadingProgress(orientation) {
    return Padding(
        padding:
        EdgeInsets.only(top: 100.0, right: 20.0, left: 20.0, bottom: 40.0),
        child: Center(child: CircularProgressIndicator()));
  }
  static showSnackBar(BuildContext context, String text,
      {duration: 1400, String actionText, Function actionCallback}) {
    Future.delayed(Duration.zero, () {
      var snackBarAction;
      if (actionText != null && actionCallback != null) {
        snackBarAction = SnackBarAction(
            label: actionText,
            onPressed: () {
              actionCallback();
            });
      }

      var snackBar = SnackBar(
          action: snackBarAction,
          duration: Duration(milliseconds: duration),
          content: Text(text),
          backgroundColor: Theme.of(context).dialogBackgroundColor);
      Scaffold.of(context).showSnackBar(snackBar);
    });
  }

}