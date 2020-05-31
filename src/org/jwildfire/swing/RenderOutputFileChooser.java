/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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

import javax.swing.filechooser.FileFilter;
import java.awt.*;

public class RenderOutputFileChooser extends DefaultFileChooser {
  private static final long serialVersionUID = 1L;
  private final String imageDefaultExtension;

  @Override
  protected String getDefaultExtension() {
    FileFilter filter = getFileFilter();
    if(filter!=null && filter instanceof VideoFileFilter) {
      return ((VideoFileFilter)filter).getDefaultExtension();
    }
    else {
      return imageDefaultExtension;
    }
  }

  public RenderOutputFileChooser(String pImageDefaultExtension) {
    imageDefaultExtension = pImageDefaultExtension;
    setPreferredSize(new Dimension(960, 600));
    FileFilter filter = new ImageFileFilter();
    addChoosableFileFilter(filter);
    addChoosableFileFilter(new VideoFileFilter());
    setAccessory(new ImageFilePreview(this));
  }

}
