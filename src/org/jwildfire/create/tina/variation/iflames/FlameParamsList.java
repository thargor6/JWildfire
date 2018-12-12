/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.variation.iflames;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.create.tina.variation.RessourceType;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlameParamsList extends ArrayList<FlameParams> implements Params {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_FLAME_SIZE = "flame_size";
  private static final String PARAM_FLAME_SIZE_VAR = "flame_size_var";
  private static final String PARAM_FLAME_ROTATE_ALPHA = "flame_rotate_alpha";
  private static final String PARAM_FLAME_ROTATE_ALPHA_VAR = "flame_rotate_alpha_var";
  private static final String PARAM_FLAME_ROTATE_ALPHA_SPEED = "flame_rotate_alpha_speed";
  private static final String PARAM_FLAME_ROTATE_ALPHA_SPEED_VAR = "flame_rotate_alpha_speed_var";
  private static final String PARAM_FLAME_ROTATE_BETA = "flame_rotate_beta";
  private static final String PARAM_FLAME_ROTATE_BETA_VAR = "flame_rotate_beta_var";
  private static final String PARAM_FLAME_ROTATE_BETA_SPEED = "flame_rotate_beta_speed";
  private static final String PARAM_FLAME_ROTATE_BETA_SPEED_VAR = "flame_rotate_beta_speed_var";
  private static final String PARAM_FLAME_ROTATE_GAMMA = "flame_rotate_gamma";
  private static final String PARAM_FLAME_ROTATE_GAMMA_VAR = "flame_rotate_gamma_var";
  private static final String PARAM_FLAME_ROTATE_GAMMA_SPEED = "flame_rotate_gamma_speed";
  private static final String PARAM_FLAME_ROTATE_GAMMA_SPEED_VAR = "flame_rotate_gamma_speed_var";
  private static final String PARAM_FLAME_SPEED_X = "flame_speed_x";
  private static final String PARAM_FLAME_SPEED_X_VAR = "flame_speed_x_var";
  private static final String PARAM_FLAME_SPEED_Y = "flame_speed_y";
  private static final String PARAM_FLAME_SPEED_Y_VAR = "flame_speed_y_var";
  private static final String PARAM_FLAME_SPEED_Z = "flame_speed_z";
  private static final String PARAM_FLAME_SPEED_Z_VAR = "flame_speed_z_var";
  private static final String PARAM_FLAME_RADIAL_ACCEL = "flame_radial_accel";
  private static final String PARAM_FLAME_RADIAL_ACCEL_VAR = "flame_radial_accel_var";
  private static final String PARAM_FLAME_TANGENTIAL_ACCEL = "flame_tangential_accel";
  private static final String PARAM_FLAME_TANGENTIAL_ACCEL_VAR = "flame_tangential_accel_var";
  private static final String PARAM_FLAME_CENTRE_X = "flame_centre_x";
  private static final String PARAM_FLAME_CENTRE_Y = "flame_centre_y";
  private static final String PARAM_FLAME_MINVAL = "flame_min_val";
  private static final String PARAM_FLAME_MAXVAL = "flame_max_val";
  private static final String PARAM_FLAME_WEIGHT = "flame_weight";
  private static final String PARAM_FLAME_PARAM1_MIN = "flame_param1_min";
  private static final String PARAM_FLAME_PARAM1_MAX = "flame_param1_max";
  private static final String PARAM_FLAME_PARAM2_MIN = "flame_param2_min";
  private static final String PARAM_FLAME_PARAM2_MAX = "flame_param2_max";
  private static final String PARAM_FLAME_PARAM3_MIN = "flame_param3_min";
  private static final String PARAM_FLAME_PARAM3_MAX = "flame_param3_max";
  public static final String RESSOURCE_FLAME = "flame";
  private static final String RESSOURCE_FLAME_PARAM1 = "flame_param1";
  private static final String RESSOURCE_FLAME_PARAM1_MAP = "flame_param1_map";
  private static final String RESSOURCE_FLAME_PARAM2 = "flame_param2";
  private static final String RESSOURCE_FLAME_PARAM2_MAP = "flame_param2_map";
  private static final String RESSOURCE_FLAME_PARAM3 = "flame_param3";
  private static final String RESSOURCE_FLAME_PARAM3_MAP = "flame_param3_map";
  private static final String PARAM_PREVIEW_R = "preview_r";
  private static final String PARAM_PREVIEW_G = "preview_g";
  private static final String PARAM_PREVIEW_B = "preview_b";
  private static final String PARAM_GRID_X_OFFSET = "grid_x_offset";
  private static final String PARAM_GRID_Y_OFFSET = "grid_y_offset";
  private static final String PARAM_GRID_X_SIZE = "grid_x_size";
  private static final String PARAM_GRID_Y_SIZE = "grid_y_size";
  private static final String PARAM_BRIGHTNESS_MIN = "brightness_min";
  private static final String PARAM_BRIGHTNESS_MAX = "brightness_max";
  private static final String PARAM_BRIGHTNESS_CHANGE = "brightness_change";
  private static final String PARAM_INSTANCING = "instancing";

  private static RGBColor[] previewColors = new RGBColor[]{new RGBColor(255, 128, 31), new RGBColor(255, 0, 0), new RGBColor(0, 255, 0), new RGBColor(0, 255, 255), new RGBColor(255, 180, 32)};

  public static FlameParamsList createFlameParams(int pCount) {
    FlameParamsList res = new FlameParamsList();
    double dV = 0.15;
    double minValue = 0.0;
    double maxValue = dV;
    int colorIdx = 0;
    for (int i = 0; i < pCount; i++) {
      FlameParams params = new FlameParams();
      res.add(params);
      if (FlameParams.EXAMPLE_FLAMES.length > i) {
        params.setFlameXML(FlameParams.EXAMPLE_FLAMES[i]);
      }
      params.setWeight(1.0);
      params.setMinVal(minValue);
      minValue += dV;
      params.setMaxVal(maxValue);
      maxValue += dV;
      params.setSize(2.0);
      params.setSizeVar(200.0);
      params.setRotateAlpha(25.0);
      params.setRotateAlphaVar(100.0);
      params.setRotateAlphaSpeed(16.0);
      params.setRotateAlphaSpeedVar(50.0);
      params.setRotateBeta(0.0);
      params.setRotateBetaVar(0.0);
      params.setRotateBetaSpeed(0.0);
      params.setRotateGamma(0.0);
      params.setRotateGammaVar(0.0);
      params.setRotateGammaSpeed(0.0);
      params.setSpeedX(0.0);
      params.setSpeedXVar(0.0);
      params.setSpeedY(0.0);
      params.setSpeedYVar(0.0);
      params.setSpeedZ(0.0);
      params.setSpeedZVar(0.0);
      params.setCentreX(0.0);
      params.setCentreY(0.0);
      params.setFlameParam1(null);
      params.setFlameParamMap1Filename(null);
      params.setFlameParam1Min(0.0);
      params.setFlameParam1Max(1.0);
      params.setFlameParam2(null);
      params.setFlameParamMap2Filename(null);
      params.setFlameParam2Min(0.0);
      params.setFlameParam2Max(1.0);
      params.setFlameParam3(null);
      params.setFlameParamMap3Filename(null);
      params.setFlameParam3Min(0.0);
      params.setFlameParam3Max(1.0);
      params.setPreviewR(previewColors[colorIdx].getRed());
      params.setPreviewG(previewColors[colorIdx].getGreen());
      params.setPreviewB(previewColors[colorIdx].getBlue());
      params.setGridXSize(20);
      params.setGridYSize(16);
      params.setBrightnessMin(1.0);
      params.setBrightnessMax(1.0);
      params.setBrightnessChange(0.0);
      params.setInstancing(false);
      colorIdx++;
      if (colorIdx >= previewColors.length) {
        colorIdx = 0;
      }
    }
    return res;
  }

  @Override
  public String[] appendParamNames(String[] pParamNames) {
    List<String> res = new ArrayList<String>(Arrays.asList(pParamNames));
    for (int i = 1; i <= size(); i++) {
      String flameIndexStr = getFlameIndexStr(i);
      res.add(PARAM_FLAME_SIZE + flameIndexStr);
      res.add(PARAM_FLAME_SIZE_VAR + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_ALPHA + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_ALPHA_VAR + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_ALPHA_SPEED + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_ALPHA_SPEED_VAR + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_BETA + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_BETA_VAR + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_BETA_SPEED + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_BETA_SPEED_VAR + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_GAMMA + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_GAMMA_VAR + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_GAMMA_SPEED + flameIndexStr);
      res.add(PARAM_FLAME_ROTATE_GAMMA_SPEED_VAR + flameIndexStr);
      res.add(PARAM_FLAME_SPEED_X + flameIndexStr);
      res.add(PARAM_FLAME_SPEED_X_VAR + flameIndexStr);
      res.add(PARAM_FLAME_SPEED_Y + flameIndexStr);
      res.add(PARAM_FLAME_SPEED_Y_VAR + flameIndexStr);
      res.add(PARAM_FLAME_SPEED_Z + flameIndexStr);
      res.add(PARAM_FLAME_SPEED_Z_VAR + flameIndexStr);
      res.add(PARAM_FLAME_RADIAL_ACCEL + flameIndexStr);
      res.add(PARAM_FLAME_RADIAL_ACCEL_VAR + flameIndexStr);
      res.add(PARAM_FLAME_TANGENTIAL_ACCEL + flameIndexStr);
      res.add(PARAM_FLAME_TANGENTIAL_ACCEL_VAR + flameIndexStr);
      res.add(PARAM_FLAME_CENTRE_X + flameIndexStr);
      res.add(PARAM_FLAME_CENTRE_Y + flameIndexStr);
      res.add(PARAM_FLAME_MINVAL + flameIndexStr);
      res.add(PARAM_FLAME_MAXVAL + flameIndexStr);
      res.add(PARAM_FLAME_WEIGHT + flameIndexStr);
      res.add(PARAM_FLAME_PARAM1_MIN + flameIndexStr);
      res.add(PARAM_FLAME_PARAM1_MAX + flameIndexStr);
      res.add(PARAM_FLAME_PARAM2_MIN + flameIndexStr);
      res.add(PARAM_FLAME_PARAM2_MAX + flameIndexStr);
      res.add(PARAM_FLAME_PARAM3_MIN + flameIndexStr);
      res.add(PARAM_FLAME_PARAM3_MAX + flameIndexStr);
      res.add(PARAM_PREVIEW_R + flameIndexStr);
      res.add(PARAM_PREVIEW_G + flameIndexStr);
      res.add(PARAM_PREVIEW_B + flameIndexStr);
      res.add(PARAM_GRID_X_OFFSET + flameIndexStr);
      res.add(PARAM_GRID_Y_OFFSET + flameIndexStr);
      res.add(PARAM_GRID_X_SIZE + flameIndexStr);
      res.add(PARAM_GRID_Y_SIZE + flameIndexStr);
      res.add(PARAM_BRIGHTNESS_MIN + flameIndexStr);
      res.add(PARAM_BRIGHTNESS_MAX + flameIndexStr);
      res.add(PARAM_BRIGHTNESS_CHANGE + flameIndexStr);
      res.add(PARAM_INSTANCING + flameIndexStr);
    }
    return res.toArray(pParamNames);
  }

  @Override
  public String[] appendRessourceNames(String[] pRessourceNames) {
    List<String> res = new ArrayList<String>(Arrays.asList(pRessourceNames));
    for (int i = 1; i <= size(); i++) {
      String flameIndexStr = getFlameIndexStr(i);
      res.add(RESSOURCE_FLAME + flameIndexStr);
      res.add(RESSOURCE_FLAME_PARAM1 + flameIndexStr);
      res.add(RESSOURCE_FLAME_PARAM1_MAP + flameIndexStr);
      res.add(RESSOURCE_FLAME_PARAM2 + flameIndexStr);
      res.add(RESSOURCE_FLAME_PARAM2_MAP + flameIndexStr);
      res.add(RESSOURCE_FLAME_PARAM3 + flameIndexStr);
      res.add(RESSOURCE_FLAME_PARAM3_MAP + flameIndexStr);
    }
    return res.toArray(pRessourceNames);
  }

  private String getFlameIndexStr(int pIndex) {
    String res = String.valueOf(pIndex);
    while (res.length() < 3)
      res = "0" + res;
    return res;
  }

  @Override
  public Object[] appendParamValues(Object[] pParamValues) {
    List<Object> res = new ArrayList<Object>(Arrays.asList(pParamValues));
    for (int i = 0; i < size(); i++) {
      FlameParams params = get(i);
      res.add(params.getSize());
      res.add(params.getSizeVar());
      res.add(params.getRotateAlpha());
      res.add(params.getRotateAlphaVar());
      res.add(params.getRotateAlphaSpeed());
      res.add(params.getRotateAlphaSpeedVar());
      res.add(params.getRotateBeta());
      res.add(params.getRotateBetaVar());
      res.add(params.getRotateBetaSpeed());
      res.add(params.getRotateBetaSpeedVar());
      res.add(params.getRotateGamma());
      res.add(params.getRotateGammaVar());
      res.add(params.getRotateGammaSpeed());
      res.add(params.getRotateGammaSpeedVar());
      res.add(params.getSpeedX());
      res.add(params.getSpeedXVar());
      res.add(params.getSpeedY());
      res.add(params.getSpeedYVar());
      res.add(params.getSpeedZ());
      res.add(params.getSpeedZVar());
      res.add(params.getRadialAcceleration());
      res.add(params.getRadialAccelerationVar());
      res.add(params.getTangentialAcceleration());
      res.add(params.getTangentialAccelerationVar());
      res.add(params.getCentreX());
      res.add(params.getCentreY());
      res.add(params.getMinVal());
      res.add(params.getMaxVal());
      res.add(params.getWeight());
      res.add(params.getFlameParam1Min());
      res.add(params.getFlameParam1Max());
      res.add(params.getFlameParam2Min());
      res.add(params.getFlameParam2Max());
      res.add(params.getFlameParam3Min());
      res.add(params.getFlameParam3Max());
      res.add(params.getPreviewR());
      res.add(params.getPreviewG());
      res.add(params.getPreviewB());
      res.add(params.getGridXOffset());
      res.add(params.getGridYOffset());
      res.add(params.getGridXSize());
      res.add(params.getGridYSize());
      res.add(params.getBrightnessMin());
      res.add(params.getBrightnessMax());
      res.add(params.getBrightnessChange());
      res.add(params.isInstancing() ? 1 : 0);
    }
    return res.toArray(pParamValues);
  }

  @Override
  public boolean setParameter(String pName, double pValue) {
    for (int i = 1; i <= size(); i++) {
      String flameIndexStr = getFlameIndexStr(i);
      if ((PARAM_FLAME_SIZE + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setSize(pValue);
        return true;
      } else if ((PARAM_FLAME_SIZE_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setSizeVar(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_ALPHA + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateAlpha(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_ALPHA_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateAlphaVar(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_ALPHA_SPEED + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateAlphaSpeed(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_ALPHA_SPEED_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateAlphaSpeedVar(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_BETA + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateBeta(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_BETA_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateBetaVar(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_BETA_SPEED + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateBetaSpeed(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_BETA_SPEED_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateBetaSpeedVar(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_GAMMA + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateGamma(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_GAMMA_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateGammaVar(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_GAMMA_SPEED + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateGammaSpeed(pValue);
        return true;
      } else if ((PARAM_FLAME_ROTATE_GAMMA_SPEED_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRotateGammaSpeedVar(pValue);
        return true;
      } else if ((PARAM_FLAME_SPEED_X + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setSpeedX(pValue);
        return true;
      } else if ((PARAM_FLAME_RADIAL_ACCEL + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRadialAcceleration(pValue);
        return true;
      } else if ((PARAM_FLAME_RADIAL_ACCEL_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setRadialAccelerationVar(pValue);
        return true;
      } else if ((PARAM_FLAME_TANGENTIAL_ACCEL + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setTangentialAcceleration(pValue);
        return true;
      } else if ((PARAM_FLAME_TANGENTIAL_ACCEL_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setTangentialAccelerationVar(pValue);
        return true;
      } else if ((PARAM_FLAME_SPEED_X_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setSpeedXVar(pValue);
        return true;
      } else if ((PARAM_FLAME_SPEED_Y + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setSpeedY(pValue);
        return true;
      } else if ((PARAM_FLAME_SPEED_Y_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setSpeedYVar(pValue);
        return true;
      } else if ((PARAM_FLAME_SPEED_Z + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setSpeedZ(pValue);
        return true;
      } else if ((PARAM_FLAME_SPEED_Z_VAR + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setSpeedZVar(pValue);
        return true;
      } else if ((PARAM_FLAME_CENTRE_X + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setCentreX(pValue);
        return true;
      } else if ((PARAM_FLAME_CENTRE_Y + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setCentreY(pValue);
        return true;
      } else if ((PARAM_FLAME_WEIGHT + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setWeight(VariationFunc.limitVal(pValue, 0.0, 100.0));
        return true;
      } else if ((PARAM_FLAME_MINVAL + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setMinVal(VariationFunc.limitVal(pValue, 0.0, 100.0));
        return true;
      } else if ((PARAM_FLAME_MAXVAL + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setMaxVal(VariationFunc.limitVal(pValue, 0.0, 100.0));
        return true;
      } else if ((PARAM_FLAME_PARAM1_MIN + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setFlameParam1Min(pValue);
        return true;
      } else if ((PARAM_FLAME_PARAM1_MAX + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setFlameParam1Max(pValue);
        return true;
      } else if ((PARAM_FLAME_PARAM2_MIN + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setFlameParam2Min(pValue);
        return true;
      } else if ((PARAM_FLAME_PARAM2_MAX + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setFlameParam2Max(pValue);
        return true;
      } else if ((PARAM_FLAME_PARAM3_MIN + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setFlameParam3Min(pValue);
        return true;
      } else if ((PARAM_FLAME_PARAM3_MAX + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setFlameParam3Max(pValue);
        return true;
      } else if ((PARAM_PREVIEW_R + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setPreviewR(VariationFunc.limitIntVal(Tools.FTOI(pValue), 0, 255));
        return true;
      } else if ((PARAM_PREVIEW_G + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setPreviewG(VariationFunc.limitIntVal(Tools.FTOI(pValue), 0, 255));
        return true;
      } else if ((PARAM_PREVIEW_B + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setPreviewB(VariationFunc.limitIntVal(Tools.FTOI(pValue), 0, 255));
        return true;
      } else if ((PARAM_GRID_X_OFFSET + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setGridXOffset(Tools.FTOI(pValue));
        return true;
      } else if ((PARAM_GRID_Y_OFFSET + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setGridYOffset(Tools.FTOI(pValue));
        return true;
      } else if ((PARAM_GRID_X_SIZE + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setGridXSize(Tools.FTOI(pValue));
        return true;
      } else if ((PARAM_GRID_Y_SIZE + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setGridYSize(Tools.FTOI(pValue));
        return true;
      } else if ((PARAM_BRIGHTNESS_MIN + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setBrightnessMin(pValue);
        return true;
      } else if ((PARAM_BRIGHTNESS_MAX + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setBrightnessMax(pValue);
        return true;
      } else if ((PARAM_BRIGHTNESS_CHANGE + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setBrightnessChange(pValue);
        return true;
      } else if ((PARAM_INSTANCING + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setInstancing(Tools.FTOI(pValue) == 1);
        return true;
      }
    }
    return false;
  }

  public boolean setRessource(String pName, byte[] pValue) {
    for (int i = 1; i <= size(); i++) {
      String flameIndexStr = getFlameIndexStr(i);
      if ((RESSOURCE_FLAME + flameIndexStr).equalsIgnoreCase(pName)) {
        String flameXML = pValue != null ? new String(pValue) : "";
        if (flameXML.length() > 0) {
          parseFlame(flameXML);
          get(i - 1).setFlameXML(flameXML);
        } else {
          get(i - 1).setFlameXML(null);
        }
        return true;
      } else if ((RESSOURCE_FLAME_PARAM1 + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setFlameParam1(pValue != null ? new String(pValue) : "");
        return true;
      } else if ((RESSOURCE_FLAME_PARAM1_MAP + flameIndexStr).equalsIgnoreCase(pName)) {
        String filename = pValue != null ? new String(pValue) : "";
        SimpleImage map = null;
        if (filename.length() > 0) {
          try {
            WFImage img = RessourceManager.getImage(filename);
            if (img.getImageWidth() < 8 || img.getImageHeight() < 8 || !(img instanceof SimpleImage)) {
              throw new Exception("Invalid param map");
            }
            map = (SimpleImage) img;
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        get(i - 1).setFlameParamMap1Filename(map != null ? filename : null);
        return true;
      } else if ((RESSOURCE_FLAME_PARAM2 + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setFlameParam2(pValue != null ? new String(pValue) : "");
        return true;
      } else if ((RESSOURCE_FLAME_PARAM2_MAP + flameIndexStr).equalsIgnoreCase(pName)) {
        String filename = pValue != null ? new String(pValue) : "";
        SimpleImage map = null;
        if (filename.length() > 0) {
          try {
            WFImage img = RessourceManager.getImage(filename);
            if (img.getImageWidth() < 8 || img.getImageHeight() < 8 || !(img instanceof SimpleImage)) {
              throw new Exception("Invalid param map");
            }
            map = (SimpleImage) img;
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        get(i - 1).setFlameParamMap2Filename(map != null ? filename : null);
        return true;
      } else if ((RESSOURCE_FLAME_PARAM3 + flameIndexStr).equalsIgnoreCase(pName)) {
        get(i - 1).setFlameParam3(pValue != null ? new String(pValue) : "");
        return true;
      } else if ((RESSOURCE_FLAME_PARAM3_MAP + flameIndexStr).equalsIgnoreCase(pName)) {
        String filename = pValue != null ? new String(pValue) : "";
        SimpleImage map = null;
        if (filename.length() > 0) {
          try {
            WFImage img = RessourceManager.getImage(filename);
            if (img.getImageWidth() < 8 || img.getImageHeight() < 8 || !(img instanceof SimpleImage)) {
              throw new Exception("Invalid param map");
            }
            map = (SimpleImage) img;
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        get(i - 1).setFlameParamMap3Filename(map != null ? filename : null);
        return true;
      }
    }
    return false;
  }

  private void parseFlame(String pFlameXML) {
    try {
      List<Flame> flames = new FlameReader(Prefs.getPrefs()).readFlamesfromXML(pFlameXML);
      if (flames.size() == 0) {
        throw new Exception("Flame is empty");
      }
    } catch (Throwable ex) {
      System.out.println("##############################################################");
      System.out.println(pFlameXML);
      System.out.println("##############################################################");
      throw new RuntimeException(ex);
    }
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    for (int i = 1; i <= size(); i++) {
      String flameIndexStr = getFlameIndexStr(i);
      if ((RESSOURCE_FLAME + flameIndexStr).equalsIgnoreCase(pName)) {
        return RessourceType.BYTEARRAY;
      } else if ((RESSOURCE_FLAME_PARAM1 + flameIndexStr).equalsIgnoreCase(pName)) {
        return RessourceType.BYTEARRAY;
      } else if ((RESSOURCE_FLAME_PARAM1_MAP + flameIndexStr).equalsIgnoreCase(pName)) {
        return RessourceType.IMAGE_FILENAME;
      } else if ((RESSOURCE_FLAME_PARAM2 + flameIndexStr).equalsIgnoreCase(pName)) {
        return RessourceType.BYTEARRAY;
      } else if ((RESSOURCE_FLAME_PARAM2_MAP + flameIndexStr).equalsIgnoreCase(pName)) {
        return RessourceType.IMAGE_FILENAME;
      } else if ((RESSOURCE_FLAME_PARAM3 + flameIndexStr).equalsIgnoreCase(pName)) {
        return RessourceType.BYTEARRAY;
      } else if ((RESSOURCE_FLAME_PARAM3_MAP + flameIndexStr).equalsIgnoreCase(pName)) {
        return RessourceType.IMAGE_FILENAME;
      }
    }
    return null;
  }

  @Override
  public byte[][] appendRessourceValues(byte[][] pRessourceValues) {
    List<byte[]> res = new ArrayList<byte[]>(Arrays.asList(pRessourceValues));
    for (int i = 0; i < size(); i++) {
      FlameParams params = get(i);
      res.add((params.getFlameXML() != null ? params.getFlameXML().getBytes() : null));
      res.add((params.getFlameParam1() != null ? params.getFlameParam1().getBytes() : null));
      res.add((params.getFlameParamMap1Filename() != null ? params.getFlameParamMap1Filename().getBytes() : null));
      res.add((params.getFlameParam2() != null ? params.getFlameParam2().getBytes() : null));
      res.add((params.getFlameParamMap2Filename() != null ? params.getFlameParamMap2Filename().getBytes() : null));
      res.add((params.getFlameParam3() != null ? params.getFlameParam3().getBytes() : null));
      res.add((params.getFlameParamMap3Filename() != null ? params.getFlameParamMap3Filename().getBytes() : null));
    }
    return res.toArray(pRessourceValues);
  }

  @Override
  public String completeImageKey(String pKey) {
    for (int i = 0; i < size(); i++) {
      FlameParams params = get(i);
      pKey += "#" + (params.getFlameXML() != null ? params.getFlameXML().hashCode() : "null") + "#"
              + Tools.doubleToString(params.getWeight()) + "#" + Tools.doubleToString(params.getMinVal()) + "#"
              + Tools.doubleToString(params.getMaxVal()) + "#" + Tools.doubleToString(params.getSize()) + "#"
              + Tools.doubleToString(params.getSizeVar()) + "#" + Tools.doubleToString(params.getRotateAlpha()) + "#"
              + Tools.doubleToString(params.getRotateAlphaVar()) + "#" + Tools.doubleToString(params.getRotateBeta()) + "#"
              + Tools.doubleToString(params.getRotateBetaVar()) + "#" + Tools.doubleToString(params.getRotateGamma()) + "#"
              + Tools.doubleToString(params.getRotateGammaVar()) + "#"
              + params.getFlameParam1() + "#" + params.getFlameParamMap1Filename() + "#"
              + Tools.doubleToString(params.getFlameParam1Min()) + "#" + Tools.doubleToString(params.getFlameParam1Max()) + "#"
              + params.getFlameParam2() + "#" + params.getFlameParamMap2Filename() + "#"
              + Tools.doubleToString(params.getFlameParam2Min()) + "#" + Tools.doubleToString(params.getFlameParam2Max()) + "#"
              + params.getFlameParam3() + "#" + params.getFlameParamMap3Filename() + "#"
              + Tools.doubleToString(params.getFlameParam3Min()) + "#" + Tools.doubleToString(params.getFlameParam3Max())
              + params.getRotateAlphaSpeed() + "#" + params.getRotateAlphaSpeedVar() + "#" + params.getRotateBetaSpeed() + "#"
              + params.getRotateBetaSpeedVar() + "#" + params.getRotateGammaSpeed() + "#" + params.getRotateGammaSpeedVar() + "#" + params.getSpeedX() + "#"
              + params.getSpeedXVar() + "#" + params.getSpeedY() + "#" + params.getSpeedYVar() + "#" + params.getSpeedZ() + "#" + params.getSpeedZVar() + "#"
              + params.getRadialAcceleration() + "#" + params.getRadialAccelerationVar() + "#" + params.getTangentialAcceleration() + "#" + params.getTangentialAccelerationVar() + "#"
              + params.getBrightnessMin() + "#" + params.getBrightnessMax() + "#" + params.getBrightnessChange() + "#" + (params.isInstancing() ? 1 : 0);

    }
    return pKey;
  }

  @Override
  public String completeParticleKey(String pKey) {
    return completeImageKey(pKey);
  }

  public int getFlameIndex(double pRefValue) {
    double dx = Double.MAX_VALUE;
    int res = -1;
    for (int i = 0; i < size(); i++) {
      FlameParams params = get(i);
      if (params.getFlameXML() != null && params.getFlameXML().length() > 0) {
        if (pRefValue >= params.getMinVal() && pRefValue <= params.getMaxVal()) {
          double dist = params.getMaxVal() - params.getMinVal();
          if (dist < dx) {
            res = i;
            dx = dist;
          }
        }
      }
    }
    //    System.out.println(pRefValue + "#" + res);
    return res;
  }

  public String paramName(String pPrefix, int pIndex) {
    return pPrefix + getFlameIndexStr(pIndex);
  }

}
