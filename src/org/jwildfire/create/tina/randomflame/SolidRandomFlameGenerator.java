/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2023 Andreas Maschke

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
package org.jwildfire.create.tina.randomflame;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.solidrender.DistantLight;
import org.jwildfire.create.tina.base.solidrender.LightDiffFuncPreset;
import org.jwildfire.create.tina.base.solidrender.ShadowType;
import org.jwildfire.create.tina.variation.PrimitivesWFFunc;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.create.tina.variation.mesh.OBJMeshPrimitiveWFFunc;
import org.jwildfire.create.tina.variation.plot.WFFuncPresetsStore;

public abstract class SolidRandomFlameGenerator extends RandomFlameGenerator {

  protected void randomizeSolidRenderingSettings(Flame flame) {
    flame.getSolidRenderSettings().setSolidRenderingEnabled(true);
    flame.setPreserveZ(true);
    flame.getSolidRenderSettings().getMaterials().get(0).setDiffuse(0.3 + Math.random() * 0.3);
    flame.getSolidRenderSettings().getMaterials().get(0).setAmbient(0.7 + Math.random() * 0.3);
    flame.getSolidRenderSettings().getMaterials().get(0).setPhong(0.2 + Math.random() * 0.8);
    flame.getSolidRenderSettings().getMaterials().get(0).setPhongSize(2.0 + Math.random() * 10.0);

    flame.getSolidRenderSettings().getMaterials().get(0).setLightDiffFunc(Math.random() < 0.5 ? LightDiffFuncPreset.COSA_SQUARE: LightDiffFuncPreset.COSA);

    flame.getSolidRenderSettings().getLights().get(0).setAltitude(60.0 + Math.random() * 60.0);
    flame.getSolidRenderSettings().getLights().get(0).setAzimuth(Math.random() * 360.0);

    flame.getSolidRenderSettings().setShadowType(ShadowType.FAST);

    flame.getSolidRenderSettings().setShadowmapSize(4096);
    flame.getSolidRenderSettings().setShadowmapBias(0.05+Math.random()*0.1);
  }

  protected VariationFunc getRandomVariation() {
    String name = getRandomVariationName();
    return VariationFuncList.getVariationFuncInstance(name, true);
  }

  private String getRandomVariationName() {
    while (true) {
      String name = VariationFuncList.getRandomVariationname();
      if (!name.startsWith("fract") && !name.startsWith("inflate") && !name.startsWith("pre_") && !name.startsWith("post_") 
          && !name.startsWith("prepost_") && !name.equals("flatten")) {
        return name;
      }
    }
  }

