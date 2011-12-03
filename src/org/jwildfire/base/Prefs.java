/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.base;

import java.io.File;

public class Prefs extends ManagedObject {
  static final String PREFS_FILE = "j-wildfire.properties";

  static final String KEY_GENERAL_PATH_IMAGES = "general.path.images";
  static final String KEY_GENERAL_PATH_SCRIPTS = "general.path.scripts";
  static final String KEY_SUNFLOW_PATH_SCENES = "sunflow.path.scenes";

  static final String KEY_TINA_PATH_FLAMES = "tina.path.flames";
  static final String KEY_TINA_RENDER_THREADS = "tina.render.threads";
  static final String KEY_TINA_RENDER_WIDTH = "tina.render.width";
  static final String KEY_TINA_RENDER_HEIGHT = "tina.render.height";

  static final String KEY_TINA_RENDER_PREVIEW_SPATIAL_OVERSAMPLE = "tina.render.preview.spatial_oversample";
  static final String KEY_TINA_RENDER_PREVIEW_COLOR_OVERSAMPLE = "tina.render.preview.color_oversample";
  static final String KEY_TINA_RENDER_PREVIEW_FILTER_RADIUS = "tina.render.preview.filter_radius";
  static final String KEY_TINA_RENDER_PREVIEW_QUALITY = "tina.render.preview.quality";

  static final String KEY_TINA_RENDER_NORMAL_SPATIAL_OVERSAMPLE = "tina.render.normal.spatial_oversample";
  static final String KEY_TINA_RENDER_NORMAL_COLOR_OVERSAMPLE = "tina.render.normal.color_oversample";
  static final String KEY_TINA_RENDER_NORMAL_FILTER_RADIUS = "tina.render.normal.filter_radius";
  static final String KEY_TINA_RENDER_NORMAL_QUALITY = "tina.render.normal.quality";

  static final String KEY_TINA_RENDER_HIGH_SPATIAL_OVERSAMPLE = "tina.render.high.spatial_oversample";
  static final String KEY_TINA_RENDER_HIGH_COLOR_OVERSAMPLE = "tina.render.high.color_oversample";
  static final String KEY_TINA_RENDER_HIGH_FILTER_RADIUS = "tina.render.high.filter_radius";
  static final String KEY_TINA_RENDER_HIGH_QUALITY = "tina.render.high.quality";

  @Property(description = "Script drawer", category = PropertyCategory.GENERAL)
  private String scriptPath = null;
  private String lastInputScriptPath = null;
  private String lastOutputScriptPath = null;

  @Property(description = "Image drawer", category = PropertyCategory.GENERAL)
  private String imagePath = null;
  private String lastInputImagePath = null;
  private String lastOutputImagePath = null;

  @Property(description = "Flame file drawer", category = PropertyCategory.TINA)
  private String tinaFlamePath = null;
  private String lastInputFlamePath = null;
  private String lastOutputFlamePath = null;

  @Property(description = "Sunflow scene file drawer", category = PropertyCategory.SUNFLOW)
  private String sunflowScenePath = null;
  private String lastInputSunflowScenePath = null;
  private String lastOutputSunflowScenePath = null;

  @Property(description = "Default render width", category = PropertyCategory.TINA)
  private int tinaRenderWidth = 800;
  @Property(description = "Default render height", category = PropertyCategory.TINA)
  private int tinaRenderHeight = 600;
  @Property(description = "Maximum number of threads", category = PropertyCategory.TINA)
  private int tinaRenderThreads = 8;

  @Property(description = "Spatial oversample for preview rendering", category = PropertyCategory.TINA)
  private int tinaRenderPreviewSpatialOversample = 1;
  @Property(description = "Color oversample for preview rendering", category = PropertyCategory.TINA)
  private int tinaRenderPreviewColorOversample = 1;
  @Property(description = "Filter radius for preview rendering", category = PropertyCategory.TINA)
  private double tinaRenderPreviewFilterRadius = 0.0;
  @Property(description = "Quality for preview rendering", category = PropertyCategory.TINA)
  private int tinaRenderPreviewQuality = 100;

  @Property(description = "Spatial oversample for normal rendering", category = PropertyCategory.TINA)
  private int tinaRenderNormalSpatialOversample = 1;
  @Property(description = "Color oversample for normal rendering", category = PropertyCategory.TINA)
  private int tinaRenderNormalColorOversample = 1;
  @Property(description = "Filter radius for normal rendering", category = PropertyCategory.TINA)
  private double tinaRenderNormalFilterRadius = 1.25;
  @Property(description = "Quality for normal rendering", category = PropertyCategory.TINA)
  private int tinaRenderNormalQuality = 150;

  @Property(description = "Spatial oversample for high quality rendering", category = PropertyCategory.TINA)
  private int tinaRenderHighSpatialOversample = 2;
  @Property(description = "Color oversample for high quality rendering", category = PropertyCategory.TINA)
  private int tinaRenderHighColorOversample = 3;
  @Property(description = "Filter radius for high quality rendering", category = PropertyCategory.TINA)
  private double tinaRenderHighFilterRadius = 1.25;
  @Property(description = "Quality for high quality rendering", category = PropertyCategory.TINA)
  private int tinaRenderHighQuality = 500;

