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

// Nearly the same as ImagePanel from sunflow, but with access to image-buffer
package org.jwildfire.create.tina.render.filter;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import org.sunflow.core.Display;
import org.sunflow.image.Color;

@SuppressWarnings("serial")
public class SunFlowImagePanel extends JPanel implements Display {
  private static final int[] BORDERS = { Color.RED.toRGB(),
      Color.GREEN.toRGB(), Color.BLUE.toRGB(), Color.YELLOW.toRGB(),
      Color.CYAN.toRGB(), Color.MAGENTA.toRGB() };
  private BufferedImage image;
  private float xo, yo;
  private float w, h;
  private long repaintCounter;

  private class ScrollZoomListener extends MouseInputAdapter {
    int mx;
    int my;
    boolean dragging;
    boolean zooming;

    @Override
    public void mousePressed(MouseEvent e) {
      mx = e.getX();
      my = e.getY();
      switch (e.getButton()) {
        case MouseEvent.BUTTON1:
          dragging = true;
          zooming = false;
          break;
        case MouseEvent.BUTTON2: {
          dragging = zooming = false;
          // if CTRL is pressed
          if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK)
            fit();
          else
            reset();
          break;
        }
        case MouseEvent.BUTTON3:
          zooming = true;
          dragging = false;
          break;
        default:
          return;
      }
      repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      int mx2 = e.getX();
      int my2 = e.getY();
      if (dragging)
        drag(mx2 - mx, my2 - my);
      if (zooming)
        zoom(mx2 - mx, my2 - my);
      mx = mx2;
      my = my2;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      // same behaviour
      mouseDragged(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      zoom(-20 * e.getWheelRotation(), 0);
    }
  }

  public SunFlowImagePanel() {
    setPreferredSize(new Dimension(640, 480));
    image = null;
    xo = yo = 0;
    w = h = 0;
    ScrollZoomListener listener = new ScrollZoomListener();
    addMouseListener(listener);
    addMouseMotionListener(listener);
    addMouseWheelListener(listener);
  }

  public void save(String filename) {
    // Bitmap.save(image, filename);
    try {
      ImageIO.write(image, "png", new File(filename));
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private synchronized void drag(int dx, int dy) {
    xo += dx;
    yo += dy;
    repaint();
  }

  private synchronized void zoom(int dx, int dy) {
    int a = Math.max(dx, dy);
    int b = Math.min(dx, dy);
    if (Math.abs(b) > Math.abs(a))
      a = b;
    if (a == 0)
      return;
    // window center
    float cx = getWidth() * 0.5f;
    float cy = getHeight() * 0.5f;

    // origin of the image in window space
    float x = xo + (getWidth() - w) * 0.5f;
    float y = yo + (getHeight() - h) * 0.5f;

    // coordinates of the pixel we are over
    float sx = cx - x;
    float sy = cy - y;

    // scale
    if (w + a > 100) {
      h = (w + a) * h / w;
      sx = (w + a) * sx / w;
      sy = (w + a) * sy / w;
      w = (w + a);
    }

    // restore center pixel

    float x2 = cx - sx;
    float y2 = cy - sy;

    xo = (x2 - (getWidth() - w) * 0.5f);
    yo = (y2 - (getHeight() - h) * 0.5f);

    repaint();
  }

  public synchronized void reset() {
    xo = yo = 0;
    if (image != null) {
      w = image.getWidth();
      h = image.getHeight();
    }
    repaint();
  }

  public synchronized void fit() {
    xo = yo = 0;
    if (image != null) {
      float wx = Math.max(getWidth() - 10, 100);
      float hx = wx * image.getHeight() / image.getWidth();
      float hy = Math.max(getHeight() - 10, 100);
      float wy = hy * image.getWidth() / image.getHeight();
      if (hx > hy) {
        w = wy;
        h = hy;
      }
      else {
        w = wx;
        h = hx;
      }
      repaint();
    }
  }

  public synchronized void imageBegin(int w, int h, int bucketSize) {
    if (image != null && w == image.getWidth() && h == image.getHeight()) {
      // dull image if it has same resolution (75%)
      for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
          int rgba = image.getRGB(x, y);
          image.setRGB(x, y, ((rgba & 0xFEFEFEFE) >>> 1) + ((rgba & 0xFCFCFCFC) >>> 2));
        }
      }
    }
    else {
      // allocate new framebuffer
      image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      // center
      this.w = w;
      this.h = h;
      xo = yo = 0;
    }
    repaintCounter = System.nanoTime();
    repaint();
  }

  public synchronized void imagePrepare(int x, int y, int w, int h, int id) {
    int border = BORDERS[id % BORDERS.length] | 0xFF000000;
    for (int by = 0; by < h; by++) {
      for (int bx = 0; bx < w; bx++) {
        if (bx == 0 || bx == w - 1) {
          if (5 * by < h || 5 * (h - by - 1) < h)
            image.setRGB(x + bx, y + by, border);
        }
        else if (by == 0 || by == h - 1) {
          if (5 * bx < w || 5 * (w - bx - 1) < w)
            image.setRGB(x + bx, y + by, border);
        }
      }
    }
    repaint();
  }

  public synchronized void imageUpdate(int x, int y, int w, int h, Color[] data, float[] alpha) {
    for (int j = 0, index = 0; j < h; j++)
      for (int i = 0; i < w; i++, index++)
        image.setRGB(x + i, y + j, data[index].copy().mul(1.0f / alpha[index]).toNonLinear().toRGBA(alpha[index]));
    repaint();
  }

  public synchronized void imageFill(int x, int y, int w, int h, Color c, float alpha) {
    int rgba = c.copy().mul(1.0f / alpha).toNonLinear().toRGBA(alpha);
    for (int j = 0, index = 0; j < h; j++)
      for (int i = 0; i < w; i++, index++)
        image.setRGB(x + i, y + j, rgba);
    fastRepaint();
  }

  public void imageEnd() {
    repaint();
  }

  private void fastRepaint() {
    long t = System.nanoTime();
    if (repaintCounter + 125000000 < t) {
      repaintCounter = t;
      repaint();
    }
  }

  @Override
  public synchronized void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (image == null)
      return;
    int x = Math.round(xo + (getWidth() - w) * 0.5f);
    int y = Math.round(yo + (getHeight() - h) * 0.5f);
    int iw = Math.round(w);
    int ih = Math.round(h);
    int x0 = x - 1;
    int y0 = y - 1;
    int x1 = x + iw + 1;
    int y1 = y + ih + 1;
    g.setColor(java.awt.Color.WHITE);
    g.drawLine(x0, y0, x1, y0);
    g.drawLine(x1, y0, x1, y1);
    g.drawLine(x1, y1, x0, y1);
    g.drawLine(x0, y1, x0, y0);
    g.drawImage(image, x, y, iw, ih, java.awt.Color.BLACK, this);
  }

  public BufferedImage getImage() {
    return image;
  }
}
