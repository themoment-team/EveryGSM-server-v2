#!/bin/bash

APP_NAME="everygsm"
HEALTH_URL="http://localhost:8080/actuator/health"
MAX_RETRIES=20
SLEEP_INTERVAL=5

echo ">>> Health check for $APP_NAME"

for i in $(seq 1 $MAX_RETRIES); do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_URL)

  if [ "$STATUS" -eq 200 ]; then
    echo ">>> Health check passed (attempt $i/$MAX_RETRIES)"
    exit 0
  fi

  echo ">>> Waiting for app to be ready... ($i/$MAX_RETRIES, status: $STATUS)"
  sleep $SLEEP_INTERVAL
done

echo ">>> Health check failed after $MAX_RETRIES attempts"
exit 1
