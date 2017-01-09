/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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

import java.io.Serializable;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.base.mathlib.VecMathLib.Matrix3D;
import org.jwildfire.base.mathlib.VecMathLib.VectorD;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.base.solidrender.DistantLight;

@SuppressWarnings("serial")
public class LightViewCalculator implements Serializable {
  private final Matrix3D lightProjectionMatrix[];
  private final VectorD lightDir[];

  public LightViewCalculator(Flame flame) {
    lightProjectionMatrix = new Matrix3D[flame.getSolidRenderSettings().getLights().size()];
    lightDir = new VectorD[flame.getSolidRenderSettings().getLights().size()];
    for (int i = 0; i < flame.getSolidRenderSettings().getLights().size(); i++) {
      DistantLight light = flame.getSolidRenderSettings().getLights().get(i);

      double alpha = Math.toRadians(-light.getAltitude());
      double beta = Math.toRadians(-light.getAzimuth());

      double sina = MathLib.sin(alpha);
      double cosa = MathLib.cos(alpha);
      double sinb = MathLib.sin(beta);
      double cosb = MathLib.cos(beta);

      Matrix3D a = new Matrix3D();
      a.m[0][0] = cosb;
      a.m[0][1] = 0;
      a.m[0][2] = sinb;

      a.m[1][0] = sina * sinb;
      a.m[1][1] = cosa;
      a.m[1][2] = -sina * cosb;

      a.m[2][0] = -cosa * sinb;
      a.m[2][1] = sina;
      a.m[2][2] = cosa * cosb;

      lightDir[i] = Matrix3D.multiply(a, new VectorD(0.0, 0.0, -1.0));

      if (light.isCastShadows()) {
        lightProjectionMatrix[i] = a;
      }
      else {
        lightProjectionMatrix[i] = null;
      }
    }
  }

  public double applyLightProjectionX(int idx, double x, double y, double z) {
    return x * lightProjectionMatrix[idx].m[0][0] + y * lightProjectionMatrix[idx].m[0][1] + z * lightProjectionMatrix[idx].m[0][2];
  }

  public double applyLightProjectionY(int idx, double x, double y, double z) {
    return x * lightProjectionMatrix[idx].m[1][0] + y * lightProjectionMatrix[idx].m[1][1] + z * lightProjectionMatrix[idx].m[1][2];
  }

  public double applyLightProjectionZ(int idx, double x, double y, double z) {
    return x * lightProjectionMatrix[idx].m[2][0] + y * lightProjectionMatrix[idx].m[2][1] + z * lightProjectionMatrix[idx].m[2][2];
  }

  public void project(XYZPoint pPoint, XYZProjectedPoint pProjectedPoint) {
    for (int i = 0; i < lightProjectionMatrix.length; i++) {
      if (lightProjectionMatrix[i] != null) {
        pProjectedPoint.lightX[i] = applyLightProjectionX(i, pPoint.x, pPoint.y, pPoint.z);
        pProjectedPoint.lightY[i] = applyLightProjectionY(i, pPoint.x, pPoint.y, pPoint.z);
        pProjectedPoint.lightZ[i] = applyLightProjectionZ(i, pPoint.x, pPoint.y, pPoint.z);
        pProjectedPoint.hasLight[i] = true;
      }
      else {
        pProjectedPoint.hasLight[i] = false;
      }
    }
  }

  public VectorD[] getLightDir() {
    return lightDir;
  }

}
