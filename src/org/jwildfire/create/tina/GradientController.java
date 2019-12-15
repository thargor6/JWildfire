package org.jwildfire.create.tina;

import java.awt.Component;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.io.Flam3GradientReader;
import org.jwildfire.create.tina.io.MapGradientWriter;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.io.UniversalPaletteReader;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.palette.RGBPaletteRenderer;
import org.jwildfire.create.tina.swing.MapFileChooser;
import org.jwildfire.create.tina.swing.StandardDialogs;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;

public class GradientController {
  private final static int GRADIENT_THUMB_WIDTH = 200;
  private final static int GRADIENT_THUMB_HEIGHT = 18;

  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final JTree gradientLibTree;
  private final JPanel gradientLibraryPanel;
  private final JButton rescanBtn;
  private final JButton newFolderBtn;
  private final JButton renameFolderBtn;
  private final JList gradientsList;
  private String previousGradientPath;

  private GradientUserNode userGradientsRootNode;
  private boolean cmbRefreshing;

  public GradientController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel, JTree pGradientLibTree, JPanel pGradientLibraryPanel,
      JButton pRescanBtn, JButton pNewFolderBtn, JButton pRenameFolderBtn, JList pGradientsList) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    gradientLibTree = pGradientLibTree;
    gradientLibraryPanel = pGradientLibraryPanel;
    rescanBtn = pRescanBtn;
    newFolderBtn = pNewFolderBtn;
    renameFolderBtn = pRenameFolderBtn;
    gradientsList = pGradientsList;

    enableControls();
    //    initGradientsLibrary();
    //    enableControls();
  }

  private boolean _firstActivated = false;

  public void onActivate() {
    if (!_firstActivated) {
      initGradientsLibrary();
      enableControls();
      _firstActivated = true;
    }
  }

  private abstract static class AbstractGradientNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;
    protected final List<RGBPalette> gradientLibraryList = new ArrayList<RGBPalette>();
    private List<SimpleImage> thumbnails;

    public AbstractGradientNode(String pCaption) {
      super(pCaption, true);
    }

    public List<RGBPalette> getGradientLibraryList() {
      return gradientLibraryList;
    }

    public void clear() {
      gradientLibraryList.clear();
      thumbnails = null;
    }

    public List<SimpleImage> getThumbnails() {
      if (thumbnails == null) {
        thumbnails = new ArrayList<SimpleImage>();
        for (RGBPalette palette : gradientLibraryList) {
          thumbnails.add(new RGBPaletteRenderer().renderHorizPalette(palette, GRADIENT_THUMB_WIDTH, GRADIENT_THUMB_HEIGHT));
        }
      }
      return thumbnails;
    }

    public String getCaption() {
      return (String) getUserObject();
    }

  }

  private static class GradientInternalNode extends AbstractGradientNode {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean isLeaf() {
      return true;
    }

    public GradientInternalNode(String pCaption) {
      super(pCaption);
    }

  }

  private static class GradientUserNode extends AbstractGradientNode {
    private static final long serialVersionUID = 1L;
    private final String path;

    @Override
    public boolean isLeaf() {
      return false;
    }

    public boolean isRoot() {
      return path == null;
    }

    public GradientUserNode(String pCaption, String pPath) {
      super(pCaption);
      path = pPath;
    }

    public void rename(String pName) {
      System.out.println(path);
      File oldFile = new File(path, (String) getUserObject());
      File newFile = new File(path, pName);
      if (newFile.exists()) {
        throw new RuntimeException("Destination file <" + pName + "> already exists ");
      }

      if (!oldFile.renameTo(newFile)) {
        throw new RuntimeException("Rename failed");
      }
      setUserObject(pName);
    }

    public String getAbsolutePath() {
      return new File(path, (String) getUserObject()).getAbsolutePath();
    }

  }

  private static class InvalidGradientFolderNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;

    public InvalidGradientFolderNode() {
      super("(user-gradient-path is empty, check the Prefs)", true);
    }
  }

  private void initGradientsLibrary() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Gradient library", true);
    GradientInternalNode initialSelNode = null;
    // Internal gradients
    {
      String[] ressources = { "flam3-palettes.xml" };
      List<String> resLst = Arrays.asList(ressources);
      Collections.sort(resLst);
      ressources = resLst.toArray(new String[]{});

      // for the base path inside the jar file
      RGBPaletteReader reader = new Flam3GradientReader();
      DefaultMutableTreeNode defaultFolderNode = null;
      for (String ressource : ressources) {
        try {
          InputStream is = reader.getClass().getResourceAsStream(ressource);
          if (is != null) {
            List<RGBPalette> palettes = reader.readPalettes(is);
            if (palettes.size() > 0) {
              GradientInternalNode node = new GradientInternalNode(ressource);
              node.getGradientLibraryList().addAll(palettes);
              if (defaultFolderNode == null) {
                defaultFolderNode = new DefaultMutableTreeNode("Built-in gradients (read-only)", true);
                root.add(defaultFolderNode);
              }
              defaultFolderNode.add(node);
              if (initialSelNode == null) {
                initialSelNode = node;
              }
            }
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

    // External flames
    {
      String baseDrawer = prefs.getTinaGradientPath();
      if (baseDrawer == null || baseDrawer.equals("") || baseDrawer.equals(".") || baseDrawer.equals("/") || baseDrawer.equals("\\")) {
        root.add(new InvalidGradientFolderNode());
      }
      else {
        GradientUserNode parentNode = userGradientsRootNode = new GradientUserNode("Your gradients", null);
        root.add(parentNode);
        scanUserGradients(baseDrawer, parentNode);
      }
    }
    gradientLibTree.setRootVisible(false);
    gradientLibTree.setModel(new DefaultTreeModel(root));
    if (initialSelNode != null) {
      try {
        TreeNode[] nodes = ((DefaultTreeModel) gradientLibTree.getModel()).getPathToRoot(initialSelNode);
        TreePath path = new TreePath(nodes);
        gradientLibTree.setSelectionPath(path);
        gradientTree_changed(null);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }

  }

  public void scanUserGradients(String path, GradientUserNode pParentNode) {
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
      }
      catch (Exception ex) {
        // ex.printStackTrace();
      }
      List<String> filenames = new ArrayList<String>();
      for (File f : list) {
        if (f.isDirectory()) {
          GradientUserNode newParentNode = new GradientUserNode(f.getName(), f.getParentFile().getAbsolutePath());
          pParentNode.add(newParentNode);
          scanUserGradients(f.getAbsolutePath(), newParentNode);
        }
        else {
          filenames.add(f.getAbsolutePath());
        }
      }
      try {
        readGradients(pParentNode, filenames);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private void readGradients(GradientUserNode pNode, List<String> pFilenames) {
    for (String filename : pFilenames) {
      List<RGBPalette> gradients = new UniversalPaletteReader().readPalettes(filename);
      if (gradients != null && gradients.size() > 0) {
        pNode.getGradientLibraryList().addAll(gradients);
      }
    }
  }

  public void enableControls() {
    DefaultMutableTreeNode node = getSelNode();
    boolean userNodeSelected = node != null && node instanceof GradientUserNode;
    newFolderBtn.setEnabled(userNodeSelected);
    renameFolderBtn.setEnabled(userNodeSelected && node != userGradientsRootNode);
  }

  private static class GradientNode {
    private final String caption;
    private final SimpleImage image;

    public GradientNode(String pCaption, SimpleImage pImage) {
      caption = pCaption;
      image = pImage;
    }

    public SimpleImage getImage() {
      return image;
    }

    @Override
    public String toString() {
      return caption;
    }

  }

  private static class GradientRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;

    private Map<GradientNode, ImageIcon> iconCache = new HashMap<GradientNode, ImageIcon>();

    public Component getListCellRendererComponent(JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean hasFocus) {
      JLabel label =
          (JLabel) super.getListCellRendererComponent(list,
              value,
              index,
              isSelected,
              hasFocus);
      GradientNode node = (GradientNode) value;
      ImageIcon icon = iconCache.get(node);
      if (icon == null) {
        icon = new ImageIcon(node.getImage().getBufferedImg());
        iconCache.put(node, icon);
      }
      label.setIcon(icon);
      return (label);
    }
  }

  public void updateGradientThumbnails(final AbstractGradientNode pNode, List<SimpleImage> pImages, List<RGBPalette> pGradientList) {
    Vector<GradientNode> listData = new Vector<GradientNode>();
    for (int i = 0; i < pImages.size(); i++) {
      GradientNode node = new GradientNode(pGradientList.get(i).toString(), pImages.get(i));
      listData.add(node);
    }
    gradientsList.setListData(listData);
    gradientsList.setCellRenderer(new GradientRenderer());
  }

  public void gradientLibraryGradientChanged() {
    AbstractGradientNode selNode;
    if (!cmbRefreshing && (selNode = getSelGradientNode()) != null) {
      if (tinaController.getCurrFlame() != null && gradientsList.getSelectedIndex() >= 0 && gradientsList.getSelectedIndex() < selNode.getGradientLibraryList().size()) {
        tinaController.saveUndoPoint();
        RGBPalette palette = selNode.getGradientLibraryList().get(gradientsList.getSelectedIndex()).makeCopy();
        tinaController.getCurrLayer().setPalette(palette);
        tinaController.setLastGradient(palette);
        tinaController.registerToEditor(tinaController.getCurrFlame(), tinaController.getCurrLayer());
        tinaController.refreshPaletteUI(palette);
        tinaController.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  private DefaultMutableTreeNode getSelNode() {
    DefaultMutableTreeNode selNode = null;
    {
      TreePath selPath = gradientLibTree.getSelectionPath();
      if (selPath != null) {
        selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
      }
    }
    return selNode;
  }

  private AbstractGradientNode getSelGradientNode() {
    DefaultMutableTreeNode selNode = getSelNode();
    return selNode != null && selNode instanceof AbstractGradientNode ? (AbstractGradientNode) selNode : null;
  }

  public void selectRandomGradient() {
    try {
      AbstractGradientNode selNode = getSelGradientNode();
      if (selNode != null && selNode.getGradientLibraryList().size() > 0) {
        if (selNode.getGradientLibraryList().size() > 1) {
          int oldIdx = gradientsList.getSelectedIndex();
          while (true) {
            int idx = (int) (Math.random() * selNode.getGradientLibraryList().size());
            if (idx != oldIdx) {
              gradientsList.setSelectedIndex(idx);
              break;
            }
          }
        }
      }
      else {
        throw new Exception("Please selected a non-empty gradient folder first");
      }
    }
    catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  public void gradientTree_changed(TreeSelectionEvent e) {
    enableControls();
    cmbRefreshing = true;
    try {
      DefaultMutableTreeNode selNode = getSelNode();
      if (selNode != null && selNode instanceof AbstractGradientNode) {
        AbstractGradientNode gradientNode = (AbstractGradientNode) selNode;
        updateGradientThumbnails(gradientNode, gradientNode.getThumbnails(), gradientNode.getGradientLibraryList());
      }
      else {
        gradientsList.setListData(new Vector<GradientNode>());
      }
    }
    finally {
      cmbRefreshing = false;
    }
  }

  public void rescanBtn_clicked() {
    initGradientsLibrary();
    enableControls();
  }

  private void rescan(File folder) {
    TreeModel model = gradientLibTree.getModel();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
    GradientUserNode userNode = findFolderNode(root, folder);
    if(userNode!=null) {
      userNode.clear();
      scanUserGradients(folder.getAbsolutePath(), userNode);
      {
        Object[] pathComponents = new Object[userNode.getLevel()];
        TreeNode currNode = userNode;
        int d=userNode.getLevel()-1;
        while(d>=0) {
          pathComponents[d--]=currNode;
          currNode = currNode.getParent();
        }
        TreePath path = new TreePath(pathComponents);
        gradientLibTree.setExpandsSelectedPaths(true);
        gradientLibTree.setSelectionPath(path);
      }
      gradientTree_changed(null);
    }
    enableControls();
  }

  private GradientUserNode findFolderNode(TreeNode parent, File searchFolder) {
    if(parent instanceof GradientUserNode) {
      GradientUserNode userNode = (GradientUserNode)parent;
      if(userNode.getAbsolutePath().equals(searchFolder.getAbsolutePath())) {
        return userNode;
      }
      else if(userNode.isRoot() && new File(prefs.getTinaGradientPath()).getAbsolutePath().equals(searchFolder.getAbsolutePath())) {
        return userNode;
      }
    }
    for(int i=0;i<parent.getChildCount();i++) {
      TreeNode child = parent.getChildAt(i);
      GradientUserNode userNode = findFolderNode(child, searchFolder);
      if(userNode!=null) {
        return userNode;
      }
    }
    return null;
  }

  public void newFolderBtn_clicked() {
    try {
      DefaultMutableTreeNode selNode = getSelNode();
      if (selNode != null && selNode instanceof GradientUserNode) {
        GradientUserNode gradientNode = (GradientUserNode) selNode;
        String newName = StandardDialogs.promptForText(rootPanel, "Please enter the name of the new sub-folder", "");
        if (newName != null) {
          checkFolderName(newName);
          File newDir;
          if (gradientNode == userGradientsRootNode) {
            newDir = new File(new File(prefs.getTinaGradientPath()).getAbsolutePath(), newName);
          }
          else {
            newDir = new File(gradientNode.getAbsolutePath(), newName);
          }

          if (!newDir.mkdir()) {
            throw new RuntimeException("The directory <" + newDir.getAbsolutePath() + "> could not be created");
          }
          GradientUserNode newNode = new GradientUserNode(newDir.getName(), newDir.getParentFile().getAbsolutePath());
          selNode.add(newNode);

          gradientLibTree.getParent().invalidate();
          gradientLibTree.getParent().validate();
          gradientLibTree.repaint();
          gradientLibTree.updateUI();
        }
      }
    }
    catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  public void renameFolderBtn_clicked() {
    try {
      DefaultMutableTreeNode selNode = getSelNode();
      if (selNode != null && selNode instanceof GradientUserNode && selNode != userGradientsRootNode) {
        GradientUserNode gradientNode = (GradientUserNode) selNode;
        String newName = StandardDialogs.promptForText(rootPanel, "Please enter a new name", gradientNode.getCaption());
        if (newName != null) {
          checkFolderName(newName);
          gradientNode.rename(newName);
          gradientLibTree.getParent().invalidate();
          gradientLibTree.getParent().validate();
          gradientLibTree.repaint();
          gradientLibTree.updateUI();
        }
      }
    }
    catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  private void checkFolderName(String pName) throws Exception {
    if (pName.length() == 0 || pName.indexOf("/") >= 0 || pName.indexOf("\\") >= 0 || pName.indexOf(".") >= 0) {
      throw new Exception("<" + pName + "> is not a valid script name");
    }
  }

  public void gradientSaveBtn_clicked() {
    try {
      Layer layer = tinaController.getCurrLayer();
      if (layer != null) {
        JFileChooser chooser = new MapFileChooser(prefs);
        if (previousGradientPath != null) {
          try {
            chooser.setCurrentDirectory(new File(previousGradientPath));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        else if (prefs.getTinaGradientPath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getTinaGradientPath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          RGBPalette gradient = layer.getPalette().makeCopy();
          gradient.setFlam3Name(file.getName());
          new MapGradientWriter().writeGradient(gradient, file.getAbsolutePath());
          previousGradientPath = file.getParent();
          tinaController.getMessageHelper().showStatusMessage(gradient, "gradient saved to library");
          rescan(file.getParentFile());
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

}
