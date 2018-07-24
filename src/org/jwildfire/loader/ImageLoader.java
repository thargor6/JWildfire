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

import java.util.Calendar;

import javax.swing.*;

import org.jwildfire.base.ManagedObject;
import org.jwildfire.image.SimpleImage;


public abstract class ImageLoader extends ManagedObject {
  protected JFrame rootFrame;

  public SimpleImage execute() {
    long t0 = initTime();
    SimpleImage res = loadImage();
    showElapsedTime(t0);
    return res;
  }

  protected abstract SimpleImage loadImage();

  private long initTime() {
    return Calendar.getInstance().getTimeInMillis();
  }

  private void showElapsedTime(long t0) {
    long t1 = Calendar.getInstance().getTimeInMillis();
    String name = this.getClass().getSimpleName();
    System.out.println(name + ": " + ((double) (t1 - t0) / 1000.0) + "s");
  }

  protected JFrame getRootFrame() {
    return rootFrame;
  }

  public void setRootFrame(JFrame pRootFrame) {
    this.rootFrame = pRootFrame;
  }

}
