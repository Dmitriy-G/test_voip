#!/bin/bash
javac src/server/*.java
jar cfe ../out/server.jar server.ServerApplication server
