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
package org.jwildfire.create.iflames.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.edit.UndoManager;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.mutagen.RandomGradientMutation;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.FlameFileChooser;
import org.jwildfire.create.tina.swing.FlameHolder;
import org.jwildfire.create.tina.swing.FlameMessageHelper;
import org.jwildfire.create.tina.swing.FlamePanelProvider;
import org.jwildfire.create.tina.swing.FlamePreviewHelper;
import org.jwildfire.create.tina.swing.FlameThumbnail;
import org.jwildfire.create.tina.swing.ImageThumbnail;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.RenderProgressBarHolder;
import org.jwildfire.create.tina.swing.RenderProgressUpdater;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.create.tina.variation.iflames.BaseFlameListCreator;
import org.jwildfire.create.tina.variation.iflames.IFlamesFunc;
import org.jwildfire.create.tina.variation.iflames.ShapeDistribution;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.swing.MainController;

public class IFlamesController implements FlameHolder, FlamePanelProvider, RenderProgressBarHolder {
  private final Prefs prefs;
  private final MainController mainController;
  private final JInternalFrame iflamesFrame;
  private final ErrorHandler errorHandler;
  private final JPanel centerPanel;
  private final FlamePreviewHelper flamePreviewHelper;
  private final JButton undoButton;
  private final JButton redoButton;
  private final JButton renderButton;
  private final JPanel imageStackPanel;
  private final JPanel flameStackPanel;
  private final JButton loadIFlameButton;
  private final JButton loadIFlameFromClipboardButton;
  private final JButton saveIFlameToClipboardButton;
  private final JButton saveIFlameButton;
  private final JButton refreshIFlameButton;
  private final JProgressBar mainProgressBar;
  private final JToggleButton autoRefreshButton;
  private final JComboBox baseFlameCmb;
  private final JPanel baseFlamePreviewRootPnl;
  private final JComboBox resolutionProfileCmb;
  private final JToggleButton edgesNorthButton;
  private final JToggleButton edgesWestButton;
  private final JToggleButton edgesEastButton;
  private final JToggleButton edgesSouthButton;
  private final JToggleButton erodeButton;
  private final JToggleButton displayPreprocessedImageButton;
  private final JWFNumberField erodeSizeField;
  private final JWFNumberField maxImageWidthField;
  private final JWFNumberField structureThresholdField;
  private final JWFNumberField structureDensityField;
  private final JWFNumberField globalScaleXField;
  private final JWFNumberField globalScaleYField;
  private final JWFNumberField globalScaleZField;
  private final JWFNumberField globalOffsetXField;
  private final JWFNumberField globalOffsetYField;
  private final JWFNumberField globalOffsetZField;
  private final JComboBox shapeDistributionCmb;

  private Flame _currFlame;
  private FlamePanel flamePanel;
  private FlamePanel baseFlamePreviewPanel;
  private FlamePanel prevFlamePanel;
  private final UndoManager<Flame> undoManager = new UndoManager<Flame>();
  private final ProgressUpdater mainProgressUpdater;
  private final FlameMessageHelper messageHelper;
  private boolean undoDebug = true;
  private boolean noRefresh;

