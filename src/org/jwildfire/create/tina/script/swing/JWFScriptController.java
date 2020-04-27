/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2019s Andreas Maschke

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
package org.jwildfire.create.tina.script.swing;

import org.jwildfire.base.MacroButton;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.ColorType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.solidrender.DistantLight;
import org.jwildfire.create.tina.base.solidrender.LightDiffFuncPreset;
import org.jwildfire.create.tina.base.solidrender.MaterialSettings;
import org.jwildfire.create.tina.base.solidrender.ShadowType;
import org.jwildfire.create.tina.base.weightingfield.WeightingFieldType;
import org.jwildfire.create.tina.io.Flam3GradientReader;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.swing.ScriptEditDialog;
import org.jwildfire.create.tina.swing.StandardDialogs;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.TinaControllerData;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.swing.ErrorHandler;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

public class JWFScriptController {
  private String prevScriptDrawer;
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final TinaControllerData data;
  private final JTree scriptTree;
  private final JTextArea scriptDescriptionTextArea;
  private final JTextArea scriptTextArea;
  private final JButton rescanScriptsBtn;
  private final JButton newScriptBtn;
  private final JButton newScriptFromFlameBtn;
  private final JButton deleteScriptBtn;
  private final JButton scriptRenameBtn;
  private final JButton scriptDuplicateBtn;
  private final JButton scriptRunBtn;
  private final JButton addMacroButtonBtn;
  private final JButton editScriptBtn;
  private boolean allowEdit = false;
  private DefaultMutableTreeNode userScriptsRootNode;

