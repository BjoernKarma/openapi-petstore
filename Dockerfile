ARG BASE_IMAGE_NAME
ARG BASE_IMAGE_VERSION
FROM ${BASE_IMAGE_NAME}:${BASE_IMAGE_VERSION} AS build
ARG APPLICATION
ARG JAR_FILE=./build/libs/${APPLICATION}.jar

VOLUME /tmp
WORKDIR /

COPY ${JAR_FILE} application.jar

ENV JAVA_OPTS="-XshowSettings:vm -XX:MaxRAMPercentage=50 -XX:+UseSerialGC -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=40"
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /application.jar ${0} ${@}"]

USER cloud