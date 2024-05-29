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

import { AlertRuleListState, PluginMarketState } from '@/types/SettingCenter/state.d';

/**
 * alert group state init
 */
export const InitAlertRuleState: AlertRuleListState = {
  addedOpen: false,
  editOpen: false,
  value: {}
};

/**
 */

export const InitPluginMarketState: PluginMarketState = {
  addedOpen: false,
  editOpen: false,
  loading: false,
  installed: true,
  downloaded: true,
  pluginList: [
    {
      id: 0,
      createTime: new Date(),
      updateTime: new Date(),
      name: '111223',
      enabled: false,
      pluginId: 'centeral',
      pluginDownloadUrl: 'http://www.baidu.com',
      pluginLocalStorageFullPath: '/tmp/dubbo',
      organization: 'flink',
      repositoryId: 'flink',
      pluginReleaseTimestamp: new Date(),
      description: 'hahahah',
      groupId: 'org.apache.flink',
      artifactId: 'flink-connector-kafka_2.11',
      currentVersion: '3.0.0',
      versionCount: 1,
      installed: false,
      downloaded: false
    }
  ],
  value: {}
};
