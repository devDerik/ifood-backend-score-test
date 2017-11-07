package ifood.score.mock.generator.menu.category;

import java.util.Arrays;
import java.util.List;

import ifood.score.menu.Category;

public class CategoryRepository {
	private static CategoryRepository instance;
	private List<Category> categories;
	
	private CategoryRepository(List<Category> categories){
		this.categories = categories;
	}
	
	public static CategoryRepository getInstance(){
		if(instance == null){
			instance = new CategoryRepository(buildCategories());
		}
		return instance;
	}
	
	private static List<Category> buildCategories(){
		return Arrays.asList(Category.values());
	}

	public List<Category> getCategories() {
		return categories;
	}
}
