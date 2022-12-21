/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2022 Andreas Maschke

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

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.base.raster.AbstractRaster;
import org.jwildfire.create.tina.farender.FAFlameWriter;
import org.jwildfire.create.tina.farender.FARenderResult;
import org.jwildfire.create.tina.farender.FARenderTools;
import org.jwildfire.create.tina.render.*;
import org.jwildfire.create.tina.render.backdrop.FlameBackdropHandler;
import org.jwildfire.create.tina.render.denoiser.AIPostDenoiserType;
import org.jwildfire.create.tina.render.gpu.GPURendererFactory;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.transform.ComposeTransformer;
import org.jwildfire.transform.RectangleTransformer;
import org.jwildfire.transform.TextTransformer;
import org.jwildfire.transform.TextTransformer.FontStyle;
import org.jwildfire.transform.TextTransformer.HAlignment;
import org.jwildfire.transform.TextTransformer.Mode;
import org.jwildfire.transform.TextTransformer.VAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class FlamePreviewHelper implements IterationObserver {
  private static final Logger logger = LoggerFactory.getLogger(FlamePreviewHelper.class);


  private final int initialImageUpdateInterval;
  private final int imageUpdateInterval;
  private final int maxImageUpdateIncInterval;
  private final Prefs prefs;
  private final JPanel centerPanel;
  private final JToggleButton toggleTransparencyButton;
  private final JToggleButton layerAppendBtn;
  private final JToggleButton layerPreviewBtn;
  private final JToggleButton gpuModeToggleButton;
  private final ProgressUpdater mainProgressUpdater;
  private final FlameHolder flameHolder;
  private final LayerHolder layerHolder;
  private final ErrorHandler errorHandler;
  private final DetachedPreviewProvider detachedPreviewProvider;
  private final FlamePanelProvider flamePanelProvider;
  private final FlameMessageHelper messageHelper;
  private final RandomBatchHolder randomBatchHolder;
  private InteractiveRendererDisplayUpdater displayUpdater =
      new EmptyInteractiveRendererDisplayUpdater();
  private FlameRenderer renderer;
  private AbstractRaster prevRaster;
  private UpdateDisplayThread updateDisplayThread;
  private Thread updateDisplayExecuteThread;
  private RenderThreads threads;
  private RenderThumbnailThread renderThumbnailThread = null;
  private GpuProgressUpdater gpuProgressUpdater = null;

  public FlamePreviewHelper(
      ErrorHandler pErrorHandler,
      JPanel pCenterPanel,
      JToggleButton pToggleTransparencyButton,
      JToggleButton pLayerAppendBtn,
      JToggleButton pLayerPreviewBtn,
      JToggleButton pGpuModeToggleButton,
      ProgressUpdater pMainProgressUpdater,
      FlameHolder pFlameHolder,
      LayerHolder pLayerHolder,
      DetachedPreviewProvider pDetachedPreviewProvider,
      FlamePanelProvider pFlamePanelProvider,
      FlameMessageHelper pMessageHelper,
      RandomBatchHolder pRandomBatchHolder) {
    prefs = Prefs.getPrefs();
    errorHandler = pErrorHandler;
    centerPanel = pCenterPanel;
    toggleTransparencyButton = pToggleTransparencyButton;
    layerAppendBtn = pLayerAppendBtn;
    layerPreviewBtn = pLayerPreviewBtn;
    gpuModeToggleButton = pGpuModeToggleButton;
    mainProgressUpdater = pMainProgressUpdater;
    flameHolder = pFlameHolder;
    layerHolder = pLayerHolder;
    detachedPreviewProvider = pDetachedPreviewProvider;
    flamePanelProvider = pFlamePanelProvider;
    messageHelper = pMessageHelper;
    randomBatchHolder = pRandomBatchHolder;

    if(prefs.isTinaLegacyRealtimePreview()) {
      initialImageUpdateInterval = 5 - 1;
      imageUpdateInterval = 8 - 2;
      maxImageUpdateIncInterval = 200 + 300;
    }
    else {
      initialImageUpdateInterval = 20;
      imageUpdateInterval = 10;
      maxImageUpdateIncInterval = 500;
    }
  }

  public void fastRefreshFlameImage(boolean pQuickRender, boolean pMouseDown, int pDownScale) {
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    SimpleImage img = fastRenderFlameImage(pQuickRender, pMouseDown, pDownScale);
    if (img != null) {
      imgPanel.setImage(img);
    }
    imgPanel.repaint();
  }

  public boolean isProgressivePreviewEnabled() {
    return isProgressivePreviewEnabled(flamePanelProvider.getFlamePanelConfig());
  }

  private boolean isProgressivePreviewEnabled(FlamePanelConfig cfg) {
    return !isTransparencyEnabled() && !isDrawFocusPointEnabled(cfg) && cfg.isProgressivePreview();
  }

  private boolean noRenderingWhileMouseMove = !Prefs.getPrefs().isTinaLegacyRealtimePreview();

  public void refreshFlameImage(
      boolean pQuickRender,
      boolean pMouseDown,
      int pDownScale,
      boolean pReRender,
      boolean pAllowUseCache) {
    cancelBackgroundRender();
    if (pQuickRender
        && detachedPreviewProvider != null
        && detachedPreviewProvider.getDetachedPreviewController() != null
        && pDownScale == 1) {
      detachedPreviewProvider.getDetachedPreviewController().setFlame(flameHolder.getFlame());
    }

    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    FlamePanelConfig cfg = flamePanelProvider.getFlamePanelConfig();

    if (pReRender) {
      if (!pQuickRender || !isProgressivePreviewEnabled(cfg) || (noRenderingWhileMouseMove && pMouseDown)) {
        SimpleImage img = renderFlameImage(pQuickRender, pMouseDown, noRenderingWhileMouseMove ? pDownScale : 2, pAllowUseCache);
        if (img != null) {
          imgPanel.setImage(img);
        }
      }
    }

    imgPanel.setBounds(imgPanel.getParentImageBounds());
    if (!cfg.isNoControls()) {
      centerPanel.getParent().validate();
      centerPanel.repaint();
    }

    if (pReRender && isProgressivePreviewEnabled(cfg) && pQuickRender && (!noRenderingWhileMouseMove || (noRenderingWhileMouseMove && !pMouseDown))) {
      if (pAllowUseCache) {
        SimpleImage img = renderFlameImage(pQuickRender, pMouseDown, pDownScale, pAllowUseCache);
        if (img != null) {
          imgPanel.setImage(img);
        }
      } else {
        startBackgroundRender(imgPanel);
      }
    }

    if (cfg.isNoControls()) {
      imgPanel.repaint();
    }

    if (pQuickRender && !cfg.isNoControls() && randomBatchHolder != null && !pMouseDown) {
      refreshThumbnailAsync();
    }
  }

  private void refreshThumbnailSync() {
    Flame flame = flameHolder.getFlame();
    List<FlameThumbnail> randomBatch = randomBatchHolder.getRandomBatch();
    for (int i = 0; i < randomBatch.size(); i++) {
      Flame bFlame = randomBatch.get(i).getFlame();
      if (bFlame == flame) {
        randomBatch.get(i).setPreview(null);
        ImagePanel pnl = randomBatch.get(i).getImgPanel();
        if (pnl != null) {
          pnl.replaceImage(randomBatch.get(i).getPreview(prefs.getTinaRenderPreviewQuality() / 2));
          pnl.repaint();
        }
        break;
      }
    }
  }

  public SimpleImage renderFlameImage(
      boolean pQuickRender, boolean pMouseDown, int pDownScale, boolean pAllowUseCache) {
    if (!pAllowUseCache) {
      prevRaster = null;
    }
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    FlamePanelConfig cfg = flamePanelProvider.getFlamePanelConfig();

    Rectangle panelBounds = imgPanel.getParentImageBounds();
    Rectangle bounds;
    if (pDownScale != 1) {
      bounds = new Rectangle(panelBounds.width / pDownScale, panelBounds.height / pDownScale);
    } else {
      bounds = panelBounds;
    }

    int renderScale = pQuickRender && pMouseDown && !pAllowUseCache ? 2 : 1;

    int width = bounds.width / renderScale;
    int height = bounds.height / renderScale;

    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
      Flame oriFlame = flameHolder.getFlame();
      Flame flame = oriFlame != null ? oriFlame.makeCopy() : null;
      if (flame != null) {
        if (GPURendererFactory.isAvailable()
            && gpuModeToggleButton != null
            && gpuModeToggleButton.isSelected()
            && !pQuickRender) {
          try {
            final int PROGRESS_STEPS = 25;
            File tmpFile = File.createTempFile("jwf", ".flame");
            try {
              FileDialogTools.ensureFileAccess(
                  new Frame(), new JPanel(), tmpFile.getAbsolutePath());
              mainProgressUpdater.initProgress(PROGRESS_STEPS);
              if (gpuProgressUpdater != null) {
                gpuProgressUpdater.signalCancel();
              }
              if (gpuProgressUpdater != null && gpuProgressUpdater.isFinished()) {
                gpuProgressUpdater = null;
              }
              if (gpuProgressUpdater == null) {
                gpuProgressUpdater = new GpuProgressUpdater(mainProgressUpdater, PROGRESS_STEPS);
                new Thread(gpuProgressUpdater).start();
              }
              FARenderResult openClRenderRes;
              long t0 = System.currentTimeMillis();
              try {
                List<Flame> preparedFlames =
                    FARenderTools.prepareFlame(
                        flame, TinaControllerContextService.getContext().isZPass());
                new FAFlameWriter().writeFlame(preparedFlames, tmpFile.getAbsolutePath());
                openClRenderRes =
                    FARenderTools.invokeFARender(
                        tmpFile.getAbsolutePath(),
                        width,
                        height,
                        prefs.getTinaRenderPreviewQuality(),
                        preparedFlames.size() > 1);
              } finally {
                try {
                  if (gpuProgressUpdater != null) {
                    gpuProgressUpdater.signalCancel();
                  }
                } catch (Exception ex) {
                  // EMPTY
                }
              }
              if (openClRenderRes.getReturnCode() != 0) {
                throw new Exception(openClRenderRes.getMessage());
              } else {
                SimpleImage img = new ImageReader().loadImage(openClRenderRes.getOutputFilename());
                mainProgressUpdater.updateProgress(PROGRESS_STEPS);
                if (!cfg.isNoControls() && messageHelper != null) {
                  long t1 = System.currentTimeMillis();
                  messageHelper.showStatusMessage(
                      flame, "render time (GPU): " + Tools.doubleToString((t1 - t0) * 0.001) + "s");
                  logger.info(openClRenderRes.getMessage());
                }
                return img;
              }
            } finally {
              if (!tmpFile.delete()) {
                tmpFile.deleteOnExit();
              }
              if (gpuProgressUpdater != null) {
                gpuProgressUpdater.signalCancel();
              }
            }
          } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            errorHandler.handleError(ex);
          }
        } else {
          double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
          double oldSampleDensity = flame.getSampleDensity();
          int oldSpatialOversampling = flame.getSpatialOversampling();
          AIPostDenoiserType oldAIPostDenoiser = flame.getAiPostDenoiser();
          try {
            double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
            double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
            double pixelsPerUnitScale = (wScl + hScl) * 0.5;
            flame.setPixelsPerUnitScale(pixelsPerUnitScale);

            boolean zpass = TinaControllerContextService.getContext().isZPass();
            info.setRenderZBuffer(zpass);

            if (pMouseDown) {
              flame.setMotionBlurLength(0);
            }

            try {
              FlameRenderer renderer;
              if (pQuickRender) {
                flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
                flame.applyFastOversamplingSettings();
                flame.setSpatialOversampling(oldSpatialOversampling);
              } else {
                flame.setSampleDensity(prefs.getTinaRenderPreviewQuality());
              }

              if (isDrawFocusPointEnabled(cfg)) {
                renderer = new DrawFocusPointFlameRenderer(flame, prefs, isTransparencyEnabled());
              } else {
                renderer = new FlameRenderer(flame, prefs, isTransparencyEnabled(), false);
              }

              if (pQuickRender) {
                renderer.setProgressUpdater(null);
              } else {
                renderer.setProgressUpdater(mainProgressUpdater);
              }

              long t0 = System.currentTimeMillis();
              renderer.setRenderScale(renderScale);
              info.setRenderZBuffer(TinaControllerContextService.getContext().isZPass());

              RenderedFlame res;
              if (prevRaster != null && pAllowUseCache) {
                info.setStoreRaster(false);
                info.setRestoredRaster(prevRaster);
                flame.setSampleDensity(prevRaster.getSampleDensity());
                res = renderer.renderFlame(info);
              } else {
                if (zpass) {
                  info.setStoreRaster(false);
                  res = renderer.renderFlame(info);
                  prevRaster = null;
                } else {
                  info.setStoreRaster(true);
                  res = renderer.renderFlame(info);
                  prevRaster = res.getRaster();
                }
              }

              SimpleImage img;
              if (zpass) {
                img =
                    new SimpleImage(
                        res.getZBuffer().getImageWidth(), res.getZBuffer().getImageHeight());
                img.setBufferedImage(
                    res.getZBuffer().getBufferedImg(),
                    res.getZBuffer().getImageWidth(),
                    res.getZBuffer().getImageHeight());
              } else {
                img = res.getImage();
              }
              long t1 = System.currentTimeMillis();
              // img.getBufferedImg().setAccelerationPriority(1.0f);

              if (layerAppendBtn != null && layerAppendBtn.isSelected() && !pMouseDown) {
                showLayerAppendModeIndicator(img);
              }

              if (layerPreviewBtn != null && layerPreviewBtn.isSelected() && layerHolder != null) {
                SimpleImage layerImg = createLayerPreview(img, renderScale, width, height);
                showLayerPreview(img, layerImg, renderScale, width, height);
              }

              if (pDownScale != 1) {
                SimpleImage background = new SimpleImage(panelBounds.width, panelBounds.height);
                ComposeTransformer cT = new ComposeTransformer();
                cT.setHAlign(ComposeTransformer.HAlignment.CENTRE);
                cT.setVAlign(ComposeTransformer.VAlignment.CENTRE);
                cT.setForegroundImage(img);
                cT.transformImage(background);
                img = background;
              }

              if (!cfg.isNoControls() && messageHelper != null) {
                messageHelper.showStatusMessage(
                    flame, "render time (CPU): " + Tools.doubleToString((t1 - t0) * 0.001) + "s");
              }

              return img;
            } catch (Throwable ex) {
              errorHandler.handleError(ex);
            }
          } finally {
            flame.setSpatialFilterRadius(oldSpatialFilterRadius);
            flame.setSampleDensity(oldSampleDensity);
            flame.setSpatialOversampling(oldSpatialOversampling);
            flame.setAiPostDenoiser(oldAIPostDenoiser);
          }
        }
      }
    }
    return null;
  }

  public SimpleImage renderAnimFrame(double imageScale, double quality) {
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();

    Rectangle panelBounds = imgPanel.getParentImageBounds();

    int width = Tools.FTOI(panelBounds.width * imageScale);
    int height = Tools.FTOI(panelBounds.height * imageScale);

    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
      Flame oriFlame = flameHolder.getFlame();
      Flame flame = oriFlame != null ? oriFlame.makeCopy() : null;
      if (flame != null) {
        double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
        double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
        double pixelsPerUnitScale = (wScl + hScl) * 0.5;
        flame.setPixelsPerUnitScale(pixelsPerUnitScale);
        try {
          flame.setSampleDensity(quality);
          flame.setSpatialOversampling(1);
          info.setRenderZBuffer(TinaControllerContextService.getContext().isZPass());
          FlameRenderer renderer = new FlameRenderer(flame, prefs, isTransparencyEnabled(), false);
          RenderedFlame res = renderer.renderFlame(info);
          SimpleImage img = res.getImage();
          // img.getBufferedImg().setAccelerationPriority(1.0f);
          return img;
        } catch (Throwable ex) {
          errorHandler.handleError(ex);
        }
      }
    }
    return null;
  }

  private SimpleImage createLayerPreview(SimpleImage img, int renderScale, int width, int height) {
    FlamePanelConfig cfg = flamePanelProvider.getFlamePanelConfig();
    Flame flame = flameHolder.getFlame();

    Layer onlyVisibleLayer = null;
    for (Layer layer : flameHolder.getFlame().getLayers()) {
      if (layer.isVisible()) {
        if (onlyVisibleLayer == null) {
          onlyVisibleLayer = layer;
        } else {
          onlyVisibleLayer = null;
          break;
        }
      }
    }
    boolean drawSubLayer =
        flame.getLayers().size() > 1
            && layerHolder.getLayer() != null
            && layerHolder.getLayer() != onlyVisibleLayer
            && !cfg.isNoControls();

    if (drawSubLayer) {
      Flame singleLayerFlame = new Flame();
      singleLayerFlame.assign(flame);
      singleLayerFlame.getLayers().clear();
      singleLayerFlame.getLayers().add(layerHolder.getLayer().makeCopy());
      singleLayerFlame.getFirstLayer().setVisible(true);
      singleLayerFlame.getFirstLayer().setWeight(1.0);
      RenderInfo lInfo =
          new RenderInfo(width / 8 * renderScale, height / 8 * renderScale, RenderMode.PREVIEW);
      double lWScl = (double) lInfo.getImageWidth() / (double) singleLayerFlame.getWidth();
      double lHScl = (double) lInfo.getImageHeight() / (double) singleLayerFlame.getHeight();
      singleLayerFlame.setPixelsPerUnitScale(
          (lWScl + lHScl) * 0.5);
      singleLayerFlame.setSampleDensity(prefs.getTinaRenderRealtimeQuality() * 2.0 / 8.0);
      FlameRenderer lRenderer = new FlameRenderer(singleLayerFlame, prefs, false, false);

      RenderedFlame lRes = lRenderer.renderFlame(lInfo);
      SimpleImage layerImg = lRes.getImage();

      boolean drawLayerNumber = true;
      if (drawLayerNumber) {
        RectangleTransformer rT = new RectangleTransformer();
        int textWidth = 28;
        int textHeight = 22;
        int margin = 2;

        rT.setColor(new java.awt.Color(0, 0, 0));
        rT.setLeft(layerImg.getImageWidth() - textWidth - 2 * margin);
        rT.setTop(layerImg.getImageHeight() - textHeight - 2 * margin);
        rT.setThickness(textHeight / 2 + 1);
        rT.setWidth(textWidth);
        rT.setHeight(textHeight);
        rT.transformImage(layerImg);

        TextTransformer txt = new TextTransformer();
        txt.setText1("  " + (flame.getLayers().indexOf(layerHolder.getLayer()) + 1) + "  ");
        txt.setAntialiasing(true);
        txt.setColor(new java.awt.Color(200, 200, 200));
        txt.setMode(Mode.NORMAL);
        txt.setFontStyle(FontStyle.BOLD);
        txt.setFontName("Arial");
        txt.setFontSize(16);
        txt.setHAlign(HAlignment.NONE);
        txt.setPosX(layerImg.getImageWidth() - textWidth - margin);
        txt.setPosY(layerImg.getImageHeight() - textHeight - margin);
        txt.setVAlign(VAlignment.NONE);
        txt.transformImage(layerImg);
      }

      RectangleTransformer rT = new RectangleTransformer();
      rT.setColor(new java.awt.Color(200, 200, 200));
      rT.setLeft(0);
      rT.setTop(0);
      rT.setThickness(3);
      rT.setWidth(lInfo.getImageWidth());
      rT.setHeight(lInfo.getImageHeight());
      rT.transformImage(layerImg);
      return layerImg;
    }
    return null;
  }

  private void showLayerPreview(
      SimpleImage img, SimpleImage layerImg, int renderScale, int width, int height) {
    if (layerImg != null) {
      ComposeTransformer cT = new ComposeTransformer();
      cT.setHAlign(ComposeTransformer.HAlignment.LEFT);
      cT.setVAlign(ComposeTransformer.VAlignment.TOP);
      cT.setTop(10);
      cT.setLeft(10);
      cT.setForegroundImage(layerImg);
      cT.transformImage(img);
    }
  }

  private void showLayerAppendModeIndicator(SimpleImage img) {
    TextTransformer txt = new TextTransformer();
    txt.setText1("layer-append-mode active");
    txt.setAntialiasing(true);
    txt.setColor(Color.RED);
    txt.setMode(Mode.NORMAL);
    txt.setFontStyle(FontStyle.BOLD);
    txt.setFontName("Arial");
    txt.setFontSize(16);
    txt.setHAlign(HAlignment.RIGHT);
    txt.setVAlign(VAlignment.BOTTOM);
    txt.transformImage(img);
  }

  private boolean isDrawFocusPointEnabled(FlamePanelConfig cfg) {
    return !cfg.isNoControls() && cfg.getMouseDragOperation() == MouseDragOperation.FOCUS;
  }

  private boolean isTransparencyEnabled() {
    return toggleTransparencyButton != null && toggleTransparencyButton.isSelected();
  }

  public SimpleImage fastRenderFlameImage(
      boolean pQuickRender, boolean pMouseDown, int pDownScale) {
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    FlamePanelConfig cfg = flamePanelProvider.getFlamePanelConfig();

    Rectangle panelBounds = imgPanel.getParentImageBounds();
    Rectangle bounds;
    if (pDownScale != 1) {
      bounds = new Rectangle(panelBounds.width / pDownScale, panelBounds.height / pDownScale);
    } else {
      bounds = panelBounds;
    }

    int renderScale = pQuickRender && pMouseDown ? 2 : 1;
    int width = bounds.width / renderScale;
    int height = bounds.height / renderScale;
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
      Flame oriFlame = flameHolder.getFlame();
      Flame flame = oriFlame != null ? oriFlame.makeCopy() : null;
      if (flame != null) {
        double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
        double oldSampleDensity = flame.getSampleDensity();
        int oldSpatialOversampling = flame.getSpatialOversampling();
        AIPostDenoiserType oldAIPostDenoiser = flame.getAiPostDenoiser();
        try {
          double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
          double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
          double pixelsPerUnitScale = (wScl + hScl) * 0.5;
          flame.setPixelsPerUnitScale(pixelsPerUnitScale);
          try {
            FlameRenderer renderer;
            if (isDrawFocusPointEnabled(cfg)) {
              renderer = new DrawFocusPointFlameRenderer(flame, prefs, isTransparencyEnabled());
            } else {
              renderer = new FlameRenderer(flame, prefs, isTransparencyEnabled(), false);
            }
            renderer.setProgressUpdater(null);
            flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
            flame.applyFastOversamplingSettings();
            renderer.setRenderScale(renderScale);
            info.setRenderZBuffer(TinaControllerContextService.getContext().isZPass());
            RenderedFlame res = renderer.renderFlame(info);
            SimpleImage img = res.getImage();
            // img.getBufferedImg().setAccelerationPriority(1.0f);
            return img;
          } catch (Throwable ex) {
            errorHandler.handleError(ex);
          }
        } finally {
          flame.setSampleDensity(oldSampleDensity);
          flame.setSpatialFilterRadius(oldSpatialFilterRadius);
          flame.setSpatialOversampling(oldSpatialOversampling);
          flame.setAiPostDenoiser(oldAIPostDenoiser);
        }
      }
    }
    return null;
  }

  public void setDerivedImage(SimpleImage pImage) {
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    imgPanel.setDerivedImage(pImage);
  }

  public void forceRepaint() {
    try {
      flamePanelProvider.getFlamePanel().paint(flamePanelProvider.getFlamePanel().getGraphics());
    } catch (Exception ex) {

    }
  }

  public Rectangle getPanelBounds() {
    return flamePanelProvider.getFlamePanel().getBounds();
  }

  public void cancelBackgroundRender() {
    if (threads == null) return;

    if (updateDisplayThread != null) {
      updateDisplayThread.cancel();
    }

    try {
      for (Thread thread : threads.getExecutingThreads()) {
        try {
          thread.setPriority(Thread.MIN_PRIORITY);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }

      if (updateDisplayExecuteThread != null) {
        updateDisplayExecuteThread.setPriority(Thread.NORM_PRIORITY);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    while (true) {
      boolean done = true;
      for (AbstractRenderThread thread : threads.getRenderThreads()) {
        if (!thread.isFinished()) {
          done = false;
          thread.cancel();
          try {
            Thread.sleep(1);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      if (done) {
        break;
      }
    }
    threads = null;

    if (updateDisplayThread != null) {
      updateDisplayThread.cancel();
      while (!updateDisplayThread.isFinished()) {
        try {
          updateDisplayThread.cancel();
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      updateDisplayThread = null;
    }
  }

  private void startBackgroundRender(FlamePanel pImgPanel) {
    prevRaster = null;
    if (flameHolder == null) {
      return;
    }
    Flame flame = flameHolder.getFlame().makeCopy();


    flame.applyFastOversamplingSettings();
    Rectangle panelBounds = pImgPanel.getParentImageBounds();

    RenderInfo info = new RenderInfo(panelBounds.width, panelBounds.height, RenderMode.PREVIEW);
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    double pixelsPerUnitScale = (wScl + hScl) * 0.5;
    flame.setPixelsPerUnitScale(pixelsPerUnitScale);
    flame.setSampleDensity(1.0);

    info.setRenderHDR(false);
    info.setRenderZBuffer(TinaControllerContextService.getContext().isZPass());
    renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);
    if (!prefs.isTinaLegacyRealtimePreview()) {
      renderer.setSleepAmount(prefs.getTinaRealtimePreviewIdleAmount());
    }
    renderer.registerIterationObserver(this);

    SimpleImage image =
        new SimpleImage(
            pImgPanel.getImage().getImageWidth(), pImgPanel.getImage().getImageHeight());
    initImage(image, flame);
    displayUpdater = new BufferedInteractiveRendererDisplayUpdater(pImgPanel, image, true);
    displayUpdater.initRender(prefs.getTinaRenderThreads());
    threads = renderer.startRenderFlame(info);
    updateDisplayThread = new UpdateDisplayThread(flame, image);

    updateDisplayExecuteThread = new Thread(updateDisplayThread);
    updateDisplayExecuteThread.setPriority(Thread.NORM_PRIORITY);
    updateDisplayExecuteThread.start();
  }

  private void initImage(SimpleImage pImage, Flame flame) {
    if (flame.getBGImageFilename() != null && !flame.getBGImageFilename().isEmpty()) {
      try {
        SimpleImage bgImg = (SimpleImage) RessourceManager.getImage(flame.getBGImageFilename());
        pImage.fillBackground(bgImg);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } else {
      new FlameBackdropHandler(flame).fillBackground(pImage);
    }
  }

  @Override
  public void notifyIterationFinished(
      AbstractRenderThread pEventSource,
      int pPlotX,
      int pPlotY,
      XYZProjectedPoint pProjectedPoint,
      double pX,
      double pY,
      double pZ,
      double pColorRed,
      double pColorGreen,
      double pColorBlue) {
    displayUpdater.iterationFinished(pEventSource, pPlotX, pPlotY);
  }

  public void stopPreviewRendering() {
    cancelBackgroundRender();
  }

  public SimpleImage getImage() {
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    return imgPanel != null ? imgPanel.getImage() : null;
  }

  public void setImage(SimpleImage pImage) {
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    imgPanel.setImage(pImage);
  }

  private void cancelThumbnailBackgroundRender() {
    if (renderThumbnailThread != null) {
      try {
        renderThumbnailThread.cancel();
        while (!renderThumbnailThread.isFinished()) {
          try {
            Thread.sleep(1);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      } finally {
        renderThumbnailThread = null;
      }
    }
  }

  private void refreshThumbnailAsync() {
    cancelThumbnailBackgroundRender();
    Flame flame = flameHolder.getFlame();
    List<FlameThumbnail> randomBatch = randomBatchHolder.getRandomBatch();
    for (int i = 0; i < randomBatch.size(); i++) {
      Flame bFlame = randomBatch.get(i).getFlame();
      if (bFlame == flame) {
        renderThumbnailThread = new RenderThumbnailThread(randomBatch, i);
        Thread executingThread = new Thread(renderThumbnailThread);
        executingThread.setPriority(Thread.MIN_PRIORITY);
        executingThread.start();
        break;
      }
    }
  }

  public FlamePanelProvider getFlamePanelProvider() {
    return flamePanelProvider;
  }

  private class UpdateDisplayThread implements Runnable, InteractiveRendererImagePostProcessor {
    private final SimpleImage image;
    private final int maxPreviewTimeInMilliseconds;
    private final double maxPreviewQuality;
    private int nextImageUpdate;
    private int lastImageUpdateInterval;
    private boolean cancelSignalled;
    private boolean replaceImageFlag;
    private boolean finished;
    private SimpleImage layerImg;

    public UpdateDisplayThread(Flame flame, SimpleImage pImage) {
      Prefs prefs = Prefs.getPrefs();
      maxPreviewTimeInMilliseconds =
          Tools.FTOI(prefs.getTinaEditorProgressivePreviewMaxRenderTime() * 1000.0);
      double renderQualityScale =
          flame.getLayers().size()
              * (flame.getSolidRenderSettings().isSolidRenderingEnabled() ? 1.5 : 1.0);
      maxPreviewQuality =
          Tools.FTOI(prefs.getTinaEditorProgressivePreviewMaxRenderQuality() * renderQualityScale);
      nextImageUpdate = initialImageUpdateInterval;
      lastImageUpdateInterval = initialImageUpdateInterval;
      image = pImage;
    }

    private double getCurrQuality() {
      try {
        if (threads != null) {
          List<AbstractRenderThread> renderThreads = threads.getRenderThreads();
          if (renderThreads != null && renderThreads.size() > 0) {
            return renderThreads
                .get(0)
                .getTonemapper()
                .calcDensity(displayUpdater.getSampleCount());
          }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      return 0.0;
    }

    @Override
    public void run() {
      long t0 = System.currentTimeMillis();
      finished = cancelSignalled = false;
      try {
        replaceImageFlag = false;
        while (!cancelSignalled) {
          double currQuality = getCurrQuality();
          if (currQuality > maxPreviewQuality
              || System.currentTimeMillis() - t0 > maxPreviewTimeInMilliseconds) {
            cancelSignalled = true;
            for (AbstractRenderThread thread : threads.getRenderThreads()) {
              if (!thread.isFinished()) {
                thread.cancel();
              }
            }
            break;
          }

          try {
            if (--nextImageUpdate <= 0) {
              lastImageUpdateInterval += imageUpdateInterval;
              if (lastImageUpdateInterval > maxImageUpdateIncInterval) {
                lastImageUpdateInterval = maxImageUpdateIncInterval;
              }
              if (!replaceImageFlag) {
                FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
                imgPanel.replaceImage(image);
                replaceImageFlag = true;
              }
              currQuality =
                  threads
                      .getRenderThreads()
                      .get(0)
                      .getTonemapper()
                      .calcDensity(displayUpdater.getSampleCount());
              if (currQuality > 0.1) {
                if (currQuality < 0.5) currQuality *= 10;
                else if (currQuality < 1.0) currQuality *= 5;
                else if (currQuality < 2.0) currQuality *= 2;
                for (AbstractRenderThread thread : threads.getRenderThreads()) {
                  thread.getTonemapper().setDensity(currQuality);
                }
                displayUpdater.updateImage(this);
              }
              nextImageUpdate = lastImageUpdateInterval;
            } else {
              Thread.sleep(1);
            }
          } catch (Throwable e) {
            cancelSignalled = true;
            e.printStackTrace();
          }
        }
      } finally {
        finished = true;
      }
    }

    public void cancel() {
      cancelSignalled = true;
    }

    public boolean isFinished() {
      return finished;
    }

    @Override
    public void postProcessImage(SimpleImage pImage) {
      if (layerAppendBtn != null && layerAppendBtn.isSelected()) {
        showLayerAppendModeIndicator(image);
      }
      if (layerPreviewBtn != null && layerPreviewBtn.isSelected() && layerHolder != null) {
        Rectangle panelBounds = flamePanelProvider.getFlamePanel().getParentImageBounds();
        if (layerImg == null) {
          layerImg = createLayerPreview(image, 1, panelBounds.width, panelBounds.height);
        }
        showLayerPreview(image, layerImg, 1, panelBounds.width, panelBounds.height);
      }
    }
  }

  private class RenderThumbnailThread implements Runnable {
    private final List<FlameThumbnail> randomBatch;
    private final int flameIdx;
    private boolean finished;
    private boolean forceAbort;
    private FlameRenderer flameRenderer;

    public RenderThumbnailThread(List<FlameThumbnail> randomBatch, int flameIdx) {
      this.randomBatch = randomBatch;
      this.flameIdx = flameIdx;
    }

    @Override
    public void run() {
      finished = forceAbort = false;
      try {
        FlameThumbnail thumbnail = randomBatch.get(flameIdx);
        RenderInfo info = thumbnail.getPreviewRenderInfo();
        int quality = prefs.getTinaRenderPreviewQuality() / 2;
        flameRenderer = thumbnail.getPreviewRenderer(info, quality);
        RenderedFlame res = flameRenderer.renderFlame(info);
        if (!forceAbort) {
          randomBatch.get(flameIdx).setPreview(res.getImage());
          ImagePanel pnl = randomBatch.get(flameIdx).getImgPanel();
          if (pnl != null) {
            pnl.replaceImage(randomBatch.get(flameIdx).getPreview(quality));
            pnl.repaint();
          }
        }
      } finally {
        finished = true;
      }
    }

    public void cancel() {
      try {
        forceAbort = true;
        flameRenderer.cancel();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    public boolean isFinished() {
      return finished;
    }
  }
}
