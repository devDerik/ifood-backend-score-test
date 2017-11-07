package ifood.score.service;

import ifood.score.exception.ScoreServiceException;
import ifood.score.exception.ScoreServiceNoContentException;
import ifood.score.menu.Category;
import ifood.score.model.*;
import ifood.score.order.Item;
import ifood.score.order.Order;
import ifood.score.repository.CategoryRelevanceRepository;
import ifood.score.repository.CategoryScoreRepository;
import ifood.score.repository.MenuItemRelevanceRepository;
import ifood.score.repository.MenuScoreRepository;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ScoreServiceImpl implements ScoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreServiceImpl.class);
    private static final int BIG_DECIMAL_SCALE = 9;
    private static final int ONE_MONTH = 1;
    private final CategoryScoreRepository categoryScoreRepository;
    private final MenuScoreRepository menuScoreRepository;
    private final MenuItemRelevanceRepository menuItemRelevanceRepository;
    private final CategoryRelevanceRepository categoryRelevanceRepository;

    public ScoreServiceImpl(CategoryScoreRepository categoryScoreRepository, MenuScoreRepository menuScoreRepository,
                            MenuItemRelevanceRepository menuItemRelevanceRepository,
                            CategoryRelevanceRepository categoryRelevanceRepository) {
        this.categoryScoreRepository = categoryScoreRepository;
        this.menuScoreRepository = menuScoreRepository;
        this.menuItemRelevanceRepository = menuItemRelevanceRepository;
        this.categoryRelevanceRepository = categoryRelevanceRepository;
    }

    @Override
    public void generateScores(Order order) {
        final List<Item> orderItems = order.getItems();
        final BigDecimal totalItemQuantity =
                BigDecimal.valueOf(orderItems.stream().mapToInt(Item::getQuantity).sum());
        final BigDecimal totalOrderPrice = BigDecimal.valueOf(
                orderItems.stream().mapToDouble(item -> item.getQuantity() * item.getMenuUnitPrice().doubleValue()).sum());
        LOGGER.debug("Total item quantity: {}", totalItemQuantity);
        LOGGER.debug("Total order price: {}", totalOrderPrice);

        categoryScores(order, orderItems, totalItemQuantity, totalOrderPrice);
        menuScores(order, orderItems, totalItemQuantity, totalOrderPrice);
    }

    @Override
    public MenuItemScore findMenuScoreById(String menuIdString) throws ScoreServiceException {
        final UUID menuId;
        try {
            menuId = UUID.fromString(menuIdString);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ScoreServiceException("Invalid menu Id: " + menuIdString, e);
        }

        final MenuItemScore menuScore = menuScoreRepository.findMenuScoreByMenuId(menuId);
        if (menuScore == null) {
            throw new ScoreServiceException("Could not find Menu Item Score of Id: " + menuIdString);
        }
        return menuScore;
    }

    @Override
    public CategoryScore findCategoryScoreByType(String categoryType) throws ScoreServiceException {
        Category category;
        try {
            category = Category.valueOf(StringUtils.isBlank(categoryType) ? Category.OTHER.name() : categoryType.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid category type: " + categoryType, e);
            category = Category.OTHER;
        }

        if (Category.OTHER.equals(category)) {
            throw new ScoreServiceException("Invalid category type: " + categoryType
                    + ". Available categories are: " + Arrays.toString(Category.values()));
        }

        return categoryScoreRepository.findCategoryScoreByCategory(category);
    }

    @Override
    public List<MenuItemScore> findMenuScoreBetweenMinMax(String minString, String maxString) throws ScoreServiceException, ScoreServiceNoContentException {
        final BigDecimal min = validateAndGetBigDecimalParam(minString);
        final BigDecimal max = validateAndGetBigDecimalParam(maxString);

        final List<MenuItemScore> menuScores = menuScoreRepository.findByScoreBetweenOrderByMenuId(min, max);
        if (CollectionUtils.isEmpty(menuScores)) {
            throw new ScoreServiceNoContentException();
        }

        return menuScores;
    }

    @Override
    public List<CategoryScore> findCategoryScoreBetweenMinMax(String minString, String maxString) throws ScoreServiceException, ScoreServiceNoContentException {
        final BigDecimal min = validateAndGetBigDecimalParam(minString);
        final BigDecimal max = validateAndGetBigDecimalParam(maxString);
        final List<CategoryScore> categoryScores = categoryScoreRepository.findByScoreBetweenOrderByCategory(min, max);

        if (CollectionUtils.isEmpty(categoryScores)) {
            throw new ScoreServiceNoContentException();
        }

        return categoryScores;
    }

    @Override
    public List<CategoryScore> getAllCategoryScore() {
        return categoryScoreRepository.findAll();
    }

    @Override
    public List<MenuItemScore> getAllMenuScore() {
        return menuScoreRepository.findAll();
    }

    @Override
    public void cancelScoresByOrderId(UUID orderId) {
        final List<MenuItemRelevance> itemsToCancel = menuItemRelevanceRepository.
                findAllByOrderIdAndStatus(orderId, RelevanceStatus.ACTIVE);
        updateMenuItemsStatus(itemsToCancel, RelevanceStatus.CANCELED);

        final List<CategoryRelevance> categoriesToCancel = categoryRelevanceRepository.
                findAllByOrderIdAndStatus(orderId, RelevanceStatus.ACTIVE);
        updateCategoriesStatus(categoriesToCancel, RelevanceStatus.CANCELED);
    }

    @Override
    public void expireScores() {
        final Date expireDate = DateTime.now().minusMonths(ONE_MONTH).toDate();
        final List<MenuItemRelevance> itemsToExpire = menuItemRelevanceRepository
                .findAllByConfirmedAtBeforeAndStatus(expireDate, RelevanceStatus.ACTIVE);
        updateMenuItemsStatus(itemsToExpire, RelevanceStatus.EXPIRED);

        final List<CategoryRelevance> categoriesToExpire = categoryRelevanceRepository
                .findAllByConfirmedAtBeforeAndStatus(expireDate, RelevanceStatus.ACTIVE);
        updateCategoriesStatus(categoriesToExpire, RelevanceStatus.EXPIRED);
    }

    private void updateCategoriesStatus(List<CategoryRelevance> categoriesToUpdate, RelevanceStatus status) {
        if (!CollectionUtils.isEmpty(categoriesToUpdate)) {
            categoriesToUpdate.forEach(category -> category.setStatus(status));
            categoryRelevanceRepository.saveAll(categoriesToUpdate);
        }
    }

    private void updateMenuItemsStatus(List<MenuItemRelevance> itemsToUpdate, RelevanceStatus status) {
        if (!CollectionUtils.isEmpty(itemsToUpdate)) {
            itemsToUpdate.forEach(item -> item.setStatus(status));
            menuItemRelevanceRepository.saveAll(itemsToUpdate);
        }
    }

    private void menuScores(Order order, List<Item> orderItems, BigDecimal totalItemQuantity, BigDecimal totalOrderPrice) {
        final Map<UUID, List<Item>> menuItems = orderItems.stream().collect(Collectors.groupingBy(Item::getMenuUuid));
        final List<MenuItemRelevance> itemsRelevance =
                buildMenuItemRelevance(order, totalItemQuantity, totalOrderPrice, menuItems);

        menuItemRelevanceRepository.saveAll(itemsRelevance);
        menuScoreRepository.saveAll(buildMenuItemScores(itemsRelevance));
    }

    private List<MenuItemScore> buildMenuItemScores(List<MenuItemRelevance> itemsRelevance) {
        final List<MenuItemScore> menuItemScores = new ArrayList<>();
        itemsRelevance.forEach(itemRelevance -> {
            final List<MenuItemRelevance> itemsOfMenu = menuItemRelevanceRepository
                    .findAllByMenuIdAndAndStatus(itemRelevance.getMenuId(), RelevanceStatus.ACTIVE);
            final Double score = itemsOfMenu.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.averagingDouble(item -> item.getRelevance().doubleValue()),
                            average -> average.compareTo(Double.valueOf("0")) > 0 ? average
                                    : itemRelevance.getRelevance().doubleValue()));
            menuItemScores.add(new MenuItemScore(itemRelevance.getMenuId(), BigDecimal.valueOf(score)));
        });
        return menuItemScores;
    }

    private List<MenuItemRelevance> buildMenuItemRelevance(Order order, BigDecimal totalItemQuantity,
                                                           BigDecimal totalOrderPrice,
                                                           Map<UUID, List<Item>> menuItems) {
        final List<MenuItemRelevance> itemsRelevance = new ArrayList<>();
        menuItems.forEach((menuId, items) -> {
            final BigDecimal itemQuantity = BigDecimal.valueOf(
                    items.stream().mapToDouble(Item::getQuantity).sum());
            final BigDecimal totalItemPrice = BigDecimal.valueOf(items.stream()
                    .mapToDouble(item -> item.getQuantity() * item.getMenuUnitPrice().doubleValue())
                    .sum());
            final BigDecimal menuItemRelevance = calculateRelevance(calculateIq(itemQuantity, totalItemQuantity),
                    calculateIp(totalItemPrice, totalOrderPrice));
            itemsRelevance.add(new MenuItemRelevance(order.getUuid(), order.getConfirmedAt(), menuId, menuItemRelevance));
        });
        return itemsRelevance;
    }

    private void categoryScores(Order order, List<Item> orderItems, BigDecimal totalItemQuantity, BigDecimal totalOrderPrice) {
        final Map<Category, List<Item>> itemsPerCategory =
                orderItems.stream().collect(Collectors.groupingBy(Item::getMenuCategory));
        final List<CategoryRelevance> categoryRelevance = buildCategoryRelevance(order, totalItemQuantity,
                totalOrderPrice, itemsPerCategory);

        categoryRelevanceRepository.saveAll(categoryRelevance);
        categoryScoreRepository.saveAll(buildCategoryScores(categoryRelevance));
    }

    private List<CategoryRelevance> buildCategoryRelevance(Order order, BigDecimal totalItemQuantity,
                                                           BigDecimal totalOrderPrice, Map<Category,
                                                            List<Item>> itemsPerCategory) {
        final List<CategoryRelevance> categoryRelevance = new ArrayList<>();
        itemsPerCategory.forEach((category, itemsOfCategory) -> {
            final BigDecimal categoryQuantity = BigDecimal.valueOf(
                    itemsOfCategory.stream().mapToDouble(Item::getQuantity).sum());
            final BigDecimal totalCategoryPrice = BigDecimal.valueOf(itemsOfCategory.stream()
                    .mapToDouble(item -> item.getQuantity() * item.getMenuUnitPrice().doubleValue())
                    .sum());
            categoryRelevance.add(new CategoryRelevance(order.getUuid(), order.getConfirmedAt(), category,
                    calculateRelevance(calculateIq(categoryQuantity, totalItemQuantity),
                    calculateIp(totalCategoryPrice, totalOrderPrice))));
        });
        return categoryRelevance;
    }

    private List<CategoryScore> buildCategoryScores(List<CategoryRelevance> categoryRelevance) {
        final List<CategoryScore> categoryScores = new ArrayList<>();
        categoryRelevance.forEach(catRelevance -> {
            final List<CategoryRelevance> itemsOfCategory = categoryRelevanceRepository
                    .findAllByCategoryAndStatus(catRelevance.getCategory(), RelevanceStatus.ACTIVE);
            final Double score = itemsOfCategory.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.averagingDouble(item -> item.getRelevance().doubleValue()),
                            average -> average.compareTo(Double.valueOf("0")) > 0 ? average
                                    : catRelevance.getRelevance().doubleValue()));
            categoryScores.add(new CategoryScore(catRelevance.getCategory(), BigDecimal.valueOf(score)));
        });
        return categoryScores;
    }

    private BigDecimal calculateIq(BigDecimal itemQuantity, BigDecimal totalItemQuantity) {
        final BigDecimal iQ = divide(itemQuantity, totalItemQuantity);
        LOGGER.debug("IQ value: {}", iQ);
        return iQ;
    }

    private BigDecimal calculateIp(BigDecimal totalItemPrice, BigDecimal totalOrderPrice) {
        final BigDecimal iP = divide(totalItemPrice, totalOrderPrice);
        LOGGER.debug("IP value: {}", iP);
        return iP;
    }

    private BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, BIG_DECIMAL_SCALE, RoundingMode.HALF_UP);

    }

    //  Relevance is calculated using the formula:
    //  Relevance = SQRT(IQ*IP*10000)
    //  Whereas:
    //  IQ = (Menu Item Quantity in Order)/(Total Items Quantity in Order)
    //  IP = (Total Menu Item Price in Order)/(Total Order Price)
    private BigDecimal calculateRelevance(BigDecimal iQ, BigDecimal iP) {
        final BigDecimal relevance = BigDecimal.valueOf(
                Math.sqrt(iQ.multiply(iP).multiply(BigDecimal.valueOf(10000)).doubleValue()))
                .setScale(BIG_DECIMAL_SCALE, RoundingMode.HALF_UP);
        LOGGER.debug("Relevance value: {}", relevance);
        return relevance;
    }

    private BigDecimal validateAndGetBigDecimalParam(String param) throws ScoreServiceException {
        final BigDecimal bigDecimal;
        try {
            bigDecimal = new BigDecimal(param);
        } catch (NumberFormatException | NullPointerException e) {
            throw new ScoreServiceException(param + " is an invalid value for above/below parameter.", e);
        }
        return bigDecimal;
    }
}
