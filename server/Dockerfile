FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
ARG JAR_FILE=target/server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} server.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /server.jar"]