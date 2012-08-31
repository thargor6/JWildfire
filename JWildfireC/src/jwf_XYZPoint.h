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

class XYZPoint {
public:
  float x;
  float y;
  float z;
  float color;

  // custom RGB colors
  short rgbColor;
  float redColor;
  float greenColor;
  float blueColor;

  XYZPoint();

  // often (but not always) used properties, calculation only if needed
  void assign(XYZPoint *p);
  void invalidate();
  void clear();
  float getPrecalcSumsq();
  float getPrecalcSqrt();
  float getPrecalcAtan();
  float getPrecalcAtanYX();
  float getPrecalcSinA();
  float getPrecalcCosA();
  bool isEqual(XYZPoint *pSrc);
private:
  float sumsq;
  bool validSumsq;
  float sqrt;
  bool validSqrt;
  float atan;
  bool validAtan;
  float atanYX;
  bool validAtanYX;
  float sinA;
  bool validSinA;
  float cosA;
  bool validCosA;
};


#endif // __JWF_XYZPOINT_H__
