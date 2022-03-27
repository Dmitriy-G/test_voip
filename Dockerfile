FROM adoptopenjdk/openjdk15

COPY /out/server.jar /server.jar
EXPOSE 50066

ENTRYPOINT ["java","-jar", "server.jar", "localhost", "50066"]
