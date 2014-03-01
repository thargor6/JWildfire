package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.fabs;
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
    projector = new DefaultPointProjector();
    Flame flame = pPacket.getFlame();
    switch (flame.getPostSymmetryType()) {
      case POINT: {
        if (flame.getPostSymmetryPointCount() > 1) {
          int pointCount = flame.getPostSymmetryPointCount() <= 64 ? flame.getPostSymmetryPointCount() : 64;
          projector = new PointSymmetryProjector(projector, pointCount, flame.getPostSymmetryCentreX(), flame.getPostSymmetryCentreY());
        }
        break;
      }
      case X_AXIS:
        projector = new XAxisSymmetryProjector(projector, flame.getPostSymmetryDistance(), flame.getPostSymmetryCentreX(), flame.getPostSymmetryCentreY(), flame.getPostSymmetryAngle());
        break;
      case Y_AXIS:
        projector = new YAxisSymmetryProjector(projector, flame.getPostSymmetryDistance(), flame.getPostSymmetryCentreX(), flame.getPostSymmetryCentreY(), flame.getPostSymmetryAngle());
        break;
    }
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

  public abstract class AxisSymmetryProjector implements PointProjector {
    protected final PointProjector parent;
    protected final double distance;
    protected final double centreX;
    protected final double centreY;
    protected final double angle;
    protected final XYZPoint a;
    protected final XYZPoint b;
    protected final double sina, cosa;
    protected final double halve_dist;
    protected final boolean doRotate;

    public AxisSymmetryProjector(PointProjector pParent, double pDistance, double pCentreX, double pCentreY, double pAngle) {
      parent = pParent;
      distance = pDistance;
      centreX = pCentreX;
      centreY = pCentreY;
      angle = pAngle;
      a = new XYZPoint();
      b = new XYZPoint();

      double a = angle * M_2PI / 180.0 / 2.0;
      doRotate = fabs(a) > EPSILON;

      sina = sin(a);
      cosa = cos(a);
      halve_dist = distance / 2.0;
    }
  }

  public class XAxisSymmetryProjector extends AxisSymmetryProjector implements PointProjector {

    public XAxisSymmetryProjector(PointProjector pParent, double pDistance, double pCentreX, double pCentreY, double pAngle) {
      super(pParent, pDistance, pCentreX, pCentreY, pAngle);
    }

    @Override
    public void projectPoint(XYZPoint q) {
      a.assign(q);
      b.assign(q);
      double dx, dy;
      dx = q.x - centreX;
      a.x = centreX + dx + halve_dist;
      b.x = centreX - dx - halve_dist;
      if (doRotate) {
        dx = a.x - centreX;
        dy = a.y - centreY;
        a.x = centreX + dx * cosa + dy * sina;
        a.y = centreY + dy * cosa - dx * sina;

        dx = b.x - centreX;
        dy = b.y - centreY;
        b.x = centreX + dx * cosa - dy * sina;
        b.y = centreY + dy * cosa + dx * sina;
      }

      parent.projectPoint(a);
      parent.projectPoint(b);
    }

  }

  public class YAxisSymmetryProjector extends AxisSymmetryProjector implements PointProjector {

    public YAxisSymmetryProjector(PointProjector pParent, double pDistance, double pCentreX, double pCentreY, double pAngle) {
      super(pParent, pDistance, pCentreX, pCentreY, pAngle);
    }

    @Override
    public void projectPoint(XYZPoint q) {
      a.assign(q);
      b.assign(q);
      double dx, dy;
      dy = q.y - centreY;
      a.y = centreY + dy + halve_dist;
      b.y = centreY - dy - halve_dist;
      if (doRotate) {
        dx = a.x - centreX;
        dy = a.y - centreY;
        a.x = centreX + dx * cosa + dy * sina;
        a.y = centreY + dy * cosa - dx * sina;

        dx = b.x - centreX;
        dy = b.y - centreY;
        b.x = centreX + dx * cosa - dy * sina;
        b.y = centreY + dy * cosa + dx * sina;
      }

      parent.projectPoint(a);
      parent.projectPoint(b);
    }

  }

  public class PointSymmetryProjector implements PointProjector {
    private final PointProjector parent;
    private final double centreX, centreY;
    private final int pointCount;
    private final double sina[], cosa[];
    private final XYZPoint ps;

    public PointSymmetryProjector(PointProjector pParent, int pPointCount, double pCentreX, double pCentreY) {
      parent = pParent;
      pointCount = pPointCount >= 1 ? pPointCount : 1;
      centreX = pCentreX;
      centreY = pCentreY;
      ps = new XYZPoint();
      sina = new double[pointCount];
      cosa = new double[pointCount];
      double da = M_2PI / (double) pointCount;
      double angle = 0.0;
      for (int i = 0; i < pointCount; i++) {
        sina[i] = sin(angle);
        cosa[i] = cos(angle);
        angle += da;
      }
    }

    @Override
    public void projectPoint(XYZPoint q) {
      double dx = q.x - centreX;
      double dy = q.y - centreY;
      parent.projectPoint(q);
      for (int i = 1; i < pointCount; i++) {
        ps.assign(q);
        ps.x = centreX + dx * cosa[i] + dy * sina[i];
        ps.y = centreY + dy * cosa[i] - dx * sina[i];
        parent.projectPoint(ps);
      }
    }
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
