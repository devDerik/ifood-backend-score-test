package ifood.score.order;

import static ifood.score.mock.generator.RandomishPicker._int;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

import ifood.score.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ifood.score.mock.generator.order.OrderPicker;

@Service
public class OrderCheckoutMock {

	@Autowired
	MessageProducer<Order> jmsCheckoutOrderProducer;

	@Autowired
	MessageProducer<UUID> jmsCancelOrderProducer;
	
	ConcurrentLinkedQueue<UUID> cancellantionQueue = new ConcurrentLinkedQueue<>();
	
	private static OrderPicker picker = new OrderPicker();
	
	@Scheduled(fixedRate=3*1000)
	public void checkoutFakeOrder(){
		IntStream.rangeClosed(1, _int(2, 12)).forEach(t -> {
			Order order = picker.pick();
			if(_int(0, 20) % 20 == 0){
				cancellantionQueue.add(order.getUuid());
			}
			jmsCheckoutOrderProducer.publish(order);
		});
	}
	
	@Scheduled(fixedRate=30*1000)
	public void cancelFakeOrder(){
		IntStream.range(1, _int(2, cancellantionQueue.size() > 2 ? cancellantionQueue.size() : 2)).forEach(t ->{
			UUID orderUuid = cancellantionQueue.poll();
			if(orderUuid != null){
				jmsCancelOrderProducer.publish(orderUuid);
			}
		});
	}
	
}
