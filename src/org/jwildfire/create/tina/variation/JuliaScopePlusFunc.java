/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2025 Andreas Maschke
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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

import java.util.Set;
import java.util.EnumSet;

public class JuliaScopePlusFunc extends VariationFunc{
    private static final long serialVersionUID = 1L;

    private static final String PARAM_POWER = "power";
    private static final String PARAM_DIST = "dist";
    private static final String PARAM_CX = "cx";
    private static final String PARAM_CY = "cy";
    private static final String PARAM_MIX_POWER_SIGN = "mixPowerSign";
    private static final String PARAM_COLOR_MODE = "colorMode";
    private static final String PARAM_COLOR_SPEED = "colorSpeed";
    private static final String PARAM_THETA_SIGN_MODE = "thetaSignMode";
    private static final String PARAM_ANGULAR_OFFSET = "angularOffset";
    private static final String PARAM_SWIRL_FACTOR = "swirlFactor";
    private static final String PARAM_FIXED_BRANCH = "fixedBranch";
    private static final String PARAM_RADIAL_OFFSET = "radialOffset";
    private static final String PARAM_ALT_POWER_SCALE = "altPowerScale";
    private static final String PARAM_ALT_POWER_ROTATE = "altPowerRotate";
    private static final String PARAM_RND_TERM_MODE = "rndTermMode";
    private static final String PARAM_MANDELBROT_LIKE = "mandelbrotLike";

    private static final String RESSOURCE_DESCRIPTION = "description";

    private static final String[] paramNames = {
            PARAM_POWER, PARAM_DIST, PARAM_CX, PARAM_CY,
            PARAM_MIX_POWER_SIGN, PARAM_COLOR_MODE, PARAM_COLOR_SPEED,
            PARAM_THETA_SIGN_MODE, PARAM_ANGULAR_OFFSET, PARAM_SWIRL_FACTOR,
            PARAM_FIXED_BRANCH, PARAM_RADIAL_OFFSET,
            PARAM_ALT_POWER_SCALE, PARAM_ALT_POWER_ROTATE,
            PARAM_RND_TERM_MODE, PARAM_MANDELBROT_LIKE
    };
    private static final String[] ressourceNames = {RESSOURCE_DESCRIPTION};

    // Fields for parameters
    private int power = genRandomPower();
    private double dist = 1.0;
    private double cX = 0.0;
    private double cY = 0.0;
    private double mixPowerSign = 0.0;
    private int colorMode = 0;
    private double colorSpeed = 1.0;
    private int thetaSignMode = 0;
    private double angularOffset = 0.0;
    private double swirlFactor = 0.0;
    private double fixedBranch = -1.0;
    private double radialOffset = 0.0;
    private double altPowerScale = 1.0;
    private double altPowerRotate = 0.0;
    private int rndTermMode = 0; // 0=always+, 1=always-, 2=by_rnd_parity, 3=random
    private int mandelbrotLike = 0; // 0=use cX,cY; 1=use pAffineTP.x,y

    private String description = "org.jwildfire.create.tina.variation.reference.ReferenceFile juliascopePlus.txt";

    private int absPowerForRandomBranches;

