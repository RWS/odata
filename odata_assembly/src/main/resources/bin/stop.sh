#!/bin/sh

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
