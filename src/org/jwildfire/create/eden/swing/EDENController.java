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
package org.jwildfire.create.eden.swing;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.eden.io.SunflowWriter;
import org.jwildfire.create.eden.primitive.Point;
import org.jwildfire.create.eden.primitive.Torus;
import org.jwildfire.create.eden.scene.Scene;
import org.jwildfire.create.tina.swing.SunflowSceneFileChooser;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.MainController;
import org.sunflow.SunflowAPI;
import org.sunflow.system.ImagePanel;
import org.sunflow.system.Timer;
import org.sunflow.system.UI;
import org.sunflow.system.UI.Module;
import org.sunflow.system.UI.PrintLevel;
import org.sunflow.system.UserInterface;

public class EDENController implements UserInterface {
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
  private final MainController mainController;
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

  public EDENController(MainController pMainController, ErrorHandler pErrorHandler, Prefs pPrefs, JTextArea pEditorTextArea, JTextArea pConsoleTextArea, ImagePanel pImagePanel,
      JButton pRenderButton, JButton pIprButton, JButton pLoadSceneButton, JButton pCancelRenderButton, JButton pBuildSceneButton, JButton pSaveSceneButton,
      JButton pClearConsoleButton, JButton pNewSceneButton) {
    mainController = pMainController;
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

  public void newEmptyScene() {
    currentFile = null;
    api = null;
    sceneType = SceneType.SC;
    currentFile = "new" + genNewFileId() + ".sc";
    editorTextArea.setText("");
    enableControls();
  }

  public void newScene() {
    currentFile = null;
    api = null;
    sceneType = SceneType.SC;
    currentFile = "new" + genNewFileId() + ".sc";
    // TODO
    String template = edenCreateSunflowScene();
    editorTextArea.setText(template);
    enableControls();
  }

  private String genNewFileId() {
    return new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
  }

  private JFileChooser getSceneJFileChooser() {
    JFileChooser fileChooser = new SunflowSceneFileChooser(Prefs.getPrefs());
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

  private boolean renderCancelled;

  public void doRender(final Sampler pSampler) {
    if (status == Status.RENDERING) {
      return;
    }
    buildScene();
    renderCancelled = false;
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
          if (!renderCancelled) {
            File file;
            try {
              file = File.createTempFile("JWF", "png");
            }
            catch (IOException e) {
              e.printStackTrace();
              throw new RuntimeException(e);
            }
            try {
              imagePanel.save(file.getAbsolutePath());
              try {
                mainController.loadImage(file.getAbsolutePath(), false);
              }
              catch (Exception e) {
                e.printStackTrace();
              }
            }
            finally {
              file.delete();
            }
          }
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
    renderCancelled = true;
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

  public static class Worker {
    private final Scene scene;
    private final Point deltaSize = new Point(0.02, 0.02, 0.02);
    private final Point deltaRotate = new Point(6, 0, 18);

    private final Point position = new Point(-150.0, 50.0, 0.0);
    private final Point direction = new Point(16.0, 0, 0);
    private final Point rotate = new Point(0.0, 0.0, 0.0);
    private final Point size = new Point(3.0, 3.0, 3.0);

    public Worker(Scene pScene, double pX, double pY, double pZ) {
      scene = pScene;
      //      position.setValue(pX, pY, pZ);
    }

    public void performStep() {
      position.setX(position.getX() + direction.getX());
      position.setY(position.getY() + direction.getY());
      position.setZ(position.getZ() + direction.getZ());
      //      System.out.println("POS " + position.getX() + " " + position.getY() + " " + position.getZ());
      direction.rotate(deltaRotate.getX(), deltaRotate.getY(), deltaRotate.getZ());
      rotate.setX(rotate.getX() + deltaRotate.getX());
      rotate.setY(rotate.getY() + deltaRotate.getY());
      rotate.setZ(rotate.getZ() + deltaRotate.getZ());

      //      System.out.println("  ROT " + rotate.getX() + " " + rotate.getY() + " " + rotate.getZ());

      size.setX(size.getX() + deltaSize.getX());
      size.setY(size.getY() + deltaSize.getY());
      size.setZ(size.getZ() + deltaSize.getZ());
      //      System.out.println("  SIZE " + size.getX() + " " + size.getY() + " " + size.getZ());

      Torus box = new Torus();
      box.getPosition().assign(position);
      box.getSize().assign(size);
      box.getRotate().assign(rotate);
      scene.addObject(box);

      deltaRotate.setZ(deltaRotate.getZ() - 0.1 * (0.5 + Math.random()));
    }
  }

  private String edenCreateSunflowScene() {
    Scene scene = new Scene();
    // A
    //    for (int i = 0; i < 3300; i++) {
    //      Sphere sphere = new Sphere();
    //      sphere.setCentre((-50.0 + Math.random() * 94.0) * 2.5, 1.0 + Math.random() * 128.0, (-3.0 + Math.random() * 920.0));
    //      sphere.setRadius(3.6 + Math.random() * 11.4);
    //      scene.addObject(sphere);
    //    }
    // B
    //    for (int i = 0; i < 6666; i++) {
    //      double shape = Math.random();
    //      if (shape < 0.25) {
    //        Sphere sphere = new Sphere();
    //        sphere.getPosition().setValue((-50.0 + Math.random() * 94.0) * 2, 1.0 + Math.random() * 128.0, (-3.0 + Math.random() * 92.0));
    //        sphere.getSize().setValue((8 + Math.random() * 11.4) * 10);
    //        scene.addObject(sphere);
    //      }
    //      else if (shape < 0.5) {
    //        Box box = new Box();
    //        box.getPosition().setValue((-50.0 + Math.random() * 94.0) * 2, 1.0 + Math.random() * 128.0, (-3.0 + Math.random() * 92.0));
    //        box.getSize().setValue((8 + Math.random() * 11.4) * 10);
    //        box.getRotate().setValue(Math.random() * 90.0, Math.random() * 90.0, Math.random() * 90.0);
    //        scene.addObject(box);
    //      }
    //      else if (shape < 0.75) {
    //        Torus torus = new Torus();
    //        torus.getPosition().setValue((-50.0 + Math.random() * 94.0) * 2, 1.0 + Math.random() * 128.0, (-3.0 + Math.random() * 92.0));
    //        torus.getSize().setValue((8 + Math.random() * 11.4) * 10);
    //        torus.getRotate().setValue(Math.random() * 90.0, Math.random() * 90.0, Math.random() * 90.0);
    //        scene.addObject(torus);
    //      }
    //      else {
    //        Cylinder cylinder = new Cylinder();
    //        cylinder.getPosition().setValue((-50.0 + Math.random() * 94.0) * 2, 1.0 + Math.random() * 128.0, (-3.0 + Math.random() * 92.0));
    //        cylinder.getSize().setValue((8 + Math.random() * 11.4) * 10);
    //        cylinder.getRotate().setValue(Math.random() * 90.0, Math.random() * 90.0, Math.random() * 90.0);
    //        scene.addObject(cylinder);
    //      }
    // C
    //    Worker worker1 = new Worker(scene, 0, 0, 0);
    //    for (int i = 0; i < 100; i++) {
    //      worker1.performStep();
    //    }
    // D
    fillDLAScene(scene);
    return new SunflowWriter().createSunflowScene(scene);
  }

  private void fillDLAScene(Scene pScene) {
    int width = 100;
    int height = 100;
    int maxIter = 20000;
    int seed = 123;
    double gridSize = 4.0;
    double maxSize = 10.0;
    double minSize = 1.0;
    double xOffset = 0;
    double yOffset = 0.0;
    double zOffset = 33.0;

    short q[][] = new short[width][height];
    iterateDLA(q, width, height, maxIter, seed);
    double maxR = Math.sqrt(width * width + height * height) * 0.5;
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (q[i][j] != 0) {
          double dx = i - (width - 1) * 0.5;
          double dy = j - (height - 1) * 0.5;
          double r = Math.sqrt(dx * dx + dy * dy) / maxR + MathLib.EPSILON;
          double size = minSize + (maxSize - minSize) * r;
          Torus sphere = new Torus();
          sphere.getPosition().setValue(dx * gridSize + xOffset, dy * gridSize + yOffset, zOffset);
          sphere.getSize().setValue(size);
          sphere.getRotate().setValue(90 + Math.random() * 12.0 - 6.0, Math.random() * 12.0 - 6.0, Math.random() * 12.0 - 6.0);
          pScene.addObject(sphere);
        }
      }
    }

  }

  private void iterateDLA(short pQ[][], int pWidth, int pHeight, int pMaxIter, int pSeed) {
    int cx = pWidth / 2;
    int cy = pHeight / 2;
    double pi2 = 2.0 * Math.PI;
    int w2 = pWidth - 2;
    int h2 = pHeight - 2;
    Tools.srand123(pSeed);
    /* create the cluster */
    pQ[cy][cx] = 1;
    double r1 = 3.0;
    double r2 = 3.0 * r1;
    for (int i = 0; i < pMaxIter; i++) {
      double phi = pi2 * Tools.drand();
      double ri = r1 * Math.cos(phi);
      double rj = r1 * Math.sin(phi);
      int ci = cy + (int) (ri + 0.5);
      int cj = cx + (int) (rj + 0.5);
      short qt = 0;
      while (qt == 0) {
        double rr = Tools.drand();
        rr += rr;
        rr += rr;
        int rd = (int) rr;
        switch (rd) {
          case 0:
            ci++;
            break;
          case 1:
            cj--;
            break;
          case 2:
            ci--;
            break;
          default:
            cj++;
        }
        if ((ci < 1) || (ci > h2) || (cj < 1) || (cj > w2)) {
          qt = 1;
          i--;
        }
        else {
          int sum = pQ[ci - 1][cj] + pQ[ci + 1][cj] + pQ[ci][cj - 1] + pQ[ci][cj + 1];
          if (sum != 0) {
            pQ[ci][cj] = qt = 1;
            double r3 = (double) (ci - cy);
            double r4 = (double) (cj - cx);
            r3 *= r3;
            r4 *= r4;
            r3 += r4;
            r3 = Math.sqrt(r3);
            if (r3 > r1) {
              r1 = r3;
              r2 = 2.1 * r1;
            }
          }
          else {
            double r3 = (double) (ci - cy);
            double r4 = (double) (cj - cx);
            r3 *= r3;
            r4 *= r4;
            r3 += r4;
            r3 = Math.sqrt(r3);
            if (r3 > r2) {
              qt = 1;
              i--;
            }
          }
        }
      }
    }
  }
}
