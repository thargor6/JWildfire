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
package org.jwildfire.transform;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class TextTransformer extends Mesh2DTransformer {
  public enum FontStyle {
    PLAIN, BOLD, ITALIC
  }

  public enum Mode {
    NORMAL, OUTLINE
  }

  public enum HAlignment {
    CENTRE, LEFT, RIGHT, NONE
  }

  public enum VAlignment {
    CENTRE, TOP, BOTTOM, NONE
  }

  @Property(description = "1st row of text")
  private String text1;

  @Property(description = "2nd row of text")
  private String text2;

  @Property(description = "3rd row of text")
  private String text3;

  @Property(description = "4th row of text")
  private String text4;

  @Property(description = "5th row of text")
  private String text5;

  @Property(description = "6th row of text")
  private String text6;

  @Property(description = "7th row of text")
  private String text7;

  @Property(category = PropertyCategory.SECONDARY, description = "Horizontal alignment of the text", editorClass = HAlignmentEditor.class)
  private HAlignment hAlign = HAlignment.CENTRE;

  @Property(category = PropertyCategory.SECONDARY, description = "Vertical alignment of the text", editorClass = VAlignmentEditor.class)
  private VAlignment vAlign = VAlignment.CENTRE;

  @Property(description = "Name of the font", editorClass = FontNameEditor.class)
  private String fontName;

  @Property(description = "Size of the font")
  private int fontSize;

  @Property(description = "Offset to the base-line of the font")
  private int baseLineOffset = 0;

  @Property(description = "Style of the font", editorClass = FontStyleEditor.class, category = PropertyCategory.SECONDARY)
  private FontStyle fontStyle;

  @Property(description = "Text color")
  private Color color;

  @Property(description = "Text mode", editorClass = ModeEditor.class)
  private Mode mode;

  @Property(description = "Text position in x-direction (when no alignment")
  private int posX;

  @Property(description = "Text position in y-direction (when no alignment")
  private int posY;

  @PropertyMin(1)
  @Property(description = "Outline width (mode=OUTLINE)", category = PropertyCategory.SECONDARY)
  private int outlineWidth;

  @Property(description = "Enable antialiasing")
  private boolean antialiasing;

  private static class TextRenderInfo {
    private Font font;
    int maxWidth = 0, maxHeight = 0, maxRow = 0;
    int yOffset = 0;
    int areaHeight;
  }

  private TextRenderInfo createTextRenderInfo(SimpleImage pImg, List<TextRow> pRows) {
    // create font and calculate the row sizes
    TextRenderInfo res = new TextRenderInfo();
    {
      Graphics g = pImg.getGraphics();
      {
        int fontStyle;
        switch (this.fontStyle) {
          case BOLD:
            fontStyle = Font.BOLD;
            break;
          case ITALIC:
            fontStyle = Font.ITALIC;
            break;
          default:
            fontStyle = Font.PLAIN;
            break;
        }
        res.font = new Font(this.fontName, fontStyle, this.fontSize);
      }
      g.setFont(res.font);
      FontMetrics fm = g.getFontMetrics();
      res.yOffset = fm.getMaxAscent();
      FontRenderContext frc = fm.getFontRenderContext();
      for (TextRow row : pRows) {
        Rectangle2D rect = res.font.getStringBounds(row.text, frc);
        row.width = (int) (rect.getWidth() + 0.5);
        row.height = (int) (rect.getHeight() + 0.5);
        if (this.mode == Mode.OUTLINE) {
          row.width += 2 * this.outlineWidth;
          row.height += 2 * this.outlineWidth;
        }
        row.height += baseLineOffset;
        if (row.width > res.maxWidth)
          res.maxWidth = row.width;
        if (row.height > res.maxHeight)
          res.maxHeight = row.height;
        if (row.row > res.maxRow)
          res.maxRow = row.row;
      }
    }
    res.areaHeight = (res.maxRow + 1) * res.maxHeight;
    return res;
  }

  public Dimension calculateTextSize() {
    SimpleImage img = new SimpleImage(320, 256);
    List<TextRow> rows = getRows();
    if ((rows == null) || (rows.size() < 1))
      return new Dimension(0, 0);
    TextRenderInfo renderInfo = createTextRenderInfo(img, rows);
    return new Dimension(renderInfo.maxWidth, renderInfo.areaHeight);
  }

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    List<TextRow> rows = getRows();
    if ((rows == null) || (rows.size() < 1))
      return;

    // calculate the offsets
    // int areaWidth = maxWidth;
    TextRenderInfo renderInfo = createTextRenderInfo(img, rows);
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    for (TextRow row : rows) {
      switch (hAlign) {
        case CENTRE:
          row.left = (width - row.width) / 2;
          break;
        case LEFT:
          row.left = 0;
          break;
        case RIGHT:
          row.left = width - row.width;
          break;
        case NONE:
          row.left = posX;
      }
      switch (vAlign) {
        case CENTRE:
          row.top = (height - renderInfo.areaHeight) / 2 + row.row * renderInfo.maxHeight;
          break;
        case TOP:
          row.top = row.row * renderInfo.maxHeight;
          break;
        case BOTTOM:
          row.top = height - (renderInfo.maxRow + 1 - row.row) * renderInfo.maxHeight;
          break;
        case NONE:
          row.top = posY + row.row * renderInfo.maxHeight;
      }
      row.top += baseLineOffset;
    }

    // draw the text
    int offLeft, offRight;
    if (this.mode == Mode.OUTLINE) {
      offLeft = offRight = this.outlineWidth;
    }
    else {
      offLeft = offRight = 0;
    }
    switch (this.mode) {
      case NORMAL: {
        Graphics g = img.getGraphics();
        g.setFont(renderInfo.font);
        g.setColor(this.color);
        Graphics2D g2d = (Graphics2D) g;
        if (antialiasing) {
          g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
              RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        for (TextRow row : rows) {
          g.drawString(row.text, offLeft + row.left, offRight + row.top + renderInfo.yOffset);
        }
      }
        break;
      case OUTLINE: {
        SimpleImage textImg = new SimpleImage(width, height);
        Graphics g = textImg.getGraphics();
        g.setFont(renderInfo.font);
        g.setColor(this.color);
        Graphics2D g2d = (Graphics2D) g;
        if (antialiasing) {
          g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
              RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        for (TextRow row : rows) {
          g.drawString(row.text, offLeft + row.left, offRight + row.top + renderInfo.yOffset);
        }
        SimpleImage dilateImg = textImg.clone();
        {
          ErodeTransformer eT = new ErodeTransformer();
          eT.setMode(ErodeTransformer.Mode.DILATE);
          eT.setSize(2 * this.outlineWidth + 1);
          eT.setShape(ErodeTransformer.Shape.DISK);
          eT.transformImage(dilateImg);
        }
        {
          AddTransformer aT = new AddTransformer();
          aT.setMode(AddTransformer.Mode.SUBTRACT);
          aT.setForegroundImage(textImg);
          aT.transformImage(dilateImg);
        }
        {
          AlphaComposeTransformer cT = new AlphaComposeTransformer();
          cT.setForegroundImage(dilateImg);
          SimpleImage alphaImg = dilateImg.clone();
          new ColorToGrayTransformer().transformImage(alphaImg);
          cT.setAlphaChannelImage(alphaImg);
          cT.transformImage(img);
        }
      }
        break;
    }

    // apply the text mask to the image
    /*
    pImg.setBufferedImage(textImg.getBufferedImg(), textImg.getImageWidth(),
        textImg.getImageHeight());*/
  }

  private List<TextRow> getRows() {
    List<TextRow> res = new ArrayList<TextRow>();
    if ((text1 != null) && (text1.length() > 0))
      res.add(new TextRow(0, text1));
    if ((text2 != null) && (text2.length() > 0))
      res.add(new TextRow(1, text2));
    if ((text3 != null) && (text3.length() > 0))
      res.add(new TextRow(2, text3));
    if ((text4 != null) && (text4.length() > 0))
      res.add(new TextRow(3, text4));
    if ((text5 != null) && (text5.length() > 0))
      res.add(new TextRow(4, text5));
    if ((text6 != null) && (text6.length() > 0))
      res.add(new TextRow(5, text6));
    if ((text7 != null) && (text7.length() > 0))
      res.add(new TextRow(6, text7));
    return res;
  }

  private static class TextRow {
    public int row;
    public String text;
    public int width;
    public int height;
    public int left;
    public int top;

    public TextRow(int pRow, String pText) {
      row = pRow;
      text = pText;
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    text1 = Tools.APP_TITLE;
    text2 = "is";
    text3 = "cool :-)";
    fontName = "Arial";
    fontStyle = FontStyle.PLAIN;
    fontSize = 120;
    color = new Color(255, 0, 51);
    mode = Mode.NORMAL;
    outlineWidth = 3;
    antialiasing = true;
    hAlign = HAlignment.CENTRE;
    vAlign = VAlignment.CENTRE;
  }

  public static class FontStyleEditor extends ComboBoxPropertyEditor {
    public FontStyleEditor() {
      super();
      setAvailableValues(new FontStyle[] { FontStyle.PLAIN, FontStyle.BOLD, FontStyle.ITALIC });
    }
  }

  public static class ModeEditor extends ComboBoxPropertyEditor {
    public ModeEditor() {
      super();
      setAvailableValues(new Mode[] { Mode.NORMAL, Mode.OUTLINE });
    }
  }

  private static String[] availableFontNames = null;

  private static String[] getAvailableFontNames() {
    if (availableFontNames == null) {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      availableFontNames = ge.getAvailableFontFamilyNames();
    }
    return availableFontNames;
  }

  public static class FontNameEditor extends ComboBoxPropertyEditor {
    public FontNameEditor() {
      super();
      setAvailableValues(getAvailableFontNames());
    }
  }

  public String getFontName() {
    return fontName;
  }

  public void setFontName(String fontName) {
    this.fontName = fontName;
  }

  public int getFontSize() {
    return fontSize;
  }

  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }

  public FontStyle getFontStyle() {
    return fontStyle;
  }

  public void setFontStyle(FontStyle fontStyle) {
    this.fontStyle = fontStyle;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public int getOutlineWidth() {
    return outlineWidth;
  }

  public void setOutlineWidth(int outlineWidth) {
    this.outlineWidth = outlineWidth;
  }

  public boolean isAntialiasing() {
    return antialiasing;
  }

  public void setAntialiasing(boolean antialiasing) {
    this.antialiasing = antialiasing;
  }

  public String getText1() {
    return text1;
  }

  public void setText1(String text1) {
    this.text1 = text1;
  }

  public String getText2() {
    return text2;
  }

  public void setText2(String text2) {
    this.text2 = text2;
  }

  public String getText3() {
    return text3;
  }

  public void setText3(String text3) {
    this.text3 = text3;
  }

  public String getText4() {
    return text4;
  }

  public void setText4(String text4) {
    this.text4 = text4;
  }

  public String getText5() {
    return text5;
  }

  public void setText5(String text5) {
    this.text5 = text5;
  }

  public String getText6() {
    return text6;
  }

  public void setText6(String text6) {
    this.text6 = text6;
  }

  public String getText7() {
    return text7;
  }

  public void setText7(String text7) {
    this.text7 = text7;
  }

  public static class HAlignmentEditor extends ComboBoxPropertyEditor {
    public HAlignmentEditor() {
      super();
      setAvailableValues(new HAlignment[] { HAlignment.CENTRE, HAlignment.LEFT, HAlignment.RIGHT, HAlignment.NONE });
    }
  }

  public static class VAlignmentEditor extends ComboBoxPropertyEditor {
    public VAlignmentEditor() {
      super();
      setAvailableValues(new VAlignment[] { VAlignment.CENTRE, VAlignment.TOP, VAlignment.BOTTOM, VAlignment.NONE });
    }
  }

  public HAlignment getHAlign() {
    return hAlign;
  }

  public void setHAlign(HAlignment hAlign) {
    this.hAlign = hAlign;
  }

  public VAlignment getVAlign() {
    return vAlign;
  }

  public void setVAlign(VAlignment vAlign) {
    this.vAlign = vAlign;
  }

  public int getBaseLineOffset() {
    return baseLineOffset;
  }

  public void setBaseLineOffset(int baseLineOffset) {
    this.baseLineOffset = baseLineOffset;
  }

  public int getPosX() {
    return posX;
  }

  public void setPosX(int posX) {
    this.posX = posX;
  }

  public int getPosY() {
    return posY;
  }

  public void setPosY(int posY) {
    this.posY = posY;
  }
}
