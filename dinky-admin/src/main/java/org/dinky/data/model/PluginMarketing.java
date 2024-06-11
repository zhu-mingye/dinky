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

package org.dinky.data.model;

import org.dinky.mybatis.model.SuperEntity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("dinky_plugin_marketing")
@ApiModel(value = "PluginMarketing", description = "PluginMarketing")
public class PluginMarketing extends SuperEntity<PluginMarketing> {

    // 插件id e.g: "org.apache.flink:flink-sql-connector-mongodb-cdc"
    private String pluginId;
    // 插件id e.g: "org.apache.flink:flink-sql-connector-mongodb-cdc"
    private String pluginDownloadUrl;
    // 本地完整存储路径 e.g.: "/Users/dinky/Downloads/flink-sql-connector-mongodb-cdc-1.0.jar"
    private String pluginLocalStorageFullPath;
    // 资源完整存储路径 e.g.: "rs:/plugin-marketing/flink-sql-connector-mongodb-cdc-1.0.jar"
    private String pluginResourceStorageFullPath;
    // 插件所属组织 e.g: "org.apache.flink"
    private String organization;
    // 插件所属仓库  e.g: "central"
    private String repositoryId;
    // 插件版本
    private LocalDateTime pluginReleaseTimestamp;
    // 插件描述
    private String description;
    // 组
    private String groupId;
    // 插件名称
    private String artifactId;
    // 插件版本号
    private String currentVersion;
    // versionCount
    private Integer versionCount;
    // 是否已经安装 0:未安装 1:已安装
    private Boolean installed;
    // 是否已经安装 0:未下载 1:已下载
    private Boolean downloaded;
}
