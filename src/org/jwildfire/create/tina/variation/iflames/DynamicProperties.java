package org.jwildfire.create.tina.variation.iflames;

import java.io.Serializable;

public class DynamicProperties implements Serializable {
  private static final long serialVersionUID = 1L;
  private final float x0, y0, z0;
  private final float vx0, vy0, vz0;
  private final float alpha0, beta0, gamma0;
  private final float alphaSpeed0, betaSpeed0, gammaSpeed0;
  private final float radialAcceleration, tangentialAcceleration;
  private final float life;

  public DynamicProperties(float pX0, float pY0, float pZ0, float pVx0, float pVy0, float pVz0, float pAlpha0, float pBeta0, float pGamma0, float pAlphaSpeed0, float pBetaSpeed0, float pGammaSpeed0, float pRadialAcceleration, float pTangentialAcceleration, float pLife) {
    x0 = pX0;
    y0 = pY0;
    z0 = pZ0;
    vx0 = pVx0;
    vy0 = pVy0;
    vz0 = pVz0;
    alpha0 = pAlpha0;
    beta0 = pBeta0;
    gamma0 = pGamma0;
    alphaSpeed0 = pAlphaSpeed0;
    betaSpeed0 = pBetaSpeed0;
    gammaSpeed0 = pGammaSpeed0;
    radialAcceleration = pRadialAcceleration;
    tangentialAcceleration = pTangentialAcceleration;
    life = pLife;
  }

  public DynamicProperties makeCopy() {
    return new DynamicProperties(x0, y0, z0, vx0, vy0, vz0, alpha0, beta0, gamma0, alphaSpeed0, betaSpeed0, gammaSpeed0, radialAcceleration, tangentialAcceleration, life);
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public float getX0() {
    return x0;
  }

  public float getY0() {
    return y0;
  }

  public float getZ0() {
    return z0;
  }

  public float getVx0() {
    return vx0;
  }

  public float getVy0() {
    return vy0;
  }

  public float getVz0() {
    return vz0;
  }

  public float getLife() {
    return life;
  }

  public float getAlpha0() {
    return alpha0;
  }

  public float getBeta0() {
    return beta0;
  }

  public float getGamma0() {
    return gamma0;
  }

  public float getAlphaSpeed0() {
    return alphaSpeed0;
  }

  public float getBetaSpeed0() {
    return betaSpeed0;
  }

  public float getGammaSpeed0() {
    return gammaSpeed0;
  }

  public float getRadialAcceleration() {
    return radialAcceleration;
  }

  public float getTangentialAcceleration() {
    return tangentialAcceleration;
  }

}
