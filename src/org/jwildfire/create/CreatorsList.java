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
package org.jwildfire.create;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class CreatorsList {
  private static List<Class<? extends ImageCreator>> items = new ArrayList<Class<? extends ImageCreator>>();
  private static Vector<String> itemVector = null;

  private static void registerCreator(Class<? extends ImageCreator> pCreator) {
    items.add(pCreator);
  }

  static {
    registerCreator(BenoitCreator.class);
    registerCreator(PlainImageCreator.class);
    registerCreator(PlasmaCreator.class);
    registerCreator(GradientCreator.class);
    registerCreator(DLACreator.class);
    registerCreator(TileBrickCreator.class);
    registerCreator(CloudCreator.class);
    registerCreator(PerlinNoiseCreator.class);
    registerCreator(FastNoiseCreator.class);
  }

  public static Vector<String> getItemVector() {
    if (itemVector == null) {
      Vector<String> v = new Vector<String>();
      for (Class<? extends ImageCreator> creator : items) {
        v.add(creator.getSimpleName());
      }
      Collections.sort(v);
      itemVector = v;
    }
    return itemVector;
  }

  public static ImageCreator getCreatorInstance(String pName) {
    for (Class<? extends ImageCreator> creatorCls : items) {
      if (creatorCls.getSimpleName().equals(pName)) {
        try {
          ImageCreator creator = creatorCls.newInstance();
          creator.initPresets();
          return creator;
        }
        catch (Throwable e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

}
