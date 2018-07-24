package org.jwildfire.create.iflames.swing;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.swing.FlameMessageHelper;

import javax.swing.*;

public class JFrameFlameMessageHelper implements FlameMessageHelper {
  private final JFrame frame;

  public JFrameFlameMessageHelper(JFrame pFrame) {
    frame = pFrame;
  }

  @Override
  public void showStatusMessage(String pStatus) {
    frame.setTitle((pStatus != null && pStatus.length() > 0 ? ": " + pStatus : ""));
  }

  @Override
  public void showStatusMessage(Flame pFlame, String pStatus) {
    if (pFlame == null)
      return;
    String prefix;
    if (pFlame.getName() != null && pFlame.getName().length() > 0) {
      prefix = "Flame \"" + pFlame.getName() + "\"";
    }
    else {
      prefix = "Unnamed flame";
    }
    if (pFlame.getLastFilename() != null && pFlame.getLastFilename().length() > 0) {
      prefix += " (" + pFlame.getLastFilename() + ") ";
    }
    else {
      prefix += " ";
    }
    frame.setTitle(prefix + (pStatus != null && pStatus.length() > 0 ? ": " + pStatus : ""));
  }

  @Override
  public void showStatusMessage(RGBPalette pGradient, String pStatus) {
    if (pGradient == null)
      return;
    String prefix;
    if (pGradient.getFlam3Name() != null && pGradient.getFlam3Name().length() > 0) {
      prefix = "Gradient \"" + pGradient.getFlam3Name() + "\"";
    }
    else {
      prefix = "Unnamed gradient";
    }
    frame.setTitle(prefix + (pStatus != null && pStatus.length() > 0 ? ": " + pStatus : ""));
  }
}
