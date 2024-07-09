package com.example;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

public class MqttPlugin {

  public static void main(String[] args)
  {
    Vertx vertx = Vertx.vertx();
    Verticle mqttVerticle = new MqttVerticle();
    vertx.deployVerticle(mqttVerticle);
  }

}
