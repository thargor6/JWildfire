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
package org.jwildfire.create.tina;

import java.util.ArrayList;
import java.util.List;

public class OctreeNode {
  private final double xmin, xmax, xcentre;
  private final double ymin, ymax, ycentre;
  private final double zmin, zmax, zcentre;
  private final double minsize;
  private OctreeNode childs[];
  private final List<OctreeValue> values = new ArrayList<>();

  public OctreeNode(double pSize) {
    this(-pSize / 2.0, pSize / 2.0, -pSize / 2.0, pSize / 2.0, -pSize / 2.0, pSize / 2.0);
  }

  public OctreeNode(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax) {
    this.xmin = xmin;
    this.xmax = xmax;
    double dx = (xmax - xmin);
    this.xcentre = xmin + dx / 2.0;
    this.ymin = ymin;
    this.ymax = ymax;
    double dy = (ymax - ymin);
    this.ycentre = ymin + dy / 2.0;
    this.zmin = zmin;
    this.zmax = zmax;
    double dz = (zmax - zmin);
    this.zcentre = zmin + dz / 2.0;
    minsize = Math.min(Math.min(dx, dy), dz);
  }

  //
  //+Y                        +Z
  //|                         /
  //|                        /
  //|                       /
  //|       o---------------o---------------o
  //|      /               /               /|
  //|     /       5       /       6       / | 
  //|    /               /               /  | 
  //|   o---------------o---------------o   | 
  //|  /               /               /|   |
  //| /       4       /       7       / | 6 |
  //|/               /               /  |   o
  //o---------------o---------------o   |  /|
  //|               |               |   | / |
  //|               |               | 7 |/  |
  //|               |               |   o   |
  //|       4       |       6       |  /|   |
  //|               |               | / | 2 |
  //|               |               |/  |   o
  //o---------------o---------------o   |  /
  //|               |               |   | /
  //|               |               | 3 |/
  //|               |               |   o
  //|       0       |       3       |  / 
  //|               |               | /  
  //|               |               |/   
  //o---------------o---------------o -----------------+X
  //
  public void subdivide() {
    double dxh = (xmax - xmin) / 2.0;
    double dyh = (ymax - ymin) / 2.0;
    double dzh = (zmax - zmin) / 2.0;
    childs = new OctreeNode[8];
    childs[0] = new OctreeNode(xmin, xmin + dxh, ymin, ymin + dyh, zmin, zmin + dzh);
    childs[1] = new OctreeNode(xmin, xmin + dxh, ymin, ymin + dyh, zmax - dzh, zmax);
    childs[2] = new OctreeNode(xmax - dxh, xmax, ymin, ymin + dyh, zmax - dzh, zmax);
    childs[3] = new OctreeNode(xmax - dxh, xmax, ymin, ymin + dyh, zmin, zmin + dzh);

    childs[4] = new OctreeNode(xmin, xmin + dxh, ymax - dyh, ymax, zmin, zmin + dzh);
    childs[5] = new OctreeNode(xmin, xmin + dxh, ymax - dyh, ymax, zmax - dzh, zmax);
    childs[6] = new OctreeNode(xmax - dxh, xmax, ymax - dyh, ymax, zmax - dzh, zmax);
    childs[7] = new OctreeNode(xmax - dxh, xmax, ymax - dyh, ymax, zmin, zmin + dzh);
  }

  public OctreeNode addValue(double x, double y, double z, Object value, double maxcellsize) {
    if (x < xmin || x > xmax || y < ymin || y > ymax || z < zmin || z > zmax) {
      return null;
    }

    OctreeNode node = this;
    while (node.minsize > maxcellsize) {
      if (node.childs == null) {
        node.subdivide();
      }
      node = node.getEnclosingNode(x, y, z);
      if (node == null) {
        throw new RuntimeException("Error adding object: (x=" + x + ", y=" + y + ", z=" + z + "), range: (x=" + xmin + " ... " + xmax + ") (y=" + ymin + " ... " + ymax + ") (z=" + zmin + " ... " + zmax + ")");
      }
    }

    //    System.out.println("Added: (x=" + x + ", y=" + y + ", z=" + z + "), range: (x=" + node.xmin + " ... " + node.xmax + ") (y=" + node.ymin + " ... " + node.ymax + ") (z=" + node.zmin + " ... " + node.zmax + ")");

    node.values.add(new OctreeValue(x, y, z, value));

    return node;
  }

  public List<OctreeNode> getEnclosedNodes(double centreX, double centreY, double centreZ, double cellsize, int maxCount) {
    double c2 = cellsize / 2.0;
    double cellXMin = centreX - c2;
    double cellXMax = centreX + c2;
    double cellYMin = centreY - c2;
    double cellYMax = centreY + c2;
    double cellZMin = centreZ - c2;
    double cellZMax = centreZ + c2;

    List<OctreeNode> res = new ArrayList<>();
    addEnclosedNodesToList(this, res, cellXMin, cellXMax, cellYMin, cellYMax, cellZMin, cellZMax, maxCount);

    return res;
  }

  private void addEnclosedNodesToList(OctreeNode node, List<OctreeNode> list, double cellXMin, double cellXMax, double cellYMin, double cellYMax, double cellZMin, double cellZMax, int maxCount) {
    if (node.childs != null) {
      for (OctreeNode child : node.childs) {
        if (maxCount > 0 && list.size() >= maxCount) {
          break;
        }
        if (child.xmin >= cellXMin && child.xmax <= cellXMax && child.ymin >= cellYMin && child.ymax <= cellYMax && child.zmin >= cellZMin && child.zmax <= cellZMax) {
          list.add(child);
        }
        addEnclosedNodesToList(child, list, cellXMin, cellXMax, cellYMin, cellYMax, cellZMin, cellZMax, maxCount);
      }
    }
  }

  private OctreeNode getEnclosingNode(double x, double y, double z) {
    int startIdx, endIdx;
    if (y > ycentre) {
      startIdx = 4;
      endIdx = 7;
    }
    else {
      startIdx = 0;
      endIdx = 3;
    }
    for (int i = startIdx; i <= endIdx; i++) {
      if (x >= childs[i].xmin && x <= childs[i].xmax && y >= childs[i].ymin && y <= childs[i].ymax && z >= childs[i].zmin && z <= childs[i].zmax) {
        return childs[i];
      }
    }
    return null;
  }

  public List<OctreeValue> getValues() {
    return values;
  }

  public void accept(OctreeNodeVisitor pVisitor) {
    pVisitor.visit(this);
    if (childs != null) {
      for (OctreeNode child : childs) {
        child.accept(pVisitor);
      }
    }
  }

}
