package com.example;

import io.vertx.amqp.AmqpClient;
import io.vertx.amqp.AmqpClientOptions;
import io.vertx.amqp.AmqpConnection;
import io.vertx.amqp.AmqpReceiver;
import io.vertx.amqp.AmqpReceiverOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonClientOptions;
import io.vertx.proton.ProtonConnection;

import java.util.concurrent.TimeUnit;

public class AmqpVerticle extends AbstractVerticle implements Handler<AsyncResult<AmqpReceiver>> {

  private AmqpConnection amqpConnection;
  // private static AmqpConfig amqpConfig  = new AmqpConfig();

  //
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    AmqpConfig amqpConfig  = new AmqpConfig();
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("host",amqpConfig.getHost());
    jsonObject.put("port",amqpConfig.getPort());
    jsonObject.put("username",amqpConfig.getUser());
    jsonObject.put("password",amqpConfig.getPassword());
    jsonObject.put("idle_timeout",30000);
    AmqpClientOptions options = new AmqpClientOptions(jsonObject);
      // .setHost(amqpConfig.getHost())
      // .setPort(amqpConfig.getPort())
      // .setUsername(amqpConfig.getUser())
      // .setPassword(amqpConfig.getPassword())
      // .setReconnectAttempts(10)
      // .setIdleTimeout(300)
      // .setReconnectInterval(30000)
      // .setReconnectAttempts(10)
      // .setSsl(true);
    AmqpClient client = AmqpClient.create(vertx, options);
    client.connect(ar -> {
      if (ar.failed()) {

        System.out.println("Unable to connect to the broker: " + ar.cause());
      } else {
        System.out.println("Connection succeeded: "+ ar.succeeded() +" : "+ ar.cause());
        amqpConnection = ar.result()
          .createReceiver("default",this)
          .exceptionHandler(e -> {
            System.out.println("连接异常");
            e.printStackTrace();
        });
      }
    });
  }

  // @Override
  // public void start(Promise<Void> startPromise) {
  //   AmqpConfig amqpConfig  = new AmqpConfig();
  //   ProtonClient client = ProtonClient.create(vertx);
  //   ProtonClientOptions options = new ProtonClientOptions();
  //   options.setSsl(true);
  //   client.connect(amqpConfig.getHost(), amqpConfig.getPort(), amqpConfig.getUser(), amqpConfig.getPassword(),  ar -> {
  //     if (!ar.succeeded()){
  //       System.out.println("Connect failed: " + ar.cause());
  //       return;
  //     }
  //     System.out.println("Connected：" + ar.cause());
  //     ProtonConnection connection = ar.result();
  //   });
  // }

  @Override
  public void stop() throws Exception {
    System.out.println("stop");
        amqpConnection.close();

  }

  @Override
  public void handle(AsyncResult<AmqpReceiver> asyncResult) {
    if (asyncResult.failed()) {
      System.out.println("Unable to create receiver");
    } else {
      AmqpReceiver receiver = asyncResult.result();
      receiver.handler(msg -> {

        // 每次接收到消息就被调用
        System.out.println("Received " + msg.bodyAsString());
      }).exceptionHandler(e ->{
        e.printStackTrace();
      });
    }
  }

}






