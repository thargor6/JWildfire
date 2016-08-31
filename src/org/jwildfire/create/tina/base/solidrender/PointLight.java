package org.jwildfire.create.tina.base.solidrender;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.io.Serializable;

import org.jwildfire.create.tina.edit.Assignable;

@SuppressWarnings("serial")
public class PointLight implements Assignable<PointLight>, Serializable {

  private double x, y, z;
  private double intensity;
  private double red, green, blue;
  private boolean castShadows = true;

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public double getRed() {
    return red;
  }

  public void setRed(double red) {
    this.red = red;
  }

  public double getGreen() {
    return green;
  }

  public void setGreen(double green) {
    this.green = green;
  }

  public double getBlue() {
    return blue;
  }

  public void setBlue(double blue) {
    this.blue = blue;
  }

  public double getIntensity() {
    return intensity;
  }

  public void setIntensity(double intensity) {
    this.intensity = intensity;
  }

  public boolean isCastShadows() {
    return castShadows;
  }

  public void setCastShadows(boolean castShadows) {
    this.castShadows = castShadows;
  }

  @Override
  public void assign(PointLight pSrc) {
    x = pSrc.x;
    y = pSrc.y;
    z = pSrc.z;
    intensity = pSrc.intensity;
    red = pSrc.red;
    green = pSrc.green;
    blue = pSrc.blue;
    castShadows = pSrc.castShadows;
  }

  @Override
  public PointLight makeCopy() {
    PointLight res = new PointLight();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(PointLight pSrc) {
    if (fabs(x - pSrc.x) > EPSILON || fabs(y - pSrc.y) > EPSILON ||
        fabs(z - pSrc.z) > EPSILON || fabs(intensity - pSrc.intensity) > EPSILON ||
        fabs(red - pSrc.red) > EPSILON || fabs(green - pSrc.green) > EPSILON ||
        fabs(blue - pSrc.blue) > EPSILON || castShadows != pSrc.castShadows) {
      return false;
    }
    return false;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }

  public void setZ(double z) {
    this.z = z;
  }
}
