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
package org.jwildfire.create.tina.render;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.BGColorType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.image.SimpleImage;

public class FlameBGColorHandler {
  private final Flame flame;

  public FlameBGColorHandler(Flame flame) {
    super();
    this.flame = flame;
  }

  public void fillBackground(SimpleImage image) {
    if (flame.getBGImageFilename() != null && !flame.getBGImageFilename().isEmpty()) {
      return;
    }

    // fast skip
    if (BGColorType.SINGLE_COLOR.equals(flame.getBgColorType())) {
      if (!(flame.getBgColorRed() > 0 || flame.getBgColorGreen() > 0 || flame.getBgColorBlue() > 0)) {
        image.fillBackground(0, 0, 0);
        return;
      }
    }
    else if (BGColorType.GRADIENT_2X2.equals(flame.getBgColorType())) {
      if (!(flame.getBgColorULRed() > 0 || flame.getBgColorULGreen() > 0 || flame.getBgColorULBlue() > 0 ||
          flame.getBgColorURRed() > 0 || flame.getBgColorURGreen() > 0 || flame.getBgColorURBlue() > 0 ||
          flame.getBgColorLLRed() > 0 || flame.getBgColorLLGreen() > 0 || flame.getBgColorLLBlue() > 0 ||
          flame.getBgColorLRRed() > 0 || flame.getBgColorLRGreen() > 0 || flame.getBgColorLRBlue() > 0)) {
        image.fillBackground(0, 0, 0);
        return;
      }
    }
    else if (BGColorType.GRADIENT_2X2_C.equals(flame.getBgColorType())) {
      if (!(flame.getBgColorULRed() > 0 || flame.getBgColorULGreen() > 0 || flame.getBgColorULBlue() > 0 ||
          flame.getBgColorURRed() > 0 || flame.getBgColorURGreen() > 0 || flame.getBgColorURBlue() > 0 ||
          flame.getBgColorLLRed() > 0 || flame.getBgColorLLGreen() > 0 || flame.getBgColorLLBlue() > 0 ||
          flame.getBgColorLRRed() > 0 || flame.getBgColorLRGreen() > 0 || flame.getBgColorLRBlue() > 0 ||
          flame.getBgColorCCRed() > 0 || flame.getBgColorCCGreen() > 0 || flame.getBgColorCCBlue() > 0)) {
        image.fillBackground(0, 0, 0);
        return;
      }
    }
    LogDensityFilter logDensityFilter = new LogDensityFilter(flame, new MarsagliaRandomGenerator());
    logDensityFilter.setRaster(null, 0, 0, image.getImageWidth(), image.getImageHeight());
    LogDensityPoint logDensityPoint = new LogDensityPoint(1);
    for (int y = 0; y < image.getImageHeight(); y++) {
      for (int x = 0; x < image.getImageWidth(); x++) {
        logDensityFilter.calculateBGColor(logDensityPoint, x, y);
        image.setRGB(x, y, Tools.roundColor(logDensityPoint.bgRed), Tools.roundColor(logDensityPoint.bgGreen), Tools.roundColor(logDensityPoint.bgBlue));
      }
    }
  }

}
