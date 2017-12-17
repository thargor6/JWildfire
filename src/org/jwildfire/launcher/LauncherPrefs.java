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
package org.jwildfire.launcher;

public class LauncherPrefs {
  static final String PREFS_FILE = "j-wildfire-launcher.properties";
  static final String KEY_JAVA_PATH = "java.path";
  static final String KEY_MEMORY_MAX = "memory.max";
  static final String KEY_PRIORITY_LOW = "priority.low";
  static final String KEY_UI_SCALE = "ui.scale";

  private String javaPath = null;
  private int maxMem = 1024;
  private boolean lowPriority = true;
  private double uiScale = 1.0;

  public String getJavaPath() {
    return javaPath;
  }

  public void setJavaPath(String pJavaPath) {
    javaPath = pJavaPath;
  }

  public int getMaxMem() {
    return maxMem;
  }

  public void setMaxMem(int pMaxMem) {
    maxMem = pMaxMem;
  }

  public void loadFromFile() throws Exception {
    new LauncherPrefsReader().readPrefs(this);
  }

  public void saveToFile() throws Exception {
    new LauncherPrefsWriter().writePrefs(this);
  }

  public boolean isLowPriority() {
    return lowPriority;
  }

  public void setLowPriority(boolean pLowPriority) {
    lowPriority = pLowPriority;
  }

  public double getUiScale() {
    return uiScale;
  }

  public void setUiScale(double uiScale) {
    this.uiScale = uiScale;
  }

}
