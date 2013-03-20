package org.jwildfire.create.tina.mutagen;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.FlameMorphService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.FlameFileChooser;
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
  private final JButton loadFlameFromEditorBtn;
  private final JButton loadFlameFromClipboardBtn;
  private final JButton loadFlameFromFileBtn;
  private final JProgressBar progressBar;
  private final JWFNumberField amountREd;
  private final JComboBox horizontalTrend1Cmb;
  private final JComboBox horizontalTrend2Cmb;
  private final JComboBox verticalTrend1Cmb;
  private final JComboBox verticalTrend2Cmb;
  private final JButton backButton;
  private final JButton forwardButton;
  private final JTextPane hintPane;

  private ImagePanel imagePanels[][];
  private final int MUTA_ROWS = 5;
  private final int MUTA_COLS = 5;
  private final List<MutationSet> mutationList = new ArrayList<MutationSet>();
  private int selectedMutationIdx = -1;

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

    public Flame getBaseFlame() {
      return baseFlame;
    }

  }

  public MutaGenController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JTabbedPane pRootTabbedPane, JPanel pFlamePanels[],
      JButton pLoadFlameFromEditorBtn, JButton pLoadFlameFromClipboardBtn, JButton pLoadFlameFromFileBtn,
      JProgressBar pProgressBar, JWFNumberField pAmountREd, JComboBox pHorizontalTrend1Cmb, JComboBox pHorizontalTrend2Cmb,
      JComboBox pVerticalTrend1Cmb, JComboBox pVerticalTrend2Cmb, JButton pBackButton, JButton pForwardButton, JTextPane pHintPane) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    flamePanels = pFlamePanels;
    prefs = pPrefs;
    rootTabbedPane = pRootTabbedPane;
    loadFlameFromEditorBtn = pLoadFlameFromEditorBtn;
    loadFlameFromClipboardBtn = pLoadFlameFromClipboardBtn;
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

    refreshTrendCmb(horizontalTrend1Cmb);
    refreshTrendCmb(horizontalTrend2Cmb);
    refreshTrendCmb(verticalTrend1Cmb);
    refreshTrendCmb(verticalTrend2Cmb);
    amountREd.setValue(1.0);
    initHintsPane();
  }

  private void refreshTrendCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    pCmb.addItem(MutationType.ALL);
    pCmb.addItem(MutationType.ADD_TRANSFORM);
    pCmb.addItem(MutationType.AFFINE);
    pCmb.addItem(MutationType.CHANGE_WEIGHT);
    pCmb.addItem(MutationType.GRADIENT_POSITION);
    pCmb.addItem(MutationType.RANDOM_FLAME);
    pCmb.addItem(MutationType.RANDOM_GRADIENT);
    pCmb.addItem(MutationType.RANDOM_PARAMETER);
    pCmb.setSelectedIndex(0);
  }

  public void importFlame(Flame pFlame) {
    Flame baseFlame = pFlame.makeCopy();
    MutationSet set = new MutationSet(MUTA_ROWS, MUTA_COLS, baseFlame);
    mutationList.add(set);
    selectedMutationIdx = mutationList.size() - 1;
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
      return FlameMorphService.morphFlames(prefs, pBaseFlame.makeCopy(), pFlame.makeCopy(), morphFrame, morphFrames);
    }
  }

  private SimpleImage renderFlame(Flame pFlame, Dimension pImgSize) {
    int pImageWidth = pImgSize.width, pImageHeight = pImgSize.height;
    SimpleImage img;
    if (pFlame != null && pImageWidth > 16 && pImageHeight > 16) {
      return img = executeRenderThread(pFlame, pImageWidth, pImageHeight);
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
        RenderInfo info = new RenderInfo(imageWidth, imageHeight);
        double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
        double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
        flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
        flame.setWidth(imageWidth);
        flame.setHeight(imageHeight);
        flame.setSampleDensity(20.0);
        renderer = new FlameRenderer(flame, prefs, false);
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
      renderer.signalCancel();
    }

  }

  private SimpleImage executeRenderThread(Flame pFlame, int pImageWidth, int pImageHeight) {
    RenderThread thread = new RenderThread(pFlame, pImageWidth, pImageHeight);
    long tMax = 1000;
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
      if (t1 - t0 > tMax) {
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
    if (selectedMutationIdx >= 0 && selectedMutationIdx < mutationList.size()) {
      MutationSet selectedSet = mutationList.get(selectedMutationIdx);

      Flame flame = selectedSet.getFlame(pRow, pCol);
      Flame morphedFlame = createWeightedFlame(selectedSet.getBaseFlame(), flame);

      tinaController.importFlame(morphedFlame);

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

  private Dimension calcImageSize() {
    createImagePanels();
    ImagePanel pnl = imagePanels[0][0];
    Dimension size = pnl.getSize();
    int imageWidth = Tools.FTOI(size.getWidth());
    int imageHeight = Tools.FTOI(size.getHeight());
    return new Dimension(imageWidth, imageHeight);
  }

  protected void mutate(int pRow, int pCol) {
    if (selectedMutationIdx >= 0 && selectedMutationIdx < mutationList.size()) {
      Dimension renderSize = calcImageSize();
      Dimension probeSize = new Dimension(80, 60);

      MutationSet selectedSet = mutationList.get(selectedMutationIdx);
      final int rows = MUTA_ROWS;
      final int cols = MUTA_COLS;
      Flame baseFlame = selectedSet.getFlame(pRow, pCol);
      baseFlame = createWeightedFlame(selectedSet.getBaseFlame(), baseFlame);

      boolean doMorph = fabs((Double) amountREd.getValue() - 1.0) > EPSILON;

      List<Flame> mutations = new ArrayList<Flame>();
      initProgress(rows, cols);
      int centreX = rows / 2;
      int centreY = cols / 2;

      SimpleImage baseFlameImg = renderFlame(baseFlame.makeCopy(), probeSize);
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
            final double MIN_RENDER_COVERAGE = 0.42;
            final double MIN_DIFF_COVERAGE = 0.28;
            final double INVALID_COVERAGE = -1.0;
            int iter = 0;
            double bestCoverage = INVALID_COVERAGE;
            Flame bestMutation = null;
            while (true) {
              Flame currMutation = baseFlame.makeCopy();
              List<MutationType> mutationTypes = createMutationTypes(x, y);
              modifyFlame(currMutation, x, y, mutationTypes);
              renderedImg = renderFlame(currMutation.makeCopy(), probeSize);
              double coverage = renderedImg != null ? RandomFlameGeneratorSampler.calculateCoverage(renderedImg, 0, 0, 0) : INVALID_COVERAGE;
              if (coverage > MIN_RENDER_COVERAGE) {
                coverage = RandomFlameGeneratorSampler.calculateDiffCoverage(renderedImg, simplifiedBaseFlameImg);
              }
              else {
                coverage = 0.0;
              }
              if (coverage > MIN_DIFF_COVERAGE) {
                mutations.add(currMutation);
                if (doMorph) {
                  Flame morphed = createWeightedFlame(baseFlame, currMutation);
                  renderedImg = renderFlame(morphed, renderSize);
                }
                else {
                  renderedImg = renderFlame(currMutation, renderSize);
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
                  renderedImg = renderFlame(morphed, renderSize);
                }
                else {
                  renderedImg = renderFlame(bestMutation, renderSize);
                }
                break;
              }
            }
          }
          else {
            mutations.add(baseFlame.makeCopy());
            renderedImg = renderFlame(baseFlame.makeCopy(), renderSize);
          }
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
      selectedMutationIdx = mutationList.size() - 1;
      enableControls();
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

  public void loadFlameFromEditorBtn_clicked() {
    if (tinaController.getCurrFlame() != null) {
      importFlame(tinaController.getCurrFlame());
    }
  }

  public void loadFlameFromClipboardBtn_clicked() {
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          List<Flame> flames = new Flam3Reader(prefs).readFlamesfromXML(xml);
          Flame flame = flames.get(0);
          if (flame != null) {
            importFlame(flame);
          }
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
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
      if (chooser.showOpenDialog(rootTabbedPane) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> flames = new Flam3Reader(prefs).readFlames(file.getAbsolutePath());
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
    if (selectedMutationIdx > 0 && mutationList.size() > 0) {
      selectedMutationIdx--;
      drawSelectedSet();
    }
  }

  public void forwardBtn_clicked() {
    if (selectedMutationIdx >= 0 && selectedMutationIdx < mutationList.size() - 1) {
      selectedMutationIdx++;
      drawSelectedSet();
    }
  }

  public void drawSelectedSet() {
    Dimension imgSize = calcImageSize();
    MutationSet selectedSet = mutationList.get(selectedMutationIdx);
    int rows = selectedSet.getRows();
    int cols = selectedSet.getCols();
    initProgress(rows, cols);
    int step = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Flame mutation = selectedSet.getFlame(i, j).makeCopy();
        Flame morphed = createWeightedFlame(selectedSet.getBaseFlame(), mutation);
        SimpleImage renderedImg = renderFlame(morphed, imgSize);
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
    backButton.setEnabled(selectedMutationIdx > 0 && selectedMutationIdx < mutationList.size());
    forwardButton.setEnabled(selectedMutationIdx >= 0 && selectedMutationIdx < mutationList.size() - 1);
  }
}
