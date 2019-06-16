package org.jwildfire.create.tina.base.weightingfield;

import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;

public class ImageMapWeightingField implements WeightingField {
  private SimpleImage image = null;
  // "D:\\TMP\\mb3dhm.jpg"
  private String imageFilename;
  private double xCentre = 0.0;
  private double yCentre = 0.0;
  private double xSize = 4.0;
  private double ySize = 4.0;

  @Override
  public double getValue(FlameTransformationContext pContext, double x, double y, double z) {
    if(image==null) {
      if(imageFilename==null || imageFilename.length()==0) {
        return 0.0;
      }
      try {
        image = (SimpleImage) RessourceManager.getImage(imageFilename);
      } catch (Exception e) {
        e.printStackTrace();
        return 0.0;
      }
    }

    double xCoord = (x+xSize*0.5-xCentre)/xSize * image.getImageWidth();
    double yCoord = (y+ySize*0.5-yCentre)/ySize * image.getImageHeight();

    int luR = image.getRValueIgnoreBounds((int) xCoord, (int) yCoord);
    int ruR = image.getRValueIgnoreBounds(((int) xCoord) + 1, (int) yCoord);
    int lbR = image.getRValueIgnoreBounds((int) xCoord, ((int) yCoord) + 1);
    int rbR = image.getRValueIgnoreBounds(((int) xCoord) + 1, ((int) yCoord) + 1);

    double px = MathLib.frac(xCoord);
    double py = MathLib.frac(yCoord);
    double avgGray = GfxMathLib.blerp(luR, ruR, lbR, rbR, px, py);
    double noise = 0.5 - avgGray / 255.0;
    return noise;
  }

  public String getImageFilename() {
    return imageFilename;
  }

  public void setImageFilename(String imageFilename) {
    this.imageFilename = imageFilename;
  }

  public double getxCentre() {
    return xCentre;
  }

  public void setxCentre(double xCentre) {
    this.xCentre = xCentre;
  }

  public double getyCentre() {
    return yCentre;
  }

  public void setyCentre(double yCentre) {
    this.yCentre = yCentre;
  }

  public double getxSize() {
    return xSize;
  }

  public void setxSize(double xSize) {
    this.xSize = xSize;
  }

  public double getySize() {
    return ySize;
  }

  public void setySize(double ySize) {
    this.ySize = ySize;
  }
}
