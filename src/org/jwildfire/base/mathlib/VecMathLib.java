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
package org.jwildfire.base.mathlib;

import java.io.Serializable;

import org.jwildfire.image.SimpleImage;

public final class VecMathLib {
  public static final double COLORSCL = 255.0;

  @SuppressWarnings("serial")
  public static final class RGBColorD implements Serializable {
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

  @SuppressWarnings("serial")
  public static final class VectorD implements Serializable, Cloneable {
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

    @Override
    public VectorD clone() {
      return new VectorD(x, y, z);
    }

    public static VectorD cross(VectorD a, VectorD b) {
      return new VectorD(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }

    public static VectorD a(VectorD a, VectorD b) {
      return new VectorD(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static VectorD subtract(VectorD a, VectorD b) {
      return new VectorD(a.x - b.x, a.y - b.y, a.z - b.z);
    }
  }

  @SuppressWarnings("serial")
  public static final class Matrix4D implements Serializable {
    public final double m[][] = new double[4][4];

    public static Matrix4D identity() {
      Matrix4D res = new Matrix4D();
      res.m[0][0] = 1.0;
      res.m[1][1] = 1.0;
      res.m[2][2] = 1.0;
      res.m[3][3] = 1.0;
      return res;
    }

    public static Matrix4D lookAt(VectorD eyePosition, VectorD lookAt, VectorD upVector) {
      VectorD e = eyePosition.clone();
      //      e.normalize();
      VectorD l = lookAt.clone();
      //      l.normalize();
      VectorD up = upVector.clone();
      //      up.normalize();
      // forward vector
      VectorD forward = VectorD.subtract(l, e);
      //      forward.normalize();
      // side vector
      VectorD side = VectorD.cross(forward, up);
      //      side.normalize();

      Matrix4D rotmat = new Matrix4D();

      rotmat.m[0][0] = side.x;
      rotmat.m[1][0] = side.y;
      rotmat.m[2][0] = side.z;
      rotmat.m[3][0] = 0.0;

      rotmat.m[0][1] = up.x;
      rotmat.m[1][1] = up.y;
      rotmat.m[2][1] = up.z;
      rotmat.m[3][1] = 0.0;

      rotmat.m[0][2] = -forward.x;
      rotmat.m[1][2] = -forward.y;
      rotmat.m[2][2] = -forward.z;
      rotmat.m[3][2] = 0.0;

      rotmat.m[3][3] = 1.0;

      Matrix4D transmat = Matrix4D.translateMatrix(-e.x, -e.y, -e.z);

      return Matrix4D.multiply(transmat, rotmat);
    }

    public static Matrix4D multiply(Matrix4D a, Matrix4D b) {
      Matrix4D res = new Matrix4D();
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
          res.m[i][j] = 0.0;
          for (int s = 0; s < 4; s++) {
            res.m[i][j] += a.m[i][s] * b.m[s][j];
          }
        }
      }
      return res;
    }

    public static Matrix4D translateMatrix(double tx, double ty, double tz) {
      Matrix4D res = Matrix4D.identity();
      res.m[0][3] = tx;
      res.m[1][3] = ty;
      res.m[2][3] = tz;
      return res;
    }

    public static VectorD multiply(Matrix4D a, VectorD v) {
      VectorD res = new VectorD();
      res.x = v.x * a.m[0][0] + v.y * a.m[0][1] + v.z * a.m[0][2] + a.m[0][3];
      res.y = v.x * a.m[1][0] + v.y * a.m[1][1] + v.z * a.m[1][2] + a.m[1][3];
      res.z = v.x * a.m[2][0] + v.y * a.m[2][1] + v.z * a.m[2][2] + a.m[2][3];
      return res;
    }
  }

  @SuppressWarnings("serial")
  public static final class Matrix3D implements Serializable {
    public final double m[][] = new double[3][3];

    public static Matrix3D identity() {
      Matrix3D res = new Matrix3D();
      res.m[0][0] = 1.0;
      res.m[1][1] = 1.0;
      res.m[2][2] = 1.0;
      return res;
    }

    public static Matrix3D multiply(Matrix3D a, Matrix3D b) {
      Matrix3D res = new Matrix3D();
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          res.m[i][j] = 0.0;
          for (int s = 0; s < 3; s++) {
            res.m[i][j] += a.m[i][s] * b.m[s][j];
          }
        }
      }
      return res;
    }

    public static VectorD multiply(Matrix3D a, VectorD v) {
      VectorD res = new VectorD();
      res.x = v.x * a.m[0][0] + v.y * a.m[0][1] + v.z * a.m[0][2];
      res.y = v.x * a.m[1][0] + v.y * a.m[1][1] + v.z * a.m[1][2];
      res.z = v.x * a.m[2][0] + v.y * a.m[2][1] + v.z * a.m[2][2];
      return res;
    }
  }
}
