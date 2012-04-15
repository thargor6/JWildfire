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
package org.jwildfire.swing;

import java.awt.Dimension;

import javax.swing.filechooser.FileFilter;

import org.jwildfire.base.Tools;

public class SWFFileChooser extends DefaultFileChooser {
  private static final long serialVersionUID = 1L;

  @Override
  protected String getDefaultExtension() {
    return Tools.FILEEXT_SWF;
  }

  public SWFFileChooser() {
    setPreferredSize(new Dimension(800, 600));
    FileFilter filter = new SWFFileFilter();
    addChoosableFileFilter(filter);
    setFileFilter(filter);
    setAcceptAllFileFilterUsed(false);
  }

}
