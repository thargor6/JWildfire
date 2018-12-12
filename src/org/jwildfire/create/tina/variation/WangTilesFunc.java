/*
JWildfire - an image and animation processor written in Java
Copyright (C) 1995-2012 Andreas Maschke

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
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.io.ImageReader;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * wangtiles
 *
 * @author Jesus Sosa
 * @date April 29, 2018
 * based on a work of:
 * https://en.wikipedia.org/wiki/Wang_tile
 * http://www.cr31.co.uk/stagecast/wang/intro.html
 */

public class WangTilesFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_ID = "id";
  public static final String PARAM_SEED = "seed";
  public static final String PARAM_SQUARE = "square";
  public static final String PARAM_SCALEX = "scale_x";
  public static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_SCALEZ = "scale_z";
  private static final String PARAM_OFFSETX = "offset_x";
  private static final String PARAM_OFFSETY = "offset_y";
  private static final String PARAM_OFFSETZ = "offset_z";
  private static final String PARAM_TILEX = "tile_x";
  private static final String PARAM_TILEY = "tile_y";
  private static final String PARAM_RESETZ = "reset_z";


  private static final String[] paramNames = {PARAM_ID, PARAM_SEED, PARAM_SQUARE, PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_TILEX, PARAM_TILEY, PARAM_RESETZ};

  private int id = 0;
  private int seed = 1000;
  private int square = 1;
  private double scaleX = 1.0;
  private double scaleY = 1.0;
  private double scaleZ = 0.0;
  private double offsetX = 0.0;
  private double offsetY = 0.0;
  private double offsetZ = 0.0;
  private int tileX = 1;
  private int tileY = 1;
  private int resetZ = 1;
  private int dc_color = 1;
  private int blend_colormap = 0;

  // derived params
  private int imgWidth, imgHeight;
  private Pixel toolPixel = new Pixel();
  private float[] rgbArray = new float[3];

  private Random rnd = null;


  private static final String[] Id = {"block2/bloc", "block2/bowtie", "block2/box", "block2/diag",
          "block2/pool", "block2/spiral", "block2/square", "block2/steps", "block2/1edge2a", "block2/1corn2a", "block2/1corn2b", /*0 --10*/
          "block4/arrow", "block4/box", "block4/brench", "block4/bubble", "block4/braid", "block4/dublin", "block4/fence",
          "block4/floor1", "block4/floor2", "block4/floor3", "block4/fold", "block4/geo", "block4/madrid", "block4/oslo", "block4/panel",
          "block4/rome", "block4/wall", "block4/wedge", "block4/1edge4a", "block4/1edge4b",                                           /* 11-30 */
          "edge2/angular", "edge2/border", "edge2/brench", "edge2/brickwall", "edge2/bridge", "edge2/celtic", "edge2/circuit",
          "edge2/dual", "edge2/drawn", "edge2/dropbox", "edge2/glob", "edge2/greek", "edge2/groove", "edge2/laser",
          "edge2/ledge", "edge2/line", "edge2/molecule",/*"edge2/path",*/"edge2/pipe", "edge2/pipe2", "edge2/rail", "edge2/road",
          "edge2/tilt", "edge2/trench", "edge2/urban", "edge2/walkway", "edge2/arrow", "edge2/balls", "edge2/lattice", "edge2/octal",
          "edge2/quad", "edge2/wang2e", /*31-61*/
          "edge3/flow", "edge3/celtic3e", "edge3/puzzle", "edge3/walkways", "edge3/wang3e", /*62-66*/
          "corn2/border", "corn2/glob", "corn2/path", "corn2/roof", "corn2/beam", "corn2/cliff", "corn2/ground", "corn2/island", "corn2/lido",
          "corn2/patch", "corn2/pcb", "corn2/sandgrass", "corn2/terrain", "corn2/wang2c", /*67-80*/
          "corn3/campus", "corn3/puzzle", "corn3/wang3c", /*81-83 */
          "truch/tru1", "truch/tru2", "truch/tru3", "truch/tru4", "truch/tru5", "truch/tru6", "truch/tru7", "truch/tru8", /*84-91 */
          "twin/brench", "twin/pipes", "twin/routes", "twin/wang", /*92-95 */
          "blob0/bridge", "blob0/commune", "blob0/dungeon", "blob0/trench", "blob0/wang", /*96-100*/
          "blob1/commune", "blob1/dungeon", "blob1/trench", "blob1/wang" /*101-104*/
  };

  static public class Tiles {


    private int[][] g_field;
    private int g_wide = 12;
    private int g_high = 8;

    private int g_fwide = 2 * g_wide + 1;
    private int g_fhigh = 2 * g_high + 1;
    private int g_piece = 2;
    private int g_order = 2;
    private boolean blob = false;
    String path = null;
    String type = null;
    String id = null;
    private Random rnd;

    private int[][] matrix;
    SimpleImage output;

    public String getIconPath(String type, String Id) {
      String path = new String("/org/jwildfire/icons/" + type + "/" + Id + "/");
      return path;

    }

    public void setPiece(int piece) {
      this.g_piece = piece;
    }

    public void setOrder(int order) {
      this.g_order = order;
    }

    public void initg_field() {
      g_field = new int[g_fwide][g_fhigh];
      for (int col = 0; col < g_fwide; col++) {
        for (int row = 0; row < g_fhigh; row++) {
          g_field[col][row] = 0;
        }
      }
    }

    public void Block_Tiles() {
      int row, col;
      //random top-left 'corner' tile
      for (row = 1; row < g_fhigh; row += 2) {
        for (col = 1; col < g_fwide; col += 2) {
          g_field[col - 1][row - 1] = (int) Math.floor(rnd.nextDouble() * g_piece);
        }
      }
      //update non matching
      for (row = 3; row < g_fhigh; row += 2) {  // not first row
        for (col = 3; col < g_fwide; col += 2) {  // not first col
          int index = g_field[col - 1][row - 3] + 2 * g_field[col - 3][row - 3] + 4 * g_field[col - 3][row - 1];
          if (index == 0) {
            g_field[col - 1][row - 1] = 1;
          } else if (index == 2) {
            g_field[col - 1][row - 1] = 0;
          } else if (index == 5) {
            g_field[col - 1][row - 1] = 1;
          } else if (index == 7) {
            g_field[col - 1][row - 1] = 0;
          }
        }
      }
    }

    public void Edge_Tiles() {
      int row, col;
      for (row = 0; row < g_fhigh - 1; row++) {  // not last row
        for (col = 0; col < g_fwide - 1; col += 2) {  // not last col
          g_field[col + (row + 1) % 2][row] = (int) Math.floor(rnd.nextDouble() * g_order);
        }
      }
      //if (g_tset == "drawn") {  // add 16 to 50% of tiles
      clearBorder();
    }

    public void Corn_Tiles() {
      int row, col;
      for (row = 0; row < g_fhigh - 1; row += 2) {  // not last row
        for (col = 0; col < g_fwide - 1; col += 2) {  // not last col

          g_field[col][row] = (int) Math.floor(rnd.nextDouble() * g_order);
        }
      }
      clearBorder();
    }

    public void Twin_Tiles() {
      int row, col, val, dd;
      for (row = 1; row < g_fhigh - 1; row += 2) {  // not last row
        for (col = 1; col < g_fwide - 1; col += 4) {  // not last col

          dd = (row + 1) % 4;  // 0 or 2
          val = (int) Math.floor(rnd.nextDouble() * 2);
          g_field[col + dd + 1][row] = val;  // horiz copy
          g_field[col + dd - 1][row] = val;
          val = (int) Math.floor(rnd.nextDouble() * 2);
          g_field[col + dd][row + 1] = val;  // vert copy
          g_field[col + dd][row - 1] = val;
        }
      }
      clearBorder();  // reset any border cells to 0
      clearBorder2();
    }

    public void Truch_Tiles() {
      //random top-left 'corner' tile
      int row, col;
      for (row = 1; row < g_fhigh; row += 2) {
        for (col = 1; col < g_fwide; col += 2) {
          g_field[col - 1][row - 1] = (int) Math.floor(rnd.nextDouble() * g_piece);
        }
      }
    }

    public void Blob_Tiles() {
      clearCells(0);
      if (blob)
        blob0_Tiles();
      else
        blob1_Tiles();
    }

    public void blob1_Tiles() {
      // all cells (edge and corner) random '0' or '1'
      int row, col;
      for (row = 1; row < g_fhigh - 1; row++) {  // not first or last 'border' rows
        for (col = 1; col < g_fwide - 1; col++) {  // not first or last 'border' cols

          if (Math.floor(rnd.nextDouble() * 5) != 0) {
            g_field[col][row] = 1;
          }  // set to 1
        }
      }
      // all central 'odd' cells
      // if edge above or left = 0 set corners to 0

      for (row = 1; row < g_fhigh; row += 2) {
        for (col = 1; col < g_fwide; col += 2) {

          if (g_field[col][row - 1] == 0) {
            g_field[col - 1][row - 1] = 0;
            g_field[col + 1][row - 1] = 0;
          }
          if (g_field[col - 1][row] == 0) {
            g_field[col - 1][row - 1] = 0;
            g_field[col - 1][row + 1] = 0;
          }
        }
      }

      removeHoles();
    }

    public void blob0_Tiles() {
      //make cell centres random 0 or 1
      int row, col;
      for (row = 1; row < g_fhigh; row += 2) {
        for (col = 1; col < g_fwide; col += 2) {

          if (Math.floor(rnd.nextDouble() * 5) != 0) g_field[col][row] = 1;  // abandon 1 in 5, leave as a 0
        }
      }

      //visit corners and check top and left edges
      for (row = 2; row < g_fhigh - 2; row += 2) {  // not first or last row
        for (col = 2; col < g_fwide - 2; col += 2) {  // not first or last col

          if (g_field[col - 1][row - 1] == 1 && g_field[col - 1][row + 1] == 1) {
            g_field[col - 1][row] = 1;
          }  // left
          if (g_field[col - 1][row - 1] == 1 && g_field[col + 1][row - 1] == 1) {
            g_field[col][row - 1] = 1;
          }  // top
        }
      }

      //horizontal edges in last column
      for (row = 2; row < g_fhigh - 2; row += 2) {  // not first or last row
        col = g_fwide - 1;
        if (g_field[col - 1][row - 1] == 1 && g_field[col - 1][row + 1] == 1) {
          g_field[col - 1][row] = 1;
        }  // left
      }
      //vertical edges in last row
      for (col = 2; col < g_fwide - 2; col += 2) {  // not first or last col
        row = g_fhigh - 1;
        if (g_field[col - 1][row - 1] == 1 && g_field[col + 1][row - 1] == 1) {
          g_field[col][row - 1] = 1;
        }  // top
      }
      removeHoles();
    }

    public void clearBorder() { // set field border to 0
      int row, col;
      for (col = 0; col < g_fwide; col++) {
        g_field[col][0] = 0;  // set top row
        g_field[col][g_fhigh - 1] = 0;  // set bottom row
      }
      for (row = 1; row < g_fhigh - 1; row++) {
        g_field[0][row] = 0;  // set left column
        g_field[g_fwide - 1][row] = 0;  // set rite column
      }
    }

    public void clearBorder2() { // set 'inner' field border to 0 for twin tilesets
      int col, row;
      for (col = 3; col < g_fwide - 1; col += 4) {
        g_field[col][2] = 0;
      }  // top
      for (col = 1; col < g_fwide - 1; col += 4) {
        g_field[col][g_fhigh - 3] = 0;
      }  // bottom
      for (row = 3; row < g_fhigh - 1; row += 4) {
        g_field[2][row] = 0;
      }  // left
      for (row = 1; row < g_fhigh - 1; row += 4) {
        g_field[g_fwide - 3][row] = 0;
      }  // rite
    }

    public void removeHoles() {
      // remove 90% of single corner holes
      int row, col;
      for (row = 2; row < g_fhigh - 2; row += 2) {  // not first or last row
        for (col = 2; col < g_fwide - 2; col += 2) {  // not first or last col

          if (Math.floor(rnd.nextDouble() * 10) == 0) continue;  // abandon 1 in 10 for variety
          if (g_field[col][row] == 0 && g_field[col - 1][row] == 1 && g_field[col + 1][row] == 1 && g_field[col][row - 1] == 1 && g_field[col][row + 1] == 1)
            g_field[col][row] = 1;
        }
      }
    }

    public void clearCells(int clear) {  // set all cells to clear
      int row, col;
      for (row = 0; row < g_fhigh - 1; row++) {  // has to be 0 for truchet block tiles
        for (col = 0; col < g_fwide - 1; col++) {
          g_field[col][row] = clear;
        }
      }
    }

    public int blockValue(int col, int row) {
      return g_field[col - 1][row - 1];
    }

    public int edgeValue(int col, int row) {
      // return midd value for edge tilesets
      return (int) ((Math.pow(g_order, 3) * g_field[col - 1][row]) + (Math.pow(g_order, 2) * g_field[col][row + 1]) + (g_order * g_field[col + 1][row]) + g_field[col][row - 1]);
    }

    public int cornValue(int col, int row) {
      // return midd value for edge tilesets
      return (int) ((Math.pow(g_order, 3) * g_field[col - 1][row - 1]) + (Math.pow(g_order, 2) * g_field[col - 1][row + 1]) + (g_order * g_field[col + 1][row + 1]) + g_field[col + 1][row - 1]);
    }

    public int blobValue(int col, int row) {
      // return midd value for edge tilesets
      return (int) 128 * g_field[col - 1][row - 1] + 64 * g_field[col - 1][row] +
              32 * g_field[col - 1][row + 1] + 16 * g_field[col][row + 1] + 8 * g_field[col + 1][row + 1] +
              4 * g_field[col + 1][row] + 2 * g_field[col + 1][row - 1] + g_field[col][row - 1];
    }

    public int truchValue(int col, int row) {
      // return midd value for truchet tilesets
      int parity = (int) ((0.5 * (col - 1) + 0.5 * (row - 1)) % 2);   // 0 or 1
      return g_piece * (parity) + g_field[col - 1][row - 1];  // stored data
    }

    public int twinValue(int col, int row) {
      int vert = 1;
      int horz = 1;
      if (col % 4 == 3) {
        vert = 2;
      }
      if (row % 4 == 3) {
        horz = 2;
      }

      return (horz * 27 * g_field[col - 1][row]) + (vert * 9 * g_field[col][row + 1]) + (horz * 3 * g_field[col + 1][row]) + vert * g_field[col][row - 1];
    }

    public void setRandom() {  // see 'putcell' for single cell/tile
      // calc midd cells and set stage tiles

      path = getIconPath(type, id);
      if ("block".equals(type))
        Block_Tiles();  // calc tile index
      else if ("edge".equals(type))
        Edge_Tiles();
      else if ("corn".equals(type))
        Corn_Tiles();
      else if ("twin".equals(type))
        Twin_Tiles();
      else if ("blob".equals(type))
        Blob_Tiles();
      else if ("truch".equals(type))
        Truch_Tiles();

      int row, col, val = 0;
      for (row = 1; row < g_fhigh; row += 2) {
        for (col = 1; col < g_fwide; col += 2) {
          if ("block".equals(type))
            val = blockValue(col, row);  // calc tile index
          else if ("edge".equals(type))
            val = edgeValue(col, row);
          else if ("corn".equals(type))
            val = cornValue(col, row);
          else if ("twin".equals(type))
            val = twinValue(col, row);
          else if ("blob".equals(type))
            val = blobValue(col, row);
          else if ("truch".equals(type))
            val = truchValue(col, row);

          g_field[col][row] = val;    // update midd cell
          matrix[col / 2][row / 2] = g_field[col][row];
        }
      }
    }

    public void render(int nfiles) throws Exception {
      SimpleImage input = null;
      SimpleImage[] pics = null;

      pics = new SimpleImage[nfiles];
      for (int i = 0; i < nfiles; i++)
        pics[i] = null;
      output = null;
      output = new SimpleImage(g_wide * 32, g_high * 32);

      for (int i = 0; i < g_high; i++) {
        for (int j = 0; j < g_wide; j++) {
          int file = matrix[j][i];
          if (pics[file] == null) {
            String filename = path + file + ".gif";
            URL url = getClass().getResource(filename);
            if (url == null) {
              url = new URL(filename);
            }
            pics[file] = new ImageReader().loadImage(url);
          }
          input = pics[matrix[j][i]];
          int height = input.getImageHeight();
          int width = input.getImageWidth();
          for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
              int argb = input.getARGBValueIgnoreBounds(col, row);
              output.setARGB(width * j + col, height * i + row, argb);
            }
          }
        }
      }
    }

    public Tiles(Random rnd, String type, String id) {
      this.type = new String(type);
      if (type.indexOf("blob0") >= 0) {
        this.type = "blob";
        this.blob = false;
      }
      if (type.indexOf("blob1") >= 0) {
        this.type = "blob";
        this.blob = true;
      }

      this.id = new String(id);
      this.rnd = rnd;

      matrix = new int[g_wide][g_high];
      initg_field();
    }

    public SimpleImage getImage() {
      return output;
    }
  }

  public void transformImage(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount, double pInputX, double pInputY) {
    double x = (pInputX - (offsetX + 0.5) + 1.0) / scaleX * (double) (imgWidth - 2);
    double y = (pInputY - (offsetY + 0.5) + 1.0) / scaleY * (double) (imgHeight - 2);
    int ix, iy;
    if (blend_colormap > 0) {
      ix = (int) MathLib.trunc(x);
      iy = (int) MathLib.trunc(y);
    } else {
      ix = Tools.FTOI(x);
      iy = Tools.FTOI(y);
    }
    if (this.tileX == 1) {
      if (ix < 0) {
        int nx = ix / imgWidth - 1;
        ix -= nx * imgWidth;
      } else if (ix >= imgWidth) {
        int nx = ix / imgWidth;
        ix -= nx * imgWidth;
      }
    }
    if (this.tileY == 1) {
      if (iy < 0) {
        int ny = iy / imgHeight - 1;
        iy -= ny * imgHeight;
      } else if (iy >= imgHeight) {
        int ny = iy / imgHeight;
        iy -= ny * imgHeight;
      }
    }

    double r, g, b;
    if (ix >= 0 && ix < imgWidth && iy >= 0 && iy < imgHeight) {
      if (colorMap instanceof SimpleImage) {
        if (blend_colormap > 0) {
          double iufrac = MathLib.frac(x);
          double ivfrac = MathLib.frac(y);
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
                  ix, iy));
          int lur = toolPixel.r;
          int lug = toolPixel.g;
          int lub = toolPixel.b;
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
                  ix + 1, iy));
          int rur = toolPixel.r;
          int rug = toolPixel.g;
          int rub = toolPixel.b;
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
                  ix, iy + 1));
          int lbr = toolPixel.r;
          int lbg = toolPixel.g;
          int lbb = toolPixel.b;
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
                  ix + 1, iy + 1));
          int rbr = toolPixel.r;
          int rbg = toolPixel.g;
          int rbb = toolPixel.b;
          r = GfxMathLib.blerp(lur, rur, lbr, rbr, iufrac, ivfrac);
          g = GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac);
          b = GfxMathLib.blerp(lub, rub, lbb, rbb, iufrac, ivfrac);
        } else {
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(
                  ix, iy));
          r = toolPixel.r;
          g = toolPixel.g;
          b = toolPixel.b;
        }
        if (dc_color > 0) {
          pVarTP.rgbColor = true;
          pVarTP.redColor = r;
          pVarTP.greenColor = g;
          pVarTP.blueColor = b;
        }
      } else {
        if (blend_colormap > 0) {
          double iufrac = MathLib.frac(x);
          double ivfrac = MathLib.frac(y);

          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
          double lur = rgbArray[0];
          double lug = rgbArray[1];
          double lub = rgbArray[2];
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix + 1, iy);
          double rur = rgbArray[0];
          double rug = rgbArray[1];
          double rub = rgbArray[2];
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy + 1);
          double lbr = rgbArray[0];
          double lbg = rgbArray[1];
          double lbb = rgbArray[2];
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix + 1, iy + 1);
          double rbr = rgbArray[0];
          double rbg = rgbArray[1];
          double rbb = rgbArray[2];
          r = GfxMathLib.blerp(lur, rur, lbr, rbr, iufrac, ivfrac);
          g = GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac);
          b = GfxMathLib.blerp(lub, rub, lbb, rbb, iufrac, ivfrac);
        } else {
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
          r = rgbArray[0];
          g = rgbArray[1];
          b = rgbArray[2];
        }
        if (dc_color > 0) {
          pVarTP.rgbColor = true;
          pVarTP.redColor = r;
          pVarTP.greenColor = g;
          pVarTP.blueColor = b;
        }
      }
    } else {
      r = g = b = 0.0;
      if (dc_color > 0) {
        pVarTP.rgbColor = true;
        pVarTP.redColor = r;
        pVarTP.greenColor = g;
        pVarTP.blueColor = b;
      }
    }
    double dz = this.offsetZ;
    if (fabs(scaleZ) > EPSILON) {
      double intensity = (0.299 * r + 0.588 * g + 0.113 * b) / 255.0;
      dz += scaleZ * intensity;
    }
    if (resetZ != 0) {
      pVarTP.z = dz;
    } else {
      pVarTP.z += dz;
    }
    if (dc_color > 0) {
      pVarTP.color = getColorIdx(Tools.FTOI(r), Tools.FTOI(g), Tools.FTOI(b));
    }
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double rect = 1.0;
    if (square == 0)
      rect = 1.5;

    pVarTP.x += pAmount * rect * (pContext.random() - 0.5);
    pVarTP.y += pAmount * (pContext.random() - 0.5);

    transformImage(pContext, pXForm, pAffineTP, pVarTP, pAmount, pVarTP.x, pVarTP.y);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{id, seed, square, scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, tileX, tileY, resetZ};
  }


  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ID.equalsIgnoreCase(pName)) {
      id = (int) Tools.limitValue(pValue, 0, Id.length);
    } else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = (int) pValue;
    else if (PARAM_SQUARE.equalsIgnoreCase(pName))
      square = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scaleX = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaleY = pValue;
    else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
      scaleZ = pValue;
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offsetX = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offsetY = pValue;
    else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
      offsetZ = pValue;
    else if (PARAM_TILEX.equalsIgnoreCase(pName))
      tileX = Tools.FTOI(pValue);
    else if (PARAM_TILEY.equalsIgnoreCase(pName))
      tileY = Tools.FTOI(pValue);
    else if (PARAM_RESETZ.equalsIgnoreCase(pName))
      resetZ = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  private WFImage colorMap;
  private RenderColor[] renderColors;
  private Map<RenderColor, Double> colorIdxMap = new HashMap<RenderColor, Double>();


  private double getColorIdx(int pR, int pG, int pB) {
    RenderColor pColor = new RenderColor(pR, pG, pB);
    Double res = colorIdxMap.get(pColor);
    if (res == null) {

      int nearestIdx = 0;
      RenderColor color = renderColors[0];
      double dr, dg, db;
      dr = (color.red - pR);
      dg = (color.green - pG);
      db = (color.blue - pB);
      double nearestDist = sqrt(dr * dr + dg * dg + db * db);
      for (int i = 1; i < renderColors.length; i++) {
        color = renderColors[i];
        dr = (color.red - pR);
        dg = (color.green - pG);
        db = (color.blue - pB);
        double dist = sqrt(dr * dr + dg * dg + db * db);
        if (dist < nearestDist) {
          nearestDist = dist;
          nearestIdx = i;
        }
      }
      res = (double) nearestIdx / (double) (renderColors.length - 1);
      colorIdxMap.put(pColor, res);
    }
    return res;
  }

  private void clearCurrColorMap() {
    colorMap = null;
    colorIdxMap.clear();
  }


  private String calculateTileKey() {
    return seed + "#" + new String(Id[id]);
  }

  @Override
  public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    try {
      String key = calculateTileKey();
      colorMap = (SimpleImage) RessourceManager.getRessource(key);
      if (colorMap == null) {
        rnd = new Random((long) seed);

        String option = new String(Id[id]);


        String type = null;
        Tiles tile = null;

        if (option.indexOf("block2") >= 0) {
          type = new String(option.substring(option.indexOf("/") + 1));
          tile = new Tiles(rnd, "block", type);
          tile.setPiece(2);
          tile.setRandom();
          tile.render(2);
        } else if (option.indexOf("block4") >= 0) {
          type = new String(option.substring(option.indexOf("/") + 1));
          tile = new Tiles(rnd, "block", type);
          tile.setPiece(4);
          tile.setRandom();
          tile.render(4);
        } else if (option.indexOf("edge2") >= 0) {
          type = new String(option.substring(option.indexOf("/") + 1));
          tile = new Tiles(rnd, "edge", type);
          tile.setOrder(2);
          tile.setRandom();
          tile.render(16);
        } else if (option.indexOf("edge3") >= 0) {
          type = new String(option.substring(option.indexOf("/") + 1));
          tile = new Tiles(rnd, "edge", type);
          tile.setOrder(3);
          tile.setRandom();
          tile.render(81);
        } else if (option.indexOf("corn2") >= 0) {
          type = new String(option.substring(option.indexOf("/") + 1));
          tile = new Tiles(rnd, "corn", type);
          tile.setOrder(2);
          tile.setRandom();
          tile.render(16);
        } else if (option.indexOf("corn3") >= 0) {
          type = new String(option.substring(option.indexOf("/") + 1));
          tile = new Tiles(rnd, "corn", type);
          tile.setOrder(3);
          tile.setRandom();
          tile.render(81);
        } else if (option.indexOf("twin") >= 0) {
          type = new String(option.substring(option.indexOf("/") + 1));
          tile = new Tiles(rnd, "twin", type);
          tile.setOrder(3);//?
          tile.setRandom();
          tile.render(256);// no se cuanto archivos
        } else if (option.indexOf("blob") >= 0) {
          type = new String(option.substring(option.indexOf("/") + 1));
          String op = new String(option.substring(0, option.indexOf("/")));
          tile = new Tiles(rnd, op, type);
          tile.setOrder(3);
          tile.setRandom();
          tile.render(256);
        } else if (option.indexOf("truch") >= 0) {
          type = new String(option.substring(option.indexOf("/") + 1));
          tile = new Tiles(rnd, "truch", type);
          if (type.indexOf("tru7") >= 0 || type.indexOf("tru8") >= 0) {
            tile.setPiece(4); //  4 pieces
            tile.setRandom();
            tile.render(8);
          } else {
            tile.setPiece(2); // 2 pieces
            tile.setRandom();
            tile.render(4);
          }
        }

        RessourceManager.putRessource(key, tile.getImage());
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    clearCurrColorMap();
    colorMap = (SimpleImage) RessourceManager.getRessource(calculateTileKey());
    renderColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    imgWidth = colorMap.getImageWidth();
    imgHeight = colorMap.getImageHeight();
  }


  @Override
  public String getName() {
    return "wangtiles";
  }
}

 
