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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public abstract class AbstractFractWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_MAX_ITER = "max_iter";
  private static final String PARAM_XMIN = "xmin";
  private static final String PARAM_XMAX = "xmax";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_DIRECT_COLOR = "direct_color";
  private static final String PARAM_SCALEZ = "scalez";
  private static final String PARAM_CLIP_ITER_MIN = "clip_iter_min";
  private static final String PARAM_CLIP_ITER_MAX = "clip_iter_max";
  private static final String PARAM_MAX_CLIP_ITER = "max_clip_iter";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_OFFSETX = "offsetx";
  private static final String PARAM_OFFSETY = "offsety";
  private static final String PARAM_OFFSETZ = "offsetz";

  protected int max_iter = 100;
  protected double xmin = -1.6;
  protected double xmax = 1.6;
  protected double ymin = -1.2;
  protected double ymax = 1.2;
  protected int direct_color = 1;
  protected double scalez = 1.0;
  protected int clip_iter_min = 3;
  protected int clip_iter_max = 80;
  protected double scale = 3.0;
  protected double offsetx = 0.0;
  protected double offsety = 0.0;
  protected double offsetz = 0.0;
  protected int max_clip_iter = 3;

  public AbstractFractWFFunc() {
    initParams();
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x0 = 0.0, y0 = 0.0;
    int iterCount = 0;
    for (int i = 0; i < max_clip_iter; i++) {
      x0 = (xmax - xmin) * pContext.random() + xmin;
      y0 = (ymax - ymin) * pContext.random() + ymin;
      iterCount = iterate(x0, y0);
      if ((clip_iter_max > 0 && iterCount >= clip_iter_max) || (clip_iter_min > 0 && iterCount <= clip_iter_min)) {
        if (i == max_clip_iter - 1) {
          pVarTP.x = pVarTP.y = pVarTP.z = -20000.0 * (Math.random() + 0.5);
          return;
        }
      }
      else {
        break;
      }
    }

    pVarTP.x += scale * pAmount * (x0 + offsetx);
    pVarTP.y += scale * pAmount * (y0 + offsety);
    pVarTP.z += scale * pAmount * (scalez / 10 * ((double) iterCount / (double) max_iter) + offsetz);
    if (direct_color != 0) {
      pVarTP.color = (double) iterCount / (double) max_iter;
      if (pVarTP.color < 0)
        pVarTP.color = 0;
      else if (pVarTP.color > 1.0)
        pVarTP.color = 1.0;
    }
  }

  protected abstract int iterate(double pX, double pY);

  protected abstract void initParams();

  protected abstract void addCustomParameterNames(List<String> pList);

  protected abstract void addCustomParameterValues(List<Object> pList);

  protected abstract boolean setCustomParameter(String pName, double pValue);

  @Override
  public String[] getParameterNames() {
    List<String> lst = new ArrayList<String>();
    lst.add(PARAM_MAX_ITER);
    lst.add(PARAM_XMIN);
    lst.add(PARAM_XMAX);
    lst.add(PARAM_YMIN);
    lst.add(PARAM_YMAX);
    addCustomParameterNames(lst);
    lst.add(PARAM_DIRECT_COLOR);
    lst.add(PARAM_SCALEZ);
    lst.add(PARAM_CLIP_ITER_MIN);
    lst.add(PARAM_CLIP_ITER_MAX);
    lst.add(PARAM_MAX_CLIP_ITER);
    lst.add(PARAM_SCALE);
    lst.add(PARAM_OFFSETX);
    lst.add(PARAM_OFFSETY);
    lst.add(PARAM_OFFSETZ);
    return lst.toArray(new String[0]);
  }

  @Override
  public Object[] getParameterValues() {
    List<Object> lst = new ArrayList<Object>();
    lst.add(max_iter);
    lst.add(xmin);
    lst.add(xmax);
    lst.add(ymin);
    lst.add(ymax);
    addCustomParameterValues(lst);
    lst.add(direct_color);
    lst.add(scalez);
    lst.add(clip_iter_min);
    lst.add(clip_iter_max);
    lst.add(max_clip_iter);
    lst.add(scale);
    lst.add(offsetx);
    lst.add(offsety);
    lst.add(offsetz);
    return lst.toArray();
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_MAX_ITER.equalsIgnoreCase(pName))
      max_iter = Tools.FTOI(pValue);
    else if (PARAM_XMIN.equalsIgnoreCase(pName))
      xmin = pValue;
    else if (PARAM_XMAX.equalsIgnoreCase(pName))
      xmax = pValue;
    else if (PARAM_YMIN.equalsIgnoreCase(pName))
      ymin = pValue;
    else if (PARAM_YMAX.equalsIgnoreCase(pName))
      ymax = pValue;
    else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName))
      direct_color = Tools.FTOI(pValue);
    else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
      scalez = pValue;
    else if (PARAM_CLIP_ITER_MIN.equalsIgnoreCase(pName))
      clip_iter_min = Tools.FTOI(pValue);
    else if (PARAM_CLIP_ITER_MAX.equalsIgnoreCase(pName))
      clip_iter_max = Tools.FTOI(pValue);
    else if (PARAM_MAX_CLIP_ITER.equalsIgnoreCase(pName))
      max_clip_iter = Tools.FTOI(pValue);
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offsetx = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offsety = pValue;
    else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
      offsetz = pValue;
    else if (!setCustomParameter(pName, pValue))
      throw new IllegalArgumentException(pName);
  }

}