  @SuppressWarnings("unchecked")
  public IFlamesController(MainController pMainController, ErrorHandler pErrorHandler,
      JInternalFrame pIflamesFrame, JPanel pCenterPanel,
      JButton pUndoButton, JButton pRedoButton, JButton pRenderButton, JPanel pImageStackPanel,
      JPanel pFlameStackPanel, JButton pLoadIFlameButton, JButton pLoadIFlameFromClipboardButton,
      JButton pSaveIFlameToClipboardButton, JButton pSaveIFlameButton, JButton pRefreshIFlameButton,
      JProgressBar pMainProgressBar, JToggleButton pAutoRefreshButton, JComboBox pBaseFlameCmb,
      JPanel pBaseFlamePreviewRootPnl, JComboBox pResolutionProfileCmb, JToggleButton pEdgesNorthButton,
      JToggleButton pEdgesWestButton, JToggleButton pEdgesEastButton, JToggleButton pEdgesSouthButton,
      JToggleButton pErodeButton, JToggleButton pDisplayPreprocessedImageButton, JWFNumberField pErodeSizeField,
      JWFNumberField pMaxImageWidthField, JWFNumberField pStructureThresholdField, JWFNumberField pStructureDensityField,
      JWFNumberField pGlobalScaleXField, JWFNumberField pGlobalScaleYField, JWFNumberField pGlobalScaleZField,
      JWFNumberField pGlobalOffsetXField, JWFNumberField pGlobalOffsetYField, JWFNumberField pGlobalOffsetZField,
      JComboBox pShapeDistributionCmb) {
    noRefresh = true;
    prefs = Prefs.getPrefs();
    mainController = pMainController;
    errorHandler = pErrorHandler;
    iflamesFrame = pIflamesFrame;
    centerPanel = pCenterPanel;
    undoButton = pUndoButton;
    redoButton = pRedoButton;
    renderButton = pRenderButton;
    imageStackPanel = pImageStackPanel;
    flameStackPanel = pFlameStackPanel;
    loadIFlameButton = pLoadIFlameButton;
    loadIFlameFromClipboardButton = pLoadIFlameFromClipboardButton;
    saveIFlameToClipboardButton = pSaveIFlameToClipboardButton;
    saveIFlameButton = pSaveIFlameButton;
    refreshIFlameButton = pRefreshIFlameButton;
    mainProgressBar = pMainProgressBar;
    autoRefreshButton = pAutoRefreshButton;
    baseFlameCmb = pBaseFlameCmb;
    baseFlamePreviewRootPnl = pBaseFlamePreviewRootPnl;
    resolutionProfileCmb = pResolutionProfileCmb;
    edgesNorthButton = pEdgesNorthButton;
    edgesWestButton = pEdgesWestButton;
    edgesEastButton = pEdgesEastButton;
    edgesSouthButton = pEdgesSouthButton;
    erodeButton = pErodeButton;
    displayPreprocessedImageButton = pDisplayPreprocessedImageButton;
    erodeSizeField = pErodeSizeField;
    maxImageWidthField = pMaxImageWidthField;

    structureThresholdField = pStructureThresholdField;
    structureDensityField = pStructureDensityField;
    globalScaleXField = pGlobalScaleXField;
    globalScaleYField = pGlobalScaleYField;
    globalScaleZField = pGlobalScaleZField;
    globalOffsetXField = pGlobalOffsetXField;
    globalOffsetYField = pGlobalOffsetYField;
    globalOffsetZField = pGlobalOffsetZField;
    shapeDistributionCmb = pShapeDistributionCmb;

    messageHelper = new JInternalFrameFlameMessageHelper(iflamesFrame);
    mainProgressUpdater = new RenderProgressUpdater(this);

    flamePreviewHelper = new FlamePreviewHelper(errorHandler, centerPanel, null,
        null, null, mainProgressUpdater, this, null, null, this, messageHelper, null);
    enableControls();

    refreshResolutionProfileCmb(resolutionProfileCmb, null);
    {
      baseFlameCmb.removeAllItems();
      for (int i = 0; i < IFlamesFunc.MAX_FLAME_COUNT; i++) {
        baseFlameCmb.addItem("Flame " + (i + 1));
      }
      baseFlameCmb.setSelectedIndex(0);
    }
    {
      shapeDistributionCmb.removeAllItems();
      shapeDistributionCmb.addItem(ShapeDistribution.HUE);
      shapeDistributionCmb.addItem(ShapeDistribution.RANDOM);
      shapeDistributionCmb.setSelectedItem(ShapeDistribution.HUE);
    }

    displayPreprocessedImageButton.setSelected(false);

    noRefresh = false;
  }

  public void setFlame(Flame pFlame) {
    _currFlame = pFlame;
  }

  @Override
  public Flame getFlame() {
    return _currFlame;
  }

  @Override
  public FlamePanel getFlamePanel() {
    if (flamePanel == null) {
      Prefs prefs = Prefs.getPrefs();
      int width = centerPanel.getWidth();
      int height = centerPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(prefs, img, 0, 0, centerPanel.getWidth(), this, null);
      flamePanel.getConfig().setWithColoredTransforms(prefs.isTinaEditorControlsWithColor());
      flamePanel.setFlamePanelTriangleMode(prefs.getTinaEditorControlsStyle());
      flamePanel.importOptions(prevFlamePanel);
      prevFlamePanel = null;
      ResolutionProfile resProfile = getResolutionProfile();
      flamePanel.setRenderWidth(resProfile.getWidth());
      flamePanel.setRenderHeight(resProfile.getHeight());
      flamePanel.setFocusable(true);

      centerPanel.add(flamePanel, BorderLayout.CENTER);
      centerPanel.getParent().validate();
      centerPanel.repaint();
      flamePanel.requestFocusInWindow();
    }
    return flamePanel;
  }

