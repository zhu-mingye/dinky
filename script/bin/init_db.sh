#!/bin/bash

DINKY_HOME=$1

echo -e "${GREEN}====================== 数据库配置文件初始化 ======================${RESET}"
echo -e "${BLUE}参数: Dinky 根目录为：${DINKY_HOME}${RESET}"

# 检查参数
if [ -z "$DINKY_HOME" ]; then
  echo -e "${RED}参数错误，请检查！${RESET}"
  exit 1
fi

while true; do
  read -p "请选择数据库类型(1.MySQL 2.PostgreSQL)：" db_type
  read -p "请输入数据库地址(主机名或IP, 默认 localhost): " -i "localhost" db_host
  read -p "请输入数据库端口(默认 3306): " -i 3306 db_port
  read -p "请输入数据库名称(默认 dinky): " -i "dinky" db_name
  read -p "请输入数据库用户名(默认 dinky): " -i "dinky" db_username
  read -s -p "请输入数据库密码(默认 dinky): " -i "dinky" db_password
  echo # 新行，以便在输入密码后换行

  # 转换为小写并去除空格
  db_type=$(echo "$db_type" |  tr -d '[:space:]')
  db_host=$(echo "$db_host" | tr -d '[:space:]')
  db_port=$(echo "$db_port" | tr -d '[:space:]')
  db_name=$(echo "$db_name" |  tr -d '[:space:]')
  db_username=$(echo "$db_username"  | tr -d '[:space:]')
  db_password=$(echo "$db_password"  | tr -d '[:space:]')

  case $db_type in
    1)
      echo -e "${YELLOW}正在配置 MySQL 数据库相关信息...${RESET}"
      config_file="${DINKY_HOME}/config/application-mysql.yml"
      # 更新 application-mysql.yml 文件中的配置
      echo -e "${GREEN} 自动初始化脚本采用 export 环境变量的方式 支持数据源的环境变量加载, 配置文件为：${config_file} ${RESET}"
      echo "export DB_ACTIVE=mysql" >> /etc/profile
      echo "export MYSQL_ADDR=${db_host}:${db_port}" >> /etc/profile
      echo "export MYSQL_DATABASE=${db_name}" >> /etc/profile
      echo "export MYSQL_USERNAME=${db_username}" >> /etc/profile
      echo "export MYSQL_PASSWORD=${db_password}" >> /etc/profile
      source /etc/profile

      echo -e "${GREEN}MySQL 数据库相关信息配置完成。请确认以下配置是否正确：${RESET}"
      grep -E '^(export DB_ACTIVE|export MYSQL_ADDR|export MYSQL_DATABASE|export MYSQL_USERNAME|export MYSQL_PASSWORD)' /etc/profile | grep -v "^#" | grep -v "^$"
      break  # 退出循环
      ;;
    2)
      echo -e "${YELLOW}正在配置 PostgreSQL 数据库相关信息...${RESET}"
      config_file="${DINKY_HOME}/config/application-pgsql.yml"

      echo -e "${GREEN}自动初始化脚本采用 export 环境变量的方式 支持数据源配置文件的环境变量加载, 配置文件为：${config_file} ${RESET}"

       # 将以下变量追加方式写入到 /etc/profile 文件中
      echo "export DB_ACTIVE=pgsql" >> /etc/profile
      echo "export POSTGRES_ADDR=${db_host}:${db_port}" >> /etc/profile
      echo "export POSTGRES_DB=${db_name}" >> /etc/profile
      echo "export POSTGRES_USER=${db_username}" >> /etc/profile
      echo "export POSTGRES_PASSWORD=${db_password}" >> /etc/profile
      source /etc/profile

      echo -e "${GREEN}PostgreSQL 数据库相关信息配置完成。请确认以下配置是否正确：${RESET}"
      grep -E '^(export DB_ACTIVE|export POSTGRES_ADDR|export POSTGRES_DB|export POSTGRES_USER|export POSTGRES_PASSWORD)' /etc/profile | grep -v "^#" | grep -v "^$"
      break  # 退出循环
      ;;
    *)
      echo -e "${RED}输入的数据库类型不正确，请重新选择正确的数据库类型.${RESET}"
      ;;
  esac
done