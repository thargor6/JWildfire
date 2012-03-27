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

import static org.jwildfire.base.MathLib.M_PI;

import org.jwildfire.create.tina.random.RandomNumberGenerator;
import org.jwildfire.create.tina.random.RandomNumberGenerator.RandGenStatus;
import org.jwildfire.create.tina.render.FlameRenderer;

public class FastFlameTransformationContextImpl implements FlameTransformationContext {
  private final RandomNumberGenerator randGen;
  private final FlameRenderer flameRenderer;

  public FastFlameTransformationContextImpl(FlameRenderer pFlameRenderer) {
    randGen = pFlameRenderer.getRandomNumberGenerator();
    flameRenderer = pFlameRenderer;
  }

  @Override
  public final double random() {
    return randGen.random();
  }

  @Override
  public final int random(int pMax) {
    return randGen.random(pMax);
  }

  @Override
  public final void setRandGenStatus(RandGenStatus pRandGenStatus) {
    randGen.setStatus(pRandGenStatus);
  }

  @Override
  public final double sin(double a) {
    //    return sin(a);
    return sin[(int) (a * radToIndex) & TRIG_PRECALC_MASK];
  }

  @Override
  public final double cos(double a) {
    //    return cos(a);
    return cos[(int) (a * radToIndex) & TRIG_PRECALC_MASK];
  }

  @Override
  public final double tan(double a) {
    //    return tan(a);
    return tan[(int) (a * radToIndex) & TRIG_PRECALC_MASK];
  }

  @Override
  public FlameRenderer getFlameRenderer() {
    return flameRenderer;
  }

  @Override
  public final double exp(double a) {
    if (a > EXP_MIN && a < EXP_MAX) {
      int idx = (int) ((a - EXP_MIN) * EXP_SCL);
      return exp[idx];
    }
    else {
      return Math.exp(a);
    }
    //    return Math.exp(a);
  }

  @Override
  public final double sqrt(double a) {
    //    return sqrt(a);
    //    if (a > 0.0 & a < SQRT_MAX) {
    //      int idx = (int) (a * SQRT_SCL);
    //      return sqrt[idx];
    //    }
    //    else {
    //      return sqrt(a);
    //    }
    return sqrt(a);
  }

  // Fast sin/cos approx following http://www.java-gaming.org/index.php?topic=24191.0
  private static final int TRIG_PRECALC_BITS, TRIG_PRECALC_MASK, TRIG_PRECALC_COUNT;
  private static final double radFull, radToIndex;
  private static final double degFull, degToIndex;
  private static final double[] sin, cos, tan;

  private static final int EXP_PRECALC_COUNT;
  private static final double EXP_MIN, EXP_MAX, EXP_SCL;
  private static final double[] exp;

  //  private static final int SQRT_PRECALC_COUNT;
  //  private static final double SQRT_MAX, SQRT_SCL;
  //  private static final double[] sqrt;

  static {
    // sin, cos, tan
    TRIG_PRECALC_BITS = 16;
    TRIG_PRECALC_MASK = ~(-1 << TRIG_PRECALC_BITS);
    TRIG_PRECALC_COUNT = TRIG_PRECALC_MASK + 1;

    radFull = (M_PI * 2.0);
    degFull = (360.0);
    radToIndex = TRIG_PRECALC_COUNT / radFull;
    degToIndex = TRIG_PRECALC_COUNT / degFull;

    sin = new double[TRIG_PRECALC_COUNT];
    cos = new double[TRIG_PRECALC_COUNT];
    tan = new double[TRIG_PRECALC_COUNT];

    for (int i = 0; i < TRIG_PRECALC_COUNT; i++) {
      sin[i] = Math.sin((i + 0.5) / TRIG_PRECALC_COUNT * radFull);
      cos[i] = Math.cos((i + 0.5) / TRIG_PRECALC_COUNT * radFull);
      tan[i] = Math.tan((i + 0.5) / TRIG_PRECALC_COUNT * radFull);
    }
    for (int i = 0; i < 360; i += 90) {
      sin[(int) (i * degToIndex) & TRIG_PRECALC_MASK] = (double) Math.sin(i * M_PI / 180.0);
      cos[(int) (i * degToIndex) & TRIG_PRECALC_MASK] = (double) Math.cos(i * M_PI / 180.0);
      tan[(int) (i * degToIndex) & TRIG_PRECALC_MASK] = (double) Math.tan(i * M_PI / 180.0);
    }
    // exp
    int EXP_PRECALC_BITS = 17;
    int EXP_PRECALC_MASK = ~(-1 << EXP_PRECALC_BITS);
    EXP_PRECALC_COUNT = EXP_PRECALC_MASK + 1;
    EXP_MIN = -2.0;
    EXP_MAX = 2.0;
    exp = new double[EXP_PRECALC_COUNT + 1];
    EXP_SCL = (double) EXP_PRECALC_COUNT / (EXP_MAX - EXP_MIN);
    for (int i = 0; i <= EXP_PRECALC_COUNT; i++) {
      double x = EXP_MIN + (double) (i + 0.5) / EXP_SCL;
      exp[i] = Math.exp(x);
    }
    // sqrt
    //    int SQRT_PRECALC_BITS = 18;
    //    int SQRT_PRECALC_MASK = ~(-1 << SQRT_PRECALC_BITS);
    //    SQRT_PRECALC_COUNT = SQRT_PRECALC_MASK + 1;
    //    SQRT_MAX = 6.0;
    //    sqrt = new double[SQRT_PRECALC_COUNT + 1];
    //    SQRT_SCL = (double) SQRT_PRECALC_COUNT / SQRT_MAX;
    //    for (int i = 0; i <= SQRT_PRECALC_COUNT; i++) {
    //      double x = (double) (i + 0.5) / SQRT_SCL;
    //      sqrt[i] = sqrt(x);
    //    }
  }

  @Override
  public double fmod(double a, double b) {
    return a % b;
  }

  @Override
  public double sqr(double a) {
    return a * a;
  }

}
