package com.example;

import io.vertx.core.Vertx;

public class AmqpPlugin {

  public static void main(String[] args) {
    AmqpVerticle amqpVerticle = new AmqpVerticle();
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(amqpVerticle);
  }

}
