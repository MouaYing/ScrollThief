ScrollThief
===========
by Jon Pimentel, Taft Sandbakken, TJ Besendorfer, Ryan Palmer, Kevin Hinton and Melissa Smith, Copyright 2014-2015
    
    To get this to run in Eclipse (Try using Libra version, just to be safe):
    
    * Make a new project using the code in the repository
    * Open the "Project" drop-down menu and select "Properties"
    * Click "Java Build Path" on the list to the left
    * Select the "Libraries" tab, and click "Add JARs..."
    * The ones you need are:
        Xbox_Java/lib/jaw.jar
        Xbox_Java/lib/XboxController.jar
        jogamp-all-platforms/jar/gluegen-rt.jar
        jogamp-all-platforms/jar/jogl-all.jar


    To build this project as an executable jar do the following.
    * Open the "Project" drop-down menu and select "Properties"
    * Click "Java Build Path" on the list to the left
    * Select the "Libraries" tab, and click "Add JARs..."
    * The ones you need are:
        Xbox_Java/lib/jaw.jar
        Xbox_Java/lib/XboxController.jar
        jogamp-all-platforms/jar/gluegen-rt.jar
        jogamp-all-platforms/jar/gluegen-rt-natives-*.jar (mainly linux, macosx, windows)
        jogamp-all-platforms/jar/jogl-all.jar
        jogamp-all-platforms/jar/jogl-all-natives-*.jar (mainly linux, macosx, windows)
        jlayer/jl1.0.1.jar
    * Next you right click on the "Project" and select Export
    * Click "Java" folder, Then "Runnable JAR file"
    * Select project from "Launch Configuration" dropdown, choose export destination, and click "Package required libraries into generated JAR"
    * Click finish and wait for JAR to be made.
        
