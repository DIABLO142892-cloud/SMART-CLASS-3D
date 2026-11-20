@echo off
setlocal

set MAVEN_HOME=%~dp0..\.mvn
if not exist "%MAVEN_HOME%\wrapper.jar" goto :download

"%MAVEN_HOME%\wrapper.jar" %*

goto :eof

:download
echo.
echo ERROR: Maven wrapper jar not found.
echo Please run 'mvn wrapper:wrapper' to generate the wrapper, or
echo download Maven manually from https://maven.apache.org/download.cgi
echo.
exit /b 1