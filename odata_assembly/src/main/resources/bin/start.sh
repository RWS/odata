#!/bin/sh

# Java options and system properties to pass to the JVM when starting the service. For example:
# JVM_OPTIONS="-Xrs -Xms256m -Xmx512m -Dmy.system.property=/var/share"
JVM_OPTIONS="-Xrs -Xms256m -Xmx512m"

BASEDIR=$(dirname $0)
CLASS_PATH=.:config:bin
LIB_DIR="lib/*"
ADDONS_DIR="addons/*"
CLASS_NAME="com.sdl.odata.container.ODataServiceContainer"
PID_FILE="sdlwebdata.pid"

cd $BASEDIR/..
if [ -f $PID_FILE ]
  then
    if ps -p $(cat $PID_FILE) > /dev/null
	then
          echo "The service already started."
          echo "To start service again, run stop.sh first."
          exit 0
    fi
fi

for LIBRARY in $LIB_DIR
do
  CLASS_PATH=$LIBRARY:$CLASS_PATH
done

for ADDON in $ADDONS_DIR
do 
  CLASS_PATH=$ADDON:$CLASS_PATH
done

echo "Starting service."
java -cp $CLASS_PATH $JVM_OPTIONS $CLASS_NAME $@ & echo $! > $PID_FILE
