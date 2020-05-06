package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import org.jwildfire.base.Tools;

public class ThreeplyFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;


  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";


  private static final String[] paramNames = {PARAM_A, PARAM_B,PARAM_C};


  double a = -55.0, b = -1.0, c = -42.0;
  
  double x0=0.0;
  double y0=0.0;


  
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Reference:
    //  http://www.jamesh.id.au/fractals/orbit/threeply.html

	  
	  int sgn = (x0>0)?1:(x0<0)?-1:0;
	    
	 double xn= y0 - sgn*Math.abs(Math.sin(x0)*Math.cos(b)+c-x0*Math.sin(a+b+c));  
	 double yn= a-x0;   

    pVarTP.x = 0.001* xn * pAmount;
    pVarTP.y = 0.001* yn * pAmount;
    
    x0=xn;
    y0=yn;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  public String getName() {
    return "threeply";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{ a, b,c };
  }

  public void setParameter(String pName, double pValue) {
	 if (pName.equalsIgnoreCase(PARAM_A)) {
      a = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_B)) {
    	b = pValue;
    }
    else if (pName.equalsIgnoreCase(PARAM_C)) {
	      	c = pValue;
	}
    else
      throw new IllegalArgumentException(pName);
  }
}
