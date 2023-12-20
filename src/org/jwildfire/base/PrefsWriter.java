/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2023 Andreas Maschke

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PrefsWriter {

  public void writePrefs(Prefs pPrefs) throws Exception {
    StringBuilder sb = new StringBuilder();
    pPrefs.setCreateTinaDefaultMacroButtons(false);

    addValue(sb, Prefs.KEY_GENERAL_DEVELOPMENT_MODE, pPrefs.isDevelopmentMode());
    addValue(sb, Prefs.KEY_GENERAL_BASE_MATH_LIB, pPrefs.getBaseMathLibType().toString());
    if (pPrefs.getLookAndFeelType() != null) {
      addValue(sb, Prefs.KEY_GENERAL_LOOK_AND_FEEL, pPrefs.getLookAndFeelType().toString());
      addValue(sb, Prefs.KEY_GENERAL_LOOK_AND_FEEL_THEME, pPrefs.getLookAndFeelTheme());
    }
    addValue(sb, Prefs.KEY_GENERAL_SHOW_TIPS_AT_STARTUP, pPrefs.isShowTipsAtStartup());
    addValue(sb, Prefs.KEY_GENERAL_SKIP_PATH_CHECK_AT_STARTUP, pPrefs.isSkipPathCheckAtStartup());
    addValue(sb, Prefs.KEY_GENERAL_SPECIAL_MAC_OS_FILE_HANDLING, pPrefs.isSpecialMacOsFileHandling());
    addValue(sb, Prefs.KEY_GENERAL_MAC_OS_SEC_BOOKMARKS, pPrefs.isMacOsUseSecurityScopedBookmarks());
    addValue(sb, Prefs.KEY_GENERAL_LAST_TIP, pPrefs.getLastTip());
    addValue(sb, Prefs.KEY_GENERAL_PATH_IMAGES, pPrefs.getImagePath());
    addValue(sb, Prefs.KEY_GENERAL_PATH_THUMBNAILS, pPrefs.getThumbnailPath());
    addValue(sb, Prefs.KEY_TINA_PATH_MOVIEFLAMES, pPrefs.getMovieFlamesPath());
    addValue(sb, Prefs.KEY_GENERAL_PATH_SOUND_FILES, pPrefs.getSoundFilePath());
    addValue(sb, Prefs.KEY_TINA_RENDER_DEFAULT_BG_TRANSPARENCY, pPrefs.isTinaDefaultBGTransparency());
    addValue(sb, Prefs.KEY_TINA_RENDER_DEFAULT_ANTIALIASING_AMOUNT, pPrefs.getTinaDefaultAntialiasingAmount());
    addValue(sb, Prefs.KEY_TINA_RENDER_DEFAULT_ANTIALIASING_RADIUS, pPrefs.getTinaDefaultAntialiasingRadius());
    addValue(sb, Prefs.KEY_TINA_PROFILE_ASSOCIATE_WITH_FLAMES, pPrefs.isTinaAssociateProfilesWithFlames());
    addValue(sb, Prefs.KEY_TINA_SAVING_STORE_FLAMES_WHEN_SAVING_IMAGE, pPrefs.isTinaSaveFlamesWhenImageIsSaved());
    addValue(sb, Prefs.KEY_TINA_SAVING_STORE_HDR_IN_IR, pPrefs.isTinaSaveHDRInIR());
    addValue(sb, Prefs.KEY_TINA_AUTOLOAD_IMAGES_IN_IR, pPrefs.isTinaAutoloadSavedImagesInIR());
    addValue(sb, Prefs.KEY_TINA_OPTIMIZED_RENDERING_IR, pPrefs.isTinaOptimizedRenderingIR());
    addValue(sb, Prefs.KEY_TINA_KEEP_TEMP_MP4_FRAMES, pPrefs.isTinaKeepTempMp4Frames());
    //    addValue(sb, Prefs.KEY_TINA_USE_EXPERIMENTAL_OPENCL_CODE, pPrefs.isTinaUseExperimentalOpenClCode());
    addValue(sb, Prefs.KEY_TINA_PATH_MESHES, pPrefs.getTinaMeshPath());
    addValue(sb, Prefs.KEY_TINA_PATH_FLAMES, pPrefs.getTinaFlamePath());
    addValue(sb, Prefs.KEY_TINA_PATH_JWFMOVIES, pPrefs.getTinaJWFMoviePath());
    addValue(sb, Prefs.KEY_TINA_PATH_JWFSCRIPTS, pPrefs.getTinaJWFScriptPath());
    addValue(sb, Prefs.KEY_TINA_PATH_CUSTOM_VARIATIONS, pPrefs.getTinaCustomVariationsPath());
    addValue(sb, Prefs.KEY_TINA_PATH_GRADIENTS, pPrefs.getTinaGradientPath());
    addValue(sb, Prefs.KEY_TINA_PATH_SVG, pPrefs.getTinaSVGPath());
    addValue(sb, Prefs.KEY_TINA_PATH_RAW_MOTION_DATA, pPrefs.getTinaRawMotionDataPath());
    addValue(sb, Prefs.KEY_TINA_RENDER_MOVIE_FRAMES, pPrefs.getTinaRenderMovieFrames());
    addValue(sb, Prefs.KEY_TINA_RENDER_PREVIEW_QUALITY, pPrefs.getTinaRenderPreviewQuality());
    addValue(sb, Prefs.KEY_TINA_RENDER_REALTIME_QUALITY, pPrefs.getTinaRenderRealtimeQuality());
    addValue(sb, Prefs.KEY_TINA_RENDER_ANIM_PREVIEW_QUALITY, pPrefs.getTinaRenderAnimPreviewQuality());
    addValue(sb, Prefs.KEY_TINA_RENDER_ANIM_PREVIEW_SIZE, pPrefs.getTinaRenderAnimPreviewSize());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_ANIM_CONTROLS_ENABLED, pPrefs.isTinaDefaultAnimationControlsEnabled());
    addValue(sb, Prefs.KEY_TINA_RANDOM_GENERATOR, pPrefs.getTinaRandomNumberGenerator().toString());
    addValue(sb, Prefs.KEY_TINA_INITIAL_RANDOMBATCH_SIZE, pPrefs.getTinaInitialRandomBatchSize());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_SIZE, pPrefs.getTinaRandomBatchSize());
    addValue(sb, Prefs.KEY_TINA_RANDOMMOVIEBATCH_SIZE, pPrefs.getTinaRandomMovieBatchSize());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_RED, pPrefs.getTinaRandomBatchBGColorRed());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_GREEN, pPrefs.getTinaRandomBatchBGColorGreen());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_BLUE, pPrefs.getTinaRandomBatchBGColorBlue());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_REFRESH_TYPE, pPrefs.getTinaRandomBatchRefreshType().toString());
    addValue(sb, Prefs.KEY_TINA_RASTER_TYPE, pPrefs.getTinaRasterType().toString());
    addValue(sb, Prefs.KEY_SUNFLOW_PATH_SCENES, pPrefs.getSunflowScenePath());
    addValue(sb, Prefs.KEY_TINA_PRESERVE_FREE_CPUS, pPrefs.getTinaPreserveFreeCPUs());
    addValue(sb, Prefs.KEY_TINA_DISABLE_WIKIMEDIA_COMMONS_WARNING, pPrefs.isTinaDisableWikimediaCommonsWarning());
    addValue(sb, Prefs.KEY_TINA_EDITOR_PROGRESSIVE_PREVIEW, pPrefs.isTinaEditorProgressivePreview());
    addValue(sb, Prefs.KEY_TINA_EDITOR_PROGRESSIVE_PREVIEW_MAX_RENDER_TIME, pPrefs.getTinaEditorProgressivePreviewMaxRenderTime());
    addValue(sb, Prefs.KEY_TINA_EDITOR_PROGRESSIVE_PREVIEW_MAX_RENDER_QUALITY, pPrefs.getTinaEditorProgressivePreviewMaxRenderQuality());
    addValue(sb, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_COLOR, pPrefs.isTinaEditorControlsWithColor());
    addValue(sb, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_ANTIALIASING, pPrefs.isTinaEditorControlsWithAntialiasing());
    addValue(sb, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_SHADOWS, pPrefs.isTinaEditorControlsWithShadows());
    addValue(sb, Prefs.KEY_TINA_EDITOR_CONTROLS_WITH_NUMBERS, pPrefs.isTinaEditorControlsWithNumbers());
    addValue(sb, Prefs.KEY_TINA_EDITOR_CONTROLS_STYLE, pPrefs.getTinaEditorControlsStyle().toString());

    addValue(sb, Prefs.KEY_TINA_EDITOR_GRID_SIZE, pPrefs.getTinaEditorGridSize());
    addValue(sb, Prefs.KEY_TINA_COLORMAP_RANDGEN_IMAGE_PATH, pPrefs.getTinaRandGenColorMapImagePath());
    addValue(sb, Prefs.KEY_TINA_EDITOR_GUIDES_COLOR_CENTER_POINT, pPrefs.getTinaEditorGuidesCenterPointColor());
    addValue(sb, Prefs.KEY_TINA_EDITOR_GUIDES_COLOR_RULE_OF_THIRDS, pPrefs.getTinaEditorGuidesRuleOfThirdsColor());
    addValue(sb, Prefs.KEY_TINA_EDITOR_GUIDES_COLOR_GOLDEN_RATIO, pPrefs.getTinaEditorGuidesGoldenRatioColor());
    addValue(sb, Prefs.KEY_TINA_EDITOR_GUIDES_LINE_WIDTH, pPrefs.getTinaEditorGuidesLineWidth());
    addValue(sb, Prefs.KEY_TINA_VERTICAL_MACRO_BUTTONS, pPrefs.isTinaMacroButtonsVertical());
    addValue(sb, Prefs.KEY_TINA_FREE_CACHE_IN_BATCH_RENDERER, pPrefs.isTinaFreeCacheInBatchRenderer());

    addValue(sb, Prefs.KEY_TINA_FONTSCALE, pPrefs.getTinaFontScale());

    addValue(sb, Prefs.KEY_TINA_MUTAGEN_MUTATIONTYPES_USER1, pPrefs.getTinaMutaGenMutationTypesUser1());
    addValue(sb, Prefs.KEY_TINA_MUTAGEN_MUTATIONTYPES_USER2, pPrefs.getTinaMutaGenMutationTypesUser2());
    addValue(sb, Prefs.KEY_TINA_MUTAGEN_MUTATIONTYPES_USER3, pPrefs.getTinaMutaGenMutationTypesUser3());

    addValue(sb, Prefs.KEY_TINA_MUTAGEN_MUTATION_TREND, pPrefs.getTinaMutaGenMutationTrend());

    addValue(sb, Prefs.KEY_TINA_QUICK_MUTATIONS_BATCH_SIZE, pPrefs.getTinaQuickMutationBatchSize());
    addValue(sb, Prefs.KEY_TINA_QUICK_MUTATIONS_DEFAULT_MUTATION_TYPE, pPrefs.getTinaQuickMutationDefaultMutationType());

    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_DUALITY_PREFERED_VARIATION, pPrefs.getTinaRandGenDualityPreferedVariation());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_DUALITY_PREFERED_VARIATION_PROBABILITY1, pPrefs.getTinaRandGenDualityPreferedVariationProbability1());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_DUALITY_PREFERED_VARIATION_PROBABILITY2, pPrefs.getTinaRandGenDualityPreferedVariationProbability2());

    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_RUN_RANDOM_SCRIPT_INCLUDED_SCRIPTS, pPrefs.getTinaRandGenRunRandomScriptIncludedScripts());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_RUN_RANDOM_SCRIPT_EXCLUDED_SCRIPTS, pPrefs.getTinaRandGenRunRandomScriptExcludedScripts());

    addValue(sb, Prefs.KEY_IFLAMES_LIBRARY_PATH_FLAMES, pPrefs.getIflamesFlameLibraryPath());
    addValue(sb, Prefs.KEY_IFLAMES_LIBRARY_PATH_IMAGES, pPrefs.getIflamesImageLibraryPath());
    addValue(sb, Prefs.KEY_IFLAMES_LOAD_LIBRARY_AT_STARTUP, pPrefs.isIflamesLoadLibraryAtStartup());

    addValue(sb, Prefs.KEY_TINA_DEFAULT_SPATIAL_OVERSAMPLING, pPrefs.getTinaDefaultSpatialOversampling());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_FILTER_VISUALISATION_FLAT, pPrefs.isTinaDefaultFilterVisualisationFlat());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_FILTER_KERNEL, pPrefs.getTinaDefaultSpatialFilterKernel().toString());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_FILTER_RADIUS, pPrefs.getTinaDefaultSpatialFilterRadius());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_AI_POST_DENOISER, pPrefs.getTinaDefaultAIPostDenoiser().toString());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_POST_OPTIX_DENOISER_BLEND, pPrefs.getTinaDefaultPostOptiXDenoiserBlend());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_FOREGROUND_OPACITY, pPrefs.getTinaDefaultForegroundOpacity());
    addValue(sb, Prefs.KEY_TINA_DISABLE_SOLID_RANDGENS, pPrefs.isTinaDisableSolidFlameRandGens());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_EXPAND_NONLINEAR_PARAMS, pPrefs.isTinaDefaultExpandNonlinearParams());
    addValue(sb, Prefs.KEY_TINA_ENABLE_LEAP_MOTION_TAB, pPrefs.isTinaEnableLeapMotionTab());
    addValue(sb, Prefs.KEY_TINA_GPU_MODE_DEFAULT_ENABLED, pPrefs.isTinaGpuModeDefaultEnabled());
    addValue(sb, Prefs.KEY_TINA_LEGACY_REALTIME_PREVIEW, pPrefs.isTinaLegacyRealtimePreview());
    addValue(sb, Prefs.KEY_TINA_FARENDER_OPTS, pPrefs.getTinaFARenderOptions());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_NEW_FLAME_TITLE, pPrefs.getTinaDefaultNewFlameTitle());
    addValue(sb, Prefs.KEY_TINA_EDITOR_DEFAULT_DOUBLECLICK_ACTION, pPrefs.getTinaEditorDoubleClickAction().toString());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_FADE_TO_WHITE_LEVEL, pPrefs.getTinaDefaultFadeToWhiteLevel());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_FPS, pPrefs.getTinaDefaultFPS());
    addValue(sb, Prefs.KEY_TINA_DEFAULT_FRAME_COUNT, pPrefs.getTinaDefaultFrameCount());
    // resolution profiles
    addValue(sb, Prefs.KEY_TINA_PROFILE_RESOLUTION_COUNT, pPrefs.getResolutionProfiles().size());
    for (int i = 0; i < pPrefs.getResolutionProfiles().size(); i++) {
      ResolutionProfile profile = pPrefs.getResolutionProfiles().get(i);
      addValue(sb, Prefs.KEY_TINA_PROFILE_RESOLUTION_DEFAULT_PROFILE + "." + i, profile.isDefaultProfile());
      addValue(sb, Prefs.KEY_TINA_PROFILE_RESOLUTION_WIDTH + "." + i, profile.getWidth());
      addValue(sb, Prefs.KEY_TINA_PROFILE_RESOLUTION_HEIGHT + "." + i, profile.getHeight());
    }
    // quality profiles
    addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_COUNT, pPrefs.getQualityProfiles().size());
    for (int i = 0; i < pPrefs.getQualityProfiles().size(); i++) {
      QualityProfile profile = pPrefs.getQualityProfiles().get(i);
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_DEFAULT_PROFILE + "." + i, profile.isDefaultProfile());
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_CAPTION + "." + i, profile.getCaption());
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_QUALITY + "." + i, profile.getQuality());
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_WITH_HDR + "." + i, profile.isWithHDR());
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_WITH_ZBUFFER + "." + i, profile.isWithZBuffer());
    }
    // window prefs
    addValue(sb, WindowPrefs.KEY_WINDOW_COUNT, pPrefs.getWindowPrefs().size());
    for (int i = 0; i < pPrefs.getWindowPrefs().size(); i++) {
      WindowPrefs prefs = pPrefs.getWindowPrefs().get(i);
      addValue(sb, WindowPrefs.KEY_NAME + "." + i, prefs.getName());
      addValue(sb, WindowPrefs.KEY_LEFT + "." + i, prefs.getLeft());
      addValue(sb, WindowPrefs.KEY_TOP + "." + i, prefs.getTop());
      addValue(sb, WindowPrefs.KEY_WIDTH + "." + i, prefs.getWidth());
      addValue(sb, WindowPrefs.KEY_HEIGHT + "." + i, prefs.getHeight());
      addValue(sb, WindowPrefs.KEY_MAXIMIZED + "." + i, prefs.isMaximized());
      addValue(sb, WindowPrefs.KEY_VISIBLE + "." + i, prefs.isVisible());
    }
    // Variation Profiles
    addValue(sb, VariationProfile.KEY_COUNT, pPrefs.getVariationProfiles().size());
    for (int i = 0; i < pPrefs.getVariationProfiles().size(); i++) {
      VariationProfile profile = pPrefs.getVariationProfiles().get(i);
      addValue(sb, VariationProfile.KEY_NAME + "." + i, profile.getName());
      addValue(sb, VariationProfile.KEY_DEFAULT + "." + i, profile.isDefaultProfile());
      addValue(sb, VariationProfile.KEY_PROFILE_TYPE + "." + i, profile.getVariationProfileType().toString());
      addValue(sb, VariationProfile.KEY_VARIATIONS + "." + i, new ArrayList<>(profile.getVariations()));
      addValue(sb, VariationProfile.KEY_VARIATION_TYPES + "." + i, profile.getVariationTypes().stream().map(vt -> vt.toString()).collect(Collectors.toList()));
    }
    // macro buttons
    addValue(sb, Prefs.KEY_TINA_CREATE_DEFAULT_MACRO_BUTTONS, pPrefs.isCreateTinaDefaultMacroButtons());
    addValue(sb, Prefs.KEY_TINA_MACRO_TOOLBAR_WIDTH, pPrefs.getTinaMacroToolbarWidth());
    addValue(sb, Prefs.KEY_TINA_MACRO_TOOLBAR_HEIGHT, pPrefs.getTinaMacroToolbarHeight());
    addValue(sb, MacroButton.KEY_MACRO_BUTTON_COUNT, pPrefs.getTinaMacroButtons().size());
    for (int i = 0; i < pPrefs.getTinaMacroButtons().size(); i++) {
      MacroButton macroButton = pPrefs.getTinaMacroButtons().get(i);
      addValue(sb, MacroButton.KEY_MACRO_BUTTON_CAPTION + "." + i, macroButton.getCaption());
      addValue(sb, MacroButton.KEY_MACRO_BUTTON_HINT + "." + i, macroButton.getHint());
      addValue(sb, MacroButton.KEY_MACRO_BUTTON_IMAGE + "." + i, macroButton.getImage());
      addValue(sb, MacroButton.KEY_MACRO_BUTTON_MACRO + "." + i, macroButton.getMacro());
      addValue(sb, MacroButton.KEY_MACRO_BUTTON_INTERNAL + "." + i, macroButton.isInternal());
    }
    // Variation favourites
    {
      addValue(sb, Prefs.KEY_VARIATION_FAVOURITE_COUNT, pPrefs.getVariationFavourites().size());
      int idx =0;
      for(String fav: pPrefs.getVariationFavourites()) {
        addValue(sb,  Prefs.KEY_VARIATION_FAVOURITE_VALUE + "." + idx++, fav);
      }
    }
    // security scoped bookmarks (only on macOS)
    {
      List<String> keys = new ArrayList<>(pPrefs.getSecurityScopedBookmarks().keySet());
      Collections.sort(keys);
      addValue(sb, Prefs.KEY_SEC_BOOKMARK_COUNT, keys.size());
      for(int i = 0;i < keys.size(); i++) {
        String key = keys.get(i);
        addValue(sb,  Prefs.KEY_SEC_BOOKMARK_KEY + "." + i, key);
        addValue(sb,  Prefs.KEY_SEC_BOOKMARK_VALUE + "." + i, pPrefs.getSecurityScopedBookmarks().get(key));
      }
    }
    Tools.writeUTF8Textfile(System.getProperty("user.home") + File.separator + Prefs.PREFS_FILE, sb.toString());
  }

  private void addValue(StringBuilder pSB, String pKey, String pValue) {
    pSB.append(pKey + "=" + (pValue != null ? pValue.replace("\\", "\\\\") : "") + "\n");
  }

  private void addValue(StringBuilder pSB, String pKey, int pValue) {
    pSB.append(pKey + "=" + String.valueOf(pValue) + "\n");
  }

  private void addValue(StringBuilder pSB, String pKey, boolean pValue) {
    pSB.append(pKey + "=" + String.valueOf(pValue) + "\n");
  }

  private void addValue(StringBuilder pSB, String pKey, double pValue) {
    pSB.append(pKey + "=" + Tools.doubleToString(pValue) + "\n");
  }

  private void addValue(StringBuilder pSB, String pKey, Color pValue) {
    pSB.append(pKey + "=" + "[" + pValue.getRed() + ", " + pValue.getGreen() + ", " + pValue.getBlue() + "]" + "\n");
  }

  private void addValue(StringBuilder pSB, String pKey, List<String> pValues) {
    StringBuilder valueBuffer = new StringBuilder();
    for (String entry : pValues) {
      if (valueBuffer.length() > 0) {
        valueBuffer.append(", ");
      }
      valueBuffer.append(encodeListEntry(entry));
    }
    pSB.append(pKey + "=" + valueBuffer.toString() + "\n");
  }

  private String encodeListEntry(String entry) {
    return entry.replace("\\", "\\\\").replace(",", "\\,");
  }

}
