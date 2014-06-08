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
import java.awt.RenderingHints;

import javax.swing.JColorChooser;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;

import com.l2fprod.common.beans.editor.FilePropertyEditor;
import com.l2fprod.common.util.ResourceManager;

public class GradientOverlay {
  private final FlamePanel parent;
  private static final int GRADIENT_OUTER_BORDER = 20;
  private static final int GRADIENT_HEIGHT = 56;
  private static final int GRADIENT_MARKER_SIZE = 24;
  private static final int GRADIENT_MARKER_DISTANCE = 15;
  private static final int GRADIENT_SIZE = RGBPalette.PALETTE_SIZE;
  private int[] markerPos = { 0, GRADIENT_SIZE - 1 };
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

  public GradientOverlay(FlamePanel pParent) {
    parent = pParent;
  }

  private void calculateSizes(int pViewportWidth, int pViewportHeight) {
    yMin = pViewportHeight - GRADIENT_OUTER_BORDER - GRADIENT_HEIGHT + 1 - GRADIENT_MARKER_SIZE - GRADIENT_MARKER_DISTANCE;
    yMax = pViewportHeight - GRADIENT_OUTER_BORDER - 1 - (GRADIENT_MARKER_SIZE * 3) / 2 - GRADIENT_MARKER_DISTANCE;

    xMin = xPos[0] = GRADIENT_OUTER_BORDER + 1;
    xMax = xPos[GRADIENT_SIZE] = pViewportWidth - GRADIENT_OUTER_BORDER - 1;
    double xScl = (double) (pViewportWidth - 2 * GRADIENT_OUTER_BORDER - 1 + 1) * gradientZoom / (double) (GRADIENT_SIZE + 2);
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
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(FlamePanelConfig.XFORM_COLOR);
    int mPos = markerPos[pMarkerIdx];
    int x = xPos[mPos] + (xPos[mPos + 1] - xPos[mPos]) / 2;
    if (x >= xMin && x <= xMax) {
      x += 1;
      int size2 = GRADIENT_MARKER_SIZE / 2;
      int yOff = yMax + 1 + size2;

      int xPoints[] = { x - size2 + 1, x, x + size2 - 1 };
      int yPoints[] = { yOff + size2, yOff - size2, yOff + size2 };
      g.fillPolygon(xPoints, yPoints, xPoints.length);
      g.fillOval(x - size2, yOff + size2, 2 * size2, 2 * size2);

      markerHandleYMin = yOff - size2;
      markerHandleYMax = yOff + size2;
      markerColorSelYMin = markerHandleYMax;
      markerColorSelYMax = yOff + 3 * size2;

      markerXMin[pMarkerIdx] = x - size2;
      markerXMax[pMarkerIdx] = x + size2;

      RGBColor color = pGradient.getColor(mPos);
      g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
      g.fillOval(x - size2 + 2, yOff + size2 + 2, 2 * size2 - 4, 2 * size2 - 4);
    }

  }

