/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.animate;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;

public class FlameMovie {
  public static final int SCRIPT_COUNT = 12;

  private final Prefs prefs;
  private final GlobalScript globalScripts[] = initGlobalScripts();
  private final XFormScript xFormScripts[] = initXFormScripts();
  private final List<Motion> motions = new ArrayList<Motion>();
  private final List<FlameMoviePart> parts = new ArrayList<FlameMoviePart>();
  private int frameWidth = 640;
  private int frameHeight = 480;
  private double framesPerSecond = 12.0;
  private int motionBlurLength = 12;
  private double motionBlurTimeStep = 0.015;
  private String name = "";
  private SequenceOutputType sequenceOutputType = SequenceOutputType.FLAMES;
  private int quality = 100;
  private boolean compat = false;

  public FlameMovie(Prefs pPrefs) {
    prefs = pPrefs;
  }

  private XFormScript[] initXFormScripts() {
    XFormScript[] res = new XFormScript[SCRIPT_COUNT];
    for (int i = 0; i < res.length; i++) {
      res[i] = new XFormScript(XFormScriptType.NONE, 1.0);
    }
    return res;
  }

  private GlobalScript[] initGlobalScripts() {
    GlobalScript[] res = new GlobalScript[SCRIPT_COUNT];
    for (int i = 0; i < res.length; i++) {
      res[i] = new GlobalScript(GlobalScriptType.NONE, 1.0);
    }
    return res;
  }

  public List<Motion> getMotions() {
    return motions;
  }

  public int getFrameCount() {
    int res = 0;
    for (FlameMoviePart part : parts) {
      if (part.getFrameCount() > 0) {
        res += part.getFrameCount();
      }
    }
    return res;
  }

  public void addPart(FlameMoviePart pPart) {
    parts.add(pPart);
  }

  public Flame getFlame(int pFrame) {
    int currFrame = 0;
    for (int i = 0; i < parts.size(); i++) {
      FlameMoviePart part = parts.get(i);
      currFrame += part.getFrameCount();
      if (currFrame >= pFrame) {
        Flame flame1 = part.getFlame().makeCopy();
        int morphFrames = part.getFrameMorphCount();
        if (morphFrames == 0 || pFrame < (currFrame - morphFrames) || i == (parts.size() - 1)) {
          return flame1;
        }
        else {
          Flame flame2 = parts.get(i + 1).getFlame().makeCopy();
          int morphFrame = pFrame - (currFrame - morphFrames);
          //          System.out.println(pFrame + ": " + morphFrame);
          return FlameMorphService.morphFlames(prefs, part.getFlameMorphType(), flame1, flame2, morphFrame, morphFrames, compat);
        }
      }
    }
    return null;
  }

  public Flame createAnimatedFlame(Flame pFlame, int pFrame) {
    Flame res = AnimationService.createFrameFlame(pFrame, getFrameCount(), getFramesPerSecond(), pFlame, getGlobalScripts(), getxFormScripts(), getMotionBlurLength(), getMotionBlurTimeStep(), getFrameWidth(), getFrameHeight(), prefs);
    res.setFrame(pFrame);
    return res;
  }

  public int getFrameWidth() {
    return frameWidth;
  }

  public void setFrameWidth(int frameWidth) {
    this.frameWidth = frameWidth;
  }

  public int getFrameHeight() {
    return frameHeight;
  }

  public void setFrameHeight(int frameHeight) {
    this.frameHeight = frameHeight;
  }

  public double getFramesPerSecond() {
    return framesPerSecond;
  }

  public void setFramesPerSecond(double framesPerSecond) {
    this.framesPerSecond = framesPerSecond;
  }

  public List<FlameMoviePart> getParts() {
    return parts;
  }

  public int getMotionBlurLength() {
    return motionBlurLength;
  }

  public void setMotionBlurLength(int motionBlurLength) {
    this.motionBlurLength = motionBlurLength;
  }

  public double getMotionBlurTimeStep() {
    return motionBlurTimeStep;
  }

  public void setMotionBlurTimeStep(double motionBlurTimeStep) {
    this.motionBlurTimeStep = motionBlurTimeStep;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name != null ? name : "";
  }

  public GlobalScript[] getGlobalScripts() {
    return globalScripts;
  }

  public XFormScript[] getxFormScripts() {
    return xFormScripts;
  }

  public SequenceOutputType getSequenceOutputType() {
    return sequenceOutputType;
  }

  public void setSequenceOutputType(SequenceOutputType pSequenceOutputType) {
    sequenceOutputType = pSequenceOutputType;
  }

  public int getQuality() {
    return quality;
  }

  public void setQuality(int pQuality) {
    quality = pQuality;
  }
  
  public boolean getCompat() {
    return compat;
  }
  
  public void setCompat(boolean pCompat) {
    compat = pCompat;
  }
}
