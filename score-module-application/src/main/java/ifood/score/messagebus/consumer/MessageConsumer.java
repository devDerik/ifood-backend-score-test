package ifood.score.messagebus.consumer;

import ifood.score.exception.MessageConsumerException;

public interface MessageConsumer<M> {

    void onMessageReceived(M message) throws MessageConsumerException;
}
