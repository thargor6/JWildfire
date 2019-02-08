/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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

import org.jwildfire.base.mathlib.DoubleWrapperWF;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.base.mathlib.VecMathLib.Matrix3D;
import org.jwildfire.base.mathlib.VecMathLib.VectorD;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.sinAndCos;

public class DLA3DWFFuncIterator {
  private DoubleWrapperWF sinPhi = new DoubleWrapperWF();
  private DoubleWrapperWF cosPhi = new DoubleWrapperWF();
  private DoubleWrapperWF sinTheta = new DoubleWrapperWF();
  private DoubleWrapperWF cosTheta = new DoubleWrapperWF();

  private final int thread_count;
  private double innerRadius;
  private double outerRadius;

  private final AbstractRandomGenerator randGen;
  private final int max_iter;
  //  private final int seed;
  private final double glue_radius;
  private final double force_x;
  private final double force_y;
  private final double force_z;

  public DLA3DWFFuncIterator(int max_iter, int seed, double glue_radius, double force_x, double force_y, double force_z, boolean singleThread) {
    super();
    this.max_iter = max_iter;
    //    this.seed = seed;
    this.glue_radius = glue_radius;
    this.force_x = force_x;
    this.force_y = force_y;
    this.force_z = force_z;
    this.thread_count = singleThread ? 1 : Prefs.getPrefs().getTinaRenderThreads();
    randGen = new MarsagliaRandomGenerator();
    randGen.randomize(seed);
  }

