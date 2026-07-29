# 构建阶段固定 Maven/JDK 版本，使开发机与 CI 产生相同字节码。
FROM maven:3.9.10-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY app/bms-app/pom.xml app/bms-app/pom.xml
COPY apps/syslog-simulator/pom.xml apps/syslog-simulator/pom.xml
COPY apps/snmp-simulator/pom.xml apps/snmp-simulator/pom.xml
COPY apps/snmp-get-lambda/pom.xml apps/snmp-get-lambda/pom.xml
COPY apps/tcp-ping-lambda/pom.xml apps/tcp-ping-lambda/pom.xml
COPY apps/alert-function/pom.xml apps/alert-function/pom.xml
COPY app/bms-app app/bms-app
COPY apps apps
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl app/bms-app -am -DskipTests package

# 运行阶段不包含 Maven，安装 curl 只用于容器健康检查。
FROM eclipse-temurin:25-jre-alpine
RUN apk add --no-cache curl \
    && addgroup -S bms \
    && adduser -S -G bms -u 10001 bms
WORKDIR /opt/bms
COPY --from=build /workspace/app/bms-app/target/bms-app-*.jar app.jar
RUN chown -R bms:bms /opt/bms
USER 10001:10001
EXPOSE 8080 5514/tcp 5514/udp 1162/udp
HEALTHCHECK --interval=10s --timeout=5s --start-period=35s --retries=6 \
  CMD curl --fail http://localhost:8080/actuator/health/readiness || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/opt/bms/app.jar"]
