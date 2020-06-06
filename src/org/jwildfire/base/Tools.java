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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import org.jwildfire.create.tina.base.ZBufferFilename;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.Pixel;

public class Tools {
  public static final String APP_TITLE = "JWildfire";
  public static final String APP_VERSION = "V6.00 ALPHA2 (06.06.2020)";

  public static final boolean SPECIAL_VERSION = false;

  public static final int MAX_SPATIAL_OVERSAMPLING = 6;

  public static final int VPREC = 1024;
  public static final int SPREC = 10;
  public static final int PLOT_BUFFER_SIZE = 4096;

  private static final Pixel toolPixel = new Pixel();
  public static final String FILE_ENCODING = "utf-8";
  public static final String FILEEXT_ANB = "anb";
  public static final String FILEEXT_FLAME = "flame";
  public static final String FILEEXT_GRADIENT = "gradient";
  public static final String FILEEXT_GIF = "gif";
  public static final String FILEEXT_HDR = "hdr";
  public static final String FILEEXT_JAVA = "java";
  public static final String FILEEXT_JFX = "jfx";
  public static final String FILEEXT_JPG = "jpg";
  public static final String FILEEXT_JPS = "jps";
  public static final String FILEEXT_JPEG = "jpeg";
  public static final String FILEEXT_JWFDANCE = "jwfdance";
  public static final String FILEEXT_JWFMOVIE = "jwfmovie";
  public static final String FILEEXT_JWFRENDER = "jwfrender";
  public static final String FILEEXT_JWFSCRIPT = "jwfscript";
  public static final String FILEEXT_MP3 = "mp3";
  public static final String FILEEXT_MP4 = "mp4";
  public static final String FILEEXT_OBJ = "obj";
  public static final String FILEEXT_PLY = "ply";
  public static final String FILEEXT_PNG = "png";
  public static final String FILEEXT_PNS = "pns";
  public static final String FILEEXT_SPL = "spl";
  public static final String FILEEXT_SUNFLOW_SCENE = "sc";
  public static final String FILEEXT_SVG = "svg";
  public static final String FILEEXT_TXT = "txt";
  public static final String FILEEXT_TEXT = "text";
  public static final String FILEEXT_UGR = "ugr";
  public static final String FILEEXT_WAV = "wav";
  public static final String FILEEXT_XML = "xml";
  public static final String FILEEXT_MAP = "map";

  public static final String CURVE_POSTFIX = "Curve";

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

  public static String readUTF8Textfile(InputStream pInputStream) throws Exception {
    StringBuffer content = new StringBuffer();
    String lineFeed = System.getProperty("line.separator");
    String line;
    Reader r = new InputStreamReader(pInputStream, "utf-8");
    BufferedReader in = new BufferedReader(r);
    while ((line = in.readLine()) != null)
      content.append(line).append(lineFeed);
    in.close();
    return content.toString();
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
    String line, lineFeed = "\r\n";
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
    pValue = pValue.replace(',', '.');
    return Double.parseDouble(pValue);
  }

  private static NumberFormat dFmtNormal;
  private static NumberFormat dFmtMicro;
  public static boolean USE_TEXTURES = true;

  static {
    dFmtNormal = DecimalFormat.getInstance(Locale.US);
    dFmtNormal.setGroupingUsed(false);
    dFmtNormal.setMaximumFractionDigits(8);
    dFmtNormal.setMinimumIntegerDigits(1);

    dFmtMicro = DecimalFormat.getInstance(Locale.US);
    dFmtMicro.setGroupingUsed(false);
    dFmtMicro.setMaximumFractionDigits(16);
    dFmtMicro.setMinimumIntegerDigits(1);
  }

  public static String doubleToString(double pValue) {
    NumberFormat fmt = Math.abs(pValue) > 0.000001 ? dFmtNormal : dFmtMicro;
    return fmt.format(pValue);
  }

  public static int stringToInt(String pValue) {
    return Integer.parseInt(pValue);
  }

  public static String intToString(int pValue) {
    return String.valueOf(pValue);
  }

  private static final byte hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  public static String byteArrayToHexString(byte[] pByteArray) {
    if (pByteArray != null && pByteArray.length > 0) {
      byte[] b = new byte[2 * pByteArray.length];
      int k = 0;
      for (int i = 0; i < pByteArray.length; i++) {
        int j = pByteArray[i];
        if (j < 0) {
          j += 256;
        }
        b[k++] = hexChars[j >> 4];
        b[k++] = hexChars[j % 16];
      }
      return new String(b);
    }
    return null;
  }

