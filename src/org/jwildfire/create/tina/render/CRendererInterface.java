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
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.image.SimpleImage;

public class CRendererInterface {
  private ProgressUpdater progressUpdater;
  private final RendererType rendererType;
  private long nextProgressUpdate;
  private String hdrOutputfilename;

  public CRendererInterface(RendererType pRendererType) {
    rendererType = pRendererType;
  }

  public static void checkFlameForCUDA(Flame pFlame) {
    for (XForm xForm : pFlame.getXForms()) {
      if (xForm.getVariationCount() > 0) {
        for (int i = 0; i < xForm.getVariationCount(); i++) {
          checkVariationForCUDA(xForm.getVariation(i));
        }
      }
    }
    if (pFlame.getFinalXForm() != null && pFlame.getFinalXForm().getVariationCount() > 0) {
      for (int i = 0; i < pFlame.getFinalXForm().getVariationCount(); i++) {
        checkVariationForCUDA(pFlame.getFinalXForm().getVariation(i));
      }
    }
  }

  private static void checkVariationForCUDA(Variation pVariation) {
    if ((pVariation.getFunc().getAvailability() & AVAILABILITY_CUDA) == 0) {
      throw new RuntimeException("Variation <" + pVariation.getFunc().getName() + "> is currently not available for the external renderer. Please try to remove it or contact the author.");
    }
  }

