/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2025 Andreas Maschke

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
package org.jwildfire.create.tina.render.gpu.swanrender;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.cedarsoftware.io.JsonIo;
import com.cedarsoftware.io.ReadOptions;
import com.cedarsoftware.io.ReadOptionsBuilder;
import com.cedarsoftware.io.WriteOptions;
import com.cedarsoftware.io.WriteOptionsBuilder;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.render.gpu.CmdLauncherTools;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;

public class SwanRenderTools {
  private static final String SWAN_CMD_MAC_OS =
      "JWildfireSwan (strong and beautiful).app//Contents/MacOS/JWildfireSwan (strong and beautiful)";
  private static final String SWAN_CMD_WINDOWS = "JWildfireSwan (strong and beautiful).app";
  private static final String SWAN_CMD_LINUX = "JWildfireSwan (strong and beautiful).app";

  private static String[] getLaunchCmd() {
    String[] cmd = new String[3];
    Prefs prefs = Prefs.getPrefs();
    Tools.OSType osType = Tools.getOSType();
    String swanInstallationPath = prefs.getSwanInstallationPath();
    if(swanInstallationPath != null && !swanInstallationPath.isEmpty()) {
      if (Tools.OSType.WINDOWS.equals(osType)) {
        swanInstallationPath = swanInstallationPath.replace("/", "\\");
        if(swanInstallationPath.charAt(swanInstallationPath.length() - 1) != '\\') {
          swanInstallationPath = swanInstallationPath + '\\';
        }
      }
      else {
        swanInstallationPath = swanInstallationPath.replace("\\", "/");
        if(swanInstallationPath.charAt(swanInstallationPath.length() - 1) != '/') {
          swanInstallationPath = swanInstallationPath + '/';
        }
      }
    }
    String exePath;
    if (Tools.OSType.WINDOWS.equals(osType)) {
      exePath = swanInstallationPath + SWAN_CMD_WINDOWS;
      if (exePath.indexOf(" ") >= 0) {
        exePath = "\"" + exePath + "\"";
      }
    }
    else if(Tools.OSType.MAC.equals(osType)) {
      exePath = swanInstallationPath + SWAN_CMD_MAC_OS;
    }
    else {
      exePath = swanInstallationPath + SWAN_CMD_LINUX;
    }
    cmd[0] = exePath;
    cmd[1] = "--swan-api";
    cmd[2] = "--swan-api-port=" + prefs.getSwanApiPort();
    return cmd;
  }

  public static void launchSwan() {
    String[] cmd = getLaunchCmd();
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    CmdLauncherTools.launchAsyncNoWait(cmd, os);
  }

  private static String getSwanApiBaseUrl() {
    Prefs prefs = Prefs.getPrefs();
    int swanApiPort = prefs.getSwanApiPort();
    if (swanApiPort <= 0) {
      swanApiPort = 8080;
    }
    return "http://localhost:" + swanApiPort + "/api";
  }

  public static String performHttpRequest(String urlPath, int timeout, String requestMethod, byte[] body) {
    try {
      URL url = new URL(urlPath);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod(requestMethod);
      con.setRequestProperty("Accept", "application/json");
      con.setRequestProperty("Content-Type", "application/json");

      con.setConnectTimeout(timeout);
      con.setReadTimeout(timeout);

      if(body!= null) {
        con.setDoOutput(true);
        DataOutputStream os = new DataOutputStream(con.getOutputStream());
        os.write(body);
        os.flush();
        os.close();
      }

      int status = con.getResponseCode();
      Reader streamReader;
      if (status > 299) {
        streamReader = new InputStreamReader(con.getErrorStream());
      } else {
        streamReader = new InputStreamReader(con.getInputStream());
      }

      BufferedReader in = new BufferedReader(streamReader);
      try {
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          content.append(inputLine);
        }

        if (status > 299) {
          throw new RuntimeException("Error response from SWAN API: " + content.toString());
        }
        else {
          return content.toString();
        }
      }
      finally{
        in.close();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static SwanApiVersionTo getSwanVersion() {
    String urlPath = getSwanApiBaseUrl() + "/v1/info";
    String response = performHttpRequest(urlPath, 500, "GET", null);
    ReadOptions readOptions = new ReadOptionsBuilder().build();
    SwanApiVersionTo version = JsonIo.toJava(response, readOptions).asClass(SwanApiVersionTo.class);
    return version;
  }

  public static int getSwanApiVersion() {
    try {
      return getSwanVersion().getApi_version() != null ? getSwanVersion().getApi_version() : -1;
    } catch (Exception e) {
      return -1; // SWAN is not running or not available
    }
  }

  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
              + Character.digit(s.charAt(i+1), 16));
    }
    return data;
  }


  public static SwanApiRenderFlameResultTo renderFlame(Flame flame, int render_width, int render_height, double render_quality) {
    String urlPath = getSwanApiBaseUrl() + "/v1/render-flame";

    SwanApiRenderFlameTo renderFlameTo = new SwanApiRenderFlameTo();
    String flameXml = null;
    try {
      flameXml = new FlameWriter().getFlameXML(flame);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    renderFlameTo.setFlame(flameXml);
    renderFlameTo.setRender_width(render_width);
    renderFlameTo.setRender_height(render_height);
    renderFlameTo.setRender_quality(render_quality);
    // TODO: prefs
    renderFlameTo.setSwarm_size(1024);
    renderFlameTo.setIterations_per_frame(20);
    renderFlameTo.setFrame(1);

    WriteOptions writeOptions = new WriteOptionsBuilder().closeStream(false).build();
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      JsonIo.toJson(os, renderFlameTo, writeOptions);
    } finally {
      try {
        os.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    String response = performHttpRequest(urlPath, 600000,"POST", os.toByteArray());
    ReadOptions readOptions = new ReadOptionsBuilder().build();
    SwanApiRenderFlameResultTo result = JsonIo.toJava(response, readOptions).asClass(SwanApiRenderFlameResultTo.class);
    return result;
  }

  public static SimpleImage decodeImageFromResult(SwanApiRenderFlameResultTo result) {
    try {
      ImageReader imageReader = new ImageReader();
      byte img_data[] = hexStringToByteArray(result.getImage_hex());
      SimpleImage img = imageReader.loadImage(img_data);
      return img;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }


}
