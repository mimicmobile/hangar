import 'dart:io';

import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';

class Utils {
  static String _cacheFileName(String path, String packageName) {
    return join(path, '$packageName.png');
  }

  static Future<String> get cachePath async {
    final directory = await getTemporaryDirectory();
    return directory.path;
  }

  static File cachedFileImage(String path, String packageName) {
    return File(_cacheFileName(path, packageName));
  }
}