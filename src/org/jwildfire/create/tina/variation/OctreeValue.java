package org.jwildfire.create.tina.variation;

public class OctreeValue {
  private final double x, y, z;
  private final Object value;

  public OctreeValue(double x, double y, double z, Object value) {
    super();
    this.x = x;
    this.y = y;
    this.z = z;
    this.value = value;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public Object getValue() {
    return value;
  }

}
