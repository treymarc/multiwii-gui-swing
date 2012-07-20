Mac Bundle Generation
=========

This ant task generates a Mac OS X application bundle.  
It assumes that you have the jarbundler ant task in your path.  
You can get jarbundler via MacPorts or from the author's website: 

	http://informagen.com/JarBundler/
 
After the installation you again have to add it to your local Maven repository:   

 	mvn install:install-file -Dfile=/path/to/jarbundler.jar -DgroupId=net.sourceforge.jarbundler -DartifactId=jarbundler -Dversion=2.2.0 -Dpackaging=jar

  
To generate an application bundle just run  

	ant bundle
  


