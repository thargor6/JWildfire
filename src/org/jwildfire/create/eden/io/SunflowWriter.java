package org.jwildfire.create.eden.io;

import org.jwildfire.base.Tools;
import org.jwildfire.create.eden.group.Group;
import org.jwildfire.create.eden.group.GroupMember;
import org.jwildfire.create.eden.primitive.Sphere;
import org.jwildfire.create.eden.scene.Scene;

public class SunflowWriter {

  public void writeScene(Scene pScene, String pFilename) throws Exception {
    Tools.writeUTF8Textfile(pFilename, createSunflowScene(pScene));
  }

  public String createSunflowScene(Scene pScene) {
    StringBuffer sb = new StringBuffer();
    sb.append("   image {\r\n" +
        "      resolution 1920 1080\r\n" +
        //        "      resolution 960 540\r\n" +
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
        "  diff 16\r\n" +
        "  refl 16\r\n" +
        "  refr 16\r\n" +
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
      else {
        throw new IllegalStateException();
      }
    }
  }

  private void writeSphere(StringBuffer pSB, Sphere pSphere) {
    pSB.append("object {\n" +
        (Math.random() <= 0.83 ? "  shader Glass\n" : "  shader Mirror\n") +
        "  type sphere\n" +
        "  c " + pSphere.getCentre().x + " " + pSphere.getCentre().z + " " + pSphere.getCentre().y + "\n" +
        "  r " + pSphere.getRadius() + "\n" +
        "}\n\n");
  }
}
