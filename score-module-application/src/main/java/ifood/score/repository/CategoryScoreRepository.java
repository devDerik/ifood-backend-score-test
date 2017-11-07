package ifood.score.repository;

import ifood.score.menu.Category;
import ifood.score.model.CategoryScore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CategoryScoreRepository extends MongoRepository<CategoryScore, Category> {
    CategoryScore findCategoryScoreByCategory(Category category);

    List<CategoryScore> findByScoreBetweenOrderByCategory(BigDecimal min, BigDecimal max);
}
