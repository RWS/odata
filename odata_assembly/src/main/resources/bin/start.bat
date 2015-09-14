@echo off
SETLOCAL enabledelayedexpansion

REM Java options and system properties to pass to the JVM when starting the service. For example:
REM JVM_OPTIONS="-Xrs -Xms256m -Xmx512m"
SET JVM_OPTIONS="-Xrs -Xms256m -Xmx512m"

SET BASEDIR=%~dp0
SET CLASS_PATH=.:config:bin
SET LIB_DIR="./lib"
SET ADDONS_DIR="addons/*"
SET CLASS_NAME="com.sdl.odata.container.ODataServiceContainer"
SET PID_FILE="sdlwebdata.pid"

cd %BASEDIR%..
if exist %PID_FILE% (
    echo "The service already started."
    echo "To start service again, run stop.sh first."
    exit 0
)

for /R %LIB_DIR% %%a in (*.jar) do (
  set CLASS_PATH=%%a;!CLASS_PATH!
)

:START
java -cp %CLASS_PATH% %JVM_OPTIONS% %CLASS_NAME%


:END
