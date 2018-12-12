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

package org.jwildfire.create.tina.variation;


import csk.taprats.geometry.Ngon;
import csk.taprats.geometry.Point;
import csk.taprats.geometry.Primitive;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.plot.DrawFunc;

import java.util.Random;

/**
 * @author Jesus Sosa
 * @date jun 19, 2018
 * inspired from a program in R language from this site
 * https://fronkonstin.com/2018/06/01/coloring-sudokus/
 */

public class NSudokuFunc extends DrawFunc {

  private static final long serialVersionUID = 1L;

  private double line_thickness;


  private static final String PARAM_LEVEL = "Level";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_SIZE = "size";
  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_TYPE = "type";


  public static int NSIZE = 1296;
  public static int NCOLS = 12;
  public static int NSUDOKU = 9;

  private static final String[] paramNames = {PARAM_LEVEL, PARAM_THICKNESS, PARAM_SIZE, PARAM_ANGLE, PARAM_TYPE};


  private int level = 5;
  private double thickness = 0.5;
  private double size = 0.5;
  private double angle = 0.0;
  private int type = 4;

  static Random randomize = new Random(1000);

  /**
   * The SudokuGenerator class creates a random standard (9x9)
   * Sudoku board through the use of backtracking techniques.
   */

  static class SudokuGenerator {
    public static final int BOARD_WIDTH = 9;
    public static final int BOARD_HEIGHT = 9;

    int[][] board;
    private int operations;

    /**
     * Constructor.  Resets board to zeros
     */

    public SudokuGenerator() {
      board = new int[BOARD_WIDTH][BOARD_HEIGHT];
    }

    /**
     * Driver method for nextBoard.
     *
     * @param difficult the number of blank spaces to insert
     * @return board, a partially completed 9x9 Sudoku board
     */
    public int[][] nextBoard(int difficulty) {
      board = new int[BOARD_WIDTH][BOARD_HEIGHT];
      nextCell(0, 0);
      makeHoles(difficulty);
      return board;
    }

    /**
     * Recursive method that attempts to place every number in a cell.
     *
     * @param x x value of the current cell
     * @param y y value of the current cell
     * @return true if the board completed legally, false if this cell
     * has no legal solutions.
     */

    public boolean nextCell(int x, int y) {
      int nextX = x;
      int nextY = y;
      int[] toCheck = {1, 2, 3, 4, 5, 6, 7, 8, 9};
      Random r = new Random();
      int tmp = 0;
      int current = 0;
      int top = toCheck.length;

      for (int i = top - 1; i > 0; i--) {
        current = r.nextInt(i);
        tmp = toCheck[current];
        toCheck[current] = toCheck[i];
        toCheck[i] = tmp;
      }

      for (int i = 0; i < toCheck.length; i++) {
        if (legalMove(x, y, toCheck[i])) {
          board[x][y] = toCheck[i];
          if (x == 8) {
            if (y == 8)
              return true;//We're done!  Yay!
            else {
              nextX = 0;
              nextY = y + 1;
            }
          } else {
            nextX = x + 1;
          }
          if (nextCell(nextX, nextY))
            return true;
        }
      }
      board[x][y] = 0;
      return false;
    }


    /**
     * Given a cell's coordinates and a possible number for that cell,
     * determine if that number can be inserted into said cell legally.
     *
     * @param x       x value of cell
     * @param y       y value of cell
     * @param current The value to check in said cell.
     * @return True if current is legal, false otherwise.
     */

    private boolean legalMove(int x, int y, int current) {
      for (int i = 0; i < 9; i++) {
        if (current == board[x][i])
          return false;
      }
      for (int i = 0; i < 9; i++) {
        if (current == board[i][y])
          return false;
      }
      int cornerX = 0;
      int cornerY = 0;
      if (x > 2)
        if (x > 5)
          cornerX = 6;
        else
          cornerX = 3;
      if (y > 2)
        if (y > 5)
          cornerY = 6;
        else
          cornerY = 3;
      for (int i = cornerX; i < 10 && i < cornerX + 3; i++)
        for (int j = cornerY; j < 10 && j < cornerY + 3; j++)
          if (current == board[i][j])
            return false;
      return true;
    }

