FROM openjdk:10-jre-slim
COPY app.jar /
CMD ["java", "-jar", "app.jar"]
