/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (at your option) any later version.
 
  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software; 
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.random;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_GPU;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;

import java.util.ArrayList;
import java.util.List;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

// A random generator following ideas of George Marsaglia, http://programmingpraxis.com/2010/10/05/george-marsaglias-random-number-generators/
public class MarsagliaOpenCLRandomGenerator extends AbstractRandomGenerator {
  private static final int SMALL_BUFFER_SIZE = 80000;
  private static final int BUFFER_SIZE = 1600000;
  private static final int MAX_BATCHES = 12;

  private float buffer[];
  private int bufferIdx;

  private static String marsagliaKernel =
      "__kernel void " +
          "marsagliaKernel(__global int *u," +
          "             __global int *v," +
          "             __global float *c)" +
          "{" +
          "int gid = get_global_id(0);" +
          "  v[gid] = 36969 * (v[gid] & 65535) + (v[gid] >> 16);" +
          "  u[gid] = 18000 * (u[gid] & 65535) + (u[gid] >> 16);" +
          "  int rnd = (v[gid] << 16) + u[gid];" +
          "  float res= rnd / (float)0x7fffffff;" +
          "  c[gid] = res < 0 ? -res : res;" +
          "}";

  private int u = 12244355;
  private int v = 34384;

  private List<float[]> batches = new ArrayList<float[]>();

  private CreateBatchThread createBatchThread;

  public MarsagliaOpenCLRandomGenerator() {
    buffer = createFastBatch();
    createBatchThread = new CreateBatchThread();
    new Thread(createBatchThread).start();
  }

  @Override
  public void randomize(long pSeed) {
    u = (int) (pSeed << 16);
    v = (int) (pSeed << 16) >> 16;
  }

