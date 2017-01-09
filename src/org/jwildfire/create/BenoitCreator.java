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
package org.jwildfire.create;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.image.SimpleImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class BenoitCreator extends ImageCreator {

  public enum Type {
    MANDELBROT, JULIA, DRAGON, SALAMANDER, METEORS, PEARLS, FORMULA
  };

  public enum FormulaStyle {
    MANDELBROT, JULIA
  }

  @Property(description = "Fractal type", editorClass = TypeEditor.class)
  private Type type = Type.MANDELBROT;

  @Property(description = "Formula style in formula mode", editorClass = FormulaStyleEditor.class)
  private FormulaStyle formulaStyle = FormulaStyle.MANDELBROT;

  @Property(description = "Formula of the fractal")
  private String formula = "((z^n)+c)";

  @Property(description = "Fractal exponent")
  @PropertyMin(2)
  @PropertyMax(20)
  private int exponent = 2;

  @Property(description = "Number of iterations")
  @PropertyMin(0)
  private int maxIter = 100;

  @Property(description = "Color repeat")
  private int colorRepeat = 100;

  @Property(description = "Even color")
  private int evenColor = 0;

  @Property(description = "Odd color")
  private int oddColor = 0;

  @Property(description = "Left border of the x-range")
  private double xMin = -3.0;

  @Property(description = "Right border of the x-range")
  private double xMax = 3.0;

  @Property(description = "Top border of the y-range")
  private double yMin = -2.1;

  @Property(description = "Bottom border of the y-range")
  private double yMax = 2.1;

  @Property(description = "Seed for the random number generator for x axis")
  private double xSeed = 0.10;

  @Property(description = "Seed for the random number generator for y axis")
  private double ySeed = 0.70;

  @SuppressWarnings("unused")
  private double gAverage = 0.0;
  private final int MAX_COL = 255;
  SimpleImage img;

  @Override
  protected void fillImage(SimpleImage res) {
    gAverage = 0.0;
    this.img = res;
    try {
      switch (this.type) {
        case MANDELBROT:
          switch (this.exponent) {
            case 2:
              mandelbrot(res);
              break;
            case 3:
              mandcubed(res);
              break;
            case 4:
              mandfourth(res);
              break;
            default:
              mandexpo(res);
          }
          break;
        case JULIA:
          switch (this.exponent) {
            case 2:
              julia(res);
              break;
            case 3:
              juliacubed(res);
              break;
            case 4:
              juliafourth(res);
              break;
            default:
              juliaexpo(res);
              break;
          }
          break;
        case DRAGON:
          dragon(res);
          break;
        case SALAMANDER:
          salamander(res);
          break;
        case METEORS:
          meteors(res);
          break;
        case PEARLS:
          pearls(res);
          break;
        case FORMULA:
          formula(res);
          break;
      }
    }
    finally {
      img = null;
    }
  }

  // macros    
  private void plot_pixel(int x, int y, int iter) {
    if (iter >= this.maxIter) {
      img.setRGB(x, y, 0, 0, 0);
    }
    else {
      int phase = iter * 6 / this.colorRepeat;
      int value = (int) (((double) iter * 6.0 / (double) this.colorRepeat - (double) phase) * 255.0 + 0.5);
      if ((this.evenColor > 0) && ((iter & 1) == 0)) {
        phase += this.evenColor;
        if (phase >= 6)
          phase -= 6;
      }
      if ((this.oddColor > 0) && ((iter & 1) == 1)) {
        phase += this.oddColor;
        if (phase >= 6)
          phase -= 6;
      }
      switch (phase) {
        case 0:
          img.setRGB(x, y, MAX_COL, value, 0);
          break;
        case 1:
          img.setRGB(x, y, MAX_COL - value, MAX_COL, 0);
          break;
        case 2:
          img.setRGB(x, y, 0, MAX_COL, value);
          break;
        case 3:
          img.setRGB(x, y, 0, MAX_COL - value, MAX_COL);
          break;
        case 4:
          img.setRGB(x, y, value, 0, MAX_COL);
          break;
        case 5:
          img.setRGB(x, y, MAX_COL, 0, MAX_COL - value);
          break;
        default: // nothing to do
          break;
      }
    }
  }

  private void pearls(SimpleImage res) {
    int i, j, iter;
    double x1, y1, x2, xs, ys, x, y, jx, jy, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    jx = this.xSeed;
    jy = this.ySeed;
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          x2 = jx * x1 - jy * y1 - ((jx * x1 + jy * y1) / (xs + ys));
          y1 = jx * y1 + jy * x1 + ((jx * y1 - jy * x1) / (xs + ys));

          x1 = x2;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  private void meteors(SimpleImage res) {
    int i, j, iter;
    double x1, y1, x2, xs, ys, x, y, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          x2 = x * x1 - y * y1 - (x * x1 + y * y1) / (xs + ys);
          y1 = x * y1 + y * x1 + (x * y1 - y * x1) / (xs + ys);

          x1 = x2;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;

    }
    gAverage = count / width * height;
  }

  private void salamander(SimpleImage res) {
    int i, j, iter;
    double x1, y1, x2, xs, ys, x, y, jx, jy, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    jx = this.xSeed;
    jy = this.ySeed;
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          x2 = (xs - ys) * jx - (2.0 * x1 * y1) * jy - 1.0;
          y1 = (xs - ys) * jy + (2.0 * x1 * y1) * jx;

          x1 = x2;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  private void dragon(SimpleImage res) {
    int i, j, iter;
    double x1, y1, x2, xs, ys, x, y, jx, jy, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    jx = this.xSeed;
    jy = this.ySeed;
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          x2 = (x1 - xs + ys) * jx - (y1 - 2.0 * x1 * y1) * jy;
          y1 = (x1 - xs + ys) * jy + (y1 - 2.0 * x1 * y1) * jx;

          x1 = x2;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  private void juliacubed(SimpleImage res) {
    int i, j, iter;
    double x1, y1, x2, xs, ys, x, y, jx, jy, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    jx = this.xSeed;
    jy = this.ySeed;
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          x2 = xs * x1 - 3.0 * x1 * ys + jx;
          y1 = 3.0 * xs * y1 - ys * y1 + jy;
          x1 = x2;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  private void juliaexpo(SimpleImage res) {
    int i, j, k, iter;
    double x1, y1, x2, y2, xs, ys, x, y, jx, jy, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();

    jx = this.xSeed;
    jy = this.ySeed;
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          x2 = x1 * (xs * xs - 10.0 * xs * ys + 5.0 * ys * ys);
          y2 = y1 * (ys * ys - 10.0 * xs * ys + 5.0 * xs * xs);
          for (k = 5; k < this.exponent; k++) {
            double xa = x1 * x2 - y1 * y2;
            double ya = x1 * y2 + x2 * y1;
            x2 = xa;
            y2 = ya;
          }

          x1 = x2 + jx;
          y1 = y2 + jy;

          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  private void juliafourth(SimpleImage res) {
    int i, j, iter;
    double x1, y1, x2, xs, ys, x, y, jx, jy, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    jx = this.xSeed;
    jy = this.ySeed;
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          x2 = xs * xs + ys * ys - 6.0 * xs * ys + jx;
          y1 = 4.0 * x1 * y1 * (xs - ys) + jy;
          x1 = x2;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  private void julia(SimpleImage res) {
    int i, j, iter;
    double x1, y1, xs, ys, x, y, jx, jy, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    jx = this.xSeed;
    jy = this.ySeed;
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          y1 = 2.0 * x1 * y1 + jy;
          x1 = xs - ys + jx;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  private void mandfourth(SimpleImage res) {
    int i, j, iter;
    double x1, y1, x2, xs, ys, x, y, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          x2 = xs * xs + ys * ys - 6.0 * xs * ys + x;
          y1 = 4.0 * x1 * y1 * (xs - ys) + y;
          x1 = x2;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;

    }
    gAverage = count / width * height;
  }

  private void mandcubed(SimpleImage res) {
    int i, j, iter;
    double x1, y1, x2, xs, ys, x, y, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          x2 = xs * x1 - 3.0 * x1 * ys + x;
          y1 = 3.0 * xs * y1 - ys * y1 + y;
          x1 = x2;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  private void mandelbrot(SimpleImage res) {
    int i, j, iter;
    double x1, y1, xs, ys, x, y, xstep, ystep, count;
    x = this.xMin;
    y = this.yMin;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    xstep = (this.xMax - x) / (double) width;
    ystep = (this.yMax - y) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          y1 = 2.0 * x1 * y1 + y;
          x1 = xs - ys + x;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  private void mandexpo(SimpleImage res) {
    int i, j, k, iter;
    double x1, y1, x2, y2, xs, ys, x, y, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          x2 = x1 * (xs * xs - 10.0 * xs * ys + 5.0 * ys * ys);
          y2 = y1 * (ys * ys - 10.0 * xs * ys + 5.0 * xs * xs);
          for (k = 5; k < this.exponent; k++) {
            double xa = x1 * x2 - y1 * y2;
            double ya = x1 * y2 + x2 * y1;
            x2 = xa;
            y2 = ya;
          }
          x1 = x2 + x;
          y1 = y2 + y;

          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  private void formula(SimpleImage res) {
    int i, j, k, len, ka, kz;
    int[] offset = new int[80];
    char operator;

    for (i = 0; i < this.formula.length(); i++) {
      form[i] = this.formula.charAt(i);
    }
    form[this.formula.length()] = 0;

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

    while (offset[0] < len) {
      i = 0;
      ka = kz = -1;
      while (i < len) {
        if (form[i] == '(')
          ka = i;
        if ((form[i] == ')') && (ka > -1)) {
          kz = i;
          form[ka] = ' ';
          form[kz] = ' ';
          offset[ka] = kz - ka + 1;
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

    for (i = 0; i < len; i++)
      while (form[i] == ' ') {
        for (j = i + 1; j < len; j++)
          form[j - 1] = form[j];
        form[len - 1] = 0;
        len--;
      }

    switch (this.formulaStyle) {
      case MANDELBROT:
        formula_mand(res);
        break;
      case JULIA:
        formula_julia(res);
        break;
    }
  }

  private static class Complex {
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

  private Complex perform_formula(double zx, double zy, double cx, double cy) {
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
        }
        else if (pnt == 0.0) {
          k = k * 10.0 + (form[i++] - '0');
        }
        else {
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
          push((double) this.exponent, 0.0);
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
            sx = ya;
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
            sx = ya;
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
            }
            else if (k == 3.0) {
              hx = sx * sx * sx - 3.0 * sx * sy * sy;
              hy = 3.0 * sx * sx * sy - sy * sy * sy;
            }
            else {
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
          }
          else {
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

  private void formula_mand(SimpleImage res) {
    int i, j, iter;
    Complex z;
    double x1, y1, xs, ys, x, y, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          z = perform_formula(x1, y1, x, y);
          x1 = z.re;
          y1 = z.im;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  private void formula_julia(SimpleImage res) {
    int i, j, iter;
    Complex z;
    double x1, y1, xs, ys, x, y, jx, jy, xstep, ystep, count;
    int width = res.getImageWidth();
    int height = res.getImageHeight();
    jx = this.xSeed;
    jy = this.ySeed;
    x = this.xMin;
    y = this.yMin;
    xstep = (this.xMax - this.xMin) / (double) width;
    ystep = (this.yMax - this.yMin) / (double) height;
    count = 0.0;
    for (j = 0; j < height; j++) {
      for (i = 0; i < width; i++) {
        x1 = x;
        y1 = y;
        iter = 0;
        xs = x1 * x1;
        ys = y1 * y1;
        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
          z = perform_formula(x1, y1, jx, jy);
          x1 = z.re;
          y1 = z.im;
          xs = x1 * x1;
          ys = y1 * y1;
        }
        plot_pixel(i, j, iter);
        count += (double) iter;
        x += xstep;
      }
      x = this.xMin;
      y += ystep;
    }
    gAverage = count / width * height;
  }

  public FormulaStyle getFormulaStyle() {
    return formulaStyle;
  }

  public void setFormulaStyle(FormulaStyle formulaStyle) {
    this.formulaStyle = formulaStyle;
  }

  public String getFormula() {
    return formula;
  }

  public void setFormula(String formula) {
    this.formula = formula;
  }

  public int getExponent() {
    return exponent;
  }

  public void setExponent(int exponent) {
    this.exponent = exponent;
  }

  public int getMaxIter() {
    return maxIter;
  }

  public void setMaxIter(int maxIter) {
    this.maxIter = maxIter;
  }

  public int getColorRepeat() {
    return colorRepeat;
  }

  public void setColorRepeat(int colorRepeat) {
    this.colorRepeat = colorRepeat;
  }

  public int getEvenColor() {
    return evenColor;
  }

  public void setEvenColor(int evenColor) {
    this.evenColor = evenColor;
  }

  public int getOddColor() {
    return oddColor;
  }

  public void setOddColor(int oddColor) {
    this.oddColor = oddColor;
  }

  public double getXMin() {
    return xMin;
  }

  public void setXMin(double xMin) {
    this.xMin = xMin;
  }

  public double getXMax() {
    return xMax;
  }

  public void setXMax(double xMax) {
    this.xMax = xMax;
  }

  public double getYMin() {
    return yMin;
  }

  public void setYMin(double yMin) {
    this.yMin = yMin;
  }

  public double getYMax() {
    return yMax;
  }

  public void setYMax(double yMax) {
    this.yMax = yMax;
  }

  public double getXSeed() {
    return xSeed;
  }

  public void setXSeed(double xSeed) {
    this.xSeed = xSeed;
  }

  public double getYSeed() {
    return ySeed;
  }

  public void setYSeed(double ySeed) {
    this.ySeed = ySeed;
  }

  public static class TypeEditor extends ComboBoxPropertyEditor {
    public TypeEditor() {
      super();
      setAvailableValues(new Type[] { Type.MANDELBROT, Type.JULIA, Type.DRAGON, Type.SALAMANDER,
          Type.METEORS, Type.PEARLS, Type.FORMULA });
    }
  }

  public static class FormulaStyleEditor extends ComboBoxPropertyEditor {
    public FormulaStyleEditor() {
      super();
      setAvailableValues(new FormulaStyle[] { FormulaStyle.MANDELBROT, FormulaStyle.JULIA });
    }
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

}
