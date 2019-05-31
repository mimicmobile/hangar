import 'package:settings/interfaces/views.dart';
import 'package:settings/models/behavior_data.dart';
import 'package:settings/models/preference_data.dart';
import 'package:settings/presenters/preference_widget_presenter.dart';

class BehaviorWidgetPresenter extends PreferenceWidgetPresenter {
  final IPreferenceWidgetView _view;
  PreferenceData data;

  BehaviorWidgetPresenter(this._view) : super(_view);

  @override
  void init() async {
    data = BehaviorData();
    super.init();
  }
}
