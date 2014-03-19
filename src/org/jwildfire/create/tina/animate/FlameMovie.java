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
  private GlobalScript globalScript1 = GlobalScript.NONE;
  private GlobalScript globalScript2 = GlobalScript.NONE;
  private GlobalScript globalScript3 = GlobalScript.NONE;
  private GlobalScript globalScript4 = GlobalScript.NONE;
  private GlobalScript globalScript5 = GlobalScript.NONE;
  private XFormScript xFormScript1 = XFormScript.NONE;
  private XFormScript xFormScript2 = XFormScript.NONE;
  private XFormScript xFormScript3 = XFormScript.NONE;
  private XFormScript xFormScript4 = XFormScript.NONE;
  private XFormScript xFormScript5 = XFormScript.NONE;
  private int colorOversampling;
  private int spatialOversampling;
  private int quality;
  private OutputFormat outputFormat;
  private final List<Motion> motions = new ArrayList<Motion>();
  private final List<FlameMoviePart> parts = new ArrayList<FlameMoviePart>();
  private int frameWidth;
  private int frameHeight;
  private double framesPerSecond;

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

  public int getColorOversampling() {
    return colorOversampling;
  }

  public void setColorOversampling(int colorOversampling) {
    this.colorOversampling = colorOversampling;
  }

  public int getSpatialOversampling() {
    return spatialOversampling;
  }

  public void setSpatialOversampling(int spatialOversampling) {
    this.spatialOversampling = spatialOversampling;
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
    this.globalScript1 = globalScript1 != null ? globalScript1 : GlobalScript.NONE;
  }

  public GlobalScript getGlobalScript2() {
    return globalScript2;
  }

  public void setGlobalScript2(GlobalScript globalScript2) {
    this.globalScript2 = globalScript2 != null ? globalScript2 : GlobalScript.NONE;
  }

  public GlobalScript getGlobalScript3() {
    return globalScript3;
  }

  public void setGlobalScript3(GlobalScript globalScript3) {
    this.globalScript3 = globalScript3 != null ? globalScript3 : GlobalScript.NONE;
  }

  public GlobalScript getGlobalScript4() {
    return globalScript4;
  }

  public void setGlobalScript4(GlobalScript globalScript4) {
    this.globalScript4 = globalScript4 != null ? globalScript4 : GlobalScript.NONE;
  }

  public GlobalScript getGlobalScript5() {
    return globalScript5;
  }

  public void setGlobalScript5(GlobalScript globalScript5) {
    this.globalScript5 = globalScript5 != null ? globalScript5 : GlobalScript.NONE;
  }

  public XFormScript getxFormScript1() {
    return xFormScript1;
  }

  public void setxFormScript1(XFormScript xFormScript1) {
    this.xFormScript1 = xFormScript1 != null ? xFormScript1 : XFormScript.NONE;
  }

  public XFormScript getxFormScript2() {
    return xFormScript2;
  }

  public void setxFormScript2(XFormScript xFormScript2) {
    this.xFormScript2 = xFormScript2 != null ? xFormScript2 : XFormScript.NONE;
  }

  public XFormScript getxFormScript3() {
    return xFormScript3;
  }

  public void setxFormScript3(XFormScript xFormScript3) {
    this.xFormScript3 = xFormScript3 != null ? xFormScript3 : XFormScript.NONE;
  }

  public XFormScript getxFormScript4() {
    return xFormScript4;
  }

  public void setxFormScript4(XFormScript xFormScript4) {
    this.xFormScript4 = xFormScript4 != null ? xFormScript4 : XFormScript.NONE;
  }

  public XFormScript getxFormScript5() {
    return xFormScript5;
  }

  public void setxFormScript5(XFormScript xFormScript5) {
    this.xFormScript5 = xFormScript5 != null ? xFormScript5 : XFormScript.NONE;
  }

  public GlobalScript[] getGlobalScripts() {
    return new GlobalScript[] { globalScript1, globalScript2, globalScript3, globalScript4, globalScript5 };
  }

  public XFormScript[] getxFormScripts() {
    return new XFormScript[] { xFormScript1, xFormScript2, xFormScript3, xFormScript4, xFormScript5 };
  }

}
