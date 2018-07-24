/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.swing;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.*;

import org.jwildfire.create.CreatorsList;
import org.jwildfire.create.ImageCreator;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.loader.ImageLoader;
import org.jwildfire.loader.LoadersList;
import org.jwildfire.swing.Buffer.BufferType;
import org.jwildfire.transform.Mesh3D;
import org.jwildfire.transform.ScaleAspect;
import org.jwildfire.transform.ScaleTransformer;
import org.jwildfire.transform.Transformer;
import org.jwildfire.transform.TransformersList;

public class ScriptProcessor {
  private final JFrame rootFrame;
  private final BufferList bufferList = new BufferList();
  private Transformer transformer = null;
  private ImageCreator creator = null;
  private ImageLoader loader = null;
  private boolean addBuffersToDesktop = false;

  public ScriptProcessor(JFrame pRootFrame) {
    rootFrame = pRootFrame;
  }

  public BufferList getBufferList() {
    return bufferList;
  }

  public Transformer getTransformer() {
    return transformer;
  }

  public ImageCreator getCreator() {
    return creator;
  }

  public ImageLoader getLoader() {
    return loader;
  }

  public Buffer loadImage(String pFilename) throws Exception {
    if (!new File(pFilename).exists())
      throw new FileNotFoundException(pFilename);
    String fileExt = null;
    {
      int p = pFilename.lastIndexOf(".");
      if (p >= 0 && p < pFilename.length() - 2) {
        fileExt = pFilename.substring(p + 1, pFilename.length());
      }
    }
    if ("hdr".equalsIgnoreCase(fileExt)) {
      SimpleHDRImage img = new ImageReader(rootFrame).loadHDRImage(pFilename);
      File file = new File(pFilename);
      Buffer buffer = bufferList.addHDRImageBuffer(addBuffersToDesktop ? rootFrame : null, file.getName(),
          img);
      return buffer;
    }
    else {
      SimpleImage img = new ImageReader(rootFrame).loadImage(pFilename);
      File file = new File(pFilename);
      Buffer buffer = bufferList.addImageBuffer(addBuffersToDesktop ? rootFrame : null, file.getName(),
          img);
      return buffer;
    }
  }

  public void selectTransformer(String pName) {
    transformer = TransformersList.getTransformerInstance(pName);
  }

  public void selectCreator(String pName) {
    creator = CreatorsList.getCreatorInstance(pName);
  }

  public Buffer executeCreator(int pWidth, int pHeight, String pOutputName, boolean pRecordAction) {
    SimpleImage img = creator.createImage(pWidth, pHeight);
    Buffer buffer = bufferList.addImageBuffer(addBuffersToDesktop ? rootFrame : null,
        creator.getName(), img);
    if ((pOutputName != null) && (pOutputName.length() > 0))
      buffer.setName(pOutputName);
    return buffer;
  }

  public void selectLoader(String pName) {
    loader = LoadersList.getLoaderInstance(rootFrame, pName);
  }

  public Buffer executeLoader(String pOutputName, boolean pRecordAction) {
    SimpleImage img = loader.execute();
    Buffer buffer = bufferList.addImageBuffer(addBuffersToDesktop ? rootFrame : null,
        loader.getName(), img);
    if ((pOutputName != null) && (pOutputName.length() > 0))
      buffer.setName(pOutputName);
    return buffer;
  }

  public static class TransformResult {
    private final Buffer inBuffer;
    private final Buffer outBuffer;
    private final Buffer outHDRBuffer;
    private final Buffer outBuffer3D;

    public TransformResult(Buffer pInBuffer, Buffer pOutBuffer, Buffer pOutHDRBuffer, Buffer pOutBuffer3D) {
      inBuffer = pInBuffer;
      outBuffer = pOutBuffer;
      outHDRBuffer = pOutHDRBuffer;
      outBuffer3D = pOutBuffer3D;
    }

    public Buffer getInBuffer() {
      return inBuffer;
    }

    public Buffer getOutBuffer() {
      return outBuffer;
    }

