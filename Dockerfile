FROM amazoncorretto:21
ENV TZ=Asia/Dhaka

ADD gateway-server.jar gateway-server.jar
ENTRYPOINT ["java", "-jar", "/gateway-server.jar"]