  public List<DLA3DWFFuncPoint> iterate() {

    List<DLA3DWFFuncPoint> res = new ArrayList<>();
    DLA3DWFFuncPoint initialPoint = new DLA3DWFFuncPoint();
    res.add(initialPoint);

    innerRadius = 3.0;
    outerRadius = 3.0 * innerRadius;

    while (res.size() < max_iter) {
      double phi = randGen.random() * (M_PI + M_PI);
      sinAndCos(phi, sinPhi, cosPhi);
      double theta = randGen.random() * (M_PI + M_PI);
      sinAndCos(theta, sinTheta, cosTheta);

      double ci = innerRadius * sinTheta.value * cosPhi.value;
      double cj = innerRadius * sinTheta.value * sinPhi.value;
      double ck = innerRadius * cosTheta.value;

      if (thread_count > 1) {
        List<WalkerThread> threads = new ArrayList<>();
        for (int i = 0; i < 2 * thread_count; i++) {
          WalkerThread thread = new WalkerThread(ci, cj, ck, res);
          threads.add(thread);
          new Thread(thread).start();
        }
        boolean isDone = false;
        while (!isDone) {
          isDone = true;
          for (WalkerThread thread : threads) {
            if (!thread.isDone()) {
              isDone = false;
              try {
                Thread.sleep(1);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              break;
            }
          }
        }
        for (WalkerThread thread : threads) {
          DLA3DWFFuncPoint nextPoint = thread.getRes();
          if (nextPoint != null) {
            res.add(nextPoint);
            if (res.size() > 1000 && res.size() % 100 == 0) {
              System.out.println("PNTS " + res.size());
            }
          }
        }
      } else {
        DLA3DWFFuncPoint nextPoint = nextWalk(ci, cj, ck, res);
        if (nextPoint != null) {
          res.add(nextPoint);
          if (res.size() > 1000 && res.size() % 100 == 0) {
            System.out.println("PNTS " + res.size());
          }
        }
      }

    }

    for (DLA3DWFFuncPoint p : res) {
      p.relDepth = (double) p.pathLength / (double) calcMaxPathLength(res, p);

      if (p.parent != null) {
        VectorD d = new VectorD(p.x - p.parent.x, p.y - p.parent.y, p.z - p.parent.z);
        d.normalize();
        p.elevation = MathLib.atan2(d.y, d.x) + MathLib.M_PI_2;
        p.azimuth = -MathLib.atan2(d.z, MathLib.sqrt(d.x * d.x + d.y * d.y));

        double sina = MathLib.sin(p.elevation);
        double cosa = MathLib.cos(p.elevation);
        double sinb = MathLib.sin(p.azimuth);
        double cosb = MathLib.cos(p.azimuth);

        Matrix3D a = new Matrix3D();
        a.m[0][0] = cosb;
        a.m[0][1] = 0;
        a.m[0][2] = sinb;

        a.m[1][0] = sina * sinb;
        a.m[1][1] = cosa;
        a.m[1][2] = -sina * cosb;

        a.m[2][0] = -cosa * sinb;
        a.m[2][1] = sina;
        a.m[2][2] = cosa * cosb;

        p.rotation = a;
      }

    }

    return res;
  }

  private class WalkerThread implements Runnable {
    private boolean done;
    private DLA3DWFFuncPoint res;

    private final double ci0;
    private final double cj0;
    private final double ck0;
    private final List<DLA3DWFFuncPoint> points;

    public WalkerThread(double ci0, double cj0, double ck0, List<DLA3DWFFuncPoint> points) {
      super();
      this.ci0 = ci0;
      this.cj0 = cj0;
      this.ck0 = ck0;
      this.points = new ArrayList<>(points);
    }

    @Override
    public void run() {
      done = false;
      try {
        res = nextWalk(ci0, cj0, ck0, points);
      } finally {
        done = true;
      }
    }

    public boolean isDone() {
      return done;
    }

    public DLA3DWFFuncPoint getRes() {
      return res;
    }

  }

  private DLA3DWFFuncPoint nextWalk(double ci0, double cj0, double ck0, List<DLA3DWFFuncPoint> res) {
    int iter = 0;
    final int maxWalk = 1000000;
    double ci = ci0;
    double cj = cj0;
    double ck = ck0;
    while (iter < maxWalk) {
      double moveRadius = 0.9;
      //        phi = randGen.random() * (M_PI + M_PI);
      //        sinAndCos(phi, sinPhi, cosPhi);
      //        theta = randGen.random() * (M_PI + M_PI);
      //        sinAndCos(theta, sinTheta, cosTheta);
      //        final double moveRadius = 0.9;
      //        
      //        ci += moveRadius * sinTheta.value * cosPhi.value + gradx;
      //        cj += moveRadius * sinTheta.value * sinPhi.value + grady;
      //        ck += moveRadius * cosTheta.value + gradz;
      switch ((int) (randGen.random() * 6)) {
        case 0:
          ci = ci + moveRadius + force_x;
          break;
        case 1:
          cj = cj - moveRadius + force_y;
          break;
        case 2:
          ci = ci - moveRadius + force_x;
          break;
        case 3:
          ck = ck - moveRadius + force_z;
          break;
        case 4:
          ck = ck + moveRadius + force_z;
          break;
        case 5:
          cj = cj + moveRadius + force_y;
          break;
        default:
          break;
      }
      double radius = MathLib.sqrt(MathLib.sqr(ci) + MathLib.sqr(cj) + MathLib.sqr(ck));
      DLA3DWFFuncPoint parent = null;
      double minRadius = Double.MAX_VALUE;
      for (DLA3DWFFuncPoint r : res) {
        double dr0 = MathLib.sqrt(MathLib.sqr(r.x - ci) + MathLib.sqr(r.y - cj) + MathLib.sqr(r.z - ck));
        if (dr0 <= glue_radius) {
          if (dr0 < minRadius) {
            minRadius = dr0;
            parent = r;
          }
        }
      }

      if (parent != null) {
        DLA3DWFFuncPoint point = new DLA3DWFFuncPoint(parent, ci, cj, ck);
        if (radius > innerRadius) {
          innerRadius = radius;
          outerRadius = 2.1 * innerRadius;
        }
        return point;
      } else if (radius > outerRadius) {
        ci = ci0;
        cj = cj0;
        ck = ck0;
        break;
      }
      iter++;
    }
    return null;
  }

  private int calcMaxPathLength(List<DLA3DWFFuncPoint> points, DLA3DWFFuncPoint p) {
    int res = p.pathLength;

    for (DLA3DWFFuncPoint r : points) {
      DLA3DWFFuncPoint s = r;
      while (s != null) {
        if (s.parent == p) {
          if (r.pathLength > res) {
            res = r.pathLength;
          }
          break;
        }
        s = s.parent;
      }
    }

    return res;
  }

}
