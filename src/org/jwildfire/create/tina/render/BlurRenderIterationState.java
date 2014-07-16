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
    if (p.rgbColor) {
      plotRed = p.redColor;
      plotGreen = p.greenColor;
      plotBlue = p.blueColor;
    }
    else {
      int colorIdx = (int) (p.color * paletteIdxScl + 0.5);
      RenderColor color = colorMap[colorIdx];
      plotRed = color.red;
      plotGreen = color.green;
      plotBlue = color.blue;
    }
    transformPlotColor(p);

    if (ctx.random() > blurFade) {
      for (int k = yIdx - blurRadius, yk = 0; k <= yIdx + blurRadius; k++, yk++) {
        if (k >= 0 && k < rasterHeight) {
          for (int l = xIdx - blurRadius, xk = 0; l <= xIdx + blurRadius; l++, xk++) {
            if (l >= 0 && l < rasterWidth) {
              // y, x
              AbstractRasterPoint rp = raster[k][l];
              double scl = blurKernel[yk][xk];
              rp.setRed(rp.getRed() + plotRed * scl * prj.intensity);
              rp.setGreen(rp.getGreen() + plotGreen * scl * prj.intensity);
              rp.setBlue(rp.getBlue() + plotBlue * scl * prj.intensity);
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
      AbstractRasterPoint rp = raster[yIdx][xIdx];
      rp.setRed(rp.getRed() + plotRed * prj.intensity);
      rp.setGreen(rp.getGreen() + plotGreen * prj.intensity);
      rp.setBlue(rp.getBlue() + plotBlue * prj.intensity);
      rp.incCount();
      if (observers != null && observers.size() > 0) {
        for (IterationObserver observer : observers) {
          observer.notifyIterationFinished(renderThread, xIdx, yIdx);
        }
      }
    }
  }
}
