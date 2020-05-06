package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.DC_CodeFunc.randomizeCode;

import js.glsl.vec2;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import java.util.Random;

import org.jwildfire.base.Tools;

public class GumowskiMiraFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_RANDOM = "random";
  private static final String PARAM_STEP = "step";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";


  private static final String[] paramNames = {PARAM_RANDOM,PARAM_STEP,PARAM_A, PARAM_B};


  int seed=1;
  int currseed=seed;
  Random  randomize=new Random((long)seed);
  double x0=5.0;
  double y0=5.0;
   double step=0.001;
  //int random=0;


  double a=0.000001,b=0.05,m=-0.080;
  
  
  double mira(double x)
  {
	  double xx=x*x;
	  return m*x + ((2.0*(1.0-m)*xx)/(1.0 + xx));
  }
  
  
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Reference:
    // https://www.openprocessing.org/sketch/195425/
	  

    double xn = y0 + (a*(1.0-(b*y0*y0))*y0) + mira(x0);
    double yn= -x0 + mira(xn);

    pVarTP.x = xn* pAmount;
    pVarTP.y = yn* pAmount;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
    
    x0=xn;
    y0=yn;

  }

  public String getName() {
    return "gumowski_mira";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{seed,step, a, b};
  }

  public void setParameter(String pName, double pValue) {
/*	 if (pName.equalsIgnoreCase(PARAM_RANDOM)) {
			seed = (int)Tools.limitValue(pValue, 0 , 10000000);
			randomize=new Random((long)seed);
			step += randomize.nextDouble()*0.1;
			if(step>1.0)
				step--;
		 // step+=step;
	      m+=step;
	}  */
		 if (pName.equalsIgnoreCase(PARAM_RANDOM)) {
				seed = (int)Tools.limitValue(pValue, 0 , 10000000);
				randomize=new Random(seed);
				if(seed>currseed)
                	step *= 2.0;
                else
                	step/=2.0;	
                if(step>1.0)
                	step-=1.0;
		        m+=step;
		        currseed=seed;
		}  
	else if (pName.equalsIgnoreCase(PARAM_STEP)) {
	      step = Tools.limitValue(pValue, 0.0, 1.0);
	      m+=step;
	}
	else if (pName.equalsIgnoreCase(PARAM_A)) {
	   randomize=new Random(seed++);
	   a=	1.e-6 + (1e-6-1.e-8)* randomize.nextDouble();
      // a = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_B)) {
      b = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }
  
	@Override
	public boolean dynamicParameterExpansion() {
		return true;
	}

	@Override
	public boolean dynamicParameterExpansion(String pName) {
		// preset_id doesn't really expand parameters, but it changes them; this will make them refresh
		return true;
	}	
}
