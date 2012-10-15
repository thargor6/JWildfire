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

class NBlurFunc: public Variation {
public:
	NBlurFunc() {
		numEdges = 3;
		numStripes = 0;
		ratioStripes = 1.0;
		ratioHole = 0.0;
		circumCircle = 0;
		adjustToLinear = 1;
		equalBlur = 1;
		exactCalc = 0;
		highlightEdges = 1.0;
		initParameterNames(9, "numEdges", "numStripes", "ratioStripes", "ratioHole", "circumCircle", "adjustToLinear", "equalBlur", "exactCalc", "highlightEdges");
		_randXYData = (RandXYData*) calloc(1, sizeof(RandXYData));
	}

	~NBlurFunc() {
		free(_randXYData);
	}

	const char* getName() const {
		return "nBlur";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "numEdges") == 0) {
			numEdges = FTOI(pValue);
		}
		else if (strcmp(pName, "numStripes") == 0) {
			numStripes = FTOI(pValue);
		}
		else if (strcmp(pName, "ratioStripes") == 0) {
			ratioStripes = pValue;
		}
		else if (strcmp(pName, "ratioHole") == 0) {
			ratioHole = pValue;
		}
		else if (strcmp(pName, "circumCircle") == 0) {
			circumCircle = FTOI(pValue);
		}
		else if (strcmp(pName, "adjustToLinear") == 0) {
			adjustToLinear = FTOI(pValue);
		}
		else if (strcmp(pName, "equalBlur") == 0) {
			equalBlur = FTOI(pValue);
		}
		else if (strcmp(pName, "exactCalc") == 0) {
			exactCalc = FTOI(pValue);
		}
		else if (strcmp(pName, "highlightEdges") == 0) {
			highlightEdges = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		// nBlur by FractalDesire, http://fractaldesire.deviantart.com/art/nBlur-plugin-190401515
		//*********Adjustment of width of shape*********
		if (adjustToLinear == TRUE) {
			if ((numEdges) % 4 == 0) {
				pAmount /= JWF_SQRT(2.0 - 2.0 * JWF_COS(_midAngle * ((JWF_FLOAT) numEdges / 2.0 - 1.0))) / 2.0;
			}
			else {
				pAmount /= JWF_SQRT(2.0 - 2.0 * JWF_COS(_midAngle * JWF_FLOOR(((JWF_FLOAT) numEdges / 2.0)))) / 2.0;
			}
		}
		//
		randXY(pContext, _randXYData);

		//********Exact calculation slower - interpolated calculation faster********
		if ((exactCalc == TRUE) && (circumCircle == FALSE)) {
			while ((_randXYData->lenXY < _randXYData->lenInnerEdges) || (_randXYData->lenXY > _randXYData->lenOuterEdges))
				randXY(pContext, _randXYData);
		}
		if ((exactCalc == TRUE) && (circumCircle == TRUE)) {
			while (_randXYData->lenXY < _randXYData->lenInnerEdges)
				randXY(pContext, _randXYData);
		}
		JWF_FLOAT xTmp = _randXYData->x;
		JWF_FLOAT yTmp = _randXYData->y;

		//**************************************************************************

		//********Begin of horizontal adjustment (rotation)********
		JWF_FLOAT x = _cosa * xTmp - _sina * yTmp;
		JWF_FLOAT y = _sina * xTmp + _cosa * yTmp;
		//*********End of horizontal adjustment (rotation)*********

		pVarTP->x += pAmount * x;
		pVarTP->y += pAmount * y;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	NBlurFunc* makeCopy() {
		return new NBlurFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		if (numEdges < 3)
			numEdges = 3;

		//*********Prepare stripes related stuff*********
		if (numStripes != 0) {
			_hasStripes = TRUE;
			if (numStripes < 0) {
				_negStripes = TRUE;
				numStripes *= -1;
			}
			else {
				_negStripes = FALSE;
			}
		}
		else {
			_hasStripes = FALSE;
			_negStripes = FALSE;
		}

		//**********Prepare angle related stuff**********
		_midAngle = M_2PI / (JWF_FLOAT) numEdges;
		if (_hasStripes == TRUE) {
			_angStripes = _midAngle / (JWF_FLOAT) (2 * numStripes);
			_angStart = _angStripes / 2.0;
			_nb_ratioComplement = 2.0 - ratioStripes;
		}

		//**********Prepare hole related stuff***********
		if ((ratioHole > 0.95) && (exactCalc == TRUE) && (circumCircle == FALSE))
			ratioHole = 0.95;

		//*********Prepare edge calculation related stuff*********
		_tan90_m_2 = tan(M_PI_2 + _midAngle / 2.0);
		double angle = _midAngle / 2.0;
		JWF_SINCOS(angle, &_sina, &_cosa);

		//*********Prepare factor of adjustment of interpolated calculation*********
		if (highlightEdges <= 0.1)
			highlightEdges = 0.1;

		//*********Prepare circumCircle-calculation*********
		if (circumCircle == TRUE) {
			exactCalc = FALSE;
			highlightEdges = 0.1;
		}

		//*********Prepare speed up related stuff*********
		_speedCalc1 = _nb_ratioComplement * _angStart;
		_speedCalc2 = ratioStripes * _angStart;
		_maxStripes = 2 * numStripes;
		if (_negStripes == FALSE) {
			_arc_tan1 = (13.0 / JWF_POW(numEdges, 1.3)) * highlightEdges;
			_arc_tan2 = (2.0 * JWF_ATAN(_arc_tan1 / (-2.0)));
		}
		else {
			_arc_tan1 = (7.5 / JWF_POW(numEdges, 1.3)) * highlightEdges;
			_arc_tan2 = (2.0 * JWF_ATAN(_arc_tan1 / (-2.0)));
		}
	}

private:
	int numEdges;
	int numStripes;
	double ratioStripes;
	double ratioHole;
	int circumCircle;
	int adjustToLinear;
	int equalBlur;
	int exactCalc;
	double highlightEdges;

	struct RandXYData {
		double x, y;
		double lenXY;
		double lenOuterEdges, lenInnerEdges;
	};

	double _midAngle, _angStripes, _angStart;
	double _tan90_m_2, _sina, _cosa;
	int _hasStripes, _negStripes;
	//**********Variables for speed up***********
	double _speedCalc1, _speedCalc2;
	double _maxStripes;
	double _arc_tan1, _arc_tan2;
	double _nb_ratioComplement;
	RandXYData *_randXYData;

	void randXY(FlameTransformationContext *pContext, RandXYData *data) {
		double x, y;
		double xTmp, yTmp, lenOuterEdges, lenInnerEdges;
		double angXY, lenXY;
		double ranTmp, angTmp, angMem;
		double ratioTmp, ratioTmpNum, ratioTmpDen;
		double speedCalcTmp;
		int count;
		if (exactCalc == TRUE) {
			angXY = pContext->randGen->random() * M_2PI;
		}
		else {
			angXY = (JWF_ATAN((JWF_FLOAT)(_arc_tan1 * (pContext->randGen->random() - 0.5))) / _arc_tan2 + 0.5 + (JWF_FLOAT) (pContext->randGen->random(32768) % numEdges)) * _midAngle;
		}
		JWF_SINCOS(angXY, &x, &y);
		angMem = angXY;

		while (angXY > _midAngle) {
			angXY -= _midAngle;
		}

		//********Begin of xy-calculation of radial stripes********
		if (_hasStripes == TRUE) {
			angTmp = _angStart;
			count = 0;

			while (angXY > angTmp) {
				angTmp += _angStripes;
				if (angTmp > _midAngle)
					angTmp = _midAngle;
				count++;
			}

			if (angTmp != _midAngle)
				angTmp -= _angStart;

			if (_negStripes == FALSE) {
				if ((count % 2) == 1) {
					if (angXY > angTmp) {
						angXY = angXY + _angStart;
						angMem = angMem + _angStart;
						JWF_SINCOS(angMem, &x, &y);
						angTmp += _angStripes;
						count++;
					}
					else {
						angXY = angXY - _angStart;
						angMem = angMem - _angStart;
						JWF_SINCOS(angMem, &x, &y);
						angTmp -= _angStripes;
						count--;
					}
				}
				if (((count % 2) == 0) && (ratioStripes > 1.0)) {
					if ((angXY > angTmp) && (count != _maxStripes)) {
						angMem = angMem - angXY + angTmp + (angXY - angTmp) / _angStart * ratioStripes * _angStart;
						angXY = angTmp + (angXY - angTmp) / _angStart * ratioStripes * _angStart;
						JWF_SINCOS(angMem, &x, &y);
					}
					else {
						angMem = angMem - angXY + angTmp - (angTmp - angXY) / _angStart * ratioStripes * _angStart;
						angXY = angTmp + (angXY - angTmp) / _angStart * ratioStripes * _angStart;
						JWF_SINCOS(angMem, &x, &y);
					}
				}
				if (((count % 2) == 0) && (ratioStripes < 1.0)) {
					if ((JWF_FABS(angXY - angTmp) > _speedCalc2) && (count != (_maxStripes))) {
						if ((angXY - angTmp) > _speedCalc2) {
							ratioTmpNum = (angXY - (angTmp + _speedCalc2)) * _speedCalc2;
							ratioTmpDen = _angStart - _speedCalc2;
							ratioTmp = ratioTmpNum / ratioTmpDen;
							double a = (angMem - angXY + angTmp + ratioTmp);
							JWF_SINCOS(a, &x, &y);
							angXY = angTmp + ratioTmp;
						}
						if ((angTmp - angXY) > _speedCalc2) {
							ratioTmpNum = ((angTmp - _speedCalc2 - angXY)) * _speedCalc2;
							ratioTmpDen = _angStart - _speedCalc2;
							ratioTmp = ratioTmpNum / ratioTmpDen;
							double a = (angMem - angXY + angTmp - ratioTmp);
							JWF_SINCOS(a, &x, &y);
							angXY = angTmp - ratioTmp;
						}
					}
					if (count == _maxStripes) {
						if ((angTmp - angXY) > _speedCalc2) {
							ratioTmpNum = ((angTmp - _speedCalc2 - angXY)) * _speedCalc2;
							ratioTmpDen = _angStart - _speedCalc2;
							ratioTmp = ratioTmpNum / ratioTmpDen;
							double a = (angMem - angXY + angTmp - ratioTmp);
							JWF_SINCOS(a, &x, &y);
							angXY = angTmp - ratioTmp;
						}
					}
				}
			}
			else {
				//********Change ratio and ratioComplement********
				ratioTmp = ratioStripes;
				ratioStripes = _nb_ratioComplement;
				_nb_ratioComplement = ratioTmp;
				speedCalcTmp = _speedCalc1;
				_speedCalc1 = _speedCalc2;
				_speedCalc2 = speedCalcTmp;
				//************************************************
				if ((count % 2) == 0) {
					if ((angXY > angTmp) && (count != _maxStripes)) {
						angXY = angXY + _angStart;
						angMem = angMem + _angStart;
						JWF_SINCOS(angMem, &x, &y);
						angTmp += _angStripes;
						count++;
					}
					else {
						angXY = angXY - _angStart;
						angMem = angMem - _angStart;
						JWF_SINCOS(angMem, &x, &y);
						angTmp -= _angStripes;
						count--;
					}
				}
				if (((count % 2) == 1) && (ratioStripes > 1.0)) {
					if ((angXY > angTmp) && (count != _maxStripes)) {
						angMem = angMem - angXY + angTmp + (angXY - angTmp) / _angStart * ratioStripes * _angStart;
						angXY = angTmp + (angXY - angTmp) / _angStart * ratioStripes * _angStart;
						JWF_SINCOS(angMem, &x, &y);
					}
					else {
						angMem = angMem - angXY + angTmp - (angTmp - angXY) / _angStart * ratioStripes * _angStart;
						angXY = angTmp + (angXY - angTmp) / _angStart * ratioStripes * _angStart;
						JWF_SINCOS(angMem, &x, &y);
					}
				}
				if (((count % 2) == 1) && (ratioStripes < 1.0)) {
					if ((fabs(angXY - angTmp) > _speedCalc2) && (count != _maxStripes)) {
						if ((angXY - angTmp) > _speedCalc2) {
							ratioTmpNum = (angXY - (angTmp + _speedCalc2)) * _speedCalc2;
							ratioTmpDen = _angStart - _speedCalc2;
							ratioTmp = ratioTmpNum / ratioTmpDen;
							double a = (angMem - angXY + angTmp + ratioTmp);
							JWF_SINCOS(a, &x, &y);
							angXY = angTmp + ratioTmp;
						}
						if ((angTmp - angXY) > _speedCalc2) {
							ratioTmpNum = ((angTmp - _speedCalc2 - angXY)) * _speedCalc2;
							ratioTmpDen = _angStart - _speedCalc2;
							ratioTmp = ratioTmpNum / ratioTmpDen;
							double a = (angMem - angXY + angTmp - ratioTmp);
							JWF_SINCOS(a, &x, &y);
							angXY = angTmp - ratioTmp;
						}
					}
					if (count == _maxStripes) {
						angTmp = _midAngle;
						if ((angTmp - angXY) > _speedCalc2) {
							ratioTmpNum = ((angTmp - _speedCalc2 - angXY)) * _speedCalc2;
							ratioTmpDen = _angStart - _speedCalc2;
							ratioTmp = ratioTmpNum / ratioTmpDen;
							double a = (angMem - angXY + angTmp - ratioTmp);
							JWF_SINCOS(a, &x, &y);
							angXY = angTmp - ratioTmp;
						}
					}
				}
				//********Restore ratio and ratioComplement*******
				ratioTmp = ratioStripes;
				ratioStripes = _nb_ratioComplement;
				_nb_ratioComplement = ratioTmp;
				speedCalcTmp = _speedCalc1;
				_speedCalc1 = _speedCalc2;
				_speedCalc2 = speedCalcTmp;
				//************************************************
			}
		}
		//********End of xy-calculation of radial stripes********

		//********Begin of calculation of edge limits********
		xTmp = _tan90_m_2 / (_tan90_m_2 - tan(angXY));
		yTmp = xTmp * JWF_TAN(angXY);
		lenOuterEdges = JWF_SQRT(xTmp * xTmp + yTmp * yTmp);
		//*********End of calculation of edge limits********

		//********Begin of radius-calculation (optionally hole)********
		if (exactCalc == TRUE) {
			if (equalBlur == TRUE)
				ranTmp = JWF_SQRT((JWF_FLOAT)pContext->randGen->random());
			else
				ranTmp = pContext->randGen->random();
		}
		else {
			if (circumCircle == TRUE) {
				if (equalBlur == TRUE)
					ranTmp = JWF_SQRT((JWF_FLOAT)pContext->randGen->random());
				else
					ranTmp = pContext->randGen->random();
			}
			else {
				if (equalBlur == TRUE)
					ranTmp = JWF_SQRT((JWF_FLOAT)pContext->randGen->random()) * lenOuterEdges;
				else
					ranTmp = pContext->randGen->random() * lenOuterEdges;
			}
		}
		lenInnerEdges = ratioHole * lenOuterEdges;

		if (exactCalc == FALSE) {
			if (ranTmp < lenInnerEdges) {
				if (circumCircle == TRUE) {
					if (equalBlur == TRUE)
						ranTmp = lenInnerEdges + JWF_SQRT((JWF_FLOAT)pContext->randGen->random()) * (1.0 - lenInnerEdges + EPSILON);
					else
						ranTmp = lenInnerEdges + pContext->randGen->random() * (1.0 - lenInnerEdges + EPSILON);
				}
				else {
					if (equalBlur == TRUE)
						ranTmp = lenInnerEdges + JWF_SQRT((JWF_FLOAT)pContext->randGen->random()) * (lenOuterEdges - lenInnerEdges);
					else
						ranTmp = lenInnerEdges + pContext->randGen->random() * (lenOuterEdges - lenInnerEdges);
				}
			}
		}

		//if(VAR(hasStripes)==TRUE) ranTmp = pow(ranTmp,0.75);
		x *= ranTmp;
		y *= ranTmp;
		lenXY = JWF_SQRT(x * x + y * y);
		//*********End of radius-calculation (optionally hole)*********
		data->x = x;
		data->y = y;
		data->lenXY = lenXY;
		data->lenOuterEdges = lenOuterEdges;
		data->lenInnerEdges = lenInnerEdges;
	}

};

