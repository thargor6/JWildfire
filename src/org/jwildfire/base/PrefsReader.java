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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

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
        pPrefs.setPlafStyle(getProperty(props, Prefs.KEY_GENERAL_PLAF_STYLE, pPrefs.getPlafStyle()));
        pPrefs.setPlafTheme(getProperty(props, Prefs.KEY_GENERAL_PLAF_THEME, pPrefs.getPlafTheme()));
        pPrefs.setImagePath(getProperty(props, Prefs.KEY_GENERAL_PATH_IMAGES, pPrefs.getImagePath()));
        pPrefs.setScriptPath(getProperty(props, Prefs.KEY_GENERAL_PATH_SCRIPTS, pPrefs.getScriptPath()));

        pPrefs.setTinaFlamePath(getProperty(props, Prefs.KEY_TINA_PATH_FLAMES, pPrefs.getTinaFlamePath()));
        pPrefs.setTinaRenderMovieFrames(getIntProperty(props, Prefs.KEY_TINA_RENDER_MOVIE_FRAMES, pPrefs.getTinaRenderMovieFrames()));
        pPrefs.setTinaRenderPreviewQuality(getIntProperty(props, Prefs.KEY_TINA_RENDER_PREVIEW_QUALITY, pPrefs.getTinaRenderPreviewQuality()));
        pPrefs.setTinaRenderRealtimeQuality(getIntProperty(props, Prefs.KEY_TINA_RENDER_REALTIME_QUALITY, pPrefs.getTinaRenderRealtimeQuality()));
        pPrefs.setTinaRandomBatchSize(getIntProperty(props, Prefs.KEY_TINA_RANDOMBATCH_SIZE, pPrefs.getTinaRandomBatchSize()));
        pPrefs.setTinaRandomBatchBGColorRed(getIntProperty(props, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_RED, pPrefs.getTinaRandomBatchBGColorRed()));
        pPrefs.setTinaRandomBatchBGColorGreen(getIntProperty(props, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_GREEN, pPrefs.getTinaRandomBatchBGColorGreen()));
        pPrefs.setTinaRandomBatchBGColorBlue(getIntProperty(props, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_BLUE, pPrefs.getTinaRandomBatchBGColorBlue()));
        // resolution profiles
        {
          int count = getIntProperty(props, Prefs.KEY_TINA_PROFILE_RESOLUTION_COUNT, 0);
          for (int i = 0; i < count; i++) {
            ResolutionProfile profile = new ResolutionProfile();
            pPrefs.getResolutionProfiles().add(profile);
            profile.setDefaultProfile(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_RESOLUTION_DEFAULT_PROFILE + "." + i, false));
            profile.setWidth(getIntProperty(props, Prefs.KEY_TINA_PROFILE_RESOLUTION_WIDTH + "." + i, 0));
            profile.setHeight(getIntProperty(props, Prefs.KEY_TINA_PROFILE_RESOLUTION_HEIGHT + "." + i, 0));
          }
        }
        // quality profiles
        {
          int count = getIntProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_COUNT, 0);
          for (int i = 0; i < count; i++) {
            QualityProfile profile = new QualityProfile();
            pPrefs.getQualityProfiles().add(profile);
            profile.setDefaultProfile(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_DEFAULT_PROFILE + "." + i, false));
            profile.setSpatialOversample(getIntProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_SPATIAL_OVERSAMPLE + "." + i, 1));
            profile.setColorOversample(getIntProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_COLOR_OVERSAMPLE + "." + i, 1));
            profile.setQuality(getIntProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_QUALITY + "." + i, 1));
            profile.setWithHDR(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_WITH_HDR + "." + i, false));
            profile.setWithHDRIntensityMap(getBooleanProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_WITH_HDR_INTENSITY_MAP + "." + i, false));
            profile.setCaption(getProperty(props, Prefs.KEY_TINA_PROFILE_QUALITY_CAPTION + "." + i, ""));
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
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "Very low quality", 1, 1, 200, false, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(true, "Low quality", 1, 2, 300, false, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "Medium quality", 1, 3, 500, false, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "High quality", 1, 3, 800, true, false));
      pPrefs.getQualityProfiles().add(new QualityProfile(false, "Very high quality", 2, 4, 1000, true, true));
    }
  }

}
