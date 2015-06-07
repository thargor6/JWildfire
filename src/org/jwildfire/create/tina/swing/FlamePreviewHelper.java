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
import org.jwildfire.create.tina.render.DrawFocusPointFlameRenderer;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
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

public class FlamePreviewHelper {
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

  public void refreshFlameImage(boolean pQuickRender, boolean pMouseDown, int pDownScale, boolean pOversamplingInPreview) {
    if (pQuickRender && detachedPreviewProvider != null && detachedPreviewProvider.getDetachedPreviewController() != null && pDownScale == 1) {
      detachedPreviewProvider.getDetachedPreviewController().setFlame(flameHolder.getFlame());
    }

    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    FlamePanelConfig cfg = flamePanelProvider.getFlamePanelConfig();

    SimpleImage img = renderFlameImage(pQuickRender, pMouseDown, pDownScale, pOversamplingInPreview);
    if (img != null) {
      imgPanel.setImage(img);
      if (!pMouseDown && !cfg.isNoControls() && randomBatchHolder != null) {
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
    }

    if (!cfg.isNoControls()) {
      centerPanel.getParent().validate();
      centerPanel.repaint();
    }
    else {
      imgPanel.repaint();
    }
  }

  public SimpleImage renderFlameImage(boolean pQuickRender, boolean pMouseDown, int pDownScale, boolean pOversamplingInPreview) {
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    FlamePanelConfig cfg = flamePanelProvider.getFlamePanelConfig();

    Rectangle panelBounds = imgPanel.getImageBounds();
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
        int oldOversampling = flame.getOversampling();
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
              flame.setSpatialFilterRadius(0.0);
              if (!pOversamplingInPreview) {
                flame.setOversampling(1);
              }
            }
            else {
              flame.setSampleDensity(prefs.getTinaRenderPreviewQuality());
            }

            if (!cfg.isNoControls() && imgPanel.getConfig().getMouseDragOperation() == MouseDragOperation.FOCUS) {
              renderer = new DrawFocusPointFlameRenderer(flame, prefs, toggleTransparencyButton != null && toggleTransparencyButton.isSelected());
            }
            else {
              renderer = new FlameRenderer(flame, prefs, toggleTransparencyButton != null && toggleTransparencyButton.isSelected(), false);
            }

            if (pQuickRender) {
              renderer.setProgressUpdater(null);
            }
            else {
              renderer.setProgressUpdater(mainProgressUpdater);
            }

            System.err.println(flame.getOversampling());
            renderer.setRenderScale(renderScale);
            long t0 = System.currentTimeMillis();
            RenderedFlame res = renderer.renderFlame(info);
            long t1 = System.currentTimeMillis();
            SimpleImage img = res.getImage();
            img.getBufferedImg().setAccelerationPriority(1.0f);

            if (layerAppendBtn != null && layerAppendBtn.isSelected() && !pMouseDown) {
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

            if (layerPreviewBtn != null && layerPreviewBtn.isSelected() && layerHolder != null) {
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
          flame.setOversampling(oldOversampling);
        }
      }
    }
    return null;
  }

  public SimpleImage fastRenderFlameImage(boolean pQuickRender, boolean pMouseDown, int pDownScale) {
    FlamePanel imgPanel = flamePanelProvider.getFlamePanel();
    FlamePanelConfig cfg = flamePanelProvider.getFlamePanelConfig();

    Rectangle panelBounds = imgPanel.getImageBounds();
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
        double oldAntialiasAmount = flame.getAntialiasAmount();
        try {
          double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
          double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(info.getImageWidth());
          flame.setHeight(info.getImageHeight());
          try {
            FlameRenderer renderer;
            if (!cfg.isNoControls() && imgPanel.getConfig().getMouseDragOperation() == MouseDragOperation.FOCUS) {
              renderer = new DrawFocusPointFlameRenderer(flame, prefs, toggleTransparencyButton != null && toggleTransparencyButton.isSelected());
            }
            else {
              renderer = new FlameRenderer(flame, prefs, toggleTransparencyButton != null && toggleTransparencyButton.isSelected(), false);
            }
            renderer.setProgressUpdater(null);
            flame.setSampleDensity(prefs.getTinaRenderRealtimeQuality());
            flame.setSpatialFilterRadius(0.0);
            flame.setAntialiasAmount(0.0);
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
          flame.setSpatialFilterRadius(oldSpatialFilterRadius);
          flame.setSampleDensity(oldSampleDensity);
          flame.setAntialiasAmount(oldAntialiasAmount);
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
}
