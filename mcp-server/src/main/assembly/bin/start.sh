#!/bin/bash

# 项目名称
APPLICATION="mcp-server"

# 获取脚本所在目录
BIN_PATH=$(cd "$(dirname "$0")" && pwd)
cd "$BIN_PATH"/..
BASE_PATH=$(pwd)

# 查找jar文件（取最新版本）
JAR_FILE=$(find "${BASE_PATH}" -name "mcp-server*.jar" -type f | sort -V | tail -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "错误：未找到 mcp-server*.jar 文件"
    echo "请在以下目录中查找："
    find "${BASE_PATH}" -type f -name "*.jar"
    exit 1
fi

echo "使用jar文件: $JAR_FILE"

# JVM 配置（根据实际服务器内存调整）
JVM_XMS="1g"
JVM_XMX="1g"
JVM_XSS="256k"

JAVA_OPT="-server -Xms${JVM_XMS} -Xmx${JVM_XMX} -Xss${JVM_XSS} \
-XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=${BASE_PATH}/logs/heapdump.hprof \
-Dfile.encoding=UTF-8"

# 创建日志目录
mkdir -p "${BASE_PATH}/logs/"

# 检查日志配置文件
LOG_CONFIG="${BASE_PATH}/config/logback-spring.xml"
if [ ! -f "$LOG_CONFIG" ]; then
    echo "警告：日志配置文件不存在: $LOG_CONFIG"
    # 使用默认配置
    LOGGING_OPT=""
else
    LOGGING_OPT="--logging.config=${LOG_CONFIG}"
fi

# 启动应用
echo "正在启动 ${APPLICATION}..."
echo "启动命令: nohup java ${JAVA_OPT} -jar \"${JAR_FILE}\" ${LOGGING_OPT} > \"${BASE_PATH}/logs/startup.log\" 2>&1 &"

nohup java ${JAVA_OPT} -jar \"${JAR_FILE}\" ${LOGGING_OPT} > \"${BASE_PATH}/logs/startup.log\" 2>&1 &

# 检查进程是否启动成功
sleep 3
PID=$(pgrep -f "java.*$(basename "$JAR_FILE")")
if [ -n "$PID" ]; then
    echo "启动成功！PID: $PID"
    echo "日志文件: ${BASE_PATH}/logs/startup.log"
else
    echo "启动失败，请检查日志: ${BASE_PATH}/logs/startup.log"
    tail -50 "${BASE_PATH}/logs/startup.log"
    exit 1
fi