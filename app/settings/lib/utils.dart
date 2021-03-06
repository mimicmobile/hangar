import 'dart:io';

import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Utils {
  static String _cacheFileName(String path, String packageName) {
    return join(path, '$packageName.png');
  }

  static Future<String> get cachePath async {
    final directory = await getTemporaryDirectory();
    return directory.path;
  }

  static File cachedFileImage(String path, String packageName) {
    return File(packageName != null ? _cacheFileName(path, packageName) : path);
  }

  static Future<SharedPreferences> getSharedPrefs() async {
    return await SharedPreferences.getInstance();
  }
}