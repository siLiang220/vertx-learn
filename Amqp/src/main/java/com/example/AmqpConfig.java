package com.example;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AmqpConfig {
  public  String host = "";
  public int port = 5672;
  public String user ;
  public String password;

  public AmqpConfig()   {
    long timeStamp = System.currentTimeMillis();

    String signContent =  ""+timeStamp;
      try {
          this.password = doSign(signContent, "", "hmacsha1");
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }

  private static String doSign(String toSignString, String secret, String signMethod) throws Exception {
    SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), signMethod);
    Mac mac = Mac.getInstance(signMethod);
    mac.init(signingKey);
    byte[] rawHmac = mac.doFinal(toSignString.getBytes());
    return Base64.encodeBase64String(rawHmac);
  }
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
