package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Stereo3dEye;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;

public class Stereo3dFlameRendererView extends FlameRendererView {
  private XYZPoint eyeOff;

  public Stereo3dFlameRendererView(Stereo3dEye pEye, Flame pFlame, AbstractRandomGenerator pRandGen, int pBorderWidth, int pMaxBorderWidth, int pImageWidth, int pImageHeight, int pRasterWidth, int pRasterHeight) {
    super(pEye, pFlame, pRandGen, pBorderWidth, pMaxBorderWidth, pImageWidth, pImageHeight, pRasterWidth, pRasterHeight);
  }

  @Override
  public void initView() {
    super.initView();
    // use a camera-matrix with three angles
    double yaw = -flame.getCamYaw() * M_PI / 180.0;
    double pitch = flame.getCamPitch() * M_PI / 180.0;
    double roll = flame.getCamRoll() * M_PI / 180.0;

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
    }
    // Matrix without eye-angle
    cameraMatrix[0][0] = cos(yaw) * cos(roll) - sin(roll) * sin(yaw) * cos(pitch);
    cameraMatrix[1][0] = -sin(yaw) * cos(roll) - cos(yaw) * cos(pitch) * sin(roll);
    cameraMatrix[2][0] = sin(roll) * sin(pitch);
    cameraMatrix[0][1] = cos(yaw) * sin(roll) + cos(roll) * sin(yaw) * cos(pitch);
    cameraMatrix[1][1] = -sin(yaw) * sin(roll) + cos(yaw) * cos(pitch) * cos(roll);
    cameraMatrix[2][1] = -cos(roll) * sin(pitch);
    cameraMatrix[0][2] = sin(yaw) * sin(pitch);
    cameraMatrix[1][2] = cos(yaw) * sin(pitch);
    cameraMatrix[2][2] = cos(pitch);

    // always force 3d projection
    doProject3D = true;
    // disable "legacy" post-2D-rotation
    cosa = 1.0;
    sina = 0.0;
    rcX = -camX0;
    rcY = -camY0;

    // compute "eye-translation"
    this.eyeOff = new XYZPoint();
    this.eyeOff.x = eyeDist;
    super.applyCameraMatrix(this.eyeOff);

    // now the full matrix
    cameraMatrix[0][0] = (cos(yaw) * cos(roll) - sin(roll) * sin(yaw) * cos(pitch)) * cos(eyeAngle) + sin(yaw) * sin(pitch) * sin(eyeAngle);
    cameraMatrix[1][0] = (-sin(yaw) * cos(roll) - cos(yaw) * cos(pitch) * sin(roll)) * cos(eyeAngle) + cos(yaw) * sin(pitch) * sin(eyeAngle);
    cameraMatrix[2][0] = sin(roll) * sin(pitch) * cos(eyeAngle) + cos(pitch) * sin(eyeAngle);
    cameraMatrix[0][1] = cos(yaw) * sin(roll) + cos(roll) * sin(yaw) * cos(pitch);
    cameraMatrix[1][1] = -sin(yaw) * sin(roll) + cos(yaw) * cos(pitch) * cos(roll);
    cameraMatrix[2][1] = -cos(roll) * sin(pitch);
    cameraMatrix[0][2] = -(cos(yaw) * cos(roll) - sin(roll) * sin(yaw) * cos(pitch)) * sin(eyeAngle) + sin(yaw) * sin(pitch) * cos(eyeAngle);
    cameraMatrix[1][2] = -(-sin(yaw) * cos(roll) - cos(yaw) * cos(pitch) * sin(roll)) * sin(eyeAngle) + cos(yaw) * sin(pitch) * cos(eyeAngle);
    cameraMatrix[2][2] = -sin(roll) * sin(pitch) * sin(eyeAngle) + cos(pitch) * cos(eyeAngle);

  }

  @Override
  protected void applyCameraMatrix(XYZPoint pPoint) {
    super.applyCameraMatrix(pPoint);
    camPoint.x += eyeOff.x;
    camPoint.y += eyeOff.y;
    camPoint.z += eyeOff.z;
  }
}
