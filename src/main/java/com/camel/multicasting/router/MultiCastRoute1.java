package com.camel.multicasting.router;

import java.util.HashMap;

import org.apache.camel.builder.RouteBuilder;

import org.springframework.stereotype.Component;

@Component
public class MultiCastRoute1 extends RouteBuilder {

    @Override
    public void configure() {

        onException(Exception.class).handled(true).log("Exception occurred while processing Route One");

        from("direct:RouteOne")
                .process(exchange -> Thread.currentThread().setName("RouteOne"))
                .log("inside Route One")
                .process(exchange -> {
                    HashMap<String, String> messageMap = (HashMap<String, String>) exchange.getProperty("MESSAGE_MAP");
                    if (messageMap.containsKey("RouteOne")) {
                        String value = messageMap.get("RouteOne") + " processed successfully";
                        exchange.getIn().setBody(value);
                    }
                })
                .end();
    }
}