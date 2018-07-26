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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.SineBlurFunc;
import org.jwildfire.create.tina.variation.VariationFunc;

public class SineBlurDOFBlurShape extends AbstractDOFBlurShape {
  private XYZPoint s, d;
  private VariationFunc fnc;
  private XForm xform;

  @Override
  public void applyDOFAndCamera(XYZPoint pSrc, XYZPoint pDest, double pZDist, double pZR) {
    s.assign(pSrc);
    d.assign(pSrc);
    double dr = camDOF_10 * pZDist * scale * doFade();
    fnc.transform(flameTransformationContext, xform, s, d, dr);
    pDest.x = d.x / pZR;
    pDest.y = d.y / pZR;
  }

  @Override
  public void prepare(FlameTransformationContext pFlameTransformationContext, AbstractRandomGenerator pRandGen, double pCamDOF_10) {
    super.prepare(pFlameTransformationContext, pRandGen, pCamDOF_10);
    s = new XYZPoint();
    d = new XYZPoint();
    xform = new XForm();
    fnc = new SineBlurFunc();
    for (String paramName : getParamNames()) {
      fnc.setParameter(paramName, params.get(paramName));
    }
    fnc.initOnce(pFlameTransformationContext, new Layer(), xform, 1.0);
    fnc.init(pFlameTransformationContext, new Layer(), xform, 1.0);
  }

  private static final List<String> paramNames = new ArrayList<String>(Arrays.asList(new String[] { SineBlurFunc.PARAM_POWER }));

  @Override
  public List<String> getParamNames() {
    return paramNames;
  }

  @Override
  public void setDefaultParams(Flame pFlame) {
    pFlame.setCamDOFFade(0.0);
    pFlame.setCamDOFScale(1.0);
    pFlame.setCamDOFAngle(0.0);
    pFlame.setCamDOFParam1(1.4);// power
  }

  @Override
  public boolean isUseColor() {
    return false;
  }

}
