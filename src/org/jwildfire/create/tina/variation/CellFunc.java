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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.floor;

public class CellFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SIZE = "size";

  private static final String[] paramNames = {PARAM_SIZE};

  private double size = 0.60;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Cell in the Apophysis Plugin Pack */

    double inv_cell_size = 1.0 / size;

    /* calculate input cell */
    int x = (int) floor(pAffineTP.x * inv_cell_size);
    int y = (int) floor(pAffineTP.y * inv_cell_size);

    /* Offset from cell origin */
    double dx = pAffineTP.x - x * size;
    double dy = pAffineTP.y - y * size;

    /* interleave cells */
    if (y >= 0) {
      if (x >= 0) {
        y *= 2;
        x *= 2;
      } else {
        y *= 2;
        x = -(2 * x + 1);
      }
    } else {
      if (x >= 0) {
        y = -(2 * y + 1);
        x *= 2;
      } else {
        y = -(2 * y + 1);
        x = -(2 * x + 1);
      }
    }

    pVarTP.x += pAmount * (dx + x * size);
    pVarTP.y -= pAmount * (dy + y * size);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{size};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SIZE.equalsIgnoreCase(pName))
      size = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "cell";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float inv_cell_size = 1.0f / varpar->cell_size;\n" +
            "\n" +
            "    /* calculate input cell */\n" +
            "    int x = (int) floorf(__x * inv_cell_size);\n" +
            "    int y = (int) floorf(__y * inv_cell_size);\n" +
            "\n" +
            "    /* Offset from cell origin */\n" +
            "    float dx = __x - x * varpar->cell_size;\n" +
            "    float dy = __y - y * varpar->cell_size;\n" +
            "\n" +
            "    /* interleave cells */\n" +
            "    if (y >= 0) {\n" +
            "      if (x >= 0) {\n" +
            "        y *= 2;\n" +
            "        x *= 2;\n" +
            "      } else {\n" +
            "        y *= 2;\n" +
            "        x = -(2 * x + 1);\n" +
            "      }\n" +
            "    } else {\n" +
            "      if (x >= 0) {\n" +
            "        y = -(2 * y + 1);\n" +
            "        x *= 2;\n" +
            "      } else {\n" +
            "        y = -(2 * y + 1);\n" +
            "        x = -(2 * x + 1);\n" +
            "      }\n" +
            "    }\n" +
            "\n" +
            "    __px += varpar->cell * (dx + x * varpar->cell_size);\n" +
            "    __py -= varpar->cell * (dy + y * varpar->cell_size);\n" +
            (context.isPreserveZCoordinate()? "__z += varpar->cell * __z;": "");
  }
}
