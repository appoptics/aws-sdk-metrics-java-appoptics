package com.appoptics.metrics.awssdkmetrics;

import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.metrics.*;
import com.amazonaws.metrics.internal.cloudwatch.PredefinedMetricTransformer;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.util.AWSRequestMetrics;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AppopticsRequestMetricCollector extends RequestMetricCollector {
    private static final Logger log = LoggerFactory.getLogger(AppopticsRequestMetricCollector.class);

    private final PredefinedMetricTransformer transformer = new PredefinedMetricTransformer();

    private final MetricHelper metricHelper;

    public AppopticsRequestMetricCollector(MetricHelper metricHelper) {
        this.metricHelper = metricHelper;
    }

    @Override
    public void collectMetrics(Request<?> request, Response<?> response) {
        AWSRequestMetrics requestMetrics = request.getAWSRequestMetrics();
        if (requestMetrics == null || !requestMetrics.isEnabled()) {
            return;
        }

        for (MetricType type: AwsSdkMetrics.getPredefinedMetrics()) {
            if (!(type instanceof RequestMetricType)) {
                continue;
            }

            for (MetricDatum datum : transformer.toMetricData(type, request, response)) {
                Optional<Dimension> metricType = findDimension(datum, "MetricType");
                Optional<Dimension> requestType = findDimension(datum, "RequestType");

                if (!metricType.isPresent()) {
                    log.warn("no metric type type present: {}", datum);
                    continue;
                }

                final String metricName = metricType.get().getValue();
                final List<MetricTag> tags = new ArrayList<>();

                tags.add(MetricTag.awsServiceTag(datum.getMetricName()));
                requestType.ifPresent(d -> tags.add(new MetricTag("request_type", d.getValue())));

                // XXX: lots of timer types, check on unit
                if (datum.getUnit().equals("Milliseconds")) {
                    Timer t = metricHelper.getTimer(metricName, tags);

                    // Convert to microseconds and truncate
                    long microseconds = (long)(datum.getValue() * 1000.0);
                    t.update(microseconds, TimeUnit.MICROSECONDS);
                } else if (datum.getUnit().equals("Count")) {
                    Histogram h = metricHelper.getHistogram(metricName, tags);

                    h.update(datum.getValue().longValue());
                } else {
                    log.warn("unknown metric unit: {}", datum);
                }
            }
        }
    }

    private Optional<Dimension> findDimension(MetricDatum datum, String key) {
        return datum.getDimensions().stream().filter(d -> d.getName().equals(key)).findFirst();
    }
}
