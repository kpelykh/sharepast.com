package com.sharepast.jms;

import com.sharepast.jms.test.TestQueueListener;
import com.sharepast.jms.test.TestQueueSender;
import com.sharepast.util.spring.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 2/23/11
 * Time: 12:33 AM
 * To change this template use File | Settings | File Templates.
 */

@Test(enabled = true)
public class JmsProducerTest {

        private static final Logger LOG = LoggerFactory.getLogger(JmsProducerTest.class);
        private String msg;

        @BeforeClass
        public void prepareBeforeTest()
                throws Exception {
            Configurator.getInstance().configure("com/sharepast/base.xml", "com/sharepast/jms/jms-test.xml");
        }

        @AfterClass
        public void afterClass() {
            Configurator.getInstance().shutdown();
        }

        @Test
        public void checkJms()
                throws Exception {

            TestQueueSender jmsSender = Configurator.squeeze(TestQueueSender.class, "testQueueSender");

            class BasicThread2 implements Runnable {
                TestQueueListener jmsListener = null;

                BasicThread2() {
                    jmsListener = Configurator.squeeze(TestQueueListener.class, "testQueueListener");
                }

                // This method is called when the thread runs
                public void run() {
                    assertNotNull(jmsListener);
                    msg = jmsListener.getMsg();
                }
            }


            Runnable runnable = new BasicThread2();
            Thread thread = new Thread(runnable);

            jmsSender.send("Test Message");

            Thread.sleep(1000);
            thread.start();
            Thread.sleep(1000);

            assertEquals(msg, "Test Message");
        }
    }
