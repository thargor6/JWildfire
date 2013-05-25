package org.jwildfire.create.tina.swing;

public enum RandomBatchRefreshType {
  CLEAR,
  INSERT,
  APPEND;

  public static Object getDefaultValue() {
    return CLEAR;
  }
}
