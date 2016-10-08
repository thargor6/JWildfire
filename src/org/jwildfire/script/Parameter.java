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
package org.jwildfire.script;

import org.jwildfire.envelope.Envelope;

public class Parameter implements Cloneable {
  private String name;
  private String value;
  private Envelope envelope;

  public Parameter clone() {
    Parameter res = new Parameter();
    res.name = name;
    res.value = value;
    res.envelope = (envelope != null) ? envelope.clone() : null;
    return res;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value != null ? value.trim() : "";
  }

  public void setValue(String pValue) {
    value = pValue;
  }

  public void setEnvelope(Envelope pEnvelope) {
    envelope = pEnvelope;
  }

  public Envelope getEnvelope() {
    return envelope;
  }
}
