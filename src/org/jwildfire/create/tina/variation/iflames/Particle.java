package org.jwildfire.create.tina.variation.iflames;

import java.io.Serializable;

public class Particle implements Serializable {
  private static final long serialVersionUID = 1L;

  private final DynamicProperties motionProperties;
  private float life;
  private Vector position = new Vector(0, 0, 0);
  private Vector speed = new Vector(0, 0, 0);
  private Vector rotation = new Vector(0, 0, 0);
  private Vector rotationSpeed = new Vector(0, 0, 0);
  private float radialAcceleration, tangentialAcceleration;

  public Particle(DynamicProperties pMotionProperties) {
    motionProperties = pMotionProperties;
    reset();
  }

  private Particle(DynamicProperties pMotionProperties, float pLife, Vector pPosition, Vector pSpeed, Vector pRotation, Vector pRotationSpeed,
                   float pRadialAcceleration, float pTangentialAcceleration) {
    motionProperties = pMotionProperties;
    life = pLife;
    position = pPosition.makeCopy();
    speed = pSpeed.makeCopy();
    rotation = pRotation.makeCopy();
    rotationSpeed = pRotationSpeed.makeCopy();
    radialAcceleration = pRadialAcceleration;
    tangentialAcceleration = pTangentialAcceleration;
  }

  public Particle makeCopy() {
    return new Particle(motionProperties, life, position, speed, rotation, rotationSpeed,
            radialAcceleration, tangentialAcceleration);
  }

  public void reset() {
    life = motionProperties.getLife();
    position = new Vector(motionProperties.getX0(), motionProperties.getY0(), motionProperties.getZ0());
    speed = new Vector(motionProperties.getVx0(), motionProperties.getVy0(), motionProperties.getVz0());
    rotation = new Vector(motionProperties.getAlpha0(), motionProperties.getBeta0(), motionProperties.getGamma0());
    rotationSpeed = new Vector(motionProperties.getAlphaSpeed0(), motionProperties.getBetaSpeed0(), motionProperties.getGammaSpeed0());
    radialAcceleration = motionProperties.getRadialAcceleration();
    tangentialAcceleration = motionProperties.getTangentialAcceleration();
  }

  public void incPosition(Vector pDelta) {
    position = VectorMath.add(position, pDelta);
  }

  public void incRotation(Vector pDelta) {
    rotation = VectorMath.add(rotation, pDelta);
  }

  public void incSpeed(Vector pDelta) {
    speed = VectorMath.add(speed, pDelta);
  }

  public void incRotationSpeed(Vector pDelta) {
    rotationSpeed = VectorMath.add(rotationSpeed, pDelta);
  }

  public void decLife(float pDT) {
    life -= pDT;
    if (life <= 0) {
      reset();
    }
  }

  public Vector getPosition() {
    return position;
  }

  public Vector getSpeed() {
    return speed;
  }

  public Vector getRotation() {
    return rotation;
  }

  public Vector getRotationSpeed() {
    return rotationSpeed;
  }

  public float getRadialAcceleration() {
    return radialAcceleration;
  }

  public float getTangentialAcceleration() {
    return tangentialAcceleration;
  }

}