  public String getFlameCUDA(Flame pFlame) {
    checkFlameForCUDA(pFlame);
    StringBuffer sb = new StringBuffer();
    sb.append("Flame *createExampleFlame() {\n");
    sb.append("  Flame *flame;\n");
    sb.append("  hostMalloc((void**)&flame,sizeof(Flame));\n");
    sb.append("  flame->create();\n");
    sb.append("  flame->width=" + pFlame.getWidth() + ";\n");
    sb.append("  flame->height=" + pFlame.getHeight() + ";\n");
    sb.append("  flame->centreX=" + doubleToCUDA(pFlame.getCentreX()) + "f;\n");
    sb.append("  flame->centreY=" + doubleToCUDA(pFlame.getCentreY()) + "f;\n");
    sb.append("  flame->pixelsPerUnit=" + doubleToCUDA(pFlame.getPixelsPerUnit()) + "f;\n");
    sb.append("  flame->camRoll=" + doubleToCUDA(pFlame.getCamRoll()) + "f;\n");
    sb.append("  flame->spatialOversample=" + pFlame.getSpatialOversample() + ";\n");
    sb.append("  flame->colorOversample=" + pFlame.getColorOversample() + ";\n");
    sb.append("  flame->spatialFilterRadius=" + doubleToCUDA(pFlame.getSpatialFilterRadius()) + "f;\n");
    sb.append("  flame->sampleDensity=" + doubleToCUDA(pFlame.getSampleDensity()) + "f;\n");
    sb.append("  flame->bgColorRed=" + pFlame.getBGColorRed() + ";\n");
    sb.append("  flame->bgColorGreen=" + pFlame.getBGColorGreen() + ";\n");
    sb.append("  flame->bgColorBlue=" + pFlame.getBGColorBlue() + ";\n");
    sb.append("  flame->brightness=" + doubleToCUDA(pFlame.getBrightness()) + "f;\n");
    sb.append("  flame->gamma=" + doubleToCUDA(pFlame.getGamma()) + "f;\n");
    sb.append("  flame->gammaThreshold=" + doubleToCUDA(pFlame.getGammaThreshold()) + "f;\n");
    sb.append("  flame->camZoom=" + doubleToCUDA(pFlame.getCamZoom()) + "f;\n");
    sb.append("  flame->camPitch=" + doubleToCUDA(pFlame.getCamPitch()) + "f;\n");
    sb.append("  flame->camYaw=" + doubleToCUDA(pFlame.getCamYaw()) + ";\n");
    sb.append("  flame->camPerspective=" + doubleToCUDA(pFlame.getCamPerspective()) + "f;\n");
    sb.append("  flame->camZ=" + doubleToCUDA(pFlame.getCamZ()) + "f;\n");
    sb.append("  flame->camDOF=" + doubleToCUDA(pFlame.getCamDOF()) + "f;\n");
    sb.append("\n");
    for (int i = 0; i < pFlame.getPalette().getSize(); i++) {
      RGBColor color = pFlame.getPalette().getColor(i);
      sb.append("  flame->palette->setColor(" + i + "," + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");\n");
    }
    sb.append("\n");
    sb.append("  flame->xFormCount=" + pFlame.getXForms().size() + ";\n");
    sb.append("  hostMalloc((void**)&flame->xForms, flame->xFormCount*sizeof(XForm*));\n");
    for (int i = 0; i < pFlame.getXForms().size(); i++) {
      XForm xForm = pFlame.getXForms().get(i);
      sb.append("  // xForm" + (i + 1) + "\n");
      sb.append("  hostMalloc((void**)&flame->xForms[" + i + "], sizeof(XForm));\n");
      sb.append("  flame->xForms[" + i + "]->init();\n");
      sb.append("  flame->xForms[" + i + "]->weight=" + doubleToCUDA(xForm.getWeight()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->color=" + doubleToCUDA(xForm.getColor()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->colorSymmetry=" + doubleToCUDA(xForm.getColorSymmetry()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff00=" + doubleToCUDA(xForm.getCoeff00()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff01=" + doubleToCUDA(xForm.getCoeff01()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff10=" + doubleToCUDA(xForm.getCoeff10()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff11=" + doubleToCUDA(xForm.getCoeff11()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff20=" + doubleToCUDA(xForm.getCoeff20()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->coeff21=" + doubleToCUDA(xForm.getCoeff21()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff00=" + doubleToCUDA(xForm.getPostCoeff00()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff01=" + doubleToCUDA(xForm.getPostCoeff01()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff10=" + doubleToCUDA(xForm.getPostCoeff10()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff11=" + doubleToCUDA(xForm.getPostCoeff11()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff20=" + doubleToCUDA(xForm.getPostCoeff20()) + "f;\n");
      sb.append("  flame->xForms[" + i + "]->postCoeff21=" + doubleToCUDA(xForm.getPostCoeff21()) + "f;\n");
      for (int j = 0; j < xForm.getVariationCount(); j++) {
        Variation var = xForm.getVariation(j);
        String paramNames[] = var.getFunc().getParameterNames();
        if (paramNames == null || paramNames.length == 0) {
          sb.append("  flame->xForms[" + i + "]->addVariation(variationFactory->newInstance(\"" + var.getFunc().getName() + "\") , " + doubleToCUDA(var.getAmount()) + "f);\n");
        }
        else {
          Object values[] = var.getFunc().getParameterValues();
          sb.append("  {\n");
          sb.append("    Variation *var=flame->xForms[" + i + "]->addVariation(variationFactory->newInstance(\"" + var.getFunc().getName() + "\") , " + doubleToCUDA(var.getAmount()) + "f);\n");
          for (int k = 0; k < paramNames.length; k++) {
            Object val = values[k];
            if (val == null) {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\",0.0f);\n");
            }
            else if (val instanceof Integer) {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\"," + ((Integer) val).intValue() + ".0f);\n");
            }
            else {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\"," + doubleToCUDA((Double) val) + "f);\n");
            }
          }
          sb.append("  }\n");
        }
      }
    }
    if (pFlame.getFinalXForm() != null) {
      XForm xForm = pFlame.getFinalXForm();
      sb.append("  // final xForm\n");
      sb.append("  hostMalloc((void**)&flame->finalXForm, sizeof(XForm));\n");
      sb.append("  flame->finalXForm->init();\n");
      sb.append("  flame->finalXForm->weight=" + doubleToCUDA(xForm.getWeight()) + "f;\n");
      sb.append("  flame->finalXForm->color=" + doubleToCUDA(xForm.getColor()) + "f;\n");
      sb.append("  flame->finalXForm->colorSymmetry=" + doubleToCUDA(xForm.getColorSymmetry()) + "f;\n");
      sb.append("  flame->finalXForm->coeff00=" + doubleToCUDA(xForm.getCoeff00()) + "f;\n");
      sb.append("  flame->finalXForm->coeff01=" + doubleToCUDA(xForm.getCoeff01()) + "f;\n");
      sb.append("  flame->finalXForm->coeff10=" + doubleToCUDA(xForm.getCoeff10()) + "f;\n");
      sb.append("  flame->finalXForm->coeff11=" + doubleToCUDA(xForm.getCoeff11()) + "f;\n");
      sb.append("  flame->finalXForm->coeff20=" + doubleToCUDA(xForm.getCoeff20()) + "f;\n");
      sb.append("  flame->finalXForm->coeff21=" + doubleToCUDA(xForm.getCoeff21()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff00=" + doubleToCUDA(xForm.getPostCoeff00()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff01=" + doubleToCUDA(xForm.getPostCoeff01()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff10=" + doubleToCUDA(xForm.getPostCoeff10()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff11=" + doubleToCUDA(xForm.getPostCoeff11()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff20=" + doubleToCUDA(xForm.getPostCoeff20()) + "f;\n");
      sb.append("  flame->finalXForm->postCoeff21=" + doubleToCUDA(xForm.getPostCoeff21()) + "f;\n");
      for (int j = 0; j < xForm.getVariationCount(); j++) {
        Variation var = xForm.getVariation(j);
        String paramNames[] = var.getFunc().getParameterNames();
        if (paramNames == null || paramNames.length == 0) {
          sb.append("  flame->finalXForm->addVariation(variationFactory->newInstance(\"" + var.getFunc().getName() + "\") , " + doubleToCUDA(var.getAmount()) + "f);\n");
        }
        else {
          Object values[] = var.getFunc().getParameterValues();
          sb.append("  {\n");
          sb.append("    Variation *var=flame->finalXForm->addVariation(variationFactory->newInstance(\"" + var.getFunc().getName() + "\") , " + doubleToCUDA(var.getAmount()) + "f);\n");
          for (int k = 0; k < paramNames.length; k++) {
            Object val = values[k];
            if (val == null) {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\",0.0f);\n");
            }
            else if (val instanceof Integer) {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\"," + ((Integer) val).intValue() + ".0f);\n");
            }
            else {
              sb.append("    var->setParameter(\"" + paramNames[k] + "\"," + doubleToCUDA((Double) val) + "f);\n");
            }
          }
          sb.append("  }\n");
        }
      }
    }
    sb.append("\n");
    sb.append("  return flame;\n");
    sb.append("}\n\n");
    return sb.toString();
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

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    int retVal = launchCmd(cmd + args, bos, statusFilename, true);
    if (retVal != 0) {
      throw new RuntimeException(bos.toString());
    }

    loadImage(ppmFilename, res.getImage());

    new File(flameFilename).delete();
    new File(ppmFilename).delete();

    if (progressUpdater != null) {
      progressUpdater.updateProgress(100);
    }
    return res;
  }

  public void loadImage(String pFilename, SimpleImage pImg) {
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

      // width * height * 3 bytes
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

    public ExecCmdThread(String pCmd, OutputStream pOS) {
      cmd = pCmd;
      os = pOS;
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

          final boolean dumpToOut = true;
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

          if (dumpToOut) {
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

  private int launchCmd(String pCmd, OutputStream pOS, String pStatusFilename, boolean pInBackground) {
    ExecCmdThread thread = new ExecCmdThread(pCmd, pOS);
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
