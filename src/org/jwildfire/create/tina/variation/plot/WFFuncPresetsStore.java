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

public class WFFuncPresetsStore {
  private WFFuncPresetsStore() {

  }

  private static ParPlot2DWFFuncPresets parPlot2DWFFuncPresets;
  private static YPlot2DWFFuncPresets yPlot2DWFFuncPresets;
  private static YPlot3DWFFuncPresets yPlot3DWFFuncPresets;
  private static IsoSFPlot3DWFFuncPresets isoSFPlot3DWFFuncPresets;
  private static SAttractor3DWFFuncPresets SAttractor3DWFFuncPresets;
  private static Knots3DWFFuncPresets Knots3DWFFuncPresets;
  private static PolarPlot2DWFFuncPresets polarPlot2DWFFuncPresets;
  private static PolarPlot3DWFFuncPresets polarPlot3DWFFuncPresets;

  public static ParPlot2DWFFuncPresets getParPlot2DWFFuncPresets() {
    if (parPlot2DWFFuncPresets == null) {
      parPlot2DWFFuncPresets = new ParPlot2DWFFuncPresets();
    }
    return parPlot2DWFFuncPresets;
  }

  public static YPlot2DWFFuncPresets getYPlot2DWFFuncPresets() {
    if (yPlot2DWFFuncPresets == null) {
      yPlot2DWFFuncPresets = new YPlot2DWFFuncPresets();
    }
    return yPlot2DWFFuncPresets;
  }

  public static YPlot3DWFFuncPresets getYPlot3DWFFuncPresets() {
    if (yPlot3DWFFuncPresets == null) {
      yPlot3DWFFuncPresets = new YPlot3DWFFuncPresets();
    }
    return yPlot3DWFFuncPresets;
  }

  public static IsoSFPlot3DWFFuncPresets getIsoSFPlot3DWFFuncPresets() {
    if (isoSFPlot3DWFFuncPresets == null) {
      isoSFPlot3DWFFuncPresets = new IsoSFPlot3DWFFuncPresets();
    }
    return isoSFPlot3DWFFuncPresets;
  }

  public static SAttractor3DWFFuncPresets getSAttractor3DWFFuncPresets() {
    if (SAttractor3DWFFuncPresets == null) {
      SAttractor3DWFFuncPresets = new SAttractor3DWFFuncPresets();
    }
    return SAttractor3DWFFuncPresets;
  }

  public static Knots3DWFFuncPresets getKnots3DWFFuncPresets() {
    if (Knots3DWFFuncPresets == null) {
      Knots3DWFFuncPresets = new Knots3DWFFuncPresets();
    }
    return Knots3DWFFuncPresets;
  }

  public static PolarPlot2DWFFuncPresets getPolarPlot2DWFFuncPresets() {
    if (polarPlot2DWFFuncPresets == null) {
      polarPlot2DWFFuncPresets = new PolarPlot2DWFFuncPresets();
    }
    return polarPlot2DWFFuncPresets;
  }

  public static PolarPlot3DWFFuncPresets getPolarPlot3DWFFuncPresets() {
    if (polarPlot3DWFFuncPresets == null) {
      polarPlot3DWFFuncPresets = new PolarPlot3DWFFuncPresets();
    }
    return polarPlot3DWFFuncPresets;
  }
}
