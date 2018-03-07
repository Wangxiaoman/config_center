package cc.linkedme.service;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CreateZkClient {
    private static CuratorFramework client = null; 
    
    public synchronized static CuratorFramework getZkClient(String connectionString){
        if(client == null){
            client = createWithOptions(connectionString, new ExponentialBackoffRetry(1000, 3), 3000, 5000);
            client.start();
        }
        return client;
    }
    
    private static CuratorFramework createWithOptions(String connectionString, RetryPolicy retryPolicy, int connectionTimeoutMs,
            int sessionTimeoutMs) {
        // using the CuratorFrameworkFactory.builder() gives fine grained control
        // over creation options. See the CuratorFrameworkFactory.Builder javadoc
        // details
        return CuratorFrameworkFactory.builder().connectString(connectionString).retryPolicy(retryPolicy)
                .connectionTimeoutMs(connectionTimeoutMs).sessionTimeoutMs(sessionTimeoutMs)
                // etc. etc.
                .build();
    }
}
