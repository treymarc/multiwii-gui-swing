MWI-SWING
=========

A small Java Swing frontend for multiwii


BUILD
=====
First you have to install the GNU Serial lib to your local maven repository:  
	`mvn install:install-file -Dfile=/path/to/mwi-swing/build/repository/gnu/serial/1.0/serial-1.0.jar -DgroupId=gnu -DartifactId=serial -Dversion=1.0 -Dpackaging=jar`

After that just run:  
`mvn clean install`


RUN
===
When the build process is finished copy the mwgui-Version-release-jar-with-dependencies.jar file to build/lib.
Then run the shell script in the build folder:  
	`build/mwi-swing.sh`


TODO
====

* Add other msp messages
* Allow datasource configuration (length of timeSerie)
* allow efficient logging

etc..

