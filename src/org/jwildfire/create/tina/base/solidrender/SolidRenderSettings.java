package org.jwildfire.create.tina.base.solidrender;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.animate.FlameMorphService;
import org.jwildfire.create.tina.edit.Assignable;

@SuppressWarnings("serial")
public class SolidRenderSettings implements Assignable<SolidRenderSettings>, Serializable {
  private boolean solidRenderingEnabled = false;
  private boolean lightsEnabled = true;
  private boolean ssaoEnabled = true;
  private double ssaoIntensity = 1.0;
  private boolean hardShadowsEnabled = false;
  private final List<MaterialSettings> materials = new ArrayList<>();
  private final List<PointLight> lights = new ArrayList<>();

  public void setupDefaults() {
    {
      MaterialSettings material = new MaterialSettings();
      material.setAmbient(0.6);
      material.setDiffuse(0.5);
      material.setPhong(0.7);
      material.setPhongSize(36.0);
      material.setPhongRed(1.0);
      material.setPhongGreen(0.9);
      material.setPhongBlue(0.3);
      materials.add(material);
    }

    {
      MaterialSettings material = new MaterialSettings();
      material.setAmbient(0.9);
      material.setDiffuse(0.3);
      material.setPhong(0.7);
      material.setPhongSize(36.0);
      material.setPhongRed(1.0);
      material.setPhongGreen(0.9);
      material.setPhongBlue(0.3);
      materials.add(material);
    }

    {
      MaterialSettings material = new MaterialSettings();
      material.setAmbient(0.9);
      material.setDiffuse(0.3);
      material.setPhong(1.0);
      material.setPhongSize(24.0);
      material.setPhongRed(1.0);
      material.setPhongGreen(1.0);
      material.setPhongBlue(1.0);
      materials.add(material);
    }

    {
      MaterialSettings material = new MaterialSettings();
      material.setAmbient(1.0);
      material.setDiffuse(0.0);
      material.setPhong(0.0);
      material.setPhongSize(24.0);
      material.setPhongRed(1.0);
      material.setPhongGreen(1.0);
      material.setPhongBlue(1.0);
      materials.add(material);
    }

    {
      MaterialSettings material = new MaterialSettings();
      material.setAmbient(0.7);
      material.setDiffuse(0.3);
      material.setPhong(0.4);
      material.setPhongSize(24.0);
      material.setPhongRed(1.0);
      material.setPhongGreen(1.0);
      material.setPhongBlue(1.0);
      materials.add(material);
    }

    {
      MaterialSettings material = new MaterialSettings();
      material.setAmbient(0.3);
      material.setDiffuse(0.7);
      material.setPhong(0.6);
      material.setPhongSize(40.0);
      material.setPhongRed(1.0);
      material.setPhongGreen(1.0);
      material.setPhongBlue(1.0);
      materials.add(material);
    }

    lights.clear();
    {
      PointLight light = new PointLight();
      lights.add(light);
      light.setPosition(-0.15, 0.12, 0.7);
      light.setIntensity(1.0);
      light.setRed(1.0);
      light.setGreen(1.0);
      light.setBlue(1.0);
      light.setCastShadows(true);
    }
    {
      PointLight light = new PointLight();
      lights.add(light);
      light.setPosition(0.25, -0.22, 0.8);
      light.setIntensity(0.5);
      light.setRed(1.0);
      light.setGreen(1.0);
      light.setBlue(1.0);
      light.setCastShadows(false);
    }
  }

  public MaterialSettings getMaterial(double materialIdx) {
    //    System.out.println(materialIdx);
    if (materialIdx < 0)
      return null;

    double idx = materialIdx * materials.size();
    int fromIdx = (int) idx;
    int toIdx = fromIdx + 1;
    double scl = MathLib.frac(idx);
    double EPS = 0.01;

    //    System.out.println(fromIdx + " " + toIdx + " " + scl + " " + idx);
    if (fromIdx >= 0 && toIdx < materials.size()) {
      if (scl < EPS) {
        return materials.get(fromIdx);
      }
      else if (scl > 1.0 - EPS) {
        return materials.get(toIdx);
      }
      else {
        return morphMaterial(materials.get(fromIdx), materials.get(toIdx), scl);
      }
    }
    else {
      return null;
    }
  }

