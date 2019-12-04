/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.mutagen;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.FlameMorphService;
import org.jwildfire.create.tina.animate.FlameMorphType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.FlameFileChooser;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.MainEditorFrame;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;

public class MutaGenController {
  public static final int PAGE_INDEX = 2;
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final JPanel flamePanels[];
  private final JButton loadFlameFromEditorBtn;
  private final JButton loadFlameFromFileBtn;
  private final JProgressBar progressBar;
  private final JWFNumberField amountREd;
  private final JComboBox<MutationType> horizontalTrend1Cmb;
  private final JComboBox<MutationType> horizontalTrend2Cmb;
  private final JComboBox<MutationType> verticalTrend1Cmb;
  private final JComboBox<MutationType> verticalTrend2Cmb;
  private final JButton backButton;
  private final JButton forwardButton;
  private final JButton saveFlameToEditorBtn;
  private final JButton saveFlameToFileBtn;
  private final JTextPane hintPane;

  private ImagePanel imagePanels[][];
  private final static int MUTA_ROWS = 5;
  private final static int MUTA_COLS = 5;
  private final List<MutationSet> mutationList = new ArrayList<MutationSet>();
  private int selectedGenerationIdx = -1;
  private int selRow = -1, selCol = -1;

  private static class MutationSet {
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

    public Flame getBaseFlame() {
      return baseFlame;
    }

  }

