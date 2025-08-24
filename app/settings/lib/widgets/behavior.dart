import 'package:flutter/material.dart';
import 'package:settings/presenters/behavior_widget_presenter.dart';
import 'package:settings/widgets/preference.dart';

class BehaviorWidget extends PreferenceWidget {
  const BehaviorWidget({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _BehaviorWidgetState();
}

class _BehaviorWidgetState extends PreferenceWidgetState<BehaviorWidget> {
  @override
  void initState() {
    super.initState();

    presenter = BehaviorWidgetPresenter(this);
    presenter.init();
  }

  @override
  Widget build(BuildContext context) {
    return super.build(context);
  }
}

