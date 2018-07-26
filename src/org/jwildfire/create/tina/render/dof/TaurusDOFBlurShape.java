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
package org.jwildfire.create.tina.render.dof;

import static org.jwildfire.base.mathlib.MathLib.M_PI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.TaurusFunc;
import org.jwildfire.create.tina.variation.VariationFunc;

public class TaurusDOFBlurShape extends AbstractDOFBlurShape {
  private XYZPoint s, d;
  private VariationFunc fnc;
  private XForm xform;

  @Override
  public void applyDOFAndCamera(XYZPoint pSrc, XYZPoint pDest, double pZDist, double pZR) {
    s.assign(pSrc);
    d.assign(pSrc);
    double fade = doFade();
    double dr = Math.abs(camDOF_10 * pZDist * scale * fade) * 0.25;
    s.x = (2.0 * M_PI - 4.0 * randGen.random() * M_PI) * fade;
    s.y = (2.0 * M_PI - 4.0 * randGen.random() * M_PI) * fade;
    fnc.transform(flameTransformationContext, xform, s, d, dr);
    rotate(pSrc, d);
    pDest.x = d.x / pZR;
    pDest.y = d.y / pZR;
  }

  @Override
  public void prepare(FlameTransformationContext pFlameTransformationContext, AbstractRandomGenerator pRandGen, double pCamDOF_10) {
    super.prepare(pFlameTransformationContext, pRandGen, pCamDOF_10);
    s = new XYZPoint();
    d = new XYZPoint();
    xform = new XForm();
    fnc = new TaurusFunc();
    for (String paramName : getParamNames()) {
      fnc.setParameter(paramName, params.get(paramName));
    }
    fnc.initOnce(pFlameTransformationContext, new Layer(), xform, 1.0);
    fnc.init(pFlameTransformationContext, new Layer(), xform, 1.0);
  }

  private static final List<String> paramNames = new ArrayList<String>(Arrays.asList(new String[] { TaurusFunc.PARAM_R, TaurusFunc.PARAM_N, TaurusFunc.PARAM_INV, TaurusFunc.PARAM_SOR }));

  @Override
  public List<String> getParamNames() {
    return paramNames;
  }

  @Override
  public void setDefaultParams(Flame pFlame) {
    pFlame.setCamDOFFade(0.0);
    pFlame.setCamDOFScale(1.0);
    pFlame.setCamDOFAngle(30.0);
    pFlame.setCamDOFParam1(3.0);// r
    pFlame.setCamDOFParam2(5.0);// n
    pFlame.setCamDOFParam3(1.5);// inv
    pFlame.setCamDOFParam4(1.0);// sor
  }

  @Override
  public boolean isUseColor() {
    return false;
  }

}
