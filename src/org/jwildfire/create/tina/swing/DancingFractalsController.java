/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.audio.JLayerInterface;
import org.jwildfire.create.tina.audio.RecordedFFT;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.transform.TextTransformer;
import org.jwildfire.transform.TextTransformer.FontStyle;
import org.jwildfire.transform.TextTransformer.HAlignment;
import org.jwildfire.transform.TextTransformer.Mode;
import org.jwildfire.transform.TextTransformer.VAlignment;

public class DancingFractalsController {
  private final ErrorHandler errorHandler;
  private final TinaController parentCtrl;
  private final Prefs prefs;
  private final JButton loadSoundBtn;
  private final JButton addFromClipboardBtn;
  private final JButton addFromEditorBtn;
  private final JButton addFromDiscBtn;
  private final JWFNumberField randomCountIEd;
  private final JButton genRandFlamesBtn;
  private final JComboBox randomGenCmb;
  private final JTable poolTable;
  private final JPanel poolFlamePreviewPnl;
  private final JSlider borderSizeSlider;
  private final JPanel flameRootPanel;
  private final JPanel graph1RootPanel;
  private final JButton flameToEditorBtn;
  private final JButton deleteFlameBtn;
  private final JTextField framesPerSecondIEd;
  private final JTextField morphFrameCountIEd;
  private final JButton startShowButton;
  private final JButton stopShowButton;

  JLayerInterface jLayer = new JLayerInterface();
  private FlamePanel flamePanel = null;
  private FlamePanel poolFlamePreviewFlamePanel = null;
  private ImagePanel graph1Panel = null;
  private List<Flame> flames = new ArrayList<Flame>();
  private boolean refreshing;
  private RealtimeAnimRenderThread renderThread;

  private RecordedFFT fft;
  private String soundFilename;
  private boolean running = false;

  public DancingFractalsController(TinaController pParent, ErrorHandler pErrorHandler, JPanel pRealtimeFlamePnl, JPanel pRealtimeGraph1Pnl,
      JButton pLoadSoundBtn, JButton pAddFromClipboardBtn, JButton pAddFromEditorBtn, JButton pAddFromDiscBtn, JWFNumberField pRandomCountIEd,
      JButton pGenRandFlamesBtn, JComboBox pRandomGenCmb, JTable pPoolTable, JPanel pPoolFlamePreviewPnl, JSlider pBorderSizeSlider,
      JButton pFlameToEditorBtn, JButton pDeleteFlameBtn, JTextField pFramesPerSecondIEd, JTextField pMorphFrameCountIEd,
      JButton pStartShowButton, JButton pStopShowButton) {
    parentCtrl = pParent;
    errorHandler = pErrorHandler;
    prefs = parentCtrl.getPrefs();
    flameRootPanel = pRealtimeFlamePnl;
    graph1RootPanel = pRealtimeGraph1Pnl;
    loadSoundBtn = pLoadSoundBtn;
    addFromClipboardBtn = pAddFromClipboardBtn;
    addFromEditorBtn = pAddFromEditorBtn;
    addFromDiscBtn = pAddFromDiscBtn;
    randomCountIEd = pRandomCountIEd;
    genRandFlamesBtn = pGenRandFlamesBtn;
    randomGenCmb = pRandomGenCmb;
    poolTable = pPoolTable;
    poolFlamePreviewPnl = pPoolFlamePreviewPnl;
    borderSizeSlider = pBorderSizeSlider;
    flameToEditorBtn = pFlameToEditorBtn;
    deleteFlameBtn = pDeleteFlameBtn;
    framesPerSecondIEd = pFramesPerSecondIEd;
    morphFrameCountIEd = pMorphFrameCountIEd;
    startShowButton = pStartShowButton;
    stopShowButton = pStopShowButton;
    refreshPoolTable();
    enableControls();
  }

  public class RenderFlameHolder implements FlameHolder {
    private Flame currFlame = null;
    private Flame nextFlame = null;

    @Override
    public Flame getFlame() {
      if (nextFlame != null) {
        Flame newFlame = nextFlame.makeCopy();
        if (renderThread != null) {
          renderThread.changeFlame(newFlame);
        }
        currFlame = newFlame;
        nextFlame = null;
      }
      return currFlame;
    }

