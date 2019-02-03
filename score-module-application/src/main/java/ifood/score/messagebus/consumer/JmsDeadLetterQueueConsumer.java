package ifood.score.messagebus.consumer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

public class JmsDeadLetterQueueConsumer implements MessageConsumer<byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsDeadLetterQueueConsumer.class);
    private static final String DEAD_LETTER_QUEUE_COUNTER = "DEAD_LETTER_QUEUE_COUNTER";
    private final Counter deadLetterQueueCounter;

    public JmsDeadLetterQueueConsumer(MeterRegistry metricsRegistry) {
        this.deadLetterQueueCounter = Counter.builder(DEAD_LETTER_QUEUE_COUNTER).register(metricsRegistry);
    }

    @Override
    @JmsListener(destination = "ActiveMQ.DLQ", containerFactory = "jmsQueueFactory")
    public void onMessageReceived(byte[] message) {
        LOGGER.warn("Received message on DEAD LETTER QUEUE.");
        this.deadLetterQueueCounter.count();
    }
}
