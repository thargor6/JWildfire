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
package org.jwildfire.create.tina.dance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.audio.JLayerInterface;
import org.jwildfire.create.tina.audio.RecordedFFT;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.action.ActionRecorder;
import org.jwildfire.create.tina.dance.action.PostRecordFlameGenerator;
import org.jwildfire.create.tina.dance.model.AnimationModelService;
import org.jwildfire.create.tina.dance.model.PlainProperty;
import org.jwildfire.create.tina.dance.model.PropertyModel;
import org.jwildfire.create.tina.dance.motion.Motion;
import org.jwildfire.create.tina.dance.motion.MotionCreatorType;
import org.jwildfire.create.tina.dance.motion.MotionType;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.FlameFileChooser;
import org.jwildfire.create.tina.swing.FlameHolder;
import org.jwildfire.create.tina.swing.FlamePanel;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.SoundFileChooser;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.swing.PropertyPanel;
import org.jwildfire.transform.TextTransformer;
import org.jwildfire.transform.TextTransformer.FontStyle;
import org.jwildfire.transform.TextTransformer.HAlignment;
import org.jwildfire.transform.TextTransformer.Mode;
import org.jwildfire.transform.TextTransformer.VAlignment;

public class DancingFractalsController {
  public static final int PAGE_INDEX = 3;
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
  private final JCheckBox doRecordCBx;
  private final JComboBox flamesCmb;
  private final JCheckBox drawTrianglesCbx;
  private final JCheckBox drawFFTCbx;
  private final JCheckBox drawFPSCbx;
  private final JTree flamePropertiesTree;
  private final JPanel motionPropertyRootPnl;
  private PropertyPanel motionPropertyPnl = null;
  private final JTable motionTable;
  private final JComboBox addMotionCmb;
  private final JButton addMotionBtn;
  private final JButton deleteMotionBtn;
  private final JButton linkMotionBtn;
  private final JButton unlinkMotionBtn;
  private final JButton selectNextPropertyBtn;
  private final JComboBox createMotionsCmb;
  private final JButton clearMotionsBtn;
  private final JButton loadProjectBtn;
  private final JButton saveProjectBtn;
  private final JButton motionLinkToAllBtn;
  private final JButton unlinkFromAllMotionsBtn;
  private final JTable motionLinksTable;

  JLayerInterface jLayer = new JLayerInterface();
  private FlamePanel flamePanel = null;
  private FlamePanel poolFlamePreviewFlamePanel = null;
  private ImagePanel graph1Panel = null;
  private DancingFlameProject project = new DancingFlameProject();
  private boolean refreshing;
  private RealtimeAnimRenderThread renderThread;
  private ActionRecorder actionRecorder;

  private RecordedFFT fft;
  private String soundFilename;
  private boolean running = false;
  private final PoolFlameHolder poolFlameHolder;

