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
#ifndef JWFVAR_SPIROGRAPH_H_
#define JWFVAR_SPIROGRAPH_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class SpirographFunc: public Variation {
public:
	SpirographFunc() {
	  a = 3.0;
	  b = 2.0;
	  d = 0.0;
	  c1 = 0.0;
	  c2 = 0.0;
	  tMin = -1.0;
	  tMax = 1.0;
	  yMin = -1.0;
	  yMax = 1.0;
		initParameterNames(9, "a", "b", "d","c1","c2","tMin","tMax","yMin","yMax" );
	}

	const char* getName() const {
		return "spirograph";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "a") == 0) {
			a = pValue;
		}
		else if (strcmp(pName, "b") == 0) {
			b = pValue;
		}
		else if (strcmp(pName, "d") == 0) {
			d = pValue;
		}
		else if (strcmp(pName, "c1") == 0) {
			c1 = pValue;
		}
		else if (strcmp(pName, "c2") == 0) {
			c2 = pValue;
		}
		else if (strcmp(pName, "tMin") == 0) {
			tMin = pValue;
		}
		else if (strcmp(pName, "tMax") == 0) {
			tMax = pValue;
		}
		else if (strcmp(pName, "yMin") == 0) {
			yMin = pValue;
		}
		else if (strcmp(pName, "yMax") == 0) {
			yMax = pValue;
		}
  }

	void transform(FlameTransformationContext *pContext, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float t = (tMax - tMin) * pContext->randGen->random() + tMin;
    float y = (yMax - yMin) * pContext->randGen->random() + yMin;
    float x1 = (a + b) * cosf(t) - c1 * cosf((a + b) / b * t);
    float y1 = (a + b) * sinf(t) - c2 * sinf((a + b) / b * t);
    pVarTP->x += pAmount * (x1 + d * cos(t) + y);
    pVarTP->y += pAmount * (y1 + d * sin(t) + y);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	SpirographFunc* makeCopy() {
		return new SpirographFunc(*this);
	}

private:
  float a;
  float b;
  float d;
  float c1;
  float c2;
  float tMin;
  float tMax;
  float yMin;
  float yMax;
};

#endif // JWFVAR_SPIROGRAPH_H_
