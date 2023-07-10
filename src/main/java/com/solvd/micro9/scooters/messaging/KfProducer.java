package com.solvd.micro9.scooters.messaging;

public interface KfProducer<K, X> {

    void send(X value);

}
