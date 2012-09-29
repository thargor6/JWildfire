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
#ifndef __JWF_XYZPOINT_H__
#define __JWF_XYZPOINT_H__

#include "jwf_Math.h"

class XYZPoint {
public:
	JWF_FLOAT x;
	JWF_FLOAT y;
	JWF_FLOAT z;
	JWF_FLOAT color;

	// custom RGB colors
	short rgbColor;
	JWF_FLOAT redColor;
	JWF_FLOAT greenColor;
	JWF_FLOAT blueColor;

	XYZPoint();

	// often (but not always) used properties, calculation only if needed
	void assign(XYZPoint *p);
	void invalidate();
	void clear();
	JWF_FLOAT getPrecalcSumsq();
	JWF_FLOAT getPrecalcSqrt();
	JWF_FLOAT getPrecalcAtan();
	JWF_FLOAT getPrecalcAtanYX();
	JWF_FLOAT getPrecalcSinA();
	JWF_FLOAT getPrecalcCosA();
	bool isEqual(XYZPoint *pSrc);
//private:
	JWF_FLOAT sumsq;
	bool validSumsq;
	JWF_FLOAT sqrt;
	bool validSqrt;
	JWF_FLOAT atan;
	bool validAtan;
	JWF_FLOAT atanYX;
	bool validAtanYX;
	JWF_FLOAT sinA;
	bool validSinA;
	JWF_FLOAT cosA;
	bool validCosA;
};

#endif // __JWF_XYZPOINT_H__
