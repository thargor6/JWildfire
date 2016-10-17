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
package org.jwildfire.transform;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.Mesh3DTransformer.Faces;
import org.jwildfire.transform.Mesh3DTransformer.Light;


public abstract class Mesh3DRenderer {
  public abstract void renderImage(Mesh3D pMesh3D, Mesh3DTransformer pMesh3DTransformer,
      SimpleImage pImg);

  protected static final double PZERO = 0.000001;
  protected static final int MAXLIGHT = 4;

  protected int x12[], y12[], z12[];
  protected int x23[], y23[], z23[];
  protected int x41[], y41[], z41[];
  protected int f3Count;
  protected int p31[], p32[], p33[];
  protected int coloro[];
  protected double x[], y[], z[];
  protected int width, height;
  protected double cx, cy;
  protected int fInd[];
  protected double fZSortArray[];
  protected final Pixel toolPixel = new Pixel();
  protected Light doLight;
  protected Faces faces;
  protected double reff, rr;
  protected double refx, refy, refz;
  protected double nfx, nfy, nfz, cosa;
  protected double viewz;
  protected int min, max, zMin, zMax;
  protected int pr, pg, pb;
  protected double ttfr, ttfg, ttfb;
  protected int havex, k12r, k23r, n23, n, k, k41r, n41;
  protected int lux, luy, luz;
  protected int rux, ruy, ruz;
  protected int rbx, rby, rbz;
  protected int n12, iv;
  protected int r, g, b;
  protected double lightX[];
  protected double lightY[];
  protected double lightZ[];
  protected double lightRed[];
  protected double lightGreen[];
  protected double lightBlue[];
  protected double ambient, diffuse, phong, phongSize;
  protected int lightCount;
  protected double[] u;
  protected double[] v;
  protected SimpleImage texture;

  protected void init(Mesh3D pMesh3D, Mesh3DTransformer pMesh3DTransformer, SimpleImage pImg) {
    /* allocate the structures */
    int bsize = 1024;
    x12 = new int[bsize];
    y12 = new int[bsize];
    z12 = new int[bsize];
    x23 = new int[bsize];
    y23 = new int[bsize];
    z23 = new int[bsize];
    x41 = new int[bsize];
    y41 = new int[bsize];
    z41 = new int[bsize];
    f3Count = pMesh3D.getFCount();
    p31 = pMesh3D.getPP1();
    p32 = pMesh3D.getPP2();
    p33 = pMesh3D.getPP3();
    x = pMesh3D.getX();
    y = pMesh3D.getY();
    z = pMesh3D.getZ();
    width = pImg.getImageWidth();
    height = pImg.getImageHeight();
    cx = (double) width / 2.0;
    cy = (double) height / 2.0;
    coloro = pMesh3D.getColor();
    u = pMesh3D.getU();
    v = pMesh3D.getV();
    texture = pMesh3D.getTexture();
    fInd = new int[f3Count];
    fZSortArray = new double[f3Count];

    // TODO cFunc 
    //  doColorFunc=obj->colorFunc;
    lightX = new double[MAXLIGHT];
    lightY = new double[MAXLIGHT];
    lightZ = new double[MAXLIGHT];
    lightRed = new double[MAXLIGHT];
    lightGreen = new double[MAXLIGHT];
    lightBlue = new double[MAXLIGHT];

    lightX[0] = pMesh3DTransformer.getLight1X();
    lightY[0] = pMesh3DTransformer.getLight1Y();
    lightZ[0] = pMesh3DTransformer.getLight1Z();
    lightRed[0] = (double) pMesh3DTransformer.getLight1Color().getRed() / 255.0;
    lightGreen[0] = (double) pMesh3DTransformer.getLight1Color().getGreen() / 255.0;
    lightBlue[0] = (double) pMesh3DTransformer.getLight1Color().getBlue() / 255.0;

    lightX[1] = pMesh3DTransformer.getLight2X();
    lightY[1] = pMesh3DTransformer.getLight2Y();
    lightZ[1] = pMesh3DTransformer.getLight2Z();
    lightRed[1] = (double) pMesh3DTransformer.getLight2Color().getRed() / 255.0;
    lightGreen[1] = (double) pMesh3DTransformer.getLight2Color().getGreen() / 255.0;
    lightBlue[1] = (double) pMesh3DTransformer.getLight2Color().getBlue() / 255.0;

    lightX[2] = pMesh3DTransformer.getLight3X();
    lightY[2] = pMesh3DTransformer.getLight3Y();
    lightZ[2] = pMesh3DTransformer.getLight3Z();
    lightRed[2] = (double) pMesh3DTransformer.getLight3Color().getRed() / 255.0;
    lightGreen[2] = (double) pMesh3DTransformer.getLight3Color().getGreen() / 255.0;
    lightBlue[2] = (double) pMesh3DTransformer.getLight3Color().getBlue() / 255.0;

    lightX[3] = pMesh3DTransformer.getLight4X();
    lightY[3] = pMesh3DTransformer.getLight4Y();
    lightZ[3] = pMesh3DTransformer.getLight4Z();
    lightRed[3] = (double) pMesh3DTransformer.getLight4Color().getRed() / 255.0;
    lightGreen[3] = (double) pMesh3DTransformer.getLight4Color().getGreen() / 255.0;
    lightBlue[3] = (double) pMesh3DTransformer.getLight4Color().getBlue() / 255.0;

    lightCount = MAXLIGHT;
    for (int i = MAXLIGHT - 1; i > 0; i--) {
      if ((lightRed[i] < MathLib.EPSILON) && (lightGreen[i] < MathLib.EPSILON) && (lightBlue[i] < MathLib.EPSILON))
        lightCount--;
      else
        break;
    }
    ambient = pMesh3DTransformer.getAmbient();
    if (ambient < 0.0)
      ambient = 0.0;
    else if (ambient > 1.0)
      ambient = 1.0;
    diffuse = pMesh3DTransformer.getDiffuse();
    if (diffuse < 0.0)
      diffuse = 0.0;
    else if (diffuse > 1.0)
      diffuse = 1.0;
    phong = pMesh3DTransformer.getPhong();
    if (phong < 0.0)
      phong = 0.0;
    phongSize = pMesh3DTransformer.getPhongSize();
    if (phongSize < 0.0)
      phongSize = 0.0;
    faces = pMesh3DTransformer.getFaces();

    doLight = pMesh3DTransformer.getLight();
    if (doLight != Light.OFF) {
      initPhongArray(phong, phongSize);
    }
    else
      faces = Faces.DOUBLE;
  }