  private void removeFlamePanel() {
    if (flamePanel != null) {
      centerPanel.remove(flamePanel);
      prevFlamePanel = flamePanel;
      flamePanel = null;
    }
  }

  @Override
  public FlamePanelConfig getFlamePanelConfig() {
    return getFlamePanel().getConfig();
  }

  public void newButton_clicked() {
    setFlame(createNewFlame());
    undoManager.initUndoStack(getFlame());
    refreshUI();
    enableControls();
  }

  private Flame createNewFlame() {
    Flame flame = new Flame();
    flame.setCamRoll(0);
    flame.setCamPitch(0);
    flame.setCamYaw(0);
    flame.setCamPerspective(0);
    flame.setWidth(800);
    flame.setHeight(600);
    flame.setCamZoom(1);
    {
      Layer layer = flame.getFirstLayer();
      new RandomGradientMutation().execute(layer);

      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance("iflames_wf", true);
      xForm.addVariation(1.0, varFunc);
    }
    return flame.makeCopy();
  }

  private void refreshPreview() {
    if (displayPreprocessedImageButton.isSelected()) {
      flamePreviewHelper.renderFlameImage(true, true, 1);
      SimpleImage img = (SimpleImage) RessourceManager.getRessource(BaseFlameListCreator.LAST_PREPROCESSED_IMAGE);
      if (img != null) {
        flamePreviewHelper.setImage(img);
        return;
      }
    }
    flamePreviewHelper.refreshFlameImage(true, false, 1);
  }

  public void enableControls() {
    // TODO
    enableUndoControls();
    boolean hasFlame = getFlame() != null;
    boolean hasIFlame = hasFlame && getIFlamesFunc() != null;
    loadIFlameButton.setEnabled(true);
    loadIFlameFromClipboardButton.setEnabled(true);
    saveIFlameToClipboardButton.setEnabled(hasIFlame);
    saveIFlameButton.setEnabled(hasIFlame);
    edgesNorthButton.setEnabled(hasIFlame);
    edgesWestButton.setEnabled(hasIFlame);
    edgesEastButton.setEnabled(hasIFlame);
    edgesSouthButton.setEnabled(hasIFlame);
    erodeButton.setEnabled(hasIFlame);
    displayPreprocessedImageButton.setEnabled(hasIFlame);
    autoRefreshButton.setEnabled(hasIFlame);
    refreshIFlameButton.setEnabled(hasIFlame);
    erodeSizeField.setEnabled(erodeButton.isEnabled() && erodeButton.isSelected());
    maxImageWidthField.setEnabled(hasIFlame);
    baseFlameCmb.setEnabled(hasIFlame);
    structureThresholdField.setEnabled(hasIFlame);
    structureDensityField.setEnabled(hasIFlame);
    globalScaleXField.setEnabled(hasIFlame);
    globalScaleYField.setEnabled(hasIFlame);
    globalScaleZField.setEnabled(hasIFlame);
    globalOffsetXField.setEnabled(hasIFlame);
    globalOffsetYField.setEnabled(hasIFlame);
    globalOffsetZField.setEnabled(hasIFlame);
    shapeDistributionCmb.setEnabled(hasIFlame);
  }

  public void saveUndoPoint() {
    if (getFlame() != null) {
      undoManager.saveUndoPoint(getFlame());
      enableUndoControls();
      undoButton.invalidate();
      undoButton.validate();
      redoButton.invalidate();
      redoButton.validate();
      undoButton.getParent().repaint();
    }
  }

