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

// Port of Hexes and Crackle plugin by slobo777, see http://slobo777.deviantart.com/art/Apo-Plugins-Hexes-And-Crackle-99243824
// All credits for this wonderful plugin to him!
#ifndef __JWF_NOISE_TOOLS_H__
#define __JWF_NOISE_TOOLS_H__

#include "jwf_Math.h"

JWF_FLOAT simplexNoise3D(JWF_FLOAT V[3]);
JWF_FLOAT simplexDNoise3D(JWF_FLOAT V[3], JWF_FLOAT R[3]);
JWF_FLOAT perlinNoise3D(JWF_FLOAT V[3], JWF_FLOAT aScale, JWF_FLOAT fScale, int octaves);
JWF_FLOAT perlinDNoise3D(JWF_FLOAT V[3], JWF_FLOAT R[3], JWF_FLOAT aScale, JWF_FLOAT fScale, int octaves);

// Cheap and cheerful vector definitions for 3D :-)
// They just make reading vector code based on arrays
// slightly nicer for me! E.g. use V[_x_] instead of V[0].

#undef _x_
#define _x_ 0
#undef _y_
#define _y_ 1
#undef _z_
#define _z_ 2

#endif // __JWF_NOISE_TOOLS_H__
