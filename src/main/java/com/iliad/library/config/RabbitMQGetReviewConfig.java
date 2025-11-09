package com.iliad.library.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQGetReviewConfig {

    public static final String QUEUE_NAME = "reviewQueue";
    public static final String EXCHANGE_NAME = "reviewExchange";
    public static final String ROUTING_KEY = "reviewRoutingKey";

    @Bean
    public Queue queueReview() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public TopicExchange exchangeReview() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingReview(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // json converter per serializzazione messaggi
    @Bean
    public MessageConverter jsonMessageConverterReview() {
        return new Jackson2JsonMessageConverter();
    }
}