  private void enableUndoControls() {
    final String UNDO_LABEL = "Undo";
    final String REDO_LABEL = "Redo";
    if (getFlame() != null) {
      int stackSize = undoManager.getUndoStackSize(getFlame());
      if (stackSize > 0) {
        int pos = undoManager.getUndoStackPosition(getFlame());
        undoButton.setEnabled(pos > 0 && pos < stackSize);
        if (undoDebug) {
          undoButton.setText("U " + pos);
        }
        else {
          undoButton.setToolTipText(UNDO_LABEL + " " + pos + "/" + stackSize);
        }
        redoButton.setEnabled(pos >= 0 && pos < stackSize - 1);
        if (undoDebug) {
          redoButton.setText("R " + stackSize);
        }
        else {
          redoButton.setToolTipText(REDO_LABEL + " " + pos + "/" + stackSize);
        }
      }
      else {
        undoButton.setEnabled(false);
        if (undoDebug) {
          undoButton.setText(UNDO_LABEL);
        }
        else {
          undoButton.setToolTipText(UNDO_LABEL);
        }
        redoButton.setEnabled(false);
        if (undoDebug) {
          redoButton.setText(REDO_LABEL);
        }
        else {
          redoButton.setToolTipText(REDO_LABEL);
        }
      }
    }
    else {
      undoButton.setEnabled(false);
      redoButton.setEnabled(false);
    }
  }

  public void undoAction() {
    if (getFlame() != null) {
      undoManager.setEnabled(false);
      try {
        undoManager.saveUndoPoint(getFlame());
        undoManager.undo(getFlame());
        enableUndoControls();
        refreshIFlame();
        enableControls();
        refreshUI();
      }
      finally {
        undoManager.setEnabled(true);
      }
    }
  }

  private void refreshUI() {
    refreshBaseFlamePreview();
    refreshImageFields();
    refreshBaseFlameFields();
    refreshPreview();
  }

  public void redoAction() {
    if (getFlame() != null) {
      undoManager.setEnabled(false);
      try {
        undoManager.redo(getFlame());
        enableUndoControls();
        refreshIFlame();
        enableControls();
        refreshUI();
      }
      finally {
        undoManager.setEnabled(true);
      }
    }
  }

  public void renderFlameButton_clicked() {
    flamePreviewHelper.refreshFlameImage(false, false, 1);
  }

  public void loadImagesButton_clicked() {
    JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
    if (prefs.getInputImagePath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    chooser.setMultiSelectionEnabled(true);
    if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
      Throwable lastError = null;
      for (File file : chooser.getSelectedFiles()) {
        try {
          String filename = file.getAbsolutePath();
          WFImage img = RessourceManager.getImage(filename);
          if (img.getImageWidth() < 16 || img.getImageHeight() < 16 || !(img instanceof SimpleImage)) {
            throw new Exception("Invalid image");
          }
          prefs.setLastInputImageFile(file);
          addImageToImageStack(filename);
        }
        catch (Throwable ex) {
          lastError = ex;
        }
      }
      refreshImageStack();
      if (lastError != null) {
        errorHandler.handleError(lastError);
      }
    }
  }

