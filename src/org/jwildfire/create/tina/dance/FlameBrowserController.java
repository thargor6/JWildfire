package org.jwildfire.create.tina.dance;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;

public class FlameBrowserController {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final JTree flamesTree;
  private final JPanel imagesPnl;

  public FlameBrowserController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel, JTree pFlamesTree, JPanel pImagesPnl) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    flamesTree = pFlamesTree;
    imagesPnl = pImagesPnl;
    enableControls();
  }

  public void enableControls() {
    String baseDrawer = prefs.getTinaJWFScriptPath();
    boolean enableUserScripts = baseDrawer != null && baseDrawer.length() > 0;

  }

  private boolean refreshing = false;

  private void refreshFlamesTree() {
    refreshing = true;
    try {
      String baseDrawer = prefs.getTinaFlamePath();
      flatFlameNodes.clear();
      if (baseDrawer != null && baseDrawer.length() > 0) {
        flatFlameNodes.scanFlames(baseDrawer);
      }
      flatFlameNodes.sortNodes();
      addFlatNodesToTree();
    }
    finally {
      refreshing = false;
    }
  }

  private static class FlamesTreeNode extends DefaultMutableTreeNode {
    private final List<FlameFlatNode> flames;

    public FlamesTreeNode(String pCaption, boolean pHasChilds, List<FlameFlatNode> pFlames) {
      super(pCaption, pHasChilds);
      flames = pFlames;
    }

    public List<FlameFlatNode> getFlames() {
      return flames;
    }

  }

  private void addFlatNodesToTree() {
    FlamesTreeNode root = new FlamesTreeNode("Flame library", true, null);
    List<Date> fileDates = flatFlameNodes.getDistinctFiledates();
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    for (Date fileDate : fileDates) {
      List<FlameFlatNode> flameNodes = flatFlameNodes.getNodes(fileDate);
      FlamesTreeNode folder = new FlamesTreeNode(sdf.format(fileDate) + " (" + flameNodes.size() + ")", true, flameNodes);
      root.add(folder);
      for (FlameFlatNode flameNode : flameNodes) {
        FlamesTreeNode node = new FlamesTreeNode(flameNode.getCaption(), false, flameNodes);
        folder.add(node);
      }

    }

    flamesTree.setModel(new DefaultTreeModel(root));
  }

  public void init() {
    flamesTree.setRootVisible(false);
    refreshFlamesTree();
  }

  private static class FlameFlatNodes implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<FlameFlatNode> nodes = new ArrayList<FlameFlatNode>();

    public void sortNodes() {
      final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Collections.sort(nodes, new Comparator<FlameFlatNode>() {

        @Override
        public int compare(FlameFlatNode o1, FlameFlatNode o2) {
          String ageString1 = sdf.format(o1.getFileage());
          String ageString2 = sdf.format(o2.getFileage());
          int ageCompare = ageString1.compareTo(ageString2);
          if (ageCompare > 0) {
            return -1;
          }
          else if (ageCompare < 0) {
            return +1;
          }
          else {
            return o1.getCaption().compareTo(o2.getCaption());
          }
        }
      });
    }

    public void clear() {
      nodes.clear();
    }

    public void scanFlames(String pPath) {
      clear();
      scanDrawer(pPath);
    }

    private void scanDrawer(String pPath) {
      File root = new File(pPath);
      File[] list = root.listFiles();
      if (list != null) {
        for (File f : list) {
          if (f.isDirectory()) {
            scanDrawer(f.getAbsolutePath());
          }
          else {
            String filename = f.getAbsolutePath();
            String lcFilename = filename.toLowerCase();
            if (lcFilename.length() != filename.length()) {
              lcFilename = filename;
            }
            int pos = lcFilename.lastIndexOf("." + Tools.FILEEXT_FLAME.toLowerCase());
            if (pos > 0 && pos == filename.length() - Tools.FILEEXT_FLAME.length() - 1) {
              String caption = f.getName().substring(0, f.getName().length() - Tools.FILEEXT_FLAME.length() - 1);
              FlameFlatNode node = new FlameFlatNode(f.getAbsolutePath(), caption, new Date(f.lastModified()));
              nodes.add(node);
            }
          }
        }
      }
    }

    public List<Date> getDistinctFiledates() {
      final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      List<Date> res = new ArrayList<Date>();
      Map<String, String> dateStrMap = new HashMap<String, String>();
      for (FlameFlatNode node : nodes) {
        String dateStr = sdf.format(node.getFileage());
        if (dateStrMap.get(dateStr) == null) {
          dateStrMap.put(dateStr, dateStr);
          res.add(node.getFileage());
        }
      }
      return res;
    }

    public List<FlameFlatNode> getNodes(Date pFileAge) {
      List<FlameFlatNode> res = new ArrayList<FlameFlatNode>();
      Calendar cal = GregorianCalendar.getInstance();
      cal.setTime(pFileAge);
      int refYear = cal.get(Calendar.YEAR);
      int refMonth = cal.get(Calendar.MONTH);
      int refDay = cal.get(Calendar.DAY_OF_MONTH);
      for (FlameFlatNode node : nodes) {
        cal.setTime(node.getFileage());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (year == refYear && month == refMonth && day == refDay) {
          res.add(node);
        }
      }
      return res;
    }

  }

  private static class FlameFlatNode implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String filename;
    private final String caption;
    private final Date fileage;

    public FlameFlatNode(String pFilename, String pCaption, Date pFileage) {
      filename = pFilename;
      caption = pCaption;
      fileage = pFileage;
    }

    public String getFilename() {
      return filename;
    }

    public String getCaption() {
      return caption;
    }

    public Date getFileage() {
      return fileage;
    }

  }

  private final FlameFlatNodes flatFlameNodes = new FlameFlatNodes();

  private FlamesTreeNode getSelNode() {
    FlamesTreeNode selNode = null;
    {
      TreePath selPath = flamesTree.getSelectionPath();
      if (selPath != null) {
        selNode = (FlamesTreeNode) selPath.getLastPathComponent();
      }
    }
    return selNode;
  }

  private JScrollPane imagesScrollPane;

  private void clearImages() {
    if (imagesScrollPane != null) {
      imagesPnl.remove(imagesScrollPane);
      imagesScrollPane = null;
    }
  }

  public void flamesTree_changed(TreeSelectionEvent e) {
    if (!refreshing) {
      enableControls();
      FlamesTreeNode selNode = getSelNode();
      clearImages();
      if (selNode != null) {
        System.out.println(selNode.getUserObject() + " " + selNode.getFlames().size());
        showImages(selNode.getFlames());
      }
      imagesPnl.validate();
    }
  }

  private void showImages(List<FlameFlatNode> pFlames) {
    final int IMG_WIDTH = 160;
    final int IMG_HEIGHT = 100;
    final int OUTER_BORDER = 20;
    final int INNER_BORDER = 10;
    int maxPnlWidth = imagesPnl.getSize().width;
    int cols = calcCols(IMG_WIDTH, OUTER_BORDER, INNER_BORDER, maxPnlWidth);
    int rows = pFlames.size() / cols;
    if (rows * cols < pFlames.size()) {
      rows++;
    }

    int pnlWidth = 2 * OUTER_BORDER + (cols - 1) * INNER_BORDER + cols * IMG_WIDTH;
    int pnlHeight = 2 * OUTER_BORDER + (rows - 1) * INNER_BORDER + rows * IMG_HEIGHT;

    JPanel imgRootPanel = new JPanel();
    imgRootPanel.setLayout(null);
    imgRootPanel.setSize(pnlWidth, pnlHeight);
    imgRootPanel.setPreferredSize(new Dimension(pnlWidth, pnlHeight));

    int flameIdx = 0;
    int y = OUTER_BORDER;
    for (int r = 0; r < rows; r++) {
      int x = OUTER_BORDER;
      for (int c = 0; c < cols; c++) {
        if (flameIdx < pFlames.size()) {
          FlameFlatNode node = pFlames.get(flameIdx++);
          SimpleImage img;
          try {
            img = renderFlame(node, IMG_WIDTH, IMG_HEIGHT);
          }
          catch (Exception ex) {
            ex.printStackTrace();
            img = new SimpleImage(IMG_WIDTH, IMG_HEIGHT);
          }
          ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
          imgPanel.setImage(img);
          imgPanel.setLocation(x, y);

          //          imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
          //            public void mouseClicked(java.awt.event.MouseEvent e) {
          //              if (e.getClickCount() > 1 || e.getButton() != MouseEvent.BUTTON1) {
          //                importFromRandomBatch(idx);
          //              }
          //            }
          //          });
          imgRootPanel.add(imgPanel);
          x += IMG_WIDTH + INNER_BORDER;
        }
        else {
          break;
        }
      }
      y += IMG_HEIGHT + INNER_BORDER;
    }

    imagesScrollPane = new JScrollPane(imgRootPanel);
    imagesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    imagesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    imagesPnl.add(imagesScrollPane, BorderLayout.CENTER);
  }

  private int calcCols(int pImgWidth, int pOuterBorder, int pInnerBorder, int pMaxPnlWidth) {
    int cols = 0;
    while (true) {
      cols++;
      int width = 2 * pOuterBorder + (cols - 1) * pInnerBorder + cols * pImgWidth;
      if (width > pMaxPnlWidth) {
        cols--;
        if (cols < 1) {
          cols = 1;
        }
        break;
      }
    }
    return cols;
  }

  private Map<String, SimpleImage> renderCache = new HashMap<String, SimpleImage>();

  private SimpleImage renderFlame(FlameFlatNode pNode, int pImgWidth, int pImgHeight) {
    SimpleImage img = renderCache.get(pNode.getFilename());
    if (img == null) {
      List<Flame> flames = new Flam3Reader(prefs).readFlames(pNode.getFilename());
      Flame renderFlame = flames.get(0);
      RenderInfo info = new RenderInfo(pImgWidth, pImgHeight);
      double wScl = (double) info.getImageWidth() / (double) renderFlame.getWidth();
      double hScl = (double) info.getImageHeight() / (double) renderFlame.getHeight();
      renderFlame.setPixelsPerUnit((wScl + hScl) * 0.5 * renderFlame.getPixelsPerUnit());
      renderFlame.setWidth(pImgWidth);
      renderFlame.setHeight(pImgHeight);
      renderFlame.setSampleDensity(prefs.getTinaRenderPreviewQuality() / 3.0);
      renderFlame.setDeFilterEnabled(false);
      FlameRenderer renderer = new FlameRenderer(renderFlame, prefs, false);
      RenderedFlame renderRes = renderer.renderFlame(info);
      img = renderRes.getImage();
      renderCache.put(pNode.getFilename(), img);
    }
    return img;
  }

}
