## Build ##
* You need the GNU Serial lib in your local maven repository, you can manualy download it from
http://multiwii.fiendie.net:8081/nexus/content/repositories/thirdparty/gnu/serial/1.0/
and install the jar with:	
		
		mvn install:install-file -Dfile=/path/to/mwi-swing/build/repository/gnu/serial/1.0/serial-1.0.jar -DgroupId=gnu -DartifactId=serial -Dversion=1.0 -Dpackaging=jar

* After that just run:

		mvn clean install

* To export a Mac OS X application bundle run:

		mvn package -P mac
For further information consult the README in the mac-bundle directory.  


## Getting RxTx ##
Download the latest version of RxTx from http://rxtx.qbang.org/pub/rxtx/rxtx-2.1-7-bins-r2.zip
and unzip it into the directory

	build/lib
Make sure you get the version suitable for your platform.


## Run ##
* You can either use `gui/build` or `MultiWiiConf` as the `INSTALL` directory.

* When the build process is finished: 
	* Copy the `mwgui-${version}-release-jar-with-dependencies.jar` file to `INSTALL/lib`.
	* Run the shell script in the `INSTALL` folder:
	 
			build/mwi-swing.sh

* On Mac OS X just run the `MultiWiiConf.app` in the `mac-bundle/dist` directory.
 

## Notes on Bluetooth ##

* Linux
	* Change the script `mwi-swing.sh` to match the name of you rfcom device.
	* Example: Device name is `rfcomm0`

			-Dgnu.io.rxtx.SerialPorts=/dev/rfcomm0
		
	* For further information read http://mailman.qbang.org/2004-May/8214506.html

* Mac OS X
	* The rxtx library does not work very well with the way the internal Bluetooth modules in some Macs and MacBooks handle serial traffic.
	* As a workaround you can use an external module like the D-Link DBT-120.


## TODO ##

* Add other MSP messages
* Allow data source configuration (length of timeSeries)
* Efficient logging
* Replay of sensor log
* ...
