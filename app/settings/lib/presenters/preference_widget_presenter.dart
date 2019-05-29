import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/reusable.dart';
import 'package:settings/utils.dart';

class PreferenceWidgetPresenter implements IPreferenceWidgetPresenter {
  final IPreferenceWidgetView _view;
  PreferenceData data;

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
    if (value != null) {
      Utils.getSharedPrefs().then((sp) async {
        if (value is int) {
          sp.setInt(pref, value);
        } else if (value is String) {
          sp.setString(pref, value);
        }
        await prefAction(pref, value);
      });
    }
    _view.refreshState(true);
  }

  @override
  Future<Null> prefAction(String pref, Object value) async {}

  @override
  void handleMessage(String s) {}
}
