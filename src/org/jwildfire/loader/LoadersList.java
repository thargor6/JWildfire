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
package org.jwildfire.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.*;


public class LoadersList {
  private static List<Class<? extends ImageLoader>> items = new ArrayList<Class<? extends ImageLoader>>();
  private static final Vector<String> itemVector;

  private static void registerLoader(Class<? extends ImageLoader> pLoader) {
    items.add(pLoader);
  }

  static {
    registerLoader(ImageSequenceLoader.class);
    itemVector = new Vector<>();
    for (Class<? extends ImageLoader> loader : items) {
      itemVector.add(loader.getSimpleName());
    }
    Collections.sort(itemVector);
  }

  public static Vector<String> getItemVector() {
    return itemVector;
  }

  public static ImageLoader getLoaderInstance(JFrame pRootFrame, String pName) {
    for (Class<? extends ImageLoader> loaderCls : items) {
      if (loaderCls.getSimpleName().equals(pName)) {
        try {
          ImageLoader res = loaderCls.newInstance();
          res.setRootFrame(pRootFrame);
          return res;
        }
        catch (Throwable e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

}
