package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.sqrt;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XYZProjectedPoint;

public class DrawFocusPointFlameRenderer extends FlameRenderer {

  public DrawFocusPointFlameRenderer(Flame pFlame, Prefs pPrefs, boolean pWithAlpha) {
    super(pFlame, pPrefs, pWithAlpha, false);
  }

  @Override
  public boolean project(XYZPoint pPoint, XYZProjectedPoint pProjectedPoint) {
    double z = pPoint.z;
    double px = cameraMatrix[0][0] * pPoint.x + cameraMatrix[1][0] * pPoint.y /*+ cameraMatrix[2][0] * z*/;
    double py = cameraMatrix[0][1] * pPoint.x + cameraMatrix[1][1] * pPoint.y + cameraMatrix[2][1] * z;
    double pz = cameraMatrix[0][2] * pPoint.x + cameraMatrix[1][2] * pPoint.y + cameraMatrix[2][2] * z;
    double zr = 1.0 - flame.getCamPerspective() * pz;

    double xdist = (px - flame.getFocusX());
    double ydist = (py - flame.getFocusY());
    double zdist = (pz - flame.getFocusZ());
    double dist = sqrt(xdist * xdist + ydist * ydist + zdist * zdist);

    if (dist > 0.12 + flame.getCamDOFArea()) {
      // dont draw outer points
      pPoint.x = 10000 + 10000 * Math.random();
      pPoint.y = 10000 + 10000 * Math.random();
    }
    else {
      pPoint.x = px / zr;
      pPoint.y = py / zr;
    }
    pProjectedPoint.intensity = 1.0;
    pProjectedPoint.x = pPoint.x * cosa + pPoint.y * sina + rcX;
    if ((pProjectedPoint.x < 0) || (pProjectedPoint.x > camW))
      return false;
    pProjectedPoint.y = pPoint.y * cosa - pPoint.x * sina + rcY;
    if ((pProjectedPoint.y < 0) || (pProjectedPoint.y > camH))
      return false;
    return true;
  }

}
