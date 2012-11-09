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

class Cubic3DFunc: public Variation {
public:
	Cubic3DFunc() {
	  xpand = 0.25;
	  style = 0.0;
		initParameterNames(2, "xpand", "style");
	}

	const char* getName() const {
		return "cubic3D";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "xpand") == 0) {
			xpand = pValue;
		}
		else if (strcmp(pName, "style") == 0) {
			style = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* cubic3D by Larry Berlin, http://aporev.deviantart.com/art/3D-Plugins-Collection-One-138514007?q=gallery%3Aaporev%2F8229210&qo=15 */
    JWF_FLOAT fill, exnze, wynze, znxy;
    JWF_FLOAT smooth = 1.0;
    JWF_FLOAT smoothStyle = 1.0;
    int useNode = 0;
    int rchoice = (int) JWF_TRUNC(pContext->randGen->random() * 8.0);
    JWF_FLOAT lattd = pAmount * 0.5;

    if (JWF_FABS(xpand) <= 1.0) {
      fill = xpand * 0.5; // values up to 0.5
    }
    else {
      fill = JWF_SQRT(xpand) * 0.5; // values above 0.5
    }

    if (fabs(pAmount) <= 0.5) {
      smooth = pAmount * 2.0; // Causes full effect above pAmount=0.5
    }
    else {
      smooth = 1.0;
    }

    if (JWF_FABS(style) <= 1.0) {
      smoothStyle = style;
    }
    else {
      if (style > 1.0) {
        smoothStyle = 1.0 + (style - 1.0) * 0.25;
      }
      else {
        smoothStyle = (style + 1.0) * 0.25 - 1.0;
      }
    }

    exnze = 1.0 - (smoothStyle * (1.0 - (JWF_COS(JWF_ATAN2(pAffineTP->x, pAffineTP->z)))));
    wynze = 1.0 - (smoothStyle * (1.0 - (JWF_SIN(JWF_ATAN2(pAffineTP->y, pAffineTP->z)))));
    if (smoothStyle > 1.0) {
      znxy = 1.0 - (smoothStyle * (1.0 - ((exnze + wynze) / 2.0 * smoothStyle)));
    }
    else {
      znxy = 1.0 - (smoothStyle * (1.0 - ((exnze + wynze) / 2.0)));
    }

    useNode = rchoice;
    if (useNode == 0) {
      pVarTP->x = ((pVarTP->x - (smooth * (1.0 - fill) * pVarTP->x * exnze)) + (pAffineTP->x * smooth * fill * exnze)) + lattd;
      pVarTP->y = ((pVarTP->y - (smooth * (1.0 - fill) * pVarTP->y * wynze)) + (pAffineTP->y * smooth * fill * wynze)) + lattd;
      pVarTP->z = ((pVarTP->z - (smooth * (1.0 - fill) * pVarTP->z * znxy)) + (pAffineTP->z * smooth * fill * znxy)) + lattd;
    }
    if (useNode == 1) {
      pVarTP->x = ((pVarTP->x - (smooth * (1.0 - fill) * pVarTP->x * exnze)) + (pAffineTP->x * smooth * fill * exnze)) + lattd;
      pVarTP->y = ((pVarTP->y - (smooth * (1.0 - fill) * pVarTP->y * wynze)) + (pAffineTP->y * smooth * fill * wynze)) - lattd;
      pVarTP->z = ((pVarTP->z - (smooth * (1.0 - fill) * pVarTP->z * znxy)) + (pAffineTP->z * smooth * fill * znxy)) + lattd;
    }
    if (useNode == 2) {
      pVarTP->x = ((pVarTP->x - (smooth * (1.0 - fill) * pVarTP->x * exnze)) + (pAffineTP->x * smooth * fill * exnze)) + lattd;
      pVarTP->y = ((pVarTP->y - (smooth * (1.0 - fill) * pVarTP->y * wynze)) + (pAffineTP->y * smooth * fill * wynze)) + lattd;
      pVarTP->z = ((pVarTP->z - (smooth * (1.0 - fill) * pVarTP->z * znxy)) + (pAffineTP->z * smooth * fill * znxy)) - lattd;
    }
    if (useNode == 3) {
      pVarTP->x = ((pVarTP->x - (smooth * (1.0 - fill) * pVarTP->x * exnze)) + (pAffineTP->x * smooth * fill * exnze)) + lattd;
      pVarTP->y = ((pVarTP->y - (smooth * (1.0 - fill) * pVarTP->y * wynze)) + (pAffineTP->y * smooth * fill * wynze)) - lattd;
      pVarTP->z = ((pVarTP->z - (smooth * (1.0 - fill) * pVarTP->z * znxy)) + (pAffineTP->z * smooth * fill * znxy)) - lattd;
    }
    if (useNode == 4) {
      pVarTP->x = ((pVarTP->x - (smooth * (1.0 - fill) * pVarTP->x * exnze)) + (pAffineTP->x * smooth * fill * exnze)) - lattd;
      pVarTP->y = ((pVarTP->y - (smooth * (1.0 - fill) * pVarTP->y * wynze)) + (pAffineTP->y * smooth * fill * wynze)) + lattd;
      pVarTP->z = ((pVarTP->z - (smooth * (1.0 - fill) * pVarTP->z * znxy)) + (pAffineTP->z * smooth * fill * znxy)) + lattd;
    }
    if (useNode == 5) {
      pVarTP->x = ((pVarTP->x - (smooth * (1.0 - fill) * pVarTP->x * exnze)) + (pAffineTP->x * smooth * fill * exnze)) - lattd;
      pVarTP->y = ((pVarTP->y - (smooth * (1.0 - fill) * pVarTP->y * wynze)) + (pAffineTP->y * smooth * fill * wynze)) - lattd;
      pVarTP->z = ((pVarTP->z - (smooth * (1.0 - fill) * pVarTP->z * znxy)) + (pAffineTP->z * smooth * fill * znxy)) + lattd;
    }
    if (useNode == 6) {
      pVarTP->x = ((pVarTP->x - (smooth * (1.0 - fill) * pVarTP->x * exnze)) + (pAffineTP->x * smooth * fill * exnze)) - lattd;
      pVarTP->y = ((pVarTP->y - (smooth * (1.0 - fill) * pVarTP->y * wynze)) + (pAffineTP->y * smooth * fill * wynze)) + lattd;
      pVarTP->z = ((pVarTP->z - (smooth * (1.0 - fill) * pVarTP->z * znxy)) + (pAffineTP->z * smooth * fill * znxy)) - lattd;
    }
    if (useNode == 7) {
      pVarTP->x = ((pVarTP->x - (smooth * (1.0 - fill) * pVarTP->x * exnze)) + (pAffineTP->x * smooth * fill * exnze)) - lattd;
      pVarTP->y = ((pVarTP->y - (smooth * (1.0 - fill) * pVarTP->y * wynze)) + (pAffineTP->y * smooth * fill * wynze)) - lattd;
      pVarTP->z = ((pVarTP->z - (smooth * (1.0 - fill) * pVarTP->z * znxy)) + (pAffineTP->z * smooth * fill * znxy)) - lattd;
    }
	}

	Cubic3DFunc* makeCopy() {
		return new Cubic3DFunc(*this);
	}

private:
  JWF_FLOAT xpand;
  JWF_FLOAT style;
};

