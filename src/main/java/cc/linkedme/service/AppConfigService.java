package cc.linkedme.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cc.linkedme.constants.Constants;

@Service
public class AppConfigService {

    @Value("${zk.connection}")
    private String connectionString;

    public List<String> getApps() throws Exception {
        return getChild(Constants.APP_PATH);

    }
    
    public Map<String,String> getAppConfigs(String appName) throws Exception{
        return getChildValue(Constants.APP_PATH+"/"+appName);
    }
    
    public int addNewApp(String appName) throws Exception{
        CuratorFramework client = CreateZkClient.getZkClient(connectionString);
        client.create().withMode(CreateMode.PERSISTENT).forPath(Constants.APP_PATH+"/"+appName, appName.getBytes());
        return 1;
    }
    
    public int addNewAppNode(String appName,String key,String value) throws Exception{
        CuratorFramework client = CreateZkClient.getZkClient(connectionString);
        client.create().withMode(CreateMode.PERSISTENT).forPath(Constants.APP_PATH+"/"+appName+"/"+key, value.getBytes());
        return 1;
    }
    
    public int updateAppNode(String appName,String key,String value) throws Exception{
        CuratorFramework client = CreateZkClient.getZkClient(connectionString);
        client.setData().forPath(Constants.APP_PATH+"/"+appName+"/"+key, value.getBytes());
        return 1;
    }
    
    
    private List<String> getChild(String path) throws Exception{
        CuratorFramework client = CreateZkClient.getZkClient(connectionString);
        List<String> children = client.getChildren().forPath(Constants.APP_PATH);
        return children;
    }
    
    
    private Map<String,String> getChildValue(String path) throws Exception{
        Map<String, String> result = new HashMap<>();
        CuratorFramework client = CreateZkClient.getZkClient(connectionString);
        List<String> children = client.getChildren().forPath(path);
        if (CollectionUtils.isNotEmpty(children)) {
            for (String child : children) {
                byte[] nodeData = client.getData()
                        .forPath(path + "/" + child);
                String value = new String(nodeData);
                result.put(child, value);
            }
        }
        return result;
    }
    
    
}
