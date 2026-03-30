#!/bin/bash

APP_NAME="everygsm"
DEPLOY_DIR="/home/ec2-user/everygsm"

echo ">>> Starting $APP_NAME Docker containers"

cd $DEPLOY_DIR/deploy/docker

docker compose -f compose.prod.yaml --env-file $DEPLOY_DIR/.env up -d --build

echo ">>> Containers started"
