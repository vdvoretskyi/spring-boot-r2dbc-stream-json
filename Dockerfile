FROM openjdk:8-jdk-alpine
MAINTAINER vdvoretskyi
VOLUME /tmp
EXPOSE 8080
ADD ./target/java.demo-0.0.1-SNAPSHOT.jar javademodocker.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/javademodocker.jar"]