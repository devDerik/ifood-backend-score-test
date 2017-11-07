package ifood.score.repository;

import ifood.score.menu.Category;
import ifood.score.model.CategoryRelevance;
import ifood.score.model.MenuItemRelevance;
import ifood.score.model.RelevanceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRelevanceRepository extends MongoRepository<CategoryRelevance, String> {
    List<CategoryRelevance> findAllByCategoryAndStatus(Category category, RelevanceStatus status);

    List<CategoryRelevance> findAllByOrderIdAndStatus(UUID orderId, RelevanceStatus status);

    List<CategoryRelevance> findAllByConfirmedAtBeforeAndStatus(Date date, RelevanceStatus status);
}