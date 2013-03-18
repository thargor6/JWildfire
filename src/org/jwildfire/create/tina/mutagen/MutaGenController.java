package org.jwildfire.create.tina.mutagen;

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
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
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
  private boolean noRefresh = false;

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

    noRefresh = true;
    try {
      amountREd.setValue(1.0);
    }
    finally {
      noRefresh = false;
    }

    initHintsPane();
  }

  private void refreshTrendCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    pCmb.addItem(MutationType.ALL);
    pCmb.addItem(MutationType.ADD_TRANSFORM);
    pCmb.addItem(MutationType.AFFINE);
    pCmb.addItem(MutationType.CHANGE_WEIGHT);
    pCmb.addItem(MutationType.GRADIENT);
    pCmb.addItem(MutationType.GRADIENT_POSITION);
    pCmb.addItem(MutationType.RANDOM_FLAME);
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
      pFlame.setSampleDensity(20.0);
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
    if (selectedMutationIdx >= 0 && selectedMutationIdx < mutationList.size()) {
      MutationSet selectedSet = mutationList.get(selectedMutationIdx);
      final int rows = MUTA_ROWS;
      final int cols = MUTA_COLS;
      Flame baseFlame = selectedSet.getFlame(pRow, pCol);
      List<Flame> mutations = new ArrayList<Flame>();
      initProgress(rows, cols);
      int centreX = rows / 2;
      int centreY = cols / 2;

      int step = 0;
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          Flame mFlame = baseFlame.makeCopy();
          // Let centre flame untouched
          if ((i != centreX || j != centreY)) {
            int x = (i - centreX);
            int y = (j - centreY);
            List<MutationType> mutationTypes = createMutationTypes(x, y);
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
        mutations.add(horizType2);
      }
      if (pY == 2 || pY == -2) {
        mutations.add(vertType2);
      }
    }
    else {
      if (pY == 2 || pY == -2) {
        mutations.add(vertType2);
      }
      if (pX == 2 || pX == -2) {
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
    MutationSet selectedSet = mutationList.get(selectedMutationIdx);
    int rows = selectedSet.getRows();
    int cols = selectedSet.getCols();
    initProgress(rows, cols);
    int step = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Flame mFlame = selectedSet.getFlame(i, j).makeCopy();
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
