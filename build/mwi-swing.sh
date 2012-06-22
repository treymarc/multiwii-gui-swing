#!/bin/sh

APPDIR=$(dirname "$0")
java  -Djava.library.path="$APPDIR/lib" -cp "$APPDIR/lib/mwgui-0.0.2-jar-with-dependencies.jar" eu.kprod.MwGui
