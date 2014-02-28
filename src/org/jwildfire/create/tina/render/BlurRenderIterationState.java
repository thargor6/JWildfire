package org.jwildfire.create.tina.render;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.raster.AbstractRasterPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class BlurRenderIterationState extends DefaultRenderIterationState {
  private static final long serialVersionUID = 1L;
  private int blurRadius;
  private double blurFade;
  private double blurKernel[][];
  private int rasterWidth, rasterHeight;
  private RenderColor globalRenderColor = new RenderColor();

  public BlurRenderIterationState(AbstractRenderThread pRenderThread, FlameRenderer pRenderer, RenderPacket pPacket, Layer pLayer, FlameTransformationContext pCtx, AbstractRandomGenerator pRandGen) {
    super(pRenderThread, pRenderer, pPacket, pLayer, pCtx, pRandGen);
  }

  @Override
  public void init() {
    blurKernel = flame.getShadingInfo().createBlurKernel();
    blurRadius = flame.getShadingInfo().getBlurRadius();

    blurFade = flame.getShadingInfo().getBlurFade();
    if (blurFade < 0.0) {
      blurFade = 0.0;
    }
    else if (blurFade > 1.0) {
      blurFade = 1.0;
    }
    rasterWidth = renderer.rasterWidth;
    rasterHeight = renderer.rasterHeight;
  }

  @Override
  protected void plotPoint(int xIdx, int yIdx, double intensity) {
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

    if (ctx.random() > blurFade) {
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
