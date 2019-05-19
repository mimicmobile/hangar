import 'package:flutter/material.dart';
import 'package:settings/presenters/appearance_widget_presenter.dart';
import 'package:settings/widgets/preference.dart';

class AppearanceWidget extends PreferenceWidget {
  const AppearanceWidget({Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _AppearanceWidgetState();
}

class _AppearanceWidgetState extends PreferenceWidgetState {
  @override
  void initState() {
    presenter = AppearanceWidgetPresenter(this);
    presenter.init();

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return super.build(context);
  }
}
