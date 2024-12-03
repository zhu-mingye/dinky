#!/bin/bash



# 定义颜色变量
export RED='\033[31m'
export GREEN='\033[32m'
export YELLOW='\033[33m'
export BLUE='\033[34m'
export MAGENTA='\033[35m'
export CYAN='\033[36m'
export RESET='\033[0m'

# 输出欢迎图案
echo -e "${GREEN}=====================================================================${RESET}"
echo -e "${GREEN}=====================================================================${RESET}"
echo -e "${GREEN}==================== 欢迎使用 Dinky 初始化脚本 =====================${RESET}"
echo -e "${GREEN}======================================================================${RESET}"
echo -e "${GREEN}======================================================================${RESET}"

# 获取安装的目录 APP_HOME 先从环境变量中获取，如果没有则使用脚本所在目录
# 先拿 DINKY_HOME 环境变量，如果没有则使用脚本所在目录
APP_HOME=${DINKY_HOME:-$(cd "$(dirname "$0")"; cd ..; pwd)}
export DINKY_HOME=${APP_HOME}

sudo chmod +x "${APP_HOME}"/bin/init_*.sh



EXTENDS_HOME="${APP_HOME}/extends"
if [ ! -d "${EXTENDS_HOME}" ]; then
    echo -e "${RED} ${EXTENDS_HOME} 目录不存在，请检查 ${RESET}"
    exit 1
fi

# 获取Dinky部署的Flink对应的版本号
FLINK_VERSION_SCAN=$(ls -n "${EXTENDS_HOME}" | grep '^d' | grep flink | awk -F 'flink' '{print $2}')
if [ -z "${FLINK_VERSION_SCAN}" ]; then
    echo -e "${RED} Dinky 部署的目录下 ${EXTENDS_HOME} 不存在 Flink 相关版本, 无法进行初始化操作，请检查。 ${RESET}"
    exit 1
fi

# 临时目录
DINKY_TMP_DIR="${APP_HOME}/tmp"
if [ ! -d "${DINKY_TMP_DIR}" ]; then
    echo -e "${YELLOW}创建临时目录 ${DINKY_TMP_DIR}...${RESET}"
    mkdir -p "${DINKY_TMP_DIR}"
    echo -e "${GREEN}临时目录创建完成${RESET}"
fi

# LIB
DINKY_LIB="${APP_HOME}/lib"
if [ ! -d "${DINKY_LIB}" ]; then
    echo -e "${RED}${DINKY_LIB} 目录不存在，请检查。 ${RESET}"
    exit 1
fi

# 函数：检查命令是否存在，不存在则尝试安装
check_command() {
    local cmd="$1"
    echo -e "${BLUE}检查命令：$cmd 是否存在......${RESET}"
    if ! command -v "$cmd" &> /dev/null; then
        if [ "$cmd" == "yum" ]; then
            echo -e "${YELLOW} 尝试使用yum安装缺失的命令...${RESET}"
            sudo yum install -y "$cmd"
        elif [ "$cmd" == "apt-get" ]; then
            echo -e "${YELLOW}尝试使用apt-get安装缺失的命令...${RESET}"
            sudo apt-get install -y "$cmd"
        else
            echo -e "${RED} $cmd 命令未找到，请手动安装后再运行此脚本。${RESET}"
            exit 1
        fi
    fi
    echo -e "${GREEN}========== 命令 $cmd 检查完成。 OK, 继续执行脚本。 ==========${RESET}"
}

sh "${APP_HOME}/bin/init_check_network.sh"

# 检查wget是否存在，不存在则尝试安装
check_command "wget"

echo -e "${GREEN}前置检查完成，欢迎使用 Dinky 初始化脚本，当前 Dinky 根路径为：${APP_HOME} ${RESET}"

function download_file() {
    source_url=$1
    target_file_dir=$2
    echo -e "${GREEN}开始下载 $source_url 到 $target_file_dir...${RESET}"
    wget -P "${target_file_dir}" "${source_url}"
    echo -e "${GREEN}下载完成。下载的文件存放地址为： $target_file_dir ${RESET}"
}

# 导出函数
export -f download_file

echo
echo
echo -e "${GREEN} ====================== 数据源驱动初始化脚本 -> 开始 ====================== ${RESET}"

