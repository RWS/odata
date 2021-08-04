@REM
@REM Copyright (c) 2021 All Rights Reserved by the RWS Group.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM     http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

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
