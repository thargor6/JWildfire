package org.jwildfire.create.tina.variation;


import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.fmod;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqr;

import org.jwildfire.base.mathlib.Complex;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class LaceFunc extends SimpleVariationFunc {

		/**
		 * Lace variation
		 * @author Jesus Sosa
		 * @date November 4, 2017
		 * based on a work of:
		 * http://paulbourke.net/fractals/lace/lace.c
		 */

			  private static final long serialVersionUID = 1L;

			  
			  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) 
			  {
		      // Lace variation Reference:
              //	http://paulbourke.net/fractals/lace/lace.c

				double x = 0.5, y = 0.75;
				

                double w=0.0;
				double r=2.0;
				

				double r0=Math.sqrt(pAffineTP.x*pAffineTP.x + pAffineTP.y*pAffineTP.y);
                double weight= Math.random();
                
               if(weight>0.75)
                {
                	w = Math.atan2(pAffineTP.y, pAffineTP.x- 1.0);
                    y =  -r0*cos(w)/r + 1.0;
                    x =  -r0*sin(w)/r;
                }
               else if(weight>0.50)
               {
            	   w = Math.atan2(pAffineTP.y-Math.sqrt(3.0)/2.0 , pAffineTP.x+0.5);
                    y = -r0 * cos(w) / r - 0.5;
                    x = -r0 * sin(w) / r + Math.sqrt(3.0)/2.0;
               }
               else if (weight>0.25)
               {
                    w = Math.atan2(pAffineTP.y + Math.sqrt(3.0)/2.0 , pAffineTP.x+0.5);
                    y = -r0 * cos(w) / r - 0.5;
                    x = -r0 * sin(w) / r - Math.sqrt(3.0)/2.0;
               }
               else {
                    w = Math.atan2(pAffineTP.y,pAffineTP.x);
                    y = -r0 * cos(w) / r;
                    x = -r0 * sin(w) / r;
                }
                
				pVarTP.x += x*pAmount;
				pVarTP.y += y*pAmount;


		//	    pVarTP.color = fmod(fabs( (sqr(pVarTP.x) + sqr(pVarTP.y ))), 1.0);
			  }
			
			  public String getName() {
			    return "lace_js";
			  }
}