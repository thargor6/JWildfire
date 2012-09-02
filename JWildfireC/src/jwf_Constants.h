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
#ifndef __JWF_CONSTANTS_H__
#define __JWF_CONSTANTS_H__

#define APP_TITLE "JWildfireC"
#define APP_VERSION "0.12 (02.09.2012)"

#define EPSILON 1.0e-8f

#define MAX_MOD_WEIGHT_COUNT 100
#define NEXT_APPLIED_XFORM_TABLE_SIZE 100
#define INITIAL_ITERATIONS 42

#undef M_PI
#define M_PI 3.1415926535898f

#undef M_PI_2
#define M_PI_2 (M_PI * 0.5f)

#undef M_PI_4
#define M_PI_4 (M_PI * 0.25f)

#undef M_1_PI
#define M_1_PI (1.0f / M_PI)

#undef M_2_PI
#define M_2_PI (2.0f / M_PI)

#undef M_2PI
#define M_2PI (2.0f * M_PI)

#define DRAWMODE_NORMAL 0
#define DRAWMODE_HIDDEN 1
#define DRAWMODE_OPAQUE 2

#ifndef TRUE
#define TRUE 1
#endif

#ifndef FALSE
#define FALSE 0
#endif

#endif // __JWF_CONSTANTS_H__
