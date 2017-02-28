@echo off
::  First change to correct directory, otherwise paths to config files don't get set properly
set PWD=%~dp0
:: set PWD = %CD%
:: chdir %PWD%
echo Running ${project.artifactId} from folder "%PWD%"
PAUSE
::  Always run using 'exec' - so bash will kill the jvm when it receives a kill signal - see http://veithen.github.io/2014/11/16/sigterm-propagation.html
 java -Xms256m -Xmx1000m -Dlog4j.configuration=file:%PWD%config\log4j.properties -jar %PWD%${project.artifactId}.${package.type} -Dspring.profiles.active=prod,swagger --spring.profiles.active=prod,swagger
 :: java -jar %PWD%${project.artifactId}.jar
 PAUSE