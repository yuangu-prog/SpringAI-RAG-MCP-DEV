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

# 创建日志目录（使用绝对路径并确保创建成功）
LOGS_DIR="${BASE_PATH}/logs"
mkdir -p "${LOGS_DIR}"
if [ $? -ne 0 ]; then
    echo "错误：无法创建日志目录 ${LOGS_DIR}"
    exit 1
fi

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

# 使用简化的命令格式，避免引号嵌套问题
CMD="nohup java ${JAVA_OPT} -jar ${JAR_FILE} ${LOGGING_OPT} > ${LOGS_DIR}/startup.log 2>&1 &"
echo "启动命令: $CMD"

eval $CMD

# 检查进程是否启动成功
sleep 3
PID=$(pgrep -f "java.*$(basename "$JAR_FILE")")
if [ -n "$PID" ]; then
    echo "启动成功！PID: $PID"
    echo "日志文件: ${LOGS_DIR}/startup.log"
else
    echo "启动失败，请检查日志: ${LOGS_DIR}/startup.log"
    if [ -f "${LOGS_DIR}/startup.log" ]; then
        tail -50 "${LOGS_DIR}/startup.log"
    else
        echo "未找到启动日志文件"
        echo "检查目录权限:"
        ls -la "${BASE_PATH}/"
        ls -la "${BASE_PATH}/logs/" 2>/dev/null || echo "logs目录不存在"
    fi
    exit 1
fi