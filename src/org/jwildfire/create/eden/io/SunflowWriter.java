package org.jwildfire.create.eden.io;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.eden.group.Group;
import org.jwildfire.create.eden.group.GroupMember;
import org.jwildfire.create.eden.primitive.Box;
import org.jwildfire.create.eden.primitive.Cylinder;
import org.jwildfire.create.eden.primitive.Point;
import org.jwildfire.create.eden.primitive.Primitive;
import org.jwildfire.create.eden.primitive.Sphere;
import org.jwildfire.create.eden.primitive.Torus;
import org.jwildfire.create.eden.scene.Scene;

public class SunflowWriter {

  public void writeScene(Scene pScene, String pFilename) throws Exception {
    Tools.writeUTF8Textfile(pFilename, createSunflowScene(pScene));
  }

  public String createSunflowScene(Scene pScene) {
    StringBuffer sb = new StringBuffer();
    sb.append("   image {\r\n" +
        //        "      resolution 1920 1080\r\n" +
        //        "      resolution 960 540\r\n" +
        "      resolution 720 405\r\n" +
        "      aa 1 1\r\n" +
        "      samples 8\r\n" +
        "      filter gaussian\r\n" +
        "    }\r\n");
    sb.append("camera {\r\n" +
        "    type pinhole\r\n" +
        "      eye    0 -340.443 73.6866\r\n" +
        "      target 0 29.155 59.1488\r\n" +
        "    up 0 0 1\r\n" +
        "    fov 42\r\n" +
        "    aspect 1.7778\r\n" +
        " }\r\n" +
        "");
    sb.append("trace-depths {\r\n" +
        "  diff 8\r\n" +
        "  refl 7\r\n" +
        "  refr 6\r\n" +
        "}\r\n" +
        "");

    sb.append("    light {\n" +
        "      type sunsky\n" +
        "      up 0 0 1\n" +
        "      east 0 1 0\n" +
        "      sundir 1 -1 0.831\n" +
        "      turbidity 2\n" +
        "      samples 16\n" +
        "    }\n\n" +
        "");
    sb.append("    shader {\n" +
        "      name ground.shader\n" +
        "      type diffuse\n" +
        "      diff 0.42 0.03 0.01\n" +
        "    }\n\n" +
        "");
    sb.append("    object {\n" +
        "      shader ground.shader\n" +
        "      type plane\n" +
        "      p 0 0 0\n" +
        "      n 0 0 1\n" +
        "    }\n\n" +
        "");
    sb.append("    shader {\n" +
        "      name Glass\n" +
        "      type glass\n" +
        "      eta 1.55\n" +
        "      color 1 1 1\n" +
        "    }\n\n" +
        "");
    sb.append("    shader {\n" +
        "      name Mirror\n" +
        "      type shiny\n" +
        "      diff 0.2 0.2 0.2\n" +
        "      refl 3.5\n" +
        "    }\n\n" +
        "");
    writeGroup(sb, pScene.getRootGroup());
    return sb.toString();
  }

  private void writeGroup(StringBuffer pSB, Group pGroup) {
    for (GroupMember member : pGroup.getMembers()) {
      if (member instanceof Group) {
        writeGroup(pSB, (Group) member);
      }
      else if (member instanceof Sphere) {
        writeSphere(pSB, (Sphere) member);
      }
      else if (member instanceof Box) {
        writeBox(pSB, (Box) member);
      }
      else if (member instanceof Cylinder) {
        writeCylinder(pSB, (Cylinder) member);
      }
      else if (member instanceof Torus) {
        writeTorus(pSB, (Torus) member);
      }
      else {
        throw new IllegalStateException();
      }
    }
  }

