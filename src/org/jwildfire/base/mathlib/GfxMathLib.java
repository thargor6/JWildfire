package org.jwildfire.base.mathlib;

public final class GfxMathLib {

  public static double clamp(double val, double min, double max) {
    return Math.max(min, Math.min(max, val));
  }

  public static double clamp(double val) {
    return clamp(val, 0.0, 1.0);
  }

  public static double blerp(double c00, double c10, double c01, double c11, double tx, double ty) {
    return GfxMathLib.lerp(GfxMathLib.lerp(c00, c10, tx), GfxMathLib.lerp(c01, c11, tx), ty);
  }

  public static double lerp(double s, double e, double t) {
    return s + (e - s) * t;
  }

  // https://en.wikipedia.org/wiki/Smoothstep
  public static double smoothstep(double edge0, double edge1, double x) {
    // Scale, bias and saturate x to 0..1 range
    x = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    // Evaluate polynomial
    return x * x * (3 - 2 * x);
  }

  // https://en.wikipedia.org/wiki/Smoothstep
  public static double smootherstep(double edge0, double edge1, double x) {
    // Scale, and clamp x to 0..1 range
    x = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    // Evaluate polynomial
    return x * x * x * (x * (x * 6 - 15) + 10);
  }

  // https://en.wikipedia.org/wiki/Smoothstep
  public static double smootheststep(double edge0, double edge1, double x) {
    // Scale, and clamp x to 0..1 range
    x = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    // Evaluate polynomial
    return x * x * x * x * (x * (x * (x * -20 + 70) - 84) + 35);
  }

  public static double step(double edge, double x) {
    return x < edge ? 0.0 : 1.0;
  }

}
