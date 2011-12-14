package kp.app.monitoring;

import kp.app.startup.AppRunner;
import kp.app.util.Build;
import kp.app.util.spring.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.Date;
import java.util.Map;

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

    @ManagedAttribute(description = "build unique ID")
    public String getBuildUniqueId() {
        return Build.getUniqueBuildId();
    }

    @ManagedAttribute(description = "build timestamp")
    public String getBuildTimestamp() {
        return Build.getTimestamp();
    }

    @ManagedAttribute(description = "build version")
    public String getBuildVersion() {
        return Build.getVersion();
    }

    @ManagedAttribute(description = "build module we got main version from ")
    public String getBuildVersionModule() {
        return Build.getModule();
    }

    @ManagedAttribute(description = "build module we got main version from ")
    public Map<String, String> getBuildComponents() {
        return Build.getComponents();
    }

    @ManagedAttribute(description = "configuration files that were used to start this instance")
    public String[] getConfigurationResources() {
        return Configurator.getInstance().getConfigurations();
    }

    @ManagedOperation(description = "stop container without any delays. Kills all current requests")
    public String stop() throws Exception {
        if (std != null)
            return String.format("started countdown %d millis ago. Delay is %d millis", std.shutdown, std.delay);

        try {
            AppRunner.stopRunner();
        } catch (Throwable the) {
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
