cd src
javac server/*.java
jar cfe ../out/server.jar server.ServerApplication server/service/*.class server/utils/*.class server/*.class
del /S *.class
timeout 10
