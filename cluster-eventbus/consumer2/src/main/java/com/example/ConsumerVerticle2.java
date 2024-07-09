package com.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.shareddata.AsyncMap;

public class ConsumerVerticle2 extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer("test_address", message -> {
      String messageBody = (String)message.body();
      System.out.println("consumer2消费者接收内容： " + messageBody);
      String[] split = messageBody.split("_");
      // 集群共享数据
      vertx.sharedData().<String,Object>getAsyncMap("data", mapRes -> {
        if (mapRes.succeeded()){
          AsyncMap<String, Object> result = mapRes.result();
          result.get(split[0],res ->{
            if (res.succeeded()){
              Object value = res.result();

              if (value == null){

              }else {
                // System.out.println("consumer2 查询分布式共享map key" +split[0] +"对应值"+ res.result());
              }
            }
          });
        }
      });

      // if ("fail".equals(messageBody)){
      //   System.out.println("消费者接收内容 处理失败： " + messageBody);
      //   message.fail(100, "fail");
      // }
      // if ("success".equals(messageBody)){
      //   System.out.println("消费者接收内容 处理成功： " + messageBody);
      //   message.reply("success");
      // }
    });
  }

}
