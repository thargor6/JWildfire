package org.jwildfire.create.tina.dance;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3PaletteReader;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.swing.StandardDialogs;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.ErrorHandler;

public class JWFScriptController {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
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
  private boolean allowEdit = false;
  private boolean editing = false;
  private boolean noTextChange = false;
  private DefaultMutableTreeNode userScriptsRootNode;

  public JWFScriptController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel, JTree pScriptTree, JTextArea pScriptDescriptionTextArea,
      JTextArea pScriptTextArea, JButton pCompileScriptButton, JButton pSaveScriptButton, JButton pRevertScriptButton, JButton pRescanScriptsBtn,
      JButton pNewScriptBtn, JButton pNewScriptFromFlameBtn, JButton pDeleteScriptBtn, JButton pScriptRenameBtn, JButton pScriptDuplicateBtn, JButton pScriptRunBtn) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
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

    enableControls();
    initScriptLibrary();
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
    newScriptBtn.setEnabled(userPathSelected && !editing);
    newScriptFromFlameBtn.setEnabled(userPathSelected && !editing && tinaController.getCurrFlame() != null);
    deleteScriptBtn.setEnabled(userScriptSelected && !editing);
    scriptRenameBtn.setEnabled(userScriptSelected && !editing);
    scriptDuplicateBtn.setEnabled(scriptSelected && !editing);
    scriptRunBtn.setEnabled(scriptSelected);
    saveScriptBtn.setEnabled(editing);
    revertScriptBtn.setEnabled(editing);
    scriptTree.setEnabled(!editing);
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
        RGBPaletteReader reader = new Flam3PaletteReader();
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
        RGBPaletteReader reader = new Flam3PaletteReader();
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
      super("(script-path is empty, check the Prefs)", null, false);
    }
  }

  private void initScriptLibrary() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Script library", true);
    // Internal flames
    {
      String[] ressources = { "Escher Flux", "Mobius Dragon", "Soft Julian", "Wrap into Bubble", "Wrap into Heart", "Wrap into SubFlame",
          "HypertilePoincare_Rev2", "Bwraps-bubble-Julian2", "Bwraps and bubbles", "Oily_Juliascope_Rev1", "Oily_Rev3",
          "Plastic", "SphericalCross_Rev2", "SuperSusan_Rev1", "TomsSpiralSpiral_Rev3", "Wedge_Sph_Marble" };
      List<String> resLst = Arrays.asList(ressources);
      Collections.sort(resLst);
      ressources = (String[]) resLst.toArray();

      // for the base path inside the jar file
      RGBPaletteReader reader = new Flam3PaletteReader();
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
      DefaultMutableTreeNode parentNode = userScriptsRootNode = new ScriptFolderNode("Your scripts", baseDrawer, true);
      root.add(parentNode);
      if (baseDrawer == null || baseDrawer.equals("") || baseDrawer.equals(".") || baseDrawer.equals("/") || baseDrawer.equals("\\")) {
        parentNode.add(new InvalidScriptFolderNode());
      }
      else {
        scanUserScripts(baseDrawer, parentNode);
      }
    }
    scriptTree.setRootVisible(false);
    scriptTree.setModel(new DefaultTreeModel(root));
  }

  public void scanUserScripts(String path, DefaultMutableTreeNode pParentNode) {
    File root = new File(path);
    File[] list = root.listFiles();
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
        String newName = StandardDialogs.promptForText(rootPanel, "Please enter a name", "");
        if (newName != null) {
          checkScriptName(newName);
          DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selNode.getParent();
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
    sb.append("import org.jwildfire.create.tina.variation.VariationFunc;\n");
    sb.append("import org.jwildfire.create.tina.variation.VariationFuncList;\n");
    sb.append("\n");
    sb.append("");
    sb.append("public void run(ScriptRunnerEnvironment pEnv) throws Exception {\n");
    sb.append("  // create a new flame\n");
    sb.append("  Flame flame=new Flame();");
    sb.append("  // set the flame main attributes\n");
    sb.append("  flame.setCamRoll(" + Tools.doubleToString(pFlame.getCamRoll()) + ");\n");
    sb.append("  flame.setCamPitch(" + Tools.doubleToString(pFlame.getCamPitch()) + ");\n");
    sb.append("  flame.setCamYaw(" + Tools.doubleToString(pFlame.getCamYaw()) + ");\n");
    sb.append("  flame.setCamPerspective(" + Tools.doubleToString(pFlame.getCamPerspective()) + ");\n");
    sb.append("  flame.setPixelsPerUnit(" + Tools.doubleToString(pFlame.getPixelsPerUnit()) + ");\n");
    sb.append("  flame.setCamZoom(" + Tools.doubleToString(pFlame.getCamZoom()) + ");\n");

    sb.append("  // create the gradient\n");
    for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
      RGBColor color = pFlame.getPalette().getColor(i);
      sb.append("  flame.getPalette().setColor(" + i + ", " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ");\n");
    }

    if (pFlame.getXForms().size() > 0) {
      sb.append("  // add transforms\n");

    }
    if (pFlame.getFinalXForms().size() > 0) {
      sb.append("  // add final transforms\n");
    }
    sb.append("  // Load the flame in the editor and refresh the UI\n");
    sb.append("  pEnv.setCurrFlame(flame);\n");
    sb.append("}\n");

    //    {
    //      XForm xForm = xForm1 = new XForm();
    //      xForm.setWeight(0.5);
    //      xForm.setColor(Math.random());
    //      xForm.setColorSymmetry(-(0.8 + (Math.random() * 0.1)));
    //
    //      xForm.setCoeff00(0.266948); // a
    //      xForm.setCoeff10(0.137096); // b
    //      xForm.setCoeff20(0.04212); // e
    //      xForm.setCoeff01(0.071529); // c 
    //      xForm.setCoeff11(-0.511651); // d 
    //      xForm.setCoeff21(-0.334332); // f 
    //
    //      xForm.setPostCoeff00(0.5);
    //      xForm.setPostCoeff10(0);
    //      xForm.setPostCoeff01(0);
    //      xForm.setPostCoeff11(0.25);
    //      xForm.setPostCoeff20(0);
    //      xForm.setPostCoeff21(0.05);
    //
    //      xForm.addVariation((0.1 + (Math.random() * 0.1)), VariationFuncList.getVariationFuncInstance("bubble", true));
    //      xForm.addVariation(2.0, VariationFuncList.getVariationFuncInstance("pre_blur", true));
    //

    return sb.toString();
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
}
