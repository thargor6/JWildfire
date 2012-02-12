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
package org.jwildfire.transform;

import java.awt.Color;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class PixelizeTransformer extends Mesh2DTransformer {
  public enum Grid {
    OFF, XY, X, Y
  }

  @Property(description = "Rectangle width")
  @PropertyMin(2)
  private int width = 20;

  @Property(description = "Rectangle height")
  @PropertyMin(2)
  private int height = 20;

  @Property(description = "Grid color")
  private Color gridColor = new Color(240, 240, 222);

  @Property(description = "Grid mode", editorClass = GridEditor.class)
  private Grid grid = Grid.XY;

  @Property(description = "Centre rectangles")
  private boolean centre = true;

  @Property(description = "Grid size")
  @PropertyMin(1)
  private int gridSize = 2;

  @Property(description = "Color falloff at the left side")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  private double gridFallOffLeft = 0.8;

  @Property(description = "Color falloff at the right side")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  private double gridFallOffRight = 0.1;

  @Property(description = "Color falloff at the top side")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  private double gridFallOffTop = 0.7;

  @Property(description = "Color falloff at the bottom side")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  private double gridFallOffBottom = 0.3;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    // Init
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    int gridSize = this.gridSize;
    Grid grid = (gridSize < 1) ? Grid.OFF : this.grid;
    Pixel pixel = new Pixel();

    int pw = this.width;
    int ph = this.height;
    if (pw < 1)
      pw = 1;
    else if (pw > width)
      pw = width;
    if (ph < 1)
      ph = 1;
    else if (ph > height)
      ph = height;
    if ((pw == 1) && (ph == 1))
      return;
    int pwo = pw;
    int pho = ph;
    int hc = width / pw;
    if ((width % pw) != 0)
      hc += 2;
    int vc = height / ph;
    if ((height % ph) != 0)
      vc += 2;
    int pwa[] = new int[hc];
    int pha[] = new int[vc];

    int gSize = 0;
    if (grid != Grid.OFF) {
      gSize = gridSize;
      if ((gSize > pwo) || (gSize > pho)) {
        if (pwo > pho)
          gSize = pho;
        else
          gSize = pwo;
      }
    }
    int grUp[] = null, ggUp[] = null, gbUp[] = null;
    int grDown[] = null, ggDown[] = null, gbDown[] = null;
    int grLeft[] = null, ggLeft[] = null, gbLeft[] = null;
    int grRight[] = null, ggRight[] = null, gbRight[] = null;

    if (((grid) != Grid.OFF) && (gSize >= 1)) {
      if ((gSize % 2) == 0)
        gSize++;
      int s = (gSize / 2 + 1);
      if (gSize > 1) {
        grUp = new int[s];
        ggUp = new int[s];
        gbUp = new int[s];
        grDown = new int[s];
        ggDown = new int[s];
        gbDown = new int[s];
        grLeft = new int[s];
        ggLeft = new int[s];
        gbLeft = new int[s];
        grRight = new int[s];
        ggRight = new int[s];
        gbRight = new int[s];
      }
    }
    //
    if ((width % pw) == 0) {
      for (int i = 0; i < hc; i++)
        pwa[i] = pw;
    }
    else {
      if (this.centre) {
        pwa[0] = (width % pw) / 2;
        if (pwa[0] == 0)
          pwa[0] = 1;
        for (int i = 1; i < (hc - 1); i++)
          pwa[i] = pw;
        pwa[hc - 1] = width - pwa[0] - (hc - 2) * pw;
        if (pwa[hc - 1] == 0)
          hc--;
      }
      else {
        hc--;
        for (int i = 0; i < (hc - 1); i++)
          pwa[i] = pw;
        pwa[hc - 1] = width - (hc - 1) * pw;
      }
    }

    if ((height % ph) == 0) {
      for (int i = 0; i < vc; i++)
        pha[i] = ph;
    }
    else {
      if (this.centre) {
        pha[0] = (height % ph) / 2;
        if (pha[0] == 0)
          pha[0] = 1;
        for (int i = 1; i < (vc - 1); i++)
          pha[i] = ph;
        pha[vc - 1] = height - pha[0] - (vc - 2) * ph;
        if (pha[vc - 1] == 0)
          vc--;
      }
      else {
        vc--;
        for (int i = 0; i < (vc - 1); i++)
          pha[i] = ph;
        pha[vc - 1] = height - (vc - 1) * ph;
      }
    }

    {
      int topOffset = 0;
      for (int i = 0; i < vc; i++) {
        ph = pha[i];
        int leftOffset = 0;
        for (int j = 0; j < hc; j++) {
          pw = pwa[j];
          int pc = pw * ph;
          /* compute the average-color */
          int ra = 0, ga = 0, ba = 0;
          for (int k = 0; k < ph; k++) {
            for (int l = 0; l < pw; l++) {
              int x = leftOffset + l;
              int y = topOffset + k;
              pixel.setARGBValue(srcImg.getARGBValue(x, y));
              ra += pixel.r;
              ga += pixel.g;
              ba += pixel.b;
            }
          }
          ra = Tools.limitColor(ra / pc);
          ga = Tools.limitColor(ga / pc);
          ba = Tools.limitColor(ba / pc);
          // set this color in the current box 
          for (int k = 0; k < ph; k++) {
            for (int l = 0; l < pw; l++) {
              int x = leftOffset + l;
              int y = topOffset + k;
              img.setRGB(x, y, pixel);
            }
          }
          leftOffset += pw;
        }
        topOffset += ph;
      }
    }

    if ((grid != Grid.OFF) && (gSize >= 1)) {
      double gFallOffUp = this.gridFallOffTop;
      double gFallOffDown = this.gridFallOffBottom;
      double gFallOffLeft = this.gridFallOffLeft;
      double gFallOffRight = this.gridFallOffRight;

      if (gFallOffUp < 0.0)
        gFallOffUp = 0.0;
      if (gFallOffDown < 0.0)
        gFallOffDown = 0.0;
      if (gFallOffLeft < 0.0)
        gFallOffLeft = 0.0;
      if (gFallOffRight < 0.0)
        gFallOffRight = 0.0;

      int s = (gSize / 2 + 1);
      int n = s - 1;
      if (gSize > 1) {
        if ((gFallOffUp < 0.0001) && (gFallOffDown < 0.0001) && (gFallOffLeft < 0.0001)
            && (gFallOffRight < 0.0001))
        {
          for (int k = 0; k < s; k++) {
            grUp[k] = grDown[k] = grLeft[k] = grRight[k] = this.gridColor.getRed();
            ggUp[k] = ggDown[k] = ggLeft[k] = ggRight[k] = this.gridColor.getGreen();
            gbUp[k] = gbDown[k] = gbLeft[k] = gbRight[k] = this.gridColor.getBlue();
          }
        }
        else {
          calcCArray(grUp, s, gFallOffUp, this.gridColor.getRed());
          calcCArray(ggUp, s, gFallOffUp, this.gridColor.getGreen());
          calcCArray(gbUp, s, gFallOffUp, this.gridColor.getBlue());
          calcCArray(grDown, s, gFallOffDown, this.gridColor.getRed());
          calcCArray(ggDown, s, gFallOffDown, this.gridColor.getGreen());
          calcCArray(gbDown, s, gFallOffDown, this.gridColor.getBlue());
          calcCArray(grLeft, s, gFallOffLeft, this.gridColor.getRed());
          calcCArray(ggLeft, s, gFallOffLeft, this.gridColor.getGreen());
          calcCArray(gbLeft, s, gFallOffLeft, this.gridColor.getBlue());
          calcCArray(grRight, s, gFallOffRight, this.gridColor.getRed());
          calcCArray(ggRight, s, gFallOffRight, this.gridColor.getGreen());
          calcCArray(gbRight, s, gFallOffRight, this.gridColor.getBlue());
        }
      }
      else
        n = 0;
      // horizontal grid     
      if ((grid == Grid.XY) || (grid == Grid.X)) {
        int topOffset = 0;
        int r, g, b;
        for (int i = 0; i < (vc - 1); i++) {
          ph = pha[i];
          topOffset += ph;
          // base grid
          r = this.gridColor.getRed();
          g = this.gridColor.getGreen();
          b = this.gridColor.getBlue();
          for (int j = 0; j < width; j++) {
            img.setRGB(j, topOffset, r, g, b);
          }
          // up 
          for (int kk = 1; kk <= n; kk++) {
            r = grUp[kk];
            g = ggUp[kk];
            b = gbUp[kk];
            for (int j = 0; j < width; j++) {
              int y = topOffset - kk;
              if (y >= 0)
                img.setRGB(j, y, r, g, b);
            }
          }
          // down
          for (int kk = 1; kk <= n; kk++) {
            r = grDown[kk];
            g = ggDown[kk];
            b = gbDown[kk];
            for (int j = 0; j < width; j++) {
              int y = topOffset + kk;
              if (y < height)
                img.setRGB(j, y, r, g, b);
            }
          }
        }
      }
      // vertical grid 
      if ((grid == Grid.XY) || (grid == Grid.Y)) {
        int r, g, b;
        int leftOffset = 0;
        for (int i = 0; i < (hc - 1); i++) {
          pw = pwa[i];
          leftOffset += pw;
          // base grid 
          r = this.gridColor.getRed();
          g = this.gridColor.getGreen();
          b = this.gridColor.getBlue();
          for (int j = 0; j < height; j++) {
            img.setRGB(leftOffset, j, r, g, b);
          }
          // left 
          for (int kk = 1; kk <= n; kk++) {
            r = grLeft[kk];
            g = ggLeft[kk];
            b = gbLeft[kk];
            for (int j = 0; j < height; j++) {
              int x = leftOffset - kk;
              if (x >= 0)
                img.setRGB(x, j, r, g, b);
            }
          }

          // right 
          for (int kk = 1; kk <= n; kk++) {
            r = grRight[kk];
            g = ggRight[kk];
            b = gbRight[kk];
            for (int j = 0; j < height; j++) {
              int x = leftOffset + kk;
              if (x < width)
                img.setRGB(x, j, r, g, b);
            }
          }

        }
      }

      // connections 
      if (grid == Grid.XY) {
        int topOffset = 0;
        for (int i = 0; i < (vc - 1); i++) {
          ph = pha[i];
          topOffset += ph;
          int leftOffset = 0;
          for (int j = 0; j < (hc - 1); j++) {
            pw = pwa[j];
            leftOffset += pw;
            int r, g, b;
            // base grid           
            r = this.gridColor.getRed();
            g = this.gridColor.getGreen();
            b = this.gridColor.getBlue();
            for (int kk = -n; kk <= +n; kk++) {
              int x = leftOffset + kk;
              int y = topOffset;
              if ((x >= 0) && (x < width) && (y >= 0) && (y < height))
                img.setRGB(x, y, r, g, b);
            }
            // up
            for (int kk = 1; kk <= n; kk++) {
              r = grUp[kk];
              g = ggUp[kk];
              b = gbUp[kk];
              for (int ll = kk; ll <= n; ll++) {
                int x = leftOffset - ll;
                int y = topOffset - kk;
                if ((x >= 0) && (x < width) && (y >= 0) && (y < height))
                  img.setRGB(x, y, r, g, b);
                x = leftOffset + ll;
                if ((x >= 0) && (x < width) && (y >= 0) && (y < height))
                  img.setRGB(x, y, r, g, b);
              }
            }
            // down
            for (int kk = 1; kk <= n; kk++) {
              r = grDown[kk];
              g = ggDown[kk];
              b = gbDown[kk];
              for (int ll = kk; ll <= n; ll++) {
                int x = leftOffset - ll;
                int y = topOffset + kk;
                if ((x >= 0) && (x < width) && (y >= 0) && (y < height))
                  img.setRGB(x, y, r, g, b);
                x = leftOffset + ll;
                if ((x >= 0) && (x < width) && (y >= 0) && (y < height))
                  img.setRGB(x, y, r, g, b);
              }
            }
          }
        }
      }
    }
  }

  private void calcCArray(int[] pColors, int pSize, double pFallOff, int pColor) {
    double ttf, ttf2, m;
    int ttw, k;
    pColors[0] = pColor;
    if (pSize == 1) {
      ttf = pFallOff * (double) pColor;
      ttw = (int) (ttf + 0.5);
      if (ttw > 255)
        ttw = 255;
      else if (ttw < 0)
        ttw = 0;
      pColors[1] = ttw;
    }
    else {
      ttf = pFallOff * (double) pColor;
      m = (double) (pColor - ttf) / ((float) (pSize - 1));
      for (k = 1; k < pSize; k++) {
        ttf2 = (float) pColor - (float) k * m;
        ttw = (int) (ttf2 + 0.5);
        if (ttw > 255)
          ttw = 255;
        else if (ttw < 0)
          ttw = 0;
        pColors[k] = ttw;
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    centre = true;
    width = pImg.getImageWidth() / 50;
    height = pImg.getImageHeight() / 50;
    gridColor = new Color(204, 204, 204);
    gridSize = 3;
    grid = Grid.XY;
    gridFallOffLeft = 0.8;
    gridFallOffRight = 0.1;
    gridFallOffTop = 0.5;
    gridFallOffBottom = 0.2;
  }

  public static class GridEditor extends ComboBoxPropertyEditor {
    public GridEditor() {
      super();
      setAvailableValues(new Grid[] { Grid.OFF, Grid.XY, Grid.X, Grid.Y });
    }
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public Color getGridColor() {
    return gridColor;
  }

  public void setGridColor(Color gridColor) {
    this.gridColor = gridColor;
  }

  public Grid getGrid() {
    return grid;
  }

  public void setGrid(Grid grid) {
    this.grid = grid;
  }

  public boolean isCentre() {
    return centre;
  }

  public void setCentre(boolean centre) {
    this.centre = centre;
  }

  public int getGridSize() {
    return gridSize;
  }

  public void setGridSize(int gridSize) {
    this.gridSize = gridSize;
  }

  public double getGridFallOffLeft() {
    return gridFallOffLeft;
  }

  public void setGridFallOffLeft(double gridFallOffLeft) {
    this.gridFallOffLeft = gridFallOffLeft;
  }

  public double getGridFallOffRight() {
    return gridFallOffRight;
  }

  public void setGridFallOffRight(double gridFallOffRight) {
    this.gridFallOffRight = gridFallOffRight;
  }

  public double getGridFallOffTop() {
    return gridFallOffTop;
  }

  public void setGridFallOffTop(double gridFallOffTop) {
    this.gridFallOffTop = gridFallOffTop;
  }

  public double getGridFallOffBottom() {
    return gridFallOffBottom;
  }

  public void setGridFallOffBottom(double gridFallOffBottom) {
    this.gridFallOffBottom = gridFallOffBottom;
  }

}
