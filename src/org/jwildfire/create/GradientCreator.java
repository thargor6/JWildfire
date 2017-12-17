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
package org.jwildfire.create;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jwildfire.base.Property;
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

public class GradientCreator extends ImageCreator {

  @Property(description = "Color of upper left edge")
  Color ulColor = new Color(255, 0, 0);
  @Property(description = "Color of upper right edge")
  Color urColor = new Color(255, 255, 0);
  @Property(description = "Color of lower left edge")
  Color llColor = new Color(0, 255, 255);
  @Property(description = "Color of lower right edge")
  Color lrColor = new Color(0, 0, 255);
  @Property(description = "Filename of a gradient file")
  private String gradientFilename = "";

  @Override
  public void fillImage(SimpleImage res) {
    if ((gradientFilename == null) || (gradientFilename.length() == 0)) {
      boolean simpleImg = ulColor.equals(urColor) && urColor.equals(llColor)
          && llColor.equals(lrColor) && lrColor.equals(ulColor);
      if (simpleImg) {
        Graphics g = res.getBufferedImg().getGraphics();
        g.setColor(ulColor);
        g.fillRect(0, 0, res.getImageWidth(), res.getImageHeight());
      }
      else {
        // UL 0
        // UR 1
        // LL 2
        // LR 3
        final int SPREC = 10;
        int width = res.getImageWidth();
        int height = res.getImageHeight();
        /*left line */
        int lmr = ((int) (llColor.getRed() - ulColor.getRed()) << SPREC) / (int) (height - 1);
        int lmg = ((int) (llColor.getGreen() - ulColor.getGreen()) << SPREC) / (int) (height - 1);
        int lmb = ((int) (llColor.getBlue() - ulColor.getBlue()) << SPREC) / (int) (height - 1);
        int lnr = ulColor.getRed();
        int lng = ulColor.getGreen();
        int lnb = ulColor.getBlue();
        /* right line */
        int rmr = ((int) (lrColor.getRed() - urColor.getRed()) << SPREC) / (int) (height - 1);
        int rmg = ((int) (lrColor.getGreen() - urColor.getGreen()) << SPREC) / (int) (height - 1);
        int rmb = ((int) (lrColor.getBlue() - urColor.getBlue()) << SPREC) / (int) (height - 1);
        int rnr = urColor.getRed();
        int rng = urColor.getGreen();
        int rnb = urColor.getBlue();

        for (int i = 0; i < height; i++) {
          /* interpolate left and right border */
          int lr, lg, lb, rr, rg, rb;
          if (i == 0) {
            lr = ulColor.getRed();
            lg = ulColor.getGreen();
            lb = ulColor.getBlue();
            rr = urColor.getRed();
            rg = urColor.getGreen();
            rb = urColor.getBlue();
          }
          else if (i == (height - 1)) {
            lr = llColor.getRed();
            lg = llColor.getGreen();
            lb = llColor.getBlue();
            rr = lrColor.getRed();
            rg = lrColor.getGreen();
            rb = lrColor.getBlue();
          }
          else {
            lr = (int) (((int) i * lmr) >> SPREC) + lnr;
            lg = (int) (((int) i * lmg) >> SPREC) + lng;
            lb = (int) (((int) i * lmb) >> SPREC) + lnb;
            rr = (int) (((int) i * rmr) >> SPREC) + rnr;
            rg = (int) (((int) i * rmg) >> SPREC) + rng;
            rb = (int) (((int) i * rmb) >> SPREC) + rnb;
          }
          /* create the horizontal line */
          int hmr = ((int) (rr - lr) << SPREC) / (int) (width - 1);
          int hmg = ((int) (rg - lg) << SPREC) / (int) (width - 1);
          int hmb = ((int) (rb - lb) << SPREC) / (int) (width - 1);
          int hnr = lr;
          int hng = lg;
          int hnb = lb;

          /* 1st pixel of the line */
          res.setRGB(0, i, lr, lg, lb);
          int thr = 0, thg = 0, thb = 0;
          for (int j = 1; j < (width - 1); j++) {
            thr += hmr;
            thg += hmg;
            thb += hmb;
            int rval = (int) (thr >> SPREC) + hnr;
            int gval = (int) (thg >> SPREC) + hng;
            int bval = (int) (thb >> SPREC) + hnb;
            res.setRGB(j, i, rval, gval, bval);
          }
          /* last pixel of the line */
          res.setRGB(width - 1, i, rr, rg, rb);
        }
      }
    }
    else {
      int width = res.getImageWidth();
      int height = res.getImageHeight();
      String content;
      try {
        content = Tools.readUTF8Textfile(gradientFilename);
      }
      catch (Exception ex) {
        ex.printStackTrace();
        throw new RuntimeException(ex);
      }
      String rows[] = content.split("[\\r\\n]+");

      // index=257 color=4273232      
      Pattern pattern = Pattern.compile("(\\s*)(index=)([0-9]+)(\\s+)(color=)([0-9]+)(\\s*)");
      Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
      int maxIdx = 0;
      for (String row : rows) {
        Matcher matcher = pattern.matcher(row);
        if (matcher.find()) {
          //          System.out.println(matcher.group(3) + "/" + matcher.group(6));
          int idx = Integer.parseInt(matcher.group(3));
          if (idx > maxIdx)
            maxIdx = idx;
          colorMap.put(idx, Integer.parseInt(matcher.group(6)));
        }
      }
      Integer lastColor = 0;
      Pixel toolPixel = new Pixel();
      for (int i = 0; i < maxIdx; i++) {
        if (i < width) {
          Integer color = colorMap.get(i);
          if (color == null) {
            color = lastColor;
          }
          else {
            lastColor = color;
          }
          toolPixel.setARGBValue(color);
          for (int j = 0; j < height; j++) {
            // is bgr, not rgb
            res.setRGB(i, j, toolPixel.b, toolPixel.g, toolPixel.r);
          }
        }
      }

    }
  }

  public Color getUlColor() {
    return ulColor;
  }

  public void setUlColor(Color ulColor) {
    this.ulColor = ulColor;
  }

  public Color getUrColor() {
    return urColor;
  }

  public void setUrColor(Color urColor) {
    this.urColor = urColor;
  }

  public Color getLlColor() {
    return llColor;
  }

  public void setLlColor(Color llColor) {
    this.llColor = llColor;
  }

  public Color getLrColor() {
    return lrColor;
  }

  public void setLrColor(Color lrColor) {
    this.lrColor = lrColor;
  }

  public String getGradientFilename() {
    return gradientFilename;
  }

  public void setGradientFilename(String gradientFilename) {
    this.gradientFilename = gradientFilename;
  }
}
