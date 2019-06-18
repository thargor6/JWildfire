package org.jwildfire.create.tina.base;

import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.sqr;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public final class TransformationDistanceColorStep extends AbstractTransformationStep {
  private static final long serialVersionUID = 1L;

  public TransformationDistanceColorStep(XForm pXForm) {
    super(pXForm);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    
    if (pDstPoint.rgbColor) {  
      return;
    }
    
    double distance = sqrt(sqr(xform.oldx - pDstPoint.x) + sqr(xform.oldy - pDstPoint.y) + sqr(xform.oldz - pDstPoint.z));
    RenderColor[] colorMap = xform.getOwner().getColorMap();
    double paletteIdxScl = colorMap.length - 2;
    int colorIdx = (int) ((xform.getColor() + distance * xform.distMult) * paletteIdxScl + 0.5) % RGBPalette.PALETTE_SIZE;
    
    RenderColor color = colorMap[colorIdx];
    pDstPoint.redColor = color.red;
    pDstPoint.greenColor = color.green;
    pDstPoint.blueColor = color.blue;

  }

}
