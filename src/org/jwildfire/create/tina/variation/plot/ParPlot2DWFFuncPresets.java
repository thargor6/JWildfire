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

public class ParPlot2DWFFuncPresets extends WFFuncPresets<ParPlot2DWFFuncPreset> {

  public ParPlot2DWFFuncPresets() {
    super("parplot2d_wf_presets.txt");
  }

  @Override
  protected ParPlot2DWFFuncPreset createDefaultPreset() {
    return new ParPlot2DWFFuncPreset(-1, "u", "0.0", "v", -1.0, 1.0, -1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
  }

  @Override
  protected ParPlot2DWFFuncPreset parsePreset(String preset) {
    int id = parseId(preset);
    String xformula = parseFormula(preset, "xformula");
    String yformula = parseFormula(preset, "yformula");
    String zformula = parseFormula(preset, "zformula");

    double umin = parseParam(preset, "umin");
    double umax = parseParam(preset, "umax");
    double vmin = parseParam(preset, "vmin");
    double vmax = parseParam(preset, "vmax");

    double param_a = parseParam(preset, "param_a");
    double param_b = parseParam(preset, "param_b");
    double param_c = parseParam(preset, "param_c");
    double param_d = parseParam(preset, "param_d");
    double param_e = parseParam(preset, "param_e");
    double param_f = parseParam(preset, "param_f");

    return new ParPlot2DWFFuncPreset(id, xformula, yformula, zformula, umin, umax, vmin, vmax, param_a, param_b, param_c, param_d, param_e, param_f);
  }

}
