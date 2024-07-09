package com.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

public class MyVerticle extends AbstractVerticle {

  private HttpServer server;

  public void start(Promise<Void> startPromise) {
    server = vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    });

    // Now bind the server:
    server.listen(8890, res -> {
      if (res.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8890");
      } else {
        startPromise.fail(res.cause());
      }
    });
  }
  public static void main(String[] args) {
    Verticle myVerticle = new MyVerticle();
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(myVerticle);
  }
}
