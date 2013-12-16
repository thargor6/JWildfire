/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
package org.jwildfire.create.tina.dance.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.jwildfire.create.tina.base.Flame;

public class FlamePropertyPath implements Serializable {
  public final static String DELIMITER = ".";
  private static final long serialVersionUID = 1L;
  private Flame flame;
  private final String path;

  public FlamePropertyPath(Flame pFlame, List<String> pPath) {
    flame = pFlame;
    StringBuilder sb = new StringBuilder();
    sb.append(pPath.get(0));
    for (int i = 1; i < pPath.size(); i++) {
      sb.append(DELIMITER);
      sb.append(pPath.get(i));
    }
    path = sb.toString();
  }

  public FlamePropertyPath(Flame pFlame, String pPath) {
    flame = pFlame;
    path = pPath;
  }

  public Flame getFlame() {
    return flame;
  }

  public String getPath() {
    return path;
  }

  public List<String> getPathComponents() {
    return Arrays.asList(path.split("\\" + DELIMITER));
  }

  public void setFlame(Flame flame) {
    this.flame = flame;
  }

}
