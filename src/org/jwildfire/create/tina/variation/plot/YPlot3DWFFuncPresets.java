/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.create.tina.variation.plot;

public class YPlot3DWFFuncPresets extends WFFuncPresets<YPlot3DWFFuncPreset> {

  public YPlot3DWFFuncPresets() {
    super("yplot3d_wf_presets.txt");
  }

  @Override
  protected YPlot3DWFFuncPreset createDefaultPreset() {
    return new YPlot3DWFFuncPreset(-1, "0.0", -1.0, 1.0, -1.0, 1.0);
  }

  @Override
  protected YPlot3DWFFuncPreset parsePreset(String preset) {
    int id = parseId(preset);
    String formula = parseAndValidateFormula(preset, "formula", "x", "z");

    double xmin = parseParam(preset, "xmin");
    double xmax = parseParam(preset, "xmax");
    double zmin = parseParam(preset, "zmin");
    double zmax = parseParam(preset, "zmax");

    return new YPlot3DWFFuncPreset(id, formula, xmin, xmax, zmin, zmax);
  }

}
