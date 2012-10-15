package org.jwildfire.create.tina.render;

public enum RendererType {
  JAVA {
    @Override
    public String toString() {
      return "Java (internal)";
    }
  },
  C32 {
    @Override
    public String toString() {
      return "C++ 32Bit (external)";
    }
  },
  C64 {
    @Override
    public String toString() {
      return "C++ 64Bit (external)";
    }
  }

}