  @Override
  public double random() {
    while (true) {
      try {
        if (buffer == null || bufferIdx >= buffer.length) {
          if (batches.size() > 0) {
            try {
              buffer = batches.get(0);
              batches.remove(0);
            }
            catch (Exception ex) {
              // no problem
            }
          }
          if (buffer == null) {
            buffer = createFastBatch();
          }
          bufferIdx = 0;
        }
        float res = buffer[bufferIdx++];
        if (res < 0.0f || res >= 1.0f) {
          //System.out.println(res);
          return random();
        }
        return res;
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
    }
  }

  private class CreateBatchThread implements Runnable {
    int n;
    int srcArrayA[];
    int srcArrayB[];
    float dstArray[];

    public float[] createCPUBatch(int n) {
      float dstArray[] = new float[n];
      for (int i = 0; i < n; i++) {
        v = 36969 * (v & 65535) + (v >> 16);
        u = 18000 * (u & 65535) + (u >> 16);
        int rnd = (v << 16) + u;
        float res = rnd / (float) 0x7fffffff;
        dstArray[i] = res < 0 ? -res : res;
      }
      return dstArray;
    }

    public void init(int pCount) {
      n = pCount;
      srcArrayA = new int[n];
      srcArrayB = new int[n];
      dstArray = new float[n];
      for (int i = 0; i < n; i++) {
        v = 36969 * (v & 65535) + (v >> 16);
        u = 18000 * (u & 65535) + (u >> 16);
        srcArrayA[i] = v;
        srcArrayB[i] = u;
      }

    }

    public void prepareOpenCL() {
      srcA = Pointer.to(srcArrayA);
      srcB = Pointer.to(srcArrayB);
      dst = Pointer.to(dstArray);

      // The platform, device type and device number
      // that will be used
      final int platformIndex = 0;
      final long deviceType = CL_DEVICE_TYPE_GPU;
      final int deviceIndex = 0;

      // Enable exceptions and subsequently omit error checks in this sample
      CL.setExceptionsEnabled(true);

      // Obtain the number of platforms
      int numPlatformsArray[] = new int[1];
      clGetPlatformIDs(0, null, numPlatformsArray);
      int numPlatforms = numPlatformsArray[0];

      // Obtain a platform ID
      cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
      clGetPlatformIDs(platforms.length, platforms, null);
      cl_platform_id platform = platforms[platformIndex];

      // Initialize the context properties
      cl_context_properties contextProperties = new cl_context_properties();
      contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

      // Obtain the number of devices for the platform
      int numDevicesArray[] = new int[1];
      clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
      int numDevices = numDevicesArray[0];

      // Obtain a device ID 
      cl_device_id devices[] = new cl_device_id[numDevices];
      clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
      cl_device_id device = devices[deviceIndex];

      // Create a context for the selected device
      context = clCreateContext(
          contextProperties, 1, new cl_device_id[] { device },
          null, null, null);

      // Create a command-queue for the selected device
      commandQueue =
          clCreateCommandQueue(context, device, 0, null);

      // Allocate the memory objects for the input- and output data
      memObjects = new cl_mem[3];
      memObjects[0] = clCreateBuffer(context,
          CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
          Sizeof.cl_int * n, srcA, null);
      memObjects[1] = clCreateBuffer(context,
          CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
          Sizeof.cl_int * n, srcB, null);
      memObjects[2] = clCreateBuffer(context,
          CL_MEM_READ_WRITE,
          Sizeof.cl_float * n, null, null);

      // Create the program from the source code
      program = clCreateProgramWithSource(context,
          1, new String[] { marsagliaKernel }, null, null);

      // Build the program
      clBuildProgram(program, 0, null, null, null, null);

      // Create the kernel
      kernel = clCreateKernel(program, "marsagliaKernel", null);

      // Set the arguments for the kernel

      clSetKernelArg(kernel, 0,
          Sizeof.cl_mem, Pointer.to(memObjects[0]));
      clSetKernelArg(kernel, 1,
          Sizeof.cl_mem, Pointer.to(memObjects[1]));
      clSetKernelArg(kernel, 2,
          Sizeof.cl_mem, Pointer.to(memObjects[2]));

      // Set the work-item dimensions
      global_work_size = new long[] { n };
      local_work_size = new long[] { 16 };
    }

    private long global_work_size[];
    private long local_work_size[];

    private Pointer srcA;
    private Pointer srcB;
    private Pointer dst;

    private cl_context context;
    private cl_program program;
    private cl_kernel kernel;
    private cl_mem memObjects[];
    private cl_command_queue commandQueue;

    public void cleanupOpenCL() {
      // Release kernel, program, and memory objects
      clReleaseMemObject(memObjects[0]);
      clReleaseMemObject(memObjects[1]);
      clReleaseMemObject(memObjects[2]);
      clReleaseKernel(kernel);
      clReleaseProgram(program);
      clReleaseCommandQueue(commandQueue);
      clReleaseContext(context);
    }

    public float[] createGPUBatch() {
      // Execute the kernel
      clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
          global_work_size, local_work_size, 0, null, null);

      //      clEnqueueReadBuffer(commandQueue, memObjects[0], CL_TRUE, 0,
      //          n * Sizeof.cl_int, srcA, 0, null, null);
      //      clEnqueueReadBuffer(commandQueue, memObjects[1], CL_TRUE, 0,
      //          n * Sizeof.cl_int, srcB, 0, null, null);
      clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
          n * Sizeof.cl_float, dst, 0, null, null);

      float[] res = new float[dstArray.length];
      System.arraycopy(dstArray, 0, res, 0, dstArray.length);
      return res;
    }

    @Override
    public void run() {
      init(BUFFER_SIZE);
      prepareOpenCL();
      try {
        while (true) {
          while (batches.size() >= MAX_BATCHES) {
            try {
              Thread.sleep(1);
            }
            catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          //          long t0 = System.currentTimeMillis();
          float[] newBatch = createGPUBatch();
          while (true) {
            try {
              batches.add(newBatch);
              break;
            }
            catch (Exception ex) {
              // no problem
            }
          }
          //          long t1 = System.currentTimeMillis();
          //          System.out.println("A" + batches.size() + " " + (t1 - t0) + " ms " + srcArrayA[0] + " " + srcArrayB[0]);
        }
      }
      finally {
        cleanupOpenCL();
      }
    }
  }

  private float[] createFastBatch() {
    return new CreateBatchThread().createCPUBatch(SMALL_BUFFER_SIZE);
  }

  @Override
  public void cleanup() {
    if (createBatchThread != null) {
      createBatchThread.cleanupOpenCL();
    }
  }
}