  protected void heapSortFloat1(double[] f, int[] ind, int count) {
    int j, help;
    for (j = count / 2; j >= 0; j--)
      correctFloat1(j, count - 1, f, ind);
    for (j = count - 1; j > 0; j--) {
      help = ind[0];
      ind[0] = ind[j];
      ind[j] = help;
      correctFloat1(0, j - 1, f, ind);
    }
  }

  private void correctFloat1(int i, int n, double[] ff, int[] ind) {
    int j, help;
    j = i * 2;
    if (j <= n) {
      if (((j + 1) <= n) && (ff[ind[j + 1]] > ff[ind[j]]))
        j++;
      if (ff[ind[j]] > ff[ind[i]]) {
        help = ind[i];
        ind[i] = ind[j];
        ind[j] = help;
        correctFloat1(j, n, ff, ind);
      }
    }
  }

  protected int bresenham3D(int x1, int y1, int z1, int x2, int y2, int z2, int[] x, int[] y,
      int[] z) {
    int dx, dy, xf, yf, a, b, c, i;
    double zz, dz;
    if (x2 > x1) {
      dx = x2 - x1;
      xf = 1;
    }
    else {
      dx = x1 - x2;
      xf = -1;
    }
    if (y2 > y1) {
      dy = y2 - y1;
      yf = 1;
    }
    else {
      dy = y1 - y2;
      yf = -1;
    }
    if (dx > dy) {
      zz = (double) z1;
      if (dx > 0)
        dz = (double) (z2 - z1) / (double) dx;
      else
        dz = 0.0;
      a = dy + dy;
      c = a - dx;
      b = c - dx;
      for (i = 0; i <= dx; i++) {
        x[i] = x1;
        y[i] = y1;
        z[i] = (int) (zz + 0.5);
        zz += dz;
        x1 += xf;
        if (c < 0) {
          c += a;
        }
        else {
          c += b;
          y1 += yf;
        }
      }
      return ((int) (dx + 1));
    }
    else {
      zz = (double) z1;
      if (dy > 0)
        dz = (double) (z2 - z1) / (double) dy;
      else
        dz = 0.0;
      a = dx + dx;
      c = a - dy;
      b = c - dy;
      for (i = 0; i <= dy; i++) {
        x[i] = x1;
        y[i] = y1;
        z[i] = (int) (zz + 0.5);
        zz += dz;
        y1 += yf;
        if (c < 0) {
          c += a;
        }
        else {
          c += b;
          x1 += xf;
        }
      }
      return ((int) (dy + 1));
    }
  }

