package ifood.score.messagebus.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifood.score.exception.MessageConsumerException;
import ifood.score.order.Order;
import ifood.score.service.ScoreService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public class JmsCheckoutLargeOrderConsumer implements MessageConsumer<byte[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsCheckoutOrderConsumer.class);
    private static final String METRIC_COUNTER_NAME = "MESSAGE_CHECKOUT_LARGE_ORDER_CONSUMED_COUNTER";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ScoreService scoreService;
    private final Counter checkouLargeOrderCounter;

    public JmsCheckoutLargeOrderConsumer(ScoreService scoreService, MeterRegistry meterRegistry) {
        this.scoreService = scoreService;
        this.checkouLargeOrderCounter = Counter.builder(METRIC_COUNTER_NAME).register(meterRegistry);
    }

    @Override
    @Transactional
    @JmsListener(destination = "${checkout.large.order.queue.name}", containerFactory = "jmsQueueFactory")
    public void onMessageReceived(byte[] message) throws MessageConsumerException {
        LOGGER.debug("Receiving checkout large order");
        final Order order;
        try {
            order = OBJECT_MAPPER.readValue(message, Order.class);
        } catch (IOException e) {
            throw new MessageConsumerException("Error serializing the message", e);
        }
        scoreService.generateScores(order);
        checkouLargeOrderCounter.count();
    }
}
