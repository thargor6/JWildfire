/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
import org.jwildfire.create.tina.base.raster.AbstractRasterPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class Pseudo3DRenderIterationState extends DefaultRenderIterationState {
  private static final long serialVersionUID = 1L;
  protected XYZPoint[] affineTA;
  protected XYZPoint[] varTA;
  protected XYZPoint[] pA;
  protected XYZPoint[] qA;
  protected XYZPoint r;
  Pseudo3DShader shader;

  public Pseudo3DRenderIterationState(AbstractRenderThread pRenderThread, FlameRenderer pRenderer, RenderPacket pPacket, Layer pLayer, FlameTransformationContext pCtx, AbstractRandomGenerator pRandGen) {
    super(pRenderThread, pRenderer, pPacket, pLayer, pCtx, pRandGen);
  }

  @Override
  public void init() {
    shader = new Pseudo3DShader(flame.getShadingInfo());
    shader.init();
  }

  @Override
  public void preFuseIter() {
    affineTA = new XYZPoint[3]; // affine part of the transformation
    for (int i = 0; i < affineTA.length; i++) {
      affineTA[i] = new XYZPoint();
    }
    varTA = new XYZPoint[3]; // complete transformation
    for (int i = 0; i < varTA.length; i++) {
      varTA[i] = new XYZPoint();
    }
    pA = new XYZPoint[3];
    for (int i = 0; i < pA.length; i++) {
      pA[i] = new XYZPoint();
    }
    r = new XYZPoint();

    pA[0].x = 2.0 * randGen.random() - 1.0;
    pA[0].y = 2.0 * randGen.random() - 1.0;
    pA[0].z = 0;
    pA[0].color = randGen.random();

    distributeInitialPoints(pA);
    qA = new XYZPoint[3];
    for (int i = 0; i < qA.length; i++) {
      qA[i] = new XYZPoint();
    }

    xf = layer.getXForms().get(0);
    xf.transformPoints(ctx, affineTA, varTA, pA, pA);
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = xf.getNextAppliedXFormTable()[randGen.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
      xf.transformPoints(ctx, affineTA, varTA, pA, pA);
    }
  }

  private void distributeInitialPoints(XYZPoint[] p) {
    p[1].x = p[0].x + 0.001;
    p[1].y = p[0].y;
    p[1].z = p[0].z;
    p[2].x = p[0].x;
    p[2].y = p[0].y + 0.001;
    p[2].z = p[0].z;
  }

  @Override
  public void iterateNext() {
    xf = xf.getNextAppliedXFormTable()[randGen.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
    if (xf == null) {
      return;
    }
    xf.transformPoints(ctx, affineTA, varTA, pA, pA);
    if (xf.getDrawMode() == DrawMode.HIDDEN)
      return;
    else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (randGen.random() > xf.getOpacity()))
      return;

    List<XForm> finalXForms = layer.getFinalXForms();
    int xIdx, yIdx;
    if (finalXForms.size() > 0) {
      for (int pIdx = 0; pIdx < pA.length; pIdx++) {
        qA[pIdx] = new XYZPoint();
      }
      finalXForms.get(0).transformPoints(ctx, affineTA, varTA, pA, qA);
      for (int i = 1; i < finalXForms.size(); i++) {
        finalXForms.get(i).transformPoints(ctx, affineTA, varTA, qA, qA);
      }
      r.assign(qA[0]);
      if (!view.project(r, prj))
        return;

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
        yIdx = (int) (view.bhs * prj.y + 0.5);
      }

    }
    else {
      for (int pIdx = 0; pIdx < pA.length; pIdx++) {
        qA[pIdx] = new XYZPoint();
        qA[pIdx].assign(pA[pIdx]);
      }
      r.assign(qA[0]);
      if (!view.project(r, prj))
        return;

      if ((flame.getAntialiasAmount() > EPSILON) && (flame.getAntialiasRadius() > EPSILON) && (randGen.random() > 1.0 - flame.getAntialiasAmount())) {
        double dr = exp(flame.getAntialiasRadius() * sqrt(-log(randGen.random()))) - 1.0;
        double da = randGen.random() * 2.0 * M_PI;
        xIdx = (int) (view.bws * prj.x + dr * cos(da) + 0.5);
        yIdx = (int) (view.bhs * prj.y + dr * sin(da) + 0.5);
      }
      else {
        xIdx = (int) (view.bws * prj.x + 0.5);
        yIdx = (int) (view.bhs * prj.y + 0.5);
      }
    }
    if (xIdx < 0 || xIdx >= renderer.rasterWidth)
      return;
    if (yIdx < 0 || yIdx >= renderer.rasterHeight)
      return;

    AbstractRasterPoint rp = renderer.raster[yIdx][xIdx];
    RenderColor color;
    if (pA[0].rgbColor) {
      color = new RenderColor();
      color.red = pA[0].redColor;
      color.green = pA[0].greenColor;
      color.blue = pA[0].blueColor;
    }
    else {
      color = colorMap[(int) (pA[0].color * paletteIdxScl + 0.5)];
    }
    RenderColor shadedColor = shader.calculateColor(qA, color);
    rp.setRed(rp.getRed() + shadedColor.red * prj.intensity);
    rp.setGreen(rp.getGreen() + shadedColor.green * prj.intensity);
    rp.setBlue(rp.getBlue() + shadedColor.blue * prj.intensity);
    rp.incCount();
    if (observers != null && observers.size() > 0) {
      for (IterationObserver observer : observers) {
        observer.notifyIterationFinished(renderThread, xIdx, yIdx);
      }
    }
  }

  @Override
  public void validateState() {
    for (int pIdx = 0; pIdx < pA.length; pIdx++) {
      if (Double.isInfinite(pA[pIdx].x) || Double.isInfinite(pA[pIdx].y) || Double.isInfinite(pA[pIdx].z) || Double.isNaN(pA[pIdx].x) || Double.isNaN(pA[pIdx].y) || Double.isNaN(pA[pIdx].z)) {
        preFuseIter();
        break;
      }
    }
  }

}
