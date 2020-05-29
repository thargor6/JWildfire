/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.jwildfire.base.mathlib.BaseMathLibType;
import org.jwildfire.create.tina.base.raster.RasterCreator;
import org.jwildfire.create.tina.random.RandomGeneratorType;
import org.jwildfire.create.tina.render.filter.FilterKernelType;
import org.jwildfire.create.tina.swing.EditorDoubleClickActionType;
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

  private List<String> getListProperty(Properties pProperties, String pKey, List<String> pDefaultValue) {
    String valStr = pProperties.getProperty(pKey, "").trim();
    List<String> res = new ArrayList<>();
    if (!valStr.isEmpty()) {
      StringTokenizer tokenizer = new StringTokenizer(valStr, ",");
      while (tokenizer.hasMoreElements()) {
        res.add(decodeListEntry((String) tokenizer.nextElement()).trim());
      }
    }
    return res;
  }

  private String decodeListEntry(String entry) {
    return entry.replace("\\\\", "\\").replace("\\,", ",");
  }

  private Color getColorProperty(Properties pProperties, String pKey, Color pDefaultValue) {
    String res = pProperties.getProperty(pKey, "").trim();
    if (res.length() > 0) {
      try {
        int p1 = res.lastIndexOf("[");
        int p2 = res.lastIndexOf("]");
        if (p1 >= 0 && p2 > p1) {
          String rgb = res.substring(p1 + 1, p2);
          StringTokenizer tokenizer = new StringTokenizer(rgb, ",");
          int r = Integer.parseInt(((String) tokenizer.nextElement()).trim());
          int g = Integer.parseInt(((String) tokenizer.nextElement()).trim());
          int b = Integer.parseInt(((String) tokenizer.nextElement()).trim());
          return new Color(r, g, b);
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
        return pDefaultValue;
      }
    }
    return pDefaultValue;
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
        pPrefs.setThumbnailPath(getProperty(props, Prefs.KEY_GENERAL_PATH_THUMBNAILS, pPrefs.getThumbnailPath()));
        pPrefs.setMovieFlamesPath(getProperty(props, Prefs.KEY_TINA_PATH_MOVIEFLAMES, pPrefs.getMovieFlamesPath()));
        pPrefs.setSoundFilePath(getProperty(props, Prefs.KEY_GENERAL_PATH_SOUND_FILES, pPrefs.getSoundFilePath()));
        pPrefs.setTinaAdvancedCodeEditor(getBooleanProperty(props, Prefs.KEY_TINA_ADVANCED_CODE_EDITOR, pPrefs.isTinaAdvancedCodeEditor()));
        pPrefs.setTinaAdvancedCodeEditorColorFix(getBooleanProperty(props, Prefs.KEY_TINA_ADVANCED_CODE_EDITOR_COLOR_FIX, pPrefs.isTinaAdvancedCodeEditorColorFix()));
        pPrefs.setTinaAdvancedCodeEditorFontSize(getIntProperty(props, Prefs.KEY_TINA_ADVANCED_CODE_EDITOR_FONT_SIZE, pPrefs.getTinaAdvancedCodeEditorFontSize()));
        pPrefs.setShowTipsAtStartup(getBooleanProperty(props, Prefs.KEY_GENERAL_SHOW_TIPS_AT_STARTUP, pPrefs.isShowTipsAtStartup()));
        pPrefs.setLastTip(getIntProperty(props, Prefs.KEY_GENERAL_LAST_TIP, pPrefs.getLastTip()));
        pPrefs.setTinaAssociateProfilesWithFlames(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_ASSOCIATE_WITH_FLAMES, pPrefs.isTinaAssociateProfilesWithFlames()));
        pPrefs.setTinaSaveFlamesWhenImageIsSaved(getBooleanProperty(props, Prefs.KEY_TINA_SAVING_STORE_FLAMES_WHEN_SAVING_IMAGE, pPrefs.isTinaSaveFlamesWhenImageIsSaved()));
        pPrefs.setTinaSaveHDRInIR(getBooleanProperty(props, Prefs.KEY_TINA_SAVING_STORE_HDR_IN_IR, pPrefs.isTinaSaveHDRInIR()));
        pPrefs.setTinaAutoloadSavedImagesInIR(getBooleanProperty(props, Prefs.KEY_TINA_AUTOLOAD_IMAGES_IN_IR, pPrefs.isTinaAutoloadSavedImagesInIR()));
        pPrefs.setTinaOptimizedRenderingIR(getBooleanProperty(props, Prefs.KEY_TINA_OPTIMIZED_RENDERING_IR, pPrefs.isTinaOptimizedRenderingIR()));
        //        pPrefs.setTinaUseExperimentalOpenClCode(getBooleanProperty(props, Prefs.KEY_TINA_USE_EXPERIMENTAL_OPENCL_CODE, pPrefs.isTinaUseExperimentalOpenClCode()));
        pPrefs.setTinaDefaultBGTransparency(getBooleanProperty(props, Prefs.KEY_TINA_RENDER_DEFAULT_BG_TRANSPARENCY, pPrefs.isTinaDefaultBGTransparency()));
        pPrefs.setTinaDefaultAntialiasingAmount(getDoubleProperty(props, Prefs.KEY_TINA_RENDER_DEFAULT_ANTIALIASING_AMOUNT, pPrefs.getTinaDefaultAntialiasingAmount()));
        pPrefs.setTinaDefaultAntialiasingRadius(getDoubleProperty(props, Prefs.KEY_TINA_RENDER_DEFAULT_ANTIALIASING_RADIUS, pPrefs.getTinaDefaultAntialiasingRadius()));
        pPrefs.setTinaFlamePath(getProperty(props, Prefs.KEY_TINA_PATH_FLAMES, pPrefs.getTinaFlamePath()));
        pPrefs.setTinaMeshPath(getProperty(props, Prefs.KEY_TINA_PATH_MESHES, pPrefs.getTinaMeshPath()));
        pPrefs.setTinaJWFMoviePath(getProperty(props, Prefs.KEY_TINA_PATH_JWFMOVIES, pPrefs.getTinaJWFMoviePath()));
        pPrefs.setTinaJWFScriptPath(getProperty(props, Prefs.KEY_TINA_PATH_JWFSCRIPTS, pPrefs.getTinaJWFScriptPath()));
        pPrefs.setTinaCustomVariationsPath(getProperty(props, Prefs.KEY_TINA_PATH_CUSTOM_VARIATIONS, pPrefs.getTinaCustomVariationsPath()));
        pPrefs.setTinaDisableSolidFlameRandGens(getBooleanProperty(props, Prefs.KEY_TINA_DISABLE_SOLID_RANDGENS, pPrefs.isTinaDisableSolidFlameRandGens()));
        pPrefs.setTinaGradientPath(getProperty(props, Prefs.KEY_TINA_PATH_GRADIENTS, pPrefs.getTinaGradientPath()));
        pPrefs.setTinaSVGPath(getProperty(props, Prefs.KEY_TINA_PATH_SVG, pPrefs.getTinaSVGPath()));
        pPrefs.setTinaRawMotionDataPath(getProperty(props, Prefs.KEY_TINA_PATH_RAW_MOTION_DATA, pPrefs.getTinaRawMotionDataPath()));
        pPrefs.setTinaRenderMovieFrames(getIntProperty(props, Prefs.KEY_TINA_RENDER_MOVIE_FRAMES, pPrefs.getTinaRenderMovieFrames()));
        pPrefs.setTinaRenderPreviewQuality(getIntProperty(props, Prefs.KEY_TINA_RENDER_PREVIEW_QUALITY, pPrefs.getTinaRenderPreviewQuality()));
        pPrefs.setTinaRenderRealtimeQuality(getDoubleProperty(props, Prefs.KEY_TINA_RENDER_REALTIME_QUALITY, pPrefs.getTinaRenderRealtimeQuality()));
        pPrefs.setTinaRenderAnimPreviewQuality(getDoubleProperty(props, Prefs.KEY_TINA_RENDER_ANIM_PREVIEW_QUALITY, pPrefs.getTinaRenderAnimPreviewQuality()));
        pPrefs.setTinaRenderAnimPreviewSize(getDoubleProperty(props, Prefs.KEY_TINA_RENDER_ANIM_PREVIEW_SIZE, pPrefs.getTinaRenderAnimPreviewSize()));
        pPrefs.setTinaDefaultAnimationControlsEnabled(getBooleanProperty(props, Prefs.KEY_TINA_DEFAULT_ANIM_CONTROLS_ENABLED, pPrefs.isTinaDefaultAnimationControlsEnabled()));
        pPrefs.setTinaPreserveFreeCPUs(getIntProperty(props, Prefs.KEY_TINA_PRESERVE_FREE_CPUS, pPrefs.getTinaPreserveFreeCPUs()));
        pPrefs.setTinaDisableWikimediaCommonsWarning(getBooleanProperty(props, Prefs.KEY_TINA_DISABLE_WIKIMEDIA_COMMONS_WARNING, pPrefs.isTinaDisableWikimediaCommonsWarning()));
        pPrefs.setTinaEditorControlsWithColor(getBooleanProperty(props, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_COLOR, pPrefs.isTinaEditorControlsWithColor()));
        pPrefs.setTinaEditorProgressivePreview(getBooleanProperty(props, Prefs.KEY_TINA_EDITOR_PROGRESSIVE_PREVIEW, pPrefs.isTinaEditorProgressivePreview()));
        pPrefs.setTinaEditorProgressivePreviewMaxRenderTime(getDoubleProperty(props, Prefs.KEY_TINA_EDITOR_PROGRESSIVE_PREVIEW_MAX_RENDER_TIME, pPrefs.getTinaEditorProgressivePreviewMaxRenderTime()));
        pPrefs.setTinaEditorProgressivePreviewMaxRenderQuality(getDoubleProperty(props, Prefs.KEY_TINA_EDITOR_PROGRESSIVE_PREVIEW_MAX_RENDER_QUALITY, pPrefs.getTinaEditorProgressivePreviewMaxRenderQuality()));
        pPrefs.setTinaFontScale(getDoubleProperty(props, Prefs.KEY_TINA_FONTSCALE, pPrefs.getTinaFontScale()));
        pPrefs.setTinaEditorControlsWithAntialiasing(getBooleanProperty(props, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_ANTIALIASING, pPrefs.isTinaEditorControlsWithAntialiasing()));
        pPrefs.setTinaEditorControlsWithShadows(getBooleanProperty(props, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_SHADOWS, pPrefs.isTinaEditorControlsWithShadows()));
        pPrefs.setTinaEditorControlsWithNumbers(getBooleanProperty(props, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_NUMBERS, pPrefs.isTinaEditorControlsWithNumbers()));
        pPrefs.setTinaEditorGridSize(getDoubleProperty(props, Prefs.KEY_TINA_EDITOR_GRID_SIZE, pPrefs.getTinaEditorGridSize()));
        pPrefs.setTinaEditorGuidesCenterPointColor(getColorProperty(props, Prefs.KEY_TINA_EDITOR_GUIDES_COLOR_CENTER_POINT, pPrefs.getTinaEditorGuidesCenterPointColor()));
        pPrefs.setTinaEditorGuidesRuleOfThirdsColor(getColorProperty(props, Prefs.KEY_TINA_EDITOR_GUIDES_COLOR_RULE_OF_THIRDS, pPrefs.getTinaEditorGuidesRuleOfThirdsColor()));
        pPrefs.setTinaEditorGuidesGoldenRatioColor(getColorProperty(props, Prefs.KEY_TINA_EDITOR_GUIDES_COLOR_GOLDEN_RATIO, pPrefs.getTinaEditorGuidesGoldenRatioColor()));
        pPrefs.setTinaEditorGuidesLineWidth(getDoubleProperty(props, Prefs.KEY_TINA_EDITOR_GUIDES_LINE_WIDTH, pPrefs.getTinaEditorGuidesLineWidth()));
        pPrefs.setTinaMacroButtonsVertical(getBooleanProperty(props, Prefs.KEY_TINA_VERTICAL_MACRO_BUTTONS, pPrefs.isTinaMacroButtonsVertical()));
        pPrefs.setTinaRandGenDualityPreferedVariation(getProperty(props, Prefs.KEY_TINA_RANDOMBATCH_DUALITY_PREFERED_VARIATION, pPrefs.getTinaRandGenDualityPreferedVariation()));
        pPrefs.setTinaRandGenDualityPreferedVariationProbability1(getDoubleProperty(props, Prefs.KEY_TINA_RANDOMBATCH_DUALITY_PREFERED_VARIATION_PROBABILITY1, pPrefs.getTinaRandGenDualityPreferedVariationProbability1()));
        pPrefs.setTinaRandGenDualityPreferedVariationProbability2(getDoubleProperty(props, Prefs.KEY_TINA_RANDOMBATCH_DUALITY_PREFERED_VARIATION_PROBABILITY2, pPrefs.getTinaRandGenDualityPreferedVariationProbability2()));

        pPrefs.setTinaDefaultSpatialOversampling(getIntProperty(props, Prefs.KEY_TINA_DEFAULT_SPATIAL_OVERSAMPLING, pPrefs.getTinaDefaultSpatialOversampling()));
        pPrefs.setTinaDefaultFilterVisualisationFlat(getBooleanProperty(props, Prefs.KEY_TINA_DEFAULT_FILTER_VISUALISATION_FLAT, pPrefs.isTinaDefaultFilterVisualisationFlat()));
        pPrefs.setTinaDefaultPostNoiseFilter(getBooleanProperty(props, Prefs.KEY_TINA_DEFAULT_POST_NOISE_FILTER, pPrefs.isTinaDefaultPostNoiseFilter()));
        pPrefs.setTinaDefaultPostNoiseFilterThreshold(getDoubleProperty(props, Prefs.KEY_TINA_DEFAULT_POST_NOISE_FILTER_THRESHOLD, pPrefs.getTinaDefaultPostNoiseFilterThreshold()));
        pPrefs.setTinaDefaultPostOptiXDenoiser(getBooleanProperty(props, Prefs.KEY_TINA_DEFAULT_POST_OPTIX_DENOISER, pPrefs.isTinaDefaultPostOptiXDenoiser()));
        pPrefs.setTinaDefaultPostOptiXDenoiserBlend(getDoubleProperty(props, Prefs.KEY_TINA_DEFAULT_POST_OPTIX_DENOISER_BLEND, pPrefs.getTinaDefaultPostOptiXDenoiserBlend()));

        pPrefs.setTinaDefaultForegroundOpacity(getDoubleProperty(props, Prefs.KEY_TINA_DEFAULT_FOREGROUND_OPACITY, pPrefs.getTinaDefaultForegroundOpacity()));
        try {
          pPrefs.setTinaDefaultSpatialFilterKernel(FilterKernelType.valueOf(getProperty(props, Prefs.KEY_TINA_DEFAULT_FILTER_KERNEL, pPrefs.getTinaDefaultSpatialFilterKernel().toString())));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        pPrefs.setTinaDefaultSpatialFilterRadius(getDoubleProperty(props, Prefs.KEY_TINA_DEFAULT_FILTER_RADIUS, pPrefs.getTinaDefaultSpatialFilterRadius()));

        pPrefs.setTinaMutaGenMutationTypesUser1(getProperty(props, Prefs.KEY_TINA_MUTAGEN_MUTATIONTYPES_USER1, pPrefs.getTinaMutaGenMutationTypesUser1()));
        pPrefs.setTinaMutaGenMutationTypesUser2(getProperty(props, Prefs.KEY_TINA_MUTAGEN_MUTATIONTYPES_USER2, pPrefs.getTinaMutaGenMutationTypesUser2()));
        pPrefs.setTinaMutaGenMutationTypesUser3(getProperty(props, Prefs.KEY_TINA_MUTAGEN_MUTATIONTYPES_USER3, pPrefs.getTinaMutaGenMutationTypesUser3()));
        pPrefs.setTinaMutaGenMutationTypeHoriz1(getProperty(props, Prefs.KEY_TINA_MUTAGEN_MUTATIONTYPE_HORIZ1, pPrefs.getTinaMutaGenMutationTypeHoriz1()));
        pPrefs.setTinaMutaGenMutationTypeHoriz2(getProperty(props, Prefs.KEY_TINA_MUTAGEN_MUTATIONTYPE_HORIZ2, pPrefs.getTinaMutaGenMutationTypeHoriz2()));
        pPrefs.setTinaMutaGenMutationTypeVert1(getProperty(props, Prefs.KEY_TINA_MUTAGEN_MUTATIONTYPE_VERT1, pPrefs.getTinaMutaGenMutationTypeVert1()));
        pPrefs.setTinaMutaGenMutationTypeVert2(getProperty(props, Prefs.KEY_TINA_MUTAGEN_MUTATIONTYPE_VERT2, pPrefs.getTinaMutaGenMutationTypeVert2()));
        pPrefs.setTinaFreeCacheInBatchRenderer(getBooleanProperty(props, Prefs.KEY_TINA_FREE_CACHE_IN_BATCH_RENDERER, pPrefs.isTinaFreeCacheInBatchRenderer()));

        pPrefs.setIflamesFlameLibraryPath(getProperty(props, Prefs.KEY_IFLAMES_LIBRARY_PATH_FLAMES, pPrefs.getIflamesFlameLibraryPath()));
        pPrefs.setIflamesImageLibraryPath(getProperty(props, Prefs.KEY_IFLAMES_LIBRARY_PATH_IMAGES, pPrefs.getIflamesImageLibraryPath()));
        pPrefs.setIflamesLoadLibraryAtStartup(getBooleanProperty(props, Prefs.KEY_IFLAMES_LOAD_LIBRARY_AT_STARTUP, pPrefs.isIflamesLoadLibraryAtStartup()));

        try {
          FlamePanelControlStyle style = FlamePanelControlStyle.valueOf(getProperty(props, Prefs.KEY_TINA_EDITOR_CONTROLS_STYLE, pPrefs.getTinaEditorControlsStyle().toString()));
          pPrefs.setTinaEditorControlsStyle(style);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }

        try {
          EditorDoubleClickActionType action = EditorDoubleClickActionType.valueOf(getProperty(props, Prefs.KEY_TINA_EDITOR_DEFAULT_DOUBLECLICK_ACTION, pPrefs.getTinaEditorDoubleClickAction().toString()));
          pPrefs.setTinaEditorDoubleClickAction(action);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        pPrefs.setTinaDefaultFadeToWhiteLevel(getDoubleProperty(props, Prefs.KEY_TINA_DEFAULT_FADE_TO_WHITE_LEVEL, pPrefs.getTinaDefaultFadeToWhiteLevel()));
        pPrefs.setTinaDefaultFPS(getIntProperty(props, Prefs.KEY_TINA_DEFAULT_FPS, pPrefs.getTinaDefaultFPS()));
        pPrefs.setTinaDefaultFrameCount(getIntProperty(props, Prefs.KEY_TINA_DEFAULT_FRAME_COUNT, pPrefs.getTinaDefaultFrameCount()));

        pPrefs.setTinaRandGenColorMapImagePath(getProperty(props, Prefs.KEY_TINA_COLORMAP_RANDGEN_IMAGE_PATH, pPrefs.getTinaRandGenColorMapImagePath()));

        pPrefs.setTinaFACLRenderPath(getProperty(props, Prefs.KEY_TINA_FACLRENDER_PATH, pPrefs.getTinaFACLRenderPath()));
        pPrefs.setTinaFACLRenderOptions(getProperty(props, Prefs.KEY_TINA_FACLRENDER_OPTS, pPrefs.getTinaFACLRenderOptions()));
        pPrefs.setTinaDefaultExpandNonlinearParams(getBooleanProperty(props, Prefs.KEY_TINA_DEFAULT_EXPAND_NONLINEAR_PARAMS, pPrefs.isTinaDefaultExpandNonlinearParams()));

        try {
          RasterCreator rasterPointPrecision = RasterCreator.valueOf(getProperty(props, Prefs.KEY_TINA_RASTER_TYPE, RasterCreator.getDefaultValue().toString()));
          pPrefs.setTinaRasterType(rasterPointPrecision);
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
        pPrefs.setTinaInitialRandomBatchSize(getIntProperty(props, Prefs.KEY_TINA_INITIAL_RANDOMBATCH_SIZE, pPrefs.getTinaInitialRandomBatchSize()));
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
          pPrefs.getResolutionProfiles().clear();
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
          pPrefs.getQualityProfiles().clear();
          for (int i = 0; i < count; i++) {
            try {
              QualityProfile profile = new QualityProfile();
              profile.setDefaultProfile(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_DEFAULT_PROFILE + "." + i, false));
              profile.setQuality(getIntProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_QUALITY + "." + i, 1));
              profile.setWithHDR(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_WITH_HDR + "." + i, false));
              profile.setWithZBuffer(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_WITH_ZBUFFER + "." + i, false));
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
                windowPrefs.setVisible(getBooleanProperty(props, WindowPrefs.KEY_VISIBLE + "." + i, false));
              }
            }
            catch (Throwable ex) {
              ex.printStackTrace();
            }
          }
        }
        // macro buttons
        pPrefs.setCreateTinaDefaultMacroButtons(getBooleanProperty(props, Prefs.KEY_TINA_CREATE_DEFAULT_MACRO_BUTTONS, pPrefs.isCreateTinaDefaultMacroButtons()));
        pPrefs.setTinaMacroToolbarWidth(getIntProperty(props, Prefs.KEY_TINA_MACRO_TOOLBAR_WIDTH, pPrefs.getTinaMacroToolbarWidth()));
        pPrefs.setTinaMacroToolbarHeight(getIntProperty(props, Prefs.KEY_TINA_MACRO_TOOLBAR_HEIGHT, pPrefs.getTinaMacroToolbarHeight()));
        {
          int count = getIntProperty(props, MacroButton.KEY_MACRO_BUTTON_COUNT, 0);
          pPrefs.getTinaMacroButtons().clear();
          for (int i = 0; i < count; i++) {
            try {
              MacroButton macroButton = new MacroButton();
              macroButton.setCaption(getProperty(props, MacroButton.KEY_MACRO_BUTTON_CAPTION + "." + i, ""));
              macroButton.setHint(getProperty(props, MacroButton.KEY_MACRO_BUTTON_HINT + "." + i, ""));
              macroButton.setImage(getProperty(props, MacroButton.KEY_MACRO_BUTTON_IMAGE + "." + i, ""));
              macroButton.setMacro(getProperty(props, MacroButton.KEY_MACRO_BUTTON_MACRO + "." + i, ""));
              macroButton.setInternal(getBooleanProperty(props, MacroButton.KEY_MACRO_BUTTON_INTERNAL + "." + i, false));
              pPrefs.getTinaMacroButtons().add(macroButton);
            }
            catch (Throwable ex) {
              ex.printStackTrace();
            }
          }
        }
        if (pPrefs.isCreateTinaDefaultMacroButtons()) {
          pPrefs.getTinaMacroButtons().clear();
          setupDefaultTinaMacroButtons(pPrefs);
        }
        //
        pPrefs.setSunflowScenePath(getProperty(props, Prefs.KEY_SUNFLOW_PATH_SCENES, pPrefs.getSunflowScenePath()));
        pPrefs.setTinaExcludedVariations(getListProperty(props, Prefs.KEY_TINA_EXCLUDED_VARIATIONS, pPrefs.getTinaExcludedVariations()));
        //

      }
      finally {
        inputStream.close();
      }
    }
    else {
      setupDefaultProfiles(pPrefs);
      setupDefaultTinaMacroButtons(pPrefs);
    }
  }

  private void addMacroButton(Prefs prefs, String caption, String hint, String script) {
    MacroButton button = new MacroButton();
    button.setCaption(caption);
    button.setHint(hint);
    button.setInternal(true);
    button.setMacro(script);
    prefs.getTinaMacroButtons().add(button);
  }

  private void setupDefaultTinaMacroButtons(Prefs pPrefs) {
    if (pPrefs.getTinaMacroButtons().size() == 0) {
      addMacroButton(pPrefs, "EschFlx", "Escher Flux", "scripts/Escher Flux.jwfscript");
      addMacroButton(pPrefs, "MobDrgn", "Mobius Dragon", "scripts/Mobius Dragon.jwfscript");
      addMacroButton(pPrefs, "Heart", "Wrap into Heart", "scripts/Wrap into Heart.jwfscript");
      addMacroButton(pPrefs, "SubFlame", "Wrap into SubFlame", "scripts/Wrap into SubFlame.jwfscript");
      addMacroButton(pPrefs, "RndFX", "Add Random Final FX by MH and MO", "scripts/Add Random Final FX by MH and MO.jwfscript");
    }
  }

  private void setupDefaultProfiles(Prefs pPrefs) {
    if (pPrefs.getResolutionProfiles().size() == 0) {
      pPrefs.getResolutionProfiles().add(new ResolutionProfile(false, 640, 480));
      pPrefs.getResolutionProfiles().add(new ResolutionProfile(true, 800, 600));
      pPrefs.getResolutionProfiles().add(new ResolutionProfile(false, 1024, 768));
      pPrefs.getResolutionProfiles().add(new ResolutionProfile(false, 1680, 1050));
      pPrefs.getResolutionProfiles().add(new ResolutionProfile(false, 1440, 900));
      pPrefs.getResolutionProfiles().add(new ResolutionProfile(false, 1920, 1080));
    }
    if (pPrefs.getQualityProfiles().size() == 0) {
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "Very low quality", 100, false, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "Low quality", 200, false, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "Medium quality", 300, true, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(true, "High quality", 500, false, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "Very high quality", 1000, true, false));
    }
  }

}
