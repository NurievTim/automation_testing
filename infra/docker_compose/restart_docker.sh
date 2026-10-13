#!/bin/bash

# Переходим в папку скрипта, чтобы относительные пути работали из любого места запуска
cd "$(dirname "$0")" || exit 1

echo ">>> Остановить Docker Compose"
docker compose down

echo ">>> Docker pull все образы браузеров"

# Образы браузеров для arm64 (Apple Silicon) и amd64 (GitHub runner) разные
if [ "$(docker info --format '{{.Architecture}}')" = "x86_64" ]; then
    export SELENOID_CONFIG_DIR=./config-amd64
else
    export SELENOID_CONFIG_DIR=./config
fi
echo ">>> Конфиг браузеров: $SELENOID_CONFIG_DIR"

# Путь до файла
json_file="$SELENOID_CONFIG_DIR/browsers.json"

# Проверяем, что jq установлен
if ! command -v jq &> /dev/null; then
    echo "❌ jq is not installed. Please install jq and try again."
    exit 1
fi

# Извлекаем все значения .image через jq
images=$(jq -r '.. | objects | select(.image) | .image' "$json_file")

# Пробегаем по каждому образу и выполняем docker pull
for image in $images; do
    echo "Pulling $image..."
    docker pull "$image"
done

export DOCKER_API_VERSION=$(docker version --format '{{.Server.APIVersion}}')
echo ">>> Docker API version: $DOCKER_API_VERSION"

echo ">>> Запуск Docker Compose"
docker compose up -d --wait