//######################################
//import org.jwildfire.base.Property;
//import org.jwildfire.base.PropertyMax;
//import org.jwildfire.base.PropertyMin;
//import org.jwildfire.image.SimpleImage;
//
//import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
//
//public class BenoitCreator extends ImageCreator {
//
//  public enum Type {
//    MANDELBROT, JULIA, DRAGON, SALAMANDER, METEORS, PEARLS, FORMULA
//  };
//
//  public enum FormulaStyle {
//    MANDELBROT, JULIA
//  }
//
//  @Property(description = "Fractal type", editorClass = TypeEditor.class)
//  private Type type = Type.MANDELBROT;
//
//  @Property(description = "Formula style in formula mode", editorClass = FormulaStyleEditor.class)
//  private FormulaStyle formulaStyle = FormulaStyle.MANDELBROT;
//
//  @Property(description = "Formula of the fractal")
//  private String formula = "((z^n)+c)";
//
//  @Property(description = "Fractal exponent")
//  @PropertyMin(2)
//  @PropertyMax(20)
//  private int exponent = 2;
//
//  @Property(description = "Number of iterations")
//  @PropertyMin(0)
//  private int maxIter = 100;
//
//  @Property(description = "Color repeat")
//  private int colorRepeat = 100;
//
//  @Property(description = "Even color")
//  private int evenColor = 0;
//
//  @Property(description = "Odd color")
//  private int oddColor = 0;
//
//  @Property(description = "Left border of the x-range")
//  private double xMin = -3.0;
//
//  @Property(description = "Right border of the x-range")
//  private double xMax = 3.0;
//
//  @Property(description = "Top border of the y-range")
//  private double yMin = -2.1;
//
//  @Property(description = "Bottom border of the y-range")
//  private double yMax = 2.1;
//
//  @Property(description = "Seed for the random number generator for x axis")
//  private double xSeed = 0.10;
//
//  @Property(description = "Seed for the random number generator for y axis")
//  private double ySeed = 0.70;
//
//  @SuppressWarnings("unused")
//  private double gAverage = 0.0;
//  private final int MAX_COL = 255;
//  SimpleImage img;
//
//
//  private void salamander(SimpleImage res) {
//    int i, j, iter;
//    double x1, y1, x2, xs, ys, x, y, jx, jy, xstep, ystep, count;
//    int width = res.getImageWidth();
//    int height = res.getImageHeight();
//    jx = this.xSeed;
//    jy = this.ySeed;
//    x = this.xMin;
//    y = this.yMin;
//    xstep = (this.xMax - this.xMin) / (double) width;
//    ystep = (this.yMax - this.yMin) / (double) height;
//    count = 0.0;
//    for (j = 0; j < height; j++) {
//      for (i = 0; i < width; i++) {
//        x1 = x;
//        y1 = y;
//        iter = 0;
//        xs = x1 * x1;
//        ys = y1 * y1;
//        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
//          x2 = (xs - ys) * jx - (2.0 * x1 * y1) * jy - 1.0;
//          y1 = (xs - ys) * jy + (2.0 * x1 * y1) * jx;
//
//          x1 = x2;
//          xs = x1 * x1;
//          ys = y1 * y1;
//        }
//        plot_pixel(i, j, iter);
//        count += (double) iter;
//        x += xstep;
//      }
//      x = this.xMin;
//      y += ystep;
//    }
//    gAverage = count / width * height;
//  }
//
//
//
//
//
//   private void formula(SimpleImage res) {
//    int i, j, k, len, ka, kz;
//    int[] offset = new int[80];
//    char operator;
//
//    for (i = 0; i < this.formula.length(); i++) {
//      form[i] = this.formula.charAt(i);
//    }
//    form[this.formula.length()] = 0;
//
//    for (i = 0; form[i] != 0; i++) {
//      j = 0;
//      while ((form[i + j] == '.') || ((form[i + j] >= '0') && (form[i + j] <= '9')))
//        j++;
//      if (j > 0)
//        offset[i] = j;
//      else
//        offset[i] = 1;
//    }
//    len = i;
//
//    while (offset[0] < len) {
//      i = 0;
//      ka = kz = -1;
//      while (i < len) {
//        if (form[i] == '(')
//          ka = i;
//        if ((form[i] == ')') && (ka > -1)) {
//          kz = i;
//          form[ka] = ' ';
//          form[kz] = ' ';
//          offset[ka] = kz - ka + 1;
//          j = ka + 1;
//          while ((form[j] != '+') && (form[j] != '-') && (form[j] != '*') && (form[j] != 0x2f)
//              && (form[j] != '^'))
//            j += offset[j];
//          operator = form[j];
//          for (k = j; k < kz - 1; k++)
//            form[k] = form[k + 1];
//          form[kz - 1] = operator;
//          ka = -1;
//        }
//        i += offset[i];
//      }
//    }
//
//    for (i = 0; i < len; i++)
//      while (form[i] == ' ') {
//        for (j = i + 1; j < len; j++)
//          form[j - 1] = form[j];
//        form[len - 1] = 0;
//        len--;
//      }
//
//    switch (this.formulaStyle) {
//      case MANDELBROT:
//        formula_mand(res);
//        break;
//      case JULIA:
//        formula_julia(res);
//        break;
//    }
//  }
//
//  private class Complex {
//    public double re;
//    public double im;
//  }
//
//  private char[] form = new char[80];
//  double stack[] = new double[2 * 20];
//  int stackptr;
//
//  private void initstack() {
//    stackptr = 0;
//  }
//
//  private void push(double x, double y) {
//    stack[stackptr++] = x;
//    stack[stackptr++] = y;
//  }
//
//  private double pop() {
//    return stack[--stackptr];
//  }
//
//  private Complex perform_formula(double zx, double zy, double cx, double cy) {
//    int i, j;
//    boolean number;
//    Complex z = new Complex();
//    double sx, sy, hx, hy, k, pnt;
//
//    initstack();
//    i = 0;
//    sx = sy = 0.0;
//    while (form[i] != 0) {
//      k = pnt = 0.0;
//      number = false;
//      while ((form[i] == '.') || ((form[i] >= '0') && (form[i] <= '9'))) {
//        number = true;
//        if (form[i] == '.') {
//          pnt = 0.1;
//          i++;
//        }
//        else if (pnt == 0.0) {
//          k = k * 10.0 + (form[i++] - '0');
//        }
//        else {
//          k = k + pnt * (form[i++] - '0');
//          pnt *= 0.1;
//        }
//      }
//      if (number)
//        push(k, 0.0);
//      switch (form[i++]) {
//        case 'z':
//          push(zx, zy);
//          break;
//        case 'c':
//          push(cx, cy);
//          break;
//        case 'i':
//          push(0.0, 1.0);
//          break;
//        case 'n':
//          push((double) this.exponent, 0.0);
//          break;
//
//        case '+':
//          hx = pop();
//          hy = pop();
//          sx = pop();
//          sy = pop();
//          sx += hx;
//          sy += hy;
//          push(sx, sy);
//          break;
//        case '-':
//          hx = pop();
//          hy = pop();
//          sx = pop();
//          sy = pop();
//          sx -= hx;
//          sy -= hy;
//          push(sx, sy);
//          break;
//        case '*':
//          hx = pop();
//          hy = pop();
//          sx = pop();
//          sy = pop();
//          {
//            double xa = hx * sx - hy * sy;
//            double ya = hx * sy + sx * hy;
//            sx = xa;
//            sx = ya;
//          }
//          push(sx, sy);
//          break;
//        case '/':
//          hx = pop();
//          hy = pop();
//          sx = pop();
//          sy = pop();
//          {
//            double da = hx * hx + hy * hy;
//            double xa = (sx * hx + sy * hy) / da;
//            double ya = (sy * hx - sx * hy) / da;
//            sx = xa;
//            sx = ya;
//          }
//          push(sx, sy);
//          break;
//        case '^':
//          k = pop();
//          hy = pop();
//          sx = pop();
//          sy = pop();
//          if (k == (double) ((long) k)) {
//            if (k == 2.0) {
//              hx = sx * sx - sy * sy;
//              hy = 2.0 * sx * sy;
//            }
//            else if (k == 3.0) {
//              hx = sx * sx * sx - 3.0 * sx * sy * sy;
//              hy = 3.0 * sx * sx * sy - sy * sy * sy;
//            }
//            else {
//              hx = sx * sx * sx * sx + sy * sy * sy * sy - 6.0 * sx * sx * sy * sy;
//              hy = 4.0 * sx * sy * (sx * sx - sy * sy);
//              for (j = 4; j < k; j++) {
//                double xa = sx * hx - sy * hy;
//                double ya = sx * hy + hx * sy;
//                hx = xa;
//                hy = ya;
//              }
//            }
//            push(hx, hy);
//          }
//          else {
//            {
//              final double PID2 = 0.5 * Math.PI;
//              double v;
//              double da = Math.pow(sx * sx + sy * sy, k / 2.0);
//              if (sx == 0.0)
//                if (sy >= 0.0)
//                  v = PID2;
//                else
//                  v = -PID2;
//              else
//                v = Math.atan2(sy, sx);
//              sx = da * Math.cos(v * k);
//              sy = da * Math.sin(v * k);
//            }
//            push(sx, sy);
//          }
//          break;
//
//        case 'x':
//          push(zx, 0.0);
//          break;
//        case 'y':
//          push(0.0, zy);
//          break;
//        case 'a':
//          push(cx, 0.0);
//          break;
//        case 'b':
//          push(0.0, cy);
//          break;
//
//        case 0xb2: /* "²" */
//          hx = pop();
//          hy = pop();
//          sx = hx * hx - hy * hy;
//          sy = 2.0 * hx * hy;
//          push(sx, sy);
//          break;
//        case 0xb3: /* "³" */
//          hx = pop();
//          hy = pop();
//          sx = hx * hx * hx - 3.0 * hx * hy * hy;
//          sy = 3.0 * hx * hx * hy - hy * hy * hy;
//          push(sx, sy);
//          break;
//      }
//    }
//    sx = pop();
//    sy = pop();
//    z.re = sx;
//    z.im = sy;
//    return z;
//  }
//
//  private void formula_mand(SimpleImage res) {
//    int i, j, iter;
//    Complex z;
//    double x1, y1, xs, ys, x, y, xstep, ystep, count;
//    int width = res.getImageWidth();
//    int height = res.getImageHeight();
//    x = this.xMin;
//    y = this.yMin;
//    xstep = (this.xMax - this.xMin) / (double) width;
//    ystep = (this.yMax - this.yMin) / (double) height;
//    count = 0.0;
//    for (j = 0; j < height; j++) {
//      for (i = 0; i < width; i++) {
//        x1 = x;
//        y1 = y;
//        iter = 0;
//        xs = x1 * x1;
//        ys = y1 * y1;
//        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
//          z = perform_formula(x1, y1, x, y);
//          x1 = z.re;
//          y1 = z.im;
//          xs = x1 * x1;
//          ys = y1 * y1;
//        }
//        plot_pixel(i, j, iter);
//        count += (double) iter;
//        x += xstep;
//      }
//      x = this.xMin;
//      y += ystep;
//    }
//    gAverage = count / width * height;
//  }
//
//  private void formula_julia(SimpleImage res) {
//    int i, j, iter;
//    Complex z;
//    double x1, y1, xs, ys, x, y, jx, jy, xstep, ystep, count;
//    int width = res.getImageWidth();
//    int height = res.getImageHeight();
//    jx = this.xSeed;
//    jy = this.ySeed;
//    x = this.xMin;
//    y = this.yMin;
//    xstep = (this.xMax - this.xMin) / (double) width;
//    ystep = (this.yMax - this.yMin) / (double) height;
//    count = 0.0;
//    for (j = 0; j < height; j++) {
//      for (i = 0; i < width; i++) {
//        x1 = x;
//        y1 = y;
//        iter = 0;
//        xs = x1 * x1;
//        ys = y1 * y1;
//        while ((iter++ < this.maxIter) && (Math.abs(xs + ys) < 4.0)) {
//          z = perform_formula(x1, y1, jx, jy);
//          x1 = z.re;
//          y1 = z.im;
//          xs = x1 * x1;
//          ys = y1 * y1;
//        }
//        plot_pixel(i, j, iter);
//        count += (double) iter;
//        x += xstep;
//      }
//      x = this.xMin;
//      y += ystep;
//    }
//    gAverage = count / width * height;
//  }
//
//  public FormulaStyle getFormulaStyle() {
//    return formulaStyle;
//  }
//
//  public void setFormulaStyle(FormulaStyle formulaStyle) {
//    this.formulaStyle = formulaStyle;
//  }
//
//  public String getFormula() {
//    return formula;
//  }
//
//  public void setFormula(String formula) {
//    this.formula = formula;
//  }
//
//  public int getExponent() {
//    return exponent;
//  }
//
//  public void setExponent(int exponent) {
//    this.exponent = exponent;
//  }
//
//  public int getMaxIter() {
//    return maxIter;
//  }
//
//  public void setMaxIter(int maxIter) {
//    this.maxIter = maxIter;
//  }
//
//  public int getColorRepeat() {
//    return colorRepeat;
//  }
//
//  public void setColorRepeat(int colorRepeat) {
//    this.colorRepeat = colorRepeat;
//  }
//
//  public int getEvenColor() {
//    return evenColor;
//  }
//
//  public void setEvenColor(int evenColor) {
//    this.evenColor = evenColor;
//  }
//
//  public int getOddColor() {
//    return oddColor;
//  }
//
//  public void setOddColor(int oddColor) {
//    this.oddColor = oddColor;
//  }
//
//  public double getXMin() {
//    return xMin;
//  }
//
//  public void setXMin(double xMin) {
//    this.xMin = xMin;
//  }
//
//  public double getXMax() {
//    return xMax;
//  }
//
//  public void setXMax(double xMax) {
//    this.xMax = xMax;
//  }
//
//  public double getYMin() {
//    return yMin;
//  }
//
//  public void setYMin(double yMin) {
//    this.yMin = yMin;
//  }
//
//  public double getYMax() {
//    return yMax;
//  }
//
//  public void setYMax(double yMax) {
//    this.yMax = yMax;
//  }
//
//  public double getXSeed() {
//    return xSeed;
//  }
//
//  public void setXSeed(double xSeed) {
//    this.xSeed = xSeed;
//  }
//
//  public double getYSeed() {
//    return ySeed;
//  }
//
//  public void setYSeed(double ySeed) {
//    this.ySeed = ySeed;
//  }
//
//  public static class TypeEditor extends ComboBoxPropertyEditor {
//    public TypeEditor() {
//      super();
//      setAvailableValues(new Type[] { Type.MANDELBROT, Type.JULIA, Type.DRAGON, Type.SALAMANDER,
//          Type.METEORS, Type.PEARLS, Type.FORMULA });
//    }
//  }
//
//  public static class FormulaStyleEditor extends ComboBoxPropertyEditor {
//    public FormulaStyleEditor() {
//      super();
//      setAvailableValues(new FormulaStyle[] { FormulaStyle.MANDELBROT, FormulaStyle.JULIA });
//    }
//  }
//
//  public Type getType() {
//    return type;
//  }
//
//  public void setType(Type type) {
//    this.type = type;
//  }
//
//}
