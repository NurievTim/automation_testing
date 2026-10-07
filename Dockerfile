FROM maven:3.9.16-eclipse-temurin-25-alpine

ARG TEST_PROFILE=api
ARG APIBASEURL=http://localhost:4111
ARG UIBASEURL=http://localhost:3000

ENV TEST_PROFILE=${TEST_PROFILE}
ENV APIBASEURL=${APIBASEURL}
ENV UIBASEURL=${UIBASEURL}

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . .

USER root

CMD /bin/bash -c " \
    mkdir -p /app/logs ; \
    { \
    if [ -n \"${TEST_PROFILE:-}\" ]; then \
            echo '>>> Running tests with profile $TEST_PROFILE' ; \
            mvn test -q -P '$TEST_PROFILE' ; \
        else \
            echo '>>> Running all tests without profile' ; \
            mvn test -q ; \
        fi ; \
    echo '>>> Running surefire report' ; \
    mvn surefire-report:report-only ; \
    } > /app/logs/run.log 2>&1"
