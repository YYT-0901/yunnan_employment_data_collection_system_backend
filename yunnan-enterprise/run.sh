#!/bin/bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
echo "使用 Java 版本:"
java -version
echo ""
echo "正在启动应用..."
./mvnw spring-boot:run
