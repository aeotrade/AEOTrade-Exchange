FROM hub.aeotrade.com/open-source/openjdk:17.0-jdk-slim
WORKDIR /usr/share/aeochainexchange

COPY ./target/aeochainexchange.jar .

RUN useradd -m aeotrade -s /bin/bash && \
    chown -R aeotrade:aeotrade /usr/share/aeochainexchange && \
    chmod +x /usr/share/aeochainexchange/aeochainexchange.jar

USER aeotrade
EXPOSE 8082
CMD ["java", "-jar", "aeochainexchange.jar"]