package ifood.score.consumer;

import ifood.score.exception.MessageConsumerException;
import ifood.score.service.ScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

import java.util.UUID;

public class JmsCancelOrderConsumer implements MessageConsumer<UUID> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsCancelOrderConsumer.class);
    private final ScoreService scoreService;

    public JmsCancelOrderConsumer(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @Override
    @JmsListener(destination = "cancel-order", containerFactory = "jmsQueueFactory")
    public void onMessageReceived(UUID message) throws MessageConsumerException {
        LOGGER.info("Cancelling Order: {}", message);
        scoreService.cancelScoresByOrderId(message);
    }
}
