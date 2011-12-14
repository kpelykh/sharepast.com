package kp.app.jms.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 2/15/11
 * Time: 10:49 PM
 * To change this template use File | Settings | File Templates.
 */

@Component("mailQueueSender")
public class MailQueueSender
{
    private final JmsTemplate jmsTemplate;

    @Autowired
    public MailQueueSender(final JmsTemplate jmsTemplate)
    {
        this.jmsTemplate = jmsTemplate;
    }

    public void send( final String message )
    {
        jmsTemplate.convertAndSend( "mailQueue", message );
    }
}
