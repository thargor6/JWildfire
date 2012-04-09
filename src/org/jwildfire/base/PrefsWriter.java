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

public class PrefsWriter {

  public void writePrefs(Prefs pPrefs) throws Exception {
    StringBuilder sb = new StringBuilder();
    addValue(sb, Prefs.KEY_GENERAL_PLAF_STYLE, pPrefs.getPlafStyle());
    addValue(sb, Prefs.KEY_GENERAL_PLAF_THEME, pPrefs.getPlafTheme());
    addValue(sb, Prefs.KEY_GENERAL_PATH_IMAGES, pPrefs.getImagePath());
    addValue(sb, Prefs.KEY_GENERAL_PATH_SCRIPTS, pPrefs.getScriptPath());
    addValue(sb, Prefs.KEY_TINA_PATH_FLAMES, pPrefs.getTinaFlamePath());
    addValue(sb, Prefs.KEY_TINA_RENDER_IMAGE_WIDTH, pPrefs.getTinaRenderImageWidth());
    addValue(sb, Prefs.KEY_TINA_RENDER_IMAGE_HEIGHT, pPrefs.getTinaRenderImageHeight());
    addValue(sb, Prefs.KEY_TINA_RENDER_MOVIE_FRAMES, pPrefs.getTinaRenderMovieFrames());
    addValue(sb, Prefs.KEY_TINA_RENDER_PREVIEW_QUALITY, pPrefs.getTinaRenderPreviewQuality());
    addValue(sb, Prefs.KEY_TINA_RENDER_REALTIME_QUALITY, pPrefs.getTinaRenderRealtimeQuality());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_SIZE, pPrefs.getTinaRandomBatchSize());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_RED, pPrefs.getTinaRandomBatchBGColorRed());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_GREEN, pPrefs.getTinaRandomBatchBGColorGreen());
    addValue(sb, Prefs.KEY_TINA_RANDOMBATCH_BGCOLOR_BLUE, pPrefs.getTinaRandomBatchBGColorBlue());
    addValue(sb, Prefs.KEY_SUNFLOW_PATH_SCENES, pPrefs.getSunflowScenePath());
    // Resolution profiles
    addValue(sb, Prefs.KEY_TINA_PROFILE_RESOLUTION_COUNT, pPrefs.getResolutionProfiles().size());
    for (int i = 0; i < pPrefs.getResolutionProfiles().size(); i++) {
      ResolutionProfile profile = pPrefs.getResolutionProfiles().get(i);
      addValue(sb, Prefs.KEY_TINA_PROFILE_RESOLUTION_DEFAULT_PROFILE + "." + i, profile.isDefaultProfile());
      addValue(sb, Prefs.KEY_TINA_PROFILE_RESOLUTION_WIDTH + "." + i, profile.getWidth());
      addValue(sb, Prefs.KEY_TINA_PROFILE_RESOLUTION_HEIGHT + "." + i, profile.getHeight());
    }
    // Quality profiles
    addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_COUNT, pPrefs.getQualityProfiles().size());
    for (int i = 0; i < pPrefs.getQualityProfiles().size(); i++) {
      QualityProfile profile = pPrefs.getQualityProfiles().get(i);
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_DEFAULT_PROFILE + "." + i, profile.isDefaultProfile());
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_CAPTION + "." + i, profile.getCaption());
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_SPATIAL_OVERSAMPLE + "." + i, profile.getSpatialOversample());
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_COLOR_OVERSAMPLE + "." + i, profile.getColorOversample());
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_QUALITY + "." + i, profile.getQuality());
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_WITH_HDR + "." + i, profile.isWithHDR());
      addValue(sb, Prefs.KEY_TINA_PROFILE_QUALITY_WITH_HDR_INTENSITY_MAP + "." + i, profile.isWithHDRIntensityMap());
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

}
