package com.sharepast.tests.jms;

import com.sharepast.commons.spring.SpringConfiguration;
import com.sharepast.commons.spring.config.BaseConfig;
import com.sharepast.commons.spring.web.AbstractHttpServer;
import com.sharepast.tests.common.SpringContextSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;


/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 2/23/11
 * Time: 12:33 AM
 * To change this template use File | Settings | File Templates.
 */

public class JmsProducerTest extends SpringContextSupport {

        private static final Logger LOG = LoggerFactory.getLogger(JmsProducerTest.class);
        private String msg;

        TestQueueSender jmsSender;

        CountDownLatch latch = new CountDownLatch(1);

        @Configuration
        @Import({BaseConfig.class, TestQueueListener.class, TestQueueSender.class})
        @ImportResource({"com/sharepast/test/jms/jms-test.xml"})
        static class TestJmsProducerConf {}

        @Override
        public Class getConfiguration() {
            return TestJmsProducerConf.class;
        }

        @BeforeClass
        public void setUp() throws Exception {
            AbstractHttpServer jmsServer = SpringConfiguration.getInstance().getBean(AbstractHttpServer.class, "activemq-server");
            Assert.assertNotNull(jmsServer);
            jmsServer.start();
        }


        @Test
        public void checkJms()
                throws Exception {

            jmsSender = SpringConfiguration.getInstance().getBean(TestQueueSender.class, "testQueueSender");
            Assert.assertNotNull(jmsSender);

            class BasicThread2 implements Runnable {


                // This method is called when the thread runs
                public void run() {
                    TestQueueListener jmsListener = SpringConfiguration.getInstance().getBean(TestQueueListener.class, "testQueueListener");
                    Assert.assertNotNull(jmsListener);
                    msg = jmsListener.getMsg();
                    latch.countDown();
                }
            }


            Runnable runnable = new BasicThread2();
            Thread thread = new Thread(runnable);

            jmsSender.send("Test Message");

            Thread.sleep(1000);
            thread.start();
            latch.await();

            Assert.assertEquals(msg, "Test Message");
        }
    }
