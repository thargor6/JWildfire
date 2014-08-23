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
package org.jwildfire.create.tina.meshgen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.meshgen.filter.PreFilter;
import org.jwildfire.create.tina.meshgen.marchingcubes.FacesMerger;
import org.jwildfire.create.tina.meshgen.marchingcubes.GenerateFacesThread;
import org.jwildfire.create.tina.meshgen.marchingcubes.ImageStackSampler;
import org.jwildfire.create.tina.meshgen.marchingcubes.Mesh;
import org.jwildfire.create.tina.meshgen.marchingcubes.MeshWriter;
import org.jwildfire.create.tina.meshgen.marchingcubes.Point;
import org.jwildfire.create.tina.render.ProgressUpdater;

public class GenerateMeshThread implements Runnable {
  private final String outFilename;
  private final String inputSequencePattern;
  private final int inputSequenceSize;
  private final int inputSequenceStep;
  private final int threshold;
  private final double spatialFilterRadius;
  private final int imageDownSample;
  private final boolean withNormals;
  private final MeshGenGenerateThreadFinishEvent finishEvent;
  private final ProgressUpdater progressUpdater;
  private boolean finished;
  private final List<GenerateFacesThread> threads = new ArrayList<GenerateFacesThread>();
  private final List<PreFilter> preFilterList;
  private boolean forceAbort;
  private Mesh mesh;

  public GenerateMeshThread(String pOutFilename, MeshGenGenerateThreadFinishEvent pFinishEvent, ProgressUpdater pProgressUpdater,
      String pInputSequencePattern, int pInputSequenceSize, int pInputSequenceStep, int pThreshold, double pSpatialFilterRadius, int pImageDownSample, boolean pWithNormals,
      List<PreFilter> pPreFilterList) {
    outFilename = pOutFilename;
    finishEvent = pFinishEvent;
    progressUpdater = pProgressUpdater;
    inputSequencePattern = pInputSequencePattern;
    inputSequenceSize = pInputSequenceSize;
    inputSequenceStep = pInputSequenceStep;
    threshold = pThreshold;
    spatialFilterRadius = pSpatialFilterRadius;
    imageDownSample = pImageDownSample;
    withNormals = pWithNormals;
    preFilterList = pPreFilterList;
  }

  @Override
  public void run() {
    mesh = null;
    System.gc();
    finished = false;
    try {
      forceAbort = false;
      long t0, t1;
      t0 = Calendar.getInstance().getTimeInMillis();
      int maxProgress = 0;
      if (progressUpdater != null) {
        maxProgress = (int) ((double) inputSequenceSize / (double) inputSequenceStep * 1.25 + 0.5);
        if (maxProgress == inputSequenceSize / inputSequenceStep) {
          maxProgress++;
        }
        progressUpdater.initProgress(maxProgress);
      }
      mesh = createMesh();

      if (progressUpdater != null) {
        progressUpdater.updateProgress(maxProgress);
      }
      t1 = Calendar.getInstance().getTimeInMillis();
      finished = true;
      finishEvent.succeeded((t1 - t0) * 0.001);
    }
    catch (Throwable ex) {
      finished = true;
      finishEvent.failed(ex);
    }
  }

  private Mesh createMesh() throws Exception {
    ImageStackSampler sampler = new ImageStackSampler(inputSequencePattern, inputSequenceSize, inputSequenceStep, spatialFilterRadius, imageDownSample, preFilterList);

    RawFaces rawFaces = createFaces(sampler, threshold);
    if (forceAbort) {
      return null;
    }

    Mesh mesh = FacesMerger.generateMesh(rawFaces);
    if (forceAbort) {
      return null;
    }

    MeshWriter.saveMesh(mesh, outFilename);
    if (forceAbort) {
      return null;
    }

    return mesh;
  }

  /**
   * Create a list of faces from the specified image data and the given isovalue.
   */
  public RawFaces createFaces(ImageStackSampler pSampler, int pSeekValue) {
    int threadCount = Prefs.getPrefs().getTinaRenderThreads();
    Set<Integer> totalProgress = new HashSet<Integer>();
    threads.clear();
    int zsize = pSampler.getStackZSize() + 1;
    if (zsize < threadCount) {
      threadCount = zsize;
    }

    int zmin = -1;
    int zPerThread = zsize / threadCount;
    for (int i = 0; i < threadCount; i++) {
      GenerateFacesThread thread = new GenerateFacesThread(pSampler, pSeekValue);
      threads.add(thread);
      thread.setZmin(zmin);
      thread.setWithNormals(withNormals);
      thread.setProgressUpdater(progressUpdater, totalProgress);
      int zmax = zmin + zPerThread - 1;
      if (zmax >= pSampler.getStackZSize()) {
        zmax = pSampler.getStackZSize() - 1;
      }
      thread.setZmin(zmin);
      thread.setZmax(zmax);
      zmin += zPerThread;
      new Thread(thread).start();
    }

    while (true) {
      boolean finished = true;
      for (int i = threads.size() - 1; i >= 0; i--) {
        GenerateFacesThread thread = threads.get(i);
        if (!thread.isDone()) {
          finished = false;
          break;
        }
      }
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (finished) {
        break;
      }
    }

    // Merge result
    List<Point> faces = new ArrayList<Point>();
    List<Point> normals;
    if (withNormals) {
      normals = new ArrayList<Point>();
      for (GenerateFacesThread thread : threads) {
        faces.addAll(thread.getFaces());
        normals.addAll(thread.getNormals());
      }
    }
    else {
      normals = null;
      for (GenerateFacesThread thread : threads) {
        faces.addAll(thread.getFaces());
      }
    }
    return new RawFaces(faces, normals);
  }

  public boolean isFinished() {
    return finished;
  }

  public void setForceAbort() {
    forceAbort = true;
    for (GenerateFacesThread thread : threads) {
      thread.setForceAbort();
    }
  }

  public Mesh getMesh() {
    return mesh;
  }

}
