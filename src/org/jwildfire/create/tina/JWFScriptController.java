package org.jwildfire.create.tina;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jwildfire.base.MacroButton;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.Flam3GradientReader;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.swing.StandardDialogs;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.TinaControllerData;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.swing.ErrorHandler;

public class JWFScriptController {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final TinaControllerData data;
  private final JTree scriptTree;
  private final JTextArea scriptDescriptionTextArea;
  private final JTextArea scriptTextArea;
  private final JButton compileScriptButton;
  private final JButton saveScriptBtn;
  private final JButton revertScriptBtn;
  private final JButton rescanScriptsBtn;
  private final JButton newScriptBtn;
  private final JButton newScriptFromFlameBtn;
  private final JButton deleteScriptBtn;
  private final JButton scriptRenameBtn;
  private final JButton scriptDuplicateBtn;
  private final JButton scriptRunBtn;
  private final JButton addMacroButtonBtn;
  private boolean allowEdit = false;
  private boolean editing = false;
  private boolean noTextChange = false;
  private DefaultMutableTreeNode userScriptsRootNode;

  public JWFScriptController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel, TinaControllerData pData, JTree pScriptTree, JTextArea pScriptDescriptionTextArea,
      JTextArea pScriptTextArea, JButton pCompileScriptButton, JButton pSaveScriptButton, JButton pRevertScriptButton, JButton pRescanScriptsBtn,
      JButton pNewScriptBtn, JButton pNewScriptFromFlameBtn, JButton pDeleteScriptBtn, JButton pScriptRenameBtn, JButton pScriptDuplicateBtn, JButton pScriptRunBtn,
      JButton pAddMacroButtonBtn) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    data = pData;
    scriptTree = pScriptTree;
    scriptDescriptionTextArea = pScriptDescriptionTextArea;
    scriptTextArea = pScriptTextArea;
    compileScriptButton = pCompileScriptButton;
    rescanScriptsBtn = pRescanScriptsBtn;
    saveScriptBtn = pSaveScriptButton;
    revertScriptBtn = pRevertScriptButton;
    newScriptBtn = pNewScriptBtn;
    newScriptFromFlameBtn = pNewScriptFromFlameBtn;
    deleteScriptBtn = pDeleteScriptBtn;
    scriptRenameBtn = pScriptRenameBtn;
    scriptDuplicateBtn = pScriptDuplicateBtn;
    scriptRunBtn = pScriptRunBtn;
    addMacroButtonBtn = pAddMacroButtonBtn;

    scriptTextArea.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void removeUpdate(DocumentEvent e) {
        scriptTextChanged();
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        scriptTextChanged();
      }

      @Override
      public void changedUpdate(DocumentEvent arg0) {
        scriptTextChanged();
      }

    });
    scriptDescriptionTextArea.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void removeUpdate(DocumentEvent e) {
        scriptDescriptionChanged();
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        scriptDescriptionChanged();
      }

      @Override
      public void changedUpdate(DocumentEvent arg0) {
        scriptDescriptionChanged();
      }

    });

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

  protected void scriptDescriptionChanged() {
    if (allowEdit && !editing && !noTextChange) {
      editing = true;
      enableControls();
    }
  }

  protected void scriptTextChanged() {
    if (allowEdit && !editing && !noTextChange) {
      editing = true;
      enableControls();
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
    allowEdit = (selNode != null) && (selNode instanceof ScriptUserNode);
    boolean scriptSelected = (selNode != null) && ((selNode instanceof ScriptUserNode) || (selNode instanceof ScriptInternalNode));
    boolean userScriptSelected = (selNode != null) && (selNode instanceof ScriptUserNode);
    boolean userPathSelected = (selNode != null) && ((selNode instanceof ScriptUserNode) || ((selNode instanceof ScriptFolderNode) && ((ScriptFolderNode) selNode).isUserDir()));

    scriptDescriptionTextArea.setEditable(allowEdit || editing);
    scriptTextArea.setEditable(allowEdit || editing);
    scriptDescriptionTextArea.setEnabled(scriptSelected);
    scriptTextArea.setEnabled(scriptSelected);

    compileScriptButton.setEnabled(scriptSelected);
    rescanScriptsBtn.setEnabled(!editing);
    newScriptBtn.setEnabled(enableUserScripts && userPathSelected && !editing);
    newScriptFromFlameBtn.setEnabled(enableUserScripts && userPathSelected && !editing && tinaController.getCurrFlame() != null);
    deleteScriptBtn.setEnabled(enableUserScripts && userScriptSelected && !editing);
    scriptRenameBtn.setEnabled(enableUserScripts && userScriptSelected && !editing);
    scriptDuplicateBtn.setEnabled(enableUserScripts && scriptSelected && !editing);
    scriptRunBtn.setEnabled(scriptSelected);
    saveScriptBtn.setEnabled(editing);
    revertScriptBtn.setEnabled(editing);
    scriptTree.setEnabled(!editing);
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

  private interface ScriptNode {
    public String getScript() throws Exception;

    public String getDescription() throws Exception;

    public String getCaption();

  }

  private static class ScriptInternalNode extends DefaultMutableTreeNode implements ScriptNode {
    private static final long serialVersionUID = 1L;
    private final String resFilename;
    private final String caption;
    private String script;
    private String description;

    public ScriptInternalNode(String pCaption, String pResFilename) {
      super(pCaption, false);
      caption = pCaption;
      resFilename = pResFilename;
    }

    @Override
    public String getScript() throws Exception {
      if (script == null) {
        // for the base path inside the jar file
        RGBPaletteReader reader = new Flam3GradientReader();
        InputStream is = reader.getClass().getResourceAsStream(resFilename);
        if (is != null) {
          script = Tools.readUTF8Textfile(is);
        }
        else {
          script = "";
        }
      }
      return script;
    }

    @Override
    public String getDescription() throws Exception {
      if (description == null) {
        // for the base path inside the jar file
        RGBPaletteReader reader = new Flam3GradientReader();
        String filename = resFilename.substring(0, resFilename.length() - Tools.FILEEXT_JWFSCRIPT.length()) + Tools.FILEEXT_TXT;
        InputStream is = reader.getClass().getResourceAsStream(filename);
        if (is != null) {
          description = Tools.readUTF8Textfile(is);
        }
        else {
          description = "";
        }
      }
      return description;
    }

    @Override
    public String getCaption() {
      return caption;
    }

    public String getResFilename() {
      return resFilename;
    }
  }

  private static class ScriptUserNode extends DefaultMutableTreeNode implements ScriptNode {
    private static final long serialVersionUID = 1L;
    private String caption;
    private final String filename;
    private String script;
    private String description;

    public ScriptUserNode(String pCaption, String pFilename) {
      super(pCaption, false);
      filename = pFilename;
      caption = pCaption;
    }

    @Override
    public String getScript() throws Exception {
      if (script == null) {
        script = Tools.readUTF8Textfile(filename);
      }
      return script;
    }

    private String getDescFilename(String pFilename) {
      return pFilename.substring(0, pFilename.length() - Tools.FILEEXT_JWFSCRIPT.length()) + Tools.FILEEXT_TXT;
    }

    @Override
    public String getDescription() throws Exception {
      if (description == null) {
        String descFilename = getDescFilename(filename);
        if (new File(descFilename).exists()) {
          description = Tools.readUTF8Textfile(descFilename);
        }
        else {
          description = "";
        }
      }
      return description;
    }

    public void saveScript(String pScript, String pDescription) throws Exception {
      if (pScript == null) {
        pScript = "";
      }
      Tools.writeUTF8Textfile(filename, pScript);
      script = pScript;

      if (pDescription == null) {
        pDescription = "";
      }
      Tools.writeUTF8Textfile(getDescFilename(filename), pDescription);
      description = pDescription;
    }

    public void deleteScript() {
      new File(filename).delete();
      String descFilename = getDescFilename(filename);
      File descFile = new File(descFilename);
      if (descFile.exists()) {
        descFile.delete();
      }
    }

    public void rename(String pNewName) throws Exception {
      File file = new File(filename);
      String scriptFilename = file.getParentFile().getAbsolutePath() + File.separator + pNewName + "." + Tools.FILEEXT_JWFSCRIPT;
      File scriptFile = new File(scriptFilename);
      if (scriptFile.exists()) {
        throw new Exception("File <" + scriptFile.getAbsolutePath() + "> already exists");
      }

      String descFilename = file.getParentFile().getAbsolutePath() + File.separator + pNewName + "." + Tools.FILEEXT_TXT;
      File descFile = new File(descFilename);
      if (descFile.exists()) {
        throw new Exception("File <" + descFile.getAbsolutePath() + "> already exists");
      }

      getDescription();
      String oldFilename = filename;
      Tools.writeUTF8Textfile(scriptFilename, getScript());
      try {
        Tools.writeUTF8Textfile(descFilename, getDescription());
      }
      catch (Exception ex) {
        new File(scriptFilename).delete();
        throw ex;
      }

      new File(oldFilename).delete();
      new File(getDescFilename(oldFilename)).delete();

      setCaption(pNewName);
    }

    private void setCaption(String pNewName) {
      caption = pNewName;
      setUserObject(pNewName);
    }

    @Override
    public String getCaption() {
      return caption;
    }

    public String getFilename() {
      return filename;
    }
  }

  private static class ScriptFolderNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;
    private final boolean userDir;
    private final String directory;

    public ScriptFolderNode(String pCaption, String pDirectory, boolean pUserDir) {
      super(pCaption, true);
      userDir = pUserDir;
      directory = pDirectory;
    }

    @Override
    public boolean isLeaf() {
      return false;
    }

    public boolean isUserDir() {
      return userDir;
    }

    public String getDirectory() {
      return directory;
    }

  }

  private static class InvalidScriptFolderNode extends ScriptFolderNode {
    private static final long serialVersionUID = 1L;

    public InvalidScriptFolderNode() {
      super("(user-script-path is empty, check the Prefs)", null, true);
    }
  }

  private void initScriptLibrary() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Script library", true);
    // Internal flames
    {
      String[] ressources = {
          "LoonieSplits DT",
          "Buckballs by DT",
          "M&M Flower DT",
          "Add Random Final FX by MH and MO",
          "Textured_Cylinders_Rev01_by_MH",
          "Crackle_Styles_Chooser_Rev01_by_MH",
          "Escher Flux", "Mobius Dragon", "Soft Julian", "Wrap into Bubble", "Wrap into Heart", "Wrap into SubFlame",
          "HypertilePoincare_Rev2", "Bwraps-bubble-Julian2", "Bwraps and bubbles", "Oily_Juliascope_Rev1", "Oily_Rev3",
          "Plastic", "SphericalCross_Rev2", "SuperSusan_Rev1", "TomsSpiralSpiral_Rev3", "Wedge_Sph_Marble" };
      List<String> resLst = Arrays.asList(ressources);
      Collections.sort(resLst);
      ressources = (String[]) resLst.toArray();

      // for the base path inside the jar file
      RGBPaletteReader reader = new Flam3GradientReader();
      DefaultMutableTreeNode defaultFolderNode = null;
      for (String ressource : ressources) {
        try {
          String resFilename = "scripts/" + ressource + "." + Tools.FILEEXT_JWFSCRIPT;
          InputStream is = reader.getClass().getResourceAsStream(resFilename);
          if (is != null) {

            ScriptInternalNode node = new ScriptInternalNode(ressource, resFilename);

            if (defaultFolderNode == null) {
              defaultFolderNode = new ScriptFolderNode("Built-in scripts (read-only)", null, false);
              root.add(defaultFolderNode);
            }
            defaultFolderNode.add(node);
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

    // External flames
    {
      String baseDrawer = prefs.getTinaJWFScriptPath();
      if (baseDrawer == null || baseDrawer.equals("") || baseDrawer.equals(".") || baseDrawer.equals("/") || baseDrawer.equals("\\")) {
        root.add(new InvalidScriptFolderNode());
      }
      else {
        DefaultMutableTreeNode parentNode = userScriptsRootNode = new ScriptFolderNode("Your scripts", baseDrawer, true);
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
    try {
      Arrays.sort(list, new Comparator<File>() {

        @Override
        public int compare(File o1, File o2) {
          return o1.getName().compareTo(o2.getName());
        }

      });
    }
    catch (Exception ex) {
      // ex.printStackTrace();
    }

    if (list != null) {
      for (File f : list) {
        if (f.isDirectory()) {
          DefaultMutableTreeNode newParentNode = new ScriptFolderNode(f.getName(), f.getAbsolutePath(), true);
          pParentNode.add(newParentNode);
          scanUserScripts(f.getAbsolutePath(), newParentNode);
        }
        else {
          String filename = f.getAbsolutePath();
          String lcFilename = filename.toLowerCase();
          if (lcFilename.length() != filename.length()) {
            lcFilename = filename;
          }
          int pos = lcFilename.lastIndexOf("." + Tools.FILEEXT_JWFSCRIPT.toLowerCase());
          if (pos > 0 && pos == filename.length() - Tools.FILEEXT_JWFSCRIPT.length() - 1) {
            String caption = f.getName().substring(0, f.getName().length() - Tools.FILEEXT_JWFSCRIPT.length() - 1);
            ScriptUserNode node = new ScriptUserNode(caption, filename);
            pParentNode.add(node);
          }
        }
      }
    }
  }

  public void scriptPropertiesTree_changed(TreeSelectionEvent e) {
    if (!editing) {
      enableControls();
      DefaultMutableTreeNode selNode = getSelNode();
      if (selNode != null && selNode instanceof ScriptNode) {
        ScriptNode scriptNode = (ScriptNode) selNode;
        noTextChange = true;
        try {
          try {
            scriptTextArea.setText(scriptNode.getScript());
            scriptTextArea.setCaretPosition(0);
            scriptDescriptionTextArea.setText(scriptNode.getDescription());
            scriptDescriptionTextArea.setCaretPosition(0);
          }
          catch (Exception ex) {
            errorHandler.handleError(ex);
          }
        }
        finally {
          noTextChange = false;
        }
      }
      else {
        scriptTextArea.setText("");
        scriptDescriptionTextArea.setText("");
      }
    }
  }

  public void rescanBtn_clicked() {
    initScriptLibrary();
  }

  public void scriptRunBtn_clicked() {
    try {
      tinaController.runScript();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void compileScriptButton_clicked() {
    try {
      tinaController.compileScript();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void saveScriptButton_clicked() {
    DefaultMutableTreeNode selNode = getSelNode();
    if (selNode != null && selNode instanceof ScriptUserNode) {
      ScriptUserNode scriptNode = (ScriptUserNode) selNode;
      try {
        scriptNode.saveScript(scriptTextArea.getText(), scriptDescriptionTextArea.getText());
        editing = false;
        enableControls();
      }
      catch (Exception ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void revertScriptButton_clicked() {
    DefaultMutableTreeNode selNode = getSelNode();
    if (selNode != null && selNode instanceof ScriptNode) {
      ScriptNode scriptNode = (ScriptNode) selNode;
      noTextChange = true;
      try {
        try {
          scriptTextArea.setText(scriptNode.getScript());
          scriptTextArea.setCaretPosition(0);
          scriptDescriptionTextArea.setText(scriptNode.getDescription());
          scriptDescriptionTextArea.setCaretPosition(0);
        }
        catch (Exception ex) {
          errorHandler.handleError(ex);
        }
      }
      finally {
        noTextChange = false;
        editing = false;
        enableControls();
      }
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
          if (selNode instanceof ScriptFolderNode) {
            parentForNewNode = selNode;
          }
          else {
            parentForNewNode = (DefaultMutableTreeNode) selNode.getParent();
          }
          String scriptFilename;
          String descFilename;
          if (selNode instanceof ScriptInternalNode) {
            String basePath = getBaseScriptPath();
            scriptFilename = basePath + newName + "." + Tools.FILEEXT_JWFSCRIPT;
            descFilename = basePath + newName + "." + Tools.FILEEXT_TXT;
          }
          else if (selNode instanceof ScriptUserNode) {
            ScriptUserNode userNode = (ScriptUserNode) selNode;
            scriptFilename = new File(userNode.getFilename()).getParentFile().getAbsolutePath() + File.separator + newName + "." + Tools.FILEEXT_JWFSCRIPT;
            descFilename = new File(userNode.getFilename()).getParentFile().getAbsolutePath() + File.separator + newName + "." + Tools.FILEEXT_TXT;
          }
          else if (selNode instanceof ScriptFolderNode && ((ScriptFolderNode) selNode).isUserDir()) {
            ScriptFolderNode folderNode = (ScriptFolderNode) selNode;
            scriptFilename = folderNode.getDirectory() + File.separator + newName + "." + Tools.FILEEXT_JWFSCRIPT;
            descFilename = folderNode.getDirectory() + File.separator + newName + "." + Tools.FILEEXT_TXT;
          }
          else {
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
          ScriptUserNode node = new ScriptUserNode(newName, scriptFilename);
          parentForNewNode.add(node);
          scriptTree.setSelectionPath(new TreePath(((DefaultTreeModel) scriptTree.getModel()).getPathToRoot(node)));
          scriptTree.getParent().invalidate();
          scriptTree.getParent().validate();
          scriptTree.repaint();
          scriptTree.updateUI();
        }
      }
    }
    catch (Exception ex) {
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
    sb.append("import org.jwildfire.create.tina.variation.VariationFunc;\n");
    sb.append("import org.jwildfire.create.tina.variation.VariationFuncList;\n");
    sb.append("import org.jwildfire.create.tina.mutagen.RandomGradientMutation;\n");
    sb.append("import org.jwildfire.create.tina.transform.XFormTransformService;\n");

    sb.append("\n");
    sb.append("");
    sb.append("public void run(ScriptRunnerEnvironment pEnv) throws Exception {\n");
    sb.append("  // create a new flame\n");
    sb.append("  Flame flame=new Flame();");
    sb.append("  flame.getLayers().clear(); // get rid of the default layer because we create all layers by ourselves");
    sb.append("  // set the flame main attributes\n");
    sb.append("  flame.setCamRoll(" + Tools.doubleToString(pFlame.getCamRoll()) + ");\n");
    sb.append("  flame.setCamPitch(" + Tools.doubleToString(pFlame.getCamPitch()) + ");\n");
    sb.append("  flame.setCamYaw(" + Tools.doubleToString(pFlame.getCamYaw()) + ");\n");
    sb.append("  flame.setCamPerspective(" + Tools.doubleToString(pFlame.getCamPerspective()) + ");\n");
    sb.append("  flame.setWidth(" + pFlame.getWidth() + ");\n");
    sb.append("  flame.setHeight(" + pFlame.getHeight() + ");\n");
    sb.append("  flame.setPixelsPerUnit(" + Tools.doubleToString(pFlame.getPixelsPerUnit()) + ");\n");
    sb.append("  flame.setCamZoom(" + Tools.doubleToString(pFlame.getCamZoom()) + ");\n");
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
    }
    else {
      pSB.append("      layer.getXForms().add(xForm);\n");
    }
    pSB.append("      xForm.setWeight(" + Tools.doubleToString(pXForm.getWeight()) + ");\n");
    pSB.append("      xForm.setColor(" + Tools.doubleToString(pXForm.getColor()) + ");\n");
    pSB.append("      xForm.setColorSymmetry(" + Tools.doubleToString(pXForm.getColorSymmetry()) + ");\n");
    pSB.append("\n");
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
      pSB.append("      xForm.addVariation(" + Tools.doubleToString(pVariation.getAmount()) + ", VariationFuncList.getVariationFuncInstance(\"" + pVariation.getFunc().getName() + "\", true));\n");
    }
    else {
      pSB.append("      {\n");
      pSB.append("        VariationFunc varFunc=VariationFuncList.getVariationFuncInstance(\"" + pVariation.getFunc().getName() + "\", true);\n");
      for (int i = 0; i < pVariation.getFunc().getParameterNames().length; i++) {
        String pName = pVariation.getFunc().getParameterNames()[i];
        Object pValue = pVariation.getFunc().getParameterValues()[i];
        if (pValue instanceof Double) {
          pSB.append("        varFunc.setParameter(\"" + pName + "\", " + Tools.doubleToString((Double) pValue) + ");\n");
        }
        else {
          pSB.append("        varFunc.setParameter(\"" + pName + "\", " + pValue + ");\n");
        }
      }
      pSB.append("        xForm.addVariation(" + Tools.doubleToString(pVariation.getAmount()) + ", varFunc);\n");
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
          if (selNode instanceof ScriptInternalNode) {
            String basePath = getBaseScriptPath();
            scriptFilename = basePath + newName + "." + Tools.FILEEXT_JWFSCRIPT;
            descFilename = basePath + newName + "." + Tools.FILEEXT_TXT;
            parent = this.userScriptsRootNode;
          }
          else if (selNode instanceof ScriptUserNode) {
            ScriptUserNode userNode = (ScriptUserNode) selNode;
            parent = (DefaultMutableTreeNode) selNode.getParent();
            scriptFilename = new File(userNode.getFilename()).getParentFile().getAbsolutePath() + File.separator + newName + "." + Tools.FILEEXT_JWFSCRIPT;
            descFilename = new File(userNode.getFilename()).getParentFile().getAbsolutePath() + File.separator + newName + "." + Tools.FILEEXT_TXT;
            parent = (DefaultMutableTreeNode) selNode.getParent();
          }
          else {
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
          ScriptUserNode node = new ScriptUserNode(newName, scriptFilename);
          parent.add(node);
          scriptTree.setSelectionPath(new TreePath(((DefaultTreeModel) scriptTree.getModel()).getPathToRoot(node)));
          scriptTree.getParent().invalidate();
          scriptTree.getParent().validate();
          scriptTree.repaint();
          scriptTree.updateUI();
        }
      }
    }
    catch (Exception ex) {
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
    if (selNode != null && selNode instanceof ScriptUserNode) {
      if (StandardDialogs.confirm(rootPanel, "Do you really want to permanently delete this script?")) {
        ScriptUserNode scriptNode = (ScriptUserNode) selNode;
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
      if (selNode != null && selNode instanceof ScriptUserNode) {
        ScriptUserNode scriptNode = (ScriptUserNode) selNode;
        String newName = StandardDialogs.promptForText(rootPanel, "Please enter a new name", scriptNode.getCaption());
        if (newName != null) {
          checkScriptName(newName);
          scriptNode.rename(newName);
          scriptTree.getParent().invalidate();
          scriptTree.getParent().validate();
          scriptTree.repaint();
        }
      }
    }
    catch (Exception ex) {
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
        if (selNode instanceof ScriptInternalNode) {
          scriptFilename = ((ScriptInternalNode) selNode).getResFilename();
          caption = ((ScriptInternalNode) selNode).getCaption();
          internal = true;
        }
        else if (selNode instanceof ScriptUserNode) {
          scriptFilename = ((ScriptUserNode) selNode).getFilename();
          caption = ((ScriptUserNode) selNode).getCaption();
          internal = false;
        }
        else {
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
    }
    catch (Exception ex) {
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
            case COL_CAPTION:
            {
              if (!valStr.equals(button.getCaption())) {
                button.setCaption(valStr);
                tinaController.refreshMacroButtonsPanel();
              }
              break;
            }
            case COL_HINT:
            {
              if (!valStr.equals(button.getHint())) {
                button.setHint(valStr);
                tinaController.refreshMacroButtonsPanel();
              }
              break;
            }
            case COL_MACRO:
            {
              if (!valStr.equals(button.getMacro())) {
                button.setMacro(valStr);
                tinaController.refreshMacroButtonsPanel();
              }
              break;
            }
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
}
