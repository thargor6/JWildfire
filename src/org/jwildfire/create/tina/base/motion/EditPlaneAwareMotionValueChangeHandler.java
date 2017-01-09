package org.jwildfire.create.tina.base.motion;

import java.io.Serializable;

import org.jwildfire.create.tina.base.EditPlane;
import org.jwildfire.create.tina.base.XForm;

@SuppressWarnings("serial")
public abstract class EditPlaneAwareMotionValueChangeHandler implements MotionValueChangeHandler, Serializable {

  protected EditPlane getCurrEditPlane(Object pTarget) {
    return ((XForm) pTarget).getOwner().getOwner().getEditPlane();
  }

  protected void setEditPlane(Object pTarget, EditPlane editPlane) {
    ((XForm) pTarget).getOwner().getOwner().setEditPlane(editPlane);
  }

  protected void setEditPlaneByPropertyName(Object pTarget, String pPropertyName) {
    if (pPropertyName.startsWith("yz"))
      setEditPlane(pTarget, EditPlane.YZ);
    else if (pPropertyName.startsWith("zx"))
      setEditPlane(pTarget, EditPlane.ZX);
    else
      setEditPlane(pTarget, EditPlane.XY);
  }
}
