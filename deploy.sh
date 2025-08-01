#!/bin/bash

BUILD_PATH=$(ls -t build/libs/*.jar | grep -v plain | head -n 1)
JAR_NAME=$(basename "$BUILD_PATH")

echo "> JAR 이름: $JAR_NAME"

CURRENT_PID=$(pgrep -f "$JAR_NAME")

if [ -n "$CURRENT_PID" ]; then
  echo "> 기존 프로세스 종료: $CURRENT_PID"
  kill -15 "$CURRENT_PID"
  sleep 3
fi

DEPLOY_JAR="$BUILD_PATH"
echo "> 실행할 JAR: $DEPLOY_JAR"
nohup java -jar "$DEPLOY_JAR" > nohup.out 2>&1 &

echo "> 배포 완료"