  private void writeTransform(StringBuffer pSB, Primitive pPrimitive) {
    Point position = pPrimitive.getPosition();
    Point rotate = pPrimitive.getRotate();
    Point size = pPrimitive.getSize();
    if (Math.abs(position.getX()) > MathLib.EPSILON || Math.abs(position.getY()) > MathLib.EPSILON || Math.abs(position.getZ()) > MathLib.EPSILON ||
        Math.abs(rotate.getX()) > MathLib.EPSILON || Math.abs(rotate.getY()) > MathLib.EPSILON || Math.abs(rotate.getZ()) > MathLib.EPSILON ||
        Math.abs(size.getX() - 1.0) > MathLib.EPSILON || Math.abs(size.getY() - 1.0) > MathLib.EPSILON || Math.abs(size.getZ() - 1.0) > MathLib.EPSILON) {
      pSB.append("  transform {\n");
      if (Math.abs(rotate.getX()) > MathLib.EPSILON) {
        pSB.append("    rotatex " + pPrimitive.getRotate().getX() + "\n");
      }
      if (Math.abs(rotate.getY()) > MathLib.EPSILON) {
        pSB.append("    rotatez " + (-pPrimitive.getRotate().getY()) + "\n");
      }
      if (Math.abs(rotate.getZ()) > MathLib.EPSILON) {
        pSB.append("    rotatey " + (-pPrimitive.getRotate().getZ()) + "\n");
      }
      if (Math.abs(size.getX() - 1.0) > MathLib.EPSILON || Math.abs(size.getY() - 1.0) > MathLib.EPSILON || Math.abs(size.getZ() - 1.0) > MathLib.EPSILON) {
        pSB.append("    scale " + pPrimitive.getSize().getX() + " " + pPrimitive.getSize().getZ() + " " + pPrimitive.getSize().getY() + "\n");
      }
      if (Math.abs(position.getX()) > MathLib.EPSILON || Math.abs(position.getY()) > MathLib.EPSILON || Math.abs(position.getZ()) > MathLib.EPSILON) {
        pSB.append("    translate " + pPrimitive.getPosition().getX() + " " + pPrimitive.getPosition().getZ() + " " + pPrimitive.getPosition().getY() + "\n");
      }
      pSB.append("  }\n");
    }
  }

  //             parameter("transform", Matrix4.translation(80, -50, 100).multiply(Matrix4.rotateX((float) -Math.PI / 6)).multiply(Matrix4.rotateY((float) Math.PI / 4)).multiply(Matrix4.rotateX((float) -Math.PI / 2).multiply(Matrix4.scale(1.2f))));

  private void writeSphere(StringBuffer pSB, Sphere pSphere) {
    pSB.append("object {\n" +
        (Math.random() <= 0.83 ? "  shader Glass\n" : "  shader Mirror\n"));
    writeTransform(pSB, pSphere);
    pSB.append("  type sphere\n" +
        "}\n\n");
  }

  private void writeCylinder(StringBuffer pSB, Cylinder pCylinder) {
    pSB.append("object {\n" +
        (Math.random() <= 0.83 ? "  shader Glass\n" : "  shader Mirror\n"));
    writeTransform(pSB, pCylinder);
    pSB.append("  type cylinder\n" +
        "}\n\n");
  }

  private void writeBox(StringBuffer pSB, Box pBox) {
    pSB.append("object {\n" +
        (Math.random() <= 0.83 ? "  shader Glass\n" : "  shader Mirror\n"));
    writeTransform(pSB, pBox);
    pSB.append("  type box\n" +
        "  points 2\n" +
        "  -1 -1 -1\n" +
        "  1 1 1\n" +
        "}\n\n");
  }

  private void writeTorus(StringBuffer pSB, Torus pTorus) {
    pSB.append("object {\n" +
        (Math.random() <= 0.75 ? "  shader Glass\n" : "  shader Mirror\n"));
    writeTransform(pSB, pTorus);
    pSB.append("  type torus\n" +
        "  r " + pTorus.getInnerRadius() + " 1.0\n" +
        "}\n\n");
  }

}
