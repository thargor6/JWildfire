package org.jwildfire.create.tina.variation.iflames;

import org.jwildfire.base.Tools;
import org.jwildfire.create.GradientCreator;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.JavaInternalRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.create.tina.variation.RessourceType;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import java.io.Serializable;
import java.util.*;

public class ImageParams implements Params, Serializable {
  private static final long serialVersionUID = 1L;
  private static final String RESSOURCE_INLINED_IMAGE = "inlined_image";
  public static final String RESSOURCE_IMAGE_FILENAME = "image_filename";

  private static final String PARAM_STRUCTURE_THRESHOLD = "structure_threshold";
  private static final String PARAM_STRUCTURE_DENSITY = "structure_density";
  private static final String PARAM_IFLAME_DENSITY = "iflame_density";
  private static final String PARAM_IFLAME_BRIGHTNESS = "iflame_brightness";
  private static final String PARAM_IMAGE_BRIGHTNESS = "image_brightness";
  private static final String PARAM_SHAPE_DISTRIBUTION = "shape_distribution";
  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_SCALEZ = "scale_z";
  private static final String PARAM_OFFSETX = "offset_x";
  private static final String PARAM_OFFSETY = "offset_y";
  private static final String PARAM_OFFSETZ = "offset_z";
  private static final String PARAM_ERODE = "erode";
  private static final String PARAM_ERODE_SIZE = "erode_size";
  private static final String PARAM_CONV_NORTH = "conv_north";
  private static final String PARAM_CONV_EAST = "conv_east";
  private static final String PARAM_CONV_SOUTH = "conv_south";
  private static final String PARAM_CONV_WEST = "conv_west";
  private static final String PARAM_MAX_IMG_WIDTH = "max_img_width";
  private static final String PARAM_ID = "id";
  private static final String PARAM_RENDER_ID = "render_id";
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_COLOR_CHG_RED = "color_chg_red";
  private static final String PARAM_COLOR_CHG_GREEN = "color_chg_green";
  private static final String PARAM_COLOR_CHG_BLUE = "color_chg_blue";
  private static final String PARAM_COLOR_CHG_HUE = "color_chg_hue";
  private static final String PARAM_COLOR_CHG_SATURATION = "color_chg_saturation";
  private static final String PARAM_COLOR_CHG_LIGHTNESS = "color_chg_lightness";

  private static final String CACHE_KEY_PREFIX_PREPROCESSED_IMAGE = "iflames_preprocessed_image";
  public static final String CACHE_KEY_PREFIX_STATISTICS = "iflames_statistics";
  public static final String CACHE_KEY_PREFIX_PROGRESS_UPDATER = "iflames_progress_updater";

  private double structure_threshold = 0.12;
  private double structure_density = 0.03;
  private double iflame_density = 0.666;
  private double iflame_brightness = 1.24;
  private double image_brightness = 0.42;
  private ShapeDistribution shape_distribution = ShapeDistribution.HUE;
  private int erode = 0;
  private int erodeSize = 5;
  private int conv_north = 1;
  private int conv_east = 1;
  private int conv_south = 0;
  private int conv_west = 0;
  private double scaleX = 19.20;
  private double scaleY = 10.80;
  private double scaleZ = 0.3;
  private double offsetX = -9.6;
  private double offsetY = -5.7;
  private double offsetZ = 0.0;
  private String imageFilename = null;
  private byte[] inlinedImage = null;
  private int inlinedImageHash = 0;
  private int maxImgWidth = 800;
  private int id = 1;
  private int renderId = 0;
  private int seed = 666;
  private double color_change_red;
  private double color_change_green;
  private double color_change_blue;
  private double color_change_hue;
  private double color_change_saturation;
  private double color_change_lightness;

  // derived params
  private WFImage colorMap;
  private String cachedPreprocessedImageKey;
  private transient Map<RenderColor, Double> colorIdxMap = new HashMap<RenderColor, Double>();
  private int imgWidth, imgHeight;
  private double scaleColorMap;
  private transient AbstractRandomGenerator randGen;

