package org.jwildfire.create.tina.mutagen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;

public class MutaGenController {
  public static final int PAGE_INDEX = 2;
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JTabbedPane rootTabbedPane;
  private final JPanel flamePanels[];
  private final JTree mutaGenTree;
  private final JButton editFlameBtn;
  private final JButton loadFlameFromEditorBtn;
  private final JButton loadFlameFromClipboardBtn;
  private final JButton loadFlameFromFileBtn;
  private final JProgressBar progressBar;
  private final JWFNumberField amountREd;
  private final JSlider amountSlider;
  private final JComboBox horizontalTrendCmb;
  private final JComboBox verticalTrendCmb;

  private ImagePanel imagePanels[][];
  private final int MUTA_ROWS = 5;
  private final int MUTA_COLS = 5;
  private final List<MutationSet> mutationList = new ArrayList<MutationSet>();
  private MutationSet selectedSet = new MutationSet(MUTA_ROWS, MUTA_COLS, (Flame) null);

  private class MutationSet {
    final int rows, cols;
    final Flame baseFlame;
    final Flame[][] flames;

    public MutationSet(int pRows, int pCols, Flame pBaseFlame) {
      rows = pRows;
      cols = pCols;
      flames = new Flame[rows][cols];
      baseFlame = pBaseFlame;
      flames[rows / 2][cols / 2] = pBaseFlame;
    }

    public MutationSet(int pRows, int pCols, Flame pBaseFlame, List<Flame> pFlames) {
      rows = pRows;
      cols = pCols;
      flames = new Flame[rows][cols];
      int idx = 0;
      for (int i = 0; i < pRows; i++) {
        for (int j = 0; j < pCols; j++) {
          flames[i][j] = pFlames.get(idx++);
        }
      }
      baseFlame = pBaseFlame;
      flames[rows / 2][cols / 2] = pBaseFlame;
    }

    public int getRows() {
      return rows;
    }

    public int getCols() {
      return cols;
    }

    public Flame getFlame(int pRow, int pCol) {
      return flames[pRow][pCol];
    }

  }

