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
package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.cos;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.Layer;

/*
* MobiusStrip variation
* derived from Apophysis mobius (or avMobius) plugin by slobo777:
*    http://slobo777.deviantart.com/art/Mobius-Apo-Variation-60525279
* Initial translation into Java by chronologicaldot -- August 2015
* Extensively revised by CozyG -- September 2015
*    (including adding of modify_z, twists, width_mode, radial_mode parameters) -- September 2015
* Note that the Apophysis "mobius" plugin that "mobius_strip" is derived from is
*    different from the other "mobius" Apophysis plugin that existing JWildfire "mobius"
*    variation is derived from, and the underlying math for the two variations is completely different
* this "mobius_strip" variation is based on equations for the Mobius strip,
*     see https://en.wikipedia.org/wiki/M%C3%B6bius_strip for details
* whereas the other "mobius" variation is based on equations for Mobius transformations,
*     see https://en.wikipedia.org/wiki/M%C3%B6bius_transformation for details
*/
public class MobiusStripFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private static int WRAP = 0;
  private static int EDGE = 1;
  private static int HIDE = 2;
  private static int LEAVE = 3;
  
  double radius = 1;
  // if width >> radius then will have self-intersections?
  double width = 1;
  // twists are really "half-twists" of 180 degrees each
  //   when set via parameter, rounded down to nearest integer
  //   single twist is classical Mobius strip
  //   odd number of twists preserve "single-side" property of Mobius strip
  //   even number of twists has two sides
  double twists = 1;

  // x range of [-rangex/2 -> range_x/2] mapped along the strip (to angle range [0 -> 2*PI])
  //    (strip should probably have explicit xmin and xmax range instead...)
  double range_x = 1;
  // y range of [-range_y/2 -> range_y/2] mapped across the strip (to width range [-width/2 -> width/2])
  //    (strip should probably have explicit ymin and ymax range instead...)
  double range_y = 1;
  
  double rotate_x = 0;
  double rotate_y = 0;
  double modify_z = 1;  // default to full 3D
  int width_mode = WRAP;  // default to wrapping all x coords around the strip
  int radial_mode = WRAP; // default to wrapping all y coords across the strip
  double xmin, xmax, ymin, ymax;
  
  double rotxSin, rotxCos, rotySin, rotyCos;
  
  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_TWISTS = "twists";
  private static final String PARAM_RANGE_X = "range x";
  private static final String PARAM_RANGE_Y = "range y";
  private static final String PARAM_ROTATE_X = "rotate x";
  private static final String PARAM_ROTATE_Y = "rotate y";
  private static final String PARAM_MODIFY_Z = "modify z";
  private static final String PARAM_WIDTH_MODE = "width mode";
  private static final String PARAM_RADIAL_MODE = "radial mode";
  private static final String[] paramNames = { PARAM_RADIUS, PARAM_WIDTH, PARAM_TWISTS, PARAM_RANGE_X, PARAM_RANGE_Y, PARAM_ROTATE_X, PARAM_ROTATE_Y, PARAM_MODIFY_Z, PARAM_WIDTH_MODE, PARAM_RADIAL_MODE };
  
  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    rotxSin = sin( rotate_x * M_2PI );
    rotxCos = cos( rotate_x * M_2PI );
    rotySin = sin( rotate_y * M_2PI );
    rotyCos = cos( rotate_y * M_2PI );
    xmin = -range_x/2;
    xmax = range_x/2;
    ymin = -range_y/2;
    ymax = range_y/2;
  }
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // t ranges from 0 to 2*PI radians around the strip
    double t;
    // s ranges from -width/2 to width/2 across the strip
    double s;

    double Mx, My, Mz, Rx, Ry, Rz;

    double x  = pAffineTP.x;
    double y  = pAffineTP.y;
    
    // HIDE mode takes precedence
    if (radial_mode == HIDE && (x > xmax || x < xmin)) {
      pVarTP.doHide = true;      
      return;
    }
    if (width_mode == HIDE && (y > ymax || y < ymin)) {
      pVarTP.doHide = true;
      return;
    }
    // then LEAVE mode
    if (radial_mode == LEAVE && (x > xmax || x < xmin)) {
      pVarTP.x += pAffineTP.x;
      pVarTP.y += pAffineTP.y;
      return;
    }
    if (width_mode == LEAVE  && (y > ymax || y < ymin)) {
      pVarTP.x += pAffineTP.x;
      pVarTP.y += pAffineTP.y;
      return;
    }

    if (range_x == 0) {
      t = 0;
    }
    else {
      if (radial_mode == WRAP) {
        x = x % (range_x/2); // wrap to [-range_x/2, range_x/2]
      }
      else if (radial_mode == EDGE) {
        if (x > xmax)  { x = xmax; }
        else if (x < xmin) { x = xmin; }
      }
      // also does this calc for points that pass prior checks when in HIDE or LEAVE mode
      t = (x * (M_PI/(range_x/2))) + M_PI;
    }

    if (range_y == 0) {
      s = 0;
    }
    else {
      if (width_mode == WRAP) { 
        y = y % (range_y/2); // wrap to [-range_y/2, range_y/2]
      }
      else if (width_mode == EDGE) {
        if (y > ymax)  { y = ymax; }
        else if (y < ymin) { y = ymin; }
      }
      // also does this calc for points that pass prior checks when in HIDE or LEAVE mode
      s = y * ((width/2)/(range_y/2));
    }
    
    // Initial "object" co-ordinates
    Mx = ( radius + s * cos( twists * t / 2 ) ) * cos( t );
    My = ( radius + s * cos( twists * t / 2 ) ) * sin( t );
    Mz = s * sin( twists * t / 2 );
    
    // Rotate around X axis (change y and z)
    // Store temporarily in R variables
    Rx = Mx;
    Ry = My * rotyCos + Mz * rotySin;
    Rz = Mz * rotyCos - My * rotySin;
    
    // Rotate around Y axis (change x and z)
    // Store back in M variables
    Mx = Rx * rotxCos - Rz * rotxSin;
    My = Ry;
    Mz = Rz * rotxCos + Rx * rotxSin;
    
    // Add final values in to variations totals
    pVarTP.x += pAmount * Mx;
    pVarTP.y += pAmount * My;
    if (modify_z != 0) {
      pVarTP.z += pAmount * Mz * modify_z;
    }
    else {
      pVarTP.z += pAmount * pVarTP.z;
    }
  }
 
  @Override
  public String[] getParameterNames() {
    return paramNames;
  }
  
  @Override
  public Object[] getParameterValues() {
    return new Object[] { radius, width, twists, range_x, range_y, rotate_x, rotate_y, modify_z, width_mode, radial_mode };
  }
  
  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName))
      width = pValue;
    else if (PARAM_TWISTS.equalsIgnoreCase(pName))
      twists = (int)pValue;
    else if (PARAM_RANGE_X.equalsIgnoreCase(pName))
      range_x = pValue;
    else if (PARAM_RANGE_Y.equalsIgnoreCase(pName))
      range_y = pValue;
    else if (PARAM_ROTATE_X.equalsIgnoreCase(pName))
      rotate_x = pValue;
    else if (PARAM_ROTATE_Y.equalsIgnoreCase(pName))
      rotate_y = pValue;
    else if (PARAM_MODIFY_Z.equalsIgnoreCase(pName))
      modify_z = pValue;
    else if (PARAM_WIDTH_MODE.equalsIgnoreCase(pName)) {
      width_mode = (int)pValue;
      if (width_mode < 0 || width_mode > 3) { width_mode = 0; }
    }
    else if (PARAM_RADIAL_MODE.equalsIgnoreCase(pName)) {
      radial_mode = (int)pValue;
      if (radial_mode < 0 || radial_mode > 3) { radial_mode = 0; }
    }
    else
      throw new IllegalArgumentException(pName);
  }
  
  @Override
  public String getName() {
    return "mobius_strip";
  }
  
  @Override
  public int getPriority() {
    return 0;
  }
}
