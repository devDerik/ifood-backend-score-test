package ifood.score.messagebus.consumer;

import ifood.score.exception.MessageConsumerException;
import ifood.score.service.ScoreService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class JmsCancelOrderConsumer implements MessageConsumer<UUID> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsCancelOrderConsumer.class);
    private static final String METRIC_COUNTER_NAME = "MESSAGE_CANCEL_ORDER_CONSUMED_COUNTER";
    private final ScoreService scoreService;
    private final Counter cancelOrderCounter;

    public JmsCancelOrderConsumer(ScoreService scoreService, MeterRegistry meterRegistry) {
        this.scoreService = scoreService;
        this.cancelOrderCounter = Counter.builder(METRIC_COUNTER_NAME).register(meterRegistry);
    }

    @Override
    @Transactional
    @JmsListener(destination = "${cancel.order.queue.name}", containerFactory = "jmsQueueFactory")
    public void onMessageReceived(UUID message) throws MessageConsumerException {
        LOGGER.info("Cancelling Order: {}", message);
        scoreService.cancelScoresByOrderId(message);
        cancelOrderCounter.count();
    }
}
