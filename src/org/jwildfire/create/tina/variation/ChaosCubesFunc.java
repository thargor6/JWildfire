
/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2022 Andreas Maschke

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
import static org.jwildfire.base.mathlib.MathLib.*;

public class ChaosCubesFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  // Parameter definitions
  private static final String PARAM_MODE = "mode";
  private static final String PARAM_MODE7_A = "mode7_A";
  private static final String PARAM_MODE7_B = "mode7_B";
  private static final String PARAM_MAX_ITERATIONS = "depth";
  private static final String PARAM_TWIST_X = "twistX";
  private static final String PARAM_TWIST_Y = "twistY";
  private static final String PARAM_TWIST_Z = "twistZ";
  private static final String PARAM_SCALE_X = "scaleX";
  private static final String PARAM_SCALE_Y = "scaleY";
  private static final String PARAM_SCALE_Z = "scaleZ";
  private static final String PARAM_OFFSET = "offset";
  private static final String PARAM_ROT_X = "rotX";
  private static final String PARAM_ROT_Y = "rotY";
  private static final String PARAM_ROT_Z = "rotZ";
  private static final String PARAM_INVERT = "invert";
  private static final String PARAM_JULIA = "julia";
  private static final String PARAM_COLOR_MODE = "colorMode";
  private static final String PARAM_COLOR_SPEED = "colorSpeed";
  private static final String PARAM_SPHERE_INVERT = "sphereInvert";
  private static final String PARAM_SPHERE_RADIUS = "sphereRadius";

  private static final String RESSOURCE_DESCRIPTION = "description";

  // Parameter arrays
  private static final String[] paramNames = {PARAM_MODE, PARAM_MODE7_A, PARAM_MODE7_B, PARAM_MAX_ITERATIONS, PARAM_TWIST_X, PARAM_TWIST_Y, PARAM_TWIST_Z, PARAM_SCALE_X, PARAM_SCALE_Y, PARAM_SCALE_Z, PARAM_OFFSET, PARAM_ROT_X, PARAM_ROT_Y, PARAM_ROT_Z, PARAM_INVERT, PARAM_JULIA, PARAM_COLOR_MODE, PARAM_COLOR_SPEED, PARAM_SPHERE_INVERT, PARAM_SPHERE_RADIUS};
  private static final String[] ressourceNames = {RESSOURCE_DESCRIPTION};

  // Member variables
  private int mode = 0;
  private int mode7_A = 0;
  private int mode7_B = 1;
  private int max_iterations = 5;
  private double twistX = 0.0;
  private double twistY = 0.0;
  private double twistZ = 0.0;
  private double scaleX = 0.3333333;
  private double scaleY = 0.3333333;
  private double scaleZ = 0.3333333;
  private double offset = 1.0;
  private double rotX = 0.0;
  private double rotY = 0.0;
  private double rotZ = 0.0;
  private int invert = 0;
  private int julia = 0;
  private int colorMode = 0;
  private double colorSpeed = 1.0;
  private int sphereInvert = 0;
  private double sphereRadius = 1.0;

  private String description = "org.jwildfire.create.tina.variation.reference.ReferenceFile chaosCubes.txt";

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    XYZPoint p = new XYZPoint();
    p.x = pAffineTP.x;
    p.y = pAffineTP.y;
    p.z = pAffineTP.z;

    double rX = Math.toRadians(rotX);
    double rY = Math.toRadians(rotY);
    double rZ = Math.toRadians(rotZ);

    if (rX != 0) rotateX(p, rX);
    if (rY != 0) rotateY(p, rY);
    if (rZ != 0) rotateZ(p, rZ);

    XYZPoint c = new XYZPoint();
    c.x = p.x;
    c.y = p.y;
    c.z = p.z;
    
    int last_ix = 0, last_iy = 0, last_iz = 0;
    int last_v_index = 0;
    double pathLength = 0.0;

    for (int i = 0; i < max_iterations; i++) {
        int current_mode = mode;
        if(current_mode == 7) { // If hybrid mode, pick one of the two sub-modes for this iteration
            current_mode = (pContext.random() < 0.5) ? mode7_A : mode7_B;
        }

        switch(current_mode) {
            case 0: case 1: case 2: case 3: case 4: case 5:
            {
                int ix, iy, iz;
                do {
                    ix = (int)(pContext.random() * 3) - 1;
                    iy = (int)(pContext.random() * 3) - 1;
                    iz = (int)(pContext.random() * 3) - 1;
                    
                    boolean is_valid_cube = false;
                    switch(current_mode) {
                      case 0: int hcm = (ix==0?1:0)+(iy==0?1:0)+(iz==0?1:0); is_valid_cube = (invert==0)?(hcm<2):(hcm>=2); break;
                      case 1: int hcv = Math.abs(ix)+Math.abs(iy)+Math.abs(iz); is_valid_cube = (invert==0)?(hcv<=1):(hcv>1); break;
                      case 2: boolean icc = (ix==0&&iy==0&&iz==0); is_valid_cube = (invert==0)?!icc:icc; break;
                      case 3: int z = (ix==0?1:0)+(iy==0?1:0)+(iz==0?1:0); is_valid_cube = (invert==0)?(z<2||(ix==0&&iy==0&&iz==0)):(z>=2&&!(ix==0&&iy==0&&iz==0)); break;
                      case 4: is_valid_cube = (invert==0)?(Math.abs(ix)+Math.abs(iy)+Math.abs(iz)==3):(Math.abs(ix)+Math.abs(iy)+Math.abs(iz)!=3); break;
                      case 5: int nz = (ix!=0?1:0)+(iy!=0?1:0)+(iz!=0?1:0); is_valid_cube = (invert==0)?(nz==2):(nz!=2); break;
                    }
                    if (is_valid_cube) { last_ix=ix; last_iy=iy; last_iz=iz; break; }
                } while (true);

                p.x = p.x * scaleX - ix * offset * (1.0 - scaleX);
                p.y = p.y * scaleY - iy * offset * (1.0 - scaleY);
                p.z = p.z * scaleZ - iz * offset * (1.0 - scaleZ);
                
                if (julia > 0) { p.x += c.x; p.y += c.y; p.z += c.z; }
                break;
            }
            case 6: // Sierpinski Pyramid
            {
                XYZPoint[] vertices = new XYZPoint[5];
                vertices[0] = new XYZPoint(); vertices[0].x=offset; vertices[0].y=offset; vertices[0].z=0;
                vertices[1] = new XYZPoint(); vertices[1].x=offset; vertices[1].y=-offset; vertices[1].z=0;
                vertices[2] = new XYZPoint(); vertices[2].x=-offset; vertices[2].y=-offset; vertices[2].z=0;
                vertices[3] = new XYZPoint(); vertices[3].x=-offset; vertices[3].y=offset; vertices[3].z=0;
                vertices[4] = new XYZPoint(); vertices[4].x=0; vertices[4].y=0; vertices[4].z=offset*scaleY*0.5;
                
                last_v_index = (int)(pContext.random() * 5);
                XYZPoint v = vertices[last_v_index];
                
                p.x = (p.x + v.x) * 0.5;
                p.y = (p.y + v.y) * 0.5;
                p.z = (p.z + v.z) * 0.5;
                break;
            }
        }
        
        if (twistX != 0.0) rotateX(p, p.x * twistX);
        if (twistY != 0.0) rotateY(p, p.y * twistY);
        if (twistZ != 0.0) rotateZ(p, p.z * twistZ);

        pathLength += Math.sqrt(p.x*p.x + p.y*p.y + p.z*p.z);
    }
    
    if (sphereInvert > 0) {
        double r2 = p.x*p.x + p.y*p.y + p.z*p.z;
        if (r2 > 1.0E-6) {
            double factor = (sphereRadius * sphereRadius) / r2;
            p.x *= factor;
            p.y *= factor;
            p.z *= factor;
        }
    }

    pVarTP.x += p.x * pAmount;
    pVarTP.y += p.y * pAmount;
    pVarTP.z += p.z * pAmount;

    if (colorMode > 0) {
        double calculatedColor = 0.0;
        switch(colorMode) {
          case 1: calculatedColor = (p.x/offset + p.y/offset + p.z/offset) / 3.0; break;
          case 2: if (mode == 6) { calculatedColor = last_v_index / 4.0; } else { calculatedColor = (last_ix+1+(last_iy+1)*3+(last_iz+1)*9)/26.0; } break;
          case 3: calculatedColor = Math.sqrt(p.x*p.x+p.y*p.y+p.z*p.z)/offset; break;
          case 4: calculatedColor = pathLength/(max_iterations*offset); break;
          case 5: double angleXY=Math.atan2(p.y,p.x); double angleXZ=Math.atan2(p.z,p.x); calculatedColor=(angleXY+angleXZ+2.0*Math.PI)/(4.0*Math.PI); break;
        }
        
        calculatedColor *= colorSpeed;
        
        calculatedColor = fmod(fmod(calculatedColor, 1.0) + 1.0, 1.0);
        pVarTP.color += (calculatedColor - pVarTP.color) * pAmount;
    }
  }

  private void rotateX(XYZPoint p, double angle) { double y=p.y; double z=p.z; p.y=cos(angle)*y-sin(angle)*z; p.z=sin(angle)*y+cos(angle)*z; }
  private void rotateY(XYZPoint p, double angle) { double x=p.x; double z=p.z; p.x=cos(angle)*x+sin(angle)*z; p.z=-sin(angle)*x+cos(angle)*z; }
  private void rotateZ(XYZPoint p, double angle) { double x=p.x; double y=p.y; p.x=cos(angle)*x-sin(angle)*y; p.y=sin(angle)*x+cos(angle)*y; }

  @Override
  public String[] getParameterNames() { return paramNames; }
  
  @Override
  public Object[] getParameterValues() {
    return new Object[] {mode, mode7_A, mode7_B, max_iterations, twistX, twistY, twistZ, scaleX, scaleY, scaleZ, offset, rotX, rotY, rotZ, invert, julia, colorMode, colorSpeed, sphereInvert, sphereRadius};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"fractal_mode", "mode7_A", "mode7_B", "depth", "twist_x", "twist_y", "twist_z", "scale_x", "scale_y", "scale_z", "offset", "rotX", "rotY", "rotZ", "invert", "julia_mode", "color_mode", "color_speed", "sphere_invert", "sphere_radius"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_MODE.equalsIgnoreCase(pName)) mode = (int) Math.max(0, Math.min(pValue, 7));
    else if (PARAM_MODE7_A.equalsIgnoreCase(pName)) mode7_A = (int) Math.max(0, Math.min(pValue, 5));
    else if (PARAM_MODE7_B.equalsIgnoreCase(pName)) mode7_B = (int) Math.max(0, Math.min(pValue, 5));
    else if (PARAM_MAX_ITERATIONS.equalsIgnoreCase(pName)) max_iterations = (int) Math.max(1, Math.min(pValue, 15));
    else if (PARAM_TWIST_X.equalsIgnoreCase(pName)) twistX = pValue;
    else if (PARAM_TWIST_Y.equalsIgnoreCase(pName)) twistY = pValue;
    else if (PARAM_TWIST_Z.equalsIgnoreCase(pName)) twistZ = pValue;
    else if (PARAM_SCALE_X.equalsIgnoreCase(pName)) scaleX = pValue;
    else if (PARAM_SCALE_Y.equalsIgnoreCase(pName)) scaleY = pValue;
    else if (PARAM_SCALE_Z.equalsIgnoreCase(pName)) scaleZ = pValue;
    else if (PARAM_OFFSET.equalsIgnoreCase(pName)) offset = pValue;
    else if (PARAM_ROT_X.equalsIgnoreCase(pName)) rotX = pValue;
    else if (PARAM_ROT_Y.equalsIgnoreCase(pName)) rotY = pValue;
    else if (PARAM_ROT_Z.equalsIgnoreCase(pName)) rotZ = pValue;
    else if (PARAM_INVERT.equalsIgnoreCase(pName)) invert = pValue > 0.5 ? 1 : 0;
    else if (PARAM_JULIA.equalsIgnoreCase(pName)) julia = pValue > 0.5 ? 1 : 0;
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) colorMode = (int) Math.max(0, Math.min(pValue, 5));
    else if (PARAM_COLOR_SPEED.equalsIgnoreCase(pName)) colorSpeed = pValue;
    else if (PARAM_SPHERE_INVERT.equalsIgnoreCase(pName)) sphereInvert = pValue > 0.5 ? 1 : 0;
    else if (PARAM_SPHERE_RADIUS.equalsIgnoreCase(pName)) sphereRadius = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] {description.getBytes()};
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_DESCRIPTION.equalsIgnoreCase(pName)) {
      return RessourceType.REFERENCE;
    }
    else throw new IllegalArgumentException(pName);
  }
  
  @Override
  public void randomize() {
  	mode = (int) (Math.random() * 8);
  	mode7_A = (int) (Math.random() * 6);
  	mode7_B = (int) (Math.random() * 6);
  	max_iterations = (int) (Math.random() * 8 + 3);
  	if (Math.random() < 0.75) twistX = 0.0;
  	else twistX = Math.random() * 6.0 - 3.0;
  	if (Math.random() < 0.75) twistY = 0.0;
  	else twistY = Math.random() * 6.0 - 3.0;
  	if (Math.random() < 0.75) twistZ = 0.0;
  	else twistZ = Math.random() * 6.0 - 3.0;
  	scaleX = Math.random() * 0.25 + 0.25;
  	scaleY = Math.random() * 0.25 + 0.25;
  	scaleZ = Math.random() * 0.25 + 0.25;
  	offset = Math.random() * 1.5 + 0.5;
  	if (Math.random() < 0.75) rotX = 0.0;
  	else rotX = Math.random() * 180.0 + 90.0;
  	if (Math.random() < 0.75) rotY = 0.0;
  	else rotY = Math.random() * 180.0 + 90.0;
  	if (Math.random() < 0.75) rotZ = 0.0;
  	else rotZ = Math.random() * 180.0 + 90.0;
  	invert = mode == 2 ? 0 : (int) (Math.random() * 2);
  	colorMode = (int) (Math.random() * 6);
  	colorSpeed = Math.random() * 8.0 - 4.0;
  	sphereInvert = (int) (Math.random() * 2);
  	sphereRadius = Math.random() * 1.5 + 0.5;
  }
  
  @Override
  public String getName() { return "chaosCubes"; }
  
  @Override
  public VariationFuncType[] getVariationTypes() { 
  	return new VariationFuncType[] {VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_DC}; 
  	}
}
