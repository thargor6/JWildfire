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
  private GlobalScript globalScript;
  private XFormScript xFormScript;
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

  public GlobalScript getGlobalScript() {
    return globalScript;
  }

  public void setGlobalScript(GlobalScript globalScript) {
    this.globalScript = globalScript;
  }

  public XFormScript getxFormScript() {
    return xFormScript;
  }

  public void setxFormScript(XFormScript xFormScript) {
    this.xFormScript = xFormScript;
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

}
