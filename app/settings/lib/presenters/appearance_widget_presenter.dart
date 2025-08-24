import 'package:settings/interfaces/views.dart';
import 'package:settings/models/appearance_data.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/presenters/preference_widget_presenter.dart';

class AppearanceWidgetPresenter extends PreferenceWidgetPresenter {
  final IPreferenceWidgetView _view;
  PreferenceData data = AppearanceData();

  AppearanceWidgetPresenter(this._view) : super(_view);

  @override
  void init() async {
    super.init();
  }
}
