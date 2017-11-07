package ifood.score.repository;

import ifood.score.model.MenuItemRelevance;
import ifood.score.model.RelevanceStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRelevanceRepository extends MongoRepository<MenuItemRelevance, String> {
    List<MenuItemRelevance> findAllByMenuIdAndAndStatus(UUID menuId, RelevanceStatus status);

    List<MenuItemRelevance> findAllByOrderIdAndStatus(UUID orderId, RelevanceStatus status);

    List<MenuItemRelevance> findAllByConfirmedAtBeforeAndStatus(Date date, RelevanceStatus status);
}
