/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.leapmotion;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;

public enum LeapMotionOutputChannel {
  XFORM_RESET {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setCoeff00(1.0);
        xform.setCoeff01(0.0);
        xform.setCoeff10(0.0);
        xform.setCoeff11(1.0);
      }
    }
  },
  XFORM_MOVE_X {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
        pProperty.setOffset(xform.getCoeff20());
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setCoeff20(pValue);
      }
    }
  },
  XFORM_MOVE_Y {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
        pProperty.setOffset(xform.getCoeff21());
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setCoeff21(pValue);
      }
    }
  },
  XFORM_ROTATE {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        XFormTransformService.rotate(xform, pValue, false);
      }
    }
  },
  XFORM_SCALE {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        XFormTransformService.scale(xform, pValue, true, true, false);
      }
    }
  },
  XFORM_RESET_POST {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setPostCoeff00(1.0);
        xform.setPostCoeff01(0.0);
        xform.setPostCoeff10(0.0);
        xform.setPostCoeff11(1.0);
      }
    }
  },
  XFORM_MOVE_X_POST {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
        pProperty.setOffset(xform.getPostCoeff20());
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setPostCoeff20(pValue);
      }
    }
  },
  XFORM_MOVE_Y_POST {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
        pProperty.setOffset(xform.getPostCoeff21());
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        xform.setPostCoeff21(pValue);
      }
    }
  },
  XFORM_ROTATE_POST {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        XFormTransformService.rotate(xform, pValue, true);
      }
    }
  },
  XFORM_SCALE_POST {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        pProperty.setEnabled(true);
      }
      else {
        pProperty.setEnabled(false);
      }
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      XForm xform = getXFormByIndex(pProperty, pFlame);
      if (xform != null) {
        XFormTransformService.scale(xform, pValue, true, true, true);
      }
    }
  },
  CAM_MOVE_X {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getCamPosX());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setCamPosX(pValue);
    }
  },
  CAM_MOVE_Y {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getCamPosX());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setCamPosY(pValue);
    }
  },
  CAM_ZOOM {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getPixelsPerUnit());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setPixelsPerUnit(pValue);
    }
  },
  CAM_ROLL {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getCamRoll());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setCamRoll(pValue);
    }
  },
  CAM_PITCH {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getCamPitch());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setCamPitch(pValue);
    }
  },
  CAM_YAW {
    @Override
    public void init(LeapMotionConnectedProperty pProperty, Flame pFlame) {
      pProperty.setEnabled(true);
      pProperty.setOffset(pFlame.getCamYaw());
    }

    @Override
    public void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue) {
      pFlame.setCamYaw(pValue);
    }
  };

  public abstract void init(LeapMotionConnectedProperty pProperty, Flame pFlame);

  private static XForm getXFormByIndex(LeapMotionConnectedProperty pProperty, Flame pFlame) {
    Layer layer = pFlame.getFirstLayer();
    if (pProperty.getIndex() >= 0 && pProperty.getIndex() < layer.getXForms().size()) {
      return layer.getXForms().get(pProperty.getIndex());
    }
    else {
      return null;
    }
  }

  public abstract void apply(LeapMotionConnectedProperty pProperty, Flame pFlame, double pValue);

}
