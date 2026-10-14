#!/bin/bash
# Запуск всех API и UI тестов на окружении из Docker Compose.
# Использование: ./run-tests-with-docker-compose.sh

set -euo pipefail

COMPOSE_DIR=infra/docker_compose
COMPOSE_FILE=$COMPOSE_DIR/docker-compose.yml
BROWSERS_JSON=$COMPOSE_DIR/config/browsers.json
IMAGE_NAME=nbank-test
CONTAINER_NAME=nbank-test
TEST_RUN="test-run-$(date +%Y%m%d_%H%M)"
TEST_OUTPUT_DIR="$PWD/test-output/$TEST_RUN"

SERVER=http://backend:4111/
BASEUIURL=http://nginx
UIREMOTE=http://selenoid:4444/wd/hub
DB_URL=jdbc:postgresql://postgres:5432/nbank
TEST_PROFILE=${1:-}

# Останавливаем окружение при любом выходе (успех, ошибка, Ctrl+C) и сохраняем код выхода
cleanup() {
  local exit_code=$?
  echo ">>> Остановка тестового окружения"
  docker rm -f $CONTAINER_NAME >/dev/null 2>&1 || true
  docker compose -f "$COMPOSE_FILE" down || true
  exit $exit_code
}
trap cleanup EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

echo ">>> Скачивание образов браузеров из $BROWSERS_JSON"
# Отдельное присваивание, чтобы ошибка jq останавливала скрипт (set -e не ловит ее внутри for)
BROWSER_IMAGES=$(jq -r '.. | objects | select(.image) | .image' "$BROWSERS_JSON")
for image in $BROWSER_IMAGES; do
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
TEST_EXIT_CODE=0
docker run --rm \
  --name $CONTAINER_NAME \
  --network nbank-network \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/surefire-reports":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/reports":/app/target/reports \
  -e SERVER=$SERVER \
  -e BASEUIURL=$BASEUIURL \
  -e UIREMOTE=$UIREMOTE \
  -e DB_URL=$DB_URL \
  -e TEST_PROFILE="$TEST_PROFILE" \
$IMAGE_NAME || TEST_EXIT_CODE=$?

if [ "$TEST_EXIT_CODE" -eq 0 ]; then
  echo ">>> Тесты прошли успешно. Отчеты - '$TEST_OUTPUT_DIR/reports'"
else
  echo ">>> Тесты упали (exit code $TEST_EXIT_CODE). Логи - '$TEST_OUTPUT_DIR/logs', отчеты - '$TEST_OUTPUT_DIR/reports'"
fi

exit $TEST_EXIT_CODE
