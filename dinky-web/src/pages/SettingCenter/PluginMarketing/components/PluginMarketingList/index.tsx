/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import {Authorized} from '@/hooks/useAccess';
import {queryList} from '@/services/api';
import {handleGetOption, handleOption, handleRemoveById} from '@/services/BusinessCrud';
import {PROTABLE_OPTIONS_PUBLIC} from '@/services/constants';
import {API_CONSTANTS} from '@/services/endpoints';
import {PermissionConstants} from '@/types/Public/constants';
import {l} from '@/utils/intl';
import ProTable, {ActionType, ProColumns} from '@ant-design/pro-table';
import React, {useEffect, useRef, useState} from 'react';
import {PluginMarketInfo} from '@/types/SettingCenter/data.d';
import {PluginMarketState} from '@/types/SettingCenter/state.d';
import {InitPluginMarketState} from '@/types/SettingCenter/init.d';
import {Button, Modal, Result, Tag} from 'antd';
import {
  CloudDownloadOutlined,
  DeploymentUnitOutlined,
  DisconnectOutlined,
  EyeOutlined,
  WarningOutlined
} from '@ant-design/icons';
import {NormalDeleteBtn} from '@/components/CallBackButton/NormalDeleteBtn';
import {CONFIG_MODEL_ASYNC, SysConfigStateType} from '@/pages/SettingCenter/GlobalSetting/model';
import {SettingConfigKeyEnum} from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import {connect, history} from '@@/exports';
import {ProCard, ProForm, ProFormSelect} from '@ant-design/pro-components';

