FROM openjdk:11-oracle
COPY ./app/target/*.jar /app.jar
# RUN  ["mkdir" , "/logger"]
# COPY ./app/src/main/resources/log4j2.xml /logger/log4j2.xml
ENTRYPOINT ["java", "-Dlog4j2.configurationFile=/logger/log4j2.xml", "-jar","/app.jar"]