while true; do
    # 步骤1：获取数据库类型，判断是否为mysql，若是则下载驱动包
    echo -e "${BLUE} ============ 请输入你的数据库类型 ================ ${RESET}"
    echo -e "${BLUE} ======== (h2 默认自带不需要执行该步骤) ===========  ${RESET}"
    echo -e "${BLUE} ============== 请选择 1、2、3 ==================  ${RESET}"
    echo -e "${BLUE} ================ 1. mysql =====================  ${RESET}"
    echo -e "${BLUE} ================ 2. pgsql =====================  ${RESET}"
    echo -e "${BLUE} ================ 3. 跳过该步骤 ==================  ${RESET}"
    echo -e "${BLUE} ================ 输入数字选择 ===================  ${RESET}"
    read -p "请输入你的数据库类型：" db_type
    case $db_type in
        1)
            echo -e "${GREEN}开始下载 mysql 驱动包...${RESET}"
            # 这里替换为真实有效的下载链接
            # 检查是否已经存在
            if [ -f "${DINKY_LIB}/mysql-connector-j-8.4.0.jar" ]; then
                echo -e "${GREEN}mysql 驱动包已存在，无需重复下载。跳过该步骤。${RESET}"
            else
                download_file https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar "${DINKY_LIB}"
                echo -e "${GREEN}下载完成，校验一下。下载的文件存放地址为： ${DINKY_LIB}/mysql-connector-j-8.4.0.jar${RESET}"
                if [ -f "${DINKY_LIB}/mysql-connector-j-8.4.0.jar" ]; then
                    echo -e "${GREEN}mysql 驱动包下载成功。${RESET}"
                else
                    echo -e "${RED}mysql 驱动包下载失败，请检查网络或手动下载。${RESET}"
                    exit 1
                fi
                echo -e "${GREEN}校验完成，可按需进行后续安装配置操作。${RESET}"
            fi
            break  # 退出循环
            ;;
        2)
            echo -e "${GREEN}貌似已经默认集成了 pgsql，无需执行该步骤。请按需进行后续安装配置操作。${RESET}"
            break  # 退出循环
            ;;
        3)
            echo -e "${GREEN}跳过该步骤。${RESET}"
            break  # 退出循环
            ;;
        *)
            echo -e "${RED}输入的数据库类型不正确，请重新运行脚本选择正确的数据库类型。${RESET}"
            ;;
    esac
done
echo -e "${GREEN} ====================== 数据源驱动初始化脚本 -> 结束 ====================== ${RESET}"

echo
echo

echo -e "${GREEN} ====================== Flink 依赖初始化脚本 -> 开始 ====================== ${RESET}"

declare -A version_map
# 创建个 map key 是 1.20 value 是 1.20.0
version_map["1.14"]="1.14.6"
version_map["1.15"]="1.15.4"
version_map["1.16"]="1.16.3"
version_map["1.17"]="1.17.2"
version_map["1.18"]="1.18.1"
version_map["1.19"]="1.19.1"
version_map["1.20"]="1.20.0"

FLINK_VERSION_SCAN=$(ls -n "${EXTENDS_HOME}" | grep '^d' | grep flink | awk -F 'flink' '{print $2}')
if [ -z "${FLINK_VERSION_SCAN}" ]; then
    echo -e "${RED}Dinky 部署的目录下 ${EXTENDS_HOME} 不存在 Flink 相关版本, 无法进行初始化操作，请检查。${RESET}"
    exit 1
else
    echo -e "${GREEN}当前 Dinky 部署的 Flink 版本号：${FLINK_VERSION_SCAN}${RESET}"
fi

# 根据 Dinky 部署的Flink对应的版本号，获取对应的 Flink 版本
CURRENT_FLINK_FULL_VERSION=${version_map[$FLINK_VERSION_SCAN]}

echo -e "${GREEN}根据扫描的当前 Flink 版本号获取 部署的 Flink 对应的版本号(全版本号)为：flink-${CURRENT_FLINK_FULL_VERSION}${RESET}"

