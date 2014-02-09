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
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.log;
import static org.jwildfire.base.mathlib.MathLib.pow;
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

public class DistanceColorRenderIterationState extends DefaultRenderIterationState {
  private static final long serialVersionUID = 1L;

  protected XYZPoint p0 = new XYZPoint();
  protected XYZPoint p1 = new XYZPoint();
  protected XYZPoint p2 = new XYZPoint();

  private double radius;
  private double scale;
  private double exponent;
  private double offsetX;
  private double offsetY;
  private double offsetZ;
  private int style;
  private int coordinate;
  private double shift;

  public DistanceColorRenderIterationState(AbstractRenderThread pRenderThread, FlameRenderer pRenderer, RenderPacket pPacket, Layer pLayer, FlameTransformationContext pCtx, AbstractRandomGenerator pRandGen) {
    super(pRenderThread, pRenderer, pPacket, pLayer, pCtx, pRandGen);
  }

  @Override
  public void init() {
    radius = flame.getShadingInfo().getDistanceColorRadius();
    scale = flame.getShadingInfo().getDistanceColorScale();
    exponent = flame.getShadingInfo().getDistanceColorExponent();
    offsetX = flame.getShadingInfo().getDistanceColorOffsetX();
    offsetY = flame.getShadingInfo().getDistanceColorOffsetY();
    offsetZ = flame.getShadingInfo().getDistanceColorOffsetZ();
    style = flame.getShadingInfo().getDistanceColorStyle();
    coordinate = flame.getShadingInfo().getDistanceColorCoordinate();
    shift = flame.getShadingInfo().getDistanceColorShift();
  }

  public void iterateNext() {
    final double DX = 0.01;
    int nextXForm = randGen.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE);
    xf = xf.getNextAppliedXFormTable()[nextXForm];
    if (xf == null) {
      return;
    }
    switch (style) {
      case 0:
        p0.assign(p);
        xf.transformPoint(ctx, affineT, varT, p, p);
        break;
      case 1:
        p0.assign(p);
        p1.assign(p);
        p0.x -= DX;
        p0.y -= DX;
        p0.z -= DX;
        p1.x += DX;
        p1.y += DX;
        p1.z += DX;
        xf.transformPoint(ctx, affineT, varT, p0, p0);
        xf.transformPoint(ctx, affineT, varT, p1, p1);
        xf.transformPoint(ctx, affineT, varT, p, p);
        break;
      case 2:
        p0.assign(p);
        p1.assign(p);
        p2.assign(p);
        p0.x -= DX;
        p0.y -= DX;
        p0.z -= DX;
        p2.x += DX;
        p2.y += DX;
        p2.z += DX;
        xf.transformPoint(ctx, affineT, varT, p0, p0);
        xf.transformPoint(ctx, affineT, varT, p1, p1);
        xf.transformPoint(ctx, affineT, varT, p2, p2);
        xf.transformPoint(ctx, affineT, varT, p, p);
        break;
      default:
        throw new IllegalArgumentException(String.valueOf(style));
    }

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
      if (!view.project(q, prj))
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
    else {
      q.assign(p);
      if (!view.project(q, prj))
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

    double cx, cy, cz;
    switch (style) {
      case 0:
        cx = p.x - p0.x + offsetX;
        cy = p.y - p0.y + offsetY;
        cz = p.z - p0.z + offsetZ;
        break;
      case 1:
        cx = 0.5 * (p1.x - p0.x) / DX + offsetX;
        cy = 0.5 * (p1.y - p0.y) / DX + offsetY;
        cz = 0.5 * (p1.z - p0.z) / DX + offsetZ;
        break;
      case 2: {
        double v1x = 0.5 * (p1.x - p0.x) / DX;
        double v1y = 0.5 * (p1.y - p0.y) / DX;
        double v1z = 0.5 * (p1.z - p0.z) / DX;
        double v2x = 0.5 * (p2.x - p1.x) / DX;
        double v2y = 0.5 * (p2.y - p1.y) / DX;
        double v2z = 0.5 * (p2.z - p1.z) / DX;
        cx = 0.5 * (v2x - v1x) / DX + offsetX;
        cy = 0.5 * (v2y - v1y) / DX + offsetY;
        cz = 0.5 * (v2z - v1z) / DX + offsetZ;
      }
        break;
      default:
        throw new IllegalArgumentException(String.valueOf(style));
    }
    double r;
    switch (coordinate) {
      case 0 /* r */:
        r = radius * pow(scale * (cx * cx + cy * cy + cz * cz), exponent);
        break;
      case 1 /* x */:
        r = radius * pow(scale * (cx * cx), exponent);
        break;
      case 2 /* y */:
        r = radius * pow(scale * (cy * cy), exponent);
        break;
      case 3 /* z */:
        r = radius * pow(scale * (cz * cz), exponent);
        break;
      case 4 /* rho */:
        r = radius * pow(scale * (atan2(cy, cx)), exponent);
        break;
      case 5 /* phi */:
        r = radius * pow(scale * (atan2(cz, cy)), exponent);
        break;
      default:
        throw new IllegalArgumentException(String.valueOf(coordinate));
    }
    p.color = r + shift;
    if (p.color < 0.0)
      p.color = 0.0;
    else if (p.color >= 1)
      p.color = 1;

    if (p.rgbColor) {
      rp.setRed(rp.getRed() + p.redColor * prj.intensity);
      rp.setGreen(rp.getGreen() + p.greenColor * prj.intensity);
      rp.setBlue(rp.getBlue() + p.blueColor * prj.intensity);
    }
    else {
      int colorIdx = (int) (p.color * paletteIdxScl + 0.5);
      RenderColor color = colorMap[colorIdx];
      rp.setRed(rp.getRed() + color.red * prj.intensity);
      rp.setGreen(rp.getGreen() + color.green * prj.intensity);
      rp.setBlue(rp.getBlue() + color.blue * prj.intensity);
    }
    rp.incCount();
    if (observers != null && observers.size() > 0) {
      for (IterationObserver observer : observers) {
        observer.notifyIterationFinished(renderThread, xIdx, yIdx);
      }
    }
  }

}
