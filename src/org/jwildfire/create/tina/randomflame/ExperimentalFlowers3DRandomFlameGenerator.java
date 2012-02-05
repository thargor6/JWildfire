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
package org.jwildfire.create.tina.randomflame;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class ExperimentalFlowers3DRandomFlameGenerator extends Flowers3DRandomFlameGenerator {

  private static final String[] fncList = { "hemisphere", "fisheye", "flux", "foci", "ngon", "power", "truchet", "xheart", "wedge_sph", "spherical", "spherical3D", "spiral", "tan", "tanh" };

  @Override
  protected Flame createFlame() {
    Flame flame = super.createFlame();
    // modify final xForm
    {
      XForm xForm = flame.getFinalXForm();
      xForm.addVariation(1.0 - 2.0 * Math.random(), VariationFuncList.getVariationFuncInstance(fncList[(int) (Math.random() * fncList.length)], true));
      if (Math.random() > 0.5) {
        xForm.addVariation(0.5 * Math.random(), VariationFuncList.getVariationFuncInstance("zcone", true));
      }
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Flowers3D (experimental)";
  }

}
