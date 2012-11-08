/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
package org.jwildfire.create.tina.render;

import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_CUDA;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.CodeSource;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.Flam3Writer;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.image.SimpleImage;

public class CRendererInterface {
  private ProgressUpdater progressUpdater;
  private final RendererType rendererType;
  private long nextProgressUpdate;
  private String hdrOutputfilename;
  private boolean withAlpha;

  public CRendererInterface(RendererType pRendererType, boolean pWithAlpha) {
    rendererType = pRendererType;
    withAlpha = pWithAlpha;
  }

  public static void checkFlameForCUDA(Flame pFlame) {
    for (XForm xForm : pFlame.getXForms()) {
      if (xForm.getVariationCount() > 0) {
        for (int i = 0; i < xForm.getVariationCount(); i++) {
          checkVariationForCUDA(xForm.getVariation(i));
        }
      }
    }
    for (XForm xForm : pFlame.getFinalXForms()) {
      if (xForm.getVariationCount() > 0) {
        for (int i = 0; i < xForm.getVariationCount(); i++) {
          checkVariationForCUDA(xForm.getVariation(i));
        }
      }
    }
  }

  private static void checkVariationForCUDA(Variation pVariation) {
    if ((pVariation.getFunc().getAvailability() & AVAILABILITY_CUDA) == 0) {
      throw new RuntimeException("Variation <" + pVariation.getFunc().getName() + "> is currently not available for the external renderer. Please try to remove it or contact the author.");
    }
  }

  private String doubleToCUDA(double pVal) {
    String res = Tools.doubleToString(pVal);
    return res.indexOf(".") < 0 ? res + ".0" : res;
  }

