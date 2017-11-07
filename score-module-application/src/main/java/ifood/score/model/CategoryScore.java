package ifood.score.model;

import ifood.score.menu.Category;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "categoryScore")
public class CategoryScore {
    @Id
    private final Category category;
    private final BigDecimal score;

    public CategoryScore(Category category, BigDecimal score) {
        this.category = category;
        this.score = score;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getScore() {
        return score;
    }
}
