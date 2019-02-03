package ifood.score.messagebus.producer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.jms.core.JmsTemplate;

import java.util.UUID;

public class JmsCancelOrderProducer implements MessageProducer<UUID> {
    private static final String METRIC_COUNTER_NAME = "MESSAGE_ORDER_CANCEL_SENT_COUNTER";
    private final JmsTemplate jmsTemplate;
    private final String queueName;
    private final Counter cancelOrderCounter;

    public JmsCancelOrderProducer(JmsTemplate jmsTemplate, String queueName, MeterRegistry meterRegistry) {
        this.jmsTemplate = jmsTemplate;
        this.queueName = queueName;
        this.cancelOrderCounter = Counter.builder(METRIC_COUNTER_NAME).register(meterRegistry);
    }


    @Override
    public void publish(UUID message) {
        jmsTemplate.convertAndSend(queueName, message);
        cancelOrderCounter.count();
    }
}
