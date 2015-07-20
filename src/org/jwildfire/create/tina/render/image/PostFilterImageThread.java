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
package org.jwildfire.create.tina.render.image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

public class PostFilterImageThread extends AbstractImageRenderThread {
  private final int startRow, endRow;
  private final SimpleImage input, output;
  private final boolean showHits;
  private final Pixel toolPixel = new Pixel();
  private final double threshold;
  int hits = 0;

  public static class PixelValue {
    public int argb;
    public double luminosity;

    public PixelValue(int pArgb, double pLuminosity) {
      argb = pArgb;
      luminosity = pLuminosity;
    }
  }

  public PostFilterImageThread(int pStartRow, int pEndRow, SimpleImage pInput, SimpleImage pOutput, double pThreshold) {
    input = pInput;
    output = pOutput;
    startRow = pStartRow;
    endRow = pEndRow;
    threshold = -MathLib.log(pThreshold) / 2.0;
    showHits = Prefs.getPrefs().isDevelopmentMode();
  }

  @Override
  public void run() {
    setDone(false);
    try {
      doFilter3();
    }
    finally {
      setDone(true);
    }
  }

  private void doFilter3() {
    hits = 0;
    for (int y = startRow; y < endRow; y++) {
      for (int x = 0; x < input.getImageWidth(); x++) {
        PixelValue pCenter = getPixel(input, toolPixel, x, y);
        PixelValue pLeft = getPixel(input, toolPixel, x - 1, y);
        PixelValue pRight = getPixel(input, toolPixel, x + 1, y);
        PixelValue pUp = getPixel(input, toolPixel, x, y - 1);
        PixelValue pDown = getPixel(input, toolPixel, x, y + 1);
        boolean left = MathLib.fabs(pCenter.luminosity - pLeft.luminosity) > threshold;
        boolean right = MathLib.fabs(pCenter.luminosity - pRight.luminosity) > threshold;
        boolean up = MathLib.fabs(pCenter.luminosity - pUp.luminosity) > threshold;
        boolean down = MathLib.fabs(pCenter.luminosity - pDown.luminosity) > threshold;
        if ((left && right && (up || down)) || (up && down || (left || right))) {
          hits++;
          if (showHits) {
            output.setARGB(x, y, 255, 0, 255, 0);
            if (x < input.getImageWidth() - 1)
              output.setARGB(x + 1, y, 255, 0, 255, 0);
            if (x > 0)
              output.setARGB(x - 1, y, 255, 0, 255, 0);
            if (y < input.getImageHeight() - 1)
              output.setARGB(x, y + 1, 255, 0, 255, 0);
            if (y > 0)
              output.setARGB(x, y - 1, 255, 0, 255, 0);
          }
          else {
            doGaussian8(y, x, pCenter, pLeft, pRight, pUp, pDown);
          }
        }
      }
    }
  }

  public void doFilter4() {
    hits = 0;
    for (int y = startRow; y < endRow; y++) {
      for (int x = 0; x < input.getImageWidth(); x++) {
        PixelValue pCenter = getPixel(input, toolPixel, x, y);
        PixelValue pLeft = getPixel(input, toolPixel, x - 1, y);
        if (MathLib.fabs(pCenter.luminosity - pLeft.luminosity) > threshold) {
          PixelValue pRight = getPixel(input, toolPixel, x + 1, y);
          if (MathLib.fabs(pCenter.luminosity - pRight.luminosity) > threshold) {
            PixelValue pUp = getPixel(input, toolPixel, x, y - 1);
            if (MathLib.fabs(pCenter.luminosity - pUp.luminosity) > threshold) {
              PixelValue pDown = getPixel(input, toolPixel, x, y + 1);
              if (MathLib.fabs(pCenter.luminosity - pDown.luminosity) > threshold) {
                hits++;
                if (showHits) {
                  output.setARGB(x, y, 255, 0, 255, 0);
                  if (x < input.getImageWidth() - 1)
                    output.setARGB(x + 1, y, 255, 0, 255, 0);
                  if (x > 0)
                    output.setARGB(x - 1, y, 255, 0, 255, 0);
                  if (y < input.getImageHeight() - 1)
                    output.setARGB(x, y + 1, 255, 0, 255, 0);
                  if (y > 0)
                    output.setARGB(x, y - 1, 255, 0, 255, 0);
                }
                else {
                  doMedian4(y, x, pCenter, pLeft, pRight, pUp, pDown);
                }
              }
            }
          }
        }

      }
    }
  }

  private void doMedian8(int y, int x, PixelValue pCenter, PixelValue pLeft, PixelValue pRight, PixelValue pUp, PixelValue pDown) {
    List<PixelValue> pixels = new ArrayList<PixelValue>();
    pixels.add(pCenter);
    pixels.add(pLeft);
    pixels.add(pRight);
    pixels.add(pUp);
    pixels.add(pDown);
    pixels.add(getPixel(input, toolPixel, x + 1, y + 1));
    pixels.add(getPixel(input, toolPixel, x + 1, y - 1));
    pixels.add(getPixel(input, toolPixel, x - 1, y + 1));
    pixels.add(getPixel(input, toolPixel, x - 1, y - 1));
    Collections.sort(pixels, new Comparator<PixelValue>() {

      @Override
      public int compare(PixelValue o1, PixelValue o2) {
        if (o1.luminosity > o2.luminosity) {
          return 1;
        }
        else if (o1.luminosity < o2.luminosity) {
          return -1;
        }
        else {
          return 0;
        }
      }
    });
    PixelValue newCentre = pixels.get(pixels.size() / 2);
    output.setARGB(x, y, newCentre.argb);
  }

