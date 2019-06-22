package org.jwildfire.create.tina.base.weightingfield;

import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;

import java.net.URL;

public class ImageMapWeightingField implements WeightingField {
  private SimpleImage image = null;
  private final String DFLT_IMAGE_RESOURCE_NAME = ImageMapWeightingField.class.getName()+"_DFLT_IMG.jpg";
  public static final String DFLT_IMAGE_FILE_NAME = "mb3dhm.jpg";

  private String imageFilename;
  private double xCentre = 0.0;
  private double yCentre = 0.0;
  private double xSize = 4.0;
  private double ySize = 4.0;

  @Override
  public double getValue(double x, double y, double z) {
    if(image==null) {
      try {
        if (imageFilename == null || imageFilename.length() == 0) {
          image = getDefaultImage();
        }
        else {
          image = (SimpleImage) RessourceManager.getImage(imageFilename);
        }
        if(image==null) {
          return 0.0;
        }
      }
      catch (Exception e) {
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

  private SimpleImage getDefaultImage() throws Exception {
    SimpleImage image = (SimpleImage) RessourceManager.getRessource(DFLT_IMAGE_RESOURCE_NAME);
    if(image==null) {
      URL url = getClass().getResource(DFLT_IMAGE_FILE_NAME);
      image = new ImageReader().loadImage(url);
      if(image!=null) {
        RessourceManager.putRessource(DFLT_IMAGE_RESOURCE_NAME, image);
      }
    }
    return image;
  }

  public String getImageFilename() {
    return imageFilename;
  }

  public void setImageFilename(String imageFilename) {
    this.imageFilename = imageFilename;
    this.image = null;
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
