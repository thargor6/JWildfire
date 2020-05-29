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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.swing.FrameControlsUtil;

public abstract class AbstractControlHandler<T extends AbstractControlShape> {
  protected final FrameControlsUtil frameControlsUtil;
  protected final Prefs prefs;
  protected final FlamePanelConfig config;

  public AbstractControlHandler(FrameControlsUtil pFrameControlsUtil, Prefs pPrefs, FlamePanelConfig pConfig) {
    frameControlsUtil = pFrameControlsUtil;
    prefs = pPrefs;
    config = pConfig;
  }

  protected static final Color XFORM_COLOR = new Color(217, 219, 223);
  protected static final Color XFORM_POST_COLOR = new Color(255, 219, 160);

  protected static final BasicStroke SELECTED_LINE_NEW = new BasicStroke(2.0f);
  protected static final BasicStroke NORMAL_CIRCLE_LINE = new BasicStroke(1.0f);
  protected static final BasicStroke SELECTED_CIRCLE_LINE = new BasicStroke(2.0f);

  protected static final BasicStroke NORMAL_LINE_NEW = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 10, 4 }, 0);

  protected static final int SHADOW_DIST = 1;

  // Apophysis-compatible colors
  protected static final Color[] XFORM_COLORS = new Color[] {
      new Color(255, 0, 0), new Color(204, 204, 0), new Color(0, 204, 0),
      new Color(0, 204, 204), new Color(64, 64, 255), new Color(204, 0, 204),
      new Color(204, 128, 0), new Color(128, 0, 79), new Color(128, 128, 34),
      new Color(96, 128, 96), new Color(80, 128, 128), new Color(79, 79, 128),
      new Color(128, 80, 128), new Color(128, 96, 34) };

  public abstract void drawXForm(Graphics2D g, XForm pXForm, int pIndex, int pXFormCount, boolean pIsFinal, boolean pShadow, boolean pIsSelected);

  public abstract boolean isInsideXForm(XForm pXForm, int pX, int pY);

  public abstract T convertXFormToShape(XForm pXForm);

  public abstract int selectNearestPoint(XForm pXForm, int pViewX, int pViewY);

  // Algorithm from http://www.blackpawn.com/texts/pointinpoly/default.html
  protected boolean insideTriange(TriangleControlShape pTriangle, int pViewX, int pViewY) {
    // Compute vectors
    //    v0 = C - A
    //    v1 = B - A
    //    v2 = P - A
    // A: pTriangle,view[0]
    // B: pTriangle,view[1]
    // C: pTriangle,view[2]
    double v0x = pTriangle.viewX[2] - pTriangle.viewX[0];
    double v0y = pTriangle.viewY[2] - pTriangle.viewY[0];
    double v1x = pTriangle.viewX[1] - pTriangle.viewX[0];
    double v1y = pTriangle.viewY[1] - pTriangle.viewY[0];
    double v2x = pViewX - pTriangle.viewX[0];
    double v2y = pViewY - pTriangle.viewY[0];
    // Compute dot products
    double dot00 = v0x * v0x + v0y * v0y;
    double dot01 = v0x * v1x + v0y * v1y;
    double dot02 = v0x * v2x + v0y * v2y;
    double dot11 = v1x * v1x + v1y * v1y;
    double dot12 = v1x * v2x + v1y * v2y;

    // Compute barycentric coordinates
    double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
    double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

    // Check if point is in triangle
    return (u >= 0) && (v >= 0) && (u + v < 1);
  }

  protected int selectNearestPointFromTriangle(TriangleControlShape pTriangle, int viewX, int viewY) {
    double dx, dy;
    dx = pTriangle.viewX[0] - viewX;
    dy = pTriangle.viewY[0] - viewY;
    double dr0 = MathLib.sqrt(dx * dx + dy * dy);
    dx = pTriangle.viewX[1] - viewX;
    dy = pTriangle.viewY[1] - viewY;
    double dr1 = MathLib.sqrt(dx * dx + dy * dy);
    dx = pTriangle.viewX[2] - viewX;
    dy = pTriangle.viewY[2] - viewY;
    double dr2 = MathLib.sqrt(dx * dx + dy * dy);
    return dr0 < dr1 ? (dr0 < dr2 ? 0 : 2) : (dr1 < dr2 ? 1 : 2);
  }

}
