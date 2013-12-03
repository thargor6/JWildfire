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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.FlameMovie;
import org.jwildfire.create.tina.animate.FlameMoviePart;

public class JWFMovieWriter {
  public void writeFlame(FlameMovie pMovie, String pFilename) throws Exception {
    Tools.writeUTF8Textfile(pFilename, getMovieXML(pMovie));
  }

  private static final int MOVIE_VERSION = 1;

  public static final String TAG_JWF_MOVIE = "jwf-movie";
  public static final String TAG_JWF_MOVIE_PART = "jwf-movie-part";

  public static final String ATTR_APPLICATION = "application";
  public static final String ATTR_COLOR_OVERSAMPLING = "color_oversampling";
  public static final String ATTR_FPS = "fps";
  public static final String ATTR_FRAME_COUNT = "frame_count";
  public static final String ATTR_FRAME_HEIGHT = "frame_height";
  public static final String ATTR_FRAME_MORPH_COUNT = "frame_morph_count";
  public static final String ATTR_FRAME_WIDTH = "frame_width";
  public static final String ATTR_MOVIE_VERSION = "movie_version";
  public static final String ATTR_OUTPUT_FORMAT = "output_format";
  public static final String ATTR_SCRIPT_GLOBAL = "script_global";
  public static final String ATTR_SCRIPT_XFORM = "script_xform";
  public static final String ATTR_SOUND_FILENAME = "sound_filename";
  public static final String ATTR_SPATIAL_OVERSAMPLING = "spatial_oversampling";
  public static final String ATTR_QUALITY = "quality";

  public String getMovieXML(FlameMovie pMovie) {
    SimpleXMLBuilder xb = new SimpleXMLBuilder();
    // Flame
    List<SimpleXMLBuilder.Attribute<?>> attrList = new ArrayList<SimpleXMLBuilder.Attribute<?>>();
    attrList.add(xb.createAttr(ATTR_APPLICATION, Tools.APP_TITLE + " " + Tools.APP_VERSION));
    attrList.add(xb.createAttr(ATTR_MOVIE_VERSION, MOVIE_VERSION));
    if (pMovie.getSoundFilename() != null && pMovie.getSoundFilename().length() > 0) {
      attrList.add(xb.createAttr(ATTR_SOUND_FILENAME, pMovie.getSoundFilename()));
    }
    attrList.add(xb.createAttr(ATTR_SCRIPT_GLOBAL, pMovie.getGlobalScript().toString()));
    attrList.add(xb.createAttr(ATTR_SCRIPT_XFORM, pMovie.getxFormScript().toString()));
    attrList.add(xb.createAttr(ATTR_FRAME_WIDTH, pMovie.getFrameWidth()));
    attrList.add(xb.createAttr(ATTR_FRAME_HEIGHT, pMovie.getFrameHeight()));
    attrList.add(xb.createAttr(ATTR_FPS, pMovie.getFramesPerSecond()));
    attrList.add(xb.createAttr(ATTR_OUTPUT_FORMAT, pMovie.getOutputFormat().toString()));
    attrList.add(xb.createAttr(ATTR_COLOR_OVERSAMPLING, pMovie.getColorOversampling()));
    attrList.add(xb.createAttr(ATTR_SPATIAL_OVERSAMPLING, pMovie.getSpatialOversampling()));
    attrList.add(xb.createAttr(ATTR_QUALITY, pMovie.getQuality()));
    xb.beginElement(TAG_JWF_MOVIE, attrList);
    for (FlameMoviePart part : pMovie.getParts()) {
      addPart(xb, part);
    }
    xb.endElement(TAG_JWF_MOVIE);
    return xb.buildXML();
  }

  private void addPart(SimpleXMLBuilder pXB, FlameMoviePart pPart) {
    List<SimpleXMLBuilder.Attribute<?>> attrList = new ArrayList<SimpleXMLBuilder.Attribute<?>>();
    attrList.add(pXB.createAttr(ATTR_FRAME_COUNT, pPart.getFrameCount()));
    attrList.add(pXB.createAttr(ATTR_FRAME_MORPH_COUNT, pPart.getFrameMorphCount()));
    pXB.beginElement(TAG_JWF_MOVIE_PART, attrList);
    if (pPart.getFlame() != null) {
      pXB.addContent(new FlameWriter().getFlameXML(pPart.getFlame()));
    }
    pXB.endElement(TAG_JWF_MOVIE_PART);
  }
}
