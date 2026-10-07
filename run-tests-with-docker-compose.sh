#!/bin/bash
# Запуск всех API и UI тестов на окружении из Docker Compose.
# Использование: ./run-tests-with-docker-compose.sh

set -uo pipefail

COMPOSE_DIR=infra/docker_compose
COMPOSE_FILE=$COMPOSE_DIR/docker-compose.yml
BROWSERS_JSON=$COMPOSE_DIR/config/browsers.json
IMAGE_NAME=nbank-test
TEST_RUN="test-run-$(date +%Y%m%d_%H%M)"
TEST_OUTPUT_DIR="$PWD/test-output/$TEST_RUN"

SERVER=http://backend:4111/
BASEUIURL=http://nginx
UIREMOTE=http://selenoid:4444/wd/hub
DB_URL=jdbc:postgresql://postgres:5432/nbank
TEST_PROFILE=${1:-}

echo ">>> Скачивание образов браузеров из $BROWSERS_JSON"
for image in $(jq -r '.. | objects | select(.image) | .image' "$BROWSERS_JSON"); do
  echo "Pulling $image..."
  docker pull "$image"
done

echo ">>> Поднятие тестового окружения"
docker compose -f "$COMPOSE_FILE" up -d --wait

echo ">>> Сборка образа с тестами"
docker build -t $IMAGE_NAME .
mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/reports"
mkdir -p "$TEST_OUTPUT_DIR/surefire-reports"

echo ">>> Запуск тестов"
docker run --rm \
  --name nbank-test \
  --network nbank-network \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/surefire-reports":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/reports":/app/target/reports \
  -e SERVER=$SERVER \
  -e BASEUIURL=$BASEUIURL \
  -e UIREMOTE=$UIREMOTE \
  -e DB_URL=$DB_URL \
  -e TEST_PROFILE="$TEST_PROFILE" \
$IMAGE_NAME
echo ">>> Тесты завершились. Отчеты - '$TEST_OUTPUT_DIR/reports'"

echo ">>> Останавка тестового окружения"
docker compose -f "$COMPOSE_FILE" down
