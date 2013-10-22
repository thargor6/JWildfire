package org.jwildfire.create.tina.dance.motion;

import java.io.Serializable;

import org.jwildfire.base.ManagedObject;
import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.DancingFlameProject;

public abstract class FlamePreprocessor extends ManagedObject implements Serializable {
  private static final long serialVersionUID = 1L;
  @Property(description = "Start frame", category = PropertyCategory.GENERAL)
  protected Integer startFrame;
  @Property(description = "End frame", category = PropertyCategory.GENERAL)
  protected Integer endFrame;

  public static int computeFrame(long pTime, int pFPS) {
    return Tools.FTOI(pTime / 1000.0 * (double) pFPS + 0.5);
  }

  protected boolean isActive(long pTime, int pFPS) {
    int frame = computeFrame(pTime, pFPS);
    return (startFrame == null || frame >= startFrame) && (endFrame == null || frame <= endFrame || endFrame <= 0);
  }

  public Integer getStartFrame() {
    return startFrame;
  }

  public void setStartFrame(Integer startFrame) {
    this.startFrame = startFrame;
  }

  public Integer getEndFrame() {
    return endFrame;
  }

  public void setEndFrame(Integer endFrame) {
    this.endFrame = endFrame;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " - " + hashCode();
  }

  public abstract Flame preprocessFlame(DancingFlameProject pProject, Flame pFlame);

}
