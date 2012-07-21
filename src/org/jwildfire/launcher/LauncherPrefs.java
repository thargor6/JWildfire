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

  private String javaPath;
  private int maxMem;

  public String getJavaPath() {
    return javaPath;
  }

  public void setJavaPath(String javaPath) {
    this.javaPath = javaPath;
  }

  public int getMaxMem() {
    return maxMem;
  }

  public void setMaxMem(int maxMem) {
    this.maxMem = maxMem;
  }

  public void loadFromFile() throws Exception {
    new LauncherPrefsReader().readPrefs(this);
  }

  public void saveToFromFile() throws Exception {
    new LauncherPrefsWriter().writePrefs(this);
  }

}
