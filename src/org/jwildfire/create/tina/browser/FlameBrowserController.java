package org.jwildfire.create.tina.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
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
        showImages(selNode.getFlames());
      }
      imagesPnl.validate();
    }
  }

  private void showImages(List<FlameFlatNode> pFlames) {
    cancelRenderThreads();
    final int IMG_WIDTH = 160;
    final int IMG_HEIGHT = 100;
    final int OUTER_BORDER = 20;
    final int INNER_BORDER = 10;
    final int LABEL_HEIGHT = 12;
    int maxPnlWidth = imagesPnl.getSize().width;
    int cols = calcCols(IMG_WIDTH, OUTER_BORDER, INNER_BORDER, maxPnlWidth);
    int rows = pFlames.size() / cols;
    if (rows * cols < pFlames.size()) {
      rows++;
    }

    int pnlWidth = 2 * OUTER_BORDER + (cols - 1) * INNER_BORDER + cols * IMG_WIDTH;
    int pnlHeight = 2 * OUTER_BORDER + (rows - 1) * INNER_BORDER + rows * (IMG_HEIGHT + LABEL_HEIGHT);

    JPanel imgRootPanel = new JPanel();
    imgRootPanel.setLayout(null);
    imgRootPanel.setSize(pnlWidth, pnlHeight);
    imgRootPanel.setPreferredSize(new Dimension(pnlWidth, pnlHeight));

    int flameIdx = 0;
    int y = OUTER_BORDER;
    List<RenderJobInfo> jobInfoLst = new ArrayList<RenderJobInfo>();
    for (int r = 0; r < rows; r++) {
      int x = OUTER_BORDER;
      for (int c = 0; c < cols; c++) {
        if (flameIdx < pFlames.size()) {
          // image
          FlameFlatNode node = pFlames.get(flameIdx++);
          ImagePanel imgPanel;
          SimpleImage img = renderCache.getImage(node);
          if (img == null) {
            img = new SimpleImage(IMG_WIDTH, IMG_HEIGHT);
            img.fillBackground(0, 0, 0);
            imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
            jobInfoLst.add(new RenderJobInfo(imgPanel, node, IMG_WIDTH, IMG_HEIGHT, x, y));
          }
          else {
            imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
          }
          imgPanel.setImage(img);
          imgPanel.setLocation(x, y);

          final String flameFilename = node.getFilename();
          imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
              if (e.getClickCount() > 1 || e.getButton() != MouseEvent.BUTTON1) {
                List<Flame> flames = new Flam3Reader(prefs).readFlames(flameFilename);
                if (flames.size() > 0) {
                  for (Flame flame : flames) {
                    tinaController.importFlame(flame, true);
                  }
                  tinaController.getRootTabbedPane().setSelectedIndex(0);
                }
              }
            }
          });
          imgRootPanel.add(imgPanel);
          // label with description 
          JLabel label = new JLabel();
          label.setText(node.getCaption());
          label.setSize(new Dimension(IMG_WIDTH, LABEL_HEIGHT));
          label.setPreferredSize(new Dimension(IMG_WIDTH, LABEL_HEIGHT));
          label.setLocation(new Point(x, y + IMG_HEIGHT));
          label.setFont(new Font("Dialog", Font.BOLD, 10));
          label.setHorizontalAlignment(SwingConstants.CENTER);
          label.setVerticalAlignment(SwingConstants.CENTER);
          imgRootPanel.add(label);
          //
          x += IMG_WIDTH + INNER_BORDER;
        }
        else {
          break;
        }
      }
      y += IMG_HEIGHT + INNER_BORDER + LABEL_HEIGHT;
    }

    imagesScrollPane = new JScrollPane(imgRootPanel);
    imagesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    imagesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    imagesPnl.add(imagesScrollPane, BorderLayout.CENTER);

    if (jobInfoLst.size() > 0) {
      startRenderThread(jobInfoLst);
    }

  }

  private void startRenderThread(List<RenderJobInfo> jobInfoLst) {
    CacheRendererThread renderThread = new CacheRendererThread(prefs, renderCache, jobInfoLst);
    new Thread(renderThread).start();
    renderThreads.add(renderThread);
  }

  private void cancelRenderThreads() {
    final int MAX_RUNNING_THREADS = 4;
    for (CacheRendererThread thread : renderThreads) {
      thread.signalCancel();
    }
    while (renderThreads.size() > MAX_RUNNING_THREADS) {
      CacheRendererThread thread = renderThreads.get(0);
      while (!thread.isDone()) {
        try {
          Thread.sleep(3);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      renderThreads.remove(thread);
    }
  }

  private List<CacheRendererThread> renderThreads = new ArrayList<CacheRendererThread>();

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

  private final RenderCache renderCache = new RenderCache();

}
