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

import org.jwildfire.base.mathlib.VecMathLib.Matrix4D;
import org.jwildfire.base.mathlib.VecMathLib.VectorD;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.base.solidrender.PointLight;

@SuppressWarnings("serial")
public class LightViewCalculator implements Serializable {
  private final Matrix4D lightProjectionMatrix[];

  public LightViewCalculator(Flame flame) {
    lightProjectionMatrix = new Matrix4D[flame.getSolidRenderSettings().getLights().size()];
    for (int i = 0; i < flame.getSolidRenderSettings().getLights().size(); i++) {
      PointLight light = flame.getSolidRenderSettings().getLights().get(i);
      if (light.isCastShadows()) {
        lightProjectionMatrix[i] = Matrix4D.lookAt(new VectorD(-light.getX(), light.getY(), light.getZ()), new VectorD(), new VectorD(0.0, 1.0, 0.0));
      }
      else {
        lightProjectionMatrix[i] = null;
      }
    }
  }

  //  public VectorD applyLightProjection(int idx, double x, double y, double z) {
  //    return Matrix4D.multiply(lightProjectionMatrix[idx], new VectorD(x, y, z));
  //  }

  public double applyLightProjectionX(int idx, double x, double y, double z) {
    return x * lightProjectionMatrix[idx].m[0][0] + y * lightProjectionMatrix[idx].m[0][1] + z * lightProjectionMatrix[idx].m[0][2] + lightProjectionMatrix[idx].m[0][3];
  }

  public double applyLightProjectionY(int idx, double x, double y, double z) {
    return x * lightProjectionMatrix[idx].m[1][0] + y * lightProjectionMatrix[idx].m[1][1] + z * lightProjectionMatrix[idx].m[1][2] + lightProjectionMatrix[idx].m[1][3];
  }

  public double applyLightProjectionZ(int idx, double x, double y, double z) {
    return x * lightProjectionMatrix[idx].m[2][0] + y * lightProjectionMatrix[idx].m[2][1] + z * lightProjectionMatrix[idx].m[2][2] + lightProjectionMatrix[idx].m[2][3];
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

}
