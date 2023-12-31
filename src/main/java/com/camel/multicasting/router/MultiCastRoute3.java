package com.camel.multicasting.router;

import java.util.HashMap;

import org.apache.camel.builder.RouteBuilder;

import org.springframework.stereotype.Component;

/**
 * Class to define the sub route - MultiCastRoute3
 *
 * @author Vishwajit
 */

@Component
public class MultiCastRoute3 extends RouteBuilder {

    @Override
    public void configure() {

        // Handle all the exceptions that occur in the sub route three
        onException(Exception.class).handled(true).log("Exception occurred while processing Route Three")
                .process(exchange -> {
                    // Process the message in case of failure
                    HashMap<String, String> messageMap = (HashMap<String, String>) exchange.getProperty("MESSAGE_MAP");
                    if (messageMap.containsKey("RouteThree")) {
                        String value = messageMap.get("RouteThree") + " processed unsuccessfully";
                        exchange.getIn().setBody(value);
                    }
                });

        from("direct:RouteThree")
                .process(exchange -> Thread.currentThread().setName("RouteThree"))
                .log("inside Route Three")
                // Throw the exception to handle the exception scenario
                .throwException(new RuntimeException())
                .process(exchange -> {
                    HashMap<String, String> messageMap = (HashMap<String, String>) exchange.getProperty("MESSAGE_MAP");
                    if (messageMap.containsKey("RouteThree")) {
                        String value = messageMap.get("RouteThree") + " processed successfully";
                        exchange.getIn().setBody(value);
                    }
                })
                .end();
    }
}