## Build ##
* The MwGui build system has been re-worked. It should now build cleanly on OS X, but for now you must manually mark the JavaApplicationStub as executable.

		mvn clean install
		
		chmod +x target\mwgui-{project.version}\MultiWii\ Swing\ GUI.app/Contents/MacOS/JavaApplicationStub
		
* Windows/Linux packaging is currently broken.


## Run ##
* You can either use `gui/build` or `MultiWiiConf` as the `INSTALL` directory.

* When the build process is finished: 
	* Copy the `mwgui-${version}-release-jar-with-dependencies.jar` file to `INSTALL/lib`.
	* Run the shell script in the `INSTALL` folder:
	 
			build/mwi-swing.sh

* On Mac OS X just run the `MultiWiiConf.app` in the `target/mwgui-{project.version}` directory.
 

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
