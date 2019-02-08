/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.create.tina.variation.plot;


import csk.taprats.geometry.*;
import org.jwildfire.base.mathlib.DoubleWrapperWF;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.VariationFunc;

import java.io.Serializable;
import java.util.ArrayList;

import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * @author Jesus Sosa
 * @date May 15, 2018
 */

public class DrawFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private double line_thickness;
  protected ArrayList<Primitive> primitives = new ArrayList<Primitive>();

  private int numEdges = 3;
  private double ratioHole = 1.0;
  private RandXYData _randXYData = new RandXYData();


  private static final int RAND_MAX = 32767;

  private int rand(FlameTransformationContext pContext) {
    return pContext.random(RAND_MAX);
  }

  private DoubleWrapperWF sina = new DoubleWrapperWF();
  private DoubleWrapperWF cosa = new DoubleWrapperWF();


  public Point plotBlur(FlameTransformationContext pContext, double x1, double y1, double pAmount) {
    double xout = x1, yout = y1;
    double r = pContext.random() * (M_PI + M_PI);
    sinAndCos(r, sina, cosa);
    double r2 = pAmount * pContext.random();
    xout += r2 * cosa.value;
    yout += r2 * sina.value;

    Point value = new Point();
    value.setX(xout);
    value.setY(yout);
    return value;
  }

  public Point plotLine(FlameTransformationContext pContext, double x1, double y1, double x2, double y2) {
    double ydiff = y2 - y1;
    double xdiff = x2 - x1;
    double m;
    if (xdiff == 0)
      m = 10000;
    else
      m = ydiff / xdiff; // slope
    double line_length = MathLib.sqrt((xdiff * xdiff) + (ydiff * ydiff));
    double xout = 0, yout = 0;
    double xoffset = 0, yoffset = 0;

    // draw point at a random distance along line
    //    (along straight line connecting endpoint1 and endpoint2)
    double d = pContext.random() * line_length;
    // x = x1 [+-] (d / (sqrt(1 + m^2)))
    // y = y1 [+-] (m * d / (sqrt(1 + m^2)))
    // determine sign based on orientation of endpoint2 relative to endpoint1
    xoffset = d / MathLib.sqrt(1 + m * m);
    if (x2 < x1) {
      xoffset = -1 * xoffset;
    }
    yoffset = Math.abs(m * xoffset);
    if (y2 < y1) {
      yoffset = -1 * yoffset;
    }
    if (line_thickness != 0) {
      xoffset += ((pContext.random() - 0.5) * line_thickness);
      yoffset += ((pContext.random() - 0.5) * line_thickness);
    }
    xout = x1 + xoffset;
    yout = y1 + yoffset;

    Point value = new Point();
    value.setX(xout);
    value.setY(yout);
    return value;
  }


  public Point plotTriangle(FlameTransformationContext pContext, Triangle triangle) {
    Point p1 = triangle.getP1();
    Point p2 = triangle.getP2();
    Point p3 = triangle.getP3();

    // uniform sampling:  http://math.stackexchange.com/questions/18686/uniform-random-point-in-triangle
    double sqrt_r1 = MathLib.sqrt(pContext.random());
    double r2 = pContext.random();
    double a = 1.0 - sqrt_r1;
    double b = sqrt_r1 * (1.0 - r2);
    double c = r2 * sqrt_r1;
    double dx = a * p1.getX() + b * p2.getX() + c * p3.getX();
    double dy = a * p1.getY() + b * p2.getY() + c * p3.getY();
    double dz = 0.0;
    Point out = new Point(dx, dy);
    return out;

  }


  public Point plotPolygon(FlameTransformationContext pContext, Ngon polygon) {
    // nBlur by FractalDesire, http://fractaldesire.deviantart.com/art/nBlur-plugin-190401515
    //*********Adjustment of width of shape*********

    //
    randXY(pContext, _randXYData, polygon);

    double xTmp = _randXYData.x;
    double yTmp = _randXYData.y;

    //**************************************************************************

    double _midAngle = M_2PI / (double) polygon.getSides();
    double _sina, _cosa;
    //*********Prepare edge calculation related stuff*********

    double angle = _midAngle / 2.0;
    _sina = sin(angle);
    _cosa = cos(angle);

    //********Begin of horizontal adjustment (rotation)********
    double x = _cosa * xTmp - _sina * yTmp;
    double y = _sina * xTmp + _cosa * yTmp;
    //*********End of horizontal adjustment (rotation)*********

    double pAmount = polygon.getScale();
    Point pos = polygon.getPos();


    Point p = new Point();

//	  p.setX(  pAmount  * x  +  pos.getX());
// 	  p.setY(  pAmount  * y  +  pos.getY());

    p.setX(x);
    p.setY(y);

    return p;

  }


  private static class RandXYData implements Serializable {
    private static final long serialVersionUID = 1L;
    public double x, y;
    public double lenXY;
    public double lenOuterEdges, lenInnerEdges;
  }

  private void randXY(FlameTransformationContext pContext, RandXYData data, Ngon polygon) {
    double x, y;
    double xTmp, yTmp, lenOuterEdges, lenInnerEdges;
    double angXY, lenXY;
    double ranTmp, angTmp, angMem;
    double ratioTmp, ratioTmpNum, ratioTmpDen;
    double speedCalcTmp;
    int count;

    double ratioHole;


    double _midAngle, _angStripes, _angStart;
    double _tan90_m_2, _sina, _cosa;


    double _arc_tan1, _arc_tan2;
    double _nb_ratioComplement;
    int nEdges = polygon.getSides();
    ratioHole = polygon.getFill();

    if (nEdges < 3)
      nEdges = 3;


    //**********Prepare angle related stuff**********
    _midAngle = M_2PI / (double) nEdges;

    //*********Prepare edge calculation related stuff*********
    _tan90_m_2 = tan(M_PI_2 + _midAngle / 2.0);
    double angle = _midAngle / 2.0;
    _sina = sin(angle);
    _cosa = cos(angle);


    _arc_tan1 = (13.0 / pow(nEdges, 1.3));
    _arc_tan2 = (2.0 * atan(_arc_tan1 / (-2.0)));

    //	   _arc_tan1 = (7.5 / pow(nEdges, 1.3));
    //	   _arc_tan2 = (2.0 * atan(_arc_tan1 / (-2.0)));

    angXY = (atan(_arc_tan1 * (pContext.random() - 0.5)) / _arc_tan2 + 0.5 + (double) (rand(pContext) % nEdges)) * _midAngle;

    x = sin(angXY);
    y = cos(angXY);
    angMem = angXY;

    while (angXY > _midAngle) {
      angXY -= _midAngle;
    }

    //********Begin of calculation of edge limits********
    xTmp = _tan90_m_2 / (_tan90_m_2 - tan(angXY));
    yTmp = xTmp * tan(angXY);
    lenOuterEdges = sqrt(xTmp * xTmp + yTmp * yTmp);
    //*********End of calculation of edge limits********


//	  ranTmp = pContext.random() * lenOuterEdges;
    ranTmp = sqrt(pContext.random()) * lenOuterEdges;

    lenInnerEdges = ratioHole * lenOuterEdges;
    ranTmp = lenInnerEdges + sqrt(pContext.random()) * (lenOuterEdges - lenInnerEdges);

    //if(VAR(hasStripes)==TRUE) ranTmp = pow(ranTmp,0.75);
    x *= ranTmp;
    y *= ranTmp;
    lenXY = sqrt(x * x + y * y);
    //*********End of radius-calculation (optionally hole)*********
    data.x = x;
    data.y = y;
    data.lenXY = lenXY;
    data.lenOuterEdges = lenOuterEdges;
    data.lenInnerEdges = lenInnerEdges;
  }

  public Primitive getPrimitive(FlameTransformationContext pContext) {

    int idx = (int) (pContext.random() * (primitives.size()));

    Primitive primitive = primitives.get(idx);

    return primitive;
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    line_thickness = 0.5 / 100;

  }


  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    Point out = null;

    Primitive primitive = null;
    double color = 0.0;

    primitive = getPrimitive(pContext);

    if (primitive.gettype() == 1) {
      Point point = (Point) primitive;
      out = plotBlur(pContext, point.getX(), point.getY(), pAmount);
      color = point.getColor();
    }
    if (primitive.gettype() == 2) {
      Line line = (Line) primitive;
      out = plotLine(pContext, line.getX1(), line.getY1(), line.getX2(), line.getY2());
      color = line.getColor();
    } else if (primitive.gettype() == 3) {
      Triangle triangle = (Triangle) primitive;
      out = plotTriangle(pContext, triangle);
      color = triangle.getColor();
    } else if (primitive.gettype() == 4) {
      Ngon polygon = (Ngon) primitive;
      out = plotPolygon(pContext, polygon);
      color = polygon.getColor();
      //    pVarTP.x +=  (q.x)*scale*cosa + (q.y)*scale*sina + offset_x;
      //    pVarTP.y += -(q.x)*scale*sina + (q.y)*scale*cosa + offset_y;
    }
    pVarTP.x += pAmount * out.getX();
    pVarTP.y += pAmount * out.getY();
    pVarTP.color = color;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }


  @Override
  public String[] getParameterNames() {
    return null;
  }

  @Override
  public Object[] getParameterValues() {
    return null;
  }

  @Override
  public void setParameter(String pName, double pValue) {

  }

  @Override
  public String getName() {
    return "draw";
  }

}

















