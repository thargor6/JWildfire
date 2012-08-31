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
#ifndef __JWF_UTIL_H__
#define __JWF_UTIL_H__

int FTOI(float pVal) {
 if (pVal > 0.0f)
   return (int) (pVal + 0.5f);
 else if (pVal < 0.0f)
   return (int) (0.0f - (fabs(pVal) + 0.5f));
 else
   return 0;
}

void hostMalloc(void **dst,int size) {
  *dst=malloc(size);
  if(*dst==0) {
	  printf("Failed to allocated %d bytes of memory.\n",size);
  }
}

void hostFree(void *dst) {
	free(dst);
}


#endif // __JWF_UTIL_H__
