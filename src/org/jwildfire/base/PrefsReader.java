/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PrefsReader {

  private String getProperty(Properties pProperties, String pKey) {
    return pProperties.getProperty(pKey, "").trim();
  }

  public void readPrefs(Prefs pPrefs) throws Exception {
    File file = new File(System.getProperty("user.home"), Prefs.PREFS_FILE);
    if (file.exists()) {
      InputStream inputStream = new FileInputStream(file);
      try {
        Properties props = new Properties();
        props.load(inputStream);
        pPrefs.setImagePath(getProperty(props, Prefs.KEY_PATH_IMAGES));
        pPrefs.setFlamePath(getProperty(props, Prefs.KEY_PATH_FLAMES));
        pPrefs.setScriptPath(getProperty(props, Prefs.KEY_PATH_SCRIPTS));
        pPrefs.setSunflowScenePath(getProperty(props, Prefs.KEY_PATH_SUBFLOW_SCENES));
      }
      finally {
        inputStream.close();
      }
    }
  }

}