  private void doGaussian8(int y, int x, PixelValue pCenter, PixelValue pLeft, PixelValue pRight, PixelValue pUp, PixelValue pDown) {
    double w0 = 0.32;
    double w1 = 0.11;
    double w2 = 0.06;

    PixelValue p1 = getPixel(input, toolPixel, x + 1, y + 1);
    PixelValue p2 = getPixel(input, toolPixel, x + 1, y - 1);
    PixelValue p3 = getPixel(input, toolPixel, x - 1, y + 1);
    PixelValue p4 = getPixel(input, toolPixel, x - 1, y - 1);

    double r = toolPixel.setARGBValue(pCenter.argb).getR() * w0 +
        (toolPixel.setARGBValue(pLeft.argb).getR() + toolPixel.setARGBValue(pRight.argb).getR() + toolPixel.setARGBValue(pUp.argb).getR() + toolPixel.setARGBValue(pDown.argb).getR()) * w1 +
        (toolPixel.setARGBValue(p1.argb).getR() + toolPixel.setARGBValue(p2.argb).getR() + toolPixel.setARGBValue(p3.argb).getR() + toolPixel.setARGBValue(p4.argb).getR()) * w2;

    double g = toolPixel.setARGBValue(pCenter.argb).getG() * w0 +
        (toolPixel.setARGBValue(pLeft.argb).getG() + toolPixel.setARGBValue(pRight.argb).getG() + toolPixel.setARGBValue(pUp.argb).getG() + toolPixel.setARGBValue(pDown.argb).getG()) * w1 +
        (toolPixel.setARGBValue(p1.argb).getG() + toolPixel.setARGBValue(p2.argb).getG() + toolPixel.setARGBValue(p3.argb).getG() + toolPixel.setARGBValue(p4.argb).getG()) * w2;
    double b = toolPixel.setARGBValue(pCenter.argb).getR() * w0 +
        (toolPixel.setARGBValue(pLeft.argb).getB() + toolPixel.setARGBValue(pRight.argb).getB() + toolPixel.setARGBValue(pUp.argb).getB() + toolPixel.setARGBValue(pDown.argb).getB()) * w1 +
        (toolPixel.setARGBValue(p1.argb).getB() + toolPixel.setARGBValue(p2.argb).getB() + toolPixel.setARGBValue(p3.argb).getB() + toolPixel.setARGBValue(p4.argb).getB()) * w2;

    toolPixel.setARGB(255, Tools.roundColor(r), Tools.roundColor(g), Tools.roundColor(b));

    output.setARGB(x, y, toolPixel.getARGBValue());
  }

  private void doGaussian4(int y, int x, PixelValue pCenter, PixelValue pLeft, PixelValue pRight, PixelValue pUp, PixelValue pDown) {
    double w0 = 0.36;
    double w1 = 0.16;

    double r = toolPixel.setARGBValue(pCenter.argb).getR() * w0 +
        (toolPixel.setARGBValue(pLeft.argb).getR() + toolPixel.setARGBValue(pRight.argb).getR() + toolPixel.setARGBValue(pUp.argb).getR() + toolPixel.setARGBValue(pDown.argb).getR()) * w1;

    double g = toolPixel.setARGBValue(pCenter.argb).getG() * w0 +
        (toolPixel.setARGBValue(pLeft.argb).getG() + toolPixel.setARGBValue(pRight.argb).getG() + toolPixel.setARGBValue(pUp.argb).getG() + toolPixel.setARGBValue(pDown.argb).getG()) * w1;
    double b = toolPixel.setARGBValue(pCenter.argb).getR() * w0 +
        (toolPixel.setARGBValue(pLeft.argb).getB() + toolPixel.setARGBValue(pRight.argb).getB() + toolPixel.setARGBValue(pUp.argb).getB() + toolPixel.setARGBValue(pDown.argb).getB()) * w1;

    toolPixel.setARGB(255, Tools.roundColor(r), Tools.roundColor(g), Tools.roundColor(b));

    output.setARGB(x, y, toolPixel.getARGBValue());
  }

  private void doMedian4(int y, int x, PixelValue pCenter, PixelValue pLeft, PixelValue pRight, PixelValue pUp, PixelValue pDown) {
    List<PixelValue> pixels = new ArrayList<PixelValue>();
    pixels.add(pCenter);
    pixels.add(pLeft);
    pixels.add(pRight);
    pixels.add(pUp);
    pixels.add(pDown);
    Collections.sort(pixels, new Comparator<PixelValue>() {

      @Override
      public int compare(PixelValue o1, PixelValue o2) {
        if (o1.luminosity > o2.luminosity) {
          return 1;
        }
        else if (o1.luminosity < o2.luminosity) {
          return -1;
        }
        else {
          return 0;
        }
      }
    });
    PixelValue newCentre = pixels.get(pixels.size() / 2);
    output.setARGB(x, y, newCentre.argb);
  }

  private PixelValue getPixel(SimpleImage img, Pixel rgbPixel, int x, int y) {
    int argb = img.getARGBValueIgnoreBounds(x, y);
    rgbPixel.setARGBValue(argb);
    return new PixelValue(argb, (rgbPixel.r * 0.299 + rgbPixel.g * 0.5880 + rgbPixel.b * 0.1130) / 255.0);
  }

}
