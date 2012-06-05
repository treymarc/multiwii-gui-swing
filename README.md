MWI-SWING
=========
A small Java Swing frontend for multiwii


BUILD
=====
* You need the GNU Serial lib in your local maven repository, you can manualy install the provided jar:  
`mvn install:install-file -Dfile=/path/to/mwi-swing/build/repository/gnu/serial/1.0/serial-1.0.jar -DgroupId=gnu -DartifactId=serial -Dversion=1.0 -Dpackaging=jar`

After that just run:  
`mvn clean install`



RUN
===
You can either use the "build" or "MultiWiiConf" as the INSTALL folder for mwi-swing

When the build process is finished 
*copy the mwgui-Version-release-jar-with-dependencies.jar file to INSTALL/lib.
*run the shell script in the INSTALL folder:  
 `build/mwi-swing.sh`


BLUETOOTH
=========

UNIX :
 change the script mwi-swing.sh  to match the name of you rfcom device.

 ex  : device is "rfcomm0"
 
	-Dgnu.io.rxtx.SerialPorts=/dev/rfcomm0
	

 http://mailman.qbang.org/2004-May/8214506.html

MAC :
  Note that the rxtx serial library does not work very well with the way the internal Bluetooth modules in some Macs and MacBooks handle serial traffic. This was a problem with all Macs I tested and manifested itself in very low throughput. I am using an external D-Link DBT-120 module as a workaround.


TODO
====

* Add other msp messages
* Allow datasource configuration (length of timeSerie)
* Allow efficient logging
* Allow sensors log replay

etc..

