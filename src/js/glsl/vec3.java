package js.glsl;

import java.io.Serializable;

public  class vec3 implements Serializable
{
    private static final long serialVersionUID = 1L;
    public double x, y, z,r,g,b;
    
    public void copyColor(double r,double g,double b)
    {
    	this.r=r;
    	this.g=g;
    	this.b=b;
    }
    
	public vec3(double val)
	{
		this.x=val;
		this.y=val;
		this.z=val;
		copyColor(val,val,val);
	}
	
    public vec3(double X,double Y,double Z)
    {
    	this.x=X;
    	this.y=Y;
    	this.z=Z;
		copyColor(X,Y,Z);
    }
    
    public vec3(vec3 v)
    {
    	this.x=v.x;
    	this.y=v.y;
    	this.z=v.z;
		copyColor(v.x,v.y,v.z);
    }

	public vec3(vec2 v, double val)
	{
		this.x=v.x;
		this.y=v.y;
		this.z=val;
		copyColor(v.x,v.y,val);
	}
	
	public vec3 plus( double d) {
		return new vec3( x + d, y + d, z + d );
	}
	
	public vec3 plus( vec3 vec) {
		return new vec3( x + vec.x, y + vec.y, z + vec.z );
	}
	
	public vec3 add( double d) {
		return new vec3( x + d, y + d, z + d );
	}
	
	public vec3 add( vec3 vec) {
		return new vec3( x + vec.x, y + vec.y, z + vec.z );
	}
	   	
	public vec3 minus( double d) {
		return new vec3( x - d, y - d, z - d );
	}
	
	public vec3 minus( vec3 vec) {
		return new vec3( x - vec.x, y - vec.y, z - vec.z );
	}
    
	public vec3 multiply(double scalar) {
		return new vec3( this.x * scalar, this.y * scalar, this.z * scalar );
	}
	
	public vec3 multiply(vec3 v) {
		return new vec3( this.x * v.x, this.y * v.y, this.z * v.z );
	}
	
	public vec3 division(double d) {
		return new vec3( this.x / d, this.y / d, this.z / d );
	}
	
	public vec3 division(vec3 v) {
		return new vec3( this.x / v.x, this.y / v.y, this.z / v.z );
	}
	
	public vec3 times(mat3 m)  // vec3= vec3*mat3
	{
		return new vec3(this.x*m.a00 + this.y*m.a10 + this.z*m.a20 , 
				        this.x*m.a01 + this.y*m.a11 + this.z*m.a21 ,
				        this.x*m.a02 + this.y*m.a12 + this.z*m.a22); 
	}
	
}