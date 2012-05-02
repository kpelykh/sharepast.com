package java.com.sharepast.jms.test;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 2/23/11
 * Time: 12:42 AM
 * To change this template use File | Settings | File Templates.
 */
@Component("testQueueListener")
public class TestQueueListener implements MessageListener
{
    private String msg;
    @Override
    public void onMessage(Message message) {
        try {
            this.msg = ((ActiveMQTextMessage) message).getText();
        } catch (JMSException ignore) {
        }
    }

    public String getMsg() {
        return msg;
    }
}

