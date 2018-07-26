package csk.taprats.geometry;
import java.util.ArrayList;

public class TestTriangulate
{
	public static final float EPSILON = 0.0000000001f;

	public static void main(String[] args)
	{

	  // Small test application demonstrating the usage of the triangulate
	  // class.


	  // Create a pretty complicated little contour by pushing them onto
	  // an vector.

	  ArrayList< Point > a = new ArrayList< Point >();

	  a.add(new Point(0F, 6F));
	  a.add(new Point(0F, 0F));
	  a.add(new Point(3F, 0F));
	  a.add(new Point(4F, 1F));
	  a.add(new Point(6F, 1F));
	  a.add(new Point(8F, 0F));
	  a.add(new Point(12F, 0F));
	  a.add(new Point(13F, 2F));
	  a.add(new Point(8F, 2F));
	  a.add(new Point(8F, 4F));
	  a.add(new Point(11F, 4F));
	  a.add(new Point(11F, 6F));
	  a.add(new Point(6F, 6F));
	  a.add(new Point(4F, 3F));
	  a.add(new Point(2F, 6F));

	  
	  for (int i = 0; i < a.size(); i++)
	  {
		final Point p1 = a.get(i );
		System.out.println("Polygon Vertices " + " " + (i + 1) +" : (" + p1.getX()+" , " +p1.getY()+ ")" );
	  }
	  // allocate an vector to hold the answer.

	  ArrayList< Point > result = new ArrayList< Point >();

	  //  Invoke the triangulator to triangulate this polygon.
	  Triangulate.Process(a, result);

	  // print out the results.
	  int tcount = result.size() / 3;

	  for (int i = 0; i < tcount; i++)
	  {
		final Point p1 = result.get(i * 3 + 0);
		final Point p2 = result.get(i * 3 + 1);
		final Point p3 = result.get(i * 3 + 2);
		System.out.println("Triangle " + " " + (i + 1) +" : (" + p1.getX()+" , " +p1.getY()+") - (" +p2.getX()+" , " +p2.getY()+") - (" +p3.getX()+" , " +p3.getY() +" )");
	  }

	}
}