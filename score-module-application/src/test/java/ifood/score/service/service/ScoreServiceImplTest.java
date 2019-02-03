package ifood.score.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ifood.score.service.ScoreServiceImpl;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.stats.hist.Histogram;
import io.micrometer.core.instrument.stats.quantile.Quantiles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.ToDoubleFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class ScoreServiceImplTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Captor
    private ArgumentCaptor<List<CategoryScore>> categoryScoreCaptor;
    @Captor
    private ArgumentCaptor<List<MenuItemScore>> menuScoreCaptor;
    @Captor
    private ArgumentCaptor<List<MenuItemRelevance>> menuItemRelevanceCaptor;
    @Captor
    private ArgumentCaptor<List<CategoryRelevance>> categoryRelevanceCaptor;
    private ScoreServiceImpl scoreService;
    private MenuScoreRepository menuScoreRepositoryMock;
    private CategoryScoreRepository categoryScoreRepositoryMock;
    private MenuItemRelevanceRepository menuItemRelevanceRepositoryMock;
    private CategoryRelevanceRepository categoryRelevanceRepositoryMock;

    @Before
    public void setUp() {
        this.menuScoreRepositoryMock = mock(MenuScoreRepository.class);
        this.categoryScoreRepositoryMock = mock(CategoryScoreRepository.class);
        this.menuItemRelevanceRepositoryMock = mock(MenuItemRelevanceRepository.class);
        this.categoryRelevanceRepositoryMock = mock(CategoryRelevanceRepository.class);
        scoreService = new ScoreServiceImpl(categoryScoreRepositoryMock, menuScoreRepositoryMock,
                menuItemRelevanceRepositoryMock, categoryRelevanceRepositoryMock, new MeterRegistryMock());
    }

    @Test
    public void processOrderTest() throws Exception {
        final byte[] jsonBytes = Files.readAllBytes(
                Paths.get(ScoreServiceImplTest.class.getClassLoader().getResource("order.json").toURI()));

        final Order order = OBJECT_MAPPER.readValue(jsonBytes, Order.class);
        scoreService.generateScores(order);
        verify(categoryScoreRepositoryMock).saveAll(categoryScoreCaptor.capture());
        final List<CategoryScore> repositoryCategoryScores = categoryScoreCaptor.getValue();
        verify(menuScoreRepositoryMock).saveAll(menuScoreCaptor.capture());
        final List<MenuItemScore> repositoryMenuItemScore = menuScoreCaptor.getValue();

        assertCategoryScores(repositoryCategoryScores);
        assertMenuScores(order.getItems(), repositoryMenuItemScore);
    }

    @Test(expected = ScoreServiceException.class)
    public void findMenuScoreByIdTest_invalidMenuId() throws ScoreServiceException {
        scoreService.findMenuScoreById("invalid");
    }

    @Test(expected = ScoreServiceException.class)
    public void findMenuScoreByIdTest_blankMenuId() throws ScoreServiceException {
        scoreService.findMenuScoreById("");
    }

    @Test(expected = ScoreServiceException.class)
    public void findMenuScoreByIdTest_nullMenuId() throws ScoreServiceException {
        scoreService.findMenuScoreById(null);
    }

    @Test(expected = ScoreServiceException.class)
    public void findCategoryScoreByTypeTest_invalidCategory() throws ScoreServiceException {
        scoreService.findCategoryScoreByType("invalid");
    }

    @Test(expected = ScoreServiceException.class)
    public void findCategoryScoreByTypeTest_blankCategory() throws ScoreServiceException {
        scoreService.findCategoryScoreByType("");
    }

    @Test(expected = ScoreServiceException.class)
    public void findCategoryScoreByTypeTest_nullCategory() throws ScoreServiceException {
        scoreService.findCategoryScoreByType(null);
    }

    @Test(expected = ScoreServiceException.class)
    public void findMenuScoreBetweenMinMaxTest_invalidMin() throws Exception {
        scoreService.findMenuScoreBetweenMinMax("invalid", "1");
    }

    @Test(expected = ScoreServiceException.class)
    public void findMenuScoreBetweenMinMaxTest_blankMin() throws Exception {
        scoreService.findMenuScoreBetweenMinMax("", "1");
    }

    @Test(expected = ScoreServiceException.class)
    public void findMenuScoreBetweenMinMaxTest_nullMin() throws Exception {
        scoreService.findMenuScoreBetweenMinMax(null, "1");
    }

    @Test(expected = ScoreServiceException.class)
    public void findMenuScoreBetweenMinMaxTest_invalidMax() throws Exception {
        scoreService.findMenuScoreBetweenMinMax("1", "invalid");
    }

    @Test(expected = ScoreServiceException.class)
    public void findMenuScoreBetweenMinMaxTest_blankMax() throws Exception {
        scoreService.findMenuScoreBetweenMinMax("1", "");
    }

    @Test(expected = ScoreServiceException.class)
    public void findMenuScoreBetweenMinMaxTest_nullMax() throws Exception {
        scoreService.findMenuScoreBetweenMinMax("1", null);
    }

    @Test(expected = ScoreServiceNoContentException.class)
    public void findMenuScoreBetweenMinMaxTest_noContent() throws Exception {
        when(menuScoreRepositoryMock.findByScoreBetweenOrderByMenuId(any(), any())).thenReturn(Collections.EMPTY_LIST);
        scoreService.findMenuScoreBetweenMinMax("1", "10");
    }

    @Test(expected = ScoreServiceException.class)
    public void findCategoryScoreBetweenMinMaxTest_invalidMin() throws Exception {
        scoreService.findCategoryScoreBetweenMinMax("invalid", "1");
    }

    @Test(expected = ScoreServiceException.class)
    public void findCategoryScoreBetweenMinMaxTest_blankMin() throws Exception {
        scoreService.findCategoryScoreBetweenMinMax("", "1");
    }

    @Test(expected = ScoreServiceException.class)
    public void findCategoryScoreBetweenMinMaxTest_nullMin() throws Exception {
        scoreService.findCategoryScoreBetweenMinMax(null, "1");
    }

    @Test(expected = ScoreServiceException.class)
    public void findCategoryScoreBetweenMinMaxTest_invalidMax() throws Exception {
        scoreService.findCategoryScoreBetweenMinMax("1", "invalid");
    }

    @Test(expected = ScoreServiceException.class)
    public void findCategoryScoreBetweenMinMaxTest_blankMax() throws Exception {
        scoreService.findCategoryScoreBetweenMinMax("1", "");
    }

    @Test(expected = ScoreServiceException.class)
    public void findCategoryScoreBetweenMinMaxTest_nullMax() throws Exception {
        scoreService.findCategoryScoreBetweenMinMax("1", null);
    }

    @Test(expected = ScoreServiceNoContentException.class)
    public void findCategoryScoreBetweenMinMaxTest_noContent() throws Exception {
        when(categoryScoreRepositoryMock.findByScoreBetweenOrderByCategory(any(), any())).thenReturn(Collections.EMPTY_LIST);
        scoreService.findMenuScoreBetweenMinMax("1", "10");
    }

    @Test
    public void cancelScoresByOrderIdTest() {
        final UUID orderId = UUID.randomUUID();
        when(menuItemRelevanceRepositoryMock.findAllByOrderIdAndStatus(orderId, RelevanceStatus.ACTIVE))
                .thenReturn(Collections.singletonList(new MenuItemRelevance(orderId, new Date(), UUID.randomUUID(), BigDecimal.TEN)));
        when(categoryRelevanceRepositoryMock.findAllByOrderIdAndStatus(orderId, RelevanceStatus.ACTIVE))
                .thenReturn(Collections.singletonList(new CategoryRelevance(orderId, new Date(), Category.BRAZILIAN, BigDecimal.TEN)));

        scoreService.cancelScoresByOrderId(orderId);
        verify(menuItemRelevanceRepositoryMock).saveAll(menuItemRelevanceCaptor.capture());
        menuItemRelevanceCaptor.getValue().forEach(item -> assertEquals(RelevanceStatus.CANCELED, item.getStatus()));

        verify(categoryRelevanceRepositoryMock).saveAll(categoryRelevanceCaptor.capture());
        categoryRelevanceCaptor.getValue().forEach(category -> assertEquals(RelevanceStatus.CANCELED, category.getStatus()));
    }

    @Test
    public void expireScoresTest() {
        when(menuItemRelevanceRepositoryMock.findAllByConfirmedAtBeforeAndStatus(any(Date.class), any(RelevanceStatus.class)))
                .thenReturn(Collections.singletonList(new MenuItemRelevance(UUID.randomUUID(), new Date(), UUID.randomUUID(), BigDecimal.TEN)));
        when(categoryRelevanceRepositoryMock.findAllByConfirmedAtBeforeAndStatus(any(Date.class), any(RelevanceStatus.class)))
                .thenReturn(Collections.singletonList(new CategoryRelevance(UUID.randomUUID(), new Date(), Category.BRAZILIAN, BigDecimal.TEN)));

        scoreService.expireScores();
        verify(menuItemRelevanceRepositoryMock).saveAll(menuItemRelevanceCaptor.capture());
        menuItemRelevanceCaptor.getValue().forEach(item -> assertEquals(RelevanceStatus.EXPIRED, item.getStatus()));

        verify(categoryRelevanceRepositoryMock).saveAll(categoryRelevanceCaptor.capture());
        categoryRelevanceCaptor.getValue().forEach(category -> assertEquals(RelevanceStatus.EXPIRED, category.getStatus()));
    }

    private void assertCategoryScores(List<CategoryScore> repositoryCategoryScores) {
        assertEquals(2, repositoryCategoryScores.size());
        repositoryCategoryScores.forEach(score -> {
            if (Category.VEGAN.equals(score.getCategory())) {
                assertEquals(new BigDecimal("30.512857683"), score.getScore());
            } else if (Category.PIZZA.equals(score.getCategory())) {
                assertEquals(new BigDecimal("58.13183589"), score.getScore());
            } else {
                fail("Unexpected category");
            }
        });
    }

    private void assertMenuScores(List<Item> expectedItems, List<MenuItemScore> repositoryMenuItemScore) {
        assertEquals(3, repositoryMenuItemScore.size());
        repositoryMenuItemScore.forEach(menuScore -> {
            if (expectedItems.get(0).getMenuUuid().equals(menuScore.getMenuId())) {
                assertEquals(new BigDecimal("29.942473579"), menuScore.getScore());
            } else if (expectedItems.get(1).getMenuUuid().equals(menuScore.getMenuId())) {
                assertEquals(new BigDecimal("30.512857683"), menuScore.getScore());
            } else if (expectedItems.get(2).getMenuUuid().equals(menuScore.getMenuId())) {
                assertEquals(new BigDecimal("28.162092394"), menuScore.getScore());
            } else {
                fail("Unexpected menu");
            }
        });
    }

    private static class MeterRegistryMock implements MeterRegistry {

        private Clock clock;
        private Histogram.Summation summation;

        @Override
        public Collection<Meter> getMeters() {
            return null;
        }

        @Override
        public Config config() {
            return null;
        }

        @Override
        public Search find(String s) {
            return null;
        }

        @Override
        public Counter counter(Meter.Id id) {
            return mock(Counter.class);
        }

        @Override
        public DistributionSummary summary(Meter.Id id, Histogram.Builder<?> builder, Quantiles quantiles) {
            return null;
        }

        @Override
        public Timer timer(Meter.Id id, Histogram.Builder<?> builder, Quantiles quantiles) {
            return mock(Timer.class);
        }

        @Override
        public More more() {
            return null;
        }

        @Override
        public Meter register(Meter.Id id, Meter.Type type, Iterable<Measurement> iterable) {
            return null;
        }

        @Override
        public <T> Gauge gauge(Meter.Id id, T t, ToDoubleFunction<T> toDoubleFunction) {
            return null;
        }

        @Override
        public Meter.Id createId(String s, Iterable<Tag> iterable, String s1, String s2) {
            return null;
        }
    }
}
