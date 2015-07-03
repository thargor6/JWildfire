/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.render.filter;

import java.awt.Color;
import java.awt.Graphics;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.FilterHolder;
import org.jwildfire.image.SimpleImage;

public class FilterKernelVisualisationFlatRenderer extends FilterHolder implements FilterKernelVisualisationRenderer {
  public FilterKernelVisualisationFlatRenderer(Flame pFlame) {
    super(pFlame);
  }

  private static final Color emptyFilterColor = new Color(160, 160, 160);
  private static final Color borderColor = new Color(200, 200, 200);
  private static final int borderWidth = 1;

  @Override
  public SimpleImage createKernelVisualisation(int pWidth, int pHeight) {
    SimpleImage img = new SimpleImage(pWidth, pHeight);
    if (noiseFilterSize > 0) {
      Graphics g = img.getGraphics();
      int rectCount = noiseFilterSize;
      int rectWidth = (pWidth - borderWidth * (rectCount + 1)) / rectCount;
      int rectHeight = (pHeight - borderWidth * (rectCount + 1)) / rectCount;
      int xOff, yOff = 0;
      for (int i = 0; i < noiseFilterSize; i++) {
        xOff = 0;
        for (int j = 0; j < noiseFilterSize; j++) {
          double fValue = filter[i][j];
          g.setColor(borderColor);
          g.fillRect(xOff, yOff, rectWidth + 1, rectHeight + 1);
          Color rectColor;
          if (fValue >= 0) {
            int fValueClr = Tools.FTOI(255.0 * fValue);
            if (fValueClr > 255) {
              fValueClr = 255;
            }
            rectColor = new Color(fValueClr, fValueClr, fValueClr);
          }
          else {
            int fValueClr = Tools.FTOI(-255.0 * (fValue - 0.5));
            if (fValueClr > 255) {
              fValueClr = 255;
            }
            rectColor = new Color(fValueClr, 0, 0);
          }
          g.setColor(rectColor);
          g.fillRect(xOff + 1, yOff + 1, rectWidth, rectHeight);
          xOff += rectWidth + 1;
        }
        yOff += rectHeight + 1;
      }
    }
    else {
      img.fillBackground(emptyFilterColor.getRed(), emptyFilterColor.getGreen(), emptyFilterColor.getBlue());
    }
    return img;
  }
}
