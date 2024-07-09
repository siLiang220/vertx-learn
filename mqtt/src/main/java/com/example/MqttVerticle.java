package com.example;

import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import io.vertx.mqtt.messages.codes.MqttSubAckReasonCode;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class MqttVerticle extends AbstractVerticle implements Handler<MqttEndpoint> {

  private MqttServer mqttServer;

  @Override
  public void start(Promise<Void> startPromise) {
    mqttServer = MqttServer.create(vertx);
    mqttServer.endpointHandler(this).listen( ar->{
      if (ar.succeeded()){
        startPromise.complete();
        System.out.println("MQTT server is listening on port " + ar.result().actualPort());
      }else {
        System.out.println("Error on starting the server");
        ar.cause().printStackTrace();
        startPromise.fail(ar.cause());
      }
    });
  }



  @Override
  public void handle(MqttEndpoint endpoint) {

    // shows main connect info
    System.out.println("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());

    if (endpoint.auth() != null) {
      System.out.println("[username = " + endpoint.auth().getUsername() + ", password = " + endpoint.auth().getPassword() + "]");
    }
    System.out.println("[properties = " + endpoint.connectProperties() + "]");
    if (endpoint.will() != null) {
      System.out.println("[will topic = " + endpoint.will().getWillTopic() + " msg = " + new String(endpoint.will().getWillMessageBytes()) +
        " QoS = " + endpoint.will().getWillQos() + " isRetain = " + endpoint.will().isWillRetain() + "]");
    }

    System.out.println("[keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");

    // 判断设备验证是否成功
    if (false) {
      endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED);
    }

    endpoint.accept(false);

    endpoint.disconnectMessageHandler(msg -> {
      System.out.println("Received disconnect from client, reason code = " + msg.code());
    }).subscribeHandler(subscribe ->{
      // 订阅topic
      List<MqttSubAckReasonCode> reasonCodes = new ArrayList<>();
      for (MqttTopicSubscription s: subscribe.topicSubscriptions()) {
        System.out.println("Subscription for " + s.topicName() + " with QoS " + s.qualityOfService());
        reasonCodes.add(MqttSubAckReasonCode.qosGranted(s.qualityOfService()));
      }
      // ack the subscriptions request
      endpoint.subscribeAcknowledge(subscribe.messageId(), reasonCodes, MqttProperties.NO_PROPERTIES);
    }).unsubscribeHandler(unsubscribe ->{
      for (String t: unsubscribe.topics()) {
        System.out.println("Unsubscription for " + t);
      }
      // ack the subscriptions request
      endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
    }).publishHandler(message->{
      System.out.println("topic: [" + message.topicName() +"] Just received message [" + message.payload().toString(Charset.defaultCharset()) + "] with QoS [" + message.qosLevel() + "]");

      if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
        endpoint.publishAcknowledge(message.messageId());
      } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
        endpoint.publishReceived(message.messageId());
      }

    }).publishReleaseHandler(messageId -> {
      //qos为  EXACTLY_ONCE endpoint 需要使用 publishReceived或者 publishReceived方法回复一个PUBREC消息给客户端
      //   endpoint 同时也要通过 publishReleaseHandler或者 publishReleaseMessageHandler指定一个 handler 来处理来自客户端的PUBREL
      endpoint.publishComplete(messageId);
    });

  }

  @Override
  public void stop() throws Exception {
    mqttServer.close(v->{
      System.out.println("MQTT server closed");
    });
  }
}
