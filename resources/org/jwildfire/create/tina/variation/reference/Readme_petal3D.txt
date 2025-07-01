Readme_petal3D

Variables:
    petal3D_width;    //  scales the petal width
    petal3D_Zshape;  //  scales the height of the shape
    petal3D_scale1;    //  scales Z relative to X
    petal3D_scale2;    //  scales Z relative to Y
    petal3D_style;      //  changes smoothly between two Z shapes

Notes:
Adding the third dimension to an existing plugin is sometimes easy and trivial, and other times it can be very challenging. This plugin was a challenge! What sort of Z shape belongs with the unique petal shape?

The basic plugin draws a petal shape along the X axis moving away from the origin. It interacts in various ways with other variations to generate many very interesting designs. 

All the variables except for "style" governs the scale of some aspect. In the case of "style" here's how it works. It defaults to 0.0 where it uses a formula called in the code, "shaper." As the "style" value increases, the shape effect smoothly changes to "shaper2." The complete change takes place at a style value of the square root of 1/2 and all values greater than that. If "style" values are negative, it reverses the up/down effects of "shaper2."

If you're using Apophysis 2.08 3D hack, the DLL file gets copied into your "plugins3D" folder, which should be in the same folder as your Apophysis program.

 If you have comments or questions about the 3D mod contact the author, Larry Berlin...

http://aporev.deviantart.com/
http://apophysisrevealed.com/
