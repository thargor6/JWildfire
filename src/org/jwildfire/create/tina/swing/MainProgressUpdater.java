package org.jwildfire.create.tina.swing;

import java.awt.Graphics;

public class MainProgressUpdater implements ProgressUpdater {
  private final TinaInternalFrame parent;

  public MainProgressUpdater(TinaInternalFrame pParent) {
    parent = pParent;
  }

  @Override
  public void initProgress(int pMaxSteps) {
    parent.getRenderProgressBar().setValue(0);
    parent.getRenderProgressBar().setMinimum(0);
    parent.getRenderProgressBar().setMaximum(pMaxSteps);
    Graphics g = parent.getRenderProgressBar().getGraphics();
    if (g != null) {
      parent.getRenderProgressBar().paint(g);
    }
  }

  @Override
  public void updateProgress(int pStep) {
    parent.getRenderProgressBar().setValue(pStep);
    Graphics g = parent.getRenderProgressBar().getGraphics();
    if (g != null) {
      parent.getRenderProgressBar().paint(g);
    }
  }

}
