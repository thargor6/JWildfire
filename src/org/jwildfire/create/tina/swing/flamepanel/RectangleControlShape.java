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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.swing.FrameControlsUtil;

public class RectangleControlShape extends AbstractControlShape {
  double x[] = new double[4];
  double y[] = new double[4];
  int viewX[] = new int[4];
  int viewY[] = new int[4];

  public RectangleControlShape(FrameControlsUtil pFrameControlsUtil, FlamePanelConfig pConfig, XForm pXForm) {
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
    // z
    x[3] = 1.0;
    y[3] = 1.0;

    // transform the points (affine transform)
    for (int i = 0; i < x.length; i++) {
      //          
      // use the same layout as Apophysis
      double tx = affineTransformedX(x[i], y[i]);
      double ty = affineTransformedY(x[i], y[i]);
      viewX[i] = triangleXToView(tx, 1.0);
      viewY[i] = triangleYToView(ty, 1.0);
    }
  }

  public List<TriangleControlShape> getTriangles() {
    List<TriangleControlShape> res = new ArrayList<TriangleControlShape>();
    res.add(new TriangleControlShape(frameControlsUtil, config, xForm,
        new double[] { x[0], x[1], x[2] },
        new double[] { y[0], y[1], y[2] },
        new int[] { viewX[0], viewX[1], viewX[2] },
        new int[] { viewY[0], viewY[1], viewY[2] }));
    res.add(new TriangleControlShape(frameControlsUtil, config, xForm,
        new double[] { x[0], x[3], x[2] },
        new double[] { y[0], y[3], y[2] },
        new int[] { viewX[0], viewX[3], viewX[2] },
        new int[] { viewY[0], viewY[3], viewY[2] }));
    return res;
  }
}
