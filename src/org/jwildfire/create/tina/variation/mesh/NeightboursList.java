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
package org.jwildfire.create.tina.variation.mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeightboursList {
  private final Map<Integer, List<Integer>> neighbours;

  public NeightboursList(SimpleMesh mesh) {
    neighbours = new HashMap<>();
    for (Face face : mesh.getFaces()) {
      addNeighbour(face.v1, face.v2);
      addNeighbour(face.v1, face.v3);
      addNeighbour(face.v2, face.v1);
      addNeighbour(face.v2, face.v3);
      addNeighbour(face.v3, face.v1);
      addNeighbour(face.v3, face.v2);
    }
  }

  private void addNeighbour(int fromVertex, int toVertex) {
    List<Integer> nList = neighbours.get(fromVertex);
    Integer toVertexObj = Integer.valueOf(toVertex);
    if (nList == null) {
      nList = new ArrayList<>();
      neighbours.put(fromVertex, nList);
      nList.add(toVertexObj);
    }
    else {
      if (nList.indexOf(toVertexObj) < 0) {
        nList.add(toVertexObj);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public List<Integer> getNeighbours(int fromVertex) {
    List<Integer> res = neighbours.get(fromVertex);
    return res != null ? res : Collections.EMPTY_LIST;
  }
}
