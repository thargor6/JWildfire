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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class PostSmartCropFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_ROUNDSTR = "roundstr";
  private static final String PARAM_ROUNDWIDTH = "roundwidth";
  private static final String PARAM_DISTORTION = "distortion";
  private static final String PARAM_EDGE = "edge";
  private static final String PARAM_SCATTER = "scatter";
  private static final String PARAM_OFFSET = "offset";
  private static final String PARAM_CROPMODE = "cropmode";
  private static final String PARAM_STATIC = "static";
  private static final String[] paramNames = {PARAM_POWER, PARAM_RADIUS, PARAM_ROUNDSTR, PARAM_ROUNDWIDTH, PARAM_DISTORTION, PARAM_EDGE, PARAM_SCATTER, PARAM_OFFSET, PARAM_CROPMODE, PARAM_STATIC};

  private double power = 4.0;
  private double radius = 1.0;
  private double roundstr = 0.0;
  private double roundwidth = 1.0;
  private double distortion = 1.0;
  private double edge = 0.0;
  private double scatter = 0.0;
  private double offset = 0.0;
  private int cropmode = 1;
  private int _static = 2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // post_smartcrop by Zy0rg, http://zy0rg.deviantart.com/art/SmartCrop-267036043
    final double xi, yi, zi;
    if (_static == 2) {
      xi = pVarTP.x;
      yi = pVarTP.y;
      zi = pVarTP.z;
    } else {
      xi = pAffineTP.x;
      yi = pAffineTP.y;
      zi = pAffineTP.z;
    }

    double ang = atan2(yi, xi);
    double rad = sqrt(xi * xi + yi * yi);

    if (post_scrop_radial) {
      double edge = this.edge * (pContext.random() - 0.5);
      double xang = ang / M_2PI + 1 + edge;
      xang = (xang - (int) xang) * M_2PI;
      if ((xang > post_scrop_workpower) == post_scrop_mode) {
        if (cropmode == 2) {
          if ((_static == 2) || (_static == -1)) {
            pVarTP.x = post_scrop_x;
            pVarTP.y = post_scrop_y;
            pVarTP.z = post_scrop_z;
            pVarTP.color = post_scrop_c;
          } else {
            pVarTP.x += post_scrop_x;
            pVarTP.y += post_scrop_y;
            pVarTP.z += post_scrop_z;
            pVarTP.color = post_scrop_c;
          }
        } else {
          double a =
              ((int) (pContext.random() * 2.0)) > 0
                  ? post_scrop_workpower + (pContext.random() * scatter + offset + edge) * M_PI
                  : -(pContext.random() * scatter + offset + edge) * M_PI;
          double s = sin(a);
          double c = cos(a);
          if ((_static == 2) || (_static == -1)) {
            pVarTP.x = pAmount * rad * c;
            pVarTP.y = pAmount * rad * s;
            pVarTP.z = pAmount * zi;
          } else {
            pVarTP.x += pAmount * rad * c;
            pVarTP.y += pAmount * rad * s;
            pVarTP.z += pAmount * zi;
          }
        }
      }
    } else {
      double coeff;
      if (distortion == 0.0)
        coeff = 1;
      else {
        double xang = (ang + M_PI) / post_scrop_alpha;
        xang = xang - (int) xang;
        xang = (xang < 0.5) ? xang : 1 - xang;
        coeff = 1 / cos(xang * post_scrop_alpha);
        if (roundstr != 0.0) {
          final double wwidth = ((roundwidth != 1.0) ? exp(log(xang * 2) * roundwidth) : (xang * 2)) * post_scrop_roundcoeff;
          coeff = fabs((1 - wwidth) * coeff + wwidth);
        }
        if (distortion != 1.0)
          coeff = exp(log(coeff) * distortion);
      }
      final double xr = coeff * ((edge != 0.0) ? post_scrop_workradius + edge * (pContext.random() - 0.5) : post_scrop_workradius);

      if ((rad > xr) == post_scrop_mode) {
        if (cropmode == 2) {
          if ((_static == 2) || (_static == -1)) {
            pVarTP.x = post_scrop_x;
            pVarTP.y = post_scrop_y;
            pVarTP.z = post_scrop_z;
            pVarTP.color = post_scrop_c;
          } else {
            pVarTP.x += post_scrop_x;
            pVarTP.y += post_scrop_y;
            pVarTP.z += post_scrop_z;
            pVarTP.color = post_scrop_c;
          }
        } else {
          final double rdc =
              (cropmode > 0 ? 1 : 0) * xr + coeff * (pContext.random() * scatter + offset);
          double s = sin(ang);
          double c = cos(ang);
          if ((_static == 2) || (_static == -1)) {
            pVarTP.x = pAmount * rdc * c;
            pVarTP.y = pAmount * rdc * s;
            pVarTP.z = pAmount * zi;
          } else {
            pVarTP.x += pAmount * rdc * c;
            pVarTP.y += pAmount * rdc * s;
            pVarTP.z += pAmount * zi;
          }
        }
      }
      else {
        post_scrop_x = pAmount * xi;
        post_scrop_y = pAmount * yi;
        post_scrop_z = pAmount * zi;
        if (cropmode == 2)
          post_scrop_c = pVarTP.color;
        if (_static > 0) {
          pVarTP.x = post_scrop_x;
          pVarTP.y = post_scrop_y;
          pVarTP.z = post_scrop_z;
        } else {
          pVarTP.x += post_scrop_x;
          pVarTP.y += post_scrop_y;
          pVarTP.z += post_scrop_z;
        }
      }
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{power, radius, roundstr, roundwidth, distortion, edge, scatter, offset, cropmode, _static};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_ROUNDSTR.equalsIgnoreCase(pName))
      roundstr = pValue;
    else if (PARAM_ROUNDWIDTH.equalsIgnoreCase(pName))
      roundwidth = pValue;
    else if (PARAM_DISTORTION.equalsIgnoreCase(pName))
      distortion = pValue;
    else if (PARAM_EDGE.equalsIgnoreCase(pName))
      edge = pValue;
    else if (PARAM_SCATTER.equalsIgnoreCase(pName))
      scatter = pValue;
    else if (PARAM_OFFSET.equalsIgnoreCase(pName))
      offset = pValue;
    else if (PARAM_CROPMODE.equalsIgnoreCase(pName))
      cropmode = limitIntVal(Tools.FTOI(pValue), 0, 2);
    else if (PARAM_STATIC.equalsIgnoreCase(pName))
      _static = limitIntVal(Tools.FTOI(pValue), -1, 2);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "post_smartcrop";
  }

  private boolean post_scrop_mode, post_scrop_radial;
  private double post_scrop_workradius, post_scrop_workpower, post_scrop_alpha, post_scrop_roundcoeff;
  private double post_scrop_x, post_scrop_y, post_scrop_z, post_scrop_c;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    post_scrop_mode = (power > 0) == (radius > 0);
    post_scrop_workradius = fabs(radius);
    post_scrop_workpower = fabs(power);
    if (post_scrop_workpower < 2) {
      post_scrop_workpower = post_scrop_workpower * M_PI;
      post_scrop_radial = true;
    } else {
      post_scrop_radial = false;
      post_scrop_alpha = M_2PI / post_scrop_workpower;
      post_scrop_roundcoeff = roundstr / sin(post_scrop_alpha / 2.0) / post_scrop_workpower * 2.0;
    }
    post_scrop_x = post_scrop_y = post_scrop_z = post_scrop_c = 0.0;
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"post_scrop_power", "post_scrop_radius", "post_scrop_roundstr", "post_scrop_roundwidth", "post_scrop_distortion", "post_scrop_edge", "post_scrop_scatter", "post_scrop_offset", "post_scrop_cropmode", "post_scrop_static"};
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "int cropmode = lroundf(__post_smartcrop_cropmode);\n"
        + "int _static = lroundf(__post_smartcrop_static);\n"
        + "short post_scrop_mode, post_scrop_radial;\n"
        + "float post_scrop_workradius, post_scrop_workpower, post_scrop_alpha, post_scrop_roundcoeff;\n"
        + "float post_scrop_x, post_scrop_y, post_scrop_z, post_scrop_c;\n"
        + "post_scrop_mode = (__post_smartcrop_power > 0) == (__post_smartcrop_radius > 0);\n"
        + "    post_scrop_workradius = fabsf(__post_smartcrop_radius);\n"
        + "    post_scrop_workpower = fabsf(__post_smartcrop_power);\n"
        + "    if (post_scrop_workpower < 2) {\n"
        + "      post_scrop_workpower = post_scrop_workpower * PI;\n"
        + "      post_scrop_radial = 1;\n"
        + "    } else {\n"
        + "      post_scrop_radial = 0;\n"
        + "      post_scrop_alpha = (2.0f*PI) / post_scrop_workpower;\n"
        + "      post_scrop_roundcoeff = __post_smartcrop_roundstr / sinf(post_scrop_alpha / 2.0) / post_scrop_workpower * 2.0;\n"
        + "    }\n"
        + "    post_scrop_x = post_scrop_y = post_scrop_z = post_scrop_c = 0.0;\n"
        + "   float xi, yi, zi;\n"
        + "    if (_static == 2) {\n"
        + "      xi = __px;\n"
        + "      yi = __py;\n"
        + "      zi = __pz;\n"
        + "    } else {\n"
        + "      xi = __x;\n"
        + "      yi = __y;\n"
        + "      zi = __z;\n"
        + "    }\n"
        + "\n"
        + "    float ang = atan2f(yi, xi);\n"
        + "    float rad = sqrtf(xi * xi + yi * yi);\n"
        + "\n"
        + "    if (post_scrop_radial) {\n"
        + "      float edge = __post_smartcrop_edge * (RANDFLOAT() - 0.5);\n"
        + "      float xang = ang / (2.0f*PI) + 1 + edge;\n"
        + "      xang = (xang - (int) xang) * (2.0f*PI);\n"
        + "      if ((xang > post_scrop_workpower) == post_scrop_mode) {\n"
        + "        if (cropmode == 2) {\n"
        + "          if ((_static == 2) || (_static == -1)) {\n"
        + "            __px = post_scrop_x;\n"
        + "            __py = post_scrop_y;\n"
        + "            __pz = post_scrop_z;\n"
        + "            __pal = post_scrop_c;\n"
        + "          } else {\n"
        + "            __px += post_scrop_x;\n"
        + "            __py += post_scrop_y;\n"
        + "            __pz += post_scrop_z;\n"
        + "            __pal = post_scrop_c;\n"
        + "          }\n"
        + "        } else {\n"
        + "          float a =\n"
        + "              ((int) (RANDFLOAT() * 2.0)) > 0\n"
        + "                  ? post_scrop_workpower + (RANDFLOAT() * __post_smartcrop_scatter + __post_smartcrop_offset + edge) * PI\n"
        + "                  : -(RANDFLOAT() * __post_smartcrop_scatter + __post_smartcrop_offset + edge) * PI;\n"
        + "          float s = sinf(a);\n"
        + "          float c = cosf(a);\n"
        + "          if ((_static == 2) || (_static == -1)) {\n"
        + "            __px = __post_smartcrop * rad * c;\n"
        + "            __py = __post_smartcrop * rad * s;\n"
        + "            __pz = __post_smartcrop * zi;\n"
        + "          } else {\n"
        + "            __px += __post_smartcrop * rad * c;\n"
        + "            __py += __post_smartcrop * rad * s;\n"
        + "            __pz += __post_smartcrop * zi;\n"
        + "          }\n"
        + "        }\n"
        + "      }\n"
        + "    } else {\n"
        + "      float coeff;\n"
        + "      if (__post_smartcrop_distortion == 0.0)\n"
        + "        coeff = 1;\n"
        + "      else {\n"
        + "        float xang = (ang + PI) / post_scrop_alpha;\n"
        + "        xang = xang - (int) xang;\n"
        + "        xang = (xang < 0.5) ? xang : 1 - xang;\n"
        + "        coeff = 1 / cosf(xang * post_scrop_alpha);\n"
        + "        if (__post_smartcrop_roundstr != 0.0) {\n"
        + "          float wwidth = ((__post_smartcrop_roundwidth != 1.0) ? expf(logf(xang * 2) * __post_smartcrop_roundwidth) : (xang * 2)) * post_scrop_roundcoeff;\n"
        + "          coeff = fabsf((1 - wwidth) * coeff + wwidth);\n"
        + "        }\n"
        + "        if (__post_smartcrop_distortion != 1.0)\n"
        + "          coeff = expf(logf(coeff) * __post_smartcrop_distortion);\n"
        + "      }\n"
        + "      float xr = coeff * ((__post_smartcrop_edge != 0.0) ? post_scrop_workradius + __post_smartcrop_edge * (RANDFLOAT() - 0.5) : post_scrop_workradius);\n"
        + "\n"
        + "      if ((rad > xr) == post_scrop_mode) {\n"
        + "        if (cropmode == 2) {\n"
        + "          if ((_static == 2) || (_static == -1)) {\n"
        + "            __px = post_scrop_x;\n"
        + "            __py = post_scrop_y;\n"
        + "            __pz = post_scrop_z;\n"
        + "            __pal = post_scrop_c;\n"
        + "          } else {\n"
        + "            __px += post_scrop_x;\n"
        + "            __py += post_scrop_y;\n"
        + "            __pz += post_scrop_z;\n"
        + "            __pal = post_scrop_c;\n"
        + "          }\n"
        + "        } else {\n"
        + "          float rdc =\n"
        + "              (cropmode > 0 ? 1 : 0) * xr + coeff * (RANDFLOAT() * __post_smartcrop_scatter + __post_smartcrop_offset);\n"
        + "          float s = sinf(ang);\n"
        + "          float c = cosf(ang);\n"
        + "          if ((_static == 2) || (_static == -1)) {\n"
        + "            __px = __post_smartcrop * rdc * c;\n"
        + "            __py = __post_smartcrop * rdc * s;\n"
        + "            __pz = __post_smartcrop * zi;\n"
        + "          } else {\n"
        + "            __px += __post_smartcrop * rdc * c;\n"
        + "            __py += __post_smartcrop * rdc * s;\n"
        + "            __pz += __post_smartcrop * zi;\n"
        + "          }\n"
        + "        }\n"
        + "      }\n"
        + "      else {\n"
        + "        post_scrop_x = __post_smartcrop * xi;\n"
        + "        post_scrop_y = __post_smartcrop * yi;\n"
        + "        post_scrop_z = __post_smartcrop * zi;\n"
        + "        if (cropmode == 2)\n"
        + "          post_scrop_c = __pal;\n"
        + "        if (_static > 0) {\n"
        + "          __px = post_scrop_x;\n"
        + "          __py = post_scrop_y;\n"
        + "          __pz = post_scrop_z;\n"
        + "        } else {\n"
        + "          __px += post_scrop_x;\n"
        + "          __py += post_scrop_y;\n"
        + "          __pz += post_scrop_z;\n"
        + "        }\n"
        + "      }\n"
        + "    }\n";
  }
}
