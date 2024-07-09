package com.example;

import com.hazelcast.config.Config;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class ConsumerMain {

  /**
   * 两个消费者消费数据并 使用hazelcast 集群 分布式map
   * @param args
   */
  public static void main(String[] args) {
     Config hazelcastConfig = new Config();
     hazelcastConfig.setClusterName("my-cluster").setNetworkConfig(hazelcastConfig.getNetworkConfig().setPort(5701).setPublicAddress("127.0.0.1"));
    HazelcastClusterManager mgr = new HazelcastClusterManager(hazelcastConfig/**/);
    // VertxOptions options = new VertxOptions().setClusterManager(mgr);
    // Vertx.clusteredVertx(options, res -> {
    //   if (res.succeeded()) {
    //     Vertx vertx = res.result();
    //     vertx.deployVerticle(new ConsumerVerticle());
    //     System.out.println("consumer Deployed success ");
    //   } else {
    //     // failed!
    //     System.out.println("field to form cluster " +  res.cause());
    //   }
    // });
    Future<Vertx> vertxFuture = Vertx.builder().withClusterManager(mgr).buildClustered();
    vertxFuture.onComplete(re -> {
      if (re.succeeded()){
        Vertx vertx = re.result();
        vertx.deployVerticle(new ConsumerVerticle());
        System.out.println("consumer Deployed success ");
      }else {
        System.out.println("field to form cluster " +  re.cause());
      }
    });
  }
}
