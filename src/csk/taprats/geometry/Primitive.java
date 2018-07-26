
package csk.taprats.geometry;

public class Primitive
{
	private int type ;


	public Primitive( int type )
	{
       this.type=type;
	}

	public int gettype(  )
	{
       return  this.type;
	}

	public  String toString()
	{
		return "[ type: " + type + " ]" ;
	}
}
