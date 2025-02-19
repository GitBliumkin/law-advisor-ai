package com.laws.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// NOTE: We need this here in order to run the migrations before any other class attempts to get
// data from dynamo before it's populate
public class Api {

  public static void main(String[] args) {
    SpringApplication.run(Api.class, args);
  }
}