  public static byte[] hexStringToByteArray(String pHexStr) {
    if (pHexStr.length() > 0) {
      if ((pHexStr.length() % 2) != 0) {
        throw new IllegalArgumentException(pHexStr);
      }
      byte[] b = new byte[pHexStr.length() / 2];
      int k = 0;
      for (int i = 0; i < pHexStr.length(); i++) {
        char c = pHexStr.charAt(i);
        int v;
        switch (c) {
          case '0':
            v = 0;
            break;
          case '1':
            v = 1;
            break;
          case '2':
            v = 2;
            break;
          case '3':
            v = 3;
            break;
          case '4':
            v = 4;
            break;
          case '5':
            v = 5;
            break;
          case '6':
            v = 6;
            break;
          case '7':
            v = 7;
            break;
          case '8':
            v = 8;
            break;
          case '9':
            v = 9;
            break;
          case 'A':
            v = 10;
            break;
          case 'B':
            v = 11;
            break;
          case 'C':
            v = 12;
            break;
          case 'D':
            v = 13;
            break;
          case 'E':
            v = 14;
            break;
          case 'F':
            v = 15;
            break;
          default:
            throw new IllegalStateException("'" + c + "'");
        }
        if ((i % 2) != 0) {
          b[i / 2] = (byte) (k + v);
        }
        else {
          k = v * 16;
        }
      }
      return b;
    }
    else {
      return null;
    }
  }

  // legacy code
  public static final int FTOI(double val) {
    int sig = MathLib.sign(val);
    if (sig > 0)
      return (int) (val + 0.5);
    else if (sig < 0)
      return (int) (0.0 - (MathLib.fabs(val) + 0.5));
    else
      return 0;
  }

  public static final byte[] readFile(String pFilename) throws Exception {
    BufferedInputStream in = new BufferedInputStream(new FileInputStream(pFilename));
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    final int BUFFER_SIZE = 40960;
    byte[] buffer = new byte[BUFFER_SIZE];
    try {
      int n;
      while ((n = in.read(buffer, 0, BUFFER_SIZE)) >= 0) {
        os.write(buffer, 0, n);
      }
      os.flush();
      os.close();
      return os.toByteArray();
    }
    finally {
      in.close();
    }
  }