  public MutaGenController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel, JPanel pFlamePanels[],
      JButton pLoadFlameFromEditorBtn, JButton pLoadFlameFromFileBtn,
      JProgressBar pProgressBar, JWFNumberField pAmountREd, JComboBox<MutationType> pHorizontalTrend1Cmb, JComboBox<MutationType> pHorizontalTrend2Cmb,
      JComboBox<MutationType> pVerticalTrend1Cmb, JComboBox<MutationType> pVerticalTrend2Cmb, JButton pBackButton, JButton pForwardButton, JTextPane pHintPane,
      JButton pSaveFlameToEditorBtn, JButton pSaveFlameToFileBtn) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    flamePanels = pFlamePanels;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    loadFlameFromEditorBtn = pLoadFlameFromEditorBtn;
    loadFlameFromFileBtn = pLoadFlameFromFileBtn;
    progressBar = pProgressBar;
    amountREd = pAmountREd;
    horizontalTrend1Cmb = pHorizontalTrend1Cmb;
    horizontalTrend2Cmb = pHorizontalTrend2Cmb;
    verticalTrend1Cmb = pVerticalTrend1Cmb;
    verticalTrend2Cmb = pVerticalTrend2Cmb;
    backButton = pBackButton;
    forwardButton = pForwardButton;
    hintPane = pHintPane;
    saveFlameToEditorBtn = pSaveFlameToEditorBtn;
    saveFlameToFileBtn = pSaveFlameToFileBtn;

    refreshTrendCmb(horizontalTrend1Cmb, Prefs.getPrefs().getTinaMutaGenMutationTypeHoriz1());
    refreshTrendCmb(horizontalTrend2Cmb, Prefs.getPrefs().getTinaMutaGenMutationTypeHoriz2());
    refreshTrendCmb(verticalTrend1Cmb, Prefs.getPrefs().getTinaMutaGenMutationTypeVert1());
    refreshTrendCmb(verticalTrend2Cmb, Prefs.getPrefs().getTinaMutaGenMutationTypeVert2());
    amountREd.setValue(1.0);
    initHintsPane();
  }

  private void refreshTrendCmb(JComboBox<MutationType> pCmb, String pInitialValue) {
    pCmb.removeAllItems();
    pCmb.addItem(MutationType.ALL);
    pCmb.addItem(MutationType.USER1);
    pCmb.addItem(MutationType.USER2);
    pCmb.addItem(MutationType.USER3);
    pCmb.addItem(MutationType.ADD_TRANSFORM);
    pCmb.addItem(MutationType.ADD_VARIATION);
    pCmb.addItem(MutationType.AFFINE);
    pCmb.addItem(MutationType.AFFINE_3D);
    pCmb.addItem(MutationType.BOKEH);
    pCmb.addItem(MutationType.CHANGE_WEIGHT);
    pCmb.addItem(MutationType.COLOR_TYPE);
    pCmb.addItem(MutationType.GRADIENT_POSITION);
    pCmb.addItem(MutationType.LOCAL_GAMMA);
    pCmb.addItem(MutationType.RANDOM_FLAME);
    pCmb.addItem(MutationType.RANDOM_GRADIENT);
    pCmb.addItem(MutationType.RANDOM_PARAMETER);
    pCmb.addItem(MutationType.SIMILAR_GRADIENT);
    pCmb.addItem(MutationType.WEIGHTING_FIELD);
    MutationType initialValue;
    try {
      initialValue = MutationType.valueOf(pInitialValue.trim().toUpperCase());
    }
    catch (Exception ex) {
      initialValue = MutationType.ALL;
    }
    pCmb.setSelectedItem(initialValue);
  }

  public void importFlame(Flame pFlame) {
    Flame baseFlame = pFlame.makeCopy();
    MutationSet set = new MutationSet(MUTA_ROWS, MUTA_COLS, baseFlame);
    mutationList.add(set);
    selectedGenerationIdx = mutationList.size() - 1;
    mutate(MUTA_ROWS / 2, MUTA_COLS / 2);
    enableControls();
  }

  private Flame createWeightedFlame(Flame pBaseFlame, Flame pFlame) {
    double amount = (Double) amountREd.getValue();
    if (amount < EPSILON) {
      return pBaseFlame.makeCopy();
    }
    else if (amount > 1.0 - EPSILON) {
      return pFlame.makeCopy();
    }
    else {
      int morphFrames = 1000;
      int morphFrame = Tools.FTOI(morphFrames * amount);
      return FlameMorphService.morphFlames(prefs, FlameMorphType.MORPH, pBaseFlame.makeCopy(), pFlame.makeCopy(), morphFrame, morphFrames, false);
    }
  }

  private SimpleImage renderFlame(Flame pFlame, Dimension pImgSize, boolean pWithTimeout) {
    int pImageWidth = pImgSize.width, pImageHeight = pImgSize.height;
    final SimpleImage img;
    if (pFlame != null && pImageWidth > 16 && pImageHeight > 16) {
      img = executeRenderThread(pFlame, pImageWidth, pImageHeight, pWithTimeout);
    }
    else {
      img = new SimpleImage(pImageWidth, pImageHeight);
    }
    return img;
  }

  private class RenderThread implements Runnable {
    private boolean done;
    private final Flame flame;
    private final int imageWidth;
    private final int imageHeight;
    private SimpleImage renderResult;
    private FlameRenderer renderer;

    public RenderThread(Flame pFlame, int pImageWidth, int pImageHeight) {
      this.flame = pFlame;
      this.imageWidth = pImageWidth;
      this.imageHeight = pImageHeight;
    }

    @Override
    public void run() {
      done = false;
      try {
        RenderInfo info = new RenderInfo(imageWidth, imageHeight, RenderMode.PREVIEW);
        double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
        double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
        flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
        flame.setWidth(imageWidth);
        flame.setHeight(imageHeight);
        flame.setSampleDensity(18.0);
        renderer = new FlameRenderer(flame, prefs, false, false);
        RenderedFlame res = renderer.renderFlame(info);
        renderResult = res.getImage();
      }
      finally {
        done = true;
      }
    }

    public boolean isDone() {
      return done;
    }

    public SimpleImage getRenderResult() {
      return renderResult;
    }

    public void forceAbort() {
      if (renderer != null) {
        renderer.signalCancel();
      }
    }

  }

  private SimpleImage executeRenderThread(Flame pFlame, int pImageWidth, int pImageHeight, boolean pWithTimeout) {
    Flame flame = pFlame.makeCopy();
    flame.applyFastOversamplingSettings();

    RenderThread thread = new RenderThread(flame, pImageWidth, pImageHeight);
    long tMax = 300;
    long t0 = System.currentTimeMillis();
    new Thread(thread).start();
    while (!thread.isDone()) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
      long t1 = System.currentTimeMillis();
      if (pWithTimeout && (t1 - t0 > tMax)) {
        thread.forceAbort();
        return null;
      }
    }
    return thread.getRenderResult();
  }

  private void createImagePanels() {
    if (imagePanels == null) {
      try {
        final int rows = MUTA_ROWS;
        final int cols = MUTA_COLS;
        final int BORDER_SIZE = 0;

        ((JPanel) flamePanels[0].getParent()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(37, 0), "selectLeftCellAction");
        ((JPanel) flamePanels[0].getParent()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(39, 0), "selectRightCellAction");
        ((JPanel) flamePanels[0].getParent()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(38, 0), "selectTopCellAction");
        ((JPanel) flamePanels[0].getParent()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(40, 0), "selectBottomCellAction");
        ((JPanel) flamePanels[0].getParent()).getActionMap().put("selectLeftCellAction", new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            selectLeftCell();
          }
        });
        ((JPanel) flamePanels[0].getParent()).getActionMap().put("selectRightCellAction", new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            selectRightCell();
          }
        });
        ((JPanel) flamePanels[0].getParent()).getActionMap().put("selectTopCellAction", new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            selectUpperCell();
          }
        });
        ((JPanel) flamePanels[0].getParent()).getActionMap().put("selectBottomCellAction", new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            selectLowerCell();
          }
        });

        imagePanels = new ImagePanel[rows][cols];
        int idx = 0;
        for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
            JPanel panel = flamePanels[idx++];
            Dimension size = panel.getSize();
            SimpleImage img = new SimpleImage(Tools.FTOI(size.getWidth()), Tools.FTOI(size.getHeight()));
            final ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
            imagePanels[i][j] = imgPanel;
            imgPanel.setImage(img);
            imgPanel.setLocation(BORDER_SIZE, BORDER_SIZE);

            final int row = i;
            final int col = j;

            imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {

              @Override
              public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 || e.getButton() == MouseEvent.BUTTON3) {
                  mutate(row, col);
                }
                else if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON2) {
                  export(row, col);
                }
              }

              @Override
              public void mousePressed(MouseEvent e) {
                selectCell(row, col);
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

  protected void selectLeftCell() {
    if (selCol < 0 || selRow < 0) {
      selectCell(0, 0);
    }
    else {
      selCol--;
      if (selCol < 0) {
        selCol = 0;
      }
      selectCell(selRow, selCol);
    }
  }

  protected void selectRightCell() {
    if (selCol < 0 || selRow < 0) {
      selectCell(0, 0);
    }
    else {
      selCol++;
      if (selCol >= MUTA_COLS) {
        selCol = MUTA_COLS - 1;
      }
      selectCell(selRow, selCol);
    }
  }

  protected void selectUpperCell() {
    if (selCol < 0 || selRow < 0) {
      selectCell(0, 0);
    }
    else {
      selRow--;
      if (selRow < 0) {
        selRow = 0;
      }
      selectCell(selRow, selCol);
    }
  }

  protected void selectLowerCell() {
    if (selCol < 0 || selRow < 0) {
      selectCell(0, 0);
    }
    else {
      selRow++;
      if (selRow >= MUTA_ROWS) {
        selRow = MUTA_ROWS - 1;
      }
      selectCell(selRow, selCol);
    }
  }

  protected void selectCell(int pRow, int pCol) {
    createImagePanels();
    final Color selColor = new Color(65, 63, 147);
    final Color deSelColor = new Color(0, 0, 0);

    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        ImagePanel pnl = imagePanels[i][j];
        boolean sel = i == pRow && j == pCol;
        ((JPanel) pnl.getParent()).setBorder(new LineBorder(sel ? selColor : deSelColor, sel ? 5 : 1));
      }
    }
    selRow = pRow;
    selCol = pCol;
    enableControls();
  }

  protected void export(int pRow, int pCol) {
    if (selectedGenerationIdx >= 0 && selectedGenerationIdx < mutationList.size()) {
      MutationSet selectedSet = mutationList.get(selectedGenerationIdx);

      Flame flame = selectedSet.getFlame(pRow, pCol);
      Flame morphedFlame = createWeightedFlame(selectedSet.getBaseFlame(), flame);

      tinaController.importFlame(morphedFlame, true);

      tinaController.getDesktop().showJFrame(MainEditorFrame.class);
      rootPanel.getParent().invalidate();
      try {
        Graphics g = rootPanel.getParent().getGraphics();
        if (g != null) {
          rootPanel.getParent().paint(g);
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }

    }
  }

  private Dimension calcImageSize() {
    createImagePanels();
    ImagePanel pnl = imagePanels[0][0];
    Dimension size = pnl.getSize();
    int imageWidth = Tools.FTOI(size.getWidth());
    int imageHeight = Tools.FTOI(size.getHeight());
    return new Dimension(imageWidth, imageHeight);
  }

  protected void mutate(int pRow, int pCol) {
    try {

      if (selectedGenerationIdx >= 0 && selectedGenerationIdx < mutationList.size()) {
        Dimension renderSize = calcImageSize();
        Dimension probeSize = new Dimension(50, 40);

        MutationSet selectedSet = mutationList.get(selectedGenerationIdx);
        final int rows = MUTA_ROWS;
        final int cols = MUTA_COLS;
        Flame baseFlame = selectedSet.getFlame(pRow, pCol);
        baseFlame = createWeightedFlame(selectedSet.getBaseFlame(), baseFlame);

        boolean doMorph = fabs((Double) amountREd.getValue() - 1.0) > EPSILON;

        List<Flame> mutations = new ArrayList<Flame>();
        initProgress(rows, cols);
        int centreX = rows / 2;
        int centreY = cols / 2;

        SimpleImage baseFlameImg = renderFlame(baseFlame.makeCopy(), probeSize, false);
        SimpleImage simplifiedBaseFlameImg = RandomFlameGeneratorSampler.createSimplifiedRefImage(baseFlameImg);

        int step = 0;
        for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
            // Let centre flame untouched
            SimpleImage renderedImg = null;
            if ((i != centreX || j != centreY)) {
              int x = (i - centreX);
              int y = (j - centreY);
              final int MAX_ITER = 10;
              final double MIN_RENDER_COVERAGE = 0.32;
              final double MIN_DIFF_COVERAGE = 0.18;
              final double INVALID_COVERAGE = -1.0;
              int iter = 0;
              double bestCoverage = INVALID_COVERAGE;
              Flame bestMutation = null;
              while (true) {
                Flame currMutation = baseFlame.makeCopy();
                List<MutationType> mutationTypes = createMutationTypes(x, y);
                modifyFlame(currMutation, x, y, mutationTypes);
                renderedImg = renderFlame(currMutation.makeCopy(), probeSize, true);
                double coverage = renderedImg != null ? RandomFlameGeneratorSampler.calculateCoverage(renderedImg, 0, 0, 0, true) : INVALID_COVERAGE;
                if (coverage > MIN_RENDER_COVERAGE) {
                  coverage = RandomFlameGeneratorSampler.calculateDiffCoverage(renderedImg, simplifiedBaseFlameImg);
                }
                if (coverage > MIN_DIFF_COVERAGE) {
                  mutations.add(currMutation);
                  if (doMorph) {
                    Flame morphed = createWeightedFlame(baseFlame, currMutation);
                    renderedImg = renderFlame(morphed, renderSize, false);
                  }
                  else {
                    renderedImg = renderFlame(currMutation, renderSize, false);
                  }
                  break;
                }
                else if (coverage > bestCoverage) {
                  bestCoverage = coverage;
                  bestMutation = currMutation;
                }
                // Don't count invalid mutations
                if (renderedImg != null) {
                  iter++;
                }
                if (iter >= MAX_ITER) {
                  mutations.add(bestMutation);
                  if (doMorph) {
                    Flame morphed = createWeightedFlame(baseFlame, bestMutation);
                    renderedImg = renderFlame(morphed, renderSize, false);
                  }
                  else {
                    renderedImg = renderFlame(bestMutation, renderSize, false);
                  }
                  break;
                }
              }
            }
            else {
              mutations.add(baseFlame.makeCopy());
              renderedImg = renderFlame(baseFlame.makeCopy(), renderSize, false);
            }

            ImagePanel pnl = imagePanels[i][j];
            pnl.setImage(renderedImg);
            showProgress(++step);

            try {
              pnl.invalidate();
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
        selectedGenerationIdx = mutationList.size() - 1;
        enableControls();
      }

    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private List<MutationType> createMutationTypes(int pX, int pY) {
    MutationType horizType1 = (MutationType) horizontalTrend1Cmb.getSelectedItem();
    MutationType horizType2 = (MutationType) horizontalTrend2Cmb.getSelectedItem();
    MutationType vertType1 = (MutationType) verticalTrend1Cmb.getSelectedItem();
    MutationType vertType2 = (MutationType) verticalTrend2Cmb.getSelectedItem();

    List<MutationType> mutations = new ArrayList<MutationType>();
    if (Math.random() < 0.5) {
      if (pX == 1 || pX == -1) {
        mutations.add(horizType1);
      }
      if (pY == 1 || pY == -1) {
        mutations.add(vertType1);
      }
    }
    else {
      if (pY == 1 || pY == -1) {
        mutations.add(vertType1);
      }
      if (pX == 1 || pX == -1) {
        mutations.add(horizType1);
      }
    }
    if (Math.random() > 0.5) {
      if (pX == 2 || pX == -2) {
        mutations.add(horizType1);
        mutations.add(horizType2);
      }
      if (pY == 2 || pY == -2) {
        mutations.add(vertType1);
        mutations.add(vertType2);
      }
    }
    else {
      if (pY == 2 || pY == -2) {
        mutations.add(vertType1);
        mutations.add(vertType2);
      }
      if (pX == 2 || pX == -2) {
        mutations.add(horizType1);
        mutations.add(horizType2);
      }
    }
    return mutations;
  }

  private void modifyFlame(Flame pFlame, double pX, int pY, List<MutationType> pMutationTypes) {
    for (MutationType mutationType : pMutationTypes) {
      Mutation mutation = mutationType.createMutationInstance();
      for (Layer layer : pFlame.getLayers()) {
        if (layer.isRenderable()) {
          mutation.execute(layer);
        }
      }
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

  public void loadFlameFromEditorBtn_clicked() {
    if (tinaController.getCurrFlame() != null) {
      importFlame(tinaController.getCurrFlame());
    }
  }

  public void loadFlameFromFileBtn_clicked() {
    try {
      JFileChooser chooser = new FlameFileChooser(prefs);
      if (prefs.getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputFlamePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
        Flame flame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        if (flame != null) {
          importFlame(flame);
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void backBtn_clicked() {
    if (selectedGenerationIdx > 0 && mutationList.size() > 0) {
      selectedGenerationIdx--;
      drawSelectedSet();
    }
  }

  public void forwardBtn_clicked() {
    if (selectedGenerationIdx >= 0 && selectedGenerationIdx < mutationList.size() - 1) {
      selectedGenerationIdx++;
      drawSelectedSet();
    }
  }

  public void drawSelectedSet() {
    Dimension imgSize = calcImageSize();
    MutationSet selectedSet = mutationList.get(selectedGenerationIdx);
    int rows = selectedSet.getRows();
    int cols = selectedSet.getCols();
    initProgress(rows, cols);
    int step = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Flame mutation = selectedSet.getFlame(i, j).makeCopy();
        Flame morphed = createWeightedFlame(selectedSet.getBaseFlame(), mutation);
        SimpleImage renderedImg = renderFlame(morphed, imgSize, false);
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
    enableControls();
  }

  private void initHintsPane() {
    hintPane.setContentType("text/html");
    try {
      Font f = new Font(Font.SANS_SERIF, 3, 10);
      hintPane.setFont(f);

      InputStream is = this.getClass().getResourceAsStream("hints.html");
      StringBuffer content = new StringBuffer();
      String lineFeed = System.getProperty("line.separator");
      String line;
      Reader r = new InputStreamReader(is, "utf-8");
      BufferedReader in = new BufferedReader(r);
      while ((line = in.readLine()) != null) {
        content.append(line).append(lineFeed);
      }
      in.close();

      hintPane.setText(content.toString());
      hintPane.setSelectionStart(0);
      hintPane.setSelectionEnd(0);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void enableControls() {
    backButton.setEnabled(selectedGenerationIdx > 0 && selectedGenerationIdx < mutationList.size());
    forwardButton.setEnabled(selectedGenerationIdx >= 0 && selectedGenerationIdx < mutationList.size() - 1);
    saveFlameToEditorBtn.setEnabled(selRow >= 0 && selCol >= 0 && selectedGenerationIdx >= 0 && selectedGenerationIdx < mutationList.size());
    saveFlameToFileBtn.setEnabled(selRow >= 0 && selCol >= 0 && selectedGenerationIdx >= 0 && selectedGenerationIdx < mutationList.size());
  }

  public void exportFlameBtn_clicked() {
    if (selectedGenerationIdx >= 0 && selectedGenerationIdx < mutationList.size() && selRow >= 0 && selCol >= 0) {
      export(selRow, selCol);
    }
  }

  public void saveFlameBtn_clicked() {
    if (selectedGenerationIdx >= 0 && selectedGenerationIdx < mutationList.size() && selRow >= 0 && selCol >= 0) {
      MutationSet selectedSet = mutationList.get(selectedGenerationIdx);
      Flame currFlame = selectedSet.getFlame(selRow, selCol);
      try {
        if (currFlame != null) {
          JFileChooser chooser = new FlameFileChooser(prefs);
          if (prefs.getOutputFlamePath() != null) {
            try {
              chooser.setCurrentDirectory(new File(prefs.getOutputFlamePath()));
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
          if (chooser.showSaveDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            new FlameWriter().writeFlame(currFlame, file.getAbsolutePath());
            currFlame.setLastFilename(file.getName());
            prefs.setLastOutputFlameFile(file);
          }
        }
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }
}
