package com.sharepast.jms.consumer.email;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Created by IntelliJ IDEA.
 * User: Kostya
 * Date: 2/15/11
 * Time: 10:50 PM
 * To change this template use File | Settings | File Templates.
 */

@Component("mailQueueListener")
public class MailQueueListener implements MessageListener
{
    public void onMessage( final Message message )
    {
        if ( message instanceof TextMessage )
        {
            final TextMessage textMessage = (TextMessage) message;
            try
            {
                System.out.println( textMessage.getText() );
            }
            catch (final JMSException e)
            {
                e.printStackTrace();
            }
        }
    }
}