  @Override
  public String[] appendParamNames(String[] pParamNames) {
    List<String> res = new ArrayList<String>(Arrays.asList(pParamNames));
    res.add(PARAM_STRUCTURE_THRESHOLD);
    res.add(PARAM_STRUCTURE_DENSITY);
    res.add(PARAM_IFLAME_DENSITY);
    res.add(PARAM_IFLAME_BRIGHTNESS);
    res.add(PARAM_IMAGE_BRIGHTNESS);
    res.add(PARAM_SHAPE_DISTRIBUTION);
    res.add(PARAM_ERODE);
    res.add(PARAM_ERODE_SIZE);
    res.add(PARAM_CONV_NORTH);
    res.add(PARAM_CONV_EAST);
    res.add(PARAM_CONV_SOUTH);
    res.add(PARAM_CONV_WEST);
    res.add(PARAM_SCALEX);
    res.add(PARAM_SCALEY);
    res.add(PARAM_SCALEZ);
    res.add(PARAM_OFFSETX);
    res.add(PARAM_OFFSETY);
    res.add(PARAM_OFFSETZ);
    res.add(PARAM_MAX_IMG_WIDTH);
    res.add(PARAM_ID);
    res.add(PARAM_RENDER_ID);
    res.add(PARAM_SEED);
    res.add(PARAM_COLOR_CHG_RED);
    res.add(PARAM_COLOR_CHG_GREEN);
    res.add(PARAM_COLOR_CHG_BLUE);
    res.add(PARAM_COLOR_CHG_HUE);
    res.add(PARAM_COLOR_CHG_SATURATION);
    res.add(PARAM_COLOR_CHG_LIGHTNESS);
    return res.toArray(pParamNames);
  }

  @Override
  public String[] appendRessourceNames(String[] pRessourceNames) {
    List<String> res = new ArrayList<String>(Arrays.asList(pRessourceNames));
    res.add(RESSOURCE_IMAGE_FILENAME);
    res.add(RESSOURCE_INLINED_IMAGE);
    return res.toArray(pRessourceNames);
  }

  @Override
  public Object[] appendParamValues(Object[] pParamValues) {
    List<Object> res = new ArrayList<Object>(Arrays.asList(pParamValues));
    res.add(structure_threshold);
    res.add(structure_density);
    res.add(iflame_density);
    res.add(iflame_brightness);
    res.add(image_brightness);
    res.add(shape_distribution.getId());
    res.add(erode);
    res.add(erodeSize);
    res.add(conv_north);
    res.add(conv_east);
    res.add(conv_south);
    res.add(conv_west);
    res.add(scaleX);
    res.add(scaleY);
    res.add(scaleZ);
    res.add(offsetX);
    res.add(offsetY);
    res.add(offsetZ);
    res.add(maxImgWidth);
    res.add(id);
    res.add(renderId);
    res.add(seed);
    res.add(color_change_red);
    res.add(color_change_green);
    res.add(color_change_blue);
    res.add(color_change_hue);
    res.add(color_change_saturation);
    res.add(color_change_lightness);
    return res.toArray(pParamValues);
  }

