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
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.render.CRendererInterface;
import org.jwildfire.create.tina.render.RendererType;
import org.jwildfire.swing.LookAndFeel;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class Prefs extends ManagedObject {
  // DON'T forget to update the assign() method after adding new properties!!!
  static final String PREFS_FILE = "j-wildfire.properties";

  static final String KEY_GENERAL_PLAF_STYLE = "general.plaf.style2";
  static final String KEY_GENERAL_PLAF_THEME = "general.plaf.theme";
  static final String KEY_GENERAL_PATH_IMAGES = "general.path.images";
  static final String KEY_GENERAL_PATH_SCRIPTS = "general.path.scripts";
  static final String KEY_GENERAL_PATH_SOUND_FILES = "sunflow.path.sound_files";
  static final String KEY_GENERAL_PATH_SWF = "sunflow.path.swf";

  static final String KEY_GENERAL_DEVELOPMENT_MODE = "general.development_mode";

  static final String KEY_SUNFLOW_PATH_SCENES = "sunflow.path.scenes";

  static final String KEY_TINA_PROFILE_RESOLUTION_COUNT = "tina.profile.resolution.count";
  static final String KEY_TINA_PROFILE_RESOLUTION_WIDTH = "tina.profile.resolution.width";
  static final String KEY_TINA_PROFILE_RESOLUTION_HEIGHT = "tina.profile.resolution.height";
  static final String KEY_TINA_PROFILE_RESOLUTION_DEFAULT_PROFILE = "tina.profile.resolution.default_profile";

  static final String KEY_TINA_PROFILE_QUALITY_COUNT = "tina.profile.quality.count";
  static final String KEY_TINA_PROFILE_QUALITY_SPATIAL_OVERSAMPLE = "tina.profile.quality.spatial_oversample";
  static final String KEY_TINA_PROFILE_QUALITY_COLOR_OVERSAMPLE = "tina.profile.quality.color_oversample";
  static final String KEY_TINA_PROFILE_QUALITY_QUALITY = "tina.profile.quality.quality";
  static final String KEY_TINA_PROFILE_QUALITY_WITH_HDR = "tina.profile.quality.with_hdr";
  static final String KEY_TINA_PROFILE_QUALITY_WITH_HDR_INTENSITY_MAP = "tina.profile.quality.with_hdr_intensity_map";
  static final String KEY_TINA_PROFILE_QUALITY_CAPTION = "tina.profile.quality.caption";
  static final String KEY_TINA_PROFILE_QUALITY_DEFAULT_PROFILE = "tina.profile.quality.default_profile";

  static final String KEY_TINA_PATH_FLAMES = "tina.path.flames";
  static final String KEY_TINA_PATH_JWFMOVIES = "tina.path.jwfmovies";
  static final String KEY_TINA_RENDER_MOVIE_FRAMES = "tina.render.movie.frames";

  static final String KEY_TINA_RENDER_REALTIME_QUALITY = "tina.render.realtime.quality";
  static final String KEY_TINA_RENDER_PREVIEW_QUALITY = "tina.render.preview.quality";
  static final String KEY_TINA_RENDER_DEFAULT_RENDERER = "tina.render.default_renderer";
  static final String KEY_TINA_PROFILE_ASSOCIATE_WITH_FLAMES = "tina.profile.associate_with_flames";

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

  @Property(description = "Sound file drawer", category = PropertyCategory.GENERAL)
  private String soundFilePath = null;
  private String lastInputSoundFilePath = null;

  @Property(description = "SWF file drawer", category = PropertyCategory.GENERAL)
  private String swfPath = null;
  private String lastOutputSWFPath = null;

  @Property(description = "Flame file drawer", category = PropertyCategory.TINA)
  private String tinaFlamePath = null;
  private String lastInputFlamePath = null;
  private String lastOutputFlamePath = null;

  @Property(description = "JWFMovie file drawer", category = PropertyCategory.TINA)
  private String tinaJWFMoviePath = null;
  private String lastInputJWFMoviePath = null;
  private String lastOutputJWFMoviePath = null;

  @Property(description = "Associate profile information with flame files", category = PropertyCategory.TINA)
  private boolean tinaAssociateProfilesWithFlames = false;

  @Property(description = "Sunflow scene file drawer", category = PropertyCategory.SUNFLOW)
  private String sunflowScenePath = null;
  private String lastInputSunflowScenePath = null;
  private String lastOutputSunflowScenePath = null;

  @Property(description = "Development mode", category = PropertyCategory.GENERAL)
  private boolean developmentMode = false;

  private final List<QualityProfile> qualityProfiles = new ArrayList<QualityProfile>();
  private final List<ResolutionProfile> resolutionProfiles = new ArrayList<ResolutionProfile>();
  private final List<WindowPrefs> windowPrefs = new ArrayList<WindowPrefs>();

  public static class PLAFStyleEditor extends ComboBoxPropertyEditor {
    public PLAFStyleEditor() {
      super();
      setAvailableValues(new String[] { LookAndFeel.PLAF_AERO, LookAndFeel.PLAF_ALUMINIUM, LookAndFeel.PLAF_BERNSTEIN, LookAndFeel.PLAF_FAST, LookAndFeel.PLAF_GRAPHITE, LookAndFeel.PLAF_HIFI, LookAndFeel.PLAF_MCWIN, LookAndFeel.PLAF_MINT,
          LookAndFeel.PLAF_NIMBUS, LookAndFeel.PLAF_NOIRE, LookAndFeel.PLAF_LUNA, LookAndFeel.PLAF_SMART, LookAndFeel.PLAF_MAC });
    }
  }

  public static class TINARendererEditor extends ComboBoxPropertyEditor {
    public TINARendererEditor() {
      super();
      if (CRendererInterface.isCUDAAvailable()) {
        setAvailableValues(new RendererType[] { RendererType.JAVA, RendererType.C32, RendererType.C64 });
      }
      else {
        setAvailableValues(new RendererType[] { RendererType.JAVA });
      }
    }
  }

  @Property(description = "Look and feel (major UI style) - changes are applied only after restarting the main program", category = PropertyCategory.TINA, editorClass = PLAFStyleEditor.class)
  private String plafStyle = LookAndFeel.PLAF_NIMBUS;
  @Property(description = "Look and feel theme (UI sub style) - changes are applied only after restarting the main program", category = PropertyCategory.TINA)
  private String plafTheme = LookAndFeel.THEME_DEFAULT;

  @Property(description = "Default number of frames for a movie", category = PropertyCategory.TINA)
  private int tinaRenderMovieFrames = 90;

  private static int tinaRenderThreads;

  @Property(description = "Quality for realtime rendering (please restart app after changing this)", category = PropertyCategory.TINA)
  private int tinaRenderRealtimeQuality = 1;

  @Property(description = "Quality for preview rendering", category = PropertyCategory.TINA)
  private int tinaRenderPreviewQuality = 100;

  @Property(description = "Number of generated flames by invoking the \"Random flames\" function", category = PropertyCategory.TINA)
  private int tinaRandomBatchSize = 24;
  @Property(description = "Red component of the background color of randomly generated flames", category = PropertyCategory.TINA)
  private int tinaRandomBatchBGColorRed = 0;
  @Property(description = "Red component of the background color of randomly generated flames", category = PropertyCategory.TINA)
  private int tinaRandomBatchBGColorGreen = 0;
  @Property(description = "Red component of the background color of randomly generated flames", category = PropertyCategory.TINA)
  private int tinaRandomBatchBGColorBlue = 0;

  @Property(description = "Default renderer", category = PropertyCategory.TINA, editorClass = TINARendererEditor.class)
  private RendererType tinaDefaultRenderer = RendererType.JAVA;

  public String getInputScriptPath() {
    return lastInputScriptPath != null ? lastInputScriptPath : scriptPath;
  }

  public String getInputSoundFilePath() {
    return lastInputSoundFilePath != null ? lastInputSoundFilePath : soundFilePath;
  }

  public String getOutputScriptPath() {
    return lastOutputScriptPath != null ? lastOutputScriptPath : scriptPath;
  }

  public String getOutputSWFPath() {
    return lastOutputSWFPath != null ? lastOutputSWFPath : swfPath;
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

  public void setLastOutputSwfFile(File pFile) {
    lastOutputSWFPath = pFile.getParent();
    if (swfPath == null || swfPath.length() == 0) {
      swfPath = lastOutputSWFPath;
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
    scriptPath = pSrc.scriptPath;
    lastInputScriptPath = pSrc.lastInputScriptPath;
    lastOutputScriptPath = pSrc.lastOutputScriptPath;
    imagePath = pSrc.imagePath;
    lastInputImagePath = pSrc.lastInputImagePath;
    lastOutputImagePath = pSrc.lastOutputImagePath;
    tinaFlamePath = pSrc.tinaFlamePath;
    lastInputFlamePath = pSrc.lastInputFlamePath;
    lastOutputFlamePath = pSrc.lastOutputFlamePath;
    tinaJWFMoviePath = pSrc.tinaJWFMoviePath;
    lastInputJWFMoviePath = pSrc.lastInputJWFMoviePath;
    lastOutputJWFMoviePath = pSrc.lastOutputJWFMoviePath;
    sunflowScenePath = pSrc.sunflowScenePath;
    lastInputSunflowScenePath = pSrc.lastInputSunflowScenePath;
    lastOutputSunflowScenePath = pSrc.lastOutputSunflowScenePath;
    soundFilePath = pSrc.soundFilePath;
    lastInputSoundFilePath = pSrc.lastInputSoundFilePath;
    swfPath = pSrc.swfPath;
    lastOutputSWFPath = pSrc.lastOutputSWFPath;
    plafStyle = pSrc.plafStyle;
    plafTheme = pSrc.plafTheme;
    developmentMode = pSrc.developmentMode;

    tinaRenderMovieFrames = pSrc.tinaRenderMovieFrames;
    tinaRenderPreviewQuality = pSrc.tinaRenderPreviewQuality;
    tinaRenderRealtimeQuality = pSrc.tinaRenderRealtimeQuality;
    tinaRandomBatchSize = pSrc.tinaRandomBatchSize;
    tinaRandomBatchBGColorRed = pSrc.tinaRandomBatchBGColorRed;
    tinaRandomBatchBGColorGreen = pSrc.tinaRandomBatchBGColorGreen;
    tinaRandomBatchBGColorBlue = pSrc.tinaRandomBatchBGColorBlue;
    tinaAssociateProfilesWithFlames = pSrc.tinaAssociateProfilesWithFlames;
    tinaDefaultRenderer = pSrc.tinaDefaultRenderer;

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

  }

  public int getTinaRenderThreads() {
    return tinaRenderThreads;
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

  public int getTinaRenderRealtimeQuality() {
    return tinaRenderRealtimeQuality;
  }

  public void setTinaRenderRealtimeQuality(int tinaRenderRealtimeQuality) {
    this.tinaRenderRealtimeQuality = tinaRenderRealtimeQuality;
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

  public String getSwfPath() {
    return swfPath;
  }

  public void setSwfPath(String swfPath) {
    this.swfPath = swfPath;
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

  public Prefs() {
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_DESKTOP));
    windowPrefs.add(new WindowPrefs(WindowPrefs.WINDOW_TINA));
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

  public RendererType getTinaDefaultRenderer() {
    return tinaDefaultRenderer != null ? tinaDefaultRenderer : RendererType.JAVA;
  }

  public void setTinaDefaultRenderer(RendererType tinaDefaultRenderer) {
    this.tinaDefaultRenderer = tinaDefaultRenderer;
  }

}

/*
1920x1080 = 1080p
1280x720 = 720p
640x360 = 360p
*/