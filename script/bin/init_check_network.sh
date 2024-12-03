#!/bin/bash

EXTERNAL_CONNECTIVITY_CHECK_URL="www.baidu.com"

# 检查网络是否可以正常连接外网 通过 ping 和 curl 方式检测
echo -e "${YELLOW}正在检测你的网络是否可以正常链接外网 (${EXTERNAL_CONNECTIVITY_CHECK_URL}) ...${RESET}"
if ! ping -c 1 ${EXTERNAL_CONNECTIVITY_CHECK_URL} &> /dev/null; then
    echo -e "${RED}你的网络使用 ping 方式无法连接外网，请检查你的网络环境是否正常, 程序将再次尝试使用 curl 方式检测网络连接...${RESET}"
    if ! curl -I -s --connect-timeout 5 ${EXTERNAL_CONNECTIVITY_CHECK_URL} -w '%{http_code}' | tail -n1 | grep "200" &> /dev/null; then
        echo -e "${RED}你的网络使用 curl 方式也无法连接外网，请检查你的网络环境是否正常。${RESET}"
        echo -e "${YELLOW}请注意，在某些网络环境中，防火墙或安全策略可能会阻止ICMP请求（也就是ping）。如果遇到这种情况，可使用 curl 方式检测网络连接。${RESET}"
        exit 1
    else
        echo -e "${GREEN}你的网络使用 curl 方式可以连接外网，继续进行下一步操作...${RESET}"
    fi
else
    echo -e "${GREEN}你的网络使用 ping 方式可以连接外网，继续进行下一步操作...${RESET}"
fi