    /**
     * Given a completed board, replace a given amount of cells with 0s
     * (to represent blanks)
     *
     * @param holesToMake How many 0s to put in the board.
     */

    public void makeHoles(int holesToMake) {
		/* We define difficulty as follows:
			Easy: 32+ clues (49 or fewer holes)
			Medium: 27-31 clues (50-54 holes)
			Hard: 26 or fewer clues (54+ holes)
			This is human difficulty, not algorighmically (though there is some correlation)
		*/

      double remainingSquares = 81;
      double remainingHoles = (double) holesToMake;

      for (int i = 0; i < 9; i++)
        for (int j = 0; j < 9; j++) {
          double holeChance = remainingHoles / remainingSquares;
          if (randomize.nextDouble() <= holeChance) {
            board[i][j] = 0;
            remainingHoles--;
          }
          remainingSquares--;
        }
    }
  }

  static class tSudoku {

    public static int[][] perm = {{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 1, 2}, {3, 2, 1}};
    public static int[][] M = new int[NSIZE][NCOLS];

    public tSudoku() {
      int[] c = new int[12];
      for (int n = 0; n < 6; n++) {
        c[0] = perm[n][0];
        c[1] = perm[n][1];
        c[2] = perm[n][2];
        for (int m = 0; m < 6; m++) {
          c[3] = perm[m][0];
          c[4] = perm[m][1];
          c[5] = perm[m][2];
          for (int l = 0; l < 6; l++) {
            c[6] = perm[l][0];
            c[7] = perm[l][1];
            c[8] = perm[l][2];
            for (int j = 0; j < 6; j++) {
              c[9] = perm[j][0];
              c[10] = perm[j][1];
              c[11] = perm[j][2];
              int row = 216 * n + 36 * m + 6 * l + j;
              for (int i = 0; i < NCOLS; i++) {
                M[row][i] = c[i];
              }
            }
          }
        }
      }
      for (int i = 0; i < NSIZE; i++) {
        for (int j = 0; j < 9; j++) {
          if (j > 5)
            M[i][j] = M[i][j] + 3 * M[i][11] - 3;
          else if (j > 2)
            M[i][j] = M[i][j] + 3 * M[i][10] - 3;
          else
            M[i][j] = M[i][j] + 3 * M[i][9] - 3;
        }
      }
    }

    public int[][] getTab() {
      return M;
    }

    public int rowCompare(int[] rowtotal, int[] x) {
      int compare = 0;
      for (int j = 0; j < x.length; j++) {
        compare = compare + ((Math.abs(rowtotal[j] - x[j]) == 0) ? 1 : 0);
      }
      return compare;
    }

    public int[] Compare(int[][] total, int[] x) {
      int[] rows = new int[NSUDOKU];


      int size = total.length;
      int[] compare = new int[size];

      for (int i = 0; i < size; i++) {
        for (int j = 0; j < NSUDOKU; j++) {
          rows[j] = total[i][j];
        }
        compare[i] = rowCompare(rows, x);
      }
      return compare;
    }

    public int[][] transSudoku(int[][] sudoku, int[] trans) {
      int[][] newsudoku = new int[9][9];
      int source;

      for (int i = 0; i < NSUDOKU; i++) {
        source = trans[i];
        for (int j = 0; j < NSUDOKU; j++) {
          newsudoku[i][j] = sudoku[source - 1][j];
        }
      }
      return newsudoku;
    }

    public int[] vecSudoku(int[][] sudoku) {
      int[] vector = new int[9 * 9];

      for (int i = 0; i < NSUDOKU; i++) {
        for (int j = 0; j < NSUDOKU; j++) {
          vector[NSUDOKU * i + j] = sudoku[j][i];
        }
      }
      return vector;
    }

