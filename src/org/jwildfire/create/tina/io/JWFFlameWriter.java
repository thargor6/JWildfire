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

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.palette.RGBPalette;

public class JWFFlameWriter extends AbstractFlameWriter {

  public String getFlameXML(Flame pFlame) {
    SimpleXMLBuilder xb = new SimpleXMLBuilder();
    // Flame
    List<SimpleXMLBuilder.Attribute<?>> attrList = createFlameAttributes(pFlame, xb);

    boolean enhancedFlame = pFlame.getLayers().size() > 1;
    xb.beginElement("jwf-flame", attrList);
    for (Layer layer : pFlame.getLayers()) {
      List<SimpleXMLBuilder.Attribute<?>> layerAttrList = new ArrayList<SimpleXMLBuilder.Attribute<?>>();
      layerAttrList.add(xb.createAttr("weight", layer.getWeight()));
      layerAttrList.add(xb.createAttr("visible", layer.isVisible() ? 1 : 0));
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
        RGBPalette palette = layer.getPalette();
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
