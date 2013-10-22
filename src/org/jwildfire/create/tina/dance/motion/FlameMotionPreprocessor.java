package org.jwildfire.create.tina.dance.motion;

import java.util.List;

import org.jwildfire.create.tina.dance.DancingFlameProject;

public interface FlameMotionPreprocessor {
  public abstract List<Motion> preprocessMotions(DancingFlameProject pProject, List<Motion> pMotions);

}