  public JWFScriptController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel, TinaControllerData pData, JTree pScriptTree, JTextArea pScriptDescriptionTextArea,
                             JTextArea pScriptTextArea, JButton pRescanScriptsBtn, JButton pNewScriptBtn, JButton pNewScriptFromFlameBtn, JButton pDeleteScriptBtn, JButton pScriptRenameBtn, JButton pScriptDuplicateBtn, JButton pScriptRunBtn,
                             JButton pAddMacroButtonBtn, JButton pEditScriptBtn) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    data = pData;
    scriptTree = pScriptTree;
    scriptDescriptionTextArea = pScriptDescriptionTextArea;
    scriptTextArea = pScriptTextArea;
    rescanScriptsBtn = pRescanScriptsBtn;
    newScriptBtn = pNewScriptBtn;
    newScriptFromFlameBtn = pNewScriptFromFlameBtn;
    deleteScriptBtn = pDeleteScriptBtn;
    scriptRenameBtn = pScriptRenameBtn;
    scriptDuplicateBtn = pScriptDuplicateBtn;
    scriptRunBtn = pScriptRunBtn;
    addMacroButtonBtn = pAddMacroButtonBtn;
    editScriptBtn = pEditScriptBtn;
    //    initScriptLibrary();
  }

  private boolean _firstActivated = false;

  public void onActivate() {
    if (!_firstActivated) {
      initScriptLibrary();
      enableControls();
      _firstActivated = true;
    }
  }

  private DefaultMutableTreeNode getSelNode() {
    DefaultMutableTreeNode selNode = null;
    {
      TreePath selPath = scriptTree.getSelectionPath();
      if (selPath != null) {
        selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
      }
    }
    return selNode;
  }

  public void enableControls() {
    String baseDrawer = prefs.getTinaJWFScriptPath();
    boolean enableUserScripts = baseDrawer != null && baseDrawer.length() > 0;

    DefaultMutableTreeNode selNode = getSelNode();
    allowEdit = (selNode != null) && (selNode instanceof JWFScriptUserNode);
    boolean scriptSelected = (selNode != null) && ((selNode instanceof JWFScriptUserNode) || (selNode instanceof JWFScriptInternalNode));
    boolean userScriptSelected = (selNode != null) && (selNode instanceof JWFScriptUserNode);
    boolean userPathSelected = (selNode != null) && ((selNode instanceof JWFScriptUserNode) || ((selNode instanceof JWFScriptFolderNode) && ((JWFScriptFolderNode) selNode).isUserDir()));

    scriptDescriptionTextArea.setEditable(false);
    scriptTextArea.setEditable(false);
    scriptDescriptionTextArea.setEnabled(scriptSelected);
    scriptTextArea.setEnabled(scriptSelected);

    rescanScriptsBtn.setEnabled(true);
    newScriptBtn.setEnabled(enableUserScripts && userPathSelected);
    newScriptFromFlameBtn.setEnabled(enableUserScripts && userPathSelected && tinaController.getCurrFlame() != null);
    deleteScriptBtn.setEnabled(enableUserScripts && userScriptSelected);
    scriptRenameBtn.setEnabled(enableUserScripts && userScriptSelected);
    scriptDuplicateBtn.setEnabled(enableUserScripts && scriptSelected);
    scriptRunBtn.setEnabled(scriptSelected);
    editScriptBtn.setEnabled(allowEdit);
    scriptTree.setEnabled(true);

    addMacroButtonBtn.setEnabled(scriptSelected);
    enableMacroButtonsControls();
  }

  private void enableMacroButtonsControls() {
    int row = data.macroButtonsTable.getSelectedRow();
    List<MacroButton> buttons = prefs.getTinaMacroButtons();
    data.macroButtonMoveUpBtn.setEnabled(row > 0 && row < buttons.size());
    data.macroButtonMoveDownBtn.setEnabled(row >= 0 && row < buttons.size() - 1);
    data.macroButtonDeleteBtn.setEnabled(row >= 0 && row < buttons.size());
  }

  interface ScriptNode {
    public String getScript() throws Exception;

    public String getDescription() throws Exception;

    public String getCaption();

  }

  private static class InvalidScriptFolderNode extends JWFScriptFolderNode {
    private static final long serialVersionUID = 1L;

    public InvalidScriptFolderNode() {
      super("(user-script-path is empty, check the Prefs)", null, true);
    }
  }

  private void initScriptLibrary() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Script library", true);
    // Internal scripts
    {
      String[] ressources = {
              "Custom_Variation_Loader_GH",
              "LoonieSplits DT",
              "Buckballs by DT",
              "M&M Flower DT",
              "Add Random Final FX by MH and MO",
              "Textured_Cylinders_Rev01_by_MH",
              "Crackle_Styles_Chooser_Rev01_by_MH",
              "Escher Flux", "Mobius Dragon", "Soft Julian", "Wrap into Bubble", "Wrap into Heart", "Wrap into SubFlame",
              "HypertilePoincare_Rev2", "Bwraps-bubble-Julian2", "Bwraps and bubbles", "Oily_Juliascope_Rev1", "Oily_Rev3",
              "Plastic", "SphericalCross_Rev2", "SuperSusan_Rev1", "TomsSpiralSpiral_Rev3",

              "Keep 'Em Separated and Framed BS", "Minkowscope-Painting-BS",

              "UG-", "UG-Ran", "UG-Ran2", "UG-Ran2sym", "UG-Ran2symPersp", "UG-Ran2symPerspYax", "UG-RanRan", "UG-RanRanRan", "UG-Sym", "UG-Sym2",

              "HB  0 Final 2 Galaxies", "HB 0 2 Galaxy Final Test", "HB 0 Final 2 Galaxies-cloudier", "HB 0 Final 2 Galaxiesv2",
              "HB 0 Final 2 Gassv2", "HB 0 Single  Galaxy", "HB 1 planetoid", "HB 2  Gas Clouds", "HB 2  Gas CloudsRan",
              "HB 2 Galaxies Gas Broad", "HB 2 Galaxies Gas Cluster", "HB 2 Galaxies Gas DoubleBroad", "HB 2 Galaxies Positive",
              "HB 2 Galaxy Spirals Test", "HB 2 Moon Singularity", "HB 2 Moons Crater Test C", "HB 2 Moons Test Circ",
              "HB 2 planetoid smooth", "HB 2 planetoids cloudy", "HB 2 planetoids", "HB 2moons-craters", "HB Galaxy 2 Gas Clouds",
              "HB Galaxy1", "HB Galaxy1a", "HB Galaxy2moontest", "HB Moon Generator", "HB Moon Surface Generator", "  HB Moon Zoom Generator II",
              "HB Planet Generator", "HB Single Galaxy",

              "LU--Lumiere-", "LU--Lumiere-CurlyCross1", "LU--Lumiere-HyLog", "LU--Lumiere-POL", "LU-Choose-Lumiere-", "LU-ChooseJS-Lumiere-",
              "LU-ChooseJS2-Lumiere-", "LU-Epi-Lumiere-", "LU-EpipluPAX-Lumiere-", "LU-Epiplus-Lumiere-",  "LU-Epipluspoly-Lumiere-",
              "LU-JULF-Lumiere-", "LU-JULFLOW-Lumiere-", "LU-Mob-Lumiere-", "LU-RandFinal-Mirror-Lumiere-", "LU-Rays-Lumiere-",
              "LU-SPHERE-3D-PIF-Mirror-Lumiere-", "LU-SPHEREPIF-Lumiere-", "LU-SPHEREPIF-Mirror-Lumiere-", "LU-SPHERF-Lumiere-", "LU-TanEpi-Lumiere-", "LU-TanF-Lumiere-",

              "SX 00 I am Ace  in space", "SX 00 I am Ace  in space2", "SX 00 I am Ace  in space3", "SX 00 I am Julia in space pitch",
              "SX 00 I am Julia in space", "SX 00 I am Julia", "SX 00 I am RanJulia in space", "SX 00 I am RanJulia",
              "SX 0 I am Lovely", "SX 1 I am Peaceful", "SX 1 I am PeacefulRC", "SX 2 I make you think", "SX 3 I need printing",
              "SX 4 I need love", "SX 5 I need patience", "SX 6 I need Tweaks", "SX 7 I make you wonder", "SX hole2Split",
              "SX jispostz", "SX LoonieSplit", "SX octagon2Split", "SX rings2Split", "SX squarize2Split", "SX tancos2Split",

              "Wedge_Sph_Marble",

              "YU-", "YU--Choose", "YU--cot", "YU--coth", "YU--cothtest", "YU--csc",
              "YU--csch", "YU--curl", "YU--curlZO", "YU--foci", "YU--ho", "YU--invpolar",
              "YU--log", "YU--secant", "YU--spiralwing", "YU--tancos", "YU-Yugen"

      };
      List<String> resLst = Arrays.asList(ressources);
      Collections.sort(resLst);
      ressources = resLst.toArray(new String[]{});

      // for the base path inside the jar file
      RGBPaletteReader reader = new Flam3GradientReader();
      DefaultMutableTreeNode defaultFolderNode = null;
      for (String ressource : ressources) {
        try {
          String resFilename = "scripts/" + ressource + "." + Tools.FILEEXT_JWFSCRIPT;
          InputStream is = reader.getClass().getResourceAsStream(resFilename);
          if (is != null) {
            is.close();
            JWFScriptInternalNode node = new JWFScriptInternalNode(ressource, resFilename);

            if (defaultFolderNode == null) {
              defaultFolderNode = new JWFScriptFolderNode("Built-in scripts (read-only)", null, false);
              root.add(defaultFolderNode);
            }
            defaultFolderNode.add(node);
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

    // External flames
    {
      String baseDrawer = prefs.getTinaJWFScriptPath();
      if (baseDrawer == null || baseDrawer.equals("") || baseDrawer.equals(".") || baseDrawer.equals("/") || baseDrawer.equals("\\")) {
        root.add(new InvalidScriptFolderNode());
      } else {
        DefaultMutableTreeNode parentNode = userScriptsRootNode = new JWFScriptFolderNode("Your scripts", baseDrawer, true);
        root.add(parentNode);
        scanUserScripts(baseDrawer, parentNode);
      }
    }
    scriptTree.setRootVisible(false);
    scriptTree.setModel(new DefaultTreeModel(root));
  }

  public void scanUserScripts(String path, DefaultMutableTreeNode pParentNode) {
    File root = new File(path);
    File[] list = root.listFiles();
    if (list != null) {
      try {
        Arrays.sort(list, new Comparator<File>() {

          @Override
          public int compare(File o1, File o2) {
            return o1.getName().compareTo(o2.getName());
          }

        });
      } catch (Exception ex) {
        // ex.printStackTrace();
      }
      for (File f : list) {
        if (f.isDirectory()) {
          DefaultMutableTreeNode newParentNode = new JWFScriptFolderNode(f.getName(), f.getAbsolutePath(), true);
          pParentNode.add(newParentNode);
          scanUserScripts(f.getAbsolutePath(), newParentNode);
        } else {
          String filename = f.getAbsolutePath();
          String lcFilename = filename.toLowerCase();
          if (lcFilename.length() != filename.length()) {
            lcFilename = filename;
          }
          int pos = lcFilename.lastIndexOf("." + Tools.FILEEXT_JWFSCRIPT.toLowerCase());
          if (pos > 0 && pos == filename.length() - Tools.FILEEXT_JWFSCRIPT.length() - 1) {
            String caption = f.getName().substring(0, f.getName().length() - Tools.FILEEXT_JWFSCRIPT.length() - 1);
            JWFScriptUserNode node = new JWFScriptUserNode(caption, filename);
            pParentNode.add(node);
          }
        }
      }
    }
  }

  public void scriptPropertiesTree_changed(TreeSelectionEvent e) {
    enableControls();
    DefaultMutableTreeNode selNode = getSelNode();
    if (selNode != null && selNode instanceof ScriptNode) {
      ScriptNode scriptNode = (ScriptNode) selNode;
      try {
        scriptTextArea.setText(scriptNode.getScript());
        scriptTextArea.setCaretPosition(0);
        scriptDescriptionTextArea.setText(scriptNode.getDescription());
        scriptDescriptionTextArea.setCaretPosition(0);
      } catch (Exception ex) {
        errorHandler.handleError(ex);
      }
    } else {
      scriptTextArea.setText("");
      scriptDescriptionTextArea.setText("");
    }
  }

  public void rescanBtn_clicked() {
    initScriptLibrary();
  }

  public void scriptRunBtn_clicked() {
    try {
      String scriptPath = null;
      DefaultMutableTreeNode selNode = this.getSelNode();
      if (selNode != null) {
        if (selNode instanceof JWFScriptUserNode) {
          scriptPath = ((JWFScriptUserNode) selNode).getFilename();
        } else if (selNode instanceof JWFScriptInternalNode) {
          scriptPath = ((JWFScriptInternalNode) selNode).getResFilename();
        }
      }
      tinaController.runScript(scriptPath, scriptTextArea.getText());
    } catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void newScript(String pDescription, String pScript) {
    try {
      DefaultMutableTreeNode selNode = getSelNode();
      if (selNode != null) {
        String newName = StandardDialogs.promptForText(rootPanel, "Please enter a script name", "");
        if (newName != null) {
          checkScriptName(newName);
          DefaultMutableTreeNode parentForNewNode;
          if (selNode instanceof JWFScriptFolderNode) {
            parentForNewNode = selNode;
          } else {
            parentForNewNode = (DefaultMutableTreeNode) selNode.getParent();
          }
          String scriptFilename;
          String descFilename;
          if (selNode instanceof JWFScriptInternalNode) {
            String basePath = getBaseScriptPath();
            scriptFilename = basePath + newName + "." + Tools.FILEEXT_JWFSCRIPT;
            descFilename = basePath + newName + "." + Tools.FILEEXT_TXT;
          } else if (selNode instanceof JWFScriptUserNode) {
            JWFScriptUserNode userNode = (JWFScriptUserNode) selNode;
            scriptFilename = new File(userNode.getFilename()).getParentFile().getAbsolutePath() + File.separator + newName + "." + Tools.FILEEXT_JWFSCRIPT;
            descFilename = new File(userNode.getFilename()).getParentFile().getAbsolutePath() + File.separator + newName + "." + Tools.FILEEXT_TXT;
          } else if (selNode instanceof JWFScriptFolderNode && ((JWFScriptFolderNode) selNode).isUserDir()) {
            JWFScriptFolderNode folderNode = (JWFScriptFolderNode) selNode;
            scriptFilename = folderNode.getDirectory() + File.separator + newName + "." + Tools.FILEEXT_JWFSCRIPT;
            descFilename = folderNode.getDirectory() + File.separator + newName + "." + Tools.FILEEXT_TXT;
          } else {
            throw new Exception("Unknown node type <" + selNode.getClass() + ">");
          }

          if (new File(scriptFilename).exists()) {
            throw new Exception("File <" + scriptFilename + "> already exists");
          }
          if (new File(descFilename).exists()) {
            throw new Exception("File <" + descFilename + "> already exists");
          }
          if (pScript == null) {
            pScript = "";
          }
          Tools.writeUTF8Textfile(scriptFilename, pScript);
          if (pDescription == null) {
            pDescription = "";
          }
          Tools.writeUTF8Textfile(descFilename, pDescription);
          JWFScriptUserNode node = new JWFScriptUserNode(newName, scriptFilename);
          parentForNewNode.add(node);
          scriptTree.setSelectionPath(new TreePath(((DefaultTreeModel) scriptTree.getModel()).getPathToRoot(node)));
          scriptTree.getParent().invalidate();
          scriptTree.getParent().validate();
          scriptTree.repaint();
          scriptTree.updateUI();
        }
      }
    } catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  public void newScriptBtn_clicked() {
    newScript(null, null);
  }

  public void newScriptFromFlameBtn_clicked() {
    Flame flame = tinaController.getCurrFlame();
    if (flame != null) {
      String desc = "This script was automatically generated by " + Tools.APP_TITLE + " " + Tools.APP_VERSION + " by converting a flame";
      newScript(desc, convertFlameToScript(flame));
    }
  }

  private String convertFlameToScript(Flame pFlame) {
    StringBuilder sb = new StringBuilder();
    sb.append("import org.jwildfire.create.tina.base.Flame;\n");
    sb.append("import org.jwildfire.create.tina.base.XForm;\n");
    sb.append("import org.jwildfire.create.tina.palette.RGBPalette;\n");
    sb.append("import org.jwildfire.create.tina.script.ScriptRunnerEnvironment;\n");
    sb.append("import org.jwildfire.create.tina.transform.XFormTransformService;\n");
    sb.append("import org.jwildfire.create.tina.base.Layer;\n");
    sb.append("import org.jwildfire.create.tina.base.DrawMode;\n");
    sb.append("import org.jwildfire.create.tina.base.ColorType;\n");
    sb.append("import org.jwildfire.create.tina.variation.Variation;\n");
    sb.append("import org.jwildfire.create.tina.variation.VariationFunc;\n");
    sb.append("import org.jwildfire.create.tina.variation.VariationFuncList;\n");
    sb.append("import org.jwildfire.create.tina.mutagen.RandomGradientMutation;\n");
    sb.append("import org.jwildfire.create.tina.transform.XFormTransformService;\n");
    sb.append("import org.jwildfire.create.tina.base.EditPlane;\n");

    sb.append("\n");
    sb.append("");
    sb.append("public void run(ScriptRunnerEnvironment pEnv) throws Exception {\n");
    sb.append("  // create a new flame\n");
    sb.append("  Flame flame=new Flame();\n");
    sb.append("  flame.getLayers().clear(); // get rid of the default layer because we create all layers by ourselves\n");
    sb.append("  // set the flame main attributes\n");
    sb.append("  flame.setCamRoll(" + Tools.doubleToString(pFlame.getCamRoll()) + ");\n");
    sb.append("  flame.setCentreX(" + Tools.doubleToString(pFlame.getCentreX()) + ");\n");
    sb.append("  flame.setCentreY(" + Tools.doubleToString(pFlame.getCentreY()) + ");\n");
    sb.append("  flame.setCamPitch(" + Tools.doubleToString(pFlame.getCamPitch()) + ");\n");
    sb.append("  flame.setCamYaw(" + Tools.doubleToString(pFlame.getCamYaw()) + ");\n");
    sb.append("  flame.setCamBank(" + Tools.doubleToString(pFlame.getCamBank()) + ");\n");
    sb.append("  flame.setCamPerspective(" + Tools.doubleToString(pFlame.getCamPerspective()) + ");\n");
    sb.append("  flame.setWidth(" + pFlame.getWidth() + ");\n");
    sb.append("  flame.setHeight(" + pFlame.getHeight() + ");\n");
    sb.append("  flame.setPixelsPerUnit(" + Tools.doubleToString(pFlame.getPixelsPerUnit()) + ");\n");
    sb.append("  flame.setCamZoom(" + Tools.doubleToString(pFlame.getCamZoom()) + ");\n");
    sb.append("  flame.setBGTransparency(" + pFlame.isBGTransparency() + ");\n");
    sb.append("  // Uncomment setBrightness or setGamma if essential for the flame\n");
    sb.append("  // flame.setBrightness(" + pFlame.getBrightness() + ");\n");
    sb.append("  // flame.setGamma(" + pFlame.getGamma() + ");\n");
    sb.append("  flame.setPreserveZ(" + pFlame.isPreserveZ() + ");\n");
    switch (pFlame.getPostSymmetryType()) {
      case POINT:
        sb.append("  flame.setPostSymmetryType(org.jwildfire.create.tina.base.PostSymmetryType." + pFlame.getPostSymmetryType().toString() + ");\n");
        sb.append("  flame.setPostSymmetryOrder(" + pFlame.getPostSymmetryOrder() + ");\n");
        sb.append("  flame.setPostSymmetryCentreX(" + Tools.doubleToString(pFlame.getPostSymmetryCentreX()) + ");\n");
        sb.append("  flame.setPostSymmetryCentreY(" + Tools.doubleToString(pFlame.getPostSymmetryCentreY()) + ");\n");
        break;
      case X_AXIS:
      case Y_AXIS:
        sb.append("  flame.setPostSymmetryType(org.jwildfire.create.tina.base.PostSymmetryType." + pFlame.getPostSymmetryType().toString() + ");\n");
        sb.append("  flame.setPostSymmetryDistance(" + Tools.doubleToString(pFlame.getPostSymmetryDistance()) + ");\n");
        sb.append("  flame.setPostSymmetryRotation(" + Tools.doubleToString(pFlame.getPostSymmetryRotation()) + ");\n");
        sb.append("  flame.setPostSymmetryCentreX(" + Tools.doubleToString(pFlame.getPostSymmetryCentreX()) + ");\n");
        sb.append("  flame.setPostSymmetryCentreY(" + Tools.doubleToString(pFlame.getPostSymmetryCentreY()) + ");\n");
        break;
      default: // nothing to do
        break;
    }
    if (pFlame.getSolidRenderSettings().isSolidRenderingEnabled()) {
      sb.append(" //// Begin of solid-rendering-settings\n");
      sb.append(" flame.getSolidRenderSettings().setSolidRenderingEnabled(true);\n");
      sb.append(" // Ambient shadows\n");
      sb.append(" flame.getSolidRenderSettings().setAoEnabled(" + (pFlame.getSolidRenderSettings().isAoEnabled() ? "true" : "false") + ");\n");
      if (pFlame.getSolidRenderSettings().isAoEnabled()) {
        sb.append(" flame.getSolidRenderSettings().setAoIntensity(" + Tools.doubleToString(pFlame.getSolidRenderSettings().getAoIntensity()) + ");\n");
        sb.append(" flame.getSolidRenderSettings().setAoSearchRadius(" + Tools.doubleToString(pFlame.getSolidRenderSettings().getAoSearchRadius()) + ");\n");
        sb.append(" flame.getSolidRenderSettings().setAoBlurRadius(" + Tools.doubleToString(pFlame.getSolidRenderSettings().getAoBlurRadius()) + ");\n");
        sb.append(" flame.getSolidRenderSettings().setAoRadiusSamples(" + pFlame.getSolidRenderSettings().getAoRadiusSamples() + ");\n");
        sb.append(" flame.getSolidRenderSettings().setAoAzimuthSamples(" + pFlame.getSolidRenderSettings().getAoAzimuthSamples() + ");\n");
        sb.append(" flame.getSolidRenderSettings().setAoFalloff(" + Tools.doubleToString(pFlame.getSolidRenderSettings().getAoFalloff()) + ");\n");
        sb.append(" flame.getSolidRenderSettings().setAoAffectDiffuse(" + Tools.doubleToString(pFlame.getSolidRenderSettings().getAoAffectDiffuse()) + ");\n");
      }
      sb.append(" // Hard shadows\n");
      sb.append(" flame.getSolidRenderSettings().setShadowType(" + pFlame.getSolidRenderSettings().getShadowType().getClass().getName() + "." + pFlame.getSolidRenderSettings().getShadowType().toString() + ");\n");
      if (!ShadowType.OFF.equals(pFlame.getSolidRenderSettings().getShadowType())) {
        sb.append(" flame.getSolidRenderSettings().setShadowmapBias(" + Tools.doubleToString(pFlame.getSolidRenderSettings().getShadowmapBias()) + ");\n");
        sb.append(" flame.getSolidRenderSettings().setShadowmapSize(" + pFlame.getSolidRenderSettings().getShadowmapSize() + ");\n");
        if (ShadowType.SMOOTH.equals(pFlame.getSolidRenderSettings().getShadowType())) {
          sb.append(" flame.getSolidRenderSettings().setShadowSmoothRadius(" + Tools.doubleToString(pFlame.getSolidRenderSettings().getShadowSmoothRadius()) + ");\n");
        }
      }
      sb.append(" // Post bokeh\n");
      sb.append(" flame.getSolidRenderSettings().setPostBokehFilterKernel(" + pFlame.getSolidRenderSettings().getPostBokehFilterKernel().getDeclaringClass().getName() + "." + pFlame.getSolidRenderSettings().getPostBokehFilterKernel().toString() + ");\n");
      sb.append(" flame.getSolidRenderSettings().setPostBokehIntensity(" + Tools.doubleToString(pFlame.getSolidRenderSettings().getPostBokehIntensity()) + ");\n");
      sb.append(" flame.getSolidRenderSettings().setPostBokehBrightness(" + Tools.doubleToString(pFlame.getSolidRenderSettings().getPostBokehBrightness()) + ");\n");
      sb.append(" flame.getSolidRenderSettings().setPostBokehSize(" + Tools.doubleToString(pFlame.getSolidRenderSettings().getPostBokehSize()) + ");\n");
      sb.append(" flame.getSolidRenderSettings().setPostBokehActivation(" + Tools.doubleToString(pFlame.getSolidRenderSettings().getPostBokehActivation()) + ");\n");
      sb.append(" // Materials\n");
      sb.append(" flame.getSolidRenderSettings().getMaterials().clear();\n");
      for (MaterialSettings material : pFlame.getSolidRenderSettings().getMaterials()) {
        sb.append(" {\n");
        sb.append("   org.jwildfire.create.tina.base.solidrender.MaterialSettings material = new org.jwildfire.create.tina.base.solidrender.MaterialSettings();\n");
        sb.append("   flame.getSolidRenderSettings().getMaterials().add(material);\n");
        sb.append("   material.setDiffuse(" + Tools.doubleToString(material.getDiffuse()) + ");\n");
        sb.append("   material.setAmbient(" + Tools.doubleToString(material.getAmbient()) + ");\n");
        sb.append("   material.setPhong(" + Tools.doubleToString(material.getPhong()) + ");\n");
        sb.append("   material.setPhongSize(" + Tools.doubleToString(material.getPhongSize()) + ");\n");
        sb.append("   material.setPhongRed(" + Tools.doubleToString(material.getPhongRed()) + ");\n");
        sb.append("   material.setPhongGreen(" + Tools.doubleToString(material.getPhongGreen()) + ");\n");
        sb.append("   material.setPhongBlue(" + Tools.doubleToString(material.getPhongBlue()) + ");\n");
        sb.append("   material.setLightDiffFunc(" + ((LightDiffFuncPreset) material.getLightDiffFunc()).getDeclaringClass().getName() + "." + material.getLightDiffFunc().toString() + ");\n");
        if (material.getReflMapFilename() != null && material.getReflMapFilename().length() > 0) {
          sb.append("   material.setReflMapFilename(\"" + material.getReflMapFilename() + "\");\n");
          sb.append("   material.setReflMapIntensity(" + Tools.doubleToString(material.getReflMapIntensity()) + ");\n");
          sb.append("   material.setReflectionMapping(" + material.getReflectionMapping().getDeclaringClass().getName() + "." + material.getReflectionMapping().toString() + ");\n");
        }
        sb.append(" }\n");
      }
      sb.append(" // Lights\n");
      sb.append(" flame.getSolidRenderSettings().getLights().clear();\n");
      for (DistantLight light : pFlame.getSolidRenderSettings().getLights()) {
        sb.append(" {\n");
        sb.append("   org.jwildfire.create.tina.base.solidrender.DistantLight light = new org.jwildfire.create.tina.base.solidrender.DistantLight();\n");
        sb.append("   flame.getSolidRenderSettings().getLights().add(light);\n");
        sb.append("   light.setRed(" + Tools.doubleToString(light.getRed()) + ");\n");
        sb.append("   light.setGreen(" + Tools.doubleToString(light.getGreen()) + ");\n");
        sb.append("   light.setBlue(" + Tools.doubleToString(light.getBlue()) + ");\n");
        sb.append("   light.setIntensity(" + Tools.doubleToString(light.getIntensity()) + ");\n");
        sb.append("   light.setCastShadows(" + (light.isCastShadows() ? "true" : "false") + ");\n");
        sb.append("   light.setAltitude(" + Tools.doubleToString(light.getAltitude()) + ");\n");
        sb.append("   light.setAzimuth(" + Tools.doubleToString(light.getAzimuth()) + ");\n");
        sb.append("   light.setShadowIntensity(" + Tools.doubleToString(light.getShadowIntensity()) + ");\n");
        sb.append("   // randomize light positions (uncomment to play around)\n");
        sb.append("   // light.setAltitude(180.0 - 360.0 * Math.random());\n");
        sb.append("   // light.setAzimuth(360.0 - 720.0 * Math.random());\n");
        sb.append(" }\n");
      }

      sb.append(" //// End of solid-rendering-settings\n");
    }

    for (int i = 0; i < pFlame.getLayers().size(); i++) {
      Layer layer = pFlame.getLayers().get(i);
      addLayer(sb, layer, i);
    }

    sb.append("  // Either update the currently selected flame (to not need to create a new thumbnail\n");
    sb.append("  // in the thumbnail ribbon after each run of the script...\n");
    sb.append("  Flame selFlame = pEnv.getCurrFlame();\n");
    sb.append("  if(selFlame!=null) {\n");
    sb.append("    selFlame.assign(flame);\n");
    sb.append("    pEnv.refreshUI();\n");
    sb.append("  }\n");
    sb.append("  // ...or load the flame in the editor and refresh the UI\n");
    sb.append("  else {\n");
    sb.append("    pEnv.setCurrFlame(flame);\n");
    sb.append("  }\n");
    sb.append("}\n");
    return sb.toString();
  }

  private void addLayer(StringBuilder pSB, Layer pLayer, int pIndex) {
    pSB.append("  // create layer " + (pIndex + 1) + "\n");
    pSB.append("  {\n");
    pSB.append("    Layer layer = new Layer();\n");
    pSB.append("    flame.getLayers().add(layer);\n");
    pSB.append("    layer.setWeight(" + Tools.doubleToString(pLayer.getWeight()) + ");\n");
    pSB.append("    layer.setDensity(" + Tools.doubleToString(pLayer.getDensity()) + ");\n");
    pSB.append("    layer.setVisible(" + pLayer.isVisible() + ");\n");
    pSB.append("    // create a random gradient\n");
    pSB.append("    new RandomGradientMutation().execute(layer);\n");

    for (int i = 0; i < pLayer.getXForms().size(); i++) {
      XForm xForm = pLayer.getXForms().get(i);
      addXForm(pSB, xForm, i, false);
    }
    for (int i = 0; i < pLayer.getFinalXForms().size(); i++) {
      XForm xForm = pLayer.getFinalXForms().get(i);
      addXForm(pSB, xForm, i, true);
    }
    //    sb.append("  // create the gradient\n");
    //    for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
    //      RGBColor color = layer.getPalette().getColor(i);
    //      sb.append("    flame.getPalette().setColor(" + i + ", " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ");\n");
    //    }
    pSB.append("  }\n");
  }

  private void addXForm(StringBuilder pSB, XForm pXForm, int pIndex, boolean pFinalXForm) {
    pSB.append("    // create " + (pFinalXForm ? "final transform" : "transform") + " " + (pIndex + 1) + "\n");
    pSB.append("    {\n");
    pSB.append("      XForm xForm = new XForm();\n");
    if (pFinalXForm) {
      pSB.append("      layer.getFinalXForms().add(xForm);\n");
    } else {
      pSB.append("      layer.getXForms().add(xForm);\n");
    }
    pSB.append("      xForm.setWeight(" + Tools.doubleToString(pXForm.getWeight()) + ");\n");
    pSB.append("      xForm.setColor(" + Tools.doubleToString(pXForm.getColor()) + ");\n");
    pSB.append("      xForm.setColorSymmetry(" + Tools.doubleToString(pXForm.getColorSymmetry()) + ");\n");
    switch (pXForm.getDrawMode()) {
    case NORMAL:
      pSB.append("      xForm.setDrawMode(DrawMode.NORMAL);\n");
      break;
    case HIDDEN:
      pSB.append("      xForm.setDrawMode(DrawMode.HIDDEN);\n");
      break;
    case OPAQUE:
      pSB.append("      xForm.setDrawMode(DrawMode.OPAQUE);\n");
      pSB.append("      xForm.setOpacity(" + Tools.doubleToString(pXForm.getOpacity()) + ");\n");
      break;
    }
    switch (pXForm.getColorType()) {
    case NONE:
      if (!pFinalXForm)
        pSB.append("      xForm.setColorType(ColorType.NONE);\n");
      break;
    case DIFFUSION:
      if (pFinalXForm) {
        pSB.append("      xForm.setColorType(ColorType.DIFFUSION);\n");
      }
      break;
    case TARGET:
      pSB.append("      xForm.setColorType(ColorType.TARGET);\n");
      RGBColor tc = pXForm.getTargetColor();
      pSB.append("      xForm.setTargetColor(" + Tools.intToString(tc.getRed()) + "," + Tools.intToString(tc.getGreen()) + "," + Tools.intToString(tc.getBlue()) +");\n");
      break;
    case TARGETG:
      pSB.append("      xForm.setColorType(ColorType.TARGETG);\n");
      break;
    case DISTANCE:
      pSB.append("      xForm.setColorType(ColorType.DISTANCE);\n");
      break;
    case CYCLIC:
      pSB.append("      xForm.setColorType(ColorType.CYCLIC);\n");
    }
    pSB.append("      xForm.setMaterial(" + Tools.doubleToString(pXForm.getMaterial()) + ");\n");
    pSB.append("      xForm.setMaterialSpeed(" + Tools.doubleToString(pXForm.getMaterialSpeed()) + ");\n");
    pSB.append("\n");

    if (!pXForm.isHasYZCoeffs() && !pXForm.isHasYZPostCoeffs() && !pXForm.isHasZXCoeffs() && !pXForm.isHasZXPostCoeffs()) {
      pSB.append("      xForm.setCoeff00(" + Tools.doubleToString(pXForm.getCoeff00()) + "); // a\n");
      pSB.append("      xForm.setCoeff10(" + Tools.doubleToString(pXForm.getCoeff10()) + "); // b\n");
      pSB.append("      xForm.setCoeff20(" + Tools.doubleToString(pXForm.getCoeff20()) + "); // e\n");
      pSB.append("      xForm.setCoeff01(" + Tools.doubleToString(pXForm.getCoeff01()) + "); // c\n");
      pSB.append("      xForm.setCoeff11(" + Tools.doubleToString(pXForm.getCoeff11()) + "); // d\n");
      pSB.append("      xForm.setCoeff21(" + Tools.doubleToString(pXForm.getCoeff21()) + "); // f\n");
      pSB.append("\n");
      pSB.append("      xForm.setPostCoeff00(" + Tools.doubleToString(pXForm.getPostCoeff00()) + ");\n");
      pSB.append("      xForm.setPostCoeff10(" + Tools.doubleToString(pXForm.getPostCoeff10()) + ");\n");
      pSB.append("      xForm.setPostCoeff01(" + Tools.doubleToString(pXForm.getPostCoeff01()) + ");\n");
      pSB.append("      xForm.setPostCoeff11(" + Tools.doubleToString(pXForm.getPostCoeff11()) + ");\n");
      pSB.append("      xForm.setPostCoeff20(" + Tools.doubleToString(pXForm.getPostCoeff20()) + ");\n");
      pSB.append("      xForm.setPostCoeff21(" + Tools.doubleToString(pXForm.getPostCoeff21()) + ");\n");
      pSB.append("\n");
    } else {
      pSB.append("      xForm.setXYCoeff00(" + Tools.doubleToString(pXForm.getXYCoeff00()) + "); // a\n");
      pSB.append("      xForm.setXYCoeff10(" + Tools.doubleToString(pXForm.getXYCoeff10()) + "); // b\n");
      pSB.append("      xForm.setXYCoeff20(" + Tools.doubleToString(pXForm.getXYCoeff20()) + "); // e\n");
      pSB.append("      xForm.setXYCoeff01(" + Tools.doubleToString(pXForm.getXYCoeff01()) + "); // c\n");
      pSB.append("      xForm.setXYCoeff11(" + Tools.doubleToString(pXForm.getXYCoeff11()) + "); // d\n");
      pSB.append("      xForm.setXYCoeff21(" + Tools.doubleToString(pXForm.getXYCoeff21()) + "); // f\n");
      pSB.append("\n");
      pSB.append("      xForm.setXYPostCoeff00(" + Tools.doubleToString(pXForm.getXYPostCoeff00()) + ");\n");
      pSB.append("      xForm.setXYPostCoeff10(" + Tools.doubleToString(pXForm.getXYPostCoeff10()) + ");\n");
      pSB.append("      xForm.setXYPostCoeff01(" + Tools.doubleToString(pXForm.getXYPostCoeff01()) + ");\n");
      pSB.append("      xForm.setXYPostCoeff11(" + Tools.doubleToString(pXForm.getXYPostCoeff11()) + ");\n");
      pSB.append("      xForm.setXYPostCoeff20(" + Tools.doubleToString(pXForm.getXYPostCoeff20()) + ");\n");
      pSB.append("      xForm.setXYPostCoeff21(" + Tools.doubleToString(pXForm.getXYPostCoeff21()) + ");\n");
      pSB.append("\n");
      pSB.append("      xForm.setYZCoeff00(" + Tools.doubleToString(pXForm.getYZCoeff00()) + ");\n");
      pSB.append("      xForm.setYZCoeff10(" + Tools.doubleToString(pXForm.getYZCoeff10()) + ");\n");
      pSB.append("      xForm.setYZCoeff20(" + Tools.doubleToString(pXForm.getYZCoeff20()) + ");\n");
      pSB.append("      xForm.setYZCoeff01(" + Tools.doubleToString(pXForm.getYZCoeff01()) + ");\n");
      pSB.append("      xForm.setYZCoeff11(" + Tools.doubleToString(pXForm.getYZCoeff11()) + ");\n");
      pSB.append("      xForm.setYZCoeff21(" + Tools.doubleToString(pXForm.getYZCoeff21()) + ");\n");
      pSB.append("\n");
      pSB.append("      xForm.setYZPostCoeff00(" + Tools.doubleToString(pXForm.getYZPostCoeff00()) + ");\n");
      pSB.append("      xForm.setYZPostCoeff10(" + Tools.doubleToString(pXForm.getYZPostCoeff10()) + ");\n");
      pSB.append("      xForm.setYZPostCoeff01(" + Tools.doubleToString(pXForm.getYZPostCoeff01()) + ");\n");
      pSB.append("      xForm.setYZPostCoeff11(" + Tools.doubleToString(pXForm.getYZPostCoeff11()) + ");\n");
      pSB.append("      xForm.setYZPostCoeff20(" + Tools.doubleToString(pXForm.getYZPostCoeff20()) + ");\n");
      pSB.append("      xForm.setYZPostCoeff21(" + Tools.doubleToString(pXForm.getYZPostCoeff21()) + ");\n");
      pSB.append("\n");
      pSB.append("      xForm.setZXCoeff00(" + Tools.doubleToString(pXForm.getZXCoeff00()) + ");\n");
      pSB.append("      xForm.setZXCoeff10(" + Tools.doubleToString(pXForm.getZXCoeff10()) + ");\n");
      pSB.append("      xForm.setZXCoeff20(" + Tools.doubleToString(pXForm.getZXCoeff20()) + ");\n");
      pSB.append("      xForm.setZXCoeff01(" + Tools.doubleToString(pXForm.getZXCoeff01()) + ");\n");
      pSB.append("      xForm.setZXCoeff11(" + Tools.doubleToString(pXForm.getZXCoeff11()) + ");\n");
      pSB.append("      xForm.setZXCoeff21(" + Tools.doubleToString(pXForm.getZXCoeff21()) + ");\n");
      pSB.append("\n");
      pSB.append("      xForm.setZXPostCoeff00(" + Tools.doubleToString(pXForm.getZXPostCoeff00()) + ");\n");
      pSB.append("      xForm.setZXPostCoeff10(" + Tools.doubleToString(pXForm.getZXPostCoeff10()) + ");\n");
      pSB.append("      xForm.setZXPostCoeff01(" + Tools.doubleToString(pXForm.getZXPostCoeff01()) + ");\n");
      pSB.append("      xForm.setZXPostCoeff11(" + Tools.doubleToString(pXForm.getZXPostCoeff11()) + ");\n");
      pSB.append("      xForm.setZXPostCoeff20(" + Tools.doubleToString(pXForm.getZXPostCoeff20()) + ");\n");
      pSB.append("      xForm.setZXPostCoeff21(" + Tools.doubleToString(pXForm.getZXPostCoeff21()) + ");\n");
      pSB.append("\n");
    }

    if(pXForm.getWeightingFieldType()!=null && !WeightingFieldType.NONE.equals(pXForm.getWeightingFieldType())) {
      pSB.append("      xForm.setWeightingFieldType(org.jwildfire.create.tina.base.weightingfield.WeightingFieldType." + pXForm.getWeightingFieldType().name() + ");\n");
      pSB.append("      xForm.setWeightingFieldInput(org.jwildfire.create.tina.base.weightingfield.WeightingFieldInputType." + pXForm.getWeightingFieldInput().name() + ");\n");
      pSB.append("      xForm.setWeightingFieldColorIntensity(" + Tools.doubleToString(pXForm.getWeightingFieldColorIntensity()) + ");\n");
      pSB.append("      xForm.setWeightingFieldVarAmountIntensity(" + Tools.doubleToString(pXForm.getWeightingFieldVarAmountIntensity()) + ");\n");
      pSB.append("      xForm.setWeightingFieldVarParam1Intensity(" + Tools.doubleToString(pXForm.getWeightingFieldVarParam1Intensity()) + ");\n");
      pSB.append("      xForm.setWeightingFieldVarParam1VarName(\"" + (pXForm.getWeightingFieldVarParam1ParamName() != null ? pXForm.getWeightingFieldVarParam1ParamName() : "") + "\");\n");
      pSB.append("      xForm.setWeightingFieldVarParam1ParamName(\"" + (pXForm.getWeightingFieldVarParam1ParamName() != null ? pXForm.getWeightingFieldVarParam1ParamName() : "") + "\");\n");
      pSB.append("      xForm.setWeightingFieldVarParam2Intensity(" + Tools.doubleToString(pXForm.getWeightingFieldVarParam2Intensity()) + ");\n");
      pSB.append("      xForm.setWeightingFieldVarParam2VarName(\"" + (pXForm.getWeightingFieldVarParam2ParamName() != null ? pXForm.getWeightingFieldVarParam2ParamName() : "") + "\");\n");
      pSB.append("      xForm.setWeightingFieldVarParam2ParamName(\"" + (pXForm.getWeightingFieldVarParam2ParamName() != null ? pXForm.getWeightingFieldVarParam2ParamName() : "") + "\");\n");
      pSB.append("      xForm.setWeightingFieldVarParam3Intensity(" + Tools.doubleToString(pXForm.getWeightingFieldVarParam3Intensity()) + ");\n");
      pSB.append("      xForm.setWeightingFieldVarParam3VarName(\"" + (pXForm.getWeightingFieldVarParam3ParamName() != null ? pXForm.getWeightingFieldVarParam3ParamName() : "") + "\");\n");
      pSB.append("      xForm.setWeightingFieldVarParam3ParamName(\"" + (pXForm.getWeightingFieldVarParam3ParamName() != null ? pXForm.getWeightingFieldVarParam3ParamName() : "") + "\");\n");
      pSB.append("      xForm.setWeightingFieldJitterIntensity(" + Tools.doubleToString(pXForm.getWeightingFieldJitterIntensity()) + ");\n");
      switch (pXForm.getWeightingFieldType()) {
        case CELLULAR_NOISE:
          pSB.append("      xForm.setWeightingFieldNoiseSeed(" + pXForm.getWeightingFieldNoiseSeed() + ");\n");
          pSB.append("      xForm.setWeightingFieldNoiseFrequency(" + Tools.doubleToString(pXForm.getWeightingFieldNoiseFrequency()) + ");\n");
          pSB.append("      xForm.setWeightingFieldCellularNoiseReturnType(org.jwildfire.create.tina.base.weightingfield.CellularNoiseReturnType." + pXForm.getWeightingFieldCellularNoiseReturnType().name() + ");\n");
          pSB.append("      xForm.setWeightingFieldCellularNoiseDistanceFunction(org.jwildfire.create.tina.base.weightingfield.CellularNoiseDistanceFunction." + pXForm.getWeightingFieldCellularNoiseDistanceFunction().name() + ");\n");
          break;
        case CUBIC_NOISE:
        case PERLIN_NOISE:
        case SIMPLEX_NOISE:
        case VALUE_NOISE:
          pSB.append("      xForm.setWeightingFieldNoiseSeed(" + pXForm.getWeightingFieldNoiseSeed() + ");\n");
          pSB.append("      xForm.setWeightingFieldNoiseFrequency(" + Tools.doubleToString(pXForm.getWeightingFieldNoiseFrequency()) + ");\n");
          break;
        case PERLIN_FRACTAL_NOISE:
        case CUBIC_FRACTAL_NOISE:
        case SIMPLEX_FRACTAL_NOISE:
        case VALUE_FRACTAL_NOISE:
          pSB.append("      xForm.setWeightingFieldNoiseSeed(" + pXForm.getWeightingFieldNoiseSeed() + ");\n");
          pSB.append("      xForm.setWeightingFieldNoiseFrequency(" + Tools.doubleToString(pXForm.getWeightingFieldNoiseFrequency()) + ");\n");
          pSB.append("      xForm.setWeightingFieldFractalType(org.jwildfire.create.tina.base.weightingfield.FractalType." + pXForm.getWeightingFieldFractalType().name() + ");\n");
          pSB.append("      xForm.setWeightingFieldFractalNoiseOctaves(" + pXForm.getWeightingFieldFractalNoiseOctaves() + ");\n");
          pSB.append("      xForm.setWeightingFieldFractalNoiseGain(" + Tools.doubleToString(pXForm.getWeightingFieldFractalNoiseGain()) + ");\n");
          pSB.append("      xForm.setWeightingFieldFractalNoiseLacunarity(" + Tools.doubleToString(pXForm.getWeightingFieldFractalNoiseLacunarity()) + ");\n");
          break;
        case IMAGE_MAP:
          pSB.append("      xForm.setWeightingFieldColorMapXSize(" + pXForm.getWeightingFieldColorMapXSize() + ");\n");
          pSB.append("      xForm.setWeightingFieldColorMapYSize(" + pXForm.getWeightingFieldColorMapYSize() + ");\n");
          pSB.append("      xForm.setWeightingFieldColorMapXCentre(" + pXForm.getWeightingFieldColorMapXCentre() + ");\n");
          pSB.append("      xForm.setWeightingFieldColorMapYCentre(" + pXForm.getWeightingFieldColorMapYCentre() + ");\n");
          pSB.append("      xForm.setWeightingFieldColorMapFilename(\"" + (pXForm.getWeightingFieldColorMapFilename() != null ? pXForm.getWeightingFieldColorMapFilename() : "") + "\");\n");

          break;
      }
      pSB.append("\n");
    }

    if (!pFinalXForm) {
      boolean hasHeader = false;
      for (int i = 0; i < pXForm.getModifiedWeights().length; i++) {
        if (fabs(pXForm.getModifiedWeights()[i] - 1.0) > EPSILON) {
          if (!hasHeader) {
            pSB.append("      // change relative weights\n");
            hasHeader = true;
          }
          pSB.append("      xForm.getModifiedWeights()[" + i + "] = " + Tools.doubleToString(pXForm.getModifiedWeights()[i]) + ";\n");
        }
      }
      if (hasHeader) {
        pSB.append("\n");
      }
    }

    if (pXForm.getVariationCount() > 0) {
      for (int i = 0; i < pXForm.getVariationCount(); i++) {
        addVariation(pSB, pXForm.getVariation(i), i);
      }
    }
    pSB.append("      // set default edit plane\n");
    pSB.append("      flame.setEditPlane(EditPlane.XY);\n");
    pSB.append("      // random affine transforms (uncomment to play around)\n");
    pSB.append("      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);\n");
    pSB.append("      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), false);\n");
    pSB.append("      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), false);\n");
    pSB.append("      // random affine post transforms (uncomment to play around)\n");
    pSB.append("      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);\n");
    pSB.append("      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);\n");
    pSB.append("      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);\n");

    pSB.append("    }\n");
  }

  private void addVariation(StringBuilder pSB, Variation pVariation, int pIndex) {
    pSB.append("      // variation " + (pIndex + 1) + "\n");
    if (pVariation.getFunc().getParameterNames().length == 0 && (pVariation.getFunc().getRessourceNames() == null || pVariation.getFunc().getRessourceNames().length == 0)) {
      if (pVariation.getPriority() != pVariation.getFunc().getPriority()) {
        pSB.append("      Variation variation = xForm.addVariation(" + Tools.doubleToString(pVariation.getAmount()) + ", VariationFuncList.getVariationFuncInstance(\"" + pVariation.getFunc().getName() + "\", true));\n");
        pSB.append("      variation.setPriority(" + pVariation.getPriority() + ");\n");
      } else {
        pSB.append("      xForm.addVariation(" + Tools.doubleToString(pVariation.getAmount()) + ", VariationFuncList.getVariationFuncInstance(\"" + pVariation.getFunc().getName() + "\", true));\n");
      }
    } else {
      pSB.append("      {\n");
      pSB.append("        VariationFunc varFunc=VariationFuncList.getVariationFuncInstance(\"" + pVariation.getFunc().getName() + "\", true);\n");
      for (int i = 0; i < pVariation.getFunc().getParameterNames().length; i++) {
        String pName = pVariation.getFunc().getParameterNames()[i];
        Object pValue = pVariation.getFunc().getParameterValues()[i];
        if (pValue instanceof Double) {
          pSB.append("        varFunc.setParameter(\"" + pName + "\", " + Tools.doubleToString((Double) pValue) + ");\n");
        } else {
          pSB.append("        varFunc.setParameter(\"" + pName + "\", " + pValue + ");\n");
        }
      }
      if (pVariation.getPriority() != pVariation.getFunc().getPriority()) {
        pSB.append("        Variation variation = xForm.addVariation(" + Tools.doubleToString(pVariation.getAmount()) + ", varFunc);\n");
        pSB.append("        variation.setPriority(" + pVariation.getPriority() + ");\n");
      } else {
        pSB.append("        xForm.addVariation(" + Tools.doubleToString(pVariation.getAmount()) + ", varFunc);\n");
      }
      pSB.append("      }\n");
    }
  }

  private String getBaseScriptPath() {
    String basePath = prefs.getTinaJWFScriptPath();
    if (basePath == null) {
      basePath = "";
    }
    if ((basePath.length() > 0) && (basePath.charAt(basePath.length() - 1) != '/') && (basePath.charAt(basePath.length() - 1) != '\\')) {
      basePath += File.separator;
    }
    return basePath;
  }

  public void duplicateScriptBtn_clicked() {
    try {
      DefaultMutableTreeNode selNode = getSelNode();
      if (selNode != null && selNode instanceof ScriptNode) {
        ScriptNode scriptNode = (ScriptNode) selNode;
        String newName = StandardDialogs.promptForText(rootPanel, "Please enter a new name", scriptNode.getCaption());
        if (newName != null) {
          checkScriptName(newName);
          String script = scriptNode.getScript();
          String description = scriptNode.getDescription();
          DefaultMutableTreeNode parent;
          String scriptFilename, descFilename;
          if (selNode instanceof JWFScriptInternalNode) {
            String basePath = getBaseScriptPath();
            scriptFilename = basePath + newName + "." + Tools.FILEEXT_JWFSCRIPT;
            descFilename = basePath + newName + "." + Tools.FILEEXT_TXT;
            parent = this.userScriptsRootNode;
          } else if (selNode instanceof JWFScriptUserNode) {
            JWFScriptUserNode userNode = (JWFScriptUserNode) selNode;
            parent = (DefaultMutableTreeNode) selNode.getParent();
            scriptFilename = new File(userNode.getFilename()).getParentFile().getAbsolutePath() + File.separator + newName + "." + Tools.FILEEXT_JWFSCRIPT;
            descFilename = new File(userNode.getFilename()).getParentFile().getAbsolutePath() + File.separator + newName + "." + Tools.FILEEXT_TXT;
            parent = (DefaultMutableTreeNode) selNode.getParent();
          } else {
            throw new Exception("Unknown node type <" + selNode.getClass() + ">");
          }
          if (new File(scriptFilename).exists()) {
            throw new Exception("File <" + scriptFilename + "> already exists");
          }
          if (new File(descFilename).exists()) {
            throw new Exception("File <" + descFilename + "> already exists");
          }
          Tools.writeUTF8Textfile(scriptFilename, script);
          Tools.writeUTF8Textfile(descFilename, description);
          JWFScriptUserNode node = new JWFScriptUserNode(newName, scriptFilename);
          parent.add(node);
          scriptTree.setSelectionPath(new TreePath(((DefaultTreeModel) scriptTree.getModel()).getPathToRoot(node)));
          scriptTree.getParent().invalidate();
          scriptTree.getParent().validate();
          scriptTree.repaint();
          scriptTree.updateUI();
        }
      }
    } catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  private void checkScriptName(String pName) throws Exception {
    if (pName.length() == 0 || pName.indexOf("/") >= 0 || pName.indexOf("\\") >= 0 || pName.indexOf(".") >= 0) {
      throw new Exception("<" + pName + "> is not a valid script name");
    }
  }

  public void deleteScriptBtn_clicked() {
    DefaultMutableTreeNode selNode = getSelNode();
    if (selNode != null && selNode instanceof JWFScriptUserNode) {
      if (StandardDialogs.confirm(rootPanel, "Do you really want to permanently delete this script?")) {
        JWFScriptUserNode scriptNode = (JWFScriptUserNode) selNode;
        scriptNode.deleteScript();
        DefaultTreeModel model = (DefaultTreeModel) scriptTree.getModel();
        model.removeNodeFromParent(selNode);
        enableControls();
      }
    }
  }

  public void scriptRename_clicked() {
    try {
      DefaultMutableTreeNode selNode = getSelNode();
      if (selNode != null && selNode instanceof JWFScriptUserNode) {
        JWFScriptUserNode scriptNode = (JWFScriptUserNode) selNode;
        String newName = StandardDialogs.promptForText(rootPanel, "Please enter a new name", scriptNode.getCaption());
        if (newName != null) {
          checkScriptName(newName);
          scriptNode.rename(newName);
          scriptTree.getParent().invalidate();
          scriptTree.getParent().validate();
          scriptTree.repaint();
        }
      }
    } catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  public void addMacroButtonBtn_clicked() {
    try {
      DefaultMutableTreeNode selNode = getSelNode();
      if (selNode != null && selNode instanceof ScriptNode) {
        String scriptFilename;
        String caption;
        boolean internal;
        if (selNode instanceof JWFScriptInternalNode) {
          scriptFilename = ((JWFScriptInternalNode) selNode).getResFilename();
          caption = ((JWFScriptInternalNode) selNode).getCaption();
          internal = true;
        } else if (selNode instanceof JWFScriptUserNode) {
          scriptFilename = ((JWFScriptUserNode) selNode).getFilename();
          caption = ((JWFScriptUserNode) selNode).getCaption();
          internal = false;
        } else {
          throw new Exception("Unknown node type <" + selNode.getClass() + ">");
        }
        String hint = caption;
        int CAPTION_MAX_SIZE = 6;
        if (caption.length() > CAPTION_MAX_SIZE) {
          caption = caption.substring(0, CAPTION_MAX_SIZE);
        }
        MacroButton button = new MacroButton();
        button.setCaption(caption);
        button.setHint(hint);
        button.setInternal(internal);
        button.setMacro(scriptFilename);
        prefs.getTinaMacroButtons().add(button);
        refreshMacroButtonsTable();
        int selRow = prefs.getTinaMacroButtons().size() - 1;
        data.macroButtonsTable.getSelectionModel().setSelectionInterval(selRow, selRow);
        enableMacroButtonsControls();
        tinaController.refreshMacroButtonsPanel();
      }
    } catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  public void macroButtonMoveUp() {
    int row = data.macroButtonsTable.getSelectedRow();
    List<MacroButton> buttons = prefs.getTinaMacroButtons();
    MacroButton button = buttons.get(row);
    buttons.remove(row);
    buttons.add(row - 1, button);
    refreshMacroButtonsTable();
    data.macroButtonsTable.getSelectionModel().setSelectionInterval(row - 1, row - 1);
    enableMacroButtonsControls();
    tinaController.refreshMacroButtonsPanel();
  }

  public void macroButtonMoveDown() {
    int row = data.macroButtonsTable.getSelectedRow();
    List<MacroButton> buttons = prefs.getTinaMacroButtons();
    MacroButton button = buttons.get(row);
    buttons.remove(row);
    buttons.add(row + 1, button);
    refreshMacroButtonsTable();
    data.macroButtonsTable.getSelectionModel().setSelectionInterval(row + 1, row + 1);
    enableMacroButtonsControls();
    tinaController.refreshMacroButtonsPanel();
  }

  public void macroButtonDelete() {
    int row = data.macroButtonsTable.getSelectedRow();
    List<MacroButton> buttons = prefs.getTinaMacroButtons();
    buttons.remove(row);
    refreshMacroButtonsTable();
    if (buttons.size() > 0) {
      data.macroButtonsTable.getSelectionModel().setSelectionInterval(0, 0);
    }

    enableMacroButtonsControls();
    tinaController.refreshMacroButtonsPanel();
  }

  public void macroButtonsTableClicked() {
    enableMacroButtonsControls();
  }

  private void refreshMacroButtonsTable() {
    final int COL_CAPTION = 0;
    final int COL_HINT = 1;
    final int COL_MACRO = 2;

    data.macroButtonsTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return prefs.getTinaMacroButtons().size();
      }

      @Override
      public int getColumnCount() {
        return 3;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_CAPTION:
            return "Caption";
          case COL_HINT:
            return "Hint";
          case COL_MACRO:
            return "Macro";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        List<MacroButton> buttons = prefs.getTinaMacroButtons();
        if (rowIndex >= 0 && rowIndex < buttons.size()) {
          MacroButton button = buttons.get(rowIndex);
          switch (columnIndex) {
            case COL_CAPTION:
              return button.getCaption();
            case COL_HINT:
              return button.getHint();
            case COL_MACRO:
              return button.getMacro();
          }
        }
        return null;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return column == COL_CAPTION || column == COL_HINT || column == COL_MACRO;
      }

      @Override
      public void setValueAt(Object aValue, int row, int column) {
        List<MacroButton> buttons = prefs.getTinaMacroButtons();
        if (row >= 0 && row < buttons.size()) {
          MacroButton button = buttons.get(row);
          String valStr = (String) aValue;
          if (valStr == null) {
            valStr = "";
          }
          switch (column) {
            case COL_CAPTION: {
              if (!valStr.equals(button.getCaption())) {
                button.setCaption(valStr);
                tinaController.refreshMacroButtonsPanel();
              }
              break;
            }
            case COL_HINT: {
              if (!valStr.equals(button.getHint())) {
                button.setHint(valStr);
                tinaController.refreshMacroButtonsPanel();
              }
              break;
            }
            case COL_MACRO: {
              if (!valStr.equals(button.getMacro())) {
                button.setMacro(valStr);
                tinaController.refreshMacroButtonsPanel();
              }
              break;
            }
            default: // nothing to do
              break;
          }
        }
        super.setValueAt(aValue, row, column);
      }

    });
    data.macroButtonsTable.getTableHeader().setFont(data.transformationsTable.getFont());
    data.macroButtonsTable.getColumnModel().getColumn(COL_CAPTION).setWidth(20);
    data.macroButtonsTable.getColumnModel().getColumn(COL_HINT).setPreferredWidth(40);
    data.macroButtonsTable.getColumnModel().getColumn(COL_MACRO).setWidth(80);
  }

  public void refreshControls() {
    refreshMacroButtonsTable();
    enableControls();
  }

  public void editScriptBtn_clicked() {
    DefaultMutableTreeNode selNode = getSelNode();
    if (selNode != null && selNode instanceof JWFScriptUserNode) {
      JWFScriptUserNode scriptNode = (JWFScriptUserNode) selNode;
      try {
        ScriptEditDialog dlg = new ScriptEditDialog(tinaController, SwingUtilities.getWindowAncestor(rootPanel), errorHandler);
        dlg.setScriptNode(scriptNode);
        dlg.setVisible(true);
      } catch (Exception ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void importSchriptsBtn_clicked() {
    try {

      JFileChooser fileChooser = new JFileChooser();
      try {
        if (prevScriptDrawer != null && prevScriptDrawer.length() > 0) {
          fileChooser.setCurrentDirectory(new File(prevScriptDrawer));
        }
      } catch (Exception ex) {

      }

      fileChooser.setFileFilter(new FileFilter() {
        @Override
        public boolean accept(File file) {
          return file.isDirectory() || file.getName().toLowerCase().endsWith(".zip");
        }

        @Override
        public String getDescription() {
          return "*.zip";
        }
      });
      if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
        prevScriptDrawer = fileChooser.getSelectedFile().getParent();

        String filename = fileChooser.getSelectedFile().getAbsolutePath();
        Map<String, byte[]> content = unzipArchive(filename);

        if (content.isEmpty()) {
          throw new Exception("Could not unzip file \"" + filename + "\"");
        }


        final String scriptExtension = ".jwfscript";
        final String gradientExtension = ".ugr";
        int scriptCount=0;
        List<String> importedScripts=new ArrayList<>();
        for (String entryName : content.keySet()) {
          String extension;
          if(entryName.endsWith(scriptExtension)) {
            extension = scriptExtension;
          }
          else if(entryName.endsWith(gradientExtension)) {
            extension = gradientExtension;
          }
          else {
            extension = null;
          }
          if (extension!=null) {
            String rawScriptName = entryName.substring(0, entryName.length() -(extension.length()));
            String scriptName = rawScriptName;
            if(scriptName.contains("\\")) {
              scriptName = scriptName.substring(scriptName.lastIndexOf("\\")+1, scriptName.length());
            }
            if(scriptName.contains("/")) {
              scriptName = scriptName.substring(scriptName.lastIndexOf("/")+1, scriptName.length());
            }
            scriptCount++;

            String baseDrawer = prefs.getTinaJWFScriptPath();
            if (baseDrawer == null || baseDrawer.equals("") || baseDrawer.equals(".") || baseDrawer.equals("/") || baseDrawer.equals("\\")) {
              throw new Exception("Invalid script-drawer. Please enter a valid script-path in the Preferences-Window (property \"tinaJWFScriptPath\")");
            }
            File destinationFile = new File(baseDrawer, scriptName + extension);
            if (!destinationFile.exists() || JOptionPane.showConfirmDialog(null,
                    "The destination-file \""+scriptName+extension+"\"already exists. Do you want to overwrite it?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION) == 0) {
              Tools.writeFile(destinationFile.getAbsolutePath(), content.get(rawScriptName + extension));
              String descExtension = ".txt";
              byte description[] = content.get(rawScriptName + descExtension);
              if (description != null) {
                Tools.writeFile(new File(baseDrawer, scriptName + descExtension).getAbsolutePath(), content.get(rawScriptName + descExtension));
              }
              importedScripts.add(scriptName);
            }
          }
        }

        if (scriptCount==0) {
          throw new Exception("File \"" + filename + "\" does not contain a script-file with \"*" + scriptExtension + "\"-extension");
        }
        if(!importedScripts.isEmpty()) {
          initScriptLibrary();
          String msg;
          if(importedScripts.size()==1) {
            msg = "The script \"" + importedScripts.get(0) + "\" was imported successfully";
          }
          else {
            String scriptsNames = importedScripts.stream().collect(Collectors.joining("\", \""));
            if(scriptsNames.length()>64) {
              scriptsNames = scriptsNames.substring(0, 64)+ "...";
            }
            msg = importedScripts.size()+ " scripts \"" + scriptsNames + "\" where imported successfully";
          }
          JOptionPane.showMessageDialog(rootPanel, msg);
        }

      }
    } catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  private Map<String, byte[]> unzipArchive(String filename) throws Exception {
    Map<String, byte[]> content = new HashMap<>();
    ZipInputStream zis = new ZipInputStream(new FileInputStream(filename));
    try {
      byte[] buffer = new byte[1024];
      ZipEntry ze = zis.getNextEntry();
      while (ze != null) {
        String entryName = ze.getName();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        while ((len = zis.read(buffer)) > 0) {
          bos.write(buffer, 0, len);
        }
        bos.close();
        content.put(entryName, bos.toByteArray());
        ze = zis.getNextEntry();
      }
    } finally {
      zis.closeEntry();
      zis.close();
    }
    return content;
  }


}
