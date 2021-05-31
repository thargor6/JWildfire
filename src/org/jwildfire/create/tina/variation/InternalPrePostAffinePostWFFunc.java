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
 * Post-Part of the prepost_affine for GPU-rendering
 */
public class InternalPrePostAffinePostWFFunc extends VariationFunc implements PrePostGPUImplementation {
  private static final long serialVersionUID = 1L;
  private final PrePostAffineFunc delegate = new PrePostAffineFunc();
  private boolean reversed;

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // EMPTY
  }

  @Override
  public String getName() {
    return "post_prepost_affine";
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
            + "    float sinyaw = sinf(varpar->post_prepost_affine_yaw * d2r), cosyaw = cosf(varpar->post_prepost_affine_yaw * d2r);\n"
            + "    float sinpitch = sinf(varpar->post_prepost_affine_pitch * d2r), cospitch = cosf(varpar->post_prepost_affine_pitch * d2r);\n"
            + "    float sinroll = sinf(varpar->post_prepost_affine_roll * d2r), cosroll = cosf(varpar->post_prepost_affine_roll * d2r);\n"
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
            + "if (varpar->post_prepost_affine != 0) {\n"
            + "\n"
            + "    float x = varpar->post_prepost_affine * varpar->post_prepost_affine_scale_x * __px;\n"
            + "    float y = varpar->post_prepost_affine * varpar->post_prepost_affine_scale_y * __py;\n"
            + "    float z = varpar->post_prepost_affine * varpar->post_prepost_affine_scale_z * __pz;\n"
            + "\n"
            + "    __px = coefxx * x + coefxy * y + coefxz * z + varpar->post_prepost_affine_move_x;\n"
            + "    __py = coefyx * x + coefyy * y + coefyz * z + varpar->post_prepost_affine_move_y;\n"
            + "    __pz = coefzx * x + coefzy * y + coefzz * z + varpar->post_prepost_affine_move_z;\n"
            + "}\n";
  }

  @Override
  public void setReversed(boolean reversed) {
    this.reversed = reversed;
  }
}
