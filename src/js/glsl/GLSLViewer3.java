package js.glsl;
/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

//Library joml Math for OpenGL 
import org.joml.Matrix3f;

// lwjgl Library
import org.lwjgl.BufferUtils;
import org.lwjgl.system.*;
import static org.lwjgl.system.MemoryUtil.*;

// lwjgl.glfw Library
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWVidMode;

// lwjgl.opengl Library

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;

// Library PNGDecoder

import de.matthiasmann.twl.utils.PNGDecoder;

//Java io Libraries
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;


public class GLSLViewer3 {

	static class Texture{

	    private int id;

	    public Texture(int id){
	        this.id = id;
	    }

	    public int getId(){
	        return id;
	    }
	}
	
    long window;
    int WIDTH = 1024;
    int HEIGHT = 768;
    int frames=0;
    private final String WINDOW_TITLE = "The GLSLViewer";

    // Quad variables
    private int vaoId = 0;
    private int vboId = 0;
    private int vboiId = 0;
    private int indicesCount = 0;
    
	 private int programID;
	 
	 private int location_iResolution;
	 private int location_iTime;
	 private int location_iMouse;
	 private int location_iFrame;
	 private int location_iChannel0;
	 private int location_iChannel1;
	 private int location_iChannel2;
	 private int location_iChannel3;
	 private int location_iChannel4;
     
 	private static final String VERTEX_FILE = "glsl/vertexShader.txt";
// 	  private static final String FRAGMENT_FILE = "glsl/squares.txt";
//	  private static final String FRAGMENT_FILE = "glsl/magicbox.txt";
//     private static final String FRAGMENT_FILE = "glsl/AnxietyKali.txt";
// 	  private static final String FRAGMENT_FILE = "glsl/RayBasedFractal.txt";
//    private static final String FRAGMENT_FILE = "glsl/circuitKali.txt";
//    private static final String FRAGMENT_FILE = "glsl/Gas.txt";
//    private static final String FRAGMENT_FILE = "glsl/JuliaQuaternion.txt";
//    private static final String FRAGMENT_FILE = "glsl/SpringTime.txt";
//    private static final String FRAGMENT_FILE = "glsl/GoldFractal.txt";
//      private static final String FRAGMENT_FILE = "glsl/KaliSet.txt";
//    private static final String FRAGMENT_FILE = "glsl/2DClouds.txt";
//    private static final String FRAGMENT_FILE = "glsl/SeaScape.txt";
      private static final String FRAGMENT_FILE = "glsl/raindrops.txt";
//	  private static final String FRAGMENT_FILE = "glsl/frame.txt";
//	  private static final String FRAGMENT_FILE = "glsl/fractal.txt";
//	  private static final String FRAGMENT_FILE = "glsl/shader.txt";
//	  private static final String FRAGMENT_FILE = "glsl/grayscott.txt";
//	  private static final String FRAGMENT_FILE = "glsl/lichtenberg.txt";  notWorking
// 	 private static final String FRAGMENT_FILE = "glsl/tutorial.txt"; 
// 	 private static final String FRAGMENT_FILE = "glsl/brick_mortar.txt"; 
// 	 private static final String FRAGMENT_FILE = "glsl/add_subs.txt"; 
 	long last_time=System.currentTimeMillis();
 	long elapsed_time=0;
    
    
    GLCapabilities caps;
    GLFWErrorCallback errCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback fbCallback;
    Callback debugProc;
    GLFWCursorPosCallback cursorPos;
    GLFWMouseButtonCallback mouseButton;


