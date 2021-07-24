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
package org.jwildfire.create.tina.variation;


import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
 * Pre-Part of the prepost_affine for GPU-rendering
 */
public class InternalPrePostAffinePreWFFunc extends VariationFunc implements PrePostGPUImplementation {
  private static final long serialVersionUID = 1L;
  private final PrePostAffineFunc delegate = new PrePostAffineFunc();
  private boolean reversed;

  @Override
  public int getPriority() {
    return -1;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // EMPTY
  }

  @Override
  public String getName() {
    return "pre_prepost_affine";
  }

  @Override
  public String[] getParameterNames() {
    return delegate.getParameterNames();
  }

  @Override
  public Object[] getParameterValues() {
    return delegate.getParameterValues();
  }

  @Override
  public void setParameter(String pName, double pValue) {
    delegate.setParameter(pName, pValue);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float coefxx, coefxy, coefxz, coefyx, coefyy, coefyz, coefzx, coefzy, coefzz;\n"
            + "float icoefxx, icoefxy, icoefxz, icoefyx, icoefyy, icoefyz, icoefzx, icoefzy, icoefzz;\n"
            + "float d2r = PI / 180.f;\n"
            + "    float sinyaw = sinf(__pre_prepost_affine_yaw * d2r), cosyaw = cosf(__pre_prepost_affine_yaw * d2r);\n"
            + "    float sinpitch = sinf(__pre_prepost_affine_pitch * d2r), cospitch = cosf(__pre_prepost_affine_pitch * d2r);\n"
            + "    float sinroll = sinf(__pre_prepost_affine_roll * d2r), cosroll = cosf(__pre_prepost_affine_roll * d2r);\n"
            + "\n"
            + "    coefxx = cosyaw * cosroll - sinyaw * sinpitch * sinroll;\n"
            + "    coefxy = -sinyaw * cosroll - cosyaw * sinpitch * sinroll;\n"
            + "    coefxz = -cospitch * sinroll;\n"
            + "\n"
            + "    coefyx = sinyaw * cospitch;\n"
            + "    coefyy = cosyaw * cospitch;\n"
            + "    coefyz = -sinpitch;\n"
            + "\n"
            + "    coefzx = cosyaw * sinroll + sinyaw * sinpitch * cosroll;\n"
            + "    coefzy = cosyaw * sinpitch * cosroll - sinyaw * sinroll;\n"
            + "    coefzz = cospitch * cosroll;\n"
            + "\n"
            + "    icoefxx = cosyaw * cosroll - sinyaw * sinpitch * sinroll;\n"
            + "    icoefxy = sinyaw * cospitch;\n"
            + "    icoefxz = cosyaw * sinroll + sinyaw * sinpitch * cosroll;\n"
            + "\n"
            + "    icoefyx = -sinyaw * cosroll - cosyaw * sinpitch * sinroll;\n"
            + "    icoefyy = cosyaw * cospitch;\n"
            + "    icoefyz = cosyaw * sinpitch * cosroll - sinyaw * sinroll;\n"
            + "\n"
            + "    icoefzx = -cospitch * sinroll;\n"
            + "    icoefzy = -sinpitch;\n"
            + "    icoefzz = cospitch * cosroll;\n"
            + "if (__pre_prepost_affine != 0) {\n"
            + "    float x = __x - __pre_prepost_affine_move_x;\n"
            + "    float y = __y - __pre_prepost_affine_move_y;\n"
            + "    float z = __z - __pre_prepost_affine_move_z;\n"
            + "    __x = (icoefxx * x + icoefxy * y + icoefxz * z) / (__pre_prepost_affine * __pre_prepost_affine_scale_x);\n"
            + "    __y = (icoefyx * x + icoefyy * y + icoefyz * z) / (__pre_prepost_affine * __pre_prepost_affine_scale_y);\n"
            + "    __z = (icoefzx * x + icoefzy * y + icoefzz * z) / (__pre_prepost_affine * __pre_prepost_affine_scale_z);\n"
            + "  __r2 = __x*__x+__y*__y;\n"
            + "  __r = sqrtf(__r2);\n"
            + "  __rinv = 1.f/__r;\n"
            + "  __phi = atan2f(__x,__y);\n"
            + "  __theta = .5f*PI-__phi;\n"
            + "  if (__theta > PI)\n"
            + "    __theta -= 2.f*PI;\n"
            + "}\n";
  }

  @Override
  public void setReversed(boolean reversed) {
    // TODO not used yet, currently not sure if the reversed state can be achieved at all (some features in regard to pre_post were removed in the past)
    this.reversed = reversed;
  }
}
