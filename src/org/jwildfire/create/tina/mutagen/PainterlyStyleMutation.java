/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2022 Andreas Maschke

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
package org.jwildfire.create.tina.mutagen;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.ColorType;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PainterlyStyleMutation extends AbstractMutation {

  public static final int MAX_BRUSH = 245;

  @Override
  public void execute(Layer layer, double mutationStrength) {
    VariationFunc brushVar = null;
    for (XForm xform : layer.getFinalXForms()) {
      for (int i = 0; i < xform.getVariationCount(); i++) {
        String varName = xform.getVariation(i).getFunc().getName();
        if (varName.equals(BrushStrokeWFFunc.NAME) || varName.equals(PostBrushStrokeWFFunc.NAME)) {
          brushVar = xform.getVariation(i).getFunc();
          break;
        }
      }
    }
    if (brushVar == null) {
      XForm xform;
      if (layer.getFinalXForms().isEmpty()) {
        xform = new XForm();
        layer.getFinalXForms().add(xform);

        xform.setWeight(1.0);
        xform.setColor(Math.random());
        xform.setColorSymmetry(Math.random());
        xform.setColorType(ColorType.DIFFUSION);
        xform.addVariation(
            1.0, VariationFuncList.getVariationFuncInstance(Linear3DFunc.NAME, true));
      } else {
        xform = layer.getFinalXForms().get(layer.getFinalXForms().size() - 1);
      }
      brushVar = VariationFuncList.getVariationFuncInstance(Math.random() > 0.35 ? PostBrushStrokeWFFunc.NAME : BrushStrokeWFFunc.NAME, true);
      xform.addVariation(0.25 + Math.random() * 0.5, brushVar);
    }
    brushVar.setParameter(AbstractBrushStrokeWFFunc.PARAM_BLEND, 0.1 + 0.2 * Math.random());
    brushVar.setParameter(AbstractBrushStrokeWFFunc.PARAM_GRID_SIZE, 0.015 + 0.02 * Math.random());
    brushVar.setParameter(AbstractBrushStrokeWFFunc.PARAM_GRID_DEFORM, 0.05 + 0.1 * Math.random());
    int brushCount = 1 + Tools.FTOI(Math.random() * 5.0);
    List<String> brushIds = new ArrayList<>();
    for (int i = 0; i < brushCount; i++) {
      while (true) {
        String brushId = formatBrushId(1 + Tools.FTOI(Math.random() * (MAX_BRUSH - 1)));
        if (!brushIds.contains(brushId)) {
          brushIds.add(brushId);
          break;
        }
      }
    }
    brushVar.setRessource(
        AbstractBrushStrokeWFFunc.RESSOURCE_BRUSH_PRESETS,
        String.join(", ", brushIds).getBytes(StandardCharsets.UTF_8));
  }

  public static String formatBrushId(int id) {
    return String.format("%03d", id);
  }

  public static int getRandomBrushId() {
    return MathLib.iMin(MAX_BRUSH, Tools.FTOI(1.0+Math.random()*MAX_BRUSH));
  }

}
