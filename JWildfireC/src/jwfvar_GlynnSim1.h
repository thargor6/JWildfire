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

class GlynnSim1Func: public Variation {
public:
	GlynnSim1Func() {
	  radius = 1.0;
	  radius1 = 0.1;
	  phi1 = 110.0;
	  thickness = 0.1;
	  _pow = 1.5;
	  contrast = 0.5;
		initParameterNames(3, "radius", "radius1", "phi1", "thickness", "pow", "contrast");
		toolPoint=(Point*)calloc(1, sizeof(Point));
	}

	~GlynnSim1Func() {
		free(toolPoint);
	}

	const char* getName() const {
		return "glynnSim1";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "radius") == 0) {
			radius = pValue;
		}
		else if (strcmp(pName, "radius1") == 0) {
			radius1 = pValue;
		}
		else if (strcmp(pName, "phi1") == 0) {
			phi1 = pValue;
		}
		else if (strcmp(pName, "thickness") == 0) {
			thickness = pValue;
		}
		else if (strcmp(pName, "pow") == 0) {
			_pow = pValue;
		}
		else if (strcmp(pName, "contrast") == 0) {
			contrast = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT r = JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
    JWF_FLOAT Alpha = radius / r;
    if (r < radius) { //object generation
      circle(pContext, toolPoint);
      pVarTP->x += pAmount * toolPoint->x;
      pVarTP->y += pAmount * toolPoint->y;
    }
    else {
      if (pContext->randGen->random() > contrast * JWF_POW(Alpha, _absPow)) {
        toolPoint->x = pAffineTP->x;
        toolPoint->y = pAffineTP->y;
      }
      else {
        toolPoint->x = Alpha * Alpha * pAffineTP->x;
        toolPoint->y = Alpha * Alpha * pAffineTP->y;
      }
      JWF_FLOAT Z = JWF_SQR(toolPoint->x - _x1) + JWF_SQR(toolPoint->y - _y1);
      if (Z < radius1 * radius1) { //object generation
        circle(pContext, toolPoint);
        pVarTP->x += pAmount * toolPoint->x;
        pVarTP->y += pAmount * toolPoint->y;
      }
      else {
        pVarTP->x += pAmount * toolPoint->x;
        pVarTP->y += pAmount * toolPoint->y;
      }
    }
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	GlynnSim1Func* makeCopy() {
		return new GlynnSim1Func(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    JWF_FLOAT a = M_PI * phi1 / 180.0;
    JWF_FLOAT sinPhi1 = JWF_SIN(a);
    JWF_FLOAT cosPhi1 = JWF_COS(a);
    _x1 = radius * cosPhi1;
    _y1 = radius * sinPhi1;
    _absPow = JWF_FABS(_pow);
	}

private:
  JWF_FLOAT radius;
  JWF_FLOAT radius1;
  JWF_FLOAT phi1;
  JWF_FLOAT thickness;
  JWF_FLOAT _pow;
  JWF_FLOAT contrast;

  JWF_FLOAT _x1, _y1, _absPow;

  struct Point {
  	JWF_FLOAT x, y;
  };

  Point *toolPoint;

  void circle(FlameTransformationContext *pContext, Point *p) {
  	JWF_FLOAT r = radius1 * (thickness + (1.0 - thickness) * pContext->randGen->random());
  	JWF_FLOAT Phi = 2.0 * M_PI * pContext->randGen->random();
  	JWF_FLOAT sinPhi = JWF_SIN(Phi);
  	JWF_FLOAT cosPhi = JWF_COS(Phi);
    p->x = r * cosPhi + _x1;
    p->y = r * sinPhi + _y1;
  }

};

