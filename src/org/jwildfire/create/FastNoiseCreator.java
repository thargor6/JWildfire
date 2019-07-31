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
import org.jwildfire.image.SimpleImage;
import fastnoise.FastNoise;
import fastnoise.FastNoise.*;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;


public class FastNoiseCreator extends ImageCreator {
  public enum ColorMode {
    CUSTOM, RANDOM, GREY
  }

  @Property(description = "Noise type", editorClass = NoiseTypeEditor.class)
  private NoiseType noiseType = NoiseType.PerlinFractal;
  
  @Property(description = "Interpolation", editorClass = InterpEditor.class)
  private Interp interp = Interp.Quintic;

  @Property(description = "Seed for the random number generator (zero to produce totally random results)")
  @PropertyMin(0)
  private int seed = 1337;

  @Property(description = "Gain (fractal types only)")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  private double gain = 0.5;

  @Property(description = "Number of octaves (fractal types only)")
  private int octaves = 3;

  @Property(description = "Frequency")
  @PropertyMin(0.0)
  private double frequency = 1.0;

  @Property(description = "Lacunarity (fractal types only)")
  @PropertyMin(0.0)
  private double lacunarity = 2.0;
  
  @Property(description = "Fractal type (fractal types only)", editorClass = FractalTypeEditor.class)
  private FractalType fractalType = FractalType.FBM;
  
  @Property(description = "Distance Function for Cellular noise)", editorClass = CellularDistanceFunctionEditor.class)
  private CellularDistanceFunction cellularDistanceFunction = CellularDistanceFunction.Euclidean;
  
  @Property(description = "Return type for Cellular noise", editorClass = CellularReturnTypeEditor.class)
  private CellularReturnType cellularReturnType = CellularReturnType.Distance;

  @Property(description = "Low color")
  private Color lowColor = new Color(20, 50, 200);

  @Property(description = "High color")
  private Color highColor = new Color(200, 220, 250);

  @Property(description = "Color mode", editorClass = ColorModeEditor.class)
  private ColorMode colorMode = ColorMode.CUSTOM;

  @Override
  protected void fillImage(SimpleImage res) {
    if (this.seed != 0)
      Tools.srand123(this.seed);

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
    
    FastNoise noise = new FastNoise((seed == 0) ? (int) Tools.drand() * 1000 : seed);
    noise.SetNoiseType(noiseType);
    noise.SetInterp(interp);
    noise.SetFractalGain((float) gain);
    noise.SetFractalOctaves(octaves);
    noise.SetFrequency((float) frequency);
    noise.SetFractalLacunarity((float) lacunarity);
    noise.SetFractalType(fractalType);
    noise.SetCellularDistanceFunction(cellularDistanceFunction);
    noise.SetCellularReturnType(cellularReturnType);
    
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    int size = (width < height) ? width : height;
    for (int i = 0; i < height; i++) {
      float y = (float) i / (float) size;
      for (int j = 0; j < width; j++) {
        float x = (float) j / (float) size;

        double noiseValue = noise.GetNoise(x, y);
        RGBVal color = getColor(noiseValue);
        fSetPixel(res, j, i, color);
      }
    }
  }

  private RGBVal cA = new RGBVal();
  private RGBVal cB = new RGBVal();

