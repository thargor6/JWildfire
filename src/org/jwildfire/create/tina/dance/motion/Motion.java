/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.ManagedObject;
import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.dance.model.FlamePropertyPath;

public abstract class Motion extends ManagedObject implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<MotionLink> motionLinks = new ArrayList<MotionLink>();

  @Property(description = "Start frame", category = PropertyCategory.GENERAL)
  protected Integer startFrame;
  @Property(description = "End frame", category = PropertyCategory.GENERAL)
  protected Integer endFrame;

  public List<MotionLink> getMotionLinks() {
    return motionLinks;
  }

  public boolean hasLink(FlamePropertyPath pSelPath) {
    return getLink(pSelPath) != null;
  }

  public MotionLink getLink(FlamePropertyPath pSelPath) {
    for (MotionLink link : motionLinks) {
      if (pSelPath.getFlame().isEqual(link.getProperyPath().getFlame()) && pSelPath.getPath().equals(link.getProperyPath())) {
        return link;
      }
    }
    return null;
  }

  public abstract double computeValue(short pFFTData[], long pTime, int pFPS);

  public static int computeFrame(long pTime, int pFPS) {
    return Tools.FTOI(pTime / 1000.0 * (double) pFPS + 0.5);
  }

  protected boolean isActive(long pTime, int pFPS) {
    int frame = computeFrame(pTime, pFPS);
    return (startFrame == null || frame >= startFrame) && (endFrame == null || frame <= endFrame);
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
}
