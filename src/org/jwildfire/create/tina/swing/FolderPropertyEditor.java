/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke

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

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.ComponentFactory;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.l2fprod.common.swing.PercentLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class FolderPropertyEditor extends AbstractPropertyEditor {
  protected JTextField textfield;
  private JButton button;
  private JButton cancelButton;

  public FolderPropertyEditor() {
    this(true);
  }

  public FolderPropertyEditor(boolean asTableEditor) {
    editor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0)) {
      public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textfield.setEnabled(enabled);
        button.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
      }
    };
    ((JPanel) editor).add("*", textfield = new JTextField());
    ((JPanel) editor).add(button = ComponentFactory.Helper.getFactory().createMiniButton());
    if (asTableEditor) {
      textfield.setBorder(LookAndFeelTweaks.EMPTY_BORDER);
    }
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectFile();
      }
    });
    ((JPanel) editor).add(cancelButton = ComponentFactory.Helper.getFactory().createMiniButton());
    cancelButton.setText("X");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectNull();
      }
    });
    textfield.setTransferHandler(new FileTransferHandler());
  }

  class FileTransferHandler extends TransferHandler {
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
      for (int i = 0, c = transferFlavors.length; i < c; i++) {
        if (transferFlavors[i].equals(DataFlavor.javaFileListFlavor)) {
          return true;
        }
      }
      return false;
    }

    public boolean importData(JComponent comp, Transferable t) {
      try {
        List list = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
        if (list.size() > 0) {
          String oldFile = (String) getValue();
          String newFile = (String) list.get(0);
          textfield.setText(newFile);
          firePropertyChange(oldFile, newFile);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return true;
    }
  }

  public Object getValue() {
    if ("".equals(textfield.getText().trim())) {
      return null;
    } else {
      return textfield.getText();
    }
  }

  public void setValue(Object value) {
    if (value instanceof String) {
      textfield.setText((String) value);
    }
    else {
      textfield.setText("");
    }
  }

  protected void selectFile() {
    String oldFolder = (String) getValue();
    String newFolder = FileDialogTools.selectDirectory( new Frame(), new JPanel(), "Select a directory", oldFolder);
    if(newFolder!=null) {
      textfield.setText(newFolder);
      firePropertyChange(oldFolder, newFolder);
    }
  }

  protected void customizeFileChooser(JFileChooser chooser) {
  }

  protected void selectNull() {
    Object oldFile = getValue();
    textfield.setText("");
    firePropertyChange(oldFile, null);
  }

}
