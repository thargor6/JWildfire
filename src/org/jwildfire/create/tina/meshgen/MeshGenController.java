/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.meshgen;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Shading;
import org.jwildfire.create.tina.base.Stereo3dMode;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.FlameFileChooser;
import org.jwildfire.create.tina.swing.FlameHolder;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.create.tina.variation.InternalSliceRangeIndicatorWFFunc;
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;

public class MeshGenController {
  private static final double Z_SCALE = 10000.0;
  private static final double CENTRE_SCALE = 5000.0;
  private static final double ZOOM_SCALE = 1000.0;
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JTabbedPane rootTabbedPane;
  private Flame currStrippedOrigFlame;
  private Flame currBaseFlame;
  private boolean refreshing;

  private final JButton fromEditorBtn;
  private final JButton fromClipboardBtn;
  private final JButton loadFlameBtn;
  private final JWFNumberField sliceCountREd;
  private final JWFNumberField slicesPerRenderREd;
  private final JWFNumberField renderWidthREd;
  private final JWFNumberField renderHeightREd;
  private final JWFNumberField renderQualityREd;
  private final JProgressBar progressbar;
  private final JButton generateBtn;
  private final JPanel topViewRootPnl;
  private final JPanel frontViewRootPnl;
  private final JPanel perspectiveViewRootPnl;
  private final JTextPane hintPane;
  private final ProgressUpdater progressUpdater;
  private final JWFNumberField centreXREd;
  private final JSlider centreXSlider;
  private final JWFNumberField centreYREd;
  private final JSlider centreYSlider;
  private final JWFNumberField zoomREd;
  private final JSlider zoomSlider;
  private final JWFNumberField zminREd;
  private final JSlider zminSlider;
  private final JWFNumberField zmaxREd;
  private final JSlider zmaxSlider;
  private final JButton topViewRenderBtn;
  private final JButton frontViewRenderBtn;
  private final JButton perspectiveViewRenderBtn;
  private final JButton topViewToEditorBtn;