  public RenderedFlame renderFlame(RenderInfo pInfo, Flame pFlame, Prefs pPrefs) throws Exception {
    if (progressUpdater != null) {
      progressUpdater.initProgress(100);
    }
    nextProgressUpdate = System.currentTimeMillis();

    RenderedFlame res = new RenderedFlame();
    res.init(pInfo);

    File tmpFile = File.createTempFile("JWF", "");
    String currTmpFilename = tmpFile.getAbsolutePath();
    tmpFile.delete();

    CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
    File jarFile = new File(codeSource.getLocation().toURI().getPath());
    File jarDir = jarFile.getParentFile();
    String jarParentDir = jarDir.getParentFile().getPath();

    String cmd;
    switch (rendererType) {
      case C32:
        cmd = jarParentDir + "\\JWildfireC\\win32\\JWildfireC.exe";
        break;
      case C64:
        cmd = jarParentDir + "\\JWildfireC\\win64\\JWildfireC.exe";
        break;
      default:
        throw new Exception("Unsupported render type <" + rendererType + ">");
    }
    if (!new File(cmd).exists()) {
      throw new Exception("Renderer <" + cmd + "> not found");
    }

    if (pPrefs.isDevelopmentMode()) {
      cmd = "F:\\DEV\\eclipse_indigo_c_workspace\\JWildfireC\\Debug\\JWildfireC.exe";
    }
    String flameFilename = currTmpFilename + ".flame";
    String ppmFilename = currTmpFilename + ".ppm";
    String statusFilename = currTmpFilename + ".ppm.status";

    try {
      new Flam3Writer().writeFlame(pFlame, flameFilename);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    String args = " -flameFilename \"" + flameFilename + "\" -outputFilename \"" + ppmFilename + "\" -threadCount " + pPrefs.getTinaRenderThreads() + " -reportStatus -outputWidth " + pInfo.getImageWidth() + " -outputHeight " + pInfo.getImageHeight() + " -sampleDensity " + doubleToCUDA(pFlame.getSampleDensity());
    if (hdrOutputfilename != null && hdrOutputfilename.length() > 0) {
      args += " -outputHDRFilename \"" + hdrOutputfilename;
    }
    if (withAlpha) {
      args += " -a";
    }

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    int retVal = launchCmd(cmd + args, bos, pPrefs, statusFilename, true);
    if (retVal != 0) {
      throw new RuntimeException(bos.toString());
    }

    loadImage(ppmFilename, res.getImage(), withAlpha);

    new File(flameFilename).delete();
    new File(ppmFilename).delete();

    if (progressUpdater != null) {
      progressUpdater.updateProgress(100);
    }
    return res;
  }

  public void loadImage(String pFilename, SimpleImage pImg, boolean pWithAlpha) {
    try {
      String magicNumber = new String();
      String imgSize = "";

      FileInputStream is = new FileInputStream(pFilename);
      byte buffer;
      // Identifier
      do {
        buffer = (byte) is.read();
        magicNumber = magicNumber + Character.valueOf((char) buffer);
      }
      while (buffer != '\n' && buffer != ' ');
      if (!(magicNumber.charAt(0) == 'P' && magicNumber.charAt(1) == '6')) {
        throw new RuntimeException("Unsupported format");
      }
      // Image size
      buffer = (byte) is.read();
      // skip comment
      if (buffer == '#') {
        do {
          buffer = (byte) is.read();
        }
        while (buffer != '\n');
        buffer = (byte) is.read();
      }

      do {
        imgSize = imgSize + Character.valueOf((char) buffer);
        buffer = (byte) is.read();
      }
      while (buffer != ' ' && buffer != '\n');

      int imgWidth = Integer.parseInt(imgSize);
      imgSize = "";
      buffer = (byte) is.read();
      do {
        imgSize = imgSize + Character.valueOf((char) buffer);
        buffer = (byte) is.read();
      }
      while (buffer != ' ' && buffer != '\n');
      int imageHeight = Integer.parseInt(imgSize);
      do {
        buffer = (byte) is.read();
      }
      while (buffer != ' ' && buffer != '\n');

      if (pWithAlpha) {
        // width * height * 4 bytes (ARGB)
        byte[] row = new byte[imgWidth * 4];
        for (int y = 0; y < imageHeight; y++) {
          is.read(row, 0, imgWidth * 4);
          for (int i = 0; i < imgWidth; i++) {
            int a = row[4 * i];
            if (a < 0)
              a += 256;
            int r = row[4 * i + 1];
            if (r < 0)
              r += 256;
            int g = row[4 * i + 2];
            if (g < 0)
              g += 256;
            int b = row[4 * i + 3];
            if (b < 0)
              b += 256;
            pImg.setARGB(i, y, a, r, g, b);
          }
        }
      }
      else {
        // width * height * 3 bytes (RGB)
        byte[] row = new byte[imgWidth * 3];
        for (int y = 0; y < imageHeight; y++) {
          is.read(row, 0, imgWidth * 3);
          for (int i = 0; i < imgWidth; i++) {
            int r = row[3 * i];
            if (r < 0)
              r += 256;
            int g = row[3 * i + 1];
            if (g < 0)
              g += 256;
            int b = row[3 * i + 2];
            if (b < 0)
              b += 256;
            pImg.setRGB(i, y, r, g, b);
          }
        }
      }
      is.close();
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private class ExecCmdThread implements Runnable {
    private final String cmd;
    private final OutputStream os;
    private boolean finished;
    private int exitVal;
    private Prefs prefs;

    public ExecCmdThread(String pCmd, OutputStream pOS, Prefs pPrefs) {
      cmd = pCmd;
      os = pOS;
      prefs = pPrefs;
    }

    @Override
    public void run() {
      finished = false;
      try {
        try {
          Runtime runtime = Runtime.getRuntime();

          Process proc;
          try {
            proc = runtime.exec(cmd);
          }
          catch (IOException ex) {
            throw new RuntimeException(ex);
          }

          final boolean dumpToOut = prefs.isDevelopmentMode();
          StreamRedirector outputStreamHandler = new StreamRedirector(proc.getInputStream(), os, dumpToOut);
          StreamRedirector errorStreamHandler = new StreamRedirector(proc.getErrorStream(), os, dumpToOut);
          errorStreamHandler.start();
          outputStreamHandler.start();

          try {
            exitVal = proc.waitFor();
          }
          catch (InterruptedException e) {
            exitVal = -1;
            e.printStackTrace();
          }

          if (dumpToOut && exitVal != 0) {
            System.out.println("EXITVALUE=" + exitVal);
          }
        }
        finally {
          try {
            os.flush();
            os.close();
          }
          catch (IOException e) {
            exitVal = -1;
            e.printStackTrace();
          }
        }
      }
      finally {
        finished = true;
      }
    }

    public boolean isFinished() {
      return finished;
    }

    public int getExitVal() {
      return exitVal;
    }
  }

  private int launchCmd(String pCmd, OutputStream pOS, Prefs pPrefs, String pStatusFilename, boolean pInBackground) {
    ExecCmdThread thread = new ExecCmdThread(pCmd, pOS, pPrefs);
    if (pInBackground) {
      new Thread(thread).start();
      while (!thread.isFinished()) {
        try {
          Thread.sleep(1);
          if (progressUpdater != null && pStatusFilename != null && (System.currentTimeMillis() > nextProgressUpdate)) {
            String currProgressStr;
            try {
              BufferedReader in = new BufferedReader(new FileReader(pStatusFilename));
              try {
                currProgressStr = in.readLine();
              }
              finally {
                in.close();
              }
            }
            catch (Throwable ex) {
              //ex.printStackTrace();
              currProgressStr = "";
            }
            int currProgressVal = 0;
            if (currProgressStr != null && currProgressStr.length() > 0) {
              try {
                currProgressVal = Integer.parseInt(currProgressStr);
              }
              catch (Exception ex) {
                currProgressVal = 0;
              }
            }
            if (currProgressVal > 0 && currProgressVal <= 100) {
              progressUpdater.updateProgress(currProgressVal);
            }
            nextProgressUpdate = System.currentTimeMillis() + 250;
          }
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    else {
      thread.run();
    }
    return thread.getExitVal();
  }

  private class StreamRedirector extends Thread {
    private final InputStream is;
    private final OutputStream os;
    private final boolean copyToOut;

    public StreamRedirector(InputStream pIs, OutputStream pOs, boolean pCopyToOut) {
      is = pIs;
      os = pOs;
      copyToOut = pCopyToOut;
    }

    public void run() {
      try {
        PrintWriter pw = null;
        try {
          if (os != null) {
            pw = new PrintWriter(os);
          }
          InputStreamReader isr = new InputStreamReader(is);
          BufferedReader br = new BufferedReader(isr);
          String line = null;
          while ((line = br.readLine()) != null) {
            if (pw != null) {
              pw.println(line);
            }
            if (copyToOut) {
              System.out.println(line);
            }
          }
        }
        finally {
          if (pw != null) {
            pw.flush();
          }
        }
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public static boolean isCUDAAvailable() {
    // Currently Windows only
    return (File.separatorChar == '\\');
  }

  public void setProgressUpdater(ProgressUpdater pProgressUpdater) {
    progressUpdater = pProgressUpdater;
  }

  public void setHDROutputfilename(String pHDRFilename) {
    hdrOutputfilename = pHDRFilename;
  }
}
