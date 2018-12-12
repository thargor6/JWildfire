/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.variation.iflames;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.dance.model.AnimationModelService;
import org.jwildfire.create.tina.dance.model.FlamePropertyPath;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.SubFlameWFFunc;
import org.jwildfire.create.tina.variation.VariationFunc;

import java.util.List;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

public class IFlamesIterator {
  private final XYZPoint affine, var, ifsPoint;
  private final VariationFunc fnc;
  private final XForm xform;
  private final FlameTransformationContext flameTransformationContext;
  private final FlameParams params;
  private final double rotateMatrix[][] = new double[3][3];

  public final static IFlamesIterator BLANK = new IFlamesIterator();

  public IFlamesIterator(FlameTransformationContext pFlameTransformationContext, FlameParams pFlameParams, List<String> pParamNames, List<Double> pParamValues) {
    flameTransformationContext = pFlameTransformationContext;
    params = pFlameParams;

    affine = new XYZPoint();
    var = new XYZPoint();
    ifsPoint = new XYZPoint();
    xform = new XForm();
    fnc = new SubFlameWFFunc();
    String flameXML = params.getFlameXML();
    if (pParamNames != null && pParamNames.size() > 0) {
      try {
        Flame flame = new FlameReader(Prefs.getPrefs()).readFlamesfromXML(flameXML).get(0);
        for (int i = 0; i < pParamNames.size(); i++) {
          FlamePropertyPath path = new FlamePropertyPath(flame, pParamNames.get(i));
          AnimationModelService.setFlameProperty(flame, path, pParamValues.get(i));
        }
        flameXML = new FlameWriter().getFlameXML(flame);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    fnc.setRessource(SubFlameWFFunc.RESSOURCE_FLAME, flameXML.getBytes());
    fnc.initOnce(pFlameTransformationContext, new Layer(), xform, 1.0);
    fnc.init(pFlameTransformationContext, new Layer(), xform, 1.0);
  }

  private IFlamesIterator() {
    affine = var = ifsPoint = null;
    fnc = null;
    xform = null;
    flameTransformationContext = null;
    params = null;
  }

  public void iterate(XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP) {
    fnc.transform(flameTransformationContext, pXForm, pAffineTP, pVarTP, 1.0);
  }

  public void iterate(XYZPoint pSrc, XYZPoint pDest, double pSize, double pAngleAlpha, double pAngleBeta, double pAngleGamma) {
    affine.clear();
    var.clear();

    double dr = pSize;
    double centreX = params.getCentreX();
    double centreY = params.getCentreY();

    fnc.transform(flameTransformationContext, xform, affine, var, 1.0);

    if (fabs(pAngleBeta) < EPSILON && fabs(pAngleGamma) < EPSILON) {
      double dx = var.x - centreX;
      double dy = var.y - centreY;
      double sina = MathLib.sin(pAngleAlpha);
      double cosa = MathLib.cos(pAngleAlpha);
      double rx = dx * cosa - dy * sina;
      double ry = dx * sina + dy * cosa;
      ifsPoint.x = rx * dr + centreX;
      ifsPoint.y = ry * dr + centreY;
      ifsPoint.z = var.z;
    } else {
      double sina = MathLib.sin(pAngleAlpha);
      double cosa = MathLib.cos(pAngleAlpha);
      double sinb = MathLib.sin(pAngleBeta);
      double cosb = MathLib.cos(pAngleBeta);
      double sing = MathLib.sin(pAngleGamma);
      double cosg = MathLib.cos(pAngleGamma);

      rotateMatrix[0][0] = cosb * cosa - sina * sinb * cosg;
      rotateMatrix[1][0] = -sinb * cosa - cosb * cosg * sina;
      rotateMatrix[2][0] = sina * sing;
      rotateMatrix[0][1] = cosb * sina + cosa * sinb * cosg;
      rotateMatrix[1][1] = -sinb * sina + cosb * cosg * cosa;
      rotateMatrix[2][1] = -cosa * sing;
      rotateMatrix[0][2] = sinb * sing;
      rotateMatrix[1][2] = cosb * sing;
      rotateMatrix[2][2] = cosg;

      double centreZ = params.getCentreZ();
      double dx = (var.x - centreX) * dr;
      double dy = (var.y - centreY) * dr;
      double dz = (var.z - centreZ) * dr;

      ifsPoint.x = rotateMatrix[0][0] * dx + rotateMatrix[1][0] * dy + rotateMatrix[2][0] * dz + centreX;
      ifsPoint.y = rotateMatrix[0][1] * dx + rotateMatrix[1][1] * dy + rotateMatrix[2][1] * dz + centreY;
      ifsPoint.z = rotateMatrix[0][2] * dx + rotateMatrix[1][2] * dy + rotateMatrix[2][2] * dz + centreZ;
    }

    pDest.x = ifsPoint.x + pSrc.x;
    pDest.y = ifsPoint.y + pSrc.y;
    pDest.z = ifsPoint.z + pSrc.z;

    pDest.redColor = var.redColor;
    pDest.greenColor = var.greenColor;
    pDest.blueColor = var.blueColor;
    pDest.rgbColor = var.rgbColor;
    pDest.color = var.color;
  }
}