    void init() throws IOException {
    	
   // Register errorCallback procesing function
        glfwSetErrorCallback(errCallback = new GLFWErrorCallback() {
            GLFWErrorCallback delegate = GLFWErrorCallback.createPrint(System.err);

            @Override
            public void invoke(int error, long description) {
                if (error == GLFW_VERSION_UNAVAILABLE)
                    System.err.println("This demo requires OpenGL 2.0 or higher.");
                delegate.invoke(error, description);
            }

            @Override
            public void free() {
                delegate.free();
            }
        });

// Initialize LWJGL3
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

//Window properties & Create Window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(WIDTH, HEIGHT, WINDOW_TITLE, NULL, NULL);
        if (window == NULL) {
            throw new AssertionError("Failed to create the GLFW window");
        }

        // Register Mosue Position callback function
        glfwSetCursorPosCallback(window,cursorPos= new GLFWCursorPosCallback()
        		{
        	     public void invoke(long window, double x, double y) {
        	      
                      float xMouse= (float)x/WIDTH;
                      float yMouse=(float)(HEIGHT - y - 1)/HEIGHT;
                      glUseProgram(programID);      
                    	GL20.glUniform4f(location_iMouse,xMouse,yMouse,(float)0.0,(float)0.0);
                        glUseProgram(0);
        		      // System.out.println("X= " + xMouse + " , " + "Y= "+ yMouse);
        	      }
        });

// Register Mouse Press  callback function
        glfwSetMouseButtonCallback(window,mouseButton= new GLFWMouseButtonCallback()
        		{
        	     public void invoke(long window, int button,int action,int mods) {
        	    	 DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
        	    	 DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);

        	    	 if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS)   
        	    	 { 
                         double xc;
                         double yc;
                         glUseProgram(programID);  
        	    		  GLFW.glfwGetCursorPos(window, xpos, ypos);
        	    		  xc=xpos.get()/WIDTH;
        	    		  yc= (HEIGHT - ypos.get() - 1)/HEIGHT;  
                   	      GL20.glUniform4f(location_iMouse,(float)xc,(float)yc,(float)xc,(float)yc);
                         glUseProgram(0);
        		       // System.out.println("Mouse Click X = " +xc +" Y =" + yc);
        	    	 }
        	      }
        });
        
 // Register FrameBufferSizeCallback  function
        
        glfwSetFramebufferSizeCallback(window, fbCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                if (width > 0 && height > 0 && (GLSLViewer3.this.WIDTH != width || GLSLViewer3.this.HEIGHT != height)) {
                    GLSLViewer3.this.WIDTH = width;
                    GLSLViewer3.this.HEIGHT = height;
                }
            }
        });

//  Register Keyboard procesing Callback  function
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action != GLFW_RELEASE)
                    return;

                if (key == GLFW_KEY_ESCAPE) {
                    glfwSetWindowShouldClose(window, true);
                }
            }
        });

 // Position Window in middle of the Screen       
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);

        try (MemoryStack frame = MemoryStack.stackPush()) {
            IntBuffer framebufferSize = frame.mallocInt(2);
            nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
            WIDTH = framebufferSize.get(0);
            HEIGHT = framebufferSize.get(1);
        }
        
// show Window
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        caps = GL.createCapabilities();
        debugProc = GLUtil.setupDebugMessageCallback();
        
// Set Backgrounf to White
        
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

