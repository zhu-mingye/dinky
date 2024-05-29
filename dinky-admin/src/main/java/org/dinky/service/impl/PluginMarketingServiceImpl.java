package org.dinky.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dinky.data.exception.BusException;
import org.dinky.data.model.PluginMarketing;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.mapper.PluginMarketingMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.PluginMarketingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PluginMarketingServiceImpl extends SuperServiceImpl<PluginMarketingMapper, PluginMarketing> implements PluginMarketingService {

    private final String LOCAL_REPO_PATH = System.getProperty("user.dir") + FileUtil.FILE_SEPARATOR + "customJar" + FileUtil.FILE_SEPARATOR;


    private final SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();

    /**
     * 同步插件市场列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncPluginMarketData() {
        //todo 调用插件市场接口，获取插件列表
        String pluginMarketUrl = systemConfiguration.getMavenRepository();
        // todo: 如何获取插件列表
        HttpRequest httpRequest = HttpUtil.createGet(pluginMarketUrl + "?q=g:" + systemConfiguration.getPluginMarketSearchKeyWord().getValue() + "&start=0&rows=" + systemConfiguration.getPluginMarketSearchKeyWordOfNumbers().getValue());
        if (StrUtil.isNotEmpty(systemConfiguration.getMavenRepositoryUser()) && StrUtil.isNotEmpty(systemConfiguration.getMavenRepositoryPassword())) {
            httpRequest.basicAuth(systemConfiguration.getMavenRepositoryUser(), systemConfiguration.getMavenRepositoryPassword());
        }
        HttpResponse httpResponse = httpRequest.execute();
        // 创建插件列表集合
        List<PluginMarketing> pluginMarketingListOfRemoteRepo = new ArrayList<>();
        if (httpResponse.isOk()) {
            String pluginMarketList = httpResponse.body();
            // todo: 1.转换插件列表为实体列表放入 list .需要转换结构 拿到的结构和定义的最终结构不一致
            pluginMarketingListOfRemoteRepo.addAll(JSONUtil.toList(pluginMarketList, PluginMarketing.class));
        }
        //  2.查询现有的插件列表
        List<PluginMarketing> pluginMarketingListInDataBase = this.list();
        AtomicLong newInsertCount = new AtomicLong(0);
        AtomicLong continueCount = new AtomicLong(0);
        AtomicBoolean result = new AtomicBoolean(false);


        List<PluginMarketing> needInsertPluginMarketingList = new ArrayList<>();

        // 3.通过唯一索引对比(`name`,`plugin_id`,`repository_id`,`group_id`,`artifact_id`,`current_version`)，如果存在则更新，如果不存在则新增
        for (PluginMarketing pluginMarketing : pluginMarketingListOfRemoteRepo) {
            //  3.1.通过唯一索引对比(`name`,`plugin_id`,`repository_id`,`group_id`,`artifact_id`,`current_version`)，如果存在不操作，如果不存在则新增
            PluginMarketing searchPluginResult = pluginMarketingListInDataBase.stream().filter(plugin -> {
                        boolean matchResult = plugin.getName().equals(pluginMarketing.getName()) &&
                                plugin.getPluginId().equals(pluginMarketing.getPluginId()) &&
                                plugin.getRepositoryId().equals(pluginMarketing.getRepositoryId()) &&
                                plugin.getGroupId().equals(pluginMarketing.getGroupId()) &&
                                plugin.getArtifactId().equals(pluginMarketing.getArtifactId()) &&
                                plugin.getCurrentVersion().equals(pluginMarketing.getCurrentVersion());

                        if (matchResult) {
                            log.debug("The plugin {} has not been synchronized and the data does not exist. Please add a new one", plugin.getName());
                            newInsertCount.getAndIncrement();
                        } else {
                            log.debug("The plugin {} has been synchronized and the data already exists. Skipping", plugin.getName());
                            continueCount.getAndIncrement();
                        }
                        return matchResult;
                    }
            ).findFirst().orElse(null);

            if (searchPluginResult == null) {
                needInsertPluginMarketingList.add(pluginMarketing);
            }
        }
        boolean saveBatch = this.saveBatch(needInsertPluginMarketingList);
        if (saveBatch) {
            log.info("The plugin has been synchronized successfully, the number of new plugins is {}, the number of existing plugins is {}", newInsertCount.get(), continueCount.get());
        } else {
            log.error("The plugin has been synchronized failed ,");
        }
        return saveBatch;
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
        if (!plugin.getDownloaded() && StrUtil.isEmpty(plugin.getPluginLocalStorageFullPath())) {
            throw new BusException("The plugin " + plugin.getName() + " has not been downloaded and the storage path is empty, so it cannot be deleted");
        }
        boolean deleted = FileUtil.del(plugin.getPluginLocalStorageFullPath());
        if (!deleted) {
            throw new BusException("Plugin " + plugin.getName() + " delete failed");
        }
        return removeById(id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean downloadedPlugin(Integer id) {
        PluginMarketing plugin = getById(id);
        if (plugin.getDownloaded()) {
            throw new BusException("Plugin " + plugin.getName() + " is already downloaded.Skip this download");
        }
        String destFileNameFullPath = LOCAL_REPO_PATH + plugin.getName();
        // todo: 调用下载接口，下载插件, 如果需要账密，则使用账号密码,可能需要重写下载器
        HttpUtil.buildBasicAuth(systemConfiguration.getMavenRepositoryUser(), systemConfiguration.getMavenRepositoryPassword(),
                StandardCharsets.UTF_8);
        HttpRequest httpRequest = HttpUtil.createGet(plugin.getPluginDownloadUrl());
        if (StrUtil.isNotEmpty(systemConfiguration.getMavenRepositoryUser()) && StrUtil.isNotEmpty(systemConfiguration.getMavenRepositoryPassword())) {
            httpRequest.basicAuth(systemConfiguration.getMavenRepositoryUser(), systemConfiguration.getMavenRepositoryPassword());
        }
        long downloadedFile = httpRequest.execute().writeBody(destFileNameFullPath);
        if (downloadedFile == 0) {
            log.error("Plugin {} download failed.", plugin.getName());
            return false;
        }
        plugin.setDownloaded(true);
        plugin.setPluginLocalStorageFullPath(destFileNameFullPath);
        return save(plugin);
    }

    /**
     * 安装插件
     *
     * @param id 插件id
     * @return 安装成功 true 安装失败 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean installPlugin(Integer id) {
        PluginMarketing plugin = getById(id);
        if (!plugin.getDownloaded()) {
            throw new BusException("Plugin " + plugin.getName() + " is not downloaded. please download it of first");
        }
        // todo: 1. 调用安装接口，安装插件,安装需要把插件的依赖下载下来，并加载到 classloader 中

        // todo: 2. 安装成功后，标记为已安装
        plugin.setInstalled(true);
        return false;
    }

//
//    public void downloadAndLoadDependency(String groupId, String artifactId, String version, String localRepoPath) throws Exception {
//
//        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
//        RepositorySystem system = newRepositorySystem(locator);
//        RepositorySystemSession session = newSession(system, LOCAL_REPO_PATH);
//
//        // 定义远程仓库
//        RemoteRepository.Builder remoteRepository = new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2");
//        if (StrUtil.isNotEmpty(systemConfiguration.getMavenRepositoryUser()) && StrUtil.isNotEmpty(systemConfiguration.getMavenRepositoryPassword())) {
//            Authentication auth = new AuthenticationBuilder()
//                    .addUsername(systemConfiguration.getMavenRepositoryUser())
//                    .addPassword(systemConfiguration.getMavenRepositoryPassword())
//                    .build();
//            remoteRepository.setAuthentication(auth);
//        }
//        RemoteRepository repository = remoteRepository.build();
//
//        // 解析依赖
//        ArtifactRequest artifactRequest = new ArtifactRequest();
//        artifactRequest.setArtifact(new DefaultArtifact("groupId:artifactId:version"));
//        artifactRequest.setRepositories(Collections.singletonList(repository));
//
//        // 下载依赖
//        ArtifactResult artifactResult = system
//                .resolveArtifact(session, artifactRequest);
//        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
//        Artifact artifact = artifactResult.getArtifact();
//        descriptorRequest.setArtifact(artifact);
//        descriptorRequest.setRepositories(Collections.singletonList(repository));
//
//        Metadata metadata = new DefaultMetadata(artifact.getGroupId(), artifact.getArtifactId(), "maven-metadata.xml", Metadata.Nature.RELEASE_OR_SNAPSHOT);
//        MetadataRequest metadataRequest = new MetadataRequest(metadata, repository, null);
//
//        List<MetadataResult> metadataResult = system.resolveMetadata(session, Collections.singletonList(metadataRequest));
//        metadataResult.forEach(m -> {
//            Metadata mMetadata = m.getMetadata();
//            log.info("metadata: {}", mMetadata);
//
//        });
//    }

//
//    private static RepositorySystemSession newSession(RepositorySystem system, String storePath) {
//        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
//        LocalRepository localRepo = new LocalRepository(storePath);
//        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
//        // set possible proxies and mirrorsD:
//        //session.setProxySelector(new DefaultProxySelector().add(new Proxy(Proxy.TYPE_HTTP, "host", 3625), Arrays.asList("localhost", "127.0.0.1")));
//        //session.setMirrorSelector(new DefaultMirrorSelector().add("my-mirror", "http://mirror", "default", false, "external:*", null));
//        return session;
//    }

    /**
     * Generates a new RepositorySystem using the provided DefaultServiceLocator.
     *
     * @param locator the DefaultServiceLocator used to create the RepositorySystem
     * @return the newly created RepositorySystem
     */
//    private static RepositorySystem newRepositorySystem(DefaultServiceLocator locator) {
//        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
//        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
//        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
//        return locator.getService(RepositorySystem.class);
//    }
//
//    private void loadJarToLocalClasspath(String path, String jarName) throws MalformedURLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
//        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//        method.setAccessible(true);
//        method.invoke(classLoader, new File(path, jarName).toURI().toURL());
//    }


}
