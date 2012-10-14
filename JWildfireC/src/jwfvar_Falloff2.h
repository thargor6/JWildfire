/*
 JWildfireC - an external C-based fractal-flame-renderer for JWildfire
 Copyright (C) 2012 Andreas Maschke

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

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class Falloff2Func: public Variation {
public:
	Falloff2Func() {
	  scatter = 1.0;
	  mindist = 0.5;
	  mul_x = 1.0;
	  mul_y = 1.0;
	  mul_z = 0.0;
	  mul_c = 0.0;
	  x0 = 0.0;
	  y0 = 0.0;
	  z0 = 0.0;
	  invert = 0;
	  type = 0;
		initParameterNames(11, "scatter", "mindist", "mul_x", "mul_y", "mul_z", "mul_c", "x0", "y0", "z0", "invert", "type");
	}

	const char* getName() const {
		return "falloff2";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "scatter") == 0) {
			scatter = pValue;
      if (scatter < 1.0e-6)
        scatter = 1.0e-6;
		}
		else if (strcmp(pName, "mindist") == 0) {
			mindist = pValue;
      if (mindist < 0.0)
        mindist = 0.0;
		}
		else if (strcmp(pName, "mul_x") == 0) {
			mul_x = pValue;
		}
		else if (strcmp(pName, "mul_y") == 0) {
			mul_y = pValue;
		}
		else if (strcmp(pName, "mul_z") == 0) {
			mul_z = pValue;
		}
		else if (strcmp(pName, "mul_c") == 0) {
			mul_c = pValue;
		}
		else if (strcmp(pName, "x0") == 0) {
			x0 = pValue;
		}
		else if (strcmp(pName, "y0") == 0) {
			y0 = pValue;
		}
		else if (strcmp(pName, "z0") == 0) {
			z0 = pValue;
		}
		else if (strcmp(pName, "invert") == 0) {
			invert = FTOI(pValue);
		}
		else if (strcmp(pName, "type") == 0) {
			type = FTOI(pValue);
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* falloff2 by Xyrus02 */
    switch (type) {
      case 1:
        calcFunctionRadial(pContext, pXForm, pAffineTP->x, pAffineTP->y, pAffineTP->z, pAffineTP, pVarTP, pAmount);
        break;
      case 2:
        calcFunctionGaussian(pContext, pXForm, pAffineTP->x, pAffineTP->y, pAffineTP->z, pAffineTP, pVarTP, pAmount);
        break;
      default:
        calcFunction(pContext, pXForm, pAffineTP->x, pAffineTP->y, pAffineTP->z, pAffineTP, pVarTP, pAmount);
        break;
    }
	}

	Falloff2Func* makeCopy() {
		return new Falloff2Func(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _rmax = 0.04 * scatter;
	}

protected:

void calcFunctionRadial(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pIn_x, JWF_FLOAT pIn_y, JWF_FLOAT pIn_z, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
  JWF_FLOAT r_in = JWF_SQRT(JWF_SQR(pIn_x) + JWF_SQR(pIn_y) + JWF_SQR(pIn_z)) + 1e-6;
  JWF_FLOAT d = JWF_SQRT(JWF_SQR(pIn_x - x0) + JWF_SQR(pIn_y - y0) + JWF_SQR(pIn_z - z0));
  if (invert != 0)
    d = 1 - d;
  if (d < 0)
    d = 0;
  d = (d - mindist) * _rmax;
  if (d < 0)
    d = 0;

  JWF_FLOAT sigma = JWF_ASIN(pIn_z / r_in) + mul_z * pContext->randGen->random() * d;
  JWF_FLOAT phi = JWF_ATAN2(pIn_y, pIn_x) + mul_y * pContext->randGen->random() * d;
  JWF_FLOAT r = r_in + mul_x * pContext->randGen->random() * d;

  JWF_FLOAT sins, coss;
  JWF_SINCOS(sigma, &sins, &coss);

  JWF_FLOAT sinp, cosp;
  JWF_SINCOS(phi, &sinp, &cosp);

  pVarTP->x += pAmount * (r * coss * cosp);
  pVarTP->y += pAmount * (r * coss * sinp);
  pVarTP->z += pAmount * (sins);
  pVarTP->color = JWF_FABS(JWF_FRAC((JWF_FLOAT)(pVarTP->color + mul_c * pContext->randGen->random() * d)));
}

void calcFunctionGaussian(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pIn_x, JWF_FLOAT pIn_y, JWF_FLOAT pIn_z, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
  JWF_FLOAT d = JWF_SQRT(JWF_SQR(pIn_x - x0) + JWF_SQR(pIn_y - y0) + JWF_SQR(pIn_z - z0));
  if (invert != 0)
    d = 1 - d;
  if (d < 0)
    d = 0;
  d = (d - mindist) * _rmax;
  if (d < 0)
    d = 0;

  JWF_FLOAT sigma = d * pContext->randGen->random() * 2 * M_PI;
  JWF_FLOAT phi = d * pContext->randGen->random() * M_PI;
  JWF_FLOAT r = d * pContext->randGen->random();

  JWF_FLOAT sins, coss;
  JWF_SINCOS(sigma, &sins, &coss);

  JWF_FLOAT sinp, cosp;
  JWF_SINCOS(phi, &sinp, &cosp);

  pVarTP->x += pAmount * (pIn_x + mul_x * r * coss * cosp);
  pVarTP->y += pAmount * (pIn_y + mul_y * r * coss * sinp);
  pVarTP->z += pAmount * (pIn_z + mul_z * r * sins);
  pVarTP->color = JWF_FABS(JWF_FRAC((JWF_FLOAT)(pVarTP->color + mul_c * pContext->randGen->random() * d)));
}

void calcFunction(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pIn_x, JWF_FLOAT pIn_y, JWF_FLOAT pIn_z, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
  JWF_FLOAT d = JWF_SQRT(JWF_SQR(pIn_x - x0) + JWF_SQR(pIn_y - y0) + JWF_SQR(pIn_z - z0));
  if (invert != 0)
    d = 1 - d;
  if (d < 0)
    d = 0;
  d = (d - mindist) * _rmax;
  if (d < 0)
    d = 0;

  pVarTP->x += pAmount * (pIn_x + mul_x * pContext->randGen->random() * d);
  pVarTP->y += pAmount * (pIn_y + mul_y * pContext->randGen->random() * d);
  pVarTP->z += pAmount * (pIn_z + mul_z * pContext->randGen->random() * d);
  pVarTP->color = JWF_FABS(JWF_FRAC((JWF_FLOAT)(pVarTP->color + mul_c * pContext->randGen->random() * d)));
}


  JWF_FLOAT scatter;
  JWF_FLOAT mindist;
  JWF_FLOAT mul_x;
  JWF_FLOAT mul_y;
  JWF_FLOAT mul_z;
  JWF_FLOAT mul_c;
  JWF_FLOAT x0;
  JWF_FLOAT y0;
  JWF_FLOAT z0;
  int invert;
  int type;
  JWF_FLOAT _rmax;
};

