package org.jwildfire.create.tina.browser;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwildfire.base.Tools;

public class FlameFlatNodes implements Serializable {
  private static final long serialVersionUID = 1L;
  private final List<FlameFlatNode> nodes = new ArrayList<FlameFlatNode>();

  public void sortNodes() {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Collections.sort(nodes, new Comparator<FlameFlatNode>() {

      @Override
      public int compare(FlameFlatNode o1, FlameFlatNode o2) {
        String ageString1 = sdf.format(o1.getFileage());
        String ageString2 = sdf.format(o2.getFileage());
        int ageCompare = ageString1.compareTo(ageString2);
        if (ageCompare > 0) {
          return -1;
        }
        else if (ageCompare < 0) {
          return +1;
        }
        else {
          return o1.getCaption().compareTo(o2.getCaption());
        }
      }
    });
  }

  public void clear() {
    nodes.clear();
  }

  public void scanFlames(String pPath) {
    clear();
    scanDrawer(pPath);
  }

  private void scanDrawer(String pPath) {
    File root = new File(pPath);
    File[] list = root.listFiles();
    if (list != null) {
      for (File f : list) {
        if (f.isDirectory()) {
          scanDrawer(f.getAbsolutePath());
        }
        else {
          String filename = f.getAbsolutePath();
          String lcFilename = filename.toLowerCase();
          if (lcFilename.length() != filename.length()) {
            lcFilename = filename;
          }
          int pos = lcFilename.lastIndexOf("." + Tools.FILEEXT_FLAME.toLowerCase());
          if (pos > 0 && pos == filename.length() - Tools.FILEEXT_FLAME.length() - 1) {
            FlameFlatNode node = new FlameFlatNode(f.getAbsolutePath(), new Date(f.lastModified()));
            nodes.add(node);
          }
        }
      }
    }
  }

  public List<Date> getDistinctFiledates() {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    List<Date> res = new ArrayList<Date>();
    Map<String, String> dateStrMap = new HashMap<String, String>();
    for (FlameFlatNode node : nodes) {
      String dateStr = sdf.format(node.getFileage());
      if (dateStrMap.get(dateStr) == null) {
        dateStrMap.put(dateStr, dateStr);
        res.add(node.getFileage());
      }
    }
    return res;
  }

  public List<FlameFlatNode> getDayNodes(Date pDay) {
    List<FlameFlatNode> res = new ArrayList<FlameFlatNode>();
    Calendar cal = GregorianCalendar.getInstance();
    cal.setTime(pDay);
    int refYear = cal.get(Calendar.YEAR);
    int refMonth = cal.get(Calendar.MONTH);
    int refDay = cal.get(Calendar.DAY_OF_MONTH);
    for (FlameFlatNode node : nodes) {
      cal.setTime(node.getFileage());
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      if (year == refYear && month == refMonth && day == refDay) {
        res.add(node);
      }
    }
    return res;
  }

  public List<Date> getDistinctMonths() {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    List<Date> res = new ArrayList<Date>();
    Map<String, String> dateStrMap = new HashMap<String, String>();
    for (FlameFlatNode node : nodes) {
      String dateStr = sdf.format(node.getFileage());
      if (dateStrMap.get(dateStr) == null) {
        dateStrMap.put(dateStr, dateStr);
        res.add(node.getFileage());
      }
    }
    return res;
  }

  public List<FlameFlatNode> getMonthNodes(Date pMonth) {
    List<FlameFlatNode> res = new ArrayList<FlameFlatNode>();
    Calendar cal = GregorianCalendar.getInstance();
    cal.setTime(pMonth);
    int refYear = cal.get(Calendar.YEAR);
    int refMonth = cal.get(Calendar.MONTH);
    for (FlameFlatNode node : nodes) {
      cal.setTime(node.getFileage());
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      if (year == refYear && month == refMonth) {
        res.add(node);
      }
    }
    return res;
  }

  public List<Date> getDistinctDays(Date pMonth) {
    final SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
    String refMonthStr = sdfMonth.format(pMonth);
    final SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
    List<Date> res = new ArrayList<Date>();
    Map<String, String> dateStrMap = new HashMap<String, String>();
    for (FlameFlatNode node : nodes) {
      String monthStr = sdfMonth.format(node.getFileage());
      if (refMonthStr.equals(monthStr)) {
        String dayStr = sdfDay.format(node.getFileage());
        if (dateStrMap.get(dayStr) == null) {
          dateStrMap.put(dayStr, dayStr);
          res.add(node.getFileage());
        }
      }
    }
    return res;
  }

}
