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

        if (oldExchange == null) {
            String data = newExchange.getIn().getBody(String.class);
            List<String> aggregatedDataList = new ArrayList<>();
            aggregatedDataList.add(data);
            newExchange.getIn().setBody(aggregatedDataList);
            return newExchange;
        }

        List<String> oldData = oldExchange.getIn().getBody(List.class);
        oldData.add(newExchange.getIn().getBody(String.class));
        oldExchange.getIn().setBody(oldData);
        return oldExchange;
    }
}