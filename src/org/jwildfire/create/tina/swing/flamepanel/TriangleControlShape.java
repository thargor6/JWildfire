/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.swing.FrameControlsUtil;

public class TriangleControlShape extends AbstractControlShape {
  double x[] = new double[3];
  double y[] = new double[3];
  int viewX[] = new int[3];
  int viewY[] = new int[3];

  protected TriangleControlShape(FrameControlsUtil pFrameControlsUtil, FlamePanelConfig pConfig, XForm pXForm, double pX[], double pY[], int pViewX[], int pViewY[]) {
    super(pFrameControlsUtil, pConfig, pXForm);
    System.arraycopy(pX, 0, x, 0, pX.length);
    System.arraycopy(pY, 0, y, 0, pY.length);
    System.arraycopy(pViewX, 0, viewX, 0, pViewX.length);
    System.arraycopy(pViewY, 0, viewY, 0, pViewY.length);
  }

  public TriangleControlShape(FrameControlsUtil pFrameControlsUtil, FlamePanelConfig pConfig, XForm pXForm, double pScale) {
    super(pFrameControlsUtil, pConfig, pXForm);
    // x
    x[0] = 1.0;
    y[0] = 0.0;
    // 0
    x[1] = 0.0;
    y[1] = 0.0;
    // y
    x[2] = 0.0;
    y[2] = 1.0;

    // transform the points (affine transform)
    for (int i = 0; i < x.length; i++) {
      //          
      // use the same layout as Apophysis
      double tx = affineTransformedX(x[i], y[i]);
      double ty = affineTransformedY(x[i], y[i]);
      viewX[i] = triangleXToView(tx, pScale);
      viewY[i] = triangleYToView(ty, pScale);
    }
  }

  public TriangleControlShape(FrameControlsUtil pFrameControlsUtil, FlamePanelConfig pConfig, XForm pXForm) {
    this(pFrameControlsUtil, pConfig, pXForm, 1.0);
  }

}
