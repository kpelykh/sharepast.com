package java.com.sharepast.jms.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 2/23/11
 * Time: 12:42 AM
 * To change this template use File | Settings | File Templates.
 */
@Component("testQueueSender")
public class TestQueueSender {

    private final JmsTemplate jmsTemplate;

        @Autowired
        public TestQueueSender(final JmsTemplate jmsTemplate)
        {
            this.jmsTemplate = jmsTemplate;
        }

        public void send( final String message )
        {
            jmsTemplate.convertAndSend( "testQueue", message );
        }
    }

