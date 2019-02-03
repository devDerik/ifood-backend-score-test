package ifood.score.messagebus.producer;

public interface MessageProducer<M> {

    /**
     * Publishes a message to the Message Bus.
     * @param message
     */
    void publish(M message);
}
