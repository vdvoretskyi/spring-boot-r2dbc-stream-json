FROM openjdk:8-jdk-alpine
MAINTAINER vdvoretskyi
VOLUME /tmp
EXPOSE 8080
EXPOSE 5005
ADD ./target/java.demo-0.0.1-SNAPSHOT.jar javademodocker.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-Dreactor.netty.http.server.accessLogEnabled=true", "-Djava.security.egd=file:/dev/./urandom","-jar","/javademodocker.jar"]