/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2016 Andreas Maschke

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

public class RenderMovieUtil {

  private RenderMovieUtil() {
    // EMPTY
  }

  public static String makeFrameName(String moveFilename, int frame, String flameName, double renderQuality, int renderWidth, int renderHeight) {
    String titleStr = flameName.trim().replace(" - ","_").replace(' ', '_').replace('\\','_').replace('/','_').replace(':','_');
    if(titleStr.length()>30) {
      titleStr=titleStr.substring(0,30);
    }
    String optionsStr = renderQuality+"_"+renderWidth+"_"+renderHeight;
    File basefn =new File(moveFilename.substring(0, moveFilename.lastIndexOf(".")));
    File tmpFn;
    if(basefn.getParentFile()!=null) {
      tmpFn = new File(basefn.getParent(), "_"+basefn.getName()+"_"+optionsStr+"_"+titleStr);
    }
    else {
      tmpFn = new File("_"+basefn.getAbsolutePath()+"_"+optionsStr+"_"+titleStr);
    }
    return String.format (tmpFn.getAbsolutePath() + "_%04d.png", frame);
  }

}
