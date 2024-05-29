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

import GeneralConfig from '@/pages/SettingCenter/GlobalSetting/SettingOverView/GeneralConfig';
import {BaseConfigProperties, GLOBAL_SETTING_KEYS} from '@/types/SettingCenter/data.d';
import { l } from '@/utils/intl';
import { Tag } from 'antd';
import React, {useEffect, useState} from 'react';

interface MavenConfigProps {
  data: BaseConfigProperties[];
  onSave: (data: BaseConfigProperties) => void;
  auth: string;
}

type MavenConfigState = {
  base: BaseConfigProperties[];
  pluginMarket: BaseConfigProperties[];
};

export const MavenConfig = ({ data, onSave, auth }: MavenConfigProps) => {
  const [loading, setLoading] = React.useState(false);

  const [enablePluginMarket, setEnablePluginMarket] = React.useState(false);
  const [filterData, setFilterData] = useState<MavenConfigState>({
    base: [],
    pluginMarket: [],
  });

  useEffect(() => {
    // 处理 data / 规则: 前缀为 sys.resource.settings.base 的为基础配置，其他的为 hdfs/oss 配置
    const base: BaseConfigProperties[] = data.filter((d) =>
      d.key.startsWith('sys.maven.settings')
    );
    const pluginMarket: BaseConfigProperties[] = data.filter((d) =>
      !d.key.startsWith('sys.maven.settings') && d.key.startsWith('sys.maven.settings.plugin')
    );
    setFilterData({ base, pluginMarket});
    // 获取是否开启插件市场
    const isEnablePluginMarket = base.find(
      (d) => d.key === GLOBAL_SETTING_KEYS.SYS_MAVEN_SETTINGS_PLUGIN_ENABLE_PLUGIN_MARKET
    )?.value;
    console.log('isEnablePluginMarket', isEnablePluginMarket);
    if (isEnablePluginMarket) {
      setEnablePluginMarket(isEnablePluginMarket === 'true');
    }
  }, [data]);

  const onSaveHandler = async (data: BaseConfigProperties) => {
    setLoading(true);
    await onSave(data);
    setLoading(false);
  };

  return (
    <>
      <GeneralConfig
        loading={loading}
        onSave={onSaveHandler}
        auth={auth}
        tag={
          <>
            <Tag color={'default'}>{l('sys.setting.tag.integration')}</Tag>
          </>
        }
        data={filterData.base}
      />
      {
        enablePluginMarket && (
          <GeneralConfig
            loading={loading}
            onSave={onSaveHandler}
            auth={auth}
            tag={
              <>
                <Tag color={'default'}>{l('sys.setting.tag.plugin_market')}</Tag>
              </>
            }
            data={filterData.pluginMarket}
          />
        )
      }
    </>
  );
};
