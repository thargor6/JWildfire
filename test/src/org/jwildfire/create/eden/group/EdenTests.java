package org.jwildfire.create.eden.group;

import org.junit.*;
import org.jwildfire.create.eden.io.SunflowWriter;
import org.jwildfire.create.eden.primitive.BasePrimitive;
import org.jwildfire.create.eden.primitive.Box;
import org.jwildfire.create.eden.primitive.Cylinder;
import org.jwildfire.create.eden.primitive.Point;
import org.jwildfire.create.eden.primitive.Primitive;
import org.jwildfire.create.eden.primitive.Sphere;
import org.jwildfire.create.eden.primitive.Torus;
import org.jwildfire.create.eden.scene.Scene;
public class EdenTests
{
	static final double defaultSize = BasePrimitive.DFLT_SIZE;
	private static Scene s;
	@BeforeClass
	public static void beforeClass()
	{
		s = new Scene();
		s.addObject(new Box());
		s.addObject(new Sphere());
		s.addObject(new Torus());
		s.addObject(new Cylinder());
	}
	@Test
	public void testGroups()
	{
		Group g = new Group();
		g.setName("g1");
		g.getMembers().add(new Box());
		Assert.assertEquals("g1",g.getName());
		Assert.assertEquals(1,g.getMembers().size());
		Assert.assertNotNull(g.getCentre());
	}
	@Test
	public void testScene()
	{
		Box b = new Box();
		b.getRotate().setValue(27d);
		b.getPosition().setValue(27d);
		Group g = s.getRootGroup();
		g.setName("gs");
		
		int size = g.getMembers().size();
		g.getMembers().add(b);
 
		Assert.assertEquals("gs",s.getRootGroup().getName());
		Assert.assertNotNull(new SunflowWriter().createSunflowScene(s));
		Assert.assertEquals(size+1,g.getMembers().size());
	}
	@Test
	public void testPoint()
	{
		Point p = new Box().getSize();
		p.assign(new Box().getSize());
		p.setValue(defaultSize);
		p.scale(.5d, .5d, .5d);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize/2,p.getX(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize/2,p.getY(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize/2,p.getZ(),.01d);
		p.rotate(720, 720, 720);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize/2,p.getX(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize/2,p.getY(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize/2,p.getZ(),.01d);
		p.setValue(defaultSize,defaultSize,defaultSize);
		p.rotate(1e-10, 1e-10, 1e-10);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize,p.getX(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize,p.getY(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize,p.getZ(),.01d);
		p.scale(2d, 2d, 2d);
		p.translate(1d, 1d, 1d);
		Assert.assertEquals(p.getClass().getSimpleName(),2*defaultSize+1d,p.getX(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),2*defaultSize+1d,p.getY(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),2*defaultSize+1d,p.getZ(),.01d);
		p.setX(359+defaultSize);
		p.setY(359+defaultSize);
		p.setZ(359+defaultSize);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize-1d,p.getX(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize-1d,p.getY(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),defaultSize-1d,p.getZ(),.01d);
		p.setX(-359-defaultSize);
		p.setY(-359-defaultSize);
		p.setZ(-359-defaultSize);
		Assert.assertEquals(p.getClass().getSimpleName(),-1*defaultSize+1d,p.getX(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),-1*defaultSize+1d,p.getY(),.01d);
		Assert.assertEquals(p.getClass().getSimpleName(),-1*defaultSize+1d,p.getZ(),.01d);
	}
	@Test
	public void testPrimitives()
	{
		for(GroupMember g:s.getRootGroup().getMembers())
		{
			Assert.assertTrue(g instanceof Primitive);
			Primitive p = (Primitive)g;
			Assert.assertEquals(p.getClass().getSimpleName(),0,p.getPosition().getX(),.01d);
			Assert.assertEquals(p.getClass().getSimpleName(),0,p.getRotate().getY(),.01d);
			Assert.assertEquals(p.getClass().getSimpleName(),defaultSize,p.getSize().getZ(),.01d);
			if(p instanceof Torus)
			{
				((Torus) p).setInnerRadius(.7d);
				Assert.assertEquals(p.getClass().getSimpleName(),.7d,((Torus) p).getInnerRadius(),0.01d);
			}
		}
		//BasePrimitive has a 
		BasePrimitive bp = new BasePrimitive();
		Assert.assertEquals(bp.getClass().getSimpleName(),0,bp.getPosition().getX(),.01d);
		Assert.assertEquals(bp.getClass().getSimpleName(),0,bp.getRotate().getY(),.01d);
		Assert.assertEquals(bp.getClass().getSimpleName(),1.0d,bp.getSize().getZ(),.01d);
	}
}