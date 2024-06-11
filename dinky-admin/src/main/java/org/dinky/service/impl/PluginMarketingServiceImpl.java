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

package org.dinky.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.PluginMarketing;
import org.dinky.data.model.Resources;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.maven.MavenDoc;
import org.dinky.data.model.maven.MavenResponseBody;
import org.dinky.data.model.maven.MavenResponseHeader;
import org.dinky.data.model.maven.MavenResult;
import org.dinky.mapper.PluginMarketingMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.resource.BaseResourceManager;
import org.dinky.service.PluginMarketingService;
import org.dinky.service.resource.ResourcesService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PluginMarketingServiceImpl extends SuperServiceImpl<PluginMarketingMapper, PluginMarketing>
        implements PluginMarketingService {

    private final String LOCAL_REPO_PATH =
            System.getProperty("user.dir") + FileUtil.FILE_SEPARATOR + "customJar" + FileUtil.FILE_SEPARATOR;

    private final SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();

    private final ResourcesService   resourcesService;


    /**
     * 同步插件市场列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncPluginMarketData() {
        //https://search.maven.org/solrsearch/select?q=g:org.apache.flink&core=gav&rows=200&wt=json
        Integer searchCount = systemConfiguration.getPluginMarketSearchKeyWordOfNumbers().getValue();
        String pluginMarketUrl = systemConfiguration.getPluginMarketSearchUrl().getValue();

        String returnFl = "id,g,a,v,p,ec,timestamp,repositoryId,versionCount,text,latestVersion,description,downloadUrl";
        // 由于单次 只能返回最高 200 条数据，所以需要分页查询,
        List<PluginMarketing> pluginMarketingListOfRemoteRepo = new ArrayList<>();
        if (searchCount > 200) {
            for (int i = 0; i < searchCount / 200 + 1; i++) {
                int start = i * 200;
                String pluginMarketSearchUrl = String.format("%s?q=g:%s+AND+p:jar&start=%d&rows=%d&wt=json&fl=%s", pluginMarketUrl, systemConfiguration.getPluginMarketSearchKeyWord().getValue(), start, 200 ,returnFl);
                pluginMarketingListOfRemoteRepo.addAll(getPluginMarketingsList(pluginMarketSearchUrl));
            }
        }else {
            String pluginMarketSearchUrl = String.format("%s?q=g:%s+AND+p:jar&start==%d&rows=%d&wt=json&fl=%s", pluginMarketUrl, systemConfiguration.getPluginMarketSearchKeyWord().getValue(), 0, searchCount,returnFl);
            pluginMarketingListOfRemoteRepo = getPluginMarketingsList(pluginMarketSearchUrl);
        }

        //  2.查询现有的插件列表
        List<PluginMarketing> pluginMarketingListInDataBase = this.list();
        AtomicLong newInsertCount = new AtomicLong(0);
        AtomicLong continueCount = new AtomicLong(0);

        List<PluginMarketing> needInsertPluginMarketingList = new ArrayList<>();

        // 3.通过唯一索引对比(`name`,`plugin_id`,`repository_id`,`group_id`,`artifact_id`,`current_version`)，如果存在则更新，如果不存在则新增
        for (PluginMarketing pluginMarketing : pluginMarketingListOfRemoteRepo) {
            // 3.1.通过唯一索引对比(`name`,`plugin_id`,`repository_id`,`group_id`,`artifact_id`,`current_version`)，如果存在不操作，如果不存在则新增

            boolean exist = pluginMarketingListInDataBase.stream()
                    .anyMatch(plugin -> {
                        boolean matchResult = plugin.getName().equals(pluginMarketing.getName())
                                && plugin.getPluginId().equals(pluginMarketing.getPluginId())
                                && plugin.getRepositoryId().equals(pluginMarketing.getRepositoryId())
                                && plugin.getGroupId().equals(pluginMarketing.getGroupId())
                                && plugin.getArtifactId().equals(pluginMarketing.getArtifactId())
                                && plugin.getCurrentVersion().equals(pluginMarketing.getCurrentVersion());
                        if (matchResult) {
                            log.debug(
                                    "The plugin {} has not been synchronized and the data does not exist. Please add a new one",
                                    plugin.getName());
                            newInsertCount.getAndIncrement();
                        } else {
                            log.debug(
                                    "The plugin {} has been synchronized and the data already exists. Skipping",
                                    plugin.getName());
                            continueCount.getAndIncrement();
                        }
                        return matchResult;
                    });

            if (!exist) {
                needInsertPluginMarketingList.add(pluginMarketing);
            }
        }
        boolean saveBatch = this.saveBatch(needInsertPluginMarketingList);
        if (saveBatch && !needInsertPluginMarketingList.isEmpty()) {
            log.info(
                    "The plugin has been synchronized successfully, the number of new plugins is {}, the number of existing plugins is {}",
                    newInsertCount.get(),
                    continueCount.get());
        } else {
            log.error("The plugin has been synchronized failed ,");
        }
        return saveBatch;
    }

    private @NotNull List<PluginMarketing> getPluginMarketingsList(String pluginMarketSearchUrl) {
        log.info("The plugin market full search url is {}", pluginMarketSearchUrl);
        HttpRequest httpRequest = HttpUtil.createGet(pluginMarketSearchUrl);
        if (StrUtil.isNotEmpty(systemConfiguration.getMavenRepositoryUser())
                && StrUtil.isNotEmpty(systemConfiguration.getMavenRepositoryPassword())) {
            httpRequest.basicAuth(
                    systemConfiguration.getMavenRepositoryUser(), systemConfiguration.getMavenRepositoryPassword());
        }
        HttpResponse httpResponse = httpRequest.execute();
        // 创建插件列表集合
        List<PluginMarketing> pluginMarketingListOfRemoteRepo = new ArrayList<>();
        if (httpResponse.isOk()) {
            String pluginMarketList = httpResponse.body();
            MavenResult mavenResult = JSONUtil.toBean(pluginMarketList, MavenResult.class);
            MavenResponseHeader responseHeader = mavenResult.getResponseHeader();
            log.info("The plugin market response header is {}, you can see the plugin market list", responseHeader);

            MavenResponseBody mavenResultResponse = mavenResult.getResponse();
            log.info("获取到了插件列表，数量为{}", mavenResultResponse.getNumFound());
            // 转换插件列表为实体列表放入 list
            List<MavenDoc> mavenResultResponseDocs = mavenResultResponse.getDocs();
            for (MavenDoc mavenDoc : mavenResultResponseDocs) {
                //过滤 packaging 只为 jar 的插件
                if (!"jar".equals(mavenDoc.getP())) {
                    continue;
                }
                pluginMarketingListOfRemoteRepo.add(getPluginMarketing(mavenDoc));
            }
        }
        return pluginMarketingListOfRemoteRepo;
    }

    private static @NotNull PluginMarketing getPluginMarketing(MavenDoc mavenDoc) {
        PluginMarketing pluginMarketing = new PluginMarketing();
        pluginMarketing.setName(mavenDoc.getId());
        pluginMarketing.setPluginId(mavenDoc.getId());

        String mavenDocLatestVersion = Opt.ofBlankAble(mavenDoc.getLatestVersion()).orElse(mavenDoc.getV());
        String pluginDownloadUrl = "https://repo.maven.apache.org/maven2/" + mavenDoc.getG().replace(".", "/") + "/" + mavenDoc.getA() + "/" + mavenDocLatestVersion + "/" + mavenDoc.getA() + "-" + mavenDocLatestVersion + ".jar";
        pluginMarketing.setPluginDownloadUrl(pluginDownloadUrl);
        pluginMarketing.setOrganization(mavenDoc.getG());
        pluginMarketing.setRepositoryId(mavenDoc.getRepositoryId());
        pluginMarketing.setPluginReleaseTimestamp(DateUtil.toLocalDateTime(new Date(mavenDoc.getTimestamp())));
        pluginMarketing.setPluginId(mavenDoc.getId());
        pluginMarketing.setGroupId(mavenDoc.getG());
        pluginMarketing.setCurrentVersion(mavenDocLatestVersion);
        pluginMarketing.setArtifactId(mavenDoc.getA());
        pluginMarketing.setVersionCount(mavenDoc.getVersionCount());
        pluginMarketing.setInstalled(false);
        pluginMarketing.setDownloaded(false);
        pluginMarketing.setEnabled(true);
        return pluginMarketing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uninstallPlugin(Integer id) {
        PluginMarketing plugin = this.baseMapper.selectById(id);
        // todo: 1.先从 classloader 中卸载

        // 卸载成功后，标记为未安装
        plugin.setInstalled(false);
        return save(plugin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePlugin(Integer id) {
        PluginMarketing plugin = getById(id);
        if (plugin.getInstalled()) {
            throw new BusException("Plugin " + plugin.getName() + " is installed, please uninstall it first.");
        }
        // todo: 1. 先从 classloader 中卸载
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();


        if (plugin.getInstalled()) {
            boolean deleted = FileUtil.del(plugin.getPluginLocalStorageFullPath());
            if (!deleted) {
                throw new BusException("Plugin " + plugin.getName() + " delete failed");
            }
        } else {
            log.info("Plugin {} is not installed, skip this deletion operation", plugin.getName());
        }

        String fullJarPath = plugin.getGroupId() +  FileUtil.FILE_SEPARATOR  + plugin.getArtifactId() + FileUtil.FILE_SEPARATOR  + plugin.getCurrentVersion() + FileUtil.FILE_SEPARATOR  + plugin.getArtifactId() + "-" + plugin.getCurrentVersion() + ".jar";

        String resourcePath = "plugin-marketing" + FileUtil.FILE_SEPARATOR + fullJarPath;
        // 从 resource manager 中卸载
        BaseResourceManager resourceManager = BaseResourceManager.getInstance();
        resourceManager.remove(resourcePath);

        resourcesService.getBaseMapper().delete(new LambdaQueryWrapper<Resources>().eq(Resources::getFullName, "/"+ resourcePath));

        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean downloadedPlugin(PluginMarketing plugin) {
        if (plugin.getDownloaded()) {
            throw new BusException("Plugin " + plugin.getName() + " is already downloaded.Skip this download");
        }
        HttpRequest httpRequest = HttpUtil.createGet(plugin.getPluginDownloadUrl());
        if (StrUtil.isNotEmpty(systemConfiguration.getMavenRepositoryUser())
                && StrUtil.isNotEmpty(systemConfiguration.getMavenRepositoryPassword())) {
            httpRequest.basicAuth(
                    systemConfiguration.getMavenRepositoryUser(), systemConfiguration.getMavenRepositoryPassword());
        }
        StreamProgress streamProgress = new StreamProgress() {
            /**
             * 开始
             */
            @Override
            public void start() {
                log.info("Plugin {} download start", plugin.getName());
            }

            @Override
            public void progress(long total, long progressSize) {
                log.info("Plugin {} download progress: {}%", plugin.getName(), (progressSize * 100 / total));
            }

            /**
             * 结束
             */
            @Override
            public void finish() {
                log.info("Plugin {} download finish", plugin.getName());
            }
        };

        String fullJarPath = plugin.getGroupId() +  FileUtil.FILE_SEPARATOR  + plugin.getArtifactId() + FileUtil.FILE_SEPARATOR  + plugin.getCurrentVersion() + FileUtil.FILE_SEPARATOR  + plugin.getArtifactId() + "-" + plugin.getCurrentVersion() + ".jar";

        String destFileNameFullPath = LOCAL_REPO_PATH + fullJarPath;

        File file = httpRequest.execute().writeBodyForFile(new File(destFileNameFullPath), streamProgress);
        if (!file.exists() && file.length() == 0) {
            log.error("Plugin {} download failed.", plugin.getName());
            return false;
        }
        log.info("Plugin {} download success. downloaded file size is {} bytes", plugin.getName(), file.length());
        plugin.setPluginLocalStorageFullPath(destFileNameFullPath);

        BaseResourceManager resourceManager = BaseResourceManager.getInstance();
        if (resourceManager != null) {
            String resourcePath = "plugin-marketing" + FileUtil.FILE_SEPARATOR + fullJarPath;
            resourceManager.putFile(resourcePath, new File(plugin.getPluginLocalStorageFullPath()));
            plugin.setPluginResourceStorageFullPath(resourcePath);
            log.info("Plugin {} put to resource manager success", plugin.getName());
        }
        // then mark as downloaded
        plugin.setDownloaded(true);

        return updateById(plugin);
    }

    /**
     * 安装插件
     *
     * @param pluginMarketing 插件信息
     * @return 安装成功 true 安装失败 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean installPlugin(PluginMarketing pluginMarketing) {
        PluginMarketing dbMarketingInfo =  getById(pluginMarketing.getId());
        if (!pluginMarketing.getDownloaded()) {
            throw new BusException("Plugin " + pluginMarketing.getName() + " is not downloaded. please download it of first");
        }
        // todo: 1. 调用安装接口，安装插件,安装需要把插件的依赖下载下来，并加载到 classloader 中
        // 重新构建 下载地址
        String pluginDownloadUrl = dbMarketingInfo.getPluginDownloadUrl();
        pluginDownloadUrl.replaceAll(dbMarketingInfo.getCurrentVersion(), pluginMarketing.getCurrentVersion());
        pluginMarketing.setPluginDownloadUrl(pluginDownloadUrl);
        boolean downloadedPlugin = downloadedPlugin(pluginMarketing);
        if (!downloadedPlugin) {
            throw new BusException("Plugin " + pluginMarketing.getName() + " download failed");
        }else {
            log.info("Plugin {} download success, then install it", pluginMarketing.getName());
            pluginMarketing.setDownloaded(true);
        }
        // todo: 2. 安装成功后，标记为已安装


        pluginMarketing.setInstalled(true);
        return updateById(pluginMarketing);
    }

    /**
     * @param pluginId 插件id
     * @return List<PluginMarketing>
     */
    @Override
    public List<String> queryAllVersionByPluginId(Integer pluginId) {
        PluginMarketing plugin = getById(pluginId);
        String pluginMarketUrl = systemConfiguration.getPluginMarketSearchUrl().getValue();
        // https://search.maven.org/solrsearch/select?q=g:org.apache.flink+AND+a:flink-annotations&core=gav&rows=20&wt=json
        String pluginMarketSearchUrl = String.format("%s?q=g:%s+AND+p:jar+AND+a:%s&core=gav&rows=%d&wt=json", pluginMarketUrl, systemConfiguration.getPluginMarketSearchKeyWord().getValue(), plugin.getArtifactId() ,systemConfiguration.getPluginMarketSearchKeyWordOfNumbers().getValue());
        return getPluginMarketingsList(pluginMarketSearchUrl).stream().map(PluginMarketing::getCurrentVersion).collect(Collectors.toList());
    }

}
