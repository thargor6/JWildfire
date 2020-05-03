package js.glsl;

import java.io.Serializable;

public class vec2 implements Serializable
{
    private static final long serialVersionUID = 1L;
    public double x, y;

	public vec2(double val)
	{
		this.x=val;
		this.y=val;
	}
	
    public vec2(double X,double Y)
    {
    	this.x=X;
    	this.y=Y;
    }
    
    public vec2(vec2 v)
    {
    	this.x=v.x;
    	this.y=v.y;
    }
    
    public vec2 plus(double b)
    {
    	vec2 vret=new vec2(0.0);
    	vret.x=x+b;
    	vret.y=y+b;
    	return vret;
    }
    
    public vec2 plus(vec2 b)
    {
    	vec2 vret=new vec2(0.0);
    	vret.x=x+b.x;
    	vret.y=y+b.y;
    	return vret;
    }
    
    public vec2 add(double b)
    {
    	vec2 vret=new vec2(0.0);
    	vret.x=x+b;
    	vret.y=y+b;
    	return vret;
    }
    
    public vec2 add(vec2 b)
    {
    	vec2 vret=new vec2(0.0);
    	vret.x=x+b.x;
    	vret.y=y+b.y;
    	return vret;
    }
    
    public vec2 minus(double b)
    {
    	vec2 vret=new vec2(0.0);
    	vret.x=x-b;
    	vret.y=y-b;
    	return vret;
    }
    
	public vec2 minus( vec2 vec) {
		vec2 vret=new vec2(0.0);
    	vret.x=x-vec.x;
    	vret.y=y-vec.y;
    	return vret;
	}
	
   	public vec2 multiply(double val)
	{
		vec2 vret=new vec2(0.0);
		vret.x=this.x*val;
		vret.y=this.y*val;
		return vret;
	}
   	
	public vec2 multiply( vec2 vec) {
	vec2 vret=new vec2(0.0);
	vret.x=x*vec.x;
	vret.y=y*vec.y;
		return vret;
	}

	public vec2 division(double d) {
		return new vec2( this.x / d, this.y / d);
	}
	
	public vec2 division(vec2 v) {
		return new vec2( this.x / v.x, this.y / v.y );
	}
	
	public vec2 times(mat2 m)
	{
		return new vec2(m.a00*this.x + m.a10*this.y ,this.x*m.a01+this.y*m.a11); 
	}
}