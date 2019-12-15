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

import static org.jwildfire.create.tina.io.AbstractFlameReader.ATTR_GRADIENT_MAP;
import static org.jwildfire.create.tina.io.AbstractFlameReader.ATTR_GRADIENT_MAP_HOFFSET;
import static org.jwildfire.create.tina.io.AbstractFlameReader.ATTR_GRADIENT_MAP_HSCALE;
import static org.jwildfire.create.tina.io.AbstractFlameReader.ATTR_GRADIENT_MAP_LCOLOR_ADD;
import static org.jwildfire.create.tina.io.AbstractFlameReader.ATTR_GRADIENT_MAP_LCOLOR_SCALE;
import static org.jwildfire.create.tina.io.AbstractFlameReader.ATTR_GRADIENT_MAP_VOFFSET;
import static org.jwildfire.create.tina.io.AbstractFlameReader.ATTR_GRADIENT_MAP_VSCALE;
import static org.jwildfire.create.tina.io.AbstractFlameReader.ATTR_SMOOTH_GRADIENT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.palette.RGBPalette;

public class JWFFlameWriter extends AbstractFlameWriter {

  public String getFlameXML(Flame pFlame) throws Exception {
    SimpleXMLBuilder xb = new SimpleXMLBuilder();
    // Flame
    List<SimpleXMLBuilder.Attribute<?>> attrList = createFlameAttributes(pFlame, xb);
    xb.beginElement("jwf-flame", attrList);
    for (Layer layer : pFlame.getLayers()) {
      List<SimpleXMLBuilder.Attribute<?>> layerAttrList = new ArrayList<SimpleXMLBuilder.Attribute<?>>();
      layerAttrList.add(xb.createAttr("weight", layer.getWeight()));
      layerAttrList.add(xb.createAttr("density", layer.getDensity()));
      layerAttrList.add(xb.createAttr("visible", layer.isVisible() ? 1 : 0));
      {
        String name = layer.getName().replaceAll("\"", "");
        if (!name.equals("")) {
          layerAttrList.add(xb.createAttr("name", name));
        }
      }
      {
        String gradientMapFilename = layer.getGradientMapFilename().replaceAll("\"", "");
        if (!gradientMapFilename.equals("")) {
          layerAttrList.add(xb.createAttr(ATTR_GRADIENT_MAP, gradientMapFilename));
          layerAttrList.add(xb.createAttr(ATTR_GRADIENT_MAP_HOFFSET, layer.getGradientMapHorizOffset()));
          layerAttrList.add(xb.createAttr(ATTR_GRADIENT_MAP_HSCALE, layer.getGradientMapHorizScale()));
          layerAttrList.add(xb.createAttr(ATTR_GRADIENT_MAP_VOFFSET, layer.getGradientMapVertOffset()));
          layerAttrList.add(xb.createAttr(ATTR_GRADIENT_MAP_VSCALE, layer.getGradientMapVertScale()));
          layerAttrList.add(xb.createAttr(ATTR_GRADIENT_MAP_LCOLOR_ADD, layer.getGradientMapLocalColorAdd()));
          layerAttrList.add(xb.createAttr(ATTR_GRADIENT_MAP_LCOLOR_SCALE, layer.getGradientMapLocalColorScale()));
        }
      }
      layerAttrList.add(xb.createAttr(ATTR_SMOOTH_GRADIENT, layer.isSmoothGradient() ? "1" : "0"));
      List<String> blackList = Collections.emptyList();
      writeMotionCurves(layer, xb, layerAttrList, null, blackList);

      RGBPalette palette = layer.getPalette();
      writeMotionCurves(palette, xb, layerAttrList, "palette_", Collections.<String> emptyList());


      xb.beginElement("layer", layerAttrList);

      // XForm
      for (XForm xForm : layer.getXForms()) {
        xb.emptyElement("xform", createXFormAttrList(xb, layer, xForm));
      }
      // FinalXForms
      for (XForm xForm : layer.getFinalXForms()) {
        xb.emptyElement("finalxform", createXFormAttrList(xb, layer, xForm));
      }
      // Palette
      {
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

      xb.endElement("layer");
    }
    xb.endElement("jwf-flame");
    return xb.buildXML();
  }

}
