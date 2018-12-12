package cc.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateZkClient {
    private static CuratorFramework client = null; 
    private static Logger LOGGER = LoggerFactory.getLogger(CreateZkClient.class);
    
    @Bean(name="zkClient")
    public CuratorFramework getZkClient(@Value("${zk.connection}")String connectionString){
        if(client == null){
            LOGGER.info("init curator client ~");
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
