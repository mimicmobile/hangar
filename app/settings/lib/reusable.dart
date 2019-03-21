import 'package:flutter/material.dart';

class Reusable {
  static loadingProgress(orientation) {
    return Padding(
        padding:
        EdgeInsets.only(top: 100.0, right: 20.0, left: 20.0, bottom: 40.0),
        child: Center(child: CircularProgressIndicator()));
  }
}