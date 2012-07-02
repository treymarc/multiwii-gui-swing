#!/bin/sh

APPDIR=$(dirname "$0")
java  -Djava.library.path="$APPDIR" -cp "$APPDIR/lib/mwgui-@VERSION@-jar-with-dependencies.jar" eu.kprod.MwGui
