@echo off
setlocal
if not exist "%~dp0target\name-sorter.jar" (
    echo Build first: run mvnw.cmd clean verify in the project directory. 1>&2
    exit /b 1
)
java -jar "%~dp0target\name-sorter.jar" %*
exit /b %errorlevel%
