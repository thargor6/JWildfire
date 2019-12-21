/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.randomgradient;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBColorKeyFrame;
import org.jwildfire.create.tina.render.GammaCorrectionFilter;
import org.jwildfire.image.Pixel;
import org.jwildfire.transform.HSLTransformer;
import org.jwildfire.transform.HSLTransformer.HSLPixel;

import java.util.ArrayList;
import java.util.List;

public class UniformColorCurveRandomGradientGenerator extends RandomGradientGenerator {

  @Override
  public String getName() {
    return "Uniform curves";
  }

  // use the same api as regular random-gradient-generators in order to avoid to change(=break) the api for existing scripts
  @Override
  public List<RGBColor> generateKeyFrames(int pKeyFrameCount) {
    if(pKeyFrameCount>24 && Math.random()>0.25)
      pKeyFrameCount /=2;

    List<RGBColor> keyFrames = new ArrayList<>();
    keyFrames.add(createRandomKeyFrame(0));
    if(pKeyFrameCount > 2) {
      double dx = 255.0 / (double)(pKeyFrameCount - 1);
      double x = 0;
      for (int i = 1; i < pKeyFrameCount - 1; i++) {
        x += dx * (0.25 + Math.random() * 1.25);
        if(x > 254.0 - MathLib.EPSILON) {
          break;
        }
        keyFrames.add(createRandomKeyFrame(Tools.FTOI(x)));
      }
    }
    keyFrames.add(createRandomKeyFrame(255));
    return keyFrames;
  }

  private RGBColorKeyFrame createRandomKeyFrame(int frame) {
    RGBColorKeyFrame keyFrame = new RGBColorKeyFrame();
    GammaCorrectionFilter.HSLRGBConverter converter = new GammaCorrectionFilter.HSLRGBConverter();

    RGBColor rndColor = generateRandomColor();
    converter.fromRgb((double)rndColor.getRed()/255.0, (double)rndColor.getGreen()/255.0, (double)rndColor.getBlue()/255.0);

    keyFrame.setHue(converter.getHue());
    keyFrame.setSaturation(converter.getSaturation());
    keyFrame.setLuminosity(converter.getLuminosity());

    keyFrame.setRed(rndColor.getRed());
    keyFrame.setGreen(rndColor.getGreen());
    keyFrame.setBlue(rndColor.getBlue());

    keyFrame.setHueKeyFrame(frame);
    keyFrame.setSaturationKeyFrame(frame);
    keyFrame.setLuminosityKeyFrame(frame);
    return keyFrame;
  }

  private RGBColor generateRandomColor() {
    double rnd = Math.random();
    if(rnd>0.125) {
      return new StrongHueRandomGradientGenerator().generateKeyFrames(1).get(0);
    }
    else if(rnd<0.0625) {
      return new SmoothRandomGradientGenerator().generateKeyFrames(1).get(0);
    }
    else {
      return new StripesRandomGradientGenerator().generateKeyFrames(1).get(0);
    }
  }

}
