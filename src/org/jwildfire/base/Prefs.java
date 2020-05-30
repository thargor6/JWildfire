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
import java.awt.Font;
import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.mathlib.BaseMathLibType;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.raster.RasterCreator;
import org.jwildfire.create.tina.random.RandomGeneratorType;
import org.jwildfire.create.tina.render.filter.FilterKernelType;
import org.jwildfire.create.tina.swing.EditorDoubleClickActionType;
import org.jwildfire.create.tina.swing.RandomBatchRefreshType;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelControlStyle;
import org.jwildfire.swing.LookAndFeelType;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class Prefs extends ManagedObject {
  // DON'T forget to update the assign() method after adding new properties!!!
  static final String PREFS_FILE = "j-wildfire.properties";

  static final String KEY_GENERAL_LOOK_AND_FEEL = "general.look_and_feel";
  static final String KEY_GENERAL_LOOK_AND_FEEL_THEME = "general.look_and_feel.theme";
  static final String KEY_GENERAL_PATH_IMAGES = "general.path.images";
  static final String KEY_GENERAL_PATH_SCRIPTS = "general.path.scripts";
  static final String KEY_GENERAL_PATH_SOUND_FILES = "sunflow.path.sound_files";

  static final String KEY_GENERAL_SHOW_TIPS_AT_STARTUP = "general.show_tips_at_startup";
  static final String KEY_GENERAL_LAST_TIP = "general.last_tip";

  static final String KEY_GENERAL_DEVELOPMENT_MODE = "general.development_mode";
  static final String KEY_GENERAL_BASE_MATH_LIB = "general.base_math_lib";
  static final String KEY_GENERAL_PATH_THUMBNAILS = "general.path.thumbnails";

  static final String KEY_SUNFLOW_PATH_SCENES = "sunflow.path.scenes";

  static final String KEY_TINA_FONTSCALE = "tina.general.font_scale";

  static final String KEY_TINA_PRESERVE_FREE_CPUS = "tina.render.preserve_free_cpus";
  static final String KEY_TINA_PROFILE_RESOLUTION_COUNT = "tina.profile.resolution.count";
  static final String KEY_TINA_PROFILE_RESOLUTION_WIDTH = "tina.profile.resolution.width";
  static final String KEY_TINA_PROFILE_RESOLUTION_HEIGHT = "tina.profile.resolution.height";
  static final String KEY_TINA_PROFILE_RESOLUTION_DEFAULT_PROFILE = "tina.profile.resolution.default_profile";

  static final String KEY_TINA_PROFILE_QUALITY_COUNT = "tina.profile.quality2.count";
  static final String KEY_TINA_PROFILE_QUALITY_QUALITY = "tina.profile.quality2.quality";
  static final String KEY_TINA_PROFILE_QUALITY_WITH_HDR = "tina.profile.quality2.with_hdr";
  static final String KEY_TINA_PROFILE_QUALITY_WITH_ZBUFFER = "tina.profile.quality2.with_zbuffer";
  static final String KEY_TINA_PROFILE_QUALITY_CAPTION = "tina.profile.quality2.caption";
  static final String KEY_TINA_PROFILE_QUALITY_DEFAULT_PROFILE = "tina.profile.quality2.default_profile";

  static final String KEY_TINA_PATH_MESHES = "tina.path.meshes";
  static final String KEY_TINA_PATH_FLAMES = "tina.path.flames";
  static final String KEY_TINA_PATH_JWFMOVIES = "tina.path.jwfmovies";
  static final String KEY_TINA_PATH_JWFSCRIPTS = "tina.path.jwfscripts";
  static final String KEY_TINA_PATH_CUSTOM_VARIATIONS = "tina.path.custom_variations";
  static final String KEY_TINA_PATH_GRADIENTS = "tina.path.gradients";
  static final String KEY_TINA_RENDER_MOVIE_FRAMES = "tina.render.movie.frames";
  static final String KEY_TINA_PATH_MOVIEFLAMES = "tina.path.movie.flames";

  static final String KEY_TINA_PATH_SVG = "tina.path.svg";

  static final String KEY_TINA_PATH_RAW_MOTION_DATA = "tina.path.raw_motion_data";

  static final String KEY_TINA_RENDER_REALTIME_QUALITY = "tina.render.realtime.quality.2";
  static final String KEY_TINA_RENDER_PREVIEW_QUALITY = "tina.render.preview.quality";

  static final String KEY_TINA_RENDER_ANIM_PREVIEW_QUALITY = "tina.render.anim.preview.quality";
  static final String KEY_TINA_RENDER_ANIM_PREVIEW_SIZE = "tina.render.anim.preview.size";
  static final String KEY_TINA_DEFAULT_ANIM_CONTROLS_ENABLED = "tina.default.anim.controls.enabled";

  static final String KEY_TINA_RENDER_DEFAULT_BG_TRANSPARENCY = "tina.render.default_bg_transparency";
  static final String KEY_TINA_RENDER_DEFAULT_ANTIALIASING_AMOUNT = "tina.render.default_antialiasing_amount.8";
  static final String KEY_TINA_RENDER_DEFAULT_ANTIALIASING_RADIUS = "tina.render.default_antialiasing_radius.5";
  static final String KEY_TINA_PROFILE_ASSOCIATE_WITH_FLAMES = "tina.profile.associate_with_flames";

  static final String KEY_TINA_DISABLE_SOLID_RANDGENS = "tina.disable_solid_randgens";

  static final String KEY_TINA_RANDOM_GENERATOR = "tina.random.generator.3";
  static final String KEY_TINA_INITIAL_RANDOMBATCH_SIZE = "tina.initial_random_batch.size";
  static final String KEY_TINA_RANDOMBATCH_SIZE = "tina.random_batch.size";
  static final String KEY_TINA_RANDOMBATCH_DUALITY_PREFERED_VARIATION = "tina.random_batch.duality.prefered_variation";
  static final String KEY_TINA_RANDOMBATCH_DUALITY_PREFERED_VARIATION_PROBABILITY1 = "tina.random_batch.duality.prefered_variation_probability1";
  static final String KEY_TINA_RANDOMBATCH_DUALITY_PREFERED_VARIATION_PROBABILITY2 = "tina.random_batch.duality.prefered_variation_probability2";
  static final String KEY_TINA_RANDOMMOVIEBATCH_SIZE = "tina.random_movie_batch.size";
  static final String KEY_TINA_RASTER_TYPE = "tina.raster_type";
  static final String KEY_TINA_RANDOMBATCH_BGCOLOR_RED = "tina.random_batch.bg_color.red";
  static final String KEY_TINA_RANDOMBATCH_BGCOLOR_GREEN = "tina.random_batch.bg_color.green";
  static final String KEY_TINA_RANDOMBATCH_BGCOLOR_BLUE = "tina.random_batch.bg_color.blue";
  static final String KEY_TINA_RANDOMBATCH_REFRESH_TYPE = "tina.random_batch.refresh_type";

  static final String KEY_TINA_EDITOR_CONTROLS_WITH_COLOR = "tina.editor.controls.with_color.3";
  static final String KEY_TINA_EDITOR_PROGRESSIVE_PREVIEW = "tina.editor.progressive_preview";
  static final String KEY_TINA_EDITOR_PROGRESSIVE_PREVIEW_MAX_RENDER_TIME = "tina.editor.progressive_preview.max_render_time.4";
  static final String KEY_TINA_EDITOR_PROGRESSIVE_PREVIEW_MAX_RENDER_QUALITY = "tina.editor.progressive_preview.max_render_quality.3";
  static final String KEY_TINA_EDITOR_CONTROLS_WITH_ANTIALIASING = "tina.editor.controls.with_antialising.2";
  static final String KEY_TINA_EDITOR_CONTROLS_WITH_SHADOWS = "tina.editor.controls.with_shadows";
  static final String KEY_TINA_EDITOR_CONTROLS_WITH_NUMBERS = "tina.editor.controls.with_numbers";
  static final String KEY_TINA_EDITOR_CONTROLS_STYLE = "tina.editor.controls.style";
  static final String KEY_TINA_EDITOR_GRID_SIZE = "tina.editor.grid_size";

  static final String KEY_TINA_ADVANCED_CODE_EDITOR = "tina.advanced_code_editor";
  static final String KEY_TINA_ADVANCED_CODE_EDITOR_COLOR_FIX = "tina.advanced_code_editor_color_fix";
  static final String KEY_TINA_ADVANCED_CODE_EDITOR_FONT_SIZE = "tina.advanced_code_editor_font_size.2";

  static final String KEY_TINA_EDITOR_GUIDES_LINE_WIDTH = "tina.editor.guides.linewidth";
  static final String KEY_TINA_EDITOR_GUIDES_COLOR_CENTER_POINT = "tina.editor.guides.color.center_point";
  static final String KEY_TINA_EDITOR_GUIDES_COLOR_RULE_OF_THIRDS = "tina.editor.guides.color.rule_of_thirds";
  static final String KEY_TINA_EDITOR_GUIDES_COLOR_GOLDEN_RATIO = "tina.editor.guides.color.golden_ratio";

  static final String KEY_TINA_DEFAULT_SPATIAL_OVERSAMPLING = "tina.default.spatial_oversampling.5";
  static final String KEY_TINA_DEFAULT_POST_NOISE_FILTER = "tina.default.post_noise_filter.2";
  static final String KEY_TINA_DEFAULT_POST_NOISE_FILTER_THRESHOLD = "tina.default.post_noise_filter_threshold";
  static final String KEY_TINA_DEFAULT_POST_OPTIX_DENOISER = "tina.default.post_optix_denoiser";
  static final String KEY_TINA_DEFAULT_POST_OPTIX_DENOISER_BLEND = "tina.default.post_optix_denoiser_blend";

  static final String KEY_TINA_DEFAULT_FOREGROUND_OPACITY = "tina.default.foreground_opacity";
  static final String KEY_TINA_DEFAULT_FILTER_KERNEL = "tina.default.filter_kernel.5";
  static final String KEY_TINA_DEFAULT_FILTER_RADIUS = "tina.default.filter_radius.1";
  static final String KEY_TINA_DEFAULT_FILTER_VISUALISATION_FLAT = "tina.default.filter_visualisation_flat";

  public static final String KEY_TINA_EDITOR_DEFAULT_DOUBLECLICK_ACTION = "tina.editor.default.double_click_action";
  public static final String KEY_TINA_DEFAULT_FADE_TO_WHITE_LEVEL = "tina.default.fade_to_white_level";
  public static final String KEY_TINA_DEFAULT_FPS = "tina.default.fps.2";
  public static final String KEY_TINA_DEFAULT_FRAME_COUNT = "tina.default.frame_count";

  static final String KEY_TINA_DEFAULT_EXPAND_NONLINEAR_PARAMS = "tina.initially_expand_nonlinear_params";
  static final String KEY_TINA_FACLRENDER_PATH = "tina.faclrender_path";
  static final String KEY_TINA_FACLRENDER_OPTS = "tina.faclrender_opts";

  static final String KEY_TINA_AUTOLOAD_IMAGES_IN_IR = "tina.autoload_images_in_ir";
  static final String KEY_TINA_SAVING_STORE_HDR_IN_IR = "tina.saving.store_hdr_in_ir";
  static final String KEY_TINA_SAVING_STORE_FLAMES_WHEN_SAVING_IMAGE = "tina.saving.store_flames_when_saving_image";
  static final String KEY_TINA_OPTIMIZED_RENDERING_IR = "tina.optimized_rendering_ir.5";

  static final String KEY_TINA_DISABLE_WIKIMEDIA_COMMONS_WARNING = "tina.random_batch.disable_wikimedia_commons_warning";
  static final String KEY_TINA_COLORMAP_RANDGEN_IMAGE_PATH = "tina.random_batch.random_gen.colormap.image_path";
  public static final String KEY_TINA_FREE_CACHE_IN_BATCH_RENDERER = "tina.free_cache_in_batch_renderer";

  public static final String KEY_TINA_EXCLUDED_VARIATIONS = "tina.excluded_variations";
  public static final String KEY_TINA_LSYSTEM_MAXLENGTH = "tina.lsystem.maxlength";

  public static final String KEY_TINA_CREATE_DEFAULT_MACRO_BUTTONS = "tina.create_default_macrobuttons.6";
  public static final String KEY_TINA_VERTICAL_MACRO_BUTTONS = "tina.macro_buttons.vertical";
  public static final String KEY_TINA_MACRO_TOOLBAR_WIDTH = "tina.toolbar.macro.width";
  public static final String KEY_TINA_MACRO_TOOLBAR_HEIGHT = "tina.toolbar.macro.height";

  public static final String KEY_TINA_MUTAGEN_MUTATIONTYPES_USER1 = "tina.mutagen.mutationtypes_user1";
  public static final String KEY_TINA_MUTAGEN_MUTATIONTYPES_USER2 = "tina.mutagen.mutationtypes_user2";
  public static final String KEY_TINA_MUTAGEN_MUTATIONTYPES_USER3 = "tina.mutagen.mutationtypes_user3";

  public static final String KEY_TINA_MUTAGEN_MUTATIONTYPE_HORIZ1 = "tina.mutagen.mutationtype_horiz1";
  public static final String KEY_TINA_MUTAGEN_MUTATIONTYPE_HORIZ2 = "tina.mutagen.mutationtype_horiz2";
  public static final String KEY_TINA_MUTAGEN_MUTATIONTYPE_VERT1 = "tina.mutagen.mutationtype_vert1";
  public static final String KEY_TINA_MUTAGEN_MUTATIONTYPE_VERT2 = "tina.mutagen.mutationtype_vert2";

  static final String KEY_IFLAMES_LIBRARY_PATH_FLAMES = "iflames.library_path.flames";
  static final String KEY_IFLAMES_LIBRARY_PATH_IMAGES = "iflames.library_path.images";
  static final String KEY_IFLAMES_LOAD_LIBRARY_AT_STARTUP = "iflames.load_library_at_startup";

  @Property(description = "Script drawer for the animation editor (please note that this NOT for scripts of the flame-editor)", category = PropertyCategory.MISC)
  private String scriptPath = null;
  private String lastInputScriptPath = null;
  private String lastOutputScriptPath = null;

  @Property(description = "Drawer for thumbnail-cache (restart of program after change required)", category = PropertyCategory.GENERAL)
  private String thumbnailPath = null;

  @Property(description = "Image drawer", category = PropertyCategory.GENERAL)
  private String imagePath = null;
  private String lastInputImagePath = null;
  private String lastOutputImagePath = null;

  @Property(description = "Sound file drawer", category = PropertyCategory.GENERAL)
  private String soundFilePath = null;
  private String lastInputSoundFilePath = null;

  @Property(description = "Movie flames drawer", category = PropertyCategory.TINA)
  private String movieFlamesPath = null;
  private String lastOutputMovieFlamesPath = null;

  @Property(description = "Number of preserved free cpus/cores", category = PropertyCategory.TINA)
  private int tinaPreserveFreeCPUs = 0;

  @Property(description = "Flame file drawer", category = PropertyCategory.TINA)
  private String tinaFlamePath = null;
  private String lastInputFlamePath = null;
  private String lastOutputFlamePath = null;

  @Property(description = "Mesh file drawer", category = PropertyCategory.TINA)
  private String tinaMeshPath = null;
  private String lastMeshPath = null;

  @Property(description = "Show tips at startup", category = PropertyCategory.GENERAL)
  private boolean showTipsAtStartup = true;

  @Property(description = "Last tip shown at startup", category = PropertyCategory.GENERAL)
  private int lastTip = 0;

  @Property(description = "Path to the flames building the flame-library for the IFlames", category = PropertyCategory.IFLAMES)
  private String iflamesFlameLibraryPath = null;

  @Property(description = "Path to the images building the image-library for the IFlames", category = PropertyCategory.IFLAMES)
  private String iflamesImageLibraryPath = null;

  @Property(description = "Automatically load the flame- and image-library when opening the IFlames-window", category = PropertyCategory.IFLAMES)
  private boolean iflamesLoadLibraryAtStartup = false;

  @Property(description = "Use an advanced editor with syntax-highlighting for editing scripts and custom variations. May not work on all systems and may not look good with all themes, so you can turn it off. A change of this property requires a program-restart", category = PropertyCategory.TINA)
  private boolean tinaAdvancedCodeEditor = true;

  @Property(description = "Make background color of advanced code editor white, overriding any look and feel settings. Only applies when advanced code editor is toggled on", category = PropertyCategory.TINA)
  private boolean tinaAdvancedCodeEditorColorFix = true;

  @Property(description = "Disable solid-flame-random-generators inside the global \"All\"-random-generator. This may make sense when you do not like solid flames at all or when the calculation takes too long on your machine. You may still manually select solid-flame-random-generators. A change of this property requires a program-restart", category = PropertyCategory.TINA)
  private boolean tinaDisableSolidFlameRandGens = false;

  @Property(description = "Set font size for advanced code editor. Only applies when advanced code editor is toggled on", category = PropertyCategory.TINA)
  private int tinaAdvancedCodeEditorFontSize = 14;

  @Property(description = "Expand the params in the non-linear tab per default. This is only the initial value, you may expand and collapse the params at any time manually", category = PropertyCategory.TINA)
  private boolean tinaDefaultExpandNonlinearParams = false;

  @Property(description = "Path to the external GPU renderer, currently only Windows-plattforms are supported. The software searches this path at program-start for a file called \"FACLRender.exe\", only when this file exists, the GPU-rendering-window is displayed, a change of this property requires a program-restart.", category = PropertyCategory.TINA)
  private String tinaFACLRenderPath = null;

  @Property(description = "Commandline-options added when invokling the external GPU-renderer. Refer to the documentation or sourcecode for more information.", category = PropertyCategory.TINA)
  private String tinaFACLRenderOptions = "-nde";

  @Property(description = "JWFMovie file drawer", category = PropertyCategory.TINA)
  private String tinaJWFMoviePath = null;
  private String lastInputJWFMoviePath = null;
  private String lastOutputJWFMoviePath = null;

  @Property(description = "Folder for user-scripts for the flame-editor", category = PropertyCategory.TINA)
  private String tinaJWFScriptPath = null;

  @Property(description = "Folder for custom-variations", category = PropertyCategory.TINA)
  private String tinaCustomVariationsPath = null;

  @Property(description = "Drawer for raw motion data", category = PropertyCategory.TINA)
  private String tinaRawMotionDataPath = null;

  @Property(description = "Gradient file drawer", category = PropertyCategory.TINA)
  private String tinaGradientPath = null;

  @Property(description = "SVG file drawer", category = PropertyCategory.TINA)
  private String tinaSVGPath = null;

  @Property(description = "Associate profile information with flame files", category = PropertyCategory.TINA)
  private boolean tinaAssociateProfilesWithFlames = false;

  @Property(description = "Free the cacher after each rendered image in the batch-renderer", category = PropertyCategory.TINA)
  private boolean tinaFreeCacheInBatchRenderer = false;

  @Property(description = "Generate and save HDR images in the interactive renderer", category = PropertyCategory.TINA)
  private boolean tinaSaveHDRInIR = false;

  @Property(description = "Automatically re-load and display saved images from within the interactive renderer (in order to show the final result which may differ from the IR display). May be also activated per user-session in the IR itself", category = PropertyCategory.TINA)
  private boolean tinaAutoloadSavedImagesInIR = true;

  @Property(description = "Disable the warning about the usage of Wikimedia Commons random-flame-generator", category = PropertyCategory.TINA)
  private boolean tinaDisableWikimediaCommonsWarning = false;

  @Property(description = "Automatically save a flame-copy when a rendered image is saved", category = PropertyCategory.TINA)
  private boolean tinaSaveFlamesWhenImageIsSaved = false;

  @Property(description = "Used a colored display for affine transforms", category = PropertyCategory.TINA)
  private boolean tinaEditorControlsWithColor = true;

  @Property(description = "Turn on progressive preview-display in the main-editor by default", category = PropertyCategory.TINA)
  private boolean tinaEditorProgressivePreview = true;

  @Property(description = "Maximum render-time for the progressive preview-display", category = PropertyCategory.TINA)
  private double tinaEditorProgressivePreviewMaxRenderTime = 25.0;

  @Property(description = "Font-scale for the flame-fractal-editor (experimental, to apply changes a restart of the program is required)", category = PropertyCategory.TINA)
  private double tinaFontScale = 1.0;

  @Property(description = "Maximum render-quality for the progressive preview-display", category = PropertyCategory.TINA)
  private double tinaEditorProgressivePreviewMaxRenderQuality = 250.0;

  @Property(description = "Turn on antialiasing for drawing lines and triangle-symbols in the editor", category = PropertyCategory.TINA)
  private boolean tinaEditorControlsWithAntialiasing = false;

  @Property(description = "Display transform-numbers inside of the triangle-symbols in the editor", category = PropertyCategory.TINA)
  private boolean tinaEditorControlsWithNumbers = true;

  @Property(description = "Add small shadows to the controls, to increase contrast with background, in the editor", category = PropertyCategory.TINA)
  private boolean tinaEditorControlsWithShadows = true;

  @Property(description = "Default spatial oversampling setting, used when creating a new flame", category = PropertyCategory.TINA)
  private int tinaDefaultSpatialOversampling = 1;

  @Property(description = "Default setting for applying a post-noise-filter to rendered images", category = PropertyCategory.TINA)
  private boolean tinaDefaultPostNoiseFilter = false;

  @Property(description = "Default threshold-setting for the post-noise-filter", category = PropertyCategory.TINA)
  private double tinaDefaultPostNoiseFilterThreshold = 0.35;

  @Property(description = "Default setting for applying the OptiX denoiser by NVidia to rendered images (only when available on your platform)", category = PropertyCategory.TINA)
  private boolean tinaDefaultPostOptiXDenoiser = false;

  @Property(description = "Default blend-setting for the OptiX denoiser by NVidia in the range 0.0 .. 1.0 (0.0 = denoised image, 1.0 = raw image, use values in between to mix between the images)", category = PropertyCategory.TINA)
  private double tinaDefaultPostOptiXDenoiserBlend = 0.11111;

  @Property(description = "Default foreground-opacity when rendering images with transparent background", category = PropertyCategory.TINA)
  private double tinaDefaultForegroundOpacity = 1.0;

  @Property(description = "Use flat (and faster) rendering to visualize filter-kernels", category = PropertyCategory.TINA)
  private boolean tinaDefaultFilterVisualisationFlat = false;

  @Property(description = "Default spatial filter-kernel, used when creating a new flame", category = PropertyCategory.TINA, editorClass = FilterKernelTypeEditor.class)
  private FilterKernelType tinaDefaultSpatialFilterKernel = FilterKernelType.MITCHELL_SMOOTH;

  @Property(description = "Default spatial filter-radius, used when creating a new flame (set to 0 in order to turn off spatial filtering)", category = PropertyCategory.TINA)
  private double tinaDefaultSpatialFilterRadius = 0.75;

  @Property(description = "Style of the controls (\"triangles\") in the editor", category = PropertyCategory.TINA, editorClass = FlamePanelTriangleStyleEditor.class)
  private FlamePanelControlStyle tinaEditorControlsStyle = FlamePanelControlStyle.TRIANGLE;

  @Property(description = "Action which is performed when you double-click at the editor-panel", category = PropertyCategory.TINA, editorClass = EditorDoubleClickActionTypeEditor.class)
  private EditorDoubleClickActionType tinaEditorDoubleClickAction = EditorDoubleClickActionType.SWITCH_TRIANGLE_CAM_EDIT;

  @Property(description = "Grid size (distance between two grid-lines) in the editor", category = PropertyCategory.TINA)
  private double tinaEditorGridSize = 0.5;

  @Property(description = "Default Fade-to-white-level of new generated flames", category = PropertyCategory.TINA)
  private double tinaDefaultFadeToWhiteLevel = 220.0;

  @Property(description = "Default FPS of new generated flames", category = PropertyCategory.TINA)
  private int tinaDefaultFPS = 25;

  @Property(description = "Default frame-count for animated flames", category = PropertyCategory.TINA)
  private int tinaDefaultFrameCount = 100;

  @Property(description = "Optimize display-refresh in the interactive renderer, but may be slower at some really old computers", category = PropertyCategory.TINA)
  private boolean tinaOptimizedRenderingIR = true;

  @Property(description = "User-defined mutation-sub-types for mutation-type USER1 in the MutaGen", category = PropertyCategory.TINA)
  private String tinaMutaGenMutationTypesUser1 = "ADD_TRANSFORM, CHANGE_WEIGHT, AFFINE, RANDOM_PARAMETER";
  @Property(description = "User-defined mutation-sub-types for mutation-type USER2 in the MutaGen", category = PropertyCategory.TINA)
  private String tinaMutaGenMutationTypesUser2 = "RANDOM_GRADIENT, LOCAL_GAMMA";
  @Property(description = "User-defined mutation-sub-types for mutation-type USER3 in the MutaGen", category = PropertyCategory.TINA)
  private String tinaMutaGenMutationTypesUser3 = "AFFINE_3D, BOKEH, RANDOM_FLAME";

  @Property(description = "Default mutation-type at category \"Horiz 1\" in the MutaGen", category = PropertyCategory.TINA)
  private String tinaMutaGenMutationTypeHoriz1 = "USER1";
  @Property(description = "Default mutation-type at category \"Horiz 2\" in the MutaGen", category = PropertyCategory.TINA)
  private String tinaMutaGenMutationTypeHoriz2 = "USER2";
  @Property(description = "Default mutation-type at category \"Vert 1\" in the MutaGen", category = PropertyCategory.TINA)
  private String tinaMutaGenMutationTypeVert1 = "ALL";
  @Property(description = "Default mutation-type at category \"Vert 2\" in the MutaGen", category = PropertyCategory.TINA)
  private String tinaMutaGenMutationTypeVert2 = "USER3";

  @Property(description = "Sunflow scene file drawer", category = PropertyCategory.SUNFLOW)
  private String sunflowScenePath = null;
  private String lastInputSunflowScenePath = null;
  private String lastOutputSunflowScenePath = null;

  @Property(description = "Prefered variations of the \"Duality\"-random-flame generator. You may specify a comma-separated list of your favourite variations", category = PropertyCategory.TINA)
  private String tinaRandGenDualityPreferedVariation = "spherical3D";
  @Property(description = "Probability to use one of the prefered variations in the 1st transform by the \"Duality\"-random-flame generator.", category = PropertyCategory.TINA)
  private double tinaRandGenDualityPreferedVariationProbability1 = 0.33;
  @Property(description = "Probability to use one of the prefered variations in the 2nd transform by the \"Duality\"-random-flame generator.", category = PropertyCategory.TINA)
  private double tinaRandGenDualityPreferedVariationProbability2 = 0.25;

  @Property(description = "Image-input-path for the \"Color map\"-random-flame-generator (is scanned recursively, so BEWARE)", category = PropertyCategory.TINA)
  private String tinaRandGenColorMapImagePath = null;

  @Property(description = "Activate development mode", category = PropertyCategory.GENERAL)
  private boolean developmentMode = false;

  @Property(description = "Implementation of basic mathematical functions to use", category = PropertyCategory.GENERAL, editorClass = BaseMathLibTypeEditor.class)
  private BaseMathLibType baseMathLibType = BaseMathLibType.getDefaultValue();

  @Property(description = "Color of the guides for the center point (restart of program after change required)", category = PropertyCategory.TINA)
  private Color tinaEditorGuidesCenterPointColor = new Color(255, 255, 255);
  @Property(description = "Color of the guides for rule of thirds (restart of program after change required)", category = PropertyCategory.TINA)
  private Color tinaEditorGuidesRuleOfThirdsColor = new Color(255, 0, 0);
  @Property(description = "Color of the guides for rule of golden ratio (restart of program after change required)", category = PropertyCategory.TINA)
  private Color tinaEditorGuidesGoldenRatioColor = new Color(0, 255, 0);
  @Property(description = "Line width of the guides (restart of program after change required)", category = PropertyCategory.TINA)
  private double tinaEditorGuidesLineWidth = 1.6;

  @Property(description = "The width of the macro-toolbar when active and in vertical mode (restart of program after change required)", category = PropertyCategory.TINA)
  private int tinaMacroToolbarWidth = 90;

  @Property(description = "The height of the macro-toolbar when active and in horizontal mode (restart of program after change required)", category = PropertyCategory.TINA)
  private int tinaMacroToolbarHeight = 28;

  @Property(description = "Create a vertical toolbar to hold macro-buttons instead of the horizontal one (restart of program after change required)", category = PropertyCategory.TINA)
  private boolean tinaMacroButtonsVertical = false;

  private final List<QualityProfile> qualityProfiles = new ArrayList<QualityProfile>();
  private final List<ResolutionProfile> resolutionProfiles = new ArrayList<ResolutionProfile>();
  private final List<WindowPrefs> windowPrefs = new ArrayList<WindowPrefs>();
  private final List<String> tinaExcludedVariations = new ArrayList<>();
  @Property(description = "Maximum size of the generated string for the L-System variation", category = PropertyCategory.TINA)
  private int tinaLSystemMaxLength = 25000;

  private boolean createTinaDefaultMacroButtons = true;
  private final List<MacroButton> tinaMacroButtons = new ArrayList<MacroButton>();

  public static class RandomBatchRefreshTypeEditor extends ComboBoxPropertyEditor {
    public RandomBatchRefreshTypeEditor() {
      super();
      setAvailableValues(new RandomBatchRefreshType[] { RandomBatchRefreshType.CLEAR, RandomBatchRefreshType.INSERT, RandomBatchRefreshType.APPEND });
    }
  }

  public static class BaseMathLibTypeEditor extends ComboBoxPropertyEditor {
    public BaseMathLibTypeEditor() {
      super();
      setAvailableValues(new BaseMathLibType[] { BaseMathLibType.FAST_MATH, BaseMathLibType.JAVA_MATH });
    }
  }

  public static class FlamePanelTriangleStyleEditor extends ComboBoxPropertyEditor {
    public FlamePanelTriangleStyleEditor() {
      super();
      setAvailableValues(new FlamePanelControlStyle[] { FlamePanelControlStyle.AXIS, FlamePanelControlStyle.CROSSHAIR, FlamePanelControlStyle.RECTANGLE, FlamePanelControlStyle.TRIANGLE, FlamePanelControlStyle.HIDDEN });
    }
  }

  public static class RandomGeneratorTypeEditor extends ComboBoxPropertyEditor {
    public RandomGeneratorTypeEditor() {
      super();
      setAvailableValues(RandomGeneratorType.values());
    }
  }

  public static class RasterCreatorEditor extends ComboBoxPropertyEditor {
    public RasterCreatorEditor() {
      super();
      setAvailableValues(new RasterCreator[] { RasterCreator.FLOAT_INT_RASTER });
    }
  }

  public static class EditorDoubleClickActionTypeEditor extends ComboBoxPropertyEditor {
    public EditorDoubleClickActionTypeEditor() {
      super();
      setAvailableValues(new EditorDoubleClickActionType[] { EditorDoubleClickActionType.ACTIVATE_TRIANGLE_EDIT, EditorDoubleClickActionType.RENDER_FLAME, EditorDoubleClickActionType.SWITCH_TRIANGLE_CAM_EDIT, EditorDoubleClickActionType.NONE });
    }
  }

  public static class FilterKernelTypeEditor extends ComboBoxPropertyEditor {
    public FilterKernelTypeEditor() {
      super();
      setAvailableValues(FilterKernelType.values());
    }
  }

  private LookAndFeelType lookAndFeelType = Tools.OSType.MAC.equals(Tools.getOSType()) ? LookAndFeelType.SYSTEM : LookAndFeelType.NIMBUS;
  private String lookAndFeelTheme = "";

  @Property(description = "Default number of frames for a movie", category = PropertyCategory.TINA)
  private int tinaRenderMovieFrames = 90;

  private static int tinaRenderThreads;

  @Property(description = "Type of the iteration-raster (less precision needs less memory)", category = PropertyCategory.TINA, editorClass = RasterCreatorEditor.class)
  private RasterCreator tinaRasterType = RasterCreator.getDefaultValue();

  @Property(description = "Random number generator to use", category = PropertyCategory.TINA, editorClass = RandomGeneratorTypeEditor.class)
  private RandomGeneratorType tinaRandomNumberGenerator = RandomGeneratorType.getDefaultValue();

  @Property(description = "Quality for realtime rendering (please restart app after changing this)", category = PropertyCategory.TINA)
  private double tinaRenderRealtimeQuality = 0.5;

  @Property(description = "Quality for preview rendering of animated flames in the main editor", category = PropertyCategory.TINA)
  private double tinaRenderAnimPreviewQuality = 1.25;

  @Property(description = "Size of the preview rendering of animated flames in the main editor (as fraction of the size of the editor view)", category = PropertyCategory.TINA)
  private double tinaRenderAnimPreviewSize = 0.5;

  @Property(description = "Indicates if the animation controls in the main editor are enabled by default", category = PropertyCategory.TINA)
  private boolean tinaDefaultAnimationControlsEnabled = true;

  @Property(description = "Number of random-flames generated during program-start", category = PropertyCategory.TINA)
  private int tinaInitialRandomBatchSize = 3;
  @Property(description = "Number of generated flames by invoking the \"Random flames\" function", category = PropertyCategory.TINA)
  private int tinaRandomBatchSize = 24;
  @Property(description = "Number of generated movies by invoking the \"Random movies\" function", category = PropertyCategory.TINA)
  private int tinaRandomMovieBatchSize = 12;
  @Property(description = "Red component of the background color of randomly generated flames", category = PropertyCategory.TINA)
  private int tinaRandomBatchBGColorRed = 0;
  @Property(description = "Red component of the background color of randomly generated flames", category = PropertyCategory.TINA)
  private int tinaRandomBatchBGColorGreen = 0;
  @Property(description = "Red component of the background color of randomly generated flames", category = PropertyCategory.TINA)
  private int tinaRandomBatchBGColorBlue = 0;
  @Property(description = "How to refresh the thumbnail ribbon after creating a new random batch", category = PropertyCategory.TINA, editorClass = RandomBatchRefreshTypeEditor.class)
  private RandomBatchRefreshType tinaRandomBatchRefreshType = RandomBatchRefreshType.CLEAR;

  @Property(description = "Default background transparency", category = PropertyCategory.TINA)
  private boolean tinaDefaultBGTransparency = false;

  @Property(description = "Default antialiasing amount (set to zero to turn antialiasing off by default)", category = PropertyCategory.TINA)
  private double tinaDefaultAntialiasingAmount = 0.25;

  @Property(description = "Default antialiasing radius (set to zero to turn antialiasing off by default)", category = PropertyCategory.TINA)
  private double tinaDefaultAntialiasingRadius = 0.5;

  @Property(description = "Quality for preview rendering", category = PropertyCategory.TINA)
  private int tinaRenderPreviewQuality = 100;

  public String getInputScriptPath() {
    return lastInputScriptPath != null ? lastInputScriptPath : scriptPath;
  }

  public String getInputSoundFilePath() {
    return lastInputSoundFilePath != null ? lastInputSoundFilePath : soundFilePath;
  }

  public String getOutputScriptPath() {
    return lastOutputScriptPath != null ? lastOutputScriptPath : scriptPath;
  }

  public String getOutputMovieFlamesPath() {
    return lastOutputMovieFlamesPath != null ? lastOutputMovieFlamesPath : movieFlamesPath;
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

  public void setLastInputSoundFile(File pFile) {
    lastInputSoundFilePath = pFile.getParent();
    if (soundFilePath == null || soundFilePath.length() == 0) {
      soundFilePath = lastInputSoundFilePath;
    }
  }

  public void setLastOutputScriptFile(File pFile) {
    lastOutputScriptPath = pFile.getParent();
    if (scriptPath == null || scriptPath.length() == 0) {
      scriptPath = lastOutputScriptPath;
    }
  }

  public void setLastOutputMovieFlamesFile(File pFile) {
    lastOutputMovieFlamesPath = pFile.getParent();
    if (movieFlamesPath == null || movieFlamesPath.length() == 0) {
      movieFlamesPath = lastOutputMovieFlamesPath;
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

  public void setLastMeshFile(File pFile) {
    lastMeshPath = pFile.getParent();
    if (tinaMeshPath == null || tinaMeshPath.length() == 0) {
      tinaMeshPath = lastMeshPath;
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

  public String getInputJWFMoviePath() {
    return lastInputJWFMoviePath != null ? lastInputJWFMoviePath : tinaJWFMoviePath;
  }

  public void setLastInputJWFMovieFile(File pFile) {
    lastInputJWFMoviePath = pFile.getParent();
    if (tinaJWFMoviePath == null || tinaJWFMoviePath.length() == 0) {
      tinaJWFMoviePath = lastInputJWFMoviePath;
    }
  }

  public String getOutputJWFMoviePath() {
    return lastOutputJWFMoviePath != null ? lastOutputJWFMoviePath : tinaJWFMoviePath;
  }

  public void setLastOutputJWFMovieFile(File pFile) {
    lastOutputJWFMoviePath = pFile.getParent();
    if (tinaJWFMoviePath == null || tinaJWFMoviePath.length() == 0) {
      tinaJWFMoviePath = lastOutputJWFMoviePath;
    }
  }

  public String getTinaJWFScriptPath() {
    return tinaJWFScriptPath;
  }

  public void setTinaJWFScriptPath(String pTinaJWFScriptPath) {
    tinaJWFScriptPath = pTinaJWFScriptPath;
  }

  public String getTinaGradientPath() {
    return tinaGradientPath;
  }

  public void setTinaGradientPath(String pTinaGradientPath) {
    tinaGradientPath = pTinaGradientPath;
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

  private void loadFromFile() throws Exception {
    new PrefsReader().readPrefs(this);
  }

  public void saveToFile() throws Exception {
    new PrefsWriter().writePrefs(this);
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public void setTinaFlamePath(String flamePath) {
    this.tinaFlamePath = flamePath;
  }

  public void setTinaJWFMoviePath(String jwfMoviePath) {
    this.tinaJWFMoviePath = jwfMoviePath;
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

  public String getTinaJWFMoviePath() {
    return tinaJWFMoviePath;
  }

  public String getSunflowScenePath() {
    return sunflowScenePath;
  }

  public String getScriptPath() {
    return scriptPath;
  }

  public void assign(Prefs pSrc) {
    thumbnailPath = pSrc.thumbnailPath;
    scriptPath = pSrc.scriptPath;
    lastInputScriptPath = pSrc.lastInputScriptPath;
    lastOutputScriptPath = pSrc.lastOutputScriptPath;
    imagePath = pSrc.imagePath;
    lastInputImagePath = pSrc.lastInputImagePath;
    lastOutputImagePath = pSrc.lastOutputImagePath;
    tinaFlamePath = pSrc.tinaFlamePath;
    lastInputFlamePath = pSrc.lastInputFlamePath;
    lastOutputFlamePath = pSrc.lastOutputFlamePath;
    tinaMeshPath = pSrc.tinaMeshPath;
    lastMeshPath = pSrc.lastMeshPath;
    tinaJWFMoviePath = pSrc.tinaJWFMoviePath;
    lastInputJWFMoviePath = pSrc.lastInputJWFMoviePath;
    lastOutputJWFMoviePath = pSrc.lastOutputJWFMoviePath;
    sunflowScenePath = pSrc.sunflowScenePath;
    lastInputSunflowScenePath = pSrc.lastInputSunflowScenePath;
    lastOutputSunflowScenePath = pSrc.lastOutputSunflowScenePath;
    soundFilePath = pSrc.soundFilePath;
    lastInputSoundFilePath = pSrc.lastInputSoundFilePath;
    movieFlamesPath = pSrc.movieFlamesPath;
    lastOutputMovieFlamesPath = pSrc.lastOutputMovieFlamesPath;
    lookAndFeelType = pSrc.lookAndFeelType;
    lookAndFeelTheme = pSrc.lookAndFeelTheme;
    developmentMode = pSrc.developmentMode;
    tinaRandomNumberGenerator = pSrc.tinaRandomNumberGenerator;
    tinaPreserveFreeCPUs = pSrc.tinaPreserveFreeCPUs;
    tinaDisableWikimediaCommonsWarning = pSrc.tinaDisableWikimediaCommonsWarning;
    tinaEditorControlsWithColor = pSrc.tinaEditorControlsWithColor;
    tinaEditorControlsWithAntialiasing = pSrc.tinaEditorControlsWithAntialiasing;
    tinaEditorControlsWithShadows = pSrc.tinaEditorControlsWithShadows;
    tinaEditorControlsStyle = pSrc.tinaEditorControlsStyle;
    tinaEditorControlsWithNumbers = pSrc.tinaEditorControlsWithNumbers;
    tinaEditorGridSize = pSrc.tinaEditorGridSize;
    tinaRandGenColorMapImagePath = pSrc.tinaRandGenColorMapImagePath;
    tinaDefaultAntialiasingAmount = pSrc.tinaDefaultAntialiasingAmount;
    tinaDefaultAntialiasingRadius = pSrc.tinaDefaultAntialiasingRadius;
    tinaRenderMovieFrames = pSrc.tinaRenderMovieFrames;
    tinaRenderPreviewQuality = pSrc.tinaRenderPreviewQuality;
    tinaRenderRealtimeQuality = pSrc.tinaRenderRealtimeQuality;
    tinaRenderAnimPreviewQuality = pSrc.tinaRenderAnimPreviewQuality;
    tinaRenderAnimPreviewSize = pSrc.tinaRenderAnimPreviewSize;
    tinaDefaultAnimationControlsEnabled = pSrc.tinaDefaultAnimationControlsEnabled;
    tinaRandomBatchSize = pSrc.tinaRandomBatchSize;
    tinaInitialRandomBatchSize = pSrc.tinaInitialRandomBatchSize;
    tinaRandomMovieBatchSize = pSrc.tinaRandomMovieBatchSize;
    tinaRandomBatchBGColorRed = pSrc.tinaRandomBatchBGColorRed;
    tinaRandomBatchBGColorGreen = pSrc.tinaRandomBatchBGColorGreen;
    tinaRandomBatchBGColorBlue = pSrc.tinaRandomBatchBGColorBlue;
    tinaRandomBatchRefreshType = pSrc.tinaRandomBatchRefreshType;
    tinaAssociateProfilesWithFlames = pSrc.tinaAssociateProfilesWithFlames;
    tinaSaveFlamesWhenImageIsSaved = pSrc.tinaSaveFlamesWhenImageIsSaved;
    tinaSaveHDRInIR = pSrc.tinaSaveHDRInIR;
    tinaAutoloadSavedImagesInIR = pSrc.tinaAutoloadSavedImagesInIR;
    tinaDefaultBGTransparency = pSrc.tinaDefaultBGTransparency;
    tinaRasterType = pSrc.tinaRasterType;
    tinaJWFScriptPath = pSrc.tinaJWFScriptPath;
    tinaCustomVariationsPath = pSrc.tinaCustomVariationsPath;
    tinaGradientPath = pSrc.tinaGradientPath;
    tinaSVGPath = pSrc.tinaSVGPath;
    baseMathLibType = pSrc.baseMathLibType;
    tinaOptimizedRenderingIR = pSrc.tinaOptimizedRenderingIR;
    tinaMacroButtonsVertical = pSrc.tinaMacroButtonsVertical;
    tinaMacroToolbarHeight = pSrc.tinaMacroToolbarHeight;
    tinaFreeCacheInBatchRenderer = pSrc.tinaFreeCacheInBatchRenderer;

    tinaRandGenDualityPreferedVariation = pSrc.tinaRandGenDualityPreferedVariation;
    tinaRandGenDualityPreferedVariationProbability1 = pSrc.tinaRandGenDualityPreferedVariationProbability1;
    tinaRandGenDualityPreferedVariationProbability2 = pSrc.tinaRandGenDualityPreferedVariationProbability2;

    tinaEditorGuidesCenterPointColor = new Color(pSrc.tinaEditorGuidesCenterPointColor.getRed(), pSrc.tinaEditorGuidesCenterPointColor.getGreen(), pSrc.tinaEditorGuidesCenterPointColor.getBlue());
    tinaEditorGuidesRuleOfThirdsColor = new Color(pSrc.tinaEditorGuidesRuleOfThirdsColor.getRed(), pSrc.tinaEditorGuidesRuleOfThirdsColor.getGreen(), pSrc.tinaEditorGuidesRuleOfThirdsColor.getBlue());
    tinaEditorGuidesGoldenRatioColor = new Color(pSrc.tinaEditorGuidesGoldenRatioColor.getRed(), pSrc.tinaEditorGuidesGoldenRatioColor.getGreen(), pSrc.tinaEditorGuidesGoldenRatioColor.getBlue());
    tinaEditorGuidesLineWidth = pSrc.tinaEditorGuidesLineWidth;

    tinaMutaGenMutationTypesUser1 = pSrc.tinaMutaGenMutationTypesUser1;
    tinaMutaGenMutationTypesUser2 = pSrc.tinaMutaGenMutationTypesUser2;
    tinaMutaGenMutationTypesUser3 = pSrc.tinaMutaGenMutationTypesUser3;

    tinaMutaGenMutationTypeHoriz1 = pSrc.tinaMutaGenMutationTypeHoriz1;
    tinaMutaGenMutationTypeHoriz2 = pSrc.tinaMutaGenMutationTypeHoriz2;
    tinaMutaGenMutationTypeVert1 = pSrc.tinaMutaGenMutationTypeVert1;
    tinaMutaGenMutationTypeVert2 = pSrc.tinaMutaGenMutationTypeVert2;

    tinaDefaultFadeToWhiteLevel = pSrc.tinaDefaultFadeToWhiteLevel;
    tinaEditorDoubleClickAction = pSrc.tinaEditorDoubleClickAction;

    tinaDefaultFPS = pSrc.tinaDefaultFPS;
    tinaDefaultFrameCount = pSrc.tinaDefaultFrameCount;
    tinaRawMotionDataPath = pSrc.tinaRawMotionDataPath;
    tinaDefaultSpatialOversampling = pSrc.tinaDefaultSpatialOversampling;
    tinaDefaultSpatialFilterKernel = pSrc.tinaDefaultSpatialFilterKernel;
    tinaDefaultSpatialFilterRadius = pSrc.tinaDefaultSpatialFilterRadius;
    tinaDefaultFilterVisualisationFlat = pSrc.tinaDefaultFilterVisualisationFlat;
    tinaDefaultForegroundOpacity = pSrc.tinaDefaultForegroundOpacity;
    tinaDefaultPostNoiseFilter = pSrc.tinaDefaultPostNoiseFilter;
    tinaDefaultPostNoiseFilterThreshold = pSrc.tinaDefaultPostNoiseFilterThreshold;
    tinaDefaultPostOptiXDenoiser = pSrc.tinaDefaultPostOptiXDenoiser;
    tinaDefaultPostOptiXDenoiserBlend = pSrc.tinaDefaultPostOptiXDenoiserBlend;

    tinaAdvancedCodeEditor = pSrc.tinaAdvancedCodeEditor;
    tinaAdvancedCodeEditorColorFix = pSrc.tinaAdvancedCodeEditorColorFix;
    tinaAdvancedCodeEditorFontSize = pSrc.tinaAdvancedCodeEditorFontSize;
    tinaEditorProgressivePreview = pSrc.tinaEditorProgressivePreview;
    tinaEditorProgressivePreviewMaxRenderTime = pSrc.tinaEditorProgressivePreviewMaxRenderTime;
    tinaEditorProgressivePreviewMaxRenderQuality = pSrc.tinaEditorProgressivePreviewMaxRenderQuality;
    tinaFontScale = pSrc.tinaFontScale;
    showTipsAtStartup = pSrc.showTipsAtStartup;
    lastTip = pSrc.lastTip;
    tinaDisableSolidFlameRandGens = pSrc.tinaDisableSolidFlameRandGens;
    tinaDefaultExpandNonlinearParams = pSrc.tinaDefaultExpandNonlinearParams;
    tinaFACLRenderPath = pSrc.tinaFACLRenderPath;
    tinaFACLRenderOptions = pSrc.tinaFACLRenderOptions;

    resolutionProfiles.clear();
    for (ResolutionProfile profile : pSrc.resolutionProfiles) {
      resolutionProfiles.add((ResolutionProfile) profile.makeCopy());
    }

    qualityProfiles.clear();
    for (QualityProfile profile : pSrc.qualityProfiles) {
      qualityProfiles.add((QualityProfile) profile.makeCopy());
    }

    windowPrefs.clear();
    for (WindowPrefs prefs : pSrc.windowPrefs) {
      windowPrefs.add((WindowPrefs) prefs.makeCopy());
    }
    tinaExcludedVariations.clear();
    for (String variation : pSrc.getTinaExcludedVariations()) {
      tinaExcludedVariations.add(variation);
    }
    tinaLSystemMaxLength = pSrc.tinaLSystemMaxLength;

    createTinaDefaultMacroButtons = pSrc.createTinaDefaultMacroButtons;
    tinaMacroToolbarWidth = pSrc.tinaMacroToolbarWidth;
    tinaMacroButtons.clear();
    for (MacroButton macroButton : pSrc.tinaMacroButtons) {
      tinaMacroButtons.add((MacroButton) macroButton.makeCopy());
    }

    iflamesFlameLibraryPath = pSrc.iflamesFlameLibraryPath;
    iflamesImageLibraryPath = pSrc.iflamesImageLibraryPath;
    iflamesLoadLibraryAtStartup = pSrc.iflamesLoadLibraryAtStartup;
  }

  public int getTinaRenderThreads() {
    int numThreads = tinaRenderThreads - tinaPreserveFreeCPUs;
    return numThreads > 1 ? numThreads : 1;
  }

  public int getTinaRenderPreviewQuality() {
    return tinaRenderPreviewQuality;
  }

  public void setTinaRenderPreviewQuality(int tinaRenderPreviewQuality) {
    this.tinaRenderPreviewQuality = tinaRenderPreviewQuality;
  }

  public int getTinaRenderMovieFrames() {
    return tinaRenderMovieFrames;
  }

  public void setTinaRenderMovieFrames(int tinaRenderMovieFrames) {
    this.tinaRenderMovieFrames = tinaRenderMovieFrames;
  }

  public double getTinaRenderRealtimeQuality() {
    return tinaRenderRealtimeQuality;
  }

  public void setTinaRenderRealtimeQuality(double tinaRenderRealtimeQuality) {
    this.tinaRenderRealtimeQuality = tinaRenderRealtimeQuality;
  }

  public int getTinaRandomBatchSize() {
    return tinaRandomBatchSize;
  }

  public void setTinaRandomBatchSize(int tinaRandomBatchSize) {
    if(tinaRandomBatchSize<64) {
      this.tinaRandomBatchSize = tinaRandomBatchSize;
    }
  }

  public int getTinaInitialRandomBatchSize() {
    return tinaInitialRandomBatchSize;
  }

  public void setTinaInitialRandomBatchSize(int tinaInitialRandomBatchSize) {
    this.tinaInitialRandomBatchSize = tinaInitialRandomBatchSize;
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

  static {
    tinaRenderThreads = Runtime.getRuntime().availableProcessors();
    if (tinaRenderThreads < 1) {
      tinaRenderThreads = 1;
    }
  }

  public List<QualityProfile> getQualityProfiles() {
    return qualityProfiles;
  }

  public List<ResolutionProfile> getResolutionProfiles() {
    return resolutionProfiles;
  }

  public String getSoundFilePath() {
    return soundFilePath;
  }

  public void setSoundFilePath(String soundFilePath) {
    this.soundFilePath = soundFilePath;
  }

  public String getMovieFlamesPath() {
    return movieFlamesPath;
  }

  public void setMovieFlamesPath(String movieFlamesPath) {
    this.movieFlamesPath = movieFlamesPath;
  }

  public boolean isTinaAssociateProfilesWithFlames() {
    return tinaAssociateProfilesWithFlames;
  }

  public void setTinaAssociateProfilesWithFlames(boolean tinaAssociateProfilesWithFlames) {
    this.tinaAssociateProfilesWithFlames = tinaAssociateProfilesWithFlames;
  }

  public WindowPrefs getWindowPrefs(String pName) {
    for (WindowPrefs prefs : windowPrefs) {
      if (prefs.getName().equals(pName)) {
        return prefs;
      }
    }
    return null;
  }

  private Prefs() {
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_TINA));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_TINA_PREVIEW));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_FORMULAEXPLORER));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_IFLAMES));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_IMAGEPROCESSING));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_FLAMEBROWSER));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_EASYMOVIEMAKER));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_DANCINGFLAMES));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_MUTAGEN));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_HELP));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_INTERACTIVERENDERER));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_MESHGEN));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_BATCHFLAMERENDERER));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_PREFERENCES));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_SYSTEMINFO));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_WELCOME));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_TIPOFTHEDAY));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_LOOKANDFEEL));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_FLAMES_GPU));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_LIST_OF_CHANGES));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_GPU_RENDERING));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_OPTIX_DENOISER_INFO));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_VARIATIONSSETTINGS));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_QUILTFLAMERENDERER));
  }

  protected List<WindowPrefs> getWindowPrefs() {
    return windowPrefs;
  }

  public boolean isDevelopmentMode() {
    return developmentMode;
  }

  public void setDevelopmentMode(boolean developmentMode) {
    this.developmentMode = developmentMode;
  }

  public boolean isTinaDefaultBGTransparency() {
    return tinaDefaultBGTransparency;
  }

  public void setTinaDefaultBGTransparency(boolean tinaDefaultBGTransparency) {
    this.tinaDefaultBGTransparency = tinaDefaultBGTransparency;
  }

  public RandomGeneratorType getTinaRandomNumberGenerator() {
    return tinaRandomNumberGenerator;
  }

  public void setTinaRandomNumberGenerator(RandomGeneratorType pTinaRandomNumberGenerator) {
    tinaRandomNumberGenerator = pTinaRandomNumberGenerator;
  }

  public BaseMathLibType getBaseMathLibType() {
    return baseMathLibType;
  }

  public void setBaseMathLibType(BaseMathLibType baseMathLibType) {
    MathLib.setBaseMathLibType(baseMathLibType);
    this.baseMathLibType = baseMathLibType;
  }

  public RandomBatchRefreshType getTinaRandomBatchRefreshType() {
    return tinaRandomBatchRefreshType;
  }

  public void setTinaRandomBatchRefreshType(RandomBatchRefreshType tinaRandomBatchRefreshType) {
    this.tinaRandomBatchRefreshType = tinaRandomBatchRefreshType;
  }

  public String getTinaSVGPath() {
    return tinaSVGPath;
  }

  public void setTinaSVGPath(String tinaSVGPath) {
    this.tinaSVGPath = tinaSVGPath;
  }

  public boolean isTinaSaveHDRInIR() {
    return tinaSaveHDRInIR;
  }

  public void setTinaSaveHDRInIR(boolean tinaSaveHDRInIR) {
    this.tinaSaveHDRInIR = tinaSaveHDRInIR;
  }

  public boolean isTinaSaveFlamesWhenImageIsSaved() {
    return tinaSaveFlamesWhenImageIsSaved;
  }

  public void setTinaSaveFlamesWhenImageIsSaved(boolean tinaSaveFlamesWhenImageIsSaved) {
    this.tinaSaveFlamesWhenImageIsSaved = tinaSaveFlamesWhenImageIsSaved;
  }

  public int getTinaPreserveFreeCPUs() {
    return tinaPreserveFreeCPUs;
  }

  public void setTinaPreserveFreeCPUs(int tinaPreserveFreeCPUs) {
    this.tinaPreserveFreeCPUs = tinaPreserveFreeCPUs;
  }

  public boolean isTinaDisableWikimediaCommonsWarning() {
    return tinaDisableWikimediaCommonsWarning;
  }

  public void setTinaDisableWikimediaCommonsWarning(boolean tinaDisableWikimediaCommonsWarning) {
    this.tinaDisableWikimediaCommonsWarning = tinaDisableWikimediaCommonsWarning;
  }

  public int getTinaRandomMovieBatchSize() {
    return tinaRandomMovieBatchSize;
  }

  public void setTinaRandomMovieBatchSize(int pTinaRandomMovieBatchSize) {
    tinaRandomMovieBatchSize = pTinaRandomMovieBatchSize;
  }

  public LookAndFeelType getLookAndFeelType() {
    return lookAndFeelType;
  }

  public void setLookAndFeelType(LookAndFeelType pLookAndFeelType) {
    lookAndFeelType = pLookAndFeelType;
  }

  public String getLookAndFeelTheme() {
    return lookAndFeelTheme;
  }

  public void setLookAndFeelTheme(String pLookAndFeelTheme) {
    lookAndFeelTheme = pLookAndFeelTheme;
  }

  public double getTinaEditorGridSize() {
    return tinaEditorGridSize;
  }

  public void setTinaEditorGridSize(double pTinaEditorGridSize) {
    tinaEditorGridSize = pTinaEditorGridSize;
  }

  public boolean isTinaEditorControlsWithShadows() {
    return tinaEditorControlsWithShadows;
  }

  public void setTinaEditorControlsWithShadows(boolean pTinaEditorControlsWithShadows) {
    tinaEditorControlsWithShadows = pTinaEditorControlsWithShadows;
  }

  public boolean isTinaEditorControlsWithColor() {
    return tinaEditorControlsWithColor;
  }

  public void setTinaEditorControlsWithColor(boolean pTinaEditorControlsWithColor) {
    tinaEditorControlsWithColor = pTinaEditorControlsWithColor;
  }

  public boolean isTinaEditorControlsWithAntialiasing() {
    return tinaEditorControlsWithAntialiasing;
  }

  public void setTinaEditorControlsWithAntialiasing(boolean pTinaEditorControlsWithAntialiasing) {
    tinaEditorControlsWithAntialiasing = pTinaEditorControlsWithAntialiasing;
  }

  public boolean isTinaEditorControlsWithNumbers() {
    return tinaEditorControlsWithNumbers;
  }

  public void setTinaEditorControlsWithNumbers(boolean pTinaEditorControlsWithNumbers) {
    tinaEditorControlsWithNumbers = pTinaEditorControlsWithNumbers;
  }

  public String getTinaRandGenColorMapImagePath() {
    return tinaRandGenColorMapImagePath;
  }

  public void setTinaRandGenColorMapImagePath(String pTinaRandGenColorMapImagePath) {
    tinaRandGenColorMapImagePath = pTinaRandGenColorMapImagePath;
  }

  private static Prefs prefs;

  public static Prefs getPrefs() {
    if (prefs == null) {
      Prefs p = new Prefs();
      try {
        p.loadFromFile();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      prefs = p;
    }
    return prefs;
  }

  public FlamePanelControlStyle getTinaEditorControlsStyle() {
    return tinaEditorControlsStyle;
  }

  public void setTinaEditorControlsStyle(FlamePanelControlStyle pTinaEditorControlsStyle) {
    tinaEditorControlsStyle = pTinaEditorControlsStyle;
  }

  public String getTinaMeshPath() {
    return tinaMeshPath;
  }

  public void setTinaMeshPath(String pTinaMeshPath) {
    tinaMeshPath = pTinaMeshPath;
  }

  public boolean isTinaOptimizedRenderingIR() {
    return tinaOptimizedRenderingIR;
  }

  public void setTinaOptimizedRenderingIR(boolean pTinaOptimizedRenderingIR) {
    tinaOptimizedRenderingIR = pTinaOptimizedRenderingIR;
  }

  public static Prefs newInstance() {
    return new Prefs();
  }

  public Color getTinaEditorGuidesCenterPointColor() {
    return tinaEditorGuidesCenterPointColor;
  }

  public void setTinaEditorGuidesCenterPointColor(Color pTinaEditorGuidesCenterPointColor) {
    tinaEditorGuidesCenterPointColor = pTinaEditorGuidesCenterPointColor;
  }

  public Color getTinaEditorGuidesRuleOfThirdsColor() {
    return tinaEditorGuidesRuleOfThirdsColor;
  }

  public void setTinaEditorGuidesRuleOfThirdsColor(Color pTinaEditorGuidesRuleOfThirdsColor) {
    tinaEditorGuidesRuleOfThirdsColor = pTinaEditorGuidesRuleOfThirdsColor;
  }

  public Color getTinaEditorGuidesGoldenRatioColor() {
    return tinaEditorGuidesGoldenRatioColor;
  }

  public void setTinaEditorGuidesGoldenRatioColor(Color pTinaEditorGuidesGoldenRatioColor) {
    tinaEditorGuidesGoldenRatioColor = pTinaEditorGuidesGoldenRatioColor;
  }

  public double getTinaEditorGuidesLineWidth() {
    return tinaEditorGuidesLineWidth;
  }

  public void setTinaEditorGuidesLineWidth(double pTinaEditorGuidesLineWidth) {
    tinaEditorGuidesLineWidth = pTinaEditorGuidesLineWidth;
  }

  public List<MacroButton> getTinaMacroButtons() {
    return tinaMacroButtons;
  }

  public boolean isCreateTinaDefaultMacroButtons() {
    return createTinaDefaultMacroButtons;
  }

  public void setCreateTinaDefaultMacroButtons(boolean createTinaDefaultMacroButtons) {
    this.createTinaDefaultMacroButtons = createTinaDefaultMacroButtons;
  }

  public int getTinaMacroToolbarWidth() {
    return tinaMacroToolbarWidth;
  }

  public void setTinaMacroToolbarWidth(int tinaMacroToolbarWidth) {
    this.tinaMacroToolbarWidth = tinaMacroToolbarWidth;
    if (this.tinaMacroToolbarWidth < 16)
      this.tinaMacroToolbarWidth = 16;
  }

  public boolean isTinaMacroButtonsVertical() {
    return tinaMacroButtonsVertical;
  }

  public void setTinaMacroButtonsVertical(boolean tinaMacroButtonsVertical) {
    this.tinaMacroButtonsVertical = tinaMacroButtonsVertical;
  }

  public int getTinaMacroToolbarHeight() {
    return tinaMacroToolbarHeight;
  }

  public void setTinaMacroToolbarHeight(int tinaMacroToolbarHeight) {
    this.tinaMacroToolbarHeight = tinaMacroToolbarHeight;
  }

  private boolean hasCheckedExperimentalMode = false;
  private boolean allowExperimentalFeatures = false;

  public boolean isAllowExperimentalFeatures() {
    if (!hasCheckedExperimentalMode) {
      hasCheckedExperimentalMode = true;
      try {
        String username = System.getProperty("user.name");
        String host = InetAddress.getLocalHost().getHostName();
        allowExperimentalFeatures = username.toLowerCase().contains("thargor") && host.toLowerCase().contains("thargor") && isDevelopmentMode();
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
    }
    return allowExperimentalFeatures;
  }

  public String getTinaRandGenDualityPreferedVariation() {
    return tinaRandGenDualityPreferedVariation;
  }

  public void setTinaRandGenDualityPreferedVariation(String pTinaRandGenDualityPreferedVariation) {
    tinaRandGenDualityPreferedVariation = pTinaRandGenDualityPreferedVariation;
  }

  public double getTinaRandGenDualityPreferedVariationProbability1() {
    return tinaRandGenDualityPreferedVariationProbability1;
  }

  public void setTinaRandGenDualityPreferedVariationProbability1(double pTinaRandGenDualityPreferedVariationProbability1) {
    tinaRandGenDualityPreferedVariationProbability1 = pTinaRandGenDualityPreferedVariationProbability1;
  }

  public double getTinaRandGenDualityPreferedVariationProbability2() {
    return tinaRandGenDualityPreferedVariationProbability2;
  }

  public void setTinaRandGenDualityPreferedVariationProbability2(double pTinaRandGenDualityPreferedVariationProbability2) {
    tinaRandGenDualityPreferedVariationProbability2 = pTinaRandGenDualityPreferedVariationProbability2;
  }

  public String getTinaMutaGenMutationTypesUser1() {
    return tinaMutaGenMutationTypesUser1;
  }

  public void setTinaMutaGenMutationTypesUser1(String pTinaMutaGenMutationTypesUser1) {
    tinaMutaGenMutationTypesUser1 = pTinaMutaGenMutationTypesUser1;
  }

  public String getTinaMutaGenMutationTypesUser2() {
    return tinaMutaGenMutationTypesUser2;
  }

  public void setTinaMutaGenMutationTypesUser2(String pTinaMutaGenMutationTypesUser2) {
    tinaMutaGenMutationTypesUser2 = pTinaMutaGenMutationTypesUser2;
  }

  public String getTinaMutaGenMutationTypesUser3() {
    return tinaMutaGenMutationTypesUser3;
  }

  public void setTinaMutaGenMutationTypesUser3(String pTinaMutaGenMutationTypesUser3) {
    tinaMutaGenMutationTypesUser3 = pTinaMutaGenMutationTypesUser3;
  }

  public String getTinaMutaGenMutationTypeHoriz1() {
    return tinaMutaGenMutationTypeHoriz1;
  }

  public void setTinaMutaGenMutationTypeHoriz1(String pTinaMutaGenMutationTypeHoriz1) {
    tinaMutaGenMutationTypeHoriz1 = pTinaMutaGenMutationTypeHoriz1;
  }

  public String getTinaMutaGenMutationTypeHoriz2() {
    return tinaMutaGenMutationTypeHoriz2;
  }

  public void setTinaMutaGenMutationTypeHoriz2(String pTinaMutaGenMutationTypeHoriz2) {
    tinaMutaGenMutationTypeHoriz2 = pTinaMutaGenMutationTypeHoriz2;
  }

  public String getTinaMutaGenMutationTypeVert1() {
    return tinaMutaGenMutationTypeVert1;
  }

  public void setTinaMutaGenMutationTypeVert1(String pTinaMutaGenMutationTypeVert1) {
    tinaMutaGenMutationTypeVert1 = pTinaMutaGenMutationTypeVert1;
  }

  public String getTinaMutaGenMutationTypeVert2() {
    return tinaMutaGenMutationTypeVert2;
  }

  public void setTinaMutaGenMutationTypeVert2(String pTinaMutaGenMutationTypeVert2) {
    tinaMutaGenMutationTypeVert2 = pTinaMutaGenMutationTypeVert2;
  }

  public String getIflamesFlameLibraryPath() {
    return iflamesFlameLibraryPath;
  }

  public void setIflamesFlameLibraryPath(String pIflamesFlameLibraryPath) {
    iflamesFlameLibraryPath = pIflamesFlameLibraryPath;
  }

  public String getIflamesImageLibraryPath() {
    return iflamesImageLibraryPath;
  }

  public void setIflamesImageLibraryPath(String pIflamesImageLibraryPath) {
    iflamesImageLibraryPath = pIflamesImageLibraryPath;
  }

  public boolean isIflamesLoadLibraryAtStartup() {
    return iflamesLoadLibraryAtStartup;
  }

  public void setIflamesLoadLibraryAtStartup(boolean pIflamesLoadLibraryAtStartup) {
    iflamesLoadLibraryAtStartup = pIflamesLoadLibraryAtStartup;
  }

  public String getThumbnailPath() {
    return thumbnailPath;
  }

  public void setThumbnailPath(String pThumbnailPath) {
    thumbnailPath = pThumbnailPath;
  }

  public boolean isTinaFreeCacheInBatchRenderer() {
    return tinaFreeCacheInBatchRenderer;
  }

  public void setTinaFreeCacheInBatchRenderer(boolean pTinaFreeCacheInBatchRenderer) {
    tinaFreeCacheInBatchRenderer = pTinaFreeCacheInBatchRenderer;
  }

  public EditorDoubleClickActionType getTinaEditorDoubleClickAction() {
    return tinaEditorDoubleClickAction;
  }

  public void setTinaEditorDoubleClickAction(EditorDoubleClickActionType pTinaEditorDoubleClickAction) {
    tinaEditorDoubleClickAction = pTinaEditorDoubleClickAction;
  }

  public double getTinaDefaultFadeToWhiteLevel() {
    return tinaDefaultFadeToWhiteLevel;
  }

  public void setTinaDefaultFadeToWhiteLevel(double pTinaDefaultFadeToWhiteLevel) {
    tinaDefaultFadeToWhiteLevel = pTinaDefaultFadeToWhiteLevel;
  }

  public int getTinaDefaultFPS() {
    return tinaDefaultFPS;
  }

  public void setTinaDefaultFPS(int pTinaDefaultFPS) {
    tinaDefaultFPS = pTinaDefaultFPS;
  }

  public String getTinaRawMotionDataPath() {
    return tinaRawMotionDataPath;
  }

  public void setTinaRawMotionDataPath(String pTinaRawMotionDataPath) {
    tinaRawMotionDataPath = pTinaRawMotionDataPath;
  }

  public int getTinaDefaultSpatialOversampling() {
    return tinaDefaultSpatialOversampling;
  }

  public void setTinaDefaultSpatialOversampling(int pTinaDefaultSpatialOversampling) {
    tinaDefaultSpatialOversampling = pTinaDefaultSpatialOversampling;
    if (tinaDefaultSpatialOversampling < 1) {
      tinaDefaultSpatialOversampling = 1;
    }
    else if (tinaDefaultSpatialOversampling > Tools.MAX_SPATIAL_OVERSAMPLING) {
      tinaDefaultSpatialOversampling = Tools.MAX_SPATIAL_OVERSAMPLING;
    }
  }

  public FilterKernelType getTinaDefaultSpatialFilterKernel() {
    return tinaDefaultSpatialFilterKernel;
  }

  public void setTinaDefaultSpatialFilterKernel(FilterKernelType pTinaDefaultSpatialFilterKernel) {
    tinaDefaultSpatialFilterKernel = pTinaDefaultSpatialFilterKernel;
  }

  public double getTinaDefaultSpatialFilterRadius() {
    return tinaDefaultSpatialFilterRadius;
  }

  public void setTinaDefaultSpatialFilterRadius(double pTinaDefaultSpatialFilterRadius) {
    tinaDefaultSpatialFilterRadius = pTinaDefaultSpatialFilterRadius;
  }

  public boolean isTinaDefaultFilterVisualisationFlat() {
    return tinaDefaultFilterVisualisationFlat;
  }

  public void setTinaDefaultFilterVisualisationFlat(boolean pTinaDefaultFilterVisualisationFlat) {
    tinaDefaultFilterVisualisationFlat = pTinaDefaultFilterVisualisationFlat;
  }

  public boolean isTinaDefaultPostNoiseFilter() {
    return tinaDefaultPostNoiseFilter;
  }

  public void setTinaDefaultPostNoiseFilter(boolean pTinaDefaultPostNoiseFilter) {
    tinaDefaultPostNoiseFilter = pTinaDefaultPostNoiseFilter;
  }

  public double getTinaDefaultPostNoiseFilterThreshold() {
    return tinaDefaultPostNoiseFilterThreshold;
  }

  public void setTinaDefaultPostNoiseFilterThreshold(double pTinaDefaultPostNoiseFilterThreshold) {
    tinaDefaultPostNoiseFilterThreshold = pTinaDefaultPostNoiseFilterThreshold;
  }

  public boolean isTinaDefaultPostOptiXDenoiser() {
    return tinaDefaultPostOptiXDenoiser;
  }

  public void setTinaDefaultPostOptiXDenoiser(boolean tinaDefaultPostOptiXDenoiser) {
    this.tinaDefaultPostOptiXDenoiser = tinaDefaultPostOptiXDenoiser;
  }

  public double getTinaDefaultPostOptiXDenoiserBlend() {
    return tinaDefaultPostOptiXDenoiserBlend;
  }

  public void setTinaDefaultPostOptiXDenoiserBlend(double tinaDefaultPostOptiXDenoiserBlend) {
    this.tinaDefaultPostOptiXDenoiserBlend = tinaDefaultPostOptiXDenoiserBlend;
  }


  public double getTinaDefaultForegroundOpacity() {
    return tinaDefaultForegroundOpacity;
  }

  public void setTinaDefaultForegroundOpacity(double pTinaDefaultForegroundOpacity) {
    tinaDefaultForegroundOpacity = pTinaDefaultForegroundOpacity;
  }

  public RasterCreator getTinaRasterType() {
    return tinaRasterType;
  }

  public void setTinaRasterType(RasterCreator pTinaRasterType) {
    tinaRasterType = pTinaRasterType;
  }

  public double getTinaDefaultAntialiasingAmount() {
    return tinaDefaultAntialiasingAmount;
  }

  public void setTinaDefaultAntialiasingAmount(double pTinaDefaultAntialiasingAmount) {
    tinaDefaultAntialiasingAmount = pTinaDefaultAntialiasingAmount;
  }

  public double getTinaDefaultAntialiasingRadius() {
    return tinaDefaultAntialiasingRadius;
  }

  public void setTinaDefaultAntialiasingRadius(double pTinaDefaultAntialiasingRadius) {
    tinaDefaultAntialiasingRadius = pTinaDefaultAntialiasingRadius;
  }

  public boolean isTinaAdvancedCodeEditor() {
    return tinaAdvancedCodeEditor;
  }

  public void setTinaAdvancedCodeEditor(boolean pTinaAdvancedCodeEditor) {
    tinaAdvancedCodeEditor = pTinaAdvancedCodeEditor;
  }

  public boolean isTinaAdvancedCodeEditorColorFix() {
    return tinaAdvancedCodeEditorColorFix;
  }

  public void setTinaAdvancedCodeEditorColorFix(boolean pTinaAdvancedCodeEditorColorFix) {
    tinaAdvancedCodeEditorColorFix = pTinaAdvancedCodeEditorColorFix;
  }

  public int getTinaAdvancedCodeEditorFontSize() {
    return tinaAdvancedCodeEditorFontSize;
  }

  public void setTinaAdvancedCodeEditorFontSize(int font_size) {
    if (font_size <= 8) {
      font_size = 10;
    }
    tinaAdvancedCodeEditorFontSize = font_size;
  }

  public boolean isTinaEditorProgressivePreview() {
    return tinaEditorProgressivePreview;
  }

  public void setTinaEditorProgressivePreview(boolean pTinaEditorProgressivePreview) {
    tinaEditorProgressivePreview = pTinaEditorProgressivePreview;
  }

  public double getTinaEditorProgressivePreviewMaxRenderTime() {
    return tinaEditorProgressivePreviewMaxRenderTime;
  }

  public void setTinaEditorProgressivePreviewMaxRenderTime(double pTinaEditorProgressivePreviewMaxRenderTime) {
    tinaEditorProgressivePreviewMaxRenderTime = pTinaEditorProgressivePreviewMaxRenderTime;
  }

  public double getTinaEditorProgressivePreviewMaxRenderQuality() {
    return tinaEditorProgressivePreviewMaxRenderQuality;
  }

  public void setTinaEditorProgressivePreviewMaxRenderQuality(double pTinaEditorProgressivePreviewMaxRenderQuality) {
    tinaEditorProgressivePreviewMaxRenderQuality = pTinaEditorProgressivePreviewMaxRenderQuality;
  }

  public String getTinaCustomVariationsPath() {
    return tinaCustomVariationsPath;
  }

  public void setTinaCustomVariationsPath(String pTinaCustomVariationsPath) {
    tinaCustomVariationsPath = pTinaCustomVariationsPath;
  }

  public Font getFont(String pName, int pStyle, int pSize) {
    int scaledSize = (int) (pSize * tinaFontScale);
    if (scaledSize < 8) {
      scaledSize = 8;
    }
    else if (scaledSize > 64) {
      scaledSize = 64;
    }
    return new Font(pName, pStyle, scaledSize);
  }

  public double getTinaFontScale() {
    return tinaFontScale;
  }

  public void setTinaFontScale(double pTinaFontScale) {
    tinaFontScale = pTinaFontScale;
  }

  public boolean isShowTipsAtStartup() {
    return showTipsAtStartup;
  }

  public void setShowTipsAtStartup(boolean showTipsAtStartup) {
    this.showTipsAtStartup = showTipsAtStartup;
  }
  
  public int getLastTip() {
    return lastTip;
  }
  
  public void setLastTip(int pLastTip) {
    lastTip = pLastTip;
  }

  public boolean isTinaDisableSolidFlameRandGens() {
    return tinaDisableSolidFlameRandGens;
  }

  public void setTinaDisableSolidFlameRandGens(boolean tinaDisableSolidFlameRandGens) {
    this.tinaDisableSolidFlameRandGens = tinaDisableSolidFlameRandGens;
  }

  public boolean isTinaAutoloadSavedImagesInIR() {
    return tinaAutoloadSavedImagesInIR;
  }

  public void setTinaAutoloadSavedImagesInIR(boolean tinaAutoloadSavedImagesInIR) {
    this.tinaAutoloadSavedImagesInIR = tinaAutoloadSavedImagesInIR;
  }

  public boolean isTinaDefaultExpandNonlinearParams() {
    return tinaDefaultExpandNonlinearParams;
  }

  public void setTinaDefaultExpandNonlinearParams(boolean tinaDefaultExpandNonlinearParams) {
    this.tinaDefaultExpandNonlinearParams = tinaDefaultExpandNonlinearParams;
  }

  public String getTinaFACLRenderPath() {
    return tinaFACLRenderPath;
  }

  public void setTinaFACLRenderPath(String tinaFACLRenderPath) {
    this.tinaFACLRenderPath = tinaFACLRenderPath;
  }

  public String getTinaFACLRenderOptions() {
    return tinaFACLRenderOptions;
  }

  public void setTinaFACLRenderOptions(String pTinaFACLRenderOptions) {
    tinaFACLRenderOptions = pTinaFACLRenderOptions;
  }

  public void setTinaExcludedVariations(List<String> pTinaExcludedVariations) {
    tinaExcludedVariations.clear();
    if (pTinaExcludedVariations != null) {
      tinaExcludedVariations.addAll(pTinaExcludedVariations);
    }
  }

  public List<String> getTinaExcludedVariations() {
    return tinaExcludedVariations;
  }
  
  public int getTinaLSystemMaxLength() {
    return tinaLSystemMaxLength;
  }
  
  public void setTinaLSystemMaxLength(int l) {
    tinaLSystemMaxLength = l;
  }

  public int getTinaDefaultFrameCount() {
    return tinaDefaultFrameCount;
  }

  public void setTinaDefaultFrameCount(int tinaDefaultFrameCount) {
    this.tinaDefaultFrameCount = tinaDefaultFrameCount;
  }

  public double getTinaRenderAnimPreviewQuality() {
    return tinaRenderAnimPreviewQuality;
  }

  public void setTinaRenderAnimPreviewQuality(double tinaRenderAnimPreviewQuality) {
    this.tinaRenderAnimPreviewQuality = tinaRenderAnimPreviewQuality;
  }

  public double getTinaRenderAnimPreviewSize() {
    return tinaRenderAnimPreviewSize;
  }

  public void setTinaRenderAnimPreviewSize(double tinaRenderAnimPreviewSize) {
    this.tinaRenderAnimPreviewSize = tinaRenderAnimPreviewSize;
  }

  public boolean isTinaDefaultAnimationControlsEnabled() {
    return tinaDefaultAnimationControlsEnabled;
  }

  public void setTinaDefaultAnimationControlsEnabled(boolean tinaDefaultAnimationControlsEnabled) {
    this.tinaDefaultAnimationControlsEnabled = tinaDefaultAnimationControlsEnabled;
  }
}
