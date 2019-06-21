/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2019 Andreas Maschke

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
package org.jwildfire.create.tina.base.weightingfield;

import org.jwildfire.create.tina.variation.*;
import org.jwildfire.create.tina.variation.iflames.IFlamesFunc;
import org.jwildfire.create.tina.variation.mesh.*;
import org.jwildfire.create.tina.variation.plot.IsoSFPlot3DWFFunc;
import org.jwildfire.create.tina.variation.plot.ParPlot2DWFFunc;
import org.jwildfire.create.tina.variation.plot.YPlot2DWFFunc;
import org.jwildfire.create.tina.variation.plot.YPlot3DWFFunc;

import java.util.ArrayList;
import java.util.List;

public class WeightingFieldVariationBlackList {
  private final static List<String> VARIATION_BLACK_LIST;

  static {
    VARIATION_BLACK_LIST = new ArrayList<>();
    VARIATION_BLACK_LIST.add(new CrackleFunc().getName());
    VARIATION_BLACK_LIST.add(new DC_CodeFunc().getName());
    VARIATION_BLACK_LIST.add(new DCCrackleWFFunc().getName());
    VARIATION_BLACK_LIST.add(new DC_SunFlowerFunc().getName());
    VARIATION_BLACK_LIST.add(new DC_WorleyFunc().getName());
    VARIATION_BLACK_LIST.add(new DLAWFFunc().getName());
    VARIATION_BLACK_LIST.add(new DLA3DWFFunc().getName());
    VARIATION_BLACK_LIST.add(new FracTerrain3DFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLAcrilicFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLApollonianFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLBaseFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLCirclesBlueFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLCircuitsFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLFractalDotsFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLGrid3DFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLHoshiFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLHyperbolicFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLKaleidoComplexFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLKaleidoscopicFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLKaliSetFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLKaliSet2Func().getName());
    VARIATION_BLACK_LIST.add(new GLSLMandalaFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLMandelBox2DFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLRandomOctreeFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLSquaresFunc().getName());
    VARIATION_BLACK_LIST.add(new GLSLStarsFieldFunc().getName());
    VARIATION_BLACK_LIST.add(new GlynnS3SubflWFFunc().getName());
    VARIATION_BLACK_LIST.add(new IFlamesFunc().getName());
    VARIATION_BLACK_LIST.add(new IsoSFPlot3DWFFunc().getName());
    VARIATION_BLACK_LIST.add(new LSystem3DWFFunc().getName());
    VARIATION_BLACK_LIST.add(new MandalaFunc().getName());
    VARIATION_BLACK_LIST.add(new Mandala2Func().getName());
    VARIATION_BLACK_LIST.add(new NSudokuFunc().getName());
    VARIATION_BLACK_LIST.add(new OBJMeshPrimitiveWFFunc().getName());
    VARIATION_BLACK_LIST.add(new OBJMeshWFFunc().getName());
    VARIATION_BLACK_LIST.add(new ParPlot2DWFFunc().getName());
    VARIATION_BLACK_LIST.add(new Strange3DFunc().getName());
    VARIATION_BLACK_LIST.add(new SubFlameWFFunc().getName());
    VARIATION_BLACK_LIST.add(new SubFlameWFFunc().getName());
    VARIATION_BLACK_LIST.add(new SunFlowersFunc().getName());
    VARIATION_BLACK_LIST.add(new SunflowerVoroniFunc().getName());
    VARIATION_BLACK_LIST.add(new SZubietaFunc().getName());
    VARIATION_BLACK_LIST.add(new TapratsFunc().getName());
    VARIATION_BLACK_LIST.add(new WoggleFunc().getName());
    VARIATION_BLACK_LIST.add(new YPlot2DWFFunc().getName());
    VARIATION_BLACK_LIST.add(new YPlot3DWFFunc().getName());

  }

  // contains all variations with a heavy initialization-part. Those variations can not be used inside weighting-fields,
  // because initialization is called for each sample (because any parameter used inside initialization could change due
  // to the weighting-field)
  public static boolean isBlackListed(String variationName) {
    return VARIATION_BLACK_LIST.contains(variationName);
  }

}
