/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2019 Andreas Maschke

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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.palette.RGBColor;

public class AllRandomGradientGenerator extends RandomGradientGenerator {
  private static List<RandomGradientGenerator> generators;

  static {
    generators = new ArrayList<RandomGradientGenerator>();
    generators.add(new StrongHueRandomGradientGenerator());
    generators.add(new StrongHueRandomGradientGenerator());
    generators.add(new UnevenColorCurveRandomGradientGenerator());
    generators.add(new UnevenColorCurveRandomGradientGenerator());
    generators.add(new UnevenColorCurveRandomGradientGenerator());
    generators.add(new StrongHueRandomGradientGenerator());
    generators.add(new StrongHueRandomGradientGenerator());
    generators.add(new UnevenColorCurveRandomGradientGenerator());
    generators.add(new UniformColorCurveRandomGradientGenerator());
    generators.add(new StripesRandomGradientGenerator());
    generators.add(new StripesRandomGradientGenerator());
    generators.add(new UnevenColorCurveRandomGradientGenerator());
    generators.add(new UnevenColorCurveRandomGradientGenerator());
    generators.add(new UnevenColorCurveRandomGradientGenerator());
    generators.add(new MonochromeRandomGradientGenerator());
    generators.add(new SmoothRandomGradientGenerator());
    generators.add(new BoldRandomGradientGenerator());
    generators.add(new UnevenColorCurveRandomGradientGenerator());
    generators.add(new BoldRandomGradientGenerator());
    generators.add(new UniformColorCurveRandomGradientGenerator());
    generators.add(new UnevenColorCurveRandomGradientGenerator());
    generators.add(new UniformColorCurveRandomGradientGenerator());
    generators.add(new UnevenColorCurveRandomGradientGenerator());
    generators.add(new UnevenColorCurveRandomGradientGenerator());
    generators.add(new BoldRandomGradientGenerator());
    generators.add(new TwoColorsRandomGradientGenerator());
  }

  private RandomGradientGenerator createRandGen() {
    RandomGradientGenerator generator = generators.get((int) (Math.random() * generators.size()));
    return generator;
  }

  @Override
  public String getName() {
    return "(All)";
  }

  @Override
  public List<RGBColor> generateKeyFrames(int pKeyFrameCount) {
    return createRandGen().generateKeyFrames(pKeyFrameCount);
  }

}