    @Override
    public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
        if (this.power == 0) {
            this.absPowerForRandomBranches = 2;
        } else {
            this.absPowerForRandomBranches = Math.max(1, iabs(Tools.FTOI(this.power)));
        }
    }

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        transformFunction(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    }

    public void transformFunction(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        int effectivePower = this.power;
        double currentCPower;
        boolean applyAltTransform = false; 
        boolean signWasFlippedForColor = false; 

        int originalPowerSign = (this.power == 0) ? 1 : Integer.signum(this.power);

        if (this.power != 0 && this.mixPowerSign >= 0.5) {
            int baseAbsPower = this.absPowerForRandomBranches;
            if (pContext.random(2) == 0) { 
                effectivePower = baseAbsPower * originalPowerSign;
                signWasFlippedForColor = (effectivePower < 0);
            } else { 
                effectivePower = -baseAbsPower * originalPowerSign;
                applyAltTransform = true; 
                signWasFlippedForColor = (effectivePower < 0);
            }
        } else {
             effectivePower = this.power; 
             signWasFlippedForColor = (effectivePower < 0);
        }
        
        if (effectivePower == 0) {
            // Determine additive components based on mandelbrotLike mode
            double addX = (this.mandelbrotLike >= 1) ? pAffineTP.x : cX;
            double addY = (this.mandelbrotLike >= 1) ? pAffineTP.y : cY;
            pVarTP.x += pAmount * (pAffineTP.x + addX); // Fallback for power 0 might use linear + addX
            pVarTP.y += pAmount * (pAffineTP.y + addY);

            if (pContext.isPreserveZCoordinate()) {
                pVarTP.z += pAmount * pAffineTP.z;
            }
            return;
        }
        
        currentCPower = this.dist / effectivePower * 0.5;

        int rnd;
        int currentFixedBranchVal = (int)round(this.fixedBranch);
        int numBranches = this.absPowerForRandomBranches > 0 ? this.absPowerForRandomBranches : 1;
        if (currentFixedBranchVal >= 0 && currentFixedBranchVal < numBranches) {
            rnd = currentFixedBranchVal;
        } else {
            rnd = pContext.random(numBranches);
        }
        
        double theta = atan2(pAffineTP.y, pAffineTP.x);
        double a;

        double thetaTermSign = 1.0;
        if (thetaSignMode == 0) { if ((rnd & 1) != 0) thetaTermSign = -1.0; }
        else if (thetaSignMode == 1) { thetaTermSign = 1.0; }
        else if (thetaSignMode == 2) { thetaTermSign = -1.0; }
        else if (thetaSignMode == 3) { thetaTermSign = (pContext.random(2) == 0) ? 1.0 : -1.0; }

        double rndTermComponentSign = 1.0;
        if (rndTermMode == 1) { rndTermComponentSign = -1.0; } // Always -
        else if (rndTermMode == 2) { if ((rnd & 1) != 0) rndTermComponentSign = -1.0; } // Based on rnd parity
        else if (rndTermMode == 3) { rndTermComponentSign = (pContext.random(2) == 0) ? 1.0 : -1.0; } // Random independent
        // if rndTermMode == 0, rndTermComponentSign remains 1.0 (always +)

        a = ( (rndTermComponentSign * 2.0 * M_PI * rnd) + (thetaTermSign * theta) ) / effectivePower;
        
        if (this.swirlFactor != 0.0) {
            double original_r = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
            if (original_r > 0.00001) { 
                a += this.swirlFactor * log(1.0 + original_r);
            }
        }
        a += this.angularOffset;
        
        double sina = sin(a);
        double cosa = cos(a);

        double r_base_sq = sqr(pAffineTP.x) + sqr(pAffineTP.y);
        double r_component_magnitude = pow(max(0.0, r_base_sq), currentCPower) + this.radialOffset;
        
        double dx_component = r_component_magnitude * cosa;
        double dy_component = r_component_magnitude * sina;

        if (applyAltTransform) {
            dx_component *= this.altPowerScale;
            dy_component *= this.altPowerScale;

            if (this.altPowerRotate != 0.0) {
                double cosRot = cos(this.altPowerRotate);
                double sinRot = sin(this.altPowerRotate);
                double temp_dx = dx_component * cosRot - dy_component * sinRot;
                dy_component = dx_component * sinRot + dy_component * cosRot;
                dx_component = temp_dx;
            }
        }

        // Apply additive term based on mandelbrotLike mode
        double addX, addY;
        if (this.mandelbrotLike >= 1) {
            addX = pAffineTP.x;
            addY = pAffineTP.y;
        } else {
            addX = cX;
            addY = cY;
        }

        pVarTP.x += pAmount * (dx_component + addX);
        pVarTP.y += pAmount * (dy_component + addY);
        
        // Color Logic
        if (colorMode != 0) {
            double calculatedColorValue = 0.0;
            switch (colorMode) {
                case 1: calculatedColorValue = a / (2.0 * M_PI); break;
                case 2: calculatedColorValue = log(1.00001 + max(0,r_component_magnitude)); break;
                case 3: calculatedColorValue = (theta + M_PI) / (2.0 * M_PI); break;
                case 4:
                    if (numBranches > 0) { 
                        calculatedColorValue = (double)rnd / numBranches;
                    } else { calculatedColorValue = 0.0; }
                    break;
                case 5: 
                    if (this.mixPowerSign >= 0.5 && this.power != 0) {
                        calculatedColorValue = signWasFlippedForColor ? 0.75 : 0.25; 
                    } else { 
                        calculatedColorValue = (this.power >= 0) ? 0.25 : 0.75;
                    }
                    break;
            }
            pVarTP.color = frac(calculatedColorValue * this.colorSpeed);
        }
        
        if (pContext.isPreserveZCoordinate()) {
            pVarTP.z += pAmount * pAffineTP.z;
        }
    }

    @Override
    public String[] getParameterNames() {
        return paramNames;
    }

    @Override
    public Object[] getParameterValues() {
        return new Object[]{power, dist, cX, cY, mixPowerSign, colorMode, colorSpeed,
                            thetaSignMode, angularOffset, swirlFactor,
                            fixedBranch, radialOffset,
                            altPowerScale, altPowerRotate,
                            rndTermMode, mandelbrotLike};
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_POWER.equalsIgnoreCase(pName)) power = (int) round(pValue);
        else if (PARAM_DIST.equalsIgnoreCase(pName)) dist = pValue;
        else if (PARAM_CX.equalsIgnoreCase(pName)) cX = pValue;
        else if (PARAM_CY.equalsIgnoreCase(pName)) cY = pValue;
        else if (PARAM_MIX_POWER_SIGN.equalsIgnoreCase(pName)) mixPowerSign = pValue;
        else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) colorMode = limitIntVal(Tools.FTOI(pValue), 0, 5);
        else if (PARAM_COLOR_SPEED.equalsIgnoreCase(pName)) colorSpeed = pValue;
        else if (PARAM_THETA_SIGN_MODE.equalsIgnoreCase(pName)) thetaSignMode = limitIntVal(Tools.FTOI(pValue), 0, 3);
        else if (PARAM_ANGULAR_OFFSET.equalsIgnoreCase(pName)) angularOffset = pValue;
        else if (PARAM_SWIRL_FACTOR.equalsIgnoreCase(pName)) swirlFactor = pValue;
        else if (PARAM_FIXED_BRANCH.equalsIgnoreCase(pName)) fixedBranch = pValue;
        else if (PARAM_RADIAL_OFFSET.equalsIgnoreCase(pName)) radialOffset = pValue;
        else if (PARAM_ALT_POWER_SCALE.equalsIgnoreCase(pName)) altPowerScale = pValue; 
        else if (PARAM_ALT_POWER_ROTATE.equalsIgnoreCase(pName)) altPowerRotate = pValue;
        else if (PARAM_RND_TERM_MODE.equalsIgnoreCase(pName)) rndTermMode = limitIntVal(Tools.FTOI(pValue), 0, 3);
        else if (PARAM_MANDELBROT_LIKE.equalsIgnoreCase(pName)) mandelbrotLike = limitIntVal(Tools.FTOI(pValue), 0, 1);
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
    public String getName() {
        return "juliascopePlus";
    }

    private int genRandomPower() {
        int res = (int) (Math.random() * 4.0 + 2.0);
        return Math.random() < 0.5 ? res : -res;
    }
    
    @Override
    public void randomize() {
        power = (int) (Math.random() * 8 + 2); 
        if (Math.random() < 0.5) power *= -1;
        if (power == 0) power = 2;

        double rRand = Math.random();
        if (rRand < 0.4) dist = Math.random() * 0.5 + 0.75;
        else if (rRand < 0.8) dist = Math.random() * 3.3 + 0.2;
        else dist = 1.0;
        if (Math.random() < 0.4) dist *= -1;

        cX = (Math.random() * 4.0) - 2.0;
        cY = (Math.random() * 4.0) - 2.0;
        mixPowerSign = (Math.random() < 0.5) ? 0.0 : 1.0;
        colorMode = Tools.FTOI(Math.random() * 6.0); 
        colorSpeed = 0.25 + Math.random() * 2.75;
        thetaSignMode = Tools.FTOI(Math.random() * 4.0); 
        angularOffset = (Math.random() * 2.0 - 1.0) * M_PI; 
        swirlFactor = (Math.random() * 1.0 - 0.5) * 0.5; 
        fixedBranch = -1.0; 
        radialOffset = (Math.random() * 0.4 - 0.2); 
        altPowerScale = 1.0 + (Math.random() - 0.5) * 0.5; 
        altPowerRotate = (Math.random() - 0.5) * M_PI * 0.25; 
        rndTermMode = Tools.FTOI(Math.random() * 4.0); // Modes 0-3
        mandelbrotLike = (Math.random() < 0.25) ? 1 : 0; // 25% chance for Mandelbrot-like
    }

    @Override
    public VariationFuncType[] getVariationTypes() {
        return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_DC};
    }

}