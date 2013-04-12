package org.jwildfire.create.tina.dance;

import java.io.File;
import java.io.InputStream;

import javax.swing.JButton;
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
import org.jwildfire.create.tina.io.Flam3PaletteReader;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.swing.JWFScriptExecuteController;
import org.jwildfire.swing.ErrorHandler;

public class JWFScriptController {
  private final JWFScriptExecuteController scriptExecuteController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JTree scriptTree;
  private final JTextArea scriptDescriptionTextArea;
  private final JTextArea scriptTextArea;
  private final JButton compileScriptButton;
  private final JButton saveScriptBtn;
  private final JButton revertScriptBtn;
  private final JButton rescanScriptsBtn;
  private final JButton newScriptBtn;
  private final JButton deleteScriptBtn;
  private final JButton scriptRenameBtn;
  private final JButton scriptDuplicateBtn;
  private final JButton scriptRunBtn;
  private boolean allowEdit = false;
  private boolean editing = false;
  private boolean noTextChange = false;

  public JWFScriptController(JWFScriptExecuteController pScriptExecuteController, ErrorHandler pErrorHandler, Prefs pPrefs, JTree pScriptTree, JTextArea pScriptDescriptionTextArea,
      JTextArea pScriptTextArea, JButton pCompileScriptButton, JButton pSaveScriptButton, JButton pRevertScriptButton, JButton pRescanScriptsBtn,
      JButton pNewScriptBtn, JButton pDeleteScriptBtn, JButton pScriptRenameBtn, JButton pScriptDuplicateBtn, JButton pScriptRunBtn) {
    scriptExecuteController = pScriptExecuteController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    scriptTree = pScriptTree;
    scriptDescriptionTextArea = pScriptDescriptionTextArea;
    scriptTextArea = pScriptTextArea;
    compileScriptButton = pCompileScriptButton;
    rescanScriptsBtn = pRescanScriptsBtn;
    saveScriptBtn = pSaveScriptButton;
    revertScriptBtn = pRevertScriptButton;
    newScriptBtn = pNewScriptBtn;
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

  private void enableControls() {
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
  }

  private static class ScriptInternalNode extends DefaultMutableTreeNode implements ScriptNode {
    private static final long serialVersionUID = 1L;
    private final String resFilename;
    private String script;
    private String description;

    public ScriptInternalNode(String pCaption, String pResFilename) {
      super(pCaption, false);
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
  }

  private static class ScriptUserNode extends DefaultMutableTreeNode implements ScriptNode {
    private static final long serialVersionUID = 1L;
    private final String filename;
    private String script;
    private String description;

    public ScriptUserNode(String pCaption, String pFilename) {
      super(pCaption, false);
      filename = pFilename;
    }

    @Override
    public String getScript() throws Exception {
      if (script == null) {
        script = Tools.readUTF8Textfile(filename);
      }
      return script;
    }

    private String getDescFilename() {
      return filename.substring(0, filename.length() - Tools.FILEEXT_JWFSCRIPT.length()) + Tools.FILEEXT_TXT;
    }

    @Override
    public String getDescription() throws Exception {
      if (description == null) {
        String descFilename = getDescFilename();
        if (new File(descFilename).exists()) {
          description = Tools.readUTF8Textfile(filename);
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
      Tools.writeUTF8Textfile(getDescFilename(), pDescription);
      description = pDescription;
    }
  }

  private static class ScriptFolderNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;
    private final boolean userDir;

    public ScriptFolderNode(String pCaption, boolean pUserDir) {
      super(pCaption, true);
      userDir = pUserDir;
    }

    @Override
    public boolean isLeaf() {
      return false;
    }

    public boolean isUserDir() {
      return userDir;
    }

  }

  private static class InvalidScriptFolderNode extends ScriptFolderNode {
    private static final long serialVersionUID = 1L;

    public InvalidScriptFolderNode() {
      super("(script-path is empty, check the Prefs)", false);
    }
  }

  private void initScriptLibrary() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Script library", true);
    // Internal flames
    {
      String[] ressources = { "Escher Flux", "Mobius Dragon", "Soft Julian", "Wrap into Bubble", "Wrap into Heart", "Wrap into SubFlame" };
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
              defaultFolderNode = new ScriptFolderNode("Built-in scripts (read-only)", false);
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
      DefaultMutableTreeNode parentNode = new ScriptFolderNode("Your scripts", true);
      root.add(parentNode);
      String baseDrawer = prefs.getTinaJWFScriptPath();
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
        DefaultMutableTreeNode newParentNode = new ScriptFolderNode(f.getName(), true);
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
      scriptExecuteController.runScript();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void compileScriptButton_clicked() {
    try {
      scriptExecuteController.compileScript();
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

  public void newScriptBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void duplicateScriptBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void deleteScriptBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void scriptRename_clicked() {
    // TODO Auto-generated method stub

  }
}
