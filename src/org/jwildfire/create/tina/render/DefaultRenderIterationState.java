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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.base.raster.AbstractRasterPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class DefaultRenderIterationState extends RenderIterationState {

  private static final long serialVersionUID = 2L;

  protected XYZPoint affineT;
  protected XYZPoint varT;
  protected XYZPoint p;
  protected XYZPoint q;
  protected XForm xf;
  protected final XYZProjectedPoint prj = new XYZProjectedPoint();
  protected PointProjector projector;

  public DefaultRenderIterationState(AbstractRenderThread pRenderThread, FlameRenderer pRenderer, RenderPacket pPacket, Layer pLayer, FlameTransformationContext pCtx, AbstractRandomGenerator pRandGen) {
    super(pRenderThread, pRenderer, pPacket, pLayer, pCtx, pRandGen);
  }

  public void init() {
    projector = new DefaultPointProjector();
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
    transformPoint();
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = xf.getNextAppliedXFormTable()[randGen.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        xf = layer.getXForms().get(0);
        return;
      }
      transformPoint();
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
    transformPoint();
    if (xf.getDrawMode() == DrawMode.HIDDEN)
      return;
    else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (randGen.random() > xf.getOpacity()))
      return;
    List<XForm> finalXForms = layer.getFinalXForms();
    if (finalXForms.size() > 0) {
      applyFinalTransforms(finalXForms);
      projector.projectPoint(q);
    }
    else {
      applyEmptyFinalTransform();
      projector.projectPoint(q);
    }
  }

  protected void applyEmptyFinalTransform() {
    q.assign(p);
  }

  protected void applyFinalTransforms(List<XForm> finalXForms) {
    finalXForms.get(0).transformPoint(ctx, affineT, varT, p, q);
    for (int i = 1; i < finalXForms.size(); i++) {
      finalXForms.get(i).transformPoint(ctx, affineT, varT, q, q);
    }
  }

  protected void transformPoint() {
    xf.transformPoint(ctx, affineT, varT, p, p);
  }

  protected void plotPoint(int xIdx, int yIdx, double intensity) {
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

  public interface PointProjector {
    void projectPoint(XYZPoint q);
  }

  public class DefaultPointProjector implements PointProjector {

    @Override
    public void projectPoint(XYZPoint q) {
      if (!view.project(q, prj))
        return;
      int xIdx, yIdx;
      if ((flame.getAntialiasAmount() > EPSILON) && (flame.getAntialiasRadius() > EPSILON) && (randGen.random() > 1.0 - flame.getAntialiasAmount())) {
        double dr = exp(flame.getAntialiasRadius() * sqrt(-log(randGen.random()))) - 1.0;
        double da = randGen.random() * 2.0 * M_PI;
        xIdx = (int) (view.bws * prj.x + dr * cos(da) + 0.5);
        if (xIdx < 0 || xIdx >= renderer.rasterWidth)
          return;
        yIdx = (int) (view.bhs * prj.y + dr * sin(da) + 0.5);
        if (yIdx < 0 || yIdx >= renderer.rasterHeight)
          return;
      }
      else {
        xIdx = (int) (view.bws * prj.x + 0.5);
        if (xIdx < 0 || xIdx >= renderer.rasterWidth)
          return;
        yIdx = (int) (view.bhs * prj.y + 0.5);
        if (yIdx < 0 || yIdx >= renderer.rasterHeight)
          return;
      }

      double intensity = prj.intensity * layer.getWeight();
      plotPoint(xIdx, yIdx, intensity);
    }

  }
}
