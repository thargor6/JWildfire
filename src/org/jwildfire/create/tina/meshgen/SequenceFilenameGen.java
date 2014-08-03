/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.meshgen;

import java.io.File;

import org.jwildfire.base.Tools;

public class SequenceFilenameGen {

  public static String createFilenamePattern(File pFileWithoutPattern) {
    String res = pFileWithoutPattern.getName();
    int p = res.lastIndexOf(".");
    if (p >= 0) {
      String ext = res.substring(p + 1, res.length());
      if (Tools.FILEEXT_PNG.equalsIgnoreCase(ext)) {
        res = res.substring(0, p);
      }
    }
    if (pFileWithoutPattern.getParentFile() != null) {
      res = new File(pFileWithoutPattern.getParentFile(), res).getAbsolutePath();
    }
    res += "%04d." + Tools.FILEEXT_PNG;
    return res;
  }

  public static String guessFilenamePattern(File pFileWithPattern) {
    String name = pFileWithPattern.getName();
    int p = name.lastIndexOf(".");
    int seqStart = -1;
    for (int i = p - 1; i >= 0; i--) {
      char c = name.charAt(i);
      if (c >= '0' && c <= '9') {
        seqStart = i;
      }
      else {
        break;
      }
    }

    if (seqStart >= 0) {
      while (seqStart < p) {
        String pattern = name.substring(0, seqStart) + "%0" + (p - seqStart) + "d" + name.substring(p, name.length());
        String filename = String.format(pattern, 1);
        if (new File(pFileWithPattern.getParentFile(), filename).exists()) {
          name = pattern;
          break;
        }
        else {
          seqStart++;
        }
      }
    }

    return new File(pFileWithPattern.getParentFile(), name).getAbsolutePath();
  }

}
