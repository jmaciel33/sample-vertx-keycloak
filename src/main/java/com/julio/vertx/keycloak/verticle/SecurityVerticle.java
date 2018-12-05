package com.julio.vertx.keycloak.verticle;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityVerticle.class);

    @Override
    public void start() throws Exception {

        Router router = Router.router(vertx);

        WebClient client = WebClient.create(vertx);
        ConfigStoreOptions file = new ConfigStoreOptions().setType("file").setConfig(new JsonObject().put("path", "application.json"));
        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(file));

        JsonObject keycloakJson = new JsonObject()
                .put("realm", "sample")
                .put("realm-public-key", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp/gp0vMeQ89n2R+Y8SoZOoJTUdqeinu8VeRq24rjbD06e/x55OanhLc5+e48jMUoMXnIkgcU0vmCkXTzVMh5DOxurjv6j5kbes+cPftBM5wXVCC8vjlmfQ7BwumpYzQmDa55wENma6fvu/mJMbLilV/1We5LXy34h//iupeugRIOSFDYClnrNpm70C3ctXZYQ5CtRCTtngv5vcLpdkNrWC/cPjT6oYfNgSuFgMgHPSmk8/YmXGC8onfGljI8E+4S8TmFmdb8wpkUXpH/hief6i96HrNPVGd1/RBb+sLaMzi+Xz8sE2Y101rrRTZA9z9zxnw5BIsOf3Qltjs8mZyR7wIDAQAB")
                .put("auth-server-url", "http://localhost:8080/auth")
                .put("ssl-required", "external")
                .put("resource", "sample-test")
                .put("credentials", new JsonObject().put("secret", "005b6dbe-f5f7-43db-9263-c10c6bb9b85e"));

        OAuth2Auth oauth2 = KeycloakAuth.create(vertx, OAuth2FlowType.PASSWORD, keycloakJson);

        router.route(HttpMethod.POST, "/login").handler(BodyHandler.create());

        router.post("/login").produces("application/json").handler(rc -> {

            LOGGER.info("received body ::: '"+rc.getBodyAsString()+"'");
            JsonObject userJson = rc.getBodyAsJson();
            LOGGER.info("User ::: "+userJson.encode());

            oauth2.authenticate(userJson, res -> {
                if (res.failed()) {
                    LOGGER.error("Access token error: {} " + res.cause().getMessage());
                    JsonObject error = new JsonObject().put("error", "invalid user");
                    rc.response().end(error.toString());
                    rc.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
                } else {
                    User user = res.result();
                    LOGGER.info("Success: we have found user: "+user.principal());
                    rc.response().end(user.principal().toString());
                }
            });
        });

        retriever.getConfig(conf -> {
            vertx.createHttpServer().requestHandler(router::accept).listen(conf.result().getInteger("port"));
        });
    }
}
