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

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.motion.MotionCurve;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class IFlamesAnimator implements Serializable {
  private static final long serialVersionUID = 1L;
  private final List<Particle> particles;
  private final MotionParams motionParams;
  private final Map<String, MotionCurve> motionCurves;
  private boolean useMotionStore = true;
  private transient IFlamesAnimatorMotionStore motionStore;

  private static final float DT = 0.0025f;

  public IFlamesAnimator(List<Particle> pParticles, MotionParams pMotionParams, Map<String, MotionCurve> pMotionCurves, IFlamesAnimatorMotionStore pMotionStore) {
    particles = pParticles;
    motionParams = pMotionParams;
    motionCurves = pMotionCurves;
    motionStore = pMotionStore;
  }

  private Vector getForce(float pLocalTime) {
    return new Vector(evalProperty((float) motionParams.getForceX0(), MotionParams.PARAM_FORCE_X0, pLocalTime),
            evalProperty((float) motionParams.getForceY0(), MotionParams.PARAM_FORCE_Y0, pLocalTime),
            evalProperty((float) motionParams.getForceZ0(), MotionParams.PARAM_FORCE_Z0, pLocalTime));
  }

  private Vector getForceCentre(float pLocalTime) {
    return new Vector(evalProperty((float) motionParams.getForceCentreX(), MotionParams.PARAM_FORCE_CENTRE_X, pLocalTime),
            evalProperty((float) motionParams.getForceCentreY(), MotionParams.PARAM_FORCE_CENTRE_Y, pLocalTime),
            evalProperty((float) motionParams.getForceCentreZ(), MotionParams.PARAM_FORCE_CENTRE_Z, pLocalTime));
  }

  private float evalProperty(float pStaticValue, String pPropertyname, float pLocalTime) {
    MotionCurve curve = motionCurves != null ? motionCurves.get(pPropertyname) : null;
    if (curve != null && curve.isEnabled()) {
      return (float) AnimationService.evalCurve(localTimeToFrame(pLocalTime), curve);
    } else {
      return pStaticValue;
    }
  }

  private float localTimeToFrame(float pLocalTime) {
    return pLocalTime * 100.0f;
  }

  public void animate(float pLocalTime) {
    float dt = DT;
    float tMax = pLocalTime;
    if (tMax < 0.0) {
      tMax = 0.0f;
    }

    float t = motionStore.getMaxStoredMotionTime(tMax, dt);
    if (t > MathLib.EPSILON) {
      motionStore.readFromStore(t, dt, particles);
      while (t < tMax) {
        computeNextTimeStep(t, dt);
        t += dt;
      }
    } else {
      for (Particle particle : particles) {
        particle.reset();
      }
      while (t < tMax) {
        computeNextTimeStep(t, dt);
        t += dt;
      }
    }
  }

  private void computeNextTimeStep(float t, float dt) {
    for (Particle particle : particles) {
      Vector force = getForce(t);
      particle.incSpeed(VectorMath.multiply(dt, force));

      float radialAccel = particle.getRadialAcceleration() * dt;
      float tangentAccel = particle.getTangentialAcceleration() * dt;
      if (MathLib.fabs(radialAccel) > MathLib.EPSILON || MathLib.fabs(tangentAccel) > MathLib.EPSILON) {
        Vector centre = getForceCentre(t);
        Vector normal = VectorMath.normalize(VectorMath.normal(particle.getPosition(), centre));
        if (MathLib.fabs(radialAccel) > MathLib.EPSILON) {
          particle.incSpeed(VectorMath.multiply(radialAccel, normal));
        }
        if (MathLib.fabs(tangentAccel) > MathLib.EPSILON) {
          Vector tangent = VectorMath.normalize(VectorMath.tangent(particle.getPosition(), centre));
          particle.incSpeed(VectorMath.multiply(tangentAccel, tangent));
        }
      }

      particle.incPosition(VectorMath.multiply(dt, particle.getSpeed()));
      particle.incRotation(VectorMath.multiply(dt, particle.getRotationSpeed()));

      particle.decLife(dt);

    }
    storeMotion(t, dt);
  }

  private void storeMotion(float t, float dt) {
    if (useMotionStore) {
      motionStore.storeMotion(t, dt, particles);
    }
  }
}
