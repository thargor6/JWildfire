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

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathparser.JEPWrapper;
import org.jwildfire.image.SimpleImage;
import org.nfunk.jep.Node;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;


public class PerlinNoiseCreator extends ImageCreator {
  public enum ColorMode {
    CUSTOM, RANDOM, GREY
  }

  public enum Shape {
    CLASSIC, RANDOM
  }

  @Property(description = "Seed for the random number generator (zero to produce totally random results)")
  @PropertyMin(0)
  private int seed = 123;

  @Property(description = "Octave persistence")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  private double persistence = 0.3;

  @Property(description = "Number of octaves")
  @PropertyMin(12)
  private int octaves = 7;

  @Property(description = "Initial frequency")
  @PropertyMin(0.0)
  private double initialFrequency = 5.0;

  @Property(description = "Frequency modifier")
  @PropertyMin(0.0)
  private double frequencyMultiplier = 2.5;

  @Property(description = "Low color")
  private Color lowColor = new Color(20, 50, 200);

  @Property(description = "High color")
  private Color highColor = new Color(200, 220, 250);

  @Property(description = "Color mode", editorClass = ColorModeEditor.class)
  private ColorMode colorMode = ColorMode.CUSTOM;

  @Property(description = "Texture shape", editorClass = ShapeEditor.class)
  private Shape shape = Shape.RANDOM;

  @Property(description = "Transformation on/off")
  private boolean transform = false;

  @Property(description = "Transformation expression")
  private String transformation = "sin(x*x)";

  @Override
  protected void fillImage(SimpleImage res) {
    if (this.seed != 0)
      Tools.srand123(this.seed);
    JEPWrapper parser = null;
    Node node = null;
    if (transform) {
      parser = new JEPWrapper();
      parser.addVariable("x", 0.0);
      node = parser.parse(transformation);
    }

    switch (colorMode) {
      case RANDOM:
        cA.randomize();
        cB.randomize();
        break;
      case CUSTOM:
        cA.setR(lowColor.getRed());
        cA.setG(lowColor.getGreen());
        cA.setB(lowColor.getBlue());
        cB.setR(highColor.getRed());
        cB.setG(highColor.getGreen());
        cB.setB(highColor.getBlue());
        break;
      case GREY:
    	double dA= Tools.drand();
    	cA.setR(dA);
    	cA.setG(dA);
    	cA.setB(dA);
    	double dB= Tools.drand();
    	cB.setR(dB);
    	cB.setG(dB);
    	cB.setB(dB);
    	break;
    }
    initShape();
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    for (int i = 0; i < height; i++) {
      double y = (double) i / (double) height;
      for (int j = 0; j < width; j++) {
        double x = (double) j / (double) width;

        double freq = initialFrequency;
        double alphaInt = 1.0;
        double grayValue = 0.0;
        for (int l = 0; l < octaves; l++) {
          double noiseValue = noise(x * freq, y * freq, 0.0);
          if (transform) {
            parser.setVarValue("x", noiseValue);
            noiseValue = (Double) parser.evaluate(node);
          }
          grayValue += alphaInt * noiseValue;
          freq *= frequencyMultiplier;
          alphaInt *= persistence;
        }
        RGBVal color = getColor(grayValue);
        fSetPixel(res, j, i, color);
      }
    }
  }

  private RGBVal cA = new RGBVal();
  private RGBVal cB = new RGBVal();

  private RGBVal getColor(double pGrayValue) {
    if (pGrayValue < 0.0)
      pGrayValue = 0 - pGrayValue;
    if (pGrayValue < 0.0001)
      pGrayValue = 0.0001;
    else if (pGrayValue > 0.9999)
      pGrayValue = 0.9999;
    RGBVal res = new RGBVal();
    if (colorMode == ColorMode.GREY) {
      res.setR(pGrayValue);
      res.setG(pGrayValue);
      res.setB(pGrayValue);
    }
    else {
      res.setR(cA.getR() * (1.0 - pGrayValue) + cB.getR() * pGrayValue);
      res.setG(cA.getG() * (1.0 - pGrayValue) + cB.getG() * pGrayValue);
      res.setB(cA.getB() * (1.0 - pGrayValue) + cB.getB() * pGrayValue);
    }

    return res;
  }

  private static class RGBVal {
    private double r;
    private double g;
    private double b;

    public double getR() {
      return r;
    }

    public void randomize() {
      r = Tools.drand();
      g = Tools.drand();
      b = Tools.drand();
    }

    public double getG() {
      return g;
    }

    public double getB() {
      return b;
    }

    public void setR(int r) {
      this.r = (double) r / 255.0;
    }

    public void setR(double r) {
      this.r = r;
    }

    public void setG(double g) {
      this.g = g;
    }

    public void setG(int g) {
      this.g = (double) g / 255.0;
      ;
    }

    public void setB(double b) {
      this.b = b;
    }

