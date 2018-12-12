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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.io.Serializable;

import static org.jwildfire.base.mathlib.MathLib.*;

public class NBlurFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_NUMEDGES = "numEdges";
  public static final String PARAM_NUMSTRIPES = "numStripes";
  public static final String PARAM_RATIOSTRIPES = "ratioStripes";
  public static final String PARAM_RATIOHOLE = "ratioHole";
  public static final String PARAM_CIRCUMCIRCLE = "circumCircle";
  private static final String PARAM_ADJUSTTOLINEAR = "adjustToLinear";
  private static final String PARAM_EQUALBLUR = "equalBlur";
  private static final String PARAM_EXACTCALC = "exactCalc";
  private static final String PARAM_HIGHLIGHTEDGES = "highlightEdges";

  private static final String[] paramNames = {PARAM_NUMEDGES, PARAM_NUMSTRIPES, PARAM_RATIOSTRIPES, PARAM_RATIOHOLE, PARAM_CIRCUMCIRCLE, PARAM_ADJUSTTOLINEAR, PARAM_EQUALBLUR, PARAM_EXACTCALC, PARAM_HIGHLIGHTEDGES};

  private int numEdges = 3;
  private int numStripes = 0;
  private double ratioStripes = 1.0;
  private double ratioHole = 0.0;
  private int circumCircle = 0;
  private int adjustToLinear = 1;
  private int equalBlur = 1;
  private int exactCalc = 0;
  private double highlightEdges = 1.0;
  private RandXYData _randXYData = new RandXYData();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // nBlur by FractalDesire, http://fractaldesire.deviantart.com/art/nBlur-plugin-190401515
    //*********Adjustment of width of shape*********
    if (this.adjustToLinear == TRUE) {
      if ((this.numEdges) % 4 == 0) {
        pAmount /= sqrt(2.0 - 2.0 * cos(this._midAngle * ((double) this.numEdges / 2.0 - 1.0))) / 2.0;
      } else {
        pAmount /= sqrt(2.0 - 2.0 * cos(this._midAngle * floor(((double) this.numEdges / 2.0)))) / 2.0;
      }
    }
    //
    randXY(pContext, _randXYData);

    //********Exact calculation slower - interpolated calculation faster********
    if ((this.exactCalc == TRUE) && (this.circumCircle == FALSE)) {
      while ((_randXYData.lenXY < _randXYData.lenInnerEdges) || (_randXYData.lenXY > _randXYData.lenOuterEdges))
        randXY(pContext, _randXYData);
    }
    if ((this.exactCalc == TRUE) && (this.circumCircle == TRUE)) {
      while (_randXYData.lenXY < _randXYData.lenInnerEdges)
        randXY(pContext, _randXYData);
    }
    double xTmp = _randXYData.x;
    double yTmp = _randXYData.y;

    //**************************************************************************

    //********Begin of horizontal adjustment (rotation)********
    double x = this._cosa * xTmp - this._sina * yTmp;
    double y = this._sina * xTmp + this._cosa * yTmp;
    //*********End of horizontal adjustment (rotation)*********

    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  private static class RandXYData implements Serializable {
    private static final long serialVersionUID = 1L;
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
    if (this.exactCalc == TRUE) {
      angXY = pContext.random() * M_2PI;
    } else {
      angXY = (atan(this._arc_tan1 * (pContext.random() - 0.5)) / this._arc_tan2 + 0.5 + (double) (rand(pContext) % this.numEdges)) * this._midAngle;
    }
    x = sin(angXY);
    y = cos(angXY);
    angMem = angXY;

    while (angXY > this._midAngle) {
      angXY -= this._midAngle;
    }

    //********Begin of xy-calculation of radial stripes******** 
    if (this._hasStripes == TRUE) {
      angTmp = this._angStart;
      count = 0;

      while (angXY > angTmp) {
        angTmp += this._angStripes;
        if (angTmp > this._midAngle)
          angTmp = this._midAngle;
        count++;
      }

      if (angTmp != this._midAngle)
        angTmp -= this._angStart;

      if (this._negStripes == FALSE) {
        if ((count % 2) != 0) {
          if (angXY > angTmp) {
            angXY = angXY + this._angStart;
            angMem = angMem + this._angStart;
            x = sin(angMem);
            y = cos(angMem);
            angTmp += this._angStripes;
            count++;
          } else {
            angXY = angXY - this._angStart;
            angMem = angMem - this._angStart;
            x = sin(angMem);
            y = cos(angMem);
            angTmp -= this._angStripes;
            count--;
          }
        }
        if (((count % 2) == 0) && (this.ratioStripes > 1.0)) {
          if ((angXY > angTmp) && (count != this._maxStripes)) {
            angMem = angMem - angXY + angTmp + (angXY - angTmp) / this._angStart * this.ratioStripes * this._angStart;
            angXY = angTmp + (angXY - angTmp) / this._angStart * this.ratioStripes * this._angStart;
            x = sin(angMem);
            y = cos(angMem);
          } else {
            angMem = angMem - angXY + angTmp - (angTmp - angXY) / this._angStart * this.ratioStripes * this._angStart;
            angXY = angTmp + (angXY - angTmp) / this._angStart * this.ratioStripes * this._angStart;
            x = sin(angMem);
            y = cos(angMem);
          }
        }
        if (((count % 2) == 0) && (this.ratioStripes < 1.0)) {
          if ((fabs(angXY - angTmp) > this._speedCalc2) && (count != (this._maxStripes))) {
            if ((angXY - angTmp) > this._speedCalc2) {
              ratioTmpNum = (angXY - (angTmp + this._speedCalc2)) * this._speedCalc2;
              ratioTmpDen = this._angStart - this._speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp + ratioTmp);
              x = sin(a);
              y = cos(a);
              angXY = angTmp + ratioTmp;
            }
            if ((angTmp - angXY) > this._speedCalc2) {
              ratioTmpNum = ((angTmp - this._speedCalc2 - angXY)) * this._speedCalc2;
              ratioTmpDen = this._angStart - this._speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp - ratioTmp);
              x = sin(a);
              y = cos(a);
              angXY = angTmp - ratioTmp;
            }
          }
          if (count == this._maxStripes) {
            if ((angTmp - angXY) > this._speedCalc2) {
              ratioTmpNum = ((angTmp - this._speedCalc2 - angXY)) * this._speedCalc2;
              ratioTmpDen = this._angStart - this._speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp - ratioTmp);
              x = sin(a);
              y = cos(a);
              angXY = angTmp - ratioTmp;
            }
          }
        }
      } else {
        //********Change ratio and ratioComplement******** 
        ratioTmp = this.ratioStripes;
        this.ratioStripes = this._nb_ratioComplement;
        this._nb_ratioComplement = ratioTmp;
        speedCalcTmp = this._speedCalc1;
        this._speedCalc1 = this._speedCalc2;
        this._speedCalc2 = speedCalcTmp;
        //************************************************ 
        if ((count % 2) == 0) {
          if ((angXY > angTmp) && (count != this._maxStripes)) {
            angXY = angXY + this._angStart;
            angMem = angMem + this._angStart;
            x = sin(angMem);
            y = cos(angMem);
            angTmp += this._angStripes;
            count++;
          } else {
            angXY = angXY - this._angStart;
            angMem = angMem - this._angStart;
            x = sin(angMem);
            y = cos(angMem);
            angTmp -= this._angStripes;
            count--;
          }
        }
        if (((count % 2) != 0) && (this.ratioStripes > 1.0)) {
          if ((angXY > angTmp) && (count != this._maxStripes)) {
            angMem = angMem - angXY + angTmp + (angXY - angTmp) / this._angStart * this.ratioStripes * this._angStart;
            angXY = angTmp + (angXY - angTmp) / this._angStart * this.ratioStripes * this._angStart;
            x = sin(angMem);
            y = cos(angMem);
          } else {
            angMem = angMem - angXY + angTmp - (angTmp - angXY) / this._angStart * this.ratioStripes * this._angStart;
            angXY = angTmp + (angXY - angTmp) / this._angStart * this.ratioStripes * this._angStart;
            x = sin(angMem);
            y = cos(angMem);
          }
        }
        if (((count % 2) != 0) && (this.ratioStripes < 1.0)) {
          if ((fabs(angXY - angTmp) > this._speedCalc2) && (count != this._maxStripes)) {
            if ((angXY - angTmp) > this._speedCalc2) {
              ratioTmpNum = (angXY - (angTmp + this._speedCalc2)) * this._speedCalc2;
              ratioTmpDen = this._angStart - this._speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp + ratioTmp);
              x = sin(a);
              y = cos(a);
              angXY = angTmp + ratioTmp;
            }
            if ((angTmp - angXY) > this._speedCalc2) {
              ratioTmpNum = ((angTmp - this._speedCalc2 - angXY)) * this._speedCalc2;
              ratioTmpDen = this._angStart - this._speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp - ratioTmp);
              x = sin(a);
              y = cos(a);
              angXY = angTmp - ratioTmp;
            }
          }
          if (count == this._maxStripes) {
            angTmp = this._midAngle;
            if ((angTmp - angXY) > this._speedCalc2) {
              ratioTmpNum = ((angTmp - this._speedCalc2 - angXY)) * this._speedCalc2;
              ratioTmpDen = this._angStart - this._speedCalc2;
              ratioTmp = ratioTmpNum / ratioTmpDen;
              double a = (angMem - angXY + angTmp - ratioTmp);
              x = sin(a);
              y = cos(a);
              angXY = angTmp - ratioTmp;
            }
          }
        }
        //********Restore ratio and ratioComplement******* 
        ratioTmp = this.ratioStripes;
        this.ratioStripes = this._nb_ratioComplement;
        this._nb_ratioComplement = ratioTmp;
        speedCalcTmp = this._speedCalc1;
        this._speedCalc1 = this._speedCalc2;
        this._speedCalc2 = speedCalcTmp;
        //************************************************ 
      }
    }
    //********End of xy-calculation of radial stripes********

    //********Begin of calculation of edge limits********
    xTmp = this._tan90_m_2 / (this._tan90_m_2 - tan(angXY));
    yTmp = xTmp * tan(angXY);
    lenOuterEdges = sqrt(xTmp * xTmp + yTmp * yTmp);
    //*********End of calculation of edge limits********

    //********Begin of radius-calculation (optionally hole)********
    if (this.exactCalc == TRUE) {
      if (this.equalBlur == TRUE)
        ranTmp = sqrt(pContext.random());
      else
        ranTmp = pContext.random();
    } else {
      if (this.circumCircle == TRUE) {
        if (this.equalBlur == TRUE)
          ranTmp = sqrt(pContext.random());
        else
          ranTmp = pContext.random();
      } else {
        if (this.equalBlur == TRUE)
          ranTmp = sqrt(pContext.random()) * lenOuterEdges;
        else
          ranTmp = pContext.random() * lenOuterEdges;
      }
    }
    lenInnerEdges = this.ratioHole * lenOuterEdges;

    if (this.exactCalc == FALSE) {
      if (ranTmp < lenInnerEdges) {
        if (this.circumCircle == TRUE) {
          if (this.equalBlur == TRUE)
            ranTmp = lenInnerEdges + sqrt(pContext.random()) * (1.0 - lenInnerEdges + SMALL_EPSILON);
          else
            ranTmp = lenInnerEdges + pContext.random() * (1.0 - lenInnerEdges + SMALL_EPSILON);
        } else {
          if (this.equalBlur == TRUE)
            ranTmp = lenInnerEdges + sqrt(pContext.random()) * (lenOuterEdges - lenInnerEdges);
          else
            ranTmp = lenInnerEdges + pContext.random() * (lenOuterEdges - lenInnerEdges);
        }
      }
    }

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
    return new String[]{"nb_numEdges", "nb_numStripes", "nb_ratioStripes", "nb_ratioHole", "nb_circumCircle", "nb_adjustToLinear", "nb_equalBlur", "nb_exactCalc", "nb_highlightEdges"};
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{numEdges, numStripes, ratioStripes, ratioHole, circumCircle, adjustToLinear, equalBlur, exactCalc, highlightEdges};
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

  private double _midAngle, _angStripes, _angStart;
  private double _tan90_m_2, _sina, _cosa;
  private int _hasStripes, _negStripes;
  //**********Variables for speed up***********
  private double _speedCalc1, _speedCalc2;
  private double _maxStripes;
  private double _arc_tan1, _arc_tan2;
  private double _nb_ratioComplement;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    if (this.numEdges < 3)
      this.numEdges = 3;

    //*********Prepare stripes related stuff*********
    if (this.numStripes != 0) {
      this._hasStripes = TRUE;
      if (this.numStripes < 0) {
        this._negStripes = TRUE;
        this.numStripes *= -1;
      } else {
        this._negStripes = FALSE;
      }
    } else {
      this._hasStripes = FALSE;
      this._negStripes = FALSE;
    }

    //**********Prepare angle related stuff**********
    this._midAngle = M_2PI / (double) this.numEdges;
    if (this._hasStripes == TRUE) {
      this._angStripes = this._midAngle / (double) (2 * this.numStripes);
      this._angStart = this._angStripes / 2.0;
      this._nb_ratioComplement = 2.0 - this.ratioStripes;
    }

    //**********Prepare hole related stuff***********
    if ((this.ratioHole > 0.95) && (this.exactCalc == TRUE) && (this.circumCircle == FALSE))
      this.ratioHole = 0.95;

    //*********Prepare edge calculation related stuff*********
    this._tan90_m_2 = tan(M_PI_2 + this._midAngle / 2.0);
    double angle = this._midAngle / 2.0;
    this._sina = sin(angle);
    this._cosa = cos(angle);

    //*********Prepare factor of adjustment of interpolated calculation*********
    if (this.highlightEdges <= 0.1)
      this.highlightEdges = 0.1;

    //*********Prepare circumCircle-calculation*********
    if (this.circumCircle == TRUE) {
      this.exactCalc = FALSE;
      this.highlightEdges = 0.1;
    }

    //*********Prepare speed up related stuff*********
    this._speedCalc1 = this._nb_ratioComplement * this._angStart;
    this._speedCalc2 = this.ratioStripes * this._angStart;
    this._maxStripes = 2 * this.numStripes;
    if (this._negStripes == FALSE) {
      this._arc_tan1 = (13.0 / pow(this.numEdges, 1.3)) * this.highlightEdges;
      this._arc_tan2 = (2.0 * atan(this._arc_tan1 / (-2.0)));
    } else {
      this._arc_tan1 = (7.5 / pow(this.numEdges, 1.3)) * this.highlightEdges;
      this._arc_tan2 = (2.0 * atan(this._arc_tan1 / (-2.0)));
    }
  }

}
