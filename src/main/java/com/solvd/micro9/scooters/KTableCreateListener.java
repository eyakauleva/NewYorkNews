package com.solvd.micro9.scooters;

import com.solvd.micro9.scooters.messaging.KStreamConfig;
import io.confluent.ksql.api.client.Client;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KTableCreateListener implements ApplicationListener<ContextRefreshedEvent> {

    private final Client ksqlClient;

    @Override
    @SneakyThrows
    public void onApplicationEvent(@NonNull final ContextRefreshedEvent event) {
        String streamCreation = """
                CREATE STREAM IF NOT EXISTS users_income (
                    user_id VARCHAR KEY,
                    income DECIMAL(12, 2)
                ) WITH (
                    KAFKA_TOPIC = '"""
                + KStreamConfig.USERS_SINK_TOPIC
                + """
                    ',
                    VALUE_FORMAT = 'DELIMITED'
                );
                """;
//        String viewTableCreation = """
//                CREATE TABLE IF NOT EXISTS users_income_view (
//                    id BIGINT PRIMARY KEY,
//                    user_id VARCHAR,
//                    income DECIMAL(12, 2)
//                ) WITH (
//                    kafka_topic='users-income',
//                    value_format='JSON'
//                );
//                """;
        String viewTableCreation = """
                CREATE TABLE IF NOT EXISTS users_income_view AS
                  SELECT
                    user_id,
                    AVG(CAST(income AS DOUBLE)) AS average_income
                  FROM users_income
                  GROUP BY user_id
                  EMIT CHANGES;
                """;
        ksqlClient.executeStatement(streamCreation).get();
        ksqlClient.executeStatement(viewTableCreation).get(); //TODO not working data input from stream to table
                                                                                    // (convertion error)
    }
}
