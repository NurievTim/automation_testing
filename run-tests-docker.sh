#!/bin/bash

IMAGE_NAME=nbank-test
TEST_RUN="test-run-$(date +%Y%m%d_%H%M)"
TEST_OUTPUT_DIR=./test-output/$TEST_RUN
TEST_PROFILE=${1:-api}

docker build -t $IMAGE_NAME .
mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/reports"
mkdir -p "$TEST_OUTPUT_DIR/surefire-reports"

echo ">>> Start tests"
docker run --rm \
  --name nbank-test \
  --network nbank-network \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/surefire-reports":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/reports":/app/target/reports \
  -e SERVER=http://backend:4111/ \
  -e BASEUIURL=http://nginx \
  -e UIREMOTE=http://selenoid:4444/wd/hub \
  -e DB_URL=jdbc:postgresql://postgres:5432/nbank \
  -e TEST_PROFILE="$TEST_PROFILE" \
$IMAGE_NAME
echo ">>> Tests finished. Reports - '$TEST_OUTPUT_DIR/reports'"