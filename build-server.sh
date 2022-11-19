#!/bin/bash
javac server/*.java
jar cfe server.jar server.ServerApplication server
