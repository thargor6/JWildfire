package org.jwildfire.create.eden.group.base;

import org.junit.*;
import org.jwildfire.create.eden.base.*;
/**
 * Boring test for eden base types
 */
public class EdenTest 
{
	static final double alphad = 1.2;
	static final float alphaf = 16f;
	@Test
	public void testAngle3Unitd()
	{
		Angle3f a = new Angle3f();
		a.setAlpha(alphad);
		a.setBeta(alphad+.1d);
		a.setGamma(alphad+.2);
		Assert.assertEquals(a.getAlpha(), alphad,.0001);
		Assert.assertEquals(a.getBeta(), alphad+.1,.0001);
		Assert.assertEquals(a.getGamma(), alphad+.2,.0001);
		Angle3f a2 = new Angle3f();
		a2.assign(a);
		Assert.assertEquals(a2.getAlpha(), alphad,.0001);
		Assert.assertEquals(a2.getBeta(), alphad+.1,.0001);
		Assert.assertEquals(a2.getGamma(), alphad+.2,.0001);
		Assert.assertTrue(a.hashCode()!=0);
		Assert.assertTrue(a.equals(a2));
		a2 = new Angle3f(alphad,alphad+.1,alphad+.2);
		Assert.assertTrue(a.equals(a2));
	}
	@Test
	public void testAngle3Unitf()
	{
		Angle3f a = new Angle3f();
		a.setAlpha(alphaf);
		a.setBeta(alphaf+1f);
		a.setGamma(alphaf+2f);
		Assert.assertEquals(a.getAlpha(), alphaf,.0001);
		Assert.assertEquals(a.getBeta(), alphaf+1f,.0001);
		Assert.assertEquals(a.getGamma(), alphaf+2f,.0001);
		Angle3f a2 = Angle3f.OPTIONAL;
		a2.assign(a);
		Assert.assertEquals(a2.getAlpha(), alphaf,.0001);
		Assert.assertEquals(a2.getBeta(), alphaf+1f,.0001);
		Assert.assertEquals(a2.getGamma(), alphaf+2f,.0001);
		Assert.assertTrue(a.hashCode()!=0);
	}
	@Test
	public void testColor3fUnit()
	{
		Color3f a = new Color3f();
		a.setR(alphaf);
		a.setB(alphaf+1f);
		a.setG(alphaf+2f);
		Assert.assertEquals(a.getR(), alphaf,.0001);
		Assert.assertEquals(a.getB(), alphaf+1f,.0001);
		Assert.assertEquals(a.getG(), alphaf+2f,.0001);
		Color3f a2 = Color3f.OPTIONAL;
		Assert.assertEquals(a2.getR(),.75,.0001);
		Assert.assertEquals(a2.getB(),.75,.0001);
		Assert.assertEquals(a2.getG(),.75,.0001);
		Assert.assertTrue(a.hashCode()!=0);
		
		Assert.assertFalse(a.equals(a2));
		a2 = new Color3f((double)alphaf,(double)alphaf+2d,(double)alphaf+1d);
//		System.out.println(a.getB()+" " +a2.getB());
//		System.out.println(a.getG()+" " +a2.getG());
//		System.out.println(a.getR()+" " +a2.getR());
		//note this equals method is not overriden, we don't expect them to be equal 
		//as they are not the same object
		Assert.assertFalse(a.equals(a2));
	}
	@Test
	public void testSize3fUnit()
	{
		Size3f a = new Size3f();
		a.setX(alphaf);
		a.setY(alphaf+1f);
		a.setZ(alphaf+2f);
		Assert.assertEquals(a.getX(), alphaf,.0001);
		Assert.assertEquals(a.getY(), alphaf+1f,.0001);
		Assert.assertEquals(a.getZ(), alphaf+2f,.0001);
		Size3f a2 = Size3f.OPTIONAL;
		Assert.assertEquals(a2.getX(), 1f,.0001);
		Assert.assertEquals(a2.getY(), 1f,.0001);
		Assert.assertEquals(a2.getZ(), 1f,.0001);
		a2.assign(a);
		Assert.assertEquals(a2.getX(), alphaf,.0001);
		Assert.assertEquals(a2.getY(), alphaf+1f,.0001);
		Assert.assertEquals(a2.getZ(), alphaf+2f,.0001);
		Assert.assertTrue(a.equals(a2));
		Assert.assertTrue(a.hashCode()!=0);
		a2 = new Size3f((double)alphaf,(double)alphaf+1d,(double)alphaf+2d);
		Assert.assertTrue(a.equals(a2));
	}
	@Test
	public void testPoint3fUnitf()
	{
		Point3f a = new Point3f();
		a.setX(alphaf);
		a.setY(alphaf+1f);
		a.setZ(alphaf+2f);
		Assert.assertEquals(a.getX(), alphaf,.0001);
		Assert.assertEquals(a.getY(), alphaf+1f,.0001);
		Assert.assertEquals(a.getZ(), alphaf+2f,.0001);
		Point3f a2 = Point3f.OPTIONAL;
		Assert.assertEquals(a2.getX(), 0f,.0001);
		Assert.assertEquals(a2.getY(), 0f,.0001);
		Assert.assertEquals(a2.getZ(), 0f,.0001);
		Assert.assertTrue(a.hashCode()!=0);
	}
	@Test
	public void testPoint3fUnitd()
	{
		Point3f a = new Point3f();
		a.setX(alphad);
		a.setY(alphad+.1d);
		a.setZ(alphad+.2);
		Assert.assertEquals(a.getX(), alphad,.0001);
		Assert.assertEquals(a.getY(), alphad+.1,.0001);
		Assert.assertEquals(a.getZ(), alphad+.2,.0001);
		Point3f a2 = new Point3f();
		a2.assign(a);
		Assert.assertEquals(a2.getX(), alphad,.0001);
		Assert.assertEquals(a2.getY(), alphad+.1,.0001);
		Assert.assertEquals(a2.getZ(), alphad+.2,.0001);
		Assert.assertTrue(a.equals(a2));
		a2 = new Point3f(.1d,.1d,.1d);
		Assert.assertFalse(a.equals(a2));
		Assert.assertTrue(a.hashCode()!=0);
	}
	@Test
	public void testFace3iUnit()
	{
		Face3i a = new Face3i();
		a.setA(2);
		a.setB(4);
		a.setC(-6);
		Assert.assertEquals(a.getA(), 2,.0001);
		Assert.assertEquals(a.getB(), 4,.0001);
		Assert.assertEquals(a.getC(), -6,.0001);
		Face3i a2 = new Face3i(a);
		Assert.assertEquals(a2.getA(), 2,.0001);
		Assert.assertEquals(a2.getB(), 4,.0001);
		Assert.assertEquals(a2.getC(), -6,.0001);
		Assert.assertTrue(a.equals(a2));
		a2 = new Face3i(a.getA(),a.getB(),a.getC());
		Assert.assertEquals(a2.getA(), 2,.0001);
		Assert.assertEquals(a2.getB(), 4,.0001);
		Assert.assertEquals(a2.getC(), -6,.0001);
		Assert.assertTrue(a.equals(a2));
		a2.setPoints(1,2,3);
		Assert.assertFalse(a.equals(a2));
		Assert.assertTrue(a.hashCode()!=0);
		Assert.assertFalse(a.equals(null));
		Assert.assertTrue(a.equals(a));
	}
}
