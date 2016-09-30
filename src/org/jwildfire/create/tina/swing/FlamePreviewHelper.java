/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.render.AbstractRenderThread;
import org.jwildfire.create.tina.render.DrawFocusPointFlameRenderer;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.IterationObserver;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderThreads;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.transform.ComposeTransformer;
import org.jwildfire.transform.RectangleTransformer;
import org.jwildfire.transform.TextTransformer;
import org.jwildfire.transform.TextTransformer.FontStyle;
import org.jwildfire.transform.TextTransformer.HAlignment;
import org.jwildfire.transform.TextTransformer.Mode;
import org.jwildfire.transform.TextTransformer.VAlignment;

public class FlamePreviewHelper implements IterationObserver {
  private final Prefs prefs;
  private final JPanel centerPanel;
  private final JToggleButton toggleTransparencyButton;
  private final JToggleButton layerAppendBtn;
  private final JToggleButton layerPreviewBtn;
  private final ProgressUpdater mainProgressUpdater;
  private final FlameHolder flameHolder;
  private final LayerHolder layerHolder;
  private final ErrorHandler errorHandler;
  private final DetachedPreviewProvider detachedPreviewProvider;
  private final FlamePanelProvider flamePanelProvider;
  private final FlameMessageHelper messageHelper;
  private final RandomBatchHolder randomBatchHolder;
  private InteractiveRendererDisplayUpdater displayUpdater = new EmptyInteractiveRendererDisplayUpdater();
  private FlameRenderer renderer;

  public FlamePreviewHelper(ErrorHandler pErrorHandler, JPanel pCenterPanel, JToggleButton pToggleTransparencyButton, JToggleButton pLayerAppendBtn, JToggleButton pLayerPreviewBtn,
      ProgressUpdater pMainProgressUpdater, FlameHolder pFlameHolder, LayerHolder pLayerHolder,
      DetachedPreviewProvider pDetachedPreviewProvider, FlamePanelProvider pFlamePanelProvider,
      FlameMessageHelper pMessageHelper, RandomBatchHolder pRandomBatchHolder) {
    prefs = Prefs.getPrefs();
    errorHandler = pErrorHandler;
    centerPanel = pCenterPanel;
    toggleTransparencyButton = pToggleTransparencyButton;
    layerAppendBtn = pLayerAppendBtn;
    layerPreviewBtn = pLayerPreviewBtn;
    mainProgressUpdater = pMainProgressUpdater;
    flameHolder = pFlameHolder;
    layerHolder = pLayerHolder;
    detachedPreviewProvider = pDetachedPreviewProvider;
    flamePanelProvider = pFlamePanelProvider;
    messageHelper = pMessageHelper;
    randomBatchHolder = pRandomBatchHolder;
  }

  public void fastRefreshFlameImage(boolean pQuickRender, boolean pMouseDown, int pDownScale) {
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    SimpleImage img = fastRenderFlameImage(pQuickRender, pMouseDown, pDownScale);
    if (img != null) {
      imgPanel.setImage(img);
    }
    imgPanel.repaint();
  }

  private boolean isProgressivePreviewEnabled(FlamePanelConfig cfg) {
    return !isTransparencyEnabled() && !isDrawFocusPointEnabled(cfg) && cfg.isProgressivePreview();
  }

