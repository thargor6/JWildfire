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

class OscilloscopeFunc: public Variation {
public:
	OscilloscopeFunc() {
		separation = 1.0;
		frequency = M_PI;
		amplitude = 1.0;
		damping = 0.0;
		initParameterNames(4, "separation", "frequency", "amplitude", "damping");
	}

	const char* getName() const {
		return "oscilloscope";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "separation") == 0) {
			separation = pValue;
		}
		else if (strcmp(pName, "frequency") == 0) {
			frequency = pValue;
		}
		else if (strcmp(pName, "amplitude") == 0) {
			amplitude = pValue;
		}
		else if (strcmp(pName, "damping") == 0) {
			damping = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT t;
		if (_noDamping) {
			t = amplitude * JWF_COS(_tpf * pAffineTP->x) + separation;
		}
		else {
			t = amplitude * JWF_EXP(-JWF_FABS(pAffineTP->x) * damping) * JWF_COS(_tpf * pAffineTP->x) + separation;
		}

		if (JWF_FABS(pAffineTP->y) <= t) {
			pVarTP->x += pAmount * pAffineTP->x;
			pVarTP->y -= pAmount * pAffineTP->y;
		}
		else {
			pVarTP->x += pAmount * pAffineTP->x;
			pVarTP->y += pAmount * pAffineTP->y;
		}
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_tpf = 2.0 * M_PI * frequency;
		_noDamping = JWF_FABS(damping) <= EPSILON;
	}

	OscilloscopeFunc* makeCopy() {
		return new OscilloscopeFunc(*this);
	}

private:
	JWF_FLOAT separation;
	JWF_FLOAT frequency;
	JWF_FLOAT amplitude;
	JWF_FLOAT damping;

	JWF_FLOAT _tpf;
	JWF_FLOAT _noDamping;
};

