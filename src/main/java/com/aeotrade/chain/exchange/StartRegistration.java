package com.aeotrade.chain.exchange;


import com.aeotrade.chain.exchange.config.ExchangeInfoConfigProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionFactoryBeanConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.xml.router.XPathRouter;
import org.springframework.integration.xml.selector.XmlValidatingMessageSelector;
import org.springframework.stereotype.Component;
import org.springframework.xml.validation.XmlValidator;
import org.springframework.xml.validation.XmlValidatorFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartRegistration implements SmartLifecycle {

    @NonNull
    private final IntegrationFlowContext flowContext;
    @NonNull
    private final ExchangeInfoConfigProperties exchangeInfoConfigProperties;
    private final ResourceLoader resourceLoader;
    @NonNull
    private final ApplicationContext applicationContext;
    private boolean running = false;
    @Override
    public void start() {
        Map<ExchangeInfoConfigProperties.ExchangeRabbitmqInfo,CachingConnectionFactory> cachingConnectionFactoryMap=new HashMap<>(exchangeInfoConfigProperties.getRabbitmqInfos().size());
        for (ExchangeInfoConfigProperties.ExchangeRabbitmqInfo rabbitmqInfo : exchangeInfoConfigProperties.getRabbitmqInfos()) {
            RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer = this.rabbitConnectionFactoryBeanConfigurer(rabbitmqInfo.getRabbitmq(), this.resourceLoader);
            CachingConnectionFactoryConfigurer cachingConnectionFactoryConfigurer = rabbitConnectionFactoryConfigurer(rabbitmqInfo.getRabbitmq());
            CachingConnectionFactory cachingConnectionFactory = null;
            try {
                cachingConnectionFactory = this.rabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, cachingConnectionFactoryConfigurer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            registryBean(rabbitmqInfo.hashCode()+"_cf",cachingConnectionFactory);
            cachingConnectionFactoryMap.put(rabbitmqInfo,cachingConnectionFactory);
        }
        XPathRouter xPathRouter = this.xPathRouter(cachingConnectionFactoryMap);
        this.registryFlows(cachingConnectionFactoryMap,xPathRouter);
        running=true;
    }

    @Override
    public void stop() {
        flowContext.getRegistry().values().forEach(IntegrationFlowContext.IntegrationFlowRegistration::destroy);
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void registryFlows(Map<ExchangeInfoConfigProperties.ExchangeRabbitmqInfo,CachingConnectionFactory> cachingConnectionFactoryMap,XPathRouter xPathRouter){
        cachingConnectionFactoryMap.forEach((exchangeRabbitmqInfo,cachingConnectionFactory)->{
            SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer(cachingConnectionFactory);
            listenerContainer.setPrefetchCount(1);
            listenerContainer.setQueueNames(exchangeRabbitmqInfo.getReceiveQueue());
            XmlValidator xmlValidator = null;
            try {
                xmlValidator = XmlValidatorFactory.createValidator(this.resourceLoader.getResource("exchange.xsd"), XmlValidatorFactory.SCHEMA_W3C_XML);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            XmlValidatingMessageSelector xmlValidatingMessageSelector=new XmlValidatingMessageSelector(xmlValidator);
            IntegrationFlow integrationFlow =
                    IntegrationFlows.from(Amqp.inboundAdapter(listenerContainer)).filter(xmlValidatingMessageSelector).route(xPathRouter).get();

            this.flowContext.registration(integrationFlow)
                    .register();
        });
    }


    private XPathRouter xPathRouter(Map<ExchangeInfoConfigProperties.ExchangeRabbitmqInfo,CachingConnectionFactory> cachingConnectionFactoryMap){
        XPathRouter xPathRouter=new XPathRouter("/ns:DxpMsg/ns:TransInfo/ns:ReceiverIds/ns:ReceiverId","ns","http://www.chinaport.gov.cn/dxp");
        xPathRouter.setEvaluateAsString(true);
        cachingConnectionFactoryMap.forEach((exchangeRabbitmqInfo, cachingConnectionFactory) -> {
            RabbitTemplateConfigurer rabbitTemplateConfigurer = this.rabbitTemplateConfigurer(exchangeRabbitmqInfo.getRabbitmq());
            RabbitTemplate rabbitTemplate = this.rabbitTemplate(rabbitTemplateConfigurer, cachingConnectionFactory);
            this.registryBean(exchangeRabbitmqInfo.hashCode()+"rt",rabbitTemplate);
            AmqpOutboundEndpoint amqpOutboundEndpoint = Amqp.outboundAdapter(rabbitTemplate).routingKey(exchangeRabbitmqInfo.getSendQueue()).get();
            this.registryBean(amqpOutboundEndpoint.hashCode()+"_amqpoutendpoint",amqpOutboundEndpoint);
            DirectChannel directChannel = MessageChannels.direct(exchangeRabbitmqInfo.hashCode() + "_channel").get();
            this.registryBean(directChannel.hashCode()+"_channel",directChannel);
            xPathRouter.setChannelMapping(exchangeRabbitmqInfo.getUserId(),directChannel.hashCode()+"_channel");
            IntegrationFlow integrationFlow =
                    IntegrationFlows.from(directChannel).handle(amqpOutboundEndpoint).get();
            this.flowContext.registration(integrationFlow)
                    .register();
        });
        this.registryBean("xpathrouter",xPathRouter);
        return xPathRouter;
    }

    private RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        return template;
    }
    private RabbitTemplateConfigurer rabbitTemplateConfigurer(RabbitProperties properties) {
        return new RabbitTemplateConfigurer(properties);
    }

    private RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(RabbitProperties properties, ResourceLoader resourceLoader) {
        return new RabbitConnectionFactoryBeanConfigurer(resourceLoader, properties);
    }

    private CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(RabbitProperties rabbitProperties) {
        return new CachingConnectionFactoryConfigurer(rabbitProperties);
    }

    private CachingConnectionFactory rabbitConnectionFactory(RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer, CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer) throws Exception {
        RabbitConnectionFactoryBean connectionFactoryBean = new RabbitConnectionFactoryBean();
        rabbitConnectionFactoryBeanConfigurer.configure(connectionFactoryBean);
        connectionFactoryBean.afterPropertiesSet();
        com.rabbitmq.client.ConnectionFactory connectionFactory =connectionFactoryBean.getObject();
        CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
        rabbitCachingConnectionFactoryConfigurer.configure(factory);
        return factory;
    }

    private void registryBean(String beanName,Object bean){
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry)((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        BeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition((Class<Object>) bean.getClass(), () -> bean)
                        .getRawBeanDefinition();
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }


}
