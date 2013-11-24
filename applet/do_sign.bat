set ARCHIVE=j-wildfire-applet.jar

mkdir tmp
cd tmp
del /S
"%JAVA_HOME%\bin\jar" vxf ..\%ARCHIVE%
cd ..

"%JAVA_HOME%\bin\jarsigner.exe" -keystore keystore -storepass software %ARCHIVE% APPLET

pause