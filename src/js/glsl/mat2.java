package js.glsl;

import java.io.Serializable;

public class mat2 implements Serializable
{
  private static final long serialVersionUID = 1L;
	
	/*
	  a00 a01 
	  a10 a11 
     */
    public double a00,a01,a10,a11;
    public mat2(double a00,double a10,double a01,double a11)
    {
    	this.a00=a00;
    	this.a10=a10;
    	this.a01=a01;
    	this.a11=a11;
    }
    
    public mat2(vec2 v1, vec2 v2)
    {
    	this.a00=v1.x;
    	this.a10=v1.y;
    	this.a01=v2.x;
    	this.a11=v2.y;
    }
    
    
    public mat2(vec4 v1)
    {
    	this.a00=v1.x;
    	this.a10=v1.y;
    	this.a01=v1.z;
    	this.a11=v1.w;
    }
    
    public vec2 times(vec2 v)
    {
    	return new vec2(a00*v.x + a01*v.y , a10*v.x + a11*v.y); 
    }
    
    public mat2 add(double f)
    {
    	return new mat2(a00+f , a10+f,  a01 +f, a11+f );
    }
    
    public mat2 minus(double f)
    {
    	return new mat2(a00-f , a10-f , a01-f ,a11-f );
    }
    public mat2 times(double f)
    {
    	return new mat2(a00*f , a10*f ,  a01*f , a11*f );
    }
    
    public mat2 division(double f)
    {
    	return new mat2(a00/f , a10/f ,  a01/f , a11/f );
    }
    
}
