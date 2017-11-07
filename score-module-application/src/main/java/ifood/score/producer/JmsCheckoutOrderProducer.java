package ifood.score.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ifood.score.order.Order;
import org.springframework.jms.core.JmsTemplate;

public class JmsCheckoutOrderProducer implements MessageProducer<Order> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final JmsTemplate jmsTemplate;
    private final String queueName;

    public JmsCheckoutOrderProducer(JmsTemplate jmsTemplate, String queueName) {
        this.jmsTemplate = jmsTemplate;
        this.queueName = queueName;
    }

    @Override
    public void publish(Order message) {
        try {
            jmsTemplate.convertAndSend(queueName, OBJECT_MAPPER.writeValueAsBytes(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing order Id: " + message.getUuid(), e);
        }
    }
}
