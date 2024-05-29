package org.dinky.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.enums.Status;
import org.dinky.data.model.PluginMarketing;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.service.PluginMarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Plugin Marketing Controller")
@AllArgsConstructor
@RequestMapping("/api/plugin-marketing")
@SaCheckLogin
public class PluginMarketingController {

    @Autowired
    private PluginMarketingService pluginMarketService;

    @GetMapping("/sync")
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_SYNC)
    public Result<Void> syncPlugins() {
        boolean syncPluginMarketData = pluginMarketService.syncPluginMarketData();
        if (!syncPluginMarketData) {
            return Result.failed(Status.SYNC_FAILED);
        }
        return Result.succeed(Status.SYNC_SUCCESS);
    }

    @PostMapping("/list")
    @ApiOperation("Get Plugin List")
    @ApiImplicitParam(name = "params", value = "params", dataType = "JsonNode", paramType = "body", required = true)
    public ProTableResult<PluginMarketing> listToken(@RequestBody JsonNode params) {
        return pluginMarketService.selectForProTable(params);
    }
//    public Result<List<PluginMarketing>> listPlugins(
//            @RequestParam(defaultValue = "") String keyword,
//            @RequestParam(defaultValue = "false") boolean installed,
//             @RequestParam(defaultValue = "false") boolean isDownloaded
//    ) {
//        return Result.succeed(pluginMarketService.listPlugins(keyword,installed,isDownloaded));
//    }


    @PutMapping("/download")
    @ApiOperation("Download Plugin")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query", required = true)
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_DOWNLOAD)
    public Result<Void> downloadPlugin(@RequestParam("id") Integer id) {
        boolean downloadAndLoadDependency = pluginMarketService.downloadedPlugin(id);
        if (!downloadAndLoadDependency) {
            return Result.failed(Status.DOWNLOAD_FAILED);
        }
        return Result.succeed(Status.DELETE_SUCCESS);
    }

    @PutMapping("/install")
    @ApiOperation("Install Plugin")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query", required = true)
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_INSTALL)
    public Result<Void> installPlugin(@RequestParam("id") Integer id) {
        boolean downloadAndLoadDependency = pluginMarketService.installPlugin(id);
        if (!downloadAndLoadDependency) {
            return Result.failed(Status.INSTALL_FAILED);
        }
        return Result.succeed(Status.INSTALL_SUCCESS);
    }

    @DeleteMapping("/uninstall")
    @ApiOperation("Uninstall Plugin")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query", required = true)
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_UNINSTALL)
    public Result<Void> uninstallPlugin(@RequestParam("id") Integer id) {
        boolean uninstalledPlugin = pluginMarketService.uninstallPlugin(id);
        if (!uninstalledPlugin) {
            return Result.failed(Status.UNINSTALL_FAILED);
        }
        return Result.succeed(Status.UNINSTALL_SUCCESS);
    }

    @DeleteMapping("/delete")
    @ApiOperation("Delete Plugin")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query", required = true)
    @SaCheckPermission(PermissionConstants.SYSTEM_SETTING_PLUGIN_MARKET_DELETE)
    public Result<Void> deletePlugin(@RequestParam("id") Integer id) {
        boolean uninstalledPlugin = pluginMarketService.deletePlugin(id);
        if (!uninstalledPlugin) {
            return Result.failed(Status.DELETE_FAILED);
        }
        return Result.succeed(Status.DELETE_SUCCESS);
    }


}
