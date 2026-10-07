#!/bin/bash

set -euo pipefail

IMAGE_NAME=nbank-tests
DOCKERHUB_USER=nurievtim
TAG=${1:-latest}
FULL_IMAGE_NAME="$DOCKERHUB_USER/$IMAGE_NAME:$TAG"

docker login -u "$DOCKERHUB_USER"

docker build -t "$FULL_IMAGE_NAME" .

docker push "$FULL_IMAGE_NAME"

echo "✅ Docker image '$FULL_IMAGE_NAME' pushed successfully!"

echo "To download docker image run: 'docker pull $DOCKERHUB_USER/$IMAGE_NAME:$TAG' "