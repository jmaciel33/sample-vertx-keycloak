package com.julio.vertx.keycloak;

import com.julio.vertx.keycloak.verticle.SecurityVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationSecurity  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationSecurity.class);

    public static void main(String[] args) throws Exception{
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new SecurityVerticle());
    }
}
