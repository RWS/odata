#!/bin/sh
#
# Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


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