  public MeshGenController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JTabbedPane pRootTabbedPane,
      JButton pFromEditorBtn, JButton pFromClipboardBtn, JButton pLoadFlameBtn, JWFNumberField pSliceCountREd,
      JWFNumberField pSlicesPerRenderREd, JWFNumberField pRenderWidthREd, JWFNumberField pRenderHeightREd,
      JWFNumberField pRenderQualityREd, JProgressBar pProgressbar, JButton pGenerateBtn, JPanel pTopViewRootPnl,
      JPanel pFrontViewRootPnl, JPanel pPerspectiveViewRootPnl, JTextPane pHintPane, JWFNumberField pCentreXREd,
      JSlider pCentreXSlider, JWFNumberField pCentreYREd, JSlider pCentreYSlider, JWFNumberField pZoomREd,
      JSlider pZoomSlider, JWFNumberField pZMinREd, JSlider pZMinSlider, JWFNumberField pZMaxREd,
      JSlider pZMaxSlider, JButton pTopViewRenderBtn, JButton pFrontViewRenderBtn, JButton pPerspectiveViewRenderBtn,
      JButton pTopViewToEditorBtn) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootTabbedPane = pRootTabbedPane;

    fromEditorBtn = pFromEditorBtn;
    fromClipboardBtn = pFromClipboardBtn;
    loadFlameBtn = pLoadFlameBtn;
    sliceCountREd = pSliceCountREd;
    slicesPerRenderREd = pSlicesPerRenderREd;
    renderWidthREd = pRenderWidthREd;
    renderHeightREd = pRenderHeightREd;
    renderQualityREd = pRenderQualityREd;
    progressbar = pProgressbar;
    generateBtn = pGenerateBtn;
    topViewRootPnl = pTopViewRootPnl;
    frontViewRootPnl = pFrontViewRootPnl;
    perspectiveViewRootPnl = pPerspectiveViewRootPnl;
    hintPane = pHintPane;
    progressUpdater = new MeshGenProgressUpdater(progressbar);
    centreXREd = pCentreXREd;
    centreXSlider = pCentreXSlider;
    centreYREd = pCentreYREd;
    centreYSlider = pCentreYSlider;
    zoomREd = pZoomREd;
    zoomSlider = pZoomSlider;
    zminREd = pZMinREd;
    zminSlider = pZMinSlider;
    zmaxREd = pZMaxREd;
    zmaxSlider = pZMaxSlider;
    topViewRenderBtn = pTopViewRenderBtn;
    frontViewRenderBtn = pFrontViewRenderBtn;
    perspectiveViewRenderBtn = pPerspectiveViewRenderBtn;
    topViewToEditorBtn = pTopViewToEditorBtn;

    initHintsPane();
    setDefaults();
  }

  private void setDefaults() {
    refreshing = true;
    try {
      sliceCountREd.setValue(512);
      slicesPerRenderREd.setValue(32);
      renderWidthREd.setValue(512);
      renderHeightREd.setValue(512);
      renderQualityREd.setValue(300);

      setZMinValue(0.0);
      setZMaxValue(0.5);
      setCentreXValue(0.0);
      setCentreYValue(0.0);
      setZoomValue(1.0);
    }
    finally {
      refreshing = false;
    }
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

  public void refreshFlameImage(boolean pQuickRender, JPanel pRootPanel, FlameHolder pFlameHolder) {
    if (!refreshing) {
      FlamePanel imgPanel = getFlamePanel(pRootPanel, pFlameHolder);
      Rectangle bounds = imgPanel.getImageBounds();
      int width = bounds.width;
      int height = bounds.height;
      if (width >= 16 && height >= 16) {
        RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
        Flame flame = pFlameHolder.getFlame();
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
            if (pQuickRender) {
              renderer.setProgressUpdater(null);
              flame.setSampleDensity(3.0);
              flame.setSpatialFilterRadius(0.0);
            }
            else {
              renderer.setProgressUpdater(progressUpdater);
              flame.setSampleDensity(prefs.getTinaRenderPreviewQuality());
            }
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
      pRootPanel.repaint();
    }
  }

  private Map<JPanel, FlamePanel> flamepanels = new HashMap<JPanel, FlamePanel>();

  private FlamePanel getFlamePanel(JPanel pRootPanel, FlameHolder pFlameHolder) {
    FlamePanel flamePanel = flamepanels.get(pRootPanel);
    if (flamePanel == null) {
      int width = pRootPanel.getWidth();
      int height = pRootPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(prefs, img, 0, 0, pRootPanel.getWidth(), pFlameHolder, null);
      flamePanel.setRenderWidth(renderWidthREd.getIntValue());
      flamePanel.setRenderHeight(renderHeightREd.getIntValue());
      flamePanel.setFocusable(true);
      pRootPanel.add(flamePanel, BorderLayout.CENTER);
      pRootPanel.getParent().validate();
      pRootPanel.repaint();
      flamePanel.requestFocusInWindow();
      flamepanels.put(pRootPanel, flamePanel);
    }
    return flamePanel;
  }

  private void removeFlamePanel(JPanel pRootPanel) {
    FlamePanel flamePanel = flamepanels.get(pRootPanel);
    if (flamePanel != null) {
      pRootPanel.remove(flamePanel);
      flamepanels.remove(pRootPanel);
    }
  }

  private abstract class PreviewFlameHolder implements FlameHolder {
    protected abstract Flame createPreviewFlame(Flame pBaseFlame);

    @Override
    public Flame getFlame() {
      if (currBaseFlame != null) {
        Flame res = createPreviewFlame(currBaseFlame);
        return res;
      }
      return null;
    }

    protected void setGradient(Flame pFlame) {
      RGBPalette gradient = new RGBPalette();
      for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
        gradient.setColor(i, 160, 160, 160);
      }
      pFlame.getFirstLayer().setPalette(gradient);
    }

    protected void addSliceVariation(Flame pFlame, boolean pWithRandomFill) {
      InternalSliceRangeIndicatorWFFunc sliceVar = new InternalSliceRangeIndicatorWFFunc();
      if (pFlame.getFirstLayer().getFinalXForms().size() == 0) {
        XForm xform = new XForm();
        xform.addVariation(1.0, new Linear3DFunc());
        xform.addVariation(1.0, sliceVar);
        pFlame.getFirstLayer().getFinalXForms().add(xform);
      }
      else {
        pFlame.getFirstLayer().getFinalXForms().get(pFlame.getFirstLayer().getFinalXForms().size() - 1).addVariation(1.0, sliceVar);
      }
      sliceVar.setParameter("thickness", (zmaxREd.getDoubleValue() - zminREd.getDoubleValue()) / sliceCountREd.getDoubleValue());
      sliceVar.setParameter("position_1", zminREd.getDoubleValue());
      sliceVar.setParameter("dc_red_1", 2450);
      sliceVar.setParameter("dc_green_1", 250);
      sliceVar.setParameter("dc_blue_1", 42);
      sliceVar.setParameter("position_2", zmaxREd.getDoubleValue());
      sliceVar.setParameter("dc_red_2", 60);
      sliceVar.setParameter("dc_green_2", 2450);
      sliceVar.setParameter("dc_blue_2", 2450);
      sliceVar.setParameter("random_fill", pWithRandomFill ? 1 : 0);
    }
  }

  private FlameHolder topViewFlameHolder = new PreviewFlameHolder() {

    @Override
    protected Flame createPreviewFlame(Flame pBaseFlame) {
      Flame res = pBaseFlame.makeCopy();
      setGradient(res);
      addSliceVariation(res, false);
      return res;
    }

  };

  private FlameHolder frontViewFlameHolder = new PreviewFlameHolder() {

    @Override
    protected Flame createPreviewFlame(Flame pBaseFlame) {
      Flame res = pBaseFlame.makeCopy();
      addSliceVariation(res, true);
      setGradient(res);
      res.setCamPitch(90.0);
      return res;
    }

  };

  private FlameHolder perspectiveViewFlameHolder = new PreviewFlameHolder() {

    @Override
    protected Flame createPreviewFlame(Flame pBaseFlame) {
      Flame res = pBaseFlame.makeCopy();
      setGradient(res);
      res.setCamPitch(60.0);
      res.setCamYaw(30.0);
      res.setCamPerspective(0.20);
      addSliceVariation(res, false);
      return res;
    }
  };

  private void refreshAllPreviews(boolean pQuickRender) {
    refreshFlameImage(pQuickRender, topViewRootPnl, topViewFlameHolder);
    refreshFlameImage(pQuickRender, frontViewRootPnl, frontViewFlameHolder);
    refreshFlameImage(pQuickRender, perspectiveViewRootPnl, perspectiveViewFlameHolder);
  }

  public void fromEditorButton_clicked() {
    try {
      Flame newFlame = tinaController.exportFlame();
      if (newFlame != null) {
        importFlame(newFlame);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void fromClipboardButton_clicked() {
    Flame newFlame = null;
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          List<Flame> flames = new FlameReader(prefs).readFlamesfromXML(xml);
          if (flames.size() > 0) {
            newFlame = flames.get(0);
          }
        }
      }
      if (newFlame == null) {
        throw new Exception("There is currently no valid flame in the clipboard");
      }
      else {
        importFlame(newFlame);
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
      if (chooser.showOpenDialog(rootTabbedPane) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
        Flame newFlame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        importFlame(newFlame);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlame(Flame pNewFlame) {
    if (pNewFlame.getLayers().size() > 1) {
      throw new RuntimeException("Only flames with one layer are supported. But you could create a mesh of each layer separately and then use all those objects in your 3d-package");
    }
    currStrippedOrigFlame = stripFlame(pNewFlame);
    currBaseFlame = createBaseFlame(currStrippedOrigFlame);
    refreshing = true;
    try {
      setCentreXValue(currBaseFlame.getCentreX());
      setCentreYValue(currBaseFlame.getCentreY());
      setZoomValue(currBaseFlame.getCamZoom());
      setZMinValue(0.0);
      setZMaxValue(1.0);
    }
    finally {
      refreshing = false;
    }
    enableControls();
    refreshAllPreviews(true);
  }

  protected Flame stripFlame(Flame pFlame) {
    Flame res = pFlame.makeCopy();
    res = AnimationService.disableMotionCurves(res);
    res.setCamDOF(0.0);
    res.setDimishZ(0.0);
    res.setCamPerspective(0.0);
    res.setCamPitch(0.0);
    res.setCamRoll(0.0);
    res.setCamYaw(0.0);
    res.setCamZ(0.0);
    res.setCamPosX(0.0);
    res.setCamPosY(0.0);
    res.setCamPosZ(0.0);
    res.setStereo3dMode(Stereo3dMode.NONE);
    res.setBGColorRed(0);
    res.setBGColorGreen(0);
    res.setBGColorBlue(0);
    res.setBGTransparency(false);
    res.getShadingInfo().setShading(Shading.FLAT);
    res.setGamma(3.0);
    // hack to force 3d-projection mode to be on
    res.setCamZ(MathLib.EPSILON * 10);
    return res;
  }

  protected Flame createBaseFlame(Flame pFlame) {
    Flame res = pFlame.makeCopy();

    RGBPalette gradient = new RGBPalette();
    for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
      gradient.setColor(i, 225, 225, 225);
    }
    res.getFirstLayer().setPalette(gradient);
    res.setAntialiasAmount(MeshGenGenerateThread.DFLT_ANTIALIAS_AMOUNT);
    res.setAntialiasRadius(MeshGenGenerateThread.DFLT_ANTIALIAS_RADIUS);
    return res;
  }

  public void renderWidth_changed() {
    removeFlamePanel(topViewRootPnl);
    removeFlamePanel(frontViewRootPnl);
    removeFlamePanel(perspectiveViewRootPnl);
    refreshAllPreviews(true);
  }

  public void renderHeight_changed() {
    renderWidth_changed();
  }

  private void setZMinValue(double pValue) {
    zminREd.setValue(pValue);
    zminSlider.setValue(Tools.FTOI(Z_SCALE * pValue));
  }

  private void setZMaxValue(double pValue) {
    zmaxREd.setValue(pValue);
    zmaxSlider.setValue(Tools.FTOI(Z_SCALE * pValue));
  }

  private void setCentreXValue(double pValue) {
    centreXREd.setValue(pValue);
    centreXSlider.setValue(Tools.FTOI(CENTRE_SCALE * pValue));
  }

  private void setCentreYValue(double pValue) {
    centreYREd.setValue(pValue);
    centreYSlider.setValue(Tools.FTOI(CENTRE_SCALE * pValue));
  }

  private void setZoomValue(double pValue) {
    zoomREd.setValue(pValue);
    zoomSlider.setValue(Tools.FTOI(ZOOM_SCALE * pValue));
  }

  public void centreXREd_changed() {
    if (!refreshing && currBaseFlame != null) {
      currBaseFlame.setCentreX(centreXREd.getDoubleValue());
      refreshing = true;
      try {
        setCentreXValue(currBaseFlame.getCentreX());
      }
      finally {
        refreshing = false;
      }
      refreshAllPreviews(true);
    }
  }

  public void centreXSlider_changed() {
    if (!refreshing && currBaseFlame != null) {
      currBaseFlame.setCentreX((double) centreXSlider.getValue() / (double) CENTRE_SCALE);
      refreshing = true;
      try {
        setCentreXValue(currBaseFlame.getCentreX());
      }
      finally {
        refreshing = false;
      }
      refreshAllPreviews(true);
    }
  }

  public void centreYREd_changed() {
    if (!refreshing && currBaseFlame != null) {
      currBaseFlame.setCentreY(centreYREd.getDoubleValue());
      refreshing = true;
      try {
        setCentreYValue(currBaseFlame.getCentreY());
      }
      finally {
        refreshing = false;
      }
      refreshAllPreviews(true);
    }
  }

  public void centreYSlider_changed() {
    if (!refreshing && currBaseFlame != null) {
      currBaseFlame.setCentreY((double) centreYSlider.getValue() / (double) CENTRE_SCALE);
      refreshing = true;
      try {
        setCentreYValue(currBaseFlame.getCentreY());
      }
      finally {
        refreshing = false;
      }
      refreshAllPreviews(true);
    }
  }

  public void zoomREd_changed() {
    if (!refreshing && currBaseFlame != null) {
      currBaseFlame.setCamZoom(zoomREd.getDoubleValue());
      refreshing = true;
      try {
        setZoomValue(currBaseFlame.getCamZoom());
      }
      finally {
        refreshing = false;
      }
      refreshAllPreviews(true);
    }
  }

  public void zoomSlider_changed() {
    if (!refreshing && currBaseFlame != null) {
      currBaseFlame.setCamZoom((double) zoomSlider.getValue() / (double) ZOOM_SCALE);
      refreshing = true;
      try {
        setZoomValue(currBaseFlame.getCamZoom());
      }
      finally {
        refreshing = false;
      }
      refreshAllPreviews(true);
    }
  }

  public void zminREd_changed() {
    if (!refreshing && currBaseFlame != null) {
      refreshing = true;
      try {
        setZMinValue(zminREd.getDoubleValue());
      }
      finally {
        refreshing = false;
      }
      refreshAllPreviews(true);
    }
  }

  public void zminSlider_changed() {
    if (!refreshing && currBaseFlame != null) {
      refreshing = true;
      try {
        setZMinValue((double) zminSlider.getValue() / (double) Z_SCALE);
      }
      finally {
        refreshing = false;
      }
      refreshAllPreviews(true);
    }
  }

  public void zmaxREd_changed() {
    if (!refreshing && currBaseFlame != null) {
      refreshing = true;
      try {
        setZMaxValue(zmaxREd.getDoubleValue());
      }
      finally {
        refreshing = false;
      }
      refreshAllPreviews(true);
    }
  }

  public void zmaxSlider_changed() {
    if (!refreshing && currBaseFlame != null) {
      refreshing = true;
      try {
        setZMaxValue((double) zmaxSlider.getValue() / (double) Z_SCALE);
      }
      finally {
        refreshing = false;
      }
      refreshAllPreviews(true);
    }
  }

  public void topViewRenderButtonClicked() {
    refreshFlameImage(false, topViewRootPnl, topViewFlameHolder);
  }

  public void frontViewRenderButtonClicked() {
    refreshFlameImage(false, frontViewRootPnl, frontViewFlameHolder);
  }

  public void perspectiveViewRenderButtonClicked() {
    refreshFlameImage(false, perspectiveViewRootPnl, perspectiveViewFlameHolder);
  }

  public void enableControls() {
    generateBtn.setEnabled(currBaseFlame != null);
    centreXREd.setEnabled(currBaseFlame != null);
    centreXSlider.setEnabled(currBaseFlame != null);
    centreYREd.setEnabled(currBaseFlame != null);
    centreYSlider.setEnabled(currBaseFlame != null);
    zoomREd.setEnabled(currBaseFlame != null);
    zoomSlider.setEnabled(currBaseFlame != null);
    zminREd.setEnabled(currBaseFlame != null);
    zminSlider.setEnabled(currBaseFlame != null);
    zmaxREd.setEnabled(currBaseFlame != null);
    zmaxSlider.setEnabled(currBaseFlame != null);
    topViewRenderBtn.setEnabled(currBaseFlame != null);
    frontViewRenderBtn.setEnabled(currBaseFlame != null);
    perspectiveViewRenderBtn.setEnabled(currBaseFlame != null);
    topViewToEditorBtn.setEnabled(currBaseFlame != null);
  }

  private MeshGenGenerateThread mainRenderThread = null;

  private void enableMainRenderControls() {
    generateBtn.setText(mainRenderThread == null ? "Generate" : "Cancel");
  }

  public void generateButton_clicked() {
    if (mainRenderThread != null) {
      mainRenderThread.setForceAbort();
      while (mainRenderThread.isFinished()) {
        try {
          Thread.sleep(10);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      mainRenderThread = null;
      enableMainRenderControls();
    }
    else if (currBaseFlame != null) {
      try {
        JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
        if (prefs.getOutputImagePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(rootTabbedPane) == JFileChooser.APPROVE_OPTION) {
          final File file = chooser.getSelectedFile();
          prefs.setLastOutputImageFile(file);

          MeshGenGenerateThreadFinishEvent finishEvent = new MeshGenGenerateThreadFinishEvent() {

            @Override
            public void succeeded(double pElapsedTime) {
              try {
                tinaController.showStatusMessage(currBaseFlame, "render time: " + Tools.doubleToString(pElapsedTime) + "s");
              }
              catch (Throwable ex) {
                errorHandler.handleError(ex);
              }
              mainRenderThread = null;
              enableMainRenderControls();
            }

            @Override
            public void failed(Throwable exception) {
              errorHandler.handleError(exception);
              mainRenderThread = null;
              enableMainRenderControls();
            }

          };
          Flame flame = currBaseFlame.makeCopy();

          mainRenderThread = new MeshGenGenerateThread(
              prefs, flame, file, finishEvent, progressUpdater, renderWidthREd.getIntValue(), renderHeightREd.getIntValue(),
              sliceCountREd.getIntValue(), slicesPerRenderREd.getIntValue(), renderQualityREd.getIntValue(), zminREd.getDoubleValue(),
              zmaxREd.getDoubleValue());

          enableMainRenderControls();
          new Thread(mainRenderThread).start();
        }
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void sliceCount_changed() {
    refreshAllPreviews(true);
  }

  public void topViewFlameToEditorBtn_clicked() {
    if (currStrippedOrigFlame != null) {
      currStrippedOrigFlame.setCentreX(centreXREd.getDoubleValue());
      currStrippedOrigFlame.setCentreY(centreYREd.getDoubleValue());
      currStrippedOrigFlame.setCamZoom(zoomREd.getDoubleValue());
      tinaController.importFlame(currStrippedOrigFlame, true);
      tinaController.getRootTabbedPane().setSelectedIndex(0);
    }
  }

}
