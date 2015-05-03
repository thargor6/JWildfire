package org.jwildfire.transform;

import java.awt.Color;

import org.junit.*;
import org.jwildfire.swing.Buffer.BufferType;

public class AddBorderTransformerTest
{
	@Test
	public void testAddBorderTransformer() throws Exception
	{ //should have a cyan border, note the alpha channel gets dropped?
		TestTransformerFrame jf = new TestTransformerFrame();
		int pos=40;
		AddBorderTransformer t = new AddBorderTransformer();
		t.initDefaultParams(jf.si);
		t.setBottomSize(pos);
		t.setLeftSize(pos);
		t.setRightSize(pos);
		t.setTopSize(pos);
		t.setColor(Color.cyan);
		Assert.assertTrue(t.acceptsInputBufferType(BufferType.IMAGE));
		Assert.assertFalse(t.acceptsInputBufferType(BufferType.HDR_IMAGE));
		
		Assert.assertEquals(pos,t.getBottomSize());
		Assert.assertEquals(pos,t.getLeftSize());
		Assert.assertEquals(pos,t.getRightSize());
		Assert.assertEquals(pos,t.getTopSize());
		Assert.assertEquals(Color.cyan,t.getColor());
		//Transformer generic
		Assert.assertTrue(t.allowShowStats());
		Assert.assertTrue(t.getOutputMesh3D(false)==null);
		Assert.assertFalse(t.supports3DOutput());
		Assert.assertEquals(BufferType.IMAGE,t.getBufferType());
		
		t.setStoreMesh3D(false);
		t.initTransformation(jf.si);
		if(TestTransformerFrame.live)
		{
			Thread.sleep(500);
		}
		t.performImageTransformation(jf.si);
		
		if(TestTransformerFrame.live)
		{
			jf.pack();
			Thread.sleep(3000);
		}
		jf.dispose();
	}
}
