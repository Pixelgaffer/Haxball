#!/bin/sh

echo " -- compiling"
javac -classpath lib/lombok-1.16.0.jar src/haxball/*/*.java
echo " -- starting jvm"
java  -classpath lib/lombok-1.16.0.jar:src haxball.networking.Server
echo " -- finished"
