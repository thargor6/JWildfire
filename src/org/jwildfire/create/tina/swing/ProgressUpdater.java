package org.jwildfire.create.tina.swing;

public interface ProgressUpdater {

  public void initProgress(int pMaxSteps);

  public void updateProgress(int pStep);

}
