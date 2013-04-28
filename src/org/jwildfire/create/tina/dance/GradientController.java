package org.jwildfire.create.tina.dance;

import javax.swing.JPanel;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.ErrorHandler;

public class GradientController {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;

  public GradientController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    enableControls();
    initGradientsLibrary();
  }

  private void initGradientsLibrary() {
    // TODO Auto-generated method stub

  }

  public void enableControls() {
    // TODO Auto-generated method stub

  }

}