  protected void reflectViewVector() {
    reff = nfz + nfz;
    refx = reff * nfx;
    refy = reff * nfy;
    refz = reff * nfz - viewz;
    rr = Math.sqrt(refx * refx + refy * refy + refz * refz);
    if (Math.abs(rr) < PZERO) {
      refx = refy = refz = 0;
    }
    else {
      refx /= rr;
      refy /= rr;
      refz /= rr;
    }

    /*
      reff=nfz+nfz; 
      refx=reff*nfx; 
      refy=reff*nfy; 
      refz=reff*nfz-viewz; 

      reff=nfx*viewx+nfy*viewy+nfz*viewz;reff+=reff; 
      refx=reff*nfx-viewx; 
      refy=reff*nfy-viewy; 
      refz=reff*nfz-viewz; 

      if(TESTLMB()!=0) { 
       printf("*n: %g %g %g\n",nfx,nfy,nfz); 
       printf("*ref: %g %g %g\n",refx,refy,refz); 
      } 
    */
  }

  protected void rFill3() {
    /* search the left and right x-value */
    havex = 0;
    if (((k >= luy) && (k <= ruy)) || ((k >= ruy) && (k <= luy))) {
      havex++;
      n = -1;
      do {
        n++;
      }
      while (y12[n] != k);
      min = max = x12[n];
      zMin = zMax = z12[n];
      k12r = n + 1;
      while (k12r < n12) {
        if (y12[k12r] != k)
          break;
        k12r++;
      }
      if ((--k12r) > n) {
        iv = x12[k12r];
        if (iv < min) {
          min = iv;
          zMin = z12[k12r];
        }
        else if (iv > max) {
          max = iv;
          zMax = z12[k12r];
        }
      }
    }
    if (((k >= ruy) && (k <= rby)) || ((k >= rby) && (k <= ruy))) {
      if (havex == 0) {
        havex++;
        n = -1;
        do {
          n++;
        }
        while (y23[n] != k);
        min = max = x23[n];
        zMin = zMax = z23[n];
        k23r = n + 1;
        while (k23r < n23) {
          if (y23[k23r] != k)
            break;
          k23r++;
        }
        if ((--k23r) > n) {
          iv = x23[k23r];
          if (iv < min) {
            min = iv;
            zMin = z23[k23r];
          }
          else if (iv > max) {
            max = iv;
            zMax = z23[k23r];
          }
        }
      }
      else {
        n = -1;
        do {
          n++;
        }
        while (y23[n] != k);
        iv = x23[n];
        if (iv < min) {
          min = iv;
          zMin = z23[n];
        }
        else if (iv > max) {
          max = iv;
          zMax = z23[n];
        }
        k23r = n + 1;
        while (k23r < n23) {
          if (y23[k23r] != k)
            break;
          k23r++;
        }
        if ((--k23r) > n) {
          iv = x23[k23r];
          if (iv < min) {
            min = iv;
            zMin = z23[k23r];
          }
          else if (iv > max) {
            max = iv;
            zMax = z23[k23r];
          }
        }
      }
    }
    if (((k >= rby) && (k <= luy)) || ((k >= luy) && (k <= rby))) {
      if (havex == 0) {
        havex++;
        n = -1;
        do {
          n++;
        }
        while (y41[n] != k);
        min = max = x41[n];
        zMin = zMax = z41[n];
        k41r = n + 1;
        while (k41r < n41) {
          if (y41[k41r] != k)
            break;
          k41r++;
        }
        if ((--k41r) > n) {
          iv = x41[k41r];
          if (iv < min) {
            min = iv;
            zMin = z41[k41r];
          }
          else if (iv > max) {
            max = iv;
            zMax = z41[k41r];
          }
        }
      }
      else {
        n = -1;
        do {
          n++;
        }
        while (y41[n] != k);
        iv = x41[n];
        if (iv < min) {
          min = iv;
          zMin = z41[n];
        }
        else if (iv > max) {
          max = iv;
          zMax = z41[n];
        }
        k41r = n + 1;
        while (k41r < n41) {
          if (y41[k41r] != k)
            break;
          k41r++;
        }
        if ((--k41r) > n) {
          iv = x41[k41r];
          if (iv < min) {
            min = iv;
            zMin = z41[k41r];
          }
          else if (iv > max) {
            max = iv;
            zMax = z41[k41r];
          }
        }
      }
    }
  }