  public static final byte[] getBytesFromInputStream(InputStream is) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    byte[] buffer = new byte[0xFFFF];
    for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
      os.write(buffer, 0, len);
    }
    return os.toByteArray();
  }

  public static final void writeFile(String pFilename, byte[] pData) throws Exception {
    FileOutputStream fos = new FileOutputStream(pFilename);
    fos.write(pData, 0, pData.length);
    fos.flush();
    fos.close();
  }

  public static boolean isImageFile(String absolutePath) {
    String lname = absolutePath != null ? absolutePath.toLowerCase() : "";
    return lname.endsWith(FILEEXT_PNG) || lname.endsWith(FILEEXT_JPG) ||  lname.endsWith(FILEEXT_GIF) || lname.endsWith(FILEEXT_JPEG) ||
            lname.endsWith(FILEEXT_JPS) || lname.endsWith(FILEEXT_HDR);
  }

  public static boolean isMovieFile(String absolutePath) {
    String lname = absolutePath != null ? absolutePath.toLowerCase() : "";
    return lname.endsWith(FILEEXT_MP4);
  }

  public static class XMLAttributes {
    private final List<XMLAttribute> attrList = new ArrayList<XMLAttribute>();
    private final Map<String, XMLAttribute> attrMap = new HashMap<String, XMLAttribute>();

    public void addAttribute(XMLAttribute pAttribute) {
      attrList.add(pAttribute);
      attrMap.put(pAttribute.getName(), pAttribute);
    }

    public XMLAttribute getAttribute(String pKey) {
      return attrMap.get(pKey);
    }

    public String get(String pKey) {
      XMLAttribute attr = getAttribute(pKey);
      return attr != null ? attr.getValue() : null;
    }

    public List<XMLAttribute> getAttributes() {
      return attrList;
    }
  }

  public static class XMLAttribute {
    private final String name;
    private final String value;

    public XMLAttribute(String pName, String pValue) {
      name = pName;
      value = pValue;
    }

    public String getName() {
      return name;
    }

    public String getValue() {
      return value;
    }

  }

  public static XMLAttributes parseAttributes(String pXML) {
    XMLAttributes res = new XMLAttributes();
    int p = 0;
    while (true) {
      int ps = pXML.indexOf("=\"", p + 1);
      if (ps < 0)
        break;
      int pe = pXML.indexOf("\"", ps + 2);
      String name = pXML.substring(p, ps).trim();
      String value;
      try {
        value = pXML.substring(ps + 2, pe);
        //      System.out.println("#" + name + "#" + value + "#");
      }
      catch (Throwable ex) {
        throw new RuntimeException("Error parsing attribute \"" + name + "\" (" + pXML + ")", ex);
      }
      if (value != null) {
        value = value.replaceAll("&quot;", "\"").replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
      }
      res.addAttribute(new XMLAttribute(name, value));
      p = pe + 2;
    }
    return res;
  }

  public static String trimFileExt(String pName) {
    int p = pName.lastIndexOf(".");
    if (p > 0 && p < pName.length() - 1) {
      return pName.substring(0, p);
    }
    return pName;
  }

  public static String getFileExt(String pName) {
    int p = pName.lastIndexOf(".");
    if (p > 0 && p < pName.length() - 1) {
      return pName.substring(p + 1, pName.length());
    }
    return "";
  }

  public static boolean stringEquals(String a, String b) {
    if (a == null)
      return b == null;
    else
      return a.equals(b);
  }

  public static void writeObjectToFile(Object pObject, String pFilename) {
    try (
        OutputStream file = new FileOutputStream(pFilename);
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);) {
      output.writeObject(pObject);
    }
    catch (IOException ex) {
      Unchecker.rethrow(ex);
    }
  }

  public static Object readObjectFromFile(String pFilename) {
    try (
        InputStream file = new FileInputStream(pFilename);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);) {
      return input.readObject();
    }
    catch (Exception ex) {
      Unchecker.rethrow(ex);
      return null;
    }
  }

  public static double limitValue(double value, double min, double max) {
    return value < min ? min : value > max ? max : value;
  }

  public static int limitValue(int value, int min, int max) {
    return value < min ? min : value > max ? max : value;
  }

  public static String getRessourceAsString(Class<?> parent, String ressource) throws Exception {
    try (InputStream is = parent.getResourceAsStream(ressource)) {
      StringBuffer content = new StringBuffer();
      String lineFeed = System.getProperty("line.separator");
      String line;
      Reader r = new InputStreamReader(is, "utf-8");
      BufferedReader in = new BufferedReader(r);
      while ((line = in.readLine()) != null) {
        content.append(line).append(lineFeed);
      }
      in.close();
      return content.toString();
    }
  }

  public static String makeZBufferFilename(String absolutePath, ZBufferFilename zBufferFilename) {
    File f = new File(absolutePath);
    switch (zBufferFilename) {
    case PRE_ZBUF:
      return new File(f.getParent(), "zbuf_" + f.getName()).getAbsolutePath();
    case POST_DEPTH:
      return new File(f.getParent(), f.getName().replaceFirst("[.]([^.]+)$", "_depth.$1")).getAbsolutePath();
    default:
      return new File(f.getParent(), "zbuf_" + f.getName()).getAbsolutePath();
    }
   }

  public static String makeHDRFilename(String absolutePath) {
    File f = new File(absolutePath);
    String name = Tools.trimFileExt(f.getName());
    return new File(f.getParent(), name + ".hdr").getAbsolutePath();
  }

  public static final Random RANDOM = new Random();

  /**
   * Returns a pseudorandom, uniformly distributed {@code int} value
   * between 0 (inclusive) and the specified value (exclusive).
   * Uses {@link Random#nextInt(int)}.
   * @param bound the upper bound (exclusive). Must be positive. (greater than zero)
   * @return a random int value between 0 (inclusive) and bound (exclusive)
   */
  public static int randomInt(int bound) {
    return RANDOM.nextInt(bound);
  }

  public static String getStacktrace(Throwable ex) {
    try {
      try (StringWriter sw = new StringWriter()) {
        try (PrintWriter pw = new PrintWriter(sw)) {
          ex.printStackTrace(pw);
          return sw.toString();
        }
      }
    }
    catch (Exception innerError) {
      throw new RuntimeException(innerError);
    }
  }

  public enum OSType {
    WINDOWS, MAC, LINUX, UNKNOWN
  }

  public static OSType getOSType() {
    String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    if (os.startsWith("mac os"))
      return OSType.MAC;
    else if (os.startsWith("windows"))
      return OSType.WINDOWS;
    else if (os.startsWith("linux"))
      return OSType.LINUX;
    else
      return OSType.UNKNOWN;
  }
}
