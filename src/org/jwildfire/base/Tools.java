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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.jwildfire.image.Pixel;

public class Tools {
  public static final String APP_TITLE = "JWildfire";
  public static final String APP_VERSION = "0.16 (10.11.2011)";

  public static final double ZERO = 0.0001;
  public static final int VPREC = 1024;
  public static final int SPREC = 10;
  private static final Pixel toolPixel = new Pixel();
  public static final String FILEEXT_JFX = "jfx";
  public static final String FILE_ENCODING = "utf-8";

  public static final double fmod33(double arg) {
    return (arg - (double) ((int) arg));
  }

  public static Integer RGBToARGBValue(int pR, int pG, int pB) {
    toolPixel.r = pR;
    toolPixel.g = pG;
    toolPixel.b = pB;
    return toolPixel.getARGBValue();
  }

  public static boolean isDebugMode() {
    return true;
  }

  public static Date now() {
    return Calendar.getInstance().getTime();
  }

  public static String DateToString(Date pDate) {
    DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    return formatter.format(pDate);
  }

  public static String TimeToString(Date pDate) {
    DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
    return formatter.format(pDate);
  }

  public static String DateTimeToString(Date pDate) {
    DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
    return formatter.format(pDate);
  }

  public static String forceFileExt(String pFilename, String pExt) {
    if ((pExt != null) && (pExt.length() > 0)) {
      boolean hasFileExt = false;
      for (int i = pFilename.length() - 1; i > 0; i--) {
        if (pFilename.charAt(i) == '.') {
          String ext = pFilename.substring(i + 1, pFilename.length());
          hasFileExt = ext.equals(pExt);
          break;
        }
      }
      if (!hasFileExt)
        pFilename = pFilename + "." + pExt;
    }
    return pFilename;
  }

  public static String readUTF8Textfile(String pTextFileName) throws Exception {
    StringBuffer content = new StringBuffer();
    String lineFeed = System.getProperty("line.separator");
    String line;
    Reader r = new InputStreamReader(new FileInputStream(pTextFileName), "utf-8");
    BufferedReader in = new BufferedReader(r);
    while ((line = in.readLine()) != null)
      content.append(line).append(lineFeed);
    in.close();
    return content.toString();
  }

  public static void writeUTF8Textfile(String pTextFileName, String pContent) throws Exception {
    String line, lineFeed = new String("\r\n");
    BufferedReader in = new BufferedReader(new StringReader(pContent));
    Writer w = new OutputStreamWriter(new FileOutputStream(pTextFileName), "utf-8");
    BufferedWriter out = new BufferedWriter(w);
    while ((line = in.readLine()) != null) {
      out.write(line);
      out.write(lineFeed);
    }
    out.close();
    in.close();
  }

  public static double fabs33(double pArg) {
    if (pArg < 0.0)
      return (0.0 - pArg);
    else
      return (pArg);
  }

  private static int a123 = 1;

  private static final int RAND_MAX123 = 0x7fffffff;

  private static int rand123() {
    return (a123 = a123 * 1103515245 + 12345) % RAND_MAX123;
  }

  public static void srand123(int pSeed) {
    a123 = pSeed;
  }

  private static double rrmax = 1.0 / (double) RAND_MAX123;

  public static double drand() {
    double res = ((double) (rand123() * rrmax));
    return (res < 0) ? 0.0 - res : res;
  }

  public static int roundColor(double pColor) {
    int res = (int) (pColor + 0.5);
    if (res < 0)
      res = 0;
    else if (res > 255)
      res = 255;
    return res;
  }

  public static int limitColor(int pColor) {
    if (pColor < 0)
      return 0;
    else if (pColor > 255)
      return 255;
    else
      return pColor;
  }

  public static int bresenham(int x1, int y1, int x2, int y2, int[] x, int[] y) {
    int dx, dy, xf, yf, a, b, c, i;
    if (x2 > x1) {
      dx = x2 - x1;
      xf = 1;
    }
    else {
      dx = x1 - x2;
      xf = -1;
    }
    if (y2 > y1) {
      dy = y2 - y1;
      yf = 1;
    }
    else {
      dy = y1 - y2;
      yf = -1;
    }
    if (dx > dy) {
      a = dy + dy;
      c = a - dx;
      b = c - dx;
      for (i = 0; i <= dx; i++) {
        x[i] = x1;
        y[i] = y1;
        x1 += xf;
        if (c < 0) {
          c += a;
        }
        else {
          c += b;
          y1 += yf;
        }
      }
      return dx + 1;
    }
    else {
      a = dx + dx;
      c = a - dy;
      b = c - dy;
      for (i = 0; i <= dy; i++) {
        x[i] = x1;
        y[i] = y1;
        y1 += yf;
        if (c < 0) {
          c += a;
        }
        else {
          c += b;
          x1 += xf;
        }
      }
      return dy + 1;
    }
  }

  public static double stringToDouble(String pValue) {
    return Double.parseDouble(pValue);
  }

  private static NumberFormat dFmtNormal;
  private static NumberFormat dFmtSmall;
  public static boolean USE_TEXTURES = true;

  static {
    dFmtNormal = DecimalFormat.getInstance(Locale.US);
    dFmtNormal.setGroupingUsed(false);
    dFmtNormal.setMaximumFractionDigits(2);
    dFmtNormal.setMinimumIntegerDigits(1);

    dFmtSmall = DecimalFormat.getInstance(Locale.US);
    dFmtSmall.setGroupingUsed(false);
    dFmtSmall.setMaximumFractionDigits(5);
    dFmtSmall.setMinimumIntegerDigits(1);
  }

  public static String doubleToString(double pValue) {
    NumberFormat fmt = Math.abs(pValue) > 0.01 ? dFmtNormal : dFmtSmall;
    return fmt.format(pValue);
  }

  public static int stringToInt(String pValue) {
    return Integer.parseInt(pValue);
  }

  public static String intToString(int pValue) {
    return String.valueOf(pValue);
  }

  public static int SIGNUM(double val) {
    if (val > 0.0)
      return 1;
    else if (val < 0.0)
      return -1;
    else
      return 0;
  }

  public static double FABS(double var) {
    if (var < 0.0)
      return 0.0 - var;
    else
      return var;
  }

  public static int FTOI(double val) {
    int sig = SIGNUM(val);
    if (sig > 0)
      return (int) (val + 0.5);
    else if (sig < 0)
      return (int) (0.0 - (FABS(val) + 0.5));
    else
      return 0;
  }

}
