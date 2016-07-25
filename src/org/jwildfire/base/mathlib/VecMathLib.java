package org.jwildfire.base.mathlib;

import org.jwildfire.image.SimpleImage;

public final class VecMathLib {
  public static final double COLORSCL = 255.0;

  public static final class RGBColorD {
    public double r, g, b;

    public RGBColorD() {
    }

    public RGBColorD(double r, double g, double b) {
      this.r = r;
      this.g = g;
      this.b = b;
    }

    public RGBColorD(double r, double g, double b, double scale) {
      this.r = r * scale;
      this.g = g * scale;
      this.b = b * scale;
    }

    public RGBColorD(RGBColorD src, double scale) {
      this.r = src.r * scale;
      this.g = src.g * scale;
      this.b = src.b * scale;
    }

    public RGBColorD(int argb) {
      setARGBValue(argb);
    }

    public void setARGBValue(int argb) {
      // int a = (argb >>> 24) & 0xff;
      r = ((argb >>> 16) & 0xff) / COLORSCL;
      g = ((argb >>> 8) & 0xff) / COLORSCL;
      b = (argb & 0xff) / COLORSCL;
    }

    public void addFrom(double r, double g, double b) {
      this.r += r;
      this.g += g;
      this.b += b;
    }

    public void addFrom(double r, double g, double b, double scale) {
      this.r += r * scale;
      this.g += g * scale;
      this.b += b * scale;
    }
  }

  public static final class UVPairD {
    public double u, v;

    public UVPairD(double u, double v) {
      this.u = u;
      this.v = v;
    }

    public static UVPairD sphericalOpenGlMapping(VectorD vec) {
      double m = 2.0 * MathLib.sqrt(vec.x * vec.x + vec.y * vec.y + MathLib.sqr(vec.z + 1.0));
      return new UVPairD(GfxMathLib.clamp(vec.x / m + 0.5), GfxMathLib.clamp(vec.y / m + 0.5));
    }

    public static UVPairD sphericalBlinnNewellLatitudeMapping(VectorD vec) {
      return new UVPairD(GfxMathLib.clamp((MathLib.atan(vec.x / vec.z) + MathLib.M_PI) / (MathLib.M_PI + MathLib.M_PI)),
          GfxMathLib.clamp((MathLib.asin(vec.y) + MathLib.M_PI * 0.5) / MathLib.M_PI));
    }

    public RGBColorD getColorFromMap(SimpleImage map) {
      int uImg = (int) (map.getImageWidth() * u);
      int vImg = (int) (map.getImageHeight() * v);

      RGBColorD lu = new RGBColorD(map.getARGBValueIgnoreBounds(uImg, vImg));
      RGBColorD ru = new RGBColorD(map.getARGBValueIgnoreBounds(uImg + 1, vImg));
      RGBColorD lb = new RGBColorD(map.getARGBValueIgnoreBounds(uImg, vImg + 1));
      RGBColorD rb = new RGBColorD(map.getARGBValueIgnoreBounds(uImg + 1, vImg + 1));

      return new RGBColorD(
          GfxMathLib.blerp(lu.r, ru.r, lb.r, rb.r, u, v),
          GfxMathLib.blerp(lu.g, ru.g, lb.g, rb.g, u, v),
          GfxMathLib.blerp(lu.b, ru.b, lb.b, rb.b, u, v));
    }
  }

  public static final class VectorD {
    public double x, y, z;

    public VectorD() {

    }

    public VectorD(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public double length() {
      return MathLib.sqrt(x * x + y * y + z * z);
    }

    public void normalize() {
      double l = length() + 1.0e-16;
      x /= l;
      y /= l;
      z /= l;
    }

    public static double dot(VectorD a, VectorD b) {
      return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static VectorD reflect(VectorD d, VectorD n) {
      // r=d - 2(d*n)n
      double dn = dot(d, n);
      return new VectorD(d.x - 2.0 * dn * n.x, d.y - 2.0 * dn * n.y, d.z - 2.0 * dn * n.z);
    }

    public void addFrom(VectorD src) {
      x += src.x;
      y += src.y;
      z += src.z;
    }

    public void addFrom(VectorD src, double scale) {
      x += src.x * scale;
      y += src.y * scale;
      z += src.z * scale;
    }

    public static VectorD cross(VectorD a, VectorD b) {
      return new VectorD(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }
  }

}
