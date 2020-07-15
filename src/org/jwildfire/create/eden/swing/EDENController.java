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
package org.jwildfire.create.eden.swing;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.Unchecker;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.eden.export.SunflowExporter;
import org.jwildfire.create.eden.scene.Scene;
import org.jwildfire.create.eden.scene.SceneElementGroup;
import org.jwildfire.create.eden.scene.VisibleSceneElement;
import org.jwildfire.create.eden.scene.material.Material;
import org.jwildfire.create.eden.scene.primitive.Box;
import org.jwildfire.create.eden.scene.primitive.Cylinder;
import org.jwildfire.create.eden.scene.primitive.Sphere;
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
    String template = edenCreateSurfaceScene();
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

  }

  @Override
  public void taskStop() {

  }

  @Override
  public void taskUpdate(int arg0) {

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
          api.parameter("accel", "kdtree");
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

  public abstract class Creator {
    protected final Scene scene;

    public abstract void create();

    public Creator(Scene pScene) {
      scene = pScene;
    }
  }

  public abstract class Transformer {
    protected final Scene scene;

    public abstract void transform(VisibleSceneElement pElement);

    public Transformer(Scene pScene) {
      scene = pScene;
    }
  }

  public class SphereCreator extends Creator {

    public SphereCreator(Scene pScene) {
      super(pScene);
    }

    @Override
    public void create() {
      Sphere sphere = scene.addSphere(-10, -10, -1, 0.4);
      sphere.setMaterial(Material.MATERIAL_MIRROR);
    }

  }

  public class BoxCreator extends Creator {

    public BoxCreator(Scene pScene) {
      super(pScene);
    }

    @Override
    public void create() {
      Box box = scene.addBox(-10, -10, 0, 3);
      box.setMaterial(Material.MATERIAL_SHINY_RED);
    }

  }

  public class Transformer1 extends Transformer {

    public Transformer1(Scene pScene) {
      super(pScene);
    }

    @Override
    public void transform(VisibleSceneElement pElement) {
      pElement.getPosition().setX(pElement.getPosition().getX() + 2.0 * (1.0 + Math.random()));
      pElement.getOrientation().setGamma(pElement.getOrientation().getGamma() + 3.0 * (1.0 + Math.random()));
      if (Math.random() < 0.5) {
        pElement.setMaterial(Material.MATERIAL_SHINY_GREEN);
      }
      else {
        pElement.setMaterial(Material.MATERIAL_SHINY_BLUE);
      }
    }

  }

  public class Transformer2 extends Transformer {

    public Transformer2(Scene pScene) {
      super(pScene);
    }

    @Override
    public void transform(VisibleSceneElement pElement) {
      pElement.getPosition().setY(pElement.getPosition().getY() + 1);
      pElement.getOrientation().setBeta(pElement.getOrientation().getBeta() + 6 * (1.0 + Math.random()));
      pElement.getOrientation().setAlpha(pElement.getOrientation().getAlpha() + 3 * (1.0 + Math.random()));
      if (Math.random() < 0.5) {
        pElement.setMaterial(Material.MATERIAL_SHINY_GREEN);
      }
      else {
        pElement.setMaterial(Material.MATERIAL_SHINY_BLUE);
      }
    }

  }

  private String edenCreateSurfaceScene() {
    try {
      Scene scene = new Scene();
      scene.addPointLight(0, 0, -200, 0.6);
      scene.addPointLight(-100, 20, -160, 0.8);
      scene.addPointLight(-10, 200, -60, 0.5);

      int width = 800;
      int height = 600;

      double surfWidth = width / 25.0;
      double surfHeight = height / 25.0;
      double surfDepth = MathLib.sqrt(surfWidth * surfWidth + surfHeight * surfHeight) / 100.0;

      List<Creator> creators = new ArrayList<Creator>();
      //      creators.add(new SphereCreator(scene));
      creators.add(new BoxCreator(scene));
      List<Transformer> transformers = new ArrayList<Transformer>();
      transformers.add(new Transformer1(scene));
      transformers.add(new Transformer2(scene));

      for (Creator creator : creators) {
        creator.create();
      }
      Map<Transformer, Set<VisibleSceneElement>> expandedMap = new HashMap<Transformer, Set<VisibleSceneElement>>();

      for (int i = 0; i < 7; i++) {
        for (Transformer transformer : transformers) {
          Set<VisibleSceneElement> expandedSet = expandedMap.get(transformer);
          if (expandedSet == null) {
            expandedSet = new HashSet<VisibleSceneElement>();
            expandedMap.put(transformer, expandedSet);
          }

          List<VisibleSceneElement> elements = scene.getAllVisibleElements();
          for (VisibleSceneElement element : elements) {
            if (!expandedSet.contains(element)) {
              VisibleSceneElement copy = element.clone();
              transformer.transform(copy);
              expandedSet.add(element);
            }
          }
        }
      }

      System.out.println("ELEMENTS: " + scene.getAllVisibleElements().size());

      return new SunflowExporter().exportScene(scene);
    }
    catch (Exception ex) {
      Unchecker.rethrow(ex);
      return null;
    }
  }

  private SceneElementGroup createGroup(org.jwildfire.create.eden.scene.Scene pScene, double size) {
    SceneElementGroup group = pScene.addGroup();
    {
      Sphere sphere = group.addSphere(-2 * size, 0, 1, size);
      sphere.setMaterial(Material.MATERIAL_MIRROR);
    }
    {
      Sphere sphere = group.addSphere(2 * size, 0, 1, size);
      sphere.setMaterial(Material.MATERIAL_GLASS);
    }
    {
      Cylinder cylinder = group.addCylinder(0, 0, 1, size / 4.0, size * 2.0);
      cylinder.setMaterial(Material.MATERIAL_MIRROR);
      cylinder.setOrientation(90.0, 0.0, 90.0);
    }
    {
      Box box = group.addBox(0, 3 * size, 0, size);
      box.setMaterial(Material.MATERIAL_SHINY_RED);
      box.setOrientation(45.0, 45.0, 45.0);
    }
    return group;
  }

}
