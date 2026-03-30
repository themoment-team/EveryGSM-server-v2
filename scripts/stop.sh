#!/bin/bash

APP_NAME="everygsm"
DEPLOY_DIR="/home/ec2-user/everygsm"

echo ">>> Stopping $APP_NAME Docker containers"

if ! docker compose -f $DEPLOY_DIR/deploy/docker/compose.prod.yaml ps -q app 2>/dev/null | grep -q .; then
  echo ">>> No running containers found, skipping stop"
  exit 0
fi

docker compose -f $DEPLOY_DIR/deploy/docker/compose.prod.yaml down
echo ">>> Containers stopped"