  @Override
  public boolean setParameter(String pName, double pValue) {
    if (PARAM_STRUCTURE_THRESHOLD.equalsIgnoreCase(pName)) {
      structure_threshold = VariationFunc.limitVal(pValue, 0.0, 0.99999);
      return true;
    } else if (PARAM_STRUCTURE_DENSITY.equalsIgnoreCase(pName)) {
      structure_density = VariationFunc.limitVal(pValue, 0.0, 1.0);
      return true;
    } else if (PARAM_IFLAME_DENSITY.equalsIgnoreCase(pName)) {
      iflame_density = VariationFunc.limitVal(pValue, 0.0, 1.0);
      return true;
    } else if (PARAM_IFLAME_BRIGHTNESS.equalsIgnoreCase(pName)) {
      iflame_brightness = pValue;
      return true;
    } else if (PARAM_IMAGE_BRIGHTNESS.equalsIgnoreCase(pName)) {
      image_brightness = pValue;
      return true;
    } else if (PARAM_SHAPE_DISTRIBUTION.equalsIgnoreCase(pName)) {
      shape_distribution = ShapeDistribution.valueOf(Tools.FTOI(pValue));
      if (shape_distribution == null) {
        shape_distribution = ShapeDistribution.HUE;
      }
      return true;
    } else if (PARAM_ERODE.equalsIgnoreCase(pName)) {
      erode = VariationFunc.limitIntVal(Tools.FTOI(pValue), 0, 1);
      return true;
    } else if (PARAM_ERODE_SIZE.equalsIgnoreCase(pName)) {
      erodeSize = Tools.FTOI(pValue);
      if (erodeSize < 3) {
        erodeSize = 3;
      }
      if (erodeSize % 2 == 0) {
        erodeSize--;
      }
      if (erodeSize > 9) {
        erodeSize = 9;
      }
      return true;
    } else if (PARAM_CONV_NORTH.equalsIgnoreCase(pName)) {
      conv_north = VariationFunc.limitIntVal(Tools.FTOI(pValue), 0, 1);
      return true;
    } else if (PARAM_CONV_EAST.equalsIgnoreCase(pName)) {
      conv_east = VariationFunc.limitIntVal(Tools.FTOI(pValue), 0, 1);
      return true;
    } else if (PARAM_CONV_SOUTH.equalsIgnoreCase(pName)) {
      conv_south = VariationFunc.limitIntVal(Tools.FTOI(pValue), 0, 1);
      return true;
    } else if (PARAM_CONV_WEST.equalsIgnoreCase(pName)) {
      conv_west = VariationFunc.limitIntVal(Tools.FTOI(pValue), 0, 1);
      return true;
    } else if (PARAM_SCALEX.equalsIgnoreCase(pName)) {
      scaleX = pValue;
      return true;
    } else if (PARAM_SCALEY.equalsIgnoreCase(pName)) {
      scaleY = pValue;
      return true;
    } else if (PARAM_SCALEZ.equalsIgnoreCase(pName)) {
      scaleZ = pValue;
      return true;
    } else if (PARAM_OFFSETX.equalsIgnoreCase(pName)) {
      offsetX = pValue;
      return true;
    } else if (PARAM_OFFSETY.equalsIgnoreCase(pName)) {
      offsetY = pValue;
      return true;
    } else if (PARAM_OFFSETZ.equalsIgnoreCase(pName)) {
      offsetZ = pValue;
      return true;
    } else if (PARAM_MAX_IMG_WIDTH.equalsIgnoreCase(pName)) {
      maxImgWidth = VariationFunc.limitIntVal(Tools.FTOI(pValue), 16, 64000);
      return true;
    } else if (PARAM_ID.equalsIgnoreCase(pName)) {
      id = Tools.FTOI(pValue);
      return true;
    } else if (PARAM_RENDER_ID.equalsIgnoreCase(pName)) {
      renderId = Tools.FTOI(pValue);
      return true;
    } else if (PARAM_SEED.equalsIgnoreCase(pName)) {
      seed = Tools.FTOI(pValue);
      return true;
    } else if (PARAM_COLOR_CHG_RED.equalsIgnoreCase(pName)) {
      color_change_red = VariationFunc.limitVal(pValue, -1.0, 1.0);
      return true;
    } else if (PARAM_COLOR_CHG_GREEN.equalsIgnoreCase(pName)) {
      color_change_green = VariationFunc.limitVal(pValue, -1.0, 1.0);
      return true;
    } else if (PARAM_COLOR_CHG_BLUE.equalsIgnoreCase(pName)) {
      color_change_blue = VariationFunc.limitVal(pValue, -1.0, 1.0);
      return true;
    } else if (PARAM_COLOR_CHG_HUE.equalsIgnoreCase(pName)) {
      color_change_hue = VariationFunc.limitVal(pValue, -1.0, 1.0);
      return true;
    } else if (PARAM_COLOR_CHG_SATURATION.equalsIgnoreCase(pName)) {
      color_change_saturation = VariationFunc.limitVal(pValue, -1.0, 1.0);
      return true;
    } else if (PARAM_COLOR_CHG_LIGHTNESS.equalsIgnoreCase(pName)) {
      color_change_lightness = VariationFunc.limitVal(pValue, -1.0, 1.0);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_IMAGE_FILENAME.equalsIgnoreCase(pName)) {
      imageFilename = pValue != null ? new String(pValue) : "";
      colorMap = null;
      colorIdxMap.clear();
      return true;
    } else if (RESSOURCE_INLINED_IMAGE.equalsIgnoreCase(pName)) {
      inlinedImage = pValue;
      inlinedImageHash = RessourceManager.calcHashCode(inlinedImage);
      colorMap = null;
      colorIdxMap.clear();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_IMAGE_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_INLINED_IMAGE.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILE;
    } else {
      return null;
    }
  }