# 步骤2：获取Dinky部署的Flink对应的版本号，然后下载Flink安装包
while true; do
    read -p "检测到 Dinky 部署的Flink版本号为：${FLINK_VERSION_SCAN}, 需要下载的 Flink 安装包版本号为：flink-${CURRENT_FLINK_FULL_VERSION}-bin-scala_2.12.tgz , 请选择是否初始化 Flink 相关依赖？（yes/no/exit）" is_init_flink
    is_init_flink=$(echo "$is_init_flink" | tr '[:upper:]' '[:lower:]' | tr -d '[:space:]')

    case $is_init_flink in
        yes | y )
            sh "${APP_HOME}"/bin/init_flink_dependences.sh "${CURRENT_FLINK_FULL_VERSION}" "${FLINK_VERSION_SCAN}" "${DINKY_TMP_DIR}" "${EXTENDS_HOME}" "${APP_HOME}"
            break  # 退出循环
            ;;
        no | n )
            echo -e "${GREEN}已跳过Flink安装包下载操作。请手动下载。${RESET}"
            break  # 退出循环
            ;;
        exit | e )
            echo -e "${GREEN}你选择了 exit，程序将退出。${RESET}"
            exit 0  # 退出脚本
            ;;
        *)
            echo -e "${RED}输入无效，请重新输入 yes/no/exit。${RESET}"
            ;;
    esac
done
echo -e "${GREEN} ====================== Flink 依赖初始化脚本 -> 结束 ====================== ${RESET}"

echo
echo

echo -e "${GREEN} ====================== Hadoop 依赖初始化脚本 -> 开始 ====================== ${RESET}"

# 步骤：询问是否是Hadoop环境，若是则等待用户选择下载hadoop-uber的版本是2还是3
while true; do
    read -p "你的部署环境是否是Hadoop环境？（yes/no/exit）" is_hadoop
    # 将输入转换为小写，以便进行不区分大小写的比较
    is_hadoop=$(echo "$is_hadoop" | tr '[:upper:]' '[:lower:]' | tr -d '[:space:]')
    case $is_hadoop in
        yes | y )
            sh "${APP_HOME}/bin/init_hadoop_dependences.sh" "${EXTENDS_HOME}"
            break  # 退出循环
            ;;
        no | n )
            echo -e "${GREEN}已跳过Hadoop相关操作。${RESET}"
            break  # 退出循环
            ;;
        exit | e )
            echo -e "${GREEN}你选择了 exit，程序将退出。${RESET}"
            exit 0  # 退出脚本
            ;;
        *)
            echo -e "${RED}输入无效，请重新输入 yes/no/exit。${RESET}"
            ;;
    esac
done
echo -e "${GREEN} ====================== Hadoop 依赖初始化脚本 -> 结束 ====================== ${RESET}"
echo

echo -e "${GREEN} === 环境初始化完成，接下来可以进行配置 Dinky 的 conf 目录下的 application 配置文件进行数据库相关配置, 或者执行初始化配置文件。====  ${RESET}"
echo

echo -e "${GREEN} ====================== 数据库配置文件初始化脚本 -> 开始 ====================== ${RESET}"

# 初始化配置文件
while true; do
    read -p "是否需要初始化数据库配置文件？(yes/no)：" is_init_db
    # 将输入转换为小写，以便进行不区分大小写的比较
    is_init_db=$(echo "$is_init_db" | tr '[:upper:]' '[:lower:]' | tr -d '[:space:]')
    case $is_init_db in
        yes | y )
            sh "${APP_HOME}/bin/init_db.sh" "${DINKY_HOME}"
            break  # 退出循环
            ;;
        no | n )
            echo -e "${GREEN}已跳过数据库初始化操作, 请手动配置数据库 ${DINKY_HOME}/config/application.yml 文件和 ${DINKY_HOME}/config/application-[mysql/pgsql].yml 文件。${RESET}"
            break  # 退出循环
            ;;
        exit | e )
            echo -e "${GREEN}已退出脚本，请手动配置数据库 ${DINKY_HOME}/config/application.yml 文件和 ${DINKY_HOME}/config/application-[mysql/pgsql].yml 文件。${RESET}"
            exit 0  # 退出脚本
            ;;
        *)
            echo -e "${RED}输入无效，请重新输入 yes/no/exit。${RESET}"
            ;;
    esac
done
echo -e "${GREEN} ====================== 数据库配置文件初始化脚本 -> 结束 ====================== ${RESET}"