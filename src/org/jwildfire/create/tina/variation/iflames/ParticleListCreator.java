package org.jwildfire.create.tina.variation.iflames;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.motion.MotionCurve;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticleListCreator {
  private final List<BaseFlame> pixelLst;
  private final MotionParams motionParams;
  private final Map<String, MotionCurve> motionCurves;
  private final IFlamesAnimatorMotionStore motionStore;

  public ParticleListCreator(List<BaseFlame> pPixelLst, MotionParams pMotionParams, Map<String, MotionCurve> pMotionCurves, IFlamesAnimatorMotionStore pMotionStore) {
    pixelLst = pPixelLst;
    motionParams = pMotionParams;
    motionCurves = pMotionCurves;
    motionStore = pMotionStore;
  }

  public List<Particle> createParticleList(float pTime) {
    List<Particle> res = new ArrayList<Particle>();
    for (BaseFlame pixel : pixelLst) {
      res.add(new Particle(pixel.getMotionProperties()));
    }
    if (pTime > MathLib.EPSILON) {
      new IFlamesAnimator(res, motionParams, motionCurves, motionStore).animate(pTime);
    }
    return res;
  }
}
