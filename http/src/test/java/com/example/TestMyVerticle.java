package com.example;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;


public class TestMyVerticle {


  public void test_my_verticle(){
    Verticle myVerticle = new MyVerticle();
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(myVerticle);
  }
}
