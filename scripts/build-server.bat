cd src
javac server/*.java
jar cfe ../out/server.jar server.ServerApplication server
del /S *.class
timeout 10