  private RGBVal getColor(double pNoiseValue) {
    pNoiseValue = (pNoiseValue + 1) / 2;
    if (pNoiseValue < 0.0001)
      pNoiseValue = 0.0001;
    else if (pNoiseValue > 0.9999)
      pNoiseValue = 0.9999;
    RGBVal res = new RGBVal();
    if (colorMode == ColorMode.GREY) {
      res.setR(pNoiseValue);
      res.setG(pNoiseValue);
      res.setB(pNoiseValue);
    }
    else {
      res.setR(cA.getR() * (1.0 - pNoiseValue) + cB.getR() * pNoiseValue);
      res.setG(cA.getG() * (1.0 - pNoiseValue) + cB.getG() * pNoiseValue);
      res.setB(cA.getB() * (1.0 - pNoiseValue) + cB.getB() * pNoiseValue);
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

  public double getGain() {
    return gain;
  }

  public void setGain(double gain) {
    this.gain = gain;
  }

  public int getOctaves() {
    return octaves;
  }

  public void setOctaves(int octaves) {
    this.octaves = octaves;
  }

  public double getFrequency() {
    return frequency;
  }

  public void setFrequency(double frequency) {
    this.frequency = frequency;
  }

  public double getLacunarity() {
    return lacunarity;
  }

  public void setLacunarity(double lacunarity) {
    this.lacunarity = lacunarity;
  }

  public static class ColorModeEditor extends ComboBoxPropertyEditor {
    public ColorModeEditor() {
      super();
      setAvailableValues(new ColorMode[] { ColorMode.CUSTOM, ColorMode.RANDOM, ColorMode.GREY });
    }
  }

  public ColorMode getColorMode() {
    return colorMode;
  }

  public void setColorMode(ColorMode colorMode) {
    this.colorMode = colorMode;
  }

  public static class NoiseTypeEditor extends ComboBoxPropertyEditor {
    public NoiseTypeEditor() {
      super();
      setAvailableValues(new NoiseType[] { NoiseType.Value, NoiseType.ValueFractal, NoiseType.Perlin, NoiseType.PerlinFractal,
          NoiseType.Simplex, NoiseType.SimplexFractal, NoiseType.Cellular, NoiseType.WhiteNoise, NoiseType.Cubic, NoiseType.CubicFractal});
    }
  }

  public NoiseType getNoiseType() {
    return noiseType;
  }

  public void setNoiseType(NoiseType noiseType) {
    this.noiseType = noiseType;
  }

  public static class InterpEditor extends ComboBoxPropertyEditor {
    public InterpEditor() {
      super();
      setAvailableValues(new Interp[] { Interp.Linear, Interp.Hermite, Interp.Quintic });
    }
  }

  public Interp getInterp() {
    return interp;
  }

  public void setInterp(Interp interp) {
    this.interp = interp;
  }

  public static class FractalTypeEditor extends ComboBoxPropertyEditor {
    public FractalTypeEditor() {
      super();
      setAvailableValues(new FractalType[] { FractalType.FBM, FractalType.Billow, FractalType.RigidMulti });
    }
  }

  public FractalType getFractalType() {
    return fractalType;
  }

  public void setFractalType(FractalType fractalType) {
    this.fractalType = fractalType;
  }

  public static class CellularDistanceFunctionEditor extends ComboBoxPropertyEditor {
    public CellularDistanceFunctionEditor() {
      super();
      setAvailableValues(new CellularDistanceFunction[] { CellularDistanceFunction.Euclidean, CellularDistanceFunction.Manhattan, CellularDistanceFunction.Natural });
    }
  }

  public CellularDistanceFunction getCellularDistanceFunction() {
    return cellularDistanceFunction;
  }

  public void setCellularDistanceFunction(CellularDistanceFunction cellularDistanceFunction) {
    this.cellularDistanceFunction = cellularDistanceFunction;
  }

  public static class CellularReturnTypeEditor extends ComboBoxPropertyEditor {
    public CellularReturnTypeEditor() {
      super();
      setAvailableValues(new CellularReturnType[] { CellularReturnType.CellValue, CellularReturnType.NoiseLookup, CellularReturnType.Distance,
          CellularReturnType.Distance2, CellularReturnType.Distance2Add, CellularReturnType.Distance2Sub, CellularReturnType.Distance2Mul, CellularReturnType.Distance2Div });
    }
  }

  public CellularReturnType getCellularReturnType() {
    return cellularReturnType;
  }

  public void setCellularReturnType(CellularReturnType cellularReturnType) {
    this.cellularReturnType = cellularReturnType;
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


}
