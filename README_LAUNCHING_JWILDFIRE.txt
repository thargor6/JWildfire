LAUNCHING JWILDFIRE
-----------------------------------------------------------------------------------
Please use always the official launcher to start JWildfire. The launcher can be 
executed by any Java runtime and can be started with any options.
The task of the launcher is to launch the main software with controlled options
and to reserve enough memory.
  
- under Windows use the start_windows.bat to start the launcher.
- under Mac OS use the start_mac.command to start the launcher.
- under Linux use the start_linux.sh to start the launcher.

If this doesnt work or if you have another operating system not listed here 
just start/open the j-wildfire-launcher.jar directly.

PLEASE DO NOT START THE j-wildfire.jar DIRECTLY!
If you start the j-wildfire.jar directly then JWildfire is started with default 
Java otions. 
In the most cases you cannot render large images with those settings because not 
enough memory is reserved at startup. So even if you have plenty of memory it can 
not be used for rendering this way. Additionally, the wrong settings may cause 
slower rendering.


HOW THE LAUNCHER WORKS
-----------------------------------------------------------------------------------
It analyzes certain system settings to locate java runtimes on your system and 
automatically selects the most recent one. You can override the automatically 
selected version by selecting another entry in the listbox.
If the scanner did not find a java runtime (probably on Macs as I have no way to 
test this) you can manually add it. You must do this only once, of course.

The memory setting lets you specify the maximum amount of memory in megabytes to use. 
This memory amount is only allocated if required. But there is no way to allocate 
more memory than here specified if JWildfire is already running. 
I. e., you must choose this value wisely. 
Example: 6000 MB should work fine to render images in high-end quality for print.


TROUBLESHOOTING
-----------------------------------------------------------------------------------
The launcher works in two ways: regular and debug mode. In regular mode it just 
start JWildfire and shuts down itself.

If you face any problems (i. E., JWildfire does not start) please activate the 
"Debug" checkbox. This forces a traced launching where log messages are recorded 
and displayed at the "Log messages" tab. 
You can easily copy those messages into the clipboard by using the "To clipboard" 
button and then report them for further assistence.
    