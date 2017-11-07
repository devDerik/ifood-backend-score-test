package ifood.score.producer;

public interface MessageProducer<M> {

    void publish(M message);
}
