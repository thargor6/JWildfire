package csk.taprats.geometry;
import java.util.*;

// Typedef an  vector of vertices which are used to represent
// a polygon/contour and a series of triangles.


public class Triangulate
{

  // triangulate a contour/polygon, places results in STL vector
  // as series of triangles.
  public static boolean Process(ArrayList< Point > contour, ArrayList< Point > result)
  {
	/* allocate and initialize list of Vertices in polygon */

	int n = contour.size();
	if (n < 3)
	{
		return false;
	}

	int[] V = new int[n];

	/* we want a counter-clockwise polygon in V */

	if (0.0f < Area(contour))
	{
	  for (int v = 0; v < n; v++)
	  {
		  V[v] = v;
	  }
	}
	else
	{
	  for (int v = 0; v < n; v++)
	  {
		  V[v] = (n - 1) - v;
	  }
	}

	int nv = n;

	/*  remove nv-2 Vertices, creating 1 triangle every time */
	int count = 2 * nv; // error detection

	for (int m = 0, v = nv - 1; nv > 2;)
	{
	  /* if we loop, it is probably a non-simple polygon */
	  if (0 >= (count--))
	  {
		//** Triangulate: ERROR - probable bad polygon!
		return false;
	  }

	  /* three consecutive vertices in current polygon, <u,v,w> */
	  int u = v;
	  if (nv <= u)
	  {
		  u = 0; // previous
	  }
	  v = u + 1;
	  if (nv <= v)
	  {
		  v = 0; // new v
	  }
	  int w = v + 1;
	  if (nv <= w)
	  {
		  w = 0; // next
	  }

	  if (Snip(contour, u, v, w, nv, V))
	  {
		int a;
		int b;
		int c;
		int s;
		int t;

		/* true names of the vertices */
		a = V[u];
		b = V[v];
		c = V[w];

		/* output Triangle */
		result.add(contour.get(a));
		result.add(contour.get(b));
		result.add(contour.get(c));

		m++;

		/* remove v from remaining polygon */
		for (s = v,t = v + 1;t < nv;s++,t++)
		{
			V[s] = V[t];
		}
		nv--;

		/* resest error detection counter */
		count = 2 * nv;
	  }
	}



	V = null;

	return true;
  }

  // compute area of a contour/polygon
  public static double Area(ArrayList< Point > contour)
  {

	int n = contour.size();

	double A = 0.0f;

	for (int p = n - 1,q = 0; q < n; p = q++)
	{
	  A += contour.get(p).getX() * contour.get(q).getY() - contour.get(q).getX() * contour.get(p).getY();
	}
	return A * 0.5f;
  }

  // decide if point Px/Py is inside triangle defined by
  // (Ax,Ay) (Bx,By) (Cx,Cy)

	 /*
	   InsideTriangle decides if a point P is Inside of the triangle
	   defined by A, B, C.
	 */
  public static boolean InsideTriangle(double Ax, double Ay, double Bx, double By, double Cx, double Cy, double Px, double Py)

  {
	double ax;
	double ay;
	double bx;
	double by;
	double cx;
	double cy;
	double apx;
	double apy;
	double bpx;
	double bpy;
	double cpx;
	double cpy;
	double cCROSSap;
	double bCROSScp;
	double aCROSSbp;

	ax = Cx - Bx;
	ay = Cy - By;
	bx = Ax - Cx;
	by = Ay - Cy;
	cx = Bx - Ax;
	cy = By - Ay;
	apx = Px - Ax;
	apy = Py - Ay;
	bpx = Px - Bx;
	bpy = Py - By;
	cpx = Px - Cx;
	cpy = Py - Cy;

	aCROSSbp = ax * bpy - ay * bpx;
	cCROSSap = cx * apy - cy * apx;
	bCROSScp = bx * cpy - by * cpx;

	return ((aCROSSbp >= 0.0f) && (bCROSScp >= 0.0f) && (cCROSSap >= 0.0f));
  }


  private static boolean Snip(ArrayList< Point > contour, int u, int v, int w, int n, int[] V)
  {
	int p;
	double Ax;
	double Ay;
	double Bx;
	double By;
	double Cx;
	double Cy;
	double Px;
	double Py;

	Ax = contour.get(V[u]).getX();
	Ay = contour.get(V[u]).getY();

	Bx = contour.get(V[v]).getX();
	By = contour.get(V[v]).getY();

	Cx = contour.get(V[w]).getX();
	Cy = contour.get(V[w]).getY();

	if (TestTriangulate.EPSILON > (((Bx - Ax) * (Cy - Ay)) - ((By - Ay) * (Cx - Ax))))
	{
		return false;
	}

	for (p = 0;p < n;p++)
	{
	  if ((p == u) || (p == v) || (p == w))
	  {
		  continue;
	  }
	  Px = contour.get(V[p]).getX();
	  Py = contour.get(V[p]).getY();
	  if (InsideTriangle(Ax, Ay, Bx, By, Cx, Cy, Px, Py))
	  {
		  return false;
	  }
	}

	return true;
  }

}