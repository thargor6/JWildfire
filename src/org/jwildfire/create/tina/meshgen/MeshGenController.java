/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextPane;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.eden.sunflow.ExampleScenes;
import org.jwildfire.create.eden.sunflow.SunflowSceneBuilder;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.BGColorType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Stereo3dMode;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.raster.RasterPointCloud;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.meshgen.filter.PreFilter;
import org.jwildfire.create.tina.meshgen.filter.PreFilterType;
import org.jwildfire.create.tina.meshgen.marchingcubes.Mesh;
import org.jwildfire.create.tina.meshgen.marchingcubes.MeshPreviewRenderer;
import org.jwildfire.create.tina.meshgen.render.MeshGenRenderOutputType;
import org.jwildfire.create.tina.meshgen.render.MeshGenRenderThread;
import org.jwildfire.create.tina.meshgen.render.RenderPointCloudThread;
import org.jwildfire.create.tina.meshgen.render.RenderSlicesThread;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.*;
import org.jwildfire.create.tina.swing.MainEditorFrame;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.create.tina.variation.InternalSliceRangeIndicatorWFFunc;
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.swing.PointCloudOutputFileChooser;

public class MeshGenController {
  private static final double Z_SCALE = 10000.0;
  private static final double CENTRE_SCALE = 5000.0;
  private static final double ZOOM_SCALE = 1000.0;
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
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
  private final JProgressBar renderSequenceProgressbar;
  private final JButton generateBtn;
  private final JPanel topViewRootPnl;
  private final JPanel frontViewRootPnl;
  private final JPanel perspectiveViewRootPnl;
  private final JTextPane hintPane;
  private final ProgressUpdater renderSequenceProgressUpdater;
  private final ProgressUpdater generateMeshProgressUpdater;
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
  private final JButton loadSequenceBtn;
  private final JWFNumberField meshGenCellSizeREd;
  private final JButton topViewRenderBtn;
  private final JButton frontViewRenderBtn;
  private final JButton perspectiveViewRenderBtn;
  private final JButton topViewToEditorBtn;
  private final JButton sequenceFromRendererBtn;
  private final JWFNumberField sequenceWidthREd;
  private final JWFNumberField sequenceHeightREd;
  private final JWFNumberField sequenceSlicesREd;
  private final JWFNumberField sequenceDownSampleREd;
  private final JWFNumberField sequenceFilterRadiusREd;
  private final JProgressBar generateMeshProgressbar;
  private final JButton generateMeshBtn;
  private final JWFNumberField sequenceThresholdREd;
  private final JLabel sequenceLbl;
  private final JPanel previewRootPanel;
  private final JCheckBox autoPreviewCBx;
  private final JButton previewImportLastGeneratedMeshBtn;
  private final JButton previewImportFromFileBtn;
  private final JButton clearPreviewBtn;
  private final JWFNumberField previewPositionXREd;
  private final JWFNumberField previewPositionYREd;
  private final JWFNumberField previewSizeREd;
  private final JWFNumberField previewScaleZREd;
  private final JWFNumberField previewRotateAlphaREd;
  private final JWFNumberField previewRotateBetaREd;
  private final JWFNumberField previewPointsREd;
  private final JWFNumberField previewPolygonsREd;
  private final JButton previewRefreshBtn;
  private final JButton previewSunflowExportBtn;
  private final JComboBox preFilter1Cmb;
  private final JComboBox preFilter2Cmb;
  private final JWFNumberField imageStepREd;
  private final JComboBox outputTypeCmb;
  public final JCheckBox meshGenTaubinSmoothCbx;
  public final JWFNumberField meshGenSmoothPassesREd;
  public final JWFNumberField meshGenSmoothLambdaREd;
  public final JWFNumberField meshGenSmoothMuREd;

  private String currSequencePattern;
  private ImagePanel previewPanel;