  @Override
  public byte[][] appendRessourceValues(byte[][] pRessourceValues) {
    List<byte[]> res = new ArrayList<byte[]>(Arrays.asList(pRessourceValues));
    res.add((imageFilename != null ? imageFilename.getBytes() : null));
    res.add(inlinedImage);
    return res.toArray(pRessourceValues);
  }

  @Override
  public String completeImageKey(String key) {
    return key + "#" + id + "#" + Tools.doubleToString(structure_threshold) + "#" + Tools.doubleToString(structure_density) + "#"
            + erode + "#" + erodeSize + "#" + conv_north + "#" + conv_east + "#" + conv_south + "#" + conv_west + "#"
            + maxImgWidth + "#" + shape_distribution + "#" + colorMap.hashCode() + "#" + seed + "#" + color_change_red + "#"
            + color_change_green + "#" + color_change_blue + "#" + color_change_hue + "#" + color_change_saturation + "#" + color_change_lightness;
  }

  @Override
  public String completeParticleKey(String pKey) {
    return completeImageKey(pKey);
  }

  public int getMaxImgWidth() {
    return maxImgWidth;
  }

  public double getStructure_threshold() {
    return structure_threshold;
  }

  public int getErode() {
    return erode;
  }

  public int getErodeSize() {
    return erodeSize;
  }

  public int getConv_north() {
    return conv_north;
  }

  public int getConv_east() {
    return conv_east;
  }

  public int getConv_south() {
    return conv_south;
  }

  public int getConv_west() {
    return conv_west;
  }

  public double getStructure_density() {
    return structure_density;
  }

