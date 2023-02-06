FROM openjdk:11
EXPOSE 8081:8081
RUN mkdir /app
COPY ./build/libs/*-all.jar /app/beigam-auth.jar
COPY ./config/application.conf /app/app.conf
ENTRYPOINT ["java","-jar","/app/beigam-auth.jar","-config=/app/app.conf"]