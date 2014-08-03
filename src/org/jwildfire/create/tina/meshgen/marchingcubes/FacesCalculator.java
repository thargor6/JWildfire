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

import java.util.List;

public class FacesCalculator {

  public static void getFaces(Cube pCube, List<Point> list, ImageStackSampler pSampler, int pSeekValue) {
    int cn = caseNumber(pCube, pSampler, pSeekValue);
    boolean directTable = !(isAmbigous(cn));
    // address in the table
    int offset = directTable ? cn * 15 : (255 - cn) * 15;
    InvalidatablePoint[] edges = pCube.getEdges();

    for (int index = 0; index < 5; index++) {
      // if there's a triangle
      if (PredefinedCases.faces[offset] != -1) {
        // pick up vertexes of the current triangle
        InvalidatablePoint edge1 = edges[PredefinedCases.faces[offset + 0]];
        InvalidatablePoint edge2 = edges[PredefinedCases.faces[offset + 1]];
        InvalidatablePoint edge3 = edges[PredefinedCases.faces[offset + 2]];

        if (edge1.invalid || edge2.invalid || edge3.invalid) {

        }
        else {
          list.add(new Point(edge1));
          list.add(new Point(edge2));
          list.add(new Point(edge3));
        }
      }
      offset += 3;
    }
  }

  /**
   * indicates if a number corresponds to an ambigous case
   * @param n number of the case to test
   * @return true if the case if ambigous
   */
  private static boolean isAmbigous(int n) {
    boolean result = false;
    for (int index = 0; index < PredefinedCases.ambigous.length; index++) {
      result |= PredefinedCases.ambigous[index] == n;
    }
    return result;
  }

  /**
   * computes the case number of the cube
   * @return the number of the case corresponding to the cube
   */
  private static int caseNumber(Cube pCube, ImageStackSampler pSampler, int pSeekValue) {
    int caseNumber = 0;
    for (int index = -1; ++index < pCube.getVertices().length; caseNumber += (pSampler.getIntensity(pCube.getVertices()[index]) - pSeekValue > 0) ? 1 << index : 0)
      ;
    return caseNumber;
  }

}