  public DancingFractalsController(TinaController pParent, ErrorHandler pErrorHandler, JPanel pRealtimeFlamePnl, JPanel pRealtimeGraph1Pnl,
      JButton pLoadSoundBtn, JButton pAddFromClipboardBtn, JButton pAddFromEditorBtn, JButton pAddFromDiscBtn, JWFNumberField pRandomCountIEd,
      JButton pGenRandFlamesBtn, JComboBox pRandomGenCmb, JPanel pPoolFlamePreviewPnl, JSlider pBorderSizeSlider,
      JButton pFlameToEditorBtn, JButton pDeleteFlameBtn, JTextField pFramesPerSecondIEd, JTextField pMorphFrameCountIEd,
      JButton pStartShowButton, JButton pStopShowButton, JCheckBox pDoRecordCBx, JComboBox pFlamesCmb, JCheckBox pDrawTrianglesCbx, JCheckBox pDrawFFTCbx, JCheckBox pDrawFPSCbx, JTree pFlamePropertiesTree,
      JPanel pMotionPropertyRootPnl, JTable pMotionTable, JComboBox pAddMotionCmb, JButton pAddMotionBtn, JButton pDeleteMotionBtn,
      JButton pLinkMotionBtn, JButton pUnlinkMotionBtn, JButton pSelectNextPropertyBtn, JComboBox pCreateMotionsCmb, JButton pClearMotionsBtn,
      JButton pLoadProjectBtn, JButton pSaveProjectBtn, JButton pMotionLinkToAllBtn, JButton pUnlinkFromAllMotionsBtn, JTable pMotionLinksTable) {
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
    poolFlamePreviewPnl = pPoolFlamePreviewPnl;
    borderSizeSlider = pBorderSizeSlider;
    flameToEditorBtn = pFlameToEditorBtn;
    deleteFlameBtn = pDeleteFlameBtn;
    framesPerSecondIEd = pFramesPerSecondIEd;
    morphFrameCountIEd = pMorphFrameCountIEd;
    startShowButton = pStartShowButton;
    stopShowButton = pStopShowButton;
    doRecordCBx = pDoRecordCBx;
    flamesCmb = pFlamesCmb;
    drawTrianglesCbx = pDrawTrianglesCbx;
    drawFFTCbx = pDrawFFTCbx;
    drawFPSCbx = pDrawFPSCbx;
    motionPropertyRootPnl = pMotionPropertyRootPnl;
    motionTable = pMotionTable;
    addMotionCmb = pAddMotionCmb;
    addMotionBtn = pAddMotionBtn;
    deleteMotionBtn = pDeleteMotionBtn;
    linkMotionBtn = pLinkMotionBtn;
    unlinkMotionBtn = pUnlinkMotionBtn;
    selectNextPropertyBtn = pSelectNextPropertyBtn;
    createMotionsCmb = pCreateMotionsCmb;
    clearMotionsBtn = pClearMotionsBtn;
    loadProjectBtn = pLoadProjectBtn;
    saveProjectBtn = pSaveProjectBtn;
    motionLinkToAllBtn = pMotionLinkToAllBtn;
    unlinkFromAllMotionsBtn = pUnlinkFromAllMotionsBtn;
    motionLinksTable = pMotionLinksTable;

    addMotionCmb.addItem(MotionType.FFT);
    addMotionCmb.addItem(MotionType.ROTATE);
    addMotionCmb.setSelectedItem(MotionType.FFT);
    createMotionsCmb.addItem(MotionCreatorType.DEFAULT);
    createMotionsCmb.setSelectedItem(MotionCreatorType.DEFAULT);

    flamePropertiesTree = pFlamePropertiesTree;
    poolFlameHolder = new PoolFlameHolder();

    refreshProjectFlames();

    enableControls();
  }

  private FlamePanel getFlamePanel() {
    if (flamePanel == null && flameRootPanel != null) {
      int borderWidth = flameRootPanel.getBorder().getBorderInsets(flameRootPanel).left;
      int width = flameRootPanel.getWidth() - borderWidth;
      int height = flameRootPanel.getHeight() - borderWidth;
      if (width < 16 || height < 16)
        return null;
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      // TODO
      flamePanel = new FlamePanel(img, 0, 0, flameRootPanel.getWidth() - borderWidth, null);
      // TODO right aspect
      flamePanel.setRenderWidth(640);
      flamePanel.setRenderHeight(480);
      flameRootPanel.add(flamePanel, BorderLayout.CENTER);
      flameRootPanel.getParent().validate();
      flameRootPanel.repaint();
    }
    flamePanel.setFlameHolder(renderThread);
    return flamePanel;
  }

