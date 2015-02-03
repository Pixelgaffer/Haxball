#!/bin/bash

libs="lib/lombok-1.16.0.jar:lib/jbox2d-library-2.2.1.1.jar"

compile ()
{
  if [ -d $1 ]; then
    for file in $(ls $1); do
      compile $1/$file
    done
  elif [[ $1 == *.java ]]; then
    sha="$(echo -n "$(sha1sum $1)")"
    sha1="${sha:0:40}"
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
