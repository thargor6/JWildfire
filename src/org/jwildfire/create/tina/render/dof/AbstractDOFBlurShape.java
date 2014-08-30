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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public abstract class AbstractDOFBlurShape implements DOFBlurShape {
  protected FlameTransformationContext flameTransformationContext;
  protected AbstractRandomGenerator randGen;
  protected Map<String, Double> params = new HashMap<String, Double>();
  protected double camDOF_10;
  protected double scale = 1.0;
  protected double rotate = 0.0;
  protected double fade = 1.0;
  protected double sina, cosa;

  @Override
  public void prepare(FlameTransformationContext pFlameTransformationContext, AbstractRandomGenerator pRandGen, double pCamDOF_10) {
    flameTransformationContext = pFlameTransformationContext;
    randGen = pRandGen;
    camDOF_10 = pCamDOF_10;
    sina = MathLib.sin(rotate * MathLib.M_2PI / 360.0);
    cosa = MathLib.cos(rotate * MathLib.M_2PI / 360.0);
  }

  @Override
  public void applyOnlyCamera(XYZPoint pSrc, XYZPoint pDest, double pZDist, double pZR) {
    pDest.x = pSrc.x / pZR;
    pDest.y = pSrc.y / pZR;
  }

  @Override
  public void setParam(String pName, double pValue) {
    params.put(pName, pValue);
  }

  public double getScale() {
    return scale;
  }

  public void setScale(double pScale) {
    scale = pScale;
  }

  public double getRotate() {
    return rotate;
  }

  public void setRotate(double pRotate) {
    rotate = pRotate;
  }

  public double getFade() {
    return fade;
  }

  public void setFade(double pFade) {
    fade = pFade;
    if (fade < 0.0) {
      fade = 0.0;
    }
    else if (fade > 1.0) {
      fade = 1.0;
    }
  }

  @Override
  public void assignParams(Flame pFlame) {
    setScale(pFlame.getCamDOFScale());
    setRotate(pFlame.getCamDOFAngle());
    setFade(pFlame.getCamDOFFade());
    List<String> paramNames = getParamNames();
    if (paramNames.size() > 0) {
      setParam(paramNames.get(0), pFlame.getCamDOFParam1());
      if (paramNames.size() > 1) {
        setParam(paramNames.get(1), pFlame.getCamDOFParam2());
        if (paramNames.size() > 2) {
          setParam(paramNames.get(2), pFlame.getCamDOFParam3());
          if (paramNames.size() > 3) {
            setParam(paramNames.get(3), pFlame.getCamDOFParam4());
            if (paramNames.size() > 4) {
              setParam(paramNames.get(4), pFlame.getCamDOFParam5());
              if (paramNames.size() > 5) {
                setParam(paramNames.get(5), pFlame.getCamDOFParam6());
              }
            }
          }
        }
      }
    }
  }

  protected double doFade() {
    if (fade <= MathLib.EPSILON) {
      return 1.0;
    }
    else if (fade >= 1.0 - MathLib.EPSILON) {
      return randGen.random();
    }
    else {
      return randGen.random() <= fade ? Math.random() : 1.0;
    }
  }

  protected void rotate(XYZPoint pSrc, XYZPoint pDest) {
    if (MathLib.fabs(rotate) > MathLib.EPSILON) {
      double dx = pDest.x - pSrc.x;
      double dy = pDest.y - pSrc.y;

      double rx = dx * cosa - dy * sina;
      double ry = dx * sina + dy * cosa;

      pDest.x = pSrc.x + rx;
      pDest.y = pSrc.y + ry;
    }
  }

}
