from time import sleep

import requests

from login import assertRespOk, url
from logger import log


class CatalogueTree:
    def __init__(self, id: int, name: str, taskId: int, children):
        self.id = id
        self.name = name
        self.taskId = taskId
        self.children: list[CatalogueTree] = children


def assertFlinkTaskIsRunning(status: str, name: str):
    # todo 这里应该判断flink是否有抛出异常，而不是只有状态
    if status != "RUNNING":
        raise Exception(f"Flink name:{name} is not RUNNING,current status:{status}")


def getTask(data_list: list[dict], name: str) -> CatalogueTree:
    for data in data_list:
        if data['name'] == name:
            return CatalogueTree(data['id'], data['name'], data['taskId'], data['children'])
        if len(data["children"]) > 0:
            result = getTask(data["children"], name)
            if result is not None:
                return result


def addCatalogue(session: requests.Session, name: str, isLeaf: bool = False, parentId: int = 0):
    add_parent_dir_resp = session.put(url("api/catalogue/saveOrUpdateCatalogue"), json={
        "name": name,
        "isLeaf": isLeaf,
        "parentId": parentId
    })
    assertRespOk(add_parent_dir_resp, "Create a dir")
    get_all_tasks_resp = session.post(url("api/catalogue/getCatalogueTreeData"), json={
        "sortValue": "",
        "sortType": ""
    })
    assertRespOk(get_all_tasks_resp, "Get job details")
    data_list: list[dict] = get_all_tasks_resp.json()['data']
    return getTask(data_list, name)


def addTask(session: requests.Session, name: str, parent_id: int = 0, dialect: str = "FlinkSql",
            statement: str = "", runtModel: str = "local", clusterId: int = -1) -> CatalogueTree:
    """
    en: Add a task
    zh: 添加一个任务
    :param session: requests.Session
    :param name:  task name
    :param parent_id:  dir id
    :param type:  task type
    :param statement:  statement
    :return CatalogueTree
    """
    add_parent_dir_resp = session.put(url("api/catalogue/saveOrUpdateCatalogueAndTask"), json={
        "name": name,
        "type": dialect,
        "firstLevelOwner": 1,
        "task": {
            "savePointStrategy": 0,
            "parallelism": 1,
            "envId": -1,
            "step": 1,
            "alertGroupId": -1,
            "type": runtModel,
            "dialect": dialect,
            "statement": statement,
            "firstLevelOwner": 1,
            "clusterId":clusterId
        },
        "isLeaf": False,
        "parentId": parent_id
    })
    assertRespOk(add_parent_dir_resp, "Create a task")
    get_all_tasks_resp = session.post(url("api/catalogue/getCatalogueTreeData"), json={
        "sortValue": "",
        "sortType": ""
    })
    assertRespOk(get_all_tasks_resp, "Get job details")
    data_list: list[dict] = get_all_tasks_resp.json()['data']
    return getTask(data_list, name)


def runTask(session: requests.Session, taskId: int) -> int:
    """
    en:Run a task
    zh:运行一个任务
    :param session: requests.Session
    :param taskId: task id
    :return:
    """
    run_task_resp = session.get(url(f"api/task/submitTask?id={taskId}"))
    assertRespOk(run_task_resp, "Run Task")
    return run_task_resp.json()['data']['jobInstanceId']


def getFlinkTaskStatus(session: requests.Session, jobInstanceId: int) -> str:
    """
    en:  Obtain the status of a Flink task
    zh:  获取Flink 任务状态
    :param session:  requests.Session
    :param jobInstanceId: job instance id
    :return: status
    """
    run_task_resp = session.get(url(f"api/jobInstance/refreshJobInfoDetail?id={jobInstanceId}&isForce=false"))
    assertRespOk(run_task_resp, "Get Task Status")
    return run_task_resp.json()['data']['instance']['status']


def runFlinkLocalTask(session: requests.Session, parentId: int, name: str, statement: str, waitTime: int = 10) -> None:
    """
    en: Run a FlinkLocal task
    zh: 运行一个 FlinkLocal任务
    :param session:  requests.Session
    :param parentId:  dir id
    :param name:  task name
    :param statement:  statement
    :param waitTime:  zh:等待时间
    """
    log.info(
        f"======================\nA Local Flink task is currently executed，name: {name}, statement: \n{statement}\n ======================")
    task = addTask(session, name, parentId, "FlinkSql", statement)
    jobInstanceId = runTask(session, task.taskId)
    sleep(waitTime)
    status = getFlinkTaskStatus(session, jobInstanceId)
    assertFlinkTaskIsRunning(status, name)


def runFlinkSessionTask(session: requests.Session, parentId: int,clusterId:int, name: str, statement: str,
                        waitTime: int = 10) -> None:
    """
    en: Run a FlinkLocal task
    zh: 运行一个 FlinkLocal任务
    :param session:  requests.Session
    :param parentId:  dir id
    :param name:  task name
    :param statement:  statement
    :param waitTime:  zh:等待时间
    """
    log.info(
        f"======================\nA Session Flink task is currently executed，name: {name}, statement: \n{statement}\n ======================")
    task = addTask(session, name, parentId, "FlinkSql", statement,"standalone",clusterId)
    jobInstanceId = runTask(session, task.taskId)
    sleep(waitTime)
    status = getFlinkTaskStatus(session, jobInstanceId)
    assertFlinkTaskIsRunning(status, name)
