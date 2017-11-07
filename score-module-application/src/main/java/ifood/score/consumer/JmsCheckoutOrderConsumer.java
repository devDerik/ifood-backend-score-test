package ifood.score.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ifood.score.exception.MessageConsumerException;
import ifood.score.order.Order;
import ifood.score.service.ScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JmsCheckoutOrderConsumer implements MessageConsumer<byte[]> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsCheckoutOrderConsumer.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ScoreService scoreService;

    public JmsCheckoutOrderConsumer(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @Override
    @Transactional
    @JmsListener(destination = "checkout-order", containerFactory = "jmsQueueFactory")
    public void onMessageReceived(byte[] message) throws MessageConsumerException {
        LOGGER.debug("Receiving checkout order");
        final Order order;
        try {
            order = OBJECT_MAPPER.readValue(message, Order.class);
        } catch (IOException e) {
            throw new MessageConsumerException("Error serializing the message", e);
        }
        scoreService.generateScores(order);
    }

    @JmsListener(destination = "ActiveMQ.DLQ", containerFactory = "jmsQueueFactory")
    public void onMessageReceivedDlq(byte[] message) {
        LOGGER.warn("Received message on DEAD LETTER QUEUE.");
    }
}
