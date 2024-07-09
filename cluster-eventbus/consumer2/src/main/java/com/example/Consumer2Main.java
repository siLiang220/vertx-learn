package com.example;

import com.hazelcast.config.Config;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class Consumer2Main {

  public static void main(String[] args) {
    Config hazelcastConfig = new Config();
    hazelcastConfig.setClusterName("my-cluster")
      .setNetworkConfig(hazelcastConfig.getNetworkConfig().setPort(5701).setPublicAddress("127.0.0.1"));
    HazelcastClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
    Future<Vertx> vertxFuture = Vertx.builder().withClusterManager(mgr).buildClustered();
    vertxFuture.onComplete(re -> {
      if (re.succeeded()) {
        Vertx vertx = re.result();
        vertx.deployVerticle(new ConsumerVerticle2());
        System.out.println("consumer Deployed success ");
      } else {
        System.out.println("field to form cluster " + re.cause());
      }
    });
  }
}