const PluginMarketingList: React.FC<connect> = (props) => {
  const {dispatch, enablePluginMarket} = props;

  const actionRef = useRef<ActionType>(); // table action
  const [pluginState, setPluginState] = useState<PluginMarketState>(InitPluginMarketState);

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setPluginState((prevState) => ({...prevState, loading: true}));
    await callback();
    setPluginState((prevState) => ({...prevState, loading: false}));
    actionRef.current?.reload?.();
  };

  useEffect(() => {
    dispatch({
      type: CONFIG_MODEL_ASYNC.queryEnablePluginMarket,
      payload: SettingConfigKeyEnum.MAVEN.toLowerCase()
    });
  }, [actionRef]);

  /**
   * handle delete
   * @param id
   */
  const handleDelete = async (id: number) => {
    Modal.confirm({
      title: l('sys.plugin.market.delete'),
      content: l('sys.plugin.market.deleteConfirm'),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () =>
        executeAndCallbackRefresh(async () =>
          handleRemoveById(API_CONSTANTS.PLUGIN_MARKET_DELETE, id)
        )
    });
  };

  /**
   * handle delete
   * @param id
   */
  const handleUnInstall = async (id: number) => {
    Modal.confirm({
      title: l('sys.plugin.market.uninstall'),
      content: l('sys.plugin.market.uninstallConfirm'),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () =>
        executeAndCallbackRefresh(async () =>
          handleRemoveById(API_CONSTANTS.PLUGIN_MARKET_UNINSTALL, id)
        )
    });
  };

  /**
   * handle check heart
   * @param item
   */
  const handleInstall = async (item: PluginMarketInfo) => {
    await executeAndCallbackRefresh(async () => {
        await handleShowAllVersion(item)
        // 如果版本列表只有一个，则直接安装
        if (pluginState.versionList.length === 1) {
          await handleOption(API_CONSTANTS.PLUGIN_MARKET_INSTALL, l('button.install'), item)
        } else {
          // 弹出选择版本弹窗
          setPluginState((prevState) => ({...prevState, chooseVersion: true}));
          Modal.confirm({
            title: l('sys.plugin.market.install'),
            content:
              <>
                <p>检测到该插件有[{pluginState.versionList.length}]个版本，请选择需要安装的版本,注意: 每个插件只可以安装一个版本,其余版本将自动卸载</p>
                <ProForm submitter={false} layout={'horizontal'}>
                  <ProFormSelect
                    label={'选择版本'}
                    name={'version'}
                    rules={[{required: true, message: '请选择版本'}]}
                    style={{width: '100%'}}
                    options={pluginState.versionList.map((item) => ({value: item, label: item, key: item}))}
                    fieldProps={{
                      defaultActiveFirstOption: true,
                      onChange: (value: string) => setPluginState((prevState) => ({
                        ...prevState,
                        chooseVersionValue: value
                      }))
                    }}
                  />
                </ProForm>
              </>,
            width: '30vw',
            okButtonProps: {
              disabled: pluginState.chooseVersionValue === '' || pluginState.chooseVersionValue === undefined,
            },
            okText: l('button.confirm'),
            cancelText: l('button.cancel'),
            onOk: async () => {
                // 如果选择了版本，则安装 指定版本
              item.currentVersion = pluginState.chooseVersionValue;
              await handleOption(API_CONSTANTS.PLUGIN_MARKET_INSTALL, l('button.install'), item)
            }
          });
        }
        //await handleOption(API_CONSTANTS.PLUGIN_MARKET_INSTALL, l('button.install'), item)
      }
    );
  };

  const handleSync = async () => {
    await executeAndCallbackRefresh(async () =>
      handleGetOption(API_CONSTANTS.PLUGIN_MARKET_SYNC, l('button.sync'), {})
    );
  };

  const handleDownload = async (item: PluginMarketInfo) => {
    await executeAndCallbackRefresh(async () =>
      handleOption(API_CONSTANTS.PLUGIN_MARKET_DOWNLOAD, l('button.download'), item)
    );
  };

  const handleShowAllVersion = async (item: PluginMarketInfo) => {
    await executeAndCallbackRefresh(async () => {
        await handleGetOption(API_CONSTANTS.PLUGIN_MARKET_QUERY_ALL_VERSION, l('button.search'), {id: item.id}).then((res) => {
          setPluginState((prevState) => ({...prevState, versionList: res.data}));
        });
        setPluginState((prevState) => ({...prevState, chooseVersion: true}));
      }
    );
  }

  /**
   * table columns
   */
  const columns: ProColumns<PluginMarketInfo>[] = [
    {
      title: l('sys.plugin.market.name'),
      dataIndex: 'name',
      width: '9%',
      copyable: true,
      ellipsis: true
    },
    {
      title: l('sys.plugin.market.pluginId'),
      width: '8%',
      dataIndex: 'pluginId'
    },
    {
      title: l('sys.plugin.market.downloadUrl'),
      width: '9%',
      dataIndex: 'pluginDownloadUrl',
      copyable: true,
      ellipsis: true
    },
    {
      title: l('sys.plugin.market.localStorageFullPath'),
      width: '9%',
      dataIndex: 'pluginLocalStorageFullPath',
      copyable: true,
      ellipsis: true
    },
    {
      title: l('sys.plugin.market.resourceStorageFullPath'),
      width: '9%',
      render: (_, item) => {
        return item.pluginResourceStorageFullPath ? 'rs:/'+ item.pluginResourceStorageFullPath : '-';
      },
      copyable: true,
      ellipsis: true
    },
    {
      title: l('sys.plugin.market.organization'),
      width: '9%',
      dataIndex: 'organization'
    },
    {
      title: l('sys.plugin.market.repositoryId'),
      width: '5%',
      dataIndex: 'repositoryId'
    },
    {
      title: l('sys.plugin.market.pluginReleaseTimestamp'),
      width: '9%',
      dataIndex: 'pluginReleaseTimestamp',
      valueType: 'dateTime'
    },
    {
      title: l('sys.plugin.market.description'),
      width: '9%',
      dataIndex: 'description'
    },
    {
      title: l('sys.plugin.market.versionCount'),
      width: '4%',
      dataIndex: 'versionCount'
    },
    {
      title: l('sys.plugin.market.pluginInfo'),
      width: '12%',
      hideInSearch: true,
      render: (_, item) => {
        return (
          <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center'}}>
            <Tag>{item.groupId}</Tag>
            <Tag>{item.artifactId}</Tag>
            <Tag>{item.currentVersion}</Tag>
          </div>
        );
      }
    },
    {
      dataIndex: 'groupId',
      hideInTable: true,
      hideInSearch: true
    },
    {
      dataIndex: 'artifactId',
      hideInTable: true,
      hideInSearch: true
    },
    {
      dataIndex: 'currentVersion',
      hideInTable: true,
      hideInSearch: true
    },
    {
      title: l('sys.plugin.market.install.download.info'),
      width: '8%',
      hideInSearch: true,
      render: (_, item) => {
        return (
          <div>
            {item.downloaded ? (
              <Tag color={'green'}>{l('sys.plugin.market.downloaded')}</Tag>
            ) : (
              <Tag color={'red'}>{l('sys.plugin.market.notDownloaded')}</Tag>
            )}
            {item.installed ? (
              <Tag color={'green'}>{l('sys.plugin.market.installed')}</Tag>
            ) : (
              <Tag color={'red'}>{l('sys.plugin.market.uninstalled')}</Tag>
            )}
          </div>
        );
      }
    },
    {
      title: l('sys.plugin.market.install.info'),
      dataIndex: 'installed',
      hideInTable: true,
      valueEnum: {
        true: {text: l('sys.plugin.market.installed')},
        false: {text: l('sys.plugin.market.uninstalled')}
      }
    },
    {
      title: l('sys.plugin.market.download.info'),
      dataIndex: 'downloaded',
      hideInTable: true,
      valueEnum: {
        true: {text: l('sys.plugin.market.downloaded')},
        false: {text: l('sys.plugin.market.notDownloaded')}
      }
    },
    {
      title: l('global.table.createTime'),
      width: '9%',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInTable: true,
      hideInSearch: true
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: '8%',
      fixed: 'right',
      render: (_, item: PluginMarketInfo) => [
        <Button
          className={'options-button'}
          disabled={item.downloaded}
          key={`${item.id}_download`}
          onClick={() => handleShowAllVersion(item)}
          title={l('button.download')}
          icon={<EyeOutlined/>}
        />,
        <Authorized
          key={`${item.id}_auth_download`}
          path={PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_DOWNLOAD}
        >
          <Button
            className={'options-button'}
            disabled={item.downloaded}
            key={`${item.id}_download`}
            onClick={() => handleDownload(item)}
            title={l('button.download')}
            icon={<CloudDownloadOutlined/>}
          />
        </Authorized>,
        <Authorized
          key={`${item.id}_auth_delete`}
          path={PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_DELETE}
        >
          <NormalDeleteBtn
            disabled={item.installed}
            key={`${item.id}_delete`}
            onClick={() => handleDelete(item.id)}
          />
        </Authorized>,
        <Authorized
          key={`${item.id}_auth_install`}
          path={PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_INSTALL}
        >
          <Button
            className={'options-button'}
            key={`${item.id}_install`}
            disabled={item.installed || !item.downloaded}
            onClick={() => handleInstall(item)}
            title={l('button.install')}
            icon={<DeploymentUnitOutlined/>}
          />
        </Authorized>,
        <Authorized
          key={`${item.id}_auth_uninstall`}
          path={PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_UNINSTALL}
        >
          <Button
            className={'options-button'}
            key={`${item.id}_uninstall`}
            disabled={!item.installed}
            onClick={() => handleUnInstall(item.id)}
            title={l('button.uninstall')}
            icon={<DisconnectOutlined/>}
          />
        </Authorized>
      ]
    }
  ];

  /**
   * render
   */
  return (
    <>
      {!enablePluginMarket ? (
        <ProCard ghost size={'small'} bodyStyle={{height: parent.innerHeight - 80}}>
          <Result
            status='warning'
            style={{alignItems: 'center', justifyContent: 'center'}}
            icon={<WarningOutlined/>}
            title={l('rc.resource.enable')}
            subTitle={l('sys.plugin.market.enable.tips')}
            extra={
              <Button
                onClick={() => {
                  history.push('/settings/globalsetting');
                }}
                type='primary'
                key='globalsetting-to-jump'
              >
                {l('menu.settings')}
              </Button>
            }
          />
        </ProCard>
      ) : (
        <ProTable<PluginMarketInfo>
          {...PROTABLE_OPTIONS_PUBLIC}
          headerTitle={l('sys.plugin.market.title')}
          actionRef={actionRef}
          loading={pluginState.loading}
          toolBarRender={() => [
            <Authorized key='sync' path={PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_SYNC}>
              <Button type='primary' key={'syncbtn'} onClick={() => handleSync()}>
                {l('button.sync')}
              </Button>
            </Authorized>
          ]}
          request={(params, sorter, filter: any) =>
            queryList(API_CONSTANTS.PLUGIN_MARKET_LIST, {
              ...params,
              sorter,
              filter
            })
          }
          columns={columns}
        />
      )}
    </>
  );
};

export default connect(({SysConfig}: { SysConfig: SysConfigStateType }) => ({
  enablePluginMarket: SysConfig.enablePluginMarket
}))(PluginMarketingList);
