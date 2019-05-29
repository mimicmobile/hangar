import 'package:settings/interfaces/views.dart';
import 'package:settings/models/appearance_data.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/presenters/preference_widget_presenter.dart';
import 'package:settings/reusable.dart';

class AppearanceWidgetPresenter extends PreferenceWidgetPresenter {
  final IPreferenceWidgetView _view;
  PreferenceData data;

  AppearanceWidgetPresenter(this._view) : super(_view);

  @override
  void init() async {
    data = AppearanceData();
    super.init();
  }

  @override
  void handleMessage(String s) {
    switch (s) {
      case "icon_pack_rebuild":
        break;
    }
  }

  @override
  Future<Null> prefAction(String pref, Object value) async {
    if (pref == "iconPack") {
      _view.showSnackBar("Rebuilding icon cache..");
      await Reusable.iconPackRebuild();
    }
  }
}
