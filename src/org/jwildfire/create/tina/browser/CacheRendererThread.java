package org.jwildfire.create.tina.browser;

import java.util.List;

import javax.swing.JPanel;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.image.SimpleImage;

public class CacheRendererThread implements Runnable {
  private final Prefs prefs;
  private final RenderCache renderCache;
  private final List<RenderJobInfo> jobs;
  private boolean done;
  private boolean cancelSignalled;

  public CacheRendererThread(Prefs pPrefs, RenderCache pRenderCache, List<RenderJobInfo> pJobs) {
    prefs = pPrefs;
    renderCache = pRenderCache;
    jobs = pJobs;
  }

  @Override
  public void run() {
    done = false;
    cancelSignalled = false;
    try {
      for (RenderJobInfo job : jobs) {
        try {
          if (cancelSignalled) {
            break;
          }
          SimpleImage img = renderFlame(job.getFlame(), job.getRenderWidth(), job.getRenderHeight());
          renderCache.putImage(job.getFlame(), img, job.getRenderWidth(), job.getRenderHeight());
          job.getDestPnl().setImage(img);
          job.getDestPnl().setLocation(job.getLocationX(), job.getLocationY());
          redraw((JPanel) job.getDestPnl());
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
    finally {
      done = true;
    }
  }

  private void redraw(JPanel pPnl) {
    pPnl.getParent().validate();
    pPnl.repaint();
  }

  public boolean isDone() {
    return done;
  }

  public void signalCancel() {
    cancelSignalled = true;
  }

  private SimpleImage renderFlame(FlameFlatNode pNode, int pImgWidth, int pImgHeight) {
    List<Flame> flames = new FlameReader(prefs).readFlames(pNode.getFilename());
    if (flames.size() > 0) {
      Flame renderFlame = flames.get(0);
      RenderInfo info = new RenderInfo(pImgWidth, pImgHeight, RenderMode.PREVIEW);
      double wScl = (double) info.getImageWidth() / (double) renderFlame.getWidth();
      double hScl = (double) info.getImageHeight() / (double) renderFlame.getHeight();
      renderFlame.setPixelsPerUnit((wScl + hScl) * 0.5 * renderFlame.getPixelsPerUnit());
      renderFlame.setWidth(pImgWidth);
      renderFlame.setHeight(pImgHeight);
      renderFlame.setSampleDensity(prefs.getTinaRenderPreviewQuality() / 3.0);
      FlameRenderer renderer = new FlameRenderer(renderFlame, prefs, false, true);
      RenderedFlame renderRes = renderer.renderFlame(info);
      return renderRes.getImage();
    }
    return null;
  }

}