    public void setNextFlame(Flame pFlame) {
      nextFlame = pFlame;
    }
  }

  private class PoolFlameHolder implements FlameHolder {
    @Override
    public Flame getFlame() {
      int row = poolTable.getSelectedRow();
      return row >= 0 && row < flames.size() ? flames.get(row) : null;
    }
  }

  private final RenderFlameHolder renderFlameHolder = new RenderFlameHolder();
  private final PoolFlameHolder poolFlameHolder = new PoolFlameHolder();

  private FlamePanel getFlamePanel() {
    if (flamePanel == null && flameRootPanel != null) {
      int borderWidth = flameRootPanel.getBorder().getBorderInsets(flameRootPanel).left;
      int width = flameRootPanel.getWidth() - borderWidth;
      int height = flameRootPanel.getHeight() - borderWidth;
      if (width < 16 || height < 16)
        return null;
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(img, 0, 0, flameRootPanel.getWidth() - borderWidth, renderFlameHolder, null, null);
      // TODO right aspect
      flamePanel.setRenderWidth(640);
      flamePanel.setRenderHeight(480);
      flameRootPanel.add(flamePanel, BorderLayout.CENTER);
      flameRootPanel.getParent().validate();
      flameRootPanel.repaint();
    }
    return flamePanel;
  }

  private FlamePanel getPoolPreviewFlamePanel() {
    if (poolFlamePreviewFlamePanel == null && poolFlamePreviewPnl != null) {
      int width = poolFlamePreviewPnl.getWidth();
      int height = poolFlamePreviewPnl.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      poolFlamePreviewFlamePanel = new FlamePanel(img, 0, 0, poolFlamePreviewPnl.getWidth(), poolFlameHolder, null, null);
      // TODO right aspect
      poolFlamePreviewFlamePanel.setRenderWidth(640);
      poolFlamePreviewFlamePanel.setRenderHeight(480);
      poolFlamePreviewFlamePanel.setDrawTriangles(false);
      poolFlamePreviewPnl.add(poolFlamePreviewFlamePanel, BorderLayout.CENTER);
      poolFlamePreviewPnl.getParent().validate();
      poolFlamePreviewPnl.repaint();
    }
    return poolFlamePreviewFlamePanel;
  }

  private ImagePanel getGraph1Panel() {
    if (graph1Panel == null && graph1RootPanel != null) {
      int width = graph1RootPanel.getWidth();
      int height = graph1RootPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      graph1Panel = new ImagePanel(img, 0, 0, graph1RootPanel.getWidth());
      graph1RootPanel.add(graph1Panel, BorderLayout.CENTER);
      graph1RootPanel.getParent().validate();
      graph1RootPanel.repaint();
    }
    return graph1Panel;
  }

