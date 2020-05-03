/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

package org.jwildfire.create.tina.variation.plot;

import org.jwildfire.base.mathlib.DoubleWrapperWF;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.variation.ColorMapHolder;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceType;
import org.jwildfire.create.tina.variation.mesh.AbstractOBJMeshWFFunc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Metaballs3DWFFunc extends AbstractOBJMeshWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_MB_MODE = "mb_mode";
  private static final String PARAM_MB_COUNT = "mb_count";
  private static final String PARAM_MB_MIN_RADIUS = "mb_min_radius";
  private static final String PARAM_MB_MAX_RADIUS = "mb_max_radius";
  private static final String PARAM_MB_NEGATIVE = "mb_negative";
  private static final String PARAM_MB_SEED = "mb_seed";
  private static final String PARAM_MB_SHARPNESS = "mb_sharpness";
  private static final String PARAM_BORDER_SIZE = "border_size";
  private static final String PARAM_XMIN = "xmin";
  private static final String PARAM_XMAX = "xmax";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_ZMIN = "zmin";
  private static final String PARAM_ZMAX = "zmax";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_COLOR_MODE = "color_mode";
  private static final String PARAM_BLEND_COLORMAP = "blend_colormap";
  private static final String PARAM_MAX_ITER = "max_iter";

  private static final String[] paramNames = {PARAM_MB_MODE, PARAM_MB_COUNT, PARAM_MB_MIN_RADIUS, PARAM_MB_MAX_RADIUS, PARAM_MB_NEGATIVE, PARAM_MB_SEED, PARAM_MB_SHARPNESS, PARAM_BORDER_SIZE, PARAM_XMIN, PARAM_XMAX, PARAM_YMIN, PARAM_YMAX, PARAM_ZMIN, PARAM_ZMAX, PARAM_MAX_ITER, PARAM_DIRECT_COLOR, PARAM_COLOR_MODE, PARAM_BLEND_COLORMAP};
  private static final String[] ressourceNames = {RESSOURCE_COLORMAP_FILENAME};

  private static final int CM_COLORMAP_X = 0;
  private static final int CM_COLORMAP_Y = 1;
  private static final int CM_COLORMAP_Z = 2;
  private static final int CM_X = 3;
  private static final int CM_Y = 4;
  private static final int CM_Z = 5;
  private static final int CM_XY = 6;
  private static final int CM_YZ = 7;
  private static final int CM_ZX = 8;
  private static final int CM_XYZ = 9;

  private double xmin = -Math.PI;
  private double xmax = Math.PI;
  private double ymin = -Math.PI;
  private double ymax = Math.PI;
  private double zmin = -Math.PI;
  private double zmax = Math.PI;

  private int direct_color = 1;
  private int color_mode = CM_XYZ;

  private double mb_sharpness = 2.75;
  private int max_iter = 160;

  private static final int MB_MODE_RANDOM_UNIFORM = 0;
  private static final int MB_MODE_RANDOM_Y_AXIS = 1;
  private static final int MB_MODE_RANDOM_RADIAL = 2;

  private int mb_mode = MB_MODE_RANDOM_UNIFORM;
  private int mb_count = 64;
  private double mb_min_radius = 0.35;
  private double mb_max_radius = 0.75;
  private double mb_negative = 0.3;
  private int mb_seed = (int) (Math.random() * 1234567);
  private double border_size = 0.42;

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{mb_mode, mb_count, mb_min_radius, mb_max_radius, mb_negative, mb_seed, mb_sharpness, border_size, xmin, xmax, ymin, ymax, zmin, zmax, max_iter, direct_color, color_mode, colorMapHolder.getBlend_colormap()};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_MB_MODE.equalsIgnoreCase(pName)) {
      mb_mode = limitIntVal(Tools.FTOI(pValue), MB_MODE_RANDOM_UNIFORM, MB_MODE_RANDOM_RADIAL);
    } else if (PARAM_MB_COUNT.equalsIgnoreCase(pName)) {
      mb_count = Tools.FTOI(pValue);
    } else if (PARAM_MB_SEED.equalsIgnoreCase(pName)) {
      mb_seed = Tools.FTOI(pValue);
    } else if (PARAM_MB_MIN_RADIUS.equalsIgnoreCase(pName)) {
      mb_min_radius = pValue;
    } else if (PARAM_MB_MAX_RADIUS.equalsIgnoreCase(pName)) {
      mb_max_radius = pValue;
    } else if (PARAM_MB_NEGATIVE.equalsIgnoreCase(pName)) {
      mb_negative = pValue;
    } else if (PARAM_MB_SHARPNESS.equalsIgnoreCase(pName)) {
      mb_sharpness = pValue;
    } else if (PARAM_BORDER_SIZE.equalsIgnoreCase(pName)) {
      border_size = pValue;
    } else if (PARAM_XMIN.equalsIgnoreCase(pName)) {
      xmin = pValue;
    } else if (PARAM_XMAX.equalsIgnoreCase(pName)) {
      xmax = pValue;
    } else if (PARAM_YMIN.equalsIgnoreCase(pName)) {
      ymin = pValue;
    } else if (PARAM_YMAX.equalsIgnoreCase(pName)) {
      ymax = pValue;
    } else if (PARAM_ZMIN.equalsIgnoreCase(pName)) {
      zmin = pValue;
    } else if (PARAM_ZMAX.equalsIgnoreCase(pName)) {
      zmax = pValue;
    } else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName)) {
      direct_color = Tools.FTOI(pValue);
    } else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) {
      color_mode = Tools.FTOI(pValue);
    } else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName)) {
      colorMapHolder.setBlend_colormap(limitIntVal(Tools.FTOI(pValue), 0, 1));
    } else if (PARAM_MAX_ITER.equalsIgnoreCase(pName)) {
      max_iter = Tools.FTOI(pValue);
      if (max_iter < 1) {
        max_iter = 1;
      }
    } else if (PARAM_MB_SHARPNESS.equalsIgnoreCase(pName)) {
      mb_sharpness = pValue;
      if (mb_sharpness < EPSILON) {
        mb_sharpness = EPSILON;
      }
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "metaballs3d_wf";
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(colorMapHolder.getColormap_filename() != null ? colorMapHolder.getColormap_filename().getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      colorMapHolder.setColormap_filename(pValue != null ? new String(pValue) : "");
      colorMapHolder.clear();
      uvIdxMap.clear();
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else
      throw new IllegalArgumentException(pName);
  }
  private double _xmin, _xmax, _dx;
  private double _ymin, _ymax, _dy;
  private double _zmin, _zmax, _dz;

  private DoubleWrapperWF sina = new DoubleWrapperWF();
  private DoubleWrapperWF cosa = new DoubleWrapperWF();
  private DoubleWrapperWF sinb = new DoubleWrapperWF();
  private DoubleWrapperWF cosb = new DoubleWrapperWF();

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    uvColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    hits = new AtomicInteger(0);
    misses = new AtomicInteger(0);
    updateBoundingBoxSettings();

    List<Metaball> m = new ArrayList<>();
    {
      double rmin = Math.min(Math.abs(mb_min_radius), Math.abs(mb_max_radius));
      double rmax = Math.max(Math.abs(mb_min_radius), Math.abs(mb_max_radius));
      double dr = rmax - rmin;
      double border = Math.abs(border_size);
      double __xmin = _xmin + border_size;
      double __xmax = _xmax - border_size;
      double __dx = __xmax - __xmin;
      double __ymin = _ymin + border_size;
      double __ymax = _ymax - border_size;
      double __dy = __ymax - __ymin;
      double __zmin = _zmin + border_size;
      double __zmax = _zmax - border_size;
      double __dz = __zmax - __zmin;

      MarsagliaRandomGenerator randGen = new MarsagliaRandomGenerator();
      randGen.randomize(mb_seed);
      for (int i = 0; i < mb_count; i++) {
        double r, x, y, z;
        switch (mb_mode) {
          case MB_MODE_RANDOM_Y_AXIS: {
            r = rmin + dr * falloff(randGen.random()) * 1.42;
            x = __xmin + __dx * randGen.random();
            y = __ymax - __dy * falloff(randGen.random());
            z = __zmin + __dz * randGen.random();
          }
          break;
          case MB_MODE_RANDOM_RADIAL: {
            r = rmin + dr * falloff(randGen.random());
            double radius = falloff(randGen.random()) * Math.min(Math.min(__dx, _dy), __dz) * 0.85;
            double alpha = randGen.random() * 2.0 * M_PI;
            sinAndCos(alpha, sina, cosa);
            double beta = randGen.random() * M_PI;
            sinAndCos(beta, sinb, cosb);
            x = __xmin + __dx * 0.5 + radius * sinb.value * cosa.value;
            y = __ymin + __dy * 0.5 + radius * sinb.value * sina.value;
            z = __zmin + __dz * 0.5 + radius * cosb.value;
          }
          break;
          case MB_MODE_RANDOM_UNIFORM:
          default: {
            r = rmin + dr * randGen.random();
            x = __xmin + __dx * randGen.random();
            y = __ymin + __dy * randGen.random();
            z = __zmin + __dz * randGen.random();
          }
          break;
        }

        if (randGen.random() < mb_negative) {
          r = 0.0 - r;
        }
        m.add(new Metaball(x, y, z, r));
      }
    }
    this.metaballs = m.toArray(new Metaball[]{});
  }

  private double falloff(double x) {
    return 1.0 / (x + 0.63) - 0.60;
  }

  private void updateBoundingBoxSettings() {
    _xmin = xmin;
    _xmax = xmax;
    if (_xmin > _xmax) {
      double t = _xmin;
      _xmin = _xmax;
      _xmax = t;
    }
    _dx = _xmax - _xmin;

    _ymin = ymin;
    _ymax = ymax;
    if (_ymin > _ymax) {
      double t = _ymin;
      _ymin = _ymax;
      _ymax = t;
    }
    _dy = _ymax - _ymin;

    _zmin = zmin;
    _zmax = zmax;
    if (_zmin > _zmax) {
      double t = _zmin;
      _zmin = _zmax;
      _zmax = t;
    }
    _dz = _zmax - _zmin;
  }

  private RenderColor[] uvColors;
  protected Map<RenderColor, Double> uvIdxMap = new HashMap<RenderColor, Double>();

  private double getUVColorIdx(int pR, int pG, int pB) {
    RenderColor pColor = new RenderColor(pR, pG, pB);
    Double res = uvIdxMap.get(pColor);
    if (res == null) {

      int nearestIdx = 0;
      RenderColor color = uvColors[0];
      double dr, dg, db;
      dr = (color.red - pR);
      dg = (color.green - pG);
      db = (color.blue - pB);
      double nearestDist = sqrt(dr * dr + dg * dg + db * db);
      for (int i = 1; i < uvColors.length; i++) {
        color = uvColors[i];
        dr = (color.red - pR);
        dg = (color.green - pG);
        db = (color.blue - pB);
        double dist = sqrt(dr * dr + dg * dg + db * db);
        if (dist < nearestDist) {
          nearestDist = dist;
          nearestIdx = i;
        }
      }
      res = (double) nearestIdx / (double) (uvColors.length - 1);
      uvIdxMap.put(pColor, res);
    }
    return res;
  }

  private static AtomicInteger hits, misses;

  @Override
  /**
   * metaballs3d_wf by Andreas Maschke
   */
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    pVarTP.doHide = true;
    double x = 0.0, y = 0.0, z = 0.0;
    for (int i = 0; i < max_iter; i++) {
      x = _xmin + pContext.random() * _dx;
      y = _ymin + pContext.random() * _dy;
      z = _zmin + pContext.random() * _dz;
      double e = evaluate(x, y, z);
      if (e > mb_sharpness) {
        pVarTP.x += pAmount * x;
        pVarTP.y += pAmount * y;
        pVarTP.z += pAmount * z;
        pVarTP.doHide = false;
        break;
      }
    }
