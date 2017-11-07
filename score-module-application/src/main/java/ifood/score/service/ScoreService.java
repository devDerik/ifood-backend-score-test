package ifood.score.service;

import ifood.score.exception.ScoreServiceException;
import ifood.score.exception.ScoreServiceNoContentException;
import ifood.score.menu.Category;
import ifood.score.model.CategoryScore;
import ifood.score.model.MenuItemScore;
import ifood.score.order.Order;

import java.util.List;
import java.util.UUID;

public interface ScoreService {

    void generateScores(Order order);

    List<CategoryScore> getAllCategoryScore();

    List<MenuItemScore> getAllMenuScore();

    MenuItemScore findMenuScoreById(String menuIdString) throws ScoreServiceException;

    CategoryScore findCategoryScoreByType(String categoryType) throws ScoreServiceException;

    List<MenuItemScore> findMenuScoreBetweenMinMax(String min, String max)  throws ScoreServiceException, ScoreServiceNoContentException;

    List<CategoryScore> findCategoryScoreBetweenMinMax(String min, String max)  throws ScoreServiceException, ScoreServiceNoContentException;

    void cancelScoresByOrderId(UUID orderId);

    void expireScores();
}
