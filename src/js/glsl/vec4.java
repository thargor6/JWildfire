package js.glsl;

import java.io.Serializable;

public class vec4 implements Serializable
{
    private static final long serialVersionUID = 1L;
    public double x, y, z, w;
    public double r,g,b,a;
    
    public void copyColor(double r,double g,double b,double a)
    {
    	this.r=r;
    	this.g=g;
    	this.b=b;
    	this.a=a;
    }

	public vec4(double val)
	{
		this.x=val;
		this.y=val;
		this.z=val;
		this.w=val;
		copyColor(val,val,val,val);
	}
    
    public vec4(double X,double Y,double Z,double W)
    {
    	this.x=X;
    	this.y=Y;
    	this.z=Z;
    	this.w=W;
        copyColor(X,Y,Z,W);
    }
    
	public vec4(vec2 v, double val1, double val2)
	{
		this.x=v.x;
		this.y=v.y;
		this.z=val1;
		this.w=val2;
		copyColor(v.x,v.y,val1,val2);
	}
	
	public vec4(vec2 v1, vec2 v2)
	{
		this.x=v1.x;
		this.y=v1.y;
		this.z=v2.x;
		this.w=v2.y;
		copyColor(v1.x,v1.y,v2.x,v2.y);
	}
	
	public vec4(vec3 v1, double v)
	{
		this.x=v1.x;
		this.y=v1.y;
		this.z=v1.z;
		this.w=v;
		copyColor(v1.x,v1.y,v1.z,v);
	}

	public vec4 plus( double d) {
		return new vec4( x + d, y + d, z + d , w + d);
	}
	
	public vec4 plus( vec4 vec) {
		return new vec4( x + vec.x, y + vec.y, z + vec.z, w+vec.w );
	}
	
	public vec4 add( double d) {
		return new vec4( x + d, y + d, z + d , w + d);
	}
	
	public vec4 add( vec4 vec) {
		return new vec4( x + vec.x, y + vec.y, z + vec.z, w+vec.w );
	}
	
	public vec4 minus( double d) {
		return new vec4( x - d, y - d, z - d,w-d );
	}
	
	public vec4 minus( vec4 vec) {
		return new vec4( x - vec.x, y - vec.y, z - vec.z, w-vec.w );
	}
   
	public vec4 multiply(double scalar) {
		return new vec4( this.x * scalar, this.y * scalar, this.z * scalar,this.w*scalar );
	}
	
	public vec4 multiply(vec4 v) {
		return new vec4( this.x * v.x, this.y * v.y, this.z * v.z,this.w*v.w );
	}
	
	public vec4 division(double d) {
		return new vec4( this.x / d, this.y / d, this.z / d,this.w/d );
	}
	
	public vec4 division(vec4 v) {
		return new vec4( this.x / v.x, this.y / v.y, this.z / v.z, this.w/v.w );
	}
	
	public vec4 times(mat4 m)
	{
		return new vec4(this.x*m.a00 + m.a01*this.y + this.z*m.a02 + this.w*m.a03 ,
				        this.x*m.a01 + m.a11*this.y + this.z*m.a12 + this.w*m.a13 ,
				        this.x*m.a02 + m.a21*this.y + this.z*m.a22 + this.w*m.a23 ,
				        this.x*m.a03 + m.a31*this.y + this.z*m.a32 + this.w*m.a33);
	}

}
