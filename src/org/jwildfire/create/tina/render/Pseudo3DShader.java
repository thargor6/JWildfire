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
package org.jwildfire.create.tina.render;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.ShadingInfo;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;

// TODO really a quick hack, copied the ugly shading stuff from the Mesh3DRenderer
public class Pseudo3DShader {
  // internal stuff
  private double reff, refx, refy, refz;
  private double nfx, nfy, nfz;
  private int r, g, b, pb, pr, pg;
  private final ShadingInfo shadingInfo;
  // derived from shadingInfo
  private final int MAXLIGHTS = 4;
  private double lightX[] = new double[MAXLIGHTS];
  private double lightY[] = new double[MAXLIGHTS];
  private double lightZ[] = new double[MAXLIGHTS];
  private double lightRed[] = new double[MAXLIGHTS];
  private double lightGreen[] = new double[MAXLIGHTS];
  private double lightBlue[] = new double[MAXLIGHTS];
  private int lightCount;
  private double ambient;
  private double diffuse;
  private double phong;
  private double phongSize;
  //
  protected final int PASIZE = 4096;
  protected double dPhong = 0.0;
  protected double phongArray[] = new double[PASIZE + 4];

  public Pseudo3DShader(ShadingInfo pShadingInfo) {
    shadingInfo = pShadingInfo;
  }

  public void init() {
    ambient = shadingInfo.getAmbient();
    diffuse = shadingInfo.getDiffuse();
    phong = shadingInfo.getPhong();
    phongSize = shadingInfo.getPhongSize();
    lightCount = 0;
    for (int i = 0; i < shadingInfo.getLightRed().length && lightCount < lightRed.length; i++) {
      if (shadingInfo.getLightRed()[i] > 0 || shadingInfo.getLightGreen()[i] > 0 || shadingInfo.getLightBlue()[i] > 0) {
        lightX[lightCount] = shadingInfo.getLightPosX()[i];
        lightY[lightCount] = shadingInfo.getLightPosY()[i];
        lightZ[lightCount] = shadingInfo.getLightPosZ()[i];
        lightRed[lightCount] = (double) shadingInfo.getLightRed()[i] / 255.0;
        lightGreen[lightCount] = (double) shadingInfo.getLightGreen()[i] / 255.0;
        lightBlue[lightCount] = (double) shadingInfo.getLightBlue()[i] / 255.0;
        lightCount++;
      }
    }
    initPhongArray(phong, phongSize);
  }

  protected void initPhongArray(double pPhong, double pPhongSize) {
    dPhong = 1.0 / (double) (PASIZE - 1);
    double a = 0.0;
    for (int i = 0; i < (PASIZE + 4); i++) {
      phongArray[i] = pPhong * Math.pow(a, pPhongSize);
      a += dPhong;
    }
  }

  protected double phongInt(double cosa) {
    return phongArray[(int) (cosa / dPhong + 0.5)];
  }

  public void distributeInitialPoints(XYZPoint[] p) {
    if (p[0].x > 0) {
      p[1].x = p[0].x + 0.01;
    }
    else {
      p[1].x = p[0].x - 0.01;
    }
    p[1].y = p[0].y;
    p[1].z = p[0].z;
    p[2].x = p[0].x;
    if (p[0].y > 0) {
      p[2].y = p[0].y + 0.01;
    }
    else {
      p[2].y = p[0].y - 0.01;
    }
    p[2].z = p[0].z;
  }

  public RenderColor calculateColor(XYZPoint[] pP, RenderColor pColor) {
    double vax = pP[1].x - pP[0].x;
    double vay = pP[1].y - pP[0].y;
    double vaz = pP[1].z - pP[0].z;
    double vbx = pP[2].x - pP[0].x;
    double vby = pP[2].y - pP[0].y;
    double vbz = pP[2].z - pP[0].z;
    nfx = vay * vbz - vaz * vby;
    nfy = vaz * vbx - vax * vbz;
    nfz = vax * vby - vay * vbx;
    double rr = Math.sqrt(nfx * nfx + nfy * nfy + nfz * nfz);
    if (Math.abs(rr) <= Tools.EPSILON) {
      nfx = nfy = 0.0;
      nfz = -1.0;
    }
    else {
      nfx = 0.0 - nfx / rr;
      nfy = 0.0 - nfy / rr;
      nfz = 0.0 - nfz / rr;
    }

    reflectViewVector();
    double cosa = 0.0 - nfz;
    //    if (cosa >= 0.0) {
    //      nfx = 0.0 - nfx;
    //      nfy = 0.0 - nfy;
    //      nfz = 0.0 - nfz;
    //      reflectViewVector();
    //      cosa = 0.0 - nfz;
    //    }

    this.r = Tools.FTOI(pColor.red);
    this.g = Tools.FTOI(pColor.green);
    this.b = Tools.FTOI(pColor.blue);
    addLight(pP[0].x, pP[0].y, pP[0].z);

    RenderColor res = new RenderColor();
    res.red = pr;
    res.green = pg;
    res.blue = pb;
    return res;
  }

  protected void reflectViewVector() {
    double viewz = -1.0;

    reff = nfz + nfz;
    refx = reff * nfx;
    refy = reff * nfy;
    refz = reff * nfz - viewz;
    double rr = Math.sqrt(refx * refx + refy * refy + refz * refz);
    if (Math.abs(rr) < Tools.EPSILON) {
      refx = refy = refz = 0;
    }
    else {
      refx /= rr;
      refy /= rr;
      refz /= rr;
    }
  }

  protected void addLight(double px, double py, double pz) {
    double ttfr = 0.0, ttfg = 0.0, ttfb = 0.0;
    double rn = (double) r / 255.0;
    double gn = (double) g / 255.0;
    double bn = (double) b / 255.0;
    for (int q = 0; q < lightCount; q++) {
      double dint = 0.0;
      double pint = 0.0;

      double lx = px - lightX[q];
      double ly = py - lightY[q];
      double lz = pz - lightZ[q];
      double rr = Math.sqrt(lx * lx + ly * ly + lz * lz);
      if (Math.abs(rr) <= Tools.EPSILON) {
        lx = ly = 0.0;
        lz = -1.0;
      }
      else {
        lx /= rr;
        ly /= rr;
        lz /= rr;
      }
      double cosa = nfx * lx + nfy * ly + nfz * lz;

      //      if (cosa < 0.0) {
      //        cosa = 0.0 - cosa;
      //        nfx = 0.0 - nfx;
      //        nfy = 0.0 - nfy;
      //        nfz = 0.0 - nfz;
      //        reflectViewVector();
      //      }
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
  }

}
