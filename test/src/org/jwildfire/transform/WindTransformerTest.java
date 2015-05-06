package org.jwildfire.transform;

import org.junit.Assert;
import org.junit.Test;
import org.jwildfire.swing.Buffer.BufferType;
import org.jwildfire.transform.WindTransformer.Direction;

public class WindTransformerTest
{
	@Test
	public void testWindTransformerRight() throws Exception
	{ 
		int intensity=20;
		TestTransformerFrame jf = new TestTransformerFrame();
		if(TestTransformerFrame.live)
		{
			Thread.sleep(500);
		}
		WindTransformer t = new WindTransformer();
		t.initDefaultParams(jf.si);
		WindTransformer.DirectionEditor de = new WindTransformer.DirectionEditor(); 
		Assert.assertEquals("LEFT",de.getValue().toString());
		t.setIntensity(intensity);
		t.setDirection(Direction.RIGHT);
		t.setProbability(50);
		t.setSeed(50);
		Assert.assertTrue(t.acceptsInputBufferType(BufferType.IMAGE));
		Assert.assertFalse(t.acceptsInputBufferType(BufferType.HDR_IMAGE));
		
		Assert.assertEquals(intensity,t.getIntensity());
		Assert.assertEquals(50,t.getProbability());
		Assert.assertEquals(50,t.getSeed());
		Assert.assertEquals("RIGHT",t.getDirection().name());
		//Transformer generic
		Assert.assertTrue(t.getOutputMesh3D(false)==null);
		Assert.assertFalse(t.supports3DOutput());
		Assert.assertEquals(BufferType.IMAGE,t.getBufferType());
		
		t.setStoreMesh3D(false);
		t.initTransformation(jf.si);
		t.performImageTransformation(jf.si);
		
		if(TestTransformerFrame.live)
		{
			jf.pack();
			Thread.sleep(500);
		}
		jf.dispose();
	}
	@Test
	public void testWindTransformerLeft() throws Exception
	{ 
		TestTransformerFrame jf = new TestTransformerFrame();
		for(int j=450;j<500;j++)
		{
			for(int i=0;i<500;i++)
			{
				jf.si.setRGB(j,i,50,0,255);
				jf.si.setARGB(j,i,50,0,0,255);//set alpha channel on left band to 50
			}
		}
		if(TestTransformerFrame.live)
		{
			Thread.sleep(500);
		}
		WindTransformer t = new WindTransformer();
		t.initDefaultParams(jf.si);
		t.setDirection(Direction.LEFT);
		t.initTransformation(jf.si);
		t.performImageTransformation(jf.si);
		
		if(TestTransformerFrame.live)
		{
			jf.pack();
			Thread.sleep(500);
		}
		jf.dispose();
	}
	@Test
	public void testDirections()
	{
		Assert.assertEquals(2,Direction.values().length);
		Assert.assertEquals(Direction.LEFT,Direction.valueOf("LEFT"));
	}
}
