MWI-SWING
=========

A small swing frontend for multiwii

INSTALL
=======

use the MultiWiiConf folder as the root directory

* copy the file "mwgui-Version-release-jar-with-dependencies.jar" to the "lib" folder

* move the scripts to the root folder 
 
 	unix/mac : mwi-swing.sh 

	windows : mwi-swing.bat

BLUETOOTH
=====

unix only :  change the script mwi-swing.sh  to match the name of you rfcom device.

ex  : device is "rfcomm0"
 
	-Dgnu.io.rxtx.SerialPorts=/dev/rfcomm0
	

http://mailman.qbang.org/2004-May/8214506.html


BUILD
=====

maven clean install

TODO
====

add other msp messages

allow datasource configuration (length of timeSerie)

allow efficient logging

etc..

