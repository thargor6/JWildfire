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
package org.jwildfire.create.tina.swing.flamepanel;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;

public abstract class AbstractControlShape {
  protected final FlamePanelConfig config;
  protected final XForm xForm;

  public AbstractControlShape(FlamePanelConfig pConfig, XForm pXForm) {
    config = pConfig;
    xForm = pXForm;
  }

  public double getC00() {
    return config.isEditPostTransform() ? xForm.getPostCoeff00() : xForm.getCoeff00();
  }

  public double getC01() {
    return config.isEditPostTransform() ? xForm.getPostCoeff01() : xForm.getCoeff01();
  }

  public double getC10() {
    return config.isEditPostTransform() ? xForm.getPostCoeff10() : xForm.getCoeff10();
  }

  public double getC11() {
    return config.isEditPostTransform() ? xForm.getPostCoeff11() : xForm.getCoeff11();
  }

  public double getC20() {
    return config.isEditPostTransform() ? xForm.getPostCoeff20() : xForm.getCoeff20();
  }

  public double getC21() {
    return config.isEditPostTransform() ? xForm.getPostCoeff21() : xForm.getCoeff21();
  }

  public double affineTransformedX(double pX, double pY) {
    //      return pX * xForm.getCoeff00() + pY * xForm.getCoeff10() + xForm.getCoeff20();
    // use the same layout as Apophysis
    return pX * getC00() + (-pY * getC10()) + getC20();
  }

  public double affineTransformedY(double pX, double pY) {
    //      return pX * xForm.getCoeff01() + pY * xForm.getCoeff11() + xForm.getCoeff21();
    // use the same layout as Apophysis
    return (-pX * getC01()) + pY * getC11() + (-getC21());
  }

  protected int triangleXToView(double pX, double pScale) {
    return Tools.FTOI(config.getTriangleViewXScale() * config.getTriangleZoom() * pScale * pX - config.getTriangleViewXTrans());
  }

  protected int triangleYToView(double pY, double pScale) {
    return Tools.FTOI(config.getTriangleViewYScale() * config.getTriangleZoom() * pScale * pY - config.getTriangleViewYTrans());
  }
}