  protected void addLight(double px, double py, double pz) {
    ttfr = ttfg = ttfb = 0.0;
    double rn = (double) r / 255.0;
    double gn = (double) g / 255.0;
    double bn = (double) b / 255.0;
    for (int q = 0; q < lightCount; q++) {
      double dint = 0.0;
      double pint = 0.0;

      double lx = px - lightX[q];
      double ly = py - lightY[q];
      double lz = pz - lightZ[q];
      rr = Math.sqrt(lx * lx + ly * ly + lz * lz);
      if (Math.abs(rr) <= PZERO) {
        lx = ly = 0.0;
        lz = -1.0;
      }
      else {
        lx /= rr;
        ly /= rr;
        lz /= rr;
      }
      cosa = nfx * lx + nfy * ly + nfz * lz;

      if (faces != Faces.DOUBLE) {
        if (cosa < 0.0)
          cosa = 0.0;
      }
      else {
        if (cosa < 0.0) {
          cosa = 0.0 - cosa;
          nfx = 0.0 - nfx;
          nfy = 0.0 - nfy;
          nfz = 0.0 - nfz;
          reflectViewVector();
        }
      }
      dint = diffuse * cosa;

      cosa = refx * lx + refy * ly + refz * lz;
      if (cosa > 0.0)
        pint = phongInt(cosa);

      dint = (dint > 1.0 ? 1.0 : dint);
      pint = (pint > 1.0 ? 1.0 : pint);
      ttfr += ((dint * rn + pint) * lightRed[q]);
      ttfg += ((dint * gn + pint) * lightGreen[q]);
      ttfb += ((dint * bn + pint) * lightBlue[q]);
    }
    ttfr += rn * ambient;
    ttfg += gn * ambient;
    ttfb += bn * ambient;
    //System.out.println(ttfr + " " + ttfg + " " + ttfb);
    int tt = (int) (ttfr * 255.0 + 0.5);
    if (tt < 0)
      tt = 0;
    else if (tt > 255)
      tt = 255;
    pr = tt;
    tt = (int) (ttfg * 255.0 + 0.5);
    if (tt < 0)
      tt = 0;
    else if (tt > 255)
      tt = 255;
    pg = tt;
    tt = (int) (ttfb * 255.0 + 0.5);
    if (tt < 0)
      tt = 0;
    else if (tt > 255)
      tt = 255;
    pb = tt;

    /*  cosa=refx*lx+refy*ly+refz*lz; \
      if(cosa>0.0) { \
       pint=phong*pow(cosa,phongSize); \
      } \*/
  }

  protected static final int PASIZE = 4096;
  protected double dPhong = 0.0;
  protected double phongArray[] = new double[PASIZE + 4];

  protected void initPhongArray(double pPhong, double pPhongSize) {
    dPhong = 1.0 / (double) (PASIZE - 1);
    double a = 0.0;
    for (int i = 0; i < (PASIZE + 4); i++) {
      phongArray[i] = pPhong * Math.pow(a, pPhongSize);
      a += dPhong;
    }
  }

  protected double phongInt(double cosa) {
    /*
    PTFLOAT res,fi;
    WORD i1,i2;
    fi=cosa/dPhong;
    i1=(WORD)fi;
    i2=i1+1;
    res=phongArray[i1]+(fi-(PTFLOAT)i1)*(phongArray[i2]-phongArray[i1]);
    if(TESTLMB()!=0) {
     printf("res: %f, exact: %f, res2: %f\n",res,oldPhong*pow(cosa,oldPhongSize),phongArray[(WORD)(cosa/dPhong+0.5)]);
    }
    return(res);
    */
    /*
        System.out.println(cosa + "/" + ((int) (cosa / dPhong + 0.5)) + ": "
            + (phongArray[(int) (cosa / dPhong + 0.5)]));
    */
    return phongArray[(int) (cosa / dPhong + 0.5)];
  }

}
