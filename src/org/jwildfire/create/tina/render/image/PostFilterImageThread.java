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

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

public class PostFilterImageThread extends AbstractImageRenderThread {
  private final int startRow, endRow;
  private final SimpleImage input, output;
  private final boolean showHits;
  private final Pixel toolPixel1 = new Pixel();
  private final Pixel toolPixel2 = new Pixel();
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
        PixelValue pCenter = getPixel(input, toolPixel1, x, y);
        PixelValue pLeft = getPixel(input, toolPixel1, x - 1, y);
        PixelValue pRight = getPixel(input, toolPixel1, x + 1, y);
        PixelValue pUp = getPixel(input, toolPixel1, x, y - 1);
        PixelValue pDown = getPixel(input, toolPixel1, x, y + 1);
        boolean left = hasHighContrast(pCenter, pLeft);
        boolean right = hasHighContrast(pCenter, pRight);
        boolean up = hasHighContrast(pCenter, pUp);
        boolean down = hasHighContrast(pCenter, pDown);
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

  private boolean hasHighContrast(PixelValue pA, PixelValue pB) {
    toolPixel1.setARGBValue(pA.argb);
    toolPixel2.setARGBValue(pB.argb);
    double dist = MathLib.sqrt(MathLib.sqr(toolPixel1.r - toolPixel2.r) + MathLib.sqr(toolPixel1.g - toolPixel2.g) + MathLib.sqr(toolPixel1.b - toolPixel2.b)) / 255.0;
    return dist > threshold;
    //    return MathLib.fabs(pA.luminosity - pB.luminosity) > threshold;
  }

  //  public void doFilter4() {
  //    hits = 0;
  //    for (int y = startRow; y < endRow; y++) {
  //      for (int x = 0; x < input.getImageWidth(); x++) {
  //        PixelValue pCenter = getPixel(input, toolPixel1, x, y);
  //        PixelValue pLeft = getPixel(input, toolPixel1, x - 1, y);
  //        if (hasHighContrast(pCenter, pLeft)) {
  //          PixelValue pRight = getPixel(input, toolPixel1, x + 1, y);
  //          if (hasHighContrast(pCenter, pRight)) {
  //            PixelValue pUp = getPixel(input, toolPixel1, x, y - 1);
  //            if (hasHighContrast(pCenter, pUp)) {
  //              PixelValue pDown = getPixel(input, toolPixel1, x, y + 1);
  //              if (hasHighContrast(pCenter, pDown)) {
  //                hits++;
  //                if (showHits) {
  //                  output.setARGB(x, y, 255, 0, 255, 0);
  //                  if (x < input.getImageWidth() - 1)
  //                    output.setARGB(x + 1, y, 255, 0, 255, 0);
  //                  if (x > 0)
  //                    output.setARGB(x - 1, y, 255, 0, 255, 0);
  //                  if (y < input.getImageHeight() - 1)
  //                    output.setARGB(x, y + 1, 255, 0, 255, 0);
  //                  if (y > 0)
  //                    output.setARGB(x, y - 1, 255, 0, 255, 0);
  //                }
  //                else {
  //                  doMedian4(y, x, pCenter, pLeft, pRight, pUp, pDown);
  //                }
  //              }
  //            }
  //          }
  //        }
  //
  //      }
  //    }
  //  }

  //  private void doMedian8(int y, int x, PixelValue pCenter, PixelValue pLeft, PixelValue pRight, PixelValue pUp, PixelValue pDown) {
  //    List<PixelValue> pixels = new ArrayList<PixelValue>();
  //    pixels.add(pCenter);
  //    pixels.add(pLeft);
  //    pixels.add(pRight);
  //    pixels.add(pUp);
  //    pixels.add(pDown);
  //    pixels.add(getPixel(input, toolPixel1, x + 1, y + 1));
  //    pixels.add(getPixel(input, toolPixel1, x + 1, y - 1));
  //    pixels.add(getPixel(input, toolPixel1, x - 1, y + 1));
  //    pixels.add(getPixel(input, toolPixel1, x - 1, y - 1));
  //    Collections.sort(pixels, new Comparator<PixelValue>() {
  //
  //      @Override
  //      public int compare(PixelValue o1, PixelValue o2) {
  //        if (o1.luminosity > o2.luminosity) {
  //          return 1;
  //        }
  //        else if (o1.luminosity < o2.luminosity) {
  //          return -1;
  //        }
  //        else {
  //          return 0;
  //        }
  //      }
  //    });
  //    PixelValue newCentre = pixels.get(pixels.size() / 2);
  //    output.setARGB(x, y, newCentre.argb);
  //  }

  private void doGaussian8(int y, int x, PixelValue pCenter, PixelValue pLeft, PixelValue pRight, PixelValue pUp, PixelValue pDown) {
    double w0 = 0.32;
    double w1 = 0.11;
    double w2 = 0.06;

    PixelValue p1 = getPixel(input, toolPixel1, x + 1, y + 1);
    PixelValue p2 = getPixel(input, toolPixel1, x + 1, y - 1);
    PixelValue p3 = getPixel(input, toolPixel1, x - 1, y + 1);
    PixelValue p4 = getPixel(input, toolPixel1, x - 1, y - 1);

    double r = toolPixel1.setARGBValue(pCenter.argb).getR() * w0 +
        (toolPixel1.setARGBValue(pLeft.argb).getR() + toolPixel1.setARGBValue(pRight.argb).getR() + toolPixel1.setARGBValue(pUp.argb).getR() + toolPixel1.setARGBValue(pDown.argb).getR()) * w1 +
        (toolPixel1.setARGBValue(p1.argb).getR() + toolPixel1.setARGBValue(p2.argb).getR() + toolPixel1.setARGBValue(p3.argb).getR() + toolPixel1.setARGBValue(p4.argb).getR()) * w2;

    double g = toolPixel1.setARGBValue(pCenter.argb).getG() * w0 +
        (toolPixel1.setARGBValue(pLeft.argb).getG() + toolPixel1.setARGBValue(pRight.argb).getG() + toolPixel1.setARGBValue(pUp.argb).getG() + toolPixel1.setARGBValue(pDown.argb).getG()) * w1 +
        (toolPixel1.setARGBValue(p1.argb).getG() + toolPixel1.setARGBValue(p2.argb).getG() + toolPixel1.setARGBValue(p3.argb).getG() + toolPixel1.setARGBValue(p4.argb).getG()) * w2;
    double b = toolPixel1.setARGBValue(pCenter.argb).getR() * w0 +
        (toolPixel1.setARGBValue(pLeft.argb).getB() + toolPixel1.setARGBValue(pRight.argb).getB() + toolPixel1.setARGBValue(pUp.argb).getB() + toolPixel1.setARGBValue(pDown.argb).getB()) * w1 +
        (toolPixel1.setARGBValue(p1.argb).getB() + toolPixel1.setARGBValue(p2.argb).getB() + toolPixel1.setARGBValue(p3.argb).getB() + toolPixel1.setARGBValue(p4.argb).getB()) * w2;

    toolPixel1.setARGB(255, Tools.roundColor(r), Tools.roundColor(g), Tools.roundColor(b));

    output.setARGB(x, y, toolPixel1.getARGBValue());
  }

  private PixelValue getPixel(SimpleImage img, Pixel rgbPixel, int x, int y) {
    int argb = img.getARGBValueIgnoreBounds(x, y);
    rgbPixel.setARGBValue(argb);
    return new PixelValue(argb, (rgbPixel.r * 0.299 + rgbPixel.g * 0.5880 + rgbPixel.b * 0.1130) / 255.0);
  }

}