  public void loadFlamesButton_clicked() {
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
    if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
      Throwable lastError = null;
      for (File file : chooser.getSelectedFiles()) {
        try {
          List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
          prefs.setLastInputFlameFile(file);
          for (int i = flames.size() - 1; i >= 0; i--) {
            addFlameToFlameStack(flames.get(i));
          }
        }
        catch (Throwable ex) {
          lastError = ex;
        }
      }
      refreshFlameStack();
      if (lastError != null) {
        errorHandler.handleError(lastError);
      }
    }
  }

  private final List<ImageThumbnail> imageStack = new ArrayList<ImageThumbnail>();
  private JScrollPane imageStackScrollPane;

  private void refreshImageStack() {
    if (imageStackScrollPane != null) {
      imageStackPanel.remove(imageStackScrollPane);
      imageStackScrollPane = null;
    }
    int panelWidth = ImageThumbnail.IMG_WIDTH + 2 * ImageThumbnail.BORDER_SIZE;
    int panelHeight = 0;
    for (int i = 0; i < imageStack.size(); i++) {
      panelHeight += ImageThumbnail.BORDER_SIZE + imageStack.get(i).getPreview().getImageHeight();
    }
    JPanel batchPanel = new JPanel();
    batchPanel.setLayout(null);
    batchPanel.setSize(panelWidth, panelHeight);
    batchPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    int yOff = ImageThumbnail.BORDER_SIZE;
    for (int i = 0; i < imageStack.size(); i++) {
      SimpleImage img = imageStack.get(i).getPreview();
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(ImageThumbnail.BORDER_SIZE, yOff);
      yOff += img.getImageHeight() + ImageThumbnail.BORDER_SIZE;
      imageStack.get(i).setImgPanel(imgPanel);
      final int idx = i;
      addRemoveImageFromStackButton(imgPanel, idx);
      imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() > 1 || e.getButton() != MouseEvent.BUTTON1) {
            importFromImageStack(idx);
          }
        }
      });
      batchPanel.add(imgPanel);
    }
    imageStackScrollPane = new JScrollPane(batchPanel);
    imageStackScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    imageStackScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    imageStackPanel.add(imageStackScrollPane, BorderLayout.CENTER);
    imageStackScrollPane.validate();
    imageStackScrollPane.getParent().validate();
  }

  private void addRemoveImageFromStackButton(ImagePanel pImgPanel, final int pIdx) {
    final int BTN_WIDTH = 20;
    final int BTN_HEIGHT = 20;
    final int BORDER = 0;
    JButton btn = new JButton();
    btn.setMinimumSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    btn.setMaximumSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    btn.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    btn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/removeThumbnail.gif")));
    btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        removeImageFromStack(pIdx);
      }
    });
    pImgPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, BORDER, BORDER));
    pImgPanel.add(btn);
    pImgPanel.invalidate();
  }

  public void importFromImageStack(int pIdx) {
    if (pIdx >= 0 && pIdx < imageStack.size() && getIFlamesFunc() != null) {
      String filename = imageStack.get(pIdx).getFilename();
      saveUndoPoint();
      getIFlamesFunc().getImageParams().setImageFilename(filename);
      refreshIFlame();
    }
  }

  protected void removeImageFromStack(int pIdx) {
    imageStack.remove(pIdx);
    refreshImageStack();
  }

  private void addImageToImageStack(String pFilename) {
    imageStack.add(0, new ImageThumbnail(pFilename, null));
  }

  private final List<FlameThumbnail> flameStack = new ArrayList<FlameThumbnail>();
  private JScrollPane flameStackScrollPane;

  private void refreshFlameStack() {
    if (flameStackScrollPane != null) {
      flameStackPanel.remove(flameStackScrollPane);
      flameStackScrollPane = null;
    }
    int panelWidth = FlameThumbnail.IMG_WIDTH + 2 * FlameThumbnail.BORDER_SIZE;
    int panelHeight = (FlameThumbnail.IMG_HEIGHT + FlameThumbnail.BORDER_SIZE) * flameStack.size();
    JPanel batchPanel = new JPanel();
    batchPanel.setLayout(null);
    batchPanel.setSize(panelWidth, panelHeight);
    batchPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
    for (int i = 0; i < flameStack.size(); i++) {
      SimpleImage img = flameStack.get(i).getPreview(3 * prefs.getTinaRenderPreviewQuality() / 4);
      // add it to the main panel
      ImagePanel imgPanel = new ImagePanel(img, 0, 0, img.getImageWidth());
      imgPanel.setImage(img);
      imgPanel.setLocation(FlameThumbnail.BORDER_SIZE, i * FlameThumbnail.IMG_HEIGHT + (i + 1) * FlameThumbnail.BORDER_SIZE);
      flameStack.get(i).setImgPanel(imgPanel);
      final int idx = i;
      addRemoveFlameFromStackButton(imgPanel, idx);
      imgPanel.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() > 1 || e.getButton() != MouseEvent.BUTTON1) {
            importFromFlameStack(idx);
          }
        }
      });
      batchPanel.add(imgPanel);
    }
    flameStackScrollPane = new JScrollPane(batchPanel);
    flameStackScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    flameStackScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    flameStackPanel.add(flameStackScrollPane, BorderLayout.CENTER);
    flameStackScrollPane.validate();
    flameStackScrollPane.getParent().validate();
  }

  private void addFlameToFlameStack(Flame pFlame) {
    flameStack.add(0, new FlameThumbnail(pFlame, null));
  }

  private void addRemoveFlameFromStackButton(ImagePanel pImgPanel, final int pIdx) {
    final int BTN_WIDTH = 20;
    final int BTN_HEIGHT = 20;
    final int BORDER = 0;
    JButton btn = new JButton();
    btn.setMinimumSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    btn.setMaximumSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    btn.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    btn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/removeThumbnail.gif")));
    btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        removeFlameFromStack(pIdx);
      }
    });
    pImgPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, BORDER, BORDER));
    pImgPanel.add(btn);
    pImgPanel.invalidate();
  }

  protected void removeFlameFromStack(int pIdx) {
    flameStack.remove(pIdx);
    refreshFlameStack();
  }

  public void importFromFlameStack(int pIdx) {
    if (pIdx >= 0 && pIdx < flameStack.size() && getIFlamesFunc() != null) {
      Flame flame = flameStack.get(pIdx).getFlame();
      saveUndoPoint();
      try {
        String flameXML = new FlameWriter().getFlameXML(flame);
        getIFlamesFunc().getFlameParams(getCurrFlameIndex()).setFlameXML(flameXML);
      }
      catch (Exception e) {
        errorHandler.handleError(e);
      }
      refreshBaseFlamePreview();
      refreshIFlame();
    }
  }

  private void refreshIFlame() {
    if (autoRefreshButton.isSelected()) {
      refreshPreview();
    }
  }

  private IFlamesFunc getIFlamesFunc() {
    Flame flame = getFlame();
    if (flame != null) {
      for (Layer layer : flame.getLayers()) {
        for (XForm xform : layer.getXForms()) {
          for (int i = 0; i < xform.getVariationCount(); i++) {
            Variation var = xform.getVariation(i);
            if (var.getFunc() instanceof IFlamesFunc) {
              return (IFlamesFunc) var.getFunc();
            }
          }
        }
        for (XForm xform : layer.getFinalXForms()) {
          for (int i = 0; i < xform.getVariationCount(); i++) {
            Variation var = xform.getVariation(i);
            if (var.getFunc() instanceof IFlamesFunc) {
              return (IFlamesFunc) var.getFunc();
            }
          }
        }
      }
    }
    return null;
  }

  private int getCurrFlameIndex() {
    return baseFlameCmb.getSelectedIndex();
  }

  @SuppressWarnings("unchecked")
  private void refreshResolutionProfileCmb(@SuppressWarnings("rawtypes") JComboBox pCmb, ResolutionProfile pSelectedProfile) {
    boolean oldNoRefresh = noRefresh;
    noRefresh = true;
    try {
      ResolutionProfile selected = pSelectedProfile;
      ResolutionProfile defaultProfile = null;
      pCmb.removeAllItems();
      for (ResolutionProfile profile : prefs.getResolutionProfiles()) {
        if (selected == null && profile.isDefaultProfile()) {
          selected = profile;
        }
        if (defaultProfile == null && profile.isDefaultProfile()) {
          defaultProfile = profile;
        }
        pCmb.addItem(profile);
      }
      if (selected != null) {
        pCmb.setSelectedItem(selected);
      }
      if (pCmb.getSelectedIndex() < 0 && defaultProfile != null) {
        pCmb.setSelectedItem(defaultProfile);
      }
      if (pCmb.getSelectedIndex() < 0 && prefs.getResolutionProfiles().size() > 0) {
        pCmb.setSelectedIndex(0);
      }
    }
    finally {
      noRefresh = oldNoRefresh;
    }
  }

  private ResolutionProfile getResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) resolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  public void resolutionProfileCmb_changed() {
    if (noRefresh || getFlame() == null) {
      return;
    }
    noRefresh = true;
    try {
      ResolutionProfile profile = getResolutionProfile();
      getFlame().setResolutionProfile(profile);
      removeFlamePanel();
      refreshPreview();
      resolutionProfileCmb.requestFocus();
    }
    finally {
      noRefresh = false;
    }
  }

  public void baseFlameCmb_changed() {
    if (noRefresh || getFlame() == null) {
      return;
    }
    refreshBaseFlamePreview();
    refreshBaseFlameFields();
  }

  private FlamePanel getBaseFlamePreviewPanel() {
    if (baseFlamePreviewPanel == null) {
      int width = baseFlamePreviewRootPnl.getWidth();
      int height = baseFlamePreviewRootPnl.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      baseFlamePreviewPanel = new FlamePanel(prefs, img, 0, 0, baseFlamePreviewRootPnl.getWidth(), this, null);
      ResolutionProfile resProfile = getResolutionProfile();
      baseFlamePreviewPanel.setRenderWidth(resProfile.getWidth());
      baseFlamePreviewPanel.setRenderHeight(resProfile.getHeight());
      baseFlamePreviewPanel.setFocusable(true);
      baseFlamePreviewRootPnl.add(baseFlamePreviewPanel, BorderLayout.CENTER);
      baseFlamePreviewRootPnl.getParent().validate();
      baseFlamePreviewRootPnl.repaint();
      baseFlamePreviewPanel.requestFocusInWindow();
    }
    return baseFlamePreviewPanel;
  }

  private void refreshBaseFlamePreview() {
    FlamePanel imgPanel = getBaseFlamePreviewPanel();
    Rectangle bounds = imgPanel.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
      IFlamesFunc iflame = getIFlamesFunc();

      String flameXML = iflame != null ? iflame.getFlameParams(getCurrFlameIndex()).getFlameXML() : null;
      Flame flame = flameXML != null && flameXML.length() > 0 ? new FlameReader(prefs).readFlamesfromXML(flameXML).get(0) : null;
      if (flame != null) {
        double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
        double oldSampleDensity = flame.getSampleDensity();
        try {
          double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
          double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(info.getImageWidth());
          flame.setHeight(info.getImageHeight());

          FlameRenderer renderer = new FlameRenderer(flame, prefs, false, false);
          renderer.setProgressUpdater(null);
          flame.setSampleDensity(1.0);
          flame.setSpatialFilterRadius(0.0);
          RenderedFlame res = renderer.renderFlame(info);
          imgPanel.setImage(res.getImage());
        }
        finally {
          flame.setSpatialFilterRadius(oldSpatialFilterRadius);
          flame.setSampleDensity(oldSampleDensity);
        }
      }
    }
    else {
      imgPanel.setImage(new SimpleImage(width, height));
    }
    baseFlamePreviewRootPnl.repaint();
  }

  private void refreshImageFields() {
    // TODO Auto-generated method stub
    IFlamesFunc iflame = getIFlamesFunc();
    if (iflame == null) {
      edgesNorthButton.setSelected(false);
      edgesWestButton.setSelected(false);
      edgesEastButton.setSelected(false);
      edgesSouthButton.setSelected(false);
      erodeButton.setSelected(false);
      erodeSizeField.setValue(0.0);
      maxImageWidthField.setValue(0.0);
      structureThresholdField.setValue(0.0);
      structureDensityField.setValue(0.0);
      globalScaleXField.setValue(0.0);
      globalScaleYField.setValue(0.0);
      globalScaleZField.setValue(0.0);
      globalOffsetXField.setValue(0.0);
      globalOffsetYField.setValue(0.0);
      globalOffsetZField.setValue(0.0);
      shapeDistributionCmb.setSelectedIndex(-1);
    }
    else {
      edgesNorthButton.setSelected(iflame.getImageParams().getConv_north() == 1);
      edgesWestButton.setSelected(iflame.getImageParams().getConv_west() == 1);
      edgesEastButton.setSelected(iflame.getImageParams().getConv_east() == 1);
      edgesSouthButton.setSelected(iflame.getImageParams().getConv_south() == 1);
      erodeButton.setSelected(iflame.getImageParams().getErode() == 1);
      erodeSizeField.setValue(iflame.getImageParams().getErodeSize());
      maxImageWidthField.setValue(iflame.getImageParams().getMaxImgWidth());
      structureThresholdField.setValue(iflame.getImageParams().getStructure_threshold());
      structureDensityField.setValue(iflame.getImageParams().getStructure_density());
      globalScaleXField.setValue(iflame.getImageParams().getScaleX());
      globalScaleYField.setValue(iflame.getImageParams().getScaleY());
      globalScaleZField.setValue(iflame.getImageParams().getScaleZ());
      globalOffsetXField.setValue(iflame.getImageParams().getOffsetX());
      globalOffsetYField.setValue(iflame.getImageParams().getOffsetY());
      globalOffsetZField.setValue(iflame.getImageParams().getOffsetZ());
      shapeDistributionCmb.setSelectedItem(iflame.getImageParams().getShape_distribution());
    }
  }

  private void refreshBaseFlameFields() {
    // TODO Auto-generated method stub
    IFlamesFunc iflame = getIFlamesFunc();
    if (iflame == null) {

    }
    else {

    }
  }

  public void loadIFlameFromClipboardButton_clicked() {
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          List<Flame> flames = new FlameReader(prefs).readFlamesfromXML(xml);
          Flame flame = flames.get(0);
          importFlame(flame);
          messageHelper.showStatusMessage(getFlame(), "opened from clipboard");
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void loadIFlameButton_clicked() {
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
      if (chooser.showOpenDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
        Flame flame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        importFlame(flame);
        getFlame().setLastFilename(file.getName());
        messageHelper.showStatusMessage(getFlame(), "opened from disc");
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void importFlame(Flame pFlame) {
    setFlame(pFlame);
    undoManager.initUndoStack(getFlame());
    setupProfiles(getFlame());
    refreshUI();
    enableControls();
  }

  public void saveIFlameToClipboardButton_clicked() {
    try {
      if (getFlame() != null) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String xml = new FlameWriter().getFlameXML(getFlame());
        StringSelection data = new StringSelection(xml);
        clipboard.setContents(data, data);
        messageHelper.showStatusMessage(getFlame(), "flame saved to clipboard");
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void saveIFlameButton_clicked() {
    try {
      if (getFlame() != null) {
        JFileChooser chooser = new FlameFileChooser(prefs);
        if (prefs.getOutputFlamePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputFlamePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(centerPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          new FlameWriter().writeFlame(getFlame(), file.getAbsolutePath());
          getFlame().setLastFilename(file.getName());
          messageHelper.showStatusMessage(getFlame(), "flame saved to disc");
          prefs.setLastOutputFlameFile(file);
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void setupProfiles(Flame pFlame) {
    if (prefs.isTinaAssociateProfilesWithFlames()) {
      if (pFlame.getResolutionProfile() != null) {
        ResolutionProfile profile = null;
        for (int i = 0; i < resolutionProfileCmb.getItemCount(); i++) {
          profile = (ResolutionProfile) resolutionProfileCmb.getItemAt(i);
          if (pFlame.getResolutionProfile().equals(profile.toString()))
            break;
          else
            profile = null;
        }
        if (profile != null) {
          resolutionProfileCmb.setSelectedItem(profile);
        }
      }
    }
  }

  @Override
  public JProgressBar getRenderProgressBar() {
    return mainProgressBar;
  }

  public void edgesNorthButton_clicked() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setConv_north(edgesNorthButton.isSelected() ? 1 : 0);
    refreshIFlame();
  }

  public void edgesEastButton_clicked() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setConv_east(edgesNorthButton.isSelected() ? 1 : 0);
    refreshIFlame();
  }

  public void edgesSouthButton_clicked() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setConv_south(edgesSouthButton.isSelected() ? 1 : 0);
    refreshIFlame();
  }

  public void edgesWestButton_clicked() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setConv_west(edgesWestButton.isSelected() ? 1 : 0);
    refreshIFlame();
  }

  public void erodeButton_clicked() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setErode(erodeButton.isSelected() ? 1 : 0);
    refreshIFlame();
    enableControls();
  }

  public void displayPreprocessedImageButton_clicked() {
    refreshPreview();
  }

  public void refreshIFlameButton_clicked() {
    RessourceManager.clearRessources(IFlamesFunc.KEY_PREFIX);
    refreshPreview();
  }

  public void erodeSizeField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setErodeSize(erodeSizeField.getIntValue());
    refreshIFlame();
    enableControls();
  }

  public void maxImageWidthField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setMaxImgWidth(maxImageWidthField.getIntValue());
    refreshIFlame();
    enableControls();
  }

  public void shapeDistributionCmb_changed() {
    if (noRefresh || getIFlamesFunc() == null) {
      return;
    }
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setShape_distribution((ShapeDistribution) shapeDistributionCmb.getSelectedItem());
    refreshIFlame();
    enableControls();
  }

  public void structureThresholdField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setStructure_threshold(structureThresholdField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void structureDensityField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setStructure_density(structureDensityField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalScaleXField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setScaleX(globalScaleXField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalScaleYField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setScaleY(globalScaleYField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalScaleZField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setScaleZ(globalScaleZField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalOffsetXField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setOffsetX(globalOffsetXField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalOffsetYField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setOffsetY(globalOffsetYField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

  public void globalOffsetZField_changed() {
    saveUndoPoint();
    getIFlamesFunc().getImageParams().setOffsetZ(globalOffsetZField.getDoubleValue());
    refreshIFlame();
    enableControls();
  }

}
