#!/bin/sh
XERCESJ_LIB=./lib/xerces
export CLASSPATH=$XERCESJ_LIB/xercesImpl.jar:$XERCESJ_LIB/xercesSamples.jar:$XERCESJ_LIB/xml-apis.jar
date
java dom.Counter -n -v -s -f $@ 
date
