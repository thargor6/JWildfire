/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

import org.jwildfire.base.mathlib.BaseMathLibType;
import org.jwildfire.create.tina.base.raster.RasterPointPrecision;
import org.jwildfire.create.tina.random.RandomGeneratorType;
import org.jwildfire.create.tina.swing.RandomBatchRefreshType;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelControlStyle;
import org.jwildfire.swing.LookAndFeelType;

public class PrefsReader {

  private String getProperty(Properties pProperties, String pKey, String pDefaultValue) {
    String res = pProperties.getProperty(pKey, "").trim();
    return res.length() > 0 ? res : pDefaultValue;
  }

  private int getIntProperty(Properties pProperties, String pKey, int pDefaultValue) {
    String val = pProperties.getProperty(pKey, "").trim();
    return val.length() > 0 ? Tools.stringToInt(val) : pDefaultValue;
  }

  private boolean getBooleanProperty(Properties pProperties, String pKey, boolean pDefaultValue) {
    String val = pProperties.getProperty(pKey, "").trim();
    return val.length() > 0 ? val.equalsIgnoreCase("true") : pDefaultValue;
  }

  private double getDoubleProperty(Properties pProperties, String pKey, double pDefaultValue) {
    String val = pProperties.getProperty(pKey, "").trim();
    return val.length() > 0 ? Tools.stringToDouble(val) : pDefaultValue;
  }

