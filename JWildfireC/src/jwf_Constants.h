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
#define APP_VERSION "0.58 (07.12.2012)"

#define MAX_MOD_WEIGHT_COUNT 100
#define NEXT_APPLIED_XFORM_TABLE_SIZE 100
#define INITIAL_ITERATIONS 42

#define DRAWMODE_NORMAL 0
#define DRAWMODE_HIDDEN 1
#define DRAWMODE_OPAQUE 2

#ifndef TRUE
#define TRUE 1
#endif

#ifndef FALSE
#define FALSE 0
#endif

struct XForm;

#endif // __JWF_CONSTANTS_H__
