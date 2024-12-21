from requests import Session
import urllib.parse as urlparse
from hdfs.client import Client
import os
from config import *
from httpUtil import assertRespOk, url
from logger import log


def addStandaloneCluster(session: Session) -> int:
    """
    en: Add a cluster instance
    zh: 添加一个集群实例
    :param session:  requests.Session
    :return:  clusterId
    """
    name = 'flink-standalone'
    add_cluster_resp = session.put(url("api/cluster"), json={
        "name": name,
        "type": "standalone",
        "hosts": standalone_address
    })
    assertRespOk(add_cluster_resp, "Add cluster")
    get_data_list = session.get(url(f"api/cluster/list?searchKeyWord={urlparse.quote(name)}&isAutoCreate=false"))
    assertRespOk(get_data_list, "Get cluster list")
    for data in get_data_list.json()['data']:
        if data['name'] == name:
            return data['id']
    raise Exception(f"Cluster {name} not found")


def addYarnCluster(session: Session) -> int:
    client = Client("http://namenode:9870")
    flink_lib_path = yarn_flink_lib
    client.makedirs(flink_lib_path)
    # Traverse the specified path and upload the file to HDFS
    for root, dirs, files in os.walk(flink_lib_path):
        for file in files:
            filepath = os.path.join(root, file)
            client.upload(flink_lib_path + "/" + file, filepath)
    jar_path = yarn_dinky_app_jar
    client.makedirs(jar_path)
    for root, dirs, files in os.walk(jar_path):
        for file in files:
            if file.endswith(".jar") and file.__contains__("dinky-app"):
                filepath = os.path.join(root, file)
                jar_path = filepath
                client.upload(jar_path, filepath)
    name = "yarn-test"
    params = {
        "type": "yarn-application",
        "name": name,
        "enabled": True,
        "config": {
            "clusterConfig": {
                "hadoopConfigPath": yarn_hadoop_conf,
                "flinkLibPath": "hdfs://" + flink_lib_path,
                "flinkConfigPath": yarn_flink_conf
            },
            "flinkConfig": {
                "configuration": {
                    "jobmanager.memory.process.size": "1024m",
                    "taskmanager.memory.process.size": "1024m",
                    "taskmanager.numberOfTaskSlots": "1",
                    "state.savepoints.dir": "hdfs:///flink/savepoint",
                    "state.checkpoints.dir": "hdfs:///flink/ckp"
                }
            },
            "appConfig": {
                "userJarPath": "hdfs://" + jar_path
            }
        }
    }
    log.info(f"Adding yarn application cluster, parameters:{params}")
    test_connection_yarn_resp = session.post(url("api/clusterConfiguration/testConnect"), json=params)
    assertRespOk(test_connection_yarn_resp, "Test yarn connectivity")
    test_connection_yarn_resp = session.put(url("api/clusterConfiguration/saveOrUpdate"), json=params)
    assertRespOk(test_connection_yarn_resp, "Add Yarn Application Cluster")
    get_app_list = session.get(url(f"api/clusterConfiguration/list?keyword={name}"), json=params)
    assertRespOk(get_app_list, "Get Yarn Application Cluster")
    for data in get_app_list.json()["data"]:
        if data["name"] == name:
            return data['id']
