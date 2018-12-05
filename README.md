#Sample with vertx and keycloak

OAuth authentication using `vertx-auth-oauth2` and `Keycloak`

Starting Keycloak with docker:

[Docker Hub: jboss/keycloak](https://hub.docker.com/r/jboss/keycloak/)


* Expose on localhost and Creating admin account
```

docker run -p 8080:8080
           -e KEYCLOAK_USER=<USERNAME> 
           -e KEYCLOAK_PASSWORD=<PASSWORD> 
           jboss/keycloak
```



```
docker run -p 8080:8080
           -e KEYCLOAK_USER=admin 
           -e KEYCLOAK_PASSWORD=admin 
           jboss/keycloak
```



* Starting sample-vertx-keycloak:

```
mvn clean package
```

* Starting app

```
mvn clean compile exec:exec -q
```