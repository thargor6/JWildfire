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

public class SAttractor3DWFFuncPresets extends WFFuncPresets<SAttractor3DWFFuncPreset> {

  public SAttractor3DWFFuncPresets() {
    super("sattractor3d_wf_presets.txt");
  }

  @Override
  protected SAttractor3DWFFuncPreset createDefaultPreset() {
    return new SAttractor3DWFFuncPreset(-1, "x", "y", "z", 1.0, 1.0, 1.0, 5, 0.1, 0.02, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
  }

  @Override
  protected SAttractor3DWFFuncPreset parsePreset(String preset) {
    int id = parseId(preset);
    String xformula = parseFormula(preset, "xformula");
    String yformula = parseFormula(preset, "yformula");
    String zformula = parseFormula(preset, "zformula");

    double startx = parseParam(preset, "startx");
    double starty = parseParam(preset, "starty");
    double startz = parseParam(preset, "startz");

    double steps = parseParam(preset, "steps");
    double radius = parseParam(preset, "radius");
    double steptime = parseParam(preset, "steptime");

    double param_a = parseParam(preset, "param_a");
    double param_b = parseParam(preset, "param_b");
    double param_c = parseParam(preset, "param_c");
    double param_d = parseParam(preset, "param_d");
    double param_e = parseParam(preset, "param_e");
    double param_f = parseParam(preset, "param_f");
    double param_g = parseParam(preset, "param_g");
    double param_h = parseParam(preset, "param_h");

    return new SAttractor3DWFFuncPreset(id, xformula, yformula, zformula, startx, starty, startz, steps, radius, steptime,
            param_a, param_b, param_c, param_d, param_e, param_f, param_g, param_h);
  }

}
