package ifood.score.messagebus.producer;

public interface MessageProducer<M> {

    void publish(M message);
}
