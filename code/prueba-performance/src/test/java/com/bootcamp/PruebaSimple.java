package com.bootcamp;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;

public class PruebaSimple extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
        .baseUrl("https://54.161.157.90")
        .disableFollowRedirect(); // <--- Evita que Gatling siga el 302 hacia el login

    private ScenarioBuilder scn = scenario("Prueba Validando Estado Explicito")
        .exec(
            http("Consultar Producto 1")
                .get("/api/v1/products/1")
                .check(status().is(200)) // Falla inmediatamente si responde 302, 401 o 403
        );

    {
        setUp(
            scn.injectOpen(
                rampUsers(30).during(Duration.ofSeconds(10))
            )
        ).protocols(httpProtocol);
    }
}