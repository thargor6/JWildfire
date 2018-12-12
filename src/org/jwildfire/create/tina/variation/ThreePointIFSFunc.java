package org.jwildfire.create.tina.variation;


import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class ThreePointIFSFunc extends SimpleVariationFunc {


  /**
   * Three Point Pivot/Overlap IFS Triangle
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of Roger Bagula:
   * https://plus.google.com/110803890168343196795/posts/9P2gTkrmQFr
   */


  private static final long serialVersionUID = 1L;


  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
//               based on a work of Roger Bagula:
//				 https://plus.google.com/110803890168343196795/posts/9P2gTkrmQFr

    double x, y;

    if (Math.random() < 0.333) {
      x = pAffineTP.x / 2.0 - pAffineTP.y / 2.0 + 0.5;
      y = -pAffineTP.x / 2.0 - pAffineTP.y / 2.0 + 0.5;
    } else if (Math.random() < 0.666) {
      x = pAffineTP.y;
      y = pAffineTP.x;
    } else {
      x = -pAffineTP.y / 2.0 + 0.5;
      y = -pAffineTP.x / 2.0 + 0.5;
    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  public String getName() {
    return "threepoint_js";
  }
}