package org.jwildfire.create.tina.render;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.raster.AbstractRasterPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class BlurRenderIterationState extends DefaultRenderIterationState {
  private static final long serialVersionUID = 1L;
  private long blurMax;
  private int blurRadius;
  private double blurKernel[][];
  private int rasterWidth, rasterHeight;
  private RenderColor globalRenderColor = new RenderColor();

  // TODO remove samples parameter
  public BlurRenderIterationState(AbstractRenderThread pRenderThread, FlameRenderer pRenderer, Flame pFlame, Layer pLayer, FlameTransformationContext pCtx, AbstractRandomGenerator pRandGen) {
    super(pRenderThread, pRenderer, pFlame, pLayer, pCtx, pRandGen);
  }

  @Override
  public void init() {
    blurKernel = flame.getShadingInfo().createBlurKernel();
    //int blurRadius = (int) (flame.getShadingInfo().getBlurRadius() * (flame.getPixelsPerUnit() / 200.0));
    blurRadius = flame.getShadingInfo().getBlurRadius();

    double fade = flame.getShadingInfo().getBlurFade();
    if (fade < 0.0) {
      fade = 0.0;
    }
    else if (fade > 1.0) {
      fade = 1.0;
    }
    blurMax = (long) ((1 - fade) * flame.getSampleDensity() * 100000);

    rasterWidth = renderer.rasterWidth;
    rasterHeight = renderer.rasterHeight;
  }

  @Override
  protected void drawPoint(int xIdx, int yIdx, double intensity) {
    RenderColor color;
    if (p.rgbColor) {
      color = globalRenderColor;
      color.red = p.redColor;
      color.green = p.greenColor;
      color.blue = p.blueColor;
    }
    else {
      int colorIdx = (int) (p.color * paletteIdxScl + 0.5);
      color = colorMap[colorIdx];
    }

    if (renderThread.getIter() < blurMax) {
      for (int k = yIdx - blurRadius, yk = 0; k <= yIdx + blurRadius; k++, yk++) {
        if (k >= 0 && k < rasterHeight) {
          for (int l = xIdx - blurRadius, xk = 0; l <= xIdx + blurRadius; l++, xk++) {
            if (l >= 0 && l < rasterWidth) {
              // y, x
              AbstractRasterPoint rp = renderer.raster[k][l];
              double scl = blurKernel[yk][xk];
              rp.setRed(rp.getRed() + color.red * scl * prj.intensity);
              rp.setGreen(rp.getGreen() + color.green * scl * prj.intensity);
              rp.setBlue(rp.getBlue() + color.blue * scl * prj.intensity);
              rp.incCount();
              if (observers != null && observers.size() > 0) {
                for (IterationObserver observer : observers) {
                  observer.notifyIterationFinished(renderThread, k, l);
                }
              }
            }
          }
        }
      }
    }
    else {
      AbstractRasterPoint rp = renderer.raster[yIdx][xIdx];
      rp.setRed(rp.getRed() + color.red * prj.intensity);
      rp.setGreen(rp.getGreen() + color.green * prj.intensity);
      rp.setBlue(rp.getBlue() + color.blue * prj.intensity);
      rp.incCount();
      if (observers != null && observers.size() > 0) {
        for (IterationObserver observer : observers) {
          observer.notifyIterationFinished(renderThread, xIdx, yIdx);
        }
      }
    }
  }
}
