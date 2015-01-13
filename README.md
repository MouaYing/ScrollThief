ScrollThief
===========
by Jon Pimentel, Taft Sandbakken, TJ Besendorfer, Ryan Palmer, Kevin Hinton and Melissa Smith, Copyright 2014-2015

    A few notes:
    
    - To create an instance of XboxAdapter, right now you need to hard-code an absolute path for xboxcontroller64.dll 
    or xboxcontroller.dll. This is problematic for running on different machines. It would be good to get this to work 
    by looking for the DLL in the PATH. ***EDIT: This has been fixed, but needs to be tested on other machines***
    
    - Whatever OpenGL library the View ends up using (e.g. JOGL) will probably need to be added to the repository 
 
