import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';
import 'package:settings/models/appearance_data.dart';
import 'package:settings/utils.dart';

class AppearanceWidgetPresenter implements IAppearanceWidgetPresenter {
  final IAppearanceWidgetView _view;
  AppearanceData appearanceData;

  AppearanceWidgetPresenter(this._view);

  @override
  void init() async {
    appearanceData = AppearanceData();
    appearanceData.refresh().then((_) {
      _view.loaded = true;
      _view.refreshState();
    });
  }

  @override
  void prefTap(String pref, Object value) {
    if (value is int && value != null) {
      Utils.getSharedPrefs().then((sp) => sp.setInt(pref, value));
    }
    _view.refreshState();
  }
}