/*
        if (pVarTP.doHide) {
          misses.addAndGet(1);
        }
        else {
          hits.addAndGet(1);
        }
        int m = misses.get();
        int h = hits.get();
        int sum = m + h;
        if (sum % 100000000 == 0) {
          System.out.println("h: " + h + ", m: " + m + ", misses: " + Tools.FTOI(100.0 * (double) m / (double) (m + h)) + "%");
        }
*/
    if (!pVarTP.doHide && direct_color > 0) {
      switch (color_mode) {
        case CM_X:
          pVarTP.color = (x - _xmin) / _dx;
          break;
        case CM_Y:
          pVarTP.color = (y - _ymin) / _dy;
          break;
        case CM_Z:
          pVarTP.color = (z - _zmin) / _dz;
          break;
        case CM_COLORMAP_X:
        case CM_COLORMAP_Y:
        case CM_COLORMAP_Z:
          if (colorMapHolder.isActive()) {
            double iu, iv;
            switch (color_mode) {
              case CM_COLORMAP_X:
                iu = GfxMathLib.clamp((y - _ymin) / _dy * (colorMapHolder.getColorMapWidth() - 1.0), 0.0, colorMapHolder.getColorMapWidth() - 1.0);
                iv = GfxMathLib.clamp(colorMapHolder.getColorMapHeight() - 1.0 - (z - _zmin) / _dz * (colorMapHolder.getColorMapHeight() - 1.0), 0, colorMapHolder.getColorMapHeight() - 1.0);
                break;
              case CM_COLORMAP_Y:
                iu = GfxMathLib.clamp((z - _zmin) / _dz * (colorMapHolder.getColorMapWidth() - 1.0), 0.0, colorMapHolder.getColorMapWidth() - 1.0);
                iv = GfxMathLib.clamp(colorMapHolder.getColorMapHeight() - 1.0 - (x - _xmin) / _dx * (colorMapHolder.getColorMapHeight() - 1.0), 0, colorMapHolder.getColorMapHeight() - 1.0);
                break;
              case CM_COLORMAP_Z:
              default:
                iu = GfxMathLib.clamp((x - _xmin) / _dx * (colorMapHolder.getColorMapWidth() - 1.0), 0.0, colorMapHolder.getColorMapWidth() - 1.0);
                iv = GfxMathLib.clamp(colorMapHolder.getColorMapHeight() - 1.0 - (y - _ymin) / _dy * (colorMapHolder.getColorMapHeight() - 1.0), 0, colorMapHolder.getColorMapHeight() - 1.0);
                break;
            }
            int ix = (int) MathLib.trunc(iu);
            int iy = (int) MathLib.trunc(iv);
            colorMapHolder.applyImageColor(pVarTP, ix, iy, iu, iv);
            pVarTP.color = getUVColorIdx(Tools.FTOI(pVarTP.redColor), Tools.FTOI(pVarTP.greenColor), Tools.FTOI(pVarTP.blueColor));
          }
          break;
        case CM_XY:
          pVarTP.color = (x - _xmin) / _dx * (y - _ymin) / _dy;
          break;
        case CM_YZ:
          pVarTP.color = (y - _ymin) / _dy * (z - _zmin) / _dz;
          break;
        case CM_ZX:
          pVarTP.color = (z - _zmin) / _dz * (x - _xmin) / _dx;
          break;
        default:
        case CM_XYZ:
          pVarTP.color = (x - _xmin) / _dx * (y - _ymin) / _dy * (z - _zmin) / _dz;
          break;
      }
      if (pVarTP.color < 0.0)
        pVarTP.color = 0.0;
      else if (pVarTP.color > 1.0)
        pVarTP.color = 1.0;
    }
  }

  private double evaluate(double x, double y, double z) {
    double f = 0;
    for (Metaball metaball : metaballs) {
      double dx = x - metaball.x;
      double dy = y - metaball.y;
      double dz = z - metaball.z;
      double r0 = metaball.influence;
      if (r0 < 0) {
        f -= r0 * r0 / (dx * dx + dy * dy + dz * dz);
      } else {
        f += r0 * r0 / (dx * dx + dy * dy + dz * dz);
      }
    }
    return f;
  }

  private Metaball[] metaballs;


  private class Metaball {
    private double x, y, z;
    private double influence;

    public Metaball(double x, double y, double z, double influence) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.influence = influence;
    }
  }
}
