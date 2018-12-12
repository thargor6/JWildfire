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

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.create.tina.variation.RessourceType;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

import java.util.List;
import java.util.Map;

public class IFlamesFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final int MAX_FLAME_COUNT = 6;
  private ImageParams imageParams = new ImageParams();
  private MotionParams motionParams = new MotionParams();
  private FlameParamsList flameParams = FlameParamsList.createFlameParams(MAX_FLAME_COUNT);

  private final String[] paramNames = flameParams.appendParamNames(motionParams.appendParamNames(imageParams.appendParamNames(new String[0])));

  private final String[] ressourceNames = flameParams.appendRessourceNames(motionParams.appendRessourceNames(imageParams.appendRessourceNames(new String[0])));

  // derived params
  private final Pixel toolPixel = new Pixel();
  private int flameIdx;
  private AbstractRandomGenerator randGen;

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return flameParams.appendParamValues(motionParams.appendParamValues(imageParams.appendParamValues(new Object[0])));
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (!imageParams.setParameter(pName, pValue) && !motionParams.setParameter(pName, pValue) && !flameParams.setParameter(pName, pValue))
      throw new IllegalArgumentException(pName);
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    imageParams.init(pContext);

    String key = genImageKey();
    flameList = (List<BaseFlame>) RessourceManager.getRessource(key);
    if (flameList == null) {
      System.out.println("FGET " + key);
      long t0 = System.currentTimeMillis();
      RessourceManager.clearRessources(KEY_PREFIX + "#" + imageParams.getId());

      flameList = new BaseFlameListCreator(flameParams, motionParams, imageParams).calcBaseFlameList(pContext, imageParams.getColorMap());
      RessourceManager.putRessource(key, flameList);
      System.out.println("NPUT " + key);
      long t1 = System.currentTimeMillis();
      if ((t1 - t0) > 500) {
        System.out.println("IFLAMES: " + flameList.size() + " flames created in " + (t1 - t0) / 1000.0 + "s");
      }
    }

    String particleKey = genParticleKey();
    String particleListKey = particleKey + "#" + Tools.doubleToString(motionParams.getTime());
    particleLst = (List<Particle>) RessourceManager.getRessource(particleListKey);
    if (particleLst == null) {
      long t0 = System.currentTimeMillis();
      IFlamesAnimatorMotionStore motionStore = getMotionStore(particleKey);
      particleLst = new ParticleListCreator(flameList, motionParams, collectMotionCurves(pXForm), motionStore).createParticleList((float) motionParams.getTime());
      RessourceManager.putRessource(particleListKey, particleLst);
      long t1 = System.currentTimeMillis();
      //      if ((t1 - t0) > 500) {
      System.out.println("IFLAMES: " + flameList.size() + " flames animated in " + (t1 - t0) / 1000.0 + "s (t=" + motionParams.getTime() + ")");
      //      }
    }
    randGen = new MarsagliaRandomGenerator();
    randGen.randomize(System.currentTimeMillis());

    flameIdx = flameList != null && flameList.size() > 0 ? randGen.random(flameList.size()) : 0;
  }

  private IFlamesAnimatorMotionStore getMotionStore(String particleKey) {
    IFlamesAnimatorMotionStore motionStore;
    {
      String storeKey = particleKey + "#motionStore";
      motionStore = (IFlamesAnimatorMotionStore) RessourceManager.getRessource(storeKey);
      if (motionStore == null) {
        motionStore = new IFlamesAnimatorMotionStore();
        RessourceManager.putRessource(storeKey, motionStore);
      }
    }
    return motionStore;
  }

  private Map<String, MotionCurve> collectMotionCurves(XForm pXForm) {
    for (int i = 0; i < pXForm.getVariationCount(); i++) {
      if (pXForm.getVariation(i).getFunc() == this) {
        return pXForm.getVariation(i).getClonedMotionCurves();
      }
    }
    throw new IllegalStateException("IFlames variation not found!");
  }

  public final static String KEY_PREFIX = "IFLAME_BASE_FLAMES#";

  private String genImageKey() {
    return flameParams.completeImageKey(motionParams.completeImageKey(imageParams.completeImageKey(KEY_PREFIX)));
  }

  private String genParticleKey() {
    return flameParams.completeParticleKey(motionParams.completeParticleKey(imageParams.completeParticleKey(KEY_PREFIX)));
  }

  private List<BaseFlame> flameList;
  private List<Particle> particleLst;

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return flameParams.appendRessourceValues(motionParams.appendRessourceValues(imageParams.appendRessourceValues(new byte[0][])));
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (!imageParams.setRessource(pName, pValue) && !motionParams.setRessource(pName, pValue) && !flameParams.setRessource(pName, pValue))
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    RessourceType res;
    res = imageParams.getRessourceType(pName);
    if (res != null) {
      return res;
    }

    res = motionParams.getRessourceType(pName);
    if (res != null) {
      return res;
    }

    res = flameParams.getRessourceType(pName);
    if (res != null) {
      return res;
    }

    throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "iflames_wf";
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (imageParams.getImage_brightness() < MathLib.EPSILON || randGen.random() < imageParams.getIFlame_density()) {
      if (flameList.size() == 0) {
        return;
      }
      //      int idx = randGen.random(flameList.size());
      int idx;
      if (randGen.random() < 0.5) {
        idx = randGen.random(flameList.size());
      } else {
        idx = flameIdx++;
        if (flameIdx >= flameList.size()) {
          flameIdx = 0;
        }
      }
      BaseFlame baseFlame = flameList.get(idx);
      Particle particle = particleLst.get(idx);
      if (baseFlame.getIterator() != null) {
        if (motionParams.getPreview() == 1) {
          XYZPoint dst = new XYZPoint();
          pVarTP.x += dst.x + particle.getPosition().getX();
          pVarTP.y += dst.y + particle.getPosition().getY();
          pVarTP.z += dst.z + particle.getPosition().getZ();
          pVarTP.rgbColor = true;
          pVarTP.redColor = baseFlame.getPreviewColorR() * imageParams.getIFlame_brightness() * baseFlame.getBrightness();
          pVarTP.greenColor = baseFlame.getPreviewColorG() * imageParams.getIFlame_brightness() * baseFlame.getBrightness();
          pVarTP.blueColor = baseFlame.getPreviewColorB() * imageParams.getIFlame_brightness() * baseFlame.getBrightness();
        } else {
          XYZPoint src = new XYZPoint();
          XYZPoint dst = new XYZPoint();
          baseFlame.getIterator().iterate(src, dst, baseFlame.getSize(), particle.getRotation().getX(), particle.getRotation().getY(), particle.getRotation().getZ());
          pVarTP.x += dst.x + particle.getPosition().getX();
          pVarTP.y += dst.y + particle.getPosition().getY();
          pVarTP.z += dst.z + particle.getPosition().getZ();
          pVarTP.color = dst.color;
          pVarTP.rgbColor = true;
          pVarTP.redColor = baseFlame.getR() * imageParams.getIFlame_brightness() * baseFlame.getBrightness();
          pVarTP.greenColor = baseFlame.getG() * imageParams.getIFlame_brightness() * baseFlame.getBrightness();
          pVarTP.blueColor = baseFlame.getB() * imageParams.getIFlame_brightness() * baseFlame.getBrightness();
        }
      } else {
        addImage(pVarTP, randGen);
      }
    } else {
      addImage(pVarTP, randGen);
    }
  }

  private void addImage(XYZPoint pVarTP, AbstractRandomGenerator randGen) {
    double xCoord = randGen.random(imageParams.getImgWidth()) + (0.5 - randGen.random());
    double yCoord = randGen.random(imageParams.getImgHeight()) + (0.5 - randGen.random());
    double dx = xCoord * imageParams.getScaleX() / (double) (imageParams.getImgWidth() - 1) + imageParams.getOffsetX();
    double dy = yCoord * imageParams.getScaleY() / (double) (imageParams.getImgHeight() - 1) + imageParams.getOffsetY();
    double dz = imageParams.getOffsetZ();

    pVarTP.x += dx;
    pVarTP.y += dy;
    pVarTP.z += dz;

    toolPixel.setARGBValue(((SimpleImage) imageParams.getColorMap()).getARGBValueIgnoreBounds((int) xCoord, (int) yCoord));
    int luR = toolPixel.r;
    int luG = toolPixel.g;
    int luB = toolPixel.b;
    pVarTP.doHide = false;
    if (luR == 0 && luG == 0 && luB == 0) {
      pVarTP.doHide = true;
      return;
    }

    toolPixel.setARGBValue(((SimpleImage) imageParams.getColorMap()).getARGBValueIgnoreBounds(((int) xCoord) + 1, (int) yCoord));
    int ruR = toolPixel.r;
    int ruG = toolPixel.g;
    int ruB = toolPixel.b;
    toolPixel.setARGBValue(((SimpleImage) imageParams.getColorMap()).getARGBValueIgnoreBounds((int) xCoord, ((int) yCoord) + 1));
    int lbR = toolPixel.r;
    int lbG = toolPixel.g;
    int lbB = toolPixel.b;
    toolPixel.setARGBValue(((SimpleImage) imageParams.getColorMap()).getARGBValueIgnoreBounds(((int) xCoord) + 1, ((int) yCoord) + 1));
    int rbR = toolPixel.r;
    int rbG = toolPixel.g;
    int rbB = toolPixel.b;

    double r = GfxMathLib.blerp(luR, ruR, lbR, rbR, MathLib.frac(xCoord), MathLib.frac(yCoord));
    double g = GfxMathLib.blerp(luG, ruG, lbG, rbG, MathLib.frac(xCoord), MathLib.frac(yCoord));
    double b = GfxMathLib.blerp(luB, ruB, lbB, rbB, MathLib.frac(xCoord), MathLib.frac(yCoord));

    pVarTP.rgbColor = true;
    pVarTP.redColor = r * imageParams.getImage_brightness();
    pVarTP.greenColor = g * imageParams.getImage_brightness();
    pVarTP.blueColor = b * imageParams.getImage_brightness();
  }

  public ImageParams getImageParams() {
    return imageParams;
  }

  public MotionParams getMotionParams() {
    return motionParams;
  }

  public FlameParams getFlameParams(int pIndex) {
    return flameParams.get(pIndex);
  }

}
