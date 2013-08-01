package org.jwildfire.create.tina.browser;

import java.io.Serializable;
import java.util.Date;

public class FlameFlatNode implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String filename;
  private final String caption;
  private final Date fileage;

  public FlameFlatNode(String pFilename, String pCaption, Date pFileage) {
    filename = pFilename;
    caption = pCaption;
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
}
