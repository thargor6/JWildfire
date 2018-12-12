/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class DCPerlinFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_SHAPE = "shape";
  private static final String PARAM_MAP = "map";
  private static final String PARAM_SELECT_CENTRE = "select_centre";
  private static final String PARAM_SELECT_RANGE = "select_range";
  public static final String PARAM_CENTRE = "centre";
  public static final String PARAM_RANGE = "range";
  private static final String PARAM_EDGE = "edge";
  private static final String PARAM_SCALE = "scale";
  public static final String PARAM_OCTAVES = "octaves";
  public static final String PARAM_AMPS = "amps";
  public static final String PARAM_FREQS = "freqs";
  private static final String PARAM_Z = "z";
  private static final String PARAM_SELECT_BAILOUT = "select_bailout";

  private static final String[] paramNames = {PARAM_SHAPE, PARAM_MAP, PARAM_SELECT_CENTRE, PARAM_SELECT_RANGE, PARAM_CENTRE, PARAM_RANGE, PARAM_EDGE, PARAM_SCALE, PARAM_OCTAVES, PARAM_AMPS, PARAM_FREQS, PARAM_Z, PARAM_SELECT_BAILOUT};

  private int shape = 0;
  private int map = 0;
  private double select_centre = 0.0;
  private double select_range = 1.0;
  private double centre = 0.25;
  private double range = 0.25;
  private double edge = 0.0;
  private double scale = 1.0;
  private int octaves = 2;
  private double amps = 2.0;
  private double freqs = 2.0;
  private double z = 0.0;
  private int select_bailout = 10;

  private final static int SHAPE_SQUARE = 0;
  private final static int SHAPE_DISC = 1;
  private final static int SHAPE_BLUR = 2;

  private final static int MAP_FLAT = 0;
  private final static int MAP_SPHERICAL = 1;
  private final static int MAP_HSPHERE = 2;
  private final static int MAP_QSPHERE = 3;
  private final static int MAP_BUBBLE = 4;
  private final static int MAP_BUBBLE2 = 5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* dc_perlin by slobo777, http://slobo777.deviantart.com/art/dc-perlin-Apophysis-Plugin-186190256 */
    double V[] = new double[3];
    double Vx = 0.0, Vy = 0.0, Col, r, theta, s, c, p, e;
    int t;

    t = 0;
    do {
      // Default edge value
      e = 0.0;
      // When pAmount is 0, use for coloring only, so no shape
      if (pAmount == 0) {
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;
      } else {
        // Assign Vx, Vy according to shape
        switch (shape) {
          case SHAPE_SQUARE:
            Vx = (1.0 + this.edge) * (pContext.random() - 0.5);
            Vy = (1.0 + this.edge) * (pContext.random() - 0.5);
            r = Vx * Vx > Vy * Vy ? Vx : Vy;
            if (r > 1.0 - this.edge) {
              e = 0.5 * (r - 1.0 + this.edge) / this.edge;
            }
            break;

          case SHAPE_DISC:
            r = pContext.random() + pContext.random();
            r = (r > 1.0) ? 2.0 - r : r;
            r *= (1.0 + this.edge);
            if (r > 1.0 - this.edge) {
              e = 0.5 * (r - 1.0 + this.edge) / this.edge;
            }
            theta = pContext.random() * M_2PI;
            s = sin(theta);
            c = cos(theta);
            Vx = 0.5 * r * s;
            Vy = 0.5 * r * c;
            break;

          case SHAPE_BLUR:
            r = (1.0 + this.edge) * pContext.random();
            if (r > 1.0 - this.edge) {
              e = 0.5 * (r - 1.0 + this.edge) / this.edge;
            }
            theta = pContext.random() * M_2PI;
            s = sin(theta);
            c = cos(theta);
            Vx = 0.5 * r * s;
            Vy = 0.5 * r * c;
            break;
          default: // nothing to do
            break;
        }
      }

      // Assign V for noise vector position according to map
      switch (this.map) {
        case MAP_FLAT:
          V[0] = this.scale * Vx;
          V[1] = this.scale * Vy;
          V[2] = this.scale * this.z;
          break;

        case MAP_SPHERICAL:
          r = 1.0 / (Vx * Vx + Vy * Vy + SMALL_EPSILON);
          V[0] = this.scale * Vx * r;
          V[1] = this.scale * Vy * r;
          V[2] = this.scale * this.z;
          break;

        case MAP_HSPHERE:
          r = 1.0 / (Vx * Vx + Vy * Vy + 0.5);
          V[0] = this.scale * Vx * r;
          V[1] = this.scale * Vy * r;
          V[2] = this.scale * this.z;
          break;

        case MAP_QSPHERE:
          r = 1.0 / (Vx * Vx + Vy * Vy + 0.25);
          V[0] = this.scale * Vx * r;
          V[1] = this.scale * Vy * r;
          V[2] = this.scale * this.z;
          break;

        case MAP_BUBBLE:
          r = 0.25 - (Vx * Vx + Vy * Vy);
          if (r < 0.0) {
            r = sqrt(-r);
          } else {
            r = sqrt(r);
          }
          V[0] = this.scale * Vx;
          V[1] = this.scale * Vy;
          V[2] = this.scale * (r + this.z);
          break;

        case MAP_BUBBLE2:
          r = 0.25 - (Vx * Vx + Vy * Vy);
          if (r < 0.0) {
            r = sqrt(-r);
          } else {
            r = sqrt(r);
          }
          V[0] = this.scale * Vx;
          V[1] = this.scale * Vy;
          V[2] = this.scale * (2 * r + this.z);
          break;
      }

      p = NoiseTools.perlinNoise3D(V, this.amps, this.freqs, this.octaves);
      // Add edge effects
      if (p > 0.0) {
        e = p * (1.0 + e * e * 20.0) + 2.0 * e;
      } else {
        e = p * (1.0 + e * e * 20.0) - 2.0 * e;
      }
    }
    while ((e < _notch_bottom || e > _notch_top) && t++ < this.select_bailout);

    // Add blur effect to transform
    pVarTP.x += pAmount * Vx;
    pVarTP.y += pAmount * Vy;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

    // Calculate and add direct colour   
    Col = this.centre + this.range * p;
    pVarTP.color = Col - floor(Col);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{shape, map, select_centre, select_range, centre, range, edge, scale, octaves, amps, freqs, z, select_bailout};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SHAPE.equalsIgnoreCase(pName))
      shape = limitIntVal(Tools.FTOI(pValue), 0, 2);
    else if (PARAM_MAP.equalsIgnoreCase(pName))
      map = limitIntVal(Tools.FTOI(pValue), 0, 5);
    else if (PARAM_SELECT_CENTRE.equalsIgnoreCase(pName))
      select_centre = limitVal(pValue, -1.0, 1.0);
    else if (PARAM_SELECT_RANGE.equalsIgnoreCase(pName))
      select_range = limitVal(pValue, 0.1, 2.0);
    else if (PARAM_CENTRE.equalsIgnoreCase(pName))
      centre = pValue;
    else if (PARAM_RANGE.equalsIgnoreCase(pName))
      range = pValue;
    else if (PARAM_EDGE.equalsIgnoreCase(pName))
      edge = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_OCTAVES.equalsIgnoreCase(pName))
      octaves = limitIntVal(Tools.FTOI(pValue), 1, 5);
    else if (PARAM_AMPS.equalsIgnoreCase(pName))
      amps = pValue;
    else if (PARAM_FREQS.equalsIgnoreCase(pName))
      freqs = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      z = pValue;
    else if (PARAM_SELECT_BAILOUT.equalsIgnoreCase(pName))
      select_bailout = limitIntVal(Tools.FTOI(pValue), 2, 1000);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dc_perlin";
  }

  private double _notch_bottom, _notch_top;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _notch_bottom = select_centre - select_range;
    _notch_bottom = (_notch_bottom > 0.75) ? 0.75 : _notch_bottom;
    _notch_bottom = (_notch_bottom < -2.0) ? -3.0 : _notch_bottom;
    _notch_top = select_centre + select_range;
    _notch_top = (_notch_top < -0.75) ? -0.75 : _notch_top;
    _notch_top = (_notch_top > 3.0) ? 3.0 : _notch_top;
  }

}
