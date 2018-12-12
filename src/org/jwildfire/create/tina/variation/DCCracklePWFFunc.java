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

// Extension of dc_crackle_wf.
// This version has a "preset" variable that sets the other variables to make common patterns. By Rick Sidwell
// Values are taken from the Built-in script Crackle_Styles_Chooser_Rev01_by_MH, originally from
// Ian Anderson and Anu Wilde.

package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;

public class DCCracklePWFFunc extends DCCrackleWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PRESET = "preset";

  protected static final String[] presetParamName = {PARAM_PRESET};

  private int preset = 0;

  @Override
  public String getName() {
    return "dc_cracklep_wf";
  }

  @Override
  public String[] getParameterNames() {
    return joinArrays(presetParamName, super.getParameterNames());
  }

  @Override
  public Object[] getParameterValues() {
    return joinArrays(new Object[]{preset}, super.getParameterValues());
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESET.equalsIgnoreCase(pName)) {
      preset = Tools.FTOI(pValue);
      switch (preset) {
        case 0:
          cellsize = 1.0;
          power = 0.2;
          distort = 0.0;
          scale = 1.00;
          z = 0.0;
          break;
        case 1:
          cellsize = 0.5;
          power = 0.5;
          distort = 1.0;
          scale = 0.95;
          z = 10.0;
          break;
        case 2:
          cellsize = 0.5;
          power = 0.01;
          distort = 0.5;
          scale = 1.0;
          z = 0.0;
          break;
        case 3:
          cellsize = 0.05;
          power = 0.9;
          distort = 0.9;
          scale = 0.5;
          z = 0.0;
          break;
        case 4:
          cellsize = 0.5;
          power = 1.0;
          distort = 1.0;
          scale = 0.93;
          z = 10.0;
          break;
        case 5:
          cellsize = 1.0;
          power = 1.0;
          distort = 0.0;
          scale = 0.9;
          z = 0.0;
          break;
        case 6:
          cellsize = 0.8;
          power = 0.5;
          distort = 0.5;
          scale = 0.8;
          z = 0.5;
          break;
        case 7:
          cellsize = 0.2;
          power = 0.01;
          distort = 0.0;
          scale = 0.4;
          z = 2.0;
          break;
        case 8:
          cellsize = 1.0;
          power = 0.5;
          distort = 0.25;
          scale = 0.5;
          z = 0.0;
          break;
        case 9:
          cellsize = 0.6;
          power = 0.75;
          distort = 1.0;
          scale = 0.25;
          z = 0.75;
          break;
        case 10:
          cellsize = 0.5;
          power = 25.0;
          distort = 0.0;
          scale = 9.0;
          z = 6.0;
          break;
        case 11:
          cellsize = 0.2;
          power = 1.0;
          distort = 0.0;
          scale = 0.4;
          z = 0.0;
          break;
        case 12:
          cellsize = 1.5;
          power = 0.01;
          distort = 0.0;
          scale = 0.4;
          z = 0.0;
          break;
        case 13:
          cellsize = 8.0;
          power = 0.01;
          distort = 0.0;
          scale = 0.4;
          z = 0.0;
          break;
        case 14:
          cellsize = 0.2;
          power = 0.05;
          distort = 1.0;
          scale = 5.0;
          z = 0.0;
          break;
        case 15:
          cellsize = 0.07;
          power = 0.05;
          distort = 0.5;
          scale = 9.0;
          z = 6.0;
          break;
        case 16:
          cellsize = 0.2;
          power = 0.1;
          distort = 0.0;
          scale = 1.5;
          z = 2.0;
          break;
        case 17:
          cellsize = 0.297494;
          power = 0.662265;
          distort = 0.0708866;
          scale = 0.228156;
          z = 0.0;
          break;
        case 18:
          cellsize = 0.205939;
          power = 1.0;
          distort = 0.0;
          scale = 0.6298;
          z = 0.35;
          break;
        case 19:
          cellsize = 0.5;
          power = 0.001;
          distort = 1.0;
          scale = 2.0;
          z = 0.0;
          break;
        case 20:
          cellsize = 0.5;
          power = 0.0005;
          distort = 0.748;
          scale = 1.465;
          z = 6.0;
          break;
      }
    } else
      super.setParameter(pName, pValue);
  }

  @Override
  public boolean dynamicParameterExpansion() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String pName) {
    // preset doesn't really expand parameters, but it changes them; this will make them refresh
    if (PARAM_PRESET.equalsIgnoreCase(pName)) return true;
    else return false;
  }

}
