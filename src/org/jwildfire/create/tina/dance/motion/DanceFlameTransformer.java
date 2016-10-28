/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.create.tina.dance.motion;

import java.util.List;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.DancingFlame;
import org.jwildfire.create.tina.dance.DancingFlameProject;
import org.jwildfire.create.tina.dance.model.AnimationModelService;

public class DanceFlameTransformer {
  private final DancingFlameProject project;

  public DanceFlameTransformer(DancingFlameProject pProject) {
    project = pProject;
  }

  public Flame createTransformedFlame(DancingFlame pFlame, short pFFTData[], long pTime, int pFPS) {
    Flame res = pFlame.getFlame().makeCopy();
    List<Motion> motions = project.getMotions(res);
    Flame refFlame = res.makeCopy();

    for (FlamePreprocessor preprocessor : project.getPreprocessors()) {
      if (preprocessor.isActive(pTime, pFPS)) {
        res = preprocessor.preprocessFlame(project, res).makeCopy();
        if (preprocessor instanceof FlameMotionPreprocessor) {
          motions = ((FlameMotionPreprocessor) preprocessor).preprocessMotions(project, motions);
          refFlame = res.makeCopy();
        }
      }
    }

    for (Motion motion : motions) {
      if (motion.getParent() == null && motion.isActive(pTime, pFPS)) {
        double value = 0.0;
        int iter = 0;
        Motion currMotion = motion;
        while (currMotion != null) {
          value += currMotion.computeValue(pFFTData, pTime, pFPS);
          Motion refMotion = currMotion;
          currMotion = null;
          for (Motion nextMotion : motions) {
            if (nextMotion.isActive(pTime, pFPS) && nextMotion.getParent() == refMotion) {
              currMotion = nextMotion;
              break;
            }
          }
          iter++;
          if (iter > 100) {
            throw new RuntimeException("Probably endless loop detected");
          }
        }
        for (MotionLink link : motion.getMotionLinks()) {
          if (link.getProperyPath().getFlame().isEqual(refFlame)) {
            AnimationModelService.setFlameProperty(res, link.getProperyPath(), value);
          }
        }
      }
    }
    return res;
  }
}
