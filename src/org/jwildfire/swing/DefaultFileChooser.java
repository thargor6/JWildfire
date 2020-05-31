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

import org.jwildfire.base.Tools;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public abstract class DefaultFileChooser extends JFileChooser {
  private static final long serialVersionUID = 1L;

  @Override
  public void approveSelection() {
    File f = getSelectedFile();

    FileFilter ff = getFileFilter();
    String fileExt = getDefaultExtension();
    if ((!ff.accept(f) || Tools.getFileExt(f.getName()).
            isEmpty()) && (fileExt != null && fileExt.length() > 0)) {
      f = new File(f.getPath() + "." + fileExt);
    }
    super.setSelectedFile(f);
    if (f.exists() && getDialogType() == SAVE_DIALOG) {
      int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
      switch (result) {
        case JOptionPane.YES_OPTION:
          super.approveSelection();
          return;
        case JOptionPane.NO_OPTION:
          return;
        case JOptionPane.CANCEL_OPTION:
          cancelSelection();
          return;
      }
    }
    super.approveSelection();
  }

  protected abstract String getDefaultExtension();

}
