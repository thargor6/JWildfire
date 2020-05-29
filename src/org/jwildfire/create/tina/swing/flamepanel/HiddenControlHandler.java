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

import java.awt.Graphics2D;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.swing.FrameControlsUtil;

public class HiddenControlHandler extends AbstractControlHandler<TriangleControlShape> {

  public HiddenControlHandler(FrameControlsUtil pFrameControlsUtil, Prefs pPrefs, FlamePanelConfig pConfig) {
    super(pFrameControlsUtil, pPrefs, pConfig);
  }

  @Override
  public void drawXForm(Graphics2D g, XForm pXForm, int pIndex, int pXFormCount, boolean pIsFinal, boolean pShadow, boolean pIsSelected) {
    // empty
  }

  @Override
  public boolean isInsideXForm(XForm pXForm, int pX, int pY) {
    return false;
  }

  @Override
  public TriangleControlShape convertXFormToShape(XForm pXForm) {
    return new TriangleControlShape(frameControlsUtil, config, pXForm);
  }

  @Override
  public int selectNearestPoint(XForm pXForm, int pViewX, int pViewY) {
    return 1;
  }
}
