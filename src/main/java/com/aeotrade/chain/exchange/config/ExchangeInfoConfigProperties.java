package com.aeotrade.chain.exchange.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties(prefix = "aeochain.exchange")
@Data
@Validated
public class ExchangeInfoConfigProperties {

    @NestedConfigurationProperty
    private List<ExchangeRabbitmqInfo> rabbitmqInfos;

    @Data
    public static class ExchangeRabbitmqInfo{
        @NotEmpty
        private String userId;
        @NotEmpty
        private String receiveQueue;
        @NotEmpty
        private String sendQueue;
        @NotNull
        private RabbitProperties rabbitmq;
    }
}
