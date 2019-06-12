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
package org.jwildfire.create.tina.palette;

public class RenderColor {
  public double red;
  public double green;
  public double blue;

  public RenderColor() {
  }

  public RenderColor(double red, double green, double blue) {
    super();
    this.red = red;
    this.green = green;
    this.blue = blue;
  }
  
  public RenderColor(double pWhiteLevel, int red, int green, int blue) {
    this.setColor(pWhiteLevel, red, green, blue);
  }
  
  public RenderColor(double pWhiteLevel, RGBColor color) {
    this.setColor(pWhiteLevel, color.getRed(), color.getGreen(), color.getBlue());
  }
  
  public void setColor(double pWhiteLevel, int red, int green, int blue) {
    double DFLT_WHITE_LEVEL = 200.0;
    this.red = red * DFLT_WHITE_LEVEL / 256.0;
    this.green = green * DFLT_WHITE_LEVEL / 256.0;
    this.blue = blue * DFLT_WHITE_LEVEL / 256.0;
  }
  
  public void setColor(double pWhiteLevel, RGBColor color) {
    this.setColor(pWhiteLevel, color.getRed(), color.getGreen(), color.getBlue());
  }
  
 @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(blue);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(green);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(red);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RenderColor other = (RenderColor) obj;
    if (Double.doubleToLongBits(blue) != Double.doubleToLongBits(other.blue))
      return false;
    if (Double.doubleToLongBits(green) != Double.doubleToLongBits(other.green))
      return false;
    if (Double.doubleToLongBits(red) != Double.doubleToLongBits(other.red))
      return false;
    return true;
  }

}
