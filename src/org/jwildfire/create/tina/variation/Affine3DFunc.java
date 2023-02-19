/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Affine3DFunc extends AbstractAffine3DFunc implements SupportsGPU {

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    affineTransform(pContext, pXForm, pAffineTP, pVarTP, pAffineTP.x, pAffineTP.y, pAffineTP.z, pAmount);
  }

  @Override
  protected void applyTransform(XYZPoint pAffineTP, XYZPoint pVarTP, double affineX, double affineY, double affineZ) {
    pVarTP.x += affineX;
    pVarTP.y += affineY;
    pVarTP.z += affineZ;
  }

  @Override
  public String getName() {
    return "affine3D";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float xa = __affine3D_rotateX * PI / 180.0f;\n"
        + " float _sinX = sinf(xa);\n"
        + " float _cosX = cosf(xa);\n"
        + " float ya = __affine3D_rotateY * PI / 180.0f;\n"
        + " float _sinY = sinf(ya);\n"
        + " float _cosY = cosf(ya);\n"
        + " float za = __affine3D_rotateZ * PI / 180.0f;\n"
        + " float _sinZ = sinf(za);\n"
        + " float _cosZ = cosf(za);\n"
        + " bool _hasShear = fabsf(__affine3D_shearXY) > EPSILON || fabsf(__affine3D_shearXZ) > EPSILON || fabsf(__affine3D_shearYX) > EPSILON ||\n"
        + "            fabsf(__affine3D_shearYZ) > EPSILON || fabsf(__affine3D_shearZX) > EPSILON || fabsf(__affine3D_shearZY) > EPSILON;"
        + "if (_hasShear) {\n"
        + "  __px += __affine3D * (_cosZ * (_cosY * (__affine3D_shearXY * __affine3D_scaleY * __y + __affine3D_shearXZ * __affine3D_scaleZ * __z + __affine3D_scaleX * __x) + _sinY * (_sinX * (__affine3D_shearYX * __affine3D_scaleX * __x + __affine3D_shearYZ * __affine3D_scaleZ * __z + __affine3D_scaleY * __y) + _cosX * (__affine3D_shearZX * __affine3D_scaleX * __x + __affine3D_shearZY * __affine3D_scaleY * __y + __affine3D_scaleZ * __z))) - _sinZ * (_cosX * (__affine3D_shearYX * __affine3D_scaleX * __x + __affine3D_shearYZ * __affine3D_scaleZ * __z + __affine3D_scaleY * __y) - _sinX * (__affine3D_shearZX * __affine3D_scaleX * __x + __affine3D_shearZY * __affine3D_scaleY * __y + __affine3D_scaleZ * __z)) + __affine3D_translateX);\n"
        + "  __py += __affine3D * (_sinZ * (_cosY * (__affine3D_shearXY * __affine3D_scaleY * __y + __affine3D_shearXZ * __affine3D_scaleZ * __z + __affine3D_scaleX * __x) + _sinY * (_sinX * (__affine3D_shearYX * __affine3D_scaleX * __x + __affine3D_shearYZ * __affine3D_scaleZ * __z + __affine3D_scaleY * __y) + _cosX * (__affine3D_shearZX * __affine3D_scaleX * __x + __affine3D_shearZY * __affine3D_scaleY * __y + __affine3D_scaleZ * __z))) + _cosZ * (_cosX * (__affine3D_shearYX * __affine3D_scaleX * __x + __affine3D_shearYZ * __affine3D_scaleZ * __z + __affine3D_scaleY * __y) - _sinX * (__affine3D_shearZX * __affine3D_scaleX * __x + __affine3D_shearZY * __affine3D_scaleY * __y + __affine3D_scaleZ * __z)) + __affine3D_translateY);\n"
        + "  __pz += __affine3D * (-_sinY * (__affine3D_shearXY * __affine3D_scaleY * __y + __affine3D_shearXZ * __affine3D_scaleZ * __z + __affine3D_scaleX * __x) + _cosY * (_sinX * (__affine3D_shearYX * __affine3D_scaleX * __x + __affine3D_shearYZ * __affine3D_scaleZ * __z + __affine3D_scaleY * __y) + _cosX * (__affine3D_shearZX * __affine3D_scaleX * __x + __affine3D_shearZY * __affine3D_scaleY * __y + __affine3D_scaleZ * __z)) + __affine3D_translateZ);\n"
        + "} else {\n"
        + "  __px += __affine3D * (_cosZ * (_cosY * __affine3D_scaleX * __x + _sinY * (_cosX * __affine3D_scaleZ * __z + _sinX * __affine3D_scaleY * __y)) - _sinZ * (_cosX * __affine3D_scaleY * __y - _sinX * __affine3D_scaleZ * __z) + __affine3D_translateX);\n"
        + "  __py += __affine3D * (_sinZ * (_cosY * __affine3D_scaleX * __x + _sinY * (_cosX * __affine3D_scaleZ * __z + _sinX * __affine3D_scaleY * __y)) + _cosZ * (_cosX * __affine3D_scaleY * __y - _sinX * __affine3D_scaleZ * __z) + __affine3D_translateY);\n"
        + "  __pz += __affine3D * (-_sinY * __affine3D_scaleX * __x + _cosY * (_cosX * __affine3D_scaleZ * __z + _sinX * __affine3D_scaleY * __y) + __affine3D_translateZ);\n"
        + "}\n";
  }
}
