/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2020 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
  General Public License as published by the Free Software Foundation; either version 2.1 of the
  License, or (at your option) any later version.

  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software;
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
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
