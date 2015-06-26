/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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

/*
 * Implementation of the marching cubes algorithm based on the 
 * "Marching Cubes Tutorial Applet Copyright (C) 2002 - GERVAISE Raphael & RICHARD Karen"
 * http://users.polytech.unice.fr/~lingrand/MarchingCubes/applet.html
 */
package org.jwildfire.create.tina.meshgen.marchingcubes;

public class EdgesCalculator {

  /**
   * computes the interpolated point along a specified whose intensity equals the reference value
   * @param pVertex1 first extremity of the edge
   * @param pVertex2 second extremity of the edge
   * @param pTargetEdge stores the resulting edge, gets invalidated when the interpolated point is beyond edge boundaries
   */

  private static void computeEdge(Point3f pVertex1, int pIntensity1, Point3f pVertex2, int pIntensity2, ImageStackSampler pSampler, int pSeekValue, InvalidatablePoint pTargetEdge) {
    if (pIntensity2 < pIntensity1) {
      computeEdge(pVertex2, pIntensity2, pVertex1, pIntensity1, pSampler, pSeekValue, pTargetEdge);
    }
    float t = (pSeekValue - pIntensity1) / (float) (pIntensity2 - pIntensity1);
    if (t >= 0 && t <= 1) {
      pTargetEdge.x = (pVertex2.x - pVertex1.x) * t + pVertex1.x;
      pTargetEdge.y = (pVertex2.y - pVertex1.y) * t + pVertex1.y;
      pTargetEdge.z = (pVertex2.z - pVertex1.z) * t + pVertex1.z;
      pTargetEdge.invalid = false;
    }
    else {
      pTargetEdge.invalid = true;
    }
  }

  /**
   * computes interpolated values along each edge of the cube 
   * (invalidates the target if interpolated value doesn't belong to the edge)
   */
  public static void computeEdges(Cube pCube, ImageStackSampler pSampler, int pSeekValue) {
    Point3f[] vertices = pCube.getVertices();
    InvalidatablePoint[] edges = pCube.getEdges();

    int intensity0 = pSampler.getIntensity(vertices[0]);
    int intensity1 = pSampler.getIntensity(vertices[1]);
    int intensity2 = pSampler.getIntensity(vertices[2]);
    int intensity3 = pSampler.getIntensity(vertices[3]);
    int intensity4 = pSampler.getIntensity(vertices[4]);
    int intensity5 = pSampler.getIntensity(vertices[5]);
    int intensity6 = pSampler.getIntensity(vertices[6]);
    int intensity7 = pSampler.getIntensity(vertices[7]);

    computeEdge(vertices[0], intensity0, vertices[1], intensity1, pSampler, pSeekValue, edges[0]);
    computeEdge(vertices[1], intensity1, vertices[2], intensity2, pSampler, pSeekValue, edges[1]);
    computeEdge(vertices[2], intensity2, vertices[3], intensity3, pSampler, pSeekValue, edges[2]);
    computeEdge(vertices[3], intensity3, vertices[0], intensity0, pSampler, pSeekValue, edges[3]);

    computeEdge(vertices[4], intensity4, vertices[5], intensity5, pSampler, pSeekValue, edges[4]);
    computeEdge(vertices[5], intensity5, vertices[6], intensity6, pSampler, pSeekValue, edges[5]);
    computeEdge(vertices[6], intensity6, vertices[7], intensity7, pSampler, pSeekValue, edges[6]);
    computeEdge(vertices[7], intensity7, vertices[4], intensity4, pSampler, pSeekValue, edges[7]);

    computeEdge(vertices[0], intensity0, vertices[4], intensity4, pSampler, pSeekValue, edges[8]);
    computeEdge(vertices[1], intensity1, vertices[5], intensity5, pSampler, pSeekValue, edges[9]);
    computeEdge(vertices[3], intensity3, vertices[7], intensity7, pSampler, pSeekValue, edges[10]);
    computeEdge(vertices[2], intensity2, vertices[6], intensity6, pSampler, pSeekValue, edges[11]);
  }
}
