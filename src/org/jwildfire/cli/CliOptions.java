package org.jwildfire.cli;

/**
 Central registry for all parameters inside the the JWildfire
 */
public enum CliOptions {
  BC(true,"batchCount", "number of generated flames"),
  DN(true,"denoiser", "ai-based post-denoiser"),
  FDN(false, "forceDenoiser", "force ai-based post-denoiser"),
  F(true,"flame", "flame path"),
  RGFLAME(true,"rndGenFlame", "random-flame-generator to use"),
  RGGRAD(true,"rndGenGradient", "random-gradient-generator to use"),
  RGSYMM(true,"rndGenSymmetry", "random-symmetry-generator to use"),
  RGWFIELD(true,"rndGenWField", "random-weighting-field-generator to use"),
  GPU(false, "useGPU", "render using GPU"),
  H(true, "renderHeight",   "render height"),
  LRGFLAME(false,"listRndGenFlame", "list the available random-flame-generators"),
  LRGGRAD(false,"listRndGenGradient", "list the available random-gradient-generators"),
  LRGSYMM(false,"listRndGenSymmetry", "list the available random-symmetry-generators"),
  LRGWFIELD(false,"listRndGenWField", "list the available random-weighting-field-generators"),
  Q(true, "renderQuality", "render quality"),
  W(true, "renderWidth", "render width");

  private final boolean hasArg;
  private final String longName;
  private final String description;

  public boolean isHasArg() {
    return hasArg;
  }

  public String getShortName() {
    return this.name().toLowerCase();
  }

  public String getLongName() {
    return longName;
  }

  public String getDescription() {
    return description;
  }

  private CliOptions(boolean hasArg, String longName, String description) {
    this.hasArg = hasArg;
    this.longName = longName;
    this.description = description;
  }
}
