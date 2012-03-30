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

import org.jwildfire.swing.LookAndFeel;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class Prefs extends ManagedObject {
  // DON'T forget to update the assign() method after adding new properties!!!
  static final String PREFS_FILE = "j-wildfire.properties";

  static final String KEY_GENERAL_PLAF_STYLE = "general.plaf.style";
  static final String KEY_GENERAL_PLAF_THEME = "general.plaf.theme";
  static final String KEY_GENERAL_PATH_IMAGES = "general.path.images";
  static final String KEY_GENERAL_PATH_SCRIPTS = "general.path.scripts";
  static final String KEY_SUNFLOW_PATH_SCENES = "sunflow.path.scenes";

  static final String KEY_TINA_PATH_FLAMES = "tina.path.flames";
  static final String KEY_TINA_RENDER_IMAGE_WIDTH = "tina.render.image.width";
  static final String KEY_TINA_RENDER_IMAGE_HEIGHT = "tina.render.image.height";
  static final String KEY_TINA_RENDER_MOVIE_WIDTH = "tina.render.movie.width";
  static final String KEY_TINA_RENDER_MOVIE_HEIGHT = "tina.render.movie.height";
  static final String KEY_TINA_RENDER_MOVIE_FRAMES = "tina.render.movie.frames";

  static final String KEY_TINA_RENDER_REALTIME_QUALITY = "tina.render.realtime.quality";

  static final String KEY_TINA_RENDER_PREVIEW_SPATIAL_OVERSAMPLE = "tina.render.preview.spatial_oversample";
  static final String KEY_TINA_RENDER_PREVIEW_COLOR_OVERSAMPLE = "tina.render.preview.color_oversample";
  static final String KEY_TINA_RENDER_PREVIEW_FILTER_RADIUS = "tina.render.preview.filter_radius";
  static final String KEY_TINA_RENDER_PREVIEW_QUALITY = "tina.render.preview.quality";

  static final String KEY_TINA_RENDER_NORMAL_SPATIAL_OVERSAMPLE = "tina.render.normal.spatial_oversample";
  static final String KEY_TINA_RENDER_NORMAL_COLOR_OVERSAMPLE = "tina.render.normal.color_oversample";
  static final String KEY_TINA_RENDER_NORMAL_FILTER_RADIUS = "tina.render.normal.filter_radius";
  static final String KEY_TINA_RENDER_NORMAL_QUALITY = "tina.render.normal.quality";
  static final String KEY_TINA_RENDER_NORMAL_HDR = "tina.render.normal.hdr";
  static final String KEY_TINA_RENDER_NORMAL_HDR_INTENSITY_MAP = "tina.render.normal.hdr.intensity_map";

  static final String KEY_TINA_RENDER_HIGH_SPATIAL_OVERSAMPLE = "tina.render.high.spatial_oversample";
  static final String KEY_TINA_RENDER_HIGH_COLOR_OVERSAMPLE = "tina.render.high.color_oversample";
  static final String KEY_TINA_RENDER_HIGH_FILTER_RADIUS = "tina.render.high.filter_radius";
  static final String KEY_TINA_RENDER_HIGH_QUALITY = "tina.render.high.quality";
  static final String KEY_TINA_RENDER_HIGH_HDR = "tina.render.high.hdr";
  static final String KEY_TINA_RENDER_HIGH_HDR_INTENSITY_MAP = "tina.render.high.hdr.intensity_map";

  static final String KEY_TINA_RENDER_MOVIE_SPATIAL_OVERSAMPLE = "tina.render.movie.spatial_oversample";
  static final String KEY_TINA_RENDER_MOVIE_COLOR_OVERSAMPLE = "tina.render.movie.color_oversample";
  static final String KEY_TINA_RENDER_MOVIE_FILTER_RADIUS = "tina.render.movie.filter_radius";
  static final String KEY_TINA_RENDER_MOVIE_QUALITY = "tina.render.movie.quality";

  static final String KEY_TINA_RANDOMBATCH_SIZE = "tina.random_batch.size";
  static final String KEY_TINA_RANDOMBATCH_BGCOLOR_RED = "tina.random_batch.bg_color.red";
  static final String KEY_TINA_RANDOMBATCH_BGCOLOR_GREEN = "tina.random_batch.bg_color.green";
  static final String KEY_TINA_RANDOMBATCH_BGCOLOR_BLUE = "tina.random_batch.bg_color.blue";

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

  public static class PLAFStyleEditor extends ComboBoxPropertyEditor {
    public PLAFStyleEditor() {
      super();
      setAvailableValues(new String[] { LookAndFeel.PLAF_AERO, LookAndFeel.PLAF_ALUMINIUM, LookAndFeel.PLAF_BERNSTEIN, LookAndFeel.PLAF_FAST, LookAndFeel.PLAF_GRAPHITE, LookAndFeel.PLAF_HIFI, LookAndFeel.PLAF_MCWIN, LookAndFeel.PLAF_MINT,
          LookAndFeel.PLAF_NIMBUS, LookAndFeel.PLAF_NOIRE, LookAndFeel.PLAF_LUNA, LookAndFeel.PLAF_SMART });
    }
  }

  @Property(description = "Look and feel (major UI style) - changes are applied only after restarting the main program", category = PropertyCategory.TINA, editorClass = PLAFStyleEditor.class)
  private String plafStyle = LookAndFeel.PLAF_NOIRE;
  @Property(description = "Look and feel theme (UI sub style) - changes are applied only after restarting the main program", category = PropertyCategory.TINA)
  private String plafTheme = LookAndFeel.THEME_DEFAULT;

  @Property(description = "Image render width", category = PropertyCategory.TINA)
  private int tinaRenderImageWidth = 800;
  @Property(description = "Image render height", category = PropertyCategory.TINA)
  private int tinaRenderImageHeight = 600;
  @Property(description = "Movie render width", category = PropertyCategory.TINA)
  private int tinaRenderMovieWidth = 640;
  @Property(description = "Movie render height", category = PropertyCategory.TINA)
  private int tinaRenderMovieHeight = 480;
  @Property(description = "Default number of frames for a movie", category = PropertyCategory.TINA)
  private int tinaRenderMovieFrames = 90;

  private static int tinaRenderThreads;

  @Property(description = "Quality for realtime rendering (please restart app after changing this)", category = PropertyCategory.TINA)
  private int tinaRenderRealtimeQuality = 1;

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
  @Property(description = "Render an additional hdr image for further processing while normal rendering", category = PropertyCategory.TINA)
  private boolean tinaRenderNormalHDR = false;
  @Property(description = "Render an additional hdr intensity image for further processing while normal rendering", category = PropertyCategory.TINA)
  private boolean tinaRenderNormalHDRIntensityMap = false;

  @Property(description = "Spatial oversample for high quality rendering", category = PropertyCategory.TINA)
  private int tinaRenderHighSpatialOversample = 2;
  @Property(description = "Color oversample for high quality rendering", category = PropertyCategory.TINA)
  private int tinaRenderHighColorOversample = 3;
  @Property(description = "Filter radius for high quality rendering", category = PropertyCategory.TINA)
  private double tinaRenderHighFilterRadius = 1.25;
  @Property(description = "Quality for high quality rendering", category = PropertyCategory.TINA)
  private int tinaRenderHighQuality = 500;
  @Property(description = "Render an additional hdr image for further processing while high quality rendering", category = PropertyCategory.TINA)
  private boolean tinaRenderHighHDR = true;
  @Property(description = "Render an additional hdr intensity image for further processing while high quality rendering", category = PropertyCategory.TINA)
  private boolean tinaRenderHighHDRIntensityMap = true;

  @Property(description = "Spatial oversample for movie rendering", category = PropertyCategory.TINA)
  private int tinaRenderMovieSpatialOversample = 2;
  @Property(description = "Color oversample for movie rendering", category = PropertyCategory.TINA)
  private int tinaRenderMovieColorOversample = 2;
  @Property(description = "Filter radius for movie rendering", category = PropertyCategory.TINA)
  private double tinaRenderMovieFilterRadius = 1.25;
  @Property(description = "Quality for movie rendering", category = PropertyCategory.TINA)
  private int tinaRenderMovieQuality = 150;

  @Property(description = "Number of generated flames by invoking the \"Random flames\" function", category = PropertyCategory.TINA)
  private int tinaRandomBatchSize = 24;
  @Property(description = "Red component of the background color of randomly generated flames", category = PropertyCategory.TINA)
  private int tinaRandomBatchBGColorRed = 0;
  @Property(description = "Red component of the background color of randomly generated flames", category = PropertyCategory.TINA)
  private int tinaRandomBatchBGColorGreen = 0;
  @Property(description = "Red component of the background color of randomly generated flames", category = PropertyCategory.TINA)
  private int tinaRandomBatchBGColorBlue = 0;

  public String getInputScriptPath() {
    return lastInputScriptPath != null ? lastInputScriptPath : scriptPath;
  }

  public String getOutputScriptPath() {
    return lastOutputScriptPath != null ? lastOutputScriptPath : scriptPath;
  }

  public void setScriptPath(String pScriptPath) {
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
    plafStyle = pSrc.plafStyle;
    plafTheme = pSrc.plafTheme;

    tinaRenderImageWidth = pSrc.tinaRenderImageWidth;
    tinaRenderImageHeight = pSrc.tinaRenderImageHeight;
    tinaRenderMovieWidth = pSrc.tinaRenderMovieWidth;
    tinaRenderMovieHeight = pSrc.tinaRenderMovieHeight;
    tinaRenderMovieFrames = pSrc.tinaRenderMovieFrames;
    tinaRenderPreviewSpatialOversample = pSrc.tinaRenderPreviewSpatialOversample;
    tinaRenderPreviewColorOversample = pSrc.tinaRenderPreviewColorOversample;
    tinaRenderPreviewFilterRadius = pSrc.tinaRenderPreviewFilterRadius;
    tinaRenderPreviewQuality = pSrc.tinaRenderPreviewQuality;
    tinaRenderNormalSpatialOversample = pSrc.tinaRenderNormalSpatialOversample;
    tinaRenderNormalColorOversample = pSrc.tinaRenderNormalColorOversample;
    tinaRenderNormalFilterRadius = pSrc.tinaRenderNormalFilterRadius;
    tinaRenderNormalQuality = pSrc.tinaRenderNormalQuality;
    tinaRenderNormalHDR = pSrc.tinaRenderNormalHDR;
    tinaRenderNormalHDRIntensityMap = pSrc.tinaRenderNormalHDRIntensityMap;
    tinaRenderHighSpatialOversample = pSrc.tinaRenderHighSpatialOversample;
    tinaRenderHighColorOversample = pSrc.tinaRenderHighColorOversample;
    tinaRenderHighFilterRadius = pSrc.tinaRenderHighFilterRadius;
    tinaRenderHighQuality = pSrc.tinaRenderHighQuality;
    tinaRenderHighHDR = pSrc.tinaRenderHighHDR;
    tinaRenderHighHDRIntensityMap = pSrc.tinaRenderHighHDRIntensityMap;
    tinaRenderMovieSpatialOversample = pSrc.tinaRenderMovieSpatialOversample;
    tinaRenderMovieColorOversample = pSrc.tinaRenderMovieColorOversample;
    tinaRenderMovieFilterRadius = pSrc.tinaRenderMovieFilterRadius;
    tinaRenderMovieQuality = pSrc.tinaRenderMovieQuality;
    tinaRenderRealtimeQuality = pSrc.tinaRenderRealtimeQuality;
    tinaRandomBatchSize = pSrc.tinaRandomBatchSize;
    tinaRandomBatchBGColorRed = pSrc.tinaRandomBatchBGColorRed;
    tinaRandomBatchBGColorGreen = pSrc.tinaRandomBatchBGColorGreen;
    tinaRandomBatchBGColorBlue = pSrc.tinaRandomBatchBGColorBlue;
  }

  public int getTinaRenderThreads() {
    return tinaRenderThreads;
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

  public int getTinaRenderMovieSpatialOversample() {
    return tinaRenderMovieSpatialOversample;
  }

  public void setTinaRenderMovieSpatialOversample(int tinaRenderMovieSpatialOversample) {
    this.tinaRenderMovieSpatialOversample = tinaRenderMovieSpatialOversample;
  }

  public int getTinaRenderMovieColorOversample() {
    return tinaRenderMovieColorOversample;
  }

  public void setTinaRenderMovieColorOversample(int tinaRenderMovieColorOversample) {
    this.tinaRenderMovieColorOversample = tinaRenderMovieColorOversample;
  }

  public double getTinaRenderMovieFilterRadius() {
    return tinaRenderMovieFilterRadius;
  }

  public void setTinaRenderMovieFilterRadius(double tinaRenderMovieFilterRadius) {
    this.tinaRenderMovieFilterRadius = tinaRenderMovieFilterRadius;
  }

  public int getTinaRenderMovieQuality() {
    return tinaRenderMovieQuality;
  }

  public void setTinaRenderMovieQuality(int tinaRenderMovieQuality) {
    this.tinaRenderMovieQuality = tinaRenderMovieQuality;
  }

  public int getTinaRenderImageWidth() {
    return tinaRenderImageWidth;
  }

  public void setTinaRenderImageWidth(int tinaRenderImageWidth) {
    this.tinaRenderImageWidth = tinaRenderImageWidth;
  }

  public int getTinaRenderImageHeight() {
    return tinaRenderImageHeight;
  }

  public void setTinaRenderImageHeight(int tinaRenderImageHeight) {
    this.tinaRenderImageHeight = tinaRenderImageHeight;
  }

  public int getTinaRenderMovieWidth() {
    return tinaRenderMovieWidth;
  }

  public void setTinaRenderMovieWidth(int tinaRenderMovieWidth) {
    this.tinaRenderMovieWidth = tinaRenderMovieWidth;
  }

  public int getTinaRenderMovieHeight() {
    return tinaRenderMovieHeight;
  }

  public void setTinaRenderMovieHeight(int tinaRenderMovieHeight) {
    this.tinaRenderMovieHeight = tinaRenderMovieHeight;
  }

  public int getTinaRenderMovieFrames() {
    return tinaRenderMovieFrames;
  }

  public void setTinaRenderMovieFrames(int tinaRenderMovieFrames) {
    this.tinaRenderMovieFrames = tinaRenderMovieFrames;
  }

  public int getTinaRenderRealtimeQuality() {
    return tinaRenderRealtimeQuality;
  }

  public void setTinaRenderRealtimeQuality(int tinaRenderRealtimeQuality) {
    this.tinaRenderRealtimeQuality = tinaRenderRealtimeQuality;
  }

  public boolean isTinaRenderHighHDR() {
    return tinaRenderHighHDR;
  }

  public void setTinaRenderHighHDR(boolean tinaRenderHighHDR) {
    this.tinaRenderHighHDR = tinaRenderHighHDR;
  }

  public boolean isTinaRenderNormalHDR() {
    return tinaRenderNormalHDR;
  }

  public void setTinaRenderNormalHDR(boolean tinaRenderNormalHDR) {
    this.tinaRenderNormalHDR = tinaRenderNormalHDR;
  }

  public int getTinaRandomBatchSize() {
    return tinaRandomBatchSize;
  }

  public void setTinaRandomBatchSize(int tinaRandomBatchSize) {
    this.tinaRandomBatchSize = tinaRandomBatchSize;
  }

  public int getTinaRandomBatchBGColorRed() {
    return tinaRandomBatchBGColorRed;
  }

  public void setTinaRandomBatchBGColorRed(int tinaRandomBatchBGColorRed) {
    this.tinaRandomBatchBGColorRed = tinaRandomBatchBGColorRed;
  }

  public int getTinaRandomBatchBGColorGreen() {
    return tinaRandomBatchBGColorGreen;
  }

  public void setTinaRandomBatchBGColorGreen(int tinaRandomBatchBGColorGreen) {
    this.tinaRandomBatchBGColorGreen = tinaRandomBatchBGColorGreen;
  }

  public int getTinaRandomBatchBGColorBlue() {
    return tinaRandomBatchBGColorBlue;
  }

  public void setTinaRandomBatchBGColorBlue(int tinaRandomBatchBGColorBlue) {
    this.tinaRandomBatchBGColorBlue = tinaRandomBatchBGColorBlue;
  }

  public boolean isTinaRenderNormalHDRIntensityMap() {
    return tinaRenderNormalHDRIntensityMap;
  }

  public void setTinaRenderNormalHDRIntensityMap(boolean tinaRenderNormalHDRIntensityMap) {
    this.tinaRenderNormalHDRIntensityMap = tinaRenderNormalHDRIntensityMap;
  }

  public boolean isTinaRenderHighHDRIntensityMap() {
    return tinaRenderHighHDRIntensityMap;
  }

  public void setTinaRenderHighHDRIntensityMap(boolean tinaRenderHighHDRIntensityMap) {
    this.tinaRenderHighHDRIntensityMap = tinaRenderHighHDRIntensityMap;
  }

  public String getPlafStyle() {
    return plafStyle;
  }

  public void setPlafStyle(String plafStyle) {
    this.plafStyle = plafStyle;
  }

  public String getPlafTheme() {
    return plafTheme;
  }

  public void setPlafTheme(String plafTheme) {
    this.plafTheme = plafTheme;
  }

  static {
    tinaRenderThreads = Runtime.getRuntime().availableProcessors();
    if (tinaRenderThreads < 1) {
      tinaRenderThreads = 1;
    }
  }
}
