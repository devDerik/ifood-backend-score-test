package ifood.score;

import ifood.score.messagebus.consumer.*;
import ifood.score.messagebus.producer.JmsCancelOrderProducer;
import ifood.score.messagebus.producer.JmsCheckoutOrderProducer;
import ifood.score.messagebus.producer.MessageProducer;
import ifood.score.repository.CategoryRelevanceRepository;
import ifood.score.repository.CategoryScoreRepository;
import ifood.score.repository.MenuItemRelevanceRepository;
import ifood.score.repository.MenuScoreRepository;
import ifood.score.service.ExpireScoresRunner;
import ifood.score.service.ScoreService;
import ifood.score.service.ScoreServiceImpl;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
public class ApplicationBeansConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String user;

    @Value("${spring.activemq.password}")
    private String password;

    @Value("${checkout.large.order.queue.name}")
    private String largeOrderQueueName;

    @Value("${large.order.number}")
    private Integer largeOrderNumber;

    @Value("${checkout.order.queue.name}")
    private String checkoutOrderQueueName;

    @Value("${cancel.order.queue.name}")
    private String cancelOrderQueueName;

    @Bean
    ScoreService scoreService(CategoryScoreRepository categoryScoreRepository, MenuScoreRepository menuScoreRepository,
                              MenuItemRelevanceRepository menuItemRelevanceRepository,
                              CategoryRelevanceRepository categoryRelevanceRepository, MeterRegistry meterRegistry) {
        return new ScoreServiceImpl(categoryScoreRepository, menuScoreRepository,
                menuItemRelevanceRepository, categoryRelevanceRepository, meterRegistry);
    }

    @Bean
    ExpireScoresRunner expireScoresRunner(ScoreService scoreService) {
        return new ExpireScoresRunner(scoreService);
    }

    // ACTIVE MQ
    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        if (StringUtils.isBlank(user)) {
            final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            activeMQConnectionFactory.setRedeliveryPolicy(getRedeliveryPolicy());
            return activeMQConnectionFactory;
        }

        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(user, password, brokerUrl);
        activeMQConnectionFactory.setRedeliveryPolicy(getRedeliveryPolicy());
        return activeMQConnectionFactory;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsQueueFactory(ConnectionFactory connectionFactory,
                                                          DefaultJmsListenerContainerFactoryConfigurer configurer) {
        final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }

    @Bean
    MessageProducer jmsCheckoutOrderProducer(JmsTemplate jmsTemplate, MeterRegistry meterRegistry) {
        return new JmsCheckoutOrderProducer(jmsTemplate, checkoutOrderQueueName,
                largeOrderQueueName, largeOrderNumber, meterRegistry);
    }

    @Bean
    MessageProducer jmsCancelOrderProducer(JmsTemplate jmsTemplate, MeterRegistry meterRegistry) {
        return new JmsCancelOrderProducer(jmsTemplate, cancelOrderQueueName, meterRegistry);
    }

    @Bean
    MessageConsumer jmsCheckoutOrderConsumer(ScoreService scoreService, MeterRegistry meterRegistry) {
        return new JmsCheckoutOrderConsumer(scoreService, meterRegistry);
    }

    @Bean
    MessageConsumer jmsCheckoutLargeOrderConsumer(ScoreService scoreService, MeterRegistry meterRegistry) {
        return new JmsCheckoutLargeOrderConsumer(scoreService, meterRegistry);
    }

    @Bean
    MessageConsumer jmsCancelOrderConsumer(ScoreService scoreService, MeterRegistry meterRegistry) {
        return new JmsCancelOrderConsumer(scoreService, meterRegistry);
    }

    @Bean
    MessageConsumer jmsDeadLetterQueueConsumer(MeterRegistry meterRegistry) {
        return new JmsDeadLetterQueueConsumer(meterRegistry);
    }

    private RedeliveryPolicy getRedeliveryPolicy() {
        final RedeliveryPolicy policy = new RedeliveryPolicy();
        policy.setInitialRedeliveryDelay(500);
        policy.setBackOffMultiplier(2);
        policy.setUseExponentialBackOff(true);
        policy.setMaximumRedeliveries(3);
        return null;
    }
}
