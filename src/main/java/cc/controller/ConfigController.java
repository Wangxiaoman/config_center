package cc.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cc.constants.CommonStatus;
import cc.constants.Constants;
import cc.constants.ResultJson;
import cc.service.AppConfigService;
import cc.util.MD5;


@RestController
@RequestMapping("/config/center")
public class ConfigController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource
    private AppConfigService appConfigService;

    @GetMapping("/apps")
    public ResultJson getApps(@RequestParam(value = "time", required = true) long time,
            @RequestParam(value = "sign", required = true) String sign) {
        if (!auth(time, sign)) {
            return new ResultJson(CommonStatus.NO_AUTH);
        }
        try {
            List<String> appNames = appConfigService.getApps();
            return new ResultJson(CommonStatus.SUCCESS, appNames);
        } catch (Exception e) {
            LOGGER.error("get apps error,ex:", e);
            return new ResultJson(CommonStatus.SERVER_ERROR);
        }
    }

    @PostMapping("/apps")
    public ResultJson postApps(@RequestParam(value = "time", required = true) long time,
            @RequestParam(value = "sign", required = true) String sign,
            @RequestParam(value = "appName", required = true) String appName) {
        if (!auth(time, sign)) {
            return new ResultJson(CommonStatus.NO_AUTH);
        }
        try {
            appConfigService.addNewApp(appName);
            return new ResultJson(CommonStatus.SUCCESS);
        } catch (Exception e) {
            LOGGER.error("save apps configs error,ex:", e);
            return new ResultJson(CommonStatus.SERVER_ERROR);
        }
    }

    @GetMapping("/apps/configs")
    public ResultJson getAppConfigs(@RequestParam(value = "time", required = true) long time,
            @RequestParam(value = "sign", required = true) String sign,
            @RequestParam(value = "appName", required = true) String appName) {
        if (!auth(time, sign)) {
            return new ResultJson(CommonStatus.NO_AUTH);
        }
        try {
            Map<String, String> result = appConfigService.getAppConfigs(appName);
            return new ResultJson(CommonStatus.SUCCESS, result);
        } catch (Exception e) {
            LOGGER.error("get apps configs error,ex:", e);
            return new ResultJson(CommonStatus.SERVER_ERROR);
        }
    }

    @PostMapping("/apps/configs")
    public ResultJson saveAppConfigs(@RequestParam(value = "time", required = true) long time,
            @RequestParam(value = "sign", required = true) String sign,
            @RequestParam(value = "appName", required = true) String appName,
            @RequestParam(value = "key", required = true) String key,
            @RequestParam(value = "value", required = true) String value,
            @RequestParam(value = "state", required = true, defaultValue = "0") int state) {
        if (!auth(time, sign)) {
            return new ResultJson(CommonStatus.NO_AUTH);
        }
        try {
            if (state == Constants.ZK_NODE_CREATE) {
                appConfigService.addNewAppNode(appName, key, value);
            } else if (state == Constants.ZK_NODE_UPDATE) {
                appConfigService.updateAppNode(appName, key, value);
            }
            return new ResultJson(CommonStatus.SUCCESS);
        } catch (Exception e) {
            LOGGER.error("save apps configs error,ex:", e);
            return new ResultJson(CommonStatus.SERVER_ERROR);
        }
    }

    private static boolean auth(long time, String sign) {
        return MD5.getMD5Code(Constants.PUB_KEY + time).equals(sign);
    }

    @GetMapping("/pubkey")
    public ResultJson getPubKey() {
        return new ResultJson(CommonStatus.SUCCESS, Constants.PUB_KEY);
    }
}
