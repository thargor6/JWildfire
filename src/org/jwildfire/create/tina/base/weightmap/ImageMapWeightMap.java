package org.jwildfire.create.tina.base.weightmap;

import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;

public class ImageMapWeightMap implements WeightMap {
  private SimpleImage bgImage = null;
  // "D:\\TMP\\mb3dhm.jpg"
  private final String imageFilename;
  private final double xCentre;
  private final double yCentre;
  private final double xSize;
  private final double ySize;

  public ImageMapWeightMap(String imageFilename, double xCentre, double yCentre, double xSize, double ySize) {
    this.imageFilename = imageFilename;
    this.xCentre = xCentre;
    this.yCentre = yCentre;
    this.xSize = xSize;
    this.ySize = ySize;
  }

  @Override
  public double getValue(FlameTransformationContext pContext, double x, double y, double z) {
    if(bgImage==null) {
      try {
        bgImage = (SimpleImage) RessourceManager.getImage(imageFilename);
      } catch (Exception e) {
        e.printStackTrace();
        return 0.0;
      }
    }

    double xCoord = (x+xSize*0.5-xCentre)/xSize * bgImage.getImageWidth();
    double yCoord = (y+ySize*0.5-yCentre)/ySize * bgImage.getImageHeight();

    int luR = bgImage.getRValueIgnoreBounds((int) xCoord, (int) yCoord);
    int ruR = bgImage.getRValueIgnoreBounds(((int) xCoord) + 1, (int) yCoord);
    int lbR = bgImage.getRValueIgnoreBounds((int) xCoord, ((int) yCoord) + 1);
    int rbR = bgImage.getRValueIgnoreBounds(((int) xCoord) + 1, ((int) yCoord) + 1);

    double px = MathLib.frac(xCoord);
    double py = MathLib.frac(yCoord);
    double avgGray = GfxMathLib.blerp(luR, ruR, lbR, rbR, px, py);
    double noise = 0.5 - avgGray / 255.0;
    return noise;
  }
}
