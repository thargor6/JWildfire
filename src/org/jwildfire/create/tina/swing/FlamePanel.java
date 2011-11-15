/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class FlamePanel extends ImagePanel {
  private final static int BORDER = 20;
  private static final Color XFORM_COLOR = new Color(217, 219, 223);

  private static final long serialVersionUID = 1L;
  private final FlameHolder flameHolder;

  public FlamePanel(SimpleImage pSimpleImage, int pX, int pY, int pWidth, FlameHolder pFlameHolder) {
    super(pSimpleImage, pX, pY, pWidth);
    flameHolder = pFlameHolder;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    paintFlame(g);
  }

  private void paintFlame(Graphics g) {
    Flame flame = flameHolder.getFlame();
    if (flame != null) {
      Rectangle bounds = this.getBounds();
      int width = bounds.width;
      int height = bounds.height;

      int areaLeft = BORDER;
      int areaRight = width - 1 - BORDER;
      int areaTop = BORDER;
      int areaBottom = height - 1 - BORDER;

      g.setColor(XFORM_COLOR);
      g.drawRect(areaLeft, areaTop, areaRight - areaLeft, areaBottom - areaTop);

      double viewXMin = -2.0;
      double viewXMax = 2.0;
      double viewYMin = -2.0;
      double viewYMax = 2.0;

      double viewXScale = (double) (width - 2 * BORDER) / (viewXMax - viewXMin);
      double viewYScale = (double) (height - 2 * BORDER) / (viewYMin - viewYMax);
      double viewXTrans = viewXMin * viewXScale - areaLeft;
      double viewYTrans = viewYMin * viewYScale - areaBottom;

      for (XForm xForm : flame.getXForms()) {
        double x[] = new double[3];
        double y[] = new double[3];
        int viewX[] = new int[3];
        int viewY[] = new int[3];
        // init points
        // x
        x[0] = 1.0;
        y[0] = 0.0;
        // 0
        x[1] = 0.0;
        y[1] = 0.0;
        // y
        x[2] = 0.0;
        y[2] = 1.0;
        // transform the points (affine transform)
        for (int i = 0; i < x.length; i++) {
          x[i] = x[i] * xForm.getCoeff00() + y[i] * xForm.getCoeff10() + xForm.getCoeff20();
          y[i] = x[i] * xForm.getCoeff01() + y[i] * xForm.getCoeff11() + xForm.getCoeff21();
          viewX[i] = Tools.FTOI(viewXScale * x[i] - viewXTrans);
          viewY[i] = Tools.FTOI(viewYScale * y[i] - viewYTrans);
        }

        g.drawPolygon(viewX, viewY, x.length);
      }

    }
  }
}
