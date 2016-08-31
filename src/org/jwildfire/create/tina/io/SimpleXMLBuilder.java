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
import java.util.Arrays;
import java.util.List;

public class SimpleXMLBuilder {
  private StringBuilder sb = new StringBuilder();
  private int currIdentLevel = 0;
  private int identSize = 2;

  public List<Attribute<?>> createAttrList(Attribute<?>... pAttributes) {
    return new ArrayList<Attribute<?>>(Arrays.asList(pAttributes));
  }

  public Attribute<String> createAttr(String pName, String pValue) {
    return new Attribute<String>(pName, pValue);
  }

  public Attribute<Integer> createAttr(String pName, Integer pValue) {
    return new Attribute<Integer>(pName, pValue);
  }

  public Attribute<Double> createAttr(String pName, Double pValue) {
    return new Attribute<Double>(pName, pValue);
  }

  public Attribute<Integer> createAttr(String pName, Boolean pValue) {
    return new Attribute<Integer>(pName, pValue ? 1 : 0);
  }

  public void beginElement(String pElement, List<Attribute<?>> attrList) {
    Attribute<?>[] attrArray = new Attribute<?>[attrList.size()];
    int idx = 0;
    for (Attribute<?> attr : attrList) {
      attrArray[idx++] = attr;
    }
    beginElement(pElement, attrArray);
  }

  public void beginElement(String pElement, Attribute<?>... attr) {
    doIdent();
    if (attr != null && attr.length > 0) {
      sb.append("<");
      sb.append(pElement);
      sb.append(" ");
      addAttributes(attr);
      sb.append(">\n");
    }
    else {
      sb.append("<");
      sb.append(pElement);
      sb.append(">\n");
    }
    currIdentLevel++;
  }

  private void addAttributes(Attribute<?>[] attr) {
    for (int i = 0; i < attr.length; i++) {
      sb.append(attr[i].getName() + "=\"");
      String value = String.valueOf(attr[i].getValue());
      sb.append(value.replaceAll("\"", "&quot;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("&", "&amp;"));
      if (i < attr.length - 1) {
        sb.append("\" ");
      }
      else {
        sb.append("\"");
      }
    }
  }

  public void endElement(String pElement) {
    currIdentLevel--;
    doIdent();
    sb.append("</");
    sb.append(pElement);
    sb.append(">\n");
  }

  public void emptyElement(String pElement, List<Attribute<?>> attrList) {
    Attribute<?>[] attrArray = new Attribute<?>[attrList.size()];
    int idx = 0;
    for (Attribute<?> attr : attrList) {
      attrArray[idx++] = attr;
    }
    emptyElement(pElement, attrArray);
  }

  public void emptyElement(String pElement, Attribute<?>... attr) {
    doIdent();
    if (attr != null && attr.length > 0) {
      sb.append("<");
      sb.append(pElement);
      sb.append(" ");
      addAttributes(attr);
      sb.append("/>\n");
    }
    else {
      sb.append("<");
      sb.append(pElement);
      sb.append("/>\n");
    }
  }

  public void simpleElement(String pElement, String pContent, List<Attribute<?>> attrList) {
    Attribute<?>[] pAttrArray = new Attribute<?>[attrList.size()];
    int idx = 0;
    for (Attribute<?> attr : attrList) {
      pAttrArray[idx++] = attr;
    }
    simpleElement(pElement, pContent, pAttrArray);
  }

  public void simpleElement(String pElement, String pContent, Attribute<?>... pAttr) {
    doIdent();
    if (pAttr != null && pAttr.length > 0) {
      sb.append("<");
      sb.append(pElement);
      sb.append(" ");
      addAttributes(pAttr);
      sb.append(">");
    }
    else {
      sb.append("<");
      sb.append(pElement);
      sb.append(">");
    }
    if (pContent != null) {
      sb.append(pContent);
    }
    sb.append("</");
    sb.append(pElement);
    sb.append(">\n");
  }

  private void doIdent() {
    for (int i = 0; i < currIdentLevel * identSize; i++) {
      sb.append(" ");
    }
  }

  public String buildXML() {
    return sb.toString();
  }

  public static class Attribute<T> {
    private final String name;
    private final T value;

    public Attribute(String pName, T pValue) {
      name = pName;
      value = pValue;
    }

    public String getName() {
      return name;
    }

    public T getValue() {
      return value;
    }

  }

  public void addContent(String pContent) {
    if (pContent != null) {
      sb.append(pContent);
    }
  }

}