  private FlamePanel getPoolPreviewFlamePanel() {
    if (poolFlamePreviewFlamePanel == null && poolFlamePreviewPnl != null) {
      int width = poolFlamePreviewPnl.getWidth();
      int height = poolFlamePreviewPnl.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      poolFlamePreviewFlamePanel = new FlamePanel(img, 0, 0, poolFlamePreviewPnl.getWidth(), poolFlameHolder);
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

  public void refreshFlameImage(Flame flame, boolean pDrawTriangles, double pFPS, boolean pDrawFPS) {
    FlamePanel imgPanel = getFlamePanel();
    if (imgPanel == null)
      return;
    Rectangle bounds = imgPanel.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height);
      if (flame != null) {
        double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
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

          new FlamePreparer(prefs).prepareFlame(flame);

          RenderedFlame res = renderer.renderFlame(info);
          SimpleImage img = res.getImage();
          if (pDrawFPS) {
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
          flame.setSampleDensity(oldSampleDensity);
        }
      }
    }
    else {
      try {
        imgPanel.setImage(new SimpleImage(width, height));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
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
        new FlamePreparer(prefs).prepareFlame(flame);
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
      if (project.getFlames().size() == 0)
        throw new Exception("No flames to animate");
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
      project.getFlames().add(pFlame);
      refreshProjectFlames();
      enableControls();
    }
  }

  public void startRender() throws Exception {
    stopRender();
    Flame selFlame = flamesCmb.getSelectedIndex() >= 0 && flamesCmb.getSelectedIndex() < project.getFlames().size() ? project.getFlames().get(flamesCmb.getSelectedIndex()) : null;
    renderThread = new RealtimeAnimRenderThread(this);
    renderThread.getFlameStack().addFlame(selFlame, 0);
    actionRecorder = new ActionRecorder(renderThread);
    renderThread.setFFTData(fft);
    renderThread.setMusicPlayer(jLayer);
    renderThread.setFFTPanel(getGraph1Panel());
    renderThread.setFramesPerSecond(Integer.parseInt(framesPerSecondIEd.getText()));
    renderThread.setDrawTriangles(drawTrianglesCbx.isSelected());
    renderThread.setDrawFFT(drawFFTCbx.isSelected());
    renderThread.setDrawFPS(drawFPSCbx.isSelected());
    actionRecorder.recordStart(selFlame);
    new Thread(renderThread).start();
  }

  public void stopRender() throws Exception {
    if (renderThread != null) {
      if (doRecordCBx.isSelected()) {
        actionRecorder.recordStop();
      }
      renderThread.setForceAbort(true);
      if (doRecordCBx.isSelected()) {
        JFileChooser chooser = new FlameFileChooser(prefs);
        if (prefs.getOutputFlamePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputFlamePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(flameRootPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          prefs.setLastOutputFlameFile(file);
          PostRecordFlameGenerator generator = new PostRecordFlameGenerator(getParentCtrl().getPrefs(), actionRecorder, renderThread, fft);
          generator.createRecordedFlameFiles(file.getAbsolutePath());
        }
      }
      renderThread = null;
      actionRecorder = null;
    }
  }

  public void genRandomFlames() {
    try {
      final int IMG_WIDTH = 80;
      final int IMG_HEIGHT = 60;
      int count = (int) ((Double) randomCountIEd.getValue() + 0.5);
      for (int i = 0; i < count; i++) {

        RandomFlameGenerator randGen = RandomFlameGeneratorList.getRandomFlameGeneratorInstance((String) randomGenCmb.getSelectedItem(), true);
        int palettePoints = 3 + (int) (Math.random() * 68.0);
        RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, palettePoints);
        project.getFlames().add(sampler.createSample().getFlame());
      }
      refreshProjectFlames();
      enableControls();
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

  private String getFlameCaption(Flame pFlame) {
    return pFlame.getName().equals("") ? String.valueOf(pFlame.hashCode()) : pFlame.getName();
  }

  private void refreshProjectFlames() {
    boolean oldRefreshing = refreshing;
    refreshing = true;
    try {
      refreshFlamesCmb();
      refreshFlamePropertiesTree();
      refreshMotionTable();
    }
    finally {
      refreshing = oldRefreshing;
    }
  }

  private void refreshMotionTable() {
    final int COL_TYPE = 0;
    final int COL_START_TIME = 1;
    final int COL_END_TIME = 2;
    motionTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        return project.getMotions().size();
      }

      @Override
      public int getColumnCount() {
        return 3;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_TYPE:
            return "Type";
          case COL_START_TIME:
            return "Start time";
          case COL_END_TIME:
            return "End time";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        Motion motion = rowIndex < project.getMotions().size() ? project.getMotions().get(rowIndex) : null;
        if (motion != null) {
          switch (columnIndex) {
            case COL_TYPE:
              return motion.getClass().getSimpleName();
            case COL_START_TIME:
              return (motion.getStartTime() != null) ? Tools.doubleToString(motion.getStartTime()) : "";
            case COL_END_TIME:
              return (motion.getEndTime() != null) ? Tools.doubleToString(motion.getEndTime()) : "";
          }
        }
        return null;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }

    });
    motionTable.getTableHeader().setFont(motionTable.getFont());
    motionTable.getColumnModel().getColumn(COL_TYPE).setWidth(20);
    motionTable.getColumnModel().getColumn(COL_START_TIME).setPreferredWidth(10);
    motionTable.getColumnModel().getColumn(COL_END_TIME).setWidth(10);
  }

