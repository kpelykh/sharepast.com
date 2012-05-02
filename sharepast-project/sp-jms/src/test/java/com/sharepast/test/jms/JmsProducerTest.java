package java.com.sharepast.test.jms;

import com.sharepast.spring.config.BaseConfig;
import com.sharepast.spring.SpringConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
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

        @Configuration
        @Import({BaseConfig.class})
        @ImportResource({"com/sharepast/jms/jms-test.xml"})
        static class TestJmsProducerConf {}

        @BeforeClass
        public void prepareBeforeTest()
                throws Exception {
            SpringConfiguration.getInstance().configure(TestJmsProducerConf.class);
        }

        @AfterClass
        public void afterClass() {
            SpringConfiguration.getInstance().shutdown();
        }

        @Test
        public void checkJms()
                throws Exception {

            TestQueueSender jmsSender = SpringConfiguration.getInstance().getBean(TestQueueSender.class, "testQueueSender");

            class BasicThread2 implements Runnable {
                TestQueueListener jmsListener = null;

                BasicThread2() {
                    jmsListener = SpringConfiguration.getInstance().getBean(TestQueueListener.class, "testQueueListener");
                }

                // This method is called when the thread runs
                public void run() {
                    Assert.assertNotNull(jmsListener);
                    msg = jmsListener.getMsg();
                }
            }


            Runnable runnable = new BasicThread2();
            Thread thread = new Thread(runnable);

            jmsSender.send("Test Message");

            Thread.sleep(1000);
            thread.start();
            Thread.sleep(1000);

            Assert.assertEquals(msg, "Test Message");
        }
    }