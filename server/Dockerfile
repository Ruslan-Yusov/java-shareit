FROM amazoncorretto:11-alpine-jdk
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} appServer.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar -Dserver.port=9090 /appServer.jar"]