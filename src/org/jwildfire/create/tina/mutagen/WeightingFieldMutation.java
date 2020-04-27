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
package org.jwildfire.create.tina.mutagen;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.weightingfield.*;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class WeightingFieldMutation implements Mutation {

  @Override
  public void execute(Layer pLayer) {
    if(pLayer.getXForms().size()==0) {
      return;
    }
    // build list of affected xforms: xforms with heigher weight should be taken more often
    List<XForm> xForms = new ArrayList<>();
    if(pLayer.getXForms().size()==1) {
      xForms.add(pLayer.getXForms().get(0));
    }
    else {
      double maxWeight = 0.0;
      for (XForm xForm : pLayer.getXForms()) {
        if (xForm.getWeight() > maxWeight) {
          maxWeight = xForm.getWeight();
        }
      }
      while (xForms.size() < 1) {
        for (XForm xForm : pLayer.getXForms()) {
          if (xForm.getWeight() / maxWeight * Math.random() > 0.25) {
            xForms.add(xForm);
            if (xForms.size() > pLayer.getXForms().size() / 2) {
              break;
            }
          }
        }
      }
    }
    // apply
    for (XForm xForm : pLayer.getXForms()) {
      if (xForms.contains(xForm)) {
        applyRandomWeightField(xForm);
      }
      else {
        clearWeightingField(xForm);
      }
    }
  }

  public void clearWeightingField(XForm xForm) {
    xForm.setWeightingFieldType(WeightingFieldType.NONE);
    xForm.getWeightingFieldVarAmountIntensityCurve().setEnabled(false);
    xForm.getWeightingFieldColorIntensityCurve().setEnabled(false);
    xForm.getWeightingFieldVarParam1IntensityCurve().setEnabled(false);
    xForm.getWeightingFieldVarParam2IntensityCurve().setEnabled(false);
    xForm.getWeightingFieldVarParam3IntensityCurve().setEnabled(false);
  }

  public void clearWeightingField(Layer layer) {
    for(XForm xForm: layer.getXForms()) {
      clearWeightingField(xForm);
    }
  }


  public void applyRandomWeightField(XForm xForm) {
    WeightingFieldType randomWeightingFieldType = randomizeWeightingFieldType();
    xForm.setWeightingFieldType(randomWeightingFieldType);
    xForm.setWeightingFieldInput(Math.random()<0.25 ? WeightingFieldInputType.POSITION : WeightingFieldInputType.AFFINE);
    if(Math.random()>0.42) {
      xForm.setWeightingFieldColorIntensity(0.5-Math.random());
    }
    if(Math.random()>0.87) {
      xForm.setWeightingFieldJitterIntensity(0.5-Math.random());
    }
    // var params
    xForm.setWeightingFieldVarParam1Intensity(0.0);
    xForm.setWeightingFieldVarParam1VarName("");
    xForm.setWeightingFieldVarParam1ParamName("");
    xForm.setWeightingFieldVarParam2Intensity(0.0);
    xForm.setWeightingFieldVarParam2VarName("");
    xForm.setWeightingFieldVarParam2ParamName("");
    xForm.setWeightingFieldVarParam3Intensity(0.0);
    xForm.setWeightingFieldVarParam3VarName("");
    xForm.setWeightingFieldVarParam3ParamName("");
    boolean hasVarParam = false;
    for (Variation var : xForm.getVariations()) {
      if(VariationFuncList.isValidVariationForWeightingFields(var.getFunc().getName()) && var.getFunc().getParameterNames().length>0 && Math.random()>0.33) {
        int idx = Math.min((int)(Math.random()*var.getFunc().getParameterNames().length), var.getFunc().getParameterNames().length-1);
        if(Math.random()>0.33) {
          xForm.setWeightingFieldVarParam1Intensity(0.05 + Math.random() * 0.2);
        }
        else {
          xForm.setWeightingFieldVarParam1Intensity(0.25 - Math.random()*0.5);
        }
        xForm.setWeightingFieldVarParam1VarName(var.getFunc().getName());
        xForm.setWeightingFieldVarParam1ParamName(var.getFunc().getParameterNames()[idx]);
        if(var.getFunc().getParameterNames().length>2) {
          int idx2 = idx;
          while(idx2==idx) {
            idx2 = Math.min((int)(Math.random()*var.getFunc().getParameterNames().length), var.getFunc().getParameterNames().length-1);
          }
          if(Math.random()>0.33) {
            xForm.setWeightingFieldVarParam2Intensity(0.05 + Math.random() * 0.2);
          }
          else {
            xForm.setWeightingFieldVarParam2Intensity(0.25 - Math.random()*0.5);
          }
          xForm.setWeightingFieldVarParam2VarName(var.getFunc().getName());
          xForm.setWeightingFieldVarParam2ParamName(var.getFunc().getParameterNames()[idx2]);
        }
        hasVarParam = true;
      }
    }

    if(!hasVarParam && xForm.getVariationCount()>1 && Math.random()>0.5) {
      int idx = Math.min((int)(Math.random()*xForm.getVariationCount()), xForm.getVariationCount()-1);
      Variation var = xForm.getVariation(idx);
      if(Math.random()>0.33) {
        xForm.setWeightingFieldVarParam1Intensity(0.05 + Math.random() * 0.2);
      }
      else {
        xForm.setWeightingFieldVarParam1Intensity(0.25 - Math.random()*0.5);
      }
      xForm.setWeightingFieldVarParam1VarName(var.getFunc().getName());
      xForm.setWeightingFieldVarParam1ParamName(Variation.WFIELD_AMOUNT_PARAM);
      hasVarParam = true;
    }



    if((hasVarParam && Math.random()>0.66) || (!hasVarParam && Math.random()>0.33)) {
      if(Math.random()>0.33) {
        xForm.setWeightingFieldVarAmountIntensity(0.05 + Math.random() * 0.5);
      }
      else {
        xForm.setWeightingFieldVarAmountIntensity(0.25 - Math.random()*0.5);
      }
    }
    else {
      xForm.setWeightingFieldVarAmountIntensity(0.01 + Math.random() * 0.15);
    }


    switch(randomWeightingFieldType) {
      case IMAGE_MAP:
        break;
      case CELLULAR_NOISE:
        randomizeSeed(xForm);
        randomizeFrequeny(xForm);
        xForm.setWeightingFieldCellularNoiseDistanceFunction(randomizeCellularNoiseDistanceFunction());
        xForm.setWeightingFieldCellularNoiseReturnType(randomizeCellularNoiseReturnType());
        break;
      case CUBIC_NOISE:
      case PERLIN_NOISE:
      case SIMPLEX_NOISE:
      case VALUE_NOISE:
        randomizeSeed(xForm);
        randomizeFrequeny(xForm);
        break;
      case CUBIC_FRACTAL_NOISE:
      case PERLIN_FRACTAL_NOISE:
      case SIMPLEX_FRACTAL_NOISE:
      case VALUE_FRACTAL_NOISE:
        randomizeSeed(xForm);
        randomizeFrequeny(xForm);
        xForm.setWeightingFieldFractalNoiseOctaves(2 + (int)(Math.random() * 4));
        xForm.setWeightingFieldFractalNoiseLacunarity(1.25 + Math.random() * 3.0);
        xForm.setWeightingFieldFractalNoiseGain(0.2 + Math.random() * 0.75);
        xForm.setWeightingFieldFractalType(randomizeFractalType());
        break;
    }
  }

  private void randomizeFrequeny(XForm xForm) {
    xForm.setWeightingFieldNoiseFrequency(0.75+Math.random()*3.0);
  }

  private void randomizeSeed(XForm xForm) {
    xForm.setWeightingFieldNoiseSeed(1+(int)(Math.random()*30000));
  }

  private static List<WeightingFieldType> DEFAULT_WEIGHTING_FIELD_TYPE_LIST;

  static {
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST = new ArrayList<>();
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.CELLULAR_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.CELLULAR_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.CELLULAR_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.CUBIC_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.CUBIC_FRACTAL_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.CUBIC_FRACTAL_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.PERLIN_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.PERLIN_FRACTAL_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.PERLIN_FRACTAL_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.PERLIN_FRACTAL_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.PERLIN_FRACTAL_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.PERLIN_FRACTAL_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.SIMPLEX_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.SIMPLEX_FRACTAL_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.SIMPLEX_FRACTAL_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.VALUE_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.VALUE_FRACTAL_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.VALUE_FRACTAL_NOISE);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.IMAGE_MAP);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.IMAGE_MAP);
    DEFAULT_WEIGHTING_FIELD_TYPE_LIST.add(WeightingFieldType.IMAGE_MAP);
  }

  private List<WeightingFieldType> weightingFieldTypeList = DEFAULT_WEIGHTING_FIELD_TYPE_LIST;

  public List<WeightingFieldType> getWeightingFieldTypeList() {
    return weightingFieldTypeList;
  }

  public void setWeightingFieldTypeList(List<WeightingFieldType> weightingFieldTypeList) {
    this.weightingFieldTypeList = weightingFieldTypeList;
  }

  private WeightingFieldType randomizeWeightingFieldType() {
    int idx =  Math.min((int)(weightingFieldTypeList.size() * Math.random()), weightingFieldTypeList.size()-1);
    return weightingFieldTypeList.get(idx);
  }

  private static List<CellularNoiseReturnType> cellularNoiseReturnTypes;

  static {
    cellularNoiseReturnTypes = new ArrayList<>();
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE2);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE2);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE2);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE_ADD);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE_ADD);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE_DIV);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE2);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE_DIV);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE_MUL);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE_MUL);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE_DIV);
    cellularNoiseReturnTypes.add(CellularNoiseReturnType.DISTANCE_DIV);
  }

  private CellularNoiseReturnType randomizeCellularNoiseReturnType() {
    int idx =  Math.min((int)(cellularNoiseReturnTypes.size() * Math.random()), cellularNoiseReturnTypes.size()-1);
    return cellularNoiseReturnTypes.get(idx);
  }

  private static List<CellularNoiseDistanceFunction> cellularNoiseDistanceFunctions;

  static {
    cellularNoiseDistanceFunctions = new ArrayList<>();
    cellularNoiseDistanceFunctions.add(CellularNoiseDistanceFunction.EUCLIDIAN);
    cellularNoiseDistanceFunctions.add(CellularNoiseDistanceFunction.MANHATTAN);
    cellularNoiseDistanceFunctions.add(CellularNoiseDistanceFunction.EUCLIDIAN);
    cellularNoiseDistanceFunctions.add(CellularNoiseDistanceFunction.NATURAL);
  }

  private CellularNoiseDistanceFunction randomizeCellularNoiseDistanceFunction() {
    int idx =  Math.min((int)(cellularNoiseDistanceFunctions.size() * Math.random()), cellularNoiseDistanceFunctions.size()-1);
    return cellularNoiseDistanceFunctions.get(idx);
  }

  private static List<FractalType> fractalTypes;

  static {
    fractalTypes = new ArrayList<>();
    fractalTypes.add(FractalType.FBM);
    fractalTypes.add(FractalType.BILLOW);
    fractalTypes.add(FractalType.FBM);
    fractalTypes.add(FractalType.RIGID_MULTI);
    fractalTypes.add(FractalType.FBM);
  }

  private FractalType randomizeFractalType() {
    int idx =  Math.min((int)(fractalTypes.size() * Math.random()), fractalTypes.size()-1);
    return fractalTypes.get(idx);
  }

}