  public void refreshFlameImage(Flame flame, boolean pDrawTriangles, double pFPS) {
    FlamePanel imgPanel = getFlamePanel();
    if (imgPanel == null)
      return;
    Rectangle bounds = imgPanel.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;
    boolean showFPS = true;
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height);
      if (flame != null) {
        double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
        int oldSpatialOversample = flame.getSpatialOversample();
        int oldColorOversample = flame.getColorOversample();
        double oldSampleDensity = flame.getSampleDensity();
        imgPanel.setDrawTriangles(pDrawTriangles);
        try {
          double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
          double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(info.getImageWidth());
          flame.setHeight(info.getImageHeight());

          FlameRenderer renderer = new FlameRenderer(flame, prefs, false);
          renderer.setProgressUpdater(null);

          flame.setGamma(1.6);
          flame.setSampleDensity(15);
          flame.setSpatialFilterRadius(0.75);

          flame.setSpatialOversample(1);
          flame.setColorOversample(1);
          RenderedFlame res = renderer.renderFlame(info);
          SimpleImage img = res.getImage();
          if (showFPS) {
            TextTransformer txt = new TextTransformer();
            txt.setText1("fps: " + Tools.doubleToString(pFPS));
            txt.setAntialiasing(false);
            txt.setColor(Color.LIGHT_GRAY);
            txt.setMode(Mode.NORMAL);
            txt.setFontStyle(FontStyle.PLAIN);
            txt.setFontName("Arial");
            txt.setFontSize(10);
            txt.setHAlign(HAlignment.LEFT);
            txt.setVAlign(VAlignment.BOTTOM);
            txt.transformImage(img);
          }
          imgPanel.setImage(img);
        }
        finally {
          flame.setSpatialFilterRadius(oldSpatialFilterRadius);
          flame.setSpatialOversample(oldSpatialOversample);
          flame.setColorOversample(oldColorOversample);
          flame.setSampleDensity(oldSampleDensity);
        }
      }
    }
    else {
      imgPanel.setImage(new SimpleImage(width, height));
    }
    flameRootPanel.repaint();
  }

  public void refreshPoolPreviewFlameImage(Flame flame) {
    FlamePanel imgPanel = getPoolPreviewFlamePanel();
    if (imgPanel == null)
      return;
    Rectangle bounds = imgPanel.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height);
      if (flame != null) {
        imgPanel.setDrawTriangles(false);
        double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
        double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
        flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
        flame.setWidth(info.getImageWidth());
        flame.setHeight(info.getImageHeight());
        FlameRenderer renderer = new FlameRenderer(flame, prefs, false);
        renderer.setProgressUpdater(null);
        flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
        flame.setSpatialOversample(1);
        flame.setColorOversample(1);
        RenderedFlame res = renderer.renderFlame(info);
        imgPanel.setImage(res.getImage());
      }
      else {
        imgPanel.setImage(new SimpleImage(width, height));
      }
    }
    else {
      imgPanel.setImage(new SimpleImage(width, height));
    }
    poolFlamePreviewPnl.repaint();
  }

  public void startShow() {
    try {
      if (flames.size() == 0)
        throw new Exception("No flames to animate");
      if (poolTable.getSelectedRow() < 0)
        poolTable.setRowSelectionInterval(0, 0);
      if (soundFilename == null || soundFilename.length() == 0)
        throw new Exception("No sound file specified");
      if (fft == null)
        throw new Exception("No FFT data");
      jLayer.stop();
      jLayer.play(soundFilename);
      startRender();
      running = true;
      enableControls();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlame(Flame pFlame) {
    if (pFlame != null) {
      flames.add(pFlame);
      refreshPoolTable();
      enableControls();
      // TODO
      //refreshFlameImage(flame, false);
    }
  }

  public void startRender() {
    stopRender();
    renderThread = new RealtimeAnimRenderThread(this);
    renderFlameHolder.setNextFlame(poolFlameHolder.getFlame());
    renderThread.setFFTData(fft);
    renderThread.setMusicPlayer(jLayer);
    renderThread.setFFTPanel(getGraph1Panel());
    renderThread.setFramesPerSecond(Integer.parseInt(framesPerSecondIEd.getText()));
    new Thread(renderThread).start();
  }

  public void stopRender() {
    if (renderThread != null) {
      renderThread.setForceAbort(true);
      renderThread = null;
    }
  }

  public void randomFlame() {
    try {
      final int IMG_WIDTH = 80;
      final int IMG_HEIGHT = 60;
      int count = (int) ((Double) randomCountIEd.getValue() + 0.5);
      for (int i = 0; i < count; i++) {

        RandomFlameGenerator randGen = RandomFlameGeneratorList.getRandomFlameGeneratorInstance((String) randomGenCmb.getSelectedItem(), true);
        int palettePoints = 3 + (int) (Math.random() * 68.0);
        RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, palettePoints);
        flames.add(sampler.createSample().getFlame());
      }
      refreshPoolTable();
      enableControls();
      // TODO
      //    flame = 

      // refreshFlameImage(flame, false);
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void loadSoundButton_clicked() {
    try {
      JFileChooser chooser = new SoundFileChooser(prefs);
      if (prefs.getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputSoundFilePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(flameRootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastInputSoundFile(file);
        setSoundFilename(file.getAbsolutePath());
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void setSoundFilename(String pFilename) throws Exception {
    if (pFilename != null && pFilename.length() > 0) {
      soundFilename = pFilename;
      fft = jLayer.recordFFT(soundFilename);
    }
    else {
      soundFilename = null;
      fft = null;
    }
  }

  public void dancingFlamesPoolTableClicked() {
    if (!refreshing) {
      boolean oldRefreshing = refreshing;
      refreshing = true;
      try {
        renderFlameHolder.setNextFlame(poolFlameHolder.getFlame());
        refreshPoolPreviewFlameImage(poolFlameHolder.getFlame());
      }
      finally {
        refreshing = oldRefreshing;
      }
    }
  }

  private void refreshPoolTable() {
    final int COL_FLAME = 0;
    final int COL_TRANSFORMS = 1;
    poolTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return flames.size();
      }

      @Override
      public int getColumnCount() {
        return 2;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_FLAME:
            return "Flame";
          case COL_TRANSFORMS:
            return "Transforms";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        Flame flame = rowIndex < flames.size() ? flames.get(rowIndex) : null;
        if (flame != null) {
          switch (columnIndex) {
            case COL_FLAME:
              return flame.getName().equals("") ? flame.hashCode() : flame.getName();
            case COL_TRANSFORMS:
              return String.valueOf(flame.getXForms().size());
          }
        }
        return null;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }

    });
    poolTable.getTableHeader().setFont(poolTable.getFont());
    poolTable.getColumnModel().getColumn(COL_FLAME).setWidth(60);
    poolTable.getColumnModel().getColumn(COL_TRANSFORMS).setWidth(16);
  }

  public void loadFlameFromClipboardButton_clicked() {
    List<Flame> newFlames = null;
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          newFlames = new Flam3Reader(prefs).readFlamesfromXML(xml);
        }
      }
      if (newFlames == null || newFlames.size() < 1) {
        throw new Exception("There is currently no valid flame in the clipboard");
      }
      else {
        flames.addAll(newFlames);
        refreshPoolTable();
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void loadFlameButton_clicked() {
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
      if (chooser.showOpenDialog(poolFlamePreviewPnl) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> newFlames = new Flam3Reader(prefs).readFlames(file.getAbsolutePath());
        prefs.setLastInputFlameFile(file);
        if (newFlames != null && newFlames.size() > 0) {
          flames.addAll(newFlames);
          refreshPoolTable();
          enableControls();
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void borderSizeSlider_changed() {
    int value = borderSizeSlider.getValue();
    int currValue = flameRootPanel.getBorder().getBorderInsets(flameRootPanel).left;
    if (currValue != value) {
      flameRootPanel.setBorder(new EmptyBorder(0, 0, value, value));
      if (flamePanel != null) {
        FlamePanel oldFlamePanel = flamePanel;
        flamePanel = null;
        flameRootPanel.remove(oldFlamePanel);
        flameRootPanel.getParent().validate();
        flameRootPanel.repaint();
      }
    }
  }

  public void flameToEditorBtn_clicked() {
    Flame flame = poolFlameHolder.getFlame();
    if (flame != null) {
      parentCtrl.importFlame(flame);
      parentCtrl.getRootTabbedPane().setSelectedIndex(0);
    }
  }

  public void deleteFlameBtn_clicked() {
    Flame flame = poolFlameHolder.getFlame();
    if (flame != null) {
      flames.remove(flame);
      refreshPoolTable();
      if (flames.size() > 0)
        poolTable.setRowSelectionInterval(0, 0);
      enableControls();
    }
  }

  public void stopShow() {
    try {
      if (jLayer != null) {
        jLayer.stop();
      }
      stopRender();
      running = false;
      enableControls();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  protected RenderFlameHolder getRenderFlameHolder() {
    return renderFlameHolder;
  }

  private void enableControls() {
    loadSoundBtn.setEnabled(!running);
    addFromClipboardBtn.setEnabled(!running);
    addFromEditorBtn.setEnabled(!running);
    addFromDiscBtn.setEnabled(!running);
    randomCountIEd.setEnabled(!running);
    genRandFlamesBtn.setEnabled(!running);
    randomGenCmb.setEnabled(!running);
    flameToEditorBtn.setEnabled(flames.size() > 0 && poolTable.getSelectedRow() >= 0);
    deleteFlameBtn.setEnabled(!running && flames.size() > 0 && poolTable.getSelectedRow() >= 0);
    framesPerSecondIEd.setEnabled(!running);
    borderSizeSlider.setEnabled(true);
    morphFrameCountIEd.setEnabled(true);
    startShowButton.setEnabled(!running && flames != null && flames.size() > 0 && soundFilename != null && soundFilename.length() > 0 && fft != null);
    stopShowButton.setEnabled(running);
  }
}
