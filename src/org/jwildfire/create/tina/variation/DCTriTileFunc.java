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
import org.jwildfire.create.tina.random.AbstractRandomGenerator;

import java.io.Serializable;

import static org.jwildfire.base.Tools.limitValue;
import static org.jwildfire.base.mathlib.MathLib.*;

public class DCTriTileFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final double M_PHI = 1.6180339887498948482045868343656;
  public static final double M_1_PHI = 1.0 / M_PHI;

  private static final String PARAM_TILING = "type";
  private static final String PARAM_T = "t";
  private static final String PARAM_COL1 = "col1";
  private static final String PARAM_COL2 = "col2";
  private static final String PARAM_ON = "on";
  private static final String PARAM_ITT = "itt";
  private static final String PARAM_EQ = "eq";
  private static final String PARAM_DEPTH = "depth";
  private static final String PARAM_ON1 = "on1";
  private static final String PARAM_ITT1 = "itt1";
  private static final String PARAM_EQ1 = "eq1";
  private static final String PARAM_DEPTH1 = "depth1";
  private static final String PARAM_ON2 = "on2";
  private static final String PARAM_ITT2 = "itt2";
  private static final String PARAM_EQ2 = "eq2";
  private static final String PARAM_DEPTH2 = "depth2";

  private static final String[] paramNames = {PARAM_TILING, PARAM_T, PARAM_COL1, PARAM_COL2, PARAM_ON, PARAM_ITT, PARAM_EQ, PARAM_DEPTH,
          PARAM_ON1, PARAM_ITT1, PARAM_EQ1, PARAM_DEPTH1, PARAM_ON2, PARAM_ITT2, PARAM_EQ2, PARAM_DEPTH2};

  private int tiling = 1;
  private int t = 2;
  private double col1 = Math.round(Math.random() * 35 + 10) / 100.0, col2 = Math.round(Math.random() * 35 + 55) / 100.0;
  private int on = (int) (Math.random() * 2), on1 = (int) (Math.random() * 2), on2 = (int) (Math.random() * 2);
  private int itt = (int) (Math.random() * 5 + 1), itt1 = (int) (Math.random() * 5), itt2 = (int) (Math.random() * 5);
  private int eq = (int) (Math.random() * 2 + 1), eq1 = (int) (Math.random() * 2 + 1), eq2 = (int) (Math.random() * 2 + 1);
  private int depth = (int) (Math.random() * 4), depth1 = (int) (Math.random() * 4), depth2 = (int) (Math.random() * 4);

  private double discretNoise(int x) {
    int n = x;
    n = (n << 13) ^ n;
    return ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / (double) 0x7fffffff;
  }

  private class triangle implements Serializable {
    private static final long serialVersionUID = 1L;
    int type;
    double x1, y1, x2, y2, x3, y3;
    double col;

    public void assign(triangle t) {
      type = t.type;
      col = t.col;
      x1 = t.x1;
      y1 = t.y1;
      x2 = t.x2;
      y2 = t.y2;
      x3 = t.x3;
      y3 = t.y3;
    }

    private void blur(XYZPoint p) {
      double u, v, v1, v2, w, x, x0, x1, x2, x3, x4, x5, x6, y, y0, y1, y2, y3, y4, y5, y6;

      if (this.x1 <= this.x2 && this.x1 <= this.x3) {
        if (this.x2 <= this.x3) {
          x1 = this.x1;
          y1 = this.y1;
          x2 = this.x2;
          y2 = this.y2;
          x3 = this.x3;
          y3 = this.y3;
        } else {
          x1 = this.x1;
          y1 = this.y1;
          x2 = this.x3;
          y2 = this.y3;
          x3 = this.x2;
          y3 = this.y2;
        }
      } else if (this.x2 <= this.x1 && this.x2 <= this.x3) {
        if (this.x1 <= this.x3) {
          x1 = this.x2;
          y1 = this.y2;
          x2 = this.x1;
          y2 = this.y1;
          x3 = this.x3;
          y3 = this.y3;
        } else {
          x1 = this.x2;
          y1 = this.y2;
          x2 = this.x3;
          y2 = this.y3;
          x3 = this.x1;
          y3 = this.y1;
        }
      } else {
        if (this.x1 <= this.x2) {
          x1 = this.x3;
          y1 = this.y3;
          x2 = this.x1;
          y2 = this.y1;
          x3 = this.x2;
          y3 = this.y2;
        } else {
          x1 = this.x3;
          y1 = this.y3;
          x2 = this.x2;
          y2 = this.y2;
          x3 = this.x1;
          y3 = this.y1;
        }
      }

      double r1 = genRand.random(), r2 = genRand.random();

      if (x1 == x2 && x2 == x3) {
        u = x1;
        if (y1 <= y2 && y1 <= y3) {
          v1 = y1;
          v2 = (y3 > y2) ? y2 : y3;
        } else if (y2 <= y1 && y2 <= y3) {
          v1 = y2;
          v2 = (y3 > y1) ? y1 : y3;
        } else {
          v1 = y3;
          v2 = (y2 > y1) ? y1 : y2;
        }
        v = v1 + r1 * (v2 - v1);
      } else if (x1 == x2) {
        x4 = (x1 + x3) / 2;
        y4 = (y1 + y3) / 2;
        x5 = x3 + x2 - x4;
        y5 = y3 + y2 - y4;
        x6 = x1 + x2 - x4;
        y6 = y1 + y2 - y4;

        x = x3 + r1 * (x4 - x3);
        y = y3 + r1 * (y4 - y3);
        x0 = x5 + r1 * (x2 - x5);
        y0 = y5 + r1 * (y2 - y5);
        u = x + r2 * (x0 - x);
        v = y + r2 * (y0 - y);
        w = y3 + (y2 - y3) / (x2 - x3) * (u - x3);

        if (y2 > y3 + (y3 - y1) / (x3 - x1) * (x2 - x3)) {
          if (v > w) {
            x = x4 + r1 * (x1 - x4);
            y = y4 + r1 * (y1 - y4);
            x0 = x2 + r1 * (x6 - x2);
            y0 = y2 + r1 * (y6 - y2);
            u = x0 + r2 * (x - x0);
            v = y0 + r2 * (y - y0);
          }
        } else {
          if (v < w) {
            x = x4 + r1 * (x1 - x4);
            y = y4 + r1 * (y1 - y4);
            x0 = x2 + r1 * (x6 - x2);
            y0 = y2 + r1 * (y6 - y2);
            u = x0 + r2 * (x - x0);
            v = y0 + r2 * (y - y0);
          }
        }
      } else {
        x4 = (x1 + x3) / 2;
        y4 = (y1 + y3) / 2;
        x5 = x1 + x2 - x4;
        y5 = y1 + y2 - y4;
        x6 = x3 + x2 - x4;
        y6 = y3 + y2 - y4;

        x = x1 + r1 * (x4 - x1);
        y = y1 + r1 * (y4 - y1);
        x0 = x5 + r1 * (x2 - x5);
        y0 = y5 + r1 * (y2 - y5);
        u = x + r2 * (x0 - x);
        v = y + r2 * (y0 - y);
        w = y1 + (y2 - y1) / (x2 - x1) * (u - x1);

        if (y2 > y1 + (y3 - y1) / (x3 - x1) * (x2 - x1)) {
          if (v > w) {
            x = x4 + r1 * (x3 - x4);
            y = y4 + r1 * (y3 - y4);
            x0 = x2 + r1 * (x6 - x2);
            y0 = y2 + r1 * (y6 - y2);
            u = x0 + r2 * (x - x0);
            v = y0 + r2 * (y - y0);
          }
        } else {
          if (v < w) {
            x = x4 + r1 * (x3 - x4);
            y = y4 + r1 * (y3 - y4);
            x0 = x2 + r1 * (x6 - x2);
            y0 = y2 + r1 * (y6 - y2);
            u = x0 + r2 * (x - x0);
            v = y0 + r2 * (y - y0);
          }
        }
      }

      if (x1 == x2 && y1 == y2) {
        u = x1 + r1 * (x3 - x1);
        v = y1 + r1 * (y3 - y1);
      }
      if (x1 == x3 && y1 == y3) {
        u = x1 + r1 * (x2 - x1);
        v = y1 + r1 * (y2 - y1);
      }
      if (x2 == x3 && y2 == y3) {
        u = x1 + r1 * (x2 - x1);
        v = y1 + r1 * (y2 - y1);
      }

      p.x = u;
      p.y = v;
      p.z = 0;
    }
  }

  private triangle orig, t1, t2;
  private XYZPoint p;
  private double alfa;
  AbstractRandomGenerator genRand;

  private void fiveFoldInit(int t, triangle orig) {
    switch (t) {
      case 1:
        orig.type = 1;
        orig.col = col1;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = cos(M_2PI / 5);
        orig.y2 = sin(M_2PI / 5);
        orig.x3 = 2 * orig.x2;
        orig.y3 = 0.0;
        break;
      case 2:
        orig.type = 2;
        orig.col = col2;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = cos(M_PI / 5);
        orig.y2 = sin(M_PI / 5);
        orig.x3 = 2 * orig.x2;
        orig.y3 = 0.0;
        break;
    }

    alfa = 2 * cos(M_2PI / 5);
  }

  private void fiveFoldDevis(triangle in, triangle out) {
    // http://tilings.math.uni-bielefeld.de/substitution/penrose-triangle-without-rotations/
    // (original FiveFold tiling)
    double x4, y4, x5, y5;
    double w;

    switch (in.type) {
      case 1:
        x4 = in.x2 + alfa * (in.x3 - in.x2);
        y4 = in.y2 + alfa * (in.y3 - in.y2);

        w = genRand.random();

        if (w < alfa * alfa) {
          out.type = 1;
          out.col = col1;
          out.x1 = in.x3;
          out.y1 = in.y3;
          out.x2 = in.x1;
          out.y2 = in.y1;
          out.x3 = x4;
          out.y3 = y4;
        } else {
          out.type = 2;
          out.col = col2;
          out.x1 = in.x2;
          out.y1 = in.y2;
          out.x2 = x4;
          out.y2 = y4;
          out.x3 = in.x1;
          out.y3 = in.y1;
        }
        break;
      case 2:
        x4 = in.x3 + alfa * (in.x1 - in.x3);
        y4 = in.y3 + alfa * (in.y1 - in.y3);
        x5 = in.x3 + alfa * (in.x2 - in.x3);
        y5 = in.y3 + alfa * (in.y2 - in.y3);

        w = genRand.random();

        if (w < alfa * alfa) {
          out.type = 2;
          out.col = col2;
          out.x1 = in.x2;
          out.y1 = in.y2;
          out.x2 = x4;
          out.y2 = y4;
          out.x3 = in.x1;
          out.y3 = in.y1;
        } else if (w < 2 * alfa * alfa) {
          out.type = 2;
          out.col = col2;
          out.x1 = in.x3;
          out.y1 = in.y3;
          out.x2 = x5;
          out.y2 = y5;
          out.x3 = x4;
          out.y3 = y4;
        } else {
          out.type = 1;
          out.col = col1;
          out.x1 = in.x2;
          out.y1 = in.y2;
          out.x2 = x4;
          out.y2 = y4;
          out.x3 = x5;
          out.y3 = y5;
        }
        break;
    }
  }

  private void trihexInit(int t, triangle orig) {
    switch (t) {
      case 1:
        orig.type = 1;
        orig.col = col1;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = cos(M_PI / 3);
        orig.y2 = 0.0;
        orig.x3 = 0.0;
        orig.y3 = sin(M_PI / 3);
        break;
      case 2:
        orig.type = 2;
        orig.col = col2;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = cos(M_PI / 3);
        orig.y2 = 0.0;
        orig.x3 = 0.0;
        orig.y3 = sin(M_PI / 3);
        break;
    }
  }

  private void trihexDevis(triangle in, triangle out) {
    // http://tilings.math.uni-bielefeld.de/substitution/trihex/
    double x4, y4, x5, y5, x6, y6;
    double w;

    x4 = (in.x1 + in.x3) / 2;
    y4 = (in.y1 + in.y3) / 2;
    x5 = (in.x2 + in.x3) / 2;
    y5 = (in.y2 + in.y3) / 2;
    x6 = (in.x1 + x5) / 2;
    y6 = (in.y1 + y5) / 2;

    w = genRand.random();
    if (w < 0.25) {
      out.type = in.type;
      out.col = in.type == 1 ? col1 : col2;
      out.x1 = x4;
      out.y1 = y4;
      out.x2 = x5;
      out.y2 = y5;
      out.x3 = in.x3;
      out.y3 = in.y3;
    } else if (w < 0.5) {
      out.type = 3 - in.type;
      out.col = in.type == 1 ? col2 : col1;
      out.x1 = x4;
      out.y1 = y4;
      out.x2 = x5;
      out.y2 = y5;
      out.x3 = in.x1;
      out.y3 = in.y1;
    } else if (w < 0.75) {
      out.type = 3 - in.type;
      out.col = in.type == 1 ? col2 : col1;
      out.x1 = x6;
      out.y1 = y6;
      out.x2 = x5;
      out.y2 = y5;
      out.x3 = in.x2;
      out.y3 = in.y2;
    } else {
      out.type = in.type;
      out.col = in.type == 1 ? col1 : col2;
      out.x1 = x6;
      out.y1 = y6;
      out.x2 = in.x1;
      out.y2 = in.y1;
      out.x3 = in.x2;
      out.y3 = in.y2;
    }

  }

  private void equiThirdsInit(int t, triangle orig) {
    switch (t) {
      case 1:
        orig.type = 1;
        orig.col = col1;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = cos(M_PI / 3);
        orig.y2 = sin(M_PI / 3);
        orig.x3 = 2 * orig.x2;
        orig.y3 = 0.0;
        break;
      case 2:
        orig.type = 2;
        orig.col = col2;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = cos(M_PI / 6);
        orig.y2 = sin(M_PI / 6);
        orig.x3 = 2 * orig.x2;
        orig.y3 = 0.0;
        break;
    }

    alfa = 1.0 / 3.0;
  }

  private void equiThirdsDevis(triangle in, triangle out) {
    // http://tilings.math.uni-bielefeld.de/substitution/equithirds/
    double x4, y4, x5, y5;
    double w;

    switch (in.type) {
      case 1:
        x4 = in.x1 + 0.5 * (in.x3 - in.x1);
        y4 = in.y1 + 0.5 * (in.y3 - in.y1);
        x5 = x4 + alfa * (in.x2 - x4);
        y5 = y4 + alfa * (in.y2 - y4);

        w = genRand.random();

        if (w < alfa) {
          out.type = 2;
          out.col = col2;
          out.x1 = in.x2;
          out.y1 = in.y2;
          out.x2 = x5;
          out.y2 = y5;
          out.x3 = in.x1;
          out.y3 = in.y1;
        } else if (w < 2 * alfa) {
          out.type = 2;
          out.col = col2;
          out.x1 = in.x3;
          out.y1 = in.y3;
          out.x2 = x5;
          out.y2 = y5;
          out.x3 = in.x2;
          out.y3 = in.y2;
        } else {
          out.type = 2;
          out.col = col2;
          out.x1 = in.x1;
          out.y1 = in.y1;
          out.x2 = x5;
          out.y2 = y5;
          out.x3 = in.x3;
          out.y3 = in.y3;
        }
        break;
      case 2:
        x4 = in.x1 + alfa * (in.x3 - in.x1);
        y4 = in.y1 + alfa * (in.y3 - in.y1);
        x5 = in.x3 + alfa * (in.x1 - in.x3);
        y5 = in.y3 + alfa * (in.y1 - in.y3);

        w = genRand.random();

        if (w < alfa) {
          out.type = 2;
          out.col = col2;
          out.x1 = in.x2;
          out.y1 = in.y2;
          out.x2 = x4;
          out.y2 = y4;
          out.x3 = in.x1;
          out.y3 = in.y1;
        } else if (w < 2 * alfa) {
          out.type = 2;
          out.col = col2;
          out.x1 = in.x3;
          out.y1 = in.y3;
          out.x2 = x5;
          out.y2 = y5;
          out.x3 = in.x2;
          out.y3 = in.y2;
        } else {
          out.type = 1;
          out.col = col1;
          out.x1 = x4;
          out.y1 = y4;
          out.x2 = in.x2;
          out.y2 = in.y2;
          out.x3 = x5;
          out.y3 = y5;
        }
        break;
    }
  }

  private void ortInit(int t, triangle orig) {
    switch (t) {
      case 1:
        orig.type = 1;
        orig.col = col1;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = cos(M_2PI / 5);
        orig.y2 = sin(M_2PI / 5);
        orig.x3 = 2 * orig.x2;
        orig.y3 = 0.0;
        break;
      case 2:
        orig.type = 2;
        orig.col = col2;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = cos(M_2PI / 5);
        orig.y2 = sin(M_2PI / 5);
        orig.x3 = 2 * orig.x2;
        orig.y3 = 0.0;
        break;
    }

    alfa = 2 * cos(M_2PI / 5);
  }

  private void ortDevis(triangle in, triangle out) {
    // http://tilings.math.uni-bielefeld.de/substitution/overlapping-robinson-triangle-1/
    double x4, y4, x5, y5, x6, y6;
    double w;

    x4 = in.x2 + alfa * (in.x1 - in.x2);
    y4 = in.y2 + alfa * (in.y1 - in.y2);
    x5 = in.x1 + alfa * (in.x2 - in.x1);
    y5 = in.y1 + alfa * (in.y2 - in.y1);
    x6 = in.x2 + alfa * (in.x3 - in.x2);
    y6 = in.y2 + alfa * (in.y3 - in.y2);

    w = genRand.random();
    if (w < 0.3) {
      out.type = 2;
      out.col = col2;
      out.x1 = x4;
      out.y1 = y4;
      out.x2 = in.x2;
      out.y2 = in.y2;
      out.x3 = x6;
      out.y3 = y6;
    } else if (w < 0.63) {
      out.type = 3 - in.type;
      out.col = in.type == 1 ? col2 : col1;
      out.x1 = x6;
      out.y1 = y6;
      out.x2 = in.x1;
      out.y2 = in.y1;
      out.x3 = x5;
      out.y3 = y5;
    } else {
      out.type = 1;
      out.col = col1;
      out.x1 = in.x3;
      out.y1 = in.y3;
      out.x2 = in.x1;
      out.y2 = in.y1;
      out.x3 = x6;
      out.y3 = y6;
    }

  }

  private void pinwheelInit(int t, triangle orig) {
    switch (t) {
      case 1:
        orig.type = 1;
        orig.col = col1;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = 0.0;
        orig.y2 = 1.0;
        orig.x3 = 2.0;
        orig.y3 = 0.0;
        break;
      case 2:
        orig.type = 2;
        orig.col = col2;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = 0.0;
        orig.y2 = 2.0;
        orig.x3 = 1.0;
        orig.y3 = 0.0;
        break;
    }
  }

  private void pinwheelDevis(triangle in, triangle out) {
    // http://tilings.math.uni-bielefeld.de/substitution/pinwheel/ and https://en.wikipedia.org/wiki/Pinwheel_tiling
    double x4, y4, x5, y5, x6, y6, x7, y7;
    int w;

    switch (in.type) {
      case 1:
        x4 = in.x2 + 0.2 * (in.x3 - in.x2);
        y4 = in.y2 + 0.2 * (in.y3 - in.y2);
        x5 = in.x2 + 0.6 * (in.x3 - in.x2);
        y5 = in.y2 + 0.6 * (in.y3 - in.y2);
        x6 = in.x1 + 0.5 * (in.x3 - in.x1);
        y6 = in.y1 + 0.5 * (in.y3 - in.y1);
        x7 = in.x1 + 0.5 * (x4 - in.x1);
        y7 = in.y1 + 0.5 * (y4 - in.y1);

        w = (int) (genRand.random() * 5);
        switch (w) {
          case 0:
            out.type = 2;
            out.col = col2;
            out.x1 = x4;
            out.y1 = y4;
            out.x2 = in.x1;
            out.y2 = in.y1;
            out.x3 = in.x2;
            out.y3 = in.y2;
            break;
          case 1:
            out.type = 2;
            out.col = col2;
            out.x1 = x7;
            out.y1 = y7;
            out.x2 = x6;
            out.y2 = y6;
            out.x3 = in.x1;
            out.y3 = in.y1;
            break;
          case 2:
            out.type = 1;
            out.col = col1;
            out.x1 = x7;
            out.y1 = y7;
            out.x2 = x4;
            out.y2 = y4;
            out.x3 = x6;
            out.y3 = y6;
            break;
          case 3:
            out.type = 1;
            out.col = col1;
            out.x1 = x5;
            out.y1 = y5;
            out.x2 = x6;
            out.y2 = y6;
            out.x3 = x4;
            out.y3 = y4;
            break;
          case 4:
            out.type = 2;
            out.col = col2;
            out.x1 = x5;
            out.y1 = y5;
            out.x2 = in.x3;
            out.y2 = in.y3;
            out.x3 = x6;
            out.y3 = y6;
            break;
        }
        break;
      case 2:
        x4 = in.x3 + 0.2 * (in.x2 - in.x3);
        y4 = in.y3 + 0.2 * (in.y2 - in.y3);
        x5 = in.x3 + 0.6 * (in.x2 - in.x3);
        y5 = in.y3 + 0.6 * (in.y2 - in.y3);
        x6 = in.x1 + 0.5 * (in.x2 - in.x1);
        y6 = in.y1 + 0.5 * (in.y2 - in.y1);
        x7 = in.x1 + 0.5 * (x4 - in.x1);
        y7 = in.y1 + 0.5 * (y4 - in.y1);

        w = (int) (genRand.random() * 5);
        switch (w) {
          case 0:
            out.type = 1;
            out.col = col1;
            out.x1 = x4;
            out.y1 = y4;
            out.x2 = in.x3;
            out.y2 = in.y3;
            out.x3 = in.x1;
            out.y3 = in.y1;
            break;
          case 1:
            out.type = 1;
            out.col = col1;
            out.x1 = x7;
            out.y1 = y7;
            out.x2 = in.x1;
            out.y2 = in.y1;
            out.x3 = x6;
            out.y3 = y6;
            break;
          case 2:
            out.type = 2;
            out.col = col2;
            out.x1 = x7;
            out.y1 = y7;
            out.x2 = x6;
            out.y2 = y6;
            out.x3 = x4;
            out.y3 = y4;
            break;
          case 3:
            out.type = 2;
            out.col = col2;
            out.x1 = x5;
            out.y1 = y5;
            out.x2 = x4;
            out.y2 = y4;
            out.x3 = x6;
            out.y3 = y6;
            break;
          case 4:
            out.type = 1;
            out.col = col1;
            out.x1 = x5;
            out.y1 = y5;
            out.x2 = x6;
            out.y2 = y6;
            out.x3 = in.x2;
            out.y3 = in.y2;
            break;
        }
        break;
    }
  }

  private void goldInit(int t, triangle orig) {
    switch (t) {
      case 1:
        orig.type = 1;
        orig.col = col1;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = M_1_PHI;
        orig.y2 = sqrt(M_1_PHI);
        orig.x3 = orig.x2;
        orig.y3 = 0.0;
        break;
      case 2:
        orig.type = 2;
        orig.col = col2;
        orig.x1 = 0.0;
        orig.y1 = 0.0;
        orig.x2 = 0.0;
        orig.y2 = sqrt(M_1_PHI);
        orig.x3 = 1.0;
        orig.y3 = 0.0;
        break;
    }

    alfa = sqr(M_1_PHI);
  }

  private void goldDevis(triangle in, triangle out) {
    // http://tilings.math.uni-bielefeld.de/substitution/golden-pinwheel/
    double x4, y4, x5, y5, x6, y6, x7, y7, x8, y8, x9, y9, x10, y10;
    double w;

    switch (in.type) {
      case 1:
        x4 = in.x1 + alfa * (in.x2 - in.x1);
        y4 = in.y1 + alfa * (in.y2 - in.y1);
        x5 = in.x2 + alfa * (in.x1 - in.x2);
        y5 = in.y2 + alfa * (in.y1 - in.y2);
        x6 = in.x3 + alfa * (in.x2 - in.x3);
        y6 = in.y3 + alfa * (in.y2 - in.y3);
        x7 = in.x1 + alfa * (in.x3 - in.x1);
        y7 = in.y1 + alfa * (in.y3 - in.y1);

        w = genRand.random();
        if (w < 0.146) {
          out.type = 1;
          out.col = col1;
          out.x1 = in.x1;
          out.y1 = in.y1;
          out.x2 = x4;
          out.y2 = y4;
          out.x3 = x7;
          out.y3 = y7;
        } else if (w < 0.382) {
          out.type = 2;
          out.col = col2;
          out.x1 = x4;
          out.y1 = y4;
          out.x2 = x7;
          out.y2 = y7;
          out.x3 = x6;
          out.y3 = y6;
        } else if (w < 0.618) {
          out.type = 2;
          out.col = col2;
          out.x1 = in.x3;
          out.y1 = in.y3;
          out.x2 = x6;
          out.y2 = y6;
          out.x3 = x7;
          out.y3 = y7;
        } else if (w < 0.764) {
          out.type = 1;
          out.col = col1;
          out.x1 = x4;
          out.y1 = y4;
          out.x2 = x6;
          out.y2 = y6;
          out.x3 = x5;
          out.y3 = y5;
        } else {
          out.type = 2;
          out.col = col2;
          out.x1 = x5;
          out.y1 = y5;
          out.x2 = x6;
          out.y2 = y6;
          out.x3 = in.x2;
          out.y3 = in.y2;
        }
        break;
      case 2:
        x4 = in.x2 + alfa * (in.x1 - in.x2);
        y4 = in.y2 + alfa * (in.y1 - in.y2);
        x5 = in.x2 + alfa * (in.x3 - in.x2);
        y5 = in.y2 + alfa * (in.y3 - in.y2);
        x6 = in.x3 + alfa * (in.x2 - in.x3);
        y6 = in.y3 + alfa * (in.y2 - in.y3);
        x7 = in.x1 + alfa * (in.x3 - in.x1);
        y7 = in.y1 + alfa * (in.y3 - in.y1);
        x8 = in.x3 + alfa * (in.x1 - in.x3);
        y8 = in.y3 + alfa * (in.y1 - in.y3);
        x9 = x4 + alfa * (x7 - x4);
        y9 = y4 + alfa * (y7 - y4);
        x10 = x7 + alfa * (x4 - x7);
        y10 = y7 + alfa * (y4 - y7);

        w = genRand.random();
        if (w < 0.146) {
          out.type = 2;
          out.col = col2;
          out.x1 = x4;
          out.y1 = y4;
          out.x2 = in.x2;
          out.y2 = in.y2;
          out.x3 = x5;
          out.y3 = y5;
        } else if (w < 0.236) {
          out.type = 1;
          out.col = col1;
          out.x1 = x4;
          out.y1 = y4;
          out.x2 = x5;
          out.y2 = y5;
          out.x3 = x9;
          out.y3 = y9;
        } else if (w < 0.382) {
          out.type = 2;
          out.col = col2;
          out.x1 = x10;
          out.y1 = y10;
          out.x2 = in.x1;
          out.y2 = in.y1;
          out.x3 = x4;
          out.y3 = y4;
        } else if (w < 0.472) {
          out.type = 1;
          out.col = col1;
          out.x1 = x7;
          out.y1 = y7;
          out.x2 = in.x1;
          out.y2 = in.y1;
          out.x3 = x10;
          out.y3 = y10;
        } else if (w < 0.618) {
          out.type = 2;
          out.col = col2;
          out.x1 = x9;
          out.y1 = y9;
          out.x2 = x5;
          out.y2 = y5;
          out.x3 = x7;
          out.y3 = y7;
        } else if (w < 0.764) {
          out.type = 2;
          out.col = col2;
          out.x1 = x6;
          out.y1 = y6;
          out.x2 = x5;
          out.y2 = y5;
          out.x3 = x7;
          out.y3 = y7;
        } else if (w < 0.854) {
          out.type = 1;
          out.col = col1;
          out.x1 = x7;
          out.y1 = y7;
          out.x2 = x6;
          out.y2 = y6;
          out.x3 = x8;
          out.y3 = y8;
        } else {
          out.type = 2;
          out.col = col2;
          out.x1 = x8;
          out.y1 = y8;
          out.x2 = x6;
          out.y2 = y6;
          out.x3 = in.x3;
          out.y3 = in.y3;
        }
        break;
    }
  }

  private void devis(triangle t1, triangle t2) {
    switch (tiling) {
      case 1:
        fiveFoldDevis(t1, t2);
        break;
      case 2:
        trihexDevis(t1, t2);
        break;
      case 3:
        equiThirdsDevis(t1, t2);
        break;
      case 4:
        ortDevis(t1, t2);
        break;
      case 5:
        pinwheelDevis(t1, t2);
        break;
      case 6:
        goldDevis(t1, t2);
        break;
    }
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    orig = new triangle();

    switch (tiling) {
      case 1:
        fiveFoldInit(t, orig);
        break;
      case 2:
        trihexInit(t, orig);
        break;
      case 3:
        equiThirdsInit(t, orig);
        break;
      case 4:
        ortInit(t, orig);
        break;
      case 5:
        pinwheelInit(t, orig);
        break;
      case 6:
        goldInit(t, orig);
        break;
    }

    genRand = pContext.getRandGen();
    t1 = new triangle();
    t2 = new triangle();
    p = new XYZPoint();
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* extension of FiveFold by eralex61, https://eralex61.deviantart.com/art/Five-fold-example-of-finite-recurent-system-in-Apo-705813367 */

    t1.assign(orig);
    t1.col = 0.25;
    t2.assign(orig);
    t2.col = 0.0;

    int n = itt;
    int k = 1;

    while (n > 0) {
      devis(t1, t2);
      t1.assign(t2);

      if (on == 0) {
        if ((k == itt) && (t1.type != eq)) n = n + depth;
      } else {
        if ((k >= itt) && (k < itt + depth) && (t1.type != eq)) n = n + 1;
      }
      n = n - 1;
      k = k + 1;
    }

    int m = 0;
    switch (t1.type) {
      case 1:
        n = itt1;
        k = 1;
        while (n > 0) {
          devis(t1, t2);
          t1.assign(t2);
          m = 2 * m + t2.type - 1;
          if (on1 == 0) {
            if ((k == itt1) && (t1.type != eq1)) n = n + depth1;
          } else {
            if ((k >= itt1) && (k < itt1 + depth1) && (t1.type != eq1)) n = n + 1;
          }
          n = n - 1;
          k = k + 1;
        }
        t1.col = discretNoise(m + 43);
        break;
      case 2:
        n = itt2;
        k = 1;
        while (n > 0) {
          devis(t1, t2);
          t1.assign(t2);
          m = 2 * m + t2.type - 1;
          if (on2 == 0) {
            if ((k == itt2) && (t1.type != eq2)) n = n + depth2;
          } else {
            if ((k >= itt2) && (k < itt2 + depth2) && (t1.type != eq2)) n = n + 1;
          }
          n = n - 1;
          k = k + 1;
        }
        t1.col = discretNoise(m + 31);
        break;
    }

    t2.blur(p);

    pVarTP.x += pAmount * p.x;
    pVarTP.y += pAmount * p.y;
    pVarTP.z += pAmount * p.z;
    pVarTP.color = fmod(t1.col + t2.col, 1.0);

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{tiling, t, col1, col2, on, itt, eq, depth, on1, itt1, eq1, depth1, on2, itt2, eq2, depth2};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"FiveFold_Tiling", "FiveFold_T", "FiveFold_Col1", "FiveFold_Col2", "FiveFold_On", "FiveFold_Itt", "FiveFold_Eq", "FiveFold_Depth", "FiveFold_On1", "FiveFold_Itt1", "FiveFold_Eq1", "FiveFold_Depth1", "FiveFold_On2", "FiveFold_Itt2", "FiveFold_Eq2", "FiveFold_Depth2"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_TILING.equalsIgnoreCase(pName))
      tiling = limitValue((int) pValue, 1, 6);
    else if (PARAM_T.equalsIgnoreCase(pName))
      t = Tools.FTOI(pValue);
    else if (PARAM_COL1.equalsIgnoreCase(pName))
      col1 = pValue;
    else if (PARAM_COL2.equalsIgnoreCase(pName))
      col2 = pValue;
    else if (PARAM_ON.equalsIgnoreCase(pName))
      on = limitValue((int) pValue, 0, 1);
    else if (PARAM_ITT.equalsIgnoreCase(pName))
      itt = Tools.FTOI(pValue);
    else if (PARAM_EQ.equalsIgnoreCase(pName))
      eq = limitValue((int) pValue, 0, 2);
    else if (PARAM_DEPTH.equalsIgnoreCase(pName))
      depth = Tools.FTOI(pValue);
    else if (PARAM_ON1.equalsIgnoreCase(pName))
      on1 = limitValue((int) pValue, 0, 1);
    else if (PARAM_ITT1.equalsIgnoreCase(pName))
      itt1 = Tools.FTOI(pValue);
    else if (PARAM_EQ1.equalsIgnoreCase(pName))
      eq1 = limitValue((int) pValue, 0, 2);
    else if (PARAM_DEPTH1.equalsIgnoreCase(pName))
      depth1 = Tools.FTOI(pValue);
    else if (PARAM_ON2.equalsIgnoreCase(pName))
      on2 = limitValue((int) pValue, 0, 1);
    else if (PARAM_ITT2.equalsIgnoreCase(pName))
      itt2 = Tools.FTOI(pValue);
    else if (PARAM_EQ2.equalsIgnoreCase(pName))
      eq2 = limitValue((int) pValue, 0, 2);
    else if (PARAM_DEPTH2.equalsIgnoreCase(pName))
      depth2 = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dc_triTile";
  }

}
