If you have problems to launch the program under Linux, please try to follow the following steps
(successfully tested with Oracle-Java-11.0.1 and Ubuntu 18.04.3):


1. Open a shell window

2. Check your java installation:
$java -version
This should display some version information, depending on the version and distribution (openjdk or oracle-java).
When the command fails, you should check if you really have java installed and fix/install it.
In case of Oracle-Java, you may currently download it from : https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html

3. display the path of the default java-installation:
$echo $JAVA_HOME
This should display a path, for example /usr/lib/jvm/java-11-oracle, we need this information later

4. Important: Extract the downloaded jwildfire.zip to a permanent folder, e.g. to /home/<user>/jwf5.00:
$cd ~
$unzip -d jwf5.00 Downloads/j-wildfire-5.00.zip
$cd jwf5.00
Note: If you double-click the downloaded *.zip-file and start some program inside it, it will not work.

5. invoke the launcher
$java -jar j-wildfire-launcher.jar

6. If you do this for the first time, you must tell the launcher the path of the java-installation,
you want to use (very often, you have multiple versions on your machine, at least after some time).

Press the "Add runtime"-button and select the path, which was displayed under 3., for example /usr/lib/jvm/java-11-oracle

Navigate to the bin-sub-folder end select the file "java". Close the Dialog.

7. Thats it! You are ready to launch. Of course, you must perform steps 2.-4. and 6. only once!

And you can add the command $java -jar j-wildfire-launcher.jar to the graphical launcher of your choice,
it should also work to just double-click at the j-wildfire-launcher.jar (but only if you have performed all the stepts above).

Have fun!

Cheers!