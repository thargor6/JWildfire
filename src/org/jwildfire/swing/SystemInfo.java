package org.jwildfire.swing;

public class SystemInfo {
  private final String osName;
  private final String osVersion;
  private final String osArch;
  private final int processors;

  public SystemInfo() {
    osName = System.getProperty("os.name");
    osVersion = System.getProperty("os.version");
    osArch = System.getProperty("os.arch");
    processors = Runtime.getRuntime().availableProcessors();
  }

  public String getOsName() {
    return osName;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public String getOsArch() {
    return osArch;
  }

  public long getTotalMemMB() {
    return Runtime.getRuntime().totalMemory() / 1024 / 1024;
  }

  public long getMaxMemMB() {
    return Runtime.getRuntime().maxMemory() / 1024 / 1024;
  }

  public long getUsedMemMB() {
    return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
  }

  public int getProcessors() {
    return processors;
  }

}
