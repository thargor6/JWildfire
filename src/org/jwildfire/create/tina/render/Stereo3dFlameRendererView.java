package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Stereo3dEye;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class Stereo3dFlameRendererView extends FlameRendererView {
  private XYZPoint eyeOff;
  private double sinEye, cosEye, focalOffset;

  public Stereo3dFlameRendererView(Stereo3dEye pEye, Flame pFlame, AbstractRandomGenerator pRandGen, int pBorderWidth, int pImageWidth, int pImageHeight, int pRasterWidth, int pRasterHeight, FlameTransformationContext pFlameTransformationContext, RenderInfo renderInfo) {
    super(pEye, pFlame, pRandGen, pBorderWidth, pImageWidth, pImageHeight, pRasterWidth, pRasterHeight, pFlameTransformationContext, renderInfo);
  }

  @Override
  public void initView() {
    super.initView();
    focalOffset = flame.getStereo3dFocalOffset();

    double eyeDist = 0.0;
    double eyeAngle = 0.0;
    switch (eye) {
      case LEFT:
        eyeAngle = flame.getAnaglyph3dAngle() * M_PI / 180.0;
        eyeDist = -flame.getAnaglyph3dEyeDist();
        break;
      case RIGHT:
        eyeAngle = -flame.getAnaglyph3dAngle() * M_PI / 180.0;
        eyeDist = flame.getAnaglyph3dEyeDist();
        break;
      default: // nothing to do
        break;
    }
    sinEye = sin(eyeAngle);
    cosEye = cos(eyeAngle);
    // always force 3d projection
    doProject3D = true;

    this.eyeOff = new XYZPoint();
    this.eyeOff.x = eyeDist;
    super.applyCameraMatrix(this.eyeOff);
  }

  @Override
  protected void applyCameraMatrix(XYZPoint pPoint) {
    super.applyCameraMatrix(pPoint);
    double offsetX = camX0;
    double offsetZ = focalOffset;
    double dx = camPoint.x - offsetX;
    double dz = camPoint.z - offsetZ;

    double nx = cosEye * dx + sinEye * dz + offsetX;
    double nz = -sinEye * dx + cosEye * dz + offsetZ;

    camPoint.x = nx + eyeOff.x;
    camPoint.y += eyeOff.y;
    camPoint.z = nz + eyeOff.z;
  }
}
