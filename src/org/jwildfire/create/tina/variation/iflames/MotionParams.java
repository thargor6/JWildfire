package org.jwildfire.create.tina.variation.iflames;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.variation.RessourceType;
import org.jwildfire.create.tina.variation.VariationFunc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MotionParams implements Params, Serializable {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_TIME = "time";
  private static final String PARAM_LIFE = "life";
  private static final String PARAM_LIFE_VAR = "life_var";
  private static final String PARAM_PREVIEW = "preview";
  public static final String PARAM_FORCE_X0 = "force_x0";
  public static final String PARAM_FORCE_Y0 = "force_y0";
  public static final String PARAM_FORCE_Z0 = "force_z0";
  public static final String PARAM_FORCE_CENTRE_X = "force_centre_x";
  public static final String PARAM_FORCE_CENTRE_Y = "force_centre_y";
  public static final String PARAM_FORCE_CENTRE_Z = "force_centre_z";
  private static final String PARAM_FORCE_WAVELEN_X = "force_wavelen_x";
  private static final String PARAM_FORCE_WAVELEN_Y = "force_wavelen_y";
  private static final String PARAM_FORCE_WAVELEN_Z = "force_wavelen_z";

  private double time = 0.0;
  private double life = 600.0;
  private double lifeVar = 0.0;
  private int preview = 0;
  private double forceX0 = 0.0;
  private double forceY0 = 0.1;
  private double forceZ0 = 0.0;
  private double forceCentreX = 0.0;
  private double forceCentreY = 0.0;
  private double forceCentreZ = 0.0;
  private double forceWavelenX = 0.5;
  private double forceWavelenY = 0.5;
  private double forceWavelenZ = 0.5;

  @Override
  public String[] appendParamNames(String[] pParamNames) {
    List<String> res = new ArrayList<String>(Arrays.asList(pParamNames));
    res.add(PARAM_TIME);
    res.add(PARAM_PREVIEW);
    res.add(PARAM_LIFE);
    res.add(PARAM_LIFE_VAR);
    res.add(PARAM_FORCE_X0);
    res.add(PARAM_FORCE_Y0);
    res.add(PARAM_FORCE_Z0);
    res.add(PARAM_FORCE_CENTRE_X);
    res.add(PARAM_FORCE_CENTRE_Y);
    res.add(PARAM_FORCE_CENTRE_Z);
    res.add(PARAM_FORCE_WAVELEN_X);
    res.add(PARAM_FORCE_WAVELEN_Y);
    res.add(PARAM_FORCE_WAVELEN_Z);
    return res.toArray(pParamNames);
  }

  @Override
  public String[] appendRessourceNames(String[] pRessourceNames) {
    List<String> res = new ArrayList<String>(Arrays.asList(pRessourceNames));
    return res.toArray(pRessourceNames);
  }

  @Override
  public Object[] appendParamValues(Object[] pParamValues) {
    List<Object> res = new ArrayList<Object>(Arrays.asList(pParamValues));
    res.add(time);
    res.add(preview);
    res.add(life);
    res.add(lifeVar);
    res.add(forceX0);
    res.add(forceY0);
    res.add(forceZ0);
    res.add(forceCentreX);
    res.add(forceCentreY);
    res.add(forceCentreZ);
    res.add(forceWavelenX);
    res.add(forceWavelenY);
    res.add(forceWavelenZ);
    return res.toArray(pParamValues);
  }

  @Override
  public boolean setParameter(String pName, double pValue) {
    if (PARAM_TIME.equalsIgnoreCase(pName)) {
      time = pValue;
      return true;
    } else if (PARAM_PREVIEW.equalsIgnoreCase(pName)) {
      preview = VariationFunc.limitIntVal(Tools.FTOI(pValue), 0, 1);
      return true;
    } else if (PARAM_LIFE.equalsIgnoreCase(pName)) {
      life = pValue;
      return true;
    } else if (PARAM_LIFE_VAR.equalsIgnoreCase(pName)) {
      lifeVar = pValue;
      if (lifeVar < 0.0) {
        lifeVar = 0.0;
      }
      return true;
    } else if (PARAM_FORCE_X0.equalsIgnoreCase(pName)) {
      forceX0 = pValue;
      return true;
    } else if (PARAM_FORCE_Y0.equalsIgnoreCase(pName)) {
      forceY0 = pValue;
      return true;
    } else if (PARAM_FORCE_Z0.equalsIgnoreCase(pName)) {
      forceZ0 = pValue;
      return true;
    } else if (PARAM_FORCE_CENTRE_X.equalsIgnoreCase(pName)) {
      forceCentreX = pValue;
      return true;
    } else if (PARAM_FORCE_CENTRE_Y.equalsIgnoreCase(pName)) {
      forceCentreY = pValue;
      return true;
    } else if (PARAM_FORCE_CENTRE_Z.equalsIgnoreCase(pName)) {
      forceCentreZ = pValue;
      return true;
    } else if (PARAM_FORCE_WAVELEN_X.equalsIgnoreCase(pName)) {
      forceWavelenX = pValue;
      return true;
    } else if (PARAM_FORCE_WAVELEN_Y.equalsIgnoreCase(pName)) {
      forceWavelenY = pValue;
      return true;
    } else if (PARAM_FORCE_WAVELEN_Z.equalsIgnoreCase(pName)) {
      forceWavelenZ = pValue;
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean setRessource(String pName, byte[] pValue) {
    return false;
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    return null;
  }

  @Override
  public byte[][] appendRessourceValues(byte[][] pRessourceValues) {
    List<byte[]> res = new ArrayList<byte[]>(Arrays.asList(pRessourceValues));
    return res.toArray(pRessourceValues);
  }

  @Override
  public String completeImageKey(String key) {
    return key + "#" + Tools.doubleToString(life) + "#" + Tools.doubleToString(lifeVar) + "#" + preview;
  }

  @Override
  public String completeParticleKey(String pKey) {
    return completeImageKey(pKey) + "#" + Tools.doubleToString(forceX0) + "#" + Tools.doubleToString(forceY0)
            + "#" + Tools.doubleToString(forceZ0) + "#" + Tools.doubleToString(forceCentreX) + "#" + Tools.doubleToString(forceCentreY)
            + "#" + Tools.doubleToString(forceCentreZ) + "#" + Tools.doubleToString(forceWavelenX) + "#" + Tools.doubleToString(forceWavelenY)
            + "#" + Tools.doubleToString(forceWavelenZ);
  }

  public double getTime() {
    return time;
  }

  public double getLife() {
    return life;
  }

  public double getLifeVar() {
    return lifeVar;
  }

  public double getForceX0() {
    return forceX0;
  }

  public double getForceY0() {
    return forceY0;
  }

  public double getForceZ0() {
    return forceZ0;
  }

  public double getForceCentreX() {
    return forceCentreX;
  }

  public double getForceCentreY() {
    return forceCentreY;
  }

  public double getForceCentreZ() {
    return forceCentreZ;
  }

  public double getForceWavelenX() {
    return forceWavelenX;
  }

  public double getForceWavelenY() {
    return forceWavelenY;
  }

  public double getForceWavelenZ() {
    return forceWavelenZ;
  }

  public int getPreview() {
    return preview;
  }

  public void setPreview(int pPreview) {
    preview = pPreview;
  }

  public void setTime(double pTime) {
    time = pTime;
  }

  public void setLife(double pLife) {
    life = pLife;
  }

  public void setLifeVar(double pLifeVar) {
    lifeVar = pLifeVar;
  }

  public void setForceX0(double pForceX0) {
    forceX0 = pForceX0;
  }

  public void setForceY0(double pForceY0) {
    forceY0 = pForceY0;
  }

  public void setForceZ0(double pForceZ0) {
    forceZ0 = pForceZ0;
  }

  public void setForceCentreX(double pForceCentreeX) {
    forceCentreX = pForceCentreeX;
  }

  public void setForceCentreY(double pForceCentreY) {
    forceCentreY = pForceCentreY;
  }

  public void setForceCentreZ(double pForceCentreZ) {
    forceCentreZ = pForceCentreZ;
  }

  public void setForceWavelenX(double pForceWavelenX) {
    forceWavelenX = pForceWavelenX;
  }

  public void setForceWavelenY(double pForceWavelenY) {
    forceWavelenY = pForceWavelenY;
  }

  public void setForceWavelenZ(double pForceWavelenZ) {
    forceWavelenZ = pForceWavelenZ;
  }

}
