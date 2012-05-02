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
package org.jwildfire.create.tina.script;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.Variation;

public class ScriptGenerator {
  private final Flame flame;

  public ScriptGenerator(Flame pFlame) {
    flame = pFlame;
  }

  public String generateScript() {
    StringBuffer sb = new StringBuffer();
    sb.append("  Flame flame = new Flame();\n");
    sb.append("  flame.setCentreX(" + Tools.doubleToString(flame.getCentreX()) + ");\n");
    sb.append("  flame.setCentreY(" + Tools.doubleToString(flame.getCentreY()) + ");\n");
    sb.append("  flame.setCamPitch(" + Tools.doubleToString(flame.getCamPitch()) + ");\n");
    sb.append("  flame.setCamRoll(" + Tools.doubleToString(flame.getCamRoll()) + ");\n");
    sb.append("  flame.setCamYaw(" + Tools.doubleToString(flame.getCamYaw()) + ");\n");
    sb.append("  flame.setPixelsPerUnit(200);\n");
    sb.append("  flame.setFinalXForm(null);\n");
    sb.append("  flame.getXForms().clear();\n");
    int idx = 0;
    for (XForm xForm : flame.getXForms()) {
      switch (++idx) {
        case 1:
          sb.append("  // 1st xForm\n");
          break;
        case 2:
          sb.append("  // 2nd xForm\n");
          break;
        case 3:
          sb.append("  // 3rd xForm\n");
          break;
        default:
          sb.append("  // " + (idx) + "th xForm\n");
          break;
      }
      sb.append("  {\n");
      sb.append("    XForm xForm = new XForm();\n");
      sb.append("    flame.getXForms().add(xForm);\n");
      sb.append("    xForm.setWeight(" + Tools.doubleToString(xForm.getWeight()) + ");\n");
      addXForm(sb, xForm);
      sb.append("    xForm.setColor(" + Tools.doubleToString(xForm.getColor()) + ");\n");
      sb.append("    xForm.setColorSymmetry(" + Tools.doubleToString(xForm.getColorSymmetry()) + ");\n");
      sb.append("  }\n");
    }
    if (flame.getFinalXForm() != null) {
      sb.append("  // final xForm\n");
      sb.append("  {\n");
      sb.append("    XForm xForm = new XForm();\n");
      sb.append("    flame.setFinalXForm(xForm);\n");
      addXForm(sb, flame.getFinalXForm());
      sb.append("  }\n");
    }
    return sb.toString();
  }

  private void addXForm(StringBuffer pSB, XForm pXForm) {
    if (pXForm.isHasCoeffs()) {
      pSB.append("    xForm.setCoeff00(" + pXForm.getCoeff00() + ");\n");
      pSB.append("    xForm.setCoeff01(" + pXForm.getCoeff01() + ");\n");
      pSB.append("    xForm.setCoeff10(" + pXForm.getCoeff10() + ");\n");
      pSB.append("    xForm.setCoeff11(" + pXForm.getCoeff11() + ");\n");
      pSB.append("    xForm.setCoeff20(" + pXForm.getCoeff20() + ");\n");
      pSB.append("    xForm.setCoeff21(" + pXForm.getCoeff21() + ");\n");
    }
    if (pXForm.isHasPostCoeffs()) {
      pSB.append("    xForm.setPostCoeff00(" + pXForm.getPostCoeff00() + ");\n");
      pSB.append("    xForm.setPostCoeff01(" + pXForm.getPostCoeff01() + ");\n");
      pSB.append("    xForm.setPostCoeff10(" + pXForm.getPostCoeff10() + ");\n");
      pSB.append("    xForm.setPostCoeff11(" + pXForm.getPostCoeff11() + ");\n");
      pSB.append("    xForm.setPostCoeff20(" + pXForm.getPostCoeff20() + ");\n");
      pSB.append("    xForm.setPostCoeff21(" + pXForm.getPostCoeff21() + ");\n");
    }
    if (pXForm.getVariationCount() > 0) {
      pSB.append("    {\n");
      pSB.append("      VariationFunc varFunc;\n");
      for (int i = 0; i < pXForm.getVariationCount(); i++) {
        Variation var = pXForm.getVariation(i);
        String paramNames[] = var.getFunc().getParameterNames();
        if (paramNames == null || paramNames.length == 0) {
          pSB.append("      xForm.addVariation(" + Tools.doubleToString(var.getAmount()) + ", VariationFuncList.getVariationFuncInstance(\"" + var.getFunc().getName() + "\", true));\n");
        }
        else {
          pSB.append("      varFunc=VariationFuncList.getVariationFuncInstance(\"" + var.getFunc().getName() + "\", true);\n");
          Object vals[] = var.getFunc().getParameterValues();
          for (int j = 0; j < paramNames.length; j++) {
            if (vals[j] instanceof Integer) {
              pSB.append("      varFunc.setParameter(\"" + paramNames[j] + "\", " + ((Integer) vals[j]) + ");\n");
            }
            else if (vals[j] instanceof Double) {
              pSB.append("      varFunc.setParameter(\"" + paramNames[j] + "\", " + Tools.doubleToString((Double) vals[j]) + ");\n");
            }
            else {
              throw new IllegalStateException();
            }
          }
          pSB.append("      xForm.addVariation(" + Tools.doubleToString(var.getAmount()) + ", varFunc);\n");
        }
      }
      pSB.append("    }\n");
    }
  }
}
