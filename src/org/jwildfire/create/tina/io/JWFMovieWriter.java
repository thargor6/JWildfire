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
import org.jwildfire.create.tina.animate.GlobalScript;
import org.jwildfire.create.tina.animate.XFormScript;
import org.jwildfire.create.tina.io.SimpleXMLBuilder.Attribute;

public class JWFMovieWriter {
  public void writeFlame(FlameMovie pMovie, String pFilename) throws Exception {
    Tools.writeUTF8Textfile(pFilename, getMovieXML(pMovie));
  }

  private static final int MOVIE_VERSION = 1;

  public static final String TAG_JWF_MOVIE = "jwf-movie";
  public static final String TAG_JWF_MOVIE_PART = "jwf-movie-part";

  public static final String ATTR_NAME = "name";
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
  public static final String ATTR_SCRIPT_GLOBAL1 = "script_global1";
  public static final String ATTR_SCRIPT_GLOBAL1_AMPLITUDE = "script_global1_amplitude";
  public static final String ATTR_SCRIPT_GLOBAL1_AMPLITUDE_CURVE = "script_global1_amplitudeCurve";
  public static final String ATTR_SCRIPT_GLOBAL2 = "script_global2";
  public static final String ATTR_SCRIPT_GLOBAL2_AMPLITUDE = "script_global2_amplitude";
  public static final String ATTR_SCRIPT_GLOBAL2_AMPLITUDE_CURVE = "script_global2_amplitudeCurve";
  public static final String ATTR_SCRIPT_GLOBAL3 = "script_global3";
  public static final String ATTR_SCRIPT_GLOBAL3_AMPLITUDE = "script_global3_amplitude";
  public static final String ATTR_SCRIPT_GLOBAL3_AMPLITUDE_CURVE = "script_global3_amplitudeCurve";
  public static final String ATTR_SCRIPT_GLOBAL4 = "script_global4";
  public static final String ATTR_SCRIPT_GLOBAL4_AMPLITUDE = "script_global4_amplitude";
  public static final String ATTR_SCRIPT_GLOBAL4_AMPLITUDE_CURVE = "script_global4_amplitudeCurve";
  public static final String ATTR_SCRIPT_GLOBAL5 = "script_global5";
  public static final String ATTR_SCRIPT_GLOBAL5_AMPLITUDE = "script_global5_amplitude";
  public static final String ATTR_SCRIPT_GLOBAL5_AMPLITUDE_CURVE = "script_global5_amplitudeCurve";
  public static final String ATTR_SCRIPT_XFORM = "script_xform";
  public static final String ATTR_SCRIPT_XFORM1 = "script_xform1";
  public static final String ATTR_SCRIPT_XFORM1_AMPLITUDE = "script_xform1_amplitude";
  public static final String ATTR_SCRIPT_XFORM1_AMPLITUDE_CURVE = "script_xform1_amplitudeCurve";
  public static final String ATTR_SCRIPT_XFORM2 = "script_xform2";
  public static final String ATTR_SCRIPT_XFORM2_AMPLITUDE = "script_xform2_amplitude";
  public static final String ATTR_SCRIPT_XFORM2_AMPLITUDE_CURVE = "script_xform2_amplitudeCurve";
  public static final String ATTR_SCRIPT_XFORM3 = "script_xform3";
  public static final String ATTR_SCRIPT_XFORM3_AMPLITUDE = "script_xform3_amplitude";
  public static final String ATTR_SCRIPT_XFORM3_AMPLITUDE_CURVE = "script_xform3_amplitudeCurve";
  public static final String ATTR_SCRIPT_XFORM4 = "script_xform4";
  public static final String ATTR_SCRIPT_XFORM4_AMPLITUDE = "script_xform4_amplitude";
  public static final String ATTR_SCRIPT_XFORM4_AMPLITUDE_CURVE = "script_xform4_amplitudeCurve";
  public static final String ATTR_SCRIPT_XFORM5 = "script_xform5";
  public static final String ATTR_SCRIPT_XFORM5_AMPLITUDE = "script_xform5_amplitude";
  public static final String ATTR_SCRIPT_XFORM5_AMPLITUDE_CURVE = "script_xform5_amplitudeCurve";
  public static final String ATTR_SOUND_FILENAME = "sound_filename";
  public static final String ATTR_SPATIAL_OVERSAMPLING = "spatial_oversampling";
  public static final String ATTR_QUALITY = "quality";
  public static final String ATTR_MOTIONBLUR_LENGTH = "motion_blur_length";
  public static final String ATTR_MOTIONBLUR_TIMESTEP = "motion_blur_timestep";