  private MaterialSettings morphMaterial(final MaterialSettings from, final MaterialSettings to, final double scl) {
    MaterialSettings morph = new MaterialSettings();
    morph.setDiffuse(FlameMorphService.morphValue(from.getDiffuse(), to.getDiffuse(), scl));
    morph.setAmbient(FlameMorphService.morphValue(from.getAmbient(), to.getAmbient(), scl));
    morph.setPhong(FlameMorphService.morphValue(from.getPhong(), to.getPhong(), scl));
    morph.setPhongSize(FlameMorphService.morphValue(from.getPhongSize(), to.getPhongSize(), scl));
    morph.setPhongRed(FlameMorphService.morphValue(from.getPhongRed(), to.getPhongRed(), scl));
    morph.setPhongGreen(FlameMorphService.morphValue(from.getPhongGreen(), to.getPhongGreen(), scl));
    morph.setPhongBlue(FlameMorphService.morphValue(from.getPhongBlue(), to.getPhongBlue(), scl));
    morph.setLightDiffFunc(new LightDiffFunc() {

      @Override
      public double evaluate(double pCosa) {
        double d1 = from.getLightDiffFunc().evaluate(pCosa);
        double d2 = to.getLightDiffFunc().evaluate(pCosa);
        return FlameMorphService.morphValue(d1, d2, scl);
      }

    });
    return morph;
  }

  public List<PointLight> getLights() {
    return lights;
  }

  public boolean isLightsEnabled() {
    return lightsEnabled;
  }

  public void setLightsEnabled(boolean lightsEnabled) {
    this.lightsEnabled = lightsEnabled;
  }

  public boolean isSsaoEnabled() {
    return ssaoEnabled;
  }

  public void setSsaoEnabled(boolean ssaoEnabled) {
    this.ssaoEnabled = ssaoEnabled;
  }

  public boolean isHardShadowsEnabled() {
    return hardShadowsEnabled;
  }

  public void setHardShadowsEnabled(boolean hardShadowsEnabled) {
    this.hardShadowsEnabled = hardShadowsEnabled;
  }

  public double getSsaoIntensity() {
    return ssaoIntensity;
  }

  public void setSsaoIntensity(double ssaoIntensity) {
    this.ssaoIntensity = ssaoIntensity;
  }

  public boolean isSolidRenderingEnabled() {
    return solidRenderingEnabled;
  }

  public void setSolidRenderingEnabled(boolean solidRenderingEnabled) {
    this.solidRenderingEnabled = solidRenderingEnabled;
  }

  @Override
  public void assign(SolidRenderSettings pSrc) {
    solidRenderingEnabled = pSrc.solidRenderingEnabled;
    lightsEnabled = pSrc.lightsEnabled;
    ssaoEnabled = pSrc.ssaoEnabled;
    ssaoIntensity = pSrc.ssaoIntensity;
    hardShadowsEnabled = pSrc.hardShadowsEnabled;
    materials.clear();
    for (MaterialSettings src : pSrc.getMaterials()) {
      MaterialSettings dst = new MaterialSettings();
      dst.assign(src);
      materials.add(dst);
    }
    for (PointLight src : pSrc.getLights()) {
      PointLight dst = new PointLight();
      dst.assign(src);
      lights.add(dst);
    }
  }

  @Override
  public SolidRenderSettings makeCopy() {
    SolidRenderSettings res = new SolidRenderSettings();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(SolidRenderSettings pSrc) {
    // do not care then solid rendering is disable 
    if (!solidRenderingEnabled && !pSrc.solidRenderingEnabled) {
      return true;
    }

    if (solidRenderingEnabled != pSrc.solidRenderingEnabled || lightsEnabled != pSrc.lightsEnabled ||
        ssaoEnabled != pSrc.ssaoEnabled || hardShadowsEnabled != pSrc.hardShadowsEnabled ||
        fabs(ssaoIntensity - pSrc.ssaoIntensity) > EPSILON) {
      return false;
    }
    if (materials.size() != pSrc.materials.size()) {
      return false;
    }
    if (lights.size() != pSrc.lights.size()) {
      return false;
    }
    for (int i = 0; i < materials.size(); i++) {
      if (!materials.get(i).isEqual(pSrc.getMaterials().get(i))) {
        return false;
      }
    }
    for (int i = 0; i < lights.size(); i++) {
      if (!lights.get(i).isEqual(pSrc.getLights().get(i))) {
        return false;
      }
    }
    return true;
  }

  public List<MaterialSettings> getMaterials() {
    return materials;
  }

}
