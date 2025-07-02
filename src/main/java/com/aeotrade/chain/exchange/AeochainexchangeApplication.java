package com.aeotrade.chain.exchange;

import com.aeotrade.chain.exchange.config.ExchangeInfoConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude =  {org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class})
@EnableConfigurationProperties(ExchangeInfoConfigProperties.class)
public class AeochainexchangeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AeochainexchangeApplication.class, args);
    }

}
