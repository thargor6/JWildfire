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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.NonHDRImageBufferComboBoxEditor;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;


public class CloudCreator extends ImageCreator {
  public enum Mode {
    CLOUDS, PLASMA
  }

  public enum ColorMode {
    GREY, COLOR, BASE_COLOR
  }

  public enum BGMode {
    COLOR, IMAGE
  }

  @Property(description = "Cloud coverage")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  private double cover = 0.5;

  @Property(description = "Cloud sharpness")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  private double sharpness = 0.975;

  @Property(description = "Octave persistence")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  private double persistence = 0.45;

  @Property(description = "Initial frequency (size of the pixel map)")
  @PropertyMin(2)
  private int initialFrequency = 5;

  @Property(description = "Number of octaves")
  @PropertyMin(2)
  private int octaves = 7;

  @Property(description = "Seed for the random number generator")
  @PropertyMin(0)
  private int seed = 123;

  @Property(description = "Cloud colors", editorClass = ColorModeEditor.class)
  private ColorMode colorMode = ColorMode.GREY;

  @Property(description = "Cloud base color (colorMode=BASE_COLOR)")
  private Color cloudColor = new Color(255, 51, 0);

  @Property(description = "Mode", editorClass = ModeEditor.class)
  private Mode mode = Mode.CLOUDS;

  @Property(description = "Background image (mode==CLOUDS)", editorClass = NonHDRImageBufferComboBoxEditor.class)
  private Buffer backgroundImg;

  @Property(description = "Background mode (mode==CLOUDS)", editorClass = BGModeEditor.class)
  private BGMode bgMode = BGMode.COLOR;

  @Property(description = "Background color (bgMode=COLOR)")
  private Color bgColor = new Color(153, 186, 255);

