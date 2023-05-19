FROM eclipse-temurin:11-jre-jammy
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/schema.sql /docker-entrypoint-initdb.d/
RUN apt-get update && apt-get install -y postgresql-client
ENTRYPOINT ["sh", "-c", "sleep 10 && PGPASSWORD=shareit psql --host=db -p 5432 -U shareit -d shareit -f /docker-entrypoint-initdb.d/schema.sql && java ${JAVA_OPTS} -jar /app.jar"]

