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
package org.jwildfire.create.tina.render;

import java.util.HashMap;
import java.util.Map;

import org.jwildfire.base.Tools;

public class PhongIntArrayCache {
  private static Map<Integer, Map<Integer, PhongIntArray>> cache = new HashMap<>();

  public static PhongIntArray getPhongIntArray(double phong, double phongSize) {
    int iPhong = Tools.FTOI(phong * 100.0);
    int iPhongSize = Tools.FTOI(phong * 10.0);

    Map<Integer, PhongIntArray> phongMap = cache.get(iPhong);
    if (phongMap == null) {
      synchronized (cache) {
        phongMap = new HashMap<>();
        cache.put(iPhong, phongMap);
      }
    }

    PhongIntArray res = phongMap.get(iPhongSize);
    if (res == null) {
      res = new PhongIntArray(phong, phongSize);
      synchronized (cache) {
        phongMap.put(iPhongSize, res);
      }
    }
    return res;
  }

}
