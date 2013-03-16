package org.jwildfire.create.tina.dance;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTree;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.model.AnimationModelService;
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

  public MutaGenController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pFlamePanels[], JTree pMutaGenTree,
      JButton pEditFlameBtn, JButton pLoadFlameFromEditorBtn, JButton pLoadFlameFromClipboardBtn, JButton pLoadFlameFromFileBtn,
      JProgressBar pProgressBar, JWFNumberField pAmountREd, JSlider pAmountSlider, JComboBox pHorizontalTrendCmb, JComboBox pVerticalTrendCmb) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    flamePanels = pFlamePanels;
    prefs = pPrefs;
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
    refreshFlameImages();
  }

  private void refreshFlameImages() {
    createImagePanels();
    if (selectedSet != null) {
      for (int i = 0; i < selectedSet.getRows(); i++) {
        for (int j = 0; j < selectedSet.getCols(); j++) {
          ImagePanel pnl = imagePanels[i][j];
          Dimension size = pnl.getSize();
          int imageWidth = Tools.FTOI(size.getWidth());
          int imageHeight = Tools.FTOI(size.getHeight());
          Flame flame = selectedSet.getFlame(i, j);
          SimpleImage img;
          if (flame != null && imageWidth > 16 && imageHeight > 16) {
            RenderInfo info = new RenderInfo(imageWidth, imageHeight);
            double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
            double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
            flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
            flame.setWidth(imageWidth);
            flame.setHeight(imageHeight);
            flame.setSampleDensity(30.0);
            FlameRenderer renderer = new FlameRenderer(flame, prefs, false);
            RenderedFlame res = renderer.renderFlame(info);
            img = res.getImage();
          }
          else {
            img = new SimpleImage(imageWidth, imageHeight);
          }
          pnl.setImage(img);
        }
      }
    }
    flamePanels[0].getParent().getParent().invalidate();
    flamePanels[0].getParent().getParent().validate();
    flamePanels[0].getParent().getParent().repaint();
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
                if (e.getClickCount() > 1 || e.getButton() != MouseEvent.BUTTON1) {
                  mutate(row, col);
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

  protected void mutate(int pRow, int pCol) {
    if (selectedSet != null && selectedSet.getFlame(pRow, pCol) != null) {
      final int rows = MUTA_ROWS;
      final int cols = MUTA_COLS;
      Flame baseFlame = selectedSet.getFlame(pRow, pCol);
      List<Flame> mutations = new ArrayList<Flame>();
      initProgress(rows, cols);

      int step = 0;
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          showProgress(step++);
          double x = (i - rows / 2) / (double) (rows / 2);
          double y = (j - cols / 2) / (double) (cols / 2);

          Flame mFlame = baseFlame.makeCopy();
          AnimationModelService.setRandomFlameProperty(mFlame, x * 3 * (0.75 + 0.25 * Math.random()));
          AnimationModelService.setRandomFlameProperty(mFlame, y * 3 * (0.75 + 0.25 * Math.random()));
          mutations.add(mFlame);
        }
      }
      MutationSet newSet = new MutationSet(rows, cols, baseFlame, mutations);
      mutationList.add(newSet);
      selectedSet = newSet;
      refreshFlameImages();
    }
  }

  private void showProgress(int pStep) {
    progressBar.setValue(pStep);
    progressBar.invalidate();
    progressBar.validate();
  }

  private void initProgress(int pRows, int pCols) {
    progressBar.setMinimum(0);
    progressBar.setValue(0);
    progressBar.setMaximum(pRows * pCols);
    progressBar.invalidate();
    progressBar.validate();
  }
}
