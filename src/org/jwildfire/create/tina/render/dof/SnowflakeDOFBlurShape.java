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

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.NBlurFunc;
import org.jwildfire.create.tina.variation.SnowflakeWFFunc;
import org.jwildfire.create.tina.variation.VariationFunc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SnowflakeDOFBlurShape extends AbstractDOFBlurShape {
  private XYZPoint s, d;
  private VariationFunc fnc;
  private XForm xform;

  @Override
  public void applyDOFAndCamera(XYZPoint pSrc, XYZPoint pDest, double pZDist, double pZR) {
    s.assign(pSrc);
    d.assign(pSrc);
    double dr = camDOF_10 * pZDist * scale * doFade();
    fnc.transform(flameTransformationContext, xform, s, d, dr);
    rotate(s, d);
    pDest.x = d.x / pZR;
    pDest.y = d.y / pZR;
  }

  @Override
  public void prepare(FlameTransformationContext pFlameTransformationContext, AbstractRandomGenerator pRandGen, double pCamDOF_10) {
    super.prepare(pFlameTransformationContext, pRandGen, pCamDOF_10);
    s = new XYZPoint();
    d = new XYZPoint();
    xform = new XForm();
    fnc = new SnowflakeWFFunc();
    for (String paramName : getParamNames()) {
      fnc.setParameter(paramName, params.get(paramName));
    }

    fnc.setParameter(SnowflakeWFFunc.PARAM_BUFFER_SIZE, 256);

    fnc.initOnce(pFlameTransformationContext, new Layer(), xform, 1.0);
    fnc.init(pFlameTransformationContext, new Layer(), xform, 1.0);
  }

  private static final List<String> paramNames = new ArrayList<String>(Arrays.asList(new String[] {  SnowflakeWFFunc.PARAM_MAX_ITER, SnowflakeWFFunc.PARAM_BG_FREEZE_LEVEL, SnowflakeWFFunc.PARAM_FG_FREEZE_SPEED, SnowflakeWFFunc.PARAM_DIFFUSION_SPEED, SnowflakeWFFunc.PARAM_RND_BG_NOISE, SnowflakeWFFunc.PARAM_SEED}));

  @Override
  public List<String> getParamNames() {
    return paramNames;
  }

  @Override
  public void setDefaultParams(Flame pFlame) {
    pFlame.setCamDOFFade(0.0);
    pFlame.setCamDOFScale(1.0);
    pFlame.setCamDOFAngle(0.0);
    pFlame.setCamDOFParam1(500); // max iter
    pFlame.setCamDOFParam2(0.5); // bg freeze level
    pFlame.setCamDOFParam3(0.0005); // fg freeze speed
    pFlame.setCamDOFParam4(0.01); // diffusion speed
    pFlame.setCamDOFParam5(0.25); // random bg noise
    pFlame.setCamDOFParam6(333); // seed
  }

  @Override
  public boolean isUseColor() {
    return true;
  }

}