  protected VariationFunc getRandom3DShape() {
    VariationFunc varFunc;
    switch (Tools.randomInt(23)) {
      case 0: {
        varFunc = VariationFuncList.getVariationFuncInstance("yplot2d_wf", true);
        varFunc.setParameter("preset_id", WFFuncPresetsStore.getYPlot2DWFFuncPresets().getRandomPresetId());
        varFunc.setParameter("xmin", -3);
        varFunc.setParameter("xmax", 2);
        varFunc.setParameter("ymin", -4);
        varFunc.setParameter("ymax", 4);
        varFunc.setParameter("zmin", -2);
        varFunc.setParameter("zmax", 2);
        varFunc.setParameter("direct_color", 1);
        break;
      }
      case 1:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("seashell3D", true);
        varFunc.setParameter("height", 3.0 + Math.random()*3.0);
        varFunc.setParameter("inner_radius", 0.2+Math.random()*0.4);
        varFunc.setParameter("final_radius", 0.2+Math.random()*0.4);
        break;
      }
      case 2: {
        varFunc = VariationFuncList.getVariationFuncInstance("yplot3d_wf", true);
        varFunc.setParameter("preset_id", WFFuncPresetsStore.getYPlot3DWFFuncPresets().getRandomPresetId());
        varFunc.setParameter("xmin", -3);
        varFunc.setParameter("xmax", 2);
        varFunc.setParameter("ymin", -2);
        varFunc.setParameter("ymax", 2);
        varFunc.setParameter("zmin", -2);
        varFunc.setParameter("zmax", 2);
        varFunc.setParameter("direct_color", 1);
        break;
      }
      case 3:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("text_wf", true);
        break;
      }
      case 4:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("terrain3D", true);
        break;
      }
      case 5:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("superShape3d", true);
        break;
      }
      case 6: {
        varFunc = VariationFuncList.getVariationFuncInstance("polarplot2d_wf", true);
        varFunc.setParameter("preset_id", WFFuncPresetsStore.getPolarPlot2DWFFuncPresets().getRandomPresetId());
        varFunc.setParameter("tmin", -Math.PI);
        varFunc.setParameter("tmax", Math.PI);
        varFunc.setParameter("rmin", -4);
        varFunc.setParameter("rmax", 4);
        varFunc.setParameter("zmin", -2);
        varFunc.setParameter("zmax", 2);
        varFunc.setParameter("direct_color", 1);
        break;
      }
      case 7:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("sunflower", true);
        break;
      }
      case 8:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("rhodonea", true);
        break;
      }
      case 9:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("dustpoint", true);
        break;
      }
      case 10:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("klein_group", true);
        break;
      }
      case 11: {
        varFunc = VariationFuncList.getVariationFuncInstance("polarplot3d_wf", true);
        varFunc.setParameter("preset_id", WFFuncPresetsStore.getPolarPlot3DWFFuncPresets().getRandomPresetId());
        varFunc.setParameter("tmin", -Math.PI);
        varFunc.setParameter("tmax", Math.PI);
        varFunc.setParameter("umin", -Math.PI);
        varFunc.setParameter("umax", Math.PI);
        varFunc.setParameter("rmin", -2);
        varFunc.setParameter("rmax", 2);
        varFunc.setParameter("direct_color", 1);
        break;
      }
      case 12:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("butterfly_fay", true);
        break;
      }
      case 13:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("knots3D", true);
        break;
      }
      case 14:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("mandelbrot", true);
        break;
      }
      case 15:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("pie3D", true);
        varFunc.setParameter("thickness", 0.2+Math.random()*0.4);
        break;
      }
      case 16: {
        varFunc = VariationFuncList.getVariationFuncInstance("parplot2d_wf", true);
        varFunc.setParameter("preset_id", WFFuncPresetsStore.getParPlot2DWFFuncPresets().getRandomPresetId());
        varFunc.setParameter("umin", -Math.PI);
        varFunc.setParameter("umax", Math.PI);
        varFunc.setParameter("vmin", -3);
        varFunc.setParameter("vmax", 8);
        varFunc.setParameter("direct_color", 1);
        varFunc.setParameter("color_mode", Math.random() > 0.666 ? 2 : Math.random() < 0.5 ? 0 : 1);
        break;
      }
      case 17: {
        varFunc = VariationFuncList.getVariationFuncInstance("dla3d_wf", true);
        varFunc.setParameter("max_iter", 100 + Math.random() * 200);
        varFunc.setParameter("inner_blur_radius", Math.random() * 0.5 + 0.1);
        varFunc.setParameter("outer_blur_radius", Math.random() * 0.5);
        varFunc.setParameter("glue_radius", 0.6 + Math.random() * 0.8);
        break;
      }
      case 18:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("obj_mesh_primitive_wf", true);
        varFunc.setParameter("primitive", Tools.randomInt(OBJMeshPrimitiveWFFunc.LOWPOLY_SHAPE_COUNT));
        varFunc.setParameter("scale_x", 1);
        varFunc.setParameter("scale_y", 1);
        varFunc.setParameter("scale_z", 1);
        varFunc.setParameter("offset_x", 0);
        varFunc.setParameter("offset_y", 0);
        varFunc.setParameter("offset_z", 0);
        varFunc.setParameter("subdiv_level", Math.random() > 0.5 ? 0 : Math.random() > 0.5 ? 2 : 1);
        varFunc.setParameter("subdiv_smooth_passes", 12);
        varFunc.setParameter("subdiv_smooth_lambda", 0.42);
        varFunc.setParameter("subdiv_smooth_mu", -0.45);
        break;
      }
      case 19:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("sattractor3D", true);
        break;
      }
      case 20:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("oscilloscope2", true);
        break;
      }
      case 21:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("spirograph", true);
        break;
      }
      default:
      {
        varFunc = VariationFuncList.getVariationFuncInstance("primitives_wf", true);
        varFunc.setParameter("shape", Tools.randomInt(PrimitivesWFFunc.SHAPE_COUNT));
        break;
      }
    }
    return varFunc;
  }

  @Override
  public double getMaxCoverage() {
    return 0.75;
  }

  @Override
  protected Flame postProcessFlameBeforeRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    pFlame.setDefaultSolidRenderingSettings();
    pFlame.setSpatialOversampling(1);

    if (pFlame.getSolidRenderSettings().getLights().size() > 1) {
      DistantLight light = pFlame.getSolidRenderSettings().getLights().get(0);
      light.setAltitude(60 - 120.0 * Math.random());
      light.setAzimuth(30.0 - 60.0 * Math.random());
    }
    if (pFlame.getSolidRenderSettings().getLights().size() > 2) {
      DistantLight light = pFlame.getSolidRenderSettings().getLights().get(1);
      light.setAltitude(40.0 - 80.0 * Math.random());
      light.setAzimuth(80.0 - 160.0 * Math.random());
    }
    return pFlame;
  }
}
