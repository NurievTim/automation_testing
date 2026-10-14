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

# Код возврата контейнера = код возврата mvn test, отчет строится в любом случае
CMD mkdir -p /app/logs ; \
    { \
        if [ -n "${TEST_PROFILE:-}" ]; then \
            echo ">>> Running tests with profile $TEST_PROFILE" ; \
            mvn test -q -P "$TEST_PROFILE" ; \
        else \
            echo '>>> Running all tests without profile' ; \
            mvn test -q ; \
        fi ; \
        TEST_EXIT_CODE=$? ; \
        echo '>>> Running surefire report' ; \
        mvn surefire-report:report-only ; \
        exit $TEST_EXIT_CODE ; \
    } > /app/logs/run.log 2>&1
