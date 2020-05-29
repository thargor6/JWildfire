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
package org.jwildfire.create.tina.swing.flamepanel;

import java.awt.Graphics2D;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.swing.FrameControlsUtil;
import org.jwildfire.create.tina.swing.MouseDragOperation;

public class CrosshairControlHandler extends AbstractControlHandler<TriangleControlShape> {

  public CrosshairControlHandler(FrameControlsUtil pFrameControlsUtil, Prefs pPrefs, FlamePanelConfig pConfig) {
    super(pFrameControlsUtil, pPrefs, pConfig);
  }

  @Override
  public void drawXForm(Graphics2D g, XForm pXForm, int pIndex, int pXFormCount, boolean pIsFinal, boolean pShadow, boolean pIsSelected) {
    if (!pShadow) {
      if (config.isWithColoredTransforms()) {
        int row = pIsFinal ? pXFormCount + pIndex : pIndex;
        int colorIdx = row % FlamePanelConfig.XFORM_COLORS.length;
        g.setColor(XFORM_COLORS[colorIdx]);
      }
      else {
        g.setColor(pIsFinal ? XFORM_POST_COLOR : XFORM_COLOR);
      }
    }

    TriangleControlShape triangle = convertXFormToShape(pXForm);
    if (pShadow) {
      for (int i = 0; i < triangle.viewX.length; i++) {
        triangle.viewX[i] += SHADOW_DIST;
        triangle.viewY[i] += SHADOW_DIST;
      }
    }
    g.setStroke(pIsSelected ? SELECTED_LINE_NEW : NORMAL_LINE_NEW);
    // selected point
    if (pIsSelected) {
      if ((config.getMouseDragOperation() == MouseDragOperation.POINTS)) {
        int radius = 10;
        g.fillOval(triangle.viewX[config.getSelectedPoint()] - radius / 2, triangle.viewY[config.getSelectedPoint()] - radius / 2, radius, radius);
      }
      int dx, dy;
      dx = triangle.viewX[0] - triangle.viewX[1];
      dy = triangle.viewY[0] - triangle.viewY[1];
      g.drawLine(triangle.viewX[1], triangle.viewY[1], triangle.viewX[1] + dx, triangle.viewY[1] + dy);
      g.drawLine(triangle.viewX[1], triangle.viewY[1], triangle.viewX[1] - dx, triangle.viewY[1] - dy);
      dx = triangle.viewX[2] - triangle.viewX[1];
      dy = triangle.viewY[2] - triangle.viewY[1];
      g.drawLine(triangle.viewX[1], triangle.viewY[1], triangle.viewX[1] + dx, triangle.viewY[1] + dy);
      g.drawLine(triangle.viewX[1], triangle.viewY[1], triangle.viewX[1] - dx, triangle.viewY[1] - dy);
    }
  }

  private final static double TRIANGLE_SCALE_CROSS = 0.1;

  @Override
  public boolean isInsideXForm(XForm pXForm, int pX, int pY) {
    return insideTriange(convertXFormToShape(pXForm), pX, pY);
  }

  @Override
  public TriangleControlShape convertXFormToShape(XForm pXForm) {
    return new TriangleControlShape(frameControlsUtil, config, pXForm, TRIANGLE_SCALE_CROSS);
  }

  @Override
  public int selectNearestPoint(XForm pXForm, int pViewX, int pViewY) {
    return selectNearestPointFromTriangle(convertXFormToShape(pXForm), pViewX, pViewY);
  }
}
