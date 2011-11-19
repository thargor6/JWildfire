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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class FlamePanel extends ImagePanel {
  private final static int BORDER = 20;
  private static final Color XFORM_COLOR = new Color(217, 219, 223);
  private static final Color BACKGROUND_COLOR = new Color(60, 60, 60);
  private static float LINE_WIDTH = 1.0f;
  private static float DASH1[] = { 6.0f };
  private static BasicStroke DASHED = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, DASH1, 0.0f);
  private static BasicStroke SOLID = new BasicStroke(LINE_WIDTH);

  private static final long serialVersionUID = 1L;
  private final FlameHolder flameHolder;

  private boolean drawImage = true;
  private boolean drawFlame = true;
  private XForm selectedXForm = null;

  private double viewXScale, viewYScale;
  private double viewXTrans, viewYTrans;

  public FlamePanel(SimpleImage pSimpleImage, int pX, int pY, int pWidth, FlameHolder pFlameHolder) {
    super(pSimpleImage, pX, pY, pWidth);
    flameHolder = pFlameHolder;
  }

  @Override
  public void paintComponent(Graphics g) {
    if (drawImage) {
      super.paintComponent(g);
    }
    else {
      fillBackground(g);
    }
    if (drawFlame) {
      paintFlame((Graphics2D) g);
    }
  }

  private void fillBackground(Graphics g) {
    g.setColor(BACKGROUND_COLOR);
    Rectangle bounds = this.getBounds();
    g.fillRect(0, 0, bounds.width, bounds.height);
  }

  private class Triangle {
    private final XForm xForm;
    double x[] = new double[3];
    double y[] = new double[3];
    int viewX[] = new int[3];
    int viewY[] = new int[3];

    public Triangle(XForm pXForm) {
      xForm = pXForm;
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
        //          
        // use the same layout as Apophysis
        double tx = affineTransformedX(x[i], y[i]);
        double ty = affineTransformedY(x[i], y[i]);
        viewX[i] = xToView(tx);
        viewY[i] = yToView(ty);
      }
    }

    public double affineTransformedX(double pX, double pY) {
      //      return pX * xForm.getCoeff00() + pY * xForm.getCoeff10() + xForm.getCoeff20();
      // use the same layout as Apophysis
      return pX * xForm.getCoeff00() + (-pY * xForm.getCoeff10()) + xForm.getCoeff20();
    }

    public double affineTransformedY(double pX, double pY) {
      //      return pX * xForm.getCoeff01() + pY * xForm.getCoeff11() + xForm.getCoeff21();
      // use the same layout as Apophysis
      return (-pX * xForm.getCoeff01()) + pY * xForm.getCoeff11() + (-xForm.getCoeff21());
    }
  }

  private void paintFlame(Graphics2D g) {
    Flame flame = flameHolder.getFlame();
    if (flame != null) {
      Rectangle bounds = this.getBounds();
      int width = bounds.width;
      int height = bounds.height;

      int areaLeft = BORDER;
      // unused:
      //      int areaRight = width - 1 - BORDER;
      //      int areaTop = BORDER;
      int areaBottom = height - 1 - BORDER;

      g.setColor(XFORM_COLOR);

      double viewXMin = -2.0;
      double viewXMax = 2.0;
      double viewYMin = -2.0;
      double viewYMax = 2.0;

      viewXScale = (double) (width - 2 * BORDER) / (viewXMax - viewXMin);
      viewYScale = (double) (height - 2 * BORDER) / (viewYMin - viewYMax);
      viewXTrans = viewXMin * viewXScale - areaLeft;
      viewYTrans = viewYMin * viewYScale - areaBottom;

      for (XForm xForm : flame.getXForms()) {
        Triangle triangle = new Triangle(xForm);
        if (selectedXForm != null && selectedXForm == xForm) {
          g.setStroke(SOLID);
        }
        else {
          g.setStroke(DASHED);
        }
        g.drawPolygon(triangle.viewX, triangle.viewY, 3);

        g.setStroke(SOLID);
        int radius = 10;
        g.drawOval(triangle.viewX[1] - radius / 2, triangle.viewY[1] - radius / 2, radius, radius);
      }

    }
  }

  private int xToView(double pX) {
    return Tools.FTOI(viewXScale * pX - viewXTrans);
  }

  private int yToView(double pY) {
    return Tools.FTOI(viewYScale * pY - viewYTrans);
  }

  private double viewToX(int viewX) {
    return ((double) viewX + viewXTrans) / viewXScale;
  }

  private double viewToY(int viewY) {
    return ((double) viewY + viewYTrans) / viewYScale;
  }

  public void setDrawImage(boolean drawImage) {
    this.drawImage = drawImage;
  }

  public void setDrawFlame(boolean drawFlame) {
    this.drawFlame = drawFlame;
  }

  public void setSelectedXForm(XForm selectedXForm) {
    this.selectedXForm = selectedXForm;
  }

  public boolean mouseDragged(int pX, int pY) {
    if (selectedXForm != null) {
      int viewDX = pX - xBeginDrag;
      int viewDY = pY - yBeginDrag;
      if (viewDX != 0 || viewDY != 0) {
        double dx = viewToX(pX) - viewToX(xBeginDrag);
        double dy = viewToY(pY) - viewToY(yBeginDrag);
        xBeginDrag = pX;
        yBeginDrag = pY;
        if (Math.abs(dx) > Tools.ZERO || Math.abs(dy) > Tools.ZERO) {
          // move
          selectedXForm.setCoeff20(selectedXForm.getCoeff20() + dx);
          selectedXForm.setCoeff21(selectedXForm.getCoeff21() - dy);
          return true;
        }
      }
    }
    return false;
  }

  int xBeginDrag, yBeginDrag;

  public void mousePressed(int x, int y) {
    if (selectedXForm != null) {
      xBeginDrag = x;
      yBeginDrag = y;
    }
  }

  public int mouseClicked(int x, int y) {
    // select flame
    Flame flame = flameHolder.getFlame();
    if (flame != null) {
      for (XForm xForm : flame.getXForms()) {
      }
    }
    return -1;
  }
}
