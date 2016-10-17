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
package org.jwildfire.launcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class JDKScanner {
  private final List<String> jdks = new ArrayList<String>();
  private int currentVersionIdx = -1;

  public void scan() {
    jdks.clear();
    currentVersionIdx = -1;
    String osname = System.getProperty("os.name");
    if (osname.startsWith("Windows")) {
    try {
      {
        List<String> keys = readStringSubKeys(HKEY_LOCAL_MACHINE, "SOFTWARE\\Javasoft\\Java Runtime Environment");
        String dfltVersion = readRegistryString("SOFTWARE\\Javasoft\\Java Runtime Environment", "CurrentVersion");
        for (String key : keys) {
          if (key != null && key.length() > 0) {
            String jdk = readRegistryString("SOFTWARE\\Javasoft\\Java Runtime Environment\\" + key, "JavaHome");
            addSafeJDK(jdk);
            if (currentVersionIdx < 0 && dfltVersion != null && key.equals(dfltVersion)) {
              currentVersionIdx = getSafeJDKIndex(jdk);
            }
          }
        }
      }
    }
    catch (Throwable ex) {

    }

    if (jdks.size() == 0) {
      try {
        List<String> keys = readStringSubKeys(HKEY_LOCAL_MACHINE, "SOFTWARE\\Wow6432Node\\Javasoft\\Java Runtime Environment");
        String dfltVersion = readRegistryString("SOFTWARE\\Wow6432Node\\Javasoft\\Java Runtime Environment", "CurrentVersion");
        for (String key : keys) {
          if (key != null && key.length() > 0) {
            String jdk = readRegistryString("SOFTWARE\\Wow6432Node\\Javasoft\\Java Runtime Environment\\" + key, "JavaHome");
            addSafeJDK(jdk);
            if (currentVersionIdx < 0 && dfltVersion != null && key.equals(dfltVersion)) {
              currentVersionIdx = getSafeJDKIndex(jdk);
            }
          }
        }
      }
      catch (Throwable ex) {

      }
    }
    }

    if (jdks.size() == 0) {
      addSafeJDK(System.getProperty("java.home"));
    }
  }

  public int getSafeJDKIndex(String pJDK) {
    if (pJDK != null) {
      pJDK = pJDK.trim();
      return jdks.indexOf(pJDK);
    }
    return -1;
  }

  public void addSafeJDK(String pJDK) {
    if (pJDK != null) {
      pJDK = pJDK.trim();
      if (pJDK.length() > 0 && jdks.indexOf(pJDK) < 0) {
        jdks.add(pJDK);
      }
    }
  }

  // Access the windows registry, inspired by http://www.rgagnon.com/javadetails/java-0630.html
  private static final int HKEY_LOCAL_MACHINE = 0x80000002;
  private static final int KEY_READ = 0x20019;
  private static Preferences systemRoot = Preferences.systemRoot();
  private static Class<? extends Preferences> userClass = systemRoot.getClass();
  private static Method regOpenKey = null;
  private static Method regCloseKey = null;
  private static Method regQueryValueEx = null;
  private static Method regQueryInfoKey = null;
  private static Method regEnumKeyEx = null;

  private String readRegistryString(String key, String value) throws Exception {
    int[] handles = (int[]) regOpenKey.invoke(systemRoot, new Object[] {
        new Integer(HKEY_LOCAL_MACHINE), toCstr(key), new Integer(KEY_READ) });
    if (handles[1] != 0) {
      return null;
    }
    byte[] res = (byte[]) regQueryValueEx.invoke(systemRoot, new Object[] {
        Integer.valueOf(handles[0]), toCstr(value) });
    regCloseKey.invoke(systemRoot, new Object[] { Integer.valueOf(handles[0]) });
    return res != null ? new String(res).trim() : null;
  }

  private List<String> readStringSubKeys(int hkey, String key) throws Exception {
    List<String> results = new ArrayList<String>();
    int[] handles = (int[]) regOpenKey.invoke(systemRoot, new Object[] {
        Integer.valueOf(hkey), toCstr(key), new Integer(KEY_READ)
    });
    if (handles[1] != 0) {
      return null;
    }
    int[] info = (int[]) regQueryInfoKey.invoke(systemRoot, new Object[] { Integer.valueOf(handles[0]) });
    int count = info[0]; // count  
    int maxlen = info[3]; // value length max
    for (int index = 0; index < count; index++) {
      byte[] name = (byte[]) regEnumKeyEx.invoke(systemRoot, new Object[] {
          Integer.valueOf(handles[0]), Integer.valueOf(index), Integer.valueOf(maxlen + 1)
      });
      results.add(new String(name).trim());
    }
    regCloseKey.invoke(systemRoot, new Object[] { Integer.valueOf(handles[0]) });
    return results;
  }

  static {
      
    String osname = System.getProperty("os.name");
    if (osname.startsWith("Windows")) {
    try {
      regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey", new Class[] { int.class, byte[].class, int.class });
      regOpenKey.setAccessible(true);
      regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey", new Class[] { int.class });
      regCloseKey.setAccessible(true);
      regQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx", new Class[] { int.class, byte[].class });
      regQueryValueEx.setAccessible(true);
      regQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey1", new Class[] { int.class });
      regQueryInfoKey.setAccessible(true);
      regEnumKeyEx = userClass.getDeclaredMethod("WindowsRegEnumKeyEx", new Class[] { int.class, int.class, int.class });
      regEnumKeyEx.setAccessible(true);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    }
  }

  private byte[] toCstr(String str) {
    byte[] result = new byte[str.length() + 1];
    for (int i = 0; i < str.length(); i++) {
      result[i] = (byte) str.charAt(i);
    }
    result[str.length()] = 0;
    return result;
  }

  public String getDefaultJDK() {
    if (currentVersionIdx >= 0 && currentVersionIdx < jdks.size()) {
      return jdks.get(currentVersionIdx);
    }

    return jdks.size() > 0 ? jdks.get(0) : null;
  }

  public List<String> getJDKs() {
    return jdks;
  }
}
