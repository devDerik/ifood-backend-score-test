package ifood.score.messagebus.consumer;

import ifood.score.exception.MessageConsumerException;

public interface MessageConsumer<M> {

    /**
     * Handles a message received from the Message Bus.
     * @param message
     * @throws MessageConsumerException
     */
    void onMessageReceived(M message) throws MessageConsumerException;
}
