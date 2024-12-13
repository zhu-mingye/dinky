import sys
from json import JSONDecodeError

import requests
from requests import Response
from logger import log

# # 创建一个logger对象
# log = logging.getLogger(__name__)
# # 创建一个控制台处理器
# console_handler = logging.StreamHandler()
# # 将控制台处理器添加到logger对象中
# log.addHandler(console_handler)
#
# # 设置控制台处理器的输出格式
# formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
# console_handler.setFormatter(formatter)
dinky_addr = sys.argv[1]

log.info(f"The address of the current request:{dinky_addr}")


def url(path: str):
    return rf"http://{dinky_addr}/{path}"


def assertRespOk(resp: Response, api_name: str):
    if resp.status_code != 200:
        raise AssertionError("api name:{api_name} request failed")
    else:
        try:
            resp_json = resp.json()
            if not resp_json["success"]:
                raise AssertionError(f"api name:{api_name} request failed.Error: {resp_json['msg']}")
        except JSONDecodeError as e:
            raise AssertionError(f"api name:{api_name} request failed.Error: {resp.content.decode()}")


def login(session: requests.Session):
    log.info("Login to Dinky, Currently in use: admin")
    login_resp: Response = session.post(url("api/login"),
                                        json={"username": "admin", "password": "dinky123!@#", "ldapLogin": False,
                                              "autoLogin": True})
    assertRespOk(login_resp, "Login")

    log.info("Select the default tenant")
    choose_tenant_resp = session.post(url("api/chooseTenant?tenantId=1"))
    assertRespOk(choose_tenant_resp, "Choose Tenant")
    session.cookies.set("tenantId", '1')