  public void init(FlameTransformationContext pContext) {
    randGen = new JavaInternalRandomGenerator();
    randGen.randomize(seed);

    colorMap = null;
    if (inlinedImage != null) {
      try {
        colorMap = RessourceManager.getImage(inlinedImageHash, inlinedImage);
        cachedPreprocessedImageKey = CACHE_KEY_PREFIX_PREPROCESSED_IMAGE + "#" + inlinedImageHash;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (imageFilename != null && imageFilename.length() > 0) {
      try {
        colorMap = RessourceManager.getImage(imageFilename);
        cachedPreprocessedImageKey = CACHE_KEY_PREFIX_PREPROCESSED_IMAGE + "#" + imageFilename;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (colorMap == null) {
      colorMap = getDfltImage();
    }
    imgWidth = colorMap.getImageWidth();
    imgHeight = colorMap.getImageHeight();

    scaleColorMap = 1.0;
    if (colorMap.getImageWidth() > maxImgWidth) {
      scaleColorMap = (double) colorMap.getImageWidth() / (double) maxImgWidth;
    }
    cachedPreprocessedImageKey += "#" + conv_north + "#" + conv_east + "#" + conv_south + "#" + conv_west + "#" + erode + "#" + erodeSize + "#" + maxImgWidth;
  }

  private static SimpleImage dfltImage = null;

  private synchronized SimpleImage getDfltImage() {
    if (dfltImage == null) {
      GradientCreator creator = new GradientCreator();
      dfltImage = creator.createImage(128, 128);
    }
    return dfltImage;
  }

  public int getImgWidth() {
    return imgWidth;
  }

  public int getImgHeight() {
    return imgHeight;
  }

  public double getScaleColorMap() {
    return scaleColorMap;
  }

  public WFImage getColorMap() {
    return colorMap;
  }

  public double getImage_brightness() {
    return image_brightness;
  }

  public double getScaleX() {
    return scaleX;
  }

  public double getScaleY() {
    return scaleY;
  }

  public double getScaleZ() {
    return scaleZ;
  }

  public double getOffsetX() {
    return offsetX;
  }

  public double getOffsetY() {
    return offsetY;
  }

  public double getOffsetZ() {
    return offsetZ;
  }

  public double getIFlame_brightness() {
    return iflame_brightness;
  }

  public double getIFlame_density() {
    return iflame_density;
  }

  public int getId() {
    return id;
  }

  public void setImageFilename(String pImageFilename) {
    imageFilename = pImageFilename;
  }

  public void setErode(int pErode) {
    erode = pErode;
  }

  public void setConv_north(int pConv_north) {
    conv_north = pConv_north;
  }

  public void setConv_east(int pConv_east) {
    conv_east = pConv_east;
  }

  public void setConv_south(int pConv_south) {
    conv_south = pConv_south;
  }

  public void setConv_west(int pConv_west) {
    conv_west = pConv_west;
  }

  public void setErodeSize(int pErodeSize) {
    erodeSize = pErodeSize;
  }

  public void setMaxImgWidth(int pMaxImgWidth) {
    maxImgWidth = pMaxImgWidth;
  }

  public ShapeDistribution getShape_distribution() {
    return shape_distribution;
  }

  public void setShape_distribution(ShapeDistribution pShape_distribution) {
    shape_distribution = pShape_distribution;
  }

  public void setStructure_threshold(double pStructure_threshold) {
    structure_threshold = pStructure_threshold;
  }

  public void setStructure_density(double pStructure_density) {
    structure_density = pStructure_density;
  }

  public void setScaleX(double pScaleX) {
    scaleX = pScaleX;
  }

  public void setScaleY(double pScaleY) {
    scaleY = pScaleY;
  }

  public void setScaleZ(double pScaleZ) {
    scaleZ = pScaleZ;
  }

  public void setOffsetX(double pOffsetX) {
    offsetX = pOffsetX;
  }

  public void setOffsetY(double pOffsetY) {
    offsetY = pOffsetY;
  }

  public void setOffsetZ(double pOffsetZ) {
    offsetZ = pOffsetZ;
  }

  public double getIflame_density() {
    return iflame_density;
  }

  public void setIflame_density(double pIflame_density) {
    iflame_density = pIflame_density;
  }

  public double getIflame_brightness() {
    return iflame_brightness;
  }

  public void setIflame_brightness(double pIflame_brightness) {
    iflame_brightness = pIflame_brightness;
  }

  public void setImage_brightness(double pImage_brightness) {
    image_brightness = pImage_brightness;
  }

  public String getCachedPreprocessedImageKey() {
    return cachedPreprocessedImageKey;
  }

  public void setCachedPreprocessedImageKey(String cachedPreprocessedImageKey) {
    this.cachedPreprocessedImageKey = cachedPreprocessedImageKey;
  }

  public int getRenderId() {
    return renderId;
  }

  public void setRenderId(int pRenderId) {
    renderId = pRenderId;
  }

  public AbstractRandomGenerator getRandGen() {
    return randGen;
  }

  public double getColorChangeRed() {
    return color_change_red;
  }

  public void setColorChangeRed(double pColorChangeRed) {
    color_change_red = pColorChangeRed;
  }

  public double getColorChangeGreen() {
    return color_change_green;
  }

  public void setColorChangeGreen(double pColorChangeGreen) {
    color_change_green = pColorChangeGreen;
  }

  public double getColorChangeBlue() {
    return color_change_blue;
  }

  public void setColorChangeBlue(double pColorChangeBlue) {
    color_change_blue = pColorChangeBlue;
  }

  public double getColorChangeHue() {
    return color_change_hue;
  }

  public void setColorChangeHue(double pColorChangeHue) {
    color_change_hue = pColorChangeHue;
  }

  public double getColorChangeSaturation() {
    return color_change_saturation;
  }

  public void setColorChangeSaturation(double pColorChangeSaturation) {
    color_change_saturation = pColorChangeSaturation;
  }

  public double getColorChangeLightness() {
    return color_change_lightness;
  }

  public void setColorChangeLightness(double pColorChangeLightness) {
    color_change_lightness = pColorChangeLightness;
  }

}
