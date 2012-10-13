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

class BWraps7Func: public Variation {
public:
	BWraps7Func() {
    cellsize = 1.0;
    space = 0.0;
    gain = 2.0;
    inner_twist = 0.0;
    outer_twist = 0.0;
		initParameterNames(5, "cellsize", "space", "gain", "inner_twist", "outer_twist");
	}

	const char* getName() const {
		return "bwraps7";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "cellsize") == 0) {
			cellsize = pValue;
		}
		else if (strcmp(pName, "space") == 0) {
			space = pValue;
		}
		else if (strcmp(pName, "gain") == 0) {
			gain = pValue;
		}
		else if (strcmp(pName, "inner_twist") == 0) {
			inner_twist = pValue;
		}
		else if (strcmp(pName, "outer_twist") == 0) {
			outer_twist = pValue;
		}
  }

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT Vx, Vy; // V is "global" vector,
    JWF_FLOAT Cx, Cy; // C is "cell centre" vector
    JWF_FLOAT Lx, Ly; // L is "local" bubble vector
    JWF_FLOAT r, theta, s, c;

    Vx = pAffineTP->x;
    Vy = pAffineTP->y;

    if (JWF_FABS(cellsize) < EPSILON) {
      // Linear if cells are too small
      pVarTP->x += pAmount * Vx;
      pVarTP->y += pAmount * Vy;
      if (pContext->isPreserveZCoordinate) {
        pVarTP->z += pAmount * pAffineTP->z;
      }
      return;
    }

    Cx = (JWF_FLOOR(Vx / cellsize) + 0.5) * cellsize;
    Cy = (JWF_FLOOR(Vy / cellsize) + 0.5) * cellsize;

    Lx = Vx - Cx;
    Ly = Vy - Cy;

    if ((Lx * Lx + Ly * Ly) > _r2) {
      // Linear if outside the bubble
      pVarTP->x += pAmount * Vx;
      pVarTP->y += pAmount * Vy;
      if (pContext->isPreserveZCoordinate) {
        pVarTP->z += pAmount * pAffineTP->z;
      }
      return;
    }

    // We're in the bubble!

    // Bubble distortion on local co-ordinates:
    Lx *= _g2;
    Ly *= _g2;
    r = _rfactor / ((Lx * Lx + Ly * Ly) / 4.0 + 1.0);
    Lx *= r;
    Ly *= r;

    // Spin around the centre:
    r = (Lx * Lx + Ly * Ly) / _r2; // r should be 0.0 - 1.0
    theta = inner_twist * (1.0 - r) + outer_twist * r;
    JWF_SINCOS(theta, &s, &c);

    // Add rotated local vectors direct to centre (avoids use of temp storage)
    Vx = Cx + c * Lx + s * Ly;
    Vy = Cy - s * Lx + c * Ly;

    // Finally add values in
    pVarTP->x += pAmount * Vx;
    pVarTP->y += pAmount * Vy;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	BWraps7Func* makeCopy() {
		return new BWraps7Func(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    JWF_FLOAT radius = 0.5 * (cellsize / (1.0 + space * space));

    // g2 is multiplier for radius
    _g2 = gain * gain + 1.0e-6;

    // Start max_bubble as maximum x or y value before applying bubble
    JWF_FLOAT max_bubble = _g2 * radius;

    if (max_bubble > 2.0) {
      // Values greater than 2.0 "recurve" round the back of the bubble
      max_bubble = 1.0;
    }
    else {
      // Expand smaller bubble to fill the space
      max_bubble *= 1.0 / ((max_bubble * max_bubble) / 4.0 + 1.0);
    }

    _r2 = radius * radius;
    _rfactor = radius / max_bubble;
	}

private:
  JWF_FLOAT cellsize;
  JWF_FLOAT space;
  JWF_FLOAT gain;
  JWF_FLOAT inner_twist;
  JWF_FLOAT outer_twist;

  JWF_FLOAT _g2, _r2, _rfactor;
};

