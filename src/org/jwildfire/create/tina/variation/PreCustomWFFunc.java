package org.jwildfire.create.tina.variation;

public class PreCustomWFFunc extends CustomWFFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public String getName() {
    return "pre_custom_wf";
  }

  @Override
  public int getPriority() {
    return -1;
  }

}
