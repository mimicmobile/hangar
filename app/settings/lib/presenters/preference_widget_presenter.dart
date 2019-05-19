import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';
import 'package:settings/models/preference_data.dart';
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
    _view.refreshState();
  }

  @override
  void prefTap(String pref, Object value) {
    if (value != null) {
      Utils.getSharedPrefs().then((sp) {
        if (value is int) {
          sp.setInt(pref, value);
        } else if (value is String) {
          sp.setString(pref, value);
        }
      });
    }
    _view.refreshState();
  }
}
