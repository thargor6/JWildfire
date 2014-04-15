package org.jwildfire.create.tina.browser;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import org.jwildfire.base.Tools;

public class FlameFlatNode implements Serializable {
  private static final long serialVersionUID = 1L;
  private String filename;
  private String caption;
  private final Date fileage;
  private boolean removed;

  public FlameFlatNode(String pFilename, Date pFileage) {
    setFilename(pFilename);
    fileage = pFileage;
  }

  public String getFilename() {
    return filename;
  }

  public String getCaption() {
    return caption;
  }

  public Date getFileage() {
    return fileage;
  }

  public void setFilename(String pFilename) {
    filename = pFilename;
    File f = new File(pFilename);
    caption = f.getName().substring(0, f.getName().length() - Tools.FILEEXT_FLAME.length() - 1);
  }

  public boolean isRemoved() {
    return removed;
  }

  public void setRemoved(boolean pRemoved) {
    removed = pRemoved;
  }
}
