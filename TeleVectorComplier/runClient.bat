rem jar name
@set jarPath=Tele.jar

rem lib directory name
@set libPath=lib

rem JVM parameter
@set javaParameter=-XX:ThreadStackSize=2048 -Xms1024m -Xmx1024m

rem exc
@java %javaParameter% -Djava.ext.dirs=%libPath% -jar %jarPath%