  public void readPrefs(Prefs pPrefs) throws Exception {
    File file = new File(System.getProperty("user.home"), Prefs.PREFS_FILE);
    if (file.exists()) {
      InputStream inputStream = new FileInputStream(file);
      try {
        Properties props = new Properties();
        props.load(inputStream);
        pPrefs.setDevelopmentMode(getBooleanProperty(props, Prefs.KEY_GENERAL_DEVELOPMENT_MODE, pPrefs.isDevelopmentMode()));
        {
          String lookAndFeel = getProperty(props, Prefs.KEY_GENERAL_LOOK_AND_FEEL, "");
          if (lookAndFeel.length() > 0) {
            try {
              pPrefs.setLookAndFeelType(LookAndFeelType.valueOf(lookAndFeel));
              pPrefs.setLookAndFeelTheme(getProperty(props, Prefs.KEY_GENERAL_LOOK_AND_FEEL_THEME, pPrefs.getLookAndFeelTheme()));
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
        pPrefs.setImagePath(getProperty(props, Prefs.KEY_GENERAL_PATH_IMAGES, pPrefs.getImagePath()));
        pPrefs.setScriptPath(getProperty(props, Prefs.KEY_GENERAL_PATH_SCRIPTS, pPrefs.getScriptPath()));
        pPrefs.setMovieFlamesPath(getProperty(props, Prefs.KEY_TINA_PATH_MOVIEFLAMES, pPrefs.getMovieFlamesPath()));
        pPrefs.setSoundFilePath(getProperty(props, Prefs.KEY_GENERAL_PATH_SOUND_FILES, pPrefs.getSoundFilePath()));

        pPrefs.setTinaAssociateProfilesWithFlames(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_ASSOCIATE_WITH_FLAMES, pPrefs.isTinaAssociateProfilesWithFlames()));
        pPrefs.setTinaSaveFlamesWhenImageIsSaved(getBooleanProperty(props, Prefs.KEY_TINA_SAVING_STORE_FLAMES_WHEN_SAVING_IMAGE, pPrefs.isTinaSaveFlamesWhenImageIsSaved()));
        pPrefs.setTinaSaveHDRInIR(getBooleanProperty(props, Prefs.KEY_TINA_SAVING_STORE_HDR_IN_IR, pPrefs.isTinaSaveHDRInIR()));
        pPrefs.setTinaOptimizedRenderingIR(getBooleanProperty(props, Prefs.KEY_TINA_OPTIMIZED_RENDERING_IR, pPrefs.isTinaOptimizedRenderingIR()));
        pPrefs.setTinaUseExperimentalOpenClCode(getBooleanProperty(props, Prefs.KEY_TINA_USE_EXPERIMENTAL_OPENCL_CODE, pPrefs.isTinaUseExperimentalOpenClCode()));
        pPrefs.setTinaDefaultBGTransparency(getBooleanProperty(props, Prefs.KEY_TINA_RENDER_DEFAULT_BG_TRANSPARENCY, pPrefs.isTinaDefaultBGTransparency()));
        pPrefs.setTinaDefaultAntialiasingAmount(getDoubleProperty(props, Prefs.KEY_TINA_RENDER_DEFAULT_ANTIALIASING_AMOUNT, pPrefs.getTinaDefaultAntialiasingAmount()));
        pPrefs.setTinaDefaultAntialiasingRadius(getDoubleProperty(props, Prefs.KEY_TINA_RENDER_DEFAULT_ANTIALIASING_RADIUS, pPrefs.getTinaDefaultAntialiasingRadius()));
        pPrefs.setTinaFlamePath(getProperty(props, Prefs.KEY_TINA_PATH_FLAMES, pPrefs.getTinaFlamePath()));
        pPrefs.setTinaMeshPath(getProperty(props, Prefs.KEY_TINA_PATH_MESHES, pPrefs.getTinaMeshPath()));
        pPrefs.setTinaJWFMoviePath(getProperty(props, Prefs.KEY_TINA_PATH_JWFMOVIES, pPrefs.getTinaJWFMoviePath()));
        pPrefs.setTinaJWFScriptPath(getProperty(props, Prefs.KEY_TINA_PATH_JWFSCRIPTS, pPrefs.getTinaJWFScriptPath()));
        pPrefs.setTinaGradientPath(getProperty(props, Prefs.KEY_TINA_PATH_GRADIENTS, pPrefs.getTinaGradientPath()));
        pPrefs.setTinaSVGPath(getProperty(props, Prefs.KEY_TINA_PATH_SVG, pPrefs.getTinaSVGPath()));
        pPrefs.setTinaRenderMovieFrames(getIntProperty(props, Prefs.KEY_TINA_RENDER_MOVIE_FRAMES, pPrefs.getTinaRenderMovieFrames()));
        pPrefs.setTinaRenderPreviewQuality(getIntProperty(props, Prefs.KEY_TINA_RENDER_PREVIEW_QUALITY, pPrefs.getTinaRenderPreviewQuality()));
        pPrefs.setTinaRenderRealtimeQuality(getDoubleProperty(props, Prefs.KEY_TINA_RENDER_REALTIME_QUALITY, pPrefs.getTinaRenderRealtimeQuality()));
        pPrefs.setTinaPreserveFreeCPUs(getIntProperty(props, Prefs.KEY_TINA_PRESERVE_FREE_CPUS, pPrefs.getTinaPreserveFreeCPUs()));
        pPrefs.setTinaDisableWikimediaCommonsWarning(getBooleanProperty(props, Prefs.KEY_TINA_DISABLE_WIKIMEDIA_COMMONS_WARNING, pPrefs.isTinaDisableWikimediaCommonsWarning()));
        pPrefs.setTinaEditorControlsWithColor(getBooleanProperty(props, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_COLOR, pPrefs.isTinaEditorControlsWithColor()));
        pPrefs.setTinaEditorControlsWithAntialiasing(getBooleanProperty(props, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_ANTIALIASING, pPrefs.isTinaEditorControlsWithAntialiasing()));
        pPrefs.setTinaEditorControlsWithShadows(getBooleanProperty(props, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_SHADOWS, pPrefs.isTinaEditorControlsWithShadows()));
        pPrefs.setTinaEditorControlsWithNumbers(getBooleanProperty(props, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_NUMBERS, pPrefs.isTinaEditorControlsWithNumbers()));
        pPrefs.setTinaEditorGridSize(getDoubleProperty(props, Prefs.KEY_TINA_EDITOR_GRID_SIZE, pPrefs.getTinaEditorGridSize()));
        pPrefs.setTinaResponsiveness(getIntProperty(props, Prefs.KEY_TINA_RESPONSIVENESS, pPrefs.getTinaResponsiveness()));
        try {
          FlamePanelControlStyle style = FlamePanelControlStyle.valueOf(getProperty(props, Prefs.KEY_TINA_EDITOR_CONTROLS_STYLE, pPrefs.getTinaEditorControlsStyle().toString()));
          pPrefs.setTinaEditorControlsStyle(style);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }

        pPrefs.setTinaRandGenColorMapImagePath(getProperty(props, Prefs.KEY_TINA_COLORMAP_RANDGEN_IMAGE_PATH, pPrefs.getTinaRandGenColorMapImagePath()));

        try {
          RasterPointPrecision rasterPointPrecision = RasterPointPrecision.valueOf(getProperty(props, Prefs.KEY_TINA_RASTERPOINT_PRECISION, RasterPointPrecision.getDefaultValue().toString()));
          pPrefs.setTinaRasterPointPrecision(rasterPointPrecision);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        try {
          pPrefs.setBaseMathLibType(BaseMathLibType.valueOf(getProperty(props, Prefs.KEY_GENERAL_BASE_MATH_LIB, BaseMathLibType.getDefaultValue().toString())));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        try {
          pPrefs.setTinaRandomNumberGenerator(RandomGeneratorType.valueOf(getProperty(props, Prefs.KEY_TINA_RANDOM_GENERATOR, RandomGeneratorType.getDefaultValue().toString())));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        pPrefs.setTinaRandomBatchSize(getIntProperty(props, Prefs.KEY_TINA_RANDOMBATCH_SIZE, pPrefs.getTinaRandomBatchSize()));
        pPrefs.setTinaRandomMovieBatchSize(getIntProperty(props, Prefs.KEY_TINA_RANDOMMOVIEBATCH_SIZE, pPrefs.getTinaRandomMovieBatchSize()));
        pPrefs.setTinaRandomBatchBGColorRed(getIntProperty(props, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_RED, pPrefs.getTinaRandomBatchBGColorRed()));
        pPrefs.setTinaRandomBatchBGColorGreen(getIntProperty(props, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_GREEN, pPrefs.getTinaRandomBatchBGColorGreen()));
        pPrefs.setTinaRandomBatchBGColorBlue(getIntProperty(props, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_BLUE, pPrefs.getTinaRandomBatchBGColorBlue()));
        try {
          pPrefs.setTinaRandomBatchRefreshType(RandomBatchRefreshType.valueOf(getProperty(props, Prefs.KEY_TINA_RANDOMBATCH_REFRESH_TYPE, RandomBatchRefreshType.getDefaultValue().toString())));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        // resolution profiles
        {
          int count = getIntProperty(props, Prefs.KEY_TINA_PROFILE_RESOLUTION_COUNT, 0);
          for (int i = 0; i < count; i++) {
            try {
              ResolutionProfile profile = new ResolutionProfile();
              profile.setDefaultProfile(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_RESOLUTION_DEFAULT_PROFILE + "." + i, false));
              profile.setWidth(getIntProperty(props, Prefs.KEY_TINA_PROFILE_RESOLUTION_WIDTH + "." + i, 0));
              profile.setHeight(getIntProperty(props, Prefs.KEY_TINA_PROFILE_RESOLUTION_HEIGHT + "." + i, 0));
              pPrefs.getResolutionProfiles().add(profile);
            }
            catch (Throwable ex) {
              ex.printStackTrace();
            }
          }
          Collections.sort(pPrefs.getResolutionProfiles(), new ResolutionProfileComparator());
        }
        // quality profiles
        {
          int count = getIntProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_COUNT, 0);
          for (int i = 0; i < count; i++) {
            try {
              QualityProfile profile = new QualityProfile();
              profile.setDefaultProfile(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_DEFAULT_PROFILE + "." + i, false));
              profile.setQuality(getIntProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_QUALITY + "." + i, 1));
              profile.setWithHDR(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_WITH_HDR + "." + i, false));
              profile.setWithHDRIntensityMap(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_WITH_HDR_INTENSITY_MAP + "." + i, false));
              profile.setCaption(getProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_CAPTION + "." + i, ""));
              pPrefs.getQualityProfiles().add(profile);
            }
            catch (Throwable ex) {
              ex.printStackTrace();
            }
          }
          Collections.sort(pPrefs.getQualityProfiles(), new QualityProfileComparator());
        }
        // window prefs
        {
          int count = getIntProperty(props, WindowPrefs.KEY_WINDOW_COUNT, 0);
          for (int i = 0; i < count; i++) {
            try {
              String name = getProperty(props, WindowPrefs.KEY_NAME + "." + i, "");
              WindowPrefs windowPrefs = pPrefs.getWindowPrefs(name);
              if (windowPrefs != null) {
                windowPrefs.setLeft(getIntProperty(props, WindowPrefs.KEY_LEFT + "." + i, 0));
                windowPrefs.setTop(getIntProperty(props, WindowPrefs.KEY_TOP + "." + i, 0));
                windowPrefs.setWidth(getIntProperty(props, WindowPrefs.KEY_WIDTH + "." + i, 0));
                windowPrefs.setHeight(getIntProperty(props, WindowPrefs.KEY_HEIGHT + "." + i, 0));
                windowPrefs.setMaximized(getBooleanProperty(props, WindowPrefs.KEY_MAXIMIZED + "." + i, false));
              }
            }
            catch (Throwable ex) {
              ex.printStackTrace();
            }
          }
        }
        //
        pPrefs.setSunflowScenePath(getProperty(props, Prefs.KEY_SUNFLOW_PATH_SCENES, pPrefs.getSunflowScenePath()));
        //
        setupDefaultProfiles(pPrefs);
      }
      finally {
        inputStream.close();
      }
    }
  }

  private void setupDefaultProfiles(Prefs pPrefs) {
    if (pPrefs.getResolutionProfiles().size() == 0) {
      pPrefs.getResolutionProfiles().add(new ResolutionProfile(false, 640, 480));
      pPrefs.getResolutionProfiles().add(new ResolutionProfile(true, 800, 600));
      pPrefs.getResolutionProfiles().add(new ResolutionProfile(false, 1024, 768));
      pPrefs.getResolutionProfiles().add(new ResolutionProfile(false, 1680, 1050));
      pPrefs.getResolutionProfiles().add(new ResolutionProfile(false, 1920, 1080));
    }
    if (pPrefs.getQualityProfiles().size() == 0) {
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "Very low quality", 200, false, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "Low quality", 500, false, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "Medium quality", 800, false, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(true, "High quality", 1000, true, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "Very high quality", 2000, true, false));
    }
  }

}
