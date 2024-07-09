package com.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

public class ProductVerticle extends AbstractVerticle {


  private AtomicInteger num = new AtomicInteger(0);

  private EventBus eventBus;
  @Override
  public void start() throws Exception {
    eventBus = vertx.eventBus();
    vertx.setPeriodic(1000, id -> {
      DeliveryOptions deliveryOptions = new DeliveryOptions();
      deliveryOptions.addHeader("header", "value");

      String message = 456+ "_success" + num.addAndGet(1);
      System.out.println("发送消息 "+ message);
      eventBus.publish("test_address", message,deliveryOptions);
    });
  }

  /**
   * publish： 发布订阅模式可将消息传递给注册在同一地址上的所有消费者
   * send: 点对点模式，消息传递给注册在同一地址上的其中一个消费者（不严格的轮询算法）
   * request: 点对点模式请求响应模式，消费者可将处理的结果响应给生产者
   */
  public void publish(){
    DeliveryOptions deliveryOptions = new DeliveryOptions();
    deliveryOptions.addHeader("header", "value");
    eventBus.publish("test_address", "success",deliveryOptions);
  }
}
