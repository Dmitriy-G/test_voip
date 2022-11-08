cd src
javac client/*.java
jar cfe ../out/client.jar client.ClientApplication client
del /S *.class
timeout 10
