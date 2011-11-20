package org.jwildfire.swing;

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
    if (!ff.accept(f)) {
      f = new File(f.getPath() + "." + getDefaultExtension());
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
