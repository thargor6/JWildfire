/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.eden.scene;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.eden.base.Point3f;
import org.jwildfire.create.eden.scene.light.PointLight;
import org.jwildfire.create.eden.scene.light.SkyLight;
import org.jwildfire.create.eden.scene.primitive.Box;
import org.jwildfire.create.eden.scene.primitive.Cylinder;
import org.jwildfire.create.eden.scene.primitive.Mesh;
import org.jwildfire.create.eden.scene.primitive.Sphere;

public class SceneElementGroup extends PositionableSceneElement {
  private final List<SceneElement> elements = new ArrayList<SceneElement>();

  public SceneElementGroup(PositionableSceneElement pParent) {
    super(pParent);
  }

  public List<SceneElement> getElements() {
    return elements;
  }

  public Sphere addSphere(double pOriginX, double pOriginY, double pOriginZ, double pRadius) {
    Sphere sphere = new Sphere(this);
    sphere.setRadius((float) pRadius);
    sphere.setPosition(new Point3f(pOriginX, pOriginY, pOriginZ));
    elements.add(sphere);
    return sphere;
  }

  public Cylinder addCylinder(double pOriginX, double pOriginY, double pOriginZ, double pRadius, double pHeight) {
    Cylinder cylinder = new Cylinder(this);
    cylinder.setRadius((float) pRadius);
    cylinder.setHeight((float) pHeight);
    cylinder.setPosition(new Point3f(pOriginX, pOriginY, pOriginZ));
    elements.add(cylinder);
    return cylinder;
  }

  public PointLight addPointLight(double pOriginX, double pOriginY, double pOriginZ, double pIntensity) {
    PointLight light = new PointLight(this);
    light.setPosition(new Point3f(pOriginX, pOriginY, pOriginZ));
    light.setIntensity(pIntensity);
    elements.add(light);
    return light;
  }

  public SkyLight addSkyLight() {
    SkyLight light = new SkyLight(this);
    elements.add(light);
    return light;
  }

  public Box addBox(double pOriginX, double pOriginY, double pOriginZ, double pSize) {
    Box box = new Box(this);
    box.setSize(pSize, pSize, pSize);
    box.setPosition(new Point3f(pOriginX, pOriginY, pOriginZ));
    elements.add(box);
    return box;
  }

  public Mesh addMesh() {
    Mesh mesh = new Mesh(this);
    elements.add(mesh);
    return mesh;
  }

  public SceneElementGroup addGroup() {
    SceneElementGroup group = new SceneElementGroup(this);
    elements.add(group);
    return group;
  }

  @Override
  public void accept(SceneElementVisitor pVisitor) {
    pVisitor.visitBefore(this);
    for (SceneElement child : elements) {
      child.accept(pVisitor);
    }
    pVisitor.visitAfter(this);
  }

}
