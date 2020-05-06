package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.DC_CodeFunc.randomizeCode;

import js.glsl.vec2;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import java.util.Random;

import org.jwildfire.base.Tools;

public class GingerBreadManFunc extends SimpleVariationFunc {

  private static final long serialVersionUID = 1L;


  double x0=-0.1;
  double y0=0.0;

  
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //  Reference:
    //  http://www.jamesh.id.au/fractals/orbit/gingerbread.html 
	  
    double xn=1 - y0 + Math.abs(x0);
    double yn=x0;
    

    pVarTP.x = xn* pAmount;
    pVarTP.y = yn* pAmount;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
    
    x0=xn;
    y0=yn;

  }

  public String getName() {
    return "gingerbread_man";
  }

}
