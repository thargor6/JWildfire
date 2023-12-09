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
package org.jwildfire.create.tina.farender;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.backdrop.FlameBackgroundHandler;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.launcher.StreamRedirector;
import org.jwildfire.transform.ComposeTransformer;

public class FARenderTools {
  private static boolean faRenderChecked = false;
  private static boolean faRenderAvalailable = false;

  private static String cudaLibrary= null;
  private static boolean cudaLibraryChecked = false;

  private static final String FARENDER_JWF_PATH = "FARenderJWF";


  private static final String FARENDER_EXE = "FARender.exe";

  private static void launchSync(String[] pCmd) {
    Runtime runtime = Runtime.getRuntime();
    try {
      runtime.exec(pCmd);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static int launchAsync(String pCmd, OutputStream pOS) {
    try {
      Runtime runtime = Runtime.getRuntime();
      Process proc = runtime.exec(pCmd);

      StreamRedirector outputStreamHandler = new StreamRedirector(proc.getInputStream(), pOS, false);
      StreamRedirector errorStreamHandler = new StreamRedirector(proc.getErrorStream(), pOS, false);
      errorStreamHandler.start();
      outputStreamHandler.start();
      if(proc.waitFor(5, TimeUnit.MINUTES)) {
        return proc.exitValue();
      }
      else {
        return -1;
      }
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static String getLaunchCmd(String pFlameFilename, int pWidth, int pHeight, int pQuality, boolean pMergedRender, boolean pAlpha) {
    StringBuilder cmd = new StringBuilder();
    String exePath=new File(Tools.getPathRelativeToCodeSource(FARENDER_JWF_PATH), FARENDER_EXE).getAbsolutePath();
    if (exePath.indexOf(" ") >= 0) {
      exePath = "\"" + exePath + "\"";
    }
    cmd.append(exePath);
    cmd.append(" " + String.valueOf(pWidth) + " " + String.valueOf(pHeight));
    String fn = pFlameFilename;
    if (fn.indexOf(" ") >= 0) {
      fn = "\"" + fn + "\"";
    }
    cmd.append(" " + fn);
    cmd.append(" -q " + pQuality);
    if(pMergedRender) {
      cmd.append(" -merge");
    }
    if(pAlpha) {
      cmd.append(" -alpha");
    }

    String opts = Prefs.getPrefs().getTinaFARenderOptions();
    if(opts==null) {
      opts = "-cuda";
    }
    else if(opts.indexOf("-cuda")<0) {
      opts+=" -cuda";
    }
    if (opts != null && opts.length() > 0) {
      cmd.append(" " + opts);
    }
    return cmd.toString();
  }

  public static FARenderResult invokeFARender(String pFlameFilename, int pWidth, int pHeight, int pQuality, boolean pMergedRender, Flame pFlame) {
    try {
      String outputFilename = Tools.trimFileExt(pFlameFilename) + ".png";
      {
        File outputFile = new File(outputFilename);
        if (outputFile.exists()) {
          if (!outputFile.delete()) {
            return new FARenderResult(1, "Could not delete file \"" + outputFilename + "\"");
          }
        }
      }

      String cmd = getLaunchCmd(pFlameFilename, pWidth, pHeight, pQuality, pMergedRender, pFlame.isBGTransparency() || pFlame.hasBGTransforms());
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      int returnCode = launchAsync(cmd, os);
      String msg = new String(os.toByteArray());
      FARenderResult res = new FARenderResult(returnCode, msg);
      res.setCommand(cmd);
      if (returnCode == 0) {
        res.setOutputFilename(outputFilename);
      }
      if(pFlame.hasBGTransforms()) {
        SimpleImage fgImage =  new ImageReader().loadImageWithAlpha(res.getOutputFilename());
        SimpleImage bgImage = new SimpleImage(pWidth, pHeight);
        new FlameBackgroundHandler(pFlame).fillBackground(bgImage);
        if(fgImage.getImageWidth()!=bgImage.getImageWidth() || fgImage.getImageHeight()!=bgImage.getImageHeight()) {
          throw new Exception("Image size mismatch");
        }
        for(int h=0;h<bgImage.getImageHeight();h++) {
          for(int w=0;w<bgImage.getImageWidth();w++) {
            int bgR = bgImage.getRValue(w, h);
            int bgG = bgImage.getGValue(w, h);
            int bgB = bgImage.getBValue(w, h);
            int fgA = fgImage.getAValue(w, h);
            int fgR = fgImage.getRValue(w, h);
            int fgG = fgImage.getGValue(w, h);
            int fgB = fgImage.getBValue(w, h);

            if(fgR==0 && fgG==0 && fgB==0) {
              fgA = 0;
            }

            int r = (fgR * fgA + bgR * (255-fgA)) / 255;
            int g = (fgG * fgA + bgG * (255-fgA)) / 255;
            int b = (fgB * fgA + bgB * (255-fgA)) / 255;
            bgImage.setARGB(w, h, 255, r, g, b);
          }
        }

        new ImageWriter().saveImage(bgImage, res.getOutputFilename());
      }
      return res;
    }
    catch (Exception ex) {
      return new FARenderResult(1, ex);
    }
  }

  public static String rewriteJavaFormulaForCUDA(String formula) {
    return formula.replaceAll("(atan2|asin|sin|acos|lerp|cos|fabs|log|pow|sqrt|sqr|sgn|exp|fmod|sinh|round|tan|cosh|hypot|rint|trunc|floor)\\(", "$1f(");
  }

  public static List<Flame> prepareFlame(Flame pFlame, boolean zPass) {
    List<Flame> res = new ArrayList<>();
    double time = pFlame.getFrame() >= 0 ? pFlame.getFrame() : 0;
    if (pFlame.getMotionBlurLength() > 0) {
      double currTime = time + pFlame.getMotionBlurLength() * pFlame.getMotionBlurTimeStep() / 2.0;
      for (int p = 1; p <= pFlame.getMotionBlurLength(); p++) {
        currTime -= pFlame.getMotionBlurTimeStep();
        Flame newFlame = AnimationService.evalMotionCurves(pFlame.makeCopy(), currTime);
        double brightnessScl = (1.0 - p * p * pFlame.getMotionBlurDecay() * 0.07 / pFlame.getMotionBlurLength());
        if (brightnessScl < 0.01) {
          brightnessScl = 0.01;
        }
        // HACK: misusing the stereo3dAngle-field because it will never be used by FARender, can later easily be changed if necessary, by introducing a dedicated field
        newFlame.setStereo3dAngle(brightnessScl);
        // HACK: misusing the spatialFilterIndicator-field because it will never be used by FARender, can later easily be changed if necessary, by introducing a dedicated field
        newFlame.setSpatialFilterIndicator(zPass);
        res.add(newFlame);
      }
    }
    else {
      Flame newFlame=pFlame.makeCopy();
      // HACK: misusing the stereo3dAngle-field because it will never be used by FARender, can later easily be changed if necessary, by introducing a dedicated field
      newFlame.setStereo3dAngle(1.0);
      // HACK: misusing the spatialFilterIndicator-field because it will never be used by FARender, can later easily be changed if necessary, by introducing a dedicated field
      newFlame.setSpatialFilterIndicator(zPass);
      res.add(AnimationService.evalMotionCurves(newFlame, time));
    }
    return res;
  }
}
