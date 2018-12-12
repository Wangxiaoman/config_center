package cc.zkclient;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @Service
// @Lazy(value = false)
public class ZkLeaderClient extends LeaderSelectorListenerAdapter implements Closeable {
    private static Logger LOGGER = LoggerFactory.getLogger(ZkLeaderClient.class);
    // @Resource
    // private CuratorFramework client;

    private final String name;
    private LeaderSelector leaderSelector;
    private final AtomicInteger leaderCount = new AtomicInteger();
    private CuratorFramework client;

    public ZkLeaderClient(CuratorFramework client, String leadPath, String name) {
        this.name = name;
        // create a leader selector using the given path for management
        // all participants in a given leader selection must use the same path
        // ExampleClient here is also a LeaderSelectorListener but this isn't required
        leaderSelector = new LeaderSelector(client, leadPath, this);
        leaderSelector.setId(name + "-" + leaderSelector.getId());

        // for most cases you will want your instance to requeue when it relinquishes leadership
        leaderSelector.autoRequeue();
        this.client = client;
    }
    
    public CuratorFramework getClient(){
        return this.client;
    }
    
    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        // we are now the leader. This method should not return until we want to relinquish
        // leadership

        final int waitSeconds = (int) (100 * Math.random()) ;

        LOGGER.info(name + " is now the leader. Waiting " + waitSeconds + " seconds...");
        LOGGER.info(
                name + " has been leader " + leaderCount.getAndIncrement() + " time(s) before.");
        
        System.out.println(name + " is now the leader. Waiting " + waitSeconds + " seconds...");
        System.out.println(name + " has been leader " + leaderCount.getAndIncrement() + " time(s) before.");
        LOGGER.info("leader:"+this.leaderSelector.getLeader() +"participants:"+ this.leaderSelector.getParticipants());
        
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
        } catch (InterruptedException e) {
            LOGGER.error(name + " was interrupted.");
            Thread.currentThread().interrupt();
        } finally {
            LOGGER.info(name + " relinquishing leadership.\n");
        }
    }

    @Override
    public void close() throws IOException {
        leaderSelector.close();
    }

    public void start() throws IOException {
        // the selection for this instance doesn't start until the leader selector is started
        // leader selection is done in the background so this call to leaderSelector.start() returns
        // immediately
        leaderSelector.start();
    }
}
