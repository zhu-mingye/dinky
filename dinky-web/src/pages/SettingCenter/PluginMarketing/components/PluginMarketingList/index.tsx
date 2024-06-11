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

import { Authorized } from '@/hooks/useAccess';
import { queryList } from '@/services/api';
import {
  handleGetOption,
  handleOption,
  handlePutDataByParams,
  handleRemoveById
} from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { PermissionConstants } from '@/types/Public/constants';
import { l } from '@/utils/intl';
import ProTable, { ActionType, ProColumns } from '@ant-design/pro-table';
import React, { useEffect, useRef, useState } from 'react';
import { PluginMarketInfo } from '@/types/SettingCenter/data.d';
import { PluginMarketState } from '@/types/SettingCenter/state.d';
import { InitPluginMarketState } from '@/types/SettingCenter/init.d';
import { Button, Modal, Result, Tag } from 'antd';
import {
  CloudDownloadOutlined,
  DeploymentUnitOutlined,
  DisconnectOutlined,
  WarningOutlined
} from '@ant-design/icons';
import { NormalDeleteBtn } from '@/components/CallBackButton/NormalDeleteBtn';
import { CONFIG_MODEL_ASYNC, SysConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { SettingConfigKeyEnum } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import { connect, history } from '@@/exports';
import { ProCard } from '@ant-design/pro-components';

const PluginMarketingList: React.FC<connect> = (props) => {
  const { dispatch, enablePluginMarket } = props;

  const actionRef = useRef<ActionType>(); // table action
  const [pluginState, setPluginState] = useState<PluginMarketState>(InitPluginMarketState);

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setPluginState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    setPluginState((prevState) => ({ ...prevState, loading: false }));
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
    await executeAndCallbackRefresh(async () =>
      handlePutDataByParams(API_CONSTANTS.PLUGIN_MARKET_INSTALL, l('button.install'), {
        id: item.id
      })
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
      width: '9%',
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
      title: l('sys.plugin.market.organization'),
      width: '9%',
      dataIndex: 'organization'
    },
    {
      title: l('sys.plugin.market.repositoryId'),
      width: '9%',
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
      title: l('sys.plugin.market.pluginInfo'),
      width: '12%',
      render: (_, item) => {
        return (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
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
        true: { text: l('sys.plugin.market.installed') },
        false: { text: l('sys.plugin.market.uninstalled') }
      }
    },
    {
      title: l('sys.plugin.market.download.info'),
      dataIndex: 'downloaded',
      hideInTable: true,
      valueEnum: {
        true: { text: l('sys.plugin.market.downloaded') },
        false: { text: l('sys.plugin.market.notDownloaded') }
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
        <Authorized
          key={`${item.id}_auth_download`}
          path={PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_DOWNLOAD}
        >
          <Button
            className={'options-button'}
            key={`${item.id}_download`}
            onClick={() => handleDownload(item)}
            title={l('button.download')}
            icon={<CloudDownloadOutlined />}
          />
        </Authorized>,
        <Authorized
          key={`${item.id}_auth_delete`}
          path={PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_DELETE}
        >
          <NormalDeleteBtn
            disabled={item.installed || !item.downloaded}
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
            icon={<DeploymentUnitOutlined />}
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
            icon={<DisconnectOutlined />}
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
        <ProCard ghost size={'small'} bodyStyle={{ height: parent.innerHeight - 80 }}>
          <Result
            status='warning'
            style={{ alignItems: 'center', justifyContent: 'center' }}
            icon={<WarningOutlined />}
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

export default connect(({ SysConfig }: { SysConfig: SysConfigStateType }) => ({
  enablePluginMarket: SysConfig.enablePluginMarket
}))(PluginMarketingList);
