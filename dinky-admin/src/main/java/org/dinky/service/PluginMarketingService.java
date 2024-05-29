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

package org.dinky.service;

import java.util.List;
import org.dinky.data.model.PluginMarketing;
import org.dinky.mybatis.service.ISuperService;

/** PluginMarketingService */
public interface PluginMarketingService extends ISuperService<PluginMarketing> {

    /**
     * 同步插件市场列表
     */
    boolean syncPluginMarketData();

    /**
     * 安装插件
     * @param id 插件id
     * @return  true 安装成功 false 安装失败
     */
    boolean uninstallPlugin(Integer id);

    /**
     * 卸载插件
     * @param id 插件id
     * @return true 卸载成功 false 卸载失败
     */
    boolean deletePlugin(Integer id);

    /**
     * 下载插件
     * @param id 插件id
     * @return 下载成功 true 下载失败 false
     */
    boolean downloadedPlugin(Integer id);

    /**
     * 安装插件
     * @param id 插件id
     * @return 安装成功 true 安装失败 false
     */
    boolean installPlugin(Integer id);


    /**
     * 下载并加载依赖
     * @param groupId
     * @param artifactId
     * @param version
     * @param localRepoPath
     */
//    boolean downloadAndLoadDependency(PluginMarketing pluginMarketing);

    /**
     * 加载jar到本地classpath
     * @param id 插件id
     */
//    void loadJarToLocalClasspath(Integer id);



}