/*  loadTexture for LWGGL2 (use slick_util.jar)
 	public int loadTexture(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG",
					new FileInputStream("glsl/" + fileName + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ".png , didn't work");
			System.exit(-1);
		}
		return texture.getTextureID();
	}*/
	
    public void setupQuad() {
        // Vertices, the order is not important.
        float[] vertices = {
                -1.f, 1.f, 0f,    // Left top         ID: 0
                -1.f, -1.f, 0f,   // Left bottom      ID: 1
                1.f, -1.f, 0f,    // Right bottom     ID: 2
                1.f, 1.f, 0f      // Right left       ID: 3
        };
        // Sending data to OpenGL requires the usage of (flipped) byte buffers
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();
         
        // OpenGL expects to draw vertices in counter clockwise order by default
        byte[] indices = {
                // Left bottom triangle
                0, 1, 2,
                // Right top triangle
                2, 3, 0
        };
        indicesCount = indices.length;
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
        indicesBuffer.put(indices);
        indicesBuffer.flip();
         
        // Create a new Vertex Array Object in memory and select it (bind)
        // A VAO can have up to 16 attributes (VBO's) assigned to it by default
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
         
        // Create a new Vertex Buffer Object in memory and select it (bind)
        // A VBO is a collection of Vectors which in this case resemble the location of each vertex.
        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
        // Put the VBO in the attributes list at index 0
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        // Deselect (bind to 0) the VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
         
        // Deselect (bind to 0) the VAO
        GL30.glBindVertexArray(0);
         
        // Create a new VBO for the indices and select it (bind)
        vboiId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        // Deselect (bind to 0) the VBO
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    
	protected void getAllUniformLocations()
	{
		location_iResolution =  GL20.glGetUniformLocation(programID,"iResolution");
		location_iTime       =  GL20.glGetUniformLocation(programID,"iTime");
		location_iMouse      =  GL20.glGetUniformLocation(programID,"iMouse");
		location_iFrame      =  GL20.glGetUniformLocation(programID,"iFrame");
		location_iChannel0   =  GL20.glGetUniformLocation(programID,"iChannel0");
		location_iChannel1   =  GL20.glGetUniformLocation(programID,"iChannel1");
		location_iChannel2   =  GL20.glGetUniformLocation(programID,"iChannel2");
		location_iChannel3   =  GL20.glGetUniformLocation(programID,"iChannel3");
		location_iChannel4   =  GL20.glGetUniformLocation(programID,"iChannel4");
	}

	 static int LoadCompileShader(String file, int type){
		StringBuilder shaderSource = new StringBuilder();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine())!=null){
				shaderSource.append(line).append("//\n");
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader!");
			System.exit(-1);
		}
		return shaderID;
	}
	 
	   public void LinkShader(String vertexFile,String fragmentFile)
	    {

	    	 int vertexShaderID;
	    	 int fragmentShaderID;
	    	
			vertexShaderID = LoadCompileShader(vertexFile,GL20.GL_VERTEX_SHADER);
			fragmentShaderID = LoadCompileShader(fragmentFile,GL20.GL_FRAGMENT_SHADER);
			programID = GL20.glCreateProgram();
			GL20.glAttachShader(programID, vertexShaderID);
			GL20.glAttachShader(programID, fragmentShaderID);
		//	bindAttributes();
			GL20.glLinkProgram(programID);
			GL20.glValidateProgram(programID);
			getAllUniformLocations();
	    }
	      

    void render() throws IOException {


        long current_time = System.currentTimeMillis();
        elapsed_time += (current_time - last_time);
        last_time = current_time;
        float time = (float) (elapsed_time / 1000.0);
        
        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(programID);
   
        
        Texture texture = loadTexture("glsl/tex00.png");
        int idTexture0=texture.getId();
        
        texture = loadTexture("glsl/tex02.png");
        int idTexture1=texture.getId();
        
        texture = loadTexture("glsl/tex03.png");
        int idTexture2=texture.getId();
        
        texture = loadTexture("glsl/tex04.png");
        int idTexture3=texture.getId();
        
        texture = loadTexture("glsl/tex06.png");
        int idTexture4=texture.getId();
        
	      
   		GL20.glUniform1f(location_iTime, time);
   		GL20.glUniform2f(location_iResolution,WIDTH,HEIGHT);
 		GL20.glUniform1i(location_iFrame,frames);
  		GL20.glUniform1i(location_iChannel0,0);
  		GL20.glUniform1i(location_iChannel1,1);
  		GL20.glUniform1i(location_iChannel2,2);
  		GL20.glUniform1i(location_iChannel3,3);
  		GL20.glUniform1i(location_iChannel4,4);

		GL13.glActiveTexture(GL13.GL_TEXTURE0 + 0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, idTexture0);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + 1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, idTexture1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + 2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, idTexture2);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + 3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, idTexture3);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + 4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, idTexture4);
  		
        // Bind to the VAO that has all the information about the vertices
        GL30.glBindVertexArray(vaoId);
        GL20.glEnableVertexAttribArray(0);
         
        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
         
        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);
         
        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        glUseProgram(0);
    }

    void loop() throws IOException {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            glViewport(0, 0, WIDTH, HEIGHT);
            
// render with the shader fragments
            render();
            frames++;

            glfwSwapBuffers(window);
        }
    }
    
    public static Texture loadTexture(String fileName) throws IOException{

    	InputStream in=new FileInputStream(fileName);
        //load png file
        PNGDecoder decoder = new PNGDecoder(in);

        //create a byte buffer big enough to store RGBA values
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());

        //decode
        decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);

        //flip the buffer so its ready to read
        buffer.flip();

        //create a texture
        int id = glGenTextures();

        //bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        //tell opengl how to unpack bytes
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //set the texture parameters, can be GL_LINEAR or GL_NEAREST
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //upload texture
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Generate Mip Map
        
//        glGenerateMipmap(GL_TEXTURE_2D);

        return new Texture(id); 
    }
    
    void run() {
        try {
    // init & show the main Window
            init();
            setupQuad();
    // Load & compile the Shader fragments
            this.LinkShader(VERTEX_FILE ,FRAGMENT_FILE );
    // main processing Loop        
            loop();

            if (debugProc != null) {
                debugProc.free();
            }
            {
            	// Free the window callbacks and destroy the window
            	// Disable the VBO index from the VAO attributes list
            	GL20.glDisableVertexAttribArray(0);

            	// Delete the vertex VBO
            	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            	GL15.glDeleteBuffers(vboId);

            	// Delete the index VBO
            	GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            	GL15.glDeleteBuffers(vboiId);

            	// Delete the VAO
            	GL30.glBindVertexArray(0);
            	GL30.glDeleteVertexArrays(vaoId);
            }
            errCallback.free();
            keyCallback.free();
            fbCallback.free();
            glfwDestroyWindow(window);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            glfwTerminate();
        }
    }

    public static void main(String[] args) {
        new GLSLViewer3().run();
    }

}