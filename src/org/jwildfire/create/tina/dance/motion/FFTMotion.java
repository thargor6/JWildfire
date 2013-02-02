/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.dance.motion;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;

public class FFTMotion extends Motion {
  private static final long serialVersionUID = 1L;

  @Property(description = "FFT channel", category = PropertyCategory.GENERAL)
  private int fftChannel = 0;

  public int getFftChannel() {
    return fftChannel;
  }

  public void setFftChannel(int pFFTIndex) {
    fftChannel = pFFTIndex;
  }

}