  public String getMovieXML(FlameMovie pMovie) throws Exception {
    SimpleXMLBuilder xb = new SimpleXMLBuilder();
    // Flame
    List<SimpleXMLBuilder.Attribute<?>> attrList = new ArrayList<SimpleXMLBuilder.Attribute<?>>();
    attrList.add(xb.createAttr(ATTR_APPLICATION, Tools.APP_TITLE + " " + Tools.APP_VERSION));
    attrList.add(xb.createAttr(ATTR_MOVIE_VERSION, MOVIE_VERSION));
    if (pMovie.getSoundFilename() != null && pMovie.getSoundFilename().length() > 0) {
      attrList.add(xb.createAttr(ATTR_SOUND_FILENAME, pMovie.getSoundFilename()));
    }

    addGlobalScript(pMovie.getGlobalScript1(), ATTR_SCRIPT_GLOBAL1, ATTR_SCRIPT_GLOBAL1_AMPLITUDE, ATTR_SCRIPT_GLOBAL1_AMPLITUDE_CURVE, attrList, xb);
    addGlobalScript(pMovie.getGlobalScript2(), ATTR_SCRIPT_GLOBAL2, ATTR_SCRIPT_GLOBAL2_AMPLITUDE, ATTR_SCRIPT_GLOBAL2_AMPLITUDE_CURVE, attrList, xb);
    addGlobalScript(pMovie.getGlobalScript3(), ATTR_SCRIPT_GLOBAL3, ATTR_SCRIPT_GLOBAL3_AMPLITUDE, ATTR_SCRIPT_GLOBAL3_AMPLITUDE_CURVE, attrList, xb);
    addGlobalScript(pMovie.getGlobalScript4(), ATTR_SCRIPT_GLOBAL4, ATTR_SCRIPT_GLOBAL4_AMPLITUDE, ATTR_SCRIPT_GLOBAL4_AMPLITUDE_CURVE, attrList, xb);
    addGlobalScript(pMovie.getGlobalScript5(), ATTR_SCRIPT_GLOBAL5, ATTR_SCRIPT_GLOBAL5_AMPLITUDE, ATTR_SCRIPT_GLOBAL5_AMPLITUDE_CURVE, attrList, xb);

    addXFormScript(pMovie.getxFormScript1(), ATTR_SCRIPT_XFORM1, ATTR_SCRIPT_XFORM1_AMPLITUDE, ATTR_SCRIPT_XFORM1_AMPLITUDE_CURVE, attrList, xb);
    addXFormScript(pMovie.getxFormScript2(), ATTR_SCRIPT_XFORM2, ATTR_SCRIPT_XFORM2_AMPLITUDE, ATTR_SCRIPT_XFORM2_AMPLITUDE_CURVE, attrList, xb);
    addXFormScript(pMovie.getxFormScript3(), ATTR_SCRIPT_XFORM3, ATTR_SCRIPT_XFORM3_AMPLITUDE, ATTR_SCRIPT_XFORM3_AMPLITUDE_CURVE, attrList, xb);
    addXFormScript(pMovie.getxFormScript4(), ATTR_SCRIPT_XFORM4, ATTR_SCRIPT_XFORM4_AMPLITUDE, ATTR_SCRIPT_XFORM4_AMPLITUDE_CURVE, attrList, xb);
    addXFormScript(pMovie.getxFormScript5(), ATTR_SCRIPT_XFORM5, ATTR_SCRIPT_XFORM5_AMPLITUDE, ATTR_SCRIPT_XFORM5_AMPLITUDE_CURVE, attrList, xb);

    String xName = pMovie.getName().replaceAll("\"", "");
    if (!xName.equals("")) {
      attrList.add(xb.createAttr(ATTR_NAME, xName));
    }
    attrList.add(xb.createAttr(ATTR_FRAME_WIDTH, pMovie.getFrameWidth()));
    attrList.add(xb.createAttr(ATTR_FRAME_HEIGHT, pMovie.getFrameHeight()));
    attrList.add(xb.createAttr(ATTR_FPS, pMovie.getFramesPerSecond()));
    attrList.add(xb.createAttr(ATTR_OUTPUT_FORMAT, pMovie.getOutputFormat().toString()));
    attrList.add(xb.createAttr(ATTR_QUALITY, pMovie.getQuality()));

    attrList.add(xb.createAttr(AbstractFlameReader.ATTR_MOTIONBLUR_LENGTH, pMovie.getMotionBlurLength()));
    attrList.add(xb.createAttr(AbstractFlameReader.ATTR_MOTIONBLUR_TIMESTEP, pMovie.getMotionBlurTimeStep()));

    xb.beginElement(TAG_JWF_MOVIE, attrList);
    for (FlameMoviePart part : pMovie.getParts()) {
      addPart(xb, part);
    }
    xb.endElement(TAG_JWF_MOVIE);
    return xb.buildXML();
  }

  private void addGlobalScript(GlobalScript pScript, String pAttrScript, String pAttrScriptAmplitude, String pAttrScriptAmplitudeCurve, List<Attribute<?>> pAttrList, SimpleXMLBuilder pXB) {
    if (pScript != null) {
      pAttrList.add(pXB.createAttr(pAttrScript, pScript.getScriptType().toString()));
      pAttrList.add(pXB.createAttr(pAttrScriptAmplitude, pScript.getAmplitude()));
      if (!pScript.getAmplitudeCurve().isEmpty()) {
        AbstractFlameWriter.addMotionCurveAttributes(pXB, pAttrList, pAttrScript + Tools.CURVE_POSTFIX + "_", pScript.getAmplitudeCurve());
      }
    }
  }

  private void addXFormScript(XFormScript pScript, String pAttrScript, String pAttrScriptAmplitude, String pAttrScriptAmplitudeCurve, List<Attribute<?>> pAttrList, SimpleXMLBuilder pXB) {
    if (pScript != null) {
      pAttrList.add(pXB.createAttr(pAttrScript, pScript.getScriptType().toString()));
      pAttrList.add(pXB.createAttr(pAttrScriptAmplitude, pScript.getAmplitude()));
      if (!pScript.getAmplitudeCurve().isEmpty()) {
        AbstractFlameWriter.addMotionCurveAttributes(pXB, pAttrList, pAttrScript + Tools.CURVE_POSTFIX + "_", pScript.getAmplitudeCurve());
      }
    }
  }

  private void addPart(SimpleXMLBuilder pXB, FlameMoviePart pPart) throws Exception {
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
