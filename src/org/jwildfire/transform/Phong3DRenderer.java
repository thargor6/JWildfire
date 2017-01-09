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

import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.Mesh3DTransformer.Faces;

public class Phong3DRenderer extends Mesh3DRenderer {
  private static final int NMAX = 256;

  // TODO : Faces.MIRRORED currently not supported

  @Override
  public void renderImage(Mesh3D pMesh3D, Mesh3DTransformer pMesh3DTransformer, SimpleImage pImg) {
    init(pMesh3D, pMesh3DTransformer, pImg);
    double nx[] = new double[f3Count];
    double ny[] = new double[f3Count];
    double nz[] = new double[f3Count];
    double perpx[] = new double[f3Count];
    double perpy[] = new double[f3Count];
    double perpz[] = new double[f3Count];
    double baseDelta[] = new double[f3Count];
    int taxis[] = new int[f3Count];
    int nb[] = new int[f3Count];
    int pf[] = new int[f3Count];
    double nbx1[] = new double[f3Count];
    double nby1[] = new double[f3Count];
    double nbz1[] = new double[f3Count];
    double nbx2[] = new double[f3Count];
    double nby2[] = new double[f3Count];
    double nbz2[] = new double[f3Count];
    double nbx3[] = new double[f3Count];
    double nby3[] = new double[f3Count];
    double nbz3[] = new double[f3Count];
    double pmaxangle = Math.cos(pMesh3DTransformer.getPhongAngle() * Math.PI / 180.0);
    /* calc the normals and baselines */
    {
      double vcx, vcy, vcz, tx, ty, tz;
      double n1x, n1y, n1z, rr1, n2x, n2y, n2z, rr2, proj, udom;
      for (int i = 0; i < f3Count; i++) {
        int p1 = p31[i];
        int p2 = p32[i];
        int p3 = p33[i];
        double x1 = cx + x[p1];
        double y1 = cy + y[p1];
        double z1 = z[p1];
        double x2 = cx + x[p2];
        double y2 = cy + y[p2];
        double z2 = z[p2];
        double x3 = cx + x[p3];
        double y3 = cy + y[p3];
        double z3 = z[p3];

        /* normals */
        double vax = x1 - x2;
        double vay = y1 - y2;
        double vaz = z1 - z2;
        double vbx = x3 - x2;
        double vby = y3 - y2;
        double vbz = z3 - z2;
        vcx = x1 - x3;
        vcy = y1 - y3;
        vcz = z1 - z3;

        n1x = vay * vbz - vaz * vby;
        n1y = vaz * vbx - vax * vbz;
        n1z = vax * vby - vay * vbx;
        rr1 = n1x * n1x + n1y * n1y + n1z * n1z;
        n2x = vby * vcz - vbz * vcy;
        n2y = vbz * vcx - vbx * vcz;
        n2z = vbx * vcy - vby * vcx;
        rr2 = n2x * n2x + n2y * n2y + n2z * n2z;

        if (rr1 > rr2) {
          rr1 = Math.sqrt(rr1);
          if (rr1 < PZERO)
            rr1 = PZERO;
          nx[i] = n1x / rr1;
          ny[i] = n1y / rr1;
          nz[i] = n1z / rr1;
        }
        else {
          rr2 = Math.sqrt(rr2);
          if (rr2 < PZERO)
            rr2 = PZERO;
          nx[i] = 0.0 - n2x / rr2;
          ny[i] = 0.0 - n2y / rr2;
          nz[i] = 0.0 - n2z / rr2;
        }

        /* default axis */
        tx = Math.abs(vbx);
        ty = Math.abs(vby);
        tz = Math.abs(vbz);
        switch (max3(tx, ty, tz)) {
          case 1:
            taxis[i] = 1;
            baseDelta[i] = vbx;
            break;
          case 2:
            taxis[i] = 2;
            baseDelta[i] = vby;
            break;
          default:
            taxis[i] = 3;
            baseDelta[i] = vbz;
        }

        /* vperp */
        vax = x2 - x3;
        vay = y2 - y3;
        vaz = z2 - z3;
        rr1 = Math.sqrt(vax * vax + vay * vay + vaz * vaz);
        if (rr1 < PZERO)
          rr1 = 1.0;
        vax /= rr1;
        vay /= rr1;
        vaz /= rr1;

        vbx = x1 - x3;
        vby = y1 - y3;
        vbz = z1 - z3;
        proj = vbx * vax + vby * vay + vbz * vaz;

        vax *= proj;
        vay *= proj;
        vaz *= proj;

        vcx = vax - vbx;
        vcy = vay - vby;
        vcz = vaz - vbz;
        rr1 = Math.sqrt(vcx * vcx + vcy * vcy + vcz * vcz);
        if (rr1 < PZERO)
          rr1 = 1.0;
        vcx /= rr1;
        vcy /= rr1;
        vcz /= rr1;

        udom = vbx * vcx + vby * vcy + vbz * vcz;
        if (udom < PZERO)
          udom = 1;
        udom = 0.0 - udom;
        perpx[i] = vcx / udom;
        perpy[i] = vcy / udom;
        perpz[i] = vcz / udom;
      }
    }

    /* search the valid point-range */
    /*   if((obj->f4Count==0) && (obj->f2Count==0) && (obj->f1Count==0) && (obj->sf3Count==0)) {*/
    int pMin = 0;
    int pMax = pMesh3D.getPCount();
    /*   }
       else {
        ULONG pp1,i;
        pMin=pCount-1;pMax=0;
        for(i=0;i<f3Count;i++) {
         pp1=p31[i];
         if(pp1<pMin) pMin=pp1;
         if(pp1>pMax) pMax=pp1;
         pp1=p32[i];
         if(pp1<pMin) pMin=pp1;
         if(pp1>pMax) pMax=pp1;
         pp1=p33[i];
         if(pp1<pMin) pMin=pp1;
         if(pp1>pMax) pMax=pp1;
        }
        pMax++;
       }
    */
    double phongAngle = pMesh3DTransformer.getPhongAngle();
    if ((phongAngle <= 0.0) || (phongAngle > 180.0)) {
      for (int i = pMin; i < pMax; i++) {
        int pcurr = 0;
        for (int bs = 0; bs < f3Count; bs++) {
          int pp1 = p31[bs];
          int pp2 = p32[bs];
          int pp3 = p33[bs];
          if (i == pp1) {
            pf[pcurr] = 1;
            nb[pcurr] = bs;
            pcurr++;
          }
          if (i == pp2) {
            pf[pcurr] = 2;
            nb[pcurr] = bs;
            pcurr++;
          }
          if (i == pp3) {
            pf[pcurr] = 3;
            nb[pcurr] = bs;
            pcurr++;
          }
          if (pcurr >= NMAX)
            break;
        }
        if (pcurr > 0) {
          double tnx = 0.0, tny = 0.0, tnz = 0.0;
          for (int bs = 0; bs < pcurr; bs++) {
            int nn = nb[bs];
            tnx += nx[nn];
            tny += ny[nn];
            tnz += nz[nn];
          }
          rr = Math.sqrt(tnx * tnx + tny * tny + tnz * tnz);
          if (rr < PZERO) {/*printf("bad normal vector\n");*/
            rr = 1.0;
          }
          tnx /= rr;
          tny /= rr;
          tnz /= rr;
          for (int bs = 0; bs < pcurr; bs++) {
            int nn = nb[bs];
            switch (pf[bs]) {
              case 1:
                nbx1[nn] = tnx;
                nby1[nn] = tny;
                nbz1[nn] = tnz;
                break;
              case 2:
                nbx2[nn] = tnx;
                nby2[nn] = tny;
                nbz2[nn] = tnz;
                break;
              default:
                nbx3[nn] = tnx;
                nby3[nn] = tny;
                nbz3[nn] = tnz;
            }
          }
        }
      }
    }
    else {
      for (int i = pMin; i < pMax; i++) {
        //        System.out.println(i + "/" + pMax);
        int pcurr = 0;
        for (int bs = 0; bs < f3Count; bs++) {
          int pp1 = p31[bs];
          int pp2 = p32[bs];
          int pp3 = p33[bs];
          if (i == pp1) {
            pf[pcurr] = 1;
            nb[pcurr] = bs;
            pcurr++;
          }
          if (i == pp2) {
            pf[pcurr] = 2;
            nb[pcurr] = bs;
            pcurr++;
          }
          if (i == pp3) {
            pf[pcurr] = 3;
            nb[pcurr] = bs;
            pcurr++;
          }
          if (pcurr >= NMAX)
            break;
        }
        if (pcurr > 0) {
          for (int bs = 0; bs < pcurr; bs++) {
            int nn = nb[bs];
            double lnx = nx[nn];
            double lny = ny[nn];
            double lnz = nz[nn];
            double tnx = 0.0, tny = 0.0, tnz = 0.0;
            for (int o = 0; o < pcurr; o++) {
              int mm = nb[o];
              double lmx = nx[mm];
              double lmy = ny[mm];
              double lmz = nz[mm];
              if (o != bs) {
                double pangle = lnx * lmx + lny * lmy + lnz * lmz;
                if (pangle > pmaxangle) {
                  tnx += lmx;
                  tny += lmy;
                  tnz += lmz;
                }
              }
              else {
                tnx += lmx;
                tny += lmy;
                tnz += lmz;
              }
            }
            rr = Math.sqrt(tnx * tnx + tny * tny + tnz * tnz);
            if (rr < PZERO) {
              tnx = lnx;
              tny = lny;
              tnz = lnz;
            }
            else {
              tnx /= rr;
              tny /= rr;
              tnz /= rr;
            }
            switch (pf[bs]) {
              case 1:
                nbx1[nn] = tnx;
                nby1[nn] = tny;
                nbz1[nn] = tnz;
                break;
              case 2:
                nbx2[nn] = tnx;
                nby2[nn] = tny;
                nbz2[nn] = tnz;
                break;
              default:
                nbx3[nn] = tnx;
                nby3[nn] = tny;
                nbz3[nn] = tnz;
            }
          }
        }
      }
    }

    /* render it */
    for (int i = 0; i < f3Count; i++) {
      int p1 = p31[i];
      int p2 = p32[i];
      int p3 = p33[i];
      fZSortArray[i] = 0.0 - (z[p1] + z[p2] + z[p3]) * 0.33;
      fInd[i] = i;
    }
    heapSortFloat1(fZSortArray, fInd, f3Count);
    for (int i = 0; i < f3Count; i++) {
      int p1 = p31[fInd[i]];
      int p2 = p32[fInd[i]];
      int p3 = p33[fInd[i]];
      double x1 = cx + x[p1];
      double y1 = cy + y[p1];
      double z1 = z[p1];
      double x2 = cx + x[p2];
      double y2 = cy + y[p2];
      double z2 = z[p2];
      double x3 = cx + x[p3];
      double y3 = cy + y[p3];
      double z3 = z[p3];
      lux = (int) (x1 + 0.5);
      luy = (int) (y1 + 0.5);
      luz = (int) (z1 + 0.5);
      rux = (int) (x2 + 0.5);
      ruy = (int) (y2 + 0.5);
      ruz = (int) (z2 + 0.5);
      rbx = (int) (x3 + 0.5);
      rby = (int) (y3 + 0.5);
      rbz = (int) (z3 + 0.5);

      cosa = 0.0 - nz[fInd[i]];
      int swapCosA = 0;

      if ((faces == Faces.DOUBLE) && (cosa >= 0.0)) {
        cosa = 0.0 - cosa;
        swapCosA = 1;
      }

      if (cosa < 0.0) {
        if (u != null) {
          int px = (int) (u[p1] * texture.getImageWidth() + 0.5);
          int py = (int) (v[p1] * texture.getImageHeight() + 0.5);
          toolPixel.setARGBValue(texture.getARGBValueIgnoreBounds(px, py));
        }
        else {
          toolPixel.setARGBValue(coloro[fInd[i]]);
        }
        r = toolPixel.r;
        g = toolPixel.g;
        b = toolPixel.b;
        nfx = nx[fInd[i]];
        nfy = ny[fInd[i]];
        nfz = nz[fInd[i]];
        double fperpx = perpx[fInd[i]];
        double fperpy = perpy[fInd[i]];
        double fperpz = perpz[fInd[i]];
        double n1x = nbx1[fInd[i]];
        double n1y = nby1[fInd[i]];
        double n1z = nbz1[fInd[i]];
        double n2x = nbx2[fInd[i]];
        double n2y = nby2[fInd[i]];
        double n2z = nbz2[fInd[i]];
        double n3x = nbx3[fInd[i]];
        double n3y = nby3[fInd[i]];
        double n3z = nbz3[fInd[i]];

        /* fill the transformed triangle */
        /* create the edges */
        /* 1->2 */
        if (p1 < p2) {
          n12 = bresenham3D(lux, luy, luz, rux, ruy, ruz, x12, y12, z12);
        }
        else {
          n12 = bresenham3D(rux, ruy, ruz, lux, luy, luz, x12, y12, z12);
        }
        /* 2->3 */
        if (p2 < p3) {
          n23 = bresenham3D(rux, ruy, ruz, rbx, rby, rbz, x23, y23, z23);
        }
        else {
          n23 = bresenham3D(rbx, rby, rbz, rux, ruy, ruz, x23, y23, z23);
        }
        /* 3->1 */
        if (p3 < p1) {
          n41 = bresenham3D(rbx, rby, rbz, lux, luy, luz, x41, y41, z41);
        }
        else {
          n41 = bresenham3D(lux, luy, luz, rbx, rby, rbz, x41, y41, z41);
        }
        /* create the bounding box */
        int xmin, xmax;
        xmin = xmax = lux;
        if (rux < xmin)
          xmin = rux;
        else if (rux > xmax)
          xmax = rux;
        if (rbx < xmin)
          xmin = rbx;
        else if (rbx > xmax)
          xmax = rbx;
        int ymin, ymax;
        ymin = ymax = luy;
        if (ruy < ymin)
          ymin = ruy;
        else if (ruy > ymax)
          ymax = ruy;
        if (rby < ymin)
          ymin = rby;
        else if (rby > ymax)
          ymax = rby;

        /* fill the area */
        for (k = ymin; k <= ymax; k++) {
          if ((k >= 0) && (k < height)) {
            rFill3();
            int zz = zMin;
            double zs;
            if (min != max)
              zs = (double) (zMax - zMin) / (double) (max - min);
            else
              zs = 0.0;
            for (int l = min; l <= max; l++) {
              if ((l >= 0) && (l < width)) {
                /* compute the color */
                double xx = (double) l;
                double yy = (double) k;

                double vpx = xx - x1;
                double vpy = yy - y1;
                double vpz = zz - z1;
                double vpx2 = x3 - x1;
                double vpy2 = y3 - y1;
                double vpz2 = z3 - z1;
                double u = vpx * fperpx + vpy * fperpy + vpz * fperpz;
                double u2 = vpx2 * fperpx + vpy2 * fperpy + vpz2 * fperpz;
                if (Math.abs(u2) < PZERO)
                  u2 = 1.0;
                u /= u2;
                double v;
                switch (taxis[fInd[i]]) {
                  case 1:
                    v = (vpx / u - (x2 - x1)) / (baseDelta[fInd[i]]);
                    break;
                  case 2:
                    v = (vpy / u - (y2 - y1)) / (baseDelta[fInd[i]]);
                    break;
                  default:
                    v = (vpz / u - (z2 - z1)) / (baseDelta[fInd[i]]);
                }

                double ntempx1 = u * (n2x - n1x) + n1x;
                double ntempy1 = u * (n2y - n1y) + n1y;
                double ntempz1 = u * (n2z - n1z) + n1z;

                double ntempx2 = u * (n3x - n1x) + n1x;
                double ntempy2 = u * (n3y - n1y) + n1y;
                double ntempz2 = u * (n3z - n1z) + n1z;

                nfx = v * (ntempx2 - ntempx1) + ntempx1;
                nfy = v * (ntempy2 - ntempy1) + ntempy1;
                nfz = v * (ntempz2 - ntempz1) + ntempz1;

                rr = Math.sqrt(nfx * nfx + nfy * nfy + nfz * nfz);
                if (rr < PZERO)
                  rr = 1.0;
                nfx /= rr;
                nfy /= rr;
                nfz /= rr;
                if (swapCosA != 0) {
                  nfx = 0.0 - nfx;
                  nfy = 0.0 - nfy;
                  nfz = 0.0 - nfz;
                }
                reflectViewVector();
                addLight(xx, yy, zz);
                pImg.setRGB(l, k, pr, pg, pb);
              }
              zz += zs;
            }
          }
        } /* k loop */
      } /* cosa */
    } /* loop */
  }

  private int max3(double x, double y, double z) {
    return ((x > y) ? ((x > z) ? 1 : 3) : ((y > z) ? 2 : 3));
  }

}