  @Override
  protected void fillImage(SimpleImage res) {
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    Random rnd = new Random();
    rnd.setSeed(this.seed);
    double cover = 1.0 - this.cover;
    double aspect = (double) width / (double) height;
    int frequency = this.initialFrequency;
    int frequencyX = (int) (frequency * aspect + 0.5);
    int frequencyY = (int) (frequency * aspect + 0.5);
    double alphaInt = 1.0f;
    BufferedImage mainImage = res.getBufferedImg();
    Graphics2D mainGr = mainImage.createGraphics();

    for (int i = 0; i < this.octaves; i++) {
      // create a small random image 
      BufferedImage rndMap = new BufferedImage(frequencyX, frequencyY, BufferedImage.TYPE_INT_ARGB);
      {
        Graphics2D g = rndMap.createGraphics();
        try {
          switch (colorMode) {
            case COLOR:
              for (int x = 0; x < frequencyX; x++) {
                for (int y = 0; y < frequencyY; y++) {
                  int rVal = rnd.nextInt(255);
                  int gVal = rnd.nextInt(255);
                  int bVal = rnd.nextInt(255);
                  g.setColor(new Color(rVal, gVal, bVal, (int) (255.0 * alphaInt + 0.5)));
                  g.fillRect(x, y, 1, 1);
                }
              }
              break;
            case GREY:
              for (int x = 0; x < frequencyX; x++) {
                for (int y = 0; y < frequencyY; y++) {
                  int val = rnd.nextInt(255);
                  g.setColor(new Color(val, val, val, (int) (255.0 * alphaInt + 0.5)));
                  g.fillRect(x, y, 1, 1);
                }
              }
              break;
            case BASE_COLOR:
              int rBase = this.cloudColor.getRed();
              int gBase = this.cloudColor.getGreen();
              int bBase = this.cloudColor.getBlue();
              for (int x = 0; x < frequencyX; x++) {
                for (int y = 0; y < frequencyY; y++) {
                  int val = rnd.nextInt(255);
                  int rVal = (rBase * val) / 255;
                  int gVal = (gBase * val) / 255;
                  int bVal = (bBase * val) / 255;
                  g.setColor(new Color(rVal, gVal, bVal, (int) (255.0 * alphaInt + 0.5)));
                  g.fillRect(x, y, 1, 1);
                }
              }
              break;
          }
        }
        finally {
          g.dispose();
        }
      }
      // scale up the image using Java-built-in interpolation
      {
        BufferedImage scaledRndMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledRndMap.createGraphics();
        try {
          g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
              RenderingHints.VALUE_INTERPOLATION_BICUBIC);
          g.drawImage(rndMap, 0, 0, width, height, 0, 0, frequencyX, frequencyY, null);
        }
        finally {
          g.dispose();
        }
        mainGr.drawImage(scaledRndMap, null, 0, 0);
      }
      alphaInt *= this.persistence;
      frequency += frequency;
      frequencyX = (int) (frequency * aspect + 0.5);
      frequencyY = (int) (frequency * aspect + 0.5);
    }
    // apply an exponential filter to let the noise more look like clouds
    if (mode == Mode.CLOUDS) {
      final double rWeight = 0.2990;
      final double gWeight = 0.5880;
      final double bWeight = 0.1130;
      SimpleImage bgImg = (this.backgroundImg != null) ? backgroundImg.getImage() : null;
      Pixel pixel = new Pixel();
      Pixel bgPixel = new Pixel();
      for (int i = 0; i < mainImage.getWidth(); i++) {
        for (int j = 0; j < mainImage.getHeight(); j++) {
          pixel.setARGBValue(res.getARGBValue(i, j));
          double lum = pixel.r * rWeight + pixel.g * gWeight + pixel.b * bWeight;
          double c = lum - (cover * 255);
          if (c < 0)
            c = 0;
          int iVal = Tools.roundColor(255.0 - (Math.pow(this.sharpness, c) * 255.0));
          int bgRed = 0, bgGreen = 0, bgBlue = 0;
          switch (bgMode) {
            case IMAGE:
              if (bgImg != null) {
                bgPixel.setARGBValue(bgImg.getARGBValueIgnoreBounds(i, j));
                bgRed = bgPixel.r;
                bgGreen = bgPixel.g;
                bgBlue = bgPixel.b;
              }
              break;
            case COLOR:
              bgRed = this.bgColor.getRed();
              bgGreen = this.bgColor.getGreen();
              bgBlue = this.bgColor.getBlue();
              break;
          }
          switch (colorMode) {
            case GREY:
              pixel.r = expose(iVal + 1.5 * bgRed);
              pixel.g = expose(iVal + 1.5 * bgGreen);
              pixel.b = expose(iVal + 1.5 * bgBlue);
              break;
            default:
              pixel.r = expose((iVal * pixel.r) / 255 + 1.5 * bgRed);
              pixel.g = expose((iVal * pixel.g) / 255 + 1.5 * bgGreen);
              pixel.b = expose((iVal * pixel.b) / 255 + 1.5 * bgBlue);
              break;
          }
          res.setRGB(i, j, pixel);
        }
      }
    }
  }

  public double getCover() {
    return cover;
  }

  public void setCover(double cover) {
    this.cover = cover;
  }

  public double getSharpness() {
    return sharpness;
  }

  public void setSharpness(double sharpness) {
    this.sharpness = sharpness;
  }

  public double getPersistence() {
    return persistence;
  }

  public void setPersistence(double persistence) {
    this.persistence = persistence;
  }

  public int getInitialFrequency() {
    return initialFrequency;
  }

  public void setInitialFrequency(int initialFrequency) {
    this.initialFrequency = initialFrequency;
  }

  public int getOctaves() {
    return octaves;
  }

  public void setOctaves(int octaves) {
    this.octaves = octaves;
  }

  public int getSeed() {
    return seed;
  }

  public void setSeed(int seed) {
    this.seed = seed;
  }

  private int expose(double light) {
    double exposure = -0.002;
    return Tools.roundColor((1.0 - Math.exp(light * exposure)) * 255.0 + 100);
  }

  public static class ColorModeEditor extends ComboBoxPropertyEditor {
    public ColorModeEditor() {
      super();
      setAvailableValues(new ColorMode[] { ColorMode.GREY, ColorMode.COLOR, ColorMode.BASE_COLOR });
    }
  }

  public static class BGModeEditor extends ComboBoxPropertyEditor {
    public BGModeEditor() {
      super();
      setAvailableValues(new BGMode[] { BGMode.COLOR, BGMode.IMAGE });
    }
  }

  public static class ModeEditor extends ComboBoxPropertyEditor {
    public ModeEditor() {
      super();
      setAvailableValues(new Mode[] { Mode.CLOUDS, Mode.PLASMA });
    }
  }

  public ColorMode getColorMode() {
    return colorMode;
  }

  public void setColorMode(ColorMode colorMode) {
    this.colorMode = colorMode;
  }

  public Color getCloudColor() {
    return cloudColor;
  }

  public void setCloudColor(Color cloudColor) {
    this.cloudColor = cloudColor;
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public Buffer getBackgroundImg() {
    return backgroundImg;
  }

  public void setBackgroundImg(Buffer backgroundImg) {
    this.backgroundImg = backgroundImg;
  }

  public BGMode getBgMode() {
    return bgMode;
  }

  public void setBgMode(BGMode bgMode) {
    this.bgMode = bgMode;
  }

  public Color getBgColor() {
    return bgColor;
  }

  public void setBgColor(Color bgColor) {
    this.bgColor = bgColor;
  }

}
