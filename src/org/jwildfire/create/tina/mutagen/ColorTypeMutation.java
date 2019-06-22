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
package org.jwildfire.create.tina.mutagen;

import java.util.ArrayList;
import java.util.List;
import org.jwildfire.create.tina.base.ColorType;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;

public class ColorTypeMutation implements Mutation {
  private static final List<ColorType> xFormColorTypes;
  private static final List<ColorType> finalXFormColorTypes;

  static {
    xFormColorTypes = new ArrayList<>();
    xFormColorTypes.add(ColorType.DIFFUSION);
    xFormColorTypes.add(ColorType.UNSET);
    xFormColorTypes.add(ColorType.DISTANCE);
    xFormColorTypes.add(ColorType.TARGET);
    xFormColorTypes.add(ColorType.UNSET);
    xFormColorTypes.add(ColorType.DIFFUSION);
    xFormColorTypes.add(ColorType.TARGETG);
    xFormColorTypes.add(ColorType.UNSET);
    xFormColorTypes.add(ColorType.NONE);
    xFormColorTypes.add(ColorType.UNSET);
    xFormColorTypes.add(ColorType.UNSET);
    xFormColorTypes.add(ColorType.DIFFUSION);

    finalXFormColorTypes = new ArrayList<>();
    finalXFormColorTypes.add(ColorType.NONE);
    finalXFormColorTypes.add(ColorType.DIFFUSION);
    finalXFormColorTypes.add(ColorType.NONE);
    finalXFormColorTypes.add(ColorType.UNSET);
    finalXFormColorTypes.add(ColorType.UNSET);
    finalXFormColorTypes.add(ColorType.DISTANCE);
    finalXFormColorTypes.add(ColorType.NONE);
    finalXFormColorTypes.add(ColorType.TARGET);
    finalXFormColorTypes.add(ColorType.UNSET);
    finalXFormColorTypes.add(ColorType.DIFFUSION);
    finalXFormColorTypes.add(ColorType.UNSET);
    finalXFormColorTypes.add(ColorType.UNSET);
    finalXFormColorTypes.add(ColorType.NONE);
    finalXFormColorTypes.add(ColorType.TARGETG);
    finalXFormColorTypes.add(ColorType.UNSET);
    finalXFormColorTypes.add(ColorType.UNSET);
    finalXFormColorTypes.add(ColorType.DIFFUSION);
    finalXFormColorTypes.add(ColorType.NONE);
  }

  @Override
  public void execute(Layer pLayer) {
    randomizeColorType(pLayer.getXForms(), xFormColorTypes);
    randomizeColorType(pLayer.getFinalXForms(), finalXFormColorTypes);
  }

  private void randomizeColorType(List<XForm> xForms, List<ColorType> colorTypes) {
    for (XForm xForm : xForms) {
      int idx = Math.min((int)(Math.random() * colorTypes.size()), colorTypes.size()-1);
      ColorType colorType = colorTypes.get(idx);
      xForm.setColorType(colorType);
      if(ColorType.TARGET.equals(colorType)) {
        randomizeTargetColor(xForm);
      }
    }

  }

  private void randomizeTargetColor(XForm xForm) {
    xForm.setTargetColor((int)(Math.random()*256), (int)(Math.random()*256), (int)(Math.random()*256));
  }

}
