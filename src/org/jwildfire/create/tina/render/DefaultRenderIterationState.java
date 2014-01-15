package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.log;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import java.util.List;

import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.base.raster.AbstractRasterPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class DefaultRenderIterationState extends RenderIterationState {

  private static final long serialVersionUID = 1L;

  protected XYZPoint affineT;
  protected XYZPoint varT;
  protected XYZPoint p;
  protected XYZPoint q;
  protected XForm xf;
  protected final XYZProjectedPoint prj = new XYZProjectedPoint();

  public DefaultRenderIterationState(AbstractRenderThread pRenderThread, FlameRenderer pRenderer, Flame pFlame, Layer pLayer, FlameTransformationContext pCtx, AbstractRandomGenerator pRandGen) {
    super(pRenderThread, pRenderer, pFlame, pLayer, pCtx, pRandGen);
  }

  public void init() {
  }

  public void preFuseIter() {
    affineT = new XYZPoint(); // affine part of the transformation
    varT = new XYZPoint(); // complete transformation
    p = new XYZPoint();
    q = new XYZPoint();
    p.x = 2.0 * randGen.random() - 1.0;
    p.y = 2.0 * randGen.random() - 1.0;
    p.z = 0.0;
    p.color = randGen.random();

    xf = layer.getXForms().get(0);
    xf.transformPoint(ctx, affineT, varT, p, p);
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = xf.getNextAppliedXFormTable()[randGen.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        xf = layer.getXForms().get(0);
        return;
      }
      xf.transformPoint(ctx, affineT, varT, p, p);
    }
  }

  public void validateState() {
    if (Double.isInfinite(p.x) || Double.isInfinite(p.y) || Double.isInfinite(p.z) || Double.isNaN(p.x) || Double.isNaN(p.y) || Double.isNaN(p.z) || xf == null) {
      preFuseIter();
    }
  }

  public void iterateNext() {
    int nextXForm = randGen.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE);
    xf = xf.getNextAppliedXFormTable()[nextXForm];
    if (xf == null) {
      return;
    }

    xf.transformPoint(ctx, affineT, varT, p, p);
    if (xf.getDrawMode() == DrawMode.HIDDEN)
      return;
    else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (randGen.random() > xf.getOpacity()))
      return;
    List<XForm> finalXForms = layer.getFinalXForms();

    int xIdx, yIdx;
    if (finalXForms.size() > 0) {
      finalXForms.get(0).transformPoint(ctx, affineT, varT, p, q);
      for (int i = 1; i < finalXForms.size(); i++) {
        finalXForms.get(i).transformPoint(ctx, affineT, varT, q, q);
      }
      if (!renderer.project(q, prj))
        return;
      if ((flame.getAntialiasAmount() > EPSILON) && (flame.getAntialiasRadius() > EPSILON) && (randGen.random() > 1.0 - flame.getAntialiasAmount())) {
        double dr = exp(flame.getAntialiasRadius() * sqrt(-log(randGen.random()))) - 1.0;
        double da = randGen.random() * 2.0 * M_PI;
        xIdx = (int) (renderer.bws * prj.x + dr * cos(da) + 0.5);
        yIdx = (int) (renderer.bhs * prj.y + dr * sin(da) + 0.5);
      }
      else {
        xIdx = (int) (renderer.bws * prj.x + 0.5);
        yIdx = (int) (renderer.bhs * prj.y + 0.5);
      }
    }
    else {
      q.assign(p);
      if (!renderer.project(q, prj))
        return;
      if ((flame.getAntialiasAmount() > EPSILON) && (flame.getAntialiasRadius() > EPSILON) && (randGen.random() > 1.0 - flame.getAntialiasAmount())) {
        double dr = exp(flame.getAntialiasRadius() * sqrt(-log(randGen.random()))) - 1.0;
        double da = randGen.random() * 2.0 * M_PI;
        xIdx = (int) (renderer.bws * prj.x + dr * cos(da) + 0.5);
        yIdx = (int) (renderer.bhs * prj.y + dr * sin(da) + 0.5);
      }
      else {
        xIdx = (int) (renderer.bws * prj.x + 0.5);
        yIdx = (int) (renderer.bhs * prj.y + 0.5);
      }
    }
    if (xIdx < 0 || xIdx >= renderer.rasterWidth)
      return;
    if (yIdx < 0 || yIdx >= renderer.rasterHeight)
      return;
    double intensity = prj.intensity * layer.getWeight();
    drawPoint(xIdx, yIdx, intensity);
  }

  protected void drawPoint(int xIdx, int yIdx, double intensity) {
    AbstractRasterPoint rp = renderer.raster[yIdx][xIdx];
    if (p.rgbColor) {
      rp.setRed(rp.getRed() + p.redColor * intensity);
      rp.setGreen(rp.getGreen() + p.greenColor * intensity);
      rp.setBlue(rp.getBlue() + p.blueColor * intensity);
    }
    else {
      int colorIdx = (int) (p.color * paletteIdxScl + 0.5);
      RenderColor color = colorMap[colorIdx];
      rp.setRed(rp.getRed() + color.red * intensity);
      rp.setGreen(rp.getGreen() + color.green * intensity);
      rp.setBlue(rp.getBlue() + color.blue * intensity);
    }
    rp.incCount();
    if (observers != null && observers.size() > 0) {
      for (IterationObserver observer : observers) {
        observer.notifyIterationFinished(renderThread, xIdx, yIdx);
      }
    }
  }

}
