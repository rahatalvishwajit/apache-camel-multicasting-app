package com.camel.multicasting.router;

import java.util.HashMap;

import org.apache.camel.builder.RouteBuilder;

import org.springframework.stereotype.Component;

/**
 * Class to define the sub route - MultiCastRoute2
 *
 * @author Vishwajit
 */

@Component
public class MultiCastRoute2 extends RouteBuilder {

    @Override
    public void configure() {

        onException(Exception.class).handled(true).log("Exception occurred while processing Route Two");

        from("direct:RouteTwo")
                .process(exchange -> Thread.currentThread().setName("RouteTwo"))
                .log("inside Route Two")
                .process(exchange -> {
                    HashMap<String, String> messageMap = (HashMap<String, String>) exchange.getProperty("MESSAGE_MAP");
                    if (messageMap.containsKey("RouteTwo")) {
                        String value = messageMap.get("RouteTwo") + " processed successfully";
                        exchange.getIn().setBody(value);
                    }
                })
                .end();
    }
}