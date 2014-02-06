/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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

import java.util.ArrayList;
import java.util.List;


public class PropertyModel extends AbstractProperty {
  private static final long serialVersionUID = 1L;
  private final List<PlainProperty> properties = new ArrayList<PlainProperty>();
  private final List<PropertyModel> childs = new ArrayList<PropertyModel>();

  public PropertyModel(AbstractProperty pParent, String pName, Class<?> pType) {
    super(pParent, pName, pType);
  }

  public List<PlainProperty> getProperties() {
    return properties;
  }

  public List<PropertyModel> getChields() {
    return childs;
  }

  public int getTotalCount() {
    int res = properties.size();
    for (PropertyModel child : childs) {
      res += child.getTotalCount();
    }
    return res;
  }

  public String toXML() {
    StringBuilder sb = new StringBuilder();
    int depth = 0;
    addToXML(sb, this, depth);
    return sb.toString();
  }

  private void addToXML(StringBuilder pSB, PropertyModel pNode, int pDepth) {
    for (int i = 0; i < pDepth; i++)
      pSB.append("  ");
    pSB.append("<" + pNode.getName());
    for (PlainProperty property : pNode.getProperties()) {
      pSB.append(" " + property.getName());
    }
    pSB.append(">\n");
    for (PropertyModel node : pNode.getChields()) {
      addToXML(pSB, node, pDepth + 1);
    }
    for (int i = 0; i < pDepth; i++)
      pSB.append("  ");
    pSB.append("</" + pNode.getName() + ">\n");
  }

}
