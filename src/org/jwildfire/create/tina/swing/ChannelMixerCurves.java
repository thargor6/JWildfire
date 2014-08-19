package org.jwildfire.create.tina.swing;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.motion.MotionCurve;

public enum ChannelMixerCurves {

  DEFAULT {
    @Override
    public void makeCurve(MotionCurve pCurve) {
      pCurve.setViewXMin(Tools.FTOI(-BORDER / 4));
      pCurve.setViewXMax(Tools.FTOI(SCALE + BORDER / 4));
      pCurve.setViewYMin(-BORDER);
      pCurve.setViewYMax(SCALE + BORDER);
      pCurve.setPoints(new int[] { 0, Tools.FTOI(SCALE / 4.0), Tools.FTOI(SCALE / 2.0), Tools.FTOI(SCALE * 3.0 / 4.0), Tools.FTOI(SCALE) },
          new double[] { 0.0, SCALE / 4.0, SCALE / 2.0, SCALE * 3.0 / 4.0, SCALE });
    }
  },
  ZERO {
    @Override
    public void makeCurve(MotionCurve pCurve) {
      pCurve.setViewXMin(Tools.FTOI(-BORDER / 4));
      pCurve.setViewXMax(Tools.FTOI(SCALE + BORDER / 4));
      pCurve.setViewYMin(-BORDER);
      pCurve.setViewYMax(SCALE + BORDER);
      pCurve.setPoints(new int[] { 0, Tools.FTOI(SCALE / 4.0), Tools.FTOI(SCALE / 2.0), Tools.FTOI(SCALE * 3.0 / 4.0), Tools.FTOI(SCALE) }, new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 });
    }
  };

  public abstract void makeCurve(MotionCurve pCurve);

  public static final double FILTER_SCALE = 100.0;

  private static final double SCALE = 256.0 * FILTER_SCALE;
  private static final double BORDER = SCALE / 10 * 2;

}
