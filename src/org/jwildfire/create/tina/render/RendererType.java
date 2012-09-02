package org.jwildfire.create.tina.render;

public enum RendererType {
  JAVA {
    @Override
    public String toString() {
      return "Java";
    }
  },
  CUDA {
    @Override
    public String toString() {
      return "C(UDA)";
    }
  }
}
