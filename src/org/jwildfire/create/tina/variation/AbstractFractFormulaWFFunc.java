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

public abstract class AbstractFractFormulaWFFunc extends AbstractFractWFFunc {
  private static final long serialVersionUID = 1L;

  public AbstractFractFormulaWFFunc() {
    initParams();
  }

  protected void prepare_formula(String pFormula) {
    int i, j, k, len, ka, kz;
    int[] offset = new int[80];
    char operator;

    for (i = 0; i < pFormula.length(); i++) {
      form[i] = pFormula.charAt(i);
    }
    form[pFormula.length()] = 0;

    for (i = 0; form[i] != 0; i++) {
      j = 0;
      while ((form[i + j] == '.') || ((form[i + j] >= '0') && (form[i + j] <= '9')))
        j++;
      if (j > 0)
        offset[i] = j;
      else
        offset[i] = 1;
    }
    len = i;

    boolean ok = true;
    while (offset[0] < len && ok) {
      i = 0;
      ka = kz = -1;
      ok = false;
      while (i < len) {
        if (form[i] == '(')
          ka = i;
        if ((form[i] == ')') && (ka > -1)) {
          kz = i;
          form[ka] = ' ';
          form[kz] = ' ';
          offset[ka] = kz - ka + 1;
          ok = true;
          j = ka + 1;
          while ((form[j] != '+') && (form[j] != '-') && (form[j] != '*') && (form[j] != 0x2f)
                  && (form[j] != '^'))
            j += offset[j];
          operator = form[j];
          for (k = j; k < kz - 1; k++)
            form[k] = form[k + 1];
          form[kz - 1] = operator;
          ka = -1;
        }
        i += offset[i];
      }
    }

    for (i = 0; i < len; i++) {
      while (form[i] == ' ') {
        for (j = i + 1; j < len; j++)
          form[j - 1] = form[j];
        form[len - 1] = 0;
        len--;
      }
    }
  }

  protected static class Complex {
    public double re;
    public double im;
  }

  private char[] form = new char[80];
  double stack[] = new double[2 * 20];
  int stackptr;

  private void initstack() {
    stackptr = 0;
  }

  private void push(double x, double y) {
    stack[stackptr++] = y;
    stack[stackptr++] = x;
  }

  private double pop() {
    return stack[--stackptr];
  }

  protected Complex perform_formula(double power, double zx, double zy, double cx, double cy) {
    int i, j;
    boolean number;
    Complex z = new Complex();
    double sx, sy, hx, hy, k, pnt;

    initstack();
    i = 0;
    sx = sy = 0.0;
    while (form[i] != 0) {
      k = pnt = 0.0;
      number = false;
      while ((form[i] == '.') || ((form[i] >= '0') && (form[i] <= '9'))) {
        number = true;
        if (form[i] == '.') {
          pnt = 0.1;
          i++;
        } else if (pnt == 0.0) {
          k = k * 10.0 + (form[i++] - '0');
        } else {
          k = k + pnt * (form[i++] - '0');
          pnt *= 0.1;
        }
      }
      if (number)
        push(k, 0.0);
      switch (form[i++]) {
        case 'z':
          push(zx, zy);
          break;
        case 'c':
          push(cx, cy);
          break;
        case 'i':
          push(0.0, 1.0);
          break;
        case 'n':
          push((double) power, 0.0);
          break;

        case '+':
          hx = pop();
          hy = pop();
          sx = pop();
          sy = pop();
          sx += hx;
          sy += hy;
          push(sx, sy);
          break;
        case '-':
          hx = pop();
          hy = pop();
          sx = pop();
          sy = pop();
          sx -= hx;
          sy -= hy;
          push(sx, sy);
          break;
        case '*':
          hx = pop();
          hy = pop();
          sx = pop();
          sy = pop();
        {
          double xa = hx * sx - hy * sy;
          double ya = hx * sy + sx * hy;
          sx = xa;
          sy = ya;
        }
        push(sx, sy);
        break;
        case '/':
          hx = pop();
          hy = pop();
          sx = pop();
          sy = pop();
        {
          double da = hx * hx + hy * hy;
          double xa = (sx * hx + sy * hy) / da;
          double ya = (sy * hx - sx * hy) / da;
          sx = xa;
          sy = ya;
        }
        push(sx, sy);
        break;
        case '^':
          k = pop();
          hy = pop();
          sx = pop();
          sy = pop();
          if (k == (double) ((long) k)) {
            if (k == 2.0) {
              hx = sx * sx - sy * sy;
              hy = 2.0 * sx * sy;
            } else if (k == 3.0) {
              hx = sx * sx * sx - 3.0 * sx * sy * sy;
              hy = 3.0 * sx * sx * sy - sy * sy * sy;
            } else {
              hx = sx * sx * sx * sx + sy * sy * sy * sy - 6.0 * sx * sx * sy * sy;
              hy = 4.0 * sx * sy * (sx * sx - sy * sy);
              for (j = 4; j < k; j++) {
                double xa = sx * hx - sy * hy;
                double ya = sx * hy + hx * sy;
                hx = xa;
                hy = ya;
              }
            }
            push(hx, hy);
          } else {
            {
              final double PID2 = 0.5 * Math.PI;
              double v;
              double da = Math.pow(sx * sx + sy * sy, k / 2.0);
              if (sx == 0.0)
                if (sy >= 0.0)
                  v = PID2;
                else
                  v = -PID2;
              else
                v = Math.atan2(sy, sx);
              sx = da * Math.cos(v * k);
              sy = da * Math.sin(v * k);
            }
            push(sx, sy);
          }
          break;

        case 'x':
          push(zx, 0.0);
          break;
        case 'y':
          push(0.0, zy);
          break;
        case 'a':
          push(cx, 0.0);
          break;
        case 'b':
          push(0.0, cy);
          break;

        case 0xb2: /* ^2 */
          hx = pop();
          hy = pop();
          sx = hx * hx - hy * hy;
          sy = 2.0 * hx * hy;
          push(sx, sy);
          break;
        case 0xb3: /* ^3 */
          hx = pop();
          hy = pop();
          sx = hx * hx * hx - 3.0 * hx * hy * hy;
          sy = 3.0 * hx * hx * hy - hy * hy * hy;
          push(sx, sy);
          break;
        default: // nothing to do
          break;
      }
    }
    sx = pop();
    sy = pop();
    z.re = sx;
    z.im = sy;
    return z;
  }

}
