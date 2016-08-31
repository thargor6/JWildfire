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
package org.jwildfire.create.tina.io;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;

public class FlameWriter {

  public void writeFlame(Flame pFlame, String pFilename) throws Exception {
    Tools.writeUTF8Textfile(pFilename, getFlameXML(pFlame));
  }

  public String getFlameXML(Flame pFlame) throws Exception {
    if (pFlame.getLayers().size() <= 1) {
      return new Flam3Writer().getFlameXML(pFlame);
    }
    else {
      return new JWFFlameWriter().getFlameXML(pFlame);
    }
  }

}
