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

/**
 * Class to define the Main Route which will be invoked when the
 * application gets started
 *
 * @author Vishwajit
 */

@Slf4j
@Component
public class MainMultiCastRoute extends RouteBuilder {

    @Override
    public void configure() {

        // Create a thread pool based on the number of sub-routes
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // Handle all the exceptions that occur in the main route
        onException(Exception.class).handled(true).log("Exception occurred while processing Main Route");

        // Timer route to generate message exchanges when a timer fires
        from("timer://mainRoute?repeatCount=1")
                .log("inside Main Multicast Route")
                .process(exchange -> {
                    // Initialize sample messages for individual routes to process
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
                    // Set direct routes to exchange header to call in parallel
                    exchange.getIn().setHeader("DYNAMIC_ROUTES", dynamicRoutes);
                })
                .recipientList(header("DYNAMIC_ROUTES")).ignoreInvalidEndpoints()
                .parallelProcessing().executorService(executorService)
                .aggregationStrategy(new SimpleFlowMergeAggregator()).onCompletion()
                .process(exchange -> {
                    // Fetch and log the final response from each exchange
                    List<String> messages = exchange.getIn().getBody(List.class);
                    ObjectMapper mapper = new ObjectMapper();
                    for (String message : messages) {
                        log.info(mapper.writeValueAsString(message));
                    }
                })
                .end();
    }
}