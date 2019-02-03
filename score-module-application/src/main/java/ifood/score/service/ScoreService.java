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

    /**
     * Process the order, calculate and persist the score value for Items and Categories.
     * @param order
     */
    void generateScores(Order order);

    /**
     * Fetches Menu Item Score by the given menu item Id.
     * @param menuIdString
     * @return {@link MenuItemScore}
     * @throws ScoreServiceException
     */
    MenuItemScore findMenuScoreById(String menuIdString) throws ScoreServiceException;

    /**
     * Fetches Category Score by the given category type
     * @param categoryType
     * @return {@link CategoryScore}
     * @throws ScoreServiceException
     */
    CategoryScore findCategoryScoreByType(String categoryType) throws ScoreServiceException;

    /**
     * Fetches the Menu Score by score between an min and max value.
     * @param min
     * @param max
     * @return {@link MenuItemScore}
     * @throws ScoreServiceException
     * @throws ScoreServiceNoContentException
     */
    List<MenuItemScore> findMenuScoreBetweenMinMax(String min, String max)  throws ScoreServiceException, ScoreServiceNoContentException;

    /**
     * Fetches the Category Score by score between an min and max value.
     * @param min
     * @param max
     * @return {@link CategoryScore}
     * @throws ScoreServiceException
     * @throws ScoreServiceNoContentException
     */
    List<CategoryScore> findCategoryScoreBetweenMinMax(String min, String max)  throws ScoreServiceException, ScoreServiceNoContentException;

    /**
     * Cancels all the Scores related to the given Order Id.
     * @param orderId
     */
    void cancelScoresByOrderId(UUID orderId);

    /**
     * Expires all the Scores that exceeds a month already.
     */
    void expireScores();
}