    public int[][] updateTotal(int[][] total, int[] compare) {
      int size = 0;
      int nsize = total.length;
      for (int i = 0; i < nsize; i++) {
        if (compare[i] == 0)
          size = size + 1;
      }

      int[][] newtotal = new int[size][9];
      int k = 0;

      for (int i = 0; i < nsize; i++) {
        if (compare[i] == 0) {
          for (int j = 0; j < NSUDOKU; j++)
            newtotal[k][j] = total[i][j];
          k = k + 1;
        }
      }
      return newtotal;
    }

    public int[] peek(int[][] newtotal) {
      int peek;
      int[] trans = new int[NSUDOKU];

      peek = (int) (randomize.nextDouble() * newtotal.length);
//		System.out.println("peek : " + peek);
      for (int j = 0; j < NSUDOKU; j++) {
        trans[j] = newtotal[peek][j];
      }
      return trans;
    }
  }

  public void create_sudokupoly(int[] color, int scale) {
    double x, y, xmin, xmax, ymin, ymax;

    for (int i = 0; i < NSUDOKU; i++)
      for (int j = 0; j < NSUDOKU; j++) {
        x = (double) i / 9.0 - 0.5;
        y = (double) j / 9.0 - 0.5;
		/*	 
			 xmin=x-(((double)level-(scale-1))/(double)level)*0.5;
			 xmax=x+(((double)level-(scale-1))/(double)level)*0.5;
			 ymin=y-(((double)level-(scale-1))/(double)level)*0.5;
			 ymax=y+(((double)level-(scale-1))/(double)level)*0.5;
	   */
        double radius = size * ((double) scale / (double) level) / 9.0;
        double area = (1.0 - 1.0 / (double) scale) == 0 ? 0.0 : thickness;
        primitives.add(new Ngon(type, radius, angle, new Point(x, y), (double) color[9 * i + j] / 9.0, area));
      }
  }

  public void build_pattern() {
    int[][] sudoku = null, newsudoku = null;
    int[] tran = null, compare = null, sudokuVector = null;
    ;
    int[][] total = null;

    tSudoku ts = new tSudoku();
    for (int n = level; n > 0; n--) {
      if (n == level) {
        SudokuGenerator sg = new SudokuGenerator();
        sudoku = sg.nextBoard(0);
        total = ts.getTab();
        tran = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        compare = ts.Compare(total, tran);
        sudokuVector = ts.vecSudoku(sudoku);
      } else {
        tran = ts.peek(total);
        newsudoku = ts.transSudoku(sudoku, tran);
        compare = ts.Compare(total, tran);
        sudokuVector = ts.vecSudoku(newsudoku);
      }
      create_sudokupoly(sudokuVector, n);
      int[][] newTotal = ts.updateTotal(total, compare);
      total = newTotal;
    }
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    line_thickness = 0.5 / 100;
    build_pattern();
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    Point out = null;

    Primitive primitive = null;
    double color = 0.0;

    primitive = getPrimitive(pContext);

    if (primitive.gettype() == 4) {
      Ngon polygon = (Ngon) primitive;
      out = plotPolygon(pContext, polygon);
      color = polygon.getColor();

      pVarTP.x += pAmount * (out.getX() * polygon.getScale() * polygon.getCosa() + out.getY() * polygon.getScale() * polygon.getSina() + polygon.getPos().getX());
      pVarTP.y += pAmount * (-out.getX() * polygon.getScale() * polygon.getSina() + out.getY() * polygon.getScale() * polygon.getCosa() + polygon.getPos().getY());
      pVarTP.color = color;

      if (pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAmount * pAffineTP.z;
      }
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{level, thickness, size, angle, type};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LEVEL.equalsIgnoreCase(pName))
      level = (int) Tools.limitValue(pValue, 1, 9);
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName)) {
      thickness = 1.0 - Tools.limitValue(pValue, 0.0, 1.0);
    } else if (PARAM_SIZE.equalsIgnoreCase(pName))
      size = Tools.limitValue(pValue, 0.3, 1.0);
    else if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else if (PARAM_TYPE.equalsIgnoreCase(pName))
      type = (int) Tools.limitValue(pValue, 3, 6);
    else
      throw new IllegalArgumentException(pName);
  }

  public String getName() {
    return "nsudoku";
  }

}