  public MutaGenController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JTabbedPane pRootTabbedPane, JPanel pFlamePanels[], JTree pMutaGenTree,
      JButton pEditFlameBtn, JButton pLoadFlameFromEditorBtn, JButton pLoadFlameFromClipboardBtn, JButton pLoadFlameFromFileBtn,
      JProgressBar pProgressBar, JWFNumberField pAmountREd, JSlider pAmountSlider, JComboBox pHorizontalTrendCmb, JComboBox pVerticalTrendCmb) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    flamePanels = pFlamePanels;
    prefs = pPrefs;
    rootTabbedPane = pRootTabbedPane;
    mutaGenTree = pMutaGenTree;
    editFlameBtn = pEditFlameBtn;
    loadFlameFromEditorBtn = pLoadFlameFromEditorBtn;
    loadFlameFromClipboardBtn = pLoadFlameFromClipboardBtn;
    loadFlameFromFileBtn = pLoadFlameFromFileBtn;
    progressBar = pProgressBar;
    amountREd = pAmountREd;
    amountSlider = pAmountSlider;
    horizontalTrendCmb = pHorizontalTrendCmb;
    refreshTrendCmb(horizontalTrendCmb);
    verticalTrendCmb = pVerticalTrendCmb;
    refreshTrendCmb(verticalTrendCmb);
  }

  private void refreshTrendCmb(JComboBox pCmb) {
    // TODO
  }

  public void importFlame(Flame pFlame) {
    Flame baseFlame = pFlame.makeCopy();
    MutationSet set = new MutationSet(MUTA_ROWS, MUTA_COLS, baseFlame);
    mutationList.add(set);
    selectedSet = set;
    mutate(MUTA_ROWS / 2, MUTA_COLS / 2);
  }

  private SimpleImage renderFlame(Flame pFlame) {
    createImagePanels();
    ImagePanel pnl = imagePanels[0][0];
    Dimension size = pnl.getSize();
    int imageWidth = Tools.FTOI(size.getWidth());
    int imageHeight = Tools.FTOI(size.getHeight());
    SimpleImage img;
    if (pFlame != null && imageWidth > 16 && imageHeight > 16) {
      RenderInfo info = new RenderInfo(imageWidth, imageHeight);
      double wScl = (double) info.getImageWidth() / (double) pFlame.getWidth();
      double hScl = (double) info.getImageHeight() / (double) pFlame.getHeight();
      pFlame.setPixelsPerUnit((wScl + hScl) * 0.5 * pFlame.getPixelsPerUnit());
      pFlame.setWidth(imageWidth);
      pFlame.setHeight(imageHeight);
      pFlame.setSampleDensity(30.0);
      FlameRenderer renderer = new FlameRenderer(pFlame, prefs, false);
      RenderedFlame res = renderer.renderFlame(info);
      img = res.getImage();
    }
    else {
      img = new SimpleImage(imageWidth, imageHeight);
    }
    return img;
  }

  private void createImagePanels() {
    if (imagePanels == null) {
      try {
        final int rows = MUTA_ROWS;
        final int cols = MUTA_COLS;
        final int BORDER_SIZE = 0;

        imagePanels = new ImagePanel[rows][cols];
        int idx = 0;
        for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
            JPanel panel = flamePanels[idx++];
            Dimension size = panel.getSize();
            SimpleImage img = new SimpleImage(Tools.FTOI(size.getWidth()), Tools.FTOI(size.getHeight()));
            ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
            imagePanels[i][j] = imgPanel;
            imgPanel.setImage(img);
            imgPanel.setLocation(BORDER_SIZE, BORDER_SIZE);

            final int row = i;
            final int col = j;
            imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {

              @Override
              public void mouseClicked(java.awt.event.MouseEvent e) {

                System.out.println(e.getClickCount() + " " + e.getButton());

                if (e.getClickCount() == 2 || e.getButton() == MouseEvent.BUTTON3) {
                  mutate(row, col);
                }
                else if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON2) {
                  export(row, col);
                }
              }

            });

            panel.add(imgPanel);
          }
        }
      }
      catch (Throwable ex) {
        imagePanels = null;
        errorHandler.handleError(ex);
      }
    }
  }

  protected void export(int pRow, int pCol) {
    if (selectedSet != null && selectedSet.getFlame(pRow, pCol) != null) {
      Flame baseFlame = selectedSet.getFlame(pRow, pCol);
      tinaController.importFlame(baseFlame);

      rootTabbedPane.setSelectedIndex(TinaController.PAGE_INDEX);
      rootTabbedPane.getParent().invalidate();
      try {
        Graphics g = rootTabbedPane.getParent().getGraphics();
        if (g != null) {
          rootTabbedPane.getParent().paint(g);
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }

    }
  }

  protected void mutate(int pRow, int pCol) {
    if (selectedSet != null && selectedSet.getFlame(pRow, pCol) != null) {
      final int rows = MUTA_ROWS;
      final int cols = MUTA_COLS;
      Flame baseFlame = selectedSet.getFlame(pRow, pCol);
      List<Flame> mutations = new ArrayList<Flame>();
      initProgress(rows, cols);
      int centreX = rows / 2;
      int centreY = cols / 2;

      MutationType horizType1 = MutationType.ALL;
      MutationType horizType2 = MutationType.ALL;
      MutationType vertType1 = MutationType.ALL;
      MutationType vertType2 = MutationType.ALL;

      int step = 0;
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          Flame mFlame = baseFlame.makeCopy();
          // Let centre flame untouched
          if ((i != centreX || j != centreY)) {
            int x = (i - centreX);
            int y = (j - centreY);
            List<MutationType> mutationTypes = createMutationTypes(x, y, horizType1, horizType2, vertType1, vertType2);
            modifyFlame(mFlame, x, y, mutationTypes);
          }
          mutations.add(mFlame);
          SimpleImage renderedImg = renderFlame(mFlame);
          ImagePanel pnl = imagePanels[i][j];
          pnl.setImage(renderedImg);
          showProgress(++step);
          pnl.invalidate();
          try {
            Graphics g = pnl.getGraphics();
            if (g != null) {
              pnl.paint(g);
            }
          }
          catch (Throwable ex) {
            ex.printStackTrace();
          }

        }
      }
      MutationSet newSet = new MutationSet(rows, cols, baseFlame, mutations);
      mutationList.add(newSet);
      selectedSet = newSet;
    }
  }

  private List<MutationType> createMutationTypes(int pX, int pY, MutationType pHorizType1, MutationType pHorizType2, MutationType pVertType1, MutationType pVertType2) {
    List<MutationType> mutations = new ArrayList<MutationType>();
    if (Math.random() < 0.5) {
      if (pX == 1 || pX == -1) {
        mutations.add(pHorizType1);
      }
      if (pY == 1 || pY == -1) {
        mutations.add(pVertType1);
      }
    }
    else {
      if (pY == 1 || pY == -1) {
        mutations.add(pVertType1);
      }
      if (pX == 1 || pX == -1) {
        mutations.add(pHorizType1);
      }
    }
    if (Math.random() > 0.5) {
      if (pX == 2 || pX == -2) {
        mutations.add(pHorizType2);
      }
      if (pY == 2 || pY == -2) {
        mutations.add(pVertType2);
      }
    }
    else {
      if (pY == 2 || pY == -2) {
        mutations.add(pVertType2);
      }
      if (pX == 2 || pX == -2) {
        mutations.add(pHorizType2);
      }
    }
    return mutations;
  }

  private void modifyFlame(Flame pFlame, double pX, int pY, List<MutationType> pMutationTypes) {
    for (MutationType mutationType : pMutationTypes) {
      Mutation mutation = mutationType.createMutationInstance();
      mutation.execute(pFlame);
    }
  }

  private void showProgress(int pStep) {
    progressBar.setValue(pStep);
    progressBar.invalidate();
    try {
      Graphics g = progressBar.getGraphics();
      if (g != null) {
        progressBar.paint(g);
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  private void initProgress(int pRows, int pCols) {
    progressBar.setMinimum(0);
    progressBar.setValue(0);
    progressBar.setMaximum(pRows * pCols);
    progressBar.invalidate();
    try {
      Graphics g = progressBar.getGraphics();
      if (g != null) {
        progressBar.paint(g);
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }
}
