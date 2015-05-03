package org.jwildfire.transform;

import org.junit.Assert;
import org.junit.Test;
import org.jwildfire.swing.Buffer.BufferType;
import org.jwildfire.transform.FlipTransformer.Axis;
import org.jwildfire.transform.FlipTransformer.AxisEditor;

public class FlipTransformerTest 
{
	@Test
	public void testFlipTransformerX() throws Exception
	{ //should blue stripe on the right
		TestTransformerFrame jf = new TestTransformerFrame();
		if(TestTransformerFrame.live)
		{
			Thread.sleep(500);
		}
		FlipTransformer t = new FlipTransformer();
		FlipTransformer.AxisEditor ed = new AxisEditor();
		Assert.assertEquals(Axis.X,ed.getValue());
		t.initDefaultParams(jf.si);
		t.setAxis(Axis.X);
		Assert.assertTrue(t.acceptsInputBufferType(BufferType.IMAGE));
		Assert.assertFalse(t.acceptsInputBufferType(BufferType.HDR_IMAGE));
		
		Assert.assertEquals("X",t.getAxis().name());
		//Transformer generic
		Assert.assertTrue(t.allowShowStats());
		Assert.assertTrue(t.getOutputMesh3D(false)==null);
		Assert.assertFalse(t.supports3DOutput());
		Assert.assertEquals(BufferType.IMAGE,t.getBufferType());
		
		t.setStoreMesh3D(false);
		t.initTransformation(jf.si);
		t.performImageTransformation(jf.si);
		
		if(TestTransformerFrame.live)
		{
			jf.pack();
			Thread.sleep(2000);
		}
		jf.dispose();
	}
	@Test
	public void testFlipTransformerY() throws Exception
	{ //should top stripe on the bottom
		TestTransformerFrame jf = new TestTransformerFrame();
		for(int j=0;j<50;j++)
		{
			for(int i=0;i<500;i++)
			{
				jf.si.setRGB(i,j,0,0,255);
				jf.si.setARGB(i,j, 50,0,0,255);//set alpha channel on left band to 50
			}
		}
		if(TestTransformerFrame.live)
		{
			Thread.sleep(500);
		}
		FlipTransformer t = new FlipTransformer();
		t.initDefaultParams(jf.si);
		t.setAxis(Axis.Y);
		Assert.assertTrue(t.acceptsInputBufferType(BufferType.IMAGE));
		Assert.assertFalse(t.acceptsInputBufferType(BufferType.HDR_IMAGE));
		
		Assert.assertEquals("Y",t.getAxis().name());
		//Transformer generic
		Assert.assertTrue(t.allowShowStats());
		Assert.assertTrue(t.getOutputMesh3D(false)==null);
		Assert.assertFalse(t.supports3DOutput());
		Assert.assertEquals(BufferType.IMAGE,t.getBufferType());
		
		t.setStoreMesh3D(false);
		t.initTransformation(jf.si);
		t.performImageTransformation(jf.si);
		jf.pack();
		if(TestTransformerFrame.live)
		{
			Thread.sleep(2000);
		}
		jf.dispose();
	}
	@Test
	public void testFlipTransformerXY() throws Exception
	{ //should blue stripe on the right
		TestTransformerFrame jf = new TestTransformerFrame();
		for(int j=0;j<50;j++)
		{
			for(int i=0;i<250;i++)
			{
				jf.si.setRGB(i,j,0,0,255);
				jf.si.setARGB(i,j, 50,0,0,255);//set alpha channel on left band to 50
			}
		}
		if(TestTransformerFrame.live)
		{
			Thread.sleep(500);
		}
		FlipTransformer t = new FlipTransformer();
		t.initDefaultParams(jf.si);
		t.setAxis(Axis.XY);
		Assert.assertTrue(t.acceptsInputBufferType(BufferType.IMAGE));
		Assert.assertFalse(t.acceptsInputBufferType(BufferType.HDR_IMAGE));
		
		Assert.assertEquals("XY",t.getAxis().name());
		//Transformer generic
		Assert.assertTrue(t.allowShowStats());
		Assert.assertTrue(t.getOutputMesh3D(false)==null);
		Assert.assertFalse(t.supports3DOutput());
		Assert.assertEquals(BufferType.IMAGE,t.getBufferType());
		
		t.setStoreMesh3D(false);
		t.initTransformation(jf.si);
		t.performImageTransformation(jf.si);
		jf.pack();
		if(TestTransformerFrame.live)
		{
			Thread.sleep(2000);
		}
		jf.dispose();
	}
}
