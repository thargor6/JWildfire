/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import static java.lang.Math.IEEEremainder;

/**
 * Multi-IFS Variation for JWildfire.
 *
 * This variation encapsulates different Iterated Function Systems (IFS).
 * It can generate a Vicsek fractal, a Trapeze fractal, and others.
 * It randomly chooses one of four affine transformations from the selected
 * IFS and assigns color based on several simple and iterative modes.
 *
 * @author Gemini, based on user-provided IFS definitions from ifstile.com
 */
public class MultiIFSFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  // Parameter names for the UI
  private static final String PARAM_IFS_TYPE = "ifs_type";
  private static final String PARAM_COLORING_MODE = "coloring_mode";
  private static final String PARAM_COLOR_SPEED = "color_speed";
  private static final String PARAM_COLOR_1 = "color1";
  private static final String PARAM_COLOR_2 = "color2";
  private static final String PARAM_COLOR_3 = "color3";
  private static final String PARAM_COLOR_4 = "color4";
  private static final String[] paramNames = { PARAM_IFS_TYPE, PARAM_COLORING_MODE, PARAM_COLOR_SPEED, PARAM_COLOR_1, PARAM_COLOR_2, PARAM_COLOR_3, PARAM_COLOR_4 };
  
  // Member variables for parameters
  private int ifs_type = 0; 
  private int coloringMode = 0; // 0:Simple, 1:Blend
  private double colorSpeed = 0.5;
  private double color1 = 0.0;
  private double color2 = 0.25;
  private double color3 = 0.5;
  private double color4 = 0.75;
  
  // Constant for Trapeze transformations
  private static final double S3D2 = 0.4330127018922193; // sqrt(3)/2

  private double fract(double x) {
    return x - Math.floor(x);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    
    // Get the input point
    double x = pAffineTP.x;
    double y = pAffineTP.y;
    
    double nx = 0.0, ny = 0.0;
    double newColor = 0.0;

    // Get a random number to select one of the transformations.
    double rand = pContext.random();

    // Select the IFS type
    // JWildfire mapping rule: coefs=(a,b,c,d,e,f) -> nx=a*x+c*y+e, ny=b*x+d*y+f
    switch(ifs_type) {
      case 1: // Trapeze_4 IFS
        if (rand < 0.25) {
            nx = 0.5 * x;
            ny = 0.5 * y;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = -0.25 * x - S3D2 * y + 0.25;
            ny = S3D2 * x - 0.25 * y + S3D2;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = -0.25 * x + S3D2 * y - 0.5;
            ny = -S3D2 * x - 0.25 * y;
            newColor = color3;
        } else {
            nx = -0.5 * x - 0.25;
            ny = -0.5 * y + S3D2;
            newColor = color4;
        }
        break;
      case 2: // Trapeze_3_5 IFS
        if (rand < 0.25) {
            nx = 0.5 * x;
            ny = 0.5 * y;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = 0.5 * x - 0.5;
            ny = 0.5 * y + 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = -0.5 * x + 0.5;
            ny = 0.5 * y + 0.5;
            newColor = color3;
        } else {
            nx = 0.5 * y + 0.5;
            ny = -0.5 * x + 0.5;
            newColor = color4;
        }
        break;
      case 3: // Trapeze_2_6 IFS
        if (rand < 0.25) {
            nx = 0.5 * x;
            ny = 0.5 * y;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = -0.5 * x + 0.5;
            ny = 0.5 * y;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = -0.25 * x - S3D2 * y + 0.25;
            ny = -S3D2 * x + 0.25 * y + S3D2;
            newColor = color3;
        } else {
            nx = -0.25 * x + S3D2 * y + 0.25;
            ny = -S3D2 * x - 0.25 * y + S3D2;
            newColor = color4;
        }
        break;
      case 4: // Spiral IFS
        if (rand < 0.25) {
            nx = 0.5 * x;
            ny = 0.5 * y;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = 0.5 * x + 0.5;
            ny = 0.5 * y;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = 0.5 * x;
            ny = 0.5 * y + 0.5;
            newColor = color3;
        } else {
            nx = 0.5 * y + 0.5;
            ny = -0.5 * x + 1.0;
            newColor = color4;
        }
        break;
      case 5: // Sierpinski Hexagon
        if (rand < 0.25) {
            nx = (0.25 * x) + (-0.4330127018922193 * y) + 0;
            ny = (0.4330127018922193 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.5;
            ny = (-0.4330127018922193 * x) + (-0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922193 * x) + (-0.25 * y) - 0.4330127018922193;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922193 * x) + (-0.25 * y) + 0.4330127018922193;
            newColor = color4;
        }
        break;
      case 6: // User Fractal
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) - 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) - 0.5;
            ny = (0.4330127018922194 * x) + (-0.25 * y) - 0.8660254037844387;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) + 1.0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color4;
        }
        break;
      case 7: // from TESTING 1 case 0
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (-0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color4;
        }
        break;
      case 8: // from TESTING 1 case 1
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 0.25;
            ny = (0 * x) + (-0.5 * y) + 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (0.4330127018922194 * x) + (0.25 * y) - 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 9: // from TESTING 1 case 2
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.5;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.25;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 10: // from TESTING 1 case 3
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 1;
            ny = (0 * x) + (-0.5 * y) - 1.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) - 1;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) - 1;
            ny = (0 * x) + (0.5 * y) - 1.5;
            newColor = color4;
        }
        break;
      case 11: // from TESTING 1 case 4
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 12: // from TESTING 1 case 5
        if (rand < 0.25) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (0 * x) + (-0.5 * y) - 1;
            ny = (-0.5 * x) + (0 * y) - 1;
            newColor = color4;
        }
        break;
      case 13: // from TESTING 1 case 6
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.5;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.5;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color4;
        }
        break;
      case 14: // from TESTING 1 case 7
        if (rand < 0.25) {
            nx = (0 * x) + (-0.5 * y) + 0;
            ny = (-0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (-0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) + 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (-0.5 * y) - 0.5;
            ny = (-0.5 * x) + (0 * y) + 0;
            newColor = color3;
        } else {
            nx = (0 * x) + (0.5 * y) - 0.5;
            ny = (0.5 * x) + (0 * y) + 0.5;
            newColor = color4;
        }
        break;
      case 15: // from TESTING 1 case 8
        if (rand < 0.25) {
            nx = (0 * x) + (-0.5 * y) + 0;
            ny = (-0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 1;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) - 1.5;
            ny = (0.5 * x) + (0 * y) + 0.5;
            newColor = color3;
        } else {
            nx = (0 * x) + (-0.5 * y) - 0.5;
            ny = (-0.5 * x) + (0 * y) + 0.5;
            newColor = color4;
        }
        break;
      case 16: // from TESTING 2 case 0
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (0.5 * y) + 0.5;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) + 2;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) + 1;
            ny = (0 * x) + (-0.5 * y) - 1.5;
            newColor = color4;
        }
        break;
      case 17: // from TESTING 2 case 1
        if (rand < 0.25) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.5;
            ny = (-0.4330127018922194 * x) + (0.25 * y) - 0.8660254037844387;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 0.25;
            ny = (0.4330127018922194 * x) + (-0.25 * y) - 1.299038105676658;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 1.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 18: // from TESTING 2 case 2
        if (rand < 0.25) {
            nx = (0 * x) + (-0.5 * y) + 0;
            ny = (-0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (0 * x) + (-0.5 * y) + 0;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color4;
        }
        break;
      case 19: // from TESTING 2 case 3
        if (rand < 0.25) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) + 0.5;
            ny = (0.5 * x) + (0 * y) + 0;
            newColor = color3;
        } else {
            nx = (0 * x) + (-0.5 * y) + 0.5;
            ny = (-0.5 * x) + (0 * y) + 0.5;
            newColor = color4;
        }
        break;
      case 20: // from TESTING 2 case 4
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (0.5 * y) + 0.5;
            ny = (0.5 * x) + (0 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (-0.5 * y) + 0.5;
            ny = (-0.5 * x) + (0 * y) + 0.5;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color4;
        }
        break;
      case 21: // from TESTING 2 case 5
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) + 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) - 0.5;
            ny = (0.5 * x) + (0 * y) + 1;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) + 1;
            newColor = color4;
        }
        break;
      case 22: // from TESTING 2 case 6
        if (rand < 0.25) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) - 1;
            newColor = color3;
        } else {
            nx = (0 * x) + (0.5 * y) - 1.5;
            ny = (0.5 * x) + (0 * y) + 0;
            newColor = color4;
        }
        break;
      case 23: // from TESTING 2 case 7
        if (rand < 0.25) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) - 1;
            newColor = color3;
        } else {
            nx = (0 * x) + (-0.5 * y) - 0.5;
            ny = (0.5 * x) + (0 * y) - 0.5;
            newColor = color4;
        }
        break;
      case 24: // from TESTING 3 case 0
        if (rand < 0.25) {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 0.5;
            ny = (0.4330127018922194 * x) + (-0.25 * y) - 0.8660254037844387;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 1.5;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color3;
        } else {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 1.5;
            ny = (0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color4;
        }
        break;
      case 25: // from TESTING 3 case 1
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.25 * x) + (0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 26: // from TESTING 3 case 2
        if (rand < 0.25) {
            nx = (0.25 * x) + (-0.4330127018922193 * y) + 0;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (0.4330127018922194 * x) + (-0.25 * y) - 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.5 * x) + (0 * y) + 0.25;
            ny = (0 * x) + (-0.5 * y) - 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) + 0.25;
            ny = (0 * x) + (0.5 * y) - 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 27: // from TESTING 3 case 3
        if (rand < 0.25) {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.25 * x) + (-0.4330127018922193 * y) + 0.5;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) + 0.25;
            ny = (0 * x) + (-0.5 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 28: // from TESTING 3 case 4
        if (rand < 0.25) {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) + 0.25;
            ny = (0 * x) + (-0.5 * y) + 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.25;
            ny = (0.4330127018922194 * x) + (0.25 * y) - 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) - 0.75;
            ny = (0 * x) + (-0.5 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 29: // from TESTING 3 case 5
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (0.5 * y) + 0.5;
            ny = (-0.5 * x) + (0 * y) + 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) + 1.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color4;
        }
        break;
      case 30: // from TESTING 3 case 6
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 0.25;
            ny = (0 * x) + (0.5 * y) + 1.299038105676658;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.25 * x) + (0.4330127018922193 * y) - 0.75;
            ny = (0.4330127018922194 * x) + (-0.25 * y) - 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (0.25 * x) + (0.4330127018922193 * y) - 0.25;
            ny = (0.4330127018922194 * x) + (-0.25 * y) + 1.299038105676658;
            newColor = color4;
        }
        break;
      case 31: // from TESTING 3 case 7
        if (rand < 0.25) {
            nx = (0 * x) + (-0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (0.5 * y) - 0.5;
            ny = (-0.5 * x) + (0 * y) + 2;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) - 1;
            ny = (-0.5 * x) + (0 * y) + 1;
            newColor = color3;
        } else {
            nx = (0 * x) + (0.5 * y) - 1.5;
            ny = (-0.5 * x) + (0 * y) + 0;
            newColor = color4;
        }
        break;
      case 32: // from TESTING 3 case 8
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (0.5 * y) + 0.5;
            ny = (0.5 * x) + (0 * y) - 1;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) + 1.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (0 * x) + (-0.5 * y) + 0.5;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color4;
        }
        break;
      case 33: // from TESTING 3 case 9
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.5;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.5;
            ny = (0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color4;
        }
        break;
      case 34: // from TESTING 3 case 10
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 1.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) - 1.5;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 1.5;
            newColor = color4;
        }
        break;
      case 35: // from TESTING 4 case 0
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 1;
            ny = (0 * x) + (-0.5 * y) - 1.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) - 1;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) - 1;
            ny = (0 * x) + (0.5 * y) - 1.5;
            newColor = color4;
        }
        break;
      case 36: // from TESTING 4 case 1
        if (rand < 0.25) {
            nx = (0.25 * x) + (-0.4330127018922193 * y) + 0;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) + 1.25;
            ny = (0 * x) + (-0.5 * y) - 1.299038105676658;
            newColor = color3;
        } else {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 37: // from TESTING 4 case 2
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.25 * x) + (0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 38: // from TESTING 4 case 3
        if (rand < 0.25) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.25 * x) + (-0.4330127018922193 * y) + 0.5;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) + 0.25;
            ny = (0 * x) + (-0.5 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 39: // from TESTING 4 case 4
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (-0.5 * y) - 0.5;
            ny = (0.5 * x) + (0 * y) - 1;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) + 0.5;
            ny = (-0.5 * x) + (0 * y) + 0.5;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) + 1.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color4;
        }
        break;
      case 40: // from TESTING 4 case 5
        if (rand < 0.25) {
            nx = (0 * x) + (-0.5 * y) + 0;
            ny = (-0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 1;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) - 1.5;
            ny = (0.5 * x) + (0 * y) + 0.5;
            newColor = color3;
        } else {
            nx = (0 * x) + (-0.5 * y) - 0.5;
            ny = (-0.5 * x) + (0 * y) + 0.5;
            newColor = color4;
        }
        break;
      case 41: // from TESTING 4 case 6
        if (rand < 0.25) {
            nx = (0 * x) + (-0.5 * y) + 0;
            ny = (-0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (-0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) + 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (-0.5 * y) - 0.5;
            ny = (-0.5 * x) + (0 * y) + 0;
            newColor = color3;
        } else {
            nx = (0 * x) + (0.5 * y) - 0.5;
            ny = (0.5 * x) + (0 * y) + 0.5;
            newColor = color4;
        }
        break;
      case 42: // from TESTING 4 case 7
        if (rand < 0.25) {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) + 0.25;
            ny = (0 * x) + (-0.5 * y) + 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.25;
            ny = (0.4330127018922194 * x) + (0.25 * y) - 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) - 0.75;
            ny = (0 * x) + (-0.5 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 43: // from TESTING 4 case 8
        if (rand < 0.25) {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.25 * x) + (-0.4330127018922193 * y) + 0.5;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) + 0.25;
            ny = (0 * x) + (-0.5 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 44: // from TESTING 4 case 9
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) - 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) - 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color4;
        }
        break;
      case 45: // from TESTING 5 case 0
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (0.5 * y) + 0.5;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) - 1;
            newColor = color4;
        }
        break;
      case 46: // from TESTING 5 case 1
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 1.5;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) - 2;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color4;
        }
        break;
      case 47: // from TESTING 5 case 2
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.5 * x) + (0 * y) - 1;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) - 0.5;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) - 0.5;
            newColor = color4;
        }
        break;
      case 48: // from TESTING 5 case 3
        if (rand < 0.25) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.75;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (0.25 * x) + (-0.4330127018922193 * y) - 0.5;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color4;
        }
        break;
      case 49: // from TESTING 5 case 4
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) + 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) - 0.5;
            ny = (0.5 * x) + (0 * y) + 1;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) + 1;
            newColor = color4;
        }
        break;
      case 50: // from TESTING 6 case 0
        if (rand < 0.25) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.25 * x) + (0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.5;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color4;
        }
        break;
      case 51: // from TESTING 6 case 1
        if (rand < 0.25) {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 0.25;
            ny = (0 * x) + (-0.5 * y) - 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.25 * x) + (0.4330127018922193 * y) - 0.25;
            ny = (0.4330127018922194 * x) + (-0.25 * y) - 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (0.25 * x) + (-0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) - 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 52: // from TESTING 6 case 2
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.25;
            ny = (0.4330127018922194 * x) + (0.25 * y) - 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) - 0.8660254037844387;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) - 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 53: // from TESTING 6 case 3
        if (rand < 0.25) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.75;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (0.25 * x) + (-0.4330127018922193 * y) - 0.5;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color4;
        }
        break;
      case 54: // from TESTING 6 case 4
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.5;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.5;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color4;
        }
        break;
      case 55: // from TESTING 6 case 5
        if (rand < 0.25) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.5;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.25;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 56: // from TESTING 6 case 6
        if (rand < 0.25) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 0.75;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.25 * x) + (0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) - 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.5;
            ny = (0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color4;
        }
        break;
      case 57: // from TESTING 6 case 7
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.5;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) - 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) - 0.5;
            ny = (0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color4;
        }
        break;
      case 58: // from TESTING 6 case 8
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (0.5 * y) + 0.5;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) - 1;
            newColor = color4;
        }
        break;
      case 59: // from TESTING 6 case 9
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 1.5;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) - 2;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color4;
        }
        break;
      case 60: // from TESTING 6 case 10
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.5 * x) + (0 * y) - 1;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) - 0.5;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) - 0.5;
            newColor = color4;
        }
        break;
      case 61: // from TESTING 6 case 11
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) - 0.5;
            ny = (0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) - 0.25;
            ny = (0 * x) + (0.5 * y) - 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) - 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 62: // from TESTING 7 case 0
        if (rand < 0.25) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (-0.5 * y) - 1;
            ny = (-0.5 * x) + (0 * y) - 1.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (-0.5 * x) + (0 * y) - 1;
            newColor = color4;
        }
        break;
      case 63: // from TESTING 7 case 1
        if (rand < 0.25) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (0.5 * x) + (0 * y) - 1;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) + 0;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (0 * x) + (-0.5 * y) - 1;
            ny = (0.5 * x) + (0 * y) - 0.5;
            newColor = color4;
        }
        break;
      case 64: // from TESTING 7 case 2
        if (rand < 0.25) {
            nx = (0 * x) + (-0.5 * y) + 0;
            ny = (-0.5 * x) + (0 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (0.5 * y) + 1.5;
            ny = (-0.5 * x) + (0 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) + 0.5;
            ny = (-0.5 * x) + (0 * y) + 0;
            newColor = color3;
        } else {
            nx = (0 * x) + (0.5 * y) + 1;
            ny = (0.5 * x) + (0 * y) - 1;
            newColor = color4;
        }
        break;
      case 65: // from TESTING 7 case 3
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.5;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.25;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 66: // from TESTING 7 case 4
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) + 0.25;
            ny = (0 * x) + (-0.5 * y) + 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (0.4330127018922194 * x) + (0.25 * y) - 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 67: // from TESTING 7 case 5
        if (rand < 0.25) {
            nx = (0.25 * x) + (-0.4330127018922193 * y) + 0;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) + 0.5;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.5;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color4;
        }
        break;
      case 68: // from TESTING 7 case 6
        if (rand < 0.25) {
            nx = (0.25 * x) + (0.4330127018922193 * y) + 0;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (0.4330127018922194 * x) + (-0.25 * y) + 0.4330127018922194;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) + 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) + 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) + 0.25;
            ny = (0 * x) + (-0.5 * y) + 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 69: // from TESTING 7 case 7
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.5;
            ny = (0.4330127018922194 * x) + (0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (0.25 * y) - 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) - 0.5;
            ny = (0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color4;
        }
        break;
      case 70: // from TESTING 7 case 8
        if (rand < 0.25) {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0 * x) + (0.5 * y) + 0.5;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (-0.5 * y) - 1;
            newColor = color4;
        }
        break;
      case 71: // from TESTING 7 case 9
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (0.5 * x) + (0 * y) - 1.5;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) - 2;
            newColor = color3;
        } else {
            nx = (0.5 * x) + (0 * y) + 0.5;
            ny = (0 * x) + (0.5 * y) - 0.5;
            newColor = color4;
        }
        break;
      case 72: // from TESTING 7 case 10
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.5 * x) + (0 * y) - 1;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (0 * x) + (0.5 * y) - 0.5;
            ny = (-0.5 * x) + (0 * y) - 0.5;
            newColor = color3;
        } else {
            nx = (-0.5 * x) + (0 * y) - 0.5;
            ny = (0 * x) + (-0.5 * y) - 0.5;
            newColor = color4;
        }
        break;
      case 73: // from TESTING 7 case 11
        if (rand < 0.25) {
            nx = (0.5 * x) + (0 * y) + 0;
            ny = (0 * x) + (0.5 * y) + 0;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = (-0.25 * x) + (-0.4330127018922193 * y) - 0.5;
            ny = (0.4330127018922194 * x) + (-0.25 * y) + 0;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = (-0.5 * x) + (0 * y) - 0.25;
            ny = (0 * x) + (0.5 * y) - 0.4330127018922194;
            newColor = color3;
        } else {
            nx = (-0.25 * x) + (0.4330127018922193 * y) - 0.25;
            ny = (-0.4330127018922194 * x) + (-0.25 * y) - 0.4330127018922194;
            newColor = color4;
        }
        break;
      case 0: // Vicsek IFS (default)
      default:
        if (rand < 0.25) {
            nx = 0.5 * x;
            ny = 0.5 * y;
            newColor = color1;
        } else if (rand < 0.5) {
            nx = 0.5 * x - 0.5;
            ny = 0.5 * y - 0.5;
            newColor = color2;
        } else if (rand < 0.75) {
            nx = 0.5 * x - 0.5;
            ny = 0.5 * y;
            newColor = color3;
        } else {
            nx = 0.5 * x;
            ny = 0.5 * y - 0.5;
            newColor = color4;
        }
        break;
    }

    // Apply the chosen coloring mode
    double oldColor = pAffineTP.color;
    switch(coloringMode) {
        case 1: // Iterative Blend
            pVarTP.color = oldColor + (newColor - oldColor) * colorSpeed;
            break;
        case 0: // Simple (default)
        default:
            pVarTP.color = newColor;
            break;
    }
    
    // Apply the result to the variation's output
    pVarTP.x += nx * pAmount;
    pVarTP.y += ny * pAmount;
    
    if (!pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAffineTP.z * pAmount;
    }
  }

  @Override
  public String getName() {
    return "multi_ifs";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { ifs_type, coloringMode, colorSpeed, color1, color2, color3, color4 };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_IFS_TYPE.equalsIgnoreCase(pName))
        ifs_type = (int) pValue;
    else if (PARAM_COLORING_MODE.equalsIgnoreCase(pName))
        coloringMode = (int) pValue;
    else if (PARAM_COLOR_SPEED.equalsIgnoreCase(pName))
        colorSpeed = pValue;
    else if (PARAM_COLOR_1.equalsIgnoreCase(pName))
        color1 = pValue;
    else if (PARAM_COLOR_2.equalsIgnoreCase(pName))
        color2 = pValue;
    else if (PARAM_COLOR_3.equalsIgnoreCase(pName))
        color3 = pValue;
    else if (PARAM_COLOR_4.equalsIgnoreCase(pName))
        color4 = pValue;
    else
        throw new IllegalArgumentException(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[] { VariationFuncType.VARTYPE_2D };
  }
}
