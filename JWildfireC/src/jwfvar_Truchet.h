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

class TruchetFunc: public Variation {
public:
	TruchetFunc() {
	  extended = 0;
	  exponent = 2.0;
	  arc_width = 0.5;
	  rotation = 0.0;
	  size = 1.0;
	  seed = 50.0;
		initParameterNames(6, "extended", "exponent", "arc_width", "rotation", "size", "seed");
	}

	const char* getName() const {
		return "truchet";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "extended") == 0) {
			extended = FTOI(pValue);
		}
		else if (strcmp(pName, "rotation") == 0) {
			rotation = pValue;
		}
		else if (strcmp(pName, "exponent") == 0) {
			exponent = pValue;
		}
		else if (strcmp(pName, "arc_width") == 0) {
			arc_width = pValue;
		}
		else if (strcmp(pName, "size") == 0) {
			size = pValue;
		}
		else if (strcmp(pName, "seed") == 0) {
			seed = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    //APO VARIABLES
    JWF_FLOAT n = exponent;
    JWF_FLOAT onen = 1.0 / exponent;
    JWF_FLOAT tdeg = rotation;
    JWF_FLOAT width = arc_width;
    JWF_FLOAT lseed = JWF_FABS(seed);
    JWF_FLOAT lseed2 = JWF_SQRT(lseed + (lseed / 2) + EPSILON) / ((lseed * 0.5) + EPSILON) * 0.25;
    //VARIABLES
    JWF_FLOAT x, y;
    int intx = 0;
    int inty = 0;
    JWF_FLOAT r = -tdeg;
    JWF_FLOAT r0 = 0.0;
    JWF_FLOAT r1 = 0.0;
    JWF_FLOAT rmax = 0.5 * (JWF_POW(2.0, 1.0 / n) - 1.0) * width;
    JWF_FLOAT sinr, cosr;
    JWF_SINCOS(r, &sinr, &cosr);
    JWF_FLOAT scale = (cosr - sinr) / pAmount;
    JWF_FLOAT tiletype = 0.0;
    JWF_FLOAT randint = 0.0;
    JWF_FLOAT modbase = 65535.0;
    JWF_FLOAT multiplier = 32747.0;//1103515245;
    JWF_FLOAT offset = 12345.0;
    JWF_FLOAT niter = 0.0;
    int randiter = 0;
    //INITIALISATION
    x = pAffineTP->x * scale;
    y = pAffineTP->y * scale;
    intx = (int) JWF_ROUND(x);
    inty = (int) JWF_ROUND(y);

    r = x - intx;
    if (r < 0.0) {
      x = 1.0 + r;
    }
    else {
      x = r;
    }

    r = y - inty;
    if (r < 0.0) {
      y = 1.0 + r;
    }
    else {
      y = r;
    }
    //CALCULATE THE TILE TYPE
    if (seed == 0.0) {
      tiletype = 0.0;
    }
    else if (seed == 1.0) {
      tiletype = 1.0;
    }
    else {
      if (extended == 0) {
        JWF_FLOAT xrand = JWF_ROUND(pAffineTP->x);
        JWF_FLOAT yrand = JWF_ROUND(pAffineTP->y);
        xrand = xrand * lseed2;
        yrand = yrand * lseed2;
        niter = xrand + yrand + xrand * yrand;
        randint = (niter + seed) * lseed2 / 2.0;
        randint = JWF_FMOD((randint * multiplier + offset), modbase);
      }
      else {
        seed = JWF_FLOOR(seed);
        int xrand = (int) JWF_ROUND(pAffineTP->x);
        int yrand = (int) JWF_ROUND(pAffineTP->y);
        niter = JWF_FABS(xrand + yrand + xrand * yrand);
        randint = seed + niter;
        randiter = 0;
        while (randiter < niter) {
          randiter += 1;
          randint = JWF_FMOD((randint * multiplier + offset), modbase);
        }
      }
      tiletype = fmod(randint, 2.0);//randint%2;
    }
    //DRAWING THE POINTS
    if (extended == 0) { //Fast drawmode
      if (tiletype < 1.0) {
        r0 = JWF_POW((JWF_POW(JWF_FABS(x), n) + JWF_POW(JWF_FABS(y), n)), onen);
        r1 = JWF_POW((JWF_POW(JWF_FABS(x - 1.0), n) + JWF_POW(JWF_FABS(y - 1.0), n)), onen);
      }
      else {
        r0 = JWF_POW((JWF_POW(JWF_FABS(x - 1.0), n) + JWF_POW(JWF_FABS(y), n)), onen);
        r1 = JWF_POW((JWF_POW(JWF_FABS(x), n) + JWF_POW(JWF_FABS(y - 1.0), n)), onen);
      }
    }
    else {
      if (tiletype == 1.0) { //Slow drawmode
        r0 = JWF_POW((JWF_POW(JWF_FABS(x), n) + JWF_POW(JWF_FABS(y), n)), onen);
        r1 = JWF_POW((JWF_POW(JWF_FABS(x - 1.0), n) + JWF_POW(JWF_FABS(y - 1.0), n)), onen);
      }
      else {
        r0 = JWF_POW((JWF_POW(JWF_FABS(x - 1.0), n) + JWF_POW(JWF_FABS(y), n)), onen);
        r1 = JWF_POW((JWF_POW(JWF_FABS(x), n) + JWF_POW(JWF_FABS(y - 1.0), n)), onen);
      }
    }

    r = JWF_FABS(r0 - 0.5) / rmax;
    if (r < 1.0) {
      pVarTP->x += size * (x + JWF_FLOOR(pAffineTP->x));
      pVarTP->y += size * (y + JWF_FLOOR(pAffineTP->y));
    }

    r = JWF_FABS(r1 - 0.5) / rmax;
    if (r < 1.0) {
      pVarTP->x += size * (x + JWF_FLOOR(pAffineTP->x));
      pVarTP->y += size * (y + JWF_FLOOR(pAffineTP->y));
      if (pContext->isPreserveZCoordinate) {
        pVarTP->z += pAmount * pAffineTP->z;
      }
    }
	}

	TruchetFunc* makeCopy() {
		return new TruchetFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    if (extended < 0) {
      extended = 0;
    }
    else if (extended > 1) {
      extended = 1;
    }
    if (exponent < 0.001) {
      exponent = 0.001;
    }
    else if (exponent > 2.0) {
      exponent = 2.0;
    }
    if (arc_width < 0.001) {
      arc_width = 0.001;
    }
    else if (arc_width > 1.0) {
      arc_width = 1.0;
    }
    if (size < 0.001) {
      size = 0.001;
    }
    else if (size > 10.0) {
      size = 10.0;
    }
	}

private:
  int extended;
  JWF_FLOAT exponent;
  JWF_FLOAT arc_width;
  JWF_FLOAT rotation;
  JWF_FLOAT size;
  JWF_FLOAT seed;
};

