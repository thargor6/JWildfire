/*
 JWildfire - an image and animation processor written in Java
 Copyright (C) 1995-2012 Andreas Maschke

 This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 General Public License as published by the Free Software Foundation; either version 2.1 of the
 License, or (at your option) any later version.

 This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this software;
 if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.create.GradientCreator;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.transform.RotateTransformer;
import org.jwildfire.transform.ScaleAspect;
import org.jwildfire.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jwildfire.base.mathlib.MathLib.*;

public class BrushStrokeWFFunc extends VariationFunc {
  public static final String NAME = "brush_stroke_wf";
  public static final String PARAM_BLEND = "blend";
  public static final String PARAM_GRID_SIZE = "grid_Size";
  public static final String PARAM_GRID_DEFORM = "grid_deform";
  private static final String PARAM_OFFSETY = "offset_y";
  private static final String PARAM_COLOR_CHANNEL = "color_channel";
  private static final String PARAM_THRESHOLD = "threshold";
  private static final String PARAM_INVERT = "invert";

  private static final String PARAM_VARIATION_BATCH_SIZE = "variation_batch_size";
  private static final String PARAM_VARIATION_ZOOM = "variation_zoom";
  private static final String PARAM_VARIATION_POSITION = "variation_position";
  private static final String PARAM_VARIATION_ROTATION = "variation_rotation";
  private static final String PARAM_VARIATION_EROSION = "variation_erosion";
  private static final String PARAM_VARIATION_SEED = "variation_seed";

  private static final String PARAM_IMAGE_MAX_SIZE = "image_max_size";

  private static final String PARAM_ANTIALIAS_RADIUS = "antialias_radius";
  private static final String RESSOURCE_IMAGE_FILENAME1 = "image_filename1";
  private static final String RESSOURCE_IMAGE_FILENAME2 = "image_filename2";
  private static final String RESSOURCE_IMAGE_FILENAME3 = "image_filename3";
  private static final String RESSOURCE_IMAGE_FILENAME4 = "image_filename4";
  private static final String RESSOURCE_IMAGE_FILENAME5 = "image_filename5";
  private static final String RESSOURCE_IMAGE_FILENAME6 = "image_filename6";
  private static final String[] paramNames = {
    PARAM_BLEND,
    PARAM_GRID_SIZE,
    PARAM_GRID_DEFORM,
    PARAM_OFFSETY,
    PARAM_COLOR_CHANNEL,
    PARAM_THRESHOLD,
    PARAM_INVERT,
    PARAM_VARIATION_BATCH_SIZE,
    PARAM_VARIATION_ZOOM,
    PARAM_VARIATION_POSITION,
    PARAM_VARIATION_ROTATION,
    PARAM_VARIATION_EROSION,
    PARAM_VARIATION_SEED,
    PARAM_IMAGE_MAX_SIZE,
    PARAM_ANTIALIAS_RADIUS
  };
  private static final String[] ressourceNames = {
    RESSOURCE_IMAGE_FILENAME1,
    RESSOURCE_IMAGE_FILENAME2,
    RESSOURCE_IMAGE_FILENAME3,
    RESSOURCE_IMAGE_FILENAME4,
    RESSOURCE_IMAGE_FILENAME5,
    RESSOURCE_IMAGE_FILENAME6
  };
  private static final int IM = 2147483647;
  private static final double AM = (1. / IM);
  private static SimpleImage dfltImage = null;
  private final Pixel toolPixel = new Pixel();
  private final float[] rgbArray = new float[3];
  private int variation_batch_size = 12;
  private double variation_zoom = 0.8;
  private double variation_position = 0.5;
  private double variation_rotation = 2.0 * M_PI;
  private double variation_erosion = 0.5;
  private int variation_seed = 12345;

  private int image_max_size = 320;

  private String imageFilename1 = null;
  private String imageFilename2 = null;
  private String imageFilename3 = null;
  private String imageFilename4 = null;
  private String imageFilename5 = null;
  private String imageFilename6 = null;
  private double blend = 0.25;
  private double grid_size = 0.025;
  private double grid_deform = 0.05;
  private double offset_y = 0.0;
  private int color_channel = 0;
  private double threshold = 0.1;
  private int invert = 1;
  private double antialias_radius = 0.75;
  // derived params
  private List<WFImage> _colorMaps;
  private Map<Integer, List<Point>> _pointsMap;

  @Override
  public void transform(
      FlameTransformationContext pContext,
      XForm pXForm,
      XYZPoint pAffineTP,
      XYZPoint pVarTP,
      double pAmount) {
    Map<Integer, List<Point>> pointsMap = getPointsMap(pContext);
    /*
        int generation =
            Math.max(
                Math.min(Tools.FTOI(variation_batch_size * pAffineTP.color), variation_batch_size), 0);
    */
    double noiseSclGeneration = 32768.0 * 0.666;
    double noiseSclX = 32768.0;
    double noiseSclY = noiseSclX * 0.42 + M_PI;

    double gridPosX = pVarTP.x - fmod(pVarTP.x, grid_size);
    double gridPosY = pVarTP.y - fmod(pVarTP.y, grid_size);

    int generation =
        Math.max(
            Math.min(
                Tools.FTOI(
                    (variation_batch_size + 1)
                        * discretNoise(
                            Tools.FTOI(noiseSclGeneration * (gridPosX + 1235.767 * gridPosY)))),
                variation_batch_size),
            0);

    List<Point> points = pointsMap.get(generation);
    if (points.size() > 1 && (blend < EPSILON || pContext.random() > blend)) {
      int pSize = points.size();
      int pIdx = pContext.random(pSize);
      Point point = points.get(pIdx);
      double rawX = point.x;
      double rawY = point.y;
      if (antialias_radius > 0.01) {
        double dr = (exp(antialias_radius * sqrt(-log(pContext.random()))) - 1.0) * 0.001;
        double da = pContext.random() * 2.0 * M_PI;
        rawX += dr * cos(da);
        rawY += dr * sin(da);
      }
      if (grid_size <= EPSILON) {
        pVarTP.x += pAmount * rawX;
        pVarTP.y += pAmount * rawY;
      } else {
        gridPosX += (0.5 - discretNoise(Tools.FTOI(noiseSclX * (gridPosX)))) * grid_deform;
        gridPosY += (0.5 - discretNoise(Tools.FTOI(noiseSclY * (gridPosY)))) * grid_deform;
        pVarTP.x = gridPosX + pAmount * rawX;
        pVarTP.y = gridPosY + pAmount * rawY;
      }
    }
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] {
      blend,
      grid_size,
      grid_deform,
      offset_y,
      color_channel,
      threshold,
      invert,
      variation_batch_size,
      variation_zoom,
      variation_position,
      variation_rotation,
      variation_erosion,
      variation_seed,
      image_max_size,
      antialias_radius
    };
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public void initOnce(
      FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.initOnce(pContext, pLayer, pXForm, pAmount);
    _colorMaps = new ArrayList<>();
    if (imageFilename1 != null && imageFilename1.length() > 0) {
      try {
        _colorMaps.add(RessourceManager.getImage(imageFilename1));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (imageFilename2 != null && imageFilename2.length() > 0) {
      try {
        _colorMaps.add(RessourceManager.getImage(imageFilename2));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (imageFilename3 != null && imageFilename3.length() > 0) {
      try {
        _colorMaps.add(RessourceManager.getImage(imageFilename3));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (imageFilename4 != null && imageFilename4.length() > 0) {
      try {
        _colorMaps.add(RessourceManager.getImage(imageFilename4));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (imageFilename5 != null && imageFilename5.length() > 0) {
      try {
        _colorMaps.add(RessourceManager.getImage(imageFilename5));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (imageFilename6 != null && imageFilename6.length() > 0) {
      try {
        _colorMaps.add(RessourceManager.getImage(imageFilename5));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (_colorMaps.isEmpty()) {
      _colorMaps.add(getDfltImage());
    }
    getPointsMap(pContext);
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_BLEND.equalsIgnoreCase(pName)) blend = pValue;
    else if (PARAM_GRID_SIZE.equalsIgnoreCase(pName)) grid_size = pValue;
    else if (PARAM_GRID_DEFORM.equalsIgnoreCase(pName)) grid_deform = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName)) offset_y = pValue;
    else if (PARAM_COLOR_CHANNEL.equalsIgnoreCase(pName)) color_channel = Tools.FTOI(pValue);
    else if (PARAM_THRESHOLD.equalsIgnoreCase(pName)) threshold = pValue;
    else if (PARAM_INVERT.equalsIgnoreCase(pName)) invert = Tools.FTOI(pValue);
    else if (PARAM_VARIATION_BATCH_SIZE.equalsIgnoreCase(pName))
      variation_batch_size = Tools.FTOI(pValue);
    else if (PARAM_VARIATION_ZOOM.equalsIgnoreCase(pName)) variation_zoom = pValue;
    else if (PARAM_VARIATION_POSITION.equalsIgnoreCase(pName)) variation_position = pValue;
    else if (PARAM_VARIATION_ROTATION.equalsIgnoreCase(pName)) variation_rotation = pValue;
    else if (PARAM_VARIATION_EROSION.equalsIgnoreCase(pName)) variation_erosion = pValue;
    else if (PARAM_VARIATION_SEED.equalsIgnoreCase(pName)) variation_seed = Tools.FTOI(pValue);
    else if (PARAM_IMAGE_MAX_SIZE.equalsIgnoreCase(pName)) image_max_size = Tools.FTOI(pValue);
    else if (PARAM_ANTIALIAS_RADIUS.equalsIgnoreCase(pName)) antialias_radius = pValue;
    else throw new IllegalArgumentException(pName);
  }

  private synchronized SimpleImage getDfltImage() {
    if (dfltImage == null) {
      GradientCreator creator = new GradientCreator();
      dfltImage = creator.createImage(256, 256);
    }
    return dfltImage;
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] {
      (imageFilename1 != null ? imageFilename1.getBytes() : null),
      (imageFilename2 != null ? imageFilename2.getBytes() : null),
      (imageFilename3 != null ? imageFilename3.getBytes() : null),
      (imageFilename4 != null ? imageFilename4.getBytes() : null),
      (imageFilename5 != null ? imageFilename5.getBytes() : null),
      (imageFilename6 != null ? imageFilename6.getBytes() : null)
    };
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_IMAGE_FILENAME1.equalsIgnoreCase(pName)) {
      imageFilename1 = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_IMAGE_FILENAME2.equalsIgnoreCase(pName)) {
      imageFilename2 = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_IMAGE_FILENAME3.equalsIgnoreCase(pName)) {
      imageFilename3 = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_IMAGE_FILENAME4.equalsIgnoreCase(pName)) {
      imageFilename4 = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_IMAGE_FILENAME5.equalsIgnoreCase(pName)) {
      imageFilename5 = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_IMAGE_FILENAME6.equalsIgnoreCase(pName)) {
      imageFilename6 = pValue != null ? new String(pValue) : "";
    } else throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_IMAGE_FILENAME1.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_IMAGE_FILENAME2.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_IMAGE_FILENAME3.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_IMAGE_FILENAME4.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_IMAGE_FILENAME5.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_IMAGE_FILENAME6.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else throw new IllegalArgumentException(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[] {
      VariationFuncType.VARTYPE_2D,
      VariationFuncType.VARTYPE_BASE_SHAPE,
      VariationFuncType.VARTYPE_SUPPORTS_EXTERNAL_SHAPES
    };
  }

  @Override
  public String getName() {
    return NAME;
  }

  private String makeRessourceKey() {
    return getName()
        + "#"
        + imageFilename1
        + "#"
        + imageFilename2
        + "#"
        + imageFilename3
        + "#"
        + imageFilename4
        + "#"
        + imageFilename5
        + "#"
        + imageFilename6
        + "#"
        + invert
        + "#"
        + threshold
        + "#"
        + color_channel
        + "#"
        + variation_batch_size
        + "#"
        + variation_rotation
        + "#"
        + variation_zoom
        + "#"
        + variation_position
        + "#"
        + variation_erosion
        + "#"
        + image_max_size
        + "#"
        + variation_seed;
  }

  private Map<Integer, List<Point>> getPointsMap(FlameTransformationContext pContext) {
    if (_pointsMap == null) {
      String key = makeRessourceKey();
      _pointsMap = (Map<Integer, List<Point>>) RessourceManager.getRessource(key);
      if (_pointsMap == null) {
        _pointsMap = new HashMap<>();
        MarsagliaRandomGenerator randomGenerator = new MarsagliaRandomGenerator();
        randomGenerator.randomize(variation_seed);
        int generationId = 0;
        for (int imgId = 0; imgId < _colorMaps.size(); imgId++) {
          WFImage colorMap = _colorMaps.get(imgId);
          if (image_max_size > 64
              && (colorMap instanceof SimpleImage)
              && ((colorMap.getImageWidth() > image_max_size)
                  || (colorMap.getImageHeight() > image_max_size))) {
            ScaleTransformer scaleT = new ScaleTransformer();
            if (colorMap.getImageWidth() > image_max_size) {
              scaleT.setAspect(ScaleAspect.KEEP_WIDTH);
              scaleT.setScaleWidth(image_max_size);
            } else {
              scaleT.setAspect(ScaleAspect.KEEP_HEIGHT);
              scaleT.setScaleHeight(image_max_size);
            }
            scaleT.transformImage(colorMap);
          }
          int batch_size = Math.max(1, variation_batch_size / _colorMaps.size());
          if (imgId == 0) {
            batch_size = variation_batch_size - batch_size * (_colorMaps.size() - 1);
          }

          for (int generation = 0; generation <= batch_size; generation++) {
            List<PointWithIntensity> pointsWithIntensity = new ArrayList<>();

            WFImage imgMap;
            int currDeltaX;
            int currDeltaY;
            double currErosion;
            if (generation == 0 || !(colorMap instanceof SimpleImage)) {
              imgMap = colorMap;
              currDeltaX = currDeltaY = 0;
              currErosion = 0.0;
            } else {
              double currZoom = Math.max(1.0 - variation_zoom * randomGenerator.random(), 0.2);
              double currAngle = variation_rotation * randomGenerator.random() * 180.0 / M_PI;
              currDeltaX =
                  Tools.FTOI(
                      (0.5 - randomGenerator.random())
                          * variation_position
                          * (double) colorMap.getImageWidth());
              currDeltaY =
                  Tools.FTOI(
                      (0.5 - randomGenerator.random())
                          * variation_position
                          * (double) colorMap.getImageHeight());
              currErosion = variation_erosion * randomGenerator.random();
              System.err.println(
                  "ZOOM"
                      + currZoom
                      + " ANGLE "
                      + currAngle
                      + " DELTA"
                      + currDeltaX
                      + " "
                      + currDeltaY);

              imgMap = ((SimpleImage) colorMap).clone();
              RotateTransformer rT = new RotateTransformer();
              rT.setZoom(currZoom);
              rT.setCentreX(imgMap.getImageWidth() / 2);
              rT.setCentreY(imgMap.getImageHeight() / 2);
              rT.setRadius(
                  Math.sqrt(
                              imgMap.getImageWidth() * imgMap.getImageWidth()
                                  + imgMap.getImageHeight() * imgMap.getImageHeight())
                          / 2.0
                      + 1.0);
              rT.setSmoothing(1);
              rT.setAmount(currAngle);
              rT.transformImage(imgMap);
            }

            int xSize = imgMap.getImageWidth();
            int ySize = imgMap.getImageHeight();
            int maxSize = Math.max(xSize, ySize);
            int xMin = 0;
            int yMin = 0;

            for (int row = 0; row < imgMap.getImageHeight(); row++) {
              int mappedRow = row + currDeltaX;
              if (mappedRow >= 0 && mappedRow < imgMap.getImageHeight()) {
                for (int column = 0; column < imgMap.getImageWidth(); column++) {
                  int mappedCol = column + currDeltaY;
                  if (mappedCol >= 0 && mappedCol < imgMap.getImageWidth()) {
                    double colorValue = getColorValue(imgMap, mappedCol, mappedRow);
                    if (colorValue >= threshold) {
                      double x = ((column - xMin) - xSize / 2.0) / (double) maxSize;
                      double y = ((row - yMin) - ySize / 2.0) / (double) maxSize;
                      if (currErosion <= EPSILON || currErosion <= randomGenerator.random()) {
                        pointsWithIntensity.add(
                            new PointWithIntensity(
                                x, y, Math.min(colorValue * colorValue + threshold, 1.0)));
                      }
                    }
                  }
                }
              }
            }

            List<Point> finalPoints = distributePoints(pointsWithIntensity);
            _pointsMap.put(generationId++, finalPoints);
          }
          RessourceManager.putRessource(key, _pointsMap);
        }
      }
    }
    return _pointsMap;
  }

  private List<Point> distributePoints(List<PointWithIntensity> pointsWithIntensity) {
    int totalSize = 64000;
    if (totalSize <= pointsWithIntensity.size() * 2) {
      totalSize = pointsWithIntensity.size() * 2;
    }

    List<Point> res = new ArrayList<>();
    double totalWeight = pointsWithIntensity.stream().mapToDouble(o -> o.intensity).sum();

    // 0.1   1  0.5
    // 20 entries
    // sum: 1.6
    // 20 * 0.1 / 1.6: 0 -> 1
    // 20 * 1.0 / 1.6: 13
    // 20 * 0.5 / 1.6: 6
    for (PointWithIntensity pi : pointsWithIntensity) {
      Point p = new Point(pi.x, pi.y);
      int count = Math.max(Tools.FTOI(((double) totalSize * pi.intensity) / totalWeight), 1);
      for (int i = 0; i < count; i++) {
        res.add(p);
      }
    }
    System.err.println("RES: " + res.size() + " " + pointsWithIntensity.size());

    return res;
  }

  private double getColorValue(WFImage imgMap, int column, int row) {
    double r, g, b, a;
    if (imgMap instanceof SimpleImage) {
      toolPixel.setARGBValue(((SimpleImage) imgMap).getARGBValue(column, row));
      r = toolPixel.r;
      g = toolPixel.g;
      b = toolPixel.b;
      a = toolPixel.a;
    } else {
      ((SimpleHDRImage) imgMap).getRGBValues(rgbArray, column, row);
      r = rgbArray[0] * 255;
      g = rgbArray[1] * 255;
      b = rgbArray[2] * 255;
      a = 255.0;
    }
    double refValue;
    switch (color_channel) {
      case 1:
        refValue = g;
        break;
      case 2:
        refValue = b;
        break;
      case 3:
        refValue = a;
        break;
      case 0:
      default:
        refValue = r;
        break;
    }
    double normedValue = Math.max(Math.min(refValue / 255.0, 1.0), 0.0);
    return invert != 0 ? 1.0 - normedValue : normedValue;
  }

  private double discretNoise(int X) {
    int n = X;
    n = (n << 13) ^ n;
    return ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) * AM;
  }

  private static class PointWithIntensity {
    private final double x, y, intensity;

    public PointWithIntensity(double x, double y, double intensity) {
      this.x = x;
      this.y = y;
      this.intensity = intensity;
    }
  }

  private static class Point {
    private final double x, y;

    public Point(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }
}
