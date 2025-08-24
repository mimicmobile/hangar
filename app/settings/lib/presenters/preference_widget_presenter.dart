import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/reusable.dart';
import 'package:settings/utils.dart';

class PreferenceWidgetPresenter implements IPreferenceWidgetPresenter {
  final IPreferenceWidgetView _view;
  late PreferenceData data;

  PreferenceWidgetPresenter(this._view);

  @override
  void init() async {
    data.refresh().then((_) {
      loaded();
    });
  }

  @override
  void loaded() {
    _view.loaded = true;
    _view.refreshState(false);
  }

  @override
  void prefTap(String pref, Object value) {
    Utils.getSharedPrefs().then((sp) async {
      var notificationRefresh = true;

      if (value is int) {
        sp.setInt(pref, value);
      } else if (value is String) {
        sp.setString(pref, value);
      }

      if (pref == "iconPack") {
        notificationRefresh = false;
        Reusable.iconPackRebuild();
      }

      _view.refreshState(notificationRefresh);
    });
    }
}
