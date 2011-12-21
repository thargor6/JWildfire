package org.jwildfire.create.tina.swing;

import java.awt.Graphics;

public class JobProgressUpdater implements ProgressUpdater {
  private final TinaInternalFrame parent;

  public JobProgressUpdater(TinaInternalFrame pParent) {
    parent = pParent;
  }

  @Override
  public void initProgress(int pMaxSteps) {
    parent.getBatchRenderJobProgressBar().setValue(0);
    parent.getBatchRenderJobProgressBar().setMinimum(0);
    parent.getBatchRenderJobProgressBar().setMaximum(pMaxSteps);
    parent.getBatchRenderJobProgressBar().invalidate();
    parent.getBatchRenderJobProgressBar().validate();
    Graphics g = parent.getBatchRenderJobProgressBar().getGraphics();
    if (g != null) {
      parent.getBatchRenderJobProgressBar().paint(g);
    }
  }

  @Override
  public void updateProgress(int pStep) {
    parent.getBatchRenderJobProgressBar().setValue(pStep);
    parent.getBatchRenderJobProgressBar().invalidate();
    parent.getBatchRenderJobProgressBar().validate();
    Graphics g = parent.getBatchRenderJobProgressBar().getGraphics();
    if (g != null) {
      parent.getBatchRenderJobProgressBar().paint(g);
    }
  }

}
