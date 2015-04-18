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
import java.util.Arrays;
import java.util.Collection;

import javax.swing.filechooser.FileFilter;

import org.jwildfire.base.Tools;

public class FlameFileFilter extends FileFilter {
  private Collection formats;

  public FlameFileFilter() {
    formats = Arrays.asList(new String[] { Tools.FILEEXT_FLAME, Tools.FILEEXT_XML });
  }

  @Override
  public boolean accept(File pFile) {
    if (pFile.isDirectory()) {
      return true;
    }
    String extension = getExtension(pFile);
    return formats.contains(extension);
  }

  @Override
  public String getDescription() {
    return "Flame files (*.flame|*.xml)";
  }

  private String getExtension(File pFile) {
    String name = pFile.getName();
    int idx = name.lastIndexOf('.');
    if (idx > 0 && idx < name.length() - 1) {
      return name.substring(idx + 1).toLowerCase();
    }
    return null;
  }

}
