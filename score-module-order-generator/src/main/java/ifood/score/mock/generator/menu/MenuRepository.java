package ifood.score.mock.generator.menu;

import static ifood.score.mock.generator.RandomishPicker._price;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ifood.score.menu.Menu;
import ifood.score.mock.generator.menu.category.CategoryPicker;

public class MenuRepository {
	private static MenuRepository instance;
	private List<Menu> menu;

	private MenuRepository(List<Menu> menu){
		this.menu = menu;
	}

	public static MenuRepository getInstance(){
		if(instance == null){
			instance = new MenuRepository(buildMenu());
		}
		return instance;
	}

	private static List<Menu> buildMenu(){
		CategoryPicker picker = new CategoryPicker();
		return IntStream.range(0, 600).mapToObj(t -> {
			Menu menu = new Menu();
			menu.setCategory(picker.pick());
			menu.setUuid(UUID.randomUUID());
			menu.setUnitPrice(_price());
			return menu;
		}).collect(Collectors.toList());
	}

	public List<Menu> getMenu() {
		return menu;
	}
}
