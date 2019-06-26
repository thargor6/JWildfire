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
    sb.append("ply\r\n" +
            "format ascii 1.0\r\n" +
            "comment " + Tools.APP_TITLE + " generated\r\n" +
            "element vertex " + pPoints.size() + "\r\n" +
            "property float x\r\n" +
            "property float y\r\n" +
            "property float z\r\n" +
            "property uchar red\r\n" +
            "property uchar green\r\n" +
            "property uchar blue\r\n" +
            "property uchar alpha\r\n" +
            "element face 0\r\n" +
            "property list uchar int vertex_indices\r\n" +
            "end_header\r\n");
    for (PCPoint p : pPoints) {
      sb.append(p.x + " " + p.y + " " + p.z + " " + Tools.roundColor(p.r) + " " + Tools.roundColor(p.g) + " " + Tools.roundColor(p.b) + " " + Tools.roundColor(p.intensity * 255.0) + "\r\n");
    }
    try {
      Tools.writeUTF8Textfile(pFilename, sb.toString());
    }
    catch (Exception ex) {
      Unchecker.rethrow(ex);
    }
  }

}
