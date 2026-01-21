#!/bin/bash

# bin目录绝对路径
BIN_DIR=$(cd "$(dirname "$0")" && pwd)

# 返回到上一级项目根目录路径
DEPLOY_DIR=$(cd "$BIN_DIR/.." && pwd)

SERVER_NAME="mcp-server"

# 使用 pgrep 查找正在运行的服务 PID
PIDS=$(pgrep -f "$SERVER_NAME")

if [ -z "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME is not started!"
    exit 1
fi

echo -e "Stopping the $SERVER_NAME ...\c"

# 尝试停止服务
for PID in $PIDS; do
    kill "$PID" > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "Successfully sent kill signal to PID: $PID"
    else
        echo "Failed to send kill signal to PID: $PID"
    fi
done

# 等待服务停止
COUNT=0
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1
    COUNT=1
    for PID in $PIDS; do
        if ps -p "$PID" > /dev/null 2>&1; then
            COUNT=0
            break
        fi
    done
done

echo "OK!"
echo "Stopped PIDs: $PIDS"