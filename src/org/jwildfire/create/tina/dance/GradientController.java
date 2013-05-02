package org.jwildfire.create.tina.dance;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.io.Flam3PaletteReader;
import org.jwildfire.create.tina.io.RGBPaletteReader;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.palette.RGBPaletteRenderer;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;

public class GradientController {
  private final static int GRADIENT_THUMB_HEIGHT = 20;
  private final static int GRADIENT_THUMB_BORDER = 2;

  private JScrollPane gradientLibraryScrollPane;

  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final JTree gradientLibTree;
  private final JPanel gradientLibraryPanel;
  private final JComboBox gradientLibraryGradientCmb;
  private DefaultMutableTreeNode userGradientsRootNode;
  private boolean cmbRefreshing;

  public GradientController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel, JTree pGradientLibTree, JPanel pGradientLibraryPanel, JComboBox pGradientLibraryGradientCmb) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    gradientLibTree = pGradientLibTree;
    gradientLibraryPanel = pGradientLibraryPanel;
    gradientLibraryGradientCmb = pGradientLibraryGradientCmb;
    enableControls();
    initGradientsLibrary();
  }

  private abstract static class AbstractGradientNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;
    protected final List<RGBPalette> gradientLibraryList = new ArrayList<RGBPalette>();
    private List<SimpleImage> thumbnails;

    public AbstractGradientNode(String pCaption) {
      super(pCaption);
    }

    public List<RGBPalette> getGradientLibraryList() {
      return gradientLibraryList;
    }

    public List<SimpleImage> getThumbnails() {
      if (thumbnails == null) {
        thumbnails = new ArrayList<SimpleImage>();
        for (RGBPalette palette : gradientLibraryList) {
          thumbnails.add(new RGBPaletteRenderer().renderHorizPalette(palette, RGBPalette.PALETTE_SIZE, GRADIENT_THUMB_HEIGHT));
        }
      }
      return thumbnails;
    }

  }

  private static class GradientInternalNode extends AbstractGradientNode {
    private static final long serialVersionUID = 1L;

    public GradientInternalNode(String pCaption) {
      super(pCaption);
    }

  }

  private static class GradientUserNode extends AbstractGradientNode {

    public GradientUserNode(String pCaption) {
      super(pCaption);
    }

  }

  private static class GradientFolderNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;
    private final boolean userDir;
    private final String directory;

    public GradientFolderNode(String pCaption, String pDirectory, boolean pUserDir) {
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

  private static class InvalidGradientFolderNode extends GradientFolderNode {
    private static final long serialVersionUID = 1L;

    public InvalidGradientFolderNode() {
      super("(gradient-path is empty, check the Prefs)", null, false);
    }
  }

  private void initGradientsLibrary() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Gradient library", true);
    // Internal gradients
    {
      String[] ressources = { "flam3-palettes.xml" };
      List<String> resLst = Arrays.asList(ressources);
      Collections.sort(resLst);
      ressources = (String[]) resLst.toArray();

      // for the base path inside the jar file
      RGBPaletteReader reader = new Flam3PaletteReader();
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
                defaultFolderNode = new GradientFolderNode("Built-in gradients (read-only)", null, false);
                root.add(defaultFolderNode);
              }
              defaultFolderNode.add(node);
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
      DefaultMutableTreeNode parentNode = userGradientsRootNode = new GradientFolderNode("Your gradients", baseDrawer, true);
      root.add(parentNode);
      if (baseDrawer == null || baseDrawer.equals("") || baseDrawer.equals(".") || baseDrawer.equals("/") || baseDrawer.equals("\\")) {
        parentNode.add(new InvalidGradientFolderNode());
      }
      else {
        //        scanUserGradients(baseDrawer, parentNode);
      }
    }
    gradientLibTree.setRootVisible(false);
    gradientLibTree.setModel(new DefaultTreeModel(root));
  }

  public void enableControls() {
    // TODO Auto-generated method stub

  }

  public void updateGradientThumbnails(final AbstractGradientNode pNode, List<SimpleImage> pImages) {
    if (gradientLibraryScrollPane != null) {
      gradientLibraryPanel.remove(gradientLibraryScrollPane);
      gradientLibraryScrollPane = null;
    }
    gradientLibraryGradientCmb.removeAllItems();
    int panelWidth = gradientLibraryPanel.getBounds().width - 2 * GRADIENT_THUMB_BORDER;
    int panelHeight = (GRADIENT_THUMB_HEIGHT + GRADIENT_THUMB_BORDER) * pImages.size();
    JPanel gradientsPanel = new JPanel();
    gradientsPanel.setLayout(null);
    gradientsPanel.setSize(panelWidth, panelHeight);
    gradientsPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    for (int i = 0; i < pImages.size(); i++) {
      SimpleImage img = pImages.get(i);
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(GRADIENT_THUMB_BORDER, i * GRADIENT_THUMB_HEIGHT + (i + 1) * GRADIENT_THUMB_BORDER);
      final int idx = i;
      imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() > 1 || (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3)) {
            importFromGradientLibrary(pNode, idx);
          }
        }
      });
      gradientsPanel.add(imgPanel);
      gradientLibraryGradientCmb.addItem(pNode.getGradientLibraryList().get(i));
    }
    gradientLibraryScrollPane = new JScrollPane(gradientsPanel);
    gradientLibraryScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    gradientLibraryScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    gradientLibraryPanel.add(gradientLibraryScrollPane, BorderLayout.CENTER);
    gradientLibraryPanel.validate();
  }

  private void importFromGradientLibrary(AbstractGradientNode pNode, int idx) {
    if (idx >= 0 && idx < pNode.getGradientLibraryList().size()) {
      gradientLibraryGradientCmb.setSelectedItem(pNode.getGradientLibraryList().get(idx));
    }
  }

  public void gradientLibraryGradientChanged() {
    // TODO
    if (!cmbRefreshing) {
      //      if (getCurrFlame() != null && data.gradientLibraryGradientCmb.getSelectedIndex() >= 0 && data.gradientLibraryGradientCmb.getSelectedIndex() < gradientLibraryList.size()) {
      //        saveUndoPoint();
      //        RGBPalette palette = gradientLibraryList.get(data.gradientLibraryGradientCmb.getSelectedIndex()).makeCopy();
      //        getCurrFlame().setPalette(palette);
      //        refreshPaletteUI(palette);
      //        refreshFlameImage(false);
      //      }
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

  public void gradientTree_changed(TreeSelectionEvent e) {
    enableControls();
    cmbRefreshing = true;
    try {
      DefaultMutableTreeNode selNode = getSelNode();
      if (selNode != null && selNode instanceof AbstractGradientNode) {
        AbstractGradientNode gradientNode = (AbstractGradientNode) selNode;
        updateGradientThumbnails(gradientNode, gradientNode.getThumbnails());
      }
      else {
        gradientLibraryGradientCmb.removeAllItems();
        if (gradientLibraryScrollPane != null) {
          gradientLibraryPanel.remove(gradientLibraryScrollPane);
          gradientLibraryScrollPane = null;
          gradientLibraryPanel.repaint();
          gradientLibraryPanel.validate();
        }
      }
    }
    finally {
      cmbRefreshing = false;
    }
  }

}
