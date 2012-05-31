package com.sharepast.monitoring;

import com.sharepast.Bootstrap;
import com.sharepast.commons.spring.SpringConfiguration;
import com.sharepast.commons.util.Build;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 1/16/11
 * Time: 10:22 PM
 * To change this template use File | Settings | File Templates.
 */
@ManagedResource(objectName = "app:name=container", description = "currently running container info")
public class ContainerInfo {

    ShutTheDown std;

    private final Logger LOG = LoggerFactory.getLogger(ContainerInfo.class);

    @ManagedAttribute(description = "build module we got main version from ")
    public List<Build.ComponentInfo> getBuildComponents() {
        return Build.getComponents();
    }

    @ManagedAttribute(description = "configuration files that were used to start this instance")
    public Class[] getConfigurationResources() {
        return SpringConfiguration.getInstance().getConfigurations();
    }

    @ManagedOperation(description = "stop container without any delays. Kills all current requests")
    public String stop() throws Exception {
        if (std != null)
            return String.format("started countdown %d millis ago. Delay is %d millis", std.shutdown, std.delay);

        try {
            Bootstrap.stopRunner();
        } catch (Throwable ignored) {
        }

        Thread t = new Thread(std = new ShutTheDown(15000L));
        t.start();

        return String.format("started countdown at %s with delay %d millis", new Date() + "", std.delay);
    }
}

class ShutTheDown
        implements Runnable {
    public volatile long shutdown = 0L;
    public volatile long delay = 15000L;

    public ShutTheDown(long delay) {
        this.shutdown = System.currentTimeMillis();
        this.delay = delay;
    }

    public void run() {
        for (; ;)
            if ((System.currentTimeMillis() - shutdown) > delay)
                System.exit(0);
            else {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                }
            }
    }

}
