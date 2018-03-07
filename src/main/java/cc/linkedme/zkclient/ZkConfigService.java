package cc.linkedme.zkclient;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import cc.linkedme.constants.Constants;


@Service
@Lazy(value = false)
public class ZkConfigService {

    private static Logger LOGGER = LoggerFactory.getLogger(ZkConfigService.class);
    private static final String ACTIVE_APP = "active";
    private static final Charset charset = Charset.forName("UTF-8");


    // 使用自己的线程池，默认接口中如果zk断掉的情况下，每次重连会重新创建一个线程池
//    private final static ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 100, 5l, TimeUnit.SECONDS,
//            new LinkedBlockingQueue<Runnable>(100), new ThreadPoolExecutor.CallerRunsPolicy());
    private PathChildrenCache childrenCache;
    private CuratorFramework client = null;

    private static Map<String, String> params = new ConcurrentHashMap<>();
    
    public static String getStringDefault(String key,String defaultValue){
        String value = params.get(key);
        if(StringUtils.isNotBlank(value)){
            return value;
        }
        return defaultValue;
    }
    
    public static boolean getBooleanDefault(String key, boolean defaultValue){
        String value = params.get(key);
        try{
            int result = Integer.valueOf(value);
            if(result > 0){
                return true;
            }
            return false;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return defaultValue;
    }
    
    public static int getIntegrDefault(String key, int defaultValue){
        String value = params.get(key);
        try{
            return Integer.valueOf(value);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return defaultValue;
    }

    private ZkConfigService(@Value("${zk.connection}") String connectionString) {
        try {
            client = createWithOptions(connectionString, new ExponentialBackoffRetry(1000, 3), 3000, 5000);
            client.start();
            watchChild(Constants.APP_PATH + "/" + ACTIVE_APP, client);
        } catch (Exception ex) {
            LOGGER.error("create zk client error,ex:", ex);
        }
    }

    protected void watchChild(String path, CuratorFramework client) throws Exception {
        childrenCache = new PathChildrenCache(client, path, true);
        ZkPathListener listener = new ZkPathListener();
        childrenCache.getListenable().addListener(listener);
        childrenCache.start();
    }

    private static CuratorFramework createWithOptions(String connectionString, RetryPolicy retryPolicy, int connectionTimeoutMs,
            int sessionTimeoutMs) {
        return CuratorFrameworkFactory.builder().connectString(connectionString).retryPolicy(retryPolicy)
                .connectionTimeoutMs(connectionTimeoutMs).sessionTimeoutMs(sessionTimeoutMs)
                // etc. etc.
                .build();
    }


    static class ZkPathListener implements PathChildrenCacheListener {
        @Override
        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            switch (event.getType()) {
                case CHILD_ADDED: {
                    String k = event.getData().getPath();
                    byte[] nodeData = client.getData().forPath(k);
                    String v = new String(nodeData,charset);
                    String key = k.replace(Constants.APP_PATH + "/" + ACTIVE_APP + "/" , "");
                    params.put(key, v);
                    LOGGER.info("Node added, path:+"+k+",key：" + key + ",value:" + v);
                    break;
                }
                case CHILD_UPDATED: {
                    String k = event.getData().getPath();
                    byte[] nodeData = client.getData().forPath(k);
                    String v = new String(nodeData,charset);
                    String key = k.replace(Constants.APP_PATH + "/" + ACTIVE_APP + "/" , "");
                    params.put(key, v);
                    LOGGER.info("Node updated, path:+"+k+", key：" + key + ",value:" + v);
                    break;
                }
                case CHILD_REMOVED: {
                    String k = event.getData().getPath();
                    params.remove(k);
                    String key = k.replace(Constants.APP_PATH + "/" + ACTIVE_APP + "/" , "");
                    LOGGER.info("Node removed, path:+"+k+" ,key：" + key );
                    break;
                }
                default:
                    break;
            }
        }
    }
}


