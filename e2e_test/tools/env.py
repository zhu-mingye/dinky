from requests import Session
import urllib.parse as urlparse

from login import url, assertRespOk


def addCluster(session: Session) -> int:
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
        "hosts": "jobmanager:8282"
    })
    assertRespOk(add_cluster_resp, "Add cluster")
    get_data_list = session.get(url(f"api/cluster/list?searchKeyWord={urlparse.quote(name)}&isAutoCreate=false"))
    assertRespOk(get_data_list, "Get cluster list")
    for data in get_data_list.json()['data']:
        if data['name'] == name:
            return data['id']
    raise Exception(f"Cluster {name} not found")