  public String getInputScriptPath() {
    return lastInputScriptPath != null ? lastInputScriptPath : scriptPath;
  }

  public String getOutputScriptPath() {
    return lastOutputScriptPath != null ? lastOutputScriptPath : scriptPath;
  }

  void setScriptPath(String pScriptPath) {
    this.scriptPath = pScriptPath;
  }

  public String getInputImagePath() {
    return lastInputImagePath != null ? lastInputImagePath : imagePath;
  }

  public String getOutputImagePath() {
    return lastOutputImagePath != null ? lastOutputImagePath : imagePath;
  }

  public void setLastInputScriptFile(File pFile) {
    lastInputScriptPath = pFile.getParent();
    if (imagePath == null || imagePath.length() == 0) {
      imagePath = lastInputScriptPath;
    }
  }

  public void setLastOutputScriptFile(File pFile) {
    lastOutputScriptPath = pFile.getParent();
    if (scriptPath == null || scriptPath.length() == 0) {
      scriptPath = lastOutputScriptPath;
    }
  }

  public void setLastInputImageFile(File pFile) {
    lastInputImagePath = pFile.getParent();
    if (imagePath == null || imagePath.length() == 0) {
      imagePath = lastInputImagePath;
    }
  }

  public void setLastOutputImageFile(File pFile) {
    lastOutputImagePath = pFile.getParent();
    if (imagePath == null || imagePath.length() == 0) {
      imagePath = lastOutputImagePath;
    }
  }

  public String getInputFlamePath() {
    return lastInputFlamePath != null ? lastInputFlamePath : tinaFlamePath;
  }

  public void setLastInputFlameFile(File pFile) {
    lastInputFlamePath = pFile.getParent();
    if (tinaFlamePath == null || tinaFlamePath.length() == 0) {
      tinaFlamePath = lastInputFlamePath;
    }
  }

  public String getOutputFlamePath() {
    return lastOutputFlamePath != null ? lastOutputFlamePath : tinaFlamePath;
  }

  public void setLastOutputFlameFile(File pFile) {
    lastOutputFlamePath = pFile.getParent();
    if (tinaFlamePath == null || tinaFlamePath.length() == 0) {
      tinaFlamePath = lastOutputFlamePath;
    }
  }

  public String getInputSunflowScenePath() {
    return lastInputSunflowScenePath != null ? lastInputSunflowScenePath : sunflowScenePath;
  }

  public void setLastInputSunflowSceneFile(File pFile) {
    lastInputSunflowScenePath = pFile.getParent();
    if (sunflowScenePath == null || sunflowScenePath.length() == 0) {
      sunflowScenePath = lastInputSunflowScenePath;
    }
  }

  public String getOutputSunflowScenePath() {
    return lastOutputSunflowScenePath != null ? lastOutputSunflowScenePath : sunflowScenePath;
  }

  public void setLastOutputSunflowSceneFile(File pFile) {
    lastOutputSunflowScenePath = pFile.getParent();
    if (sunflowScenePath == null || sunflowScenePath.length() == 0) {
      sunflowScenePath = lastOutputSunflowScenePath;
    }
  }

  public void loadFromFile() throws Exception {
    new PrefsReader().readPrefs(this);
  }

