import 'package:settings/interfaces/presenters.dart';
import 'package:settings/interfaces/views.dart';

class BehaviorWidgetPresenter implements IBehaviorWidgetPresenter {
  final IBehaviorWidgetView _view;

  BehaviorWidgetPresenter(this._view);
}
