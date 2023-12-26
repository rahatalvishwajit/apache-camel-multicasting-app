package com.camel.multicasting.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.camel.multicasting.aggregator.SimpleFlowMergeAggregator;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MainMultiCastRoute extends RouteBuilder {

    @Override
    public void configure() {

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        onException(Exception.class).handled(true).log("Exception occurred while processing Main Route");

        from("timer://mainRoute?repeatCount=1")
                .log("inside Main Multicast Route")
                .process(exchange -> {
                    HashMap<String, String> routeMap = new HashMap<>();
                    routeMap.put("RouteOne", "Sample message for Route One");
                    routeMap.put("RouteTwo", "Sample message for Route Two");
                    routeMap.put("RouteThree", "Sample message for Route Three");
                    exchange.setProperty("MESSAGE_MAP", routeMap);
                    Set<String> routeMapKeys = routeMap.keySet();
                    ArrayList<String> dynamicRoutes = new ArrayList<>();
                    for (String routeMapKey : routeMapKeys) {
                        dynamicRoutes.add("direct:" + routeMapKey);
                    }
                    exchange.getIn().setHeader("DYNAMIC_ROUTES", dynamicRoutes);
                })
                .recipientList(header("DYNAMIC_ROUTES")).ignoreInvalidEndpoints()
                .parallelProcessing().executorService(executorService)
                .aggregationStrategy(new SimpleFlowMergeAggregator()).onCompletion()
                .process(exchange -> {
                    List<String> messages = exchange.getIn().getBody(List.class);
                    ObjectMapper mapper = new ObjectMapper();
                    for (String message : messages) {
                        log.info(mapper.writeValueAsString(message));
                    }
                })
                .end();
    }
}