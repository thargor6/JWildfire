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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.sunflow.SunflowAPI;
import org.sunflow.system.ImagePanel;
import org.sunflow.system.Timer;
import org.sunflow.system.UI;
import org.sunflow.system.UI.Module;
import org.sunflow.system.UI.PrintLevel;
import org.sunflow.system.UserInterface;

public class SunflowController implements UserInterface {
  private String currentFile;
  private SunflowAPI api;
  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  private final JTextArea editorTextArea;
  private final JTextArea consoleTextArea;
  private final ImagePanel imagePanel;

  public SunflowController(ErrorHandler pErrorHandler, Prefs pPrefs, JTextArea pEditorTextArea, JTextArea pConsoleTextArea, ImagePanel pImagePanel) {
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    editorTextArea = pEditorTextArea;
    consoleTextArea = pConsoleTextArea;
    imagePanel = pImagePanel;
    UI.set(this);
  }

  public void newScene() {
    currentFile = null;
    api = null;
    String template = "import org.sunflow.core.*;\nimport org.sunflow.core.accel.*;\nimport org.sunflow.core.camera.*;\nimport org.sunflow.core.primitive.*;\nimport org.sunflow.core.shader.*;\nimport org.sunflow.image.Color;\nimport org.sunflow.math.*;\n\npublic void build() {\n  // your code goes here\n\n}\n";
    editorTextArea.setText(template);
  }

  public class SceneFileFilter extends FileFilter {

    @Override
    public boolean accept(File pFile) {
      if (pFile.isDirectory())
        return true;
      String extension = getExtension(pFile);
      return (extension != null) && (extension.equals("sc") || extension.equals("java"));
    }

    @Override
    public String getDescription() {
      return "Sunflow scene";
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

  private JFileChooser getSceneJFileChooser() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.addChoosableFileFilter(new SceneFileFilter());
    fileChooser.setAcceptAllFileFilterUsed(false);
    return fileChooser;
  }

  public void loadScene() {
    try {
      JFileChooser chooser = getSceneJFileChooser();
      chooser.setCurrentDirectory(new File(prefs.getInputScenePath()));
      if (chooser.showOpenDialog(editorTextArea) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        String filename = file.getAbsolutePath();
        if (filename.endsWith(".java")) {
          editorTextArea.setText(Tools.readUTF8Textfile(filename));
        }
        else {
          String template = "import org.sunflow.core.*;\nimport org.sunflow.core.accel.*;\nimport org.sunflow.core.camera.*;\nimport org.sunflow.core.primitive.*;\nimport org.sunflow.core.shader.*;\nimport org.sunflow.image.Color;\nimport org.sunflow.math.*;\n\npublic void build() {\n  include(\"" + filename.replace("\\", "\\\\") + "\");\n}\n";
          editorTextArea.setText(template);
        }
        prefs.setLastInputSceneFile(file);
        currentFile = filename;
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void buildScene() {
    try {
      clearConsole();
      Timer t = new Timer();
      t.start();
      api = SunflowAPI.compile(editorTextArea.getText());
      if (currentFile != null) {
        String dir = new File(currentFile).getAbsoluteFile().getParent();
        api.searchpath("texture", dir);
        api.searchpath("include", dir);
      }
      api.build();
      t.end();
      UI.printInfo(Module.GUI, "Build time: %s", t.toString());
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void clearConsole() {
    consoleTextArea.setText("");
  }

  @Override
  public void print(Module m, PrintLevel level, String s) {
    if (level == PrintLevel.ERROR)
      JOptionPane.showMessageDialog(editorTextArea, s, String.format("Error - %s", m.name()), JOptionPane.ERROR_MESSAGE);
    println(UI.formatOutput(m, level, s));
  }

  private void println(final String s) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        consoleTextArea.append(s + "\n");
      }
    });
  }

  @Override
  public void taskStart(String arg0, int arg1, int arg2) {
    // TODO Auto-generated method stub

  }

  @Override
  public void taskStop() {
    // TODO Auto-generated method stub

  }

  @Override
  public void taskUpdate(int arg0) {
    // TODO Auto-generated method stub

  }

  public void renderScene() {
    new Thread() {
      @Override
      public void run() {
        setEnableInterface(false);
        clearConsole();
        if (api != null) {
          api.parameter("sampler", "bucket");
          api.options(SunflowAPI.DEFAULT_OPTIONS);
          api.render(SunflowAPI.DEFAULT_OPTIONS, imagePanel);
        }
        else
          UI.printError(Module.GUI, "Nothing to render!");
        setEnableInterface(true);
      }

    }.start();
  }

  private void setEnableInterface(boolean b) {
    // TODO Auto-generated method stub

  }
}
