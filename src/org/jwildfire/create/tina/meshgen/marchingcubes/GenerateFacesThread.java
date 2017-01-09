/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.meshgen.marchingcubes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jwildfire.create.tina.render.ProgressUpdater;

public class GenerateFacesThread implements Runnable {
  private int zmin, zmax;
  private boolean done;
  private List<Point3f> faces = new ArrayList<Point3f>();
  private List<Point3f> normals = new ArrayList<Point3f>();
  private final ImageStackSampler sampler;
  private final int threshold;
  private boolean forceAbort;
  private boolean withNormals;
  private ProgressUpdater progressUpdater;
  private Set<Integer> progressOfAllThreads;

  public GenerateFacesThread(ImageStackSampler pSampler, int pThreshold) {
    sampler = pSampler;
    threshold = pThreshold;
  }

  public void setZmin(int pZmin) {
    zmin = pZmin;
  }

  public void setZmax(int pZmax) {
    zmax = pZmax;
  }

  @Override
  public void run() {
    done = false;
    try {
      forceAbort = false;
      Cube cube = new Cube();
      for (int z = zmin; z <= zmax + 1; z += 1) {
        for (int x = -1; x < sampler.getStackXSize() + 1; x += 1) {
          for (int y = -1; y < sampler.getStackYSize() + 1; y += 1) {
            cube.initCube(x, y, z);
            EdgesCalculator.computeEdges(cube, sampler, threshold);
            FacesCalculator.getFaces(cube, faces, withNormals ? normals : null, sampler, threshold);
          }
          if (forceAbort) {
            break;
          }
        }
        if (progressUpdater != null) {
          progressOfAllThreads.add(Integer.valueOf(z));
          progressUpdater.updateProgress(progressOfAllThreads.size());
        }
        if (forceAbort) {
          break;
        }
      }
    }
    finally {
      done = true;
    }
  }

  public boolean isDone() {
    return done;
  }

  public List<Point3f> getFaces() {
    return faces;
  }

  public void setProgressUpdater(ProgressUpdater pProgressUpdater, Set<Integer> pProgressOfAllThreads) {
    progressUpdater = pProgressUpdater;
    progressOfAllThreads = pProgressOfAllThreads;
  }

  public void setForceAbort() {
    forceAbort = true;
  }

  public void setWithNormals(boolean pWithNormals) {
    withNormals = pWithNormals;
  }

  public List<Point3f> getNormals() {
    return normals;
  }

}
