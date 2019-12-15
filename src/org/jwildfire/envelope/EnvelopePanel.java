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
package org.jwildfire.envelope;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;

public class EnvelopePanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private static final Color BG_COLOR = new Color(0, 0, 0);
  private static final Color GRID_COLOR = new Color(55, 59, 65);
  private static final Color LABEL_COLOR = new Color(174, 179, 186);
  private static final Color CAPTION_COLOR = new Color(154, 159, 166);
  private static final Color ENVELOPE_COLOR = new Color(217, 219, 223);

  //  private static final String LBL_FONT_NAME = "Arial";
  //  private static final int LBL_FONT_SIZE = 10;
  private static final String LBL_FONT_NAME = "SansSerif";
  private static final int LBL_FONT_SIZE = 10;
  private static final int CAPTION_FONT_SIZE = 12;

  private Envelope envelope;
  private final Prefs prefs;
  private boolean drawGrid = true;
  private boolean drawTicks = true;
  private String panelCaption;

  public EnvelopePanel() {
    super();
    prefs = Prefs.getPrefs();
  }

  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    if (prefs.isTinaEditorControlsWithAntialiasing()) {
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }
    super.paintComponent(g);
    EnvelopeView envelopeView = new EnvelopeView(this);
    g.setColor(BG_COLOR);
    g.fillRect(0, 0, envelopeView.getWidth(), envelopeView.getHeight());
    if (envelope != null) {
      if (drawGrid) {
        drawGrid(g, envelopeView);
      }
      drawLines(g, envelopeView);
      if (envelope != null && !envelope.isLocked()) {
        drawPoints(g, envelopeView);
      }
    }
  }

  private void drawGrid(Graphics g, EnvelopeView pEnvelopeView) {
    g.setColor(GRID_COLOR);
    int xnum = 20, ynum = 12;

    /* lines */
    int lineMax = 1000;
    {
      double dx = envelope.getViewXMax() - envelope.getViewXMin();
      double step = Tools.FTOI(dx / (double) xnum);
      if (step < 1.0)
        step = 1.0;
      int div = Tools.FTOI((double) (envelope.getViewXMin() / step));
      double x = (double) div * step;
      int lineCount = 0;
      do {
        double fx = pEnvelopeView.getEnvelopeXScale() * x - pEnvelopeView.getEnvelopeXTrans();
        int dxl = Tools.FTOI(fx);
        if ((dxl >= pEnvelopeView.getEnvelopeLeft()) && (dxl <= pEnvelopeView.getEnvelopeRight())) {
          g.drawLine(dxl, pEnvelopeView.getEnvelopeTop(), dxl, pEnvelopeView.getEnvelopeBottom());
        }
        x += step;
      }
      while (x < envelope.getViewXMax() && ++lineCount < lineMax);
    }

    {
      double dy = envelope.getViewYMax() - envelope.getViewYMin();
      double step = calcSteps(ynum, dy);

      int div = Tools.FTOI((double) (envelope.getViewYMin() / step));
      double y = (double) div * step;
      int lineCount = 0;
      do {
        double fy = pEnvelopeView.getEnvelopeYScale() * y - pEnvelopeView.getEnvelopeYTrans();
        int dyl = Tools.FTOI(fy);
        if ((dyl >= pEnvelopeView.getEnvelopeTop()) && (dyl <= pEnvelopeView.getEnvelopeBottom())) {
          g.drawLine(pEnvelopeView.getEnvelopeLeft(), dyl, pEnvelopeView.getEnvelopeRight(), dyl);
        }
        y += step;
      }
      while (y < envelope.getViewYMax() && ++lineCount < lineMax);
    }
    /* tick-labels */
    if (drawTicks) {
      g.setColor(LABEL_COLOR);
      Font font = new Font(LBL_FONT_NAME, Font.PLAIN, LBL_FONT_SIZE);
      g.setFont(font);
      FontMetrics fm = g.getFontMetrics();
      int yFontOffset = fm.getMaxAscent();
      FontRenderContext frc = fm.getFontRenderContext();
      {
        double dx = envelope.getViewXMax() - envelope.getViewXMin();
        double step = Tools.FTOI(dx / (double) xnum);
        if (step < 1.0)
          step = 1.0;
        int div = Tools.FTOI((double) (envelope.getViewXMin() / step));
        double x = (double) div * step;
        int cnt = 0;
        int tickCount = 0;
        do {
          double fx = pEnvelopeView.getEnvelopeXScale() * x - pEnvelopeView.getEnvelopeXTrans();
          int dxl = Tools.FTOI(fx);
          cnt++;
          if (cnt % 2 == 0) {
            String hs = String.valueOf(Tools.FTOI(x));
            Rectangle2D rect = font.getStringBounds(hs, frc);
            int tw = (int) (rect.getWidth() + 0.5);
            int leftEdge = dxl - tw / 2;
            if ((leftEdge >= pEnvelopeView.getEnvelopeLeft())
                && ((leftEdge + tw) <= pEnvelopeView.getEnvelopeRight()))
            {
              int topEdge = pEnvelopeView.getEnvelopeBottom() - yFontOffset;
              g.drawString(hs, leftEdge, topEdge);
            }
          }
          x += step;
        }
        while (x < envelope.getViewXMax() && ++tickCount < lineMax);
      }
      {
        double dy = envelope.getViewYMax() - envelope.getViewYMin();
        double step = calcSteps(ynum, dy);
        int div = Tools.FTOI((double) (envelope.getViewYMin() / step));
        double x = (double) div * step;
        int cnt = 0;
        int tickCount = 0;
        do {
          double fx = pEnvelopeView.getEnvelopeYScale() * x - pEnvelopeView.getEnvelopeYTrans();
          int dxl = Tools.FTOI(fx);
          cnt++;
          if (cnt % 2 == 0) {
            String hs = Tools.doubleToString(x);
            int topEdge = dxl - (yFontOffset / 2) - 1;
            if (topEdge >= pEnvelopeView.getEnvelopeTop()) {
              int leftEdge = pEnvelopeView.getEnvelopeLeft() + 3;
              g.drawString(hs, leftEdge, topEdge);
            }
          }
          x += step;
        }
        while (x < envelope.getViewYMax() && ++tickCount < lineMax);
      }
    }
    // panel caption
    if(panelCaption!=null && panelCaption.length()>0) {
      g.setColor(CAPTION_COLOR);
      Font font = new Font(LBL_FONT_NAME, Font.PLAIN, CAPTION_FONT_SIZE);
      g.setFont(font);
      FontMetrics fm = g.getFontMetrics();
      int yFontOffset = fm.getMaxAscent();
      FontRenderContext frc = fm.getFontRenderContext();
      Rectangle2D rect = font.getStringBounds(panelCaption, frc);

      int left = pEnvelopeView.getEnvelopeLeft() + (pEnvelopeView.getEnvelopeRight() - pEnvelopeView.getEnvelopeLeft()) / 2 - Tools.FTOI(rect.getWidth() / 2.0);
      int top = pEnvelopeView.getEnvelopeTop() + (pEnvelopeView.getEnvelopeBottom() - pEnvelopeView.getEnvelopeTop()) / 2 + yFontOffset / 2;
      g.drawString(panelCaption, left, top);
    }
  }

  private double calcSteps(int ynum, double dy) {
    double step = dy / (double) ynum;
    if (step > 1.0) {
      step = Tools.FTOI(step);
    }
    else if (step > 0.5) {
      step = 0.5;
    }
    else if (step > 0.25) {
      step = 0.25;
    }
    else if (step > 0.125) {
      step = 0.125;
    }
    else if (step > 0.01) {
      step = 0.01;
    }
    else if (step > 0.001) {
      step = 0.001;
    }
    else if (step > 0.0001) {
      step = 0.0001;
    }
    else {
      step = 0.0001;
    }

    while (dy / step > 2 * ynum) {
      step += step;
    }

    return step;
  }

  private void drawLines(Graphics g, EnvelopeView pEnvelopeView) {
    if (envelope.size() < 2)
      return;
    g.setColor(ENVELOPE_COLOR);

    int subdiv = Interpolation.calcSubDivPRV(envelope.getX(), envelope.size());
    Interpolation interpolationX, interpolationY;
    if (envelope.size() > 2) {
      switch (envelope.getInterpolation()) {
        case SPLINE:
          interpolationX = new SplineInterpolation();
          interpolationY = new SplineInterpolation();
          break;
        case BEZIER:
          interpolationX = new BezierInterpolation();
          interpolationY = new BezierInterpolation();
          break;
        default:
          interpolationX = new LinearInterpolation();
          interpolationY = new LinearInterpolation();
          break;
      }
    }
    else {
      interpolationX = new LinearInterpolation();
      interpolationY = new LinearInterpolation();
    }
    interpolationX.setSrc(envelope.getX());
    interpolationX.setSnum(envelope.size());
    interpolationX.setSubdiv(subdiv);
    interpolationX.interpolate();
    interpolationY.setSrc(envelope.getY());
    interpolationY.setSnum(envelope.size());
    interpolationY.setSubdiv(subdiv);
    interpolationY.interpolate();

    if (interpolationX.getDnum() != interpolationY.getDnum())
      throw new IllegalStateException();

    double[] sx = interpolationX.getDest();
    double[] sy = interpolationY.getDest();
    int sCount = interpolationY.getDnum();
    double x = sx[0];
    double y = sy[0];
    double fx = pEnvelopeView.getEnvelopeXScale() * x - pEnvelopeView.getEnvelopeXTrans();
    double fy = pEnvelopeView.getEnvelopeYScale() * y - pEnvelopeView.getEnvelopeYTrans();
    int dxl = Tools.FTOI(fx);
    int dyl = Tools.FTOI(fy);

    for (int i = 1; i < sCount; i++) {
      x = (double) sx[i];
      y = (double) sy[i];
      fx = pEnvelopeView.getEnvelopeXScale() * x - pEnvelopeView.getEnvelopeXTrans();
      fy = pEnvelopeView.getEnvelopeYScale() * y - pEnvelopeView.getEnvelopeYTrans();
      int dx = Tools.FTOI(fx);
      int dy = Tools.FTOI(fy);
      if ((dx >= pEnvelopeView.getEnvelopeLeft()) && (dx <= pEnvelopeView.getEnvelopeRight())
          && (dxl >= pEnvelopeView.getEnvelopeLeft()) && (dxl <= pEnvelopeView.getEnvelopeRight())
          && (dy >= pEnvelopeView.getEnvelopeTop()) && (dy <= pEnvelopeView.getEnvelopeBottom())
          && (dyl >= pEnvelopeView.getEnvelopeTop()) && (dyl <= pEnvelopeView.getEnvelopeBottom()))
      {
        g.drawLine(dxl, dyl, dx, dy);
        //if(i>1) (void)WritePixel(Envelope_RastPort,dxl,dyl);
      }
      dxl = dx;
      dyl = dy;
    }
  }

  private void drawPoints(Graphics g, EnvelopeView pEnvelopeView) {
    g.setColor(ENVELOPE_COLOR);

    final int envelopeRadius = 3;

    for (int i = 0; i < envelope.size(); i++) {
      double x = envelope.getX()[i];
      double y = envelope.getY()[i];
      double fx = pEnvelopeView.getEnvelopeXScale() * x - pEnvelopeView.getEnvelopeXTrans();
      double fy = pEnvelopeView.getEnvelopeYScale() * y - pEnvelopeView.getEnvelopeYTrans();
      int dx = Tools.FTOI(fx);
      int dy = Tools.FTOI(fy);
      if ((dx >= pEnvelopeView.getEnvelopeLeft()) && (dx <= pEnvelopeView.getEnvelopeRight())
          && (dy >= pEnvelopeView.getEnvelopeTop()) && (dy <= pEnvelopeView.getEnvelopeBottom()))
      {
        int left = dx - envelopeRadius;
        if (left < pEnvelopeView.getEnvelopeLeft())
          left = pEnvelopeView.getEnvelopeLeft();
        int right = dx + envelopeRadius;
        if (right > pEnvelopeView.getEnvelopeRight())
          right = pEnvelopeView.getEnvelopeRight();
        int top = dy - envelopeRadius;
        if (top < pEnvelopeView.getEnvelopeTop())
          top = pEnvelopeView.getEnvelopeTop();
        int bottom = dy + envelopeRadius;
        if (bottom > pEnvelopeView.getEnvelopeBottom())
          bottom = pEnvelopeView.getEnvelopeBottom();
        g.fillRect(left, top, right - left + 1, bottom - top + 1);
        if (i == envelope.getSelectedIdx()) {
          left = dx - envelopeRadius - 2;
          if (left < pEnvelopeView.getEnvelopeLeft())
            left = pEnvelopeView.getEnvelopeLeft();
          right = dx + envelopeRadius + 2;
          if (right > pEnvelopeView.getEnvelopeRight())
            right = pEnvelopeView.getEnvelopeRight();
          top = dy - envelopeRadius - 2;
          if (top < pEnvelopeView.getEnvelopeTop())
            top = pEnvelopeView.getEnvelopeTop();
          bottom = dy + envelopeRadius + 2;
          if (bottom > pEnvelopeView.getEnvelopeBottom())
            bottom = pEnvelopeView.getEnvelopeBottom();
          g.drawRect(left, top, right - left, bottom - top);
        }
      }
    }
  }

  public void setEnvelope(Envelope pEnvelope) {
    envelope = pEnvelope;
  }

  protected Envelope getEnvelope() {
    return envelope;
  }

  public void setDrawGrid(boolean pDrawGrid) {
    drawGrid = pDrawGrid;
  }

  public void setDrawTicks(boolean pDrawTicks) {
    drawTicks = pDrawTicks;
  }

  public void setCaption(String panelCaption) {
    this.panelCaption = panelCaption;
  }
}
