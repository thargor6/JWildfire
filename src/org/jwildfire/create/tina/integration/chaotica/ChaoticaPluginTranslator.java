/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.integration.chaotica;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChaoticaPluginTranslator {
  private final String translatedName;
  private final Map<String, String> propertyNames = new HashMap<String, String>();
  private final Map<String, Double> fixedValues = new HashMap<String, Double>();

  public ChaoticaPluginTranslator(String pChaoticaName, Pair<?, ?>... pPropertyNames) {
    translatedName = pChaoticaName;
    for (Pair<?, ?> pair : pPropertyNames) {
      if (pair instanceof NamePair) {
        NamePair namePair = (NamePair) pair;
        propertyNames.put(namePair.getFirst(), namePair.getSecond());
      }
      else if (pair instanceof FixedValue) {
        FixedValue fixedValue = (FixedValue) pair;
        propertyNames.put(fixedValue.getFirst(), fixedValue.getFirst());
        fixedValues.put(fixedValue.getFirst(), fixedValue.getSecond());
      }
    }
  }

  public String getTranslatedName() {
    return translatedName;
  }

  public String translatePropertyName(String propertyName) {
    if (propertyNames.isEmpty()) {
      return translatedName + "_" + propertyName;
    }
    else {
      String translatedProperty = propertyNames.get(propertyName);
      return translatedProperty != null ? translatedName + "_" + translatedProperty : null;
    }
  }

  public List<String> getFixedValueNames() {
    return new ArrayList<String>(fixedValues.keySet());
  }

  public Double getFixedValue(String pName) {
    return fixedValues.get(pName);
  }
}
