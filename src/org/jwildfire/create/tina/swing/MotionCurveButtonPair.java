package org.jwildfire.create.tina.swing;

import java.awt.Rectangle;

import javax.swing.JButton;

public class MotionCurveButtonPair implements MotionCurveEditor {
  private final JButton mainButton;
  private final JButton curveButton;
  Rectangle initialMainBounds;
  Rectangle mainWithCurveBounds;
  private boolean withMotionCurve;
  private static final int BORDER = 0;

  public MotionCurveButtonPair(JButton pMainButton, JButton pCurveButton) {
    curveButton = pCurveButton;
    mainButton = pMainButton;
    Rectangle initialCurveBounds = curveButton.getBounds();
    initialMainBounds = mainButton.getBounds();
    int off = initialCurveBounds.width + BORDER;
    mainWithCurveBounds = new Rectangle(initialMainBounds.x + off, initialMainBounds.y, initialMainBounds.width - off, initialMainBounds.height);
  }

  @Override
  public boolean isWithMotionCurve() {
    return withMotionCurve;
  }

  @Override
  public void setWithMotionCurve(boolean pWithMotionCurve) {
    withMotionCurve = pWithMotionCurve;
    if (withMotionCurve) {
      curveButton.setVisible(true);
      mainButton.setBounds(mainWithCurveBounds);
    }
    else {
      curveButton.setVisible(false);
      mainButton.setBounds(initialMainBounds);
    }
  }
}
