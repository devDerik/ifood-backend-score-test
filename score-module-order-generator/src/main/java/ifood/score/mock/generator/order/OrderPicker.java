package ifood.score.mock.generator.order;

import static ifood.score.mock.generator.RandomishPicker._int;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ifood.score.mock.generator.Picker;
import ifood.score.order.Item;
import ifood.score.order.Order;

public class OrderPicker implements Picker<Order> {

	private Double MAX_ORDER_VALUE = 150.0;
	private Integer MAX_ORDER_ITEMS = 20;

	public Order pick() {

		List<Item> items = pickItems();

		Order order = new Order();
		order.setUuid(UUID.randomUUID());
		order.setRestaurantUuid(UUID.randomUUID());
		order.setCustomerUuid(UUID.randomUUID());
		order.setAddressUuid(UUID.randomUUID());
		order.setConfirmedAt(new Date());
		order.setItems(items);
		return order;
	}

	private List<Item> pickItems() {
		List<Item> items = new LinkedList<>();
		ItemPicker picker = new ItemPicker();
		Item item = picker.pick();
		Integer quantity = Math.min(quantity(item), MAX_ORDER_ITEMS);
		Double price = 0.0;
		Integer i = -1;
		do{
			i++;
			items.add(item);
			item = picker.pick();
			price += item.getQuantity() * item.getMenuUnitPrice().doubleValue();
		}while(i < quantity && quantity < MAX_ORDER_VALUE && price.intValue() % 4 != 0);
		return items;
	}

	private Integer quantity(Item item) {
		Double top = MAX_ORDER_VALUE - (item.getMenuUnitPrice().doubleValue() * item.getQuantity());
		top /= (item.getMenuUnitPrice().doubleValue() * item.getQuantity());
		Integer seed = (int) top.doubleValue();
		if(seed <= 1){
			return seed;
		}
		return _int(1, seed);
	}
	
}
