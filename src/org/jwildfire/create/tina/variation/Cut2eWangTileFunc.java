/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2023 Andreas Maschke

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
package org.jwildfire.create.tina.variation;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Random;

public class Cut2eWangTileFunc extends VariationFunc {

  /*
   * Variation :cut_2ewangtile
   * Date: august 29, 2019
   * Jesus Sosa
   * Reference & Credits:  https://www.shadertoy.com/view/Wds3z7
   */

  private static final long serialVersionUID = 1L;

  private static final String PARAM_SEED = "seed";
  private static final String PARAM_MODE = "mode";
  private static final String PARAM_ZOOM = "zoom";
  private static final String PARAM_INVERT = "invert";
  private static final String[] additionalParamNames = {
    PARAM_SEED, PARAM_MODE, PARAM_ZOOM, PARAM_INVERT
  };
  int seed = 1000;
  int mode = 1;
  double zoom = 25.0;
  int invert = 0;
  Random randomize = new Random(seed);
  double x0 = 0., y0 = 0.;
  double k = .1;
  double K2 = ((1. - k) / 2.);
  double K3 = (Math.sqrt(2.) * .5 - K2);
  // "Hash without Sine" - borrowed from Dave Hoskins's shader
  // https://www.shadertoy.com/view/4djSRW
  double HASHSCALE1 = 0.1031;

  double tile0(vec2 uv) {
    double v = G.length(uv) - K3;
    double w = K2 - G.length(new vec2(Math.abs(uv.x) - .5, uv.y - .5));
    return v =
        G.mix(
            v,
            w,
            // smoothstep(.1, -.1, abs(uv.x) - uv.y)
            G.step(Math.abs(uv.x), uv.y));
  }

  double tile1(vec2 uv) {
    return Math.abs(G.length(uv.minus(.5)) - .5) - k * .5;
  }

  double tile2(vec2 uv) {
    return Math.abs(uv.x) - k * .5;
  }

  double tile3(vec2 uv) {
    return Math.max(-uv.x - k * .5, K2 - G.length(new vec2(uv.x - .5, Math.abs(uv.y) - .5)));
  }

  double tile4(vec2 uv) {
    return K2 - G.length(new vec2(Math.abs(uv.x) - .5, Math.abs(uv.y) - .5));
  }

  double tile(vec2 uv, int tile) {
    switch (tile) {
      case 0:
        return 1.414;
      case 1:
        return Math.max(tile0(uv), .15 - G.length(uv));
      case 2:
        return tile0(new vec2(uv.y, uv.x));
      case 3:
        return tile1(uv);
      case 4:
        return tile0(new vec2(uv.x, -uv.y));
      case 5:
        return tile2(uv);
      case 6:
        return tile1(new vec2(uv.x, -uv.y));
      case 7:
        return tile3(uv);
      case 8:
        return tile0(new vec2(uv.y, -uv.x));
      case 9:
        return tile1(new vec2(-uv.x, uv.y));
      case 10:
        return tile2(new vec2(uv.y, uv.x));
      case 11:
        return tile3(new vec2(uv.y, uv.x));
      case 12:
        return tile1(new vec2(-uv.x, -uv.y));
      case 13:
        return tile3(new vec2(-uv.x, uv.y));
      case 14:
        return tile3(new vec2(-uv.y, uv.x));
      case 15:
        return tile4(uv);
    }
    return 1.414;
  }

  double hash(vec2 p) {
    vec3 p3 = G.fract(new vec3(p.x, p.y, p.x).multiply(HASHSCALE1));
    p3 = p3.plus(G.dot(p3, new vec3(p3.y, p3.z, p3.x).plus(19.19)));
    return G.fract((p3.x + p3.y) * p3.z);
  }

  double map(vec2 uv) {
    int b = 0;
    uv = uv.plus(.5);
    vec2 id = G.floor(uv);
    if (hash(id) >= .5) b += 1;
    if (hash(id.multiply(-1.)) >= .5) b += 8;
    if (hash(id.minus(new vec2(0., 1.))) >= .5) b += 4;
    if (hash((id.plus(new vec2(1., 0.)).multiply(-1.0))) >= .5) b += 2;

    return tile(G.fract(uv).minus(.5), b);
  }

  vec2 rotate(vec2 uv, double a) {
    double co = Math.cos(a);
    double si = Math.sin(a);
    return uv.times(new mat2(co, si, -si, co));
  }

  double height(vec2 uv) {
    double r = map(uv) - .1;
    return Math.sqrt(.01 - Math.min(r * r, .01));
  }

  public boolean UnitSquare(double x) {
		return (x >= 0.0) && (x <= 1.0);
	}

  public void transform(
      FlameTransformationContext pContext,
      XForm pXForm,
      XYZPoint pAffineTP,
      XYZPoint pVarTP,
      double pAmount) {
    double x, y, px_center, py_center;

    if (mode == 0) {
      x = pAffineTP.x;
      y = pAffineTP.y;
      px_center = 0.0;
      py_center = 0.0;
    } else {
      x = pContext.random();
      y = pContext.random();
      px_center = 0.5;
      py_center = 0.5;
    }

    vec2 uv = new vec2(x * zoom, y * zoom);
    uv = uv.plus(new vec2(x0, y0));
    vec3 rd = G.normalize(new vec3(uv, 1.66));

    vec2 h = new vec2(.01, 0.);
    double v = map(uv);
    double c0 = height(uv);
    double c1 = height(uv.plus(new vec2(h.x, h.y)));
    double c2 = height(uv.plus(new vec2(h.y, h.x)));

    double color;
    color = 1.0;
    color =
        G.mix(1.0, .0, G.smoothstep(0., 2. / 2000.0, Math.min(v, Math.abs(v - .1) - .1) / zoom));
    color = G.sqrt(color);

    pVarTP.doHide = false;
    if (invert == 0) {
      if (color > 0.30) {
        x = 0;
        y = 0;
        pVarTP.doHide = true;
      }
    } else {
      if (color <= 0.30) {
        x = 0;
        y = 0;
        pVarTP.doHide = true;
      }
    }
    pVarTP.x = pAmount * (x - px_center);
    pVarTP.y = pAmount * (y - py_center);
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public void init(
      FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    randomize = new Random(seed);
    x0 = seed * randomize.nextDouble();
    y0 = seed * randomize.nextDouble();
  }

  public String getName() {
    return "cut_2ewangtile";
  }

  public String[] getParameterNames() {
    return (additionalParamNames);
  }

  public Object[] getParameterValues() { //
    return (new Object[] {seed, mode, zoom, invert});
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_SEED)) {
      seed = (int) pValue;
      randomize = new Random(seed);
    } else if (pName.equalsIgnoreCase(PARAM_MODE)) {
      mode = (int) Tools.limitValue(pValue, 0, 1);
    } else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
      zoom = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
      invert = (int) Tools.limitValue(pValue, 0, 1);
    } else throw new IllegalArgumentException(pName);
  }

  @Override
  public boolean dynamicParameterExpansion() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String pName) {
    // preset_id doesn't really expand parameters, but it changes them; this will make them refresh
    return true;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[] {
      VariationFuncType.VARTYPE_2D,
      VariationFuncType.VARTYPE_BASE_SHAPE,
      VariationFuncType.VARTYPE_SIMULATION
    };
  }
}
