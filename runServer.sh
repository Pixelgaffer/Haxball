#!/bin/bash

libs="lib/lombok-1.16.0.jar:lib/commons-math3-3.2.jar"

compile ()
{
  if [ -d $1 ]; then
    for file in $(ls $1); do
      compile $1/$file
    done
  elif [[ $1 == *.java ]]; then
    sha1="$(echo -n "$(openssl sha1 $1)")"
    if [ -f $1.sha1 ] && [ "$sha1" == "$(cat $1.sha1)" ]; then
      echo -n "$sha1" > $1.sha1
    else
      #echo "  - compiling $1"
      cmd="javac -classpath src/:$libs $1"
      echo $cmd
      $cmd
      echo -n "$sha1" > $1.sha1
    fi
  fi
}

echo " -- compiling"
#javac -classpath lib/lombok-1.16.0.jar src/haxball/*/*.java
compile src/haxball
echo " -- starting jvm"
java  -classpath src/:$libs haxball.networking.Server
echo " -- finished"
