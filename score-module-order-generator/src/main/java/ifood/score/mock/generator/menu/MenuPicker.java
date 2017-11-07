package ifood.score.mock.generator.menu;

import static ifood.score.mock.generator.RandomishPicker._int;

import java.util.List;

import ifood.score.menu.Menu;
import ifood.score.mock.generator.Picker;

public class MenuPicker implements Picker<Menu> {

	public Menu pick() {
		List<Menu> menu = MenuRepository.getInstance().getMenu();
		int index = _int(0, menu.size()-1);
		return menu.get(index);
	}
	
}