    public void setB(int b) {
      this.b = (double) b / 255.0;
    }
  }

  private void fSetPixel(SimpleImage pImg, int pX, int pY, RGBVal pColor) {
    int r = Tools.roundColor(255.0 * pColor.getR());
    int g = Tools.roundColor(255.0 * pColor.getG());
    int b = Tools.roundColor(255.0 * pColor.getB());
    pImg.setRGB(pX, pY, r, g, b);
  }

  public int getSeed() {
    return seed;
  }

  public void setSeed(int seed) {
    this.seed = seed;
  }

  private double noise(double x, double y, double z) {
    int X = (int) Math.floor(x) & 255, // FIND UNIT CUBE THAT
    Y = (int) Math.floor(y) & 255, // CONTAINS POINT.
    Z = (int) Math.floor(z) & 255;
    x -= Math.floor(x); // FIND RELATIVE X,Y,Z
    y -= Math.floor(y); // OF POINT IN CUBE.
    z -= Math.floor(z);
    double u = fade(x), // COMPUTE FADE CURVES
    v = fade(y), // FOR EACH OF X,Y,Z.
    w = fade(z);
    int A = p[X] + Y, AA = p[A] + Z, AB = p[A + 1] + Z, // HASH COORDINATES OF
    B = p[X + 1] + Y, BA = p[B] + Z, BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,

    return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z), // AND ADD
        grad(p[BA], x - 1, y, z)), // BLENDED
        lerp(u, grad(p[AB], x, y - 1, z), // RESULTS
            grad(p[BB], x - 1, y - 1, z))),// FROM  8
        lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1), // CORNERS
            grad(p[BA + 1], x - 1, y, z - 1)), // OF CUBE
            lerp(u, grad(p[AB + 1], x, y - 1, z - 1), grad(p[BB + 1], x - 1, y - 1, z - 1))));
  }

  private double fade(double t) {
    return t * t * t * (t * (t * 6 - 15) + 10);
  }

  private double lerp(double t, double a, double b) {
    return a + t * (b - a);
  }

  private double grad(int hash, double x, double y, double z) {
    int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
    double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
    v = h < 4 ? y : h == 12 || h == 14 ? x : z;
    return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
  }

  static final int p[] = new int[512], permutation[] = { 151, 160, 137, 91, 90, 15, 131, 13, 201,
      95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6,
      148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88,
      237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
      77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40,
      244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200,
      196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
      124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17,
      182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155,
      167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104,
      218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145,
      235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121,
      50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195,
      78, 66, 215, 61, 156, 180 };

  private void initShape() {
    switch (shape) {
      case CLASSIC:
        for (int i = 0; i < 256; i++) {
          p[256 + i] = p[i] = permutation[i];
        }
        break;
      case RANDOM:
        for (int i = 0; i < 256; i++) {
          p[256 + i] = p[i] = (int) (255.0 * Tools.drand() + 0.5);
        }
        break;
    }
  }

  public double getPersistence() {
    return persistence;
  }

  public void setPersistence(double persistence) {
    this.persistence = persistence;
  }

  public int getOctaves() {
    return octaves;
  }

  public void setOctaves(int octaves) {
    this.octaves = octaves;
  }

  public double getInitialFrequency() {
    return initialFrequency;
  }

  public void setInitialFrequency(double initialFrequency) {
    this.initialFrequency = initialFrequency;
  }

  public double getFrequencyMultiplier() {
    return frequencyMultiplier;
  }

  public void setFrequencyMultiplier(double frequencyMultiplier) {
    this.frequencyMultiplier = frequencyMultiplier;
  }

  public static class ColorModeEditor extends ComboBoxPropertyEditor {
    public ColorModeEditor() {
      super();
      setAvailableValues(new ColorMode[] { ColorMode.CUSTOM, ColorMode.RANDOM, ColorMode.GREY });
    }
  }

  public static class ShapeEditor extends ComboBoxPropertyEditor {
    public ShapeEditor() {
      super();
      setAvailableValues(new Shape[] { Shape.CLASSIC, Shape.RANDOM });
    }
  }

  public ColorMode getColorMode() {
    return colorMode;
  }

  public void setColorMode(ColorMode colorMode) {
    this.colorMode = colorMode;
  }

  public Color getLowColor() {
    return lowColor;
  }

  public void setLowColor(Color lowColor) {
    this.lowColor = lowColor;
  }

  public Color getHighColor() {
    return highColor;
  }

  public void setHighColor(Color highColor) {
    this.highColor = highColor;
  }

  public Shape getShape() {
    return shape;
  }

  public void setShape(Shape shape) {
    this.shape = shape;
  }

  public boolean isTransform() {
    return transform;
  }

  public void setTransform(boolean transform) {
    this.transform = transform;
  }

  public String getTransformation() {
    return transformation;
  }

  public void setTransformation(String transformation) {
    this.transformation = transformation;
  }

}
