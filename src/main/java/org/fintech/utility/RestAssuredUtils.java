package org.fintech.utility;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestAssuredUtils {
    private static final Logger logger = LoggerFactory.getLogger(RestAssuredUtils.class);

    public static RequestSpecification getRequestSpecification() {
        String baseUrl = ConfigReader.get("baseUrl");
        logger.info("Building RequestSpecification with base URL: {}", baseUrl);
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType("application/json")
                .build();
    }

    public static ResponseSpecification getResponseSpecification(int expectedStatusCode) {
        logger.info("Building ResponseSpecification with expected status code: {}", expectedStatusCode);
        return new ResponseSpecBuilder()
                .expectStatusCode(expectedStatusCode)
                .build();
    }
}

