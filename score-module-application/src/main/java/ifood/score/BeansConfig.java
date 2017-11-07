package ifood.score;

import ifood.score.consumer.JmsCancelOrderConsumer;
import ifood.score.consumer.JmsCheckoutOrderConsumer;
import ifood.score.consumer.MessageConsumer;
import ifood.score.producer.JmsCancelOrderProducer;
import ifood.score.producer.JmsCheckoutOrderProducer;
import ifood.score.producer.MessageProducer;
import ifood.score.repository.CategoryRelevanceRepository;
import ifood.score.repository.CategoryScoreRepository;
import ifood.score.repository.MenuItemRelevanceRepository;
import ifood.score.repository.MenuScoreRepository;
import ifood.score.service.ExpireScoresRunner;
import ifood.score.service.ScoreService;
import ifood.score.service.ScoreServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class BeansConfig {

    @Bean
    ScoreService scoreService(CategoryScoreRepository categoryScoreRepository, MenuScoreRepository menuScoreRepository,
                              MenuItemRelevanceRepository menuItemRelevanceRepository,
                              CategoryRelevanceRepository categoryRelevanceRepository) {
        return new ScoreServiceImpl(categoryScoreRepository, menuScoreRepository,
                menuItemRelevanceRepository, categoryRelevanceRepository);
    }

    @Bean
    ExpireScoresRunner expireScoresRunner(ScoreService scoreService) {
        return new ExpireScoresRunner(scoreService);
    }

    @Bean
    MessageProducer jmsCheckoutOrderProducer(JmsTemplate jmsTemplate) {
        return new JmsCheckoutOrderProducer(jmsTemplate, "checkout-order");
    }

    @Bean
    MessageProducer jmsCancelOrderProducer(JmsTemplate jmsTemplate) {
        return new JmsCancelOrderProducer(jmsTemplate, "cancel-order");
    }

    @Bean
    MessageConsumer jmsCheckoutOrderConsumer(ScoreService scoreService) {
        return new JmsCheckoutOrderConsumer(scoreService);
    }

    @Bean
    MessageConsumer jmsCancelOrderConsumer(ScoreService scoreService) {
        return new JmsCancelOrderConsumer(scoreService);
    }
}
