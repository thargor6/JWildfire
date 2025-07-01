Readme_3D_Lattices plugins

These 3D Plugins written by Larry Berlin, September 2009
email:     larry@apophysisrevealed.com
http://apophysisrevealed.com/   
http://aporev.deviantart.com/

Both of these plugins develop a lattice structure of nodes, around which Apo 2.08 3D hack draws fractals. The result is a very unique design environment. 
CubicLattice_3D creates the 8 vertices of a cube. Hexaplay3D develops what is essentially a snowflake structure.

Variables:
    cubicLattice_3D   -   cubic3D_xpand  default=0.2  determines the size of patterns around each node.

   Hints: 
1. Use it early in your designs. Design with Pitch=90. It can be added later too.
2. Add FX (Final Transform) and Rotate in the Transform Editor to see the cubic shape (post_rotate_x=-0.15, post_rotate_y=-0.15, post_spin_z=0.5)
3. Experiment with "foci_3D" in small amounts!
4. Best results generally with other 3D plugins. Experiment!
5. Build a cubic shape in 1st TX, then later transforms will build structures inside the cubic form.
   
    hexaplay3D   -   hexa3D_majp     default=1.0    Number of major planes
                            -   hexa3D_scale     default=0.25  scale of XY factors
                            -   hexa3D_zlift       default=0.25  scale of Z axis 

Major Planes - This develops either one plane or two parallel planes.
Any value <=1.0 sets the default of 1 plane.
Any value > 1.0 sets 2 planes, and by subtraction of 1.0, develops the distance between the two planes, equidistant above and below the XY plane. So, setting "hexa3D_majp=3.0" shows two planes with a separation of 2.0.

The scale factors adjust the amount of pattern development around each node of the basic shapes. The amount of Z axis shape evident in the pattern will depend on whether it derives from a 2D or a 3D plugin. For that reason it is useful to have separate control over the XY and the Z scale factors.

1. Start with Pitch=90  As you start designing, you are looking at the side view.
2. Add FX (Final Transform) and Rotate the flame in the Transform Editor to better see the snowflake shape (post_rotate_x=-0.15, post_rotate_y=-0.15, post_spin_z=0.5)
3. Change variable "hexa3D_majp" to a value above 1.0 and watch the two hexagonal planes separate.
4. Add further TX without hexaplay3D and it will develop in the center of the pattern.
5. Use multiple TX's and use different variation values to scale them differently. For example, set hexaplay3D in TX-1 to 5 and set it in TX-2 to 3. The lattice of the second TX will nest inside the first set. The larger value will be the outside hexagonal lattice.
6. Most plugins have interesting effects, but 3D plugins generally give better results.
7. Sometimes small amounts of "foci_3D" helps to develop a pattern.
