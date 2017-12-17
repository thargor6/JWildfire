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
package org.jwildfire.launcher;

import java.io.File;

import org.jwildfire.base.Tools;

public class LauncherPrefsWriter {

  public static void deletePrefs() throws Exception {
    File file = new File(System.getProperty("user.home") + File.separator + LauncherPrefs.PREFS_FILE);
    if (file.exists()) {
      if (!file.delete()) {
        throw new Exception("Could not delete file <" + file.getAbsolutePath() + ">");
      }
    }
  }

  public void writePrefs(LauncherPrefs pPrefs) throws Exception {
    StringBuilder sb = new StringBuilder();
    addValue(sb, LauncherPrefs.KEY_JAVA_PATH, pPrefs.getJavaPath());
    addValue(sb, LauncherPrefs.KEY_MEMORY_MAX, pPrefs.getMaxMem());
    addValue(sb, LauncherPrefs.KEY_PRIORITY_LOW, pPrefs.isLowPriority());
    addValue(sb, LauncherPrefs.KEY_UI_SCALE, pPrefs.getUiScale());
    Tools.writeUTF8Textfile(System.getProperty("user.home") + File.separator + LauncherPrefs.PREFS_FILE, sb.toString());
  }

  private void addValue(StringBuilder pSB, String pKey, String pValue) {
    pSB.append(pKey + "=" + (pValue != null ? pValue.replace("\\", "\\\\") : "") + "\n");
  }

  private void addValue(StringBuilder pSB, String pKey, int pValue) {
    pSB.append(pKey + "=" + String.valueOf(pValue) + "\n");
  }

  private void addValue(StringBuilder pSB, String pKey, boolean pValue) {
    pSB.append(pKey + "=" + String.valueOf(pValue) + "\n");
  }

  private void addValue(StringBuilder pSB, String pKey, double pValue) {
    pSB.append(pKey + "=" + Tools.doubleToString(pValue) + "\n");
  }

}