  private void drawGradient(Graphics2D g, RGBPalette pGradient) {
    g.setColor(FlamePanelConfig.XFORM_COLOR);
    g.drawRect(xMin - 1, yMin - 1, xMax - xMin + 1, yMax - yMin + 2);
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

  public boolean mouseDragged(double pDX, int pPosX, int pPosY, Layer pLayer) {
    boolean reRender = false;
    int iDX = (int) (pDX + 0.5);
    if (iDX < 1 && pDX > 0.0) {
      iDX = 1;
    }
    else if (iDX > -1 && pDX < 0.0) {
      iDX = -1;
    }
    // drag marker
    if (dragStartY > markerHandleYMin && dragStartY < markerHandleYMax) {
      if (dragMarkerIdx >= 0) {
        setMarker(pPosX, dragMarkerIdx);
      }
    }
    // drag gradient
    else if (dragStartY > yMin && dragStartY < yMax) {
      if (fabs(gradientZoom - 1.0) < EPSILON) {
        int modShift = pLayer.getPalette().getModShift() + iDX;
        pLayer.getPalette().shiftColors(modShift);
        reRender = true;
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
    return reRender;
  }

  private void setMarker(int pPosX, int pIndex) {
    for (int i = 0; i < GRADIENT_SIZE; i++) {
      int x = pPosX;
      if ((x >= xPos[i] && x <= xPos[i + 1]) || (i == 0 && x <= xPos[i + 1]) || (i == GRADIENT_SIZE - 1 && x >= xPos[i])) {
        int newPos = i;
        int leftLimit = 0;
        int rightLimit = GRADIENT_SIZE - 1;
        if (newPos < leftLimit) {
          newPos = leftLimit;
        }
        else if (newPos > rightLimit) {
          newPos = rightLimit;
        }
        markerPos[pIndex] = newPos;
        break;
      }
    }
  }

  private int getMarkerByPosition(int pPosX, boolean pExact) {
    int res = -1;
    int minDist = -1;
    for (int i = 0; i < markerPos.length; i++) {
      if (pExact) {
        if (pPosX >= markerXMin[i] && pPosX <= markerXMax[i]) {
          return i;
        }
      }
      else {
        int cx = markerXMin[i] + (markerXMax[i] - markerXMin[i]) / 2;
        int dist = iabs(cx - pPosX);
        if (minDist < 0 || dist < minDist) {
          minDist = dist;
          res = i;
        }
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
    dragMarkerIdx = getMarkerByPosition(dragStartX, false);
  }

  public boolean mouseClicked(int x, int y, RGBPalette pGradient) {
    if (y >= markerColorSelYMin && y <= markerColorSelYMax) {
      int marker = getMarkerByPosition(x, true);
      if (marker >= 0) {
        return gradientMarker_selectColor(marker, pGradient);
      }
    }
    else if (y >= markerHandleYMin && y <= markerHandleYMax) {
      int marker = getMarkerByPosition(x, false);
      setMarker(x, marker);
      return true;
    }
    return false;
  }

  public void fadeRange(RGBPalette pGradient) {
    pGradient.fadeRange(getFrom(), getTo());
  }

  public void invertRange(RGBPalette pGradient) {
    pGradient.negativeColors();
  }

  public void reverseRange(RGBPalette pGradient) {
    pGradient.reverseColors();
  }

  public void sortRange(RGBPalette pGradient) {
    pGradient.sort();
  }

  public void selectAll() {
    markerPos[0] = 0;
    markerPos[1] = GRADIENT_SIZE - 1;
  }

  public void gradientMarker_move(int marker, int pDeltaPos) {
    if (marker >= 0) {
      int leftLimit = 0;
      int rightLimit = GRADIENT_SIZE - 1;
      int newPos = markerPos[marker] + pDeltaPos;
      if (newPos < leftLimit) {
        newPos = leftLimit;
      }
      else if (newPos > rightLimit) {
        newPos = rightLimit;
      }
      markerPos[marker] = newPos;
    }
  }

  public boolean gradientMarker_selectColor(int marker, RGBPalette pGradient) {
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
    return false;
  }

  public int getFrom() {
    return markerPos[0] <= markerPos[1] ? markerPos[0] : markerPos[1];
  }

  public int getTo() {
    return markerPos[0] <= markerPos[1] ? markerPos[1] : markerPos[0];
  }

  private RGBColor[] currClipboard;

  public void copyRange(RGBPalette pGradient) {
    if (getTo() >= getFrom()) {
      currClipboard = new RGBColor[getTo() - getFrom() + 1];
      for (int i = getFrom(); i <= getTo(); i++) {
        currClipboard[i - getFrom()] = pGradient.getRawColor(i);
      }
    }
  }

  public void pasteRange(RGBPalette pGradient) {
    if (currClipboard != null && currClipboard.length > 0) {
      for (int i = getFrom(); (i <= getTo()) && ((i - getFrom()) < currClipboard.length); i++) {
        RGBColor color = currClipboard[i - getFrom()];
        pGradient.setColor(i, color.getRed(), color.getGreen(), color.getBlue());
      }
    }
  }

  public void eraseRange(RGBPalette pGradient) {
    if (getTo() >= getFrom()) {
      for (int i = getFrom(); i <= getTo(); i++) {
        pGradient.setColor(i, 0, 0, 0);
      }
    }
  }

  public void monochrome(RGBPalette pGradient) {
    pGradient.monochrome(getFrom(), getTo());
  }

  public void fadeAll(RGBPalette pGradient) {
    int startIdx = 0;//getFrom();
    int endIdx = GRADIENT_SIZE - 1; // getTo();
    for (int i = startIdx; i <= endIdx; i++) {
      RGBColor color = pGradient.getRawColor(i);
      if (color.getRed() > 0 || color.getGreen() > 0 || color.getBlue() > 0 || i == endIdx) {
        if (startIdx < i) {
          pGradient.fadeRange(startIdx, i);
          startIdx = i;
        }
      }
    }
  }
}
