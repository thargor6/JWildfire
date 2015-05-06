package org.jwildfire.transform;

import org.junit.*;
import org.jwildfire.swing.Buffer.BufferType;

public class AlphaTest 
{
	@Test
	public void testAlphaLargerThanImage() throws Exception
	{
		TestTransformerFrame jf = new TestTransformerFrame();
		int left=501;//set these to be larger than the frame, image should stay the same
		int top=501;
		AlphaTransformer at = new AlphaTransformer();
		at.initDefaultParams(jf.si);
		at.setTop(top);
		at.setLeft(left);
		at.setStoreMesh3D(true);
		at.setAlphaChannel(jf.getBuffer());
		Assert.assertFalse(at.supports3DOutput());
		Assert.assertTrue(at.acceptsInputBufferType(BufferType.IMAGE));
		Assert.assertFalse(at.acceptsInputBufferType(BufferType.HDR_IMAGE));
		Assert.assertEquals(top,at.getTop());
		Assert.assertEquals(left,at.getLeft());
		Assert.assertEquals(jf.getBuffer().getName(),at.getAlphaChannel().getName());
		at.initTransformation(jf.si);
		if(TestTransformerFrame.live)
			Thread.sleep(1000);
		System.out.println("xform");
		at.performImageTransformation(jf.si);
		jf.pack();
		if(TestTransformerFrame.live)
			Thread.sleep(1000);
		jf.dispose();
	}
	@Test
	public void testAlphaChannelPositive() throws Exception
	{
		TestTransformerFrame jf = new TestTransformerFrame();
		AlphaTransformer at = new AlphaTransformer();
		at.initDefaultParams(jf.si);
		at.setStoreMesh3D(true);
		at.setAlphaChannel(jf.getBuffer());
		Assert.assertFalse(at.supports3DOutput());
		Assert.assertTrue(at.acceptsInputBufferType(BufferType.IMAGE));
		Assert.assertFalse(at.acceptsInputBufferType(BufferType.HDR_IMAGE));
		Assert.assertEquals(jf.getBuffer().getName(),at.getAlphaChannel().getName());
		at.initTransformation(jf.si);
		if(TestTransformerFrame.live)
			Thread.sleep(1000);
		System.out.println("xform");
		at.performImageTransformation(jf.si);
		jf.pack();
		if(TestTransformerFrame.live)
			Thread.sleep(1000);
		jf.dispose();
	}
	@Test
	public void testAlphaChannelNegativeTop() throws Exception
	{	//grey box should be shifted to remaining top 25 pixels
		TestTransformerFrame jf = new TestTransformerFrame();
		AlphaTransformer at = new AlphaTransformer();
		at.initDefaultParams(jf.si);
		at.setTop(-475);
		at.setStoreMesh3D(true);
		at.setAlphaChannel(jf.getBuffer());
		Assert.assertFalse(at.supports3DOutput());
		Assert.assertTrue(at.acceptsInputBufferType(BufferType.IMAGE));
		Assert.assertFalse(at.acceptsInputBufferType(BufferType.HDR_IMAGE));
		Assert.assertEquals(jf.getBuffer().getName(),at.getAlphaChannel().getName());
		at.initTransformation(jf.si);
		if(TestTransformerFrame.live)
			Thread.sleep(1000);
		System.out.println("xform");
		at.performImageTransformation(jf.si);
		jf.pack();
		if(TestTransformerFrame.live)
			Thread.sleep(1000);
		jf.dispose();
	}
	@Test
	public void testAlphaChannelNegativeLeft() throws Exception
	{	//grey box should be shifted to remaining 25 pixels on the left
		TestTransformerFrame jf = new TestTransformerFrame();
		AlphaTransformer at = new AlphaTransformer();
		at.initDefaultParams(jf.si);
		at.setLeft(-475);
		at.setStoreMesh3D(true);
		at.setAlphaChannel(jf.getBuffer());
		//at.performPixelTransformation(jf.getBuffer().getImage());
		Assert.assertFalse(at.supports3DOutput());
		Assert.assertTrue(at.acceptsInputBufferType(BufferType.IMAGE));
		Assert.assertFalse(at.acceptsInputBufferType(BufferType.HDR_IMAGE));
		Assert.assertEquals(jf.getBuffer().getName(),at.getAlphaChannel().getName());
		at.initTransformation(jf.si);
		if(TestTransformerFrame.live)
			Thread.sleep(1000);
		System.out.println("xform");
		at.performImageTransformation(jf.si);
		jf.pack();
		if(TestTransformerFrame.live)
			Thread.sleep(1000);
		jf.dispose();
	}
	@Test
	public void testAlphaChannelNegativeLeftAndTop() throws Exception
	{	//grey box should be shifted to a small remaining 25 pixel box in upper left
		TestTransformerFrame jf = new TestTransformerFrame();
		AlphaTransformer at = new AlphaTransformer();
		at.initDefaultParams(jf.si);
		at.setLeft(-475);
		at.setTop(-475);
		at.setStoreMesh3D(true);
		at.setAlphaChannel(jf.getBuffer());
		//at.performPixelTransformation(jf.getBuffer().getImage());
		Assert.assertFalse(at.supports3DOutput());
		Assert.assertTrue(at.acceptsInputBufferType(BufferType.IMAGE));
		Assert.assertFalse(at.acceptsInputBufferType(BufferType.HDR_IMAGE));
		Assert.assertEquals(jf.getBuffer().getName(),at.getAlphaChannel().getName());
		at.initTransformation(jf.si);
		if(TestTransformerFrame.live)
			Thread.sleep(1000);
		System.out.println("xform");
		at.performImageTransformation(jf.si);
		jf.pack();
		if(TestTransformerFrame.live)
			Thread.sleep(1000);
		jf.dispose();
	}
}

