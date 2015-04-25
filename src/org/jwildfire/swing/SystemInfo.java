package org.jwildfire.swing;

import java.lang.management.ManagementFactory;

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
    return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() / 1024 /1024;
  }

  public long getMaxMemMB() {
    return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1024 /1024;
  }

  public long getUsedMemMB() {
    return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024;
  }

  public int getProcessors() {
    return processors;
  }

}
