package org.jwildfire.create.tina.variation;

public class PostCustomWFFunc extends CustomWFFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public String getName() {
    return "post_custom_wf";
  }

  @Override
  public int getPriority() {
    return 1;
  }

}
