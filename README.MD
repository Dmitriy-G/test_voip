## About project
This is a simple voice transmitter. It works like one room, all active clients can talk with each other.

The main goal of this project it's the practice in using sockets and pure java, without frameworks and libraries. Also, the practice in implementation of some custom solutions like a secured UDP.

## Project Structure
Client - jar file that will run the user. The client must open the UDP socket and send/receive voice.

Server - jar file that will run on the server side. The server must listen UDP port, receive data from clients and send data to clients.

## Tech stack
Javax Sound - for capturing and reproducing voice

Java Net - for transferring voice via network

Java Concurrent - for working with multiple clients

Javax Swing/AWT - for a simple user interface

Java Security/Javax Crypto - for implementation of security

Bat/Sh scripts - for building these jar files

Docker - for deploying server jar

Jenkins - for creating the pipeline

## Functional requirements
### Client
1. Must capture voice from the microphone.
2. Must play sound via the sound channel.
3. Must transfer voice to server
4. Must receive data from the server, convert it to sound, and play it.

### Server
1. Must work with multiple clients
2. Must correct receive and transfer the voice

## Non functional requirements
### Security
1. Identification - server must determine valid clients. All packages from other tools must be rejected and registered in the log.
2. Client authentication - the server must determine an actual client. All clients must confirm their identity. If the client pretends to be another client, the package must be rejected. Also, must be a registered security incident.
3. Server authentication - the client must be sure that he works with the real server.
   
   If a client receives a package from the fake server, the package must be rejected. Also, must be a registered security incident.
   
   Clients must not send any package to a fake server.
   
   If the client decides that configured server is a fake server or the configuration is incorrect, the client must be told about it to the user and wait for a new configuration.
   
4. Transfer data via not secured channel (UDP) - before data will be transferred via a network, it must be encrypted.

### Reliability
1. Server must work with 100+ clients simultaneously
2. Must not be any latency or voice distortions

## How to run
### Push image

docker tag "image-name"
docker image push "image-name"

### Pull image and run
docker pull "image-name"
docker run --name "test-voip" -d -p 3333:3333/udp "image-name"
