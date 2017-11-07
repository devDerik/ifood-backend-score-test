package ifood.score.producer;

import org.springframework.jms.core.JmsTemplate;

import java.util.UUID;

public class JmsCancelOrderProducer implements MessageProducer<UUID> {
    private final JmsTemplate jmsTemplate;
    private final String queueName;

    public JmsCancelOrderProducer(JmsTemplate jmsTemplate, String queueName) {
        this.jmsTemplate = jmsTemplate;
        this.queueName = queueName;
    }


    @Override
    public void publish(UUID message) {
        jmsTemplate.convertAndSend(queueName, message);
    }
}
