
# Getting started with the OData Framework

Please find below brief instructions on how to setup and run the OData Framework, as well as how to add extensions and/or configurations.

## Start/stop the OData Framework as a standalone service from the command line in Windows

To start the OData Framework as a standalone service in Windows, please run the Power Shell script `.\bin\start.bat`. 
Closing the Power Shell script window used to start the OData Framework as a standalone service would kill the process therefore stopping the service. 

## Start/stop the OData Framework as a standalone service from the command line in Linux/Unix

To start the OData Framework as a standalone service in Linux/Unix, please run the Shell script `./bin/start.sh`.
To stop the OData Framework as a standalone service in Linux/Unix, please run the Shell script `./bin/stop.sh`.

Before you will start your work with shell scripts, make sure you make the files executable.
Use 'chmod +x start.sh' or 'chmod +x stop.sh' for the respective scripts.

## How to add extensions and configuration to the OData Framework

- Extensions can be added to the OData Framework under the folder `./addons`.
- Configurations can be added to the OData Framework under the folder `./config`.

**NOTE:** After adding extensions and/or configurations to the OData Framework, this needs to be restarted for these to take effect.

## HTTPS mode in OData Framework

The application is configured by default not to use https mode.
Also can be overriden by command line arguments:
--https.enabled=true - to enable https mode (default: false)
--https.keystore-path=keystore_location - to provide location of keystore file (default: config/keystore)
--https.port=9999 - to override port for https (default: 8084)
--https.key-alias=tomcat - to provide key alias (default: tomcat)
--https.keystore-passwd=new_passwd - to provide keystore password (default: changeit)
--https.truststore-passwd=some_passwd - to provide truststore password (default: changeit)