  public void refreshFlameImage(boolean pQuickRender, boolean pMouseDown, int pDownScale, boolean pReRender, boolean pAllowUseCache) {
    // TODO
    //    pAllowUseCache = false;

    if (!pAllowUseCache) {
      prevRenderer = null;
    }
    cancelBackgroundRender();
    if (pQuickRender && detachedPreviewProvider != null && detachedPreviewProvider.getDetachedPreviewController() != null && pDownScale == 1) {
      detachedPreviewProvider.getDetachedPreviewController().setFlame(flameHolder.getFlame());
    }

    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    FlamePanelConfig cfg = flamePanelProvider.getFlamePanelConfig();

    if (pReRender) {
      if (!pQuickRender || !isProgressivePreviewEnabled(cfg)) {
        SimpleImage img = renderFlameImage(pQuickRender, pMouseDown, pDownScale, pAllowUseCache);
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
    else {
      imgPanel.repaint();
    }

    if (pReRender && isProgressivePreviewEnabled(cfg) && pQuickRender) {
      if (pAllowUseCache) {
        System.out.println("ATTEMPT REUSE");
        SimpleImage img = renderFlameImage(pQuickRender, pMouseDown, pDownScale, pAllowUseCache);
        if (img != null) {
          imgPanel.setImage(img);
        }
      }
      else {
        startBackgroundRender(imgPanel);
      }
    }

    if (pQuickRender && !cfg.isNoControls() && randomBatchHolder != null) {
      refreshThumbnail();
    }

  }

  private void refreshThumbnail() {
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

  private FlameRenderer prevRenderer;

  public SimpleImage renderFlameImage(boolean pQuickRender, boolean pMouseDown, int pDownScale, boolean pAllowUseCache) {
    if (!pAllowUseCache) {
      prevRenderer = null;
    }
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    FlamePanelConfig cfg = flamePanelProvider.getFlamePanelConfig();

    Rectangle panelBounds = imgPanel.getParentImageBounds();
    Rectangle bounds;
    if (pDownScale != 1) {
      bounds = new Rectangle(panelBounds.width / pDownScale, panelBounds.height / pDownScale);
    }
    else {
      bounds = panelBounds;
    }

    int renderScale = pQuickRender && pMouseDown ? 2 : 1;

    int width = bounds.width / renderScale;
    int height = bounds.height / renderScale;

    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
      Flame flame = flameHolder.getFlame();
      if (flame != null) {
        double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
        double oldSampleDensity = flame.getSampleDensity();
        int oldSpatialOversampling = flame.getSpatialOversampling();
        int oldColorOversampling = flame.getColorOversampling();
        boolean oldSampleJittering = flame.isSampleJittering();
        boolean oldPostNoiseFilter = flame.isPostNoiseFilter();
        try {
          double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
          double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(info.getImageWidth());
          flame.setHeight(info.getImageHeight());
          try {
            FlameRenderer renderer;
            if (pQuickRender) {
              flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
              flame.applyFastOversamplingSettings();
            }
            else {
              flame.setSampleDensity(prefs.getTinaRenderPreviewQuality());
            }

            if (isDrawFocusPointEnabled(cfg)) {
              renderer = new DrawFocusPointFlameRenderer(flame, prefs, isTransparencyEnabled());
            }
            else {
              renderer = new FlameRenderer(flame, prefs, isTransparencyEnabled(), false);
            }

            if (pQuickRender) {
              renderer.setProgressUpdater(null);
            }
            else {
              renderer.setProgressUpdater(mainProgressUpdater);
            }

            long t0 = System.currentTimeMillis();
            renderer.setRenderScale(renderScale);

            RenderedFlame res;
            if (prevRenderer != null && pAllowUseCache) {
              System.out.println("!!!REUSE");
              res = prevRenderer.rerenderFlame(info);
            }
            else {
              res = renderer.renderFlame(info);
              prevRenderer = renderer;
              System.out.println("NO REUSE!!!");
            }

            SimpleImage img = res.getImage();
            long t1 = System.currentTimeMillis();
            img.getBufferedImg().setAccelerationPriority(1.0f);

            if (layerAppendBtn != null && layerAppendBtn.isSelected() && !pMouseDown) {
              showLayerAppendModeIndicator(img);
            }

            if (layerPreviewBtn != null && layerPreviewBtn.isSelected() && layerHolder != null) {
              showLayerPreview(img, renderScale, width, height);
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
              messageHelper.showStatusMessage(flame, "render time: " + Tools.doubleToString((t1 - t0) * 0.001) + "s");
            }

            return img;
          }
          catch (Throwable ex) {
            errorHandler.handleError(ex);
          }
        }
        finally {
          flame.setSpatialFilterRadius(oldSpatialFilterRadius);
          flame.setSampleDensity(oldSampleDensity);
          flame.setSpatialOversampling(oldSpatialOversampling);
          flame.setColorOversampling(oldColorOversampling);
          flame.setSampleJittering(oldSampleJittering);
          flame.setPostNoiseFilter(oldPostNoiseFilter);
        }
      }
    }
    return null;
  }

  private void showLayerPreview(SimpleImage img, int renderScale, int width, int height) {
    FlamePanelConfig cfg = flamePanelProvider.getFlamePanelConfig();
    Flame flame = flameHolder.getFlame();

    Layer onlyVisibleLayer = null;
    for (Layer layer : flameHolder.getFlame().getLayers()) {
      if (layer.isVisible()) {
        if (onlyVisibleLayer == null) {
          onlyVisibleLayer = layer;
        }
        else {
          onlyVisibleLayer = null;
          break;
        }
      }
    }
    boolean drawSubLayer = flame.getLayers().size() > 1 && layerHolder.getLayer() != null && layerHolder.getLayer() != onlyVisibleLayer && !cfg.isNoControls();

    if (drawSubLayer) {
      Flame singleLayerFlame = new Flame();
      singleLayerFlame.assign(flame);
      singleLayerFlame.getLayers().clear();
      singleLayerFlame.getLayers().add(layerHolder.getLayer().makeCopy());
      singleLayerFlame.getFirstLayer().setVisible(true);
      singleLayerFlame.getFirstLayer().setWeight(1.0);
      RenderInfo lInfo = new RenderInfo(width / 4 * renderScale, height / 4 * renderScale, RenderMode.PREVIEW);
      double lWScl = (double) lInfo.getImageWidth() / (double) singleLayerFlame.getWidth();
      double lHScl = (double) lInfo.getImageHeight() / (double) singleLayerFlame.getHeight();
      singleLayerFlame.setPixelsPerUnit((lWScl + lHScl) * 0.5 * singleLayerFlame.getPixelsPerUnit() * 0.5);
      singleLayerFlame.setWidth(lInfo.getImageWidth());
      singleLayerFlame.setHeight(lInfo.getImageHeight());
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

      ComposeTransformer cT = new ComposeTransformer();
      cT.setHAlign(ComposeTransformer.HAlignment.LEFT);
      cT.setVAlign(ComposeTransformer.VAlignment.BOTTOM);
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

  public SimpleImage fastRenderFlameImage(boolean pQuickRender, boolean pMouseDown, int pDownScale) {
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    FlamePanelConfig cfg = flamePanelProvider.getFlamePanelConfig();

    Rectangle panelBounds = imgPanel.getParentImageBounds();
    Rectangle bounds;
    if (pDownScale != 1) {
      bounds = new Rectangle(panelBounds.width / pDownScale, panelBounds.height / pDownScale);
    }
    else {
      bounds = panelBounds;
    }

    int renderScale = pQuickRender && pMouseDown ? 2 : 1;
    int width = bounds.width / renderScale;
    int height = bounds.height / renderScale;
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height, RenderMode.PREVIEW);
      Flame flame = flameHolder.getFlame();
      if (flame != null) {
        double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
        double oldSampleDensity = flame.getSampleDensity();
        int oldSpatialOversampling = flame.getSpatialOversampling();
        int oldColorOversampling = flame.getColorOversampling();
        boolean oldSampleJittering = flame.isSampleJittering();
        boolean oldPostNoiseFilter = flame.isPostNoiseFilter();
        try {
          double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
          double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(info.getImageWidth());
          flame.setHeight(info.getImageHeight());
          try {
            FlameRenderer renderer;
            if (isDrawFocusPointEnabled(cfg)) {
              renderer = new DrawFocusPointFlameRenderer(flame, prefs, isTransparencyEnabled());
            }
            else {
              renderer = new FlameRenderer(flame, prefs, isTransparencyEnabled(), false);
            }
            renderer.setProgressUpdater(null);
            flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
            flame.applyFastOversamplingSettings();
            renderer.setRenderScale(renderScale);
            RenderedFlame res = renderer.renderFlame(info);
            SimpleImage img = res.getImage();
            img.getBufferedImg().setAccelerationPriority(1.0f);
            return img;
          }
          catch (Throwable ex) {
            errorHandler.handleError(ex);
          }
        }
        finally {
          flame.setSampleDensity(oldSampleDensity);
          flame.setSpatialFilterRadius(oldSpatialFilterRadius);
          flame.setSpatialOversampling(oldSpatialOversampling);
          flame.setColorOversampling(oldColorOversampling);
          flame.setSampleJittering(oldSampleJittering);
          flame.setPostNoiseFilter(oldPostNoiseFilter);
        }
      }
    }
    return null;
  }

  public void setImage(SimpleImage pImage) {
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    imgPanel.setImage(pImage);
  }

  public void forceRepaint() {
    try {
      flamePanelProvider.getFlamePanel().paint(flamePanelProvider.getFlamePanel().getGraphics());
    }
    catch (Exception ex) {

    }
  }

  public Rectangle getPanelBounds() {
    return flamePanelProvider.getFlamePanel().getBounds();
  }

  private UpdateDisplayThread updateDisplayThread;
  private Thread updateDisplayExecuteThread;
  private RenderThreads threads;

  public void cancelBackgroundRender() {
    if (threads == null)
      return;

    if (updateDisplayThread != null) {
      updateDisplayThread.cancel();
    }

    try {
      for (Thread thread : threads.getExecutingThreads()) {
        try {
          thread.setPriority(Thread.NORM_PRIORITY);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }

      if (updateDisplayExecuteThread != null) {
        updateDisplayExecuteThread.setPriority(Thread.MAX_PRIORITY);
      }
    }
    catch (Exception ex) {
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
          }
          catch (InterruptedException e) {
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
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      updateDisplayThread = null;
    }
  }

  private void startBackgroundRender(FlamePanel pImgPanel) {
    if (flameHolder == null) {
      return;
    }
    Flame flame = flameHolder.getFlame().makeCopy();
    if (flame == null) {
      return;
    }
    flame.applyFastOversamplingSettings();
    Rectangle panelBounds = pImgPanel.getParentImageBounds();

    RenderInfo info = new RenderInfo(panelBounds.width, panelBounds.height, RenderMode.PREVIEW);
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(info.getImageWidth());
    flame.setHeight(info.getImageHeight());
    flame.setSampleDensity(10.0);
    info.setRenderHDR(false);
    info.setRenderZBuffer(false);
    renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);
    renderer.registerIterationObserver(this);

    SimpleImage image = new SimpleImage(pImgPanel.getImage().getImageWidth(), pImgPanel.getImage().getImageHeight());
    initImage(image, flame.getBGColorRed(), flame.getBGColorGreen(), flame.getBGColorBlue(), flame.getBGImageFilename());

    displayUpdater = new BufferedInteractiveRendererDisplayUpdater(pImgPanel, image, true);
    displayUpdater.initRender(prefs.getTinaRenderThreads());
    threads = renderer.startRenderFlame(info);
    for (Thread thread : threads.getExecutingThreads()) {
      thread.setPriority(Thread.MIN_PRIORITY);
    }

    updateDisplayThread = new UpdateDisplayThread(image);
    updateDisplayExecuteThread = new Thread(updateDisplayThread);
    updateDisplayExecuteThread.setPriority(Thread.MIN_PRIORITY);
    updateDisplayExecuteThread.start();
  }

  private void initImage(SimpleImage pImage, int pBGRed, int pBGGreen, int pBGBlue, String pBGImagefile) {
    if (pBGRed > 0 || pBGGreen > 0 || pBGBlue > 0) {
      pImage.fillBackground(pBGRed, pBGGreen, pBGBlue);
    }
    else {
      pImage.fillBackground(0, 0, 0);
    }
    if (pBGImagefile != null && pBGImagefile.length() > 0) {
      try {
        SimpleImage bgImg = (SimpleImage) RessourceManager.getImage(pBGImagefile);
        pImage.fillBackground(bgImg);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private final static int INITIAL_IMAGE_UPDATE_INTERVAL = 5;
  private final static int IMAGE_UPDATE_INC_INTERVAL = 10;
  private final static int MAX_UPDATE_INC_INTERVAL = 100;

  private class UpdateDisplayThread implements Runnable, InteractiveRendererImagePostProcessor {
    private int nextImageUpdate;
    private int lastImageUpdateInterval;
    private boolean cancelSignalled;
    private boolean replaceImageFlag;
    private boolean finished;
    private SimpleImage image;
    private int maxPreviewTimeInMilliseconds;
    private double maxPreviewQuality;

    public UpdateDisplayThread(SimpleImage pImage) {
      Prefs prefs = Prefs.getPrefs();
      maxPreviewTimeInMilliseconds = Tools.FTOI(prefs.getTinaEditorProgressivePreviewMaxRenderTime() * 1000.0);
      maxPreviewQuality = prefs.getTinaEditorProgressivePreviewMaxRenderQuality();
      nextImageUpdate = INITIAL_IMAGE_UPDATE_INTERVAL;
      lastImageUpdateInterval = INITIAL_IMAGE_UPDATE_INTERVAL;
      image = pImage;
    }

    private double getCurrQuality() {
      try {
        if (threads != null) {
          List<AbstractRenderThread> renderThreads = threads.getRenderThreads();
          if (renderThreads != null && renderThreads.size() > 0) {
            return renderThreads.get(0).getTonemapper().calcDensity(displayUpdater.getSampleCount());
          }
        }
      }
      catch (Exception ex) {
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
          if (currQuality > maxPreviewQuality || System.currentTimeMillis() - t0 > maxPreviewTimeInMilliseconds) {
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
              lastImageUpdateInterval += IMAGE_UPDATE_INC_INTERVAL;
              if (lastImageUpdateInterval > MAX_UPDATE_INC_INTERVAL) {
                lastImageUpdateInterval = MAX_UPDATE_INC_INTERVAL;
              }
              if (!replaceImageFlag) {
                FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
                imgPanel.replaceImage(image);
                replaceImageFlag = true;
              }
              currQuality = threads.getRenderThreads().get(0).getTonemapper().calcDensity(displayUpdater.getSampleCount());
              if (currQuality > 0.25) {
                if (currQuality < 0.5)
                  currQuality *= 10;
                else if (currQuality < 1.0)
                  currQuality *= 5;
                for (AbstractRenderThread thread : threads.getRenderThreads()) {
                  thread.getTonemapper().setDensity(currQuality);
                }
                displayUpdater.updateImage(this);
              }
              nextImageUpdate = lastImageUpdateInterval;
            }
            else {
              Thread.sleep(1);
            }
          }
          catch (Throwable e) {
            e.printStackTrace();
          }
        }
      }
      finally {
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
        showLayerPreview(image, 1, panelBounds.width, panelBounds.height);
      }
    }

  }

  @Override
  public void notifyIterationFinished(AbstractRenderThread pEventSource, int pPlotX, int pPlotY, XYZProjectedPoint pProjectedPoint, double pX, double pY, double pZ, double pColorRed, double pColorGreen, double pColorBlue) {
    displayUpdater.iterationFinished(pEventSource, pPlotX, pPlotY);
  }

  public void stopPreviewRendering() {
    cancelBackgroundRender();
  }
}
