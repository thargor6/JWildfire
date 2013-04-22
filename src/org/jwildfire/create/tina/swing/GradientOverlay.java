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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.iabs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JColorChooser;
import javax.swing.JPanel;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;

import com.l2fprod.common.beans.editor.FilePropertyEditor;
import com.l2fprod.common.util.ResourceManager;

public class GradientOverlay {
  private final JPanel parent;
  private static final Color GRADIENT_BORDER_COLOR = new Color(217, 219, 223);
  private static final int GRADIENT_OUTER_BORDER = 10;
  private static final int GRADIENT_HEIGHT = 50;
  private static final int GRADIENT_MARKER_SIZE = 15;
  private static final int GRADIENT_MARKER_DISTANCE = 5;
  private static final int GRADIENT_SIZE = RGBPalette.PALETTE_SIZE;
  private int[] markerPos = { 0, 111 };
  private int[] markerXMin = new int[markerPos.length];
  private int[] markerXMax = new int[markerPos.length];

  private int dragStartX, dragStartY, dragMarkerIdx;
  private int markerHandleYMin;
  private int markerHandleYMax;
  private int markerColorSelYMin;
  private int markerColorSelYMax;

  private double gradientOff = 0;
  private double gradientZoom = 1.0;

  private int xMin, xMax, yMin, yMax;
  private int xPos[] = new int[GRADIENT_SIZE + 1];

  public GradientOverlay(JPanel pParent) {
    parent = pParent;
  }

  private void calculateSizes(int pViewportWidth, int pViewportHeight) {
    yMin = pViewportHeight - GRADIENT_OUTER_BORDER - GRADIENT_HEIGHT + 1 - GRADIENT_MARKER_SIZE - GRADIENT_MARKER_DISTANCE;
    yMax = pViewportHeight - GRADIENT_OUTER_BORDER - 1 - GRADIENT_MARKER_SIZE - GRADIENT_MARKER_DISTANCE;

    xMin = xPos[0] = GRADIENT_OUTER_BORDER + 1;
    xMax = xPos[GRADIENT_SIZE] = pViewportWidth - 2 * GRADIENT_OUTER_BORDER;
    double xScl = (double) (pViewportWidth - 2 * GRADIENT_OUTER_BORDER - 2) * gradientZoom / (double) (GRADIENT_SIZE + 1);
    for (int i = 1; i < GRADIENT_SIZE; i++) {
      xPos[i] = GRADIENT_OUTER_BORDER + 1 + (int) ((i + gradientOff) * xScl + 0.5);
    }
  }

  public void paintGradient(Graphics2D g, RGBPalette pGradient, Rectangle pBounds) {
    if (pGradient != null) {
      int width = pBounds.width;
      int height = pBounds.height;
      calculateSizes(width, height);

      drawGradient(g, pGradient);
      drawMarkers(g, pGradient);
    }
  }

  private void drawMarkers(Graphics2D g, RGBPalette pGradient) {
    drawMarker(g, pGradient, 0);
    drawMarker(g, pGradient, 1);
  }

  private void drawMarker(Graphics2D g, RGBPalette pGradient, int pMarkerIdx) {
    g.setColor(GRADIENT_BORDER_COLOR);
    int mPos = markerPos[pMarkerIdx];
    int x = xPos[mPos] + (xPos[mPos + 1] - xPos[mPos]) / 2;
    if (x > xMin && x < xMax) {
      x += 1;
      int size2 = GRADIENT_MARKER_SIZE / 2;
      int yOff = yMax + 1 + size2;

      int xPoints[] = { x - size2, x, x + size2, x + size2, x - size2 };
      int yPoints[] = { yOff + size2, yOff - size2, yOff + size2, yOff + 3 * size2, yOff + 3 * size2 };
      g.fillPolygon(xPoints, yPoints, xPoints.length);
      markerHandleYMin = yOff - size2;
      markerHandleYMax = yOff + size2;
      markerColorSelYMin = markerHandleYMax;
      markerColorSelYMax = yOff + 3 * size2;

      markerXMin[pMarkerIdx] = x - size2;
      markerXMax[pMarkerIdx] = x + size2;

      RGBColor color = pGradient.getColor(mPos);
      g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
      g.fillRect(x - size2 + 1, yOff + size2 + 1, size2 * 2 - 2, size2 * 2 - 2);
    }

  }

