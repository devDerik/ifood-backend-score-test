package ifood.score.messagebus.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ifood.score.order.Order;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.jms.core.JmsTemplate;

public class JmsCheckoutOrderProducer implements MessageProducer<Order> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String METRIC_COUNTER_NAME = "MESSAGE_CHECKOUT_ORDER_SENT_COUNTER";
    private static final String METRIC_LARGE_CHECKOUT_COUNTER_NAME = "MESSAGE_CHECKOUT_LARGE_ORDER_SENT_COUNTER";
    private final JmsTemplate jmsTemplate;
    private final String queueName;
    private final String largeOrderQueueName;
    private final Integer largeOrderNumber;
    private final Counter checkoutOrderCounter;
    private final Counter checkoutLargeOrderCounter;

    public JmsCheckoutOrderProducer(JmsTemplate jmsTemplate, String queueName,
                                    String largeOrderQueueName, Integer largeOrderNumber,
                                    MeterRegistry meterRegistry) {
        this.jmsTemplate = jmsTemplate;
        this.queueName = queueName;
        this.largeOrderQueueName = largeOrderQueueName;
        this.largeOrderNumber = largeOrderNumber;
        this.checkoutOrderCounter = Counter.builder(METRIC_COUNTER_NAME).register(meterRegistry);
        this.checkoutLargeOrderCounter = Counter.builder(METRIC_LARGE_CHECKOUT_COUNTER_NAME).register(meterRegistry);
    }

    @Override
    public void publish(Order message) {
        final byte[] serializedMessage;
        try {
            serializedMessage = OBJECT_MAPPER.writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing order Id: " + message.getUuid(), e);
        }
        if (message.getItems().size() > largeOrderNumber) {
            jmsTemplate.convertAndSend(largeOrderQueueName, serializedMessage);
            checkoutLargeOrderCounter.count();
        } else {
            jmsTemplate.convertAndSend(queueName, serializedMessage);
            checkoutOrderCounter.count();
        }
    }
}
