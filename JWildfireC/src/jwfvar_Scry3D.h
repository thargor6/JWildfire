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


#include "jwf_Variation.h"

class Scry3DFunc: public Variation {
public:
	Scry3DFunc() {
	}

	const char* getName() const {
		return "scry_3D";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    /* scry_3D by Larry Berlin, http://aporev.deviantart.com/art/New-3D-Plugins-136484533?q=gallery%3Aaporev%2F8229210&qo=22 */
    JWF_FLOAT inv = 1.0 / (pAmount + EPSILON);
    JWF_FLOAT t = JWF_SQR(pAffineTP->x) + JWF_SQR(pAffineTP->y) + JWF_SQR(pAffineTP->z);
    JWF_FLOAT r = 1.0 / (JWF_SQRT(t) * (t + inv));
    JWF_FLOAT Footzee, kikr;
    kikr = JWF_ATAN2(pAffineTP->y, pAffineTP->x);
    Footzee = pAffineTP->z;
    pVarTP->x += pAffineTP->x * r;
    pVarTP->y += pAffineTP->y * r;

    if (Footzee != 0.0) {
      pVarTP->z += Footzee * r;
    }
    else {
      Footzee = kikr;
      pVarTP->z += Footzee * r;
    }
	}

	Scry3DFunc* makeCopy() {
		return new Scry3DFunc(*this);
	}

};

