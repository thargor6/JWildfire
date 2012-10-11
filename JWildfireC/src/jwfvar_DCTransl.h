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

class DCTranslFunc: public Variation {
public:
	DCTranslFunc() {
	  x0 = 0.0;
	  x1 = 1.0;
	  factor = 1.0;
	  overwrite = 1;
	  clamp = 0;
		initParameterNames(5, "x0", "x1", "factor", "overwrite", "clamp");
	}

	const char* getName() const {
		return "dc_ztransl";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "x0") == 0) {
			x0 = pValue;
		}
		else if (strcmp(pName, "x1") == 0) {
			x1 = pValue;
		}
		else if (strcmp(pName, "factor") == 0) {
			factor = pValue;
		}
		else if (strcmp(pName, "overwrite") == 0) {
			overwrite = FTOI(pValue);
		}
		else if (strcmp(pName, "clamp") == 0) {
			clamp = FTOI(pValue);
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* dc_ztransl by Xyrus02, http://xyrus02.deviantart.com/art/DC-ZTransl-plugins-for-Apo7X-210719008?q=gallery%3Afractal-resources%2F24660058&qo=32 */
    JWF_FLOAT zf = factor * (pAffineTP->color - _x0) / _x1_m_x0;
    if (clamp != 0)
      zf = zf < 0 ? 0 : zf > 1 ? 1 : zf;
    pVarTP->x += pAmount * pAffineTP->x;
    pVarTP->y += pAmount * pAffineTP->y;

    if (overwrite == 0)
      pVarTP->z += pAmount * pAffineTP->z * zf;
    else
      pVarTP->z += pAmount * zf;
	}

	DCTranslFunc* makeCopy() {
		return new DCTranslFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    _x0 = x0 < x1 ? x0 : x1;
    _x1 = x0 > x1 ? x0 : x1;
    _x1_m_x0 = _x1 - _x0 == 0 ? EPSILON : _x1 - _x0;
	}

private:
  JWF_FLOAT x0;
  JWF_FLOAT x1;
  JWF_FLOAT factor;
  int overwrite;
  int clamp;

  JWF_FLOAT _x0, _x1, _x1_m_x0;
};

