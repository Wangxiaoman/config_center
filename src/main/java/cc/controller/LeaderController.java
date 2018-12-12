package cc.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cc.constants.Constants;
import cc.zkclient.ZkLeaderClient;

@RestController
@RequestMapping("/config/leader")
public class LeaderController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    private Map<String, ZkLeaderClient> leaderClientMap = new HashMap<>();
    private Map<String, CuratorFramework> clientMap = new HashMap<>();
    private final static String LEADER_PARENT_PATH = Constants.APP_PATH + "/" + "leader";
    private final static String LEADER_PATH = "/data";

    @GetMapping("/client")
    public String createClient(
            @RequestParam(value = "clientName", required = true) String clientName) {
        LOGGER.info("create leader client , name:{}", clientName);
        clientName = "Client #" + clientName;

        CuratorFramework client = null;
        ZkLeaderClient zkLeaderClient = null;
        try {
            client = CuratorFrameworkFactory.newClient(LEADER_PARENT_PATH,
                    new ExponentialBackoffRetry(1000, 3));
            clientMap.put(clientName, client);

            zkLeaderClient = new ZkLeaderClient(client, LEADER_PATH, "Client #" + clientName);
            leaderClientMap.put(clientName, zkLeaderClient);

            client.start();
            zkLeaderClient.start();
        } catch (Exception ex) {
            LOGGER.error("error:", ex);
        } 
        return "OK";
    }
    
    @GetMapping("/client/close")
    public String closeClient(
            @RequestParam(value = "clientName", required = true) String clientName) {
        CuratorFramework client = null;
        ZkLeaderClient zkLeaderClient = null;
        clientName = "Client #" + clientName;
        try{
            client = clientMap.get(clientName);
            zkLeaderClient = leaderClientMap.get(clientName);
            
            LOGGER.info("curent client:{},zkLeaderClient:{}", client, zkLeaderClient);
        }finally{
            LOGGER.info("Shutting down...");
            if(zkLeaderClient != null){
                CloseableUtils.closeQuietly(zkLeaderClient);
            }
            if(client != null){
                CloseableUtils.closeQuietly(client);
            }
            LOGGER.info("curent client:{},zkLeaderClient:{}", client, zkLeaderClient);
        }
        return "OK";
    }
}
