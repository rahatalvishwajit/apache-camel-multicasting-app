package com.camel.multicasting.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

/**
 * Class to implement the AggregationStrategy to aggregate the response
 * from all sub-routes that have been invoked
 *
 * @author Vishwajit
 */

public class SimpleFlowMergeAggregator implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        /*
         * In aggregation, for the first time oldExchange will always be null
         * Populate the data from newExchange and return it
         */
        if (oldExchange == null) {
            String data = newExchange.getIn().getBody(String.class);
            List<String> aggregatedDataList = new ArrayList<>();
            aggregatedDataList.add(data);
            newExchange.getIn().setBody(aggregatedDataList);
            return newExchange;
        }

        // Fetch the previous exchange data from oldExchange
        List<String> oldData = oldExchange.getIn().getBody(List.class);
        // Add the newExchange data to oldExchange body
        oldData.add(newExchange.getIn().getBody(String.class));
        oldExchange.getIn().setBody(oldData);
        return oldExchange;
    }
}