package com.solvd.micro9.scooters;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class KSQLClientProducer {

    @Bean
    //@Scope("prototype")
    Client ksqlClient() {
        ClientOptions options = ClientOptions.create()
                .setHost("localhost")
                .setPort(8088);
        return Client.create(options);
    }

}
