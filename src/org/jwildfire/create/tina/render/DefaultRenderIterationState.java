/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.base.solidrender.ShadowType;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.render.GammaCorrectionFilter.HSLRGBConverter;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class DefaultRenderIterationState extends RenderIterationState {

  private static final long serialVersionUID = 2L;

  protected XYZPoint affineT;
  protected XYZPoint varT;
  protected XYZPoint p;
  protected XYZPoint q;
  protected XForm xf;
  protected final XYZProjectedPoint prj;
  protected PointProjector projector;
  protected final boolean solidRendering;

  public DefaultRenderIterationState(AbstractRenderThread pRenderThread, FlameRenderer pRenderer, RenderPacket pPacket, Layer pLayer, FlameTransformationContext pCtx, AbstractRandomGenerator pRandGen, boolean pUsePlotBuffer) {
    super(pRenderThread, pRenderer, pPacket, pLayer, pCtx, pRandGen);
    plotBuffer = initPlotBuffer(pUsePlotBuffer ? (RenderMode.PRODUCTION.equals(pRenderer.getRenderInfo().getRenderMode()) ? Tools.PLOT_BUFFER_SIZE : Tools.PLOT_BUFFER_SIZE / 2) : 1);
    solidRendering = flame.getSolidRenderSettings().isSolidRenderingEnabled();
    projector = new DefaultPointProjector();

    boolean withLightmaps = ShadowType.areShadowsEnabled(flame.getSolidRenderSettings().getShadowType());
    prj = new XYZProjectedPoint(withLightmaps ? flame.getSolidRenderSettings().getLights().size() : 0);

    Flame flame = pPacket.getFlame();
    switch (flame.getPostSymmetryType()) {
      case POINT: {
        if (flame.getPostSymmetryOrder() > 1) {
          int order = flame.getPostSymmetryOrder() <= 64 ? flame.getPostSymmetryOrder() : 64;
          projector = new PointSymmetryProjector(projector, order, flame.getPostSymmetryCentreX(), flame.getPostSymmetryCentreY());
        }
        break;
      }
      case X_AXIS:
        projector = new XAxisSymmetryProjector(projector, flame.getPostSymmetryDistance(), flame.getPostSymmetryCentreX(), flame.getPostSymmetryCentreY(), flame.getPostSymmetryRotation());
        break;
      case Y_AXIS:
        projector = new YAxisSymmetryProjector(projector, flame.getPostSymmetryDistance(), flame.getPostSymmetryCentreX(), flame.getPostSymmetryCentreY(), flame.getPostSymmetryRotation());
        break;
      default: // nothing to do
        break;
    }
  }

  public void init() {
    if (raster != null)
      raster.notifyInit(view.getLightViewCalculator());
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
    p.material = randGen.random();
    p.modGamma = 0.0;
    p.modContrast = 0.0;
    p.modSaturation = 0.0;
    p.modHue = 0.0;

    xf = layer.getXForms().get(0);
    transformPoint();
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = selectNextXForm(xf);
      if (xf == null) {
        xf = layer.getXForms().get(0);
        return;
      }
      transformPoint();
    }
  }

  protected XForm selectNextXForm(XForm pFrom) {
    return pFrom.getNextAppliedXFormTable()[randGen.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
  }

  public void validateState() {
    if (Double.isInfinite(p.x) || Double.isInfinite(p.y) || Double.isInfinite(p.z) || Double.isNaN(p.x) || Double.isNaN(p.y) || Double.isNaN(p.z) || xf == null) {
      preFuseIter();
    }
  }

  public void iterateNext() {
    if (layer.getDensity() < 1 && randGen.random() > layer.getDensity()*layer.getDensity())
      return;
    xf = selectNextXForm(xf);
    transformPoint();
    if (xf.getDrawMode() == DrawMode.HIDDEN)
      return;
    else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (randGen.random() > xf.getOpacity()))
      return;
    List<XForm> finalXForms = layer.getFinalXForms();
    if (finalXForms.size() > 0) {
      applyFinalTransforms(finalXForms);
    }
    else {
      applyEmptyFinalTransform();
    }
    projector.projectPoint(q);
  }

  public void iterateNext(List<RenderSlice> pSlices) {
    if (layer.getDensity() < 1 && randGen.random() > layer.getDensity()*layer.getDensity())
      return;
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
    }
    else {
      applyEmptyFinalTransform();
    }

    if (setupSliceRaster(q, pSlices)) {
      projector.projectPoint(q);
    }
  }

  private boolean setupSliceRaster(XYZPoint pPoint, List<RenderSlice> pSlices) {
    int sliceIdx = getSliceIndex(pPoint, pSlices, 0);
    if (sliceIdx >= 0) {
      raster = pSlices.get(sliceIdx).getRaster();
      return true;
    }
    else {
      raster = null;
      return false;
    }
  }

  private int getSliceIndex(XYZPoint pPoint, List<RenderSlice> pSlices, int pStartIndex) {
    double value = pPoint.z;
    for (int i = pStartIndex; i < pSlices.size(); i++) {
      RenderSlice slice = pSlices.get(i);
      if (value >= slice.getZmin() && value < slice.getZmax()) {
        return i;
      }
    }
    return -1;
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

  protected double plotRed, plotGreen, plotBlue, plotContribution;
  protected double dimishZRed = flame.getDimishZRed();
  protected double dimishZGreen = flame.getDimishZGreen();
  protected double  dimishZBlue = flame.getDimishZBlue();

  protected int plotBufferIdx = 0;

  protected PlotSample[] plotBuffer;

  protected int shadowMapPlotBufferIdx[] = initShadowMapPlotBufferIdx();
  protected PlotSample[][] shadowMapPlotBuffer = initShadowMapPlotBuffer();

  protected double lerp(double a, double b, double t) {
    return (1 - t) * a + t * b;
  }
  protected void plotPoint(int screenX, int screenY, double rawX, double rawY, double dzIntensity, double lIntensity, XYZPoint origin) {
    plotRed = origin.redColor;
    plotGreen = origin.greenColor;
    plotBlue = origin.blueColor;
    
    dimishZRed = flame.getDimishZRed();
    dimishZGreen = flame.getDimishZGreen();
    dimishZBlue = flame.getDimishZBlue();

    if (!solidRendering)
      transformPlotColor(p);
    double finalRed = lerp(dimishZRed, plotRed,  dzIntensity) * lIntensity;
    double finalGreen = lerp(dimishZGreen, plotGreen,  dzIntensity) * lIntensity;
    double finalBlue = lerp(dimishZBlue, plotBlue,  dzIntensity) * lIntensity;
    plotBuffer[plotBufferIdx++].set(screenX, screenY, finalRed, finalGreen, finalBlue, rawX, rawY, prj.z * view.bws, p.material, prj.dofDist, origin.x, origin.y, origin.z, p.receiveOnlyShadows);
    if (plotBufferIdx >= plotBuffer.length) {
      applySamplesToRaster();
    }
    if (observers != null && observers.size() > 0) {
      for (IterationObserver observer : observers) {
        observer.notifyIterationFinished(renderThread, screenX, screenY, prj, q.x, q.y, q.z, finalRed, finalGreen, finalBlue);
      }
    }
  }

  private PlotSample[][] initShadowMapPlotBuffer() {
    if (flame.isWithShadows()) {
      PlotSample[][] res = new PlotSample[flame.getSolidRenderSettings().getLights().size()][];
      for (int i = 0; i < flame.getSolidRenderSettings().getLights().size(); i++) {
        if (flame.getSolidRenderSettings().getLights().get(i).isCastShadows()) {
          res[i] = new PlotSample[Tools.PLOT_BUFFER_SIZE];
          for (int j = 0; j < res[i].length; j++) {
            res[i][j] = new PlotSample();
          }
        }
        else {
          res[i] = null;
        }
      }
      return res;
    }
    else {
      return new PlotSample[0][];
    }
  }

  private int[] initShadowMapPlotBufferIdx() {
    if (flame.isWithShadows()) {
      return new int[flame.getSolidRenderSettings().getLights().size()];
    }
    else {
      return new int[0];
    }
  }

  protected void applySamplesToRaster() {
    raster.addSamples(plotBuffer, plotBufferIdx);
    plotBufferIdx = 0;
  }

  private PlotSample[] initPlotBuffer(int pSize) {
    PlotSample[] res = new PlotSample[pSize];
    for (int i = 0; i < res.length; i++) {
      res[i] = new PlotSample();
    }
    return res;
  }

  protected void transformPlotColor(XYZPoint p) {
    if (fabs(p.modGamma) > EPSILON) {
      double gamma = 4.2 / (4.2 - p.modGamma);
      double alpha = plotRed * 0.299 + plotGreen * 0.588 + plotBlue * 0.113;
      if (alpha > EPSILON) {
        double modAlpha = Math.pow(alpha, gamma);
        plotRed *= modAlpha / alpha;
        plotGreen *= modAlpha / alpha;
        plotBlue *= modAlpha / alpha;
      }
    }
    if (fabs(p.modContrast) > EPSILON) {
      double gamma = 1.2 / (1.2 - p.modContrast * 0.5);
      plotRed = (plotRed - 127.5) * gamma + 127.5;
      plotGreen = (plotGreen - 127.5) * gamma + 127.5;
      plotBlue = (plotBlue - 127.5) * gamma + 127.5;
    }
    if (fabs(p.modSaturation) > EPSILON) {
      double avg = plotRed * 0.299 + plotGreen * 0.588 + plotBlue * 0.113;
      plotRed += (plotRed - avg) * p.modSaturation;
      plotGreen += (plotGreen - avg) * p.modSaturation;
      plotBlue += (plotBlue - avg) * p.modSaturation;
    }
    if (fabs(p.modHue) > EPSILON) {
      hslrgbConverter.fromRgb(plotRed / MathLib.C_255, plotGreen / MathLib.C_255, plotBlue / MathLib.C_255);
      hslrgbConverter.fromHsl(hslrgbConverter.getHue() + p.modHue, hslrgbConverter.getSaturation(), hslrgbConverter.getLuminosity());
      plotRed = Tools.roundColor(hslrgbConverter.getRed() * MathLib.C_255);
      plotGreen = Tools.roundColor(hslrgbConverter.getGreen() * MathLib.C_255);
      plotBlue = Tools.roundColor(hslrgbConverter.getBlue() * MathLib.C_255);
    }
  }

  HSLRGBConverter hslrgbConverter = new HSLRGBConverter();

  public interface PointProjector {
    void projectPoint(XYZPoint q);
  }

  public abstract class AxisSymmetryProjector implements PointProjector {
    protected final PointProjector parent;
    protected final double distance;
    protected final double centreX;
    protected final double centreY;
    protected final double rotation;
    protected final XYZPoint a;
    protected final XYZPoint b;
    protected final double sina, cosa;
    protected final double halve_dist;
    protected final boolean doRotate;

    public AxisSymmetryProjector(PointProjector pParent, double pDistance, double pCentreX, double pCentreY, double pRotation) {
      parent = pParent;
      distance = pDistance;
      centreX = pCentreX;
      centreY = pCentreY;
      rotation = pRotation;
      a = new XYZPoint();
      b = new XYZPoint();

      double a = rotation * M_2PI / 180.0 / 2.0;
      doRotate = fabs(a) > EPSILON;

      sina = sin(a);
      cosa = cos(a);
      halve_dist = distance / 2.0;
    }
  }

  public class XAxisSymmetryProjector extends AxisSymmetryProjector implements PointProjector {

    public XAxisSymmetryProjector(PointProjector pParent, double pDistance, double pCentreX, double pCentreY, double pRotation) {
      super(pParent, pDistance, pCentreX, pCentreY, pRotation);
    }

    @Override
    public void projectPoint(XYZPoint q) {
      if (q.doHide)
        return;
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

    public YAxisSymmetryProjector(PointProjector pParent, double pDistance, double pCentreX, double pCentreY, double pRotation) {
      super(pParent, pDistance, pCentreX, pCentreY, pRotation);
    }

    @Override
    public void projectPoint(XYZPoint q) {
      if (q.doHide)
        return;
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

  public static class PointSymmetryProjector implements PointProjector {
    private final PointProjector parent;
    private final double centreX, centreY;
    private final int order;
    private final double sina[], cosa[];
    private final XYZPoint ps;

    public PointSymmetryProjector(PointProjector pParent, int pOrder, double pCentreX, double pCentreY) {
      parent = pParent;
      order = pOrder >= 1 ? pOrder : 1;
      centreX = pCentreX;
      centreY = pCentreY;
      ps = new XYZPoint();
      sina = new double[order];
      cosa = new double[order];
      double da = M_2PI / (double) order;
      double angle = 0.0;
      for (int i = 0; i < order; i++) {
        sina[i] = sin(angle);
        cosa[i] = cos(angle);
        angle += da;
      }
    }

    @Override
    public void projectPoint(XYZPoint q) {
      if (q.doHide)
        return;
      double dx = q.x - centreX;
      double dy = q.y - centreY;
      parent.projectPoint(q);
      for (int i = 0; i < order; i++) {
        ps.assign(q);
        ps.x = centreX + dx * cosa[i] + dy * sina[i];
        ps.y = centreY + dy * cosa[i] - dx * sina[i];
        parent.projectPoint(ps);
      }
    }
  }

  public class DefaultPointProjector implements PointProjector {
    XYZPoint untransformed = new XYZPoint();

    @Override
    public void projectPoint(XYZPoint q) {
      if (q.doHide)
        return;
      // TODO overkill, need only xyz
      untransformed.assign(q);
      boolean insideView = view.project(q, prj);
      if (prj.hasLight != null && !p.receiveOnlyShadows) {
        for (int i = 0; i < prj.hasLight.length; i++) {
          if (prj.hasLight[i]) {
            plotShadowMapPoint(i, prj.lightX[i], prj.lightY[i], prj.lightZ[i]);
          }
        }
      }
      if (!insideView || q.isNaN())
        return;

      double rawX, rawY;
      int screenX, screenY;
      if ((flame.getAntialiasAmount() > EPSILON) && (flame.getAntialiasRadius() > EPSILON) && (randGen.random() > 1.0 - flame.getAntialiasAmount())) {
        double dr = exp(flame.getAntialiasRadius() * sqrt(-log(randGen.random()))) - 1.0;
        double da = randGen.random() * 2.0 * M_PI;
        rawX = view.bws * prj.x + dr * cos(da);
        screenX = (int) (rawX + 0.5);
        if (screenX < 0 || screenX >= renderer.rasterWidth)
          return;
        rawY = view.bhs * prj.y + dr * sin(da);
        screenY = (int) (rawY + 0.5);
        if (screenY < 0 || screenY >= renderer.rasterHeight)
          return;
      }
      else {
        rawX = view.bws * prj.x;
        screenX = (int) (rawX + 0.5);
        if (screenX < 0 || screenX >= renderer.rasterWidth)
          return;
        rawY = view.bhs * prj.y;
        screenY = (int) (rawY + 0.5);
        if (screenY < 0 || screenY >= renderer.rasterHeight)
          return;
      }
      plotPoint(screenX, screenY, rawX, rawY, prj.intensity, layer.getWeight(), untransformed);
    }

    private void plotShadowMapPoint(int i, double x, double y, double z) {
      shadowMapPlotBuffer[i][shadowMapPlotBufferIdx[i]++].set(x, y, z);
      if (shadowMapPlotBufferIdx[i] >= shadowMapPlotBuffer[i].length) {
        applyShadowMapSamplesToRaster(i);
      }
    }

    private void applyShadowMapSamplesToRaster(int idx) {
      raster.addShadowMapSamples(idx, shadowMapPlotBuffer[idx], shadowMapPlotBufferIdx[idx]);
      shadowMapPlotBufferIdx[idx] = 0;
    }
  }

  public void cleanup() {
    if (plotBufferIdx > 0) {
      applySamplesToRaster();
    }
  }
}
