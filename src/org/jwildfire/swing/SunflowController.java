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

import javax.swing.JButton;
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
  private enum SceneType {
    JAVA, SC
  };

  private enum Status {
    IDLE, RENDERING
  };

  private enum Sampler {
    BUCKET, IPR
  }

  private String currentFile;
  private SunflowAPI api;
  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  private final JTextArea editorTextArea;
  private final JTextArea consoleTextArea;
  private final ImagePanel imagePanel;
  private final JButton renderButton;
  private final JButton iprButton;
  private final JButton loadSceneButton;
  private final JButton cancelRenderButton;
  private final JButton buildSceneButton;
  private final JButton saveSceneButton;
  private final JButton clearConsoleButton;
  private final JButton newSceneButton;

  private SceneType sceneType = null;
  private Status status = Status.IDLE;

  public SunflowController(ErrorHandler pErrorHandler, Prefs pPrefs, JTextArea pEditorTextArea, JTextArea pConsoleTextArea, ImagePanel pImagePanel,
      JButton pRenderButton, JButton pIprButton, JButton pLoadSceneButton, JButton pCancelRenderButton, JButton pBuildSceneButton, JButton pSaveSceneButton,
      JButton pClearConsoleButton, JButton pNewSceneButton) {
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    editorTextArea = pEditorTextArea;
    consoleTextArea = pConsoleTextArea;
    renderButton = pRenderButton;
    iprButton = pIprButton;
    loadSceneButton = pLoadSceneButton;
    cancelRenderButton = pCancelRenderButton;
    buildSceneButton = pBuildSceneButton;
    saveSceneButton = pSaveSceneButton;
    clearConsoleButton = pClearConsoleButton;
    newSceneButton = pNewSceneButton;
    imagePanel = pImagePanel;
    UI.set(this);
  }

  public void newScene() {
    currentFile = null;
    api = null;
    // TODO
    String template = "";

    editorTextArea.setText(template);
    enableControls();
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
      if (prefs.getInputSunflowScenePath() != null) {
        chooser.setCurrentDirectory(new File(prefs.getInputSunflowScenePath()));
      }
      if (chooser.showOpenDialog(editorTextArea) == JFileChooser.APPROVE_OPTION) {
        api = null;
        File file = chooser.getSelectedFile();
        String filename = file.getAbsolutePath();
        if (filename.endsWith(".java")) {
          sceneType = SceneType.JAVA;
        }
        else {
          sceneType = SceneType.SC;
        }
        editorTextArea.setText(Tools.readUTF8Textfile(filename));
        prefs.setLastInputSunflowSceneFile(file);
        currentFile = filename;
        enableControls();
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
      switch (sceneType) {
        case JAVA:
          api = SunflowAPI.compile(editorTextArea.getText());
          break;
        case SC: {
          File tmpFile = File.createTempFile("jwf", ".sc");
          try {
            String filename = tmpFile.getAbsolutePath();
            Tools.writeUTF8Textfile(filename, editorTextArea.getText());
            String template = "import org.sunflow.core.*;\nimport org.sunflow.core.accel.*;\nimport org.sunflow.core.camera.*;\nimport org.sunflow.core.primitive.*;\nimport org.sunflow.core.shader.*;\nimport org.sunflow.image.Color;\nimport org.sunflow.math.*;\n\npublic void build() {\n  include(\"" + filename.replace("\\", "\\\\") + "\");\n}\n";
            api = SunflowAPI.compile(template);
          }
          finally {
            tmpFile.deleteOnExit();
          }
        }
          break;
      }
      if (currentFile != null) {
        String dir = new File(currentFile).getAbsoluteFile().getParent();
        api.searchpath("texture", dir);
        api.searchpath("include", dir);
      }
      api.build();
      t.end();
      UI.printInfo(Module.GUI, "Build time: %s", t.toString());
      enableControls();
    }
    catch (Throwable ex) {
      api = null;
      errorHandler.handleError(ex);
    }
  }

  void clearConsole() {
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
    doRender(Sampler.BUCKET);
  }

  public void doRender(final Sampler pSampler) {
    if (status == Status.RENDERING) {
      return;
    }
    buildScene();
    new Thread() {
      @Override
      public void run() {
        status = Status.RENDERING;
        try {
          enableControls();
          clearConsole();
          api.parameter("sampler", pSampler == Sampler.BUCKET ? "bucket" : "ipr");
          api.options(SunflowAPI.DEFAULT_OPTIONS);
          api.render(SunflowAPI.DEFAULT_OPTIONS, imagePanel);
        }
        finally {
          status = Status.IDLE;
          enableControls();
        }
      }
    }.start();
  }

  private void enableControls() {
    boolean hasScene = editorTextArea.getText().length() > 0;
    boolean idle = status == Status.IDLE;
    renderButton.setEnabled(idle && hasScene);
    iprButton.setEnabled(renderButton.isEnabled());
    loadSceneButton.setEnabled(idle);
    cancelRenderButton.setEnabled(!idle);
    buildSceneButton.setEnabled(renderButton.isEnabled());
    saveSceneButton.setEnabled(renderButton.isEnabled());
    newSceneButton.setEnabled(loadSceneButton.isEnabled());
    clearConsoleButton.setEnabled(idle);
  }

  public void cancelRendering() {
    UI.taskCancel();
  }

  public void iprScene() {
    doRender(Sampler.IPR);
  }

  public void saveScene() {
    try {
      JFileChooser chooser = getSceneJFileChooser();
      chooser.setCurrentDirectory(new File(prefs.getOutputSunflowScenePath()));
      if (currentFile != null) {
        chooser.setSelectedFile(new File(currentFile));
      }
      if (chooser.showSaveDialog(editorTextArea) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        String filename = file.getAbsolutePath();
        switch (sceneType) {
          case JAVA:
            if (!filename.endsWith(".java")) {
              filename += ".java";
            }
            break;
          case SC:
            if (!filename.endsWith(".sc")) {
              filename += ".sc";
            }
            break;
          default:
            throw new IllegalStateException();
        }
        Tools.writeUTF8Textfile(filename, editorTextArea.getText());
        prefs.setLastOutputSunflowSceneFile(file);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

}
