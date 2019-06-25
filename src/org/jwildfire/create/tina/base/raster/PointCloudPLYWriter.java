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
package org.jwildfire.create.tina.base.raster;

import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.base.Unchecker;
import org.jwildfire.create.tina.base.raster.RasterPointCloud.PCPoint;

public class PointCloudPLYWriter {

  public void writePLY(List<PCPoint> pPoints, String pFilename) {
    StringBuilder sb = new StringBuilder();
    sb.append("ply\n" +
        "format ascii 1.0\n" +
        "comment " + Tools.APP_TITLE + " generated\n" +
        "element vertex " + pPoints.size() + "\n" +
        "property float x\n" +
        "property float y\n" +
        "property float z\n" +
        "property uchar red\n" +
        "property uchar green\n" +
        "property uchar blue\n" +
        "property uchar alpha\n" +
        "property uchar float intensity\n" +
        //"element face " + pPoints.size() + "\n" +
        "element face 0\n" +
        "property list uchar uint vertex_indices\n" +
        "end_header\n");
    for (PCPoint p : pPoints) {
      sb.append(p.x + " " + p.y + " " + p.z + " " + Tools.roundColor(p.r) + " " + Tools.roundColor(p.g) + " " + Tools.roundColor(p.b) + " " + Tools.roundColor(p.intensity * 255.0) + " " + p.intensity + "\n");
    }/*
    for (int i=0; i< pPoints.size(); i++) {
      sb.append("1 "+ i + "\n");
    } */
    try {
      Tools.writeUTF8Textfile(pFilename, sb.toString());
    }
    catch (Exception ex) {
      Unchecker.rethrow(ex);
    }
  }

}
