#!/bin/sh
#
# Copyright (c) 2014-2021 All Rights Reserved by the RWS.
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


BASEDIR=$(dirname $0)
PID_FILE="sdlwebdata.pid"

cd $BASEDIR/..

if [ -f $PID_FILE ] 
  then
    if ps -p $(cat $PID_FILE) > /dev/null
      then
        echo "Stopping service."
        kill -TERM $(cat $PID_FILE)
        echo "Service stopped."
      else 
	    echo "The service is not started or failed to start last time."
    fi
    rm $PID_FILE &> /dev/null
  else
    echo "The service is not started."
fi
