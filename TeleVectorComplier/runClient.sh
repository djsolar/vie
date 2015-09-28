#!/bin/sh

#jar name
jarPath=Tele.jar

#lib directory name
libPath=lib

#JVM parameter
javaParameter="-XX:ThreadStackSize=2048 -Xms2048m -Xmx2048m -XXlargeObjectLimit:2k -Xgcprio:throughput"

#exc
java $javaParameter -Djava.ext.dirs=$libPath -jar $jarPath
