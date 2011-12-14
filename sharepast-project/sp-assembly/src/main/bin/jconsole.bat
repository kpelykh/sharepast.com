@echo off
set JAVA_HOME="C:\Program Files\Java\jdk1.6.0_23"
echo service:jmx:jmxmp://localhost:9092
rem set JMX_ARGUMENTS=service:jmx:jmxmp://localhost:9092
%JAVA_HOME%\bin\jconsole.exe -J-Djava.class.path=%JAVA_HOME%\lib\jconsole.jar;%JAVA_HOME%\lib\tools.jar;%USERPROFILE%\.m2\repository\com\sun\jdmk\jmxremote_optional\5.1\jmxremote_optional-5.1.jar"