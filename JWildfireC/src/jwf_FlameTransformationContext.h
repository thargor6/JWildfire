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
#ifndef __JWF_FLAME_TRANSFORMATION_CONTEXT_H__
#define __JWF_FLAME_TRANSFORMATION_CONTEXT_H__

#include "jwf_RandGen.h"

class FlameTransformationContext {
public:
	FlameTransformationContext(const int pPreserveZCoordinate) {
		isPreserveZCoordinate=pPreserveZCoordinate;
		randGen = new RandGen();
		randGen->init((int) (rand() * 666));
	}

	~FlameTransformationContext() {
		delete randGen;
	}

	RandGen *randGen;
	bool isPreserveZCoordinate;
	int threadIdx;
};


#endif // __JWF_FLAME_TRANSFORMATION_CONTEXT_H__
