package org.jwildfire.create.tina.render.backdrop;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.render.LogDensityFilter;
import org.jwildfire.create.tina.render.LogDensityPoint;
import org.jwildfire.create.tina.render.image.AbstractImageRenderThread;
import org.jwildfire.image.SimpleImage;

public class FlameBackgroundRenderThread extends AbstractImageRenderThread {
  private final LogDensityFilter logDensityFilter;
  private final int startRow, endRow;
  private final SimpleImage image;
  private final LogDensityPoint logDensityPoint;

  private final FlameBackgroundRenderContext ctx;

  public FlameBackgroundRenderThread(Flame pFlame, int pStartRow, int pEndRow, SimpleImage pImage, int pThreadID) {
    startRow = pStartRow;
    endRow = pEndRow;
    image = pImage;
    ctx = new FlameBackgroundRenderContext(pFlame, pThreadID);
    logDensityFilter = new LogDensityFilter(ctx.getPreparedFlame(), new MarsagliaRandomGenerator());
    logDensityFilter.setRaster(null, 0, 0, image.getImageWidth(), image.getImageHeight());
    logDensityPoint = new LogDensityPoint(1);
  }

  @Override
  public void run() {
    setDone(false);
    try {
      for (int i = startRow; i < endRow; i++) {
        for (int j = 0; j < image.getImageWidth(); j++) {
          logDensityFilter.calculateBGColor(ctx, logDensityPoint, j, i);
          image.setRGB(j, i, Tools.roundColor(logDensityPoint.bgRed), Tools.roundColor(logDensityPoint.bgGreen), Tools.roundColor(logDensityPoint.bgBlue));
        }
      }
    } finally {
      setDone(true);
    }
  }

}
