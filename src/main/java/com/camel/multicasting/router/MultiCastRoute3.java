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

        onException(Exception.class).handled(true).log("Exception occurred while processing Route Three")
                .process(exchange -> {
                    HashMap<String, String> messageMap = (HashMap<String, String>) exchange.getProperty("MESSAGE_MAP");
                    if (messageMap.containsKey("RouteThree")) {
                        String value = messageMap.get("RouteThree") + " processed unsuccessfully";
                        exchange.getIn().setBody(value);
                    }
                });

        from("direct:RouteThree")
                .process(exchange -> Thread.currentThread().setName("RouteThree"))
                .log("inside Route Three")
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