FROM adoptopenjdk/openjdk15

COPY /out/server.jar /server.jar
EXPOSE 8080/udp

ENTRYPOINT ["java","-jar", "server.jar", "localhost", "3333"]
#ENTRYPOINT ["java","-jar", "-Xdebug", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "server.jar", "localhost", "8080"]