    public Buffer getOutBuffer3D() {
      return outBuffer3D;
    }

    public Buffer getOutHDRBuffer() {
      return outHDRBuffer;
    }
  }

  public TransformResult executeTransformer(String pInputName, boolean pStoreMesh3D,
      String pOutputName, String pOutput3DName, boolean pRecordAction) {
    Buffer inBuffer = bufferList.bufferByName(pInputName);
    if (inBuffer == null) {
      dumpBuffers();
      throw new RuntimeException("Input buffer <" + pInputName + "> not found");
    }
    SimpleImage newImg = null;
    SimpleHDRImage newHDRImg = null;
    if (inBuffer.getBufferType() == BufferType.IMAGE) {
      transformer.setStoreMesh3D(pStoreMesh3D);
      newImg = inBuffer.getImage().clone();
      transformer.transformImage(newImg);
    }
    else if (inBuffer.getBufferType() == BufferType.HDR_IMAGE) {
      transformer.setStoreMesh3D(pStoreMesh3D);
      newHDRImg = inBuffer.getHDRImage().clone();
      transformer.transformImage(newHDRImg);
    }
    else if (inBuffer.getBufferType() == BufferType.MESH3D) {
      transformer.setStoreMesh3D(pStoreMesh3D);
      Mesh3D mesh3D = inBuffer.getMesh3D();
      newImg = new SimpleImage(mesh3D.getImageWidth(), mesh3D.getImageHeight());
      transformer.setInputMesh3D(mesh3D);
      transformer.transformImage(newImg);
    }
    Buffer outBuffer = null;
    if (newImg != null) {
      outBuffer = bufferList.addImageBuffer(addBuffersToDesktop ? rootFrame : null,
          transformer.getName(), newImg);
      if ((pOutputName != null) && (pOutputName.length() > 0))
        outBuffer.setName(pOutputName);
    }
    Buffer outHDRBuffer = null;
    if (newHDRImg != null) {
      outHDRBuffer = bufferList.addHDRImageBuffer(addBuffersToDesktop ? rootFrame : null,
          transformer.getName(), newHDRImg);
      if ((pOutputName != null) && (pOutputName.length() > 0))
        outHDRBuffer.setName(pOutputName);
    }
    Buffer outBuffer3D = null;
    if (pStoreMesh3D) {
      ScaleTransformer scaleT = new ScaleTransformer();
      scaleT.setAspect(ScaleAspect.KEEP_WIDTH);
      scaleT.setUnit(ScaleTransformer.Unit.PIXELS);
      scaleT.setScaleWidth(120);
      SimpleImage scaledImg = newImg.clone();
      scaleT.transformImage(scaledImg);
      outBuffer3D = bufferList.addMesh3DBuffer(addBuffersToDesktop ? rootFrame : null, pInputName,
          transformer.getOutputMesh3D(true), scaledImg);
      if ((pOutput3DName != null) && (pOutput3DName.length() > 0))
        outBuffer3D.setName(pOutput3DName);
    }
    return new TransformResult(inBuffer, outBuffer, outHDRBuffer, outBuffer3D);
  }

  private void dumpBuffers() {
    System.out.println("BUFFERS:");
    for (Buffer buffer : bufferList) {
      System.out.println(buffer.getName() + "#" + buffer.getBufferType());
    }
  }

  public void saveLastImage(String pFilename) throws Exception {
    SimpleImage img = getLastImage();
    if (img == null)
      throw new IllegalStateException();
    new ImageWriter().saveAsJPEG(img, pFilename);
  }

  public void setAddBuffersToDesktop(boolean addBuffersToDesktop) {
    this.addBuffersToDesktop = addBuffersToDesktop;
  }

  public SimpleImage getLastImage() {
    for (int i = bufferList.size() - 1; i >= 0; i--) {
      Buffer buffer = bufferList.get(i);
      if (buffer.getBufferType() == BufferType.IMAGE) {
        return buffer.getImage();
      }
    }
    return null;
  }

}
