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
#ifndef __JWF_TYPES_H__
#define __JWF_TYPES_H__

struct RasterPoint {
	JWF_FLOAT red;
	JWF_FLOAT green;
	JWF_FLOAT blue;
	long int count;

	void clear() {
		red = green = blue = 0.0;
		count = 0;
	}
};

struct LogDensityPoint {
	JWF_FLOAT red;
	JWF_FLOAT green;
	JWF_FLOAT blue;
	JWF_FLOAT intensity;

	void clear() {
		red = green = blue = intensity = 0.0;
	}
};

struct GammaCorrectedRGBPoint {
	int red;
	int green;
	int blue;
	int alpha;
};

struct GammaCorrectedHDRPoint {
	JWF_FLOAT red;
	JWF_FLOAT green;
	JWF_FLOAT blue;
};

#endif // __JWF_TYPES_H__
