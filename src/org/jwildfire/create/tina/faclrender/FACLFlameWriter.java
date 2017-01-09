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
package org.jwildfire.create.tina.faclrender;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.AbstractFlameWriter;
import org.jwildfire.create.tina.io.SimpleXMLBuilder;
import org.jwildfire.create.tina.io.SimpleXMLBuilder.Attribute;
import org.jwildfire.create.tina.palette.RGBPalette;

public class FACLFlameWriter extends AbstractFlameWriter {

  public void writeFlame(Flame pFlame, String pFilename) throws Exception {
    Tools.writeUTF8Textfile(pFilename, getFlameXML(translateFlame(pFlame)));
  }

  private Flame translateFlame(Flame pFlame) {
    Flame flame = pFlame.makeCopy();
    flame.setPixelsPerUnit(flame.getPixelsPerUnit() * flame.getCamZoom());
    flame.setCamZoom(1.0);
    return flame;
  }

  public String getFlameXML(Flame pFlame) throws Exception {
    SimpleXMLBuilder xb = new SimpleXMLBuilder();
    List<SimpleXMLBuilder.Attribute<?>> flamesAttrList = new ArrayList<>();
    flamesAttrList.add(new Attribute<String>("name", ""));

    xb.beginElement("Flames", flamesAttrList);

    // Flame
    List<SimpleXMLBuilder.Attribute<?>> attrList = createFlameAttributes(pFlame, xb);
    Layer layer = pFlame.getFirstLayer();

    xb.beginElement("flame", attrList);
    // XForm
    for (XForm xForm : layer.getXForms()) {
      xb.emptyElement("xform", createXFormAttrList(xb, layer, xForm));
    }
    // FinalXForms
    for (XForm xForm : layer.getFinalXForms()) {
      xb.emptyElement("finalxform", createXFormAttrList(xb, layer, xForm));
    }
    // Gradient
    addGradient(xb, layer);
    xb.endElement("flame");
    xb.endElement("Flames");
    return xb.buildXML();
  }

  private void addGradient(SimpleXMLBuilder xb, Layer layer) {
    RGBPalette palette = layer.getPalette();
    for (int i = 0; i < 256; i++) {
      List<SimpleXMLBuilder.Attribute<?>> attrList = new ArrayList<>();
      attrList.add(new Attribute<Integer>("index", i));
      String rgbStr = palette.getColor(i).getRed() + " " + palette.getColor(i).getGreen() + " " + palette.getColor(i).getBlue();
      attrList.add(new Attribute<String>("rgb", rgbStr));
      xb.simpleElement("color", null, attrList);
    }
  }
}
