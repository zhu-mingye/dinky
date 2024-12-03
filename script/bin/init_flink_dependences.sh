#!/bin/bash

# 拿到参数并赋值变量
CURRENT_FLINK_FULL_VERSION=$1
FLINK_VERSION_SCAN=$2
DINKY_TMP_DIR=$3
EXTENDS_HOME=$4
DINKY_HOME=$5

echo -e "${GREEN}====================== Flink 依赖初始化 ======================${RESET}"

echo -e "${BLUE}参数: 当前Flink版本为：${CURRENT_FLINK_FULL_VERSION}，扫描的Flink版本为：${FLINK_VERSION_SCAN} ，临时目录为：${DINKY_TMP_DIR} ，扩展包目录为：${EXTENDS_HOME} ，Dinky 根目录为：${DINKY_HOME}${RESET}"

# 检查参数
if [ -z "$CURRENT_FLINK_FULL_VERSION" ] || [ -z "$FLINK_VERSION_SCAN" ] || [ -z "$DINKY_TMP_DIR" ] || [ -z "$EXTENDS_HOME" ] || [ -z "$DINKY_HOME" ]; then
  echo -e "${RED}参数错误，请检查！${RESET}"
  exit 1
fi

# 检查是否已存在flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz 文件
if [ -f "$DINKY_TMP_DIR/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz" ]; then
  echo -e "${YELLOW}$DINKY_TMP_DIR 下已存在 flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz 文件，为了确保完整性, 执行先删除 ${DINKY_TMP_DIR}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz 文件后重新下载${RESET}"
  rm -rf ${DINKY_TMP_DIR}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz
  # 是否有解压之后的flink目录，有则删除
  if [ -d "$DINKY_TMP_DIR/flink-${CURRENT_FLINK_FULL_VERSION}" ]; then
    echo -e "${YELLOW}已存在 flink 目录，删除 $DINKY_TMP_DIR/flink-${CURRENT_FLINK_FULL_VERSION}"
    rm -rf $DINKY_TMP_DIR/flink-${CURRENT_FLINK_FULL_VERSION}
  fi
fi

# 尝试从清华大学镜像下载
try_tsinghua_mirror() {
    local tsinghua_url="https://mirrors.tuna.tsinghua.edu.cn/apache/flink/flink-${CURRENT_FLINK_FULL_VERSION}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz"
    local apache_url="https://archive.apache.org/dist/flink/flink-${CURRENT_FLINK_FULL_VERSION}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz"

    echo -e "${GREEN}开始下载 Flink-${FLINK_VERSION_SCAN} 安装包... 存放至 ${DINKY_TMP_DIR} 目录下${RESET}"
    if download_file "$tsinghua_url" "$DINKY_TMP_DIR"; then
        echo -e "${BLUE}当前下载的Flink安装包地址为：${tsinghua_url}${RESET}"
        return 0
    else
        echo -e "${YELLOW}清华大学镜像中未找到文件，尝试从 Apache 官方源下载...${RESET}"
        if download_file "$apache_url" "$DINKY_TMP_DIR"; then
            echo -e "${BLUE}当前下载的Flink安装包地址为：${apache_url}${RESET}"
            return 0
        else
            echo -e "${RED}从 Apache 官方源下载也失败了，请检查网络或手动下载。${RESET}"
            return 1
        fi
    fi
}

# 调用下载函数
if ! try_tsinghua_mirror; then
    exit 0
fi


echo -e "${GREEN}Flink安装包下载完成。${RESET}"
echo -e "\n${GREEN}===============================================================${RESET}\n"
echo -e "${GREEN}开始解压Flink安装包...${RESET}"
tar -zxvf ${DINKY_TMP_DIR}/flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz -C ${DINKY_TMP_DIR}/
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Flink安装包解压完成。${RESET}"
else
    echo -e "${RED}Flink安装包解压失败，请检查。${RESET}"
    exit 1
fi

echo -e "\n${GREEN}===============================================================${RESET}\n"

# 查看并获取解压后的flink开头的目录名称
flink_dir_tmp=$(ls -n ${DINKY_TMP_DIR} | grep '^d' | grep flink | awk '{print $9}')
full_flink_dir_tmp="${DINKY_TMP_DIR}/${flink_dir_tmp}"
echo -e "${BLUE}解压后的目录名称：${full_flink_dir_tmp}${RESET}"



echo -e "${GREEN}处理 ${full_flink_dir_tmp}/lib/flink-table-planner-loader* 文件...${RESET}"
rm -rf ${full_flink_dir_tmp}/lib/flink-table-planner-loader*
echo -e "${GREEN}处理完成。${RESET}"

echo -e "${GREEN}处理 ${full_flink_dir_tmp}/opt/flink-table-planner_2.12-*.jar 文件...${RESET}"
mv ${full_flink_dir_tmp}/opt/flink-table-planner_2.12-*.jar ${full_flink_dir_tmp}/lib/
echo -e "${GREEN}处理完成。${RESET}"

echo -e "${GREEN}处理 flink jar 依赖 到 dinky 中...${RESET}"
cp -r ${full_flink_dir_tmp}/lib/*.jar ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
echo -e "${GREEN}jar 依赖处理完成。${RESET}"

echo -e "${GREEN}处理 flink-sql-client ...${RESET}"
cp -r ${full_flink_dir_tmp}/opt/flink-sql-client-*.jar ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
echo -e "${GREEN}处理完成。${RESET}"

echo -e "${GREEN}处理 flink-cep-scala ...${RESET}"
cp -r ${full_flink_dir_tmp}/opt/flink-cep-scala*.jar ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
echo -e "${GREEN}处理完成。${RESET}"

echo -e "${GREEN}处理 flink-queryable-state-runtime ...${RESET}"
cp -r ${full_flink_dir_tmp}/opt/flink-queryable-state-runtime*.jar ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
echo -e "${GREEN}处理完成。${RESET}"

echo -e "${GREEN}处理 flink-state-processor-api ...${RESET}"
cp -r ${full_flink_dir_tmp}/opt/flink-state-processor-api*.jar ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
echo -e "${GREEN}处理完成。${RESET}"

echo -e "${GREEN} ================= 列出 ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/ 目录下的文件 ==============${RESET}"
ls -l ${EXTENDS_HOME}/flink${FLINK_VERSION_SCAN}/
# 请核对以上依赖文件
echo -e "${YELLOW}请核对以上依赖文件。${RESET}"

echo -e "${GREEN}基础依赖处理完成, 请根据实际情况进行后续操作。${RESET}"