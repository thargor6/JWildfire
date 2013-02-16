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
package org.jwildfire.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;


public class FormulaPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private double x[];
  private double y[][];

  public void setData(double pX[], double pY[][]) {
    x = pX;
    y = pY;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Color bgColor = Color.BLACK;
    Color borderColor = Color.WHITE;
    Color curveColor[] = { Color.YELLOW, Color.RED, Color.GREEN };
    Rectangle bounds = getBounds();

    int width = bounds.width;
    int height = bounds.height;

    g.setColor(bgColor);
    g.fillRect(0, 0, width, height);
    g.setColor(borderColor);
    g.drawRect(0, 0, width - 1, height - 1);

    //    x = new double[100];
    //    y = new double[5][100];
    //    {
    //      double xMin = 0.0;
    //      double xMax = 1.0;
    //      double dx = (xMax - xMin) / (pCount - 1);
    //      double cx = xMin;
    //      for (int i = 0; i < pCount; i++) {
    //        x[i] = cx;
    //        y[0][i] = x[i] * x[i];
    //        y[1][i] = x[i] * x[i] * x[i];
    //        y[2][i] = x[i] * x[i] * x[i] * x[i];
    //        y[3][i] = -0.5 * (1.0 - Math.exp(x[i]));
    //        y[4][i] = 1.0 - Math.exp(-x[i]);
    //        cx += dx;
    //      }
    //    }

    if ((x == null) || (y == null))
      return;
    int pCount = x.length;

    // compute min/max
    double xMin = pCount > 0 ? x[0] : 0.0;
    double xMax = xMin;
    double yMin = pCount > 0 ? y[0][0] : 0.0;
    double yMax = yMin;
    for (int i = 1; i < pCount; i++) {
      if (x[i] < xMin) {
        xMin = x[i];
      }
      else if (x[i] > xMax) {
        xMax = x[i];
      }
      for (int j = 0; j < y.length; j++) {
        if (y[j][i] < yMin) {
          yMin = y[j][i];
        }
        else if (y[j][i] > yMax) {
          yMax = y[j][i];
        }
      }
    }
    if ((xMax - xMin) < MathLib.EPSILON) {
      xMax = xMin + MathLib.EPSILON;
    }
    if ((yMax - yMin) < MathLib.EPSILON) {
      yMax = yMin + MathLib.EPSILON;
    }
    // visual area
    double visXMin = xMin - (xMax - xMin) * 0.1;
    double visXMax = xMax + (xMax - xMin) * 0.1;
    double visYMin = yMin - (yMax - yMin) * 0.1;
    double visYMax = yMax + (yMax - yMin) * 0.1;
    // scaling factors
    double xScale = width / (visXMax - visXMin);
    double yScale = height / (visYMax - visYMin);
    int dxi = (int) (visXMin * xScale + 0.5);
    int dyi = (int) (visYMin * yScale + 0.5);
    // draw curve
    for (int j = 0; j < y.length; j++) {
      g.setColor(curveColor[j % curveColor.length]);
      int xi[] = new int[pCount];
      int yi[] = new int[pCount];
      for (int i = 0; i < pCount; i++) {
        xi[i] = (int) (x[i] * xScale + 0.5) - dxi;
        yi[i] = height - ((int) (y[j][i] * yScale + 0.5) - dyi);
      }
      g.drawPolyline(xi, yi, pCount);
    }
    // draw ticks
    g.setColor(borderColor);
    final int TICK_LENGTH = 7;
    final int LABEL_OFF = 3;
    final int X_TICKS = 3;
    int fontHeight = getFont().getSize();
    {
      double xStep = calcTickStep(visXMax, visXMin, X_TICKS);
      double tickXMin = calcTickMinMax(visXMin, xStep);
      double tickXMax = calcTickMinMax(visXMax, xStep) + xStep;
      // System.out.println(tickXMin + " " + tickXMax + " " + xStep);
      double xt = tickXMin;
      while (xt + xStep < tickXMax + MathLib.EPSILON) {
        int xti = (int) (xt * xScale + 0.5) - dxi;
        g.drawLine(xti, 0, xti, TICK_LENGTH);
        g.drawLine(xti, height - 1, xti, height - 1 - TICK_LENGTH);
        String label = Tools.doubleToString(xt);
        g.drawString(label, xti - g.getFontMetrics().stringWidth(label) / 2, height - 1
            - TICK_LENGTH - LABEL_OFF);
        xt += xStep;
      }
    }

    {
      final int yTicks = (int) ((double) height / (double) width * X_TICKS + 0.5);
      double yStep = calcTickStep(visYMax, visYMin, yTicks);
      double tickYMin = calcTickMinMax(visYMin, yStep);
      double tickYMax = calcTickMinMax(visYMax, yStep) + yStep;
      // System.out.println(tickYMin + " " + tickYMax + " "
      // + yStep);
      double yt = tickYMin;
      while (yt + yStep < tickYMax + MathLib.EPSILON) {
        int yti = height - ((int) (yt * yScale + 0.5) - dyi);
        g.drawLine(0, yti, TICK_LENGTH, yti);
        g.drawLine(width - 1, yti, width - 1 - TICK_LENGTH, yti);
        String label = Tools.doubleToString(yt);
        g.drawString(label,
            width - 1 - TICK_LENGTH - LABEL_OFF - g.getFontMetrics().stringWidth(label), yti
                + fontHeight / 2);
        yt += yStep;
      }
    }
  }

  private double calcTickMinMax(double pVisMin, double pStep) {
    double res = 0.0;
    if (pVisMin > 0.0) {
      while (res + pStep < pVisMin) {
        res += pStep;
      }
    }
    else {
      while (res - pStep > pVisMin) {
        res -= pStep;
      }
    }
    return res;
  }

  private double calcTickStep(double pVisMax, double pVisMin, int pTicks) {
    double res = (pVisMax - pVisMin) / (pTicks + 1);
    if (res < 1.0) {
      double v = res;
      res = 1.0;
      while (v < 1.0) {
        v *= 5.0;
        res /= 5.0;
      }
    }
    else {
      res = (int) (res + 0.5);
    }
    return res;
  }

}
