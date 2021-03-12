package org.jwildfire.create.tina.variation;


import java.util.Random;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class HopalongFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_RANDOM = "random";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_STARTX = "startx";
  private static final String PARAM_STARTY = "starty";

  // Autor: Jesus Sosa
  // Hopalong attractor or Martin attractor
  // Reference& credits: https://www.youtube.com/watch?v=JhHugpABjDo

  private static final String[] paramNames = {PARAM_RANDOM,PARAM_A, PARAM_B, PARAM_C,PARAM_STARTX, PARAM_STARTY};

  double a = .75;
  double b = 0.50;
  double c = 0.25;

  double x0 = 0.0;
  double y0 = 0.0;

  int seed=1;
  Random  randomize=new Random((long)seed);
  
  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

	  
	  double x1 = y0 - Math.signum(x0) * Math.sqrt(Math.abs(b * x0 - c));
      double y1 = a - x0;
    
    pVarTP.x += pAmount * x1;
    pVarTP.y += pAmount * y1;
    x0=x1;
    y0=y1;

  }

  public String getName() {
    return "hopalong";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{seed,a, b, c, x0, y0};
  }

  public void setParameter(String pName, double pValue) {
    if(pName.equalsIgnoreCase(PARAM_RANDOM)) {
		seed = (int)Tools.limitValue(pValue, 0 , 10000000);
		randomize=new Random(seed);
		   a=-1.0 + 2.0 * randomize.nextDouble();
		   b=-1.0 + 2.0 * randomize.nextDouble();
		   c=-1.0 + 2.0 * randomize.nextDouble();
		   x0=-1.0 + 2.0 * randomize.nextDouble();
		   y0=-1.0 + 2.0 * randomize.nextDouble();
    }
	  else if (pName.equalsIgnoreCase(PARAM_A)) {
      a = Tools.limitValue(pValue, -1.0 , 1.0);
    } else if (pName.equalsIgnoreCase(PARAM_B)) {
      b = Tools.limitValue(pValue, -1.0 , 1.0);
    }  
    else if (pName.equalsIgnoreCase(PARAM_C)) {
        c = Tools.limitValue(pValue, -1.0 , 1.0);
      }
    else if (pName.equalsIgnoreCase(PARAM_STARTX)) {
      x0 = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_STARTY)) {
      y0 = pValue;
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

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_BASE_SHAPE };
  }

}