  private void drawGradient(Graphics2D g, RGBPalette pGradient) {
    g.setColor(GRADIENT_BORDER_COLOR);
    g.drawRect(xMin - 1, yMin - 1, xMax - xMin + 2, yMax - yMin + 2);
    for (int i = 0; i < GRADIENT_SIZE; i++) {
      int cxMin = xPos[i], cxMax = xPos[i + 1] - 1;
      RGBColor color = pGradient.getColor(i);
      g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
      for (int cx = cxMin; cx <= cxMax && cx >= xMin && cx <= xMax; cx++) {
        g.drawLine(cx, yMin, cx, yMax);
      }
    }
  }

  public double getGradientZoom() {
    return gradientZoom;
  }

  public void setGradientOff(double gradientOff) {
    this.gradientOff = gradientOff;
  }

  public double getGradientOff() {
    return gradientOff;
  }

  public void setGradientZoom(double pGradientZoom) {
    if (pGradientZoom < 1.0) {
      pGradientZoom = 1.0;
    }
    if (fabs(pGradientZoom - 1.0) < EPSILON) {
      gradientOff = 0.0;
    }
    this.gradientZoom = pGradientZoom;
  }

  public void mouseDragged(double pDX, int pPosX, int pPosY, Flame pFlame) {
    int iDX = (int) (pDX + 0.5);
    if (iDX < 1 && pDX > 0.0) {
      iDX = 1;
    }
    else if (iDX > -1 && pDX < 0.0) {
      iDX = -1;
    }
    // drag gradient
    if (dragStartY > yMin && dragStartY < yMax) {
      if (fabs(gradientZoom - 1.0) < EPSILON) {
        int modShift = pFlame.getPalette().getModShift() + iDX;
        pFlame.getPalette().setModShift(modShift);
      }
      else {
        gradientOff += iDX;
        if (gradientOff > 0) {
          gradientOff = 0;
        }
        else {

        }
      }
    }
    // drag marker
    else if (dragStartY > markerHandleYMin && dragStartY < markerHandleYMax) {
      if (dragMarkerIdx >= 0) {
        for (int i = 0; i < GRADIENT_SIZE; i++) {
          int x = pPosX;
          if (x >= xPos[i] && x <= xPos[i + 1]) {
            int newPos = i;
            int leftLimit = dragMarkerIdx == 0 ? 0 : markerPos[dragMarkerIdx - 1] + 1;
            int rightLimit = dragMarkerIdx == markerPos.length - 1 ? GRADIENT_SIZE - 1 : markerPos[dragMarkerIdx + 1] - 1;
            if (newPos < leftLimit) {
              newPos = leftLimit;
            }
            else if (newPos > rightLimit) {
              newPos = rightLimit;
            }
            markerPos[dragMarkerIdx] = newPos;
            break;
          }
        }
      }
    }
  }

  private int getMarkerByPosition(int pPosX) {
    int res = -1;
    int minDist = -1;
    for (int i = 0; i < markerPos.length; i++) {
      int cx = markerXMin[i] + (markerXMax[i] - markerXMin[i]) / 2;
      int dist = iabs(cx - pPosX);
      if (minDist < 0 || dist < minDist) {
        minDist = dist;
        res = i;
      }
    }
    return res;
  }

  public void mouseWheelAction(double dz) {
    double zoom = getGradientZoom() + dz * 5.0;
    setGradientZoom(zoom);
  }

  public void beginDrag(int pStartX, int pStartY) {
    dragStartX = pStartX;
    dragStartY = pStartY;
    dragMarkerIdx = getMarkerByPosition(dragStartX);
  }

  public boolean mouseClicked(int x, int y, RGBPalette pGradient) {
    if (y >= markerColorSelYMin && y <= markerColorSelYMax) {
      int marker = getMarkerByPosition(x);
      if (marker >= 0) {
        ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
        String title = rm.getString("ColorPropertyEditor.title");

        RGBColor color = pGradient.getColor(markerPos[marker]);

        Color selectedColor = JColorChooser.showDialog(parent, title, new Color(color.getRed(), color.getGreen(), color.getBlue()));
        if (selectedColor != null) {

          pGradient.setColor(markerPos[marker], selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue());
          return true;
        }
      }
    }
    return false;
  }

  public void fadeRange(RGBPalette pGradient) {
    pGradient.fadeRange(markerPos[0], markerPos[1]);
  }

  public void invertRange(RGBPalette pGradient) {
    pGradient.negativeColors(markerPos[0], markerPos[1]);
  }

  public void reverseRange(RGBPalette pGradient) {
    pGradient.reverseColors(markerPos[0], markerPos[1]);
  }

  public void sortRange(RGBPalette pGradient) {
    pGradient.sort(markerPos[0], markerPos[1]);
  }

  public void selectAll() {
    markerPos[0] = 0;
    markerPos[1] = GRADIENT_SIZE - 1;
  }

}
