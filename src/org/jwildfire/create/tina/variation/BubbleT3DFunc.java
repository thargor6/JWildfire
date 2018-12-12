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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class BubbleT3DFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_NUMBER_OF_STRIPES = "number_of_stripes";
  private static final String PARAM_RATIO_OF_STRIPES = "ratio_of_stripes";
  private static final String PARAM_ANGLE_OF_HOLE = "angle_of_hole";
  private static final String PARAM_EXPONENT_Z = "exponent_z";
  private static final String PARAM_SYMMETRY_Z = "symmetry_z";
  private static final String PARAM_MODUS_BLUR = "modus_blur";
  private static final String[] paramNames = {PARAM_NUMBER_OF_STRIPES, PARAM_RATIO_OF_STRIPES, PARAM_ANGLE_OF_HOLE, PARAM_EXPONENT_Z, PARAM_SYMMETRY_Z, PARAM_MODUS_BLUR};

  private int number_of_stripes = 0;
  private double ratio_of_stripes = 1.0;
  private double angle_of_hole = 0.0;
  private double exponent_z = 1.0;
  private int symmetry_z = 0;
  private int modus_blur = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* bubbleT3D by FractalDesire, http://fractaldesire.deviantart.com/art/bubbleT3D-plugin-188587423 */
    double x = pAffineTP.x, y = pAffineTP.y, z = pAffineTP.z;
    double xTmp, yTmp, angTmp, angRot, fac;
    double rad = (sqr(x) + sqr(y)) / 4 + 1;
    double angXY, angZ;

    // calculation of stripes
    angXY = atan2(x, y);
    if (angXY < 0)
      angXY += M_2PI;

    if (number_of_stripes != 0) {
      while (angXY > angStrip2) {
        angXY -= angStrip2;
      }
      if (invStripes == FALSE) {
        if (angXY > angStrip1) {
          if (modus_blur == FALSE) {
            x = 0.0;
            y = 0.0;
          } else {
            if (ratio_of_stripes == 1) {
              xTmp = c * x - s * y;
              yTmp = s * x + c * y;
              x = xTmp;
              y = yTmp;
            } else {
              angRot = (angXY - angStrip1) / (angStrip2 - angStrip1);
              angRot = angXY - angRot * angStrip1;
              s = sin(angRot);
              c = cos(angRot);
              xTmp = c * x - s * y;
              yTmp = s * x + c * y;
              x = xTmp;
              y = yTmp;
            }
          }

        }
      } else {
        if (angXY < angStrip1) {
          if (modus_blur == FALSE) {
            x = 0.0;
            y = 0.0;
          } else {
            if (ratio_of_stripes == 1) {
              xTmp = c * x - s * y;
              yTmp = s * x + c * y;
              x = xTmp;
              y = yTmp;
            } else {
              angRot = (angXY - angStrip1) / (angStrip1);
              angRot = angXY - angRot * (angStrip2 - angStrip1);
              s = sin(angRot);
              c = cos(angRot);
              xTmp = c * x - s * y;
              yTmp = s * x + c * y;
              x = xTmp;
              y = yTmp;
            }
          }
        }
      }
    }

    //calculation of holes
    x = x / rad;
    y = y / rad;
    if ((x != 0) || (y != 0)) {
      z = 2 / pow(rad, exponent_z) - 1;
      if (exponent_z <= 2)
        angZ = M_PI - acos((z / (x * x + y * y + z * z)));
      else
        angZ = M_PI - atan2(sqr(x * x + y * y), z);
    } else {
      z = 0.0;
      angZ = 0.0;
    }

    // no symmetry to z-axis
    if (symmetry_z == FALSE) {
      if (invHole == FALSE) {
        if (angZ > angle_of_hole) {
          if ((modus_blur == FALSE) || (exponent_z != 1.0)) {
            x = 0.0;
            y = 0.0;
            z = 0.0;
          } else {
            angTmp = (M_PI - angZ) / angHoleComp * angle_of_hole - M_PI_2;
            angZ -= M_PI_2;
            fac = cos(angTmp) / cos(angZ);
            x = x * fac;
            y = y * fac;
            z = z * (sin(angTmp) / sin(angZ));
          }
        }
      } else {
        if (angZ < angle_of_hole) {
          if ((modus_blur == FALSE) || (exponent_z != 1.0)) {
            x = 0.0;
            y = 0.0;
            z = 0.0;
          } else {
            angTmp = M_PI - angZ / angHoleComp * angle_of_hole - M_PI_2;
            angZ -= M_PI_2;
            fac = cos(angTmp) / cos(angZ);
            x = x * fac;
            y = y * fac;
            z = z * (sin(angTmp) / sin(angZ));
          }
        }
      }
    }
    // symmetry to z-axis
    else {
      if ((angZ > angle_of_hole) || (angZ < (M_PI - angle_of_hole))) {
        if ((modus_blur == FALSE) || (exponent_z != 1.0)) {
          x = 0.0;
          y = 0.0;
          z = 0.0;
        } else {
          if (angZ > angle_of_hole) {
            angTmp = (M_PI - angZ) / angHoleComp * (M_PI - 2 * angHoleComp) + angHoleComp - M_PI_2;
          } else {
            angTmp = M_PI_2 - (angZ / angHoleComp * (M_PI - 2 * angHoleComp) + angHoleComp);
          }
          angZ -= M_PI_2;
          fac = cos(angTmp) / cos(angZ);
          x = x * fac;
          y = y * fac;
          z = z * (sin(angTmp) / sin(angZ));
        }
      }
    }

    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;
    pVarTP.z += pAmount * z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{number_of_stripes, ratio_of_stripes, angle_of_hole, exponent_z, symmetry_z, modus_blur};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"number_of_stripes", "ratio_of_stripes", "angle_of_hole", "exponentZ", "_symmetryZ", "_modusBlur"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_NUMBER_OF_STRIPES.equalsIgnoreCase(pName)) {
      number_of_stripes = Tools.FTOI(pValue);
      if (number_of_stripes < 0)
        number_of_stripes = 0;
    } else if (PARAM_RATIO_OF_STRIPES.equalsIgnoreCase(pName))
      ratio_of_stripes = limitVal(pValue, 0.0, 2.0);
    else if (PARAM_ANGLE_OF_HOLE.equalsIgnoreCase(pName))
      angle_of_hole = pValue;
    else if (PARAM_EXPONENT_Z.equalsIgnoreCase(pName))
      exponent_z = pValue;
    else if (PARAM_SYMMETRY_Z.equalsIgnoreCase(pName))
      symmetry_z = Tools.FTOI(pValue);
    else if (PARAM_MODUS_BLUR.equalsIgnoreCase(pName))
      modus_blur = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "bubbleT3D";
  }

  private static final int TRUE = 1;
  private static final int FALSE = 0;

  private double angHoleComp;
  private double angStrip, angStrip1, angStrip2;
  private int invStripes, invHole;
  private double s, c;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // calculation of constants of stripes
    if (number_of_stripes < 0) {
      number_of_stripes = iabs(number_of_stripes);
      invStripes = TRUE;
    } else {
      invStripes = FALSE;
    }
    if (number_of_stripes != 0) {
      angStrip = M_PI / (double) number_of_stripes;
      angStrip2 = 2 * angStrip;
      s = sin(angStrip);
      c = cos(angStrip);
      if (ratio_of_stripes < 0.01)
        ratio_of_stripes = 0.01;
      if (ratio_of_stripes > 1.99)
        ratio_of_stripes = 1.99;
      angStrip1 = ratio_of_stripes * angStrip;
    }

    // calculation of constants of holes
    if (symmetry_z == TRUE) {
      if (angle_of_hole < 0.0)
        angle_of_hole = fabs(angle_of_hole);
      if (angle_of_hole > 179.9)
        angle_of_hole = 179.9;
    }

    if (angle_of_hole < 0) {
      angle_of_hole = fabs(angle_of_hole);
      invHole = TRUE;
      angle_of_hole = (angle_of_hole / 360 * M_2PI) / 2;
    } else {
      invHole = FALSE;
      angle_of_hole = M_PI - (angle_of_hole / 360 * M_2PI) / 2;
    }

    angHoleComp = M_PI - angle_of_hole;
  }
}
