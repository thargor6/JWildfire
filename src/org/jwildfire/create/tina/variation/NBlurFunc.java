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
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class NBlurFunc extends VariationFunc {

  private static final String PARAM_NUMEDGES = "numEdges";
  private static final String PARAM_NUMSTRIPES = "numStripes";
  private static final String PARAM_RATIOSTRIPES = "ratioStripes";
  private static final String PARAM_RATIOHOLE = "ratioHole";
  private static final String PARAM_CIRCUMCIRCLE = "circumCircle";
  private static final String PARAM_ADJUSTTOLINEAR = "adjustToLinear";
  private static final String PARAM_EQUALBLUR = "equalBlur";
  private static final String PARAM_EXACTCALC = "exactCalc";
  private static final String PARAM_HIGHLIGHTEDGES = "highlightEdges";

  private static final String[] paramNames = { PARAM_NUMEDGES, PARAM_NUMSTRIPES, PARAM_RATIOSTRIPES, PARAM_RATIOHOLE, PARAM_CIRCUMCIRCLE, PARAM_ADJUSTTOLINEAR, PARAM_EQUALBLUR, PARAM_EXACTCALC, PARAM_HIGHLIGHTEDGES };

  private int numEdges = 3;
  private int numStripes = 0;
  private double ratioStripes = 1.0;
  private double ratioHole = 0.0;
  private int circumCircle = 0;
  private int adjustToLinear = 1;
  private int equalBlur = 1;
  private int exactCalc = 0;
  private double highlightEdges = 1.0;
  private RandXYData randXYData = new RandXYData();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // nBlur by FractalDesire, http://fractaldesire.deviantart.com/art/nBlur-plugin-190401515
    //*********Adjustment of width of shape*********
    if (this.adjustToLinear == Constants.TRUE) {
      if ((this.numEdges) % 4 == 0) {
        pAmount /= Math.sqrt(2.0 - 2.0 * Math.cos(this.midAngle * ((double) this.numEdges / 2.0 - 1.0))) / 2.0;
      }
      else
      {
        pAmount /= Math.sqrt(2.0 - 2.0 * Math.cos(this.midAngle * Math.floor(((double) this.numEdges / 2.0)))) / 2.0;
      }
    }
    //
    randXY(pContext, randXYData);

    //********Exact calculation slower - interpolated calculation faster********
    if ((this.exactCalc == Constants.TRUE) && (this.circumCircle == Constants.FALSE))
    {
      while ((randXYData.lenXY < randXYData.lenInnerEdges) || (randXYData.lenXY > randXYData.lenOuterEdges))
        randXY(pContext, randXYData);
    }
    if ((this.exactCalc == Constants.TRUE) && (this.circumCircle == Constants.TRUE))
    {
      while (randXYData.lenXY < randXYData.lenInnerEdges)
        randXY(pContext, randXYData);
    }
    double xTmp = randXYData.x;
    double yTmp = randXYData.y;

    //**************************************************************************

    //********Begin of horizontal adjustment (rotation)********
    double x = this.cosa * xTmp - this.sina * yTmp;
    double y = this.sina * xTmp + this.cosa * yTmp;
    //*********End of horizontal adjustment (rotation)*********

    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;

  }

  private static class RandXYData {
    public double x, y;
    public double lenXY;
    public double lenOuterEdges, lenInnerEdges;
  }

  private void randXY(FlameTransformationContext pContext, RandXYData data) {
    double x, y;
    double xTmp, yTmp, lenOuterEdges, lenInnerEdges;
    double angXY, lenXY;
    double ranTmp, angTmp, angMem;
    double ratioTmp, ratioTmpNum, ratioTmpDen;
    double speedCalcTmp;
    int count;
    if (this.exactCalc == Constants.TRUE)
    {
      angXY = pContext.random() * Constants.M_2PI;
    }
    else
    {
      angXY = (Math.atan(this.arc_tan1 * (pContext.random() - 0.5)) / this.arc_tan2 + 0.5 + (double) (rand(pContext) % this.numEdges)) * this.midAngle;
    }
    x = Math.sin(angXY);
    y = Math.cos(angXY);
    angMem = angXY;

    while (angXY > this.midAngle)
    {
      angXY -= this.midAngle;
    }

    //********Begin of xy-calculation of radial stripes******** 
    if (this.hasStripes == Constants.TRUE)
    {
      angTmp = this.angStart;
      count = 0;

      while (angXY > angTmp)
      {
        angTmp += this.angStripes;
        if (angTmp > this.midAngle)
          angTmp = this.midAngle;
        count++;
      }

      if (angTmp != this.midAngle)
        angTmp -= this.angStart;

      if (this.negStripes == Constants.FALSE)
      {
        if ((count % 2) == 1)
        {
          if (angXY > angTmp)
          {
            angXY = angXY + this.angStart;
            angMem = angMem + this.angStart;
            x = Math.sin(angMem);
            y = Math.cos(angMem);
            angTmp += this.angStripes;
            count++;
          }
          else
          {
            angXY = angXY - this.angStart;
            angMem = angMem - this.angStart;
            x = Math.sin(angMem);
            y = Math.cos(angMem);
            angTmp -= this.angStripes;
            count--;
          }
        }
        if (((count % 2) == 0) && (this.ratioStripes > 1.0))
        {
          if ((angXY > angTmp) && (count != this.maxStripes))
          {
            angMem = angMem - angXY + angTmp + (angXY - angTmp) / this.angStart * this.ratioStripes * this.angStart;
            angXY = angTmp + (angXY - angTmp) / this.angStart * this.ratioStripes * this.angStart;
            x = Math.sin(angMem);
            y = Math.cos(angMem);
          }
          else
          {
            angMem = angMem - angXY + angTmp - (angTmp - angXY) / this.angStart * this.ratioStripes * this.angStart;
            angXY = angTmp + (angXY - angTmp) / this.angStart * this.ratioStripes * this.angStart;
            x = Math.sin(angMem);
            y = Math.cos(angMem);
          }
        }
        if (((count % 2) == 0) && (this.ratioStripes < 1.0))
        {
          if ((Math.abs(angXY - angTmp) > this.speedCalc2) && (count != (this.maxStripes)))
          {
            if ((angXY - angTmp) > this.speedCalc2)
            {
              ratioTmpNum = (angXY - (angTmp + this.speedCalc2)) * this.speedCalc2;
              ratioTmpDen = this.angStart - this.speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp + ratioTmp);
              x = Math.sin(a);
              y = Math.cos(a);
              angXY = angTmp + ratioTmp;
            }
            if ((angTmp - angXY) > this.speedCalc2)
            {
              ratioTmpNum = ((angTmp - this.speedCalc2 - angXY)) * this.speedCalc2;
              ratioTmpDen = this.angStart - this.speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp - ratioTmp);
              x = Math.sin(a);
              y = Math.cos(a);
              angXY = angTmp - ratioTmp;
            }
          }
          if (count == this.maxStripes)
          {
            if ((angTmp - angXY) > this.speedCalc2)
            {
              ratioTmpNum = ((angTmp - this.speedCalc2 - angXY)) * this.speedCalc2;
              ratioTmpDen = this.angStart - this.speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp - ratioTmp);
              x = Math.sin(a);
              y = Math.cos(a);
              angXY = angTmp - ratioTmp;
            }
          }
        }
      }
      else
      {
        //********Change ratio and ratioComplement******** 
        ratioTmp = this.ratioStripes;
        this.ratioStripes = this.nb_ratioComplement;
        this.nb_ratioComplement = ratioTmp;
        speedCalcTmp = this.speedCalc1;
        this.speedCalc1 = this.speedCalc2;
        this.speedCalc2 = speedCalcTmp;
        //************************************************ 
        if ((count % 2) == 0)
        {
          if ((angXY > angTmp) && (count != this.maxStripes))
          {
            angXY = angXY + this.angStart;
            angMem = angMem + this.angStart;
            x = Math.sin(angMem);
            y = Math.cos(angMem);
            angTmp += this.angStripes;
            count++;
          }
          else
          {
            angXY = angXY - this.angStart;
            angMem = angMem - this.angStart;
            x = Math.sin(angMem);
            y = Math.cos(angMem);
            angTmp -= this.angStripes;
            count--;
          }
        }
        if (((count % 2) == 1) && (this.ratioStripes > 1.0))
        {
          if ((angXY > angTmp) && (count != this.maxStripes))
          {
            angMem = angMem - angXY + angTmp + (angXY - angTmp) / this.angStart * this.ratioStripes * this.angStart;
            angXY = angTmp + (angXY - angTmp) / this.angStart * this.ratioStripes * this.angStart;
            x = Math.sin(angMem);
            y = Math.cos(angMem);
          }
          else
          {
            angMem = angMem - angXY + angTmp - (angTmp - angXY) / this.angStart * this.ratioStripes * this.angStart;
            angXY = angTmp + (angXY - angTmp) / this.angStart * this.ratioStripes * this.angStart;
            x = Math.sin(angMem);
            y = Math.cos(angMem);
          }
        }
        if (((count % 2) == 1) && (this.ratioStripes < 1.0))
        {
          if ((Math.abs(angXY - angTmp) > this.speedCalc2) && (count != this.maxStripes))
          {
            if ((angXY - angTmp) > this.speedCalc2)
            {
              ratioTmpNum = (angXY - (angTmp + this.speedCalc2)) * this.speedCalc2;
              ratioTmpDen = this.angStart - this.speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp + ratioTmp);
              x = Math.sin(a);
              y = Math.cos(a);
              angXY = angTmp + ratioTmp;
            }
            if ((angTmp - angXY) > this.speedCalc2)
            {
              ratioTmpNum = ((angTmp - this.speedCalc2 - angXY)) * this.speedCalc2;
              ratioTmpDen = this.angStart - this.speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp - ratioTmp);
              x = Math.sin(a);
              y = Math.cos(a);
              angXY = angTmp - ratioTmp;
            }
          }
          if (count == this.maxStripes)
          {
            angTmp = this.midAngle;
            if ((angTmp - angXY) > this.speedCalc2)
            {
              ratioTmpNum = ((angTmp - this.speedCalc2 - angXY)) * this.speedCalc2;
              ratioTmpDen = this.angStart - this.speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp - ratioTmp);
              x = Math.sin(a);
              y = Math.cos(a);
              angXY = angTmp - ratioTmp;
            }
          }
        }
        //********Restore ratio and ratioComplement******* 
        ratioTmp = this.ratioStripes;
        this.ratioStripes = this.nb_ratioComplement;
        this.nb_ratioComplement = ratioTmp;
        speedCalcTmp = this.speedCalc1;
        this.speedCalc1 = this.speedCalc2;
        this.speedCalc2 = speedCalcTmp;
        //************************************************ 
      }
    }
    //********End of xy-calculation of radial stripes********

    //********Begin of calculation of edge limits********
    xTmp = this.tan90_m_2 / (this.tan90_m_2 - Math.tan(angXY));
    yTmp = xTmp * Math.tan(angXY);
    lenOuterEdges = Math.sqrt(xTmp * xTmp + yTmp * yTmp);
    //*********End of calculation of edge limits********

    //********Begin of radius-calculation (optionally hole)********
    if (this.exactCalc == Constants.TRUE)
    {
      if (this.equalBlur == Constants.TRUE)
        ranTmp = Math.sqrt(pContext.random());
      else
        ranTmp = pContext.random();
    }
    else
    {
      if (this.circumCircle == Constants.TRUE)
      {
        if (this.equalBlur == Constants.TRUE)
          ranTmp = Math.sqrt(pContext.random());
        else
          ranTmp = pContext.random();
      }
      else
      {
        if (this.equalBlur == Constants.TRUE)
          ranTmp = Math.sqrt(pContext.random()) * lenOuterEdges;
        else
          ranTmp = pContext.random() * lenOuterEdges;
      }
    }
    lenInnerEdges = this.ratioHole * lenOuterEdges;

    if (this.exactCalc == Constants.FALSE)
    {
      if (ranTmp < lenInnerEdges)
      {
        if (this.circumCircle == Constants.TRUE)
        {
          if (this.equalBlur == Constants.TRUE)
            ranTmp = lenInnerEdges + Math.sqrt(pContext.random()) * (1.0 - lenInnerEdges + Constants.EPSILON);
          else
            ranTmp = lenInnerEdges + pContext.random() * (1.0 - lenInnerEdges + Constants.EPSILON);
        }
        else
        {
          if (this.equalBlur == Constants.TRUE)
            ranTmp = lenInnerEdges + Math.sqrt(pContext.random()) * (lenOuterEdges - lenInnerEdges);
          else
            ranTmp = lenInnerEdges + pContext.random() * (lenOuterEdges - lenInnerEdges);
        }
      }
    }

    //if(VAR(hasStripes)==TRUE) ranTmp = pow(ranTmp,0.75);
    x *= ranTmp;
    y *= ranTmp;
    lenXY = Math.sqrt(x * x + y * y);
    //*********End of radius-calculation (optionally hole)*********
    data.x = x;
    data.y = y;
    data.lenXY = lenXY;
    data.lenOuterEdges = lenOuterEdges;
    data.lenInnerEdges = lenInnerEdges;
  }

  private static final int RAND_MAX = 32767;

  private int rand(FlameTransformationContext pContext) {
    return pContext.random(RAND_MAX);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[] { "nb_numEdges", "nb_numStripes", "nb_ratioStripes", "nb_ratioHole", "nb_circumCircle", "nb_adjustToLinear", "nb_equalBlur", "nb_exactCalc", "nb_highlightEdges" };
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { numEdges, numStripes, ratioStripes, ratioHole, circumCircle, adjustToLinear, equalBlur, exactCalc, highlightEdges };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_NUMEDGES.equalsIgnoreCase(pName))
      numEdges = Tools.FTOI(pValue);
    else if (PARAM_NUMSTRIPES.equalsIgnoreCase(pName))
      numStripes = Tools.FTOI(pValue);
    else if (PARAM_RATIOSTRIPES.equalsIgnoreCase(pName))
      ratioStripes = limitVal(pValue, 0.0, 2.0);
    else if (PARAM_RATIOHOLE.equalsIgnoreCase(pName))
      ratioHole = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_CIRCUMCIRCLE.equalsIgnoreCase(pName))
      circumCircle = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_ADJUSTTOLINEAR.equalsIgnoreCase(pName))
      adjustToLinear = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_EQUALBLUR.equalsIgnoreCase(pName))
      equalBlur = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_EXACTCALC.equalsIgnoreCase(pName))
      exactCalc = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_HIGHLIGHTEDGES.equalsIgnoreCase(pName))
      highlightEdges = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "nBlur";
  }

  private double midAngle, angStripes, angStart;
  private double tan90_m_2, sina, cosa;
  private int hasStripes, negStripes;
  //**********Variables for speed up***********
  private double speedCalc1, speedCalc2;
  private double maxStripes;
  private double arc_tan1, arc_tan2;
  private double nb_ratioComplement;

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm) {
    if (this.numEdges < 3)
      this.numEdges = 3;

    //*********Prepare stripes related stuff*********
    if (this.numStripes != 0) {
      this.hasStripes = Constants.TRUE;
      if (this.numStripes < 0) {
        this.negStripes = Constants.TRUE;
        this.numStripes *= -1;
      }
      else
      {
        this.negStripes = Constants.FALSE;
      }
    }
    else
    {
      this.hasStripes = Constants.FALSE;
      this.negStripes = Constants.FALSE;
    }

    //**********Prepare angle related stuff**********
    this.midAngle = Constants.M_2PI / (double) this.numEdges;
    if (this.hasStripes == Constants.TRUE) {
      this.angStripes = this.midAngle / (double) (2 * this.numStripes);
      this.angStart = this.angStripes / 2.0;
      this.nb_ratioComplement = 2.0 - this.ratioStripes;
    }

    //**********Prepare hole related stuff***********
    if ((this.ratioHole > 0.95) && (this.exactCalc == Constants.TRUE) && (this.circumCircle == Constants.FALSE))
      this.ratioHole = 0.95;

    //*********Prepare edge calculation related stuff*********
    this.tan90_m_2 = Math.tan(Constants.M_PI_2 + this.midAngle / 2.0);
    double angle = this.midAngle / 2.0;
    this.sina = Math.sin(angle);
    this.cosa = Math.cos(angle);

    //*********Prepare factor of adjustment of interpolated calculation*********
    if (this.highlightEdges <= 0.1)
      this.highlightEdges = 0.1;

    //*********Prepare circumCircle-calculation*********
    if (this.circumCircle == Constants.TRUE)
    {
      this.exactCalc = Constants.FALSE;
      this.highlightEdges = 0.1;
    }

    //*********Prepare speed up related stuff*********
    this.speedCalc1 = this.nb_ratioComplement * this.angStart;
    this.speedCalc2 = this.ratioStripes * this.angStart;
    this.maxStripes = 2 * this.numStripes;
    if (this.negStripes == Constants.FALSE)
    {
      this.arc_tan1 = (13.0 / Math.pow(this.numEdges, 1.3)) * this.highlightEdges;
      this.arc_tan2 = (2.0 * Math.atan(this.arc_tan1 / (-2.0)));
    }
    else
    {
      this.arc_tan1 = (7.5 / Math.pow(this.numEdges, 1.3)) * this.highlightEdges;
      this.arc_tan2 = (2.0 * Math.atan(this.arc_tan1 / (-2.0)));
    }
  }
}