  public void saveToFromFile() throws Exception {
    new PrefsWriter().writePrefs(this);
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public void setTinaFlamePath(String flamePath) {
    this.tinaFlamePath = flamePath;
  }

  public void setSunflowScenePath(String sunflowScenePath) {
    this.sunflowScenePath = sunflowScenePath;
  }

  public String getImagePath() {
    return imagePath;
  }

  public String getTinaFlamePath() {
    return tinaFlamePath;
  }

  public String getSunflowScenePath() {
    return sunflowScenePath;
  }

  public String getScriptPath() {
    return scriptPath;
  }

  public void assign(Prefs pSrc) {
    scriptPath = pSrc.scriptPath;
    lastInputScriptPath = pSrc.lastInputScriptPath;
    lastOutputScriptPath = pSrc.lastOutputScriptPath;
    imagePath = pSrc.imagePath;
    lastInputImagePath = pSrc.lastInputImagePath;
    lastOutputImagePath = pSrc.lastOutputImagePath;
    tinaFlamePath = pSrc.tinaFlamePath;
    lastInputFlamePath = pSrc.lastInputFlamePath;
    lastOutputFlamePath = pSrc.lastOutputFlamePath;
    sunflowScenePath = pSrc.sunflowScenePath;
    lastInputSunflowScenePath = pSrc.lastInputSunflowScenePath;
    lastOutputSunflowScenePath = pSrc.lastOutputSunflowScenePath;

    tinaRenderWidth = pSrc.tinaRenderWidth;
    tinaRenderHeight = pSrc.tinaRenderHeight;
    tinaRenderThreads = pSrc.tinaRenderThreads;
    tinaRenderPreviewSpatialOversample = pSrc.tinaRenderPreviewSpatialOversample;
    tinaRenderPreviewColorOversample = pSrc.tinaRenderPreviewColorOversample;
    tinaRenderPreviewFilterRadius = pSrc.tinaRenderPreviewFilterRadius;
    tinaRenderPreviewQuality = pSrc.tinaRenderPreviewQuality;
    tinaRenderNormalSpatialOversample = pSrc.tinaRenderNormalSpatialOversample;
    tinaRenderNormalColorOversample = pSrc.tinaRenderNormalColorOversample;
    tinaRenderNormalFilterRadius = pSrc.tinaRenderNormalFilterRadius;
    tinaRenderNormalQuality = pSrc.tinaRenderNormalQuality;
    tinaRenderHighSpatialOversample = pSrc.tinaRenderHighSpatialOversample;
    tinaRenderHighColorOversample = pSrc.tinaRenderHighColorOversample;
    tinaRenderHighFilterRadius = pSrc.tinaRenderHighFilterRadius;
    tinaRenderHighQuality = pSrc.tinaRenderHighQuality;
  }

  public int getTinaRenderWidth() {
    return tinaRenderWidth;
  }

  public void setTinaRenderWidth(int tinaRenderWidth) {
    this.tinaRenderWidth = tinaRenderWidth;
  }

  public int getTinaRenderHeight() {
    return tinaRenderHeight;
  }

  public void setTinaRenderHeight(int tinaRenderHeight) {
    this.tinaRenderHeight = tinaRenderHeight;
  }

  public int getTinaRenderThreads() {
    return tinaRenderThreads;
  }

  public void setTinaRenderThreads(int tinaRenderThreads) {
    this.tinaRenderThreads = tinaRenderThreads;
  }

  public int getTinaRenderPreviewSpatialOversample() {
    return tinaRenderPreviewSpatialOversample;
  }

  public void setTinaRenderPreviewSpatialOversample(int tinaRenderPreviewSpatialOversample) {
    this.tinaRenderPreviewSpatialOversample = tinaRenderPreviewSpatialOversample;
  }

  public int getTinaRenderPreviewColorOversample() {
    return tinaRenderPreviewColorOversample;
  }

  public void setTinaRenderPreviewColorOversample(int tinaRenderPreviewColorOversample) {
    this.tinaRenderPreviewColorOversample = tinaRenderPreviewColorOversample;
  }

  public double getTinaRenderPreviewFilterRadius() {
    return tinaRenderPreviewFilterRadius;
  }

  public void setTinaRenderPreviewFilterRadius(double tinaRenderPreviewFilterRadius) {
    this.tinaRenderPreviewFilterRadius = tinaRenderPreviewFilterRadius;
  }

  public int getTinaRenderPreviewQuality() {
    return tinaRenderPreviewQuality;
  }

  public void setTinaRenderPreviewQuality(int tinaRenderPreviewQuality) {
    this.tinaRenderPreviewQuality = tinaRenderPreviewQuality;
  }

  public int getTinaRenderNormalSpatialOversample() {
    return tinaRenderNormalSpatialOversample;
  }

  public void setTinaRenderNormalSpatialOversample(int tinaRenderNormalSpatialOversample) {
    this.tinaRenderNormalSpatialOversample = tinaRenderNormalSpatialOversample;
  }

  public int getTinaRenderNormalColorOversample() {
    return tinaRenderNormalColorOversample;
  }

  public void setTinaRenderNormalColorOversample(int tinaRenderNormalColorOversample) {
    this.tinaRenderNormalColorOversample = tinaRenderNormalColorOversample;
  }

  public double getTinaRenderNormalFilterRadius() {
    return tinaRenderNormalFilterRadius;
  }

  public void setTinaRenderNormalFilterRadius(double tinaRenderNormalFilterRadius) {
    this.tinaRenderNormalFilterRadius = tinaRenderNormalFilterRadius;
  }

  public int getTinaRenderNormalQuality() {
    return tinaRenderNormalQuality;
  }

  public void setTinaRenderNormalQuality(int tinaRenderNormalQuality) {
    this.tinaRenderNormalQuality = tinaRenderNormalQuality;
  }

  public int getTinaRenderHighSpatialOversample() {
    return tinaRenderHighSpatialOversample;
  }

  public void setTinaRenderHighSpatialOversample(int tinaRenderHighSpatialOversample) {
    this.tinaRenderHighSpatialOversample = tinaRenderHighSpatialOversample;
  }

  public int getTinaRenderHighColorOversample() {
    return tinaRenderHighColorOversample;
  }

  public void setTinaRenderHighColorOversample(int tinaRenderHighColorOversample) {
    this.tinaRenderHighColorOversample = tinaRenderHighColorOversample;
  }

  public double getTinaRenderHighFilterRadius() {
    return tinaRenderHighFilterRadius;
  }

  public void setTinaRenderHighFilterRadius(double tinaRenderHighFilterRadius) {
    this.tinaRenderHighFilterRadius = tinaRenderHighFilterRadius;
  }

  public int getTinaRenderHighQuality() {
    return tinaRenderHighQuality;
  }

  public void setTinaRenderHighQuality(int tinaRenderHighQuality) {
    this.tinaRenderHighQuality = tinaRenderHighQuality;
  }
}
