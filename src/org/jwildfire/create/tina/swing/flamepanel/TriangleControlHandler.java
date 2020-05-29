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
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.swing.FrameControlsUtil;
import org.jwildfire.create.tina.swing.MouseDragOperation;

public class TriangleControlHandler extends AbstractControlHandler<TriangleControlShape> {

  public TriangleControlHandler(FrameControlsUtil pFrameControlsUtil, Prefs pPrefs, FlamePanelConfig pConfig) {
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
    g.drawPolygon(triangle.viewX, triangle.viewY, 3);

    // selected point
    if (pIsSelected && (config.getMouseDragOperation() == MouseDragOperation.POINTS)) {
      int radius = 10;
      g.fillOval(triangle.viewX[config.getSelectedPoint()] - radius / 2, triangle.viewY[config.getSelectedPoint()] - radius / 2, radius, radius);
    }
    // axes
    {
      int offset = 16;
      int cx = (triangle.viewX[0] + triangle.viewX[1] + triangle.viewX[2]) / 3;
      int cy = (triangle.viewY[0] + triangle.viewY[1] + triangle.viewY[2]) / 3;
      String label;
      switch (pXForm.getEditPlane()) {
        case XY:
    	  label = "XOY";
    	  break;
        case YZ:
    	  label = "YOZ";
    	  break;
        default:
    	  label = "XOZ";
    	  break;
      }
      if (pIsSelected) {
        for (int i = 0; i < triangle.viewX.length; i++) {
          double dx = triangle.viewX[i] - cx;
          double dy = triangle.viewY[i] - cy;
          double dr = MathLib.sqrt(dx * dx + dy * dy) + MathLib.EPSILON;
          dx /= dr;
          dy /= dr;
          g.drawString(String.valueOf(label.charAt(i)), triangle.viewX[i] + (int) (offset * dx), triangle.viewY[i] + (int) (offset * dy));
        }
      }
      if (prefs.isTinaEditorControlsWithNumbers()) {
        g.setStroke(pIsSelected ? SELECTED_CIRCLE_LINE : NORMAL_CIRCLE_LINE);
        int radius = config.isEditPostTransform() ? 28 : 24;
        g.drawOval(cx - radius / 2, cy - radius / 2, radius, radius);
        String lbl = pIsFinal ? (config.isEditPostTransform() ? "PF" : "F") + String.valueOf(pIndex + 1) : (config.isEditPostTransform() ? "PT" : "T") + String.valueOf(pIndex + 1);
        g.drawString(lbl, cx - (config.isEditPostTransform() ? 12 : 6), cy + 6);
      }
    }
  }

  @Override
  public boolean isInsideXForm(XForm pXForm, int pX, int pY) {
    return insideTriange(convertXFormToShape(pXForm), pX, pY);
  }

  @Override
  public TriangleControlShape convertXFormToShape(XForm pXForm) {
    return new TriangleControlShape(frameControlsUtil, config, pXForm);
  }

  @Override
  public int selectNearestPoint(XForm pXForm, int pViewX, int pViewY) {
    return selectNearestPointFromTriangle(convertXFormToShape(pXForm), pViewX, pViewY);
  }

}
