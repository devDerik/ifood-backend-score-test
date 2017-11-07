package ifood.score.repository;

import ifood.score.model.MenuItemScore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface MenuScoreRepository extends MongoRepository<MenuItemScore, UUID> {
    MenuItemScore findMenuScoreByMenuId(UUID menuId);

    List<MenuItemScore> findByScoreBetweenOrderByMenuId(BigDecimal min, BigDecimal max);
}
