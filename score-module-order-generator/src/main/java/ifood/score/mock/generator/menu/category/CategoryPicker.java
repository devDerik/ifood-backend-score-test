package ifood.score.mock.generator.menu.category;

import static ifood.score.mock.generator.RandomishPicker._int;

import java.util.List;

import ifood.score.menu.Category;
import ifood.score.mock.generator.Picker;

public class CategoryPicker implements Picker<Category> {

	public Category pick() {
		List<Category> categories = CategoryRepository.getInstance().getCategories();
		int index = _int(0, categories.size()-1);
		return categories.get(index);
	}
	
}
