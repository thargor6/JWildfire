/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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
package org.jwildfire.create.tina.mutagen;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.BGColorType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.randomgradient.AllRandomGradientGenerator;

public
class RandomBGColorMutation extends AbstractMutation {

  // for Script-compatibility
  @Deprecated
  public void execute(Flame pFlame) {
    execute(pFlame.getFirstLayer(), 1.0);
  }

  @Override
  public void execute(Layer pLayer, double mutationStrength) {
    int keyFrames = 3 + Tools.randomInt(56);
    boolean fadePaletteColors = Math.random() > 0.33;
    boolean uniformWidth = Math.random() > 0.75;
    RGBPalette palette = new AllRandomGradientGenerator().generatePalette(keyFrames, fadePaletteColors, uniformWidth);
    double mode = Math.random();
    Flame flame = pLayer.getOwner();
    if(mode < 0.5) {
      flame.setBgColorType(BGColorType.SINGLE_COLOR);
      int idx = (int)(Math.random() * palette.getSize());
      RGBColor color = palette.getColor(idx);
      flame.setBgColorRed(color.getRed());
      flame.setBgColorGreen(color.getGreen());
      flame.setBgColorBlue(color.getBlue());
    }
    else if(mode < 0.8) {
      flame.setBgColorType(BGColorType.GRADIENT_2X2);
      {
        int idx = (int) (Math.random() * (palette.getSize() - 2));
        {
          RGBColor color = palette.getColor(idx);
          flame.setBgColorLLRed(color.getRed());
          flame.setBgColorLLGreen(color.getGreen());
          flame.setBgColorLLBlue(color.getBlue());
        }
        {
          RGBColor color = palette.getColor(idx + 1);
          flame.setBgColorLRRed(color.getRed());
          flame.setBgColorLRGreen(color.getGreen());
          flame.setBgColorLRBlue(color.getBlue());
        }
      }
      {
        int idx = (int) (Math.random() * (palette.getSize() - 2));
        {
          RGBColor color = palette.getColor(idx);
          flame.setBgColorULRed(color.getRed());
          flame.setBgColorULGreen(color.getGreen());
          flame.setBgColorULBlue(color.getBlue());
        }
        {
          RGBColor color = palette.getColor(idx + 1);
          flame.setBgColorURRed(color.getRed());
          flame.setBgColorURGreen(color.getGreen());
          flame.setBgColorURBlue(color.getBlue());
        }
      }
    }
    else {
      flame.setBgColorType(BGColorType.GRADIENT_2X2_C);
      {
        int idx = (int) (Math.random() * (palette.getSize() - 2));
        {
          RGBColor color = palette.getColor(idx);
          flame.setBgColorLLRed(color.getRed());
          flame.setBgColorLLGreen(color.getGreen());
          flame.setBgColorLLBlue(color.getBlue());
        }
        {
          RGBColor color = palette.getColor(idx + 1);
          flame.setBgColorLRRed(color.getRed());
          flame.setBgColorLRGreen(color.getGreen());
          flame.setBgColorLRBlue(color.getBlue());
        }
      }
      {
        int idx = (int) (Math.random() * (palette.getSize() - 2));
        {
          RGBColor color = palette.getColor(idx);
          flame.setBgColorULRed(color.getRed());
          flame.setBgColorULGreen(color.getGreen());
          flame.setBgColorULBlue(color.getBlue());
        }
        {
          RGBColor color = palette.getColor(idx + 1);
          flame.setBgColorURRed(color.getRed());
          flame.setBgColorURGreen(color.getGreen());
          flame.setBgColorURBlue(color.getBlue());
        }
      }
      {
        int idx = (int) (Math.random() * palette.getSize());
        RGBColor color = palette.getColor(idx);
        flame.setBgColorCCRed(color.getRed());
        flame.setBgColorCCGreen(color.getGreen());
        flame.setBgColorCCBlue(color.getBlue());
      }
    }
  }

}