  public MeshGenController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel,
      JButton pFromEditorBtn, JButton pFromClipboardBtn, JButton pLoadFlameBtn, JWFNumberField pSliceCountREd,
      JWFNumberField pSlicesPerRenderREd, JWFNumberField pRenderWidthREd, JWFNumberField pRenderHeightREd,
      JWFNumberField pRenderQualityREd, JProgressBar pRenderSequenceProgressbar, JButton pGenerateBtn, JPanel pTopViewRootPnl,
      JPanel pFrontViewRootPnl, JPanel pPerspectiveViewRootPnl, JTextPane pHintPane, JWFNumberField pCentreXREd,
      JSlider pCentreXSlider, JWFNumberField pCentreYREd, JSlider pCentreYSlider, JWFNumberField pZoomREd,
      JSlider pZoomSlider, JWFNumberField pZMinREd, JSlider pZMinSlider, JWFNumberField pZMaxREd,
      JSlider pZMaxSlider, JWFNumberField pMeshGenCellSizeREd, JButton pTopViewRenderBtn, JButton pFrontViewRenderBtn, JButton pPerspectiveViewRenderBtn,
      JButton pTopViewToEditorBtn, JButton pLoadSequenceBtn, JWFNumberField pSequenceWidthREd, JWFNumberField pSequenceHeightREd,
      JWFNumberField pSequenceSlicesREd, JWFNumberField pSequenceDownSampleREd, JWFNumberField pSequenceFilterRadiusREd,
      JProgressBar pGenerateMeshProgressbar, JButton pGenerateMeshBtn, JButton pSequenceFromRendererBtn,
      JWFNumberField pSequenceThresholdREd, JLabel pSequenceLbl, JPanel pPreviewRootPanel, JCheckBox pAutoPreviewCBx,
      JButton pPreviewImportLastGeneratedMeshBtn, JButton pPreviewImportFromFileBtn, JButton pClearPreviewBtn,
      JWFNumberField pPreviewPositionXREd, JWFNumberField pPreviewPositionYREd,
      JWFNumberField pPreviewSizeREd, JWFNumberField pPreviewScaleZREd, JWFNumberField pPreviewRotateAlphaREd,
      JWFNumberField pPreviewRotateBetaREd, JWFNumberField pPreviewPointsREd, JWFNumberField pPreviewPolygonsREd,
      JButton pRefreshPreviewBtn, JButton pPreviewSunflowExportBtn, JComboBox pPreFilter1Cmb, JComboBox pPreFilter2Cmb,
      JWFNumberField pImageStepREd, JComboBox pOutputTypeCmb, JCheckBox pMeshGenTaubinSmoothCbx, JWFNumberField pMeshGenSmoothPassesREd,
      JWFNumberField pMeshGenSmoothLambdaREd, JWFNumberField pMeshGenSmoothMuREd) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;

    fromEditorBtn = pFromEditorBtn;
    fromClipboardBtn = pFromClipboardBtn;
    loadFlameBtn = pLoadFlameBtn;
    sliceCountREd = pSliceCountREd;
    slicesPerRenderREd = pSlicesPerRenderREd;
    renderWidthREd = pRenderWidthREd;
    renderHeightREd = pRenderHeightREd;
    renderQualityREd = pRenderQualityREd;
    renderSequenceProgressbar = pRenderSequenceProgressbar;
    renderSequenceProgressUpdater = new MeshGenProgressUpdater(renderSequenceProgressbar);
    generateBtn = pGenerateBtn;
    topViewRootPnl = pTopViewRootPnl;
    frontViewRootPnl = pFrontViewRootPnl;
    perspectiveViewRootPnl = pPerspectiveViewRootPnl;
    hintPane = pHintPane;
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
    meshGenCellSizeREd = pMeshGenCellSizeREd;
    topViewRenderBtn = pTopViewRenderBtn;
    frontViewRenderBtn = pFrontViewRenderBtn;
    perspectiveViewRenderBtn = pPerspectiveViewRenderBtn;
    topViewToEditorBtn = pTopViewToEditorBtn;
    loadSequenceBtn = pLoadSequenceBtn;
    sequenceWidthREd = pSequenceWidthREd;
    sequenceHeightREd = pSequenceHeightREd;
    sequenceSlicesREd = pSequenceSlicesREd;
    sequenceDownSampleREd = pSequenceDownSampleREd;
    sequenceFilterRadiusREd = pSequenceFilterRadiusREd;
    generateMeshBtn = pGenerateMeshBtn;
    sequenceFromRendererBtn = pSequenceFromRendererBtn;
    generateMeshProgressbar = pGenerateMeshProgressbar;
    generateMeshProgressUpdater = new MeshGenProgressUpdater(generateMeshProgressbar);
    sequenceThresholdREd = pSequenceThresholdREd;
    sequenceLbl = pSequenceLbl;
    previewRootPanel = pPreviewRootPanel;
    autoPreviewCBx = pAutoPreviewCBx;
    previewImportLastGeneratedMeshBtn = pPreviewImportLastGeneratedMeshBtn;
    previewImportFromFileBtn = pPreviewImportFromFileBtn;
    clearPreviewBtn = pClearPreviewBtn;
    previewPositionXREd = pPreviewPositionXREd;
    previewPositionYREd = pPreviewPositionYREd;
    previewSizeREd = pPreviewSizeREd;
    previewScaleZREd = pPreviewScaleZREd;
    previewRotateAlphaREd = pPreviewRotateAlphaREd;
    previewRotateBetaREd = pPreviewRotateBetaREd;
    previewPointsREd = pPreviewPointsREd;
    previewPolygonsREd = pPreviewPolygonsREd;
    previewRefreshBtn = pRefreshPreviewBtn;
    previewSunflowExportBtn = pPreviewSunflowExportBtn;
    preFilter1Cmb = pPreFilter1Cmb;
    preFilter2Cmb = pPreFilter2Cmb;
    imageStepREd = pImageStepREd;
    outputTypeCmb = pOutputTypeCmb;
    meshGenTaubinSmoothCbx = pMeshGenTaubinSmoothCbx;
    meshGenSmoothPassesREd = pMeshGenSmoothPassesREd;
    meshGenSmoothLambdaREd = pMeshGenSmoothLambdaREd;
    meshGenSmoothMuREd = pMeshGenSmoothMuREd;

    initHintsPane();
    sequenceWidthREd.setEditable(false);
    sequenceHeightREd.setEditable(false);
    sequenceSlicesREd.setEditable(false);
    previewPointsREd.setEditable(false);
    previewPolygonsREd.setEditable(false);
    setDefaults();

    enableControls();
  }

  private void setDefaults() {
    refreshing = true;
    try {
      sliceCountREd.setValue(512);
      slicesPerRenderREd.setValue(32);
      renderWidthREd.setValue(512);
      renderHeightREd.setValue(512);
      renderQualityREd.setValue(300);
      preFilter1Cmb.setSelectedItem(PreFilterType.NONE);
      preFilter2Cmb.setSelectedItem(PreFilterType.NONE);

      sequenceDownSampleREd.setValue(2.0);
      sequenceFilterRadiusREd.setValue(0.25);
      sequenceThresholdREd.setValue(0);
      setCurrSequencePattern(null);

      previewPositionXREd.setValue(0.0);
      previewPositionYREd.setValue(0.0);
      previewSizeREd.setValue(1.0);
      previewScaleZREd.setValue(1.0);
      previewRotateAlphaREd.setValue(27.0);
      previewRotateBetaREd.setValue(56.0);
      imageStepREd.setValue(1);

      meshGenTaubinSmoothCbx.setSelected(false);
      //      meshGenSmoothPassesREd.setValue(12);
      //      meshGenSmoothLambdaREd.setValue(0.42);
      //      meshGenSmoothMuREd.setValue(-0.45);

      meshGenSmoothPassesREd.setValue(20);
      meshGenSmoothLambdaREd.setValue(0.84);
      meshGenSmoothMuREd.setValue(-0.90);

      setZMinValue(0.0);
      setZMaxValue(0.5);
      setCentreXValue(0.0);
      setCentreYValue(0.0);
      setZoomValue(1.0);
      setMaxOctreeCellSizeValue(RasterPointCloud.DFLT_MAX_OCTREE_CELL_SIZE);
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
              renderer.setProgressUpdater(renderSequenceProgressUpdater);
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
      flamePanel = new FlamePanel(prefs, img, 0, 0, pRootPanel.getWidth(), pFlameHolder, null, null);
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
      res.setCamBank(0.0);
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
      if (chooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
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
      setMaxOctreeCellSizeValue(RasterPointCloud.DFLT_MAX_OCTREE_CELL_SIZE);
    }
    finally {
      refreshing = false;
    }
    enableControls();
    zminREd_changed();
    //    refreshAllPreviews(true);
  }

  protected Flame stripFlame(Flame pFlame) {
    Flame res = pFlame.makeCopy();
    res = AnimationService.resetMotionCurves(res);
    res.setCamDOF(0.0);
    res.setDimishZ(0.0);
    res.setCamPerspective(0.0);
    res.setCamPitch(0.0);
    res.setCamRoll(0.0);
    res.setCamBank(0.0);
    res.setCamYaw(0.0);
    res.setCamBank(0.0);
    res.setCamZ(0.0);
    res.setCamPosX(0.0);
    res.setCamPosY(0.0);
    res.setCamPosZ(0.0);
    res.setStereo3dMode(Stereo3dMode.NONE);
    res.setBgColorType(BGColorType.SINGLE_COLOR);
    res.setBgColorRed(0);
    res.setBgColorGreen(0);
    res.setBgColorBlue(0);
    res.setBGTransparency(false);
    res.setGamma(3.0);
    // hack to force 3d-projection mode to be on
    res.setCamZ(MathLib.EPSILON * 10);
    return res;
  }

  protected Flame createBaseFlame(Flame pFlame) {
    Flame res = pFlame.makeCopy();
    /*
    RGBPalette gradient = new RGBPalette();
    for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
      gradient.setColor(i, 225, 225, 225);
    }
    res.getFirstLayer().setPalette(gradient);
    */
    res.getSolidRenderSettings().setSolidRenderingEnabled(false);
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

  private void setMaxOctreeCellSizeValue(double pValue) {
    meshGenCellSizeREd.setValue(pValue);
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
    boolean isRendering = renderSlicesThread != null || generateMeshThread != null;
    boolean hasFlame = currBaseFlame != null;
    generateBtn.setEnabled(hasFlame && generateMeshThread == null);
    generateMeshBtn.setEnabled(renderSlicesThread == null && sequenceSlicesREd.getIntValue() > 0);
    generateBtn.setText(renderSlicesThread == null ? "Generate" : "Cancel");
    generateMeshBtn.setText(generateMeshThread == null ? "Generate" : "Cancel");

    boolean pointCloudMode = isPointCloudMode();

    centreXREd.setEnabled(hasFlame && !isRendering);
    centreXSlider.setEnabled(hasFlame && !isRendering);
    centreYREd.setEnabled(hasFlame && !isRendering);
    centreYSlider.setEnabled(hasFlame && !isRendering);
    zoomREd.setEnabled(hasFlame && !isRendering);
    zoomSlider.setEnabled(hasFlame && !isRendering);
    zminREd.setEnabled(hasFlame && !isRendering);
    zminSlider.setEnabled(hasFlame && !isRendering);
    zmaxREd.setEnabled(hasFlame && !isRendering);
    zmaxSlider.setEnabled(hasFlame && !isRendering);
    meshGenCellSizeREd.setEnabled(hasFlame && !isRendering && pointCloudMode);

    topViewRenderBtn.setEnabled(hasFlame && !isRendering);
    frontViewRenderBtn.setEnabled(hasFlame && !isRendering);
    perspectiveViewRenderBtn.setEnabled(hasFlame && !isRendering);
    topViewToEditorBtn.setEnabled(hasFlame && !isRendering);
    fromEditorBtn.setEnabled(!isRendering);
    fromClipboardBtn.setEnabled(!isRendering);
    loadFlameBtn.setEnabled(!isRendering);
    sliceCountREd.setEnabled(hasFlame && !isRendering && !pointCloudMode);
    slicesPerRenderREd.setEnabled(hasFlame && !isRendering && !pointCloudMode);
    renderWidthREd.setEnabled(hasFlame && !isRendering);
    renderHeightREd.setEnabled(hasFlame && !isRendering);
    renderQualityREd.setEnabled(hasFlame && !isRendering);

    outputTypeCmb.setEnabled(hasFlame && !isRendering);

    preFilter1Cmb.setEnabled(!isRendering);
    preFilter2Cmb.setEnabled(!isRendering);
    imageStepREd.setEnabled(!isRendering);

    boolean smoothing = meshGenTaubinSmoothCbx.isSelected();
    meshGenSmoothPassesREd.setEnabled(smoothing);
    meshGenSmoothLambdaREd.setEnabled(smoothing);
    meshGenSmoothMuREd.setEnabled(smoothing);

    sequenceFromRendererBtn.setEnabled(lastRenderedSequenceOutFilePattern != null && !isRendering);
    loadSequenceBtn.setEnabled(!isRendering);
    sequenceDownSampleREd.setEnabled(!isRendering);
    sequenceFilterRadiusREd.setEnabled(!isRendering);
    sequenceThresholdREd.setEnabled(!isRendering);

    boolean hashMesh = currPreviewMesh != null;
    previewImportLastGeneratedMeshBtn.setEnabled(lastGeneratedMeshFilename != null);
    previewImportFromFileBtn.setEnabled(!isRendering);
    clearPreviewBtn.setEnabled(hashMesh);
    previewPositionXREd.setEnabled(hashMesh);
    previewPositionYREd.setEnabled(hashMesh);
    previewSizeREd.setEnabled(hashMesh);
    previewScaleZREd.setEnabled(hashMesh);
    previewRotateAlphaREd.setEnabled(hashMesh);
    previewRotateBetaREd.setEnabled(hashMesh);
    previewRefreshBtn.setEnabled(hashMesh);
    previewSunflowExportBtn.setEnabled(hashMesh);
  }

  private boolean isPointCloudMode() {
    return MeshGenRenderOutputType.POINTCLOUD.equals(outputTypeCmb.getSelectedItem());
  }

  private MeshGenRenderThread renderSlicesThread = null;
  private GenerateMeshThread generateMeshThread = null;
  private String lastRenderedSequenceOutFilePattern = null;
  private String lastGeneratedMeshFilename = null;
  private MeshPair currPreviewMesh = null;

  public void generateButton_clicked() {
    if (renderSlicesThread != null) {
      renderSlicesThread.setForceAbort();
      while (renderSlicesThread.isFinished()) {
        try {
          Thread.sleep(10);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      renderSlicesThread = null;
      enableControls();
    }
    else if (currBaseFlame != null) {
      try {
        JFileChooser chooser;
        if (isPointCloudMode()) {
          chooser = new PointCloudOutputFileChooser(Tools.FILEEXT_PLY);
          if (prefs.getTinaMeshPath() != null) {
            try {
              chooser.setCurrentDirectory(new File(prefs.getTinaMeshPath()));
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
        else {
          chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
          if (prefs.getOutputImagePath() != null) {
            try {
              chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
        if (chooser.showSaveDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
          final File file = chooser.getSelectedFile();
          prefs.setLastOutputImageFile(file);

          MeshGenGenerateThreadFinishEvent finishEvent = new MeshGenGenerateThreadFinishEvent() {

            @Override
            public void succeeded(double pElapsedTime) {
              try {
                tinaController.getMessageHelper().showStatusMessage(currBaseFlame, "render time: " + Tools.doubleToString(pElapsedTime) + "s");
              }
              catch (Throwable ex) {
                errorHandler.handleError(ex);
              }
              renderSlicesThread = null;
              enableControls();
            }

            @Override
            public void failed(Throwable exception) {
              errorHandler.handleError(exception);
              renderSlicesThread = null;
              enableControls();
            }

          };
          Flame flame = currBaseFlame.makeCopy();

          switch (getOutputType()) {
            case VOXELSTACK: {
              String outfilenamePattern = SequenceFilenameGen.createFilenamePattern(file);

              Flame grayFlame = flame.makeCopy();
              RGBPalette gradient = new RGBPalette();
              for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
                gradient.setColor(i, 225, 225, 225);
              }
              grayFlame.getFirstLayer().setPalette(gradient);

              renderSlicesThread = new RenderSlicesThread(
                  prefs, grayFlame, outfilenamePattern, finishEvent, renderSequenceProgressUpdater, renderWidthREd.getIntValue(), renderHeightREd.getIntValue(),
                  sliceCountREd.getIntValue(), slicesPerRenderREd.getIntValue(), renderQualityREd.getIntValue(), zminREd.getDoubleValue(),
                  zmaxREd.getDoubleValue());

              lastRenderedSequenceOutFilePattern = outfilenamePattern;
              break;
            }
            case POINTCLOUD: {
              renderSlicesThread = new RenderPointCloudThread(
                  prefs, flame, file.getAbsolutePath(), finishEvent, renderSequenceProgressUpdater, renderWidthREd.getIntValue(), renderHeightREd.getIntValue(),
                  renderQualityREd.getIntValue(), zminREd.getDoubleValue(), zmaxREd.getDoubleValue(), meshGenCellSizeREd.getDoubleValue());
              break;
            }
          }

          enableControls();
          new Thread(renderSlicesThread).start();
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
      tinaController.getDesktop().showJFrame(MainEditorFrame.class);
    }
  }

  public void importSequenceFromRendererButton_clicked() {
    importSequence(lastRenderedSequenceOutFilePattern);
  }

  public void loadSequenceButton_clicked() {
    JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
    if (prefs.getInputImagePath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getInputImagePath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    if (chooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      prefs.setLastInputImageFile(file);
      importSequence(SequenceFilenameGen.guessFilenamePattern(file));
    }
  }

  private void importSequence(String pFilenamePattern) {
    try {
      final int firstIndex = 1;
      int count = 0;
      String lastFilename = null;
      for (int i = firstIndex; i < Integer.MAX_VALUE; i++) {
        String filename = String.format(pFilenamePattern, i);
        if (filename.equals(lastFilename)) {
          break;
        }
        if (new File(filename).exists()) {
          count++;
        }
        else {
          break;
        }
        lastFilename = filename;
      }
      if (count > 0) {
        File first = new File(String.format(pFilenamePattern, firstIndex));
        SimpleImage img = new ImageReader(rootPanel).loadImage(first.getAbsolutePath());
        if (img != null) {
          sequenceWidthREd.setValue(img.getImageWidth());
          sequenceHeightREd.setValue(img.getImageHeight());
          sequenceSlicesREd.setValue(count);
          setCurrSequencePattern(pFilenamePattern);
        }
        enableControls();
      }
      else
        throw new Exception("An image sequence has to contain at least one image");
    }
    catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  public void generateMeshButton_clicked() {
    if (generateMeshThread != null) {
      generateMeshThread.setForceAbort();
      while (generateMeshThread.isFinished()) {
        try {
          Thread.sleep(10);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      generateMeshThread = null;
      enableControls();
    }
    else if (sequenceSlicesREd.getIntValue() > 0) {
      try {
        JFileChooser chooser = new MeshFileChooser(prefs);
        if (prefs.getTinaMeshPath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getTinaMeshPath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
          final File outFile = chooser.getSelectedFile();
          prefs.setLastMeshFile(outFile);

          MeshGenGenerateThreadFinishEvent finishEvent = new MeshGenGenerateThreadFinishEvent() {

            @Override
            public void succeeded(double pElapsedTime) {
              try {
                tinaController.getMessageHelper().showStatusMessage("Mesh creation time: " + Tools.doubleToString(pElapsedTime) + "s");
              }
              catch (Throwable ex) {
                errorHandler.handleError(ex);
              }
              lastGeneratedMeshFilename = outFile.getAbsolutePath();
              if (autoPreviewCBx.isEnabled()) {
                currPreviewMesh = new MeshPair(generateMeshThread.getMesh());
                refreshPreviewMeshInfo();
              }
              generateMeshThread = null;
              System.gc();
              enableControls();
              if (autoPreviewCBx.isEnabled()) {
                refreshPreview(false);
              }
            }

            @Override
            public void failed(Throwable exception) {
              errorHandler.handleError(exception);
              generateMeshThread = null;
              enableControls();
            }

          };

          generateMeshThread = new GenerateMeshThread(outFile.getAbsolutePath(), finishEvent, generateMeshProgressUpdater,
              getCurrSequencePattern(), sequenceSlicesREd.getIntValue(), imageStepREd.getIntValue(), sequenceThresholdREd.getIntValue(), sequenceFilterRadiusREd.getDoubleValue(),
              sequenceDownSampleREd.getIntValue(), true, getPreFilterList(), meshGenTaubinSmoothCbx.isSelected(), meshGenSmoothPassesREd.getIntValue(),
              meshGenSmoothLambdaREd.getDoubleValue(), meshGenSmoothMuREd.getDoubleValue());

          enableControls();
          new Thread(generateMeshThread).start();
        }
      }
      catch (Throwable ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  private List<PreFilter> getPreFilterList() {
    List<PreFilter> preFilterList = new ArrayList<PreFilter>();
    if (preFilter1Cmb.getSelectedItem() != null) {
      preFilterList.add(((PreFilterType) preFilter1Cmb.getSelectedItem()).getFilter());
    }
    if (preFilter2Cmb.getSelectedItem() != null) {
      preFilterList.add(((PreFilterType) preFilter2Cmb.getSelectedItem()).getFilter());
    }
    return preFilterList;
  }

  private void setCurrSequencePattern(String pPattern) {
    currSequencePattern = pPattern;
    if (pPattern == null) {
      sequenceLbl.setText("(none)");
    }
    else {
      sequenceLbl.setText(new File(currSequencePattern).getName());
    }
  }

  private String getCurrSequencePattern() {
    return currSequencePattern;
  }

  private ImagePanel getPreviewPanel() {
    if (previewPanel == null) {
      int width = previewRootPanel.getWidth();
      int height = previewRootPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);

      previewPanel = new ImagePanel(img, 0, 0, previewRootPanel.getWidth());
      previewRootPanel.add(previewPanel, BorderLayout.CENTER);
      previewRootPanel.getParent().validate();
      previewRootPanel.repaint();
    }
    return previewPanel;
  }

  protected void refreshPreview(boolean pFastPreview) {
    ImagePanel panel = getPreviewPanel();
    int width = panel.getWidth();
    int height = panel.getHeight();
    panel.setImage(renderPreviewImage(width, height, pFastPreview));
    previewRootPanel.repaint();
  }

  private SimpleImage renderPreviewImage(int pWidth, int pHeight, boolean pFastPreview) {
    if (currPreviewMesh == null) {
      return new SimpleImage(pWidth, pHeight);
    }
    Mesh mesh = pFastPreview ? currPreviewMesh.getReducedMesh() : currPreviewMesh.getMesh();
    return MeshPreviewRenderer.renderMesh(mesh, pWidth, pHeight,
        previewPositionXREd.getDoubleValue(), previewPositionYREd.getDoubleValue(),
        previewSizeREd.getDoubleValue(), previewScaleZREd.getDoubleValue(),
        previewRotateAlphaREd.getDoubleValue(), previewRotateBetaREd.getDoubleValue());
  }

  public void previewSize_changed(boolean pMouseAdjusting) {
    refreshPreview(pMouseAdjusting);
  }

  public void previewPositionX_changed(boolean pMouseAdjusting) {
    refreshPreview(pMouseAdjusting);
  }

  public void previewPositionY_changed(boolean pMouseAdjusting) {
    refreshPreview(pMouseAdjusting);
  }

  public void previewRotateAlpha_changed(boolean pMouseAdjusting) {
    refreshPreview(pMouseAdjusting);
  }

  public void previewRotateBeta_changed(boolean pMouseAdjusting) {
    refreshPreview(pMouseAdjusting);
  }

  public void previewScaleZ_changed(boolean pMouseAdjusting) {
    refreshPreview(pMouseAdjusting);
  }

  public void loadPreviewMeshBtn_clicked() {
    try {
      JFileChooser chooser = new MeshFileChooser(prefs);
      if (prefs.getTinaMeshPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getTinaMeshPath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        importPreviewMesh(file.getAbsolutePath());
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void importPreviewMesh(String pFilename) {
    Mesh mesh = SimpleWavefrontObjLoader.readMesh(pFilename);
    currPreviewMesh = new MeshPair(mesh);
    refreshPreviewMeshInfo();
    enableControls();
    refreshPreview(false);
  }

  public void importLastGeneratedMeshIntoPreviewBtn_clicked() {
    try {
      importPreviewMesh(lastGeneratedMeshFilename);
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void clearPreviewButton_clicked() {
    currPreviewMesh = null;
    refreshPreviewMeshInfo();
    System.gc();
    refreshPreview(false);
    enableControls();
  }

  private void refreshPreviewMeshInfo() {
    if (currPreviewMesh == null) {
      previewPointsREd.setValue(0);
      previewPolygonsREd.setValue(0);
    }
    else {
      previewPointsREd.setValue(currPreviewMesh.getMesh().getVertices().size());
      previewPolygonsREd.setValue(currPreviewMesh.getMesh().getFaces().size());
    }
  }

  public void refreshPreviewButton_clicked() {
    refreshPreview(false);
  }

  public void previewSunflowExportButton_clicked() {
    try {
      JFileChooser chooser = new SunflowSceneFileChooser(prefs);
      if (prefs.getSunflowScenePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getSunflowScenePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        exportMeshToSunflow(currPreviewMesh.getMesh(), file.getAbsolutePath());
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void exportMeshToSunflow(Mesh pMesh, String pFilename) {
    try {
      long t0 = System.currentTimeMillis();
      SunflowSceneBuilder scene = ExampleScenes.getExampleScene1();
      scene.addLegacyMesh()
          .withMesh(pMesh)
          .withName("generated mesh")
          .withShader(ExampleScenes.SHADER_SHINY);
      Tools.writeUTF8Textfile(pFilename, scene.getProduct());
      long t1 = System.currentTimeMillis();
      double elapsedTime = (t1 - t0) / 1000.0;
      tinaController.getMessageHelper().showStatusMessage(currBaseFlame, "Scene \"" + new File(pFilename).getName() + "\" exported, elapsed time: " + Tools.doubleToString(elapsedTime) + "s");
    }
    catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  public void outputTypeCmb_changed() {
    enableControls();
  }

  private MeshGenRenderOutputType getOutputType() {
    return (MeshGenRenderOutputType) outputTypeCmb.getSelectedItem();
  }

  public void taubinSmoothCbx_clicked() {
    enableControls();
  }

}
