/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.dance;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.solidrender.ShadowType;

public class FlamePreparer {
  private final Prefs prefs;

  public FlamePreparer(Prefs pPrefs) {
    prefs = pPrefs;
  }

  public Flame createRenderFlame(Flame pSrc) {
    Flame res = pSrc.makeCopy();

    //    res.setBGTransparency(false);
    //    res.setGamma(1.5);
    //    res.setBrightness(3.36);
    //    res.getPalette().setModRed(90);
    //    res.getPalette().setModRed(60);
    //    res.getPalette().setModBlue(-60);
    //    res.setSampleDensity(2 * prefs.getTinaRenderRealtimeQuality());
    //    res.setSpatialFilterRadius(0.75);

    res.setBGTransparency(false);
    //    res.setGamma(res.getGamma() - 0.5);
    //    res.getPalette().setModSaturation(-24);
    //    res.setGamma(2.5);
    //    res.setBrightness(5.0);
    //    res.getPalette().setModRed(30);
    //res.getPalette().setModSaturation(-160);
    //    res.getPalette().setModRed(20);
    //    res.getPalette().setModBlue(-20);
    if (res.getSolidRenderSettings().isSolidRenderingEnabled()) {
      res.getSolidRenderSettings().setAoEnabled(false);
      res.getSolidRenderSettings().setShadowType(ShadowType.OFF);
      res.setCamDOF(0.0);
      res.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
    }
    else {
      res.setSampleDensity(2 * prefs.getTinaRenderRealtimeQuality());
    }

    return res;
  }

}
