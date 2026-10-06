package com.bootcamp;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;

public class PruebaCompleja extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
        .baseUrl("https://54.161.157.90")
        .disableFollowRedirect(); 

    private ScenarioBuilder scn = scenario("Prueba Kong + Authelia + API Products")
        .exec(
            http("1. Login en Authelia")
                .post("/authelia/api/firstfactor")
                .header("Content-Type", "application/json")
                .body(StringBody("{\"username\": \"juanperez\", \"password\": \"MP66nHHqo5Ws\", \"keepMeLoggedIn\": true}"))
                .check(status().is(200)) 
        )
        .pause(1)
        .exec(
            http("2. GET /api/v1/products/1")
                .get("/api/v1/products/1")
                .check(status().is(200))
        );
    {
        setUp(
            scn.injectOpen(
                rampUsers(30).during(Duration.ofSeconds(10))
            )
        ).protocols(httpProtocol);
    }
}