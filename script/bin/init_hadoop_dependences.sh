#!/bin/bash

# 拿到参数并赋值变量
EXTENDS_HOME=$1


echo -e "${GREEN}====================== Hadoop 依赖初始化 ======================${RESET}"
echo -e "${BLUE}参数: 扩展包目录为：${EXTENDS_HOME}${RESET}"

# 选择要下载的 Hadoop-uber 版本
read -p "请选择要下载的 Hadoop-uber 版本（输入 2 或者 3）：" hadoop_uber_version
hadoop_uber_version=$(echo "$hadoop_uber_version" | tr '[:upper:]' '[:lower:]' | tr -d '[:space:]')

# 根据用户选择的版本下载相应的 Hadoop-uber 包
case $hadoop_uber_version in
    2)
        echo -e "${YELLOW}开始下载 Hadoop-uber 2 版本包...${RESET}"
        # 检查是否已下载过 Hadoop-uber 包
        if [ -f "$EXTENDS_HOME/flink-shaded-hadoop-2-uber-2.8.3-10.0.jar" ]; then
            echo -e "${YELLOW}已存在 flink-shaded-hadoop-2-uber-2.8.3-10.0.jar 文件，无需重复下载。${RESET}"
        else
          download_url="https://repo1.maven.org/maven2/org/apache/flink/flink-shaded-hadoop-2-uber/2.8.3-10.0/flink-shaded-hadoop-2-uber-2.8.3-10.0.jar"
          download_file "$download_url" "$EXTENDS_HOME"
        fi
        ;;
    3)
        # 检查是否已下载过 Hadoop-uber 包
         # 检查是否已下载过 Hadoop-uber 包
        if [ -f "$EXTENDS_HOME/flink-shaded-hadoop-3-uber-3.1.1.7.2.9.0-173-9.0.jar" ]; then
            echo -e "${YELLOW}已存在 flink-shaded-hadoop-3-uber-3.1.1.7.2.9.0-173-9.0.jar 文件，无需重复下载。${RESET}"
        else
           echo -e "${YELLOW}开始下载 Hadoop-uber 3 版本包...${RESET}"
           download_url="https://repository.cloudera.com/artifactory/cloudera-repos/org/apache/flink/flink-shaded-hadoop-3-uber/3.1.1.7.2.9.0-173-9.0/flink-shaded-hadoop-3-uber-3.1.1.7.2.9.0-173-9.0.jar"
           download_file "$download_url" "$EXTENDS_HOME"
        fi
        ;;
    *)
        echo -e "${RED}输入的版本号不正确，请重新运行脚本选择正确的版本。${RESET}"
        ;;
esac

echo -e "${GREEN}下载完成，可按需进行后续安装配置操作。${RESET}"