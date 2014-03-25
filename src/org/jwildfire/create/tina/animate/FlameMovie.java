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
package org.jwildfire.create.tina.animate;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;

public class FlameMovie {
  private final Prefs prefs;
  private String soundFilename;
  private GlobalScript globalScript1 = new GlobalScript(GlobalScriptType.NONE, 1.0);
  private GlobalScript globalScript2 = new GlobalScript(GlobalScriptType.NONE, 1.0);
  private GlobalScript globalScript3 = new GlobalScript(GlobalScriptType.NONE, 1.0);
  private GlobalScript globalScript4 = new GlobalScript(GlobalScriptType.NONE, 1.0);
  private GlobalScript globalScript5 = new GlobalScript(GlobalScriptType.NONE, 1.0);
  private XFormScript xFormScript1 = new XFormScript(XFormScriptType.NONE, 1.0);
  private XFormScript xFormScript2 = new XFormScript(XFormScriptType.NONE, 1.0);
  private XFormScript xFormScript3 = new XFormScript(XFormScriptType.NONE, 1.0);
  private XFormScript xFormScript4 = new XFormScript(XFormScriptType.NONE, 1.0);
  private XFormScript xFormScript5 = new XFormScript(XFormScriptType.NONE, 1.0);
  private int quality = 100;
  private OutputFormat outputFormat;
  private final List<Motion> motions = new ArrayList<Motion>();
  private final List<FlameMoviePart> parts = new ArrayList<FlameMoviePart>();
  private int frameWidth = 640;
  private int frameHeight = 480;
  private double framesPerSecond = 12.0;
  private int motionBlurLength = 12;
  private double motionBlurTimeStep = 0.015;
  private String name = "";

  public FlameMovie(Prefs pPrefs) {
    prefs = pPrefs;
  }

  public String getSoundFilename() {
    return soundFilename;
  }

  public void setSoundFilename(String soundFilename) {
    this.soundFilename = soundFilename;
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
          return FlameMorphService.morphFlames(prefs, flame1, flame2, morphFrame, morphFrames);
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

  public OutputFormat getOutputFormat() {
    return outputFormat;
  }

  public void setOutputFormat(OutputFormat outputFormat) {
    this.outputFormat = outputFormat;
  }

  public List<FlameMoviePart> getParts() {
    return parts;
  }

  public int getQuality() {
    return quality;
  }

  public void setQuality(int quality) {
    this.quality = quality;
  }

  public GlobalScript getGlobalScript1() {
    return globalScript1;
  }

  public void setGlobalScript1(GlobalScript globalScript1) {
    this.globalScript1 = globalScript1;
  }

  public GlobalScript getGlobalScript2() {
    return globalScript2;
  }

  public void setGlobalScript2(GlobalScript globalScript2) {
    this.globalScript2 = globalScript2;
  }

  public GlobalScript getGlobalScript3() {
    return globalScript3;
  }

  public void setGlobalScript3(GlobalScript globalScript3) {
    this.globalScript3 = globalScript3;
  }

  public GlobalScript getGlobalScript4() {
    return globalScript4;
  }

  public void setGlobalScript4(GlobalScript globalScript4) {
    this.globalScript4 = globalScript4;
  }

  public GlobalScript getGlobalScript5() {
    return globalScript5;
  }

  public void setGlobalScript5(GlobalScript globalScript5) {
    this.globalScript5 = globalScript5;
  }

  public XFormScript getxFormScript1() {
    return xFormScript1;
  }

  public void setxFormScript1(XFormScript xFormScript1) {
    this.xFormScript1 = xFormScript1;
  }

  public XFormScript getxFormScript2() {
    return xFormScript2;
  }

  public void setxFormScript2(XFormScript xFormScript2) {
    this.xFormScript2 = xFormScript2;
  }

  public XFormScript getxFormScript3() {
    return xFormScript3;
  }

  public void setxFormScript3(XFormScript xFormScript3) {
    this.xFormScript3 = xFormScript3;
  }

  public XFormScript getxFormScript4() {
    return xFormScript4;
  }

  public void setxFormScript4(XFormScript xFormScript4) {
    this.xFormScript4 = xFormScript4;
  }

  public XFormScript getxFormScript5() {
    return xFormScript5;
  }

  public void setxFormScript5(XFormScript xFormScript5) {
    this.xFormScript5 = xFormScript5;
  }

  public GlobalScript[] getGlobalScripts() {
    return new GlobalScript[] { globalScript1, globalScript2, globalScript3, globalScript4, globalScript5 };
  }

  public XFormScript[] getxFormScripts() {
    return new XFormScript[] { xFormScript1, xFormScript2, xFormScript3, xFormScript4, xFormScript5 };
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
}
