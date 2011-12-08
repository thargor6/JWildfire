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
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;

public class Flam3Writer {

  public void writeFlame(Flame pFlame, String pFilename) throws Exception {
    SimpleXMLBuilder xb = new SimpleXMLBuilder();
    // Flame
    xb.beginElement("flame",
        xb.createAttr("name", "JWildfire"),
        xb.createAttr("version", Tools.APP_VERSION),
        xb.createAttr("size", pFlame.getWidth() + " " + pFlame.getHeight()),
        xb.createAttr("center", pFlame.getCentreX() + " " + pFlame.getCentreY()),
        xb.createAttr("scale", pFlame.getPixelsPerUnit()),
        //        xb.createAttr("rotate", -(pFlame.getCamRoll() * Math.PI) / 180.0),
        xb.createAttr("rotate", pFlame.getCamRoll()),
        xb.createAttr("oversample", pFlame.getSpatialOversample()),
        xb.createAttr("color_oversample", pFlame.getColorOversample()),
        xb.createAttr("filter", pFlame.getSpatialFilterRadius()),
        xb.createAttr("quality", pFlame.getSampleDensity()),
        xb.createAttr("background", pFlame.getBGColorRed() + " " + pFlame.getBGColorGreen() + " " + pFlame.getBGColorBlue()),
        xb.createAttr("brightness", pFlame.getBrightness()),
        xb.createAttr("gamma", pFlame.getGamma()),
        xb.createAttr("gamma_threshold", pFlame.getGammaThreshold()),
        xb.createAttr("estimator_radius", 9),
        xb.createAttr("estimator_minimum", 0),
        xb.createAttr("estimator_curve", 0.4),
        xb.createAttr("temporal_samples", 1.0),
        xb.createAttr("cam_zoom", pFlame.getCamZoom()),
        xb.createAttr("cam_pitch", (pFlame.getCamPitch() * Math.PI) / 180.0),
        xb.createAttr("cam_yaw", (pFlame.getCamYaw() * Math.PI) / 180.0),
        xb.createAttr("cam_perspective", pFlame.getCamPerspective())
        );
    // XForm
    for (XForm xForm : pFlame.getXForms()) {
      xb.emptyElement("xform", createXFormAttrList(xb, pFlame, xForm));
    }

    if (pFlame.getFinalXForm() != null) {
      xb.emptyElement("finalxform", createXFormAttrList(xb, pFlame, pFlame.getFinalXForm()));
    }
    // Palette
    {
      RGBPalette palette = pFlame.getPalette();
      xb.beginElement("palette",
          xb.createAttr("count", palette.getSize()),
          xb.createAttr("format", "RGB"));
      StringBuilder rgb = new StringBuilder();
      for (int i = 0; i < palette.getSize(); i++) {
        String hs;
        hs = Integer.toHexString(palette.getColor(i).getRed()).toUpperCase();
        rgb.append(hs.length() > 1 ? hs : "0" + hs);
        hs = Integer.toHexString(palette.getColor(i).getGreen()).toUpperCase();
        rgb.append(hs.length() > 1 ? hs : "0" + hs);
        hs = Integer.toHexString(palette.getColor(i).getBlue()).toUpperCase();
        rgb.append(hs.length() > 1 ? hs : "0" + hs);
        if ((i + 1) % 12 == 0) {
          rgb.append("\n");
        }
      }
      xb.addContent(rgb.toString());
      xb.endElement("palette");
    }
    xb.endElement("flame");
    String xml = xb.buildXML();
    Tools.writeUTF8Textfile(pFilename, xml);
  }

  private List<SimpleXMLBuilder.Attribute<?>> createXFormAttrList(SimpleXMLBuilder pXB, Flame pFlame, XForm pXForm) {
    List<SimpleXMLBuilder.Attribute<?>> attrList = new ArrayList<SimpleXMLBuilder.Attribute<?>>();
    attrList.add(pXB.createAttr("weight", pXForm.getWeight()));
    attrList.add(pXB.createAttr("color", pXForm.getColor()));
    attrList.add(pXB.createAttr("symmetry", pXForm.getColorSymmetry()));
    attrList.add(pXB.createAttr("coefs", pXForm.getCoeff00() + " " + pXForm.getCoeff01() + " " + pXForm.getCoeff10() + " " + pXForm.getCoeff11() + " " + pXForm.getCoeff20() + " " + pXForm.getCoeff21()));
    if (pXForm.hasPostCoeffs()) {
      attrList.add(pXB.createAttr("post", pXForm.getPostCoeff00() + " " + pXForm.getPostCoeff01() + " " + pXForm.getPostCoeff10() + " " + pXForm.getPostCoeff11() + " " + pXForm.getPostCoeff20() + " " + pXForm.getPostCoeff21()));
    }
    {
      String hs = "";
      for (int i = 0; i < pFlame.getXForms().size() - 1; i++) {
        hs += pXForm.getModifiedWeights()[i] + " ";
      }
      hs += pXForm.getModifiedWeights()[pFlame.getXForms().size() - 1];
      attrList.add(pXB.createAttr("chaos", hs));
    }

    for (Variation v : pXForm.getVariations()) {
      VariationFunc func = v.getFunc();
      attrList.add(pXB.createAttr(func.getName(), v.getAmount()));
      String params[] = func.getParameterNames();
      if (params != null) {
        Object vals[] = func.getParameterValues();
        for (int i = 0; i < params.length; i++) {
          if (vals[i] instanceof Integer) {
            attrList.add(pXB.createAttr(func.getCustomizedParamName(func.getName() + "_" + params[i]), (Integer) vals[i]));
          }
          else if (vals[i] instanceof Double) {
            attrList.add(pXB.createAttr((func.getName() + "_" + params[i]), (Double) vals[i]));
          }
          else {
            throw new IllegalStateException();
          }
        }
      }
    }
    return attrList;
  }
}
