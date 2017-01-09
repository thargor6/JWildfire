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
package org.jwildfire.create.tina.swing;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jwildfire.base.Prefs;

public class QuickSaveFilenameGen {
  private int counter = 1;
  private final Prefs prefs;
  private final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

  private static final String prefix = "qsave";

  public QuickSaveFilenameGen(Prefs pPrefs) {
    prefs = pPrefs;
  }

  public String generateFilename(String pFilename) {
    String res = prefs.getOutputFlamePath();
    if (res == null) {
      return null;
    }
    if (res.length() > 0 && res.charAt(res.length() - 1) != '\\' && res.charAt(res.length() - 1) != '/' && res.charAt(res.length() - 1) != ':')
      res += File.separator;
    return res + pFilename;
  }

  public String generateNextFilename() {
    String dateStr = fmt.format(new Date());
    while (true) {
      String res = prefs.getOutputFlamePath();
      if (res != null && res.length() > 0 && res.charAt(res.length() - 1) != '\\' && res.charAt(res.length() - 1) != '/' && res.charAt(res.length() - 1) != ':')
        res += File.separator;
      String numberStr = String.valueOf(counter++);
      while (numberStr.length() < 3) {
        numberStr = "0" + numberStr;
      }
      res += prefix + "_" + dateStr + "_" + numberStr + ".flame";
      if (!new File(res).exists()) {
        return res;
      }
    }
  }
}
