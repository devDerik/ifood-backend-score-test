package ifood.score.mock.generator.order;

import static ifood.score.mock.generator.RandomishPicker._int;

import ifood.score.menu.Menu;
import ifood.score.mock.generator.Picker;
import ifood.score.mock.generator.menu.MenuPicker;
import ifood.score.order.Item;
public class ItemPicker implements Picker<Item> {

	private static final MenuPicker MENU_PICKER = new MenuPicker();
	
	public Item pick() {
		Menu menu = MENU_PICKER.pick();
		Integer quantity = quantity(menu.getUnitPrice().intValue());
		
		Item item = new Item();
		item.setQuantity(quantity);
		item.setMenuCategory(menu.getCategory());
		item.setMenuUuid(menu.getUuid());
		item.setMenuUnitPrice(menu.getUnitPrice());
		
		return item;
	}

	private Integer quantity(int seed) {
		int q = 1;
		if(seed % 10 > 7){
			q = _int(1, 4);
		}else if(seed % 10 > 5){
			q = _int(1,3);
		}
		if(seed <= 5){
			q *= 3;
		}
		return q;
	}
}
