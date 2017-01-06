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

public class IsoSFPlot3DWFFuncPresets extends WFFuncPresets<IsoSFPlot3DWFFuncPreset> {

  public IsoSFPlot3DWFFuncPresets() {
    super("isosfplot3d_wf_presets.txt");
  }

  @Override
  protected IsoSFPlot3DWFFuncPreset createDefaultPreset() {
    return new IsoSFPlot3DWFFuncPreset(-1, "x", -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
  }

  @Override
  protected IsoSFPlot3DWFFuncPreset parsePreset(String preset) {
    int id = parseId(preset);
    String formula = parseFormula(preset, "formula");

    double xmin = parseParam(preset, "xmin");
    double xmax = parseParam(preset, "xmax");
    double ymin = parseParam(preset, "ymin");
    double ymax = parseParam(preset, "ymax");
    double zmin = parseParam(preset, "zmin");
    double zmax = parseParam(preset, "zmax");

    double param_a = parseParam(preset, "param_a");
    double param_b = parseParam(preset, "param_b");
    double param_c = parseParam(preset, "param_c");
    double param_d = parseParam(preset, "param_d");
    double param_e = parseParam(preset, "param_e");
    double param_f = parseParam(preset, "param_f");

    return new IsoSFPlot3DWFFuncPreset(id, formula, xmin, xmax, ymin, ymax, zmin, zmax, param_a, param_b, param_c, param_d, param_e, param_f);
  }

}
