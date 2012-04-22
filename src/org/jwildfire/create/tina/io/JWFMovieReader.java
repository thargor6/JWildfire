/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
import org.jwildfire.create.tina.animate.FlameMovie;

public class JWFMovieReader {

  public FlameMovie readMovie(String pFilename) {
    try {
      String movieXML = Tools.readUTF8Textfile(pFilename);
      return readMovieFromXML(movieXML);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public FlameMovie readMovieFromXML(String pXML) {
    String movieXML;
    {
      int ps = pXML.indexOf("<" + JWFMovieWriter.TAG_JWF_MOVIE + " ");
      if (ps < 0)
        return null;
      int pe = pXML.indexOf("</" + JWFMovieWriter.TAG_JWF_MOVIE + ">", ps + 1);
      if (pe < 0)
        return null;
      movieXML = pXML.substring(ps, pe);
    }
    FlameMovie res = new FlameMovie();

    return res;
  }
}
