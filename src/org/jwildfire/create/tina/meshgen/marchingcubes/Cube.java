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

public class Cube {
  protected final Point3f[] vertices;
  protected final InvalidatablePoint[] edges;

  public Cube() {
    vertices = new Point3f[8];
    for (int i = 0; i < 8; i++)
      vertices[i] = new Point3f();

    edges = new InvalidatablePoint[12];
    for (int i = 0; i < 12; i++)
      edges[i] = new InvalidatablePoint();
  }

  /**
   * initializes a Cube object
   *        _________           0______x
   *       /v0    v1/|         /|
   *      /________/ |        / | 
   *      |v3    v2| /v5    y/  |z 
   *      |________|/
   *       v7    v6
   */
  public void initCube(int x, int y, int z) {
    vertices[0].set(x, y, z);
    vertices[1].set(x + 1, y, z);
    vertices[2].set(x + 1, y + 1, z);
    vertices[3].set(x, y + 1, z);
    vertices[4].set(x, y, z + 1);
    vertices[5].set(x + 1, y, z + 1);
    vertices[6].set(x + 1, y + 1, z + 1);
    vertices[7].set(x, y + 1, z + 1);
  }

  public Point3f[] getVertices() {
    return vertices;
  }

  public InvalidatablePoint[] getEdges() {
    return edges;
  }

}