  private void refreshFlamesCmb() {
    Flame selFlame = flamesCmb.getSelectedIndex() >= 0 && flamesCmb.getSelectedIndex() < project.getFlames().size() ? project.getFlames().get(flamesCmb.getSelectedIndex()) : null;
    int newSelIdx = -1;
    flamesCmb.removeAllItems();
    for (int i = 0; i < project.getFlames().size(); i++) {
      Flame flame = project.getFlames().get(i);
      if (newSelIdx < 0 && flame.equals(selFlame)) {
        newSelIdx = i;
      }
      flamesCmb.addItem(getFlameCaption(flame));
    }
    flamesCmb.setSelectedIndex(newSelIdx >= 0 ? newSelIdx : project.getFlames().size() > 0 ? 0 : -1);
  }

  private class FlamePropertiesTreeNode<T> extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1L;
    private final T nodeData;

    public FlamePropertiesTreeNode(String pCaption, T pNodeData, boolean pAllowsChildren) {
      super(pCaption, pAllowsChildren);
      nodeData = pNodeData;
    }

    public T getNodeData() {
      return nodeData;
    }
  }

  private void refreshFlamePropertiesTree() {
    FlamePropertiesTreeNode<Object> root = new FlamePropertiesTreeNode<Object>("Flames", null, true);
    for (Flame flame : project.getFlames()) {
      FlamePropertiesTreeNode<Flame> flameNode = new FlamePropertiesTreeNode<Flame>(getFlameCaption(flame), flame, true);
      PropertyModel model = AnimationModelService.createModel(flame);
      addNodesToTree(model, flameNode);
      root.add(flameNode);
    }
    flamePropertiesTree.setModel(new DefaultTreeModel(root));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void addNodesToTree(PropertyModel pModel, FlamePropertiesTreeNode<?> pParentNode) {
    for (PropertyModel subNode : pModel.getChields()) {
      FlamePropertiesTreeNode child = new FlamePropertiesTreeNode(subNode.getName(), subNode, true);
      pParentNode.add(child);
      addNodesToTree(subNode, child);
    }
    for (PlainProperty property : pModel.getProperties()) {
      FlamePropertiesTreeNode<?> child = new FlamePropertiesTreeNode(property.getName(), property, false);
      pParentNode.add(child);
    }
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
        for (Flame newFlame : newFlames) {
          project.getFlames().add(newFlame);
        }
        refreshProjectFlames();
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
      chooser.setMultiSelectionEnabled(true);
      if (chooser.showOpenDialog(poolFlamePreviewPnl) == JFileChooser.APPROVE_OPTION) {
        for (File file : chooser.getSelectedFiles()) {
          List<Flame> newFlames = new Flam3Reader(prefs).readFlames(file.getAbsolutePath());
          prefs.setLastInputFlameFile(file);
          if (newFlames != null && newFlames.size() > 0) {
            for (Flame newFlame : newFlames) {
              project.getFlames().add(newFlame);
            }
          }
        }
        refreshProjectFlames();
        enableControls();
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
      int idx = flamePropertiesTree.getSelectionRows()[0];
      // cant remove the flame by object reference because its a clone
      project.getFlames().remove(idx);
      refreshProjectFlames();
      if (project.getFlames().size() == 0) {
        flamePropertiesTree_changed(null);
      }
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

  private void enableControls() {
    loadSoundBtn.setEnabled(!running);
    addFromClipboardBtn.setEnabled(!running);
    addFromEditorBtn.setEnabled(!running);
    addFromDiscBtn.setEnabled(!running);
    randomCountIEd.setEnabled(!running);
    genRandFlamesBtn.setEnabled(!running);
    randomGenCmb.setEnabled(!running);
    boolean flameSelected = poolFlameHolder.getFlame() != null;
    flameToEditorBtn.setEnabled(flameSelected);
    deleteFlameBtn.setEnabled(flameSelected);

    framesPerSecondIEd.setEnabled(!running);
    borderSizeSlider.setEnabled(true);
    morphFrameCountIEd.setEnabled(true);
    startShowButton.setEnabled(!running && project.getFlames().size() > 0 && soundFilename != null && soundFilename.length() > 0 && fft != null);
    stopShowButton.setEnabled(running);
    doRecordCBx.setEnabled(!running);

    motionTable.setEnabled(!running);
    addMotionCmb.setEnabled(!running);
    addMotionBtn.setEnabled(!running);
    Motion selMotion = motionTable.getSelectedRow() >= 0 && motionTable.getSelectedRow() < project.getMotions().size() ? project.getMotions().get(motionTable.getSelectedRow()) : null;

    deleteMotionBtn.setEnabled(!running && selMotion != null);
    {
      boolean linkMotionEnabled = false;
      boolean unlinkMotionEnabled = false;
      boolean selectNextLinkEnabled = false;
      if (!running) {
        FlamePropertiesTreeNode<?> selectedLeaf = getSelectedLeaf();
        if (selectedLeaf != null) {
          Object data = selectedLeaf.getNodeData();
          boolean propertySelected = data != null && data instanceof PlainProperty;
          if (propertySelected && selMotion != null) {

          }
        }

        selectNextLinkEnabled = selMotion != null && project.getLinks(selMotion).size() > 0;
      }
      linkMotionBtn.setEnabled(linkMotionEnabled);
      unlinkMotionBtn.setEnabled(unlinkMotionEnabled);
      selectNextPropertyBtn.setEnabled(selectNextLinkEnabled);
    }
    createMotionsCmb.setEnabled(!running);
    clearMotionsBtn.setEnabled(!running && project.getMotions().size() > 0);
    loadProjectBtn.setEnabled(!running);
    saveProjectBtn.setEnabled(!running);
  }

  protected TinaController getParentCtrl() {
    return parentCtrl;
  }

  public void drawTrianglesCBx_changed() {
    if (renderThread != null) {
      renderThread.setDrawTriangles(drawTrianglesCbx.isSelected());
    }
  }

  public void drawFFTCBx_changed() {
    if (renderThread != null) {
      renderThread.setDrawFFT(drawFFTCbx.isSelected());
    }
  }

  public void drawFPSCBx_changed() {
    if (renderThread != null) {
      renderThread.setDrawFPS(drawFPSCbx.isSelected());
    }
  }

  public void flameCmb_changed() {
    if (!refreshing && renderThread != null) {
      Flame selFlame = flamesCmb.getSelectedIndex() >= 0 && flamesCmb.getSelectedIndex() < project.getFlames().size() ? project.getFlames().get(flamesCmb.getSelectedIndex()) : null;
      if (selFlame != null && renderThread != null) {
        int morphFrameCount = Integer.parseInt(morphFrameCountIEd.getText());
        renderThread.getFlameStack().addFlame(selFlame, morphFrameCount);
        if (actionRecorder != null)
          actionRecorder.recordFlameChange(selFlame, morphFrameCount);
      }
    }
  }

  private FlamePropertiesTreeNode<?> getSelectedLeaf() {
    if (flamePropertiesTree.getSelectionPath() != null) {
      Object[] selection = flamePropertiesTree.getSelectionPath().getPath();
      if (selection != null && selection.length > 0) {
        return (FlamePropertiesTreeNode<?>) selection[selection.length - 1];
      }
    }
    return null;
  }

  private class PoolFlameHolder implements FlameHolder {

    @Override
    public Flame getFlame() {
      if (flamePropertiesTree.getSelectionPath() != null) {
        Object[] selection = flamePropertiesTree.getSelectionPath().getPath();
        if (selection != null && selection.length > 1) {
          FlamePropertiesTreeNode<?> flameNode = (FlamePropertiesTreeNode<?>) selection[1];
          Flame selFlame = (Flame) flameNode.getNodeData();
          return selFlame;
        }
      }
      return null;
    }
  }

  public void flamePropertiesTree_changed(TreeSelectionEvent e) {
    if (!refreshing) {
      Flame selFlame = null;
      TreePath path = e != null ? e.getNewLeadSelectionPath() : null;
      if (path != null) {
        Object[] selection = path.getPath();
        if (selection != null && selection.length > 1) {
          FlamePropertiesTreeNode<?> flameNode = (FlamePropertiesTreeNode<?>) selection[1];
          selFlame = (Flame) flameNode.getNodeData();
        }
      }
      refreshPoolPreviewFlameImage(selFlame);
      enableControls();
    }
  }

  public void linkMotionBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void selectNextLinkedPropertyBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void unlinkMotionBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void deleteMotionBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void createMotionsBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void clearMotionsBtn_clicked() {

  }

  public void addMotionBtn_clicked() {
    try {
      MotionType motionType = (MotionType) addMotionCmb.getSelectedItem();
      if (motionType != null) {
        Motion newMotion = motionType.getMotionClass().newInstance();
        project.getMotions().add(newMotion);

        boolean oldRefreshing = refreshing;
        refreshing = true;
        try {
          refreshMotionTable();
        }
        finally {
          refreshing = oldRefreshing;
        }

        int selectRow = project.getMotions().size() - 1;
        motionTable.getSelectionModel().setSelectionInterval(selectRow, selectRow);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void motionTableClicked() {
    if (!refreshing) {
      if (motionPropertyPnl != null) {
        motionPropertyRootPnl.remove(motionPropertyPnl);
        motionPropertyPnl = null;
      }
      if (project.getMotions().size() > 0 && motionTable.getSelectedRow() >= 0 && motionTable.getSelectedRow() < project.getMotions().size()) {
        Motion motion = project.getMotions().get(motionTable.getSelectedRow());
        motionPropertyPnl = new PropertyPanel(motion);

        PropertyChangeListener listener = new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent evt) {
            refreshing = true;
            try {
              int oldSel = motionTable.getSelectedRow();
              refreshMotionTable();
              motionTable.getSelectionModel().setSelectionInterval(oldSel, oldSel);
            }
            finally {
              refreshing = false;
            }
          }
        };
        motionPropertyPnl.addPropertySheetChangeListener(listener);

        motionPropertyRootPnl.add(motionPropertyPnl,
            BorderLayout.CENTER);
        enableControls();
      }
      motionPropertyRootPnl.invalidate();
      motionPropertyRootPnl.validate();
    }
  }

  public void dancingFlamesLoadProjectBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void dancingFlamesSaveProjectBtn_clicked() {
    // TODO Auto-generated method stub

  